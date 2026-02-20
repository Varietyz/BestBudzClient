package com.bestbudz.engine.core.error;

import java.awt.Color;

public class ErrorEnums {

	public static final Color BACKGROUND = new Color(18, 18, 20);
	public static final Color CARD_BACKGROUND = new Color(28, 28, 32);
	public static final Color PRIMARY_TEXT = new Color(220, 220, 220);
	public static final Color SECONDARY_TEXT = new Color(160, 160, 160);
	public static final Color ACCENT_COLOR = new Color(0, 122, 255);
	public static final Color SUCCESS_COLOR = new Color(52, 199, 89);
	public static final Color WARNING_COLOR = new Color(255, 159, 10);
	public static final Color ERROR_COLOR = new Color(255, 69, 58);
	public static final Color PANEL_COLOR = new Color(28, 28, 32, 240);

	public enum ErrorType {
		LOADING_ERROR("Loading Error", "Game failed to load properly",
			new String[]{"Corrupted cache files", "Network connectivity issues", "Insufficient system resources", "Java compatibility problems"}),
		GENERIC_ERROR("Generic Error", "Unable to load game",
			new String[]{"Unknown system error", "Try restarting the application", "Check system requirements", "Contact support if issue persists"}),
		ALREADY_LOADED("Instance Error", "Game already running",
			new String[]{"Close all browser windows", "End any Java processes", "Restart your computer if needed", "Check task manager for running instances"}),
		CACHE_ERROR("Cache Error", "Cache files corrupted or missing",
			new String[]{"Cache files may be corrupted", "Insufficient disk space", "File permission issues", "Use 'Clear Cache' to resolve"}),
		GRAPHICS_ERROR("Graphics Error", "Failed to initialize graphics",
			new String[]{"Update graphics drivers", "Check DirectX/OpenGL support", "Reduce screen resolution", "Disable hardware acceleration"}),
		NETWORK_ERROR("Network Error", "Connection or download failed",
			new String[]{"Check internet connection", "Verify firewall settings", "Try different network/VPN", "Restart router/modem"}),
		MEMORY_ERROR("Memory Error", "Insufficient memory available",
			new String[]{"Close other applications", "Increase Java heap size", "Free up system memory", "Restart computer"}),
		UNKNOWN("Unknown Error", "An unexpected error occurred",
			new String[]{"Try restarting the application", "Clear cache if problem persists", "Check system requirements", "Report issue to support"});

		public final String title;
		public final String description;
		public final String[] details;

		ErrorType(String title, String description, String[] details) {
			this.title = title;
			this.description = description;
			this.details = details;
		}
	}

	public enum ConsoleLevel {
		INFO("INFO", PRIMARY_TEXT, "ℹ️"),
		SUCCESS("SUCCESS", SUCCESS_COLOR, "✅"),
		WARNING("WARN", WARNING_COLOR, "⚠️"),
		ERROR("ERROR", ERROR_COLOR, "❌"),
		DEBUG("DEBUG", SECONDARY_TEXT, "🔍");

		public final String prefix;
		public final Color color;
		public final String emoji;

		ConsoleLevel(String prefix, Color color, String emoji) {
			this.prefix = prefix;
			this.color = color;
			this.emoji = emoji;
		}
	}

	public static class ConsoleEntry {
		public final long timestamp;
		public final String message;
		public final ConsoleLevel level;

		public ConsoleEntry(String message, ConsoleLevel level) {
			this.timestamp = System.currentTimeMillis();
			this.message = message;
			this.level = level;
		}
	}

	public enum ActionType {
		TOGGLE_CONSOLE("Toggle Console", ACCENT_COLOR),
		CLEAR_CACHE("Clear Cache", WARNING_COLOR),
		DELETE_CACHE("Delete Cache Files", ERROR_COLOR),
		COPY_LOGS("Copy Logs", new Color(100, 100, 180)),
		RESTART("Restart", new Color(0, 150, 0)),
		EXIT("Exit", ERROR_COLOR),
		SCROLL_UP("▲", ACCENT_COLOR),
		SCROLL_DOWN("▼", ACCENT_COLOR);

		public final String label;
		public final Color color;

		ActionType(String label, Color color) {
			this.label = label;
			this.color = color;
		}
	}

	public static class Rect {
		public final int x, y, w, h;

		public Rect(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public boolean contains(int px, int py) {
			return px >= x && px <= x + w && py >= y && py <= y + h;
		}
	}
}
