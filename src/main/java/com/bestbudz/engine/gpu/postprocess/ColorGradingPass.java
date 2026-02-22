package com.bestbudz.engine.gpu.postprocess;

import com.bestbudz.engine.gpu.shader.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;

/**
 * Color grading pass using a 32x32x32 3D LUT texture.
 * Default LUT applies a warm ENB-style tint:
 *   - Warm amber tint in shadows
 *   - Desaturated blues in highlights
 *   - Lifted blacks (no pure black)
 *   - Compressed highlight range
 */
public class ColorGradingPass {

	private static final int LUT_SIZE = 32;

	private boolean initialized = false;

	private ShaderProgram shader;
	private int lutTexture;

	// Internal FBO for the tonemap pass to render into
	private PostProcessFBO inputFBO;

	// Uniform locations
	private int uScene, uLUT, uStrength;

	public void initialize(int width, int height) {
		if (initialized) return;

		shader = new ShaderProgram(
			PostProcessShaders.FULLSCREEN_VERTEX,
			PostProcessShaders.COLOR_GRADING_FRAGMENT
		);
		if (!shader.isValid()) {
			System.err.println("[ColorGradingPass] Shader compilation failed");
			return;
		}

		shader.bind();
		uScene = shader.getUniformLocation("uScene");
		uLUT = shader.getUniformLocation("uLUT");
		uStrength = shader.getUniformLocation("uStrength");
		shader.unbind();

		inputFBO = new PostProcessFBO(width, height, PostProcessFBO.FORMAT_RGBA8);

		createDefaultLUT();

		initialized = true;
		System.out.println("[ColorGradingPass] Initialized");
	}

	/**
	 * Execute color grading: reads from inputFBO, writes to outputFBO.
	 */
	public void execute(PostProcessFBO outputFBO) {
		if (!initialized) return;

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		outputFBO.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		shader.bind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, inputFBO.getColorTexture());
		shader.setUniform1i(uScene, 0);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, lutTexture);
		shader.setUniform1i(uLUT, 1);

		shader.setUniform1f(uStrength, EnvironmentConfig.colorGradingStrength);

		FullscreenQuad.draw();

		shader.unbind();
		outputFBO.unbind();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/** The tonemap pass renders into this FBO as intermediate step */
	public PostProcessFBO getInputFBO() {
		return inputFBO;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void resize(int width, int height) {
		if (inputFBO != null) inputFBO.resize(width, height);
	}

	private void createDefaultLUT() {
		ByteBuffer lutData = BufferUtils.createByteBuffer(LUT_SIZE * LUT_SIZE * LUT_SIZE * 3);

		for (int b = 0; b < LUT_SIZE; b++) {
			for (int g = 0; g < LUT_SIZE; g++) {
				for (int r = 0; r < LUT_SIZE; r++) {
					float rf = (float) r / (LUT_SIZE - 1);
					float gf = (float) g / (LUT_SIZE - 1);
					float bf = (float) b / (LUT_SIZE - 1);

					// Lift blacks: raise minimum from 0 to ~0.03
					rf = 0.03f + rf * 0.97f;
					gf = 0.03f + gf * 0.97f;
					bf = 0.03f + bf * 0.97f;

					// Warm amber tint in shadows
					float lum = rf * 0.2126f + gf * 0.7152f + bf * 0.0722f;
					float shadowWeight = Math.max(0.0f, 1.0f - lum * 3.0f); // strongest in darks
					rf += shadowWeight * 0.06f;  // add warmth
					gf += shadowWeight * 0.03f;
					bf -= shadowWeight * 0.02f;  // reduce blue

					// Desaturate highlights
					float highlightWeight = Math.max(0.0f, (lum - 0.5f) * 2.0f);
					float desat = highlightWeight * 0.25f;
					rf = rf + (lum - rf) * desat;
					gf = gf + (lum - gf) * desat;
					bf = bf + (lum - bf) * desat;

					// Compress highlight range slightly
					rf = rf * (1.0f - highlightWeight * 0.1f) + highlightWeight * 0.1f * 0.9f;
					gf = gf * (1.0f - highlightWeight * 0.1f) + highlightWeight * 0.1f * 0.88f;
					bf = bf * (1.0f - highlightWeight * 0.1f) + highlightWeight * 0.1f * 0.85f;

					// Clamp
					rf = Math.max(0.0f, Math.min(1.0f, rf));
					gf = Math.max(0.0f, Math.min(1.0f, gf));
					bf = Math.max(0.0f, Math.min(1.0f, bf));

					lutData.put((byte) (rf * 255));
					lutData.put((byte) (gf * 255));
					lutData.put((byte) (bf * 255));
				}
			}
		}
		lutData.flip();

		lutTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, lutTexture);
		GL12.glTexImage3D(GL12.GL_TEXTURE_3D, 0, GL11.GL_RGB8,
			LUT_SIZE, LUT_SIZE, LUT_SIZE, 0,
			GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, lutData);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL12.GL_TEXTURE_3D, GL12.GL_TEXTURE_WRAP_R, GL12.GL_CLAMP_TO_EDGE);
		GL11.glBindTexture(GL12.GL_TEXTURE_3D, 0);
	}

	public void cleanup() {
		if (shader != null) { shader.cleanup(); shader = null; }
		if (inputFBO != null) { inputFBO.cleanup(); inputFBO = null; }
		if (lutTexture != 0) { GL11.glDeleteTextures(lutTexture); lutTexture = 0; }
		initialized = false;
	}
}
