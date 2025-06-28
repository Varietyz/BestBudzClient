package com.bestbudz.graphics.hpbar;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.engine.config.ColorConfig;

/**
 * Enhanced HP Bar System - Dynamic Scaling & Color Cycling
 *
 * Features:
 * - Dynamic width scaling based on max HP
 * - Color cycling system for infinite HP ranges
 * - Clean, minimal design
 */
public class HPbar {

	// Configuration constants
	private static final int MIN_WIDTH = 30;
	private static final int MAX_WIDTH = 250;
	private static final int PIXELS_PER_10HP = 1;
	private static final int HEIGHT = 3;

	/**
	 * Draw HP bar - clean and simple
	 */
	public static void drawHPBar(int x, int y, long currentHP, long maxHP) {
		if (maxHP == 0) return;
		// Validate HP values
		if (maxHP <= 0) maxHP = 1;
		if (currentHP < 0) currentHP = 0;

		// Calculate dynamic width based on max HP
		int width = calculateDynamicWidth(maxHP);

		// Calculate HP percentage (can exceed 100% for overbuffs)
		double hpPercent = (double) currentHP / maxHP;
		int currentCycle = (int) Math.floor(hpPercent);

		// Draw the HP bar
		drawHPBarBackground(x, y, width, currentCycle);
		drawHPBarFill(x, y, width, hpPercent);
	}

	/**
	 * Calculate dynamic width based on max HP
	 */
	private static int calculateDynamicWidth(long maxHP) {
		int calculatedWidth = (int) (maxHP / 10) * PIXELS_PER_10HP + MIN_WIDTH;
		return Math.min(Math.max(calculatedWidth, MIN_WIDTH), MAX_WIDTH);
	}

	/**
	 * Draw the background and border of the HP bar
	 */
	private static void drawHPBarBackground(int x, int y, int width, int currentCycle) {
		int startX = x - (width / 2);

		// Draw black border
		DrawingArea.drawPixels(HEIGHT + 2, y - 1, startX - 1, 0x000000, width + 2);

		int backgroundColor;
		if (currentCycle == 0) {
			// First cycle uses dark red background (original)
			backgroundColor = 0x100000;
		} else {
			// Overbuff cycles use darkened version of PREVIOUS cycle's healthy color
			int previousCycleHealthy = getCycleHealthyColor(currentCycle - 1);
			backgroundColor = darkenColor(previousCycleHealthy, 0.4);
		}

		// Draw background
		DrawingArea.drawPixels(HEIGHT, y, startX, backgroundColor, width);
	}

	/**
	 * Draw the HP fill with cycling colors and visual degradation
	 */
	private static void drawHPBarFill(int x, int y, int width, double hpPercent) {
		int startX = x - (width / 2);

		// Calculate which 100% range we're in and progress within that range
		int currentCycle;
		double cycleProgress;

		if (hpPercent > 0 && hpPercent == Math.floor(hpPercent)) {
			// Exact boundary case (100%, 200%, 300%, etc.)
			currentCycle = (int) hpPercent - 1; // Stay in previous cycle
			cycleProgress = 1.0; // At 100% of that cycle
		} else {
			// Normal case
			currentCycle = (int) Math.floor(hpPercent);
			cycleProgress = hpPercent - currentCycle; // 0.0 to 1.0 within current cycle
		}

		// FIXED: Fill width based on CURRENT CYCLE progress, not total HP!
		int fillWidth = (int) (cycleProgress * width);

		if (fillWidth > 0) {
			// Get the degradation color for current cycle and progress
			int hpColor = getCycleDegradationColor(currentCycle, cycleProgress);
			DrawingArea.drawPixels(HEIGHT, y, startX, hpColor, fillWidth);
		}
	}

	/**
	 * Get degradation color for a specific cycle and progress within that cycle
	 * Each cycle has its own healthy->critical color progression
	 */
	private static int getCycleDegradationColor(int cycle, double progressInCycle) {
		// Special case: Only show red at exactly 0% HP
		if (cycle == 0 && progressInCycle == 0.0) {
			return 0xFF0000; // Red only at true 0% HP
		}

		// Get the three colors for this cycle
		int healthyColor = getCycleHealthyColor(cycle);   // Color at 100%
		int midColor = getCycleMidColor(cycle);          // Color at ~50%
		int criticalColor = getCycleCriticalColor(cycle); // Color at ~0%

		// Three-stage interpolation for better visual feedback
		if (progressInCycle > 0.5) {
			// Upper half: 100% -> 50% (healthy -> mid)
			double t = (1.0 - progressInCycle) / 0.5; // 0 at 100%, 1 at 50%
			return interpolateColor(healthyColor, midColor, t);
		} else {
			// Lower half: 50% -> 0% (mid -> critical)
			double t = (0.5 - progressInCycle) / 0.5; // 0 at 50%, 1 at 0%
			return interpolateColor(midColor, criticalColor, t);
		}
	}

	/**
	 * Get "healthy" color for each cycle (what shows at 100% of that cycle)
	 */
	private static int getCycleHealthyColor(int cycle) {
		// Healthy colors for each cycle - NO green or red past cycle 0
		int[] healthyColors = {
			0x00FF00, // Cycle 0: Green (normal - ONLY cycle with green)
			0x0088FF, // Cycle 1: Bright Blue
			0x8800FF, // Cycle 2: Purple
			0x00CCFF, // Cycle 3: Cyan
			0xFFCC00, // Cycle 4: Gold/Yellow
			0x4400FF, // Cycle 5: Deep Purple
			0x00FFFF, // Cycle 6: Pure Cyan
			0xFF00CC, // Cycle 7: Magenta
			0xCCFF00, // Cycle 8: Lime Yellow
			0x0044FF, // Cycle 9: Deep Blue
		};

		if (cycle < healthyColors.length) {
			return healthyColors[cycle];
		} else {
			// Generate procedural healthy color
			return generateProceduralColor(cycle);
		}
	}

	private static int getCycleMidColor(int cycle) {
		// Warning colors for each cycle - NO green or red tones
		int[] midColors = {
			0xFFFF00, // Cycle 0: Yellow (classic warning)
			0x4488FF, // Cycle 1: Light Blue
			0xAA44FF, // Cycle 2: Light Purple
			0x44CCFF, // Cycle 3: Light Cyan
			0xFFDD44, // Cycle 4: Light Gold
			0x6644FF, // Cycle 5: Medium Purple
			0x44FFFF, // Cycle 6: Light Cyan
			0xFF44AA, // Cycle 7: Light Magenta
			0xDDFF44, // Cycle 8: Light Lime Yellow
			0x4466FF, // Cycle 9: Medium Blue
		};

		if (cycle < midColors.length) {
			return midColors[cycle];
		} else {
			// Generate procedural mid color
			int healthyColor = generateProceduralColor(cycle);
			int criticalColor = darkenColor(healthyColor, 0.4);
			return interpolateColor(healthyColor, criticalColor, 0.5);
		}
	}

	/**
	 * Get "critical" color for each cycle (what shows at 0% of that cycle)
	 */
	private static int getCycleCriticalColor(int cycle) {
		// Critical colors for each cycle - NO red or green except cycle 0
		int[] criticalColors = {
			0xFF0000, // Cycle 0: RED (only cycle allowed red)
			0x004488, // Cycle 1: Dark Blue
			0x440088, // Cycle 2: Dark Purple
			0x006688, // Cycle 3: Dark Cyan
			0x886600, // Cycle 4: Dark Gold/Brown
			0x220044, // Cycle 5: Very Dark Purple
			0x008888, // Cycle 6: Dark Cyan
			0x880044, // Cycle 7: Dark Magenta
			0x668800, // Cycle 8: Dark Yellow (no green)
			0x002244, // Cycle 9: Very Dark Blue
		};

		if (cycle < criticalColors.length) {
			return criticalColors[cycle];
		} else {
			// Generate procedural critical color (darker version, never red or green)
			int healthyColor = generateProceduralColor(cycle);
			return darkenColor(healthyColor, 0.6);
		}
	}

	/**
	 * Darken a color by a factor
	 */
	private static int darkenColor(int color, double factor) {
		int r = (int)(((color >> 16) & 0xFF) * (1.0 - factor));
		int g = (int)(((color >> 8) & 0xFF) * (1.0 - factor));
		int b = (int)((color & 0xFF) * (1.0 - factor));
		return (r << 16) | (g << 8) | b;
	}

	/**
	 * Generate procedural colors for extreme HP values (1000%+)
	 */
	private static int generateProceduralColor(int cycle) {
		// Use cycle number to generate deterministic but varied colors
		float hue = ((cycle * 137.5f) % 360) / 360f; // Golden angle distribution
		float saturation = 0.8f + (cycle % 3) * 0.1f; // Vary saturation slightly
		float brightness = 0.9f + (cycle % 2) * 0.1f; // Vary brightness slightly

		return hsbToRgb(hue, saturation, brightness);
	}

	/**
	 * Convert HSB to RGB
	 */
	private static int hsbToRgb(float hue, float saturation, float brightness) {
		int rgb = java.awt.Color.HSBtoRGB(hue, saturation, brightness);
		return rgb & 0xFFFFFF; // Remove alpha channel
	}

	/**
	 * Color interpolation helper
	 */
	public static int interpolateColor(int startColor, int endColor, double t) {
		// Clamp t to [0, 1]
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