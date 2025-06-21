package com.bestbudz.dock.ui.panel.social;

import com.bestbudz.dock.util.RainbowHoverUtil;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.config.ColorConfig.*;
import com.bestbudz.ui.interfaces.Chatbox;
import com.bestbudz.graphics.text.TextInput;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple ChatPanel - Minimal Logic with Reversed Display Order
 * Just displays messages as they come, no complex timestamp tracking or sorting
 */
public class ChatPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private JScrollPane scrollPane;
	private JTextPane chatDisplay;
	private JTextField inputField;
	private JComboBox<String> channelSelector;
	private StyledDocument document;

	private ScheduledExecutorService updateScheduler;
	private volatile boolean isDisposed = false;

	// Simple tracking - just remember what we've already displayed
	private String lastDisplayedContent = "";

	private static final String[] CHANNELS = {"All", "Public", "Private", "Trade"};

	public ChatPanel() {
		initializeUI();
		startUpdateScheduler();
	}

	private void initializeUI() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(10, 10, 5, 10));

		// Chat display
		chatDisplay = new JTextPane() {
			@Override
			public boolean getScrollableTracksViewportWidth() {
				return true; // Force text to wrap to viewport width
			}
		};
		chatDisplay.setEditable(false);
		chatDisplay.setOpaque(true);
		chatDisplay.setBackground(CHAT_BACKGROUND_COLOR);
		chatDisplay.setForeground(WHITE_UI_COLOR);
		chatDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		chatDisplay.setMargin(new Insets(12, 12, 2, 12));
		document = chatDisplay.getStyledDocument();

		scrollPane = new JScrollPane(chatDisplay);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createLineBorder(SCROLLBAR_COLOR, 1, true));

		// Hide scrollbar UI but keep functionality
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
		scrollPane.getVerticalScrollBar().setOpaque(false);

		add(scrollPane, BorderLayout.CENTER);

		// Input area
		JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
		inputPanel.setOpaque(false);
		inputPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

		// Channel selector
		channelSelector = new JComboBox<>(CHANNELS);
		channelSelector.setBackground(CHAT_INPUT_BACKGROUND_COLOR);
		channelSelector.setForeground(WHITE_UI_COLOR);
		channelSelector.setBorder(BorderFactory.createLineBorder(SCROLLBAR_COLOR, 1));
		channelSelector.setPreferredSize(new Dimension(85, 32));
		channelSelector.addActionListener(e -> updateDisplay());

		// Input field
		inputField = new JTextField();
		inputField.setBackground(CHAT_INPUT_BACKGROUND_COLOR);
		inputField.setForeground(WHITE_UI_COLOR);
		inputField.setCaretColor(WHITE_UI_COLOR);
		inputField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(SCROLLBAR_COLOR, 1),
			new EmptyBorder(6, 12, 6, 12)
		));
		inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		inputField.addActionListener(this::sendMessage);

		// Send button
		JButton sendButton = new JButton("Send");
		sendButton.setBackground(BUTTON_COLOR);
		sendButton.setForeground(WHITE_UI_COLOR);
		sendButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		sendButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		sendButton.setFocusPainted(false);
		sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		sendButton.addActionListener(this::sendMessage);
		RainbowHoverUtil.applyRainbowHover(sendButton);

		inputPanel.add(channelSelector, BorderLayout.WEST);
		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);

		add(inputPanel, BorderLayout.SOUTH);
	}

	private void startUpdateScheduler() {
		updateScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "ChatPanel-Update");
			t.setDaemon(true);
			return t;
		});

		updateScheduler.scheduleWithFixedDelay(() -> {
			if (!isDisposed && Client.loggedIn) {
				SwingUtilities.invokeLater(this::checkForUpdates);
			}
		}, 500, 500, TimeUnit.MILLISECONDS);
	}

	private void checkForUpdates() {
		if (!Client.loggedIn || isDisposed) return;

		// Simple check - just see if chat content changed
		StringBuilder currentContent = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			if (Chatbox.chatMessages[i] != null) {
				currentContent.append(Chatbox.chatMessages[i]);
			}
		}

		String current = currentContent.toString();
		if (!current.equals(lastDisplayedContent)) {
			lastDisplayedContent = current;
			updateDisplay();
		}
	}

	private void updateDisplay() {
		if (!Client.loggedIn || isDisposed) return;

		try {
			// Remember scroll position
			JScrollBar vBar = scrollPane.getVerticalScrollBar();
			boolean wasAtBottom = vBar.getValue() >= (vBar.getMaximum() - vBar.getVisibleAmount() - 10);

			// Clear and rebuild
			document.remove(0, document.getLength());

			int selectedChannel = channelSelector.getSelectedIndex();

			// Display messages in reverse order (99 to 0 instead of 0 to 99)
			for (int i = 99; i >= 0; i--) {
				if (Chatbox.chatMessages[i] != null && !Chatbox.chatMessages[i].trim().isEmpty()) {
					String message = Chatbox.chatMessages[i];
					String username = (i < Chatbox.chatNames.length) ? Chatbox.chatNames[i] : null;
					int messageType = (i < Chatbox.chatTypes.length) ? Chatbox.chatTypes[i] : 0;
					int rights = (i < Chatbox.chatRights.length) ? Chatbox.chatRights[i] : 0;

					// Get title and title color
					String title = null;
					String titleColor = null;
					try {
						title = getChatTitle(i);
						titleColor = getChatTitleColor(i);
					} catch (Exception e) {
						// Fallback if title access fails
					}

					if (shouldShowMessage(messageType, selectedChannel)) {
						addMessage(message, username, messageType, rights, title, titleColor);
					}
				}
			}

			// Auto-scroll if we were at bottom
			if (wasAtBottom) {
				SwingUtilities.invokeLater(() -> {
					vBar.setValue(vBar.getMaximum());
				});
			}

		} catch (Exception e) {
			System.err.println("Error updating chat: " + e.getMessage());
		}
	}

	private boolean shouldShowMessage(int messageType, int channel) {
		switch (channel) {
			case 0: return true; // All
			case 1: return messageType == 1 || messageType == 2; // Public
			case 2: return messageType == 3 || messageType == 5 || messageType == 6 || messageType == 7; // Private
			case 3: return messageType == 4 || messageType == 8; // Trade
			default: return true;
		}
	}

	private void addMessage(String message, String username, int messageType, int rights, String title, String titleColor) {
		try {
			Color baseColor = getMessageColor(messageType);

			if (username != null && !username.trim().isEmpty()) {
				// Clean username (remove @cr tags)
				String cleanUsername = cleanUsername(username);

				// Add rank if present
				if (rights > 0) {
					Style rankStyle = document.addStyle("rank", null);
					StyleConstants.setForeground(rankStyle, new Color(255, 215, 0));
					StyleConstants.setBold(rankStyle, true);
					document.insertString(document.getLength(), getRankSymbol(rights) + " ", rankStyle);
				}

				// Add title if present
				if (title != null && !title.trim().isEmpty()) {
					String cleanTitle = processTitleText(title);
					Color titleColorObj = parseTitleColor(titleColor);

					Style titleStyle = document.addStyle("title", null);
					StyleConstants.setForeground(titleStyle, titleColorObj);
					document.insertString(document.getLength(), cleanTitle + " ", titleStyle);
				}

				// Add username
				Style usernameStyle = document.addStyle("username", null);
				StyleConstants.setForeground(usernameStyle, new Color(114, 137, 218));
				StyleConstants.setBold(usernameStyle, true);
				document.insertString(document.getLength(), cleanUsername + ": ", usernameStyle);
			}

			// Add message with color code support
			addFormattedMessage(message, baseColor);
			document.insertString(document.getLength(), "\n", null);

		} catch (BadLocationException e) {
			// Ignore
		}
	}

	/**
	 * Get rank symbol for rights level
	 */
	private String getRankSymbol(int rights) {
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
	 * Process title text for color codes
	 */
	private String processTitleText(String title) {
		if (title == null) return null;
		return title.replaceAll("<[^>]+>", "");
	}

	/**
	 * Parse title color from hex string to Color object
	 */
	private Color parseTitleColor(String colorHex) {
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
	 * Helper method to get chat title with fallback
	 */
	private String getChatTitle(int index) {
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
	 * Helper method to get chat title color with fallback
	 */
	private String getChatTitleColor(int index) {
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
	 * Clean username by removing @cr tags
	 */
	private String cleanUsername(String username) {
		if (username == null) return "";
		if (username.startsWith("@cr")) {
			int endIndex = username.indexOf("@", 3);
			if (endIndex > 0) {
				return username.substring(endIndex + 1);
			}
		}
		return username;
	}

	/**
	 * Add message with RuneScape color code support and proper text wrapping
	 */
	private void addFormattedMessage(String message, Color defaultColor) throws BadLocationException {
		if (message == null) return;

		// Remove image tags and HTML-style formatting tags
		String processedMessage = message.replaceAll("<img=\\d+>", "");
		processedMessage = cleanHtmlTags(processedMessage);

		// Force word wrapping by breaking long strings
		processedMessage = wrapLongText(processedMessage);

		// Check for RuneScape style color codes (@red@, @gre@, etc.)
		if (hasColorCodes(processedMessage)) {
			addColorCodedMessage(processedMessage, defaultColor);
		} else {
			// Regular message
			Style messageStyle = document.addStyle("message", null);
			StyleConstants.setForeground(messageStyle, defaultColor);
			document.insertString(document.getLength(), processedMessage, messageStyle);
		}
	}

	/**
	 * Clean HTML-style tags like <col=FFFF64>, <shad=0>, </shad>, </col>, etc.
	 */
	private String cleanHtmlTags(String text) {
		if (text == null) return null;

		// Remove color tags: <col=FFFF64>, </col>
		text = text.replaceAll("<col=[^>]*>", "");
		text = text.replaceAll("</col>", "");

		// Remove shadow tags: <shad=0>, </shad>
		text = text.replaceAll("<shad=[^>]*>", "");
		text = text.replaceAll("</shad>", "");

		// Remove any other HTML-style tags
		text = text.replaceAll("<[^>]+>", "");

		return text;
	}

	/**
	 * Wrap long text to prevent overflow
	 */
	private String wrapLongText(String text) {
		if (text == null) return text;

		StringBuilder wrapped = new StringBuilder();
		String[] words = text.split(" ");
		int lineLength = 0;

		for (String word : words) {
			// If a single word is too long, break it up
			if (word.length() > 50) {
				if (lineLength > 0) {
					wrapped.append("\n");
					lineLength = 0;
				}
				// Break long words into chunks
				while (word.length() > 50) {
					wrapped.append(word.substring(0, 50)).append("\n");
					word = word.substring(50);
					lineLength = 0;
				}
				if (!word.isEmpty()) {
					wrapped.append(word);
					lineLength = word.length();
				}
			} else {
				// Normal word wrapping
				if (lineLength + word.length() + 1 > 60) {
					wrapped.append("\n").append(word);
					lineLength = word.length();
				} else {
					if (lineLength > 0) {
						wrapped.append(" ");
						lineLength++;
					}
					wrapped.append(word);
					lineLength += word.length();
				}
			}
		}

		return wrapped.toString();
	}

	/**
	 * Check if message has color codes
	 */
	private boolean hasColorCodes(String message) {
		return message != null && message.matches(".*@\\w{3,}@.*");
	}

	/**
	 * Add message with color code processing and proper wrapping
	 */
	private void addColorCodedMessage(String message, Color defaultColor) throws BadLocationException {
		// First wrap the message to prevent overflow
		String wrappedMessage = wrapLongText(message);

		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(@\\w+@)");
		java.util.regex.Matcher matcher = pattern.matcher(wrappedMessage);

		int lastEnd = 0;
		Color currentColor = defaultColor;

		while (matcher.find()) {
			// Add text before the color code
			if (matcher.start() > lastEnd) {
				String textPart = wrappedMessage.substring(lastEnd, matcher.start());
				if (!textPart.isEmpty()) {
					Style style = document.addStyle("colored", null);
					StyleConstants.setForeground(style, currentColor);
					document.insertString(document.getLength(), textPart, style);
				}
			}

			// Extract and apply color code
			String colorCode = matcher.group(1);
			String colorName = colorCode.substring(1, colorCode.length() - 1);
			Color newColor = getColorFromName(colorName);

			// Only change color if we found a valid color
			if (!newColor.equals(Color.WHITE) ||
				colorName.toLowerCase().equals("whi") ||
				colorName.toLowerCase().equals("white")) {
				currentColor = newColor;
			} else {
				// If it's not a recognized color, just add the text as-is
				Style style = document.addStyle("colored", null);
				StyleConstants.setForeground(style, currentColor);
				document.insertString(document.getLength(), colorCode, style);
			}

			lastEnd = matcher.end();
		}

		// Add remaining text after last color code
		if (lastEnd < wrappedMessage.length()) {
			String remaining = wrappedMessage.substring(lastEnd);
			if (!remaining.isEmpty()) {
				Style style = document.addStyle("colored", null);
				StyleConstants.setForeground(style, currentColor);
				document.insertString(document.getLength(), remaining, style);
			}
		}
	}

	/**
	 * Get color from RuneScape color name
	 */
	private Color getColorFromName(String colorName) {
		if (colorName == null) return Color.WHITE;

		String name = colorName.toLowerCase();
		switch (name) {
			case "red": return new Color(255, 0, 0);
			case "gre": case "green": return new Color(0, 255, 0);
			case "blu": case "blue": return new Color(0, 0, 255);
			case "yel": case "yellow": return new Color(255, 255, 0);
			case "cya": case "cyan": return new Color(0, 255, 255);
			case "mag": case "magenta": return new Color(255, 0, 255);
			case "whi": case "white": return new Color(255, 255, 255);
			case "bla": case "black": return new Color(0, 0, 0);
			case "ora": case "orange": return new Color(255, 165, 0);
			case "pur": case "purple": return new Color(128, 0, 128);
			case "gry": case "gray": case "grey": return new Color(128, 128, 128);
			case "or1": return new Color(255, 165, 0);
			case "or2": return new Color(255, 140, 0);
			case "or3": return new Color(255, 69, 0);
			default: return Color.WHITE;
		}
	}

	private Color getMessageColor(int messageType) {
		switch (messageType) {
			case 0: return new Color(250, 166, 26); // System
			case 1:
			case 2: return new Color(185, 187, 190); // Public
			case 3:
			case 5:
			case 6:
			case 7: return new Color(237, 66, 69); // Private
			case 4:
			case 8: return new Color(163, 190, 140); // Trade
			default: return new Color(220, 221, 222); // Default
		}
	}

	private void sendMessage(ActionEvent e) {
		String message = inputField.getText().trim();
		if (message.isEmpty() || !Client.loggedIn) return;

		try {
			if (Client.stream != null) {
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
			}

			inputField.setText("");
			inputField.requestFocusInWindow();

		} catch (Exception ex) {
			System.err.println("Error sending message: " + ex.getMessage());
		}
	}

	@Override
	public String getPanelID() {
		return "Chat";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (Client.loggedIn) {
			updateDisplay();
			inputField.requestFocusInWindow();
		}
	}

	@Override
	public void onDeactivate() {
		// Nothing needed
	}

	@Override
	public void updateText() {
		if (Client.loggedIn && !isDisposed) {
			SwingUtilities.invokeLater(this::updateDisplay);
		}
	}

	@Override
	public void updateDockText(int index, String text) {
		updateText();
	}

	public void dispose() {
		isDisposed = true;
		if (updateScheduler != null && !updateScheduler.isShutdown()) {
			updateScheduler.shutdown();
		}
	}
}