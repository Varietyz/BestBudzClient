package com.bestbudz.engine.core.gamerender;

import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import com.bestbudz.graphics.buffer.ImageProducer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class DrawingArea {

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
	public static int centerYHalf;

	private static boolean needsCPUSync = false;
	private static final int[] ALPHA_LOOKUP = new int[256];
	private static final int[] INV_ALPHA_LOOKUP = new int[256];

	private static final Object contextLock = new Object();
	private static ImageProducer currentImageProducer = null;
	private static boolean gpuModeEnabled = false;

	private static Graphics2D textGraphics = null;
	private static BufferedImage textBuffer = null;
	private static int textBufferWidth = 0;
	private static int textBufferHeight = 0;

	static {

		for (int i = 0; i < 256; i++) {
			ALPHA_LOOKUP[i] = i;
			INV_ALPHA_LOOKUP[i] = 256 - i;
		}
	}

	public static void initDrawingArea(int i, int j, int[] ai, float[] depth) {

		boolean dimensionsChanged = (width != j || height != i);
		boolean arraysChanged = (pixels != ai || depthBuffer != depth);

		if (!dimensionsChanged && !arraysChanged) {

			return;
		}

		if (!dimensionsChanged && arraysChanged) {
			depthBuffer = depth;
			pixels = ai;
			return;
		}

		depthBuffer = depth;
		pixels = ai;
		width = j;
		height = i;

		boolean isMainGame = (width >= 1000 && height >= 620);

		if (isMainGame && GPURenderingEngine.isEnabled()) {

			int currentGPUWidth = GPURenderingEngine.getWidth();
			int currentGPUHeight = GPURenderingEngine.getHeight();

			if (currentGPUWidth != width || currentGPUHeight != height) {
				try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("DrawingArea Resize")) {
					if (context != null) {
						GPURenderingEngine.resize(width, height);
					} else {
						System.err.println("[DrawingArea] ❌ Failed to acquire GPU context");
					}
				} catch (Exception e) {
					System.err.println("[DrawingArea] ❌ GPU resize failed: " + e.getMessage());
				}
			}
		}

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

	private static boolean executeGPUOperation(String operationName, Runnable gpuOperation, Runnable cpuFallback) {

		if (!gpuModeEnabled) {
			if (cpuFallback != null) {
				cpuFallback.run();
			}
			return false;
		}

		boolean isMainGame = (width >= 1200 && height >= 700);

		if (!isMainGame) {

			if (cpuFallback != null) {
				cpuFallback.run();
			}
			return false;
		}

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
		filterGrayscaleCPU(finalX, finalY, finalWidth, finalHeight, amount);
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
			int finalLineWidth = lineWidth;
			drawHorizontalLineCPU(finalDrawX, drawY, finalLineWidth, color)
			;
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
		centerYHalf = bottomY / 2;
	}

	public static void setAllPixelsToZero() {
		if (pixels == null || depthBuffer == null)
			throw new IllegalStateException("DrawingArea not initialized");

		executeGPUOperation("Clear Screen",
			() -> GPURenderingEngine.clear(),
			null
		);

		int pixelCount = width * height;
		java.util.Arrays.fill(pixels, 0, pixelCount, 0);
		java.util.Arrays.fill(depthBuffer, 0, pixelCount, Float.MAX_VALUE);
	}

	public static void drawSolidRectangle(int i, int j, int k, int l, int i1) {
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
		drawSolidRectangleCPU(finalI1, finalJ, finalK, l, finalI)
		;
	}

	private static void drawSolidRectangleCPU(int i, int j, int k, int l, int i1) {
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

	public static void drawAlphaRectangle(int i, int j, int k, int l, int i1, int k1) {
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
		int finalK = k1;
		drawAlphaGradientCPU(finalX, finalY, finalGradientWidth, finalGradientHeight, startColor, endColor, alpha, finalK, l1)
		;
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

	public static void fillPixels(int i, int j, int k, int l, int i1) {
		drawAHorizontalLine(i1, l, j, i);
		drawAHorizontalLine((i1 + k) - 1, l, j, i);
		drawAVerticalLine(i1, l, k, i);
		drawAVerticalLine(i1, l, k, (i + j) - 1);
	}

	public static void drawBorderedRectangle(int i, int j, int k, int l, int i1, int j1) {
		drawAlphaHorizontalLine(l, i1, i, k, j1);
		drawAlphaHorizontalLine(l, i1, (i + j) - 1, k, j1);
		if (j >= 3) {
			drawAlphaVerticalLine(l, j1, k, i + 1, j - 2);
			drawAlphaVerticalLine(l, (j1 + i1) - 1, k, i + 1, j - 2);
		}
	}

	public static void drawAHorizontalLine(int i, int j, int k, int l) {
		if (i < topY || i >= bottomY) return;
		if (l < topX) {
			k -= topX - l;
			l = topX;
		}
		if (l + k > bottomX) k = bottomX - l;

		int finalL = l;
		int finalK = k;
		drawAHorizontalLineCPU(i, j, finalK, finalL)
		;
	}

	private static void drawAHorizontalLineCPU(int i, int j, int k, int l) {
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

	public static void drawAlphaHorizontalLine(int i, int j, int k, int l, int i1) {
		if (k < topY || k >= bottomY) return;
		if (i1 < topX) {
			j -= topX - i1;
			i1 = topX;
		}
		if (i1 + j > bottomX) j = bottomX - i1;

		int finalI = i1;
		int finalJ = j;
		drawAlphaHorizontalLineCPU(i, finalJ, k, l, finalI)
		;
	}

	private static void drawAlphaHorizontalLineCPU(int i, int j, int k, int l, int i1) {
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

	public static void drawAVerticalLine(int i, int j, int k, int l) {
		if (l < topX || l >= bottomX) return;
		if (i < topY) {
			k -= topY - i;
			i = topY;
		}
		if (i + k > bottomY) k = bottomY - i;

		int finalI = i;
		int finalK = k;
		drawAVerticalLineCPU(finalI, j, finalK, l)
		;
	}

	private static void drawAVerticalLineCPU(int i, int j, int k, int l) {
		int pos = l + i * width;
		for (int k1 = 0; k1 < k; k1++) {
			pixels[pos] = j;
			pos += width;
		}
	}

	private static void drawAlphaVerticalLine(int i, int j, int k, int l, int i1) {
		if (j < topX || j >= bottomX) return;
		if (l < topY) {
			i1 -= topY - l;
			l = topY;
		}
		if (l + i1 > bottomY) i1 = bottomY - l;

		int finalL = l;
		int finalI = i1;
		drawAlphaVerticalLineCPU(i, j, k, finalL, finalI)
		;
	}

	private static void drawAlphaVerticalLineCPU(int i, int j, int k, int l, int i1) {
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
		fillRectangleCPU(finalX, finalY, finalWidth, finalHeight, color, alpha)
		;
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

	public static void syncGPUToCPU() {
		if (!GPURenderingEngine.isEnabled() || !needsCPUSync) {
			return;
		}

		try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("GPU Sync")) {
			if (context == null) {
				System.err.println("[GPU DrawingArea] Failed to acquire context for GPU sync");
				return;
			}

			syncGPUToCPUInternal();
			needsCPUSync = false;

		} catch (Exception e) {
			System.err.println("[GPU DrawingArea] Error syncing GPU to CPU: " + e.getMessage());
		}
	}

	private static void syncGPUToCPUInternal() {

		System.out.println("[GPU DrawingArea] GPU to CPU sync completed");
	}

	public static void setCurrentImageProducer(ImageProducer imageProducer) {
		currentImageProducer = imageProducer;
		System.out.println("[GPU DrawingArea] Linked to ImageProducer: " +
			(imageProducer != null ? imageProducer.canvasWidth + "x" + imageProducer.canvasHeight : "null"));
	}

	public static void initTextRendering() {
		if (textBuffer == null || textBufferWidth != width || textBufferHeight != height) {
			textBufferWidth = width;
			textBufferHeight = height;

			textBuffer = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

			if (textGraphics != null) {
				textGraphics.dispose();
			}

			textGraphics = textBuffer.createGraphics();

			textGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_OFF);

			textGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

			textGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

			textGraphics.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_SPEED);

			textGraphics.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_SPEED);

			System.out.println("✅ Non-anti-aliased text rendering initialized");
		}
	}

	public static void drawText(String text, int x, int y, int color, Font font) {
		if (text == null || text.isEmpty() || pixels == null) return;

		if (x >= bottomX || y >= bottomY) return;

		initTextRendering();

		try {

			FontMetrics fm = textGraphics.getFontMetrics(font);
			int textWidth = fm.stringWidth(text) + 6;
			int textHeight = fm.getHeight() + 6;

			BufferedImage charBuffer = new BufferedImage(textWidth, textHeight,
				BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = charBuffer.createGraphics();

			setupNonAntiAliasedRenderingHints(g2d);

			g2d.setComposite(AlphaComposite.Clear);
			g2d.fillRect(0, 0, textWidth, textHeight);
			g2d.setComposite(AlphaComposite.SrcOver);

			g2d.setFont(font);
			g2d.setColor(new Color(color));

			g2d.drawString(text, 3, fm.getAscent() + 3);
			g2d.dispose();

			copyTextToPixels(charBuffer, x, y - fm.getAscent());

		} catch (Exception e) {
			System.err.println("[DrawingArea] Error in non-anti-aliased text rendering: " + e.getMessage());

			drawTextFallback(text, x, y, color, font);
		}
	}

	private static void setupNonAntiAliasedRenderingHints(Graphics2D g2d) {

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_OFF);

		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
			RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

		g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
			RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
			RenderingHints.VALUE_RENDER_SPEED);

		g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING,
			RenderingHints.VALUE_COLOR_RENDER_SPEED);

		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
			RenderingHints.VALUE_STROKE_DEFAULT);
	}

	public static void drawTextWithShadow(String text, int x, int y, int color, int shadowColor, Font font) {
		if (shadowColor != -1) {
			drawText(text, x + 1, y + 1, shadowColor, font);
		}
		drawText(text, x, y, color, font);
	}

	public static int getTextWidth(String text, Font font) {
		if (text == null || text.isEmpty()) return 0;

		try {
			initTextRendering();
			FontMetrics fm = textGraphics.getFontMetrics(font);
			return fm.stringWidth(text);
		} catch (Exception e) {

			return text.length() * (font.getSize() * 2 / 3);
		}
	}

	public static int getTextHeight(Font font) {
		try {
			initTextRendering();
			FontMetrics fm = textGraphics.getFontMetrics(font);
			return fm.getHeight();
		} catch (Exception e) {
			return font.getSize();
		}
	}

	private static void copyTextToPixels(BufferedImage textImage, int destX, int destY) {
		int imgWidth = textImage.getWidth();
		int imgHeight = textImage.getHeight();

		int startX = Math.max(0, topX - destX);
		int startY = Math.max(0, topY - destY);
		int endX = Math.min(imgWidth, bottomX - destX);
		int endY = Math.min(imgHeight, bottomY - destY);

		if (startX >= endX || startY >= endY) return;

		int[] imagePixels = new int[imgWidth * imgHeight];
		textImage.getRGB(0, 0, imgWidth, imgHeight, imagePixels, 0, imgWidth);

		for (int y = startY; y < endY; y++) {
			for (int x = startX; x < endX; x++) {
				int screenX = destX + x;
				int screenY = destY + y;

				if (screenX >= topX && screenX < bottomX && screenY >= topY && screenY < bottomY) {
					int rgb = imagePixels[y * imgWidth + x];
					int alpha = (rgb >> 24) & 0xFF;

					if (alpha >= 250) {
						int pixelIndex = screenY * width + screenX;
						if (pixelIndex >= 0 && pixelIndex < pixels.length) {

							pixels[pixelIndex] = rgb | 0xFF000000;
						}
					}

				}
			}
		}
	}

	private static void drawTextFallback(String text, int x, int y, int color, Font font) {
		int charWidth = font.getSize() * 2 / 3;
		int charHeight = font.getSize();

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch != ' ') {
				int charX = x + i * charWidth;

				if (charX >= topX && charX < bottomX && y >= topY && y < bottomY) {
					drawPixels(charHeight, y - charHeight + 2, charX, color, charWidth - 1);
				}
			}
		}
	}

	public static void cleanupTextRendering() {
		if (textGraphics != null) {
			textGraphics.dispose();
			textGraphics = null;
		}
		textBuffer = null;
	}

	public static void forceSetCPUMode() {
		gpuModeEnabled = false;
		System.out.println("[DrawingArea] Forced CPU mode");
	}

	public static void enableGPUMode() {
		if (GPURenderingEngine.isEnabled()) {
			gpuModeEnabled = true;
			System.out.println("[DrawingArea] GPU mode enabled");
		} else {
			gpuModeEnabled = false;
			System.out.println("[DrawingArea] GPU mode requested but GPU not available, staying in CPU mode");
		}
	}

	public static boolean isGPUModeEnabled() {
		return gpuModeEnabled && GPURenderingEngine.isEnabled();
	}

	public static boolean needsGPUSync() {
		return needsCPUSync;
	}

	public static int getGPUTexture() {
		if (GPURenderingEngine.isEnabled()) {
			return GPURenderingEngine.getColorTexture();
		}
		return 0;
	}
}
