package com.bestbudz.graphics;

public class FogUtil {
	public static int mix(int color, int fogColor, float factor) {
		if (factor <= 0f) return color;
		if (factor >= 1f) return fogColor;

		int factorInt = (int)(factor * 256);
		int invFactor = 256 - factorInt;

		int cR = (color >> 16) & 0xFF;
		int cG = (color >> 8) & 0xFF;
		int cB = color & 0xFF;

		int fR = (fogColor >> 16) & 0xFF;
		int fG = (fogColor >> 8) & 0xFF;
		int fB = fogColor & 0xFF;

		int nR = ((cR * invFactor) + (fR * factorInt)) >> 8;
		int nG = ((cG * invFactor) + (fG * factorInt)) >> 8;
		int nB = ((cB * invFactor) + (fB * factorInt)) >> 8;

		return (nR << 16) | (nG << 8) | nB;
	}
}
