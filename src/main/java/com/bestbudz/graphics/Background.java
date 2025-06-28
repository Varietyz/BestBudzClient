package com.bestbudz.graphics;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.network.ArchiveLoader;
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

	public Background(ArchiveLoader archiveLoader, String s, int i) {
		byte[] data = archiveLoader.extractFile(s + ".dat");
		if (data == null || data.length == 0) {
			//System.err.println("Background data missing or empty: " + s);
			textureData = new byte[0];
			backgroundWidth = 0;
			backgroundHeight = 0;
			return;
		}
		Stream stream = new Stream(data);

		byte[] indexData = archiveLoader.extractFile("index.dat");
		if (indexData == null || indexData.length == 0) {
			//System.err.println("Background index data missing or empty.");
			textureData = new byte[0];
			backgroundWidth = 0;
			backgroundHeight = 0;
			return;
		}
		Stream stream_1 = new Stream(indexData);

		if (stream.position + 2 > stream.buffer.length) {
			//System.err.println("Background data too short for header.");
			textureData = new byte[0];
			backgroundWidth = 0;
			backgroundHeight = 0;
			return;
		}
		stream_1.position = stream.readUnsignedWord();

		if (stream_1.position + 6 > stream_1.buffer.length) {
			//System.err.println("Background index data too short.");
			textureData = new byte[0];
			backgroundWidth = 0;
			backgroundHeight = 0;
			return;
		}

		anInt1456 = stream_1.readUnsignedWord();
		anInt1457 = stream_1.readUnsignedWord();

		int j = stream_1.readUnsignedByte();
		pixelData = new int[j];
		for (int k = 0; k < j - 1; k++) {
			if (stream_1.position + 3 > stream_1.buffer.length) {
				//System.err.println("Background index data incomplete reading runes.");
				textureData = new byte[0];
				backgroundWidth = 0;
				backgroundHeight = 0;
				return;
			}
			pixelData[k + 1] = stream_1.read3Bytes();
		}

		// Adjust offsets for i layers (skip data)
		for (int l = 0; l < i; l++) {
			if (stream_1.position + 5 > stream_1.buffer.length) {
				//System.err.println("Background index data incomplete adjusting offsets.");
				textureData = new byte[0];
				backgroundWidth = 0;
				backgroundHeight = 0;
				return;
			}
			stream_1.position += 2;
			int width = stream_1.readUnsignedWord();
			int height = stream_1.readUnsignedWord();
			if (width < 0 || height < 0 || stream_1.position + (width * height) > stream_1.buffer.length) {
				//System.err.println("Background index data invalid width/height or buffer overflow.");
				textureData = new byte[0];
				backgroundWidth = 0;
				backgroundHeight = 0;
				return;
			}
			stream.position += width * height;
			stream_1.position++;
		}

		if (stream_1.position + 6 > stream_1.buffer.length) {
		//	System.err.println("Background index data too short for remaining fields.");
			textureData = new byte[0];
			backgroundWidth = 0;
			backgroundHeight = 0;
			return;
		}

		anInt1454 = stream_1.readUnsignedByte();
		anInt1455 = stream_1.readUnsignedByte();
		backgroundWidth = stream_1.readUnsignedWord();
		backgroundHeight = stream_1.readUnsignedWord();
		int i1 = stream_1.readUnsignedByte();

		int j1 = backgroundWidth * backgroundHeight;
		if (j1 < 0 || stream.position + j1 > stream.buffer.length) {
		//	System.err.println("Background data length invalid or exceeds buffer size.");
			textureData = new byte[0];
			return;
		}

		textureData = new byte[j1];

		if (i1 == 0) {
			for (int k1 = 0; k1 < j1; k1++) {
				textureData[k1] = stream.readSignedByte();
			}
		} else if (i1 == 1) {
			for (int l1 = 0; l1 < backgroundWidth; l1++) {
				for (int i2 = 0; i2 < backgroundHeight; i2++) {
					textureData[l1 + i2 * backgroundWidth] = stream.readSignedByte();
				}
			}
		} else {
		//	System.err.println("Background format " + i1 + " not supported.");
			textureData = new byte[0];
		}
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
