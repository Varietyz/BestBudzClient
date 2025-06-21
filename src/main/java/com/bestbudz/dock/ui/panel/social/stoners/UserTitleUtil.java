package com.bestbudz.dock.ui.panel.social.stoners;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.interfaces.Chatbox;
import java.awt.Color;

public class UserTitleUtil {

	/**
	 * Get chat title with fallback using the same logic as ChatPanel
	 */
	public static String getChatTitle(int index) {
		try {
			Class<?> chatboxClass = Class.forName("com.bestbudz.ui.interfaces.Chatbox");
			java.lang.reflect.Field field = chatboxClass.getField("chatTitles");
			String[] chatTitles = (String[]) field.get(null);
			return (chatTitles != null && index >= 0 && index < chatTitles.length) ? chatTitles[index] : null;
		} catch (Exception e) {
			try {
				return Chatbox.class.getDeclaredField("chatTitles").get(null) != null ?
					((String[]) Chatbox.class.getDeclaredField("chatTitles").get(null))[index] : null;
			} catch (Exception e2) {
				return null;
			}
		}
	}

	/**
	 * Get chat title color with fallback using the same logic as ChatPanel
	 */
	public static String getChatTitleColor(int index) {
		try {
			Class<?> chatboxClass = Class.forName("com.bestbudz.ui.interfaces.Chatbox");
			java.lang.reflect.Field field = chatboxClass.getField("chatColors");
			String[] chatColors = (String[]) field.get(null);
			return (chatColors != null && index >= 0 && index < chatColors.length) ? chatColors[index] : null;
		} catch (Exception e) {
			try {
				return Chatbox.class.getDeclaredField("chatColors").get(null) != null ?
					((String[]) Chatbox.class.getDeclaredField("chatColors").get(null))[index] : null;
			} catch (Exception e2) {
				return null;
			}
		}
	}

	/**
	 * Process title text for color codes using the same logic as ChatPanel
	 */
	public static String processTitleText(String title) {
		if (title == null) return null;
		return title.replaceAll("<[^>]+>", "");
	}

	/**
	 * Parse title color from hex string to Color object using the same logic as ChatPanel
	 */
	public static Color parseTitleColor(String colorHex) {
		if (colorHex == null || colorHex.isEmpty()) {
			return new Color(255, 255, 255);
		}

		try {
			if (colorHex.startsWith("#")) {
				colorHex = colorHex.substring(1);
			}
			int rgb = Integer.parseInt(colorHex, 16);
			return new Color(rgb);
		} catch (NumberFormatException e) {
			return new Color(255, 255, 255);
		}
	}

	/**
	 * Get title for a specific username by finding their Stoner object
	 */
	public static String getTitleForUser(String username) {
		if (username == null || username.trim().isEmpty()) return null;

		// Check if it's our own stoner first
		if (Client.myStoner != null && username.equals(Client.myStoner.name)) {
			return Client.myStoner.title;
		}

		// Search through the stoner array for matching name
		if (Client.stonerArray != null) {
			for (int i = 0; i < Client.maxStoners; i++) {
				if (Client.stonerArray[i] != null &&
					username.equals(Client.stonerArray[i].name)) {
					return Client.stonerArray[i].title;
				}
			}
		}

		return null;
	}

	/**
	 * Get title color for a specific username by finding their Stoner object
	 */
	public static String getTitleColorForUser(String username) {
		if (username == null || username.trim().isEmpty()) return null;

		// Check if it's our own stoner first
		if (Client.myStoner != null && username.equals(Client.myStoner.name)) {
			return Client.myStoner.titleColor;
		}

		// Search through the stoner array for matching name
		if (Client.stonerArray != null) {
			for (int i = 0; i < Client.maxStoners; i++) {
				if (Client.stonerArray[i] != null &&
					username.equals(Client.stonerArray[i].name)) {
					return Client.stonerArray[i].titleColor;
				}
			}
		}

		return null;
	}
}