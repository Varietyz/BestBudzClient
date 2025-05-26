package com.bestbudz.client.ui.panel.chat;

import com.bestbudz.engine.Client;
import com.bestbudz.ui.TextInput;
import com.bestbudz.ui.interfaces.Chatbox;
import static com.bestbudz.engine.Client.chatTypeView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Final ChatCore - Pure Basic Functionality
 * Handles:
 * - Simple message sending to game world
 * - Message filtering by channel
 * - Text processing and validation
 * - Game integration
 */
public class ChatCore {

	// Chat channel modes - Basic channels only
	public static final String[] CHANNELS = {"All", "Public", "Private", "Trade"};
	public static final int[] CHANNEL_TYPES = {0, 1, 2, 3};

	// Message processing interface
	public interface MessageProcessor {
		void onSystemMessage(String message);
		void refreshDisplay();
	}

	private MessageProcessor messageProcessor;

	public ChatCore(MessageProcessor processor) {
		this.messageProcessor = processor;
	}

	// Simple chat message data structure with timestamp and title support
	public static class ChatMessageData {
		public String message, username, title, titleColor;
		public int messageType, rights;
		public long timestamp;

		// Main constructor - NO automatic timestamp assignment
		public ChatMessageData(String message, String username, int messageType, int rights, String title, String titleColor) {
			this.message = message;
			this.username = username;
			this.messageType = messageType;
			this.rights = rights;
			this.title = title;
			this.titleColor = titleColor;
			// Do NOT assign timestamp here - let caller handle it
		}

		// Backward compatibility constructor
		public ChatMessageData(String message, String username, int messageType, int rights) {
			this(message, username, messageType, rights, null, null);
		}

		// Constructor with explicit timestamp
		public ChatMessageData(String message, String username, int messageType, int rights, String title, String titleColor, long timestamp) {
			this.message = message;
			this.username = username;
			this.messageType = messageType;
			this.rights = rights;
			this.title = title;
			this.titleColor = titleColor;
			this.timestamp = timestamp; // Use provided timestamp
		}
	}

	/**
	 * Send public chat message to game world
	 */
	public void sendPublicChat(String message) {
		try {
			if (Client.stream != null && Client.loggedIn) {
				// Exact sequence from working Client.java code
				Client.stream.createFrame(4);                    // Public chat packet ID = 4
				Client.stream.writeWordBigEndian(0);             // Placeholder for message-block length
				int blockStart = Client.stream.currentOffset;

				Client.stream.method425(0);                     // Chat effect (0 = normal)
				Client.stream.method425(0);                     // Color code (0 = default)

				// Use TextInput.method526 for proper text encoding
				Client.aStream_834.currentOffset = 0;
				TextInput.method526(message, Client.aStream_834);
				Client.stream.method441(0, Client.aStream_834.buffer, Client.aStream_834.currentOffset);

				// Patch the length at the reserved spot
				Client.stream.writeBytes(Client.stream.currentOffset - blockStart);

				// Set overhead text parameters exactly like Client.java does
				String processedText = TextInput.processText(message);
				Client.myStoner.textSpoken = processedText;      // → triggers overhead
				Client.myStoner.anInt1513 = 0;                   // color index (0 = default)
				Client.myStoner.anInt1531 = 0;                   // effect type (0 = normal)
				Client.myStoner.textCycle = 150;                 // duration in cycles (150 ticks)

				// Add to chatbox
				Chatbox.pushMessage(processedText, 2, Client.myStoner.name, Client.myStoner.title, Client.myStoner.titleColor);

				System.out.println("Sent public chat: " + message);
			}

		} catch (Exception e) {
			System.err.println("Error sending public chat: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Check if message should be shown in current channel
	 */
	public boolean shouldShowMessage(int messageType, int currentChannel) {
		switch (currentChannel) {
			case 0: return true; // All
			case 1: return messageType == 1 || messageType == 2; // Public
			case 2: return messageType == 3 || messageType == 5 || messageType == 6 || messageType == 7; // Private
			case 3: return messageType == 4 || messageType == 8; // Trade
			default: return true;
		}
	}

	/**
	 * Clean username by removing @cr tags
	 */
	public String cleanUsername(String username) {
		if (username == null) return "";

		if (username.startsWith("@cr")) {
			Pattern pattern = Pattern.compile("@cr\\d+?@");
			Matcher matcher = pattern.matcher(username);
			if (matcher.find()) {
				return username.substring(matcher.end());
			}
		}
		return username;
	}

	/**
	 * Get rank symbol for rights level
	 */
	public String getRankName(int rights) {
		switch (rights) {
			case 1: return "★";
			case 2: return "♦";
			case 3: return "♠";
			case 4: return "♣";
			case 5: return "♥";
			default: return "◦";
		}
	}

	/**
	 * Get formatted timestamp from milliseconds
	 */
	public String getFormattedTimestamp(long timestampMillis) {
		java.time.LocalDateTime dateTime = java.time.LocalDateTime.ofInstant(
			java.time.Instant.ofEpochMilli(timestampMillis),
			java.time.ZoneId.systemDefault()
		);
		return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
	}

	/**
	 * Update channel from game state
	 */
	public int getChannelFromGameState() {
		for (int i = 0; i < CHANNEL_TYPES.length; i++) {
			if (CHANNEL_TYPES[i] == chatTypeView) {
				return i;
			}
		}
		return 0; // Default to "All"
	}

	/**
	 * Set game chat type view
	 */
	public void setGameChatTypeView(int selectedChannel) {
		if (selectedChannel < CHANNEL_TYPES.length) {
			chatTypeView = CHANNEL_TYPES[selectedChannel];
		}
	}

	/**
	 * Clean up when core is being destroyed
	 */
	public void dispose() {
		messageProcessor = null;
	}

	/**
	 * Parse title color from hex string to Color object
	 */
	public java.awt.Color parseTitleColor(String colorHex) {
		if (colorHex == null || colorHex.isEmpty()) {
			return new java.awt.Color(255, 255, 255); // Default white
		}

		try {
			// Remove # if present
			if (colorHex.startsWith("#")) {
				colorHex = colorHex.substring(1);
			}

			// Parse hex color
			int rgb = Integer.parseInt(colorHex, 16);
			return new java.awt.Color(rgb);
		} catch (NumberFormatException e) {
			return new java.awt.Color(255, 255, 255); // Default white on error
		}
	}

	/**
	 * Process title text for color codes
	 */
	public String processTitleText(String title) {
		if (title == null) return null;

		// Remove HTML-like tags from title display (keep clean)
		return title.replaceAll("<[^>]+>", "");
	}

	/**
	 * Process message text for color codes and formatting
	 */
	public String processMessageText(String message) {
		if (message == null) return "";

		// Keep the original message for processing but return it unchanged
		// The actual color/image processing will happen in the UI layer
		return message;
	}

	/**
	 * Remove color/image tags from message for clean text display
	 */
	public String stripTags(String message) {
		if (message == null) return "";

		// Remove <img=X> tags
		String cleaned = message.replaceAll("<img=\\d+>", "");

		// Remove other potential tags
		cleaned = cleaned.replaceAll("<[^>]+>", "");

		return cleaned.trim();
	}

	/**
	 * Get color for color code or color name
	 */
	public java.awt.Color getColorFromCode(int colorCode) {
		switch (colorCode) {
			case 0: return new java.awt.Color(0, 0, 0);        // Black
			case 1: return new java.awt.Color(0, 0, 255);      // Blue
			case 2: return new java.awt.Color(0, 128, 0);      // Green
			case 3: return new java.awt.Color(0, 255, 255);    // Cyan
			case 4: return new java.awt.Color(128, 0, 0);      // Dark Red
			case 5: return new java.awt.Color(128, 0, 128);    // Purple
			case 6: return new java.awt.Color(255, 165, 0);    // Orange
			case 7: return new java.awt.Color(192, 192, 192);  // Light Gray
			case 8: return new java.awt.Color(128, 128, 128);  // Gray
			case 9: return new java.awt.Color(0, 0, 255);      // Light Blue
			case 10: return new java.awt.Color(0, 255, 0);     // Light Green
			case 11: return new java.awt.Color(0, 255, 255);   // Light Cyan
			case 12: return new java.awt.Color(255, 0, 0);     // Red
			case 13: return new java.awt.Color(255, 0, 255);   // Magenta
			case 14: return new java.awt.Color(255, 255, 0);   // Yellow
			case 15: return new java.awt.Color(255, 255, 255); // White
			default: return new java.awt.Color(255, 255, 255); // Default to white
		}
	}

	/**
	 * Get color from RuneScape color name (e.g., "red", "gre", "blu")
	 */
	public java.awt.Color getColorFromName(String colorName) {
		if (colorName == null) return new java.awt.Color(255, 255, 255);

		String name = colorName.toLowerCase();
		switch (name) {
			case "red": return new java.awt.Color(255, 0, 0);
			case "gre": case "green": return new java.awt.Color(0, 255, 0);
			case "blu": case "blue": return new java.awt.Color(0, 0, 255);
			case "yel": case "yellow": return new java.awt.Color(255, 255, 0);
			case "cya": case "cyan": return new java.awt.Color(0, 255, 255);
			case "mag": case "magenta": return new java.awt.Color(255, 0, 255);
			case "whi": case "white": return new java.awt.Color(255, 255, 255);
			case "bla": case "black": return new java.awt.Color(0, 0, 0);
			case "ora": case "orange": return new java.awt.Color(255, 165, 0);
			case "pur": case "purple": return new java.awt.Color(128, 0, 128);
			case "gry": case "gray": case "grey": return new java.awt.Color(128, 128, 128);
			case "or1": return new java.awt.Color(255, 165, 0);  // Orange variant
			case "or2": return new java.awt.Color(255, 140, 0);  // Dark orange
			case "or3": return new java.awt.Color(255, 69, 0);   // Red orange
			default: return new java.awt.Color(255, 255, 255);
		}
	}

	/**
	 * Check if text contains RuneScape style color codes (@red@, @gre@, etc.)
	 */
	public boolean hasColorCodes(String message) {
		if (message == null) return false;
		// Look for @color@ pattern (3 or more letters)
		return message.matches(".*@\\w{3,}@.*");
	}
}