package com.bestbudz.graphics.hpbar;

import com.bestbudz.engine.core.gamerender.DrawingArea;

public class HPbar {

	private static final int MIN_WIDTH = 30;
	private static final int MAX_WIDTH = 250;
	private static final int PIXELS_PER_10HP = 1;
	private static final int HEIGHT = 3;

	public static void drawHPBar(int x, int y, long currentHP, long maxHP) {
		if (maxHP == 0) return;

		if (maxHP <= 0) maxHP = 1;
		if (currentHP < 0) currentHP = 0;

		int width = calculateDynamicWidth(maxHP);

		double hpPercent = (double) currentHP / maxHP;
		int currentCycle = (int) Math.floor(hpPercent);

		drawHPBarBackground(x, y, width, currentCycle);
		drawHPBarFill(x, y, width, hpPercent);
	}

	private static int calculateDynamicWidth(long maxHP) {
		int calculatedWidth = (int) (maxHP / 10) * PIXELS_PER_10HP + MIN_WIDTH;
		return Math.min(Math.max(calculatedWidth, MIN_WIDTH), MAX_WIDTH);
	}

	private static void drawHPBarBackground(int x, int y, int width, int currentCycle) {
		int startX = x - (width / 2);

		DrawingArea.drawPixels(HEIGHT + 2, y - 1, startX - 1, 0x000000, width + 2);

		int backgroundColor;
		if (currentCycle == 0) {

			backgroundColor = 0x100000;
		} else {

			int previousCycleHealthy = getCycleHealthyColor(currentCycle - 1);
			backgroundColor = darkenColor(previousCycleHealthy, 0.4);
		}

		DrawingArea.drawPixels(HEIGHT, y, startX, backgroundColor, width);
	}

	private static void drawHPBarFill(int x, int y, int width, double hpPercent) {
		int startX = x - (width / 2);

		int currentCycle;
		double cycleProgress;

		if (hpPercent > 0 && hpPercent == Math.floor(hpPercent)) {

			currentCycle = (int) hpPercent - 1;
			cycleProgress = 1.0;
		} else {

			currentCycle = (int) Math.floor(hpPercent);
			cycleProgress = hpPercent - currentCycle;
		}

		int fillWidth = (int) (cycleProgress * width);

		if (fillWidth > 0) {

			int hpColor = getCycleDegradationColor(currentCycle, cycleProgress);
			DrawingArea.drawPixels(HEIGHT, y, startX, hpColor, fillWidth);
		}
	}

	private static int getCycleDegradationColor(int cycle, double progressInCycle) {

		if (cycle == 0 && progressInCycle == 0.0) {
			return 0xFF0000;
		}

		int healthyColor = getCycleHealthyColor(cycle);
		int midColor = getCycleMidColor(cycle);
		int criticalColor = getCycleCriticalColor(cycle);

		if (progressInCycle > 0.5) {

			double t = (1.0 - progressInCycle) / 0.5;
			return interpolateColor(healthyColor, midColor, t);
		} else {

			double t = (0.5 - progressInCycle) / 0.5;
			return interpolateColor(midColor, criticalColor, t);
		}
	}

	private static int getCycleHealthyColor(int cycle) {

		int[] healthyColors = {
			0x00FF00,
			0x0088FF,
			0x8800FF,
			0x00CCFF,
			0xFFCC00,
			0x4400FF,
			0x00FFFF,
			0xFF00CC,
			0xCCFF00,
			0x0044FF,
		};

		if (cycle < healthyColors.length) {
			return healthyColors[cycle];
		} else {

			return generateProceduralColor(cycle);
		}
	}

	private static int getCycleMidColor(int cycle) {

		int[] midColors = {
			0xFFFF00,
			0x4488FF,
			0xAA44FF,
			0x44CCFF,
			0xFFDD44,
			0x6644FF,
			0x44FFFF,
			0xFF44AA,
			0xDDFF44,
			0x4466FF,
		};

		if (cycle < midColors.length) {
			return midColors[cycle];
		} else {

			int healthyColor = generateProceduralColor(cycle);
			int criticalColor = darkenColor(healthyColor, 0.4);
			return interpolateColor(healthyColor, criticalColor, 0.5);
		}
	}

	private static int getCycleCriticalColor(int cycle) {

		int[] criticalColors = {
			0xFF0000,
			0x004488,
			0x440088,
			0x006688,
			0x886600,
			0x220044,
			0x008888,
			0x880044,
			0x668800,
			0x002244,
		};

		if (cycle < criticalColors.length) {
			return criticalColors[cycle];
		} else {

			int healthyColor = generateProceduralColor(cycle);
			return darkenColor(healthyColor, 0.6);
		}
	}

	private static int darkenColor(int color, double factor) {
		int r = (int)(((color >> 16) & 0xFF) * (1.0 - factor));
		int g = (int)(((color >> 8) & 0xFF) * (1.0 - factor));
		int b = (int)((color & 0xFF) * (1.0 - factor));
		return (r << 16) | (g << 8) | b;
	}

	private static int generateProceduralColor(int cycle) {

		float hue = ((cycle * 137.5f) % 360) / 360f;
		float saturation = 0.8f + (cycle % 3) * 0.1f;
		float brightness = 0.9f + (cycle % 2) * 0.1f;

		return hsbToRgb(hue, saturation, brightness);
	}

	private static int hsbToRgb(float hue, float saturation, float brightness) {
		int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
		return rgb & 0xFFFFFF;
	}

	public static int interpolateColor(int startColor, int endColor, double t) {

		t = Math.max(0, Math.min(1, t));

		int r1 = (startColor >> 16) & 0xFF;
		int g1 = (startColor >> 8) & 0xFF;
		int b1 = startColor & 0xFF;

		int r2 = (endColor >> 16) & 0xFF;
		int g2 = (endColor >> 8) & 0xFF;
		int b2 = endColor & 0xFF;

		int r = (int) (r1 + t * (r2 - r1));
		int g = (int) (g1 + t * (g2 - g1));
		int b = (int) (b1 + t * (b2 - b1));

		return (r << 16) | (g << 8) | b;
	}
}
