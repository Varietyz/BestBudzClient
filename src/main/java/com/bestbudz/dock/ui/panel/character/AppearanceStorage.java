
package com.bestbudz.dock.ui.panel.character;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Map;
import java.util.Properties;

public class AppearanceStorage {

	private static final String BESTBUDZ_FOLDER = ".BestBudzCache";
	private static final String APPEARANCE_FILE = "appearance.json";
	private static final String OLD_APPEARANCE_FILE = "appearance.dock";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	private final Path appearanceFilePath;
	private final Path oldAppearanceFilePath;
	private String currentUsername;

	public AppearanceStorage() {

		Path homeDir = Paths.get(System.getProperty("user.home"));
		Path bestbudzDir = homeDir.resolve(BESTBUDZ_FOLDER);

		try {
			Files.createDirectories(bestbudzDir);
			appearanceFilePath = bestbudzDir.resolve(APPEARANCE_FILE);
			oldAppearanceFilePath = bestbudzDir.resolve(OLD_APPEARANCE_FILE);
		} catch (IOException e) {
			throw new RuntimeException("Failed to create .bestbudz directory", e);
		}

		migrateFromProperties();
	}

	public void setCurrentUser(String username) {
		this.currentUsername = username;
	}

	public void saveAppearance(AppearanceData appearance) {
		if (currentUsername == null || currentUsername.trim().isEmpty()) {
			System.err.println("Cannot save appearance: no username set");
			return;
		}

		try {
			JsonObject root = loadJson();
			root.addProperty("currentUser", currentUsername);

			JsonObject users = root.has("users") ? root.getAsJsonObject("users") : new JsonObject();
			JsonObject userData = new JsonObject();
			userData.addProperty("gender", appearance.gender);
			userData.addProperty("head", appearance.head);
			userData.addProperty("jaw", appearance.jaw);
			userData.addProperty("torso", appearance.torso);
			userData.addProperty("arms", appearance.arms);
			userData.addProperty("hands", appearance.hands);
			userData.addProperty("legs", appearance.legs);
			userData.addProperty("feet", appearance.feet);
			userData.addProperty("hairColor", appearance.hairColor);
			userData.addProperty("torsoColor", appearance.torsoColor);
			userData.addProperty("legsColor", appearance.legsColor);
			userData.addProperty("feetColor", appearance.feetColor);
			userData.addProperty("skinColor", appearance.skinColor);
			users.add(currentUsername, userData);
			root.add("users", users);

			saveJson(root);
			System.out.println("Saved appearance for user: " + currentUsername);
			System.out.println("Saved data: " + appearance);

		} catch (Exception e) {
			System.err.println("Failed to save appearance: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public AppearanceData loadAppearance() {
		if (currentUsername == null || currentUsername.trim().isEmpty()) {
			System.out.println("No username set, using defaults");
			return getDefaultAppearance();
		}

		try {
			JsonObject root = loadJson();
			if (!root.has("users")) return getDefaultAppearance();

			JsonObject users = root.getAsJsonObject("users");
			if (!users.has(currentUsername)) {
				System.out.println("No saved appearance for user: " + currentUsername + ", using defaults");
				return getDefaultAppearance();
			}

			JsonObject u = users.getAsJsonObject(currentUsername);
			AppearanceData appearance = new AppearanceData();

			appearance.gender = getByte(u, "gender", AppearanceConfig.Defaults.GENDER);
			appearance.head = getByte(u, "head", getDefaultForGender(appearance.gender, "head"));
			appearance.jaw = getByte(u, "jaw", getDefaultForGender(appearance.gender, "jaw"));
			appearance.torso = getByte(u, "torso", getDefaultForGender(appearance.gender, "torso"));
			appearance.arms = getByte(u, "arms", getDefaultForGender(appearance.gender, "arms"));
			appearance.hands = getByte(u, "hands", getDefaultForGender(appearance.gender, "hands"));
			appearance.legs = getByte(u, "legs", getDefaultForGender(appearance.gender, "legs"));
			appearance.feet = getByte(u, "feet", getDefaultForGender(appearance.gender, "feet"));
			appearance.hairColor = getByte(u, "hairColor", AppearanceConfig.Defaults.HAIR_COLOR);
			appearance.torsoColor = getByte(u, "torsoColor", AppearanceConfig.Defaults.TORSO_COLOR);
			appearance.legsColor = getByte(u, "legsColor", AppearanceConfig.Defaults.LEGS_COLOR);
			appearance.feetColor = getByte(u, "feetColor", AppearanceConfig.Defaults.FEET_COLOR);
			appearance.skinColor = getByte(u, "skinColor", AppearanceConfig.Defaults.SKIN_COLOR);

			System.out.println("Loaded appearance for user: " + currentUsername);
			System.out.println("Loaded data: " + appearance);
			return appearance;

		} catch (Exception e) {
			System.err.println("Failed to load appearance: " + e.getMessage());
			e.printStackTrace();
			return getDefaultAppearance();
		}
	}

	public String getLastUser() {
		try {
			JsonObject root = loadJson();
			return root.has("currentUser") ? root.get("currentUser").getAsString() : "";
		} catch (Exception e) {
			return "";
		}
	}

	public boolean hasAppearanceData(String username) {
		if (username == null || username.trim().isEmpty()) {
			return false;
		}

		try {
			JsonObject root = loadJson();
			if (!root.has("users")) return false;
			boolean hasData = root.getAsJsonObject("users").has(username);
			System.out.println("User " + username + " has saved appearance data: " + hasData);
			return hasData;
		} catch (Exception e) {
			System.err.println("Error checking for user data: " + e.getMessage());
			return false;
		}
	}

	private JsonObject loadJson() throws IOException {
		if (Files.exists(appearanceFilePath)) {
			String content = new String(Files.readAllBytes(appearanceFilePath), StandardCharsets.UTF_8);
			return GSON.fromJson(content, JsonObject.class);
		}
		return new JsonObject();
	}

	private void saveJson(JsonObject root) throws IOException {
		Files.write(appearanceFilePath, GSON.toJson(root).getBytes(StandardCharsets.UTF_8));
	}

	private void migrateFromProperties() {
		if (Files.exists(appearanceFilePath) || !Files.exists(oldAppearanceFilePath)) {
			return;
		}

		try {
			Properties props = new Properties();
			try (InputStream input = Files.newInputStream(oldAppearanceFilePath)) {
				props.load(input);
			}

			JsonObject root = new JsonObject();
			String currentUser = props.getProperty("current.user", "");
			root.addProperty("currentUser", currentUser);

			// Collect all unique usernames from property keys
			java.util.Set<String> usernames = new java.util.HashSet<>();
			for (String key : props.stringPropertyNames()) {
				if (key.contains(".") && !key.equals("current.user")) {
					usernames.add(key.substring(0, key.indexOf('.')));
				}
			}

			JsonObject users = new JsonObject();
			for (String user : usernames) {
				JsonObject userData = new JsonObject();
				String prefix = user + ".";
				putByteIfPresent(userData, "gender", props, prefix + "gender");
				putByteIfPresent(userData, "head", props, prefix + "head");
				putByteIfPresent(userData, "jaw", props, prefix + "jaw");
				putByteIfPresent(userData, "torso", props, prefix + "torso");
				putByteIfPresent(userData, "arms", props, prefix + "arms");
				putByteIfPresent(userData, "hands", props, prefix + "hands");
				putByteIfPresent(userData, "legs", props, prefix + "legs");
				putByteIfPresent(userData, "feet", props, prefix + "feet");
				putByteIfPresent(userData, "hairColor", props, prefix + "hair.color");
				putByteIfPresent(userData, "torsoColor", props, prefix + "torso.color");
				putByteIfPresent(userData, "legsColor", props, prefix + "legs.color");
				putByteIfPresent(userData, "feetColor", props, prefix + "feet.color");
				putByteIfPresent(userData, "skinColor", props, prefix + "skin.color");
				users.add(user, userData);
			}
			root.add("users", users);

			saveJson(root);
			Files.delete(oldAppearanceFilePath);
			System.out.println("Migrated appearance.dock -> appearance.json");
		} catch (Exception e) {
			System.err.println("Failed to migrate appearance.dock: " + e.getMessage());
		}
	}

	private void putByteIfPresent(JsonObject obj, String jsonKey, Properties props, String propKey) {
		String val = props.getProperty(propKey);
		if (val != null) {
			try {
				obj.addProperty(jsonKey, Byte.parseByte(val));
			} catch (NumberFormatException ignored) {}
		}
	}

	private byte getByte(JsonObject obj, String key, byte defaultValue) {
		if (obj.has(key)) {
			try {
				return obj.get(key).getAsByte();
			} catch (Exception e) {
				return defaultValue;
			}
		}
		return defaultValue;
	}

	private byte getDefaultForGender(byte gender, String part) {
		if (gender == 0) {
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
		} else {
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

		if (appearance.gender == 0) {
			appearance.head = AppearanceConfig.Defaults.MALE_HEAD;
			appearance.jaw = AppearanceConfig.Defaults.MALE_JAW;
			appearance.torso = AppearanceConfig.Defaults.MALE_TORSO;
			appearance.arms = AppearanceConfig.Defaults.MALE_ARMS;
			appearance.hands = AppearanceConfig.Defaults.MALE_HANDS;
			appearance.legs = AppearanceConfig.Defaults.MALE_LEGS;
			appearance.feet = AppearanceConfig.Defaults.MALE_FEET;
		} else {
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
