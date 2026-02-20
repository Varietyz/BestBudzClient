package com.bestbudz.engine.gpu;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class GPUContextManager {

	private static volatile GPUContextManager instance;
	private static final Object instanceLock = new Object();

	private volatile long primaryContext = 0;
	private volatile long currentContextWindow = 0;
	private final AtomicBoolean isInitialized = new AtomicBoolean(false);
	private final AtomicBoolean isShuttingDown = new AtomicBoolean(false);

	private final ReentrantLock contextLock = new ReentrantLock(true);
	private final ConcurrentHashMap<Long, String> threadContextMap = new ConcurrentHashMap<>();
	private final AtomicLong activeContextThread = new AtomicLong(-1);

	private final ConcurrentHashMap<Long, Long> sharedContexts = new ConcurrentHashMap<>();
	private volatile boolean contextSharingEnabled = true;

	private volatile long totalContextSwitches = 0;
	private volatile long lastContextSwitchTime = 0;
	private final AtomicLong contextOperationCount = new AtomicLong(0);

	private GPUContextManager() {

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

	public boolean initialize() {
		if (isInitialized.get()) {
			System.out.println("[GPU Context] Already initialized");
			return true;
		}

		contextLock.lock();
		try {
			if (isInitialized.get()) {
				return true;
			}

			System.out.println("[GPU Context] Initializing context manager...");

			try {

				GLFWErrorCallback.createPrint(System.err).set();

				if (!GLFW.glfwInit()) {
					System.err.println("[GPU Context] Failed to initialize GLFW");

					GLFWErrorCallback errorCallback = GLFW.glfwSetErrorCallback((error, description) -> {
						System.err.println("[GPU Context] GLFW Error " + error + ": " + GLFWErrorCallback.getDescription(description));
					});

					return false;
				}

				System.out.println("[GPU Context] ✅ GLFW initialized successfully");

			} catch (Exception e) {
				System.err.println("[GPU Context] Exception during GLFW initialization: " + e.getMessage());
				e.printStackTrace();
				return false;
			}

			if (!createPrimaryContext()) {
				System.err.println("[GPU Context] Failed to getPooledStream primary context");
				cleanup();
				return false;
			}

			if (!initializeOpenGL()) {
				System.err.println("[GPU Context] Failed to initialize OpenGL");
				cleanup();
				return false;
			}

			if (!validateInitialization()) {
				System.err.println("[GPU Context] Initialization validation failed");
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

	private boolean createPrimaryContext() {
		try {

			GLFW.glfwDefaultWindowHints();
			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
			GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);

			GLFW.glfwWindowHint(GLFW.GLFW_DOUBLEBUFFER, GLFW.GLFW_TRUE);
			GLFW.glfwWindowHint(GLFW.GLFW_DEPTH_BITS, 24);
			GLFW.glfwWindowHint(GLFW.GLFW_STENCIL_BITS, 8);

			primaryContext = GLFW.glfwCreateWindow(1, 1, "BestBudz GPU Context", 0, 0);
			if (primaryContext == MemoryUtil.NULL) {
				System.err.println("[GPU Context] Failed to getPooledStream primary context window");

				GLFW.glfwSetErrorCallback((error, description) -> {
					System.err.println("[GPU Context] GLFW Window Creation Error " + error + ": " +
						GLFWErrorCallback.getDescription(description));
				});

				return false;
			}

			System.out.println("[GPU Context] ✅ Primary context window created successfully");

			GLFW.glfwMakeContextCurrent(primaryContext);

			try {
				GL.createCapabilities();
				System.out.println("[GPU Context] ✅ OpenGL capabilities created");
			} catch (Exception e) {
				System.err.println("[GPU Context] Failed to getPooledStream OpenGL capabilities: " + e.getMessage());
				return false;
			}

			GLFW.glfwSwapInterval(0);

			currentContextWindow = primaryContext;
			activeContextThread.set(Thread.currentThread().getId());
			threadContextMap.put(Thread.currentThread().getId(), "Primary");

			System.out.println("[GPU Context] Primary context created on thread: " + Thread.currentThread().getName());
			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] Error creating primary context: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean initializeOpenGL() {
		try {

			long currentContext = GLFW.glfwGetCurrentContext();
			if (currentContext != primaryContext) {
				System.err.println("[GPU Context] Context mismatch during OpenGL initialization");
				return false;
			}

			String version = null;
			String vendor = null;
			String renderer = null;

			try {
				version = GL11.glGetString(GL11.GL_VERSION);
				vendor = GL11.glGetString(GL11.GL_VENDOR);
				renderer = GL11.glGetString(GL11.GL_RENDERER);
			} catch (Exception e) {
				System.err.println("[GPU Context] Failed to get OpenGL info: " + e.getMessage());
				return false;
			}

			if (version == null || vendor == null || renderer == null) {
				System.err.println("[GPU Context] OpenGL info is null - context may be invalid");
				return false;
			}

			System.out.println("[GPU Context] OpenGL Version: " + version);
			System.out.println("[GPU Context] GPU Vendor: " + vendor);
			System.out.println("[GPU Context] GPU Renderer: " + renderer);

			if (!validateOpenGLVersion(version)) {
				System.err.println("[GPU Context] Insufficient OpenGL version");
				return false;
			}

			try {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				int error = GL11.glGetError();
				if (error != GL11.GL_NO_ERROR) {
					System.err.println("[GPU Context] OpenGL error during state setup: " + error);
					return false;
				}

			} catch (Exception e) {
				System.err.println("[GPU Context] Exception during OpenGL state setup: " + e.getMessage());
				return false;
			}

			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] Error initializing OpenGL: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean validateOpenGLVersion(String version) {
		try {
			if (version == null || version.isEmpty()) {
				return false;
			}

			String[] parts = version.split("\\.");
			if (parts.length < 2) {
				return false;
			}

			int major = Integer.parseInt(parts[0]);
			int minor = Integer.parseInt(parts[1].split(" ")[0]);

			return major > 3 || (major == 3 && minor >= 3);

		} catch (Exception e) {
			System.err.println("[GPU Context] Error parsing OpenGL version: " + e.getMessage());
			return false;
		}
	}

	private boolean validateInitialization() {
		try {

			if (primaryContext == 0) {
				System.err.println("[GPU Context] Primary context is 0");
				return false;
			}

			long currentContext = GLFW.glfwGetCurrentContext();
			if (currentContext != primaryContext) {
				System.err.println("[GPU Context] Context not current after initialization");
				return false;
			}

			try {
				GL11.glGetError();
				int[] viewport = new int[4];
				GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewport);

				int error = GL11.glGetError();
				if (error != GL11.GL_NO_ERROR) {
					System.err.println("[GPU Context] OpenGL error during validation: " + error);
					return false;
				}
			} catch (Exception e) {
				System.err.println("[GPU Context] Exception during OpenGL validation: " + e.getMessage());
				return false;
			}

			System.out.println("[GPU Context] ✅ Initialization validation passed");
			return true;

		} catch (Exception e) {
			System.err.println("[GPU Context] Error during validation: " + e.getMessage());
			return false;
		}
	}

	public ContextToken acquireContext() {
		return acquireContext("Unknown", 5000);
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

			if (!contextLock.tryLock(timeoutMs, java.util.concurrent.TimeUnit.MILLISECONDS)) {
				System.err.println("[GPU Context] ⚠️ Timeout acquiring context for " + operation +
					" on thread " + threadName + " (waited " + timeoutMs + "ms)");
				return null;
			}

			if (primaryContext == 0) {
				System.err.println("[GPU Context] Primary context is null during acquisition");
				contextLock.unlock();
				return null;
			}

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

	public boolean isContextCurrent() {
		if (!isInitialized.get()) {
			return false;
		}

		return activeContextThread.get() == Thread.currentThread().getId() &&
			GLFW.glfwGetCurrentContext() == primaryContext;
	}

	public long createSharedContext(String threadName) {
		if (!isInitialized.get() || !contextSharingEnabled) {
			return 0;
		}

		contextLock.lock();
		try {

			GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_FALSE);
			long sharedWindow = GLFW.glfwCreateWindow(1, 1, "Shared Context: " + threadName, 0, primaryContext);

			if (sharedWindow != MemoryUtil.NULL) {
				sharedContexts.put(Thread.currentThread().getId(), sharedWindow);
				System.out.println("[GPU Context] Created shared context for thread: " + threadName);
				return sharedWindow;
			} else {
				System.err.println("[GPU Context] Failed to getPooledStream shared context for: " + threadName);
				return 0;
			}

		} catch (Exception e) {
			System.err.println("[GPU Context] Error creating shared context: " + e.getMessage());
			return 0;
		} finally {
			contextLock.unlock();
		}
	}

	public void persistThroughGameLoad() {
		if (!isInitialized.get()) {
			return;
		}

		System.out.println("[GPU Context] Persisting context through game load...");

		contextLock.lock();
		try {

			if (primaryContext != 0 && GLFW.glfwWindowShouldClose(primaryContext)) {
				System.out.println("[GPU Context] Primary context window marked for closure, recreating...");
				recreatePrimaryContext();
			}

			threadContextMap.clear();
			activeContextThread.set(-1);

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

	private void recreatePrimaryContext() {
		try {

			if (primaryContext != 0) {
				GLFW.glfwDestroyWindow(primaryContext);
				primaryContext = 0;
			}

			createPrimaryContext();
			initializeOpenGL();

		} catch (Exception e) {
			System.err.println("[GPU Context] Error recreating primary context: " + e.getMessage());
		}
	}

	public void cleanupGPUResources() {
		contextLock.lock();
		try {
			if (primaryContext != 0) {
				makeContextCurrentSafe();

				try {

				} catch (Exception e) {
					System.err.println("[GPU Context] Error cleaning up shaders: " + e.getMessage());
				}

				System.out.println("[GPU Context] GPU resources cleaned up");
			}
		} finally {
			contextLock.unlock();
		}
	}

	public void shutdown() {
		if (!isInitialized.get()) {
			return;
		}

		isShuttingDown.set(true);

		contextLock.lock();
		try {
			System.out.println("[GPU Context] Shutting down context manager...");

			cleanupGPUResources();

			for (Long window : sharedContexts.values()) {
				if (window != 0) {
					GLFW.glfwDestroyWindow(window);
				}
			}
			sharedContexts.clear();

			cleanup();

			threadContextMap.clear();
			activeContextThread.set(-1);
			isInitialized.set(false);

			System.out.println("[GPU Context] ✅ Context manager shutdown complete");

		} finally {
			contextLock.unlock();
			isShuttingDown.set(false);
		}
	}

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

	public String getContextStatistics() {
		return String.format("GPU Context Stats - Active Thread: %d, Context Switches: %d, " +
				"Operations: %d, Threads Using Context: %d, Initialized: %s",
			activeContextThread.get(),
			totalContextSwitches,
			contextOperationCount.get(),
			threadContextMap.size(),
			isInitialized.get());
	}

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
				if (duration > 1000) {
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
