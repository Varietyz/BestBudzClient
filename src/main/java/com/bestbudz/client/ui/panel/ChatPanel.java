package com.bestbudz.client.ui.panel;

import com.bestbudz.client.ui.panel.chat.ChatCore;
import com.bestbudz.client.ui.panel.chat.ChatCore.ChatMessageData;
import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.config.Configuration;
import com.bestbudz.engine.Client;
import com.bestbudz.ui.interfaces.Chatbox;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Simple ChatPanel - Basic Functionality
 * Features:
 * - Clean message display from game
 * - Basic message sending
 * - Simple channel filtering
 * - Proper message ordering (newest at bottom)
 */
public class ChatPanel extends JPanel implements UIPanel, DockTextUpdatable, ChatCore.MessageProcessor {

	// UI Components
	private JScrollPane scrollPane;
	private JTextPane chatDisplay;
	private JTextField inputField;
	private JComboBox<String> channelSelector;
	private StyledDocument chatDocument;

	// Core logic handler
	private ChatCore chatCore;

	// Threading for updates
	private ScheduledExecutorService updateScheduler;
	private volatile boolean isDisposed = false;
	private volatile boolean autoScroll = true;

	// Single message tracking - assign timestamp once, never change
	private final Map<String, Long> messageOnceTimestamps = new HashMap<>();

	// Color scheme - Clean and simple
	private static final Color BACKGROUND_COLOR = new Color(54, 57, 63);
	private static final Color TEXT_COLOR = new Color(220, 221, 222);
	private static final Color INPUT_BACKGROUND = new Color(64, 68, 75);
	private static final Color BORDER_COLOR = new Color(79, 84, 92);

	// Message type colors
	private static final Color PUBLIC_COLOR = new Color(185, 187, 190);
	private static final Color PRIVATE_COLOR = new Color(237, 66, 69);
	private static final Color SYSTEM_COLOR = new Color(250, 166, 26);
	private static final Color TRADE_COLOR = new Color(163, 190, 140);
	private static final Color USERNAME_COLOR = new Color(114, 137, 218);
	private static final Color TIMESTAMP_COLOR = new Color(114, 118, 125);

	public ChatPanel() {
		chatCore = new ChatCore(this);
		initializeUI();
		startUpdateScheduler();
	}

	private void initializeUI() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		createChatDisplay();
		createInputArea();

		SwingUtilities.invokeLater(this::loadInitialMessages);
	}

	private void createChatDisplay() {
		chatDisplay = new JTextPane();
		chatDisplay.setEditable(false);
		chatDisplay.setOpaque(true);
		chatDisplay.setBackground(BACKGROUND_COLOR);
		chatDisplay.setForeground(TEXT_COLOR);
		chatDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		chatDisplay.setMargin(new Insets(10, 10, 10, 10));
		chatDocument = chatDisplay.getStyledDocument();

		chatDisplay.addMouseWheelListener(this::handleScrollWheel);
		chatDisplay.addMouseListener(new ChatClickHandler());

		scrollPane = new JScrollPane(chatDisplay);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1, true));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		styleScrollBar();
		add(scrollPane, BorderLayout.CENTER);
	}

	private void createInputArea() {
		JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
		inputPanel.setOpaque(false);
		inputPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

		// Channel selector
		channelSelector = new JComboBox<>(ChatCore.CHANNELS);
		channelSelector.setBackground(INPUT_BACKGROUND);
		channelSelector.setForeground(TEXT_COLOR);
		channelSelector.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		channelSelector.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		channelSelector.setPreferredSize(new Dimension(85, 32));
		channelSelector.addActionListener(this::onChannelChange);

		// Input field
		inputField = createInputField();

		// Send button
		JButton sendButton = createSendButton();

		// Layout
		inputPanel.add(channelSelector, BorderLayout.WEST);
		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(sendButton, BorderLayout.EAST);

		add(inputPanel, BorderLayout.SOUTH);
	}

	private JTextField createInputField() {
		JTextField field = new JTextField();
		field.setBackground(INPUT_BACKGROUND);
		field.setForeground(TEXT_COLOR);
		field.setCaretColor(TEXT_COLOR);
		field.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			new EmptyBorder(6, 12, 6, 12)
		));
		field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		field.addActionListener(this::onSendMessage);
		addInputFieldBehavior(field);
		return field;
	}

	private JButton createSendButton() {
		JButton button = new JButton("Send");
		button.setBackground(new Color(88, 101, 242));
		button.setForeground(Color.WHITE);
		button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(this::onSendMessage);

		button.addMouseListener(new MouseAdapter() {
			private final Color originalColor = button.getBackground();

			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(new Color(
					Math.min(255, originalColor.getRed() + 30),
					Math.min(255, originalColor.getGreen() + 30),
					Math.min(255, originalColor.getBlue() + 30)
				));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(originalColor);
			}
		});

		return button;
	}

	private void addInputFieldBehavior(JTextField field) {
		final String placeholder = "Message...";
		final Color placeholderColor = new Color(114, 118, 125);

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(TEXT_COLOR);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (field.getText().trim().isEmpty()) {
					field.setText(placeholder);
					field.setForeground(placeholderColor);
				}
			}
		});

		field.setText(placeholder);
		field.setForeground(placeholderColor);
	}

	private void styleScrollBar() {
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(79, 84, 92);
				this.trackColor = BACKGROUND_COLOR;
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroSizeButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroSizeButton();
			}

			private JButton createZeroSizeButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}
		});
		verticalBar.setPreferredSize(new Dimension(12, 0));
	}

	private void startUpdateScheduler() {
		updateScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread t = new Thread(r, "ChatPanel-Update");
			t.setDaemon(true);
			return t;
		});

		updateScheduler.scheduleWithFixedDelay(() -> {
			if (!isDisposed) {
				checkForUpdates();
			}
		}, 200, 200, TimeUnit.MILLISECONDS);
	}

	private void checkForUpdates() {
		if (!Client.loggedIn || isDisposed) return;

		try {
			boolean hasNewMessages = false;
			for (int i = 0; i < 50; i++) {
				if (Chatbox.chatMessages[i] != null && !Chatbox.chatMessages[i].trim().isEmpty()) {
					hasNewMessages = true;
					break;
				}
			}

			if (hasNewMessages) {
				SwingUtilities.invokeLater(this::updateDisplay);
			}
		} catch (Exception e) {
			System.err.println("Error in chat update check: " + e.getMessage());
		}
	}

	// ChatCore.MessageProcessor implementation
	@Override
	public void onSystemMessage(String message) {
		if (!isDisposed) {
			SwingUtilities.invokeLater(() -> appendSystemMessage(message));
		}
	}

	@Override
	public void refreshDisplay() {
		if (!isDisposed) {
			SwingUtilities.invokeLater(this::updateDisplay);
		}
	}

	private void updateDisplay() {
		if (!Client.loggedIn || isDisposed) return;

		try {
			chatDocument.remove(0, chatDocument.getLength());

			int currentChannel = channelSelector.getSelectedIndex();
			List<ChatMessageData> messages = getGameMessages(currentChannel);

			if (messages.isEmpty()) {
				addWelcomeMessage();
			} else {
				renderMessages(messages);
			}

			if (autoScroll) {
				scrollToBottom();
			}

		} catch (BadLocationException e) {
			System.err.println("Error updating display: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Unexpected error in updateDisplay: " + e.getMessage());
		}
	}

	/**
	 * Get messages directly from game chatbox with preserved timestamps and titles
	 */
	private List<ChatMessageData> getGameMessages(int currentChannel) {
		List<ChatMessageData> messages = new ArrayList<>();

		// Read messages from game chatbox (index 0 = NEWEST message)
		for (int i = 0; i < 500; i++) {
			if (Chatbox.chatMessages[i] != null && !Chatbox.chatMessages[i].trim().isEmpty()) {
				String message = Chatbox.chatMessages[i];
				String username = (i < Chatbox.chatNames.length) ? Chatbox.chatNames[i] : null;
				int messageType = (i < Chatbox.chatTypes.length) ? Chatbox.chatTypes[i] : 0;
				int rights = (Chatbox.chatRights != null && i < Chatbox.chatRights.length) ? Chatbox.chatRights[i] : 0;

				// Get title and title color from Chatbox arrays
				String title = null;
				String titleColor = null;
				try {
					title = getChatTitle(i);
					titleColor = getChatTitleColor(i);
				} catch (Exception e) {
					// Fallback if title access fails
					title = null;
					titleColor = null;
				}

				if (chatCore.shouldShowMessage(messageType, currentChannel)) {
					// Create unique identifier for this specific message content
					String messageId = message + "|" + (username != null ? username : "") + "|" + messageType;

					// APPEND-ONCE LOGIC: If we've never seen this exact message before, timestamp it
					if (!messageOnceTimestamps.containsKey(messageId)) {
						// NEW MESSAGE - assign current timestamp and never touch again
						messageOnceTimestamps.put(messageId, System.currentTimeMillis());
					}

					// Always use the stored timestamp (assigned once, never changed)
					long timestamp = messageOnceTimestamps.get(messageId);

					// Create message data with the permanent timestamp
					ChatMessageData msgData = new ChatMessageData(message, username, messageType, rights, title, titleColor, timestamp);
					messages.add(msgData);
				}
			}
		}

		// Clean up very old timestamps to prevent memory leaks (keep last 1000)
		if (messageOnceTimestamps.size() > 1000) {
			List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(messageOnceTimestamps.entrySet());
			sortedEntries.sort(Map.Entry.<String, Long>comparingByValue().reversed());

			messageOnceTimestamps.clear();
			for (int i = 0; i < 500; i++) {
				Map.Entry<String, Long> entry = sortedEntries.get(i);
				messageOnceTimestamps.put(entry.getKey(), entry.getValue());
			}
		}

		// Sort messages by timestamp (oldest first) so newest appear at bottom
		messages.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
		return messages;
	}

	/**
	 * Helper method to get chat title (improved access with fallback)
	 */
	private String getChatTitle(int index) {
		try {
			// First try: Direct field access since it's public static
			Class<?> chatboxClass = Class.forName("com.bestbudz.ui.interfaces.Chatbox");
			java.lang.reflect.Field field = chatboxClass.getField("chatTitles");
			String[] chatTitles = (String[]) field.get(null);
			String title = (chatTitles != null && index >= 0 && index < chatTitles.length) ? chatTitles[index] : null;

			return title;
		} catch (Exception e) {
			// Second try: Direct access to Chatbox class if imported
			try {
				// If we can access Chatbox directly, use it
				return Chatbox.class.getDeclaredField("chatTitles").get(null) != null ?
					((String[]) Chatbox.class.getDeclaredField("chatTitles").get(null))[index] : null;
			} catch (Exception e2) {
				return null;
			}
		}
	}

	/**
	 * Helper method to get chat title color (improved access with fallback)
	 */
	private String getChatTitleColor(int index) {
		try {
			// First try: Direct field access since it's public static
			Class<?> chatboxClass = Class.forName("com.bestbudz.ui.interfaces.Chatbox");
			java.lang.reflect.Field field = chatboxClass.getField("chatColors");
			String[] chatColors = (String[]) field.get(null);
			String color = (chatColors != null && index >= 0 && index < chatColors.length) ? chatColors[index] : null;

			return color;
		} catch (Exception e) {
			// Second try: Direct access to Chatbox class if imported
			try {
				return Chatbox.class.getDeclaredField("chatColors").get(null) != null ?
					((String[]) Chatbox.class.getDeclaredField("chatColors").get(null))[index] : null;
			} catch (Exception e2) {
				return null;
			}
		}
	}

	private void renderMessages(List<ChatMessageData> messages) throws BadLocationException {
		// Show last 100 messages for performance
		int startIndex = Math.max(0, messages.size() - 100);

		for (int i = startIndex; i < messages.size(); i++) {
			ChatMessageData msg = messages.get(i);
			renderMessage(msg);
		}
	}

	private void renderMessage(ChatMessageData msg) throws BadLocationException {
		// Add timestamp only if enabled in configuration
		if (Configuration.enableTimeStamps) {
			String timestamp = chatCore.getFormattedTimestamp(msg.timestamp);
			Style timestampStyle = createStyle("timestamp", TIMESTAMP_COLOR, 11, false, false);
			chatDocument.insertString(chatDocument.getLength(), "[" + timestamp + "] ", timestampStyle);
		}

		// Render message based on type - preserve color codes for system messages
		switch (msg.messageType) {
			case 0: // System - Keep color codes for login/logout messages
				addFormattedMessage(msg.message, SYSTEM_COLOR);
				break;
			case 1:
			case 2: // Public
				renderUserMessage(msg.username, msg.message, msg.rights, PUBLIC_COLOR, msg.title, msg.titleColor);
				break;
			case 3:
			case 7: // Private - Display in private channel
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
				break;
			case 4: // Trade
				renderUserMessage(msg.username, msg.message, 0, TRADE_COLOR, msg.title, msg.titleColor);
				break;
			default:
				addFormattedMessage(msg.message, SYSTEM_COLOR);
				break;
		}

		chatDocument.insertString(chatDocument.getLength(), "\n", null);
	}

	private void renderUserMessage(String username, String message, int rights, Color baseColor, String title, String titleColor) throws BadLocationException {
		if (username != null) {
			String cleanUsername = chatCore.cleanUsername(username);

			// Add rank if present
			if (rights > 0) {
				Style rankStyle = createStyle("rank", new Color(255, 215, 0), 12, true, false);
				chatDocument.insertString(chatDocument.getLength(), chatCore.getRankName(rights) + " ", rankStyle);
			}

			// Add title if present
			if (title != null && !title.trim().isEmpty()) {
				String cleanTitle = chatCore.processTitleText(title);
				Color titleColorObj = chatCore.parseTitleColor(titleColor);

				Style titleStyle = createStyle("title", titleColorObj, 12, false, false);
				chatDocument.insertString(chatDocument.getLength(), cleanTitle + " ", titleStyle);
			}

			// Add username (removed underline)
			Style usernameStyle = createStyle("username_" + cleanUsername, USERNAME_COLOR, 12, true, false);
			chatDocument.insertString(chatDocument.getLength(), cleanUsername + ": ", usernameStyle);
		}

		// Add message with color code and image support
		addFormattedMessage(message, baseColor);
	}

	/**
	 * Add message with support for color codes and img tags
	 */
	private void addFormattedMessage(String message, Color defaultColor) throws BadLocationException {
		if (message == null) return;

		// For system messages, preserve more color functionality but still remove <img=X> tags
		String processedMessage = message.replaceAll("<img=\\d+>", "");

		// Check for RuneScape style color codes (@red@, @gre@, etc.)
		if (chatCore.hasColorCodes(processedMessage)) {
			addRuneScapeFormattedMessage(processedMessage, defaultColor);
			return;
		}

		// Regular text without color codes
		addMessage(processedMessage, defaultColor, false);
	}

	/**
	 * Handle RuneScape style color codes (@red@, @gre@, etc.)
	 */
	private void addRuneScapeFormattedMessage(String message, Color defaultColor) throws BadLocationException {
		// More flexible pattern to catch color codes
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(@\\w+@)");
		java.util.regex.Matcher matcher = pattern.matcher(message);

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
			String colorName = colorCode.substring(1, colorCode.length() - 1); // Remove @ symbols
			Color newColor = chatCore.getColorFromName(colorName);

			// Only change color if we found a valid color
			if (!newColor.equals(new java.awt.Color(255, 255, 255)) || colorName.toLowerCase().equals("whi") || colorName.toLowerCase().equals("white")) {
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
	 * Split message by tags while preserving the tags
	 */
	private String[] splitMessageByTags(String message) {
		java.util.List<String> parts = new java.util.ArrayList<>();
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(<img=\\d+>)");
		java.util.regex.Matcher matcher = pattern.matcher(message);

		int lastEnd = 0;
		while (matcher.find()) {
			// Add text before the tag
			if (matcher.start() > lastEnd) {
				String textPart = message.substring(lastEnd, matcher.start());
				if (!textPart.isEmpty()) {
					parts.add(textPart);
				}
			}

			// Add the tag itself
			parts.add(matcher.group(1));
			lastEnd = matcher.end();
		}

		// Add remaining text after last tag
		if (lastEnd < message.length()) {
			String remaining = message.substring(lastEnd);
			if (!remaining.isEmpty()) {
				parts.add(remaining);
			}
		}

		return parts.toArray(new String[0]);
	}

	private void addMessage(String text, Color color, boolean italic) throws BadLocationException {
		Style style = createStyle("message", color, 12, false, false);
		StyleConstants.setItalic(style, italic);
		chatDocument.insertString(chatDocument.getLength(), text, style);
	}

	private Style createStyle(String name, Color color, int fontSize, boolean bold, boolean underline) {
		Style style = chatDocument.addStyle(name, null);
		StyleConstants.setForeground(style, color);
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setBold(style, bold);
		StyleConstants.setUnderline(style, underline);
		return style;
	}

	private void addWelcomeMessage() throws BadLocationException {
		Style welcomeStyle = createStyle("welcome", new Color(150, 150, 150), 12, false, false);
		StyleConstants.setItalic(welcomeStyle, true);
		chatDocument.insertString(0, "Welcome to chat! Messages will appear here when you connect.\n", welcomeStyle);
	}

	private void appendSystemMessage(String message) {
		try {
			// Add timestamp only if enabled in configuration
			if (Configuration.enableTimeStamps) {
				String timestamp = chatCore.getFormattedTimestamp(System.currentTimeMillis());
				Style systemStyle = createStyle("system_msg", SYSTEM_COLOR, 12, false, false);
				StyleConstants.setItalic(systemStyle, true);
				chatDocument.insertString(chatDocument.getLength(), "[" + timestamp + "] [System] " + message + "\n", systemStyle);
			} else {
				Style systemStyle = createStyle("system_msg", SYSTEM_COLOR, 12, false, false);
				StyleConstants.setItalic(systemStyle, true);
				chatDocument.insertString(chatDocument.getLength(), "[System] " + message + "\n", systemStyle);
			}
			scrollToBottom();
		} catch (BadLocationException e) {
			// Ignore
		}
	}

	private void loadInitialMessages() {
		updateChannelFromGame();
		updateDisplay();
	}

	// Event handlers
	private void onSendMessage(ActionEvent e) {
		String message = inputField.getText().trim();

		if (message.equals("Message...") || message.isEmpty() || !Client.loggedIn) {
			return;
		}

		try {
			// Send public chat message only
			chatCore.sendPublicChat(message);

			inputField.setText("");
			inputField.setForeground(TEXT_COLOR);
			inputField.requestFocusInWindow();

		} catch (Exception ex) {
			System.err.println("Error sending message: " + ex.getMessage());
		}
	}

	private void onChannelChange(ActionEvent e) {
		int selectedChannel = channelSelector.getSelectedIndex();
		chatCore.setGameChatTypeView(selectedChannel);
		updateDisplay();
	}

	private void handleScrollWheel(MouseWheelEvent e) {
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		autoScroll = vBar.getValue() >= vBar.getMaximum() - vBar.getVisibleAmount() - 50;
	}

	private void updateChannelFromGame() {
		int gameChannel = chatCore.getChannelFromGameState();
		if (gameChannel != channelSelector.getSelectedIndex()) {
			channelSelector.setSelectedIndex(gameChannel);
		}
	}

	public void scrollToBottom() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
			verticalBar.setValue(verticalBar.getMaximum());
			autoScroll = true;
		});
	}

	// UIPanel implementation
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
		if (!Client.loggedIn) return;

		SwingUtilities.invokeLater(() -> {
			updateDisplay();
			inputField.requestFocusInWindow();
		});

		updateChannelFromGame();

		if (updateScheduler == null || updateScheduler.isShutdown()) {
			startUpdateScheduler();
		}
	}

	@Override
	public void onDeactivate() {
		// Keep scheduler running
	}

	// DockTextUpdatable implementation
	@Override
	public void updateText() {
		if (Client.loggedIn && !isDisposed) {
			SwingUtilities.invokeLater(this::updateDisplay);
		}
	}

	@Override
	public void updateDockText(int index, String text) {
		if (Client.loggedIn && !isDisposed) {
			SwingUtilities.invokeLater(this::updateDisplay);
		}
	}

	// Cleanup
	public void dispose() {
		isDisposed = true;

		if (updateScheduler != null && !updateScheduler.isShutdown()) {
			updateScheduler.shutdown();
			try {
				if (!updateScheduler.awaitTermination(1, TimeUnit.SECONDS)) {
					updateScheduler.shutdownNow();
				}
			} catch (InterruptedException e) {
				updateScheduler.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}

		if (chatCore != null) {
			chatCore.dispose();
		}
	}

	// Username click handling for inserting usernames
	private class ChatClickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				int pos = chatDisplay.viewToModel(e.getPoint());
				StyledDocument doc = chatDisplay.getStyledDocument();
				Element element = doc.getCharacterElement(pos);
				AttributeSet attrs = element.getAttributes();

				String styleName = (String) attrs.getAttribute(StyleConstants.NameAttribute);
				if (styleName != null && styleName.startsWith("username_")) {
					String username = styleName.substring(9);

					if (SwingUtilities.isLeftMouseButton(e)) {
						// Left click - insert username into input
						insertUsernameIntoInput(username);
					}
				}
			} catch (Exception ex) {
				// Ignore click errors
			}
		}
	}

	private void insertUsernameIntoInput(String username) {
		String currentText = inputField.getText();

		if (currentText.equals("Message...")) {
			currentText = "";
		}

		String newText = currentText.isEmpty() ? "@" + username + " " : currentText + " @" + username + " ";
		inputField.setText(newText);
		inputField.setForeground(TEXT_COLOR);
		inputField.requestFocusInWindow();
		inputField.setCaretPosition(newText.length());
	}
}