package com.bestbudz.engine.core.gamerender;

import com.bestbudz.engine.core.Client;
import com.bestbudz.network.Stream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class Texture {

	// Constants for better maintainability
	private static final int CACHE_SIZE = 678;
	private static final int MAX_MIPMAP_LEVELS = 8;
	private static final int BASE_TEXTURE_SIZE = 128;
	private static final int MAX_TEXTURE_DIMENSION = 128;
	private static final int UPSCALE_THRESHOLD = 64;
	private static final int UPSCALE_FACTOR = 2;
	private static final float INV_255 = 1.0f / 255.0f;

	// Thread-safe cache with better performance characteristics
	private static final ConcurrentHashMap<Integer, Texture> textureCache = new ConcurrentHashMap<>(CACHE_SIZE);
	private static final ReentrantReadWriteLock cacheLock = new ReentrantReadWriteLock();

	// Pre-allocated arrays for mipmap levels to reduce GC pressure
	public final int[][] mipmaps = new int[MAX_MIPMAP_LEVELS][];

	// Validation and bounds checking
	public static Texture get(int index) {
		if (!isValidIndex(index)) {
			return null;
		}

		cacheLock.readLock().lock();
		try {
			Texture texture = textureCache.get(index);
			if (texture != null) {
				return texture;
			}
		} finally {
			cacheLock.readLock().unlock();
		}

		// Request texture loading if not in cache
		if (Client.onDemandFetcher != null) {
			Client.onDemandFetcher.method558(4, index);
		}
		return null;
	}

	public static boolean decode(int index, byte[] data) {
		if (!isValidIndex(index) || data == null || data.length < 4) {
			return false;
		}

		try {
			final Texture texture = new Texture();
			final Stream buffer = new Stream(data);

			final int width = buffer.readUnsignedWord();
			final int height = buffer.readUnsignedWord();

			// Validate dimensions
			if (!isValidDimensions(width, height)) {
				return false;
			}

			// Initialize base mipmap with proper size
			texture.mipmaps[0] = new int[BASE_TEXTURE_SIZE * BASE_TEXTURE_SIZE];

			// Optimized texture loading with better memory access patterns
			if (width <= UPSCALE_THRESHOLD && height <= UPSCALE_THRESHOLD) {
				decodeWithUpscaling(texture, buffer, width, height);
			} else {
				decodeDirectly(texture, buffer, width, height);
			}

			// Generate mipmaps efficiently
			texture.generateMipmaps();

			// Thread-safe cache insertion
			cacheLock.writeLock().lock();
			try {
				textureCache.put(index, texture);
			} finally {
				cacheLock.writeLock().unlock();
			}

			return true;

		} catch (Exception e) {
			// Log error in production code
			System.err.println("Failed to decode texture " + index + ": " + e.getMessage());
			return false;
		}
	}

	private static void decodeWithUpscaling(Texture texture, Stream buffer, int width, int height) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int rgb = buffer.read3Bytes();
				final int x2 = x << 1;
				final int y2 = y << 1;

				// Batch set operations for better cache locality
				texture.setPixel(x2, y2, rgb);
				texture.setPixel(x2 + 1, y2, rgb);
				texture.setPixel(x2 + 1, y2 + 1, rgb);
				texture.setPixel(x2, y2 + 1, rgb);
			}
		}
	}

	private static void decodeDirectly(Texture texture, Stream buffer, int width, int height) {
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int rgb = buffer.read3Bytes();
				texture.setPixel(x, y, rgb);
			}
		}
	}

	private void setPixel(int x, int y, int rgb) {
		if (isValidCoordinate(x, y)) {
			mipmaps[0][x + (y << 7)] = rgb;
		}
	}

	private void generateMipmaps() {
		int currentSize = BASE_TEXTURE_SIZE >> 1; // Start at 64x64

		for (int level = 1; level < MAX_MIPMAP_LEVELS && currentSize > 0; level++) {
			final int[] srcMipmap = mipmaps[level - 1];
			final int[] dstMipmap = mipmaps[level] = new int[currentSize * currentSize];
			final int srcSize = currentSize << 1;

			generateMipmapLevel(srcMipmap, dstMipmap, srcSize, currentSize);
			currentSize >>= 1;
		}
	}

	private static void generateMipmapLevel(int[] src, int[] dst, int srcSize, int dstSize) {
		for (int y = 0; y < dstSize; y++) {
			final int srcY = y << 1;
			final int srcRowOffset = srcY * srcSize;
			final int srcNextRowOffset = (srcY + 1) * srcSize;
			final int dstRowOffset = y * dstSize;

			for (int x = 0; x < dstSize; x++) {
				final int srcX = x << 1;

				// Sample 2x2 block from source mipmap
				final int rgb1 = src[srcRowOffset + srcX];
				final int rgb2 = src[srcRowOffset + srcX + 1];
				final int rgb3 = src[srcNextRowOffset + srcX];
				final int rgb4 = src[srcNextRowOffset + srcX + 1];

				// Use faster averaging method for better performance
				dst[dstRowOffset + x] = averageColors(rgb1, rgb2, rgb3, rgb4);
			}
		}
	}

	private static int averageColors(int rgb1, int rgb2, int rgb3, int rgb4) {
		// Fast path: if all colors are the same
		if (rgb1 == rgb2 && rgb2 == rgb3 && rgb3 == rgb4) {
			return rgb1;
		}

		// Count non-zero pixels for proper averaging
		int count = 0;
		long totalR = 0, totalG = 0, totalB = 0;

		for (int rgb : new int[]{rgb1, rgb2, rgb3, rgb4}) {
			if (rgb != 0) {
				// Extract RGB components efficiently
				final int r = (rgb >> 16) & 0xFF;
				final int g = (rgb >> 8) & 0xFF;
				final int b = rgb & 0xFF;

				// Gamma-correct averaging (squared values for better visual quality)
				totalR += r * r;
				totalG += g * g;
				totalB += b * b;
				count++;
			}
		}

		if (count == 0) {
			return 0;
		}

		// Calculate gamma-corrected average
		final int avgR = (int) Math.sqrt(totalR / count);
		final int avgG = (int) Math.sqrt(totalG / count);
		final int avgB = (int) Math.sqrt(totalB / count);

		// Clamp values and combine
		return (Math.min(avgR, 255) << 16) | (Math.min(avgG, 255) << 8) | Math.min(avgB, 255);
	}

	// Thread-safe cache management
	public static void clearCache() {
		cacheLock.writeLock().lock();
		try {
			textureCache.clear();
		} finally {
			cacheLock.writeLock().unlock();
		}
	}

	public static void removeTexture(int index) {
		if (isValidIndex(index)) {
			cacheLock.writeLock().lock();
			try {
				textureCache.remove(index);
			} finally {
				cacheLock.writeLock().unlock();
			}
		}
	}

	public static int getCacheSize() {
		cacheLock.readLock().lock();
		try {
			return textureCache.size();
		} finally {
			cacheLock.readLock().unlock();
		}
	}

	// Validation helpers
	private static boolean isValidIndex(int index) {
		return index >= 0 && index < CACHE_SIZE;
	}

	private static boolean isValidDimensions(int width, int height) {
		return width > 0 && height > 0 &&
			width <= MAX_TEXTURE_DIMENSION && height <= MAX_TEXTURE_DIMENSION &&
			isPowerOfTwo(width) && isPowerOfTwo(height);
	}

	private static boolean isValidCoordinate(int x, int y) {
		return x >= 0 && y >= 0 && x < BASE_TEXTURE_SIZE && y < BASE_TEXTURE_SIZE;
	}

	private static boolean isPowerOfTwo(int value) {
		return value > 0 && (value & (value - 1)) == 0;
	}

	// Memory management
	public void dispose() {
		for (int i = 0; i < mipmaps.length; i++) {
			mipmaps[i] = null;
		}
	}

	// Legacy compatibility
	@Deprecated
	public static void reset() {
		clearCache();
	}
}