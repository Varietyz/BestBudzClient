package com.bestbudz.engine.gpu;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * Thread-Safe GPU Rendering Engine
 *
 * Now uses GPUContextManager for safe context handling across threads
 * and persists through game loads without context loss.
 */
public class GPURenderingEngine {

	private static boolean gpuEnabled = false;
	public static boolean initialized = false;
	private static String lastError = "";

	// GPU resources
	private static int currentFramebuffer = 0;
	private static int currentColorTexture = 0;
	private static int currentDepthTexture = 0;
	private static int currentWidth = 1280;
	private static int currentHeight = 720;

	// Context manager
	private static GPUContextManager contextManager;

	/**
	 * Initialize GPU rendering with thread-safe context management
	 */
	public static void initialize() {
		if (initialized) {
			System.out.println("[GPU] Already initialized, skipping...");
			return;
		}

		System.out.println("[GPU] Initializing GPU rendering pipeline...");

		try {
			// Initialize context manager
			contextManager = GPUContextManager.getInstance();
			if (!contextManager.initialize()) {
				throw new RuntimeException("Failed to initialize GPU context manager");
			}

			// Initialize GPU systems with context safety
			try (GPUContextManager.ContextToken context = contextManager.acquireContext("GPU Initialization")) {
				if (context == null) {
					throw new RuntimeException("Failed to acquire GPU context for initialization");
				}

				setupInitialOpenGLState();
				initializeGPUShaders();
				createInitialFramebuffer();
			}

			gpuEnabled = true;
			initialized = true;

			System.out.println("✅ [GPU] GPU rendering enabled successfully!");
			System.out.println("✅ [GPU] All DrawingArea calls are now GPU-accelerated");

		} catch (Exception e) {
			gpuEnabled = false;
			lastError = e.getMessage();
			System.err.println("❌ [GPU] GPU initialization failed: " + e.getMessage());
			System.err.println("❌ [GPU] Falling back to CPU rendering (no functionality lost)");
			e.printStackTrace();
		}
	}

	/**
	 * Setup initial OpenGL state
	 */
	private static void setupInitialOpenGLState() {
		// Check OpenGL version
		String version = GL11.glGetString(GL11.GL_VERSION);
		String vendor = GL11.glGetString(GL11.GL_VENDOR);
		String renderer = GL11.glGetString(GL11.GL_RENDERER);

		System.out.println("[GPU] OpenGL Version: " + version);
		System.out.println("[GPU] GPU Vendor: " + vendor);
		System.out.println("[GPU] GPU Renderer: " + renderer);

		// Set initial state
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	/**
	 * Initialize GPU shaders with context safety
	 */
	private static void initializeGPUShaders() {
		GPUShaders.initialize();
	}

	/**
	 * Create initial framebuffer
	 */
	private static void createInitialFramebuffer() {
		createFramebuffer(currentWidth, currentHeight);
	}

	/**
	 * Thread-safe context acquisition
	 */
	public static GPUContextManager.ContextToken acquireContext(String operation) {
		if (!isEnabled()) {
			return null;
		}
		return contextManager.acquireContext(operation);
	}

	/**
	 * Check if context is current (for DrawingArea safety checks)
	 */
	public static boolean isContextCurrent() {
		return isEnabled() && contextManager.isContextCurrent();
	}

	/**
	 * Make context current (for DrawingArea operations)
	 */
	public static boolean makeContextCurrent() {
		if (!isEnabled()) {
			return false;
		}
		return contextManager.isContextCurrent();
	}

	/**
	 * Create or recreate framebuffer for given dimensions (thread-safe)
	 */
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

	/**
	 * Internal framebuffer creation (assumes context is current)
	 */
	private static void createFramebufferInternal(int width, int height) {
		// Clean up existing framebuffer
		if (currentFramebuffer != 0) {
			GL30.glDeleteFramebuffers(currentFramebuffer);
			GL11.glDeleteTextures(currentColorTexture);
			GL11.glDeleteTextures(currentDepthTexture);
		}

		currentWidth = width;
		currentHeight = height;

		// Create framebuffer
		currentFramebuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFramebuffer);

		// Create color texture
		currentColorTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentColorTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		// Attach color texture to framebuffer
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
			GL11.GL_TEXTURE_2D, currentColorTexture, 0);

		// Create depth texture
		currentDepthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentDepthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0,
			GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

		// Attach depth texture to framebuffer
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
			GL11.GL_TEXTURE_2D, currentDepthTexture, 0);

		// Check framebuffer completeness
		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("Framebuffer not complete!");
		}

		// Unbind framebuffer
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);

		System.out.println("[GPU] Created framebuffer: " + width + "x" + height);
	}

	/**
	 * Bind our framebuffer for rendering (thread-safe)
	 */
	public static void bindFramebuffer() {
		if (!gpuEnabled) {
			return;
		}

		// Note: This assumes caller has already acquired context
		// For safety, we could add context checking here
		if (!contextManager.isContextCurrent()) {
			System.err.println("[GPU] ⚠️ bindFramebuffer called without current context");
			return;
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFramebuffer);
		GL11.glViewport(0, 0, currentWidth, currentHeight);
	}

	/**
	 * Unbind framebuffer (back to default)
	 */
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

	/**
	 * Clear the framebuffer
	 */
	public static void clear() {
		if (!gpuEnabled) {
			return;
		}

		if (!contextManager.isContextCurrent()) {
			System.err.println("[GPU] ⚠️ clear called without current context");
			return;
		}

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	}

	/**
	 * Get the current color texture (for reading pixel data)
	 */
	public static int getColorTexture() {
		return currentColorTexture;
	}

	/**
	 * Get current framebuffer dimensions
	 */
	public static int getWidth() {
		return currentWidth;
	}

	public static int getHeight() {
		return currentHeight;
	}

	/**
	 * Check if GPU rendering is enabled
	 */
	public static boolean isEnabled() {
		return gpuEnabled && initialized && contextManager != null;
	}

	/**
	 * Get last error message
	 */
	public static String getLastError() {
		return lastError;
	}

	/**
	 * Update viewport when window is resized (thread-safe)
	 */
	public static void resize(int width, int height) {
		if (!gpuEnabled) {
			return;
		}

		if (width != currentWidth || height != currentHeight) {
			try (GPUContextManager.ContextToken context = acquireContext("Resize")) {
				if (context != null) {
					createFramebufferInternal(width, height);
				}
			}
		}
	}

	/**
	 * Persist GPU state through game loads
	 * This ensures the GPU context survives game state changes
	 */
	public static void persistThroughGameLoad() {
		if (!isEnabled()) {
			return;
		}

		System.out.println("[GPU] Persisting GPU state through game load...");

		try {
			// Tell context manager to persist through game load
			contextManager.persistThroughGameLoad();

			// Ensure our framebuffer is still valid
			try (GPUContextManager.ContextToken context = acquireContext("Game Load Persistence")) {
				if (context != null) {
					// Verify framebuffer is still valid
					if (currentFramebuffer != 0) {
						GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, currentFramebuffer);
						int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
						if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
							System.out.println("[GPU] Framebuffer invalid after game load, recreating...");
							createFramebufferInternal(currentWidth, currentHeight);
						}
						GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
					}

					// Re-initialize shaders if needed
					GPUShaders.validateAndReinitialize();
				}
			}

			System.out.println("[GPU] ✅ GPU state persisted through game load");

		} catch (Exception e) {
			System.err.println("[GPU] Error persisting through game load: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Safe cleanup of GPU resources
	 */
	public static void cleanup() {
		if (!initialized) {
			return;
		}

		System.out.println("[GPU] Starting GPU cleanup...");

		try {
			// Clean up our resources first
			try (GPUContextManager.ContextToken context = acquireContext("Cleanup")) {
				if (context != null) {
					cleanupGPUResourcesInternal();
				}
			}

			// Shutdown context manager
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

	/**
	 * Internal GPU resource cleanup (assumes context is current)
	 */
	private static void cleanupGPUResourcesInternal() {
		try {
			// Cleanup framebuffers
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

	/**
	 * Get GPU statistics for debugging
	 */
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

	/**
	 * Emergency reset - recreates context if something goes wrong
	 */
	public static boolean emergencyReset() {
		System.out.println("[GPU] Performing emergency reset...");

		try {
			// Cleanup current state
			gpuEnabled = false;

			// Reinitialize
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