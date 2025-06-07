// AppearanceUtils.java - Helper utilities for appearance management

package com.bestbudz.dock.ui.panel.character;

import com.bestbudz.engine.core.Client;

/**
 * Utility class for appearance-related operations
 *
 * Provides helper methods for managing appearance data and validation.
 */
public class AppearanceUtils {

	/**
	 * Validates an appearance configuration against server rules
	 */
	public static boolean validateAppearance(AppearanceStorage.AppearanceData appearance) {
		// Validate gender
		if (appearance.gender < 0 || appearance.gender > 1) {
			System.err.println("Invalid gender: " + appearance.gender);
			return false;
		}

		// Validate parts for current gender
		int[] headRange = AppearanceConfig.getPartRange(0, appearance.gender);
		if (!isInRange(appearance.head, headRange)) {
			System.err.println("Invalid head value: " + appearance.head + " for gender " + appearance.gender);
			return false;
		}

		int[] jawRange = AppearanceConfig.getPartRange(1, appearance.gender);
		if (!isInRange(appearance.jaw, jawRange)) {
			System.err.println("Invalid jaw value: " + appearance.jaw + " for gender " + appearance.gender);
			return false;
		}

		int[] torsoRange = AppearanceConfig.getPartRange(2, appearance.gender);
		if (!isInRange(appearance.torso, torsoRange)) {
			System.err.println("Invalid torso value: " + appearance.torso + " for gender " + appearance.gender);
			return false;
		}

		int[] armsRange = AppearanceConfig.getPartRange(3, appearance.gender);
		if (!isInRange(appearance.arms, armsRange)) {
			System.err.println("Invalid arms value: " + appearance.arms + " for gender " + appearance.gender);
			return false;
		}

		int[] handsRange = AppearanceConfig.getPartRange(4, appearance.gender);
		if (!isInRange(appearance.hands, handsRange)) {
			System.err.println("Invalid hands value: " + appearance.hands + " for gender " + appearance.gender);
			return false;
		}

		int[] legsRange = AppearanceConfig.getPartRange(5, appearance.gender);
		if (!isInRange(appearance.legs, legsRange)) {
			System.err.println("Invalid legs value: " + appearance.legs + " for gender " + appearance.gender);
			return false;
		}

		int[] feetRange = AppearanceConfig.getPartRange(6, appearance.gender);
		if (!isInRange(appearance.feet, feetRange)) {
			System.err.println("Invalid feet value: " + appearance.feet + " for gender " + appearance.gender);
			return false;
		}

		// Validate colors
		for (int i = 0; i < 5; i++) {
			int[] colorRange = AppearanceConfig.getColorRange(i);
			byte colorValue = getColorValue(appearance, i);
			if (!isInRange(colorValue, colorRange)) {
				System.err.println("Invalid color " + i + " value: " + colorValue);
				return false;
			}
		}

		return true;
	}

	/**
	 * Fixes invalid appearance values by setting them to valid defaults
	 */
	public static AppearanceStorage.AppearanceData fixInvalidAppearance(AppearanceStorage.AppearanceData appearance) {
		System.out.println("Fixing invalid appearance data...");

		// Fix gender first
		if (appearance.gender < 0 || appearance.gender > 1) {
			appearance.gender = AppearanceConfig.Defaults.GENDER;
		}

		// Fix parts based on gender
		appearance.head = fixPartValue(appearance.head, 0, appearance.gender);
		appearance.jaw = fixPartValue(appearance.jaw, 1, appearance.gender);
		appearance.torso = fixPartValue(appearance.torso, 2, appearance.gender);
		appearance.arms = fixPartValue(appearance.arms, 3, appearance.gender);
		appearance.hands = fixPartValue(appearance.hands, 4, appearance.gender);
		appearance.legs = fixPartValue(appearance.legs, 5, appearance.gender);
		appearance.feet = fixPartValue(appearance.feet, 6, appearance.gender);

		// Fix colors
		appearance.hairColor = fixColorValue(appearance.hairColor, 0);
		appearance.torsoColor = fixColorValue(appearance.torsoColor, 1);
		appearance.legsColor = fixColorValue(appearance.legsColor, 2);
		appearance.feetColor = fixColorValue(appearance.feetColor, 3);
		appearance.skinColor = fixColorValue(appearance.skinColor, 4);

		System.out.println("Fixed appearance data: " + appearance);
		return appearance;
	}

	/**
	 * Creates a clean default appearance for the specified gender
	 */
	public static AppearanceStorage.AppearanceData createDefaultAppearance(byte gender) {
		AppearanceStorage.AppearanceData appearance = new AppearanceStorage.AppearanceData();

		appearance.gender = gender;

		if (gender == 0) { // Male
			appearance.head = AppearanceConfig.Defaults.MALE_HEAD;
			appearance.jaw = AppearanceConfig.Defaults.MALE_JAW;
			appearance.torso = AppearanceConfig.Defaults.MALE_TORSO;
			appearance.arms = AppearanceConfig.Defaults.MALE_ARMS;
			appearance.hands = AppearanceConfig.Defaults.MALE_HANDS;
			appearance.legs = AppearanceConfig.Defaults.MALE_LEGS;
			appearance.feet = AppearanceConfig.Defaults.MALE_FEET;
		} else { // Female
			appearance.head = AppearanceConfig.Defaults.FEMALE_HEAD;
			appearance.jaw = AppearanceConfig.Defaults.FEMALE_JAW;
			appearance.torso = AppearanceConfig.Defaults.FEMALE_TORSO;
			appearance.arms = AppearanceConfig.Defaults.FEMALE_ARMS;
			appearance.hands = AppearanceConfig.Defaults.FEMALE_HANDS;
			appearance.legs = AppearanceConfig.Defaults.FEMALE_LEGS;
			appearance.feet = AppearanceConfig.Defaults.FEMALE_FEET;
		}

		// Default colors
		appearance.hairColor = AppearanceConfig.Defaults.HAIR_COLOR;
		appearance.torsoColor = AppearanceConfig.Defaults.TORSO_COLOR;
		appearance.legsColor = AppearanceConfig.Defaults.LEGS_COLOR;
		appearance.feetColor = AppearanceConfig.Defaults.FEET_COLOR;
		appearance.skinColor = AppearanceConfig.Defaults.SKIN_COLOR;

		return appearance;
	}

	/**
	 * Gets a readable description of the appearance
	 */
	public static String getAppearanceDescription(AppearanceStorage.AppearanceData appearance) {
		StringBuilder desc = new StringBuilder();
		desc.append(appearance.gender == 0 ? "Male Stoner" : "Female Stonerette");
		desc.append(" with ");

		// Add color descriptions (you could expand this with actual color names)
		desc.append("hair color ").append(appearance.hairColor);
		desc.append(", torso color ").append(appearance.torsoColor);
		desc.append(", legs color ").append(appearance.legsColor);
		desc.append(", feet color ").append(appearance.feetColor);
		desc.append(", skin color ").append(appearance.skinColor);

		return desc.toString();
	}

	/**
	 * Checks if the current client session has changed users
	 */
	public static boolean hasUserChanged(String lastKnownUser) {
		String currentUser = Client.myUsername != null ? Client.myUsername : "";
		return !currentUser.equals(lastKnownUser);
	}

	// Helper methods
	private static boolean isInRange(int value, int[] range) {
		if (range[0] == -1 && range[1] == -1) {
			return value == -1; // Special case for unavailable options
		}
		return value >= range[0] && value <= range[1];
	}

	private static byte fixPartValue(byte value, int partIndex, byte gender) {
		int[] range = AppearanceConfig.getPartRange(partIndex, gender);
		if (isInRange(value, range)) {
			return value; // Already valid
		}

		// Return gender-appropriate default
		if (gender == 0) { // Male
			switch (partIndex) {
				case 0: return AppearanceConfig.Defaults.MALE_HEAD;
				case 1: return AppearanceConfig.Defaults.MALE_JAW;
				case 2: return AppearanceConfig.Defaults.MALE_TORSO;
				case 3: return AppearanceConfig.Defaults.MALE_ARMS;
				case 4: return AppearanceConfig.Defaults.MALE_HANDS;
				case 5: return AppearanceConfig.Defaults.MALE_LEGS;
				case 6: return AppearanceConfig.Defaults.MALE_FEET;
				default: return 0;
			}
		} else { // Female
			switch (partIndex) {
				case 0: return AppearanceConfig.Defaults.FEMALE_HEAD;
				case 1: return AppearanceConfig.Defaults.FEMALE_JAW;
				case 2: return AppearanceConfig.Defaults.FEMALE_TORSO;
				case 3: return AppearanceConfig.Defaults.FEMALE_ARMS;
				case 4: return AppearanceConfig.Defaults.FEMALE_HANDS;
				case 5: return AppearanceConfig.Defaults.FEMALE_LEGS;
				case 6: return AppearanceConfig.Defaults.FEMALE_FEET;
				default: return 0;
			}
		}
	}

	private static byte fixColorValue(byte value, int colorIndex) {
		int[] range = AppearanceConfig.getColorRange(colorIndex);
		if (isInRange(value, range)) {
			return value; // Already valid
		}

		// Return appropriate default
		switch (colorIndex) {
			case 0: return AppearanceConfig.Defaults.HAIR_COLOR;
			case 1: return AppearanceConfig.Defaults.TORSO_COLOR;
			case 2: return AppearanceConfig.Defaults.LEGS_COLOR;
			case 3: return AppearanceConfig.Defaults.FEET_COLOR;
			case 4: return AppearanceConfig.Defaults.SKIN_COLOR;
			default: return 0;
		}
	}

	private static byte getColorValue(AppearanceStorage.AppearanceData appearance, int colorIndex) {
		switch (colorIndex) {
			case 0: return appearance.hairColor;
			case 1: return appearance.torsoColor;
			case 2: return appearance.legsColor;
			case 3: return appearance.feetColor;
			case 4: return appearance.skinColor;
			default: return 0;
		}
	}
}