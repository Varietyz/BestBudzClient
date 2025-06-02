package com.bestbudz.graphics;

public class FogHandler {
	public static final int FOG_COLOR = 0x888564; // rgb(136, 133, 100)

	private static final int FOG_START = 2000;
	private static final int FOG_END = 3000;
	private static final float FOG_RANGE = FOG_END - FOG_START;
	private static final float INV_FOG_RANGE = 1.0f / FOG_RANGE;

	public void renderFog(int[] colorBuffer, float[] depthBuffer) {
		for (int i = 0; i < colorBuffer.length; i++) {
			float depth = depthBuffer[i];
			if (depth <= FOG_START) continue;

			float factor = depth >= FOG_END
				? 1.0f
				: (depth - FOG_START) * INV_FOG_RANGE;

			colorBuffer[i] = FogUtil.mix(colorBuffer[i], FOG_COLOR, factor);
		}
	}
}
