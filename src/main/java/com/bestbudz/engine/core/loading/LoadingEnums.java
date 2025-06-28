package com.bestbudz.engine.core.loading;

import java.awt.Color;

/**
 * Enums and constants for the loading system
 */
public class LoadingEnums {

	// Visual constants
	public static final Color BACKGROUND = new Color(18, 18, 20);
	public static final Color CARD_BACKGROUND = new Color(28, 28, 32);
	public static final Color PRIMARY_TEXT = new Color(220, 220, 220);
	public static final Color SECONDARY_TEXT = new Color(160, 160, 160);
	public static final Color ACCENT_COLOR = new Color(0, 122, 255);
	public static final Color SUCCESS_COLOR = new Color(52, 199, 89);
	public static final Color WARNING_COLOR = new Color(255, 159, 10);
	public static final Color ERROR_COLOR = new Color(255, 69, 58);

	/**
	 * Loading phases with progress ranges
	 */
	public enum LoadingPhase {
		INITIALIZING("Getting Ready", 0, 10),
		LOADING_ASSETS("Loading Game Assets", 10, 40),
		PROCESSING_DATA("Processing Game Data", 40, 70),
		FINALIZING("Almost Ready", 70, 95),
		COMPLETE("Ready to Play!", 95, 100);

		public final String displayName;
		public final int startProgress;
		public final int endProgress;

		LoadingPhase(String displayName, int startProgress, int endProgress) {
			this.displayName = displayName;
			this.startProgress = startProgress;
			this.endProgress = endProgress;
		}
	}

	/**
	 * Log levels with visual styling
	 */
	public enum LogLevel {
		INFO("INFO", PRIMARY_TEXT, "ℹ️"),
		SUCCESS("SUCCESS", SUCCESS_COLOR, "✅"),
		WARNING("WARN", WARNING_COLOR, "⚠️"),
		ERROR("ERROR", ERROR_COLOR, "❌");

		public final String prefix;
		public final Color color;
		public final String emoji;

		LogLevel(String prefix, Color color, String emoji) {
			this.prefix = prefix;
			this.color = color;
			this.emoji = emoji;
		}
	}

	/**
	 * Simple log entry class
	 */
	public static class LogEntry {
		public final long timestamp;
		public final String message;
		public final LogLevel level;

		public LogEntry(String message, LogLevel level) {
			this.timestamp = System.currentTimeMillis();
			this.message = message;
			this.level = level;
		}
	}
}