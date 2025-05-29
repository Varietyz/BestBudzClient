package com.bestbudz.dock.ui.modal.dialogue.logic;

import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueCore.DialogueData;
import static com.bestbudz.dock.ui.modal.dialogue.helper.DialogueExtractor.isItemInterface;

import com.bestbudz.dock.ui.modal.dialogue.style.DialogueModalComponents;
import javax.swing.*;
import java.awt.Color;
import java.awt.Component;

/**
 * MINIMAL DialogueModalLogic - No state management, just direct packet sending
 */
public class DialogueModalLogic {

	private final DialogueCore dialogueCore;
	private final DialogueModalState state;
	private final StatusMessageHandler statusHandler;

	public DialogueModalLogic(DialogueCore dialogueCore, DialogueModalState state, StatusMessageHandler statusHandler) {
		this.dialogueCore = dialogueCore;
		this.state = state;
		this.statusHandler = statusHandler;
	}

	/**
	 * Handle input submission - MINIMAL
	 */
	public void handleSubmit(String inputText, DialogueData currentDialogue) {
		// Not used in your system based on server code, but keep for compatibility
		System.out.println("Input submit not implemented in minimal version");
	}

	/**
	 * Handle option selection - MINIMAL, NO STATE MANAGEMENT
	 */
	public void handleOptionClick(int optionIndex, DialogueData currentDialogue) {
		try {
			if (currentDialogue == null) {
				System.out.println("No current dialogue - ignoring option click");
				return;
			}

			System.out.println("=== OPTION CLICK (MINIMAL) ===");
			System.out.println("Option Index: " + optionIndex);
			System.out.println("DialogueID: " + currentDialogue.dialogueId);

			// Get the child ID for this option
			int childId = -1;
			if (currentDialogue.optionChildIds != null && optionIndex < currentDialogue.optionChildIds.length) {
				childId = currentDialogue.optionChildIds[optionIndex];
				System.out.println("Using child ID: " + childId);
			} else {
				System.out.println("No child ID available, using -1");
			}

			// JUST SEND THE PACKET - NO STATE MANAGEMENT
			dialogueCore.sendDialogueResponse(optionIndex, currentDialogue.dialogueId, childId);

			statusHandler.showStatus("Option selected", DialogueModalComponents.SUCCESS_COLOR, 1000);

		} catch (Exception e) {
			System.err.println("Error handling option click: " + e.getMessage());
			statusHandler.showStatus("Error selecting option", DialogueModalComponents.ERROR_COLOR, 2000);
		}
	}

	/**
	 * Handle continue button - MINIMAL, NO STATE MANAGEMENT
	 */
	public void handleContinue(DialogueData currentDialogue) {
		try {
			if (currentDialogue == null) {
				System.out.println("No current dialogue - ignoring continue");
				return;
			}

			System.out.println("=== CONTINUE CLICK (MINIMAL) ===");
			System.out.println("DialogueID: " + currentDialogue.dialogueId);
			System.out.println("Continue Child ID: " + currentDialogue.continueChildId);

			// JUST SEND THE PACKET - NO STATE MANAGEMENT
			dialogueCore.sendDialogueResponse(-1, currentDialogue.dialogueId, currentDialogue.continueChildId);

			statusHandler.showStatus("Continue clicked", DialogueModalComponents.SUCCESS_COLOR, 1000);

		} catch (Exception e) {
			System.err.println("Error handling continue: " + e.getMessage());
			statusHandler.showStatus("Error continuing dialogue", DialogueModalComponents.ERROR_COLOR, 2000);
		}
	}

	/**
	 * Determine interface type - MINIMAL
	 */
	public DialogueInterfaceType determineInterfaceType(DialogueData dialogue) {
		if (dialogue.hasInput) {
			return DialogueInterfaceType.INPUT;
		} else if (dialogue.isOptions && dialogue.options != null && dialogue.options.length > 0) {
			return DialogueInterfaceType.OPTIONS;
		} else if (dialogue.hasContinueButton) {
			return DialogueInterfaceType.CONTINUE;
		} else if (isItemInterface(dialogue.dialogueId)) {
			return DialogueInterfaceType.ITEM_INFO;
		} else {
			return DialogueInterfaceType.INFORMATION;
		}
	}

	/**
	 * Get status message - MINIMAL
	 */
	public String getStatusMessage(DialogueData dialogue) {
		DialogueInterfaceType type = determineInterfaceType(dialogue);

		switch (type) {
			case INPUT:
				return "Enter " + (dialogue.isNumericInput ? "number" : "text") + " and press Enter";
			case OPTIONS:
				if (isItemInterface(dialogue.dialogueId)) {
					return "Click an item to select it";
				} else {
					return "Press number key (1-" + dialogue.options.length + ") or click option";
				}
			case CONTINUE:
				return "Press Space or click Continue";
			case ITEM_INFO:
				return "Press Space or click Continue";
			default:
				return "";
		}
	}

	/**
	 * Update component states - MINIMAL (NO STATE MANAGEMENT)
	 */
	public void updateComponentStates(JPanel optionsPanel, JTextField inputField,
									  JButton submitButton, JButton continueButton,
									  DialogueData currentDialogue, String[] originalOptions) {
		// NO STATE MANAGEMENT - always enable everything
		inputField.setEnabled(true);
		submitButton.setEnabled(true);
		submitButton.setText("Submit");
		continueButton.setEnabled(true);

		if (currentDialogue != null && currentDialogue.continueButtonText != null) {
			continueButton.setText(currentDialogue.continueButtonText);
		} else {
			continueButton.setText("Continue");
		}

		// Enable all option buttons
		for (Component comp : optionsPanel.getComponents()) {
			if (comp instanceof JButton) {
				JButton button = (JButton) comp;
				if (button.getActionCommand() != null && button.getActionCommand().startsWith("option_")) {
					button.setEnabled(true);
					// Restore original text if needed
					if (originalOptions != null) {
						String actionCommand = button.getActionCommand();
						try {
							int optionIndex = Integer.parseInt(actionCommand.substring(7));
							if (optionIndex < originalOptions.length) {
								String optionText = originalOptions[optionIndex].trim();
								if (!optionText.matches("^\\d+\\..*")) {
									optionText = (optionIndex + 1) + ". " + optionText;
								}
								button.setText(optionText);
							}
						} catch (Exception e) {
							// Keep current text
						}
					}
				}
			}
		}
	}

	// Interface types
	public enum DialogueInterfaceType {
		INPUT,
		OPTIONS,
		CONTINUE,
		ITEM_INFO,
		INFORMATION
	}

	// Status message handler interface
	public interface StatusMessageHandler {
		void showStatus(String message, Color color, int durationMs);
	}
}