package com.bestbudz.engine.gpu.postprocess;

import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Orchestrates the full post-processing chain:
 *   HDR Scene FBO --> Bloom extract --> Blur --> SSAO --> Tone map + composite --> LDR output
 *
 * The final LDR texture is read back via glReadPixels in GameState.
 */
public class PostProcessPipeline {

	private static boolean initialized = false;

	// Final LDR output FBO (RGBA8) -- this is what glReadPixels reads from
	private static PostProcessFBO ldrOutputFBO;

	// Tone mapping shader (includes bloom composite + SSAO)
	private static ShaderProgram tonemapShader;
	private static int tmScene, tmBloom, tmSSAO;
	private static int tmExposure, tmGamma, tmBloomIntensity, tmSSAOStrength;
	private static int tmBloomEnabled, tmSSAOEnabled;

	// Sub-passes (initialized in later sub-phases)
	private static BloomPass bloomPass;
	private static SSAOPass ssaoPass;
	private static ColorGradingPass colorGradingPass;

	public static boolean initialize(int width, int height) {
		if (initialized) return true;

		try {
			FullscreenQuad.init();

			ldrOutputFBO = new PostProcessFBO(width, height, PostProcessFBO.FORMAT_RGBA8);

			// Compile tone mapping shader
			tonemapShader = new ShaderProgram(
				PostProcessShaders.FULLSCREEN_VERTEX,
				PostProcessShaders.TONEMAP_FRAGMENT
			);
			if (!tonemapShader.isValid()) {
				System.err.println("[PostProcessPipeline] Tonemap shader compilation failed");
				return false;
			}

			// Cache uniform locations
			tonemapShader.bind();
			tmScene = tonemapShader.getUniformLocation("uScene");
			tmBloom = tonemapShader.getUniformLocation("uBloom");
			tmSSAO = tonemapShader.getUniformLocation("uSSAO");
			tmExposure = tonemapShader.getUniformLocation("uExposure");
			tmGamma = tonemapShader.getUniformLocation("uGamma");
			tmBloomIntensity = tonemapShader.getUniformLocation("uBloomIntensity");
			tmSSAOStrength = tonemapShader.getUniformLocation("uSSAOStrength");
			tmBloomEnabled = tonemapShader.getUniformLocation("uBloomEnabled");
			tmSSAOEnabled = tonemapShader.getUniformLocation("uSSAOEnabled");
			tonemapShader.unbind();

			// Initialize bloom pass
			bloomPass = new BloomPass();
			bloomPass.initialize(width, height);

			// Initialize SSAO pass
			ssaoPass = new SSAOPass();
			ssaoPass.initialize(width, height);

			// Initialize color grading pass
			colorGradingPass = new ColorGradingPass();
			colorGradingPass.initialize(width, height);

			initialized = true;
			System.out.println("[PostProcessPipeline] Initialized (" + width + "x" + height + ")");
			return true;

		} catch (Exception e) {
			System.err.println("[PostProcessPipeline] Init failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Run the full post-process chain on the scene HDR FBO.
	 *
	 * @param hdrSceneTexture the HDR scene color texture
	 * @param depthTexture    the scene depth texture (for SSAO)
	 * @param width           viewport width
	 * @param height          viewport height
	 */
	public static void execute(int hdrSceneTexture, int depthTexture, int width, int height) {
		if (!initialized) return;

		// Ensure FBOs match current size
		ldrOutputFBO.resize(width, height);

		// ---- 1. Bloom ----
		int bloomTexture = 0;
		boolean doBloom = EnvironmentConfig.bloomEnabled && bloomPass != null && bloomPass.isInitialized();
		if (doBloom) {
			bloomPass.resize(width, height);
			bloomPass.execute(hdrSceneTexture);
			bloomTexture = bloomPass.getBloomTexture();
		}

		// ---- 2. SSAO ----
		int ssaoTexture = 0;
		boolean doSSAO = EnvironmentConfig.ssaoEnabled && ssaoPass != null && ssaoPass.isInitialized();
		if (doSSAO) {
			ssaoPass.resize(width, height);
			ssaoPass.execute(depthTexture);
			ssaoTexture = ssaoPass.getSSAOTexture();
		}

		// ---- 3. Tone mapping + composite ----
		// Render to a temp FBO for color grading input
		boolean doColorGrading = EnvironmentConfig.colorGradingStrength > 0.01f
			&& colorGradingPass != null && colorGradingPass.isInitialized();

		PostProcessFBO tonemapTarget = doColorGrading ? colorGradingPass.getInputFBO() : ldrOutputFBO;
		if (doColorGrading) {
			colorGradingPass.resize(width, height);
		}

		tonemapTarget.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		tonemapShader.bind();

		// Scene HDR texture
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, hdrSceneTexture);
		tonemapShader.setUniform1i(tmScene, 0);

		// Bloom texture
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, doBloom ? bloomTexture : 0);
		tonemapShader.setUniform1i(tmBloom, 1);

		// SSAO texture
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, doSSAO ? ssaoTexture : 0);
		tonemapShader.setUniform1i(tmSSAO, 2);

		// Uniforms
		tonemapShader.setUniform1f(tmExposure, EnvironmentConfig.exposure);
		tonemapShader.setUniform1f(tmGamma, EnvironmentConfig.gamma);
		tonemapShader.setUniform1f(tmBloomIntensity, EnvironmentConfig.bloomIntensity);
		tonemapShader.setUniform1f(tmSSAOStrength, EnvironmentConfig.ssaoStrength);
		tonemapShader.setUniform1i(tmBloomEnabled, doBloom ? 1 : 0);
		tonemapShader.setUniform1i(tmSSAOEnabled, doSSAO ? 1 : 0);

		FullscreenQuad.draw();

		tonemapShader.unbind();
		tonemapTarget.unbind();

		// ---- 4. Color grading ----
		if (doColorGrading) {
			colorGradingPass.execute(ldrOutputFBO);
		}

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/** Get the final LDR FBO id for glReadPixels */
	public static int getLDRFramebuffer() {
		return ldrOutputFBO != null ? ldrOutputFBO.getFbo() : 0;
	}

	/** Get the final LDR color texture */
	public static int getLDRColorTexture() {
		return ldrOutputFBO != null ? ldrOutputFBO.getColorTexture() : 0;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void resize(int width, int height) {
		if (!initialized) return;
		ldrOutputFBO.resize(width, height);
		if (bloomPass != null) bloomPass.resize(width, height);
		if (ssaoPass != null) ssaoPass.resize(width, height);
		if (colorGradingPass != null) colorGradingPass.resize(width, height);
	}

	public static void cleanup() {
		if (ldrOutputFBO != null) { ldrOutputFBO.cleanup(); ldrOutputFBO = null; }
		if (tonemapShader != null) { tonemapShader.cleanup(); tonemapShader = null; }
		if (bloomPass != null) { bloomPass.cleanup(); bloomPass = null; }
		if (ssaoPass != null) { ssaoPass.cleanup(); ssaoPass = null; }
		if (colorGradingPass != null) { colorGradingPass.cleanup(); colorGradingPass = null; }
		FullscreenQuad.cleanup();
		initialized = false;
		System.out.println("[PostProcessPipeline] Cleaned up");
	}
}
