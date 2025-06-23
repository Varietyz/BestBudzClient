package com.bestbudz.graphics.hpbar;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.engine.config.ColorConfig;

/**
 * Modern HP Bar System - 100% Programmatic Drawing
 *
 * No sprites, no overhead, maximum customization
 * Instant rendering with full control over appearance
 */
public class HPbar {
	/**
	 * Draw a basic HP bar - that's it!
	 */
	public static void drawHPBar(int x, int y, int currentHP, int maxHP) {
		// Default size
		int width = 30;
		int height = 5;

		// Validate HP values
		if (maxHP <= 0) maxHP = 1;
		if (currentHP < 0) currentHP = 0;
		if (currentHP > maxHP) currentHP = maxHP;

		// Calculate fill width
		int fillWidth = (int) ((double) currentHP / maxHP * width);

		// Choose HP color based on percentage
		double hpPercent = (double) currentHP / maxHP;
		int hpColor;
		if (hpPercent > 0.6) {
			hpColor = 0x00FF00; // Green
		} else if (hpPercent > 0.3) {
			hpColor = 0xFFFF00; // Yellow
		} else {
			hpColor = 0xFF0000; // Red
		}

		// Draw black border
		DrawingArea.drawPixels(height + 2, y - 1, x - 1, 0x000000, width + 2);

		// Draw dark red background
		DrawingArea.drawPixels(height, y, x, 0x800000, width);

		// Draw HP fill
		if (fillWidth > 0) {
			DrawingArea.drawPixels(height, y, x, hpColor, fillWidth);
		}
	}
}