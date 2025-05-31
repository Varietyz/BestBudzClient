package com.bestbudz.dock.ui.panel.social;

import com.bestbudz.dock.util.RainbowHoverUtil;
import com.bestbudz.dock.ui.panel.social.chat.ChatCore;
import com.bestbudz.dock.ui.panel.social.chat.ChatInteractionHandler;
import com.bestbudz.dock.ui.panel.social.chat.ChatRenderer;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.ui.panel.social.chat.ChatCore.ChatMessageData;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.config.ColorConfig.*;
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
 * Enhanced ChatPanel with Separated Chat
 *
 * UPDATED: Now uses separated ChatCore
 * Features:
 * - ChatCore: Handles chat messages only
 * - Clean separation of concerns
 */
public class ChatPanel extends JPanel implements UIPanel, DockTextUpdatable,
	ChatCore.MessageProcessor,
	ChatInteractionHandler.ChatInputCallback {

	// UI Components
	private JScrollPane scrollPane;
	private JTextPane chatDisplay;
	private JTextField inputField;
	private JComboBox<String> channelSelector;
	private StyledDocument chatDocument;
	private JPanel mainChatContainer;

	// Separated core systems
	private ChatCore chatCore;
	private ChatRenderer chatRenderer;
	private ChatInteractionHandler interactionHandler;

	// Threading for updates
	private ScheduledExecutorService updateScheduler;
	private volatile boolean isDisposed = false;
	private volatile boolean autoScroll = true;

	// Message tracking with timestamps
	private final Map<String, Long> messageOnceTimestamps = new HashMap<>();

	// Color scheme


	public ChatPanel() {
		initializeComponents();
		initializeUI();
		startUpdateScheduler();
	}

	/**
	 * Initialize core components - now with separated chatcore
	 */
	private void initializeComponents() {
		// Initialize chat core for chat functionality
		chatCore = new ChatCore(this);
	}

	/**
	 * Initialize UI components and layout
	 */
	private void initializeUI() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(10, 10, 10, 10));

		createChatDisplay();
		createInputArea();

		// Initialize helper classes after UI is created
		chatRenderer = new ChatRenderer(chatDocument, chatCore);
		interactionHandler = new ChatInteractionHandler(chatDisplay, inputField, chatCore, this);

		SwingUtilities.invokeLater(this::loadInitialMessages);
	}

	/**
	 * Create the main chat display area
	 */
	private void createChatDisplay() {
		chatDisplay = new JTextPane();
		chatDisplay.setEditable(false);
		chatDisplay.setOpaque(true);
		chatDisplay.setBackground(CHAT_BACKGROUND_COLOR);
		chatDisplay.setForeground(WHITE_UI_COLOR);
		chatDisplay.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		chatDisplay.setMargin(new Insets(10, 10, 10, 10));
		chatDocument = chatDisplay.getStyledDocument();

		chatDisplay.addMouseWheelListener(this::handleScrollWheel);

		scrollPane = new JScrollPane(chatDisplay);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createLineBorder(SCROLLBAR_COLOR, 1, true));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		styleScrollBar();

		// Simple container for chat
		mainChatContainer = new JPanel(new BorderLayout());
		mainChatContainer.setOpaque(false);
		mainChatContainer.add(scrollPane, BorderLayout.CENTER);

		add(mainChatContainer, BorderLayout.CENTER);
	}

	/**
	 * Create the input area with channel selector and send button
	 */
	private void createInputArea() {
		JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
		inputPanel.setOpaque(false);
		inputPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

		// Channel selector
		channelSelector = new JComboBox<>(ChatCore.CHANNELS);
		channelSelector.setBackground(CHAT_INPUT_BACKGROUND_COLOR);
		channelSelector.setForeground(WHITE_UI_COLOR);
		channelSelector.setBorder(BorderFactory.createLineBorder(SCROLLBAR_COLOR, 1));
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

	/**
	 * Create and configure the message input field
	 */
	private JTextField createInputField() {
		JTextField field = new JTextField();
		field.setBackground(CHAT_INPUT_BACKGROUND_COLOR);
		field.setForeground(WHITE_UI_COLOR);
		field.setCaretColor(WHITE_UI_COLOR);
		field.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(SCROLLBAR_COLOR, 1),
			new EmptyBorder(6, 12, 6, 12)
		));
		field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		field.addActionListener(this::onSendMessage);
		addInputFieldBehavior(field);
		return field;
	}

	/**
	 * Create and configure the send button
	 */
	private JButton createSendButton() {
		JButton button = new JButton("Send");
		button.setBackground(BUTTON_COLOR);
		button.setForeground(WHITE_UI_COLOR);
		button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
		button.setFont(new Font("Segoe UI", Font.BOLD, 12));
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(this::onSendMessage);

		RainbowHoverUtil.applyRainbowHover(button);


		return button;
	}

	/**
	 * Add placeholder behavior to input field
	 */
	private void addInputFieldBehavior(JTextField field) {
		final String placeholder = "";
		final Color placeholderColor = LIGHT_GRAY_COLOR;

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (field.getText().equals(placeholder)) {
					field.setText("");
					field.setForeground(WHITE_UI_COLOR);
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

	/**
	 * Style the scroll bar to match theme
	 */
	private void styleScrollBar() {
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = SCROLLBAR_COLOR;
				this.trackColor = CHAT_BACKGROUND_COLOR;
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

	/**
	 * Start the update scheduler for periodic message checking
	 */
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

	/**
	 * Check for new message updates
	 */
	private void checkForUpdates() {
		if (!Client.loggedIn || isDisposed) return;

		try {
			boolean hasNewMessages = false;

			// Check for regular chat messages
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

	// ChatInteractionHandler.ChatInputCallback implementation
	@Override
	public void onMessageSent(String message) {
		try {
			chatCore.sendPublicChat(message);
			inputField.setText("Message...");
			inputField.setForeground(LIGHT_GRAY_COLOR); // Reset to placeholder
			inputField.requestFocusInWindow();
		} catch (Exception ex) {
			System.err.println("Error sending message: " + ex.getMessage());
		}
	}

	@Override
	public void onPrivateMessageRequest(String username) {
		try {
			interactionHandler.startPrivateMessage(username);
		} catch (Exception ex) {
			System.err.println("Error starting private message: " + ex.getMessage());
		}
	}


	/**
	 * Update the chat display
	 */
	private void updateDisplay() {
		if (!Client.loggedIn || isDisposed) return;

		try {
			chatDocument.remove(0, chatDocument.getLength());

			int currentChannel = channelSelector.getSelectedIndex();
			List<ChatMessageData> messages = getGameMessages(currentChannel);

			if (messages.isEmpty()) {
				chatRenderer.addWelcomeMessage();
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
	 * Get CHAT messages from game chatbox
	 */
	private List<ChatMessageData> getGameMessages(int currentChannel) {
		List<ChatMessageData> messages = new ArrayList<>();

		for (int i = 0; i < 500; i++) {
			if (Chatbox.chatMessages[i] != null && !Chatbox.chatMessages[i].trim().isEmpty()) {
				String message = Chatbox.chatMessages[i];
				String username = (i < Chatbox.chatNames.length) ? Chatbox.chatNames[i] : null;
				int messageType = (i < Chatbox.chatTypes.length) ? Chatbox.chatTypes[i] : 0;
				int rights = (Chatbox.chatRights != null && i < Chatbox.chatRights.length) ? Chatbox.chatRights[i] : 0;

				String title = null;
				String titleColor = null;
				try {
					title = getChatTitle(i);
					titleColor = getChatTitleColor(i);
				} catch (Exception e) {
					// Fallback if title access fails
				}

				if (chatCore.shouldShowMessage(messageType, currentChannel)) {
					// Create unique identifier for message
					String messageId = message + "|" + (username != null ? username : "") + "|" + messageType;

					// Assign timestamp once per unique message
					if (!messageOnceTimestamps.containsKey(messageId)) {
						messageOnceTimestamps.put(messageId, System.currentTimeMillis());
					}

					long timestamp = messageOnceTimestamps.get(messageId);

					ChatMessageData msgData = new ChatMessageData(message, username, messageType, rights, title, titleColor, timestamp);
					messages.add(msgData);
				}
			}
		}

		// Clean up old timestamps to prevent memory leaks
		if (messageOnceTimestamps.size() > 1000) {
			List<Map.Entry<String, Long>> sortedEntries = new ArrayList<>(messageOnceTimestamps.entrySet());
			sortedEntries.sort(Map.Entry.<String, Long>comparingByValue().reversed());

			messageOnceTimestamps.clear();
			for (int i = 0; i < 500 && i < sortedEntries.size(); i++) {
				Map.Entry<String, Long> entry = sortedEntries.get(i);
				messageOnceTimestamps.put(entry.getKey(), entry.getValue());
			}
		}

		// Sort messages by timestamp (oldest first)
		messages.sort((a, b) -> Long.compare(a.timestamp, b.timestamp));
		return messages;
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
	 * Render all messages using ChatRenderer
	 */
	private void renderMessages(List<ChatMessageData> messages) throws BadLocationException {
		// Show last 100 messages for performance
		int startIndex = Math.max(0, messages.size() - 100);

		for (int i = startIndex; i < messages.size(); i++) {
			ChatMessageData msg = messages.get(i);
			chatRenderer.renderMessage(msg);
		}
	}

	/**
	 * Add system message using ChatRenderer
	 */
	private void appendSystemMessage(String message) {
		try {
			chatRenderer.appendSystemMessage(message);
			scrollToBottom();
		} catch (BadLocationException e) {
			// Ignore
		}
	}

	/**
	 * Load initial messages on startup
	 */
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

		onMessageSent(message); // Call the callback method directly
	}

	private void onChannelChange(ActionEvent e) {
		int selectedChannel = channelSelector.getSelectedIndex();
		chatCore.setGameChatTypeView(selectedChannel);

		// Notify interaction handler about channel change
		interactionHandler.onChannelChanged(selectedChannel);

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

		// Dispose both cores
		if (chatCore != null) {
			chatCore.dispose();
		}


		// Dispose interaction handler
		if (interactionHandler != null) {
			interactionHandler.dispose();
		}
	}
}