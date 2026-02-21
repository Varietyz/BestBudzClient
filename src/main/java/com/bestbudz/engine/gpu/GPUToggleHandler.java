package com.bestbudz.engine.gpu;

import com.bestbudz.engine.config.EngineConfig;

/**
 * Handles GPU rendering toggle from the dock settings panel.
 * Manages the lifecycle of GPU initialization and shutdown at runtime.
 */
public class GPUToggleHandler {

	private static volatile boolean gpuStartupPending = false;
	private static volatile boolean gpuShutdownPending = false;

	/**
	 * Called from the Swing EDT when the GPU toggle changes.
	 * Schedules the actual init/shutdown to happen on the game loop thread
	 * (since OpenGL context is bound to the game thread).
	 */
	public static void onGPUToggled(boolean enabled) {
		if (enabled) {
			gpuStartupPending = true;
			gpuShutdownPending = false;
			System.out.println("[GPUToggle] GPU rendering enabled - will initialize on next game tick");
		} else {
			gpuShutdownPending = true;
			gpuStartupPending = false;
			System.out.println("[GPUToggle] GPU rendering disabled - will shutdown on next game tick");
		}
	}

	/**
	 * Called from the game loop thread each tick to process pending GPU state changes.
	 * Must run on the same thread that owns the OpenGL context.
	 */
	public static void processPendingToggle() {
		if (gpuStartupPending) {
			gpuStartupPending = false;
			startGPU();
		}

		if (gpuShutdownPending) {
			gpuShutdownPending = false;
			stopGPU();
		}
	}

	private static void startGPU() {
		if (GPURenderingEngine.isEnabled()) {
			System.out.println("[GPUToggle] GPU already running");
			return;
		}

		System.out.println("[GPUToggle] Starting GPU rendering...");
		EngineConfig.ENABLE_GPU = true;

		try {
			// Initialize context manager if needed
			GPUContextManager contextManager = GPUContextManager.getInstance();
			if (!contextManager.isContextCurrent()) {
				if (!contextManager.initialize()) {
					System.err.println("[GPUToggle] Context manager init failed");
					EngineConfig.ENABLE_GPU = false;
					return;
				}
			}

			// Acquire context and initialize rendering engine
			try (GPUContextManager.ContextToken ctx = contextManager.acquireContext("GPU Toggle Start", 5000)) {
				if (ctx == null) {
					System.err.println("[GPUToggle] Failed to acquire context");
					EngineConfig.ENABLE_GPU = false;
					return;
				}

				GPURenderingEngine.initialize();

				if (!GPURenderingEngine.isEnabled()) {
					System.err.println("[GPUToggle] Rendering engine init failed");
					EngineConfig.ENABLE_GPU = false;
					return;
				}

				// Initialize the GPU interface
				RS317GPUInterface.initialize();
			}

			System.out.println("[GPUToggle] GPU rendering started successfully");

			// Mark GPU state as valid so the rendering path is taken immediately
			com.bestbudz.engine.core.GameState.setGPUStateValid(true);

			// Force region reload so the scene re-renders with GPU
			com.bestbudz.engine.core.Client.loadingStage = 1;

		} catch (Exception e) {
			System.err.println("[GPUToggle] GPU startup failed: " + e.getMessage());
			e.printStackTrace();
			EngineConfig.ENABLE_GPU = false;
		}
	}

	private static void stopGPU() {
		if (!GPURenderingEngine.isEnabled() && !GPURenderingEngine.initialized) {
			System.out.println("[GPUToggle] GPU already stopped");
			return;
		}

		System.out.println("[GPUToggle] Stopping GPU rendering...");
		EngineConfig.ENABLE_GPU = false;

		try {
			RS317GPUInterface.cleanup();
			GPURenderingEngine.cleanup();
			System.out.println("[GPUToggle] GPU rendering stopped successfully");

			// Force region reload so the scene re-renders with CPU
			com.bestbudz.engine.core.Client.loadingStage = 1;

		} catch (Exception e) {
			System.err.println("[GPUToggle] GPU shutdown error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static boolean hasPendingToggle() {
		return gpuStartupPending || gpuShutdownPending;
	}
}
