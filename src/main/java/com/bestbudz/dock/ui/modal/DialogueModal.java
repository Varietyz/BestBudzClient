package com.bestbudz.dock.ui.modal;

import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueCore;
import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueCore.DialogueData;
import static com.bestbudz.dock.ui.modal.dialogue.helper.DialogueExtractor.isItemInterface;
import com.bestbudz.dock.ui.modal.dialogue.helper.DialogueTextCleaner;

import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueModalLogic;
import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueModalState;
import com.bestbudz.dock.ui.modal.dialogue.style.DialogueModalComponents;
import com.bestbudz.dock.ui.modal.dialogue.style.DialogueModalPositioning;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * DialogueModal - Updated to use DialogueCore instead of ChatCore for dialogue functionality
 *
 * This maintains all existing functionality while using the new separated architecture:
 * - DialogueCore: Handles all dialogue-specific logic
 * - DialogueModalComponents: UI component creation and styling
 * - DialogueModalLogic: Business logic and interaction handling
 * - DialogueModalState: State management
 * - DialogueModalPositioning: Window positioning and dragging
 *
 * CRITICAL FIXES MAINTAINED:
 * - No premature closing after continue/option clicks
 * - Waits for server to send next dialogue or close command
 * - Better option detection for Make-X and right-click menus
 * - Full support for interactive chained dialogues
 */
public class DialogueModal extends JDialog implements ActionListener, DialogueModalLogic.StatusMessageHandler {

	// Core dependencies - now using DialogueCore instead of ChatCore
	private final DialogueCore dialogueCore;
	private final Runnable onCloseCallback;

	// Separated concerns
	private final DialogueModalState state;
	private final DialogueModalLogic logic;

	// UI Components (managed by DialogueModalComponents)
	private JLabel npcNameLabel;
	private JTextArea dialogueTextArea;
	private JPanel optionsPanel;
	private JButton continueButton;
	private JTextField inputField;
	private JButton submitButton;
	private JLabel statusLabel;

	// Status management
	private Timer statusTimer;

	public DialogueModal(Window parent, DialogueCore dialogueCore, Runnable onCloseCallback) {
		super(parent, "Dialogue", ModalityType.MODELESS);
		this.dialogueCore = dialogueCore;
		this.onCloseCallback = onCloseCallback;

		// Initialize separated concerns
		this.state = new DialogueModalState();
		this.logic = new DialogueModalLogic(dialogueCore, state, this);

		initializeModal();
	}

	/**
	 * Initialize the modal dialog using component factory
	 */
	private void initializeModal() {
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setAlwaysOnTop(true);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.NONE);

		// Create main panel using component factory
		JPanel mainPanel = DialogueModalComponents.createMainPanel();

		// Initialize component references
		npcNameLabel = new JLabel();
		dialogueTextArea = new JTextArea();
		statusLabel = new JLabel();

		// Create and add components using factory methods
		createHeader(mainPanel);
		createDialogueArea(mainPanel);
		createInteractionArea(mainPanel);

		setContentPane(mainPanel);

		// Make draggable using positioning utility
		DialogueModalPositioning.makeDraggable(this, mainPanel, npcNameLabel);

		// Handle window closing
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				closeModalSafely();
			}
		});

		setupKeyBindings();
		setSize(500, 300);
	}

	/**
	 * Create header using component factory
	 */
	private void createHeader(JPanel parent) {
		JPanel headerPanel = DialogueModalComponents.createHeaderPanel(
			npcNameLabel, statusLabel, e -> closeModalSafely()
		);
		parent.add(headerPanel, BorderLayout.NORTH);
	}

	/**
	 * Create dialogue area using component factory
	 */
	private void createDialogueArea(JPanel parent) {
		JScrollPane scrollPane = DialogueModalComponents.createDialogueArea(dialogueTextArea);
		parent.add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Create interaction area using component factory
	 */
	private void createInteractionArea(JPanel parent) {
		optionsPanel = DialogueModalComponents.createInteractionPanel();

		// Create components using factory methods
		continueButton = DialogueModalComponents.createContinueButton(this);
		inputField = DialogueModalComponents.createInputField();
		submitButton = DialogueModalComponents.createSubmitButton(this);

		// Add enter key support for input field
		inputField.addActionListener(e -> handleSubmit());

		// Add components to panel
		optionsPanel.add(continueButton);
		optionsPanel.add(Box.createVerticalStrut(10));
		optionsPanel.add(inputField);
		optionsPanel.add(Box.createVerticalStrut(8));
		optionsPanel.add(submitButton);

		parent.add(optionsPanel, BorderLayout.SOUTH);
	}

	/**
	 * Setup keyboard shortcuts
	 */
	private void setupKeyBindings() {
		JRootPane rootPane = getRootPane();

		// Escape key to close
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke("ESCAPE"), "closeDialog");
		rootPane.getActionMap().put("closeDialog", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				closeModalSafely();
			}
		});

		// Number keys 1-9 for dialogue options
		for (int i = 1; i <= 9; i++) {
			final int optionIndex = i - 1;
			rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(String.valueOf(i)), "option" + i);
			rootPane.getActionMap().put("option" + i, new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DialogueData currentDialogue = state.getCurrentDialogue();
					if (currentDialogue != null && currentDialogue.isOptions &&
						currentDialogue.options != null &&
						optionIndex < currentDialogue.options.length &&
						!state.isWaitingForNextDialogue()) {
						logic.handleOptionClick(optionIndex, currentDialogue);
					}
				}
			});
		}

		// Space/Enter for continue and input submission
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke("SPACE"), "continue");
		rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
			KeyStroke.getKeyStroke("ENTER"), "continue");
		rootPane.getActionMap().put("continue", new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DialogueData currentDialogue = state.getCurrentDialogue();
				if (currentDialogue != null && !state.isWaitingForNextDialogue()) {
					if (currentDialogue.hasContinueButton && !state.isProcessingClick()) {
						logic.handleContinue(currentDialogue);
					} else if (currentDialogue.hasInput && inputField.isVisible()) {
						handleSubmit();
					}
				}
			}
		});
	}

	/**
	 * Main method to show dialogue - now much cleaner
	 */
	public void showDialogue(DialogueData dialogue) {
		state.setCurrentDialogue(dialogue);

		System.out.println("=== SHOWING DIALOGUE ===");
		System.out.println("DialogueID: " + dialogue.dialogueId);
		System.out.println("HasOptions: " + dialogue.isOptions);
		System.out.println("HasInput: " + dialogue.hasInput);
		System.out.println("HasContinue: " + dialogue.hasContinueButton);
		System.out.println("IsItemInterface: " + isItemInterface(dialogue.dialogueId));
		if (dialogue.options != null) {
			System.out.println("Options: " + java.util.Arrays.toString(dialogue.options));
		}

		// Reset processing state for new dialogue
		state.resetProcessingState();

		// Set NPC name
		updateNpcName(dialogue);

		// Set dialogue text with enhanced cleaning
		updateDialogueText(dialogue);

		// Show appropriate interface based on dialogue type
		showAppropriateInterface(dialogue);

		// Adjust dialog size and position
		adjustDialogSize(dialogue);
		if (!isVisible()) {
			DialogueModalPositioning.centerOnScreen(this);
		}

		// Show the dialog
		setVisible(true);
		toFront();
		requestFocus();

		// Focus appropriate component
		focusAppropriateComponent(dialogue);

		// Refresh layout
		optionsPanel.revalidate();
		optionsPanel.repaint();

		// Show context-appropriate status message
		String statusMessage = logic.getStatusMessage(dialogue);
		if (!statusMessage.isEmpty()) {
			showStatus(statusMessage, new Color(180, 180, 180), 3000);
		}

		System.out.println("Displaying dialogue: " + dialogue.dialogueId + " - " +
			DialogueTextCleaner.cleanAndFilterText(dialogue.message)
				.substring(0, Math.min(50, DialogueTextCleaner.cleanAndFilterText(dialogue.message).length())));
	}

	/**
	 * Update NPC name display
	 */
	private void updateNpcName(DialogueData dialogue) {
		if (dialogue.npcName != null && !dialogue.npcName.equals("NPC")) {
			npcNameLabel.setText(dialogue.npcName);
		} else {
			npcNameLabel.setText(isItemInterface(dialogue.dialogueId) ? "Item Interface" : "NPC");
		}
	}

	/**
	 * Update dialogue text display
	 */
	private void updateDialogueText(DialogueData dialogue) {
		String cleanText = DialogueTextCleaner.cleanAndFilterText(dialogue.message);
		dialogueTextArea.setText(cleanText);
		dialogueTextArea.setCaretPosition(0);
	}

	/**
	 * Show appropriate interface based on dialogue type using logic handler
	 */
	private void showAppropriateInterface(DialogueData dialogue) {
		// Clear previous options
		optionsPanel.removeAll();

		DialogueModalLogic.DialogueInterfaceType interfaceType = logic.determineInterfaceType(dialogue);

		switch (interfaceType) {
			case INPUT:
				showInputInterface(dialogue);
				break;
			case OPTIONS:
				showOptionsInterface(dialogue);
				break;
			case CONTINUE:
				showContinueInterface();
				break;
			case ITEM_INFO:
				showItemInformationInterface(dialogue);
				break;
			case INFORMATION:
				showInformationInterface();
				break;
		}
	}

	/**
	 * Show input interface
	 */
	private void showInputInterface(DialogueData dialogue) {
		if (dialogue.inputField != null) {
			inputField.setText(dialogue.inputField);
		} else {
			inputField.setText("");
		}

		inputField.setVisible(true);
		submitButton.setVisible(true);
		continueButton.setVisible(false);

		optionsPanel.add(Box.createVerticalStrut(10));

		String inputType = dialogue.isNumericInput ? "number" : "text";
		JLabel inputLabel = new JLabel("Enter " + inputType + ":");
		inputLabel.setForeground(DialogueModalComponents.TEXT_COLOR);
		inputLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		inputLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
		optionsPanel.add(inputLabel);

		optionsPanel.add(Box.createVerticalStrut(5));
		optionsPanel.add(inputField);
		optionsPanel.add(Box.createVerticalStrut(8));
		optionsPanel.add(submitButton);

		// Apply numeric filter if needed
		if (dialogue.isNumericInput) {
			DialogueModalComponents.applyNumericFilter(inputField);
		}
	}

	/**
	 * Show options interface
	 */
	private void showOptionsInterface(DialogueData dialogue) {
		continueButton.setVisible(false);
		inputField.setVisible(false);
		submitButton.setVisible(false);

		DialogueModalComponents.createOptionButtons(optionsPanel, dialogue.options, this);
	}

	/**
	 * Show continue interface
	 */
	private void showContinueInterface() {
		inputField.setVisible(false);
		submitButton.setVisible(false);

		DialogueData currentDialogue = state.getCurrentDialogue();
		if (currentDialogue != null && currentDialogue.continueButtonText != null) {
			continueButton.setText(currentDialogue.continueButtonText);
		} else {
			continueButton.setText("Continue");
		}

		continueButton.setVisible(true);
		optionsPanel.add(continueButton);
	}

	/**
	 * Show item information interface
	 */
	private void showItemInformationInterface(DialogueData dialogue) {
		inputField.setVisible(false);
		submitButton.setVisible(false);

		// Only show continue for single item displays
		if (dialogue.dialogueId == 306) {
			continueButton.setVisible(true);
			continueButton.setText("Continue");
			optionsPanel.add(continueButton);
		} else {
			continueButton.setVisible(false);
			JPanel infoPanel = DialogueModalComponents.createInfoPanel(
				"Item interface - make a selection", null);
			optionsPanel.add(Box.createVerticalStrut(15));
			optionsPanel.add(infoPanel);
		}
	}

	/**
	 * Show information interface for non-interactive dialogues
	 */
	private void showInformationInterface() {
		inputField.setVisible(false);
		submitButton.setVisible(false);
		continueButton.setVisible(false);

		JPanel infoPanel = DialogueModalComponents.createInfoPanel(
			"Information dialogue - no interaction required",
			"Close dialogue to continue");

		optionsPanel.add(Box.createVerticalStrut(15));
		optionsPanel.add(infoPanel);
	}

	/**
	 * Focus appropriate component based on dialogue type
	 */
	private void focusAppropriateComponent(DialogueData dialogue) {
		if (dialogue.hasInput && inputField.isVisible()) {
			inputField.requestFocusInWindow();
			inputField.selectAll();
		} else {
			requestFocusInWindow();
		}
	}

	/**
	 * Adjust dialog size using component utility
	 */
	private void adjustDialogSize(DialogueData dialogue) {
		Dimension newSize = DialogueModalComponents.calculateDialogSize(dialogue);
		setSize(newSize);
	}

	/**
	 * Handle button clicks - delegates to logic handler
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		DialogueData currentDialogue = state.getCurrentDialogue();

		try {
			if ("continue".equals(command)) {
				logic.handleContinue(currentDialogue);
			} else if ("submit".equals(command)) {
				handleSubmit();
			} else if (command.startsWith("option_")) {
				int optionIndex = Integer.parseInt(command.substring(7));
				logic.handleOptionClick(optionIndex, currentDialogue);
			}

			// Update component states after action
			logic.updateComponentStates(optionsPanel, inputField, submitButton,
				continueButton, currentDialogue,
				currentDialogue != null ? currentDialogue.options : null);

		} catch (Exception ex) {
			System.err.println("Error handling action: " + command + " - " + ex.getMessage());
			showStatus("Error processing action", DialogueModalComponents.ERROR_COLOR, 2000);
		}
	}

	/**
	 * Handle input submission - delegates to logic handler
	 */
	private void handleSubmit() {
		DialogueData currentDialogue = state.getCurrentDialogue();
		if (currentDialogue != null) {
			String inputText = inputField.getText().trim();
			logic.handleSubmit(inputText, currentDialogue);

			// Update component states after submission
			logic.updateComponentStates(optionsPanel, inputField, submitButton,
				continueButton, currentDialogue, null);
		}
	}

	/**
	 * Implementation of StatusMessageHandler interface
	 */
	@Override
	public void showStatus(String message, Color color, int durationMs) {
		if (statusTimer != null && statusTimer.isRunning()) {
			statusTimer.stop();
		}

		statusLabel.setText(message);
		statusLabel.setForeground(color);

		// For chained dialogue messages, show longer
		if (message.contains("Waiting") || message.contains("Processing")) {
			durationMs = Math.max(durationMs, 5000);
		}

		statusTimer = new Timer(durationMs, e -> {
			if (!state.isWaitingForNextDialogue()) {
				statusLabel.setText("");
				statusLabel.setForeground(new Color(180, 180, 180));
			}
		});
		statusTimer.setRepeats(false);
		statusTimer.start();
	}

	/**
	 * Close modal safely - ONLY when user manually closes or server sends close command
	 */
	private void closeModalSafely() {
		try {
			// Cancel any running timers
			if (statusTimer != null && statusTimer.isRunning()) {
				statusTimer.stop();
			}

			// ONLY close dialogue interface when user manually closes modal
			dialogueCore.closeDialogueInterface();

			// Hide and dispose modal
			setVisible(false);
			dispose();

			// Call cleanup callback
			if (onCloseCallback != null) {
				onCloseCallback.run();
			}

			System.out.println("Modal closed safely (user initiated)");
		} catch (Exception e) {
			System.err.println("Error closing modal: " + e.getMessage());
		}
	}

	/**
	 * Check if modal should stay open for chained dialogues
	 * This method can be called by the parent to check if we're waiting for server response
	 */
	public boolean isWaitingForNextDialogue() {
		return state.isWaitingForNextDialogue();
	}

	/**
	 * Reset waiting state when new dialogue arrives
	 * This should be called by the parent when a new dialogue is detected
	 */
	public void onNewDialogueReceived() {
		state.onNewDialogueReceived();
	}

	/**
	 * Clean up resources
	 */
	@Override
	public void dispose() {
		try {
			// Cancel all timers
			if (statusTimer != null && statusTimer.isRunning()) {
				statusTimer.stop();
			}

			// Clear state
			state.clear();
			statusTimer = null;

			System.out.println("Modal disposed with cleanup");
		} catch (Exception e) {
			System.err.println("Error during modal disposal: " + e.getMessage());
		} finally {
			super.dispose();
		}
	}
}