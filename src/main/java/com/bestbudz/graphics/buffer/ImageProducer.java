package com.bestbudz.graphics.buffer;

import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;

public final class ImageProducer {

	public final float[] depthBuffer;
	public final int[] canvasRaster;
	public final int canvasWidth;
	public final int canvasHeight;
	private final BufferedImage bufferedImage;

	private ByteBuffer gpuPixelBuffer;
	private boolean isGPUBacked = false;
	private long lastGPUSync = 0;
	private static final long MIN_SYNC_INTERVAL = 16;

	private static volatile ImageProducer currentInstance = null;
	private static final Object instanceLock = new Object();

	private volatile boolean gpuDataValid = false;
	private volatile boolean renderingInProgress = false;

	private static volatile boolean sharedContextAcquired = false;

	public ImageProducer(int canvasWidth, int canvasHeight) {
		this.canvasWidth = canvasWidth;
		this.canvasHeight = canvasHeight;

		depthBuffer = new float[canvasWidth * canvasHeight];
		bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
		canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();

		synchronized (instanceLock) {
			if (currentInstance != null) {
				System.out.println("[ImageProducer] Replacing previous instance during resize");
				currentInstance.cleanup();
			}
			currentInstance = this;
		}

		initializeGPUBackend();

		initDrawingArea();

		System.out.println("[ImageProducer] Created: " + canvasWidth + "x" + canvasHeight +
			" (GPU: " + isGPUBacked + ")");
	}

	private void initializeGPUBackend() {
		if (!GPURenderingEngine.isEnabled()) {
			isGPUBacked = false;
			return;
		}

		try {

			gpuPixelBuffer = ByteBuffer.allocateDirect(canvasWidth * canvasHeight * 4);
			isGPUBacked = true;
			gpuDataValid = false;

			System.out.println("[ImageProducer] GPU backend ready: " + canvasWidth + "x" + canvasHeight);

		} catch (Exception e) {
			System.err.println("[ImageProducer] GPU backend init failed: " + e.getMessage());
			isGPUBacked = false;
		}
	}

	public void drawGraphics(int y, Graphics2D graphics, int x) {
		if (isGPUBacked && GPURenderingEngine.isEnabled()) {

			syncGPUDataWhenNeeded();
		}

		graphics.drawImage(bufferedImage, x, y, null);
	}

	private void syncGPUDataWhenNeeded() {
		long currentTime = System.currentTimeMillis();

		if (currentTime - lastGPUSync < MIN_SYNC_INTERVAL) {
			return;
		}

		if (renderingInProgress) {
			return;
		}

		if (!gpuDataValid) {
			return;
		}

		performGPUSync();
		lastGPUSync = currentTime;
	}

	private void performGPUSync() {
		try (GPUContextManager.ContextToken context =
				 GPURenderingEngine.acquireContext("ImageProducer Sync")) {

			if (context == null) {
				return;
			}

			renderingInProgress = true;

			int gpuWidth = GPURenderingEngine.getWidth();
			int gpuHeight = GPURenderingEngine.getHeight();

			if (gpuWidth != canvasWidth || gpuHeight != canvasHeight) {
				System.err.println("[ImageProducer] Dimension mismatch: Canvas=" +
					canvasWidth + "x" + canvasHeight +
					", GPU=" + gpuWidth + "x" + gpuHeight);
				return;
			}

			readGPUFramebufferOptimized();

			convertGPUToCanvas();

			gpuDataValid = false;

		} catch (Exception e) {
			System.err.println("[ImageProducer] GPU sync error: " + e.getMessage());
		} finally {
			renderingInProgress = false;
		}
	}

	private void readGPUFramebufferOptimized() {

		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, GPURenderingEngine.getFramebufferId());

		gpuPixelBuffer.clear();

		GL11.glReadPixels(0, 0, canvasWidth, canvasHeight,
			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, gpuPixelBuffer);

		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, 0);
	}

	private void convertGPUToCanvas() {
		gpuPixelBuffer.rewind();

		for (int y = 0; y < canvasHeight; y++) {
			int flippedY = canvasHeight - 1 - y;
			int dstRowStart = flippedY * canvasWidth;
			int srcRowStart = y * canvasWidth * 4;

			for (int x = 0; x < canvasWidth; x++) {
				int srcIndex = srcRowStart + x * 4;
				int dstIndex = dstRowStart + x;

				int r = gpuPixelBuffer.get(srcIndex) & 0xFF;
				int g = gpuPixelBuffer.get(srcIndex + 1) & 0xFF;
				int b = gpuPixelBuffer.get(srcIndex + 2) & 0xFF;

				canvasRaster[dstIndex] = (r << 16) | (g << 8) | b;
			}
		}
	}

	public void initDrawingArea() {

		if (isGPUBacked && GPURenderingEngine.isEnabled()) {
			int currentGPUWidth = GPURenderingEngine.getWidth();
			int currentGPUHeight = GPURenderingEngine.getHeight();

			if (currentGPUWidth != canvasWidth || currentGPUHeight != canvasHeight) {
				try (GPUContextManager.ContextToken context =
						 GPURenderingEngine.acquireContext("ImageProducer InitDrawingArea")) {
					if (context != null) {
						GPURenderingEngine.resize(canvasWidth, canvasHeight);
						System.out.println("[ImageProducer] GPU resized to: " + canvasWidth + "x" + canvasHeight);
					}
				} catch (Exception e) {
					System.err.println("[ImageProducer] GPU resize error: " + e.getMessage());
				}
			}
		}

		DrawingArea.initDrawingArea(canvasHeight, canvasWidth, canvasRaster, depthBuffer);
	}

	public Graphics2D getImageGraphics() {
		return bufferedImage.createGraphics();
	}

	public void markGPUDataReady() {
		if (isGPUBacked) {
			gpuDataValid = true;
		}
	}

	public void forceSyncGPU() {
		if (!isGPUBacked || !GPURenderingEngine.isEnabled()) {
			return;
		}

		if (renderingInProgress) {
			return;
		}

		lastGPUSync = 0;
		gpuDataValid = true;
		syncGPUDataWhenNeeded();
	}

	public boolean isGPUBacked() {
		return isGPUBacked && GPURenderingEngine.isEnabled();
	}

	public void handleContextRecreation() {
		if (!isGPUBacked) {
			return;
		}

		System.out.println("[ImageProducer] Handling context recreation...");

		try {

			gpuDataValid = false;
			renderingInProgress = false;
			lastGPUSync = 0;

			if (gpuPixelBuffer == null || gpuPixelBuffer.capacity() != canvasWidth * canvasHeight * 4) {
				gpuPixelBuffer = ByteBuffer.allocateDirect(canvasWidth * canvasHeight * 4);
			}

			initDrawingArea();

			System.out.println("[ImageProducer] Context recreation complete");

		} catch (Exception e) {
			System.err.println("[ImageProducer] Context recreation error: " + e.getMessage());
			isGPUBacked = false;
		}
	}

	public void cleanup() {
		synchronized (instanceLock) {
			if (currentInstance == this) {
				currentInstance = null;
			}
		}

		renderingInProgress = false;
		gpuDataValid = false;

		if (gpuPixelBuffer != null) {
			gpuPixelBuffer = null;
		}

		isGPUBacked = false;
		System.out.println("[ImageProducer] Cleaned up");
	}

	public static ImageProducer getCurrentInstance() {
		synchronized (instanceLock) {
			return currentInstance;
		}
	}

	public boolean validateGPUState() {
		if (!isGPUBacked) {
			return true;
		}

		if (!GPURenderingEngine.isEnabled()) {
			isGPUBacked = false;
			return true;
		}

		int gpuWidth = GPURenderingEngine.getWidth();
		int gpuHeight = GPURenderingEngine.getHeight();

		if (gpuWidth != canvasWidth || gpuHeight != canvasHeight) {
			System.out.println("[ImageProducer] GPU dimensions inconsistent, will resize on next init");
			return false;
		}

		return true;
	}

	public String getSyncStatistics() {
		long timeSinceSync = System.currentTimeMillis() - lastGPUSync;
		return String.format("ImageProducer[%dx%d] GPU:%s Valid:%s Rendering:%s LastSync:%dms",
			canvasWidth, canvasHeight, isGPUBacked, gpuDataValid,
			renderingInProgress, timeSinceSync);
	}
}
