package com.bestbudz.dock.ui.modal.dialogue;

import com.bestbudz.dock.ui.modal.style.ModalStyle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Hybrid DialogueModal - Combines the best of both implementations
 * - Uses the excellent packet handling from the fixed implementation
 * - Uses the simple, working modal display from the previous implementation
 */
public class DialogueModal extends JDialog {

	private final DialogueCore dialogueCore;
	private final Runnable onCloseCallback;

	// UI Components
	private JLabel npcNameLabel;
	private JTextArea dialogueTextArea;
	private JPanel optionsPanel;
	private JButton continueButton;
	private JTextField inputField;
	private JButton submitButton;
	private JLabel statusLabel;

	// State management
	private Point mousePosition;
	private boolean isProcessingClick = false;
	private Timer statusTimer;

	public DialogueModal(Window parent, DialogueCore dialogueCore, Runnable onCloseCallback) {
		super((parent instanceof Frame) ? (Frame) parent : null, "Dialogue", true);
		this.dialogueCore = dialogueCore;
		this.onCloseCallback = onCloseCallback;

		captureMousePosition();
		initializeUI();
		setupEvents();
	}

	private void captureMousePosition() {
		try {
			mousePosition = MouseInfo.getPointerInfo().getLocation();
			System.out.println("🎭 Mouse captured at: " + mousePosition);
		} catch (Exception e) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			mousePosition = new Point(screenSize.width / 2, screenSize.height / 2);
		}
	}

	private void initializeUI() {
		// Apply main modal styling from ModalStyle
		ModalStyle.styleMainDialog(this);

		JPanel mainPanel = (JPanel) getContentPane();

		// Create title panel
		JPanel titlePanel = createTitlePanel();
		mainPanel.add(titlePanel, BorderLayout.NORTH);

		// Create content panel
		JPanel contentPanel = ModalStyle.createContentPanel();
		contentPanel.setLayout(new BorderLayout());

		// NPC Name
		npcNameLabel = new JLabel("NPC", SwingConstants.CENTER);
		npcNameLabel.setFont(ModalStyle.FONT_TITLE);
		npcNameLabel.setForeground(ModalStyle.TEXT_ACCENT);
		npcNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		contentPanel.add(npcNameLabel, BorderLayout.NORTH);

		// Dialogue text area
		dialogueTextArea = new JTextArea();
		dialogueTextArea.setFont(ModalStyle.FONT_CONTENT);
		dialogueTextArea.setForeground(ModalStyle.TEXT_PRIMARY);
		dialogueTextArea.setBackground(ModalStyle.BACKGROUND_CONTENT);
		dialogueTextArea.setEditable(false);
		dialogueTextArea.setWrapStyleWord(true);
		dialogueTextArea.setLineWrap(true);
		dialogueTextArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JScrollPane scrollPane = new JScrollPane(dialogueTextArea);
		scrollPane.setBackground(ModalStyle.BACKGROUND_CONTENT);
		scrollPane.setBorder(null);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contentPanel.add(scrollPane, BorderLayout.CENTER);

		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// Create button panel
		JPanel buttonPanel = ModalStyle.createButtonPanel();
		buttonPanel.setLayout(new BorderLayout());

		// Status label
		statusLabel = new JLabel("", SwingConstants.CENTER);
		statusLabel.setFont(ModalStyle.FONT_SMALL);
		statusLabel.setForeground(ModalStyle.TEXT_SECONDARY);
		buttonPanel.add(statusLabel, BorderLayout.NORTH);

		// Options panel
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.setBackground(ModalStyle.BACKGROUND_PANEL);
		buttonPanel.add(optionsPanel, BorderLayout.CENTER);

		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setSize(ModalStyle.MODAL_SIZE_MEDIUM);
		positionAtMouse();
	}

	private JPanel createTitlePanel() {
		JPanel titlePanel = ModalStyle.createTitlePanel("Dialogue");

		// Get the close button and add our action
		Component[] components = titlePanel.getComponents();
		for (Component comp : components) {
			if (comp instanceof JButton && "×".equals(((JButton) comp).getText())) {
				((JButton) comp).addActionListener(e -> closeDialog());
				break;
			}
		}

		return titlePanel;
	}

	private void setupEvents() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeDialog();
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					closeDialog();
				} else if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
					if (!dialogueCore.isInputDialog()) {
						handleContinue();
					} else if (inputField != null && inputField.isVisible()) {
						handleInputSubmit();
					}
				} else if (e.getKeyCode() >= KeyEvent.VK_1 && e.getKeyCode() <= KeyEvent.VK_5) {
					// Handle number key shortcuts for options
					int option = e.getKeyCode() - KeyEvent.VK_1 + 1;
					if (dialogueCore.getDialogueType() == DialogueCore.TYPE_OPTION && option <= dialogueCore.getOptions().length) {
						handleOptionSelect(option);
					}
				}
			}
		});
		setFocusable(true);
	}

	/**
	 * Main display method - uses DialogueCore data
	 */
	public void displayDialogue() {
		SwingUtilities.invokeLater(() -> {
			if (!dialogueCore.isDialogueActive()) {
				System.out.println("🎭 No active dialogue in DialogueCore");
				return;
			}

			int dialogueType = dialogueCore.getDialogueType();
			System.out.println("🎭 Displaying dialogue type: " + dialogueType + " for interface: " + dialogueCore.getCurrentInterfaceId());

			// Update NPC name
			updateNpcName();

			// Update dialogue text
			updateDialogueText();

			// Clear previous options
			optionsPanel.removeAll();

			// Show appropriate interface based on dialogue type
			switch (dialogueType) {
				case DialogueCore.TYPE_NPC_DIALOGUE:
				case DialogueCore.TYPE_PLAYER_DIALOGUE:
				case DialogueCore.TYPE_STATEMENT:
					showContinueInterface();
					break;
				case DialogueCore.TYPE_OPTION:
					showOptionsInterface();
					break;
				case DialogueCore.TYPE_ITEM:
				case DialogueCore.TYPE_ITEM_CREATION:
				case DialogueCore.TYPE_ITEM_SELECTION:
					showItemInterface();
					break;
				case DialogueCore.TYPE_INPUT:
					showInputInterface();
					break;
				case DialogueCore.TYPE_INFORMATION_BOX:
					showInformationInterface();
					break;
				default:
					showContinueInterface();
					break;
			}

			// Adjust size and position
			adjustSizeAndPosition();

			// Make visible if not already
			if (!isVisible()) {
				setVisible(true);
				toFront();
				requestFocus();
			}

			// Refresh display
			optionsPanel.revalidate();
			optionsPanel.repaint();
		});
	}

	private void updateNpcName() {
		String name = "Dialogue";

		if (dialogueCore.getNPCId() > 0) {
			name = "NPC #" + dialogueCore.getNPCId();
		} else if (dialogueCore.isPlayerDialogue()) {
			name = "Player";
		} else if (dialogueCore.getDialogueType() == DialogueCore.TYPE_OPTION) {
			name = "Choose Option";
		} else if (dialogueCore.isInputDialog()) {
			name = "Input Required";
		}

		// Check if we have a title from DialogueCore
		if (!dialogueCore.getTitle().isEmpty()) {
			name = dialogueCore.getTitle();
		}

		npcNameLabel.setText(name);
	}

	private void updateDialogueText() {
		StringBuilder text = new StringBuilder();

		// Add title if present
		if (!dialogueCore.getTitle().isEmpty()) {
			text.append(dialogueCore.getTitle()).append("\n\n");
		}

		// Add dialogue lines
		String[] lines = dialogueCore.getLines();
		for (String line : lines) {
			if (line != null && !line.trim().isEmpty()) {
				if (text.length() > 0) text.append("\n");
				text.append(line.trim());
			}
		}

		// If no text, show interface info
		if (text.length() == 0) {
			text.append("Interface ").append(dialogueCore.getCurrentInterfaceId()).append(" (loading...)");
		}

		dialogueTextArea.setText(text.toString());
		dialogueTextArea.setCaretPosition(0);
	}

	private void showContinueInterface() {
		continueButton = ModalStyle.createPrimaryButton("Continue");
		continueButton.addActionListener(e -> handleContinue());

		optionsPanel.add(Box.createVerticalStrut(10));
		optionsPanel.add(continueButton);

		showStatus("Press Space or click Continue", ModalStyle.TEXT_SECONDARY, 3000);
	}

	private void showOptionsInterface() {
		String[] options = dialogueCore.getOptions();

		for (int i = 0; i < options.length; i++) {
			if (options[i] != null && !options[i].trim().isEmpty()) {
				final int optionIndex = i + 1; // Options are 1-based
				JButton optionButton = ModalStyle.createOptionButton(options[i], optionIndex);
				optionButton.addActionListener(e -> handleOptionSelect(optionIndex));

				optionsPanel.add(Box.createVerticalStrut(5));
				optionsPanel.add(optionButton);
			}
		}

		showStatus("Press number key (1-" + options.length + ") or click option", ModalStyle.TEXT_SECONDARY, 5000);
	}

// FIXED: Enhanced showItemInterface method for DialogueModal.java
// Replace the showItemInterface method in your DialogueModal class with this improved version

	private void showItemInterface() {
		// Show items if available
		int[] itemIds = dialogueCore.getItemIds();
		String[] itemTexts = dialogueCore.getItemTexts();
		int[] itemAmounts = dialogueCore.getItemAmounts();

		System.out.println("🎭 MODAL: Showing item interface with " + itemIds.length + " items");
		for (int i = 0; i < itemIds.length; i++) {
			System.out.println("🎭 MODAL: Item " + i + " - ID: " + itemIds[i] +
				", Amount: " + (itemAmounts.length > i ? itemAmounts[i] : 1) +
				", Text: '" + (itemTexts.length > i ? itemTexts[i] : "") + "'");
		}

		if (itemIds.length > 0) {
			JPanel itemDisplayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
			itemDisplayPanel.setBackground(ModalStyle.BACKGROUND_PANEL);

			for (int i = 0; i < itemIds.length; i++) {
				if (itemIds[i] > 0) {
					// Get item text - prefer itemTexts array, fallback to extracting from dialogue lines
					String itemText = "";
					if (itemTexts.length > i && itemTexts[i] != null && !itemTexts[i].trim().isEmpty()) {
						itemText = itemTexts[i];
					} else {
						// Fallback: try to get text from dialogue lines
						String[] lines = dialogueCore.getLines();
						if (lines.length > 0 && lines[0] != null && !lines[0].trim().isEmpty()) {
							itemText = lines[0]; // Use first line as item description
						} else {
							// Last resort: use a descriptive name
							itemText = "Item " + itemIds[i];
						}
					}

					int amount = (itemAmounts.length > i) ? itemAmounts[i] : 1;

					JPanel itemPanel = ModalStyle.createItemPanel(itemIds[i], amount, itemText);

					// Make items clickable for selection dialogues
					if (dialogueCore.isItemSelectionDialog() || dialogueCore.isItemCreationDialog()) {
						final int itemIndex = i + 1; // 1-based for server
						itemPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
						itemPanel.addMouseListener(new MouseAdapter() {
							@Override
							public void mouseClicked(MouseEvent e) {
								System.out.println("🎭 MODAL: Item " + itemIndex + " clicked");
								handleOptionSelect(itemIndex);
							}
							@Override
							public void mouseEntered(MouseEvent e) {
								itemPanel.setBorder(BorderFactory.createLineBorder(ModalStyle.ACCENT_GOLD, 2));
							}
							@Override
							public void mouseExited(MouseEvent e) {
								itemPanel.setBorder(BorderFactory.createLineBorder(ModalStyle.ACCENT_GOLD, 1));
							}
						});
					}

					itemDisplayPanel.add(itemPanel);
				}
			}

			optionsPanel.add(itemDisplayPanel);
		}

		// Add continue button for simple item displays
		if (!dialogueCore.isItemSelectionDialog() && !dialogueCore.isItemCreationDialog()) {
			continueButton = ModalStyle.createPrimaryButton("Continue");
			continueButton.addActionListener(e -> handleContinue());
			optionsPanel.add(Box.createVerticalStrut(10));
			optionsPanel.add(continueButton);
		}

		String statusMsg = dialogueCore.isItemSelectionDialog() ? "Click an item to select it" : "Press Space or click Continue";
		showStatus(statusMsg, ModalStyle.TEXT_SECONDARY, 5000);
	}

	private void showInputInterface() {
		// Add input prompt
		String prompt = dialogueCore.getInputPrompt();
		if (!prompt.isEmpty()) {
			JLabel promptLabel = ModalStyle.createTextLabel(prompt, ModalStyle.TEXT_PRIMARY);
			optionsPanel.add(promptLabel);
		}

		// Add input field
		inputField = ModalStyle.createInputField("Enter text...");
		inputField.addActionListener(e -> handleInputSubmit());
		optionsPanel.add(Box.createVerticalStrut(10));
		optionsPanel.add(inputField);

		// Add submit button
		submitButton = ModalStyle.createPrimaryButton("Submit");
		submitButton.addActionListener(e -> handleInputSubmit());
		optionsPanel.add(Box.createVerticalStrut(8));
		optionsPanel.add(submitButton);

		// Focus the input field
		SwingUtilities.invokeLater(() -> inputField.requestFocus());

		showStatus("Enter text and press Enter", ModalStyle.TEXT_SECONDARY, 5000);
	}

	private void showInformationInterface() {
		JButton closeButton = ModalStyle.createPrimaryButton("Close");
		closeButton.addActionListener(e -> closeDialog());

		optionsPanel.add(Box.createVerticalStrut(10));
		optionsPanel.add(closeButton);

		showStatus("Information dialogue", ModalStyle.TEXT_SECONDARY, 3000);
	}

	private void adjustSizeAndPosition() {
		// Calculate appropriate size based on content
		int dialogueType = dialogueCore.getDialogueType();
		int lineCount = dialogueCore.getLines().length;
		int optionCount = dialogueCore.getOptions().length;

		Dimension newSize = ModalStyle.getModalSize(dialogueType, lineCount, optionCount);

		// Adjust for item displays
		if (dialogueCore.getItemIds().length > 0) {
			newSize.width = Math.max(newSize.width, 500);
		}

		// Adjust for options
		if (optionCount > 3) {
			newSize.height = Math.max(newSize.height, 400);
		}

		setSize(newSize);
		positionAtMouse();
	}

	private void positionAtMouse() {
		if (mousePosition != null) {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension modalSize = getSize();

			int x = mousePosition.x + 10;
			int y = mousePosition.y + 10;

			// Keep on screen
			if (x + modalSize.width > screenSize.width) {
				x = mousePosition.x - modalSize.width - 10;
			}
			if (y + modalSize.height > screenSize.height) {
				y = mousePosition.y - modalSize.height - 10;
			}

			x = Math.max(0, Math.min(x, screenSize.width - modalSize.width));
			y = Math.max(0, Math.min(y, screenSize.height - modalSize.height));

			setLocation(x, y);
		} else {
			setLocationRelativeTo(null);
		}
	}

	// Event handlers - use DialogueCore for packet sending
	private void handleContinue() {
		if (isProcessingClick) return;
		isProcessingClick = true;

		System.out.println("🎭 Continue clicked for interface " + dialogueCore.getCurrentInterfaceId());

		// Send the packet through DialogueCore
		dialogueCore.handleContinue();

		// Close dialog after a short delay to ensure packet is sent
		SwingUtilities.invokeLater(() -> {
			isProcessingClick = false;
			closeDialog();
		});
	}

	private void handleOptionSelect(int option) {
		if (isProcessingClick) return;
		isProcessingClick = true;

		System.out.println("🎭 Option " + option + " selected for interface " + dialogueCore.getCurrentInterfaceId());

		// Send the packet through DialogueCore
		dialogueCore.handleOptionSelect(option);

		// Close dialog after a short delay to ensure packet is sent
		SwingUtilities.invokeLater(() -> {
			isProcessingClick = false;
			closeDialog();
		});
	}

	private void handleInputSubmit() {
		if (inputField != null) {
			String input = inputField.getText().trim();
			if (!input.isEmpty() && !input.equals("Enter text...")) {
				System.out.println("🎭 Input submitted: " + input);

				// Send the packet through DialogueCore
				dialogueCore.handleInputSubmit(input);

				// Close dialog after a short delay to ensure packet is sent
				SwingUtilities.invokeLater(() -> {
					closeDialog();
				});
			}
		}
	}

	private void closeDialog() {
		System.out.println("🎭 Closing dialog");

		dialogueCore.handleDialogueClose();
		setVisible(false);
		if (onCloseCallback != null) {
			onCloseCallback.run();
		}
	}

	// Status message handling
	private void showStatus(String message, Color color, int durationMs) {
		if (statusTimer != null && statusTimer.isRunning()) {
			statusTimer.stop();
		}

		statusLabel.setText(message);
		statusLabel.setForeground(color);

		statusTimer = new Timer(durationMs, e -> {
			statusLabel.setText("");
			statusLabel.setForeground(ModalStyle.TEXT_SECONDARY);
		});
		statusTimer.setRepeats(false);
		statusTimer.start();
	}

	// Public methods for UIModalManager compatibility
	public void showDialogue(int interfaceId, Object rsInterface) {
		displayDialogue();
	}

	public void refreshUI() {
		displayDialogue();
	}

	public void showInputPrompt(String inputType, String prompt) {
		dialogueCore.handleInputPrompt(prompt);
		displayDialogue();
	}

	public void onServerCloseDialogue() {
		SwingUtilities.invokeLater(() -> {
			setVisible(false);
			if (onCloseCallback != null) {
				onCloseCallback.run();
			}
		});
	}

	public boolean isDialogueActive() {
		return isVisible() && dialogueCore.isDialogueActive();
	}

	public int getCurrentInterfaceId() {
		return dialogueCore.getCurrentInterfaceId();
	}

	@Override
	public void dispose() {
		if (statusTimer != null && statusTimer.isRunning()) {
			statusTimer.stop();
		}
		System.out.println("🎭 Disposing dialog");
		super.dispose();
	}
}