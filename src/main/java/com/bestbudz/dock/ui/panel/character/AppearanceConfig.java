// Fixed AppearanceConfig.java with correct color ranges

package com.bestbudz.dock.ui.panel.character;

/**
 * Configuration constants for the Appearance Panel system
 *
 * Contains all validation ranges, default values, and color mappings
 * that match the server-side ChangeAppearancePacket validation.
 */
public class AppearanceConfig {

	// Validation ranges from server (ChangeAppearancePacket.java)
	public static final int[][] MALE_VALUES = {
		{0, 8},   // head
		{10, 17}, // jaw
		{18, 25}, // torso
		{26, 31}, // arms
		{33, 34}, // hands
		{36, 40}, // legs
		{42, 43}  // feet
	};

	public static final int[][] FEMALE_VALUES = {
		{45, 54}, // head
		{-1, -1}, // jaw (none available)
		{56, 60}, // torso
		{61, 65}, // arms
		{67, 68}, // hands
		{70, 77}, // legs
		{79, 80}  // feet
	};

	// FIXED: Color ranges matching server's ALLOWED_COLORS exactly
	// Server calculates: {0, IDK_COLORS[i].length} for each color array
	public static final int[][] COLOR_RANGES = {
		{0, 71}, // hair colors (IDK_COLORS[0].length = 82)
		{0, 15}, // torso colors (IDK_COLORS[1].length = 82)
		{0, 15}, // legs colors (IDK_COLORS[2].length = 82)
		{0, 65}, // feet colors (IDK_COLORS[3].length = 66)
		{0, 67}  // skin colors (IDK_COLORS[4].length = 82)
	};

	// Default appearance values (from server setToDefault method)
	public static final class Defaults {
		public static final byte GENDER = 0; // Male

		// Male defaults (server default values)
		public static final byte MALE_HEAD = 1;
		public static final byte MALE_JAW = 17;
		public static final byte MALE_TORSO = 25;
		public static final byte MALE_ARMS = 26;
		public static final byte MALE_HANDS = 34;
		public static final byte MALE_LEGS = 37;
		public static final byte MALE_FEET = 42;

		// Female defaults (using minimum valid values)
		public static final byte FEMALE_HEAD = 48;
		public static final byte FEMALE_JAW = -1; // No jaw for female
		public static final byte FEMALE_TORSO = 57;
		public static final byte FEMALE_ARMS = 61;
		public static final byte FEMALE_HANDS = 68;
		public static final byte FEMALE_LEGS = 76;
		public static final byte FEMALE_FEET = 79;

		// Default colors (start at 0 for all)
		public static final byte HAIR_COLOR = 47;
		public static final byte TORSO_COLOR = 6;
		public static final byte LEGS_COLOR = 4;
		public static final byte FEET_COLOR = 60;
		public static final byte SKIN_COLOR = 62;
	}

	// Appearance part indices (matches server app[] array)
	public static final class Parts {
		public static final int HEAD = 0;   // app[0]
		public static final int TORSO = 1;  // app[1]
		public static final int ARMS = 2;   // app[2]
		public static final int HANDS = 3;  // app[3]
		public static final int LEGS = 4;   // app[4]
		public static final int FEET = 5;   // app[5]
		public static final int JAW = 6;    // app[6]
	}

	// Color indices (matches server col[] array)
	public static final class Colors {
		public static final int HAIR = 0;   // col[0]
		public static final int TORSO = 1;  // col[1]
		public static final int LEGS = 2;   // col[2]
		public static final int FEET = 3;   // col[3]
		public static final int SKIN = 4;   // col[4]
	}

	// UI Constants
	public static final class UI {
		public static final String MALE_LABEL = "Stoner";
		public static final String FEMALE_LABEL = "Stonerette";

		public static final String[] PART_NAMES = {
			"Head", "Jaw", "Torso", "Arms", "Hands", "Legs", "Feet"
		};

		public static final String[] COLOR_NAMES = {
			"Hair", "Torso", "Legs", "Feet", "Skin"
		};

		public static final String DESIGN_SECTION = "Design";
		public static final String COLOR_SECTION = "Colour";
		public static final String GENDER_SECTION = "Gender";
	}

	/**
	 * Gets the validation range for a specific appearance part and gender
	 */
	public static int[] getPartRange(int partIndex, byte gender) {
		if (partIndex < 0 || partIndex >= MALE_VALUES.length) {
			return new int[]{0, 0}; // Invalid range
		}

		if (gender == 0) { // Male
			return MALE_VALUES[partIndex];
		} else { // Female
			return FEMALE_VALUES[partIndex];
		}
	}

	/**
	 * Gets the color range for a specific color type
	 */
	public static int[] getColorRange(int colorIndex) {
		if (colorIndex < 0 || colorIndex >= COLOR_RANGES.length) {
			return new int[]{0, 0}; // Invalid range
		}
		return COLOR_RANGES[colorIndex];
	}

	/**
	 * Validates if a value is within the allowed range for a part/gender combination
	 */
	public static boolean isValidPartValue(int partIndex, int value, byte gender) {
		int[] range = getPartRange(partIndex, gender);
		if (range[0] == -1 && range[1] == -1) {
			return value == -1; // No options available (like female jaw)
		}
		return value >= range[0] && value <= range[1];
	}

	/**
	 * Validates if a color value is within the allowed range
	 */
	public static boolean isValidColorValue(int colorIndex, int value) {
		int[] range = getColorRange(colorIndex);
		return value >= range[0] && value <= range[1];
	}

	/**
	 * Cycles a value within its valid range
	 */
	public static int cycleValue(int current, int min, int max, int direction) {
		if (min == -1 && max == -1) return current; // No options available

		current += direction;
		if (current > max) current = min;
		if (current < min) current = max;
		return current;
	}
}