package com.bestbudz.dock.ui.panel.example;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration constants and settings for the Example Panel
 *
 * PURPOSE:
 * - Centralized configuration management
 * - Constants for interface IDs, limits, and settings
 * - Easy modification of panel behavior
 * - Version and feature flags
 * - Default values and thresholds
 *
 * ORGANIZATION:
 * - Interface IDs: Server communication identifiers
 * - Display Settings: UI behavior and limits
 * - Data Thresholds: Business logic boundaries
 * - Feature Flags: Enable/disable functionality
 * - Default Values: Fallback settings
 */
public class ExampleConfig {

	// ==========================================
	// INTERFACE IDs (for server communication)
	// ==========================================

	/** Main panel activation interface ID */
	public static final int PANEL_ACTIVATION_ID = 29000;

	/** Refresh/reload data interface ID */
	public static final int REFRESH_ACTION_ID = 29001;

	/** Primary action button interface ID */
	public static final int PRIMARY_ACTION_ID = 29002;

	/** Secondary action button interface ID */
	public static final int SECONDARY_ACTION_ID = 29003;

	/** Settings/config interface ID */
	public static final int SETTINGS_ACTION_ID = 29004;

	/** Close/exit panel interface ID */
	public static final int CLOSE_ACTION_ID = 29005;

	// Example of grouped interface IDs for related actions
	public static final int[] EXAMPLE_GROUP_IDS = {29010, 29011, 29012, 29013, 29014};

	// ==========================================
	// DISPLAY SETTINGS
	// ==========================================

	/** Maximum number of items to display in lists */
	public static final int MAX_DISPLAY_ROWS = 50;

	/** Default number of columns for grid layouts */
	public static final int DEFAULT_GRID_COLUMNS = 4;

	/** Maximum characters for item names before truncation */
	public static final int MAX_ITEM_NAME_LENGTH = 25;

	/** Refresh interval in milliseconds */
	public static final long REFRESH_INTERVAL = 5000; // 5 seconds

	/** Animation duration in milliseconds */
	public static final int ANIMATION_DURATION = 300;

	/** Scroll speed for lists */
	public static final int SCROLL_SPEED = 16;

	// ==========================================
	// DATA THRESHOLDS AND LIMITS
	// ==========================================

	/** Minimum allowed value for inputs */
	public static final int MIN_VALUE = 0;

	/** Maximum allowed value for inputs */
	public static final int MAX_VALUE = 999999;

	/** Threshold for "low" values (affects color coding) */
	public static final int LOW_THRESHOLD = 100;

	/** Threshold for "medium" values (affects color coding) */
	public static final int MEDIUM_THRESHOLD = 1000;

	/** Critical threshold (affects warnings) */
	public static final int CRITICAL_THRESHOLD = 10;

	/** Maximum cache size for data storage */
	public static final int MAX_CACHE_SIZE = 1000;

	// ==========================================
	// FEATURE FLAGS
	// ==========================================

	/** Enable/disable rainbow hover effects */
	public static final boolean ENABLE_RAINBOW_HOVER = true;

	/** Enable/disable animations */
	public static final boolean ENABLE_ANIMATIONS = true;

	/** Enable/disable tooltips */
	public static final boolean ENABLE_TOOLTIPS = true;

	/** Enable/disable sound effects */
	public static final boolean ENABLE_SOUNDS = false;

	/** Enable/disable error dialog popups */
	public static final boolean SHOW_ERROR_DIALOGS = true;

	/** Enable/disable debug logging */
	public static final boolean DEBUG_MODE = false;

	/** Enable/disable auto-refresh */
	public static final boolean AUTO_REFRESH = true;

	// ==========================================
	// DEFAULT VALUES
	// ==========================================

	/** Default panel title */
	public static final String DEFAULT_TITLE = "Example Panel";

	/** Default filter text */
	public static final String DEFAULT_FILTER = "";

	/** Default sort order (true = ascending) */
	public static final boolean DEFAULT_SORT_ASCENDING = true;

	/** Default page size for pagination */
	public static final int DEFAULT_PAGE_SIZE = 20;

	// ==========================================
	// RESOURCE PATHS
	// ==========================================

	/** Base path for panel sprites */
	public static final String SPRITE_BASE_PATH = "sprites/example/";

	/** Panel icon path */
	public static final String PANEL_ICON = SPRITE_BASE_PATH + "example_icon.png";

	/** Action button icons */
	public static final String REFRESH_ICON = SPRITE_BASE_PATH + "refresh.png";
	public static final String SETTINGS_ICON = SPRITE_BASE_PATH + "settings.png";
	public static final String CLOSE_ICON = SPRITE_BASE_PATH + "close.png";

	// ==========================================
	// VERSION AND INFO
	// ==========================================

	/** Panel version */
	public static final String PANEL_VERSION = "1.0.0";

	/** Panel description */
	public static final String PANEL_DESCRIPTION = "Example panel for demonstration purposes";

	/** Last updated date */
	public static final String LAST_UPDATED = "2025-06-06";

	// ==========================================
	// DYNAMIC MAPPINGS
	// ==========================================

	/** Map of action names to interface IDs */
	private static final Map<String, Integer> ACTION_MAPPINGS = new HashMap<>();

	static {
		// Initialize action mappings
		ACTION_MAPPINGS.put("refresh", REFRESH_ACTION_ID);
		ACTION_MAPPINGS.put("primary", PRIMARY_ACTION_ID);
		ACTION_MAPPINGS.put("secondary", SECONDARY_ACTION_ID);
		ACTION_MAPPINGS.put("settings", SETTINGS_ACTION_ID);
		ACTION_MAPPINGS.put("close", CLOSE_ACTION_ID);
	}

	// ==========================================
	// UTILITY METHODS
	// ==========================================

	/**
	 * Get interface ID for a named action
	 * @param actionName Name of the action
	 * @return Interface ID or -1 if not found
	 */
	public static int getActionInterfaceId(String actionName) {
		return ACTION_MAPPINGS.getOrDefault(actionName.toLowerCase(), -1);
	}

	/**
	 * Check if a value is within valid range
	 * @param value Value to check
	 * @return true if valid, false otherwise
	 */
	public static boolean isValidValue(int value) {
		return value >= MIN_VALUE && value <= MAX_VALUE;
	}

	/**
	 * Get the appropriate threshold level for a value
	 * @param value Value to categorize
	 * @return "low", "medium", "high", or "critical"
	 */
	public static String getThresholdLevel(int value) {
		if (value <= CRITICAL_THRESHOLD) return "critical";
		if (value <= LOW_THRESHOLD) return "low";
		if (value <= MEDIUM_THRESHOLD) return "medium";
		return "high";
	}

	/**
	 * Check if a feature is enabled
	 * @param featureName Name of the feature
	 * @return true if enabled, false otherwise
	 */
	public static boolean isFeatureEnabled(String featureName) {
		switch (featureName.toLowerCase()) {
			case "rainbow":
			case "rainbow_hover":
				return ENABLE_RAINBOW_HOVER;
			case "animations":
				return ENABLE_ANIMATIONS;
			case "tooltips":
				return ENABLE_TOOLTIPS;
			case "sounds":
				return ENABLE_SOUNDS;
			case "debug":
				return DEBUG_MODE;
			case "auto_refresh":
				return AUTO_REFRESH;
			default:
				return false;
		}
	}

	/**
	 * Get resource path with base path prepended
	 * @param resourceName Name of the resource file
	 * @return Full path to the resource
	 */
	public static String getResourcePath(String resourceName) {
		return SPRITE_BASE_PATH + resourceName;
	}

	/**
	 * Format configuration info for debugging
	 * @return String containing key configuration values
	 */
	public static String getConfigInfo() {
		return String.format(
			"ExampleConfig read3BytesBE%s - Max Display: %d, Refresh: %dms, Features: [Rainbow: %s, Animations: %s, Debug: %s]",
			PANEL_VERSION, MAX_DISPLAY_ROWS, REFRESH_INTERVAL,
			ENABLE_RAINBOW_HOVER, ENABLE_ANIMATIONS, DEBUG_MODE
		);
	}
}