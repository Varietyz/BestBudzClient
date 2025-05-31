package com.bestbudz.dock.ui.manager;

import com.bestbudz.dock.ui.modal.dialogue.DialogueCore;
import com.bestbudz.dock.ui.modal.dialogue.DialogueModal;
import com.bestbudz.engine.core.Client;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Iterator;
import javax.swing.Timer;

/**
 * UIModalManager - Clean Java 11 implementation
 * Manages dialogue modals using DialogueCore for packet handling
 */
public class UIModalManager {

	private final Window parentWindow;
	private final Client client;
	private DialogueModal currentDialogue;
	private DialogueCore dialogueCore;

	// Configuration
	private boolean useModalDialogues = true;
	private boolean debugMode = true;

	// Store packets until we know if it's a dialogue interface
	private List<PendingPacket> pendingPackets = new ArrayList<PendingPacket>();
	private Timer dialogueTimer;

	// Packet buffer entry
	private static class PendingPacket {
		public final String type;
		public final int frameId;
		public final String text;
		public final int npcId;
		public final int itemId;
		public final int amount;
		public final int config1;
		public final int config2;

		public PendingPacket(String type, int frameId, String text) {
			this.type = type;
			this.frameId = frameId;
			this.text = text;
			this.npcId = -1;
			this.itemId = -1;
			this.amount = -1;
			this.config1 = -1;
			this.config2 = -1;
		}

		public PendingPacket(String type, int frameId, int npcId) {
			this.type = type;
			this.frameId = frameId;
			this.text = null;
			this.npcId = npcId;
			this.itemId = -1;
			this.amount = -1;
			this.config1 = -1;
			this.config2 = -1;
		}

		public PendingPacket(String type, int frameId, int itemId, int amount) {
			this.type = type;
			this.frameId = frameId;
			this.text = null;
			this.npcId = -1;
			this.itemId = itemId;
			this.amount = amount;
			this.config1 = -1;
			this.config2 = -1;
		}

		public PendingPacket(String type, int frameId, int config1, int config2, boolean isConfig) {
			this.type = type;
			this.frameId = frameId;
			this.text = null;
			this.npcId = -1;
			this.itemId = config2;
			this.amount = 1;
			this.config1 = config1;
			this.config2 = config2;
		}
	}

	// Dialogue interface IDs
	private static final Set<Integer> DIALOGUE_INTERFACES = new HashSet<Integer>(Arrays.asList(
		// NPC Chat
		4882, 4887, 4893, 4900,
		// Player Chat
		968, 973, 979, 986,
		// Options
		2459, 2469, 2480, 2492,
		// Statements
		356, 359, 363, 368, 374,
		// Items
		306, 6231, 8880, 6179,
		// Timed dialogues
		12378, 12383, 11891, 12773, 12777, 12782, 11884, 12788, 12790, 12793, 12797, 12802,
		// Additional
		4429, 8866, 2400
	));

	public UIModalManager(Window parentWindow, Client client) {
		this.parentWindow = parentWindow;
		this.client = client;
		this.dialogueCore = new DialogueCore();

		if (client != null) {
			this.dialogueCore.initialize(client, this);
		}

		System.out.println("🎭 UIModalManager initialized with DialogueCore");
	}

	public void showDialogue(int interfaceId, Object rsInterface) {
		if (!useModalDialogues) {
			return;
		}

		System.out.println("🎭 showDialogue called with ID: " + interfaceId);

		// Check if this is actually a dialogue interface
		if (!DIALOGUE_INTERFACES.contains(interfaceId)) {
			System.out.println("🎭 Interface " + interfaceId + " is NOT a dialogue interface - ignoring");
			return;
		}

		// Stop any existing timer
		if (dialogueTimer != null) {
			dialogueTimer.stop();
		}

		// Check if this is the same interface being reopened
		boolean isSameInterface = (currentDialogue != null &&
			dialogueCore.getCurrentInterfaceId() == interfaceId);

		if (isSameInterface) {
			System.out.println("🎭 Same interface " + interfaceId + " reopening - preserving state");

			// Apply any new pending packets
			applyPendingPackets();
			pendingPackets.clear();

			// Update existing modal
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (currentDialogue != null) {
						currentDialogue.displayDialogue();
					}
				}
			});
			return;
		}

		// FIXED: Close existing dialogue if different interface - clear state properly
		if (currentDialogue != null) {
			System.out.println("🎭 Closing previous dialogue and clearing state");
			dialogueCore.handleDialogueClose(); // Clear state before disposing
			currentDialogue.dispose();
			currentDialogue = null;
			pendingPackets.clear(); // Clear any stale packets
		}

		// Set up the interface in DialogueCore
		dialogueCore.handleChatBoxInterface(interfaceId, rsInterface);

		// Apply all pending packets
		applyPendingPackets();
		pendingPackets.clear();

		// Create and show new modal
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				currentDialogue = new DialogueModal(parentWindow, dialogueCore, new Runnable() {
					public void run() {
						onDialogueClose();
					}
				});
				currentDialogue.displayDialogue();
				System.out.println("🎭 New modal created and displayed");
			}
		});
	}

	private void applyPendingPackets() {
		for (PendingPacket packet : pendingPackets) {
			switch (packet.type) {
				case "string":
					dialogueCore.handleStringPacket(packet.frameId, packet.text);
					break;
				case "npc_head":
					dialogueCore.handleNPCDialogueHead(packet.frameId, packet.npcId);
					break;
				case "item":
					dialogueCore.handleItemDisplay(packet.frameId, packet.itemId, packet.amount);
					break;
				case "config":
					dialogueCore.handleInterfaceConfig(packet.frameId, packet.config1, packet.config2);
					break;
			}
		}
	}

	private void removePendingPacket(String type, int frameId) {
		Iterator<PendingPacket> iterator = pendingPackets.iterator();
		while (iterator.hasNext()) {
			PendingPacket packet = iterator.next();
			if (packet.type.equals(type) && packet.frameId == frameId) {
				iterator.remove();
			}
		}
	}

	public void feedStringPacket(int frameId, String text) {
		if (debugMode) {
			System.out.println("🎭 String packet received - Frame: " + frameId + ", Text: '" + text + "'");
		}

		// Store packet for potential reuse
		removePendingPacket("string", frameId);
		pendingPackets.add(new PendingPacket("string", frameId, text));

		if (currentDialogue != null && dialogueCore.isDialogueActive()) {
			// Apply immediately and update modal
			dialogueCore.handleStringPacket(frameId, text);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (currentDialogue != null) {
						currentDialogue.displayDialogue();
					}
				}
			});
		} else {
			if (debugMode) {
				System.out.println("🎭 Stored string packet for future dialogue");
			}
		}
	}

	public void feedNPCHead(int frameId, int npcId) {
		if (debugMode) {
			System.out.println("🎭 NPC head packet received - Frame: " + frameId + ", NPC: " + npcId);
		}

		removePendingPacket("npc_head", frameId);
		pendingPackets.add(new PendingPacket("npc_head", frameId, npcId));

		if (currentDialogue != null && dialogueCore.isDialogueActive()) {
			dialogueCore.handleNPCDialogueHead(frameId, npcId);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (currentDialogue != null) {
						currentDialogue.displayDialogue();
					}
				}
			});
		}
	}

	public void feedItemDisplay(int frameId, int itemId, int amount) {
		if (debugMode) {
			System.out.println("🎭 Item display packet received - Frame: " + frameId + ", Item: " + itemId);
		}

		removePendingPacket("item", frameId);
		pendingPackets.add(new PendingPacket("item", frameId, itemId, amount));

		if (currentDialogue != null && dialogueCore.isDialogueActive()) {
			dialogueCore.handleItemDisplay(frameId, itemId, amount);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (currentDialogue != null) {
						currentDialogue.displayDialogue();
					}
				}
			});
		}
	}

	public void feedInterfaceConfig(int frameId, int config1, int config2) {
		if (debugMode) {
			System.out.println("🎭 Interface config packet received - Frame: " + frameId);
		}

		removePendingPacket("config", frameId);
		pendingPackets.add(new PendingPacket("config", frameId, config1, config2, true));

		if (currentDialogue != null && dialogueCore.isDialogueActive()) {
			dialogueCore.handleInterfaceConfig(frameId, config1, config2);
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					if (currentDialogue != null) {
						currentDialogue.displayDialogue();
					}
				}
			});
		}
	}

	public void onServerCloseDialogue() {
		System.out.println("🎭 Server close dialogue - clearing all state");

		if (dialogueTimer != null) {
			dialogueTimer.stop();
		}

		// FIXED: Clear pending packets to prevent stale data
		pendingPackets.clear();
		System.out.println("🎭 Cleared " + pendingPackets.size() + " pending packets");

		// FIXED: Clear DialogueCore state
		if (dialogueCore != null) {
			dialogueCore.handleDialogueClose();
			System.out.println("🎭 DialogueCore state cleared");
		}

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (currentDialogue != null) {
					currentDialogue.onServerCloseDialogue();
					currentDialogue = null;
					System.out.println("🎭 Modal disposed and reference cleared");
				}
			}
		});
	}

	private void onDialogueClose() {
		System.out.println("🎭 Dialogue closed callback - cleaning up");

		// FIXED: Clear DialogueCore state when modal is closed by user
		if (dialogueCore != null) {
			dialogueCore.handleDialogueClose();
			System.out.println("🎭 DialogueCore state cleared on user close");
		}

		// Clear pending packets to prevent them from being applied to next dialogue
		pendingPackets.clear();
		System.out.println("🎭 Cleared pending packets on dialogue close");

		currentDialogue = null;
	}

	// Public API methods
	public boolean isDialogueActive() {
		return currentDialogue != null && currentDialogue.isDialogueActive();
	}

	public int getCurrentDialogueInterfaceId() {
		if (currentDialogue != null) {
			return currentDialogue.getCurrentInterfaceId();
		}
		return dialogueCore.getCurrentInterfaceId();
	}

	public DialogueModal getCurrentDialogue() {
		return currentDialogue;
	}

	public DialogueCore getDialogueCore() {
		return dialogueCore;
	}

	public void setUseModalDialogues(boolean use) {
		this.useModalDialogues = use;
	}

	public void setDebugMode(boolean debug) {
		this.debugMode = debug;
	}

	public boolean isUsingModalDialogues() {
		return useModalDialogues;
	}

	public boolean isDebugMode() {
		return debugMode;
	}

	public boolean hasVisibleModals() {
		return currentDialogue != null && currentDialogue.isDialogueActive();
	}

	public void closeAllModals() {
		System.out.println("🎭 Closing all modals and clearing state");

		if (dialogueTimer != null) {
			dialogueTimer.stop();
		}

		if (currentDialogue != null) {
			currentDialogue.onServerCloseDialogue();
			currentDialogue = null;
		}

		// FIXED: Clear DialogueCore state
		if (dialogueCore != null) {
			dialogueCore.handleDialogueClose();
			System.out.println("🎭 DialogueCore state cleared in closeAllModals");
		}

		pendingPackets.clear();
		System.out.println("🎭 Cleared all pending packets in closeAllModals");
	}

	public void dispose() {
		if (dialogueTimer != null) {
			dialogueTimer.stop();
		}
		if (currentDialogue != null) {
			currentDialogue.dispose();
		}
		pendingPackets.clear();
	}


	// Additional modal methods
	public void showInputPrompt(String inputType, String prompt) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (currentDialogue != null) {
					currentDialogue.showInputPrompt(inputType, prompt);
				} else {
					currentDialogue = new DialogueModal(parentWindow, dialogueCore, new Runnable() {
						public void run() {
							onDialogueClose();
						}
					});
					currentDialogue.showInputPrompt(inputType, prompt);
				}
			}
		});
	}

	public void showConfirmation(String title, String message, Runnable onConfirm, Runnable onCancel) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				int result = JOptionPane.showConfirmDialog(parentWindow, message, title, JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION && onConfirm != null) {
					onConfirm.run();
				} else if (result == JOptionPane.NO_OPTION && onCancel != null) {
					onCancel.run();
				}
			}
		});
	}

	public void showProgress(String title, String message, boolean indeterminate) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JOptionPane.showMessageDialog(parentWindow, message, title, JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
}