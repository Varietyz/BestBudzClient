package com.bestbudz.engine.gpu;

import com.bestbudz.rendering.model.Model;

/**
 * RS317 GPU Interface - FIXED VERSION
 */
public class RS317GPUInterface {

	private static boolean gpuEnabled = false;
	private static boolean initializationAttempted = false;
	private static GPUContextManager contextManager;

	/**
	 * FIXED: Initialize GPU rendering system - INTEGRATED with existing system
	 */
	public static boolean initialize() {
		if (gpuEnabled) {
			System.out.println("[RS317 GPU] Already initialized, returning true");
			return true;
		}

		if (initializationAttempted) {
			System.out.println("[RS317 GPU] Initialization already attempted and failed");
			return false;
		}

		initializationAttempted = true;
		System.out.println("[RS317 GPU] Initializing GPU rendering system (INTEGRATED)...");

		try {
			// Get the existing context manager instance
			contextManager = GPUContextManager.getInstance();

			// FIXED: Check if context manager is properly initialized
			if (contextManager == null) {
				System.err.println("[RS317 GPU] Context manager is null");
				return false;
			}

			// FIXED: Don't check if context is current here - acquire it properly
			System.out.println("[RS317 GPU] Context manager found, acquiring context for model renderer...");

			// Use existing context to initialize model renderer
			try (GPUContextManager.ContextToken context =
					 contextManager.acquireContext("RS317 Model Renderer Init", 5000)) {

				if (context == null || !context.isValid()) {
					System.err.println("[RS317 GPU] Could not acquire context for model renderer initialization");
					return false;
				}

				System.out.println("[RS317 GPU] Context acquired, initializing model renderer...");

				// Initialize the model renderer using the existing context
				if (!RS317ModelRenderer.initialize()) {
					System.err.println("[RS317 GPU] Failed to initialize model renderer");
					return false;
				}

				System.out.println("[RS317 GPU] Model renderer initialized successfully");
			}

			gpuEnabled = true;
			System.out.println("[RS317 GPU] ✅ GPU rendering ready!");
			return true;

		} catch (Exception e) {
			System.err.println("[RS317 GPU] ❌ Initialization failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Check if GPU rendering is active
	 */
	public static boolean isActive() {
		return gpuEnabled && contextManager != null;
	}

	/**
	 * Render a 3D model using existing context system
	 */
	public static void renderModel(Model model, int worldX, int worldY, int worldZ,
								   int rotationX, int rotationY, int rotationZ,
								   int lightingModifier) {
		if (!isActive()) return;

		try (GPUContextManager.ContextToken context =
				 contextManager.acquireContext("Model Render", 1000)) {

			if (context != null && context.isValid()) {
				RS317ModelRenderer.renderModel(model, worldX, worldY, worldZ,
					rotationX, rotationY, rotationZ, lightingModifier);
			}
		} catch (Exception e) {
			System.err.println("[RS317 GPU] Error in renderModel: " + e.getMessage());
			// Don't disable GPU on single errors
		}
	}

	/**
	 * Set screen size for proper rendering
	 */
	public static void setScreenSize(int width, int height) {
		if (gpuEnabled) {
			System.out.println("[RS317 GPU] Screen size set to: " + width + "x" + height);
		}
	}

	/**
	 * Cleanup GPU resources
	 */
	public static void cleanup() {
		if (gpuEnabled) {
			System.out.println("[RS317 GPU] Shutting down GPU rendering...");

			try {
				RS317ModelRenderer.cleanup();
				gpuEnabled = false;
				System.out.println("[RS317 GPU] ✅ GPU rendering shutdown complete");

			} catch (Exception e) {
				System.err.println("[RS317 GPU] Error during cleanup: " + e.getMessage());
			}
		}
	}

	/**
	 * Get GPU status for debugging
	 */
	public static String getStatus() {
		if (!gpuEnabled) {
			return "GPU: Disabled" + (initializationAttempted ? " (Failed Init)" : " (Not Attempted)");
		}
		try {
			if (contextManager != null) {
				return "GPU: Active - " + contextManager.getContextStatistics();
			}
			return "GPU: Active (No Context Manager)";
		} catch (Exception e) {
			return "GPU: Error - " + e.getMessage();
		}
	}
}