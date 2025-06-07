package com.bestbudz.graphics.buffer;

import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import org.lwjgl.opengl.GL11;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

/**
 * Thread-Safe GPU-POWERED ImageProducer
 *
 * CRITICAL: This file completely replaces the original ImageProducer.java
 * Same package, same class name = ZERO refactoring needed anywhere in the project
 *
 * EXACT 1:1 API match with original CPU version, now with thread-safe GPU operations
 */
public final class ImageProducer {

	// EXACT same fields as original - 1:1 match
	public final float[] depthBuffer;
	public final int[] canvasRaster;
	public final int canvasWidth;
	public final int canvasHeight;
	private final BufferedImage bufferedImage;

	// GPU backend (hidden from existing code)
	private ByteBuffer gpuPixelBuffer;
	private boolean isGPUBacked = false;
	private long lastSyncTime = 0;
	private static final long SYNC_THROTTLE_MS = 16; // ~60 FPS max sync rate

	// Thread safety
	private final Object syncLock = new Object();
	private volatile boolean syncInProgress = false;

	/**
	 * EXACT same constructor signature and behavior as original
	 */
	public ImageProducer(int canvasWidth, int canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;

		// Always create the BufferedImage (same as original)
		depthBuffer = new float[canvasWidth * canvasHeight];
		bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

		// Initialize GPU backend if available
		initializeGPUBackend();

		// Call initDrawingArea() exactly like original
		initDrawingArea();
	}

	/**
	 * Thread-safe GPU backend initialization
	 */
	private void initializeGPUBackend() {
		if (!GPURenderingEngine.isEnabled()) {
			isGPUBacked = false;
			return;
		}

		try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("ImageProducer Init")) {
			if (context == null) {
				System.err.println("[GPU ImageProducer] Failed to acquire context for initialization");
				isGPUBacked = false;
				return;
			}

			gpuPixelBuffer = ByteBuffer.allocateDirect(canvasWidth * canvasHeight * 4);
			isGPUBacked = true;
			System.out.println("[GPU ImageProducer] Created GPU-backed ImageProducer: " + canvasWidth + "x" + canvasHeight);

		} catch (Exception e) {
			System.err.println("[GPU ImageProducer] Failed to create GPU backend, using CPU: " + e.getMessage());
			isGPUBacked = false;
		}
	}

	/**
	 * EXACT same method signature as original
	 */
	public void drawGraphics(int y, Graphics2D graphics, int x) {
		if (isGPUBacked && GPURenderingEngine.isEnabled()) {
			// GPU path: sync GPU data to BufferedImage, then draw
			syncGPUToBufferedImageIfNeeded();
		} else {
			// CPU path: BufferedImage already contains correct data from canvasRaster
		}

		// Always draw the BufferedImage (same as original)
		graphics.drawImage(bufferedImage, x, y, null);
	}

	/**
	 * EXACT same method signature as original
	 */
	public void initDrawingArea() {
		// GPU version: ensure GPU framebuffer matches dimensions
		if (isGPUBacked && GPURenderingEngine.isEnabled()) {
			try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("ImageProducer InitDrawingArea")) {
				if (context != null) {
					GPURenderingEngine.resize(canvasWidth, canvasHeight);
				}
			} catch (Exception e) {
				System.err.println("[GPU ImageProducer] Error resizing GPU framebuffer: " + e.getMessage());
			}
		}

		// Always call DrawingArea.initDrawingArea (same as original)
		DrawingArea.initDrawingArea(
			canvasHeight, canvasWidth, canvasRaster, bufferedImage != null ? depthBuffer : null);
	}

	/**
	 * EXACT same method signature as original
	 */
	public Graphics2D getImageGraphics() {
		return bufferedImage.createGraphics();
	}

	/**
	 * Thread-safe sync GPU framebuffer to BufferedImage (only when needed)
	 */
	private void syncGPUToBufferedImageIfNeeded() {
		// Check throttling
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastSyncTime < SYNC_THROTTLE_MS) {
			return; // Use cached data
		}

		// Prevent concurrent syncs
		if (syncInProgress) {
			return;
		}

		synchronized (syncLock) {
			if (syncInProgress) {
				return; // Double-check after acquiring lock
			}

			syncInProgress = true;
			try {
				performGPUSync();
				lastSyncTime = currentTime;
			} finally {
				syncInProgress = false;
			}
		}
	}

	/**
	 * Perform the actual GPU synchronization
	 */
	private void performGPUSync() {
		try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("ImageProducer Sync")) {
			if (context == null) {
				return; // Failed to acquire context
			}

			// Get GPU texture
			int gpuTexture = GPURenderingEngine.getColorTexture();
			if (gpuTexture == 0) {
				return; // No GPU texture available
			}

			// Bind GPU framebuffer and read pixels
			GPURenderingEngine.bindFramebuffer();

			// Clear the buffer
			gpuPixelBuffer.clear();

			// Read RGBA pixels from GPU
			GL11.glReadPixels(0, 0, canvasWidth, canvasHeight, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, gpuPixelBuffer);

			GPURenderingEngine.unbindFramebuffer();

			// Convert RGBA to RGB and flip Y coordinate, update canvasRaster
			convertGPUDataToCanvas();

		} catch (Exception e) {
			System.err.println("[GPU ImageProducer] Error syncing GPU to BufferedImage: " + e.getMessage());
		}
	}

	/**
	 * Convert GPU RGBA data to canvas RGB format
	 */
	private void convertGPUDataToCanvas() {
		gpuPixelBuffer.rewind();

		// Process in chunks for better cache performance
		final int chunkSize = Math.min(1024, canvasWidth);

		for (int y = 0; y < canvasHeight; y++) {
			int flippedY = canvasHeight - 1 - y; // OpenGL Y is flipped

			for (int xStart = 0; xStart < canvasWidth; xStart += chunkSize) {
				int xEnd = Math.min(xStart + chunkSize, canvasWidth);

				for (int x = xStart; x < xEnd; x++) {
					int srcIndex = (y * canvasWidth + x) * 4;
					int dstIndex = flippedY * canvasWidth + x;

					// Extract RGB components (ignore alpha for compatibility)
					int r = gpuPixelBuffer.get(srcIndex) & 0xFF;
					int g = gpuPixelBuffer.get(srcIndex + 1) & 0xFF;
					int b = gpuPixelBuffer.get(srcIndex + 2) & 0xFF;

					canvasRaster[dstIndex] = (r << 16) | (g << 8) | b;
				}
			}
		}

		// Note: canvasRaster is already linked to bufferedImage's data buffer
		// so updating canvasRaster automatically updates the BufferedImage
	}

	/**
	 * Force immediate GPU synchronization (for debugging/special cases)
	 */
	public void forceSyncGPU() {
		if (!isGPUBacked || !GPURenderingEngine.isEnabled()) {
			return;
		}

		synchronized (syncLock) {
			lastSyncTime = 0; // Force sync regardless of throttling
			syncInProgress = false; // Reset flag
			syncGPUToBufferedImageIfNeeded();
		}
	}

	/**
	 * Check if this ImageProducer is GPU-backed
	 */
	public boolean isGPUBacked() {
		return isGPUBacked && GPURenderingEngine.isEnabled();
	}

	/**
	 * Get sync statistics for debugging
	 */
	public String getSyncStatistics() {
		long timeSinceLastSync = System.currentTimeMillis() - lastSyncTime;
		return String.format("ImageProducer[%dx%d] - GPU: %s, Last Sync: %dms ago, In Progress: %s",
			canvasWidth, canvasHeight, isGPUBacked, timeSinceLastSync, syncInProgress);
	}

	/**
	 * Handle GPU context loss/recreation
	 */
	public void handleContextRecreation() {
		if (!isGPUBacked) {
			return;
		}

		System.out.println("[GPU ImageProducer] Handling context recreation...");

		synchronized (syncLock) {
			try {
				// Reinitialize GPU buffer
				if (gpuPixelBuffer != null) {
					gpuPixelBuffer.clear();
				}

				// Reinitialize drawing area with new context
				initDrawingArea();

				// Reset sync state
				lastSyncTime = 0;
				syncInProgress = false;

				System.out.println("[GPU ImageProducer] ✅ Context recreation handled successfully");

			} catch (Exception e) {
				System.err.println("[GPU ImageProducer] ❌ Error handling context recreation: " + e.getMessage());
				// Fall back to CPU mode
				isGPUBacked = false;
			}
		}
	}

	/**
	 * Cleanup GPU resources
	 */
	public void cleanup() {
		synchronized (syncLock) {
			if (gpuPixelBuffer != null) {
				// DirectByteBuffer cleanup is handled by GC
				gpuPixelBuffer = null;
			}

			isGPUBacked = false;
			syncInProgress = false;

			System.out.println("[GPU ImageProducer] Cleaned up GPU resources");
		}
	}

	/**
	 * Validate GPU state and attempt recovery if needed
	 */
	public boolean validateAndRecover() {
		if (!isGPUBacked) {
			return true; // CPU mode is always valid
		}

		if (!GPURenderingEngine.isEnabled()) {
			System.out.println("[GPU ImageProducer] GPU disabled, switching to CPU mode");
			isGPUBacked = false;
			return true;
		}

		try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("ImageProducer Validation")) {
			if (context == null) {
				System.err.println("[GPU ImageProducer] Failed to acquire context for validation");
				return false;
			}

			// Check if framebuffer is still valid
			int currentTexture = GPURenderingEngine.getColorTexture();
			if (currentTexture == 0) {
				System.out.println("[GPU ImageProducer] GPU texture invalid, attempting recovery...");
				handleContextRecreation();
				return isGPUBacked; // Return new state after recovery attempt
			}

			return true;

		} catch (Exception e) {
			System.err.println("[GPU ImageProducer] Validation error: " + e.getMessage());

			// Attempt recovery
			try {
				handleContextRecreation();
				return isGPUBacked;
			} catch (Exception recoveryError) {
				System.err.println("[GPU ImageProducer] Recovery failed: " + recoveryError.getMessage());
				isGPUBacked = false;
				return false;
			}
		}
	}

	/**
	 * Get current framebuffer dimensions (for validation)
	 */
	public boolean validateDimensions() {
		if (!isGPUBacked) {
			return true;
		}

		int gpuWidth = GPURenderingEngine.getWidth();
		int gpuHeight = GPURenderingEngine.getHeight();

		if (gpuWidth != canvasWidth || gpuHeight != canvasHeight) {
			System.out.println("[GPU ImageProducer] Dimension mismatch detected: " +
				"Canvas=" + canvasWidth + "x" + canvasHeight +
				", GPU=" + gpuWidth + "x" + gpuHeight);

			// Attempt to resize GPU framebuffer
			try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("ImageProducer Resize")) {
				if (context != null) {
					GPURenderingEngine.resize(canvasWidth, canvasHeight);
					return true;
				}
			} catch (Exception e) {
				System.err.println("[GPU ImageProducer] Failed to resize GPU framebuffer: " + e.getMessage());
				return false;
			}
		}

		return true;
	}

	/**
	 * Perform maintenance operations (should be called periodically)
	 */
	public void performMaintenance() {
		if (!isGPUBacked) {
			return;
		}

		// Validate state
		if (!validateAndRecover()) {
			System.err.println("[GPU ImageProducer] Maintenance validation failed");
			return;
		}

		// Validate dimensions
		if (!validateDimensions()) {
			System.err.println("[GPU ImageProducer] Maintenance dimension validation failed");
		}

		// Additional maintenance tasks could be added here
		// e.g., memory usage monitoring, performance metrics, etc.
	}
}