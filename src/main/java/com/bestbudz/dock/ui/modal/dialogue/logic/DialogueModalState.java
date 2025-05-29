package com.bestbudz.dock.ui.modal.dialogue.logic;

import com.bestbudz.dock.ui.modal.dialogue.logic.DialogueCore.DialogueData;

/**
 * SIMPLE DialogueModalState - No over-engineering
 */
public class DialogueModalState {

	private DialogueData currentDialogue;
	private boolean isProcessingClick = false;
	private boolean waitingForNextDialogue = false;
	private long lastActionTime = 0;
	private static final long TIMEOUT = 8000; // 8 seconds timeout

	public DialogueData getCurrentDialogue() {
		return currentDialogue;
	}

	public void setCurrentDialogue(DialogueData dialogue) {
		System.out.println("Setting dialogue: " + (dialogue != null ? dialogue.dialogueId : "null"));
		this.currentDialogue = dialogue;
		// Reset states when new dialogue arrives
		if (dialogue != null) {
			resetProcessingState();
		}
	}

	public boolean isProcessingClick() {
		// Check for timeout
		if (isProcessingClick && lastActionTime > 0) {
			long elapsed = System.currentTimeMillis() - lastActionTime;
			if (elapsed > TIMEOUT) {
				System.out.println("Processing click timeout - resetting");
				isProcessingClick = false;
				lastActionTime = 0;
			}
		}
		return isProcessingClick;
	}

	public void setProcessingClick(boolean processing) {
		System.out.println("Setting processing click: " + processing);
		this.isProcessingClick = processing;
		if (processing) {
			lastActionTime = System.currentTimeMillis();
		} else {
			lastActionTime = 0;
		}
	}

	public boolean isWaitingForNextDialogue() {
		// Check for timeout
		if (waitingForNextDialogue && lastActionTime > 0) {
			long elapsed = System.currentTimeMillis() - lastActionTime;
			if (elapsed > TIMEOUT) {
				System.out.println("Waiting timeout - resetting");
				waitingForNextDialogue = false;
				lastActionTime = 0;
			}
		}
		return waitingForNextDialogue;
	}

	public void setWaitingForNextDialogue(boolean waiting) {
		System.out.println("Setting waiting for next dialogue: " + waiting);
		this.waitingForNextDialogue = waiting;
		if (waiting) {
			lastActionTime = System.currentTimeMillis();
		} else {
			lastActionTime = 0;
		}
	}

	public void resetProcessingState() {
		System.out.println("Resetting all processing states");
		isProcessingClick = false;
		waitingForNextDialogue = false;
		lastActionTime = 0;
	}

	public void onNewDialogueReceived() {
		System.out.println("New dialogue received - resetting states");
		resetProcessingState();
	}

	public boolean shouldStayOpen() {
		return waitingForNextDialogue;
	}

	public void clear() {
		System.out.println("Clearing all state data");
		currentDialogue = null;
		resetProcessingState();
	}
}