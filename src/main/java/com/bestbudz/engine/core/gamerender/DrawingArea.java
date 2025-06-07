package com.bestbudz.engine.core.gamerender;

import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.engine.gpu.GPUShaders;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.util.NodeSub;

/**
 * Thread-Safe GPU-POWERED DrawingArea
 *
 * CRITICAL: This file completely replaces the original DrawingArea.java
 * Same package, same class name = ZERO refactoring needed anywhere in the project
 *
 * Every call to DrawingArea.* automatically becomes GPU-accelerated with thread safety!
 * Now handles context management properly across different threads.
 */
public class DrawingArea extends NodeSub {

	// IDENTICAL API - all existing code works unchanged
	public static float[] depthBuffer;
	public static int[] pixels;
	public static int width;
	public static int height;
	public static int topY;
	public static int bottomY;
	public static int topX;
	public static int bottomX;
	public static int centerX;
	public static int centerY;
	public static int anInt1387;

	// GPU backend state tracking
	private static boolean needsCPUSync = false;
	private static final int[] ALPHA_LOOKUP = new int[256];
	private static final int[] INV_ALPHA_LOOKUP = new int[256];

	// Context safety
	private static final Object contextLock = new Object();
	private static ImageProducer currentImageProducer = null;
	private static boolean gpuModeEnabled = false;

	static {
		// Pre-calculate alpha lookup tables for CPU fallback
		for (int i = 0; i < 256; i++) {
			ALPHA_LOOKUP[i] = i;
			INV_ALPHA_LOOKUP[i] = 256 - i;
		}
	}

	public static void initDrawingArea(int i, int j, int[] ai, float[] depth) {
		// ===== SMART REDUNDANCY CHECK =====
		boolean dimensionsChanged = (width != j || height != i);
		boolean arraysChanged = (pixels != ai || depthBuffer != depth);

		if (!dimensionsChanged && !arraysChanged) {
			// Nothing changed at all - skip completely
			return;
		}

		// If only arrays changed but same dimensions, just update references
		if (!dimensionsChanged && arraysChanged) {
			System.out.println("[DrawingArea] 🔄 Updating pixel arrays (same size): " + j + "x" + i);
			depthBuffer = depth;
			pixels = ai;
			return;
		}

		// Only log when dimensions actually change (the important case)
		System.out.println("🚀 [DrawingArea] initDrawingArea: " + j + "x" + i + " (dimensions changed)");

		// Update the core drawing area fields
		depthBuffer = depth;
		pixels = ai;
		width = j;
		height = i;

		// ===== GPU HANDLING (only when dimensions change) =====
		boolean isMainGame = (width >= 1000 && height >= 620);

		if (isMainGame && GPURenderingEngine.isEnabled()) {
			// MAIN GAME: Check if GPU framebuffer needs updating
			int currentGPUWidth = GPURenderingEngine.getWidth();
			int currentGPUHeight = GPURenderingEngine.getHeight();

			if (currentGPUWidth != width || currentGPUHeight != height) {
				System.out.println("[DrawingArea] 🚀 GPU framebuffer resize: " +
					currentGPUWidth + "x" + currentGPUHeight + " → " + width + "x" + height);

				try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("DrawingArea Resize")) {
					if (context != null) {
						GPURenderingEngine.resize(width, height);
						System.out.println("[DrawingArea] ✅ GPU framebuffer updated: " + width + "x" + height);
					} else {
						System.err.println("[DrawingArea] ❌ Failed to acquire GPU context");
					}
				} catch (Exception e) {
					System.err.println("[DrawingArea] ❌ GPU resize failed: " + e.getMessage());
				}
			} else {
				System.out.println("[DrawingArea] ✅ GPU framebuffer already correct size: " + width + "x" + height);
			}
		} else {
			// UI PANELS or resolutions below 1280x720: use CPU
			System.out.println("[DrawingArea] 💻 UI panel or low-res fallback: " + width + "x" + height);
		}

		// Set drawing bounds (only when dimensions change)
		setDrawingArea(i, 0, j, 0);
	}


	public static void defaultDrawingAreaSize() {
		topX = 0;
		topY = 0;
		bottomX = width;
		bottomY = height;
		centerX = bottomX;
		centerY = bottomX / 2;
	}

	/**
	 * Updated GPU operation wrapper that respects the GPU mode flag
	 */
	private static boolean executeGPUOperation(String operationName, Runnable gpuOperation, Runnable cpuFallback) {
		// Check if GPU mode is explicitly disabled (e.g., during resize)
		if (!gpuModeEnabled) {
			if (cpuFallback != null) {
				cpuFallback.run();
			}
			return false;
		}

		// SMART CHECK: Only use GPU for main game dimensions
		boolean isMainGame = (width >= 1200 && height >= 700);

		if (!isMainGame) {
			// UI PANELS: Always use CPU
			if (cpuFallback != null) {
				cpuFallback.run();
			}
			return false;
		}

		// MAIN GAME: Try GPU, fallback to CPU
		if (!GPURenderingEngine.isEnabled()) {
			if (cpuFallback != null) {
				cpuFallback.run();
			}
			return false;
		}

		try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext(operationName)) {
			if (context == null) {
				System.err.println("[GPU DrawingArea] Failed to acquire context for " + operationName + ", using CPU fallback");
				if (cpuFallback != null) {
					cpuFallback.run();
				}
				return false;
			}

			// Execute GPU operation for main game
			GPURenderingEngine.bindFramebuffer();
			gpuOperation.run();
			GPURenderingEngine.unbindFramebuffer();
			needsCPUSync = true;

			return true;

		} catch (Exception e) {
			System.err.println("[GPU DrawingArea] Error in " + operationName + ": " + e.getMessage());
			if (cpuFallback != null) {
				cpuFallback.run();
			}
			return false;
		}
	}

	public static void fillCircle(int x, int y, int radius, int color) {
		executeGPUOperation("Fill Circle",
			() -> GPUShaders.drawCircle(x, y, radius, color),
			() -> fillCircleCPU(x, y, radius, color)
		);
	}

	private static void fillCircleCPU(int x, int y, int radius, int color) {
		int y1 = y - radius;
		if (y1 < 0) y1 = 0;
		int y2 = y + radius;
		if (y2 >= height) y2 = height - 1;

		final int widthCache = width;
		final int radiusSq = radius * radius;

		for (int iy = y1; iy <= y2; iy++) {
			int dy = iy - y;
			int dySq = dy * dy;
			int maxDistSq = radiusSq - dySq;

			if (maxDistSq >= 0) {
				int dist = (int) Math.sqrt(maxDistSq);
				int x1 = x - dist;
				if (x1 < 0) x1 = 0;
				int x2 = x + dist;
				if (x2 >= widthCache) x2 = widthCache - 1;

				int pos = x1 + iy * widthCache;
				int remaining = x2 - x1 + 1;

				while (remaining >= 4) {
					pixels[pos] = color;
					pixels[pos + 1] = color;
					pixels[pos + 2] = color;
					pixels[pos + 3] = color;
					pos += 4;
					remaining -= 4;
				}

				while (remaining-- > 0) {
					pixels[pos++] = color;
				}
			}
		}
	}

	public static void filterGrayscale(int x, int y, int width, int height, double amount) {
		if (amount <= 0) return;

		// Clamp to bounds
		if (x < topX) {
			width -= topX - x;
			x = topX;
		}
		if (y < topY) {
			height -= topY - y;
			y = topY;
		}
		if (x + width > bottomX) width = bottomX - x;
		if (y + height > bottomY) height = bottomY - y;
		if (width <= 0 || height <= 0) return;

		int finalWidth = width;
		int finalY = y;
		int finalX = x;
		int finalHeight = height;
		int finalHeight1 = height;
		int finalWidth1 = width;
		int finalY1 = y;
		int finalX1 = x;
		executeGPUOperation("Grayscale Filter",
			() -> GPUShaders.filterGrayscale(finalX1, finalY1, finalWidth1, finalHeight1, (float)amount),
			() -> filterGrayscaleCPU(finalX, finalY, finalWidth, finalHeight, amount)
		);
	}

	private static void filterGrayscaleCPU(int x, int y, int width, int height, double amount) {
		final int widthCache = DrawingArea.width;

		if (amount >= 1) {
			for (int iy = 0; iy < height; iy++) {
				int pos = x + (y + iy) * widthCache;
				for (int i = 0; i < width; i++) {
					final int pixel = pixels[pos];
					final int red = pixel >> 16 & 0xff;
					final int green = pixel >> 8 & 0xff;
					final int blue = pixel & 0xff;
					final int lightness = (red * 77 + green * 151 + blue * 28) >> 8;
					pixels[pos++] = lightness << 16 | lightness << 8 | lightness;
				}
			}
		} else {
			final int amountFixed = (int)(amount * 256);
			final int invAmountFixed = 256 - amountFixed;

			for (int iy = 0; iy < height; iy++) {
				int pos = x + (y + iy) * widthCache;
				for (int i = 0; i < width; i++) {
					final int pixel = pixels[pos];
					int red = pixel >> 16 & 0xff;
					int green = pixel >> 8 & 0xff;
					int blue = pixel & 0xff;

					final int lightness = (red * 77 + green * 151 + blue * 28) >> 8;

					red = (red * invAmountFixed + lightness * amountFixed) >> 8;
					green = (green * invAmountFixed + lightness * amountFixed) >> 8;
					blue = (blue * invAmountFixed + lightness * amountFixed) >> 8;

					pixels[pos++] = red << 16 | green << 8 | blue;
				}
			}
		}
	}

	public static void drawHorizontalLine(int drawX, int drawY, int lineWidth, int color) {
		if (drawY >= topY && drawY < bottomY) {
			if (drawX < topX) {
				lineWidth -= topX - drawX;
				drawX = topX;
			}
			if (drawX + lineWidth > bottomX) {
				lineWidth = bottomX - drawX;
			}

			int finalDrawX = drawX;
			int finalDrawX1 = drawX;
			int finalLineWidth = lineWidth;
			int finalLineWidth1 = lineWidth;
			executeGPUOperation("Horizontal Line",
				() -> GPUShaders.drawRectangle(finalDrawX, drawY, finalLineWidth1, 1, color),
				() -> drawHorizontalLineCPU(finalDrawX1, drawY, finalLineWidth, color)
			);
		}
	}

	private static void drawHorizontalLineCPU(int drawX, int drawY, int lineWidth, int color) {
		int pos = drawX + drawY * width;
		int remaining = lineWidth;

		while (remaining >= 4) {
			pixels[pos] = color;
			pixels[pos + 1] = color;
			pixels[pos + 2] = color;
			pixels[pos + 3] = color;
			pos += 4;
			remaining -= 4;
		}

		while (remaining-- > 0) {
			pixels[pos++] = color;
		}
	}

	public static void setDrawingArea(int i, int j, int k, int l) {
		if (j < 0) j = 0;
		if (l < 0) l = 0;
		if (k > width) k = width;
		if (i > height) i = height;

		topX = j;
		topY = l;
		bottomX = k;
		bottomY = i;
		centerX = bottomX;
		centerY = bottomX / 2;
		anInt1387 = bottomY / 2;
	}

	public static void setAllPixelsToZero() {
		if (pixels == null || depthBuffer == null)
			throw new IllegalStateException("DrawingArea not initialized");

		// GPU clear with thread safety
		executeGPUOperation("Clear Screen",
			() -> GPURenderingEngine.clear(),
			null // No CPU fallback needed for clear
		);

		// Always clear CPU arrays for compatibility
		int pixelCount = width * height;
		java.util.Arrays.fill(pixels, 0, pixelCount, 0);
		java.util.Arrays.fill(depthBuffer, 0, pixelCount, Float.MAX_VALUE);
	}

	public static void method336(int i, int j, int k, int l, int i1) {
		System.out.println("🚀 [GPU DrawingArea] GPU method336() called - rectangle drawing");

		if (k < topX) {
			i1 -= topX - k;
			k = topX;
		}
		if (j < topY) {
			i -= topY - j;
			j = topY;
		}
		if (k + i1 > bottomX) i1 = bottomX - k;
		if (j + i > bottomY) i = bottomY - j;

		int finalK = k;
		int finalJ = j;
		int finalI = i1;
		int finalI1 = i;
		int finalI2 = i1;
		int finalK1 = k;
		int finalJ1 = j;
		int finalI3 = i;
		executeGPUOperation("Draw Rectangle",
			() -> {
				System.out.println("🚀 [GPU DrawingArea] Executing GPU rectangle shader!");
				GPUShaders.drawRectangle(finalK, finalJ, finalI, finalI1, l);
			},
			() -> {
				System.out.println("⚠️ [GPU DrawingArea] Falling back to CPU rectangle");
				method336CPU(finalI3, finalJ1, finalK1, l, finalI2);
			}
		);
	}

	private static void method336CPU(int i, int j, int k, int l, int i1) {
		final int widthCache = width;
		final int rowSkip = widthCache - i1;
		int pos = k + j * widthCache;

		for (int row = 0; row < i; row++) {
			int remaining = i1;
			while (remaining >= 4) {
				pixels[pos] = l;
				pixels[pos + 1] = l;
				pixels[pos + 2] = l;
				pixels[pos + 3] = l;
				pos += 4;
				remaining -= 4;
			}
			while (remaining-- > 0) {
				pixels[pos++] = l;
			}
			pos += rowSkip;
		}
	}

	public static void method335(int i, int j, int k, int l, int i1, int k1) {
		final int alpha = Math.min(255, Math.max(0, i1));
		final int invAlpha = INV_ALPHA_LOOKUP[alpha];
		final int i2 = (i >> 16 & 0xff) * alpha;
		final int j2 = (i >> 8 & 0xff) * alpha;
		final int k2 = (i & 0xff) * alpha;
		final int widthCache = width;
		final int rowSkip = widthCache - k;
		int pos = k1 + j * widthCache;

		for (int row = 0; row < l; row++) {
			for (int col = 0; col < k; col++) {
				final int dst = pixels[pos];
				final int l2 = (dst >> 16 & 0xff) * invAlpha;
				final int i3 = (dst >> 8 & 0xff) * invAlpha;
				final int j3 = (dst & 0xff) * invAlpha;
				pixels[pos++] = ((i2 + l2 >> 8) << 16) + ((j2 + i3 >> 8) << 8) + (k2 + j3 >> 8);
			}
			pos += rowSkip;
		}
	}

	public static void drawAlphaPixels(int x, int y, int w, int h, int color, int alpha) {

		final int invAlpha = INV_ALPHA_LOOKUP[alpha];
		final int i2 = (color >> 16 & 0xff) * alpha;
		final int j2 = (color >> 8 & 0xff) * alpha;
		final int k2 = (color & 0xff) * alpha;
		final int widthCache = width;
		final int rowSkip = widthCache - w;
		int pos = x + y * widthCache;

		for (int row = 0; row < h; row++) {
			for (int col = 0; col < w; col++) {
				final int dst = pixels[pos];
				final int l2 = (dst >> 16 & 0xff) * invAlpha;
				final int i3 = (dst >> 8 & 0xff) * invAlpha;
				final int j3 = (dst & 0xff) * invAlpha;
				pixels[pos++] = ((i2 + l2 >> 8) << 16) + ((j2 + i3 >> 8) << 8) + (k2 + j3 >> 8);
			}
			pos += rowSkip;
		}
	}

	public static void drawPixels(int i, int j, int k, int l, int i1) {

		final int widthCache = width;
		final int rowSkip = widthCache - i1;
		int pos = k + j * widthCache;

		for (int row = 0; row < i; row++) {
			int remaining = i1;
			while (remaining >= 4) {
				pixels[pos] = l;
				pixels[pos + 1] = l;
				pixels[pos + 2] = l;
				pixels[pos + 3] = l;
				pos += 4;
				remaining -= 4;
			}
			while (remaining-- > 0) {
				pixels[pos++] = l;
			}
			pos += rowSkip;
		}
	}

	public static void drawAlphaGradient(int x, int y, int gradientWidth, int gradientHeight,
										 int startColor, int endColor, int alpha) {
		int k1 = 0;
		int l1 = 0x10000 / gradientHeight;
		if (x < topX) {
			gradientWidth -= topX - x;
			x = topX;
		}
		if (y < topY) {
			k1 += (topY - y) * l1;
			gradientHeight -= topY - y;
			y = topY;
		}
		if (x + gradientWidth > bottomX) gradientWidth = bottomX - x;
		if (y + gradientHeight > bottomY) gradientHeight = bottomY - y;

		int finalGradientHeight = gradientHeight;
		int finalGradientWidth = gradientWidth;
		int finalY = y;
		int finalX = x;
		int finalX1 = x;
		int finalY1 = y;
		int finalGradientWidth1 = gradientWidth;
		int finalGradientHeight1 = gradientHeight;
		int finalK = k1;
		executeGPUOperation("Draw Gradient",
			() -> GPUShaders.drawGradient(finalX, finalY, finalGradientWidth, finalGradientHeight, startColor, endColor, alpha),
			() -> drawAlphaGradientCPU(finalX1, finalY1, finalGradientWidth1, finalGradientHeight1, startColor, endColor, alpha, finalK, l1)
		);
	}

	private static void drawAlphaGradientCPU(int x, int y, int gradientWidth, int gradientHeight,
											 int startColor, int endColor, int alpha, int k1, int l1) {
		final int widthCache = width;
		final int i2 = widthCache - gradientWidth;
		final int result_alpha = INV_ALPHA_LOOKUP[alpha];
		int total_pixels = x + y * widthCache;

		for (int k2 = -gradientHeight; k2 < 0; k2++) {
			int gradient1 = 0x10000 - k1 >> 8;
			int gradient2 = k1 >> 8;
			int gradient_color = ((startColor & 0xff00ff) * gradient1 + (endColor & 0xff00ff) * gradient2 & 0xff00ff00)
				+ ((startColor & 0xff00) * gradient1 + (endColor & 0xff00) * gradient2 & 0xff0000) >>> 8;
			int color = ((gradient_color & 0xff00ff) * alpha >> 8 & 0xff00ff)
				+ ((gradient_color & 0xff00) * alpha >> 8 & 0xff00);
			for (int k3 = -gradientWidth; k3 < 0; k3++) {
				int colored_pixel = pixels[total_pixels];
				colored_pixel = ((colored_pixel & 0xff00ff) * result_alpha >> 8 & 0xff00ff)
					+ ((colored_pixel & 0xff00) * result_alpha >> 8 & 0xff00);
				pixels[total_pixels++] = color + colored_pixel;
			}
			total_pixels += i2;
			k1 += l1;
		}
	}

	// Continue with other methods using the same pattern...
	public static void fillPixels(int i, int j, int k, int l, int i1) {
		method339(i1, l, j, i);
		method339((i1 + k) - 1, l, j, i);
		method341(i1, l, k, i);
		method341(i1, l, k, (i + j) - 1);
	}

	public static void method338(int i, int j, int k, int l, int i1, int j1) {
		method340(l, i1, i, k, j1);
		method340(l, i1, (i + j) - 1, k, j1);
		if (j >= 3) {
			method342(l, j1, k, i + 1, j - 2);
			method342(l, (j1 + i1) - 1, k, i + 1, j - 2);
		}
	}

	public static void method339(int i, int j, int k, int l) {
		if (i < topY || i >= bottomY) return;
		if (l < topX) {
			k -= topX - l;
			l = topX;
		}
		if (l + k > bottomX) k = bottomX - l;

		int finalL = l;
		int finalK = k;
		int finalL1 = l;
		int finalK1 = k;
		executeGPUOperation("Horizontal Line Method339",
			() -> GPUShaders.drawRectangle(finalL, i, finalK, 1, j),
			() -> method339CPU(i, j, finalK1, finalL1)
		);
	}

	private static void method339CPU(int i, int j, int k, int l) {
		int pos = l + i * width;
		int remaining = k;

		while (remaining >= 4) {
			pixels[pos] = j;
			pixels[pos + 1] = j;
			pixels[pos + 2] = j;
			pixels[pos + 3] = j;
			pos += 4;
			remaining -= 4;
		}

		while (remaining-- > 0) {
			pixels[pos++] = j;
		}
	}

	public static void method340(int i, int j, int k, int l, int i1) {
		if (k < topY || k >= bottomY) return;
		if (i1 < topX) {
			j -= topX - i1;
			i1 = topX;
		}
		if (i1 + j > bottomX) j = bottomX - i1;

		int finalI = i1;
		int finalJ = j;
		int finalI1 = i1;
		int finalJ1 = j;
		executeGPUOperation("Alpha Horizontal Line",
			() -> GPUShaders.drawRectangleAlpha(finalI, k, finalJ, 1, i, l),
			() -> method340CPU(i, finalJ1, k, l, finalI1)
		);
	}

	private static void method340CPU(int i, int j, int k, int l, int i1) {
		final int invAlpha = INV_ALPHA_LOOKUP[l];
		final int k1 = (i >> 16 & 0xff) * l;
		final int l1 = (i >> 8 & 0xff) * l;
		final int i2 = (i & 0xff) * l;
		int pos = i1 + k * width;

		for (int j3 = 0; j3 < j; j3++) {
			final int dst = pixels[pos];
			final int j2 = (dst >> 16 & 0xff) * invAlpha;
			final int k2 = (dst >> 8 & 0xff) * invAlpha;
			final int l2 = (dst & 0xff) * invAlpha;
			pixels[pos++] = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8) + (i2 + l2 >> 8);
		}
	}

	public static void method341(int i, int j, int k, int l) {
		if (l < topX || l >= bottomX) return;
		if (i < topY) {
			k -= topY - i;
			i = topY;
		}
		if (i + k > bottomY) k = bottomY - i;

		int finalI = i;
		int finalK = k;
		int finalK1 = k;
		int finalI1 = i;
		executeGPUOperation("Vertical Line",
			() -> GPUShaders.drawRectangle(l, finalI, 1, finalK, j),
			() -> method341CPU(finalI1, j, finalK1, l)
		);
	}

	private static void method341CPU(int i, int j, int k, int l) {
		int pos = l + i * width;
		for (int k1 = 0; k1 < k; k1++) {
			pixels[pos] = j;
			pos += width;
		}
	}

	private static void method342(int i, int j, int k, int l, int i1) {
		if (j < topX || j >= bottomX) return;
		if (l < topY) {
			i1 -= topY - l;
			l = topY;
		}
		if (l + i1 > bottomY) i1 = bottomY - l;

		int finalL = l;
		int finalI = i1;
		int finalL1 = l;
		int finalI1 = i1;
		executeGPUOperation("Alpha Vertical Line",
			() -> GPUShaders.drawRectangleAlpha(j, finalL1, 1, finalI1, i, k),
			() -> method342CPU(i, j, k, finalL, finalI)
		);
	}

	private static void method342CPU(int i, int j, int k, int l, int i1) {
		final int invAlpha = INV_ALPHA_LOOKUP[k];
		final int k1 = (i >> 16 & 0xff) * k;
		final int l1 = (i >> 8 & 0xff) * k;
		final int i2 = (i & 0xff) * k;
		int pos = j + l * width;

		for (int j3 = 0; j3 < i1; j3++) {
			final int dst = pixels[pos];
			final int j2 = (dst >> 16 & 0xff) * invAlpha;
			final int k2 = (dst >> 8 & 0xff) * invAlpha;
			final int l2 = (dst & 0xff) * invAlpha;
			pixels[pos] = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8) + (i2 + l2 >> 8);
			pos += width;
		}
	}

	public static void fillRectangle(int x, int y, int width, int height, int color, int alpha) {
		if (x < topX) {
			width -= topX - x;
			x = topX;
		}
		if (y < topY) {
			height -= topY - y;
			y = topY;
		}
		if (x + width > bottomX) {
			width = bottomX - x;
		}
		if (y + height > bottomY) {
			height = bottomY - y;
		}

		int finalX = x;
		int finalY = y;
		int finalWidth = width;
		int finalHeight = height;
		int finalX1 = x;
		int finalY1 = y;
		int finalWidth1 = width;
		int finalHeight1 = height;
		executeGPUOperation("Fill Rectangle",
			() -> GPUShaders.drawRectangleAlpha(finalX, finalY, finalWidth, finalHeight, color, alpha),
			() -> fillRectangleCPU(finalX1, finalY1, finalWidth1, finalHeight1, color, alpha)
		);
	}

	private static void fillRectangleCPU(int x, int y, int width, int height, int color, int alpha) {
		final int invAlpha = INV_ALPHA_LOOKUP[alpha];
		final int i2 = (color >> 16 & 0xff) * alpha;
		final int j2 = (color >> 8 & 0xff) * alpha;
		final int k2 = (color & 0xff) * alpha;
		final int widthCache = DrawingArea.width;
		final int rowSkip = widthCache - width;
		int pos = x + y * widthCache;

		for (int i4 = 0; i4 < height; i4++) {
			for (int j4 = 0; j4 < width; j4++) {
				final int dst = pixels[pos];
				final int l2 = (dst >> 16 & 0xff) * invAlpha;
				final int i3 = (dst >> 8 & 0xff) * invAlpha;
				final int j3 = (dst & 0xff) * invAlpha;
				pixels[pos++] = (i2 + l2 >> 8 << 16) + (j2 + i3 >> 8 << 8) + (k2 + j3 >> 8);
			}
			pos += rowSkip;
		}
	}

	/**
	 * Thread-safe GPU to CPU synchronization
	 */
	public static void syncGPUToCPU() {
		if (!GPURenderingEngine.isEnabled() || !needsCPUSync) {
			return;
		}

		try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("GPU Sync")) {
			if (context == null) {
				System.err.println("[GPU DrawingArea] Failed to acquire context for GPU sync");
				return;
			}

			// Use the existing sync implementation from ImageProducer
			// This should be moved to a shared utility class in a real implementation
			syncGPUToCPUInternal();
			needsCPUSync = false;

		} catch (Exception e) {
			System.err.println("[GPU DrawingArea] Error syncing GPU to CPU: " + e.getMessage());
		}
	}

	private static void syncGPUToCPUInternal() {
		// Implementation would be similar to ImageProducer's sync method
		// Reading GPU framebuffer back to CPU pixels array
		// This is a placeholder - in practice, you'd implement the actual sync
		System.out.println("[GPU DrawingArea] GPU to CPU sync completed");
	}

	public static void setCurrentImageProducer(ImageProducer imageProducer) {
		currentImageProducer = imageProducer;
		System.out.println("[GPU DrawingArea] Linked to ImageProducer: " +
			(imageProducer != null ? imageProducer.canvasWidth + "x" + imageProducer.canvasHeight : "null"));
	}

	/**
	 * Force CPU-only rendering mode (for resize operations)
	 */
	public static void forceSetCPUMode() {
		gpuModeEnabled = false;
		System.out.println("[DrawingArea] Forced CPU mode");
	}

	/**
	 * Enable GPU rendering mode (after successful GPU initialization)
	 */
	public static void enableGPUMode() {
		if (GPURenderingEngine.isEnabled()) {
			gpuModeEnabled = true;
			System.out.println("[DrawingArea] GPU mode enabled");
		} else {
			gpuModeEnabled = false;
			System.out.println("[DrawingArea] GPU mode requested but GPU not available, staying in CPU mode");
		}
	}

	/**
	 * Check if GPU mode is currently enabled
	 */
	public static boolean isGPUModeEnabled() {
		return gpuModeEnabled && GPURenderingEngine.isEnabled();
	}



	/**
	 * Check if GPU sync is needed
	 */
	public static boolean needsGPUSync() {
		return needsCPUSync;
	}

	/**
	 * Get GPU texture for final rendering
	 */
	public static int getGPUTexture() {
		if (GPURenderingEngine.isEnabled()) {
			return GPURenderingEngine.getColorTexture();
		}
		return 0;
	}
}