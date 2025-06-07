package com.bestbudz.engine.gpu;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Thread-Safe GPU Context Manager
 *
 * Handles OpenGL context sharing across multiple threads safely,
 * persists through game loads, and prevents context conflicts.
 */
public class GPUContextManager {

	// ===== SINGLETON PATTERN =====
	private static volatile GPUContextManager instance;
	private static final Object instanceLock = new Object();

	// ===== CONTEXT STATE =====
	private volatile long primaryContext = 0;
	private volatile long currentContextWindow = 0;
	private final AtomicBoolean isInitialized = new AtomicBoolean(false);
	private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

	// ===== THREAD SAFETY =====
	private final ReentrantLock contextLock = new ReentrantLock(true); // Fair lock
	private final ConcurrentHashMap<Long, String> threadContextMap = new ConcurrentHashMap<>();
	private final AtomicLong activeContextThread = new AtomicLong(-1);

	// ===== CONTEXT SHARING =====
	private final ConcurrentHashMap<Long, Long> sharedContexts = new ConcurrentHashMap<>();
	private volatile boolean contextSharingEnabled = true;

	// ===== PERFORMANCE TRACKING =====
	private volatile long totalContextSwitches = 0;
	private volatile long lastContextSwitchTime = 0;
	private final AtomicLong contextOperationCount = new AtomicLong(0);

	private GPUContextManager() {
		// Private constructor for singleton
	}

	public static GPUContextManager getInstance() {
		if (instance == null) {
			synchronized (instanceLock) {
				if (instance == null) {
					instance = new GPUContextManager();
				}
			}
		}
		return instance;
	}

	/**
	 * Initialize the GPU context system
	 * This should be called once at application startup
	 */
	public boolean initialize() {
		if (isInitialized.get()) {
			System.out.println("[GPU Context] Already initialized");
			return true;
		}

		contextLock.lock();
		try {
			if (isInitialized.get()) {
				return true; // Double-check after acquiring lock
			}

			System.out.println("[GPU Context] Initializing context manager...");

			// Set up GLFW error callback
			GLFWErrorCallback.createPrint(System.err).set();

			// Initialize GLFW
			if (!GLFW.glfwInit()) {
				System.err.println("[GPU Context] Failed to initialize GLFW");
				return false;
			}

			// Create primary context
			if (!createPrimaryContext()) {
				System.err.println("[GPU Context] Failed to create primary context");
				cleanup();
				return false;
			}

			// Initialize OpenGL
			if (!initializeOpenGL()) {
				System.err.println("[GPU Context] Failed to initialize OpenGL");
				cleanup();
				return false;
			}

			isInitialized.set(true);
			System.out.println("[GPU Context] ✅ Context manager initialized successfully");
			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] ❌ Initialization failed: " + e.getMessage());
			e.printStackTrace();
			cleanup();
			return false;
		} finally {
			contextLock.unlock();
		}
	}

	/**
	 * Create the primary OpenGL context
	 */
	private boolean createPrimaryContext() {
		try {
			// Configure GLFW for OpenGL 3.3 Core Profile
			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

			// Create hidden window for primary context
			primaryContext = GLFW.glfwCreateWindow(1, 1, "BestBudz GPU Context", 0, 0);
			if (primaryContext == MemoryUtil.NULL) {
				System.err.println("[GPU Context] Failed to create primary context window");
				return false;
			}

			// Make context current and create capabilities
			GLFW.glfwMakeContextCurrent(primaryContext);
			GL.createCapabilities();

			// Disable VSync for the hidden window
			GLFW.glfwSwapInterval(0);

			currentContextWindow = primaryContext;
			activeContextThread.set(Thread.currentThread().getId());
			threadContextMap.put(Thread.currentThread().getId(), "Primary");

			System.out.println("[GPU Context] Primary context created on thread: " + Thread.currentThread().getName());
			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] Error creating primary context: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Initialize OpenGL state
	 */
	private boolean initializeOpenGL() {
		try {
			// Check OpenGL version
			String version = GL11.glGetString(GL11.GL_VERSION);
			String vendor = GL11.glGetString(GL11.GL_VENDOR);
			String renderer = GL11.glGetString(GL11.GL_RENDERER);

			System.out.println("[GPU Context] OpenGL Version: " + version);
			System.out.println("[GPU Context] GPU Vendor: " + vendor);
			System.out.println("[GPU Context] GPU Renderer: " + renderer);

			// Set default OpenGL state
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL11.GL_CULL_FACE);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] Error initializing OpenGL: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Thread-safe context acquisition
	 * This ensures only one thread can use the OpenGL context at a time
	 */
	public ContextToken acquireContext() {
		return acquireContext("Unknown", 5000); // 5 second timeout
	}

	public ContextToken acquireContext(String operation) {
		return acquireContext(operation, 5000);
	}

	public ContextToken acquireContext(String operation, long timeoutMs) {
		if (!isInitialized.get() || isShuttingDown.get()) {
			System.err.println("[GPU Context] Cannot acquire context: not initialized or shutting down");
			return null;
		}

		long threadId = Thread.currentThread().getId();
		String threadName = Thread.currentThread().getName();

		try {
			// Try to acquire the lock with timeout
			if (!contextLock.tryLock(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)) {
				System.err.println("[GPU Context] ⚠️ Timeout acquiring context for " + operation +
					" on thread " + threadName + " (waited " + timeoutMs + "ms)");
				return null;
			}

			// Make context current if it's not already current on this thread
			if (activeContextThread.get() != threadId) {
				if (!makeContextCurrentSafe()) {
					contextLock.unlock();
					return null;
				}

				activeContextThread.set(threadId);
				totalContextSwitches++;
				lastContextSwitchTime = System.currentTimeMillis();
			}

			threadContextMap.put(threadId, operation);
			contextOperationCount.incrementAndGet();

			return new ContextToken(this, threadId, operation);

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			System.err.println("[GPU Context] Context acquisition interrupted for " + operation);
			return null;
		} catch (Exception e) {
			System.err.println("[GPU Context] Error acquiring context for " + operation + ": " + e.getMessage());
			if (contextLock.isHeldByCurrentThread()) {
				contextLock.unlock();
			}
			return null;
		}
	}

	/**
	 * Release context (called by ContextToken)
	 */
	void releaseContext(long threadId, String operation) {
		try {
			if (contextLock.isHeldByCurrentThread()) {
				threadContextMap.remove(threadId);
				contextLock.unlock();
			} else {
				System.err.println("[GPU Context] ⚠️ Attempted to release context not held by current thread: " + operation);
			}
		} catch (Exception e) {
			System.err.println("[GPU Context] Error releasing context for " + operation + ": " + e.getMessage());
		}
	}

	/**
	 * Safely make context current
	 */
	private boolean makeContextCurrentSafe() {
		try {
			if (primaryContext == 0) {
				System.err.println("[GPU Context] Primary context is null");
				return false;
			}

			GLFW.glfwMakeContextCurrent(primaryContext);
			currentContextWindow = primaryContext;
			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] Error making context current: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Check if context is current on calling thread
	 */
	public boolean isContextCurrent() {
		if (!isInitialized.get()) {
			return false;
		}

		return activeContextThread.get() == Thread.currentThread().getId() &&
			GLFW.glfwGetCurrentContext() == primaryContext;
	}

	/**
	 * Create a shared context for a specific thread (advanced usage)
	 */
	public long createSharedContext(String threadName) {
		if (!isInitialized.get() || !contextSharingEnabled) {
			return 0;
		}

		contextLock.lock();
		try {
			// Create shared context window
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
			long sharedWindow = GLFW.glfwCreateWindow(1, 1, "Shared Context: " + threadName, 0, primaryContext);

			if (sharedWindow != MemoryUtil.NULL) {
				sharedContexts.put(Thread.currentThread().getId(), sharedWindow);
				System.out.println("[GPU Context] Created shared context for thread: " + threadName);
				return sharedWindow;
			} else {
				System.err.println("[GPU Context] Failed to create shared context for: " + threadName);
				return 0;
			}

		} catch (Exception e) {
			System.err.println("[GPU Context] Error creating shared context: " + e.getMessage());
			return 0;
		} finally {
			contextLock.unlock();
		}
	}

	/**
	 * Persist context through game state changes
	 * This is called during game loads to ensure context survives
	 */
	public void persistThroughGameLoad() {
		if (!isInitialized.get()) {
			return;
		}

		System.out.println("[GPU Context] Persisting context through game load...");

		contextLock.lock();
		try {
			// Ensure context is still valid
			if (primaryContext != 0 && GLFW.glfwWindowShouldClose(primaryContext)) {
				System.out.println("[GPU Context] Primary context window marked for closure, recreating...");
				recreatePrimaryContext();
			}

			// Clear any stale thread mappings
			threadContextMap.clear();
			activeContextThread.set(-1);

			// Make context current on the current thread
			makeContextCurrentSafe();
			activeContextThread.set(Thread.currentThread().getId());
			threadContextMap.put(Thread.currentThread().getId(), "GameLoad");

			System.out.println("[GPU Context] ✅ Context persisted through game load");

		} catch (Exception e) {
			System.err.println("[GPU Context] Error persisting context: " + e.getMessage());
		} finally {
			contextLock.unlock();
		}
	}

	/**
	 * Recreate primary context if needed
	 */
	private void recreatePrimaryContext() {
		try {
			// Clean up old context
			if (primaryContext != 0) {
				GLFW.glfwDestroyWindow(primaryContext);
				primaryContext = 0;
			}

			// Create new primary context
			createPrimaryContext();
			initializeOpenGL();

		} catch (Exception e) {
			System.err.println("[GPU Context] Error recreating primary context: " + e.getMessage());
		}
	}

	/**
	 * Safe cleanup of GPU resources before context destruction
	 */
	public void cleanupGPUResources() {
		contextLock.lock();
		try {
			if (primaryContext != 0) {
				makeContextCurrentSafe();

				// Clean up GPU shaders and resources
				try {
					GPUShaders.cleanup();
				} catch (Exception e) {
					System.err.println("[GPU Context] Error cleaning up shaders: " + e.getMessage());
				}

				System.out.println("[GPU Context] GPU resources cleaned up");
			}
		} finally {
			contextLock.unlock();
		}
	}

	/**
	 * Shutdown the context manager
	 */
	public void shutdown() {
		if (!isInitialized.get()) {
			return;
		}

		isShuttingDown.set(true);

		contextLock.lock();
		try {
			System.out.println("[GPU Context] Shutting down context manager...");

			// Clean up GPU resources first
			cleanupGPUResources();

			// Clean up shared contexts
			for (Long window : sharedContexts.values()) {
				if (window != 0) {
					GLFW.glfwDestroyWindow(window);
				}
			}
			sharedContexts.clear();

			// Clean up primary context
			cleanup();

			// Clear state
			threadContextMap.clear();
			activeContextThread.set(-1);
			isInitialized.set(false);

			System.out.println("[GPU Context] ✅ Context manager shutdown complete");

		} finally {
			contextLock.unlock();
			isShuttingDown.set(false);
		}
	}

	/**
	 * Internal cleanup
	 */
	private void cleanup() {
		try {
			if (primaryContext != 0) {
				GLFW.glfwDestroyWindow(primaryContext);
				primaryContext = 0;
			}

			GLFW.glfwTerminate();

		} catch (Exception e) {
			System.err.println("[GPU Context] Error during cleanup: " + e.getMessage());
		}
	}

	/**
	 * Get context statistics for debugging
	 */
	public String getContextStatistics() {
		return String.format("GPU Context Stats - Active Thread: %d, Context Switches: %d, " +
				"Operations: %d, Threads Using Context: %d, Initialized: %s",
			activeContextThread.get(),
			totalContextSwitches,
			contextOperationCount.get(),
			threadContextMap.size(),
			isInitialized.get());
	}

	/**
	 * Context Token for RAII-style context management
	 */
	public static class ContextToken implements AutoCloseable {
		private final GPUContextManager manager;
		private final long threadId;
		private final String operation;
		private final long acquiredTime;
		private volatile boolean released = false;

		ContextToken(GPUContextManager manager, long threadId, String operation) {
			this.manager = manager;
			this.threadId = threadId;
			this.operation = operation;
			this.acquiredTime = System.currentTimeMillis();
		}

		@Override
		public void close() {
			if (!released) {
				released = true;
				long duration = System.currentTimeMillis() - acquiredTime;
				if (duration > 1000) { // Log long-held contexts
					System.out.println("[GPU Context] Long context hold: " + operation + " held for " + duration + "ms");
				}
				manager.releaseContext(threadId, operation);
			}
		}

		public boolean isValid() {
			return !released && manager.isInitialized.get() && !manager.isShuttingDown.get();
		}
	}
}