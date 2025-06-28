package com.bestbudz.dock.ui.panel.client.bubblebudz.ui;

import java.awt.*;

public class BubbleBudzStyle {
	// Enhanced Neon Colors for better readability
	public static final Color BG_OVERLAY = new Color(5, 5, 15, 220);
	public static final Color PANEL_BG = new Color(8, 8, 20, 200);
	public static final Color TEXT_NEON_CYAN = new Color(0, 255, 255);
	public static final Color TEXT_NEON_PINK = new Color(255, 100, 200);
	public static final Color TEXT_NEON_VIOLET = new Color(200, 100, 255);
	public static final Color TEXT_NEON_GREEN = new Color(100, 255, 100);
	public static final Color TEXT_PRIMARY = new Color(255, 255, 255);
	public static final Color TEXT_SECONDARY = new Color(200, 200, 220);
	public static final Color SCORE_GLOW = new Color(255, 255, 0);

	// Enhanced Fonts
	public static final Font FONT_BRAND = new Font("SansSerif", Font.BOLD, 14);
	public static final Font FONT_SCORE = new Font("SansSerif", Font.BOLD, 20);
	public static final Font FONT_TIMER = new Font("SansSerif", Font.BOLD, 16);
	public static final Font FONT_BEST = new Font("SansSerif", Font.BOLD, 12);
	public static final Font FONT_INSTRUCTION = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font FONT_LOADING = new Font("SansSerif", Font.BOLD, 14);

	public static Color[] getNeonColors() {
		return new Color[]{TEXT_NEON_CYAN, TEXT_NEON_PINK, TEXT_NEON_VIOLET, TEXT_NEON_GREEN};
	}
}