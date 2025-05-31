package com.bestbudz.graphics;

public class FogHandler {
	public int fogColor = 0xB3B281;

	// Pre-calculate these once to avoid repeated calculations
	private static final int FOG_START = 2000;
	private static final int FOG_END = 3000;
	private static final float FOG_RANGE = FOG_END - FOG_START; // 1000.0f
	private static final float INV_FOG_RANGE = 1.0f / FOG_RANGE; // 0.001f

	public void renderFog(int[] colorBuffer, float[] depthBuffer) {
		// Process all pixels in one simple loop
		for (int i = 0; i < colorBuffer.length; i++) {
			float depth = depthBuffer[i];

			// Skip pixels that don't need fog (most important optimization)
			if (depth <= FOG_START) continue;

			// Calculate fog factor using pre-computed inverse
			float factor;
			if (depth >= FOG_END) {
				factor = 1.0f; // Full fog
			} else {
				factor = (depth - FOG_START) * INV_FOG_RANGE; // Fast calculation
			}

			// Apply fog
			colorBuffer[i] = FogUtil.mix(colorBuffer[i], fogColor, factor);
		}
	}
}