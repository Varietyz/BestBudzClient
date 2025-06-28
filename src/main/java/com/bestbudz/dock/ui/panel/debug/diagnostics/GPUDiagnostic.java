package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPUMonitor;
import com.bestbudz.engine.core.GameState;
import org.lwjgl.opengl.GL11;

public class GPUDiagnostic extends BaseDiagnostic {

	public GPUDiagnostic() {
		super("[GPU Acceleration]", DiagnosticStyle.CATEGORY_GPU, DiagnosticStyle.CATEGORY_GPU);
		setUpdateInterval(2000); // Update every 2 seconds for GPU stats
	}

	@Override
	protected void onInitialize() {
		// No special initialization needed
	}

	@Override
	protected void collectData() {
		// GPU Engine Status
		boolean gpuEnabled = GPURenderingEngine.isEnabled();
		boolean gpuInitialized = GPURenderingEngine.initialized;

		addRow("GPU Engine", gpuEnabled ? "Enabled" : "Disabled",
			gpuEnabled ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_WARNING);

		addRow("Initialized", gpuInitialized ? "Yes" : "No",
			gpuInitialized ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_CRITICAL);

		// Context Manager Status
		GPUContextManager contextManager = null;
		try {
			contextManager = GPUContextManager.getInstance();
			boolean contextCurrent = contextManager.isContextCurrent();

			addRow("Context", contextCurrent ? "Current" : "Not Current",
				contextCurrent ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_WARNING);
		} catch (Exception e) {
			addRow("Context", "Error", DiagnosticStyle.STATUS_CRITICAL);
		}

		// Shader Status
		//boolean shadersInit = GPUShaders.isInitialized();
		//addRow("Shaders", shadersInit ? "Loaded" : "Not Loaded",
		//	shadersInit ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_CRITICAL);

		// OpenGL Information (cached - safe for UI thread)
		if (gpuEnabled && gpuInitialized) {
			// THREAD-SAFE: Get cached OpenGL info instead of direct GL calls
			// This avoids context conflicts between UI thread and GameLoop thread
			try {
				String gpuStats = GPURenderingEngine.getGPUStatistics();
				if (gpuStats != null && gpuStats.contains("OpenGL")) {
					// Extract cached version info from statistics
					addRow("OpenGL", "Available", DiagnosticStyle.STATUS_GOOD);
				} else {
					addRow("OpenGL", "Unknown", DiagnosticStyle.TEXT_MUTED);
				}

				// Show generic GPU info without GL calls
				addRow("GPU Info", "Context Active", DiagnosticStyle.TEXT_MUTED);

			} catch (Exception e) {
				addRow("OpenGL", "Info N/A", DiagnosticStyle.TEXT_MUTED);
			}
		}

		// Framebuffer Information
		if (gpuEnabled) {
			int fbWidth = GPURenderingEngine.getWidth();
			int fbHeight = GPURenderingEngine.getHeight();
			addRow("Framebuffer", fbWidth + "x" + fbHeight, DiagnosticStyle.TEXT_MUTED);
		}

		// GPU Monitor Health
		try {
			GPUMonitor monitor = GPUMonitor.getInstance();
			boolean healthy = monitor.isHealthy();
			String healthStatus = String.valueOf(monitor.getHealthStatus());

			addRow("Health", healthy ? "Good" : "Issues",
				healthy ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_WARNING);

			if (!healthy && healthStatus != null && !healthStatus.isEmpty()) {
				String truncatedStatus = DiagnosticStyle.truncateString(healthStatus, 15);
				addRow("Issues", truncatedStatus, DiagnosticStyle.STATUS_WARNING);
			}
		} catch (Exception e) {
			addRow("Monitor", "Unavailable", DiagnosticStyle.STATUS_WARNING);
		}

		// GameState GPU Status
		try {
			String gpuStatus = GameState.getGPUStatus();
			if (gpuStatus != null && !gpuStatus.isEmpty()) {
				// Extract key info from status string
				if (gpuStatus.contains("Valid: true")) {
					addRow("State Valid", "Yes", DiagnosticStyle.STATUS_GOOD);
				} else if (gpuStatus.contains("Valid: false")) {
					addRow("State Valid", "No", DiagnosticStyle.STATUS_WARNING);
				}

				// Extract failure count if present
				if (gpuStatus.contains("Failures: ")) {
					try {
						String failuresPart = gpuStatus.substring(gpuStatus.indexOf("Failures: ") + 10);
						String failures = failuresPart.split(",")[0].trim();
						addRow("Failures", failures,
							failures.equals("0/3") ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_WARNING);
					} catch (Exception ignored) {}
				}
			}
		} catch (Exception e) {
			addRow("Game State", "Error", DiagnosticStyle.STATUS_WARNING);
		}

		// Performance Statistics
		if (gpuEnabled) {
			try {
				String gpuStats = GPURenderingEngine.getGPUStatistics();
				if (gpuStats != null) {
					// Try to extract framebuffer info
					if (gpuStats.contains("Framebuffer: ")) {
						String fbInfo = gpuStats.substring(gpuStats.indexOf("Framebuffer: ") + 13);
						String fbSize = fbInfo.split(",")[0].trim();
						if (!fbSize.isEmpty()) {
							addRow("FB Active", fbSize, DiagnosticStyle.TEXT_MUTED);
						}
					}
				}
			} catch (Exception e) {
				// Ignore statistics errors
			}
		}

		// Last error information
		if (!gpuEnabled || !gpuInitialized) {
			try {
				String lastError = GPURenderingEngine.getLastError();
				if (lastError != null && !lastError.isEmpty()) {
					String truncatedError = DiagnosticStyle.truncateString(lastError, 15);
					addRow("Last Error", truncatedError, DiagnosticStyle.STATUS_CRITICAL);
				}
			} catch (Exception e) {
				// Ignore error retrieval errors
			}
		}
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.GPU;
	}

	@Override
	public int getPriority() {
		return 2; // High priority for GPU diagnostics
	}
}