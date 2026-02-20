package com.bestbudz.dock.ui.panel.example;

import java.util.HashMap;
import java.util.Map;

public class ExampleConfig {

	public static final int PANEL_ACTIVATION_ID = 29000;

	public static final int REFRESH_ACTION_ID = 29001;

	public static final int PRIMARY_ACTION_ID = 29002;

	public static final int SECONDARY_ACTION_ID = 29003;

	public static final int SETTINGS_ACTION_ID = 29004;

	public static final int CLOSE_ACTION_ID = 29005;

	public static final int[] EXAMPLE_GROUP_IDS = {29010, 29011, 29012, 29013, 29014};

	public static final int MAX_DISPLAY_ROWS = 50;

	public static final int DEFAULT_GRID_COLUMNS = 4;

	public static final int MAX_ITEM_NAME_LENGTH = 25;

	public static final long REFRESH_INTERVAL = 5000;

	public static final int ANIMATION_DURATION = 300;

	public static final int SCROLL_SPEED = 16;

	public static final int MIN_VALUE = 0;

	public static final int MAX_VALUE = 999999;

	public static final int LOW_THRESHOLD = 100;

	public static final int MEDIUM_THRESHOLD = 1000;

	public static final int CRITICAL_THRESHOLD = 10;

	public static final int MAX_CACHE_SIZE = 1000;

	public static final boolean ENABLE_RAINBOW_HOVER = true;

	public static final boolean ENABLE_ANIMATIONS = true;

	public static final boolean ENABLE_TOOLTIPS = true;

	public static final boolean ENABLE_SOUNDS = false;

	public static final boolean SHOW_ERROR_DIALOGS = true;

	public static final boolean DEBUG_MODE = false;

	public static final boolean AUTO_REFRESH = true;

	public static final String DEFAULT_TITLE = "Example Panel";

	public static final String DEFAULT_FILTER = "";

	public static final boolean DEFAULT_SORT_ASCENDING = true;

	public static final int DEFAULT_PAGE_SIZE = 20;

	public static final String SPRITE_BASE_PATH = "sprites/example/";

	public static final String PANEL_ICON = SPRITE_BASE_PATH + "example_icon.png";

	public static final String REFRESH_ICON = SPRITE_BASE_PATH + "refresh.png";
	public static final String SETTINGS_ICON = SPRITE_BASE_PATH + "settings.png";
	public static final String CLOSE_ICON = SPRITE_BASE_PATH + "close.png";

	public static final String PANEL_VERSION = "1.0.0";

	public static final String PANEL_DESCRIPTION = "Example panel for demonstration purposes";

	public static final String LAST_UPDATED = "2025-06-06";

	private static final Map<String, Integer> ACTION_MAPPINGS = new HashMap<>();

	static {

		ACTION_MAPPINGS.put("refresh", REFRESH_ACTION_ID);
		ACTION_MAPPINGS.put("primary", PRIMARY_ACTION_ID);
		ACTION_MAPPINGS.put("secondary", SECONDARY_ACTION_ID);
		ACTION_MAPPINGS.put("settings", SETTINGS_ACTION_ID);
		ACTION_MAPPINGS.put("close", CLOSE_ACTION_ID);
	}

	public static int getActionInterfaceId(String actionName) {
		return ACTION_MAPPINGS.getOrDefault(actionName.toLowerCase(), -1);
	}

	public static boolean isValidValue(int value) {
		return value >= MIN_VALUE && value <= MAX_VALUE;
	}

	public static String getThresholdLevel(int value) {
		if (value <= CRITICAL_THRESHOLD) return "critical";
		if (value <= LOW_THRESHOLD) return "low";
		if (value <= MEDIUM_THRESHOLD) return "medium";
		return "high";
	}

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

	public static String getResourcePath(String resourceName) {
		return SPRITE_BASE_PATH + resourceName;
	}

	public static String getConfigInfo() {
		return String.format(
			"ExampleConfig read3BytesBE%s - Max Display: %d, Refresh: %dms, Features: [Rainbow: %s, Animations: %s, Debug: %s]",
			PANEL_VERSION, MAX_DISPLAY_ROWS, REFRESH_INTERVAL,
			ENABLE_RAINBOW_HOVER, ENABLE_ANIMATIONS, DEBUG_MODE
		);
	}
}
