package com.bestbudz.graphics;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.network.Stream;

public final class Background extends DrawingArea
{

  public int[] pixelData;
  public byte[] textureData;
  public int backgroundWidth;
  public int backgroundHeight;
  public int anInt1454;
  public int anInt1455;
  public int anInt1456;
  private int anInt1457;

	public static Background loadFromExtracted(String name, int index) {
		Background bg = new Background();
		try {
			String key = name + "_" + index;
			com.google.gson.JsonObject mediaSpriteIndex = com.bestbudz.cache.JsonCacheLoader.loadJsonObject("media_sprites/_index.json");
			if (mediaSpriteIndex != null && mediaSpriteIndex.has(key)) {
				com.google.gson.JsonObject meta = mediaSpriteIndex.getAsJsonObject(key);
				String file = meta.get("file").getAsString();
				int width = meta.get("width").getAsInt();
				int height = meta.get("height").getAsInt();
				int offsetX = meta.has("offsetX") ? meta.get("offsetX").getAsInt() : 0;
				int offsetY = meta.has("offsetY") ? meta.get("offsetY").getAsInt() : 0;
				int fullWidth = meta.has("fullWidth") ? meta.get("fullWidth").getAsInt() : width;
				int fullHeight = meta.has("fullHeight") ? meta.get("fullHeight").getAsInt() : height;

				byte[] pngData = com.bestbudz.cache.JsonCacheLoader.loadFileBytes("media_sprites/" + file);
				if (pngData != null) {
					java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(pngData);
					javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
					int imgW = icon.getIconWidth();
					int imgH = icon.getIconHeight();
					int[] pixels = new int[imgW * imgH];
					java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(
						image, 0, 0, imgW, imgH, pixels, 0, imgW);
					pg.grabPixels();

					// Build palette from unique colors
					java.util.LinkedHashMap<Integer, Integer> colorToIndex = new java.util.LinkedHashMap<>();
					colorToIndex.put(0, 0); // index 0 = transparent
					for (int p : pixels) {
						int rgb = p & 0xFFFFFF;
						if (!colorToIndex.containsKey(rgb)) {
							colorToIndex.put(rgb, colorToIndex.size());
						}
					}

					bg.pixelData = new int[colorToIndex.size()];
					for (java.util.Map.Entry<Integer, Integer> e : colorToIndex.entrySet()) {
						bg.pixelData[e.getValue()] = e.getKey();
					}

					bg.textureData = new byte[imgW * imgH];
					for (int i2 = 0; i2 < pixels.length; i2++) {
						int rgb = pixels[i2] & 0xFFFFFF;
						Integer idx = colorToIndex.get(rgb);
						bg.textureData[i2] = (byte) (idx != null ? idx : 0);
					}

					bg.backgroundWidth = imgW;
					bg.backgroundHeight = imgH;
					bg.anInt1454 = offsetX;
					bg.anInt1455 = offsetY;
					bg.anInt1456 = fullWidth;
					bg.anInt1457 = fullHeight;
					return bg;
				}
			}

			// Try textures directory (for numbered textures)
			com.google.gson.JsonObject texIndex = com.bestbudz.cache.JsonCacheLoader.loadJsonObject("textures/_index.json");
			if (texIndex != null && texIndex.has(name)) {
				com.google.gson.JsonObject meta = texIndex.getAsJsonObject(name);
				String file = meta.get("file").getAsString();
				int width = meta.get("width").getAsInt();
				int height = meta.get("height").getAsInt();

				byte[] pngData = com.bestbudz.cache.JsonCacheLoader.loadFileBytes("textures/" + file);
				if (pngData != null) {
					java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(pngData);
					javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
					int imgW = icon.getIconWidth();
					int imgH = icon.getIconHeight();
					int[] pixels = new int[imgW * imgH];
					java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(
						image, 0, 0, imgW, imgH, pixels, 0, imgW);
					pg.grabPixels();

					java.util.LinkedHashMap<Integer, Integer> colorToIndex = new java.util.LinkedHashMap<>();
					colorToIndex.put(0, 0);
					for (int p : pixels) {
						int rgb = p & 0xFFFFFF;
						if (!colorToIndex.containsKey(rgb)) {
							colorToIndex.put(rgb, colorToIndex.size());
						}
					}

					bg.pixelData = new int[colorToIndex.size()];
					for (java.util.Map.Entry<Integer, Integer> e : colorToIndex.entrySet()) {
						bg.pixelData[e.getValue()] = e.getKey();
					}

					bg.textureData = new byte[imgW * imgH];
					for (int i2 = 0; i2 < pixels.length; i2++) {
						int rgb = pixels[i2] & 0xFFFFFF;
						Integer idx = colorToIndex.get(rgb);
						bg.textureData[i2] = (byte) (idx != null ? idx : 0);
					}

					bg.backgroundWidth = imgW;
					bg.backgroundHeight = imgH;
					bg.anInt1454 = 0;
					bg.anInt1455 = 0;
					// Rasterizer expects textures at 128x128 (or 64x64)
					bg.anInt1456 = imgW <= 64 ? 64 : 128;
					bg.anInt1457 = imgH <= 64 ? 64 : 128;
					return bg;
				}
			}
		} catch (Exception e) {
			System.err.println("[Background] Failed to load from extracted: " + name + "_" + index + ": " + e.getMessage());
		}

		return null;
	}

	private Background() {
	}

	public void method356() {
    anInt1456 /= 2;
    anInt1457 /= 2;
    byte[] abyte0 = new byte[anInt1456 * anInt1457];
    int i = 0;
    for (int j = 0; j < backgroundHeight; j++) {
      for (int k = 0; k < backgroundWidth; k++) {
        abyte0[(k + anInt1454 >> 1) + (j + anInt1455 >> 1) * anInt1456] = textureData[i++];
      }
    }
    textureData = abyte0;
    backgroundWidth = anInt1456;
    backgroundHeight = anInt1457;
    anInt1454 = 0;
    anInt1455 = 0;
  }

  public void method357() {
    if (backgroundWidth == anInt1456 && backgroundHeight == anInt1457) {
      return;
    }
    byte[] abyte0 = new byte[anInt1456 * anInt1457];
    int i = 0;
    for (int j = 0; j < backgroundHeight; j++) {
      for (int k = 0; k < backgroundWidth; k++) {
        abyte0[k + anInt1454 + (j + anInt1455) * anInt1456] = textureData[i++];
      }
    }
    textureData = abyte0;
    backgroundWidth = anInt1456;
    backgroundHeight = anInt1457;
    anInt1454 = 0;
    anInt1455 = 0;
  }

  public void method360(int i, int j, int k) {
    for (int i1 = 0; i1 < pixelData.length; i1++) {
      int j1 = pixelData[i1] >> 16 & 0xff;
      j1 += i;
      if (j1 < 0) {
        j1 = 0;
      } else if (j1 > 255) {
        j1 = 255;
      }
      int k1 = pixelData[i1] >> 8 & 0xff;
      k1 += j;
      if (k1 < 0) {
        k1 = 0;
      } else if (k1 > 255) {
        k1 = 255;
      }
      int l1 = pixelData[i1] & 0xff;
      l1 += k;
      if (l1 < 0) {
        l1 = 0;
      } else if (l1 > 255) {
        l1 = 255;
      }
      pixelData[i1] = (j1 << 16) + (k1 << 8) + l1;
    }
  }

  public void drawBackground(int i, int k) {
    i += anInt1454;
    k += anInt1455;
    int l = i + k * DrawingArea.width;
    int i1 = 0;
    int j1 = backgroundHeight;
    int k1 = backgroundWidth;
    int l1 = DrawingArea.width - k1;
    int i2 = 0;
    if (k < DrawingArea.topY) {
      int j2 = DrawingArea.topY - k;
      j1 -= j2;
      k = DrawingArea.topY;
      i1 += j2 * k1;
      l += j2 * DrawingArea.width;
    }
    if (k + j1 > DrawingArea.bottomY) {
      j1 -= (k + j1) - DrawingArea.bottomY;
    }
    if (i < DrawingArea.topX) {
      int k2 = DrawingArea.topX - i;
      k1 -= k2;
      i = DrawingArea.topX;
      i1 += k2;
      l += k2;
      i2 += k2;
      l1 += k2;
    }
    if (i + k1 > DrawingArea.bottomX) {
      int l2 = (i + k1) - DrawingArea.bottomX;
      k1 -= l2;
      i2 += l2;
      l1 += l2;
    }
    if (!(k1 <= 0 || j1 <= 0)) {
      method362(j1, DrawingArea.pixels, textureData, l1, l, k1, i1, pixelData, i2);
    }
  }

  private void method362(
      int i, int[] ai, byte[] abyte0, int j, int k, int l, int i1, int[] ai1, int j1) {
    int k1 = -(l >> 2);
    l = -(l & 3);
    for (int l1 = -i; l1 < 0; l1++) {
      for (int i2 = k1; i2 < 0; i2++) {
        byte byte1 = abyte0[i1++];
        if (byte1 != 0) {
          ai[k++] = ai1[byte1 & 0xff];
        } else {
          k++;
        }
        byte1 = abyte0[i1++];
        if (byte1 != 0) {
          ai[k++] = ai1[byte1 & 0xff];
        } else {
          k++;
        }
        byte1 = abyte0[i1++];
        if (byte1 != 0) {
          ai[k++] = ai1[byte1 & 0xff];
        } else {
          k++;
        }
        byte1 = abyte0[i1++];
        if (byte1 != 0) {
          ai[k++] = ai1[byte1 & 0xff];
        } else {
          k++;
        }
      }
      for (int j2 = l; j2 < 0; j2++) {
        byte byte2 = abyte0[i1++];
        if (byte2 != 0) {
          ai[k++] = ai1[byte2 & 0xff];
        } else {
          k++;
        }
      }
      k += j;
      i1 += j1;
    }
  }
}
