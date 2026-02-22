package com.bestbudz.engine.gpu.postprocess;

import com.bestbudz.engine.gpu.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

/**
 * Bloom post-processing pass:
 *   1. Extract bright pixels above threshold (half-res)
 *   2. Gaussian blur horizontal (ping-pong)
 *   3. Gaussian blur vertical (ping-pong)
 *   Output: blurred bloom texture for additive composite in tone mapping.
 */
public class BloomPass {

	private boolean initialized = false;

	private ShaderProgram extractShader;
	private ShaderProgram blurShader;

	// Half-resolution ping-pong FBOs
	private PostProcessFBO extractFBO;
	private PostProcessFBO pingFBO;
	private PostProcessFBO pongFBO;

	// Extract shader uniforms
	private int exScene, exThreshold;

	// Blur shader uniforms
	private int blInput, blDirection;

	private int halfWidth, halfHeight;

	public void initialize(int fullWidth, int fullHeight) {
		if (initialized) return;

		halfWidth = Math.max(1, fullWidth / 2);
		halfHeight = Math.max(1, fullHeight / 2);

		// Extract shader
		extractShader = new ShaderProgram(
			PostProcessShaders.FULLSCREEN_VERTEX,
			PostProcessShaders.BLOOM_EXTRACT_FRAGMENT
		);
		if (!extractShader.isValid()) {
			System.err.println("[BloomPass] Extract shader failed");
			return;
		}

		extractShader.bind();
		exScene = extractShader.getUniformLocation("uScene");
		exThreshold = extractShader.getUniformLocation("uThreshold");
		extractShader.unbind();

		// Blur shader
		blurShader = new ShaderProgram(
			PostProcessShaders.FULLSCREEN_VERTEX,
			PostProcessShaders.BLUR_FRAGMENT
		);
		if (!blurShader.isValid()) {
			System.err.println("[BloomPass] Blur shader failed");
			return;
		}

		blurShader.bind();
		blInput = blurShader.getUniformLocation("uInput");
		blDirection = blurShader.getUniformLocation("uDirection");
		blurShader.unbind();

		// Half-res FBOs (RGBA16F to preserve HDR bloom values)
		extractFBO = new PostProcessFBO(halfWidth, halfHeight, PostProcessFBO.FORMAT_RGBA16F);
		pingFBO = new PostProcessFBO(halfWidth, halfHeight, PostProcessFBO.FORMAT_RGBA16F);
		pongFBO = new PostProcessFBO(halfWidth, halfHeight, PostProcessFBO.FORMAT_RGBA16F);

		initialized = true;
		System.out.println("[BloomPass] Initialized (" + halfWidth + "x" + halfHeight + ")");
	}

	public void execute(int hdrSceneTexture) {
		if (!initialized) return;

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// 1. Extract bright pixels
		extractFBO.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		extractShader.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, hdrSceneTexture);
		extractShader.setUniform1i(exScene, 0);
		extractShader.setUniform1f(exThreshold, EnvironmentConfig.bloomThreshold);
		FullscreenQuad.draw();
		extractShader.unbind();
		extractFBO.unbind();

		// 2. Horizontal blur: extract -> ping
		pingFBO.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		blurShader.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, extractFBO.getColorTexture());
		blurShader.setUniform1i(blInput, 0);
		blurShader.setUniform2f(blDirection, 1.0f, 0.0f);
		FullscreenQuad.draw();
		blurShader.unbind();
		pingFBO.unbind();

		// 3. Vertical blur: ping -> pong
		pongFBO.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		blurShader.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, pingFBO.getColorTexture());
		blurShader.setUniform1i(blInput, 0);
		blurShader.setUniform2f(blDirection, 0.0f, 1.0f);
		FullscreenQuad.draw();
		blurShader.unbind();
		pongFBO.unbind();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public int getBloomTexture() {
		return pongFBO != null ? pongFBO.getColorTexture() : 0;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void resize(int fullWidth, int fullHeight) {
		int newHW = Math.max(1, fullWidth / 2);
		int newHH = Math.max(1, fullHeight / 2);
		if (newHW == halfWidth && newHH == halfHeight) return;
		halfWidth = newHW;
		halfHeight = newHH;
		if (extractFBO != null) extractFBO.resize(halfWidth, halfHeight);
		if (pingFBO != null) pingFBO.resize(halfWidth, halfHeight);
		if (pongFBO != null) pongFBO.resize(halfWidth, halfHeight);
	}

	public void cleanup() {
		if (extractShader != null) { extractShader.cleanup(); extractShader = null; }
		if (blurShader != null) { blurShader.cleanup(); blurShader = null; }
		if (extractFBO != null) { extractFBO.cleanup(); extractFBO = null; }
		if (pingFBO != null) { pingFBO.cleanup(); pingFBO = null; }
		if (pongFBO != null) { pongFBO.cleanup(); pongFBO = null; }
		initialized = false;
	}
}
