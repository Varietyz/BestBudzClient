
package com.bestbudz.dock.ui.panel.character;

public class AppearanceConfig {

	public static final int[][] MALE_VALUES = {
		{0, 8},
		{10, 17},
		{18, 25},
		{26, 31},
		{33, 34},
		{36, 40},
		{42, 43}
	};

	public static final int[][] FEMALE_VALUES = {
		{45, 54},
		{-1, -1},
		{56, 60},
		{61, 65},
		{67, 68},
		{70, 77},
		{79, 80}
	};

	public static final int[][] COLOR_RANGES = {
		{0, 71},
		{0, 15},
		{0, 15},
		{0, 65},
		{0, 67}
	};

	public static final class Defaults {
		public static final byte GENDER = 0;

		public static final byte MALE_HEAD = 1;
		public static final byte MALE_JAW = 17;
		public static final byte MALE_TORSO = 25;
		public static final byte MALE_ARMS = 26;
		public static final byte MALE_HANDS = 34;
		public static final byte MALE_LEGS = 37;
		public static final byte MALE_FEET = 42;

		public static final byte FEMALE_HEAD = 48;
		public static final byte FEMALE_JAW = -1;
		public static final byte FEMALE_TORSO = 57;
		public static final byte FEMALE_ARMS = 61;
		public static final byte FEMALE_HANDS = 68;
		public static final byte FEMALE_LEGS = 76;
		public static final byte FEMALE_FEET = 79;

		public static final byte HAIR_COLOR = 47;
		public static final byte TORSO_COLOR = 6;
		public static final byte LEGS_COLOR = 4;
		public static final byte FEET_COLOR = 60;
		public static final byte SKIN_COLOR = 62;
	}

	public static final class Parts {
		public static final int HEAD = 0;
		public static final int TORSO = 1;
		public static final int ARMS = 2;
		public static final int HANDS = 3;
		public static final int LEGS = 4;
		public static final int FEET = 5;
		public static final int JAW = 6;
	}

	public static final class Colors {
		public static final int HAIR = 0;
		public static final int TORSO = 1;
		public static final int LEGS = 2;
		public static final int FEET = 3;
		public static final int SKIN = 4;
	}

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

	public static int[] getPartRange(int partIndex, byte gender) {
		if (partIndex < 0 || partIndex >= MALE_VALUES.length) {
			return new int[]{0, 0};
		}

		if (gender == 0) {
			return MALE_VALUES[partIndex];
		} else {
			return FEMALE_VALUES[partIndex];
		}
	}

	public static int[] getColorRange(int colorIndex) {
		if (colorIndex < 0 || colorIndex >= COLOR_RANGES.length) {
			return new int[]{0, 0};
		}
		return COLOR_RANGES[colorIndex];
	}

	public static boolean isValidPartValue(int partIndex, int value, byte gender) {
		int[] range = getPartRange(partIndex, gender);
		if (range[0] == -1 && range[1] == -1) {
			return value == -1;
		}
		return value >= range[0] && value <= range[1];
	}

	public static boolean isValidColorValue(int colorIndex, int value) {
		int[] range = getColorRange(colorIndex);
		return value >= range[0] && value <= range[1];
	}

	public static int cycleValue(int current, int min, int max, int direction) {
		if (min == -1 && max == -1) return current;

		current += direction;
		if (current > max) current = min;
		if (current < min) current = max;
		return current;
	}
}
