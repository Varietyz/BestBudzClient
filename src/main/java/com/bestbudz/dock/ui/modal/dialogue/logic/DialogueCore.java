package com.bestbudz.dock.ui.modal.dialogue.logic;

import com.bestbudz.dock.ui.panel.social.chat.ChatCore;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.dock.ui.modal.dialogue.helper.DialogueExtractor;

/**
 * MINIMAL DialogueCore - Just send packets, no complex state management
 */
public class DialogueCore {

	public static class DialogueData extends ChatCore.ChatMessageData
	{
		public String npcName;
		public String[] options;
		public int dialogueId;
		public boolean isOptions;
		public boolean isContinue;
		public String continueButtonText;
		public boolean hasContinueButton;
		public int[] optionChildIds;
		public int continueChildId;

		// Add missing fields that DialogueModal expects
		public String inputField;
		public boolean hasInput;
		public boolean hasInteractiveElements;
		public int inputChildId;
		public boolean isNumericInput;

		public DialogueData(String message, String npcName, int dialogueId, String[] options) {
			super(message, npcName, 20, 0);
			this.npcName = npcName;
			this.options = options;
			this.dialogueId = dialogueId;
			this.isOptions = options != null && options.length > 0;
			this.isContinue = !isOptions;
			this.timestamp = System.currentTimeMillis();

			// Initialize missing fields with defaults
			this.inputField = null;
			this.hasInput = false;
			this.hasInteractiveElements = false;
			this.inputChildId = -1;
			this.isNumericInput = false;
		}

		public void setInputField(String inputField) {
			this.inputField = inputField;
			this.hasInput = inputField != null;
		}
	}

	public interface DialogueEventListener {
		void onDialogueOpened(DialogueData dialogue);
		void onDialogueClosed();
		void onDialogueResponse(int optionIndex, int dialogueId);
	}

	private DialogueEventListener eventListener;

	public DialogueCore(DialogueEventListener listener) {
		this.eventListener = listener;
	}

	/**
	 * Send dialogue response - JUST SEND THE PACKET, NO STATE MANAGEMENT
	 */
	public void sendDialogueResponse(int optionIndex, int dialogueId, int childId) {
		try {
			if (Client.stream != null && Client.loggedIn) {
				int buttonId = childId != -1 ? childId : dialogueId;

				System.out.println("Sending button click: " + buttonId + " (option: " + optionIndex + ", dialogue: " + dialogueId + ")");

				// Just send the packet - no state management
				Client.stream.createFrame(185);
				Client.stream.writeWord(buttonId);

				if (eventListener != null) {
					eventListener.onDialogueResponse(optionIndex, dialogueId);
				}
			}
		} catch (Exception e) {
			System.err.println("Error sending dialogue response: " + e.getMessage());
		}
	}

	public void sendDialogueResponse(int optionIndex, int dialogueId) {
		sendDialogueResponse(optionIndex, dialogueId, -1);
	}

	/**
	 * Get active dialogue - MINIMAL
	 */
	public DialogueData getActiveDialogue() {
		try {
			int activeDialogueId = -1;

			if (Client.dialogID != -1) {
				activeDialogueId = Client.dialogID;
			} else if (Client.backDialogID != -1) {
				activeDialogueId = Client.backDialogID;
			} else if (Client.openInterfaceID != -1) {
				activeDialogueId = Client.openInterfaceID;
			}

			if (activeDialogueId != -1) {
				RSInterface dialogueInterface = RSInterface.interfaceCache[activeDialogueId];
				if (dialogueInterface != null) {
					String npcName = DialogueExtractor.extractNpcName(dialogueInterface);
					String dialogueText = DialogueExtractor.extractAllDialogueText(dialogueInterface);
					String[] options = DialogueExtractor.extractDialogueOptions(dialogueInterface);
					String continueButtonText = DialogueExtractor.extractContinueButtonText(dialogueInterface);

					DialogueData dialogue = new DialogueData(dialogueText, npcName, activeDialogueId, options);
					dialogue.continueButtonText = continueButtonText;
					dialogue.hasContinueButton = continueButtonText != null;
					dialogue.optionChildIds = DialogueExtractor.extractOptionChildIds(dialogueInterface);
					dialogue.continueChildId = DialogueExtractor.extractContinueChildId(dialogueInterface);

					if (eventListener != null) {
						eventListener.onDialogueOpened(dialogue);
					}

					return dialogue;
				}
			}
		} catch (Exception e) {
			System.err.println("Error getting active dialogue: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Close dialogue - MINIMAL
	 */
	public void closeDialogueInterface() {
		try {
			if (Client.backDialogID != -1) {
				Client.backDialogID = -1;
				Client.inputTaken = true;
			}
			if (Client.dialogID != -1) {
				Client.dialogID = -1;
				Client.inputTaken = true;
			}
			if (Client.openInterfaceID != -1) {
				Client.openInterfaceID = -1;
			}

			if (eventListener != null) {
				eventListener.onDialogueClosed();
			}
		} catch (Exception e) {
			System.err.println("Error closing dialogue: " + e.getMessage());
		}
	}

	public boolean isInDialogue() {
		return Client.dialogID != -1 || Client.backDialogID != -1 || Client.openInterfaceID != -1;
	}

	public void setEventListener(DialogueEventListener listener) {
		this.eventListener = listener;
	}

	public void dispose() {
		eventListener = null;
	}
}