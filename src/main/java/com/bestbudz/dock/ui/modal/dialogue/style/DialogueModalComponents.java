package com.bestbudz.dock.ui.modal.dialogue.style;

import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueCore;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Factory class for creating and configuring dialogue modal UI components
 * Handles all the visual styling and component creation logic
 */
public class DialogueModalComponents {

	// Color constants
	public static final Color MODAL_BACKGROUND = new Color(40, 43, 48);
	public static final Color BORDER_COLOR = new Color(79, 84, 92);
	public static final Color TEXT_COLOR = new Color(220, 221, 222);
	public static final Color NPC_NAME_COLOR = new Color(255, 215, 0);
	public static final Color OPTION_COLOR = new Color(88, 101, 242);
	public static final Color OPTION_HOVER_COLOR = new Color(118, 131, 255);
	public static final Color SUCCESS_COLOR = new Color(87, 242, 135);
	public static final Color ERROR_COLOR = new Color(242, 87, 87);
	public static final Color WAITING_COLOR = new Color(255, 193, 7);

	private DialogueModalComponents() {
		// Utility class - prevent instantiation
	}

	/**
	 * Create the main panel with proper styling
	 */
	public static JPanel createMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBackground(MODAL_BACKGROUND);
		mainPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 3),
			new EmptyBorder(15, 20, 20, 20)
		));
		return mainPanel;
	}

	/**
	 * Create the header panel with NPC name, status, and close button
	 */
	public static JPanel createHeaderPanel(JLabel npcNameLabel, JLabel statusLabel, ActionListener closeListener) {
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setOpaque(false);
		headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

		// NPC name label
		npcNameLabel.setText("NPC");
		npcNameLabel.setForeground(NPC_NAME_COLOR);
		npcNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
		npcNameLabel.setHorizontalAlignment(SwingConstants.LEFT);

		// Status label
		statusLabel.setText("");
		statusLabel.setForeground(new Color(180, 180, 180));
		statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

		// Close button
		JButton closeButton = createCloseButton(closeListener);

		headerPanel.add(npcNameLabel, BorderLayout.WEST);
		headerPanel.add(statusLabel, BorderLayout.CENTER);
		headerPanel.add(closeButton, BorderLayout.EAST);

		return headerPanel;
	}

	/**
	 * Create the dialogue text area with scroll pane
	 */
	public static JScrollPane createDialogueArea(JTextArea dialogueTextArea) {
		dialogueTextArea.setEditable(false);
		dialogueTextArea.setOpaque(true);
		dialogueTextArea.setBackground(new Color(54, 57, 63));
		dialogueTextArea.setForeground(TEXT_COLOR);
		dialogueTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		dialogueTextArea.setLineWrap(true);
		dialogueTextArea.setWrapStyleWord(true);
		dialogueTextArea.setBorder(new EmptyBorder(15, 15, 15, 15));

		JScrollPane scrollPane = new JScrollPane(dialogueTextArea);
		scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(450, 120));

		// Style the scroll bar
		styleScrollBar(scrollPane.getVerticalScrollBar());

		return scrollPane;
	}

	/**
	 * Create the interaction panel container
	 */
	public static JPanel createInteractionPanel() {
		JPanel optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setOpaque(false);
		optionsPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
		return optionsPanel;
	}

	/**
	 * Create a continue button
	 */
	public static JButton createContinueButton(ActionListener listener) {
		JButton continueButton = new JButton("Continue");
		continueButton.setBackground(OPTION_COLOR);
		continueButton.setForeground(Color.WHITE);
		continueButton.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
		continueButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		continueButton.setFocusPainted(false);
		continueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		continueButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		continueButton.setActionCommand("continue");
		continueButton.addActionListener(listener);
		continueButton.setVisible(false);

		addHoverEffect(continueButton);
		return continueButton;
	}

	/**
	 * Create an input field with proper styling
	 */
	public static JTextField createInputField() {
		JTextField inputField = new JTextField();
		inputField.setBackground(new Color(64, 68, 75));
		inputField.setForeground(TEXT_COLOR);
		inputField.setCaretColor(TEXT_COLOR);
		inputField.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			new EmptyBorder(8, 12, 8, 12)
		));
		inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
		inputField.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputField.setMaximumSize(new Dimension(400, 35));
		inputField.setVisible(false);
		return inputField;
	}

	/**
	 * Create a submit button
	 */
	public static JButton createSubmitButton(ActionListener listener) {
		JButton submitButton = new JButton("Submit");
		submitButton.setBackground(OPTION_COLOR);
		submitButton.setForeground(Color.WHITE);
		submitButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		submitButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
		submitButton.setFocusPainted(false);
		submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		submitButton.setActionCommand("submit");
		submitButton.addActionListener(listener);
		submitButton.setVisible(false);

		addHoverEffect(submitButton);
		return submitButton;
	}

	/**
	 * Create option buttons for dialogue choices
	 */
	public static void createOptionButtons(JPanel container, String[] options, ActionListener listener) {
		container.removeAll();

		for (int i = 0; i < options.length; i++) {
			if (options[i] == null || options[i].trim().isEmpty()) {
				continue;
			}

			String optionText = options[i].trim();

			// Don't add numbers if the option already has them
			if (!optionText.matches("^\\d+\\..*")) {
				optionText = (i + 1) + ". " + optionText;
			}

			JButton optionButton = new JButton(optionText);
			optionButton.setBackground(OPTION_COLOR);
			optionButton.setForeground(Color.WHITE);
			optionButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			optionButton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
			optionButton.setFocusPainted(false);
			optionButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			optionButton.setAlignmentX(Component.CENTER_ALIGNMENT);
			optionButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, optionButton.getPreferredSize().height));

			optionButton.setActionCommand("option_" + i);
			optionButton.addActionListener(listener);

			addHoverEffect(optionButton);

			if (i > 0) {
				container.add(Box.createVerticalStrut(8));
			}

			container.add(optionButton);
		}
	}

	/**
	 * Create an information label for non-interactive dialogues
	 */
	public static JPanel createInfoPanel(String mainText, String subText) {
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setOpaque(false);

		JLabel infoLabel = new JLabel(mainText);
		infoLabel.setForeground(new Color(180, 180, 180));
		infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));

		infoPanel.add(infoLabel);

		if (subText != null && !subText.isEmpty()) {
			JLabel subLabel = new JLabel(subText);
			subLabel.setForeground(new Color(160, 160, 160));
			subLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));

			infoPanel.add(Box.createVerticalStrut(3));
			infoPanel.add(subLabel);
		}

		return infoPanel;
	}

	/**
	 * Apply numeric filter to input field
	 */
	public static void applyNumericFilter(JTextField inputField) {
		((AbstractDocument) inputField.getDocument()).setDocumentFilter(
			new DocumentFilter() {
				@Override
				public void insertString(FilterBypass fb, int offset, String string,
										 AttributeSet attr) throws BadLocationException {
					if (string.matches("\\d*")) {
						super.insertString(fb, offset, string, attr);
					}
				}

				@Override
				public void replace(FilterBypass fb, int offset, int length, String text,
									AttributeSet attrs) throws BadLocationException {
					if (text.matches("\\d*")) {
						super.replace(fb, offset, length, text, attrs);
					}
				}
			}
		);
	}

	/**
	 * Calculate optimal dialog size based on content
	 */
	public static Dimension calculateDialogSize(DialogueCore.DialogueData dialogue) {
		int minWidth = 500;
		int baseHeight = 200;

		String text = dialogue.message;
		if (text == null || text.isEmpty()) {
			text = "...";
		}

		int textLength = text.length();
		int estimatedRows = Math.max(2, (textLength / 50) + text.split("\n").length);
		estimatedRows = Math.min(estimatedRows, 12);

		int textAreaHeight = estimatedRows * 18 + 30;

		int calculatedWidth = minWidth;
		if (textLength > 200) {
			calculatedWidth = Math.min(600, minWidth + 50);
		}
		if (textLength > 400) {
			calculatedWidth = Math.min(650, minWidth + 100);
		}

		int interactionHeight = 60;
		if (dialogue.hasInput) {
			interactionHeight = 120;
		} else if (dialogue.isOptions && dialogue.options != null) {
			interactionHeight = Math.max(80, dialogue.options.length * 45 + 30);
		}

		int totalHeight = 80 + textAreaHeight + interactionHeight + 40;
		totalHeight = Math.max(totalHeight, 250);
		totalHeight = Math.min(totalHeight, 700);

		return new Dimension(calculatedWidth, totalHeight);
	}

	// Private helper methods

	private static JButton createCloseButton(ActionListener closeListener) {
		JButton closeButton = new JButton("×");
		closeButton.setForeground(TEXT_COLOR);
		closeButton.setBackground(new Color(220, 53, 69));
		closeButton.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		closeButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
		closeButton.setFocusPainted(false);
		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeButton.addActionListener(closeListener);

		// Add hover effect
		closeButton.addMouseListener(new MouseAdapter() {
			Color originalColor = closeButton.getBackground();
			@Override
			public void mouseEntered(MouseEvent e) {
				closeButton.setBackground(new Color(255, 69, 69));
			}
			@Override
			public void mouseExited(MouseEvent e) {
				closeButton.setBackground(originalColor);
			}
		});

		return closeButton;
	}

	private static void styleScrollBar(JScrollBar scrollBar) {
		scrollBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = BORDER_COLOR;
				this.trackColor = new Color(54, 57, 63);
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
	}

	private static void addHoverEffect(JButton button) {
		Color originalColor = button.getBackground();

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(OPTION_HOVER_COLOR);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(originalColor);
			}
		});
	}
}