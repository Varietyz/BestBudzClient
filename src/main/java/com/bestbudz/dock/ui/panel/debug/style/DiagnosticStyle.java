package com.bestbudz.dock.ui.panel.debug.style;

import com.bestbudz.engine.config.ColorConfig;
import java.awt.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class DiagnosticStyle {

	public static final Color BACKGROUND_DARK = ColorConfig.MAIN_FRAME_COLOR;
	public static final Color BACKGROUND_MEDIUM = new Color(31, 41, 55);
	public static final Color BACKGROUND_SECTION = ColorConfig.BG_COLOR;

	public static final Color TEXT_PRIMARY = new Color(249, 250, 251);
	public static final Color TEXT_SECONDARY = new Color(209, 213, 219);
	public static final Color TEXT_MUTED = new Color(163, 163, 163);

	public static final Color BORDER_DEFAULT = new Color(75, 85, 99);

	public static final Color STATUS_GOOD = new Color(34, 197, 94);
	public static final Color STATUS_WARNING = new Color(234, 179, 8);
	public static final Color STATUS_CRITICAL = new Color(239, 68, 68);

	public static final Color CATEGORY_HEADER = new Color(59, 130, 246);
	public static final Color CATEGORY_CACHE = new Color(245, 158, 11);
	public static final Color CATEGORY_NETWORK = new Color(14, 165, 233);
	public static final Color CATEGORY_PERFORMANCE = new Color(168, 85, 247);
	public static final Color CATEGORY_ENTITY = new Color(34, 197, 94);
	public static final Color CATEGORY_WORLD = new Color(251, 146, 60);
	public static final Color CATEGORY_SYSTEM = new Color(156, 163, 175);
	public static final Color CATEGORY_GPU = new Color(227, 64, 64);

	public static final Color BUTTON_PRIMARY = new Color(59, 130, 246);
	public static final Color BUTTON_PRIMARY_HOVER = new Color(37, 99, 235);

	public static final int BREAKPOINT_SM = 400;
	public static final int BREAKPOINT_MD = 650;
	public static final int BREAKPOINT_LG = 950;
	public static final int BREAKPOINT_XL = 1200;

	public static final int CONTENT_PADDING = 6;
	public static final int SECTION_GAP = 8;
	public static final int SECTION_INTERNAL_PADDING = 8;
	public static final int BUTTON_PADDING = 4;

	public static final int MIN_SECTION_WIDTH = 180;
	public static final int PREFERRED_SECTION_WIDTH = 250;
	public static final int MAX_SECTION_WIDTH = 320;
	public static final int SECTION_HEIGHT = 140;

	public static Font getHeaderFont(int containerWidth) {
		int baseFontSize = getBaseFontSize(containerWidth);
		return new Font("SansSerif", Font.BOLD, baseFontSize + 2);
	}

	public static Font getDataFont(int containerWidth) {
		int baseFontSize = getBaseFontSize(containerWidth);
		return new Font("SansSerif", Font.PLAIN, baseFontSize);
	}

	public static Font getBoldDataFont(int containerWidth) {
		int baseFontSize = getBaseFontSize(containerWidth);
		return new Font("SansSerif", Font.BOLD, baseFontSize);
	}

	private static int getBaseFontSize(int containerWidth) {
		if (containerWidth < BREAKPOINT_SM) return 10;
		if (containerWidth < BREAKPOINT_MD) return 11;
		if (containerWidth < BREAKPOINT_LG) return 12;
		return 12;
	}

	public static Border createSectionBorder(Color borderColor) {
		return BorderFactory.createCompoundBorder(
			new LineBorder(borderColor, 1),
			new EmptyBorder(SECTION_INTERNAL_PADDING, SECTION_INTERNAL_PADDING,
				SECTION_INTERNAL_PADDING, SECTION_INTERNAL_PADDING)
		);
	}

	public static Border createContentBorder() {
		return new EmptyBorder(CONTENT_PADDING, CONTENT_PADDING,
			CONTENT_PADDING, CONTENT_PADDING);
	}

	public static Border createButtonBorder() {
		return BorderFactory.createEmptyBorder(BUTTON_PADDING, 12, BUTTON_PADDING, 12);
	}

	public static Color getStatusColor(double percentage) {
		if (percentage > 90) return STATUS_CRITICAL;
		if (percentage > 75) return STATUS_WARNING;
		return STATUS_GOOD;
	}

	public static Color getFpsColor(int fps) {
		if (fps < 15) return STATUS_CRITICAL;
		if (fps >= 60) return STATUS_GOOD;
		return STATUS_WARNING;
	}

	public static Color getMemoryColor(long used, long max) {
		double usage = (double) used / max;
		return getStatusColor(usage * 100);
	}

	public static String formatBytes(long bytes) {
		if (bytes < 1024) return bytes + " B";
		if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
		if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
		return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
	}

	public static String formatNumber(int number) {
		if (number < 1000) return String.valueOf(number);
		if (number < 1000000) return String.format("%.1fK", number / 1000.0);
		return String.format("%.1fM", number / 1000000.0);
	}

	public static String formatTime(long milliseconds) {
		long seconds = milliseconds / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		long days = hours / 24;

		if (days > 0) return days + "d " + (hours % 24) + "h";
		if (hours > 0) return hours + "h " + (minutes % 60) + "m";
		if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
		return seconds + "s";
	}

	public static String truncateString(String str, int maxLength) {
		if (str == null || str.length() <= maxLength) return str != null ? str : "";
		return str.substring(0, maxLength - 3) + "...";
	}
}
