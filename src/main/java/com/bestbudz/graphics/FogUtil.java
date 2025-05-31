package com.bestbudz.graphics;

public class FogUtil {

	public static int mix(int color, int fogColor, float factor) {
		// Early exits - most important optimization
		if (factor <= 0f) return color;
		if (factor >= 1f) return fogColor;

		// Use integer math only - much faster than float
		int factorInt = (int)(factor * 256); // Convert to 0-256 range
		int invFactor = 256 - factorInt;

		// Extract RGB components
		int cR = (color >> 16) & 0xFF;
		int cG = (color >> 8) & 0xFF;
		int cB = color & 0xFF;

		int fR = (fogColor >> 16) & 0xFF;
		int fG = (fogColor >> 8) & 0xFF;
		int fB = fogColor & 0xFF;

		// Integer interpolation with bit shift (faster than division)
		int nR = ((cR * invFactor) + (fR * factorInt)) >> 8;
		int nG = ((cG * invFactor) + (fG * factorInt)) >> 8;
		int nB = ((cB * invFactor) + (fB * factorInt)) >> 8;

		return (nR << 16) | (nG << 8) | nB;
	}
}