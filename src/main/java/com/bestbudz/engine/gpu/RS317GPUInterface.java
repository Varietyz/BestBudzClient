package com.bestbudz.engine.gpu;

import com.bestbudz.rendering.model.Model;

public class RS317GPUInterface {

	private static boolean gpuEnabled = false;
	private static boolean initializationAttempted = false;
	private static GPUContextManager contextManager;

	public static boolean initialize() {
		if (gpuEnabled) {
			return true;
		}

		if (initializationAttempted) {
			return false;
		}

		initializationAttempted = true;

		try {
			contextManager = GPUContextManager.getInstance();

			if (contextManager == null) {
				System.err.println("[RS317 GPU] Context manager is null");
				return false;
			}

			// GPUModelRenderer is initialized by GPURenderingEngine.initialize()
			// Just verify it's ready
			if (!GPUModelRenderer.isInitialized()) {
				System.err.println("[RS317 GPU] GPUModelRenderer not initialized");
				return false;
			}

			gpuEnabled = true;
			System.out.println("[RS317 GPU] GPU rendering interface ready");
			return true;

		} catch (Exception e) {
			System.err.println("[RS317 GPU] Initialization failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isActive() {
		return gpuEnabled && contextManager != null && GPUModelRenderer.isInitialized()
			&& GPUModelRenderer.isInFrame();
	}

	/**
	 * Render a model at camera-relative position with the given yaw rotation.
	 * Called from Animable.render() and Model.render()/renderAtFixedPosition().
	 */
	public static void renderModel(Model model, int worldX, int worldY, int worldZ,
									int rotation) {
		if (!isActive()) return;

		GPUModelRenderer.renderModel(model, worldX, worldY, worldZ, rotation);
	}

	public static void setScreenSize(int width, int height) {
		// Reserved for future use
	}

	public static void cleanup() {
		if (gpuEnabled) {
			// GPUModelRenderer cleanup handled by GPURenderingEngine
			gpuEnabled = false;
		}
		initializationAttempted = false;
	}

	public static String getStatus() {
		if (!gpuEnabled) {
			return "GPU: Disabled" + (initializationAttempted ? " (Failed Init)" : " (Not Attempted)");
		}
		return "GPU: Active - Models: " + GPUModelRenderer.getModelsRendered() +
			", Tris: " + GPUModelRenderer.getTrianglesRendered();
	}
}
