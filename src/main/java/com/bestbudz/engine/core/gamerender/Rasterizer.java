package com.bestbudz.engine.core.gamerender;

import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.engine.core.gamerender.ColorPalette.adjustColorBrightness;
import com.bestbudz.graphics.Background;
import com.bestbudz.network.ArchiveLoader;

public final class Rasterizer extends DrawingArea
{

	public static final int[] reciprocalTable;
	public static final int textureAmount = 51;
	public static boolean lowMemoryMode = true;
	public static boolean enableClipping;
	public static boolean enableDepthBuffer = true;
	public static int alphaBlendValue;
	public static int viewportCenterX;
	public static int viewportCenterY;
	public static int[] sinTable;
	public static int[] cosTable;
	public static int[] scanlineOffsets;
	public static Background[] backgroundTextures = new Background[textureAmount];
	public static int[] textureLastUsed = new int[textureAmount];
	public static int textureAccessCounter;
	public static int[] colorPalette = new int[0x10000];
	private static int currentMipmapLevel;
	private static boolean isOpaque;
	private static final int[] DIVISION_TABLE;
	private static int loadedTextureCount;
	private static boolean[] textureHasTransparency = new boolean[textureAmount];
	private static int[] textureAverageColors = new int[textureAmount];
	private static int poolIndex;
	private static int[][][] texturePool;
	private static int[][][] activeMipmaps = new int[textureAmount][][];
	public static int[][] textureColorArrays = new int[textureAmount][];
	public static boolean isRenderingItem = false;
	static {
		DIVISION_TABLE = new int[512];
		reciprocalTable = new int[2048];
		sinTable = new int[2048];
		cosTable = new int[2048];
		for (int index = 1; index < 512; index++) {
			DIVISION_TABLE[index] = 32768 / index;
		}
		for (int x = 1; x < 2048; x++) {
			reciprocalTable[x] = 0x10000 / x;
		}
		for (int y = 0; y < 2048; y++) {
			sinTable[y] = (int) (65536D * Math.sin((double) y * 0.0030679614999999999D));
			cosTable[y] = (int) (65536D * Math.cos((double) y * 0.0030679614999999999D));
		}
	}

	public static void nullLoader() {
		sinTable = null;
		cosTable = null;
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
		for (int x = 0; x < textureAmount; x++)
			activeMipmaps[x] = null;

	}

	public static void initializeTexturePool() {
		if (texturePool == null) {
			poolIndex = 20;
			texturePool = new int[poolIndex][][];
			for (int index = 0; index < poolIndex; index++) {
				texturePool[index] = new int[][] { new int[16384], new int[4096], new int[1024], new int[256],
						new int[64], new int[16], new int[4], new int[1] };
			}
			for (int y = 0; y < textureAmount; y++)
				activeMipmaps[y] = null;
		}
	}

	public static void loadTextures(ArchiveLoader archiveLoader) {
		loadedTextureCount = 0;
		for (int x = 0; x < textureAmount; x++)
			try {
				backgroundTextures[x] = new Background(archiveLoader, String.valueOf(x), 0);
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

	// COMPLETE METHOD TO REPLACE: getMipmapTexels
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
					texels[0][j2 + (j1 << 7)] = texturePalette[background.textureData[(j2 >> 1) + ((j1 >> 1) << 6)]];
			}
		} else {
			for (int shade1 = 0; shade1 < 16384; shade1++)
				texels[0][shade1] = texturePalette[background.textureData[shade1]];
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



	private static int texelPos(int defaultIndex) {
		int x = (defaultIndex & 127) >> currentMipmapLevel;
		int y = (defaultIndex >> 7) >> currentMipmapLevel;
		return x + (y << (7 - currentMipmapLevel));
	}

	private static void setMipmapLevel(int vertexY1, int vertexY2, int vertexY3, int vertexX1, int vertexX2, int vertexX3, int textureId) {
		if (!enableDepthBuffer) {
			currentMipmapLevel = 0;
			return;
		}
		if (!SettingsConfig.enableMipMapping) {
			if (currentMipmapLevel != 0) {
				currentMipmapLevel = 0;
			}
			return;
		}
		if (textureId == 17 || textureId == 34) {
			currentMipmapLevel = 0;
			return;
		}
		int textureArea = vertexX1 * (vertexY2 - vertexY3) + vertexX2 * (vertexY3 - vertexY1) + vertexX3 * (vertexY1 - vertexY2) >> 1;
		if (textureArea < 0) {
			textureArea = -textureArea;
		}
		if (textureArea > 16384) {
			currentMipmapLevel = 0;
		} else if (textureArea > 4096) {
			currentMipmapLevel = 1;
		} else if (textureArea > 1024) {
			currentMipmapLevel = 1;
		} else if (textureArea > 256) {
			currentMipmapLevel = 2;
		} else if (textureArea > 64) {
			currentMipmapLevel = 3;
		} else if (textureArea > 16) {
			currentMipmapLevel = 4;
		} else if (textureArea > 4) {
			currentMipmapLevel = 5;
		} else if (textureArea > 1) {
			currentMipmapLevel = 6;
		} else {
			currentMipmapLevel = 7;
		}
	}

	// NEW METHOD TO ADD: drawDepthTriangle
	public static void drawDepthTriangle(int x_a, int x_b, int x_c, int y_a, int y_b, int y_c, float z_a, float z_b, float z_c) {
		int a_to_b = 0;
		if (y_b != y_a) {
			a_to_b = (x_b - x_a << 16) / (y_b - y_a);
		}
		int b_to_c = 0;
		if (y_c != y_b) {
			b_to_c = (x_c - x_b << 16) / (y_c - y_b);
		}
		int c_to_a = 0;
		if (y_c != y_a) {
			c_to_a = (x_a - x_c << 16) / (y_a - y_c);
		}

		float b_aX = x_b - x_a;
		float b_aY = y_b - y_a;
		float c_aX = x_c - x_a;
		float c_aY = y_c - y_a;
		float b_aZ = z_b - z_a;
		float c_aZ = z_c - z_a;

		float div = b_aX * c_aY - c_aX * b_aY;
		float depth_slope = (b_aZ * c_aY - c_aZ * b_aY) / div;
		float depth_increment = (c_aZ * b_aX - b_aZ * c_aX) / div;
		if (y_a <= y_b && y_a <= y_c) {
			if (y_a < DrawingArea.bottomY) {
				if (y_b > DrawingArea.bottomY)
					y_b = DrawingArea.bottomY;
				if (y_c > DrawingArea.bottomY)
					y_c = DrawingArea.bottomY;
				z_a = z_a - depth_slope * x_a + depth_slope;
				if (y_b < y_c) {
					x_c = x_a <<= 16;
					if (y_a < 0) {
						x_c -= c_to_a * y_a;
						x_a -= a_to_b * y_a;
						z_a -= depth_increment * y_a;
						y_a = 0;
					}
					x_b <<= 16;
					if (y_b < 0) {
						x_b -= b_to_c * y_b;
						y_b = 0;
					}
					if (y_a != y_b && c_to_a < a_to_b || y_a == y_b && c_to_a > b_to_c) {
						y_c -= y_b;
						y_b -= y_a;
						y_a = scanlineOffsets[y_a];
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_c >> 16, x_b >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_b += b_to_c;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					} else {
						y_c -= y_b;
						y_b -= y_a;
						y_a = scanlineOffsets[y_a];
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_b >> 16, x_c >> 16, z_a, depth_slope);
							x_c += c_to_a;
							x_b += b_to_c;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					}
				} else {
					x_b = x_a <<= 16;
					if (y_a < 0) {
						x_b -= c_to_a * y_a;
						x_a -= a_to_b * y_a;
						z_a -= depth_increment * y_a;
						y_a = 0;
					}
					x_c <<= 16;
					if (y_c < 0) {
						x_c -= b_to_c * y_c;
						y_c = 0;
					}
					if (y_a != y_c && c_to_a < a_to_b || y_a == y_c && b_to_c > a_to_b) {
						y_b -= y_c;
						y_c -= y_a;
						y_a = scanlineOffsets[y_a];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_b >> 16, x_a >> 16, z_a, depth_slope);
							x_b += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_c >> 16, x_a >> 16, z_a, depth_slope);
							x_c += b_to_c;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					} else {
						y_b -= y_c;
						y_c -= y_a;
						y_a = scanlineOffsets[y_a];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_a, x_a >> 16, x_b >> 16, z_a, depth_slope);
							x_b += c_to_a;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
						while (--y_b >= 0) {
							drawDepthTriangleScanline(y_a, x_a >> 16, x_c >> 16, z_a, depth_slope);
							x_c += b_to_c;
							x_a += a_to_b;
							z_a += depth_increment;
							y_a += DrawingArea.width;
						}
					}
				}
			}
		} else if (y_b <= y_c) {
			if (y_b < DrawingArea.bottomY) {
				if (y_c > DrawingArea.bottomY)
					y_c = DrawingArea.bottomY;
				if (y_a > DrawingArea.bottomY)
					y_a = DrawingArea.bottomY;
				z_b = z_b - depth_slope * x_b + depth_slope;
				if (y_c < y_a) {
					x_a = x_b <<= 16;
					if (y_b < 0) {
						x_a -= a_to_b * y_b;
						x_b -= b_to_c * y_b;
						z_b -= depth_increment * y_b;
						y_b = 0;
					}
					x_c <<= 16;
					if (y_c < 0) {
						x_c -= c_to_a * y_c;
						y_c = 0;
					}
					if (y_b != y_c && a_to_b < b_to_c || y_b == y_c && a_to_b > c_to_a) {
						y_a -= y_c;
						y_c -= y_b;
						y_b = scanlineOffsets[y_b];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_a >> 16, x_c >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_c += c_to_a;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					} else {
						y_a -= y_c;
						y_c -= y_b;
						y_b = scanlineOffsets[y_b];
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_c >> 16, x_a >> 16, z_b, depth_slope);
							x_a += a_to_b;
							x_c += c_to_a;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					}
				} else {
					x_c = x_b <<= 16;
					if (y_b < 0) {
						x_c -= a_to_b * y_b;
						x_b -= b_to_c * y_b;
						z_b -= depth_increment * y_b;
						y_b = 0;
					}
					x_a <<= 16;
					if (y_a < 0) {
						x_a -= c_to_a * y_a;
						y_a = 0;
					}
					if (a_to_b < b_to_c) {
						y_c -= y_a;
						y_a -= y_b;
						y_b = scanlineOffsets[y_b];
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_c >> 16, x_b >> 16, z_b, depth_slope);
							x_c += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_a >> 16, x_b >> 16, z_b, depth_slope);
							x_a += c_to_a;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					} else {
						y_c -= y_a;
						y_a -= y_b;
						y_b = scanlineOffsets[y_b];
						while (--y_a >= 0) {
							drawDepthTriangleScanline(y_b, x_b >> 16, x_c >> 16, z_b, depth_slope);
							x_c += a_to_b;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
						while (--y_c >= 0) {
							drawDepthTriangleScanline(y_b, x_b >> 16, x_a >> 16, z_b, depth_slope);
							x_a += c_to_a;
							x_b += b_to_c;
							z_b += depth_increment;
							y_b += DrawingArea.width;
						}
					}
				}
			}
		} else if (y_c < DrawingArea.bottomY) {
			if (y_a > DrawingArea.bottomY)
				y_a = DrawingArea.bottomY;
			if (y_b > DrawingArea.bottomY)
				y_b = DrawingArea.bottomY;
			z_c = z_c - depth_slope * x_c + depth_slope;
			if (y_a < y_b) {
				x_b = x_c <<= 16;
				if (y_c < 0) {
					x_b -= b_to_c * y_c;
					x_c -= c_to_a * y_c;
					z_c -= depth_increment * y_c;
					y_c = 0;
				}
				x_a <<= 16;
				if (y_a < 0) {
					x_a -= a_to_b * y_a;
					y_a = 0;
				}
				if (b_to_c < c_to_a) {
					y_b -= y_a;
					y_a -= y_c;
					y_c = scanlineOffsets[y_c];
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_b >> 16, x_a >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_a += a_to_b;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				} else {
					y_b -= y_a;
					y_a -= y_c;
					y_c = scanlineOffsets[y_c];
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_a >> 16, x_b >> 16, z_c, depth_slope);
						x_b += b_to_c;
						x_a += a_to_b;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				}
			} else {
				x_a = x_c <<= 16;
				if (y_c < 0) {
					x_a -= b_to_c * y_c;
					x_c -= c_to_a * y_c;
					z_c -= depth_increment * y_c;
					y_c = 0;
				}
				x_b <<= 16;
				if (y_b < 0) {
					x_b -= a_to_b * y_b;
					y_b = 0;
				}
				if (b_to_c < c_to_a) {
					y_a -= y_b;
					y_b -= y_c;
					y_c = scanlineOffsets[y_c];
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_a >> 16, x_c >> 16, z_c, depth_slope);
						x_a += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_b >> 16, x_c >> 16, z_c, depth_slope);
						x_b += a_to_b;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				} else {
					y_a -= y_b;
					y_b -= y_c;
					y_c = scanlineOffsets[y_c];
					while (--y_b >= 0) {
						drawDepthTriangleScanline(y_c, x_c >> 16, x_a >> 16, z_c, depth_slope);
						x_a += b_to_c;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
					while (--y_a >= 0) {
						drawDepthTriangleScanline(y_c, x_c >> 16, x_b >> 16, z_c, depth_slope);
						x_b += a_to_b;
						x_c += c_to_a;
						z_c += depth_increment;
						y_c += DrawingArea.width;
					}
				}
			}
		}
	}

	// NEW METHOD TO ADD: drawDepthTriangleScanline
	private static void drawDepthTriangleScanline(int dest_off, int start_x, int end_x, float depth, float depth_slope) {
		int dbl = DrawingArea.depthBuffer.length;
		if (enableClipping) {
			if (end_x > DrawingArea.width) {
				end_x = DrawingArea.width;
			}
			if (start_x < 0) {
				start_x = 0;
			}
		}
		if (start_x >= end_x) {
			return;
		}
		dest_off += start_x - 1;
		int loops = end_x - start_x >> 2;
		depth += depth_slope * (float) start_x;
		if (alphaBlendValue == 0) {
			while (--loops >= 0) {
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
			}
			for (loops = end_x - start_x & 3; --loops >= 0;) {
				dest_off++;
				if (dest_off >= 0 && dest_off < dbl) {
					DrawingArea.depthBuffer[dest_off] = depth;
				}
				depth += depth_slope;
			}
			return;
		}
		while (--loops >= 0) {
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
		}
		for (loops = end_x - start_x & 3; --loops >= 0;) {
			dest_off++;
			if (dest_off >= 0 && dest_off < dbl) {
				DrawingArea.depthBuffer[dest_off] = depth;
			}
			depth += depth_slope;
		}
	}

	public static void renderFlatTriangle(int vertexY1, int vertexY2, int vertexY3, int vertexX1, int vertexX2, int vertexX3,
										  int color1, int color2, int color3, float vertexZ1, float vertexZ2, float vertexZ3) {
		int faceColor = color1;

		if (SettingsConfig.enableFlatShading && !isRenderingItem) {
			renderTriangle(vertexY1, vertexY2, vertexY3, vertexX1, vertexX2, vertexX3, faceColor, faceColor, faceColor, vertexZ1, vertexZ2, vertexZ3);
		} else {
			renderTriangle(vertexY1, vertexY2, vertexY3, vertexX1, vertexX2, vertexX3, color1, color2, color3, vertexZ1, vertexZ2, vertexZ3);
		}
	}


	public static void renderTriangle(int vertexY1, int vertexY2, int vertexY3, int vertexX1, int vertexX2, int vertexX3, int color1, int color2, int color3, float vertexZ1,
									  float vertexZ2, float vertexZ3) {
		if ((vertexY1 < 0 && vertexY2 < 0 && vertexY3 < 0) || (vertexY1 >= DrawingArea.bottomY && vertexY2 >= DrawingArea.bottomY && vertexY3 >= DrawingArea.bottomY))
			return;

		if (vertexZ1 < 0.0F && vertexZ2 < 0.0F && vertexZ3 < 0.0F) {
			return;
		}
		if (vertexZ1 > 10000.0F && vertexZ2 > 10000.0F && vertexZ3 > 10000.0F) {
			return;
		}

		if (enableDepthBuffer) {
			int crossProduct = (vertexX2 - vertexX1) * (vertexY3 - vertexY1) - (vertexX3 - vertexX1) * (vertexY2 - vertexY1);
			if (crossProduct >= 1) {
				return;
			}
		}

		int minX = Math.min(Math.min(vertexX1, vertexX2), vertexX3);
		int maxX = Math.max(Math.max(vertexX1, vertexX2), vertexX3);
		int minY = Math.min(Math.min(vertexY1, vertexY2), vertexY3);
		int maxY = Math.max(Math.max(vertexY1, vertexY2), vertexY3);

		if (maxX < 0 || minX >= DrawingArea.width || maxY < 0 || minY >= DrawingArea.height) {
			return;
		}

		int rgb1 = colorPalette[color1];
		int rgb2 = colorPalette[color2];
		int rgb3 = colorPalette[color3];
		int r1 = rgb1 >> 16 & 0xff;
		int g1 = rgb1 >> 8 & 0xff;
		int b1 = rgb1 & 0xff;
		int r2 = rgb2 >> 16 & 0xff;
		int g2 = rgb2 >> 8 & 0xff;
		int b2 = rgb2 & 0xff;
		int r3 = rgb3 >> 16 & 0xff;
		int g3 = rgb3 >> 8 & 0xff;
		int b3 = rgb3 & 0xff;
		int edge1Slope = 0;
		int redGradient1 = 0;
		int greenGradient1 = 0;
		int blueGradient1 = 0;
		if (vertexY2 != vertexY1) {
			edge1Slope = (vertexX2 - vertexX1 << 16) / (vertexY2 - vertexY1);
			redGradient1 = (r2 - r1 << 16) / (vertexY2 - vertexY1);
			greenGradient1 = (g2 - g1 << 16) / (vertexY2 - vertexY1);
			blueGradient1 = (b2 - b1 << 16) / (vertexY2 - vertexY1);
		}
		int edge2Slope = 0;
		int redGradient2 = 0;
		int greenGradient2 = 0;
		int blueGradient2 = 0;
		if (vertexY3 != vertexY2) {
			edge2Slope = (vertexX3 - vertexX2 << 16) / (vertexY3 - vertexY2);
			redGradient2 = (r3 - r2 << 16) / (vertexY3 - vertexY2);
			greenGradient2 = (g3 - g2 << 16) / (vertexY3 - vertexY2);
			blueGradient2 = (b3 - b2 << 16) / (vertexY3 - vertexY2);
		}
		int edge3Slope = 0;
		int redGradient3 = 0;
		int greenGradient3 = 0;
		int blueGradient3 = 0;
		if (vertexY3 != vertexY1) {
			edge3Slope = (vertexX1 - vertexX3 << 16) / (vertexY1 - vertexY3);
			redGradient3 = (r1 - r3 << 16) / (vertexY1 - vertexY3);
			greenGradient3 = (g1 - g3 << 16) / (vertexY1 - vertexY3);
			blueGradient3 = (b1 - b3 << 16) / (vertexY1 - vertexY3);
		}
		float edgeAX = vertexX2 - vertexX1;
		float edgeAY = vertexY2 - vertexY1;
		float edgeBX = vertexX3 - vertexX1;
		float edgeBY = vertexY3 - vertexY1;
		float edgeAZ = vertexZ2 - vertexZ1;
		float edgeBZ = vertexZ3 - vertexZ1;

		float areaInverse = edgeAX * edgeBY - edgeBX * edgeAY;
		float depthSlopeX = (edgeAZ * edgeBY - edgeBZ * edgeAY) / areaInverse;
		float depthSlopeY = (edgeBZ * edgeAX - edgeAZ * edgeBX) / areaInverse;
		if (vertexY1 <= vertexY2 && vertexY1 <= vertexY3) {
			if (vertexY1 >= DrawingArea.bottomY) {
				return;
			}
			if (vertexY2 > DrawingArea.bottomY) {
				vertexY2 = DrawingArea.bottomY;
			}
			if (vertexY3 > DrawingArea.bottomY) {
				vertexY3 = DrawingArea.bottomY;
			}
			vertexZ1 = vertexZ1 - depthSlopeX * vertexX1 + depthSlopeX;
			if (vertexY2 < vertexY3) {
				vertexX3 = vertexX1 <<= 16;
				r3 = r1 <<= 16;
				g3 = g1 <<= 16;
				b3 = b1 <<= 16;
				if (vertexY1 < 0) {
					vertexX3 -= edge3Slope * vertexY1;
					vertexX1 -= edge1Slope * vertexY1;
					r3 -= redGradient3 * vertexY1;
					g3 -= greenGradient3 * vertexY1;
					b3 -= blueGradient3 * vertexY1;
					r1 -= redGradient1 * vertexY1;
					g1 -= greenGradient1 * vertexY1;
					b1 -= blueGradient1 * vertexY1;
					vertexZ1 -= depthSlopeY * vertexY1;
					vertexY1 = 0;
				}
				vertexX2 <<= 16;
				r2 <<= 16;
				g2 <<= 16;
				b2 <<= 16;
				if (vertexY2 < 0) {
					vertexX2 -= edge2Slope * vertexY2;
					r2 -= redGradient2 * vertexY2;
					g2 -= greenGradient2 * vertexY2;
					b2 -= blueGradient2 * vertexY2;
					vertexY2 = 0;
				}
				if (vertexY1 != vertexY2 && edge3Slope < edge1Slope || vertexY1 == vertexY2 && edge3Slope > edge2Slope) {
					vertexY3 -= vertexY2;
					vertexY2 -= vertexY1;
					for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY2 >= 0; vertexY1 += DrawingArea.width) {
						rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX3 >> 16, vertexX1 >> 16, r3, g3, b3, r1, g1, b1, vertexZ1, depthSlopeX);
						vertexX3 += edge3Slope;
						vertexX1 += edge1Slope;
						r3 += redGradient3;
						g3 += greenGradient3;
						b3 += blueGradient3;
						r1 += redGradient1;
						g1 += greenGradient1;
						b1 += blueGradient1;
						vertexZ1 += depthSlopeY;
					}
					while (--vertexY3 >= 0) {
						rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX3 >> 16, vertexX2 >> 16, r3, g3, b3, r2, g2, b2, vertexZ1, depthSlopeX);
						vertexX3 += edge3Slope;
						vertexX2 += edge2Slope;
						r3 += redGradient3;
						g3 += greenGradient3;
						b3 += blueGradient3;
						r2 += redGradient2;
						g2 += greenGradient2;
						b2 += blueGradient2;
						vertexY1 += DrawingArea.width;
						vertexZ1 += depthSlopeY;
					}
					return;
				}
				vertexY3 -= vertexY2;
				vertexY2 -= vertexY1;
				for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY2 >= 0; vertexY1 += DrawingArea.width) {
					rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX1 >> 16, vertexX3 >> 16, r1, g1, b1, r3, g3, b3, vertexZ1, depthSlopeX);
					vertexX3 += edge3Slope;
					vertexX1 += edge1Slope;
					r3 += redGradient3;
					g3 += greenGradient3;
					b3 += blueGradient3;
					r1 += redGradient1;
					g1 += greenGradient1;
					b1 += blueGradient1;
					vertexZ1 += depthSlopeY;
				}
				while (--vertexY3 >= 0) {
					rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX2 >> 16, vertexX3 >> 16, r2, g2, b2, r3, g3, b3, vertexZ1, depthSlopeX);
					vertexX3 += edge3Slope;
					vertexX2 += edge2Slope;
					r3 += redGradient3;
					g3 += greenGradient3;
					b3 += blueGradient3;
					r2 += redGradient2;
					g2 += greenGradient2;
					b2 += blueGradient2;
					vertexY1 += DrawingArea.width;
					vertexZ1 += depthSlopeY;
				}
				return;
			}
			vertexX2 = vertexX1 <<= 16;
			r2 = r1 <<= 16;
			g2 = g1 <<= 16;
			b2 = b1 <<= 16;
			if (vertexY1 < 0) {
				vertexX2 -= edge3Slope * vertexY1;
				vertexX1 -= edge1Slope * vertexY1;
				r2 -= redGradient3 * vertexY1;
				g2 -= greenGradient3 * vertexY1;
				b2 -= blueGradient3 * vertexY1;
				r1 -= redGradient1 * vertexY1;
				g1 -= greenGradient1 * vertexY1;
				b1 -= blueGradient1 * vertexY1;
				vertexZ1 -= depthSlopeY * vertexY1;
				vertexY1 = 0;
			}
			vertexX3 <<= 16;
			r3 <<= 16;
			g3 <<= 16;
			b3 <<= 16;
			if (vertexY3 < 0) {
				vertexX3 -= edge2Slope * vertexY3;
				r3 -= redGradient2 * vertexY3;
				g3 -= greenGradient2 * vertexY3;
				b3 -= blueGradient2 * vertexY3;
				vertexY3 = 0;
			}
			if (vertexY1 != vertexY3 && edge3Slope < edge1Slope || vertexY1 == vertexY3 && edge2Slope > edge1Slope) {
				vertexY2 -= vertexY3;
				vertexY3 -= vertexY1;
				for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY3 >= 0; vertexY1 += DrawingArea.width) {
					rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX2 >> 16, vertexX1 >> 16, r2, g2, b2, r1, g1, b1, vertexZ1, depthSlopeX);
					vertexX2 += edge3Slope;
					vertexX1 += edge1Slope;
					r2 += redGradient3;
					g2 += greenGradient3;
					b2 += blueGradient3;
					r1 += redGradient1;
					g1 += greenGradient1;
					b1 += blueGradient1;
					vertexZ1 += depthSlopeY;
				}
				while (--vertexY2 >= 0) {
					rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX3 >> 16, vertexX1 >> 16, r3, g3, b3, r1, g1, b1, vertexZ1, depthSlopeX);
					vertexX3 += edge2Slope;
					vertexX1 += edge1Slope;
					r3 += redGradient2;
					g3 += greenGradient2;
					b3 += blueGradient2;
					r1 += redGradient1;
					g1 += greenGradient1;
					b1 += blueGradient1;
					vertexY1 += DrawingArea.width;
					vertexZ1 += depthSlopeY;
				}
				return;
			}
			vertexY2 -= vertexY3;
			vertexY3 -= vertexY1;
			for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY3 >= 0; vertexY1 += DrawingArea.width) {
				rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX1 >> 16, vertexX2 >> 16, r1, g1, b1, r2, g2, b2, vertexZ1, depthSlopeX);
				vertexX2 += edge3Slope;
				vertexX1 += edge1Slope;
				r2 += redGradient3;
				g2 += greenGradient3;
				b2 += blueGradient3;
				r1 += redGradient1;
				g1 += greenGradient1;
				b1 += blueGradient1;
				vertexZ1 += depthSlopeY;
			}
			while (--vertexY2 >= 0) {
				rasterizeScanline(DrawingArea.pixels, vertexY1, vertexX1 >> 16, vertexX3 >> 16, r1, g1, b1, r3, g3, b3, vertexZ1, depthSlopeX);
				vertexX3 += edge2Slope;
				vertexX1 += edge1Slope;
				r3 += redGradient2;
				g3 += greenGradient2;
				b3 += blueGradient2;
				r1 += redGradient1;
				g1 += greenGradient1;
				b1 += blueGradient1;
				vertexY1 += DrawingArea.width;
				vertexZ1 += depthSlopeY;
			}
			return;
		}
		if (vertexY2 <= vertexY3) {
			if (vertexY2 >= DrawingArea.bottomY) {
				return;
			}
			if (vertexY3 > DrawingArea.bottomY) {
				vertexY3 = DrawingArea.bottomY;
			}
			if (vertexY1 > DrawingArea.bottomY) {
				vertexY1 = DrawingArea.bottomY;
			}
			vertexZ2 = vertexZ2 - depthSlopeX * vertexX2 + depthSlopeX;
			if (vertexY3 < vertexY1) {
				vertexX1 = vertexX2 <<= 16;
				r1 = r2 <<= 16;
				g1 = g2 <<= 16;
				b1 = b2 <<= 16;
				if (vertexY2 < 0) {
					vertexX1 -= edge1Slope * vertexY2;
					vertexX2 -= edge2Slope * vertexY2;
					r1 -= redGradient1 * vertexY2;
					g1 -= greenGradient1 * vertexY2;
					b1 -= blueGradient1 * vertexY2;
					r2 -= redGradient2 * vertexY2;
					g2 -= greenGradient2 * vertexY2;
					b2 -= blueGradient2 * vertexY2;
					vertexZ2 -= depthSlopeY * vertexY2;
					vertexY2 = 0;
				}
				vertexX3 <<= 16;
				r3 <<= 16;
				g3 <<= 16;
				b3 <<= 16;
				if (vertexY3 < 0) {
					vertexX3 -= edge3Slope * vertexY3;
					r3 -= redGradient3 * vertexY3;
					g3 -= greenGradient3 * vertexY3;
					b3 -= blueGradient3 * vertexY3;
					vertexY3 = 0;
				}
				if (vertexY2 != vertexY3 && edge1Slope < edge2Slope || vertexY2 == vertexY3 && edge1Slope > edge3Slope) {
					vertexY1 -= vertexY3;
					vertexY3 -= vertexY2;
					for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY3 >= 0; vertexY2 += DrawingArea.width) {
						rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX1 >> 16, vertexX2 >> 16, r1, g1, b1, r2, g2, b2, vertexZ2, depthSlopeX);
						vertexX1 += edge1Slope;
						vertexX2 += edge2Slope;
						r1 += redGradient1;
						g1 += greenGradient1;
						b1 += blueGradient1;
						r2 += redGradient2;
						g2 += greenGradient2;
						b2 += blueGradient2;
						vertexZ2 += depthSlopeY;
					}
					while (--vertexY1 >= 0) {
						rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX1 >> 16, vertexX3 >> 16, r1, g1, b1, r3, g3, b3, vertexZ2, depthSlopeX);
						vertexX1 += edge1Slope;
						vertexX3 += edge3Slope;
						r1 += redGradient1;
						g1 += greenGradient1;
						b1 += blueGradient1;
						r3 += redGradient3;
						g3 += greenGradient3;
						b3 += blueGradient3;
						vertexY2 += DrawingArea.width;
						vertexZ2 += depthSlopeY;
					}
					return;
				}
				vertexY1 -= vertexY3;
				vertexY3 -= vertexY2;
				for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY3 >= 0; vertexY2 += DrawingArea.width) {
					rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX2 >> 16, vertexX1 >> 16, r2, g2, b2, r1, g1, b1, vertexZ2, depthSlopeX);
					vertexX1 += edge1Slope;
					vertexX2 += edge2Slope;
					r1 += redGradient1;
					g1 += greenGradient1;
					b1 += blueGradient1;
					r2 += redGradient2;
					g2 += greenGradient2;
					b2 += blueGradient2;
					vertexZ2 += depthSlopeY;
				}
				while (--vertexY1 >= 0) {
					rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX3 >> 16, vertexX1 >> 16, r3, g3, b3, r1, g1, b1, vertexZ2, depthSlopeX);
					vertexX1 += edge1Slope;
					vertexX3 += edge3Slope;
					r1 += redGradient1;
					g1 += greenGradient1;
					b1 += blueGradient1;
					r3 += redGradient3;
					g3 += greenGradient3;
					b3 += blueGradient3;
					vertexY2 += DrawingArea.width;
					vertexZ2 += depthSlopeY;
				}
				return;
			}
			vertexX3 = vertexX2 <<= 16;
			r3 = r2 <<= 16;
			g3 = g2 <<= 16;
			b3 = b2 <<= 16;
			if (vertexY2 < 0) {
				vertexX3 -= edge1Slope * vertexY2;
				vertexX2 -= edge2Slope * vertexY2;
				r3 -= redGradient1 * vertexY2;
				g3 -= greenGradient1 * vertexY2;
				b3 -= blueGradient1 * vertexY2;
				r2 -= redGradient2 * vertexY2;
				g2 -= greenGradient2 * vertexY2;
				b2 -= blueGradient2 * vertexY2;
				vertexZ2 -= depthSlopeY * vertexY2;
				vertexY2 = 0;
			}
			vertexX1 <<= 16;
			r1 <<= 16;
			g1 <<= 16;
			b1 <<= 16;
			if (vertexY1 < 0) {
				vertexX1 -= edge3Slope * vertexY1;
				r1 -= redGradient3 * vertexY1;
				g1 -= greenGradient3 * vertexY1;
				b1 -= blueGradient3 * vertexY1;
				vertexY1 = 0;
			}
			if (edge1Slope < edge2Slope) {
				vertexY3 -= vertexY1;
				vertexY1 -= vertexY2;
				for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY1 >= 0; vertexY2 += DrawingArea.width) {
					rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX3 >> 16, vertexX2 >> 16, r3, g3, b3, r2, g2, b2, vertexZ2, depthSlopeX);
					vertexX3 += edge1Slope;
					vertexX2 += edge2Slope;
					r3 += redGradient1;
					g3 += greenGradient1;
					b3 += blueGradient1;
					r2 += redGradient2;
					g2 += greenGradient2;
					b2 += blueGradient2;
					vertexZ2 += depthSlopeY;
				}
				while (--vertexY3 >= 0) {
					rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX1 >> 16, vertexX2 >> 16, r1, g1, b1, r2, g2, b2, vertexZ2, depthSlopeX);
					vertexX1 += edge3Slope;
					vertexX2 += edge2Slope;
					r1 += redGradient3;
					g1 += greenGradient3;
					b1 += blueGradient3;
					r2 += redGradient2;
					g2 += greenGradient2;
					b2 += blueGradient2;
					vertexY2 += DrawingArea.width;
					vertexZ2 += depthSlopeY;
				}
				return;
			}
			vertexY3 -= vertexY1;
			vertexY1 -= vertexY2;
			for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY1 >= 0; vertexY2 += DrawingArea.width) {
				rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX2 >> 16, vertexX3 >> 16, r2, g2, b2, r3, g3, b3, vertexZ2, depthSlopeX);
				vertexX3 += edge1Slope;
				vertexX2 += edge2Slope;
				r3 += redGradient1;
				g3 += greenGradient1;
				b3 += blueGradient1;
				r2 += redGradient2;
				g2 += greenGradient2;
				b2 += blueGradient2;
				vertexZ2 += depthSlopeY;
			}
			while (--vertexY3 >= 0) {
				rasterizeScanline(DrawingArea.pixels, vertexY2, vertexX2 >> 16, vertexX1 >> 16, r2, g2, b2, r1, g1, b1, vertexZ2, depthSlopeX);
				vertexX1 += edge3Slope;
				vertexX2 += edge2Slope;
				r1 += redGradient3;
				g1 += greenGradient3;
				b1 += blueGradient3;
				r2 += redGradient2;
				g2 += greenGradient2;
				b2 += blueGradient2;
				vertexY2 += DrawingArea.width;
				vertexZ2 += depthSlopeY;
			}
			return;
		}
		if (vertexY3 >= DrawingArea.bottomY) {
			return;
		}
		if (vertexY1 > DrawingArea.bottomY) {
			vertexY1 = DrawingArea.bottomY;
		}
		if (vertexY2 > DrawingArea.bottomY) {
			vertexY2 = DrawingArea.bottomY;
		}
		vertexZ3 = vertexZ3 - depthSlopeX * vertexX3 + depthSlopeX;
		if (vertexY1 < vertexY2) {
			vertexX2 = vertexX3 <<= 16;
			r2 = r3 <<= 16;
			g2 = g3 <<= 16;
			b2 = b3 <<= 16;
			if (vertexY3 < 0) {
				vertexX2 -= edge2Slope * vertexY3;
				vertexX3 -= edge3Slope * vertexY3;
				r2 -= redGradient2 * vertexY3;
				g2 -= greenGradient2 * vertexY3;
				b2 -= blueGradient2 * vertexY3;
				r3 -= redGradient3 * vertexY3;
				g3 -= greenGradient3 * vertexY3;
				b3 -= blueGradient3 * vertexY3;
				vertexZ3 -= depthSlopeY * vertexY3;
				vertexY3 = 0;
			}
			vertexX1 <<= 16;
			r1 <<= 16;
			g1 <<= 16;
			b1 <<= 16;
			if (vertexY1 < 0) {
				vertexX1 -= edge1Slope * vertexY1;
				r1 -= redGradient1 * vertexY1;
				g1 -= greenGradient1 * vertexY1;
				b1 -= blueGradient1 * vertexY1;
				vertexY1 = 0;
			}
			if (edge2Slope < edge3Slope) {
				vertexY2 -= vertexY1;
				vertexY1 -= vertexY3;
				for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY1 >= 0; vertexY3 += DrawingArea.width) {
					rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX2 >> 16, vertexX3 >> 16, r2, g2, b2, r3, g3, b3, vertexZ3, depthSlopeX);
					vertexX2 += edge2Slope;
					vertexX3 += edge3Slope;
					r2 += redGradient2;
					g2 += greenGradient2;
					b2 += blueGradient2;
					r3 += redGradient3;
					g3 += greenGradient3;
					b3 += blueGradient3;
					vertexZ3 += depthSlopeY;
				}
				while (--vertexY2 >= 0) {
					rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX2 >> 16, vertexX1 >> 16, r2, g2, b2, r1, g1, b1, vertexZ3, depthSlopeX);
					vertexX2 += edge2Slope;
					vertexX1 += edge1Slope;
					r2 += redGradient2;
					g2 += greenGradient2;
					b2 += blueGradient2;
					r1 += redGradient1;
					g1 += greenGradient1;
					b1 += blueGradient1;
					vertexY3 += DrawingArea.width;
					vertexZ3 += depthSlopeY;
				}
				return;
			}
			vertexY2 -= vertexY1;
			vertexY1 -= vertexY3;
			for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY1 >= 0; vertexY3 += DrawingArea.width) {
				rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX3 >> 16, vertexX2 >> 16, r3, g3, b3, r2, g2, b2, vertexZ3, depthSlopeX);
				vertexX2 += edge2Slope;
				vertexX3 += edge3Slope;
				r2 += redGradient2;
				g2 += greenGradient2;
				b2 += blueGradient2;
				r3 += redGradient3;
				g3 += greenGradient3;
				b3 += blueGradient3;
				vertexZ3 += depthSlopeY;
			}
			while (--vertexY2 >= 0) {
				rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX1 >> 16, vertexX2 >> 16, r1, g1, b1, r2, g2, b2, vertexZ3, depthSlopeX);
				vertexX2 += edge2Slope;
				vertexX1 += edge1Slope;
				r2 += redGradient2;
				g2 += greenGradient2;
				b2 += blueGradient2;
				r1 += redGradient1;
				g1 += greenGradient1;
				b1 += blueGradient1;
				vertexZ3 += depthSlopeY;
				vertexY3 += DrawingArea.width;
			}
			return;
		}
		vertexX1 = vertexX3 <<= 16;
		r1 = r3 <<= 16;
		g1 = g3 <<= 16;
		b1 = b3 <<= 16;
		if (vertexY3 < 0) {
			vertexX1 -= edge2Slope * vertexY3;
			vertexX3 -= edge3Slope * vertexY3;
			r1 -= redGradient2 * vertexY3;
			g1 -= greenGradient2 * vertexY3;
			b1 -= blueGradient2 * vertexY3;
			r3 -= redGradient3 * vertexY3;
			g3 -= greenGradient3 * vertexY3;
			b3 -= blueGradient3 * vertexY3;
			vertexZ3 -= depthSlopeY * vertexY3;
			vertexY3 = 0;
		}
		vertexX2 <<= 16;
		r2 <<= 16;
		g2 <<= 16;
		b2 <<= 16;
		if (vertexY2 < 0) {
			vertexX2 -= edge1Slope * vertexY2;
			r2 -= redGradient1 * vertexY2;
			g2 -= greenGradient1 * vertexY2;
			b2 -= blueGradient1 * vertexY2;
			vertexY2 = 0;
		}
		if (edge2Slope < edge3Slope) {
			vertexY1 -= vertexY2;
			vertexY2 -= vertexY3;
			for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY2 >= 0; vertexY3 += DrawingArea.width) {
				rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX1 >> 16, vertexX3 >> 16, r1, g1, b1, r3, g3, b3, vertexZ3, depthSlopeX);
				vertexX1 += edge2Slope;
				vertexX3 += edge3Slope;
				r1 += redGradient2;
				g1 += greenGradient2;
				b1 += blueGradient2;
				r3 += redGradient3;
				g3 += greenGradient3;
				b3 += blueGradient3;
				vertexZ3 += depthSlopeY;
			}
			while (--vertexY1 >= 0) {
				rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX2 >> 16, vertexX3 >> 16, r2, g2, b2, r3, g3, b3, vertexZ3, depthSlopeX);
				vertexX2 += edge1Slope;
				vertexX3 += edge3Slope;
				r2 += redGradient1;
				g2 += greenGradient1;
				b2 += blueGradient1;
				r3 += redGradient3;
				g3 += greenGradient3;
				b3 += blueGradient3;
				vertexZ3 += depthSlopeY;
				vertexY3 += DrawingArea.width;
			}
			return;
		}
		vertexY1 -= vertexY2;
		vertexY2 -= vertexY3;
		for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY2 >= 0; vertexY3 += DrawingArea.width) {
			rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX3 >> 16, vertexX1 >> 16, r3, g3, b3, r1, g1, b1, vertexZ3, depthSlopeX);
			vertexX1 += edge2Slope;
			vertexX3 += edge3Slope;
			r1 += redGradient2;
			g1 += greenGradient2;
			b1 += blueGradient2;
			r3 += redGradient3;
			g3 += greenGradient3;
			b3 += blueGradient3;
			vertexZ3 += depthSlopeY;
		}
		while (--vertexY1 >= 0) {
			rasterizeScanline(DrawingArea.pixels, vertexY3, vertexX3 >> 16, vertexX2 >> 16, r3, g3, b3, r2, g2, b2, vertexZ3, depthSlopeX);
			vertexX2 += edge1Slope;
			vertexX3 += edge3Slope;
			r2 += redGradient1;
			g2 += greenGradient1;
			b2 += blueGradient1;
			r3 += redGradient3;
			g3 += greenGradient3;
			b3 += blueGradient3;
			vertexY3 += DrawingArea.width;
			vertexZ3 += depthSlopeY;
		}
	}

	// COMPLETE METHOD TO REPLACE: rasterizeScanline
	public static void rasterizeScanline(int[] pixelBuffer, int offset, int vertexX1, int vertexX2, int r1, int g1, int b1, int r2, int g2, int b2,
										 float depth, float depthSlopeX) {
		int n = vertexX2 - vertexX1;
		if (n <= 0) {
			return;
		}

		r2 = (r2 - r1) / n;
		g2 = (g2 - g1) / n;
		b2 = (b2 - b1) / n;
		if (enableClipping) {
			if (vertexX2 > DrawingArea.centerX) {
				n -= vertexX2 - DrawingArea.centerX;
				vertexX2 = DrawingArea.centerX;
			}
			if (vertexX1 < 0) {
				n = vertexX2;
				r1 -= vertexX1 * r2;
				g1 -= vertexX1 * g2;
				b1 -= vertexX1 * b2;
				vertexX1 = 0;
			}
		}
					/* // CAREFUL OPTIMIZING THIS BLOCK SCOPE, CHANGES HERE COULD BREAK ITEM RENDERING. HOWEVER IT DOES SOLVE OBJECTS AND ROOF DISTANCE DEPTH. BUT BREAKS ITEM DEPTH!
			See an optimised version here:
			* 			if (alphaBlendValue == 0) {
				while (--n >= 0) {
					if (enableDepthBuffer && depth >= DrawingArea.depthBuffer[offset]) {
						depth += depthSlopeX;
						r1 += r2;
						g1 += g2;
						b1 += b2;
						offset++;
						continue;
					}
					pixelBuffer[offset] = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
					DrawingArea.depthBuffer[offset] = depth;
					depth += depthSlopeX;

					This could be a replacement for the code below, but remember, it fixes roof culling, it breaks item culling.
			*/
		if (vertexX1 < vertexX2) {
			offset += vertexX1;
			depth += depthSlopeX * (float) vertexX1;
			if (alphaBlendValue == 0) {
				while (--n >= 0) {
					pixelBuffer[offset] = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
					DrawingArea.depthBuffer[offset] = depth;
					depth += depthSlopeX;
					r1 += r2;
					g1 += g2;
					b1 += b2;
					offset++;
				}
			} else {
				final int a1 = alphaBlendValue;
				final int a2 = 256 - alphaBlendValue;
				int rgb;
				int dst;
				while (--n >= 0) {
					if (enableDepthBuffer && depth >= DrawingArea.depthBuffer[offset]) {
						depth += depthSlopeX;
						r1 += r2;
						g1 += g2;
						b1 += b2;
						offset++;
						continue;
					}
					rgb = (r1 & 0xff0000) | (g1 >> 8 & 0xff00) | (b1 >> 16 & 0xff);
					rgb = ((rgb & 0xff00ff) * a2 >> 8 & 0xff00ff) + ((rgb & 0xff00) * a2 >> 8 & 0xff00);
					dst = pixelBuffer[offset];
					pixelBuffer[offset] = rgb + ((dst & 0xff00ff) * a1 >> 8 & 0xff00ff)
						+ ((dst & 0xff00) * a1 >> 8 & 0xff00);
					DrawingArea.depthBuffer[offset] = depth;
					depth += depthSlopeX;
					r1 += r2;
					g1 += g2;
					b1 += b2;
					offset++;
				}
			}
		}
	}

	public static void renderSolidTriangle(int vertexY1, int vertexY2, int vertexY3, int vertexX1, int vertexX2, int vertexX3, int shade1, float vertexZ1, float vertexZ2, float vertexZ3) {
		if (vertexZ1 < 0.0F && vertexZ2 < 0.0F && vertexZ3 < 0.0F) {
			return;
		}
		if (vertexZ1 > 10000.0F && vertexZ2 > 10000.0F && vertexZ3 > 10000.0F) {
			return;
		}
		int edge1Slope = 0;
		if (vertexY2 != vertexY1) {
			edge1Slope = (vertexX2 - vertexX1 << 16) / (vertexY2 - vertexY1);
		}
		int edge2Slope = 0;
		if (vertexY3 != vertexY2) {
			edge2Slope = (vertexX3 - vertexX2 << 16) / (vertexY3 - vertexY2);
		}
		int edge3Slope = 0;
		if (vertexY3 != vertexY1) {
			edge3Slope = (vertexX1 - vertexX3 << 16) / (vertexY1 - vertexY3);
		}
		float edgeAX = vertexX2 - vertexX1;
		float edgeAY = vertexY2 - vertexY1;
		float edgeBX = vertexX3 - vertexX1;
		float edgeBY = vertexY3 - vertexY1;
		float edgeAZ = vertexZ2 - vertexZ1;
		float edgeBZ = vertexZ3 - vertexZ1;

		float areaInverse = edgeAX * edgeBY - edgeBX * edgeAY;
		float depthSlopeX = (edgeAZ * edgeBY - edgeBZ * edgeAY) / areaInverse;
		float depthSlopeY = (edgeBZ * edgeAX - edgeAZ * edgeBX) / areaInverse;
		if (vertexY1 <= vertexY2 && vertexY1 <= vertexY3) {
			if (vertexY1 >= DrawingArea.bottomY)
				return;
			if (vertexY2 > DrawingArea.bottomY)
				vertexY2 = DrawingArea.bottomY;
			if (vertexY3 > DrawingArea.bottomY)
				vertexY3 = DrawingArea.bottomY;
			vertexZ1 = vertexZ1 - depthSlopeX * vertexX1 + depthSlopeX;
			if (vertexY2 < vertexY3) {
				vertexX3 = vertexX1 <<= 16;
				if (vertexY1 < 0) {
					vertexX3 -= edge3Slope * vertexY1;
					vertexX1 -= edge1Slope * vertexY1;
					vertexZ1 -= depthSlopeY * vertexY1;
					vertexY1 = 0;
				}
				vertexX2 <<= 16;
				if (vertexY2 < 0) {
					vertexX2 -= edge2Slope * vertexY2;
					vertexY2 = 0;
				}
				if (vertexY1 != vertexY2 && edge3Slope < edge1Slope || vertexY1 == vertexY2 && edge3Slope > edge2Slope) {
					vertexY3 -= vertexY2;
					vertexY2 -= vertexY1;
					for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY2 >= 0; vertexY1 += DrawingArea.width) {
						rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX3 >> 16, vertexX1 >> 16, vertexZ1, depthSlopeX);
						vertexX3 += edge3Slope;
						vertexX1 += edge1Slope;
						vertexZ1 += depthSlopeY;
					}

					while (--vertexY3 >= 0) {
						rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX3 >> 16, vertexX2 >> 16, vertexZ1, depthSlopeX);
						vertexX3 += edge3Slope;
						vertexX2 += edge2Slope;
						vertexY1 += DrawingArea.width;
						vertexZ1 += depthSlopeY;
					}
					return;
				}
				vertexY3 -= vertexY2;
				vertexY2 -= vertexY1;
				for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY2 >= 0; vertexY1 += DrawingArea.width) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX1 >> 16, vertexX3 >> 16, vertexZ1, depthSlopeX);
					vertexX3 += edge3Slope;
					vertexX1 += edge1Slope;
					vertexZ1 += depthSlopeY;
				}

				while (--vertexY3 >= 0) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX2 >> 16, vertexX3 >> 16, vertexZ1, depthSlopeX);
					vertexX3 += edge3Slope;
					vertexX2 += edge2Slope;
					vertexY1 += DrawingArea.width;
					vertexZ1 += depthSlopeY;
				}
				return;
			}
			vertexX2 = vertexX1 <<= 16;
			if (vertexY1 < 0) {
				vertexX2 -= edge3Slope * vertexY1;
				vertexX1 -= edge1Slope * vertexY1;
				vertexZ1 -= depthSlopeY * vertexY1;
				vertexY1 = 0;

			}
			vertexX3 <<= 16;
			if (vertexY3 < 0) {
				vertexX3 -= edge2Slope * vertexY3;
				vertexY3 = 0;
			}
			if (vertexY1 != vertexY3 && edge3Slope < edge1Slope || vertexY1 == vertexY3 && edge2Slope > edge1Slope) {
				vertexY2 -= vertexY3;
				vertexY3 -= vertexY1;
				for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY3 >= 0; vertexY1 += DrawingArea.width) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX2 >> 16, vertexX1 >> 16, vertexZ1, depthSlopeX);
					vertexZ1 += depthSlopeY;
					vertexX2 += edge3Slope;
					vertexX1 += edge1Slope;
				}

				while (--vertexY2 >= 0) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX3 >> 16, vertexX1 >> 16, vertexZ1, depthSlopeX);
					vertexZ1 += depthSlopeY;
					vertexX3 += edge2Slope;
					vertexX1 += edge1Slope;
					vertexY1 += DrawingArea.width;
				}
				return;
			}
			vertexY2 -= vertexY3;
			vertexY3 -= vertexY1;
			for (vertexY1 = scanlineOffsets[vertexY1]; --vertexY3 >= 0; vertexY1 += DrawingArea.width) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX1 >> 16, vertexX2 >> 16, vertexZ1, depthSlopeX);
				vertexZ1 += depthSlopeY;
				vertexX2 += edge3Slope;
				vertexX1 += edge1Slope;
			}

			while (--vertexY2 >= 0) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY1, shade1, vertexX1 >> 16, vertexX3 >> 16, vertexZ1, depthSlopeX);
				vertexZ1 += depthSlopeY;
				vertexX3 += edge2Slope;
				vertexX1 += edge1Slope;
				vertexY1 += DrawingArea.width;
			}
			return;
		}
		if (vertexY2 <= vertexY3) {
			if (vertexY2 >= DrawingArea.bottomY)
				return;
			if (vertexY3 > DrawingArea.bottomY)
				vertexY3 = DrawingArea.bottomY;
			if (vertexY1 > DrawingArea.bottomY)
				vertexY1 = DrawingArea.bottomY;
			vertexZ2 = vertexZ2 - depthSlopeX * vertexX2 + depthSlopeX;
			if (vertexY3 < vertexY1) {
				vertexX1 = vertexX2 <<= 16;
				if (vertexY2 < 0) {
					vertexX1 -= edge1Slope * vertexY2;
					vertexX2 -= edge2Slope * vertexY2;
					vertexZ2 -= depthSlopeY * vertexY2;
					vertexY2 = 0;
				}
				vertexX3 <<= 16;
				if (vertexY3 < 0) {
					vertexX3 -= edge3Slope * vertexY3;
					vertexY3 = 0;
				}
				if (vertexY2 != vertexY3 && edge1Slope < edge2Slope || vertexY2 == vertexY3 && edge1Slope > edge3Slope) {
					vertexY1 -= vertexY3;
					vertexY3 -= vertexY2;
					for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY3 >= 0; vertexY2 += DrawingArea.width) {
						rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX1 >> 16, vertexX2 >> 16, vertexZ2, depthSlopeX);
						vertexZ2 += depthSlopeY;
						vertexX1 += edge1Slope;
						vertexX2 += edge2Slope;
					}

					while (--vertexY1 >= 0) {
						rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX1 >> 16, vertexX3 >> 16, vertexZ2, depthSlopeX);
						vertexZ2 += depthSlopeY;
						vertexX1 += edge1Slope;
						vertexX3 += edge3Slope;
						vertexY2 += DrawingArea.width;
					}
					return;
				}
				vertexY1 -= vertexY3;
				vertexY3 -= vertexY2;
				for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY3 >= 0; vertexY2 += DrawingArea.width) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX2 >> 16, vertexX1 >> 16, vertexZ2, depthSlopeX);
					vertexZ2 += depthSlopeY;
					vertexX1 += edge1Slope;
					vertexX2 += edge2Slope;
				}

				while (--vertexY1 >= 0) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX3 >> 16, vertexX1 >> 16, vertexZ2, depthSlopeX);
					vertexZ2 += depthSlopeY;
					vertexX1 += edge1Slope;
					vertexX3 += edge3Slope;
					vertexY2 += DrawingArea.width;
				}
				return;
			}
			vertexX3 = vertexX2 <<= 16;
			if (vertexY2 < 0) {
				vertexX3 -= edge1Slope * vertexY2;
				vertexX2 -= edge2Slope * vertexY2;
				vertexZ2 -= depthSlopeY * vertexY2;
				vertexY2 = 0;
			}
			vertexX1 <<= 16;
			if (vertexY1 < 0) {
				vertexX1 -= edge3Slope * vertexY1;
				vertexY1 = 0;
			}
			if (edge1Slope < edge2Slope) {
				vertexY3 -= vertexY1;
				vertexY1 -= vertexY2;
				for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY1 >= 0; vertexY2 += DrawingArea.width) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX3 >> 16, vertexX2 >> 16, vertexZ2, depthSlopeX);
					vertexZ2 += depthSlopeY;
					vertexX3 += edge1Slope;
					vertexX2 += edge2Slope;
				}

				while (--vertexY3 >= 0) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX1 >> 16, vertexX2 >> 16, vertexZ2, depthSlopeX);
					vertexZ2 += depthSlopeY;
					vertexX1 += edge3Slope;
					vertexX2 += edge2Slope;
					vertexY2 += DrawingArea.width;
				}
				return;
			}
			vertexY3 -= vertexY1;
			vertexY1 -= vertexY2;
			for (vertexY2 = scanlineOffsets[vertexY2]; --vertexY1 >= 0; vertexY2 += DrawingArea.width) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX2 >> 16, vertexX3 >> 16, vertexZ2, depthSlopeX);
				vertexZ2 += depthSlopeY;
				vertexX3 += edge1Slope;
				vertexX2 += edge2Slope;
			}

			while (--vertexY3 >= 0) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY2, shade1, vertexX2 >> 16, vertexX1 >> 16, vertexZ2, depthSlopeX);
				vertexZ2 += depthSlopeY;
				vertexX1 += edge3Slope;
				vertexX2 += edge2Slope;
				vertexY2 += DrawingArea.width;
			}
			return;
		}
		if (vertexY3 >= DrawingArea.bottomY)
			return;
		if (vertexY1 > DrawingArea.bottomY)
			vertexY1 = DrawingArea.bottomY;
		if (vertexY2 > DrawingArea.bottomY)
			vertexY2 = DrawingArea.bottomY;
		vertexZ3 = vertexZ3 - depthSlopeX * vertexX3 + depthSlopeX;
		if (vertexY1 < vertexY2) {
			vertexX2 = vertexX3 <<= 16;
			if (vertexY3 < 0) {
				vertexX2 -= edge2Slope * vertexY3;
				vertexX3 -= edge3Slope * vertexY3;
				vertexZ3 -= depthSlopeY * vertexY3;
				vertexY3 = 0;
			}
			vertexX1 <<= 16;
			if (vertexY1 < 0) {
				vertexX1 -= edge1Slope * vertexY1;
				vertexY1 = 0;
			}
			if (edge2Slope < edge3Slope) {
				vertexY2 -= vertexY1;
				vertexY1 -= vertexY3;
				for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY1 >= 0; vertexY3 += DrawingArea.width) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX2 >> 16, vertexX3 >> 16, vertexZ3, depthSlopeX);
					vertexZ3 += depthSlopeY;
					vertexX2 += edge2Slope;
					vertexX3 += edge3Slope;
				}

				while (--vertexY2 >= 0) {
					rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX2 >> 16, vertexX1 >> 16, vertexZ3, depthSlopeX);
					vertexZ3 += depthSlopeY;
					vertexX2 += edge2Slope;
					vertexX1 += edge1Slope;
					vertexY3 += DrawingArea.width;
				}
				return;
			}
			vertexY2 -= vertexY1;
			vertexY1 -= vertexY3;
			for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY1 >= 0; vertexY3 += DrawingArea.width) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX3 >> 16, vertexX2 >> 16, vertexZ3, depthSlopeX);
				vertexZ3 += depthSlopeY;
				vertexX2 += edge2Slope;
				vertexX3 += edge3Slope;
			}

			while (--vertexY2 >= 0) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX1 >> 16, vertexX2 >> 16, vertexZ3, depthSlopeX);
				vertexZ3 += depthSlopeY;
				vertexX2 += edge2Slope;
				vertexX1 += edge1Slope;
				vertexY3 += DrawingArea.width;
			}
			return;
		}
		vertexX1 = vertexX3 <<= 16;
		if (vertexY3 < 0) {
			vertexX1 -= edge2Slope * vertexY3;
			vertexX3 -= edge3Slope * vertexY3;
			vertexZ3 -= depthSlopeY * vertexY3;
			vertexY3 = 0;
		}
		vertexX2 <<= 16;
		if (vertexY2 < 0) {
			vertexX2 -= edge1Slope * vertexY2;
			vertexY2 = 0;
		}
		if (edge2Slope < edge3Slope) {
			vertexY1 -= vertexY2;
			vertexY2 -= vertexY3;
			for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY2 >= 0; vertexY3 += DrawingArea.width) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX1 >> 16, vertexX3 >> 16, vertexZ3, depthSlopeX);
				vertexZ3 += depthSlopeY;
				vertexX1 += edge2Slope;
				vertexX3 += edge3Slope;
			}

			while (--vertexY1 >= 0) {
				rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX2 >> 16, vertexX3 >> 16, vertexZ3, depthSlopeX);
				vertexZ3 += depthSlopeY;
				vertexX2 += edge1Slope;
				vertexX3 += edge3Slope;
				vertexY3 += DrawingArea.width;
			}
			return;
		}
		vertexY1 -= vertexY2;
		vertexY2 -= vertexY3;
		for (vertexY3 = scanlineOffsets[vertexY3]; --vertexY2 >= 0; vertexY3 += DrawingArea.width) {
			rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX3 >> 16, vertexX1 >> 16, vertexZ3, depthSlopeX);
			vertexZ3 += depthSlopeY;
			vertexX1 += edge2Slope;
			vertexX3 += edge3Slope;
		}

		while (--vertexY1 >= 0) {
			rasterizeSolidScanline(DrawingArea.pixels, vertexY3, shade1, vertexX3 >> 16, vertexX2 >> 16, vertexZ3, depthSlopeX);
			vertexZ3 += depthSlopeY;
			vertexX2 += edge1Slope;
			vertexX3 += edge3Slope;
			vertexY3 += DrawingArea.width;
		}
	}

	// COMPLETE METHOD TO REPLACE: rasterizeSolidScanline
	private static void rasterizeSolidScanline(int[] pixelBuffer, int offset, int color, int start_x, int end_x, float depth,
											   float depthSlopeX) {
		int rgb;
		if (enableClipping) {
			if (end_x > DrawingArea.centerX) {
				end_x = DrawingArea.centerX;
			}
			if (start_x < 0) {
				start_x = 0;
			}
		}
		if (start_x >= end_x) {
			return;
		}
		offset += start_x;
		rgb = end_x - start_x >> 2;
		depth += depthSlopeX * (float) start_x;
		if (alphaBlendValue == 0) {
			while (--rgb >= 0) {
				for (int index = 0; index < 4; index++) {
					pixelBuffer[offset] = color;
					DrawingArea.depthBuffer[offset] = depth;
					offset++;
					depth += depthSlopeX;
				}
			}
			for (rgb = end_x - start_x & 3; --rgb >= 0;) {
				pixelBuffer[offset] = color;
				DrawingArea.depthBuffer[offset] = depth;
				offset++;
				depth += depthSlopeX;
			}
			return;
		}
		int dest_alpha = alphaBlendValue;
		int src_alpha = 256 - alphaBlendValue;
		color = ((color & 0xff00ff) * src_alpha >> 8 & 0xff00ff) + ((color & 0xff00) * src_alpha >> 8 & 0xff00);
		while (--rgb >= 0) {
			for (int index = 0; index < 4; index++) {
				if (enableDepthBuffer && depth >= DrawingArea.depthBuffer[offset]) {
					offset++;
					depth += depthSlopeX;
					continue;
				}
				pixelBuffer[offset] = color + ((pixelBuffer[offset] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff)
					+ ((pixelBuffer[offset] & 0xff00) * dest_alpha >> 8 & 0xff00);
				DrawingArea.depthBuffer[offset] = depth;
				offset++;
				depth += depthSlopeX;
			}
		}
		for (rgb = end_x - start_x & 3; --rgb >= 0;) {
			if (enableDepthBuffer && depth >= DrawingArea.depthBuffer[offset]) {
				offset++;
				depth += depthSlopeX;
				continue;
			}
			pixelBuffer[offset] = color + ((pixelBuffer[offset] & 0xff00ff) * dest_alpha >> 8 & 0xff00ff)
				+ ((pixelBuffer[offset] & 0xff00) * dest_alpha >> 8 & 0xff00);
			DrawingArea.depthBuffer[offset] = depth;
			offset++;
			depth += depthSlopeX;
		}
	}

	public static void renderTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c, int shade2, int l2, int l3, int textureU1,
												 int textureU2, int textureU3, int textureV1, int textureV2, int textureV3, int textureW1, int textureW2, int textureW3, int textureId, float vertexZ1, float vertexZ2,
												 float vertexZ3) {
		try {
			if (!enableDepthBuffer) {
				renderTexturedTriangleNoDepth(y_a, y_b, y_c, x_a, x_b, x_c, shade2, l2, l3, textureU1, textureU2, textureU3, textureV1, textureV2, textureV3, textureW1, textureW2, textureW3, textureId);
				return;
			}
			if (vertexZ1 < 0.0F && vertexZ2 < 0.0F && vertexZ3 < 0.0F) {
				return;
			}
			if (vertexZ1 > 10000.0F && vertexZ2 > 10000.0F && vertexZ3 > 10000.0F) {
				return;
			}

// Skip triangles completely outside viewport
			int minX = Math.min(Math.min(x_a, x_b), x_c);
			int maxX = Math.max(Math.max(x_a, x_b), x_c);
			int minY = Math.min(Math.min(y_a, y_b), y_c);
			int maxY = Math.max(Math.max(y_a, y_b), y_c);

			if (maxX < 0 || minX >= DrawingArea.width || maxY < 0 || minY >= DrawingArea.height) {
				return; // Triangle completely outside screen
			}

			shade2 = 0x7f - shade2 << 1;
			l2 = 0x7f - l2 << 1;
			l3 = 0x7f - l3 << 1;
			setMipmapLevel(y_a, y_b, y_c, x_a, x_b, x_c, textureId);
			int[] texels = getMipmapTexels(textureId)[currentMipmapLevel];
			isOpaque = !textureHasTransparency[textureId];
			textureU2 = textureU1 - textureU2;
			textureV2 = textureV1 - textureV2;
			textureW2 = textureW1 - textureW2;
			textureU3 -= textureU1;
			textureV3 -= textureV1;
			textureW3 -= textureW1;
			int nextTextureV = textureU3 * textureV1 - textureV3 * textureU1 << (WorldController.viewDistance == 9 ? 14 : 15);
			int i5 = textureV3 * textureW1 - textureW3 * textureV1 << 8;
			int j5 = textureW3 * textureU1 - textureU3 * textureW1 << 5;
			int k5 = textureU2 * textureV1 - textureV2 * textureU1 << (WorldController.viewDistance == 9 ? 14 : 15);
			int l5 = textureV2 * textureW1 - textureW2 * textureV1 << 8;
			int i6 = textureW2 * textureU1 - textureU2 * textureW1 << 5;
			int j6 = textureV2 * textureU3 - textureU2 * textureV3 << (WorldController.viewDistance == 9 ? 14 : 15);
			int k6 = textureW2 * textureV3 - textureV2 * textureW3 << 8;
			int screenOffset = textureU2 * textureW3 - textureW2 * textureU3 << 5;
			int i7 = 0;
			int textureUStep = 0;
			if (y_b != y_a) {
				i7 = (x_b - x_a << 16) / (y_b - y_a);
				textureUStep = (l2 - shade2 << 16) / (y_b - y_a);
			}
			int k7 = 0;
			int textureVStep = 0;
			if (y_c != y_b) {
				k7 = (x_c - x_b << 16) / (y_c - y_b);
				textureVStep = (l3 - l2 << 16) / (y_c - y_b);
			}
			int i8 = 0;
			int j8 = 0;
			if (y_c != y_a) {
				i8 = (x_a - x_c << 16) / (y_a - y_c);
				j8 = (shade2 - l3 << 16) / (y_a - y_c);
			}
			float edgeAX = x_b - x_a;
			float edgeAY = y_b - y_a;
			float edgeBX = x_c - x_a;
			float edgeBY = y_c - y_a;
			float edgeAZ = vertexZ2 - vertexZ1;
			float edgeBZ = vertexZ3 - vertexZ1;

			float areaInverse = edgeAX * edgeBY - edgeBX * edgeAY;
			float depthSlopeX = (edgeAZ * edgeBY - edgeBZ * edgeAY) / areaInverse;
			float depthSlopeY = (edgeBZ * edgeAX - edgeAZ * edgeBX) / areaInverse;
			if (y_a <= y_b && y_a <= y_c) {
				if (y_a >= DrawingArea.bottomY)
					return;
				if (y_b > DrawingArea.bottomY)
					y_b = DrawingArea.bottomY;
				if (y_c > DrawingArea.bottomY)
					y_c = DrawingArea.bottomY;
				vertexZ1 = vertexZ1 - depthSlopeX * x_a + depthSlopeX;
				if (y_b < y_c) {
					x_c = x_a <<= 16;
					l3 = shade2 <<= 16;
					if (y_a < 0) {
						x_c -= i8 * y_a;
						x_a -= i7 * y_a;
						vertexZ1 -= depthSlopeY * y_a;
						l3 -= j8 * y_a;
						shade2 -= textureUStep * y_a;
						y_a = 0;
					}
					x_b <<= 16;
					l2 <<= 16;
					if (y_b < 0) {
						x_b -= k7 * y_b;
						l2 -= textureVStep * y_b;
						y_b = 0;
					}
					int k8 = y_a - viewportCenterY;
					nextTextureV += j5 * k8;
					k5 += i6 * k8;
					j6 += screenOffset * k8;
					if (y_a != y_b && i8 < i7 || y_a == y_b && i8 > k7) {
						y_c -= y_b;
						y_b -= y_a;
						y_a = scanlineOffsets[y_a];
						while (--y_b >= 0) {
							rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_c >> 16, x_a >> 16, l3, shade2, nextTextureV, k5, j6, i5, l5, k6,
									vertexZ1, depthSlopeX);
							x_c += i8;
							x_a += i7;
							vertexZ1 += depthSlopeY;
							l3 += j8;
							shade2 += textureUStep;
							y_a += DrawingArea.width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						while (--y_c >= 0) {
							rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_c >> 16, x_b >> 16, l3, l2, nextTextureV, k5, j6, i5, l5, k6,
									vertexZ1, depthSlopeX);
							x_c += i8;
							x_b += k7;
							vertexZ1 += depthSlopeY;
							l3 += j8;
							l2 += textureVStep;
							y_a += DrawingArea.width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						return;
					}
					y_c -= y_b;
					y_b -= y_a;
					y_a = scanlineOffsets[y_a];
					while (--y_b >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_a >> 16, x_c >> 16, shade2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ1,
								depthSlopeX);
						x_c += i8;
						x_a += i7;
						vertexZ1 += depthSlopeY;
						l3 += j8;
						shade2 += textureUStep;
						y_a += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y_c >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_b >> 16, x_c >> 16, l2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ1,
								depthSlopeX);
						x_c += i8;
						x_b += k7;
						vertexZ1 += depthSlopeY;
						l3 += j8;
						l2 += textureVStep;
						y_a += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				x_b = x_a <<= 16;
				l2 = shade2 <<= 16;
				if (y_a < 0) {
					x_b -= i8 * y_a;
					x_a -= i7 * y_a;
					vertexZ1 -= depthSlopeY * y_a;
					l2 -= j8 * y_a;
					shade2 -= textureUStep * y_a;
					y_a = 0;
				}
				x_c <<= 16;
				l3 <<= 16;
				if (y_c < 0) {
					x_c -= k7 * y_c;
					l3 -= textureVStep * y_c;
					y_c = 0;
				}
				int l8 = y_a - viewportCenterY;
				nextTextureV += j5 * l8;
				k5 += i6 * l8;
				j6 += screenOffset * l8;
				if (y_a != y_c && i8 < i7 || y_a == y_c && k7 > i7) {
					y_b -= y_c;
					y_c -= y_a;
					y_a = scanlineOffsets[y_a];
					while (--y_c >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_b >> 16, x_a >> 16, l2, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ1,
								depthSlopeX);
						x_b += i8;
						x_a += i7;
						l2 += j8;
						shade2 += textureUStep;
						vertexZ1 += depthSlopeY;
						y_a += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y_b >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_c >> 16, x_a >> 16, l3, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ1,
								depthSlopeX);
						x_c += k7;
						x_a += i7;
						l3 += textureVStep;
						shade2 += textureUStep;
						vertexZ1 += depthSlopeY;
						y_a += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				y_b -= y_c;
				y_c -= y_a;
				y_a = scanlineOffsets[y_a];
				while (--y_c >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_a >> 16, x_b >> 16, shade2, l2, nextTextureV, k5, j6, i5, l5, k6, vertexZ1,
							depthSlopeX);
					x_b += i8;
					x_a += i7;
					l2 += j8;
					shade2 += textureUStep;
					vertexZ1 += depthSlopeY;
					y_a += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--y_b >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_a, x_a >> 16, x_c >> 16, shade2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ1,
							depthSlopeX);
					x_c += k7;
					x_a += i7;
					l3 += textureVStep;
					shade2 += textureUStep;
					vertexZ1 += depthSlopeY;
					y_a += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			if (y_b <= y_c) {
				if (y_b >= DrawingArea.bottomY)
					return;
				if (y_c > DrawingArea.bottomY)
					y_c = DrawingArea.bottomY;
				if (y_a > DrawingArea.bottomY)
					y_a = DrawingArea.bottomY;
				vertexZ2 = vertexZ2 - depthSlopeX * x_b + depthSlopeX;
				if (y_c < y_a) {
					x_a = x_b <<= 16;
					shade2 = l2 <<= 16;
					if (y_b < 0) {
						x_a -= i7 * y_b;
						x_b -= k7 * y_b;
						vertexZ2 -= depthSlopeY * y_b;
						shade2 -= textureUStep * y_b;
						l2 -= textureVStep * y_b;
						y_b = 0;
					}
					x_c <<= 16;
					l3 <<= 16;
					if (y_c < 0) {
						x_c -= i8 * y_c;
						l3 -= j8 * y_c;
						y_c = 0;
					}
					int i9 = y_b - viewportCenterY;
					nextTextureV += j5 * i9;
					k5 += i6 * i9;
					j6 += screenOffset * i9;
					if (y_b != y_c && i7 < k7 || y_b == y_c && i7 > i8) {
						y_a -= y_c;
						y_c -= y_b;
						y_b = scanlineOffsets[y_b];
						while (--y_c >= 0) {
							rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_a >> 16, x_b >> 16, shade2, l2, nextTextureV, k5, j6, i5, l5, k6,
									vertexZ2, depthSlopeX);
							x_a += i7;
							x_b += k7;
							shade2 += textureUStep;
							l2 += textureVStep;
							vertexZ2 += depthSlopeY;
							y_b += DrawingArea.width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						while (--y_a >= 0) {
							rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_a >> 16, x_c >> 16, shade2, l3, nextTextureV, k5, j6, i5, l5, k6,
									vertexZ2, depthSlopeX);
							x_a += i7;
							x_c += i8;
							shade2 += textureUStep;
							l3 += j8;
							vertexZ2 += depthSlopeY;
							y_b += DrawingArea.width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						return;
					}
					y_a -= y_c;
					y_c -= y_b;
					y_b = scanlineOffsets[y_b];
					while (--y_c >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_b >> 16, x_a >> 16, l2, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ2,
								depthSlopeX);
						x_a += i7;
						x_b += k7;
						shade2 += textureUStep;
						l2 += textureVStep;
						vertexZ2 += depthSlopeY;
						y_b += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y_a >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_c >> 16, x_a >> 16, l3, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ2,
								depthSlopeX);
						x_a += i7;
						x_c += i8;
						shade2 += textureUStep;
						l3 += j8;
						vertexZ2 += depthSlopeY;
						y_b += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				x_c = x_b <<= 16;
				l3 = l2 <<= 16;
				if (y_b < 0) {
					x_c -= i7 * y_b;
					x_b -= k7 * y_b;
					vertexZ2 -= depthSlopeY * y_b;
					l3 -= textureUStep * y_b;
					l2 -= textureVStep * y_b;
					y_b = 0;
				}
				x_a <<= 16;
				shade2 <<= 16;
				if (y_a < 0) {
					x_a -= i8 * y_a;
					shade2 -= j8 * y_a;
					y_a = 0;
				}
				int j9 = y_b - viewportCenterY;
				nextTextureV += j5 * j9;
				k5 += i6 * j9;
				j6 += screenOffset * j9;
				if (i7 < k7) {
					y_c -= y_a;
					y_a -= y_b;
					y_b = scanlineOffsets[y_b];
					while (--y_a >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_c >> 16, x_b >> 16, l3, l2, nextTextureV, k5, j6, i5, l5, k6, vertexZ2,
								depthSlopeX);
						x_c += i7;
						x_b += k7;
						l3 += textureUStep;
						l2 += textureVStep;
						vertexZ2 += depthSlopeY;
						y_b += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y_c >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_a >> 16, x_b >> 16, shade2, l2, nextTextureV, k5, j6, i5, l5, k6, vertexZ2,
								depthSlopeX);
						x_a += i8;
						x_b += k7;
						shade2 += j8;
						l2 += textureVStep;
						vertexZ2 += depthSlopeY;
						y_b += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				y_c -= y_a;
				y_a -= y_b;
				y_b = scanlineOffsets[y_b];
				while (--y_a >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_b >> 16, x_c >> 16, l2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ2,
							depthSlopeX);
					x_c += i7;
					x_b += k7;
					l3 += textureUStep;
					l2 += textureVStep;
					vertexZ2 += depthSlopeY;
					y_b += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--y_c >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_b, x_b >> 16, x_a >> 16, l2, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ2,
							depthSlopeX);
					x_a += i8;
					x_b += k7;
					shade2 += j8;
					l2 += textureVStep;
					vertexZ2 += depthSlopeY;
					y_b += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			if (y_c >= DrawingArea.bottomY)
				return;
			if (y_a > DrawingArea.bottomY)
				y_a = DrawingArea.bottomY;
			if (y_b > DrawingArea.bottomY)
				y_b = DrawingArea.bottomY;
			vertexZ3 = vertexZ3 - depthSlopeX * x_c + depthSlopeX;
			if (y_a < y_b) {
				x_b = x_c <<= 16;
				l2 = l3 <<= 16;
				if (y_c < 0) {
					x_b -= k7 * y_c;
					x_c -= i8 * y_c;
					vertexZ3 -= depthSlopeY * y_c;
					l2 -= textureVStep * y_c;
					l3 -= j8 * y_c;
					y_c = 0;
				}
				x_a <<= 16;
				shade2 <<= 16;
				if (y_a < 0) {
					x_a -= i7 * y_a;
					shade2 -= textureUStep * y_a;
					y_a = 0;
				}
				int k9 = y_c - viewportCenterY;
				nextTextureV += j5 * k9;
				k5 += i6 * k9;
				j6 += screenOffset * k9;
				if (k7 < i8) {
					y_b -= y_a;
					y_a -= y_c;
					y_c = scanlineOffsets[y_c];
					while (--y_a >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_b >> 16, x_c >> 16, l2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
								depthSlopeX);
						x_b += k7;
						x_c += i8;
						l2 += textureVStep;
						l3 += j8;
						vertexZ3 += depthSlopeY;
						y_c += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y_b >= 0) {
						rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_b >> 16, x_a >> 16, l2, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
								depthSlopeX);
						x_b += k7;
						x_a += i7;
						l2 += textureVStep;
						shade2 += textureUStep;
						vertexZ3 += depthSlopeY;
						y_c += DrawingArea.width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				y_b -= y_a;
				y_a -= y_c;
				y_c = scanlineOffsets[y_c];
				while (--y_a >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_c >> 16, x_b >> 16, l3, l2, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
							depthSlopeX);
					x_b += k7;
					x_c += i8;
					l2 += textureVStep;
					l3 += j8;
					vertexZ3 += depthSlopeY;
					y_c += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--y_b >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_a >> 16, x_b >> 16, shade2, l2, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
							depthSlopeX);
					x_b += k7;
					x_a += i7;
					l2 += textureVStep;
					shade2 += textureUStep;
					vertexZ3 += depthSlopeY;
					y_c += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			x_a = x_c <<= 16;
			shade2 = l3 <<= 16;
			if (y_c < 0) {
				x_a -= k7 * y_c;
				x_c -= i8 * y_c;
				vertexZ3 -= depthSlopeY * y_c;
				shade2 -= textureVStep * y_c;
				l3 -= j8 * y_c;
				y_c = 0;
			}
			x_b <<= 16;
			l2 <<= 16;
			if (y_b < 0) {
				x_b -= i7 * y_b;
				l2 -= textureUStep * y_b;
				y_b = 0;
			}
			int l9 = y_c - viewportCenterY;
			nextTextureV += j5 * l9;
			k5 += i6 * l9;
			j6 += screenOffset * l9;
			if (k7 < i8) {
				y_a -= y_b;
				y_b -= y_c;
				y_c = scanlineOffsets[y_c];
				while (--y_b >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_a >> 16, x_c >> 16, shade2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
							depthSlopeX);
					x_a += k7;
					x_c += i8;
					shade2 += textureVStep;
					l3 += j8;
					vertexZ3 += depthSlopeY;
					y_c += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--y_a >= 0) {
					rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_b >> 16, x_c >> 16, l2, l3, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
							depthSlopeX);
					x_b += i7;
					x_c += i8;
					l2 += textureUStep;
					l3 += j8;
					vertexZ3 += depthSlopeY;
					y_c += DrawingArea.width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			y_a -= y_b;
			y_b -= y_c;
			y_c = scanlineOffsets[y_c];
			while (--y_b >= 0) {
				rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_c >> 16, x_a >> 16, l3, shade2, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
						depthSlopeX);
				x_a += k7;
				x_c += i8;
				shade2 += textureVStep;
				l3 += j8;
				vertexZ3 += depthSlopeY;
				y_c += DrawingArea.width;
				nextTextureV += j5;
				k5 += i6;
				j6 += screenOffset;
			}
			while (--y_a >= 0) {
				rasterizeTexturedScanline(DrawingArea.pixels, texels, y_c, x_c >> 16, x_b >> 16, l3, l2, nextTextureV, k5, j6, i5, l5, k6, vertexZ3,
						depthSlopeX);
				x_b += i7;
				x_c += i8;
				l2 += textureUStep;
				l3 += j8;
				vertexZ3 += depthSlopeY;
				y_c += DrawingArea.width;
				nextTextureV += j5;
				k5 += i6;
				j6 += screenOffset;
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	// COMPLETE METHOD TO REPLACE: rasterizeTexturedScanline
	private static void rasterizeTexturedScanline(int[] pixelBuffer, int[] texture, int pixelBufferOffset, int start_x, int end_x, int shadeValue,
												  int gradient, int a1, int i2, int j2, int k2, int a2, int i3, float depth, float depthSlopeX) {
		int index = 0;
		int x = 0;
		if (start_x >= end_x)
			return;
		int dl = (gradient - shadeValue) / (end_x - start_x);
		int n;
		if (enableClipping) {
			if (end_x > DrawingArea.centerX)
				end_x = DrawingArea.centerX;
			if (start_x < 0) {
				shadeValue -= start_x * dl;
				start_x = 0;
			}
		}
		if (start_x >= end_x) {
			return;
		}
		n = end_x - start_x >> 3;
		pixelBufferOffset += start_x;
		depth += depthSlopeX * (float) start_x;
		int nextTextureU = 0;
		int nextTextureV = 0;
		int screenOffset = start_x - viewportCenterX;
		a1 += (k2 >> 3) * screenOffset;
		i2 += (a2 >> 3) * screenOffset;
		j2 += (i3 >> 3) * screenOffset;
		int l5 = j2 >> 14;
		if (l5 != 0) {
			index = a1 / l5;
			x = i2 / l5;
			if (index < 0)
				index = 0;
			else if (index > 16256)
				index = 16256;
		}
		a1 += k2;
		i2 += a2;
		j2 += i3;
		l5 = j2 >> 14;
		if (l5 != 0) {
			nextTextureU = a1 / l5;
			nextTextureV = i2 / l5;
			if (nextTextureU < 7)
				nextTextureU = 7;
			else if (nextTextureU > 16256)
				nextTextureU = 16256;
		}
		int textureUStep = nextTextureU - index >> 3;
		int textureVStep = nextTextureV - x >> 3;
		if (isOpaque) {
			while (n-- > 0) {
				for (int i = 0; i < 8; i++) {
					int rgb = texture[texelPos((x & 0x3f80) + (index >> 7))];
					int l = shadeValue >> 16;
					pixelBuffer[pixelBufferOffset] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
					DrawingArea.depthBuffer[pixelBufferOffset] = depth;
					pixelBufferOffset++;
					depth += depthSlopeX;
					index += textureUStep;
					x += textureVStep;
					shadeValue += dl;
				}
				index = nextTextureU;
				x = nextTextureV;
				a1 += k2;
				i2 += a2;
				j2 += i3;
				int i6 = j2 >> 14;
				if (i6 != 0) {
					nextTextureU = a1 / i6;
					nextTextureV = i2 / i6;
					if (nextTextureU < 7)
						nextTextureU = 7;
					else if (nextTextureU > 16256)
						nextTextureU = 16256;
				}
				textureUStep = nextTextureU - index >> 3;
				textureVStep = nextTextureV - x >> 3;
				shadeValue += dl;
			}
			for (n = end_x - start_x & 7; n-- > 0;) {
				int rgb = texture[texelPos((x & 0x3f80) + (index >> 7))];
				int l = shadeValue >> 16;
				pixelBuffer[pixelBufferOffset] = ((rgb & 0xff00ff) * l & ~0xff00ff) + ((rgb & 0xff00) * l & 0xff0000) >> 8;
				DrawingArea.depthBuffer[pixelBufferOffset] = depth;
				pixelBufferOffset++;
				depth += depthSlopeX;
				index += textureUStep;
				x += textureVStep;
				shadeValue += dl;
			}
			return;
		}
		while (n-- > 0) {
			for (int i = 0; i < 8; i++) {
				int i9 = texture[texelPos((x & 0x3f80) + (index >> 7))];
				if (i9 != 0) {
					int l = shadeValue >> 16;
					pixelBuffer[pixelBufferOffset] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 8;
					DrawingArea.depthBuffer[pixelBufferOffset] = depth;
				}
				depth += depthSlopeX;
				pixelBufferOffset++;
				index += textureUStep;
				x += textureVStep;
				shadeValue += dl;
			}
			index = nextTextureU;
			x = nextTextureV;
			a1 += k2;
			i2 += a2;
			j2 += i3;
			int j6 = j2 >> 14;
			if (j6 != 0) {
				nextTextureU = a1 / j6;
				nextTextureV = i2 / j6;
				if (nextTextureU < 7)
					nextTextureU = 7;
				else if (nextTextureU > 16256)
					nextTextureU = 16256;
			}
			textureUStep = nextTextureU - index >> 3;
			textureVStep = nextTextureV - x >> 3;
			shadeValue += dl;
		}
		for (int l3 = end_x - start_x & 7; l3-- > 0;) {
			int j9 = texture[texelPos((x & 0x3f80) + (index >> 7))];
			if (j9 != 0) {
				int l = shadeValue >> 16;
				pixelBuffer[pixelBufferOffset] = ((j9 & 0xff00ff) * l & ~0xff00ff) + ((j9 & 0xff00) * l & 0xff0000) >> 8;
				DrawingArea.depthBuffer[pixelBufferOffset] = depth;
			}
			depth += depthSlopeX;
			pixelBufferOffset++;
			index += textureUStep;
			x += textureVStep;
			shadeValue += dl;
		}
	}

	public static void renderTexturedTriangleNoDepth(int index, int x, int y, int l, int shade3, int j1, int shade1, int shade2, int i2, int j2, int k2,
													 int l2, int i3, int j3, int k3, int l3, int i4, int nextTextureU, int k4) {
		try {
			shade1 = 0x7f - shade1;
			shade2 = 0x7f - shade2;
			i2 = 0x7f - i2;
			setMipmapLevel(index, x, y, l, shade3, j1, k4);
			int[] texels = getMipmapTexels(k4)[currentMipmapLevel];
			isOpaque = !textureHasTransparency[k4];
			k2 = j2 - k2;
			j3 = i3 - j3;
			i4 = l3 - i4;
			l2 -= j2;
			k3 -= i3;
			nextTextureU -= l3;
			int nextTextureV = l2 * i3 - k3 * j2 << (WorldController.viewDistance == 9 ? 14 : 15);
			int i5 = k3 * l3 - nextTextureU * i3 << 8;
			int j5 = nextTextureU * j2 - l2 * l3 << 5;
			int k5 = k2 * i3 - j3 * j2 << (WorldController.viewDistance == 9 ? 14 : 15);
			int l5 = j3 * l3 - i4 * i3 << 8;
			int i6 = i4 * j2 - k2 * l3 << 5;
			int j6 = j3 * l2 - k2 * k3 << (WorldController.viewDistance == 9 ? 14 : 15);
			int k6 = i4 * k3 - j3 * nextTextureU << 8;
			int screenOffset = k2 * nextTextureU - i4 * l2 << 5;
			int i7 = 0;
			int textureUStep = 0;
			if (x != index) {
				i7 = (shade3 - l << 16) / (x - index);
				textureUStep = (shade2 - shade1 << 16) / (x - index);
			}
			int k7 = 0;
			int textureVStep = 0;
			if (y != x) {
				k7 = (j1 - shade3 << 16) / (y - x);
				textureVStep = (i2 - shade2 << 16) / (y - x);
			}
			int i8 = 0;
			int j8 = 0;
			if (y != index) {
				i8 = (l - j1 << 16) / (index - y);
				j8 = (shade1 - i2 << 16) / (index - y);
			}
			if (index <= x && index <= y) {
				if (index >= bottomY)
					return;
				if (x > bottomY)
					x = bottomY;
				if (y > bottomY)
					y = bottomY;
				if (x < y) {
					j1 = l <<= 16;
					i2 = shade1 <<= 16;
					if (index < 0) {
						j1 -= i8 * index;
						l -= i7 * index;
						i2 -= j8 * index;
						shade1 -= textureUStep * index;
						index = 0;
					}
					shade3 <<= 16;
					shade2 <<= 16;
					if (x < 0) {
						shade3 -= k7 * x;
						shade2 -= textureVStep * x;
						x = 0;
					}
					int k8 = index - viewportCenterY;
					nextTextureV += j5 * k8;
					k5 += i6 * k8;
					j6 += screenOffset * k8;
					if (index != x && i8 < i7 || index == x && i8 > k7) {
						y -= x;
						x -= index;
						index = scanlineOffsets[index];
						while (--x >= 0) {
							rasterizeTexturedScanlineNoDepth(pixels, texels, index, j1 >> 16, l >> 16, i2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
							j1 += i8;
							l += i7;
							i2 += j8;
							shade1 += textureUStep;
							index += width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						while (--y >= 0) {
							rasterizeTexturedScanlineNoDepth(pixels, texels, index, j1 >> 16, shade3 >> 16, i2 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
							j1 += i8;
							shade3 += k7;
							i2 += j8;
							shade2 += textureVStep;
							index += width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						return;
					}
					y -= x;
					x -= index;
					index = scanlineOffsets[index];
					while (--x >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, index, l >> 16, j1 >> 16, shade1 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						j1 += i8;
						l += i7;
						i2 += j8;
						shade1 += textureUStep;
						index += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, index, shade3 >> 16, j1 >> 16, shade2 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						j1 += i8;
						shade3 += k7;
						i2 += j8;
						shade2 += textureVStep;
						index += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				shade3 = l <<= 16;
				shade2 = shade1 <<= 16;
				if (index < 0) {
					shade3 -= i8 * index;
					l -= i7 * index;
					shade2 -= j8 * index;
					shade1 -= textureUStep * index;
					index = 0;
				}
				j1 <<= 16;
				i2 <<= 16;
				if (y < 0) {
					j1 -= k7 * y;
					i2 -= textureVStep * y;
					y = 0;
				}
				int l8 = index - viewportCenterY;
				nextTextureV += j5 * l8;
				k5 += i6 * l8;
				j6 += screenOffset * l8;
				if (index != y && i8 < i7 || index == y && k7 > i7) {
					x -= y;
					y -= index;
					index = scanlineOffsets[index];
					while (--y >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, index, shade3 >> 16, l >> 16, shade2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						shade3 += i8;
						l += i7;
						shade2 += j8;
						shade1 += textureUStep;
						index += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--x >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, index, j1 >> 16, l >> 16, i2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						j1 += k7;
						l += i7;
						i2 += textureVStep;
						shade1 += textureUStep;
						index += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				x -= y;
				y -= index;
				index = scanlineOffsets[index];
				while (--y >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, index, l >> 16, shade3 >> 16, shade1 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					shade3 += i8;
					l += i7;
					shade2 += j8;
					shade1 += textureUStep;
					index += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--x >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, index, l >> 16, j1 >> 16, shade1 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					j1 += k7;
					l += i7;
					i2 += textureVStep;
					shade1 += textureUStep;
					index += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			if (x <= y) {
				if (x >= bottomY)
					return;
				if (y > bottomY)
					y = bottomY;
				if (index > bottomY)
					index = bottomY;
				if (y < index) {
					l = shade3 <<= 16;
					shade1 = shade2 <<= 16;
					if (x < 0) {
						l -= i7 * x;
						shade3 -= k7 * x;
						shade1 -= textureUStep * x;
						shade2 -= textureVStep * x;
						x = 0;
					}
					j1 <<= 16;
					i2 <<= 16;
					if (y < 0) {
						j1 -= i8 * y;
						i2 -= j8 * y;
						y = 0;
					}
					int i9 = x - viewportCenterY;
					nextTextureV += j5 * i9;
					k5 += i6 * i9;
					j6 += screenOffset * i9;
					if (x != y && i7 < k7 || x == y && i7 > i8) {
						index -= y;
						y -= x;
						x = scanlineOffsets[x];
						while (--y >= 0) {
							rasterizeTexturedScanlineNoDepth(pixels, texels, x, l >> 16, shade3 >> 16, shade1 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
							l += i7;
							shade3 += k7;
							shade1 += textureUStep;
							shade2 += textureVStep;
							x += width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						while (--index >= 0) {
							rasterizeTexturedScanlineNoDepth(pixels, texels, x, l >> 16, j1 >> 16, shade1 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
							l += i7;
							j1 += i8;
							shade1 += textureUStep;
							i2 += j8;
							x += width;
							nextTextureV += j5;
							k5 += i6;
							j6 += screenOffset;
						}
						return;
					}
					index -= y;
					y -= x;
					x = scanlineOffsets[x];
					while (--y >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, x, shade3 >> 16, l >> 16, shade2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						l += i7;
						shade3 += k7;
						shade1 += textureUStep;
						shade2 += textureVStep;
						x += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--index >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, x, j1 >> 16, l >> 16, i2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						l += i7;
						j1 += i8;
						shade1 += textureUStep;
						i2 += j8;
						x += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				j1 = shade3 <<= 16;
				i2 = shade2 <<= 16;
				if (x < 0) {
					j1 -= i7 * x;
					shade3 -= k7 * x;
					i2 -= textureUStep * x;
					shade2 -= textureVStep * x;
					x = 0;
				}
				l <<= 16;
				shade1 <<= 16;
				if (index < 0) {
					l -= i8 * index;
					shade1 -= j8 * index;
					index = 0;
				}
				int j9 = x - viewportCenterY;
				nextTextureV += j5 * j9;
				k5 += i6 * j9;
				j6 += screenOffset * j9;
				if (i7 < k7) {
					y -= index;
					index -= x;
					x = scanlineOffsets[x];
					while (--index >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, x, j1 >> 16, shade3 >> 16, i2 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						j1 += i7;
						shade3 += k7;
						i2 += textureUStep;
						shade2 += textureVStep;
						x += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--y >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, x, l >> 16, shade3 >> 16, shade1 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						l += i8;
						shade3 += k7;
						shade1 += j8;
						shade2 += textureVStep;
						x += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				y -= index;
				index -= x;
				x = scanlineOffsets[x];
				while (--index >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, x, shade3 >> 16, j1 >> 16, shade2 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					j1 += i7;
					shade3 += k7;
					i2 += textureUStep;
					shade2 += textureVStep;
					x += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--y >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, x, shade3 >> 16, l >> 16, shade2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					l += i8;
					shade3 += k7;
					shade1 += j8;
					shade2 += textureVStep;
					x += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			if (y >= bottomY)
				return;
			if (index > bottomY)
				index = bottomY;
			if (x > bottomY)
				x = bottomY;
			if (index < x) {
				shade3 = j1 <<= 16;
				shade2 = i2 <<= 16;
				if (y < 0) {
					shade3 -= k7 * y;
					j1 -= i8 * y;
					shade2 -= textureVStep * y;
					i2 -= j8 * y;
					y = 0;
				}
				l <<= 16;
				shade1 <<= 16;
				if (index < 0) {
					l -= i7 * index;
					shade1 -= textureUStep * index;
					index = 0;
				}
				int k9 = y - viewportCenterY;
				nextTextureV += j5 * k9;
				k5 += i6 * k9;
				j6 += screenOffset * k9;
				if (k7 < i8) {
					x -= index;
					index -= y;
					y = scanlineOffsets[y];
					while (--index >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, y, shade3 >> 16, j1 >> 16, shade2 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						shade3 += k7;
						j1 += i8;
						shade2 += textureVStep;
						i2 += j8;
						y += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					while (--x >= 0) {
						rasterizeTexturedScanlineNoDepth(pixels, texels, y, shade3 >> 16, l >> 16, shade2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
						shade3 += k7;
						l += i7;
						shade2 += textureVStep;
						shade1 += textureUStep;
						y += width;
						nextTextureV += j5;
						k5 += i6;
						j6 += screenOffset;
					}
					return;
				}
				x -= index;
				index -= y;
				y = scanlineOffsets[y];
				while (--index >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, y, j1 >> 16, shade3 >> 16, i2 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					shade3 += k7;
					j1 += i8;
					shade2 += textureVStep;
					i2 += j8;
					y += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--x >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, y, l >> 16, shade3 >> 16, shade1 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					shade3 += k7;
					l += i7;
					shade2 += textureVStep;
					shade1 += textureUStep;
					y += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			l = j1 <<= 16;
			shade1 = i2 <<= 16;
			if (y < 0) {
				l -= k7 * y;
				j1 -= i8 * y;
				shade1 -= textureVStep * y;
				i2 -= j8 * y;
				y = 0;
			}
			shade3 <<= 16;
			shade2 <<= 16;
			if (x < 0) {
				shade3 -= i7 * x;
				shade2 -= textureUStep * x;
				x = 0;
			}
			int l9 = y - viewportCenterY;
			nextTextureV += j5 * l9;
			k5 += i6 * l9;
			j6 += screenOffset * l9;
			if (k7 < i8) {
				index -= x;
				x -= y;
				y = scanlineOffsets[y];
				while (--x >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, y, l >> 16, j1 >> 16, shade1 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					l += k7;
					j1 += i8;
					shade1 += textureVStep;
					i2 += j8;
					y += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				while (--index >= 0) {
					rasterizeTexturedScanlineNoDepth(pixels, texels, y, shade3 >> 16, j1 >> 16, shade2 >> 8, i2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
					shade3 += i7;
					j1 += i8;
					shade2 += textureUStep;
					i2 += j8;
					y += width;
					nextTextureV += j5;
					k5 += i6;
					j6 += screenOffset;
				}
				return;
			}
			index -= x;
			x -= y;
			y = scanlineOffsets[y];
			while (--x >= 0) {
				rasterizeTexturedScanlineNoDepth(pixels, texels, y, j1 >> 16, l >> 16, i2 >> 8, shade1 >> 8, nextTextureV, k5, j6, i5, l5, k6);
				l += k7;
				j1 += i8;
				shade1 += textureVStep;
				i2 += j8;
				y += width;
				nextTextureV += j5;
				k5 += i6;
				j6 += screenOffset;
			}
			while (--index >= 0) {
				rasterizeTexturedScanlineNoDepth(pixels, texels, y, j1 >> 16, shade3 >> 16, i2 >> 8, shade2 >> 8, nextTextureV, k5, j6, i5, l5, k6);
				shade3 += i7;
				j1 += i8;
				shade2 += textureUStep;
				i2 += j8;
				y += width;
				nextTextureV += j5;
				k5 += i6;
				j6 += screenOffset;
			}
		} catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	private static void rasterizeTexturedScanlineNoDepth(int[] texels, int[] textureData, int y, int vertexX1, int vertexX2, int lig1, int lig2, int shade2, int i2,
														 int j2, int k2, int l2, int i3) {
		int index = 0;
		int x = 0;
		if (vertexX1 >= vertexX2)
			return;
		int shadeStep = (lig2 - lig1) / (vertexX2 - vertexX1);
		int k3;
		if (enableClipping) {
			if (vertexX2 > bottomX)
				vertexX2 = bottomX;
			if (vertexX1 < 0) {
				lig1 -= vertexX1 * shadeStep;
				vertexX1 = 0;
			}
			if (vertexX1 >= vertexX2)
				return;
			k3 = vertexX2 - vertexX1 >> 3;
		} else {
			if (vertexX2 - vertexX1 > 7) {
				k3 = vertexX2 - vertexX1 >> 3;
			} else {
				k3 = 0;
			}
		}
		y += vertexX1;
		int nextTextureU = 0;
		int nextTextureV = 0;
		int screenOffset = vertexX1 - viewportCenterX;
		shade2 += (k2 >> 3) * screenOffset;
		i2 += (l2 >> 3) * screenOffset;
		j2 += (i3 >> 3) * screenOffset;
		int l5 = j2 >> 14;
		if (l5 != 0) {
			index = shade2 / l5;
			x = i2 / l5;
			if (index < 0)
				index = 0;
			else if (index > 16256)
				index = 16256;
		}
		shade2 += k2;
		i2 += l2;
		j2 += i3;
		l5 = j2 >> 14;
		if (l5 != 0) {
			nextTextureU = shade2 / l5;
			nextTextureV = i2 / l5;
			if (nextTextureU < 7)
				nextTextureU = 7;
			else if (nextTextureU > 16256)
				nextTextureU = 16256;
		}
		int textureUStep = nextTextureU - index >> 3;
		int textureVStep = nextTextureV - x >> 3;
		if (isOpaque) {
			while (k3-- > 0) {
				int i9;
				int l;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
				i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
				index = nextTextureU;
				x = nextTextureV;
				lig1 += shadeStep;
				shade2 += k2;
				i2 += l2;
				j2 += i3;
				int i6 = j2 >> 14;
				if (i6 != 0) {
					nextTextureU = shade2 / i6;
					nextTextureV = i2 / i6;
					if (nextTextureU < 7)
						nextTextureU = 7;
					else if (nextTextureU > 16256)
						nextTextureU = 16256;
				}
				textureUStep = nextTextureU - index >> 3;
				textureVStep = nextTextureV - x >> 3;
			}
			for (k3 = vertexX2 - vertexX1 & 7; k3-- > 0;) {
				int j9;
				int l;
				j9 = textureData[texelPos((x & 0x3f80) + (index >> 7))];
				l = lig1 >> 8;
				texels[y++] = ((j9 & 0xff00ff) * l & ~0xff00ff) + ((j9 & 0xff00) * l & 0xff0000) >> 7;
				index += textureUStep;
				x += textureVStep;
				lig1 += shadeStep;
			}

			return;
		}
		while (k3-- > 0) {
			int i9;
			int l;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
			if ((i9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((i9 & 0xff00ff) * l & ~0xff00ff) + ((i9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index = nextTextureU;
			x = nextTextureV;
			lig1 += shadeStep;
			shade2 += k2;
			i2 += l2;
			j2 += i3;
			int j6 = j2 >> 14;
			if (j6 != 0) {
				nextTextureU = shade2 / j6;
				nextTextureV = i2 / j6;
				if (nextTextureU < 7)
					nextTextureU = 7;
				else if (nextTextureU > 16256)
					nextTextureU = 16256;
			}
			textureUStep = nextTextureU - index >> 3;
			textureVStep = nextTextureV - x >> 3;
		}
		for (int l3 = vertexX2 - vertexX1 & 7; l3-- > 0;) {
			int j9;
			int l;
			if ((j9 = textureData[texelPos((x & 0x3f80) + (index >> 7))]) != 0) {
				l = lig1 >> 8;
				texels[y] = ((j9 & 0xff00ff) * l & ~0xff00ff) + ((j9 & 0xff00) * l & 0xff0000) >> 7;
			}
			y++;
			index += textureUStep;
			x += textureVStep;
			lig1 += shadeStep;
		}
	}
}