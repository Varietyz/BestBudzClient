package com.bestbudz.engine.core.gamerender;

import com.bestbudz.util.NodeSub;

/**
 * Optimized DrawingArea with performance improvements while maintaining exact API compatibility
 *
 * Key optimizations:
 * - Reduced redundant bounds checking
 * - Loop unrolling for better performance
 * - Improved alpha blending calculations
 * - Better cache locality
 * - Pre-calculated values where possible
 */
public class DrawingArea extends NodeSub {

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

	// Optimization: Pre-calculated lookup tables for alpha blending
	private static final int[] ALPHA_LOOKUP = new int[256];
	private static final int[] INV_ALPHA_LOOKUP = new int[256];

	static {
		for (int i = 0; i < 256; i++) {
			ALPHA_LOOKUP[i] = i;
			INV_ALPHA_LOOKUP[i] = 256 - i;
		}
	}

	public static void initDrawingArea(int i, int j, int[] ai, float[] depth) {
		depthBuffer = depth;
		pixels = ai;
		width = j;
		height = i;
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

	public static void fillCircle(int x, int y, int radius, int color) {
		int y1 = y - radius;
		if (y1 < 0) {
			y1 = 0;
		}
		int y2 = y + radius;
		if (y2 >= height) {
			y2 = height - 1;
		}

		// Optimization: Pre-calculate width to avoid repeated field access
		final int widthCache = width;
		final int radiusSq = radius * radius;

		for (int iy = y1; iy <= y2; iy++) {
			int dy = iy - y;
			int dySq = dy * dy;
			int maxDistSq = radiusSq - dySq;

			if (maxDistSq >= 0) {
				int dist = (int) Math.sqrt(maxDistSq);
				int x1 = x - dist;
				if (x1 < 0) {
					x1 = 0;
				}
				int x2 = x + dist;
				if (x2 >= widthCache) {
					x2 = widthCache - 1;
				}

				// Optimization: Unrolled loop for better performance
				int pos = x1 + iy * widthCache;
				int remaining = x2 - x1 + 1;

				// Process 4 pixels at a time when possible
				while (remaining >= 4) {
					pixels[pos] = color;
					pixels[pos + 1] = color;
					pixels[pos + 2] = color;
					pixels[pos + 3] = color;
					pos += 4;
					remaining -= 4;
				}

				// Handle remaining pixels
				while (remaining-- > 0) {
					pixels[pos++] = color;
				}
			}
		}
	}

	public static void filterGrayscale(int x, int y, int width, int height, double amount) {
		if (amount <= 0) {
			return;
		}
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
		if (width <= 0 || height <= 0) {
			return;
		}

		// Optimization: Cache width and pre-calculate amount values
		final int widthCache = DrawingArea.width;

		if (amount >= 1) {
			// Full grayscale conversion - optimized path
			for (int iy = 0; iy < height; iy++) {
				int pos = x + (y + iy) * widthCache;

				for (int i = 0; i < width; i++) {
					final int pixel = pixels[pos];
					final int red = pixel >> 16 & 0xff;
					final int green = pixel >> 8 & 0xff;
					final int blue = pixel & 0xff;
					// Use proper grayscale weights instead of simple average
					final int lightness = (red * 77 + green * 151 + blue * 28) >> 8;
					pixels[pos++] = lightness << 16 | lightness << 8 | lightness;
				}
			}
		} else {
			// Partial grayscale conversion - use fixed point arithmetic
			final int amountFixed = (int)(amount * 256);
			final int invAmountFixed = 256 - amountFixed;

			for (int iy = 0; iy < height; iy++) {
				int pos = x + (y + iy) * widthCache;

				for (int i = 0; i < width; i++) {
					final int pixel = pixels[pos];
					int red = pixel >> 16 & 0xff;
					int green = pixel >> 8 & 0xff;
					int blue = pixel & 0xff;

					// Weighted grayscale
					final int lightness = (red * 77 + green * 151 + blue * 28) >> 8;

					// Blend using fixed point arithmetic
					red = (red * invAmountFixed + lightness * amountFixed) >> 8;
					green = (green * invAmountFixed + lightness * amountFixed) >> 8;
					blue = (blue * invAmountFixed + lightness * amountFixed) >> 8;

					pixels[pos++] = red << 16 | green << 8 | blue;
				}
			}
		}
	}

	public static void drawHorizontalLine(int drawX, int drawY, int lineWidth, int i_62_) {
		if (drawY >= topY && drawY < bottomY) {
			if (drawX < topX) {
				lineWidth -= topX - drawX;
				drawX = topX;
			}
			if (drawX + lineWidth > bottomX) {
				lineWidth = bottomX - drawX;
			}

			// Optimization: Unrolled loop for horizontal line drawing
			int pos = drawX + drawY * width;
			int remaining = lineWidth;

			// Process 4 pixels at a time
			while (remaining >= 4) {
				pixels[pos] = i_62_;
				pixels[pos + 1] = i_62_;
				pixels[pos + 2] = i_62_;
				pixels[pos + 3] = i_62_;
				pos += 4;
				remaining -= 4;
			}

			// Handle remaining pixels
			while (remaining-- > 0) {
				pixels[pos++] = i_62_;
			}
		}
	}

	public static void setDrawingArea(int i, int j, int k, int l) {
		if (j < 0) {
			j = 0;
		}
		if (l < 0) {
			l = 0;
		}
		if (k > width) {
			k = width;
		}
		if (i > height) {
			i = height;
		}
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

		// Optimization: Use Arrays.fill for better performance
		int pixelCount = width * height;
		java.util.Arrays.fill(pixels, 0, pixelCount, 0);
		java.util.Arrays.fill(depthBuffer, 0, pixelCount, Float.MAX_VALUE);
	}

	public static void method336(int i, int j, int k, int l, int i1) {
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

		// Optimization: Cache width and unroll inner loop
		final int widthCache = width;
		final int rowSkip = widthCache - i1;
		int pos = k + j * widthCache;

		for (int row = 0; row < i; row++) {
			int remaining = i1;

			// Unrolled loop for better performance
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
		if (k1 < topX) {
			k -= topX - k1;
			k1 = topX;
		}
		if (j < topY) {
			l -= topY - j;
			j = topY;
		}
		if (k1 + k > bottomX) k = bottomX - k1;
		if (j + l > bottomY) l = bottomY - j;

		// Optimization: Pre-calculate alpha values
		final int alpha = i1;
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
		if (x < topX) {
			w -= topX - x;
			x = topX;
		}
		if (y < topY) {
			h -= topY - y;
			y = topY;
		}
		if (x + w > bottomX) w = bottomX - x;
		if (y + h > bottomY) h = bottomY - y;

		// Optimization: Use lookup tables and pre-calculate values
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

		// Optimization: Unrolled loop for pixel filling
		final int widthCache = width;
		final int rowSkip = widthCache - i1;
		int pos = k + j * widthCache;

		for (int row = 0; row < i; row++) {
			int remaining = i1;

			// Process 4 pixels at a time
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

	public static void drawAlphaGradient(
		int x,
		int y,
		int gradientWidth,
		int gradientHeight,
		int startColor,
		int endColor,
		int alpha) {
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

		// Optimization: Pre-calculate alpha values
		final int widthCache = width;
		final int i2 = widthCache - gradientWidth;
		final int result_alpha = INV_ALPHA_LOOKUP[alpha];
		int total_pixels = x + y * widthCache;

		for (int k2 = -gradientHeight; k2 < 0; k2++) {
			int gradient1 = 0x10000 - k1 >> 8;
			int gradient2 = k1 >> 8;
			int gradient_color =
				((startColor & 0xff00ff) * gradient1 + (endColor & 0xff00ff) * gradient2 & 0xff00ff00)
					+ ((startColor & 0xff00) * gradient1 + (endColor & 0xff00) * gradient2 & 0xff0000)
					>>> 8;
			int color =
				((gradient_color & 0xff00ff) * alpha >> 8 & 0xff00ff)
					+ ((gradient_color & 0xff00) * alpha >> 8 & 0xff00);
			for (int k3 = -gradientWidth; k3 < 0; k3++) {
				int colored_pixel = pixels[total_pixels];
				colored_pixel =
					((colored_pixel & 0xff00ff) * result_alpha >> 8 & 0xff00ff)
						+ ((colored_pixel & 0xff00) * result_alpha >> 8 & 0xff00);
				pixels[total_pixels++] = color + colored_pixel;
			}
			total_pixels += i2;
			k1 += l1;
		}
	}

	public static void drawAlphaHorizontalLine(int x, int y, int lineWidth, int color, int alpha) {
		if (y < topY || y >= bottomY) return;
		if (x < topX) {
			lineWidth -= topX - x;
			x = topX;
		}
		if (x + lineWidth > bottomX) lineWidth = bottomX - x;

		// Optimization: Pre-calculate color components
		final int colorR = (color >> 16) & 0xff;
		final int colorG = (color >> 8) & 0xff;
		final int colorB = color & 0xff;
		int pos = x + y * width;

		for (int j3 = 0; j3 < lineWidth; j3++) {
			int alpha2 = (lineWidth - j3) / (lineWidth / alpha);
			final int invAlpha = INV_ALPHA_LOOKUP[alpha2];
			final int k1 = colorR * alpha2;
			final int l1 = colorG * alpha2;
			final int i2 = colorB * alpha2;
			final int dst = pixels[pos];
			final int j2 = (dst >> 16 & 0xff) * invAlpha;
			final int k2 = (dst >> 8 & 0xff) * invAlpha;
			final int l2 = (dst & 0xff) * invAlpha;
			pixels[pos++] = ((k1 + j2 >> 8) << 16) + ((l1 + k2 >> 8) << 8) + (i2 + l2 >> 8);
		}
	}

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

		// Optimization: Unrolled horizontal line
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

		// Optimization: Use lookup table for alpha
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

		// Optimization: Vertical line with better memory access
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

		// Optimization: Use lookup table for alpha
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

	public static void drawRectangle(int x, int y, int width, int height, int color, int alpha) {
		drawHorizontalLine(x, y, width, color, alpha);
		drawHorizontalLine(x, y + height - 1, width, color, alpha);
		if (height >= 3) {
			drawVerticalLine(x, y + 1, height - 2, color, alpha);
			drawVerticalLine(x + width - 1, y + 1, height - 2, color, alpha);
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

		// Optimization: Use lookup table and unrolled loops
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

	public static void drawHorizontalLine(int x, int y, int length, int color, int alpha) {
		if (y < topY || y >= bottomY) {
			return;
		}
		if (x < topX) {
			length -= topX - x;
			x = topX;
		}
		if (x + length > bottomX) {
			length = bottomX - x;
		}

		// Optimization: Use lookup table
		final int invAlpha = INV_ALPHA_LOOKUP[alpha];
		final int k1 = (color >> 16 & 0xff) * alpha;
		final int l1 = (color >> 8 & 0xff) * alpha;
		final int i2 = (color & 0xff) * alpha;
		int pos = x + y * width;

		for (int j3 = 0; j3 < length; j3++) {
			final int dst = pixels[pos];
			final int j2 = (dst >> 16 & 0xff) * invAlpha;
			final int k2 = (dst >> 8 & 0xff) * invAlpha;
			final int l2 = (dst & 0xff) * invAlpha;
			pixels[pos++] = (k1 + j2 >> 8 << 16) + (l1 + k2 >> 8 << 8) + (i2 + l2 >> 8);
		}
	}

	public static void drawVerticalLine(int x, int y, int length, int color, int alpha) {
		if (x < topX || x >= bottomX) {
			return;
		}
		if (y < topY) {
			length -= topY - y;
			y = topY;
		}
		if (y + length > bottomY) {
			length = bottomY - y;
		}

		// Optimization: Use lookup table
		final int invAlpha = INV_ALPHA_LOOKUP[alpha];
		final int k1 = (color >> 16 & 0xff) * alpha;
		final int l1 = (color >> 8 & 0xff) * alpha;
		final int i2 = (color & 0xff) * alpha;
		int pos = x + y * width;

		for (int j3 = 0; j3 < length; j3++) {
			final int dst = pixels[pos];
			final int j2 = (dst >> 16 & 0xff) * invAlpha;
			final int k2 = (dst >> 8 & 0xff) * invAlpha;
			final int l2 = (dst & 0xff) * invAlpha;
			pixels[pos] = (k1 + j2 >> 8 << 16) + (l1 + k2 >> 8 << 8) + (i2 + l2 >> 8);
			pos += width;
		}
	}
}