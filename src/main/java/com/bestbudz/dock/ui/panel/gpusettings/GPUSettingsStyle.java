package com.bestbudz.dock.ui.panel.gpusettings;

import java.awt.Color;
import java.awt.Font;

/**
 * Visual styling constants for the GPU Settings dock panel.
 * Dark theme matching the existing dock UI.
 */
public final class GPUSettingsStyle {

	private GPUSettingsStyle() {}

	// Panel background
	public static final Color PANEL_BG = new Color(30, 30, 35);
	public static final Color SECTION_BG = new Color(38, 38, 44);
	public static final Color SECTION_HEADER_BG = new Color(45, 45, 52);

	// Text colors
	public static final Color TEXT_PRIMARY = new Color(220, 220, 220);
	public static final Color TEXT_SECONDARY = new Color(160, 160, 170);
	public static final Color TEXT_ACCENT = new Color(130, 180, 255);

	// Slider styling
	public static final Color SLIDER_TRACK = new Color(55, 55, 62);
	public static final Color SLIDER_FILL = new Color(80, 150, 255);
	public static final Color SLIDER_THUMB = new Color(200, 200, 210);
	public static final int SLIDER_HEIGHT = 28;

	// Toggle styling
	public static final Color TOGGLE_OFF = new Color(60, 60, 65);
	public static final Color TOGGLE_ON = new Color(76, 175, 80);

	// Preset button styling
	public static final Color PRESET_BTN_BG = new Color(50, 50, 58);
	public static final Color PRESET_BTN_HOVER = new Color(65, 65, 75);
	public static final Color PRESET_BTN_ACTIVE = new Color(80, 150, 255);

	// Fonts
	public static final Font SECTION_HEADER_FONT = new Font("SansSerif", Font.BOLD, 12);
	public static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 11);
	public static final Font VALUE_FONT = new Font("SansSerif", Font.PLAIN, 10);
	public static final Font PRESET_FONT = new Font("SansSerif", Font.BOLD, 11);

	// Spacing
	public static final int SECTION_PADDING = 6;
	public static final int ROW_HEIGHT = 32;
	public static final int LABEL_WIDTH = 100;
	public static final int SECTION_GAP = 4;
}
