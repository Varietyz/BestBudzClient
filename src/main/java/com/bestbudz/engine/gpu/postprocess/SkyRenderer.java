package com.bestbudz.engine.gpu.postprocess;

import com.bestbudz.engine.gpu.GPUCameraSync;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import org.lwjgl.opengl.GL11;

/**
 * Renders a procedural sky + sun disc as a fullscreen quad behind all scene geometry.
 * Draws with depth testing disabled and depth write disabled so it sits behind everything.
 * Uses inverse view-projection matrix to reconstruct world-space ray direction per pixel.
 */
public class SkyRenderer {

	private static boolean initialized = false;
	private static ShaderProgram shader;

	// Uniform locations
	private static int uInvViewProjection;
	private static int uSunDirection;
	private static int uSunColor;
	private static int uSkyColorZenith;
	private static int uSkyColorHorizon;
	private static int uSunSize;

	public static boolean initialize() {
		if (initialized) return true;

		FullscreenQuad.init();

		shader = new ShaderProgram(
			PostProcessShaders.SKY_VERTEX,
			PostProcessShaders.SKY_FRAGMENT
		);

		if (!shader.isValid()) {
			System.err.println("[SkyRenderer] Shader compilation failed");
			return false;
		}

		shader.bind();
		uInvViewProjection = shader.getUniformLocation("uInvViewProjection");
		uSunDirection = shader.getUniformLocation("uSunDirection");
		uSunColor = shader.getUniformLocation("uSunColor");
		uSkyColorZenith = shader.getUniformLocation("uSkyColorZenith");
		uSkyColorHorizon = shader.getUniformLocation("uSkyColorHorizon");
		uSunSize = shader.getUniformLocation("uSunSize");
		shader.unbind();

		initialized = true;
		System.out.println("[SkyRenderer] Initialized");
		return true;
	}

	/**
	 * Render sky into the currently bound FBO.
	 * Must be called AFTER clear and BEFORE scene geometry rendering.
	 */
	public static void render() {
		if (!initialized) return;

		// Disable depth write so sky sits behind all geometry
		GL11.glDepthMask(false);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		shader.bind();

		// Inverse VP for ray direction reconstruction
		shader.setUniformMatrix4fv(uInvViewProjection,
			GPUCameraSync.getInverseViewProjectionMatrix());

		// Sun direction from EnvironmentConfig
		float[] sunDir = EnvironmentConfig.getSunDirection();
		shader.setUniform3f(uSunDirection, sunDir[0], sunDir[1], sunDir[2]);
		shader.setUniform3f(uSunColor,
			EnvironmentConfig.sunColorR,
			EnvironmentConfig.sunColorG,
			EnvironmentConfig.sunColorB);
		shader.setUniform3f(uSkyColorZenith,
			EnvironmentConfig.skyZenithR,
			EnvironmentConfig.skyZenithG,
			EnvironmentConfig.skyZenithB);
		shader.setUniform3f(uSkyColorHorizon,
			EnvironmentConfig.skyHorizonR,
			EnvironmentConfig.skyHorizonG,
			EnvironmentConfig.skyHorizonB);
		shader.setUniform1f(uSunSize, EnvironmentConfig.sunSize);

		FullscreenQuad.draw();

		shader.unbind();

		// Restore depth state
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void cleanup() {
		if (shader != null) {
			shader.cleanup();
			shader = null;
		}
		initialized = false;
	}
}
