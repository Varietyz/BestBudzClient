package com.bestbudz.client.ui.panel;

import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.engine.Client;
import com.bestbudz.ui.TextInput;
import com.bestbudz.ui.interfaces.Chatbox;
import com.bestbudz.util.TextClass;
import static com.bestbudz.engine.Client.chatTypeView;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simplified ChatPanel that sends messages directly to server
 * Features:
 * - Real-time chat display from game
 * - Direct packet sending without client interference
 * - Clean message formatting
 * - @mention notifications
 */
public class ChatPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private JScrollPane scrollPane;
	private JTextPane chatDisplay;
	private JTextField inputField;
	private JComboBox<String> channelSelector;
	private StyledDocument chatDocument;
	private Timer refreshTimer;
	private int lastMessageCount = 0;

	// Chat channel modes
	private static final String[] CHANNELS = {"All", "Public", "Private", "Clan", "Trade"};
	private static final int[] CHANNEL_TYPES = {0, 1, 2, 4, 3};

	// Color scheme
	private static final Color BACKGROUND_COLOR = new Color(35, 35, 35);
	private static final Color TEXT_COLOR = Color.WHITE;
	private static final Color PUBLIC_COLOR = new Color(127, 169, 255);
	private static final Color PRIVATE_COLOR = new Color(255, 82, 86);
	private static final Color SYSTEM_COLOR = new Color(255, 255, 0);
	private static final Color CLAN_COLOR = new Color(95, 209, 122);
	private static final Color TRADE_COLOR = new Color(128, 0, 128);
	private static final Color USERNAME_COLOR = new Color(255, 215, 0);
	private static final Color MENTION_COLOR = new Color(88, 101, 242);

	public ChatPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setPreferredSize(null);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(0, 0));

		initializeComponents();
		setupLayout();
		startRefreshTimer();
	}

	private void initializeComponents() {
		// Create chat display area
		chatDisplay = new JTextPane();
		chatDisplay.setEditable(false);
		chatDisplay.setOpaque(true);
		chatDisplay.setBackground(BACKGROUND_COLOR);
		chatDisplay.setForeground(TEXT_COLOR);
		chatDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		chatDocument = chatDisplay.getStyledDocument();

		// Make usernames clickable
		chatDisplay.addMouseListener(new ChatClickHandler());
		chatDisplay.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));

		// Create scroll pane
		scrollPane = new JScrollPane(chatDisplay);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60), 1));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setBlockIncrement(64);
		styleScrollBar();

		// Create input field
		inputField = new JTextField();
		inputField.setBackground(new Color(45, 45, 45));
		inputField.setForeground(TEXT_COLOR);
		inputField.setCaretColor(TEXT_COLOR);
		inputField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(80, 80, 80), 1),
			new EmptyBorder(4, 8, 4, 8)
		));
		inputField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		inputField.addActionListener(this::sendMessage);

		// Create channel selector
		channelSelector = new JComboBox<>(CHANNELS);
		channelSelector.setBackground(new Color(45, 45, 45));
		channelSelector.setForeground(TEXT_COLOR);
		channelSelector.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80), 1));
		channelSelector.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		channelSelector.addActionListener(this::channelChanged);
		updateChannelFromGame();
	}

	private void setupLayout() {
		add(scrollPane, BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout(5, 0));
		bottomPanel.setOpaque(false);
		bottomPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

		channelSelector.setPreferredSize(new Dimension(80, 28));
		bottomPanel.add(channelSelector, BorderLayout.WEST);
		bottomPanel.add(inputField, BorderLayout.CENTER);
		bottomPanel.add(createSendButton(), BorderLayout.EAST);

		add(bottomPanel, BorderLayout.SOUTH);
		loadChatHistory();
	}

	private JButton createSendButton() {
		JButton sendButton = new JButton("Send");
		sendButton.setBackground(new Color(70, 130, 180));
		sendButton.setForeground(Color.WHITE);
		sendButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
		sendButton.setFont(new Font("Segoe UI", Font.BOLD, 11));
		sendButton.setFocusPainted(false);
		sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		sendButton.addActionListener(this::sendMessage);

		sendButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				sendButton.setBackground(new Color(100, 150, 200));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sendButton.setBackground(new Color(70, 130, 180));
			}
		});

		return sendButton;
	}

	private void styleScrollBar() {
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(100, 100, 100);
				this.trackColor = new Color(40, 40, 40);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createInvisibleButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createInvisibleButton();
			}

			private JButton createInvisibleButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}
		});
		verticalBar.setPreferredSize(new Dimension(8, 0));
	}

	private void startRefreshTimer() {
		refreshTimer = new Timer(100, e -> updateChatDisplay());
		refreshTimer.start();
	}

	private void updateChatDisplay() {
		if (!Client.loggedIn) return;

		boolean hasNewMessages = false;
		for (int i = 0; i < 10; i++) {
			if (Chatbox.chatMessages[i] != null) {
				hasNewMessages = true;
				break;
			}
		}

		if (hasNewMessages || lastMessageCount == 0) {
			SwingUtilities.invokeLater(this::loadChatHistory);
			lastMessageCount = hasNewMessages ? 1 : 0;
		}
	}

	private void loadChatHistory() {
		if (!Client.loggedIn) return;

		try {
			chatDocument.remove(0, chatDocument.getLength());

			java.util.List<ChatMessageData> messages = new java.util.ArrayList<>();

			for (int i = 0; i < 500; i++) {
				if (Chatbox.chatMessages[i] != null && !Chatbox.chatMessages[i].trim().isEmpty()) {
					String message = Chatbox.chatMessages[i];
					String username = (i < Chatbox.chatNames.length) ? Chatbox.chatNames[i] : null;
					int messageType = (i < Chatbox.chatTypes.length) ? Chatbox.chatTypes[i] : 0;
					int rights = (Chatbox.chatRights != null && i < Chatbox.chatRights.length) ? Chatbox.chatRights[i] : 0;

					messages.add(new ChatMessageData(message, username, messageType, rights));
				}
			}

			if (messages.isEmpty()) {
				Style statusStyle = chatDocument.addStyle("status", null);
				StyleConstants.setForeground(statusStyle, new Color(150, 150, 150));
				StyleConstants.setItalic(statusStyle, true);
				chatDocument.insertString(0, "[System] Chat ready. Messages will appear here.\n", statusStyle);
			} else {
				java.util.Collections.reverse(messages);
				int startIndex = Math.max(0, messages.size() - 100);

				for (int i = startIndex; i < messages.size(); i++) {
					ChatMessageData msgData = messages.get(i);
					addChatMessage(msgData.message, msgData.username, msgData.messageType, msgData.rights);
				}
			}

			scrollToBottom();

		} catch (BadLocationException e) {
			System.err.println("Error loading chat history: " + e.getMessage());
		}
	}

	private static class ChatMessageData {
		String message, username;
		int messageType, rights;

		ChatMessageData(String message, String username, int messageType, int rights) {
			this.message = message;
			this.username = username;
			this.messageType = messageType;
			this.rights = rights;
		}
	}

	private void addChatMessage(String message, String username, int messageType, int rights) {
		if (message == null) return;

		try {
			int currentChannel = channelSelector.getSelectedIndex();
			if (!shouldShowMessage(messageType, currentChannel)) {
				return;
			}

			boolean isMentioned = checkForMention(message);
			String timestamp = getCurrentTimestamp();

			// Add timestamp
			Style timestampStyle = chatDocument.addStyle("timestamp", null);
			StyleConstants.setForeground(timestampStyle, new Color(150, 150, 150));
			StyleConstants.setFontSize(timestampStyle, 10);
			chatDocument.insertString(chatDocument.getLength(), "[" + timestamp + "] ", timestampStyle);

			// Add message based on type
			switch (messageType) {
				case 0:
					addSystemMessage(message);
					break;
				case 1:
				case 2:
					addPublicMessage(username, message, rights, isMentioned);
					break;
				case 3:
				case 7:
					addPrivateMessage(username, message, rights);
					break;
				case 4:
					addTradeMessage(username, message);
					break;
				case 5:
				case 6:
					addClanMessage(username, message, rights, isMentioned);
					break;
				default:
					addSystemMessage(message);
					break;
			}

			chatDocument.insertString(chatDocument.getLength(), "\n", null);

		} catch (BadLocationException e) {
			System.err.println("Error adding chat message: " + e.getMessage());
		}
	}

	private void addSystemMessage(String message) throws BadLocationException {
		Style systemStyle = chatDocument.addStyle("system", null);
		StyleConstants.setForeground(systemStyle, SYSTEM_COLOR);
		StyleConstants.setItalic(systemStyle, true);
		chatDocument.insertString(chatDocument.getLength(), message, systemStyle);
	}

	private void addPublicMessage(String username, String message, int rights, boolean isMentioned) throws BadLocationException {
		if (username != null) {
			String cleanUsername = cleanUsername(username);

			if (rights > 0) {
				Style crownStyle = chatDocument.addStyle("crown", null);
				StyleConstants.setForeground(crownStyle, new Color(255, 215, 0));
				StyleConstants.setBold(crownStyle, true);
				chatDocument.insertString(chatDocument.getLength(), "[" + getRankName(rights) + "] ", crownStyle);
			}

			Style usernameStyle = chatDocument.addStyle("username_" + cleanUsername, null);
			StyleConstants.setForeground(usernameStyle, USERNAME_COLOR);
			StyleConstants.setBold(usernameStyle, true);
			StyleConstants.setUnderline(usernameStyle, true);
			chatDocument.insertString(chatDocument.getLength(), cleanUsername + ": ", usernameStyle);
		}

		// Add message with mention highlighting
		addFormattedMessage(message, PUBLIC_COLOR, isMentioned);
	}

	private void addPrivateMessage(String username, String message, int rights) throws BadLocationException {
		Style privateStyle = chatDocument.addStyle("private", null);
		StyleConstants.setForeground(privateStyle, PRIVATE_COLOR);

		if (username != null) {
			String cleanUsername = cleanUsername(username);
			chatDocument.insertString(chatDocument.getLength(), "PM from " + cleanUsername + ": ", privateStyle);
		}
		chatDocument.insertString(chatDocument.getLength(), message, privateStyle);
	}

	private void addTradeMessage(String username, String message) throws BadLocationException {
		Style tradeStyle = chatDocument.addStyle("trade", null);
		StyleConstants.setForeground(tradeStyle, TRADE_COLOR);

		if (username != null) {
			String cleanUsername = cleanUsername(username);
			chatDocument.insertString(chatDocument.getLength(), cleanUsername + " ", tradeStyle);
		}
		chatDocument.insertString(chatDocument.getLength(), message, tradeStyle);
	}

	private void addClanMessage(String username, String message, int rights, boolean isMentioned) throws BadLocationException {
		Style clanStyle = chatDocument.addStyle("clan", null);
		StyleConstants.setForeground(clanStyle, CLAN_COLOR);

		if (username != null) {
			String cleanUsername = cleanUsername(username);
			chatDocument.insertString(chatDocument.getLength(), "[Clan] " + cleanUsername + ": ", clanStyle);
		}
		addFormattedMessage(message, CLAN_COLOR, isMentioned);
	}

	private void addFormattedMessage(String message, Color baseColor, boolean isMentioned) throws BadLocationException {
		if (message == null) return;

		// Simple mention parsing
		String[] mentions = message.split("(?=@\\w+|@everyone)|(?<=@\\w|@everyone)");

		for (String part : mentions) {
			if (part.startsWith("@")) {
				Style mentionStyle = chatDocument.addStyle("mention", null);
				StyleConstants.setForeground(mentionStyle, MENTION_COLOR);
				StyleConstants.setBold(mentionStyle, true);
				chatDocument.insertString(chatDocument.getLength(), part, mentionStyle);
			} else if (!part.isEmpty()) {
				Style messageStyle = chatDocument.addStyle("message", null);
				StyleConstants.setForeground(messageStyle, baseColor);
				chatDocument.insertString(chatDocument.getLength(), part, messageStyle);
			}
		}
	}

	private String cleanUsername(String username) {
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

	private String getRankName(int rights) {
		switch (rights) {
			case 1: return "★";
			case 2: return "♦";
			case 3: return "♠";
			case 4: return "♣";
			case 5: return "♥";
			default: return "◦";
		}
	}

	private boolean shouldShowMessage(int messageType, int currentChannel) {
		switch (currentChannel) {
			case 0: return true;
			case 1: return messageType == 1 || messageType == 2;
			case 2: return messageType == 3 || messageType == 5 || messageType == 6 || messageType == 7;
			case 3: return messageType == 5 || messageType == 6;
			case 4: return messageType == 4 || messageType == 8;
			default: return true;
		}
	}

	private String getCurrentTimestamp() {
		java.time.LocalTime now = java.time.LocalTime.now();
		return String.format("%02d:%02d", now.getHour(), now.getMinute());
	}

	private boolean checkForMention(String message) {
		if (message == null || Client.myUsername == null) return false;

		String myName = Client.myUsername.toLowerCase();
		String lowerMessage = message.toLowerCase();

		return lowerMessage.contains("@" + myName) || lowerMessage.contains("@everyone");
	}

	private void triggerMentionNotification() {
		// Removed - no notification system
	}

	private void clearNotifications() {
		// Removed - no notification system
	}

	private void sendMessage(ActionEvent e) {
		String message = inputField.getText().trim();
		if (message.isEmpty() || !Client.loggedIn) return;

		try {
			if (message.startsWith("/")) {
				handleCommand(message);
			} else {
				sendPublicChat(message);
			}

			inputField.setText("");

		} catch (Exception ex) {
			System.err.println("Error sending message: " + ex.getMessage());
		}
	}

	private void sendPublicChat(String message) {
		try {
			// Use EXACT same sequence as working Client.java code
			if (Client.stream != null && Client.loggedIn) {

				// Exact sequence from Client.java lines 6778-6886
				Client.stream.createFrame(4);                    // Public chat packet ID = 4
				Client.stream.writeWordBigEndian(0);             // Placeholder for message-block length
				int blockStart = Client.stream.currentOffset;

				Client.stream.method425(0);                     // Chat effect (i3 in original = 0)
				Client.stream.method425(0);                     // Color code (0 = default)

				// Use TextInput.method526 for proper text encoding like the original
				Client.aStream_834.currentOffset = 0;
				TextInput.method526(message, Client.aStream_834);
				Client.stream.method441(0, Client.aStream_834.buffer, Client.aStream_834.currentOffset);

				// Patch the length at the reserved spot
				Client.stream.writeBytes(Client.stream.currentOffset - blockStart);

				// CRITICAL: Set overhead text parameters exactly like Client.java does
				String processedText = TextInput.processText(message);
				Client.myStoner.textSpoken = processedText;      // → triggers overhead
				Client.myStoner.anInt1513 = 0;                   // color index (0 = default)
				Client.myStoner.anInt1531 = 0;                   // effect type (0 = normal)
				Client.myStoner.textCycle = 150;                 // duration in cycles (150 ticks)

				// Add to chatbox
				Chatbox.pushMessage(processedText, 2, Client.myStoner.name, Client.myStoner.title, Client.myStoner.titleColor);

				System.out.println("Sent public chat with overhead: " + message);
			}

		} catch (Exception e) {
			System.err.println("Error sending public chat: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendPrivateMessage(String username, String message) {
		try {
			long usernameAsLong = TextClass.longForName(username.trim());

			// Use the game's existing PM system
			Client.inputTaken = true;
			Client.inputDialogState = 0;
			Client.messagePromptRaised = true;
			Client.promptInput = message;
			Client.stonersListAction = 3;
			Client.aLong953 = usernameAsLong;
			Client.aString1121 = "Say what u want to " + username;

			System.out.println("Initiated PM to " + username + ": " + message);

		} catch (Exception e) {
			System.err.println("Error sending PM: " + e.getMessage());
		}
	}

	private void handleCommand(String message) {
		String[] parts = message.split(" ", 2);
		String command = parts[0].toLowerCase();

		switch (command) {
			case "/pm":
			case "/tell":
			case "/msg":
				if (parts.length > 1) {
					String[] pmParts = parts[1].split(" ", 2);
					if (pmParts.length == 2) {
						sendPrivateMessage(pmParts[0], pmParts[1]);
					} else {
						showSystemMessage("Usage: /pm <username> <message>");
					}
				} else {
					showSystemMessage("Usage: /pm <username> <message>");
				}
				break;
			case "/clear":
				clearChat();
				break;
			default:
				sendPublicChat(message);
				break;
		}
	}

	private void showSystemMessage(String message) {
		SwingUtilities.invokeLater(() -> {
			try {
				Style systemStyle = chatDocument.addStyle("system_help", null);
				StyleConstants.setForeground(systemStyle, SYSTEM_COLOR);
				StyleConstants.setItalic(systemStyle, true);

				String timestamp = getCurrentTimestamp();
				chatDocument.insertString(chatDocument.getLength(),
					"[" + timestamp + "] [System] " + message + "\n", systemStyle);
				scrollToBottom();
			} catch (BadLocationException e) {
				// Ignore
			}
		});
	}

	private void clearChat() {
		try {
			chatDocument.remove(0, chatDocument.getLength());
		} catch (BadLocationException e) {
			System.err.println("Error clearing chat: " + e.getMessage());
		}
	}

	private void channelChanged(ActionEvent e) {
		int selectedChannel = channelSelector.getSelectedIndex();

		if (selectedChannel < CHANNEL_TYPES.length) {
			chatTypeView = CHANNEL_TYPES[selectedChannel];
		}

		loadChatHistory();
	}

	private void updateChannelFromGame() {
		for (int i = 0; i < CHANNEL_TYPES.length; i++) {
			if (CHANNEL_TYPES[i] == chatTypeView) {
				channelSelector.setSelectedIndex(i);
				break;
			}
		}
	}

	public void scrollToBottom() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
			verticalBar.setValue(verticalBar.getMaximum());
		});
	}

	private class ChatClickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			int pos = chatDisplay.viewToModel(e.getPoint());

			try {
				StyledDocument doc = chatDisplay.getStyledDocument();
				Element element = doc.getCharacterElement(pos);
				AttributeSet attrs = element.getAttributes();

				String styleName = (String) attrs.getAttribute(StyleConstants.NameAttribute);
				if (styleName != null && styleName.startsWith("username_")) {
					String username = styleName.substring(9);
					handleUsernameClick(username);
				}
			} catch (Exception ex) {
				// Ignore
			}
		}
	}

	private void handleUsernameClick(String username) {
		String currentText = inputField.getText();
		if (currentText.isEmpty()) {
			inputField.setText("@" + username + " ");
		} else {
			inputField.setText(currentText + " @" + username + " ");
		}
		inputField.requestFocusInWindow();
		inputField.setCaretPosition(inputField.getText().length());
	}

	@Override
	public void updateText() {
		if (Client.loggedIn) {
			updateChatDisplay();
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
		if (!Client.loggedIn) return;

		SwingUtilities.invokeLater(() -> {
			loadChatHistory();
			inputField.requestFocusInWindow();
		});

		updateChannelFromGame();

		if (refreshTimer != null) {
			refreshTimer.restart();
		} else {
			startRefreshTimer();
		}
	}

	@Override
	public void onDeactivate() {
		// Keep running for notifications
	}

	@Override
	public void updateDockText(int index, String text) {
		if (!Client.loggedIn) return;

		SwingUtilities.invokeLater(() -> {
			loadChatHistory();
		});
	}
}