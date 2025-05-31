package com.bestbudz.util;

import java.awt.Color;

public class ColorUtility {

	private static final float sat = 1.0f;
	private static final float bri = 1.0f;
	public static int fadeStep = 1;
	public static int fadingToColor;
	public static boolean switchColor = false;
	private static float hue = 0.0f;

	public static int getHexColors() {
		hue += 0.0005F;
		Color base = new Color(Color.HSBtoRGB(hue, sat, bri));
		Color color = new Color(base.getRed(), base.getGreen(), base.getBlue(), 125);
		return color.getRGB();
	}

	// Optimized fade without Color object creation
	public static int fadeColors(Color color1, Color color2, float step) {
		if (step <= 0) return color1.getRGB();
		if (step >= 100) return color2.getRGB();

		float ratio = step * 0.01f; // Divide by 100
		float invRatio = 1.0f - ratio;

		int r = (int) (color2.getRed() * ratio + color1.getRed() * invRatio);
		int g = (int) (color2.getGreen() * ratio + color1.getGreen() * invRatio);
		int b = (int) (color2.getBlue() * ratio + color1.getBlue() * invRatio);

		return (r << 16) | (g << 8) | b;
	}
}