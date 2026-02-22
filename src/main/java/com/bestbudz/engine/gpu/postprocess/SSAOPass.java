package com.bestbudz.engine.gpu.postprocess;

import com.bestbudz.engine.gpu.GPUCameraSync;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Screen-Space Ambient Occlusion (SSAO) pass:
 *   1. Sample hemisphere around each fragment using depth buffer
 *   2. Compare depths to determine occlusion
 *   3. Box blur the result
 *   Output: single-channel AO texture (1.0 = no occlusion, 0.0 = fully occluded)
 *
 * Runs at quarter resolution for performance.
 */
public class SSAOPass {

	private static final int KERNEL_SIZE = 32;
	private static final int NOISE_SIZE = 4;

	private boolean initialized = false;

	private ShaderProgram ssaoShader;
	private ShaderProgram blurShader;

	// Quarter-resolution FBOs
	private PostProcessFBO ssaoFBO;
	private PostProcessFBO blurFBO;

	// 4x4 noise texture
	private int noiseTexture;

	// Hemisphere kernel samples
	private float[] kernelSamples;

	// SSAO shader uniforms
	private int uDepthTex, uNoiseTex, uProjection, uInvProjection;
	private int uNoiseScale, uRadius, uBias;
	private int[] uSamples;

	// Blur shader uniforms
	private int uBlurSSAO;

	private int quarterWidth, quarterHeight;

	public void initialize(int fullWidth, int fullHeight) {
		if (initialized) return;

		quarterWidth = Math.max(1, fullWidth / 4);
		quarterHeight = Math.max(1, fullHeight / 4);

		// Generate kernel and noise
		generateKernel();
		createNoiseTexture();

		// SSAO shader
		ssaoShader = new ShaderProgram(
			PostProcessShaders.FULLSCREEN_VERTEX,
			PostProcessShaders.SSAO_FRAGMENT
		);
		if (!ssaoShader.isValid()) {
			System.err.println("[SSAOPass] SSAO shader failed");
			return;
		}

		ssaoShader.bind();
		uDepthTex = ssaoShader.getUniformLocation("uDepthTex");
		uNoiseTex = ssaoShader.getUniformLocation("uNoiseTex");
		uProjection = ssaoShader.getUniformLocation("uProjection");
		uInvProjection = ssaoShader.getUniformLocation("uInvProjection");
		uNoiseScale = ssaoShader.getUniformLocation("uNoiseScale");
		uRadius = ssaoShader.getUniformLocation("uRadius");
		uBias = ssaoShader.getUniformLocation("uBias");

		uSamples = new int[KERNEL_SIZE];
		for (int i = 0; i < KERNEL_SIZE; i++) {
			uSamples[i] = ssaoShader.getUniformLocation("uSamples[" + i + "]");
		}

		// Upload kernel samples
		for (int i = 0; i < KERNEL_SIZE; i++) {
			ssaoShader.setUniform3f(uSamples[i],
				kernelSamples[i * 3], kernelSamples[i * 3 + 1], kernelSamples[i * 3 + 2]);
		}
		ssaoShader.unbind();

		// Blur shader
		blurShader = new ShaderProgram(
			PostProcessShaders.FULLSCREEN_VERTEX,
			PostProcessShaders.SSAO_BLUR_FRAGMENT
		);
		if (!blurShader.isValid()) {
			System.err.println("[SSAOPass] Blur shader failed");
			return;
		}

		blurShader.bind();
		uBlurSSAO = blurShader.getUniformLocation("uSSAO");
		blurShader.unbind();

		// Quarter-res FBOs (R8 single channel)
		ssaoFBO = new PostProcessFBO(quarterWidth, quarterHeight, PostProcessFBO.FORMAT_R8);
		blurFBO = new PostProcessFBO(quarterWidth, quarterHeight, PostProcessFBO.FORMAT_R8);

		initialized = true;
		System.out.println("[SSAOPass] Initialized (" + quarterWidth + "x" + quarterHeight + ")");
	}

	public void execute(int depthTexture) {
		if (!initialized) return;

		GL11.glDisable(GL11.GL_DEPTH_TEST);

		// 1. SSAO sampling
		ssaoFBO.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		ssaoShader.bind();

		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		ssaoShader.setUniform1i(uDepthTex, 0);

		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, noiseTexture);
		ssaoShader.setUniform1i(uNoiseTex, 1);

		ssaoShader.setUniformMatrix4fv(uProjection, GPUCameraSync.getProjectionMatrix());
		ssaoShader.setUniformMatrix4fv(uInvProjection, GPUCameraSync.getInverseProjectionMatrix());
		ssaoShader.setUniform2f(uNoiseScale,
			(float) quarterWidth / NOISE_SIZE,
			(float) quarterHeight / NOISE_SIZE);
		ssaoShader.setUniform1f(uRadius, EnvironmentConfig.ssaoRadius);
		ssaoShader.setUniform1f(uBias, EnvironmentConfig.ssaoBias);

		FullscreenQuad.draw();
		ssaoShader.unbind();
		ssaoFBO.unbind();

		// 2. Box blur
		blurFBO.bind();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		blurShader.bind();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, ssaoFBO.getColorTexture());
		blurShader.setUniform1i(uBlurSSAO, 0);
		FullscreenQuad.draw();
		blurShader.unbind();
		blurFBO.unbind();

		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public int getSSAOTexture() {
		return blurFBO != null ? blurFBO.getColorTexture() : 0;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void resize(int fullWidth, int fullHeight) {
		int newQW = Math.max(1, fullWidth / 4);
		int newQH = Math.max(1, fullHeight / 4);
		if (newQW == quarterWidth && newQH == quarterHeight) return;
		quarterWidth = newQW;
		quarterHeight = newQH;
		if (ssaoFBO != null) ssaoFBO.resize(quarterWidth, quarterHeight);
		if (blurFBO != null) blurFBO.resize(quarterWidth, quarterHeight);
	}

	private void generateKernel() {
		Random rand = new Random(42);
		kernelSamples = new float[KERNEL_SIZE * 3];

		for (int i = 0; i < KERNEL_SIZE; i++) {
			// Random point in hemisphere (z > 0)
			float x = rand.nextFloat() * 2.0f - 1.0f;
			float y = rand.nextFloat() * 2.0f - 1.0f;
			float z = rand.nextFloat(); // hemisphere: z in [0,1]

			// Normalize
			float len = (float) Math.sqrt(x * x + y * y + z * z);
			if (len < 0.001f) { x = 0; y = 0; z = 1; len = 1; }
			x /= len; y /= len; z /= len;

			// Scale: more samples near the origin (accelerating interpolation)
			float scale = (float) i / KERNEL_SIZE;
			scale = 0.1f + scale * scale * 0.9f; // lerp(0.1, 1.0, scale^2)
			x *= scale; y *= scale; z *= scale;

			kernelSamples[i * 3] = x;
			kernelSamples[i * 3 + 1] = y;
			kernelSamples[i * 3 + 2] = z;
		}
	}

	private void createNoiseTexture() {
		Random rand = new Random(123);
		ByteBuffer noiseData = BufferUtils.createByteBuffer(NOISE_SIZE * NOISE_SIZE * 3);

		for (int i = 0; i < NOISE_SIZE * NOISE_SIZE; i++) {
			// Random rotation vector in tangent space (z=0)
			float x = rand.nextFloat() * 2.0f - 1.0f;
			float y = rand.nextFloat() * 2.0f - 1.0f;
			// Encode as unsigned byte [0,255] from [-1,1]
			noiseData.put((byte) ((int) ((x * 0.5f + 0.5f) * 255)));
			noiseData.put((byte) ((int) ((y * 0.5f + 0.5f) * 255)));
			noiseData.put((byte) 0);
		}
		noiseData.flip();

		noiseTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, noiseTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, NOISE_SIZE, NOISE_SIZE, 0,
			GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE, noiseData);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public void cleanup() {
		if (ssaoShader != null) { ssaoShader.cleanup(); ssaoShader = null; }
		if (blurShader != null) { blurShader.cleanup(); blurShader = null; }
		if (ssaoFBO != null) { ssaoFBO.cleanup(); ssaoFBO = null; }
		if (blurFBO != null) { blurFBO.cleanup(); blurFBO = null; }
		if (noiseTexture != 0) { GL11.glDeleteTextures(noiseTexture); noiseTexture = 0; }
		initialized = false;
	}
}
