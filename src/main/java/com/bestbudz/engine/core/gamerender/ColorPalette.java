package com.bestbudz.engine.core.gamerender;

import static com.bestbudz.engine.core.gamerender.Rasterizer.backgroundTextures;
import static com.bestbudz.engine.core.gamerender.Rasterizer.colorPalette;
import static com.bestbudz.engine.core.gamerender.Rasterizer.textureColorArrays;
import static com.bestbudz.engine.core.gamerender.Rasterizer.applyTexture;
import static com.bestbudz.engine.core.gamerender.Rasterizer.textureAmount;

public class ColorPalette {
	private static final double GLOBAL_HUE_SHIFT = 0.6; // +10%
	private static final boolean ENABLE_RED_OVERRIDE = true;
	private static final boolean ENABLE_YELLOW_SKIP = true;

	public static void generateColorPalette(double brightnessModifier) {
		int index = 0;

		for (int huesatIndex = 0; huesatIndex < 512; huesatIndex++) {
			double originalHue = (huesatIndex / 8.0) / 64.0 + 0.0078125;
			double modifiedHue = modifyHue(originalHue);

			double saturation = (huesatIndex & 7) / 8.0 + 0.0625;
			saturation = Math.min(1.0, saturation * 0.8);

			for (int luminanceLevel = 0; luminanceLevel < 128; luminanceLevel++) {
				double luminance = luminanceLevel / 128.0;

				double red = luminance, green = luminance, blue = luminance;

				if (saturation != 0.0) {
					double chromaticMax = luminance < 0.5
						? luminance * (1.0 + saturation)
						: (luminance + saturation) - (luminance * saturation);
					double chromaticMin = 2 * luminance - chromaticMax;

					red = hueToRgb(chromaticMin, chromaticMax, modifiedHue + 1.0 / 3.0);
					green = hueToRgb(chromaticMin, chromaticMax, modifiedHue);
					blue = hueToRgb(chromaticMin, chromaticMax, modifiedHue - 1.0 / 3.0);
				}

				int redInt = (int) (red * 256.0);
				int greenInt = (int) (green * 256.0);
				int blueInt = (int) (blue * 256.0);

				int rgbColor = (redInt << 16) | (greenInt << 8) | blueInt;
				rgbColor = adjustColorBrightness(rgbColor, brightnessModifier);
				colorPalette[index++] = rgbColor != 0 ? rgbColor : 1;
			}
		}

		for (int textureId = 0; textureId < textureAmount; textureId++) {
			if (backgroundTextures[textureId] == null) continue;
			int[] sourcePixels = backgroundTextures[textureId].pixelData;
			textureColorArrays[textureId] = new int[sourcePixels.length];
			for (int pixelIndex = 0; pixelIndex < sourcePixels.length; pixelIndex++) {
				int adjustedColor = adjustColorBrightness(sourcePixels[pixelIndex], brightnessModifier);
				textureColorArrays[textureId][pixelIndex] = (adjustedColor & 0xf8f8ff) == 0 && pixelIndex != 0 ? 1 : adjustedColor;
			}
		}

		for (int textureId = 0; textureId < textureAmount; textureId++) {
			applyTexture(textureId);
		}
	}

	private static double modifyHue(double originalHue) {
		boolean inYellowRange = originalHue >= 0.05 && originalHue <= 0.16;

		if (ENABLE_RED_OVERRIDE && (originalHue <= 0.05 || originalHue >= 0.97)) {
			return 0.50; // on red
		}

		if (ENABLE_YELLOW_SKIP && inYellowRange) {
			return originalHue;
		}

		double shiftedHue = originalHue + GLOBAL_HUE_SHIFT;
		return shiftedHue > 1.0 ? shiftedHue - 1.0 : shiftedHue;
	}

	private static double hueToRgb(double chromaticMin, double chromaticMax, double hue) {
		if (hue < 0) hue += 1.0;
		if (hue > 1) hue -= 1.0;
		if (hue < 1.0 / 6.0) return chromaticMin + (chromaticMax - chromaticMin) * 6.0 * hue;
		if (hue < 1.0 / 2.0) return chromaticMax;
		if (hue < 2.0 / 3.0) return chromaticMin + (chromaticMax - chromaticMin) * (2.0 / 3.0 - hue) * 6.0;
		return chromaticMin;
	}

	public static int adjustColorBrightness(int rgbColor, double brightnessExponent) {
		double redComponent = (double) (rgbColor >> 16) / 256D;
		double greenComponent = (double) (rgbColor >> 8 & 0xff) / 256D;
		double blueComponent = (double) (rgbColor & 0xff) / 256D;
		redComponent = Math.pow(redComponent, brightnessExponent);
		greenComponent = Math.pow(greenComponent, brightnessExponent);
		blueComponent = Math.pow(blueComponent, brightnessExponent);
		int adjustedRed = (int) (redComponent * 256D);
		int adjustedGreen = (int) (greenComponent * 256D);
		int adjustedBlue = (int) (blueComponent * 256D);
		return (adjustedRed << 16) + (adjustedGreen << 8) + adjustedBlue;
	}
}
