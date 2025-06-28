// AppearanceStorage.java - Fixed version with proper default loading

package com.bestbudz.dock.ui.panel.character;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Manages persistence of appearance data to the .bestbudz folder
 *
 * Stores appearance data in a simple properties file format for easy reading/writing.
 * Each character's appearance is saved and loaded based on their username.
 */
public class AppearanceStorage {

	private static final String BESTBUDZ_FOLDER = ".BestBudzCache";
	private static final String APPEARANCE_FILE = "appearance.dock";
	private static final String CURRENT_USER_KEY = "current.user";

	// Property keys for appearance data
	private static final String GENDER_KEY = "gender";
	private static final String HEAD_KEY = "head";
	private static final String JAW_KEY = "jaw";
	private static final String TORSO_KEY = "torso";
	private static final String ARMS_KEY = "arms";
	private static final String HANDS_KEY = "hands";
	private static final String LEGS_KEY = "legs";
	private static final String FEET_KEY = "feet";
	private static final String HAIR_COLOR_KEY = "hair.color";
	private static final String TORSO_COLOR_KEY = "torso.color";
	private static final String LEGS_COLOR_KEY = "legs.color";
	private static final String FEET_COLOR_KEY = "feet.color";
	private static final String SKIN_COLOR_KEY = "skin.color";

	private final Path appearanceFilePath;
	private String currentUsername;

	public AppearanceStorage() {
		// Get user home directory and getPooledStream .bestbudz folder if needed
		Path homeDir = Paths.get(System.getProperty("user.home"));
		Path bestbudzDir = homeDir.resolve(BESTBUDZ_FOLDER);

		try {
			Files.createDirectories(bestbudzDir);
			appearanceFilePath = bestbudzDir.resolve(APPEARANCE_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Failed to getPooledStream .bestbudz directory", e);
		}
	}

	/**
	 * Sets the current username for loading/saving appearance data
	 */
	public void setCurrentUser(String username) {
		this.currentUsername = username;
	}

	/**
	 * Saves the current appearance data for the current user
	 */
	public void saveAppearance(AppearanceData appearance) {
		if (currentUsername == null || currentUsername.trim().isEmpty()) {
			System.err.println("Cannot save appearance: no username set");
			return;
		}

		try {
			Properties props = loadProperties();

			// Save current user
			props.setProperty(CURRENT_USER_KEY, currentUsername);

			// Save appearance data with user prefix
			String userPrefix = currentUsername + ".";
			props.setProperty(userPrefix + GENDER_KEY, String.valueOf(appearance.gender));
			props.setProperty(userPrefix + HEAD_KEY, String.valueOf(appearance.head));
			props.setProperty(userPrefix + JAW_KEY, String.valueOf(appearance.jaw));
			props.setProperty(userPrefix + TORSO_KEY, String.valueOf(appearance.torso));
			props.setProperty(userPrefix + ARMS_KEY, String.valueOf(appearance.arms));
			props.setProperty(userPrefix + HANDS_KEY, String.valueOf(appearance.hands));
			props.setProperty(userPrefix + LEGS_KEY, String.valueOf(appearance.legs));
			props.setProperty(userPrefix + FEET_KEY, String.valueOf(appearance.feet));
			props.setProperty(userPrefix + HAIR_COLOR_KEY, String.valueOf(appearance.hairColor));
			props.setProperty(userPrefix + TORSO_COLOR_KEY, String.valueOf(appearance.torsoColor));
			props.setProperty(userPrefix + LEGS_COLOR_KEY, String.valueOf(appearance.legsColor));
			props.setProperty(userPrefix + FEET_COLOR_KEY, String.valueOf(appearance.feetColor));
			props.setProperty(userPrefix + SKIN_COLOR_KEY, String.valueOf(appearance.skinColor));

			saveProperties(props);
			System.out.println("Saved appearance for user: " + currentUsername);
			System.out.println("Saved data: " + appearance);

		} catch (Exception e) {
			System.err.println("Failed to save appearance: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Loads appearance data for the current user, or returns defaults if not found
	 */
	public AppearanceData loadAppearance() {
		if (currentUsername == null || currentUsername.trim().isEmpty()) {
			System.out.println("No username set, using defaults");
			return getDefaultAppearance();
		}

		try {
			Properties props = loadProperties();
			String userPrefix = currentUsername + ".";

			// Check if user has saved data
			if (!props.containsKey(userPrefix + GENDER_KEY)) {
				System.out.println("No saved appearance for user: " + currentUsername + ", using defaults");
				return getDefaultAppearance();
			}

			// Load saved appearance data
			AppearanceData appearance = new AppearanceData();

			// Load gender first as it affects other defaults
			appearance.gender = getByte(props, userPrefix + GENDER_KEY, AppearanceConfig.Defaults.GENDER);

			// Load parts with gender-appropriate defaults
			appearance.head = getByte(props, userPrefix + HEAD_KEY, getDefaultForGender(appearance.gender, "head"));
			appearance.jaw = getByte(props, userPrefix + JAW_KEY, getDefaultForGender(appearance.gender, "jaw"));
			appearance.torso = getByte(props, userPrefix + TORSO_KEY, getDefaultForGender(appearance.gender, "torso"));
			appearance.arms = getByte(props, userPrefix + ARMS_KEY, getDefaultForGender(appearance.gender, "arms"));
			appearance.hands = getByte(props, userPrefix + HANDS_KEY, getDefaultForGender(appearance.gender, "hands"));
			appearance.legs = getByte(props, userPrefix + LEGS_KEY, getDefaultForGender(appearance.gender, "legs"));
			appearance.feet = getByte(props, userPrefix + FEET_KEY, getDefaultForGender(appearance.gender, "feet"));

			// Load colors
			appearance.hairColor = getByte(props, userPrefix + HAIR_COLOR_KEY, AppearanceConfig.Defaults.HAIR_COLOR);
			appearance.torsoColor = getByte(props, userPrefix + TORSO_COLOR_KEY, AppearanceConfig.Defaults.TORSO_COLOR);
			appearance.legsColor = getByte(props, userPrefix + LEGS_COLOR_KEY, AppearanceConfig.Defaults.LEGS_COLOR);
			appearance.feetColor = getByte(props, userPrefix + FEET_COLOR_KEY, AppearanceConfig.Defaults.FEET_COLOR);
			appearance.skinColor = getByte(props, userPrefix + SKIN_COLOR_KEY, AppearanceConfig.Defaults.SKIN_COLOR);

			System.out.println("Loaded appearance for user: " + currentUsername);
			System.out.println("Loaded data: " + appearance);
			return appearance;

		} catch (Exception e) {
			System.err.println("Failed to load appearance: " + e.getMessage());
			e.printStackTrace();
			return getDefaultAppearance();
		}
	}

	/**
	 * Gets the last logged in user from the storage file
	 */
	public String getLastUser() {
		try {
			Properties props = loadProperties();
			return props.getProperty(CURRENT_USER_KEY, "");
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * Checks if appearance data exists for a specific user
	 */
	public boolean hasAppearanceData(String username) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}

		try {
			Properties props = loadProperties();
			boolean hasData = props.containsKey(username + "." + GENDER_KEY);
			System.out.println("User " + username + " has saved appearance data: " + hasData);
			return hasData;
		} catch (Exception e) {
			System.err.println("Error checking for user data: " + e.getMessage());
			return false;
		}
	}

	// Helper methods
	private Properties loadProperties() throws IOException {
		Properties props = new Properties();
		if (Files.exists(appearanceFilePath)) {
			try (InputStream input = Files.newInputStream(appearanceFilePath)) {
				props.load(input);
				System.out.println("Loaded properties from: " + appearanceFilePath);
			}
		} else {
			System.out.println("No appearance file found at: " + appearanceFilePath);
		}
		return props;
	}

	private void saveProperties(Properties props) throws IOException {
		try (OutputStream output = Files.newOutputStream(appearanceFilePath)) {
			props.store(output, "BestBudz Dock Appearance Data");
			System.out.println("Saved properties to: " + appearanceFilePath);
		}
	}

	private byte getByte(Properties props, String key, byte defaultValue) {
		try {
			String value = props.getProperty(key);
			if (value != null) {
				byte result = Byte.parseByte(value);
				System.out.println("Loaded " + key + " = " + result);
				return result;
			} else {
				System.out.println("Key " + key + " not found, using default: " + defaultValue);
				return defaultValue;
			}
		} catch (NumberFormatException e) {
			System.err.println("Invalid value for " + key + ", using default: " + defaultValue);
			return defaultValue;
		}
	}

	private byte getDefaultForGender(byte gender, String part) {
		if (gender == 0) { // Male
			switch (part) {
				case "head": return AppearanceConfig.Defaults.MALE_HEAD;
				case "jaw": return AppearanceConfig.Defaults.MALE_JAW;
				case "torso": return AppearanceConfig.Defaults.MALE_TORSO;
				case "arms": return AppearanceConfig.Defaults.MALE_ARMS;
				case "hands": return AppearanceConfig.Defaults.MALE_HANDS;
				case "legs": return AppearanceConfig.Defaults.MALE_LEGS;
				case "feet": return AppearanceConfig.Defaults.MALE_FEET;
				default: return 0;
			}
		} else { // Female
			switch (part) {
				case "head": return AppearanceConfig.Defaults.FEMALE_HEAD;
				case "jaw": return AppearanceConfig.Defaults.FEMALE_JAW;
				case "torso": return AppearanceConfig.Defaults.FEMALE_TORSO;
				case "arms": return AppearanceConfig.Defaults.FEMALE_ARMS;
				case "hands": return AppearanceConfig.Defaults.FEMALE_HANDS;
				case "legs": return AppearanceConfig.Defaults.FEMALE_LEGS;
				case "feet": return AppearanceConfig.Defaults.FEMALE_FEET;
				default: return 0;
			}
		}
	}

	private AppearanceData getDefaultAppearance() {
		AppearanceData appearance = new AppearanceData();
		appearance.gender = AppearanceConfig.Defaults.GENDER;

		// Use gender-appropriate defaults
		if (appearance.gender == 0) { // Male
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

		appearance.hairColor = AppearanceConfig.Defaults.HAIR_COLOR;
		appearance.torsoColor = AppearanceConfig.Defaults.TORSO_COLOR;
		appearance.legsColor = AppearanceConfig.Defaults.LEGS_COLOR;
		appearance.feetColor = AppearanceConfig.Defaults.FEET_COLOR;
		appearance.skinColor = AppearanceConfig.Defaults.SKIN_COLOR;

		System.out.println("Created default appearance: " + appearance);
		return appearance;
	}

	/**
	 * Simple data class to hold appearance information
	 */
	public static class AppearanceData {
		public byte gender;
		public byte head, jaw, torso, arms, hands, legs, feet;
		public byte hairColor, torsoColor, legsColor, feetColor, skinColor;

		@Override
		public String toString() {
			return String.format("AppearanceData{gender=%d, head=%d, jaw=%d, torso=%d, arms=%d, hands=%d, legs=%d, feet=%d, hairColor=%d, torsoColor=%d, legsColor=%d, feetColor=%d, skinColor=%d}",
				gender, head, jaw, torso, arms, hands, legs, feet, hairColor, torsoColor, legsColor, feetColor, skinColor);
		}
	}
}