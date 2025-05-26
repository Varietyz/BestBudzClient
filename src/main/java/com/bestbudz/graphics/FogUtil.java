package com.bestbudz.graphics;

public class FogUtil {

  public static int mix(int color, int fogColor, float factor) {
    if (factor >= 1f) {
      return fogColor;
    }
    if (factor <= 0f) {
      return color;
    }
    int aR = (color >> 16) & 0xFF;
    int aG = (color >> 8) & 0xFF;
    int aB = (color) & 0xFF;

    int bR = (fogColor >> 16) & 0xFF;
    int bG = (fogColor >> 8) & 0xFF;
    int bB = (fogColor) & 0xFF;

    int dR = bR - aR;
    int dG = bG - aG;
    int dB = bB - aB;

    int nR = (int) (aR + (dR * factor));
    int nG = (int) (aG + (dG * factor));
    int nB = (int) (aB + (dB * factor));
    return (nR << 16) + (nG << 8) + nB;
  }
}
