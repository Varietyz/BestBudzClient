package com.bestbudz.dock.ui.panel.social.chat;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/**
 * ChatInteractionHandler - Handles CHAT user interactions only
 *
 * UPDATED: Dialogue interaction removed - now handled by DialogueModal
 * Responsibilities:
 * - Handle username clicks for PM/mentions
 * - Manage cursor changes for clickable usernames
 * - Process user input and send chat messages
 * - NO dialogue handling (moved to DialogueModal)
 */
public class ChatInteractionHandler {

	private final JTextPane chatDisplay;
	private final JTextField inputField;
	private final ChatCore chatCore;
	private final ChatInputCallback inputCallback;

	// Interface for handling input callbacks
	public interface ChatInputCallback {
		void onMessageSent(String message);
		void onSystemMessage(String message);
		void onPrivateMessageRequest(String username);
	}

	public ChatInteractionHandler(JTextPane chatDisplay, JTextField inputField,
								  ChatCore chatCore, ChatInputCallback callback) {
		this.chatDisplay = chatDisplay;
		this.inputField = inputField;
		this.chatCore = chatCore;
		this.inputCallback = callback;

		setupEventHandlers();
	}

	/**
	 * Setup all event handlers for the chat interface
	 */
	private void setupEventHandlers() {
		// Mouse click handler for chat display
		chatDisplay.addMouseListener(new ChatClickHandler());
		chatDisplay.addMouseMotionListener(new ChatMouseMotionHandler());

		// Input field action handler
		inputField.addActionListener(this::handleInputSubmission);

		// Input field focus handler for placeholder text
		inputField.addFocusListener(new InputFocusHandler());
	}

	/**
	 * Handle mouse clicks in the chat display
	 */
	private class ChatClickHandler extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			try {
				int pos = chatDisplay.viewToModel(e.getPoint());
				StyledDocument doc = chatDisplay.getStyledDocument();
				Element element = doc.getCharacterElement(pos);
				AttributeSet attrs = element.getAttributes();

				String styleName = (String) attrs.getAttribute(StyleConstants.NameAttribute);

				if (styleName != null) {
					// Only handle username clicks now - dialogue clicks removed
					if (styleName.startsWith("username_")) {
						String username = styleName.substring(9);
						if (SwingUtilities.isLeftMouseButton(e)) {
							insertUsernameIntoInput(username);
						} else if (SwingUtilities.isRightMouseButton(e)) {
							showUserContextMenu(username, e.getPoint());
						}
					}
					// Dialogue click handling REMOVED - now handled by DialogueModal
				}
			} catch (Exception ex) {
				// Ignore click errors
				System.err.println("Error handling chat click: " + ex.getMessage());
			}
		}
	}

	/**
	 * Handle mouse motion for cursor changes
	 */
	private class ChatMouseMotionHandler extends MouseMotionAdapter {
		@Override
		public void mouseMoved(MouseEvent e) {
			updateCursorForPosition(e.getPoint());
		}
	}

	/**
	 * Handle focus events for input field placeholder
	 */
	private class InputFocusHandler extends FocusAdapter {
		@Override
		public void focusGained(FocusEvent e) {
			if (inputField.getText().equals("Message...")) {
				inputField.setText("");
				inputField.setForeground(ChatRenderer.getTextColor());
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			if (inputField.getText().trim().isEmpty()) {
				inputField.setText("Message...");
				inputField.setForeground(new Color(114, 118, 125)); // Placeholder color
			}
		}
	}

	/**
	 * Update cursor based on position over clickable elements
	 */
	private void updateCursorForPosition(Point point) {
		try {
			int pos = chatDisplay.viewToModel(point);
			StyledDocument doc = chatDisplay.getStyledDocument();
			Element element = doc.getCharacterElement(pos);
			AttributeSet attrs = element.getAttributes();

			String styleName = (String) attrs.getAttribute(StyleConstants.NameAttribute);
			// Only usernames are clickable now - dialogue elements removed
			boolean isClickable = styleName != null && styleName.startsWith("username_");

			chatDisplay.setCursor(isClickable ?
				Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) :
				Cursor.getDefaultCursor());
		} catch (Exception e) {
			chatDisplay.setCursor(Cursor.getDefaultCursor());
		}
	}

	/**
	 * Insert username into input field for mentions
	 */
	private void insertUsernameIntoInput(String username) {
		String currentText = inputField.getText();

		// Clear placeholder text
		if (currentText.equals("Message...")) {
			currentText = "";
		}

		// Add username mention
		String newText = currentText.isEmpty() ?
			"@" + username + " " :
			currentText + " @" + username + " ";

		inputField.setText(newText);
		inputField.setForeground(ChatRenderer.getTextColor());
		inputField.requestFocusInWindow();
		inputField.setCaretPosition(newText.length());
	}

	/**
	 * Show context menu for user interactions
	 */
	private void showUserContextMenu(String username, Point location) {
		JPopupMenu contextMenu = new JPopupMenu();

		// Add to friends option
		JMenuItem addFriend = new JMenuItem("Add Friend");
		addFriend.addActionListener(e -> {
			// TODO: Implement add friend functionality
			inputCallback.onSystemMessage("Add friend feature not yet implemented");
		});

		// Private message option
		JMenuItem privateMessage = new JMenuItem("Send PM");
		privateMessage.addActionListener(e -> {
			inputCallback.onPrivateMessageRequest(username);
		});

		// Mention option
		JMenuItem mention = new JMenuItem("Mention User");
		mention.addActionListener(e -> {
			insertUsernameIntoInput(username);
		});

		// Ignore option
		JMenuItem ignore = new JMenuItem("Ignore User");
		ignore.addActionListener(e -> {
			// TODO: Implement ignore functionality
			inputCallback.onSystemMessage("Ignore feature not yet implemented");
		});

		contextMenu.add(mention);
		contextMenu.add(privateMessage);
		contextMenu.addSeparator();
		contextMenu.add(addFriend);
		contextMenu.add(ignore);

		// Show menu at click location
		SwingUtilities.convertPointToScreen(location, chatDisplay);
		SwingUtilities.convertPointFromScreen(location, chatDisplay.getParent());
		contextMenu.show(chatDisplay, location.x, location.y);
	}

	/**
	 * Handle input field submission
	 */
	private void handleInputSubmission(ActionEvent e) {
		String message = inputField.getText().trim();

		// Validate input
		if (message.equals("Message...") || message.isEmpty()) {
			return;
		}

		// Validate message length and content
		if (!chatCore.isValidMessage(message)) {
			inputCallback.onSystemMessage("Invalid message - too long or empty");
			return;
		}

		// Check if it's a private message
		if (message.toLowerCase().startsWith("pm to ")) {
			handlePrivateMessage(message);
		} else if (message.startsWith("/")) {
			handleChatCommand(message);
		} else {
			// Regular public message
			inputCallback.onMessageSent(message);
		}

		// Clear input field
		clearInputField();
	}

	/**
	 * Handle private message sending
	 */
	private void handlePrivateMessage(String message) {
		try {
			// Parse PM format: "PM to username: message"
			String[] parts = message.split(":", 2);
			if (parts.length == 2) {
				String usernamePart = parts[0].trim();
				String actualMessage = parts[1].trim();

				// Extract username from "PM to username"
				if (usernamePart.toLowerCase().startsWith("pm to ")) {
					String targetUsername = usernamePart.substring(6).trim();

					// Send through chat core
					chatCore.sendPrivateMessage(targetUsername, actualMessage);
					inputCallback.onSystemMessage("PM sent to " + targetUsername);
				}
			} else {
				inputCallback.onSystemMessage("Invalid PM format. Use: PM to username: message");
			}
		} catch (Exception ex) {
			inputCallback.onSystemMessage("Error sending private message");
			System.err.println("Error handling PM: " + ex.getMessage());
		}
	}

	/**
	 * Handle chat commands (like /help, /clear, etc.)
	 */
	private void handleChatCommand(String command) {
		String cmd = command.toLowerCase();

		switch (cmd) {
			case "/help":
				inputCallback.onSystemMessage("Commands: /help, /clear, /time");
				inputCallback.onSystemMessage("Click usernames to mention or right-click for options");
				break;

			case "/clear":
				// This would need to be handled by the parent component
				inputCallback.onSystemMessage("Chat cleared");
				break;

			case "/time":
				inputCallback.onSystemMessage("Current time: " +
					chatCore.getFormattedTimestamp(System.currentTimeMillis()));
				break;

			default:
				inputCallback.onSystemMessage("Unknown command: " + command);
				inputCallback.onSystemMessage("Type /help for available commands");
				break;
		}
	}

	/**
	 * Clear and reset input field to placeholder state
	 */
	private void clearInputField() {
		inputField.setText("Message...");
		inputField.setForeground(new Color(114, 118, 125)); // Placeholder color
	}

	/**
	 * Focus input field and clear placeholder if needed
	 */
	public void focusInput() {
		inputField.requestFocusInWindow();
		if (inputField.getText().equals("Message...")) {
			inputField.setText("");
			inputField.setForeground(ChatRenderer.getTextColor());
		}
	}

	/**
	 * Check if input field has real content (not placeholder)
	 */
	public boolean hasInputContent() {
		String text = inputField.getText().trim();
		return !text.isEmpty() && !text.equals("Message...");
	}

	/**
	 * Get current input text (excluding placeholder)
	 */
	public String getInputText() {
		String text = inputField.getText().trim();
		return text.equals("Message...") ? "" : text;
	}

	/**
	 * Set input text programmatically
	 */
	public void setInputText(String text) {
		inputField.setText(text);
		inputField.setForeground(ChatRenderer.getTextColor());
		inputField.setCaretPosition(text.length());
	}

	/**
	 * Prepare input for private message to specific user
	 */
	public void startPrivateMessage(String username) {
		String pmText = "PM to " + username + ": ";
		setInputText(pmText);
		inputField.requestFocusInWindow();
	}

	/**
	 * Handle channel switching
	 */
	public void onChannelChanged(int newChannel) {
		String channelName = chatCore.getChannelName(newChannel);
		inputCallback.onSystemMessage("Switched to " + channelName + " channel");
	}

	/**
	 * Clean up resources
	 */
	public void dispose() {
		// Remove event listeners if needed
		// Clear references
	}
}