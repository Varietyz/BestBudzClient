package com.bestbudz.dock.ui.panel.social.chat;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.TextInput;
import com.bestbudz.ui.interfaces.Chatbox;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;

/**
 * ChatCore - Focused on chat functionality only
 * Handles sending messages, channel management, and chat-related utilities
 *
 * Dialogue functionality has been moved to DialogueCore for better separation of concerns
 */
public class ChatCore {

	// Chat channel modes
	public static final String[] CHANNELS = {"All", "Public", "Private", "Trade"};
	public static final int[] CHANNEL_TYPES = {0, 1, 2, 3};

	// Message types enum for better organization
	public enum MessageType {
		SYSTEM(0), PUBLIC(1), PUBLIC_CONT(2), PRIVATE(3), TRADE(4),
		PRIVATE_CONT(5), PRIVATE_IN(6), PRIVATE_OUT(7), TRADE_CONT(8);

		public final int value;
		MessageType(int value) { this.value = value; }
	}

	// Message processing interface
	public interface MessageProcessor {
		void onSystemMessage(String message);
		void refreshDisplay();
	}

	private MessageProcessor messageProcessor;

	public ChatCore(MessageProcessor processor) {
		this.messageProcessor = processor;
	}

	// Base chat message data structure
	public static class ChatMessageData {
		public String message, username, title, titleColor;
		public int messageType, rights;
		public long timestamp;

		public ChatMessageData(String message, String username, int messageType, int rights, String title, String titleColor) {
			this.message = message;
			this.username = username;
			this.messageType = messageType;
			this.rights = rights;
			this.title = title;
			this.titleColor = titleColor;
			this.timestamp = System.currentTimeMillis();
		}

		public ChatMessageData(String message, String username, int messageType, int rights) {
			this(message, username, messageType, rights, null, null);
		}

		public ChatMessageData(String message, String username, int messageType, int rights, String title, String titleColor, long timestamp) {
			this.message = message;
			this.username = username;
			this.messageType = messageType;
			this.rights = rights;
			this.title = title;
			this.titleColor = titleColor;
			this.timestamp = timestamp;
		}
	}

	/**
	 * Send public chat message to game world
	 */
	public void sendPublicChat(String message) {
		try {
			if (Client.stream != null && Client.loggedIn) {
				Client.stream.createFrame(4);
				Client.stream.writeWordBigEndian(0);
				int blockStart = Client.stream.currentOffset;

				Client.stream.method425(0);
				Client.stream.method425(0);

				Client.aStream_834.currentOffset = 0;
				TextInput.method526(message, Client.aStream_834);
				Client.stream.method441(0, Client.aStream_834.buffer, Client.aStream_834.currentOffset);

				Client.stream.writeBytes(Client.stream.currentOffset - blockStart);

				String processedText = TextInput.processText(message);
				Client.myStoner.textSpoken = processedText;
				Client.myStoner.anInt1513 = 0;
				Client.myStoner.anInt1531 = 0;
				Client.myStoner.textCycle = 150;

				Chatbox.pushMessage(processedText, 2, Client.myStoner.name, Client.myStoner.title, Client.myStoner.titleColor);

				System.out.println("Sent public chat: " + message);
			}
		} catch (Exception e) {
			System.err.println("Error sending public chat: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Send private message
	 */
	public void sendPrivateMessage(String recipient, String message) {
		try {
			if (Client.stream != null && Client.loggedIn) {
				// Implementation for private message sending would go here
				// This is a placeholder for the actual private message protocol
				System.out.println("Sent private message to " + recipient + ": " + message);
			}
		} catch (Exception e) {
			System.err.println("Error sending private message: " + e.getMessage());
		}
	}

	/**
	 * Send trade message
	 */
	public void sendTradeMessage(String message) {
		try {
			if (Client.stream != null && Client.loggedIn) {
				// Implementation for trade message sending would go here
				System.out.println("Sent trade message: " + message);
			}
		} catch (Exception e) {
			System.err.println("Error sending trade message: " + e.getMessage());
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
	 * Get formatted timestamp from milliseconds - Java 11 compatible
	 */
	public String getFormattedTimestamp(long timestampMillis) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(
			Instant.ofEpochMilli(timestampMillis),
			ZoneId.systemDefault()
		);
		return String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
	}

	/**
	 * Update channel from game state
	 */
	public int getChannelFromGameState() {
		for (int i = 0; i < CHANNEL_TYPES.length; i++) {
			if (CHANNEL_TYPES[i] == Client.chatTypeView) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * Set game chat type view
	 */
	public void setGameChatTypeView(int selectedChannel) {
		if (selectedChannel < CHANNEL_TYPES.length) {
			Client.chatTypeView = CHANNEL_TYPES[selectedChannel];
		}
	}

	/**
	 * Parse title color from hex string to Color object
	 */
	public java.awt.Color parseTitleColor(String colorHex) {
		if (colorHex == null || colorHex.isEmpty()) {
			return new java.awt.Color(255, 255, 255);
		}

		try {
			if (colorHex.startsWith("#")) {
				colorHex = colorHex.substring(1);
			}
			int rgb = Integer.parseInt(colorHex, 16);
			return new java.awt.Color(rgb);
		} catch (NumberFormatException e) {
			return new java.awt.Color(255, 255, 255);
		}
	}

	/**
	 * Process title text for color codes
	 */
	public String processTitleText(String title) {
		if (title == null) return null;
		return title.replaceAll("<[^>]+>", "");
	}

	/**
	 * Get color from RuneScape color name
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
			case "or1": return new java.awt.Color(255, 165, 0);
			case "or2": return new java.awt.Color(255, 140, 0);
			case "or3": return new java.awt.Color(255, 69, 0);
			default: return new java.awt.Color(255, 255, 255);
		}
	}

	/**
	 * Check if text contains RuneScape style color codes
	 */
	public boolean hasColorCodes(String message) {
		if (message == null) return false;
		return message.matches(".*@\\w{3,}@.*");
	}

	/**
	 * Process chat message for display
	 */
	public ChatMessageData processMessage(String message, String username, int messageType, int rights, String title, String titleColor) {
		return new ChatMessageData(message, cleanUsername(username), messageType, rights, processTitleText(title), titleColor);
	}

	/**
	 * Get channel name from channel type
	 */
	public String getChannelName(int channelType) {
		for (int i = 0; i < CHANNEL_TYPES.length; i++) {
			if (CHANNEL_TYPES[i] == channelType) {
				return CHANNELS[i];
			}
		}
		return "Unknown";
	}

	/**
	 * Validate message before sending
	 */
	public boolean isValidMessage(String message) {
		return message != null && !message.trim().isEmpty() && message.length() <= 100;
	}

	/**
	 * Clean up resources
	 */
	public void dispose() {
		messageProcessor = null;
	}
}