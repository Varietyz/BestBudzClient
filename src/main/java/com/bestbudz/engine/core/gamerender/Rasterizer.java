package com.bestbudz.engine.core.gamerender;

import com.bestbudz.cache.JsonCacheLoader;
import static com.bestbudz.engine.core.gamerender.ColorPalette.adjustColorBrightness;
import com.bestbudz.graphics.Background;
import com.google.gson.JsonObject;

public final class Rasterizer extends DrawingArea
{

	public static int textureAmount = 0;
	public static boolean lowMemoryMode = true;
	public static int viewportCenterX;
	public static int viewportCenterY;
	public static int[] sinTable;
	public static int[] cosTable;
	public static int[] scanlineOffsets;
	public static Background[] backgroundTextures = new Background[0];
	public static int[] textureLastUsed = new int[0];
	public static int textureAccessCounter;
	public static int[] colorPalette = new int[0x10000];
	private static int loadedTextureCount;
	private static boolean[] textureHasTransparency = new boolean[0];
	private static int[] textureAverageColors = new int[0];
	private static int poolIndex;
	private static int[][][] texturePool;
	private static int[][][] activeMipmaps = new int[0][][];
	public static int[][] textureColorArrays = new int[0][];
	static {
		sinTable = new int[2048];
		cosTable = new int[2048];
		for (int y = 0; y < 2048; y++) {
			sinTable[y] = (int) (65536D * Math.sin((double) y * 0.0030679614999999999D));
			cosTable[y] = (int) (65536D * Math.cos((double) y * 0.0030679614999999999D));
		}
	}

	public static void nullLoader() {
		scanlineOffsets = null;
		backgroundTextures = null;
		textureHasTransparency = null;
		textureAverageColors = null;
		texturePool = null;
		activeMipmaps = null;
		textureLastUsed = null;
		colorPalette = null;
		textureColorArrays = null;
	}

	public static void initializeViewport() {
		scanlineOffsets = new int[DrawingArea.height];
		for (int x = 0; x < DrawingArea.height; x++)
			scanlineOffsets[x] = DrawingArea.width * x;

		viewportCenterX = DrawingArea.width / 2;
		viewportCenterY = DrawingArea.height / 2;
	}

	public static void setViewportSize(int width, int height) {
		scanlineOffsets = new int[height];
		for (int l = 0; l < height; l++)
			scanlineOffsets[l] = width * l;

		viewportCenterX = width / 2;
		viewportCenterY = height / 2;
	}

	public static void clearMipmapCache() {
		texturePool = null;
		if (activeMipmaps != null) {
			for (int x = 0; x < textureAmount; x++)
				activeMipmaps[x] = null;
		}
	}

	public static void initializeTexturePool() {
		if (texturePool == null) {
			poolIndex = 20;
			texturePool = new int[poolIndex][][];
			for (int index = 0; index < poolIndex; index++) {
				texturePool[index] = new int[][] { new int[16384], new int[4096], new int[1024], new int[256],
						new int[64], new int[16], new int[4], new int[1] };
			}
			if (activeMipmaps != null) {
				for (int y = 0; y < textureAmount; y++)
					activeMipmaps[y] = null;
			}
		}
	}

	private static void initTextureArrays(int count) {
		textureAmount = count;
		backgroundTextures = new Background[count];
		textureLastUsed = new int[count];
		textureHasTransparency = new boolean[count];
		textureAverageColors = new int[count];
		activeMipmaps = new int[count][][];
		textureColorArrays = new int[count][];
	}

	public static void loadTextures() {
		// Determine texture count dynamically from extracted cache index
		int count = 0;
		JsonObject index = JsonCacheLoader.loadJsonObject("textures/_index.json");
		if (index != null) {
			for (String key : index.keySet()) {
				try {
					int id = Integer.parseInt(key);
					if (id >= count) count = id + 1;
				} catch (NumberFormatException ignored) {}
			}
		}
		if (count == 0) count = 51; // fallback for missing index
		initTextureArrays(count);
		System.out.println("[Rasterizer] Loading " + count + " textures from extracted cache");

		loadedTextureCount = 0;
		for (int x = 0; x < textureAmount; x++)
			try {
				backgroundTextures[x] = Background.loadFromExtracted(String.valueOf(x), 0);
				if (lowMemoryMode && backgroundTextures[x].anInt1456 == 128)
					backgroundTextures[x].method356();
				else
					backgroundTextures[x].method357();
				loadedTextureCount++;
			} catch (Exception _ex)
			{
				throw new RuntimeException(_ex);
			}

	}

	public static int getTextureAverageColor(int textureId) {
		if (textureAverageColors[textureId] != 0)
			return textureAverageColors[textureId];
		int y = 0;
		int l = 0;
		int shade3 = 0;
		int j1 = textureColorArrays[textureId].length;
		for (int shade1 = 0; shade1 < j1; shade1++) {
			y += textureColorArrays[textureId][shade1] >> 16 & 0xff;
			l += textureColorArrays[textureId][shade1] >> 8 & 0xff;
			shade3 += textureColorArrays[textureId][shade1] & 0xff;
		}

		int shade2 = (y / j1 << 16) + (l / j1 << 8) + shade3 / j1;
		shade2 = adjustColorBrightness(shade2, 1.3999999999999999D);
		if (shade2 == 0)
			shade2 = 1;
		textureAverageColors[textureId] = shade2;
		return shade2;
	}

	public static void applyTexture(int textureId) {
		if (activeMipmaps[textureId] == null)
			return;
		texturePool[poolIndex++] = activeMipmaps[textureId];
		activeMipmaps[textureId] = null;
	}

	public static int[][] getMipmapTexels(int textureId) {
		if (textureAccessCounter > 1000000) {
			for (int index = 0; index < textureAmount; index++) {
				textureLastUsed[index] = 0;
			}
			textureAccessCounter = 1;
		}

		if (textureId < 0 || textureId >= textureAmount) {
			return null;
		}

		textureLastUsed[textureId] = textureAccessCounter++;

		if (activeMipmaps[textureId] != null)
			return activeMipmaps[textureId];

		int[][] texels;
		if (poolIndex > 0) {
			texels = texturePool[--poolIndex];
			texturePool[poolIndex] = null;
		} else {
			int lastUsed = Integer.MAX_VALUE;
			int target = -1;
			for (int l = 0; l < loadedTextureCount; l++) {
				if (activeMipmaps[l] != null && textureLastUsed[l] < lastUsed) {
					lastUsed = textureLastUsed[l];
					target = l;
				}
			}

			if (target != -1) {
				texels = activeMipmaps[target];
				activeMipmaps[target] = null;
			} else {
				texels = new int[][] {
					new int[16384], new int[4096], new int[1024], new int[256],
					new int[64], new int[16], new int[4], new int[1]
				};
			}
		}

		activeMipmaps[textureId] = texels;
		Background background = backgroundTextures[textureId];
		int[] texturePalette = textureColorArrays[textureId];

		if (background.backgroundWidth == 64) {
			for (int j1 = 0; j1 < 128; j1++) {
				for (int j2 = 0; j2 < 128; j2++)
					texels[0][j2 + (j1 << 7)] = texturePalette[background.textureData[(j2 >> 1) + ((j1 >> 1) << 6)] & 0xFF];
			}
		} else {
			for (int shade1 = 0; shade1 < 16384; shade1++)
				texels[0][shade1] = texturePalette[background.textureData[shade1] & 0xFF];
		}

		textureHasTransparency[textureId] = false;
		for (int shade2 = 0; shade2 < 16384; shade2++) {
			texels[0][shade2] &= 0xf8f8ff;
			int k2 = texels[0][shade2];
			if (k2 == 0)
				textureHasTransparency[textureId] = true;
		}

		for (int level = 1, size = 64; level < 8; level++) {
			int[] src = texels[level - 1];
			int[] dst = texels[level];
			for (int x = 0; x < size; x++) {
				for (int y = 0; y < size; y++) {
					double r = 0, g = 0, b = 0;
					int count = 0;
					for (int rgb : new int[] {
						src[x + (y * size << 1) << 1],
						src[(x + (y * size << 1) << 1) + 1],
						src[(x + (y * size << 1) << 1) + (size << 1)],
						src[(x + (y * size << 1) << 1) + (size << 1) + 1]
					}) {
						if (rgb != 0) {
							double dr = (rgb >> 16 & 0xff) / 255d;
							double dg = (rgb >> 8 & 0xff) / 255d;
							double db = (rgb & 0xff) / 255d;
							r += dr * dr;
							g += dg * dg;
							b += db * db;
							count++;
						}
					}
					if (count != 0) {
						int ri = Math.round(255 * (float) Math.sqrt(r / count));
						int gi = Math.round(255 * (float) Math.sqrt(g / count));
						int bi = Math.round(255 * (float) Math.sqrt(b / count));
						dst[x + y * size] = ri << 16 | gi << 8 | bi;
					} else {
						dst[x + y * size] = 0;
					}
				}
			}
			size >>= 1;
		}

		return texels;
	}


}
