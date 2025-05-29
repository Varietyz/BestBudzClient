package com.bestbudz.graphics.text;

import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import java.util.Random;

public final class TextDrawingArea extends DrawingArea {

  public byte[][] aByteArrayArray1491;
  public int[] anIntArray1492;
  public int[] anIntArray1493;
  public int[] anIntArray1494;
  public int[] anIntArray1495;
  public int[] anIntArray1496;
  public int anInt1497;
  public Random aRandom1498;
  public boolean aBoolean1499;

  public TextDrawingArea(boolean flag, String s, StreamLoader streamLoader) {
    try {
      int length = (s.equals("hit_full") || s.equals("critical_full")) ? 58 : 256;
      aByteArrayArray1491 = new byte[length][];
      anIntArray1492 = new int[length];
      anIntArray1493 = new int[length];
      anIntArray1494 = new int[length];
      anIntArray1495 = new int[length];
      anIntArray1496 = new int[length];
      aRandom1498 = new Random();
      aBoolean1499 = false;
      Stream stream = new Stream(streamLoader.getDataForName(s + ".dat"));
      Stream stream_1 = new Stream(streamLoader.getDataForName("index.dat"));
      stream_1.currentOffset = stream.readUnsignedWord() + 4;
      int k = stream_1.readUnsignedByte();
      if (k > 0) stream_1.currentOffset += 3 * (k - 1);
      for (int l = 0; l < length; l++) {
        anIntArray1494[l] = stream_1.readUnsignedByte();
        anIntArray1495[l] = stream_1.readUnsignedByte();
        int i1 = anIntArray1492[l] = stream_1.readUnsignedWord();
        int j1 = anIntArray1493[l] = stream_1.readUnsignedWord();
        int k1 = stream_1.readUnsignedByte();
        int l1 = i1 * j1;
        aByteArrayArray1491[l] = new byte[l1];
        if (k1 == 0) {
          for (int i2 = 0; i2 < l1; i2++) aByteArrayArray1491[l][i2] = stream.readSignedByte();

        } else if (k1 == 1) {
          for (int j2 = 0; j2 < i1; j2++) {
            for (int l2 = 0; l2 < j1; l2++)
              aByteArrayArray1491[l][j2 + l2 * i1] = stream.readSignedByte();
          }
        }
        if (j1 > anInt1497 && l < 128) anInt1497 = j1;
        anIntArray1494[l] = 1;
        anIntArray1496[l] = i1 + 2;
        int k2 = 0;
        for (int i3 = j1 / 7; i3 < j1; i3++) k2 += aByteArrayArray1491[l][i3 * i1];
        if (k2 <= j1 / 7) {
          anIntArray1496[l]--;
          anIntArray1494[l] = 0;
        }
        k2 = 0;
        for (int j3 = j1 / 7; j3 < j1; j3++) k2 += aByteArrayArray1491[l][(i1 - 1) + j3 * i1];
        if (k2 <= j1 / 7) anIntArray1496[l]--;
      }
      if (flag) {
        anIntArray1496[32] = anIntArray1496[73];
      } else {
        anIntArray1496[32] = anIntArray1496[105];
      }
    } catch (Exception _ex)
	{
		throw new RuntimeException(_ex);
	}
  }

  public void drawText(int i, String s, int k, int l) {
    method385(i, s, k, l - method384(s) / 2);
  }

  public void method382(int i, int j, String s, int l, boolean flag) {
    method389(flag, j - getTextWidth(s) / 2, i, s, l);
  }

  public int getTextWidth(String s) {
    if (s == null) return 0;
    int j = 0;
    for (int k = 0; k < s.length(); k++)
      if (s.charAt(k) == '@' && k + 4 < s.length() && s.charAt(k + 4) == '@') k += 4;
      else j += anIntArray1496[s.charAt(k)];
    return j;
  }

  public int method384(String s) {
    if (s == null) return 0;
    int j = 0;
    for (int k = 0; k < s.length(); k++) j += anIntArray1496[s.charAt(k)];
    return j;
  }

  public void method385(int i, String s, int j, int l) {
    if (s == null) return;
    j -= anInt1497;
    for (int i1 = 0; i1 < s.length(); i1++) {
      char c = s.charAt(i1);
      if (c != ' ')
        method392(
            aByteArrayArray1491[c],
            l + anIntArray1494[c],
            j + anIntArray1495[c],
            anIntArray1492[c],
            anIntArray1493[c],
            i);
      l += anIntArray1496[c];
    }
  }

  public void method386(int i, String s, int j, int k, int l) {
    if (s == null) return;
    j -= method384(s) / 2;
    l -= anInt1497;
    for (int i1 = 0; i1 < s.length(); i1++) {
      char c = s.charAt(i1);
      if (c != ' ')
        method392(
            aByteArrayArray1491[c],
            j + anIntArray1494[c],
            l + anIntArray1495[c] + (int) (Math.sin((double) i1 / 2D + (double) k / 5D) * 5D),
            anIntArray1492[c],
            anIntArray1493[c],
            i);
      j += anIntArray1496[c];
    }
  }

  public void method387(int i, String s, int j, int k, int l) {
    if (s == null) return;
    i -= method384(s) / 2;
    k -= anInt1497;
    for (int i1 = 0; i1 < s.length(); i1++) {
      char c = s.charAt(i1);
      if (c != ' ')
        method392(
            aByteArrayArray1491[c],
            i + anIntArray1494[c] + (int) (Math.sin((double) i1 / 5D + (double) j / 5D) * 5D),
            k + anIntArray1495[c] + (int) (Math.sin((double) i1 / 3D + (double) j / 5D) * 5D),
            anIntArray1492[c],
            anIntArray1493[c],
            l);
      i += anIntArray1496[c];
    }
  }

  public void method388(int i, String s, int j, int k, int l, int i1) {
    if (s == null) return;
    double d = 7D - (double) i / 8D;
    if (d < 0.0D) d = 0.0D;
    l -= method384(s) / 2;
    k -= anInt1497;
    for (int k1 = 0; k1 < s.length(); k1++) {
      char c = s.charAt(k1);
      if (c != ' ')
        method392(
            aByteArrayArray1491[c],
            l + anIntArray1494[c],
            k + anIntArray1495[c] + (int) (Math.sin((double) k1 / 1.5D + (double) j) * d),
            anIntArray1492[c],
            anIntArray1493[c],
            i1);
      l += anIntArray1496[c];
    }
  }

  public void method389(boolean flag1, int i, int j, String s, int k) {
    aBoolean1499 = false;
    int l = i;
    if (s == null) return;
    k -= anInt1497;
    for (int i1 = 0; i1 < s.length(); i1++)
      if (s.charAt(i1) == '@' && i1 + 4 < s.length() && s.charAt(i1 + 4) == '@') {
        int j1 = getColorByName(s.substring(i1 + 1, i1 + 4));
        if (j1 != -1) j = j1;
        i1 += 4;
      } else {
        char c = s.charAt(i1);
        if (c != ' ') {
          if (flag1)
            method392(
                aByteArrayArray1491[c],
                i + anIntArray1494[c] + 1,
                k + anIntArray1495[c] + 1,
                anIntArray1492[c],
                anIntArray1493[c],
                0);
          method392(
              aByteArrayArray1491[c],
              i + anIntArray1494[c],
              k + anIntArray1495[c],
              anIntArray1492[c],
              anIntArray1493[c],
              j);
        }
        i += anIntArray1496[c];
      }
    if (aBoolean1499)
      DrawingArea.method339(
          k + (int) ((double) anInt1497 * 0.69999999999999996D), 0x800000, i - l, l);
  }

	private int getColorByName(String s) {
		switch (s) {
			case "mbl":
				return ColorConfig.COLOR_LAVENDER_PINK;
			case "mye":
				return ColorConfig.CHAT_MINT_AQUA;
			case "mre":
				return ColorConfig.CHAT_LIGHT_MAGENTA;
			case "369":
				return ColorConfig.COLOR_LILAC;
			case "mon":
				return ColorConfig.COLOR_PEACH_ORANGE;
			case "gry":
				return ColorConfig.COLOR_LIME_YELLOW;
			case "red":
				return ColorConfig.CHAT_SOFT_PINK;
			case "gre":
				return ColorConfig.CHAT_BABY_BLUE;
			case "blu":
				return ColorConfig.COLOR_LIME_GREEN;
			case "yel":
				return ColorConfig.COLOR_PASTEL_VIOLET;
			case "cya":
				return ColorConfig.COLOR_SPRING_GREEN;
			case "mag":
				return ColorConfig.COLOR_BRIGHT_YELLOW;
			case "whi":
				return ColorConfig.COLOR_FROST_TEAL;
			case "bla":
				return ColorConfig.COLOR_LIGHT_AMBER;
			case "lre":
				return ColorConfig.COLOR_DUSTY_PERIWINKLE;
			case "dre":
				return ColorConfig.COLOR_SUNFLOWER_YELLOW;
			case "dbl":
				return ColorConfig.COLOR_MINTY_GREEN;
			case "or1":
				return ColorConfig.COLOR_CYAN_TURQUOISE;
			case "or2":
				return ColorConfig.COLOR_LAVENDER;
			case "or3":
				return ColorConfig.COLOR_ROSE_PINK;
			case "gr1":
				return ColorConfig.COLOR_SOFT_PEACH;
			case "gr2":
				return ColorConfig.COLOR_CORAL_ORANGE;
			case "gr3":
				return ColorConfig.COLOR_CREAM_ROSE;
			case "str":
				aBoolean1499 = true;
				break;
		}
		if (s.equals("end")) aBoolean1499 = false;
		return -1;
	}


  private void method392(byte[] abyte0, int i, int j, int k, int l, int i1) {
    int j1 = i + j * width;
    int k1 = width - k;
    int l1 = 0;
    int i2 = 0;
    if (j < topY) {
      int j2 = topY - j;
      l -= j2;
      j = topY;
      i2 += j2 * k;
      j1 += j2 * width;
    }
    if (j + l >= bottomY) l -= ((j + l) - bottomY);
    if (i < topX) {
      int k2 = topX - i;
      k -= k2;
      i = topX;
      i2 += k2;
      j1 += k2;
      l1 += k2;
      k1 += k2;
    }
    if (i + k >= bottomX) {
      int l2 = ((i + k) - bottomX);
      k -= l2;
      l1 += l2;
      k1 += l2;
    }
    if (!(k <= 0 || l <= 0)) {
      method393(pixels, abyte0, i1, i2, j1, k, l, k1, l1);
    }
  }

  private void method393(
      int[] ai, byte[] abyte0, int i, int j, int k, int l, int i1, int j1, int k1) {
    int l1 = -(l >> 2);
    l = -(l & 3);
    for (int i2 = -i1; i2 < 0; i2++) {
      for (int j2 = l1; j2 < 0; j2++) {
        if (abyte0[j++] != 0) ai[k++] = i;
        else k++;
        if (abyte0[j++] != 0) ai[k++] = i;
        else k++;
        if (abyte0[j++] != 0) ai[k++] = i;
        else k++;
        if (abyte0[j++] != 0) ai[k++] = i;
        else k++;
      }
      for (int k2 = l; k2 < 0; k2++)
        if (abyte0[j++] != 0) ai[k++] = i;
        else k++;

      k += j1;
      j += k1;
    }
  }

  private void method395(
      byte[] abyte0, int i, int j, int[] ai, int l, int i1, int j1, int k1, int l1, int i2) {
    l1 = ((l1 & 0xff00ff) * i2 & 0xff00ff00) + ((l1 & 0xff00) * i2 & 0xff0000) >> 8;
    i2 = 256 - i2;
    for (int j2 = -i; j2 < 0; j2++) {
      for (int k2 = -i1; k2 < 0; k2++)
        if (abyte0[l++] != 0) {
          int l2 = ai[j];
          ai[j++] =
              (((l2 & 0xff00ff) * i2 & 0xff00ff00) + ((l2 & 0xff00) * i2 & 0xff0000) >> 8) + l1;
        } else {
          j++;
        }
      j += k1;
      l += j1;
    }
  }

  public void drawRightAlignedString(String s, int x, int y, int color) {
    drawString(s, x - method384(s), y, color);
  }

  public void drawString(String s, int x, int y, int color) {
    if (s == null) return;
    y -= height;
    for (int i1 = 0; i1 < s.length(); i1++) {
      char c = s.charAt(i1);
      if (c != ' ')
        method392(
            aByteArrayArray1491[c],
            x + anIntArray1494[c],
            y + anIntArray1495[c],
            anIntArray1492[c],
            anIntArray1493[c],
            color);
      x += anIntArray1496[c];
    }
  }
}
