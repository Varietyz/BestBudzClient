package com.bestbudz.dock.ui.panel.social.chat;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.dock.ui.panel.social.chat.ChatCore.ChatMessageData;

import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ChatRenderer - Handles CHAT message rendering only
 *
 * UPDATED: Fixed timestamp handling for system messages and improved message organization
 * Responsibilities:
 * - Render regular chat messages with colors and formatting
 * - Handle color codes and text processing for chat
 * - Manage styles and text formatting for chat display
 * - Proper timestamp assignment for all message types
 */
public class ChatRenderer {

	// Chat color scheme
	private static final Color BACKGROUND_COLOR = new Color(54, 57, 63);
	private static final Color TEXT_COLOR = new Color(220, 221, 222);
	private static final Color PUBLIC_COLOR = new Color(185, 187, 190);
	private static final Color PRIVATE_COLOR = new Color(237, 66, 69);
	private static final Color SYSTEM_COLOR = new Color(250, 166, 26);
	private static final Color TRADE_COLOR = new Color(163, 190, 140);
	private static final Color USERNAME_COLOR = new Color(114, 137, 218);
	private static final Color TIMESTAMP_COLOR = new Color(114, 118, 125);

	private final StyledDocument document;
	private final ChatCore chatCore;

	public ChatRenderer(StyledDocument document, ChatCore chatCore) {
		this.document = document;
		this.chatCore = chatCore;
	}

	/**
	 * Render a single CHAT message (no dialogue messages)
	 */
	public void renderMessage(ChatMessageData msg) throws BadLocationException {
		// Skip dialogue messages - they're handled by DialogueModal now
		if (isDialogueMessage(msg.messageType)) {
			System.out.println("Skipping dialogue message - handled by DialogueModal");
			return;
		}

		// Add timestamp if enabled - use the message's actual timestamp
		if (SettingsConfig.enableTimeStamps) {
			String timestamp = chatCore.getFormattedTimestamp(msg.timestamp);
			Style timestampStyle = createStyle("timestamp", TIMESTAMP_COLOR, 11, false, false);
			document.insertString(document.getLength(), "[" + timestamp + "] ", timestampStyle);
		}

		// Render message based on type
		switch (msg.messageType) {
			case 0: // System
				addFormattedMessage(msg.message, SYSTEM_COLOR);
				break;
			case 1:
			case 2: // Public
				renderUserMessage(msg.username, msg.message, msg.rights, PUBLIC_COLOR, msg.title, msg.titleColor);
				break;
			case 3:
			case 5:
			case 6:
			case 7: // Private
				renderPrivateMessage(msg);
				break;
			case 4:
			case 8: // Trade
				renderUserMessage(msg.username, msg.message, 0, TRADE_COLOR, msg.title, msg.titleColor);
				break;
			default:
				addFormattedMessage(msg.message, SYSTEM_COLOR);
				break;
		}

		document.insertString(document.getLength(), "\n", null);
	}

	/**
	 * Check if message type is dialogue-related (should be handled by DialogueModal)
	 */
	private boolean isDialogueMessage(int messageType) {
		return messageType == 20 || messageType == 21 || messageType == 22; // Dialogue types
	}

	/**
	 * Render private message with special formatting
	 */
	private void renderPrivateMessage(ChatMessageData msg) throws BadLocationException {
		String pmPrefix = "PM from " + chatCore.cleanUsername(msg.username);
		if (msg.title != null && !msg.title.trim().isEmpty()) {
			String cleanTitle = chatCore.processTitleText(msg.title);
			pmPrefix = "PM from " + cleanTitle + " " + chatCore.cleanUsername(msg.username);
		}
		pmPrefix += ": ";

		// Add prefix in private color
		addMessage(pmPrefix, PRIVATE_COLOR, false);
		// Add message content with color code support
		addFormattedMessage(msg.message, PRIVATE_COLOR);
	}

	/**
	 * Render user message with username, title, and rank
	 */
	private void renderUserMessage(String username, String message, int rights, Color baseColor,
								   String title, String titleColor) throws BadLocationException {
		if (username != null) {
			String cleanUsername = chatCore.cleanUsername(username);

			// Add rank if present
			if (rights > 0) {
				Style rankStyle = createStyle("rank", new Color(255, 215, 0), 12, true, false);
				document.insertString(document.getLength(), chatCore.getRankName(rights) + " ", rankStyle);
			}

			// Add title if present
			if (title != null && !title.trim().isEmpty()) {
				String cleanTitle = chatCore.processTitleText(title);
				Color titleColorObj = chatCore.parseTitleColor(titleColor);

				Style titleStyle = createStyle("title", titleColorObj, 12, false, false);
				document.insertString(document.getLength(), cleanTitle + " ", titleStyle);
			}

			// Add username as clickable (for mentions/PM)
			Style usernameStyle = createClickableStyle("username_" + cleanUsername, USERNAME_COLOR, 12, true, false);
			document.insertString(document.getLength(), cleanUsername + ": ", usernameStyle);
		}

		// Add message with color code and image support
		addFormattedMessage(message, baseColor);
	}

	/**
	 * Add message with support for color codes and formatting
	 */
	private void addFormattedMessage(String message, Color defaultColor) throws BadLocationException {
		if (message == null) return;

		// Remove image tags
		String processedMessage = message.replaceAll("<img=\\d+>", "");

		// Check for RuneScape style color codes
		if (chatCore.hasColorCodes(processedMessage)) {
			addRuneScapeFormattedMessage(processedMessage, defaultColor);
			return;
		}

		// For long messages, add line breaks to prevent horizontal overflow
		String wrappedMessage = wrapLongMessage(processedMessage);
		addMessage(wrappedMessage, defaultColor, false);
	}

	/**
	 * Wrap long messages to prevent horizontal scrolling
	 */
	private String wrapLongMessage(String message) {
		if (message.length() <= 80) {
			return message; // Short enough, no wrapping needed
		}

		StringBuilder wrapped = new StringBuilder();
		String[] words = message.split(" ");
		int currentLineLength = 0;
		int maxLineLength = 70; // Adjust based on your chat width

		for (String word : words) {
			if (currentLineLength + word.length() + 1 > maxLineLength && currentLineLength > 0) {
				wrapped.append("\n");
				currentLineLength = 0;
			}

			if (currentLineLength > 0) {
				wrapped.append(" ");
				currentLineLength++;
			}

			wrapped.append(word);
			currentLineLength += word.length();
		}

		return wrapped.toString();
	}

	/**
	 * Handle RuneScape style color codes (@red@, @gre@, etc.)
	 */
	private void addRuneScapeFormattedMessage(String message, Color defaultColor) throws BadLocationException {
		Pattern pattern = Pattern.compile("(@\\w+@)");
		Matcher matcher = pattern.matcher(message);

		int lastEnd = 0;
		Color currentColor = defaultColor;

		while (matcher.find()) {
			// Add text before the color code
			if (matcher.start() > lastEnd) {
				String textPart = message.substring(lastEnd, matcher.start());
				if (!textPart.isEmpty()) {
					addMessage(textPart, currentColor, false);
				}
			}

			// Extract and apply color code
			String colorCode = matcher.group(1);
			String colorName = colorCode.substring(1, colorCode.length() - 1);
			Color newColor = chatCore.getColorFromName(colorName);

			// Only change color if we found a valid color
			if (!newColor.equals(new Color(255, 255, 255)) ||
				colorName.toLowerCase().equals("whi") ||
				colorName.toLowerCase().equals("white")) {
				currentColor = newColor;
			} else {
				// If it's not a recognized color, just add the text as-is
				addMessage(colorCode, currentColor, false);
			}

			lastEnd = matcher.end();
		}

		// Add remaining text after last color code
		if (lastEnd < message.length()) {
			String remaining = message.substring(lastEnd);
			if (!remaining.isEmpty()) {
				addMessage(remaining, currentColor, false);
			}
		}
	}

	/**
	 * Add simple text message with styling
	 */
	private void addMessage(String text, Color color, boolean italic) throws BadLocationException {
		Style style = createStyle("message", color, 12, false, false);
		StyleConstants.setItalic(style, italic);
		document.insertString(document.getLength(), text, style);
	}

	/**
	 * Create a standard text style
	 */
	public Style createStyle(String name, Color color, int fontSize, boolean bold, boolean underline) {
		Style style = document.addStyle(name, null);
		StyleConstants.setForeground(style, color);
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBold(style, bold);
		StyleConstants.setUnderline(style, underline);
		return style;
	}

	/**
	 * Create a clickable text style for interactive elements (usernames only now)
	 */
	private Style createClickableStyle(String name, Color color, int fontSize, boolean bold, boolean underline) {
		Style style = createStyle(name, color, fontSize, bold, underline);
		style.addAttribute("clickable", true);
		return style;
	}

	/**
	 * Add welcome message when no messages are present
	 */
	public void addWelcomeMessage() throws BadLocationException {
		Style welcomeStyle = createStyle("welcome", new Color(150, 150, 150), 12, false, false);
		StyleConstants.setItalic(welcomeStyle, true);
		document.insertString(0, "Welcome to chat! Messages will appear here when you connect.\n", welcomeStyle);
	}

	/**
	 * Add system message with specific timestamp (FIXED)
	 */
	public void appendSystemMessage(String message, long timestamp) throws BadLocationException {
		if (SettingsConfig.enableTimeStamps) {
			String timeStr = chatCore.getFormattedTimestamp(timestamp);
			Style systemStyle = createStyle("system_msg", SYSTEM_COLOR, 12, false, false);
			StyleConstants.setItalic(systemStyle, true);
			document.insertString(document.getLength(), "[" + timeStr + "] [System] " + message + "\n", systemStyle);
		} else {
			Style systemStyle = createStyle("system_msg", SYSTEM_COLOR, 12, false, false);
			StyleConstants.setItalic(systemStyle, true);
			document.insertString(document.getLength(), "[System] " + message + "\n", systemStyle);
		}
	}

	/**
	 * Add system message with current timestamp (for backwards compatibility)
	 */
	public void appendSystemMessage(String message) throws BadLocationException {
		appendSystemMessage(message, System.currentTimeMillis());
	}

	/**
	 * Add notification that dialogue opened (optional feedback in chat)
	 */
	public void addDialogueNotification(String npcName, int dialogueId) throws BadLocationException {
		Style dialogueNotifyStyle = createStyle("dialogue_notify", new Color(255, 215, 0), 11, false, false);
		StyleConstants.setItalic(dialogueNotifyStyle, true);
		String notification = String.format("[Dialogue opened with %s - ID: %d]\n",
			npcName != null ? npcName : "NPC", dialogueId);
		document.insertString(document.getLength(), notification, dialogueNotifyStyle);
	}

	/**
	 * Clear all chat content
	 */
	public void clearChat() {
		try {
			document.remove(0, document.getLength());
		} catch (BadLocationException e) {
			System.err.println("Error clearing chat: " + e.getMessage());
		}
	}

	/**
	 * Get background color for styling consistency
	 */
	public static Color getBackgroundColor() {
		return BACKGROUND_COLOR;
	}

	/**
	 * Get text color for styling consistency
	 */
	public static Color getTextColor() {
		return TEXT_COLOR;
	}
}