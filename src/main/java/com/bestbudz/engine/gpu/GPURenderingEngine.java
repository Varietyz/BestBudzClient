package com.bestbudz.engine.gpu;

import com.bestbudz.engine.gpu.postprocess.PostProcessPipeline;
import com.bestbudz.engine.gpu.postprocess.SkyRenderer;
import com.bestbudz.engine.gpu.scene.GPUSceneUploader;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class GPURenderingEngine {

	private static boolean gpuEnabled = false;
	public static boolean initialized = false;
	private static String lastError = "";

	private static int currentFramebuffer = 0;
	private static int currentColorTexture = 0;
	private static int currentDepthTexture = 0;
	private static int currentWidth = 1280;
	private static int currentHeight = 720;

	private static GPUContextManager contextManager;

	public static void initialize() {
		if (initialized) {
			System.out.println("[GPU] Already initialized, skipping...");
			return;
		}

		System.out.println("[GPU] Initializing GPU rendering pipeline...");

		try {

			contextManager = GPUContextManager.getInstance();
			if (!contextManager.initialize()) {
				throw new RuntimeException("Failed to initialize GPU context manager");
			}

			try (GPUContextManager.ContextToken context = contextManager.acquireContext("GPU Initialization")) {
				if (context == null) {
					throw new RuntimeException("Failed to acquire GPU context for initialization");
				}

				setupInitialOpenGLState();
				createInitialFramebuffer();

				if (!GPUModelRenderer.initialize()) {
					System.err.println("[GPU] GPUModelRenderer init failed, continuing without model rendering");
				}

				if (!GPUTextureManager.initialize()) {
					System.err.println("[GPU] GPUTextureManager init failed, continuing without textures");
				}

				if (!GPUSceneUploader.initialize()) {
					System.err.println("[GPU] GPUSceneUploader init failed, continuing without terrain upload");
				}

				if (!com.bestbudz.engine.gpu.scene.GPUStaticScene.initialize()) {
					System.err.println("[GPU] GPUStaticScene init failed, continuing without static object upload");
				}

				if (!SkyRenderer.initialize()) {
					System.err.println("[GPU] SkyRenderer init failed, continuing without sky");
				}

				if (!PostProcessPipeline.initialize(currentWidth, currentHeight)) {
					System.err.println("[GPU] PostProcessPipeline init failed, continuing without post-processing");
				}
			}

			gpuEnabled = true;
			initialized = true;

			System.out.println("[GPU] GPU rendering enabled successfully");

		} catch (Exception e) {
			gpuEnabled = false;
			lastError = e.getMessage();
			System.err.println("❌ [GPU] GPU initialization failed: " + e.getMessage());
			System.err.println("❌ [GPU] Falling back to CPU rendering (no functionality lost)");
			e.printStackTrace();
		}
	}

	private static void setupInitialOpenGLState() {

		String version = GL11.glGetString(GL11.GL_VERSION);
		String vendor = GL11.glGetString(GL11.GL_VENDOR);
		String renderer = GL11.glGetString(GL11.GL_RENDERER);

		System.out.println("[GPU] OpenGL Version: " + version);
		System.out.println("[GPU] GPU Vendor: " + vendor);
		System.out.println("[GPU] GPU Renderer: " + renderer);

		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private static void createInitialFramebuffer() {
		createFramebuffer(currentWidth, currentHeight);
	}

	public static GPUContextManager.ContextToken acquireContext(String operation) {
		if (!isEnabled()) {
			return null;
		}
		return contextManager.acquireContext(operation);
	}

	public static boolean isContextCurrent() {
		return isEnabled() && contextManager.isContextCurrent();
	}

	public static boolean makeContextCurrent() {
		if (!isEnabled()) {
			return false;
		}
		return contextManager.isContextCurrent();
	}

	public static void createFramebuffer(int width, int height) {
		if (!gpuEnabled) {
			return;
		}

		try (GPUContextManager.ContextToken context = acquireContext("Create Framebuffer")) {
			if (context == null) {
				System.err.println("[GPU] Failed to acquire context for framebuffer creation");
				return;
			}

			createFramebufferInternal(width, height);

		} catch (Exception e) {
			System.err.println("[GPU] Error creating framebuffer: " + e.getMessage());
		}
	}

	private static void createFramebufferInternal(int width, int height) {

		if (currentFramebuffer != 0) {
			GL30.glDeleteFramebuffers(currentFramebuffer);
			GL11.glDeleteTextures(currentColorTexture);
			GL11.glDeleteTextures(currentDepthTexture);
		}

		currentWidth = width;
		currentHeight = height;

		currentFramebuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFramebuffer);

		currentColorTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentColorTexture);
		// RGBA16F for HDR light accumulation (Phase 5 lighting pipeline)
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL30.GL_RGBA16F, width, height, 0,
			GL11.GL_RGBA, GL11.GL_FLOAT, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
			GL11.GL_TEXTURE_2D, currentColorTexture, 0);

		currentDepthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentDepthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0,
			GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
			GL11.GL_TEXTURE_2D, currentDepthTexture, 0);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer not complete!");
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		System.out.println("[GPU] Created framebuffer: " + width + "x" + height);
	}

	public static void bindFramebuffer() {
		if (!gpuEnabled) {
			return;
		}

		if (!contextManager.isContextCurrent()) {
			System.err.println("[GPU] ⚠️ bindFramebuffer called without current context");
			return;
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFramebuffer);
		GL11.glViewport(0, 0, currentWidth, currentHeight);
	}

	public static void unbindFramebuffer() {
		if (!gpuEnabled) {
			return;
		}

		if (!contextManager.isContextCurrent()) {
			System.err.println("[GPU] ⚠️ unbindFramebuffer called without current context");
			return;
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public static void clear() {
		if (!gpuEnabled) {
			return;
		}

		if (!contextManager.isContextCurrent()) {
			System.err.println("[GPU] ⚠️ clear called without current context");
			return;
		}

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	public static int getFramebufferId() {
		return currentFramebuffer;
	}

	public static int getColorTexture() {
		return currentColorTexture;
	}

	public static int getDepthTexture() {
		return currentDepthTexture;
	}

	public static int getWidth() {
		return currentWidth;
	}

	public static int getHeight() {
		return currentHeight;
	}

	public static boolean isEnabled() {
		return gpuEnabled && initialized && contextManager != null;
	}

	public static String getLastError() {
		return lastError;
	}

	public static void resize(int width, int height) {
		if (!gpuEnabled) {
			return;
		}

		System.out.println("[GPU] Resizing GPU framebuffer from " + currentWidth + "x" + currentHeight +
			" to " + width + "x" + height);

		if (width != currentWidth || height != currentHeight) {

			if (!contextManager.isContextCurrent()) {
				System.err.println("[GPU] ⚠️ resize() called without current context");
				return;
			}

			try {
				createFramebufferInternal(width, height);
				PostProcessPipeline.resize(width, height);
				System.out.println("[GPU] ✅ Framebuffer resized successfully to " + width + "x" + height);
			} catch (Exception e) {
				System.err.println("[GPU] ❌ Error resizing framebuffer: " + e.getMessage());
				e.printStackTrace();
			}
		} else {
			System.out.println("[GPU] No resize needed - dimensions unchanged");
		}
	}

	public static void persistThroughGameLoad() {
		if (!isEnabled()) {
			return;
		}

		System.out.println("[GPU] Persisting GPU state through game load...");

		try {

			contextManager.persistThroughGameLoad();

			try (GPUContextManager.ContextToken context = acquireContext("Game Load Persistence")) {
				if (context != null) {

					if (currentFramebuffer != 0) {
						GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFramebuffer);
						int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
						if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
							System.out.println("[GPU] Framebuffer invalid after game load, recreating...");
							createFramebufferInternal(currentWidth, currentHeight);
						}
						GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
					}

				}
			}

			System.out.println("[GPU] ✅ GPU state persisted through game load");

		} catch (Exception e) {
			System.err.println("[GPU] Error persisting through game load: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static void cleanup() {
		if (!initialized) {
			return;
		}

		System.out.println("[GPU] Starting GPU cleanup...");

		try {

			try (GPUContextManager.ContextToken context = acquireContext("Cleanup")) {
				if (context != null) {
					cleanupGPUResourcesInternal();
				}
			}

			if (contextManager != null) {
				contextManager.shutdown();
				contextManager = null;
			}

			System.out.println("[GPU] ✅ GPU cleanup completed");

		} catch (Exception e) {
			System.err.println("[GPU] Error during cleanup: " + e.getMessage());
		} finally {
			gpuEnabled = false;
			initialized = false;
		}
	}

	private static void cleanupGPUResourcesInternal() {
		try {
			PostProcessPipeline.cleanup();
			SkyRenderer.cleanup();
			com.bestbudz.engine.gpu.scene.GPUStaticScene.cleanup();
			GPUSceneUploader.cleanup();
			GPUTextureManager.cleanup();
			GPUModelRenderer.cleanup();

			if (currentFramebuffer != 0) {
				GL30.glDeleteFramebuffers(currentFramebuffer);
				currentFramebuffer = 0;
			}

			if (currentColorTexture != 0) {
				GL11.glDeleteTextures(currentColorTexture);
				currentColorTexture = 0;
			}

			if (currentDepthTexture != 0) {
				GL11.glDeleteTextures(currentDepthTexture);
				currentDepthTexture = 0;
			}

			System.out.println("[GPU] GPU resources cleaned up");

		} catch (Exception e) {
			System.err.println("[GPU] Error cleaning up GPU resources: " + e.getMessage());
		}
	}

	public static String getGPUStatistics() {
		if (contextManager != null) {
			return String.format("GPU Stats - Enabled: %s, Framebuffer: %dx%d, %s",
				gpuEnabled,
				currentWidth,
				currentHeight,
				contextManager.getContextStatistics());
		} else {
			return "GPU Stats - Context Manager: NULL";
		}
	}

	public static boolean emergencyReset() {
		System.out.println("[GPU] Performing emergency reset...");

		try {

			gpuEnabled = false;

			initialize();

			if (isEnabled()) {
				System.out.println("[GPU] ✅ Emergency reset successful");
				return true;
			} else {
				System.err.println("[GPU] ❌ Emergency reset failed");
				return false;
			}

		} catch (Exception e) {
			System.err.println("[GPU] Emergency reset error: " + e.getMessage());
			return false;
		}
	}
}
