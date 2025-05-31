package com.bestbudz.dock.ui.modal.dialogue;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.Client.inputString;
import static com.bestbudz.engine.core.Client.stream;
import com.bestbudz.network.packets.PacketParser;
import java.util.HashMap;
import java.util.Map;

/**
 * Fixed DialogueCore with proper string reset and item display handling
 */
public class DialogueCore {

	// Button IDs for option selection (from client constants)
	private static final int OPTIONS_2_1 = 9157;
	private static final int OPTIONS_2_2 = 9158;
	private static final int OPTIONS_3_1 = 9167;
	private static final int OPTIONS_3_2 = 9168;
	private static final int OPTIONS_3_3 = 9169;
	private static final int OPTIONS_4_1 = 9178;
	private static final int OPTIONS_4_2 = 9179;
	private static final int OPTIONS_4_3 = 9180;
	private static final int OPTIONS_4_4 = 9181;
	private static final int OPTIONS_5_1 = 9190;
	private static final int OPTIONS_5_2 = 9191;
	private static final int OPTIONS_5_3 = 9192;
	private static final int OPTIONS_5_4 = 9193;
	private static final int OPTIONS_5_5 = 9194;

	// Interface IDs - ALL dialogue types for proper recognition
	private static final int[] NPC_INTERFACES = {4882, 4887, 4893, 4900};
	private static final int[] PLAYER_INTERFACES = {968, 973, 979, 986};
	private static final int[] OPTION_INTERFACES = {2459, 2469, 2480, 2492};
	private static final int[] STATEMENT_INTERFACES = {356, 359, 363, 368, 374};
	private static final int[] ITEM_INTERFACES = {306, 6231, 6179, 8880, 2400, 4429, 8866};
	private static final int[] TIMED_NPC_INTERFACES = {12378, 12383, 11891};
	private static final int[] TIMED_PLAYER_INTERFACES = {12773, 12777, 12782, 11884};
	private static final int[] TIMED_STATEMENT_INTERFACES = {12788, 12790, 12793, 12797, 12802};
	private static final int[] INPUT_INTERFACES = {162, 163, 164, 165, 166, 167};

	// Dialogue type constants
	public static final int TYPE_NPC_DIALOGUE = 0;
	public static final int TYPE_PLAYER_DIALOGUE = 1;
	public static final int TYPE_OPTION = 2;
	public static final int TYPE_STATEMENT = 3;
	public static final int TYPE_ITEM = 4;
	public static final int TYPE_INPUT = 5;
	public static final int TYPE_TIMED = 6;
	public static final int TYPE_ITEM_CREATION = 7;
	public static final int TYPE_ITEM_SELECTION = 8;
	public static final int TYPE_INFORMATION_BOX = 9;

	// State
	private Client client;
	private Object packetSender;
	private boolean isInitialized = false;

	// Core dialogue state
	private int currentInterfaceId = -1;
	private int dialogueType = -1;
	private String title = "";
	private String[] lines = new String[0];
	private String[] options = new String[0];
	private int npcId = -1;
	private boolean isPlayerDialogue = false;
	private boolean isTimed = false;
	private long timedUntil = 0;

	// Input dialog state
	private String inputPrompt = "";
	private String inputText = "";
	private boolean isInputDialog = false;
	private int inputType = 0; // 0=text, 1=amount, 2=number

	// Item dialog state
	private int[] itemIds = new int[0];
	private int[] itemAmounts = new int[0];
	private String[] itemTexts = new String[0];
	private int[] itemMaxAmounts = new int[0];
	private boolean isItemSelectable = false;
	private boolean hasScrolling = false;

	// Animation and display state
	private int headAnimationId = -1;
	private int itemRotation1 = 0;
	private int itemRotation2 = 0;
	private int itemZoom = 100;

	// COMPLETE Interface mappings for 317 RSPS
	private final Map<Integer, Integer> lineMapping = new HashMap<>();
	private final Map<Integer, Integer> optionMapping = new HashMap<>();
	private final Map<Integer, String> titleMapping = new HashMap<>();

	public DialogueCore() {
		initializeMappings();
	}

	public void initialize(Client client, Object packetSender) {
		this.client = client;
		this.packetSender = packetSender;
		this.isInitialized = true;
		System.out.println("🎭 DialogueCore initialized with client");
	}

	private void initializeMappings() {
		// NPC chat mappings (from SendNpcChatbox packets)
		lineMapping.put(4885, 0);                    // 1 line NPC
		lineMapping.put(4890, 0); lineMapping.put(4891, 1);  // 2 line NPC
		lineMapping.put(4896, 0); lineMapping.put(4897, 1); lineMapping.put(4898, 2);  // 3 line NPC
		lineMapping.put(4903, 0); lineMapping.put(4904, 1); lineMapping.put(4905, 2); lineMapping.put(4906, 3);  // 4 line NPC

		// Player chat mappings (from SendPlayerChatbox packets)
		lineMapping.put(971, 0);                     // 1 line player
		lineMapping.put(976, 0); lineMapping.put(977, 1);   // 2 line player
		lineMapping.put(982, 0); lineMapping.put(983, 1); lineMapping.put(984, 2);   // 3 line player
		lineMapping.put(989, 0); lineMapping.put(990, 1); lineMapping.put(991, 2); lineMapping.put(992, 3);   // 4 line player

		// Statement mappings (from SendStatement packets)
		lineMapping.put(357, 0);                     // 1 line statement
		lineMapping.put(360, 0); lineMapping.put(361, 1);   // 2 line statement
		lineMapping.put(364, 0); lineMapping.put(365, 1); lineMapping.put(366, 2);   // 3 line statement
		lineMapping.put(369, 0); lineMapping.put(370, 1); lineMapping.put(371, 2); lineMapping.put(372, 3);   // 4 line statement
		lineMapping.put(375, 0); lineMapping.put(376, 1); lineMapping.put(377, 2); lineMapping.put(378, 3); lineMapping.put(379, 4);   // 5 line statement

		// Option mappings (from SendOption packets) - CRITICAL FOR OPTION DISPLAY
		optionMapping.put(2461, 0); optionMapping.put(2462, 1);  // 2 options
		optionMapping.put(2471, 0); optionMapping.put(2472, 1); optionMapping.put(2473, 2);  // 3 options
		optionMapping.put(2482, 0); optionMapping.put(2483, 1); optionMapping.put(2484, 2); optionMapping.put(2485, 3);  // 4 options
		optionMapping.put(2494, 0); optionMapping.put(2495, 1); optionMapping.put(2496, 2); optionMapping.put(2497, 3); optionMapping.put(2498, 4);  // 5 options

		// Title mappings (for character names and dialogue titles)
		titleMapping.put(4884, "npc_title_1");    // NPC titles
		titleMapping.put(4889, "npc_title_2");
		titleMapping.put(4895, "npc_title_3");
		titleMapping.put(4902, "npc_title_4");
		titleMapping.put(970, "player_title_1");   // Player titles
		titleMapping.put(975, "player_title_2");
		titleMapping.put(981, "player_title_3");
		titleMapping.put(988, "player_title_4");

		// Item text mappings (from SendItem packets)
		lineMapping.put(308, 0);    // sendItem1 text
		lineMapping.put(6232, 0);   // sendItem2 text1
		lineMapping.put(6233, 1);   // sendItem2 text2
		lineMapping.put(8889, 0);   // makeItem3 itemName1
		lineMapping.put(8893, 1);   // makeItem3 itemName2
		lineMapping.put(8897, 2);   // makeItem3 itemName3

		// Information box mappings
		titleMapping.put(6180, "info_title");
		lineMapping.put(6181, 0); lineMapping.put(6182, 1); lineMapping.put(6183, 2); lineMapping.put(6184, 3);

		// Timed dialogue mappings
		titleMapping.put(12380, "timed_npc_title_2"); lineMapping.put(12381, 0); lineMapping.put(12382, 1);
		titleMapping.put(12385, "timed_npc_title_3"); lineMapping.put(12386, 0); lineMapping.put(12387, 1); lineMapping.put(12388, 2);
		titleMapping.put(11893, "timed_npc_title_4"); lineMapping.put(11894, 0); lineMapping.put(11895, 1); lineMapping.put(11896, 2); lineMapping.put(11897, 3);

		titleMapping.put(12775, "timed_player_title_1"); lineMapping.put(12776, 0);
		titleMapping.put(12779, "timed_player_title_2"); lineMapping.put(12780, 0); lineMapping.put(12781, 1);
		titleMapping.put(12784, "timed_player_title_3"); lineMapping.put(12785, 0); lineMapping.put(12786, 1); lineMapping.put(12787, 2);
		titleMapping.put(11886, "timed_player_title_4"); lineMapping.put(11887, 0); lineMapping.put(11888, 1); lineMapping.put(11889, 2); lineMapping.put(11890, 3);

		lineMapping.put(12789, 0);
		lineMapping.put(12791, 0); lineMapping.put(12792, 1);
		lineMapping.put(12794, 0); lineMapping.put(12795, 1); lineMapping.put(12796, 2);
		lineMapping.put(12798, 0); lineMapping.put(12799, 1); lineMapping.put(12800, 2); lineMapping.put(12801, 3);
		lineMapping.put(12803, 0); lineMapping.put(12804, 1); lineMapping.put(12805, 2); lineMapping.put(12806, 3); lineMapping.put(12807, 4);

		System.out.println("🎭 DialogueCore mappings initialized - " + lineMapping.size() + " line mappings, " +
			optionMapping.size() + " option mappings, " + titleMapping.size() + " title mappings");
	}

	// Main methods called by UIModalManager

	public void handleChatBoxInterface(int interfaceId, Object rsInterface) {
		System.out.println("🎭 DialogueCore.handleChatBoxInterface: Interface " + interfaceId +
			" (previous: " + currentInterfaceId + ")");

		// Check if this is the same interface being reused
		boolean isSameInterface = (this.currentInterfaceId == interfaceId);

		this.currentInterfaceId = interfaceId;
		this.dialogueType = determineDialogueType(interfaceId);

		// ALWAYS reset state for new interfaces to prevent stale data
		if (!isSameInterface) {
			System.out.println("🎭 Resetting dialogue state for NEW interface " + interfaceId);
			resetDialogueState();
		} else {
			System.out.println("🎭 Preserving existing dialogue state for SAME interface " + interfaceId);
		}

		// Set interface-specific properties
		this.isPlayerDialogue = (dialogueType == TYPE_PLAYER_DIALOGUE);
		this.isTimed = isTimedInterface(interfaceId);
		this.isInputDialog = isInputInterface(interfaceId);

		// Set timed expiration (5 seconds for timed dialogues)
		if (isTimed) {
			timedUntil = System.currentTimeMillis() + 5000;
		}

		System.out.println("🎭 DialogueCore.handleChatBoxInterface: Interface " + interfaceId +
			", Type: " + dialogueType + ", Timed: " + isTimed + ", State preserved: " + isSameInterface);
	}

	/**
	 * FIXED: Reset all dialogue state to prevent stale data
	 */
	private void resetDialogueState() {
		title = "";
		lines = new String[0];
		options = new String[0];
		npcId = -1;
		inputPrompt = "";
		inputText = "";
		itemIds = new int[0];
		itemAmounts = new int[0];
		itemTexts = new String[0];
		itemMaxAmounts = new int[0];
		headAnimationId = -1;
		isItemSelectable = false;
		hasScrolling = false;
		System.out.println("🎭 RESET: All dialogue state cleared");
	}

	public void handleStringPacket(int interfaceId, String text) {
		if (text == null) text = "";

		System.out.println("🎭 DialogueCore.handleStringPacket: Frame " + interfaceId + " = '" + text + "'");

		// Handle titles
		if (titleMapping.containsKey(interfaceId)) {
			this.title = text;
			System.out.println("🎭 Set title: '" + text + "'");
			return;
		}

		// Handle dialogue lines
		Integer lineIndex = lineMapping.get(interfaceId);
		if (lineIndex != null) {
			ensureLineCapacity(lineIndex + 1);
			lines[lineIndex] = text;
			System.out.println("🎭 Set line " + lineIndex + ": '" + text + "'");
			return;
		}

		// Handle options - CRITICAL FOR OPTION DIALOGUES
		Integer optionIndex = optionMapping.get(interfaceId);
		if (optionIndex != null) {
			ensureOptionCapacity(optionIndex + 1);
			options[optionIndex] = text;
			System.out.println("🎭 Set option " + optionIndex + ": '" + text + "'");
			return;
		}

		// Handle input prompts
		if (isInputDialog) {
			this.inputPrompt = text;
			System.out.println("🎭 Set input prompt: '" + text + "'");
			return;
		}

		// FIXED: Handle item texts with proper mapping
		if (interfaceId == 308) {
			// Single item text (sendItem1)
			ensureItemTextCapacity(1);
			itemTexts[0] = text;
			System.out.println("🎭 Set single item text (308): '" + text + "'");
			return;
		}
		if (interfaceId == 6232) {
			// First item in two-item display
			ensureItemTextCapacity(2);
			itemTexts[0] = text;
			System.out.println("🎭 Set item 1 text (6232): '" + text + "'");
			return;
		}
		if (interfaceId == 6233) {
			// Second item in two-item display
			ensureItemTextCapacity(2);
			itemTexts[1] = text;
			System.out.println("🎭 Set item 2 text (6233): '" + text + "'");
			return;
		}
		if (interfaceId >= 8889 && interfaceId <= 8897 && interfaceId % 4 == 1) {
			// Make-X item names (8889, 8893, 8897)
			int itemIndex = (interfaceId - 8889) / 4;
			ensureItemTextCapacity(itemIndex + 1);
			itemTexts[itemIndex] = text;
			System.out.println("🎭 Set make-X item " + (itemIndex + 1) + " text (" + interfaceId + "): '" + text + "'");
			return;
		}

		System.out.println("🎭 Unhandled string packet: Frame " + interfaceId + " = '" + text + "'");
	}

	public void handleNPCDialogueHead(int interfaceId, int npcId) {
		this.npcId = npcId;
		this.isPlayerDialogue = false;
		System.out.println("🎭 Set NPC head: " + npcId + " on frame " + interfaceId);
	}

	public void handlePlayerDialogueHead(int interfaceId) {
		this.isPlayerDialogue = true;
		this.npcId = -1;
		System.out.println("🎭 Set player head on frame " + interfaceId);
	}

	public void handleModelAnimation(int interfaceId, int animationId) {
		this.headAnimationId = animationId;
		System.out.println("🎭 Set animation: " + animationId + " on frame " + interfaceId);
	}

	public void handleItemDisplay(int interfaceId, int itemId, int amount) {
		ensureItemCapacity(1);
		itemIds[0] = itemId;
		itemAmounts[0] = amount;
		System.out.println("🎭 Set single item display: Item " + itemId + " x" + amount);
	}

	public void handleInputPrompt(String prompt) {
		this.inputPrompt = prompt;
		this.isInputDialog = true;
		this.dialogueType = TYPE_INPUT;
		System.out.println("🎭 Set input prompt: '" + prompt + "'");
	}

	// Handle interface config packets (for item displays, model configs)
	public void handleInterfaceConfig(int frameId, int config1, int config2) {
		System.out.println("🎭 DialogueCore.handleInterfaceConfig: Frame " + frameId + " = " + config1 + ", " + config2);

		// sendItem1: frame 307 gets item ID in config2
		if (frameId == 307) {
			ensureItemCapacity(1);
			itemIds[0] = config2;
			itemAmounts[0] = 1;
			System.out.println("🎭 Set item for sendItem1: Item " + config2);
			return;
		}

		// sendItem2: frames 6235 and 6236 get item IDs
		if (frameId == 6235) {
			ensureItemCapacity(2);
			itemIds[0] = config2;
			itemAmounts[0] = 1;
			System.out.println("🎭 Set item 1 for sendItem2: Item " + config2);
			return;
		}
		if (frameId == 6236) {
			ensureItemCapacity(2);
			itemIds[1] = config2;
			itemAmounts[1] = 1;
			System.out.println("🎭 Set item 2 for sendItem2: Item " + config2);
			return;
		}

		// makeItem3: frames 8883, 8884, 8885 get item IDs
		if (frameId >= 8883 && frameId <= 8885) {
			int itemIndex = frameId - 8883;
			ensureItemCapacity(3);
			itemIds[itemIndex] = config2;
			itemAmounts[itemIndex] = 1;
			System.out.println("🎭 Set item " + (itemIndex + 1) + " for makeItem3: Item " + config2);
			return;
		}

		// NPC head models (for NPC dialogues)
		if (frameId == 4883 || frameId == 4888 || frameId == 4894 || frameId == 4901) {
			this.npcId = config2;
			System.out.println("🎭 Set NPC head model: " + config2);
			return;
		}

		System.out.println("🎭 Unhandled interface config: Frame " + frameId + " = " + config1 + ", " + config2);
	}

	// Enhanced item handling methods
	public void handleMultipleItems(int interfaceId, int[] itemIds, int[] amounts) {
		this.itemIds = new int[itemIds.length];
		this.itemAmounts = new int[amounts.length];
		System.arraycopy(itemIds, 0, this.itemIds, 0, itemIds.length);
		System.arraycopy(amounts, 0, this.itemAmounts, 0, amounts.length);
		System.out.println("🎭 Set multiple items: " + itemIds.length + " items");
	}

	public void handleItemCreation(int interfaceId, int[] itemIds, String[] itemNames, int[] maxAmounts) {
		this.dialogueType = TYPE_ITEM_CREATION;
		this.itemIds = new int[itemIds.length];
		this.itemAmounts = new int[maxAmounts.length];
		this.itemTexts = new String[itemNames.length];

		System.arraycopy(itemIds, 0, this.itemIds, 0, itemIds.length);
		System.arraycopy(maxAmounts, 0, this.itemAmounts, 0, maxAmounts.length);
		System.arraycopy(itemNames, 0, this.itemTexts, 0, itemNames.length);
		System.out.println("🎭 Set item creation dialogue with " + itemIds.length + " items");
	}

	public void handleTwoItemSelection(int interfaceId, int item1Id, int item2Id, String text1, String text2) {
		this.dialogueType = TYPE_ITEM_SELECTION;
		this.itemIds = new int[]{item1Id, item2Id};
		this.itemAmounts = new int[]{1, 1};
		this.itemTexts = new String[]{text1, text2};
		this.isItemSelectable = true;
		System.out.println("🎭 Set two-item selection: " + item1Id + ", " + item2Id);
	}

	public void handleInformationBox(int interfaceId, String title, String[] lines) {
		this.dialogueType = TYPE_INFORMATION_BOX;
		this.title = title != null ? title : "";
		this.lines = new String[lines.length];
		System.arraycopy(lines, 0, this.lines, 0, lines.length);
		System.out.println("🎭 Set information box with " + lines.length + " lines");
	}

	// Event handlers for user actions

	public void handleContinue() {
		if (packetSender != null && isInitialized) {
			sendContinuePacket();
		}
		System.out.println("🎭 Dialogue continue handled");
	}

	public void handleOptionSelect(int option) {
		if (packetSender != null && isInitialized) {
			sendOptionPacket(option);
		}
		System.out.println("🎭 Option " + option + " selected");
	}

	public void handleInputSubmit(String input) {
		this.inputText = input;
		if (packetSender != null && isInitialized) {
			sendInputPacket(input);
		}
		System.out.println("🎭 Input submitted: " + input);
	}

	/**
	 * FIXED: Properly clear dialogue when closed
	 */
	public void handleDialogueClose() {
		System.out.println("🎭 Dialogue closed - clearing all state");
		clearDialogue();
	}

	// Integration with button click packets
	public boolean handleButtonClick(int buttonId) {
		if (!isDialogueActive()) return false;

		// Map button IDs to option numbers
		switch (buttonId) {
			case OPTIONS_2_1: case OPTIONS_3_1: case OPTIONS_4_1: case OPTIONS_5_1:
				handleOptionSelect(1); return true;
			case OPTIONS_2_2: case OPTIONS_3_2: case OPTIONS_4_2: case OPTIONS_5_2:
				handleOptionSelect(2); return true;
			case OPTIONS_3_3: case OPTIONS_4_3: case OPTIONS_5_3:
				handleOptionSelect(3); return true;
			case OPTIONS_4_4: case OPTIONS_5_4:
				handleOptionSelect(4); return true;
			case OPTIONS_5_5:
				handleOptionSelect(5); return true;
		}
		return false;
	}

	// Getters for DialogueModal
	public boolean isDialogueActive() { return currentInterfaceId != -1; }
	public int getCurrentInterfaceId() { return currentInterfaceId; }
	public int getDialogueType() { return dialogueType; }
	public String getTitle() { return title; }
	public String[] getLines() { return lines; }
	public String[] getOptions() { return options; }
	public int getNPCId() { return npcId; }
	public boolean isPlayerDialogue() { return isPlayerDialogue; }
	public boolean isClosable() { return !isTimed || isExpired(); }
	public boolean isTimed() { return isTimed; }
	public boolean isExpired() { return isTimed && System.currentTimeMillis() >= timedUntil; }
	public boolean isInputDialog() { return isInputDialog; }
	public String getInputPrompt() { return inputPrompt; }
	public String getInputText() { return inputText; }
	public int getInputType() { return inputType; }
	public int[] getItemIds() { return itemIds; }
	public int[] getItemAmounts() { return itemAmounts; }
	public String[] getItemTexts() { return itemTexts; }
	public int[] getItemMaxAmounts() { return itemMaxAmounts; }
	public boolean isItemSelectable() { return isItemSelectable; }
	public boolean hasScrolling() { return hasScrolling; }
	public int getHeadAnimationId() { return headAnimationId; }
	public int getItemRotation1() { return itemRotation1; }
	public int getItemRotation2() { return itemRotation2; }
	public int getItemZoom() { return itemZoom; }

	// Convenience methods
	public boolean isItemCreationDialog() { return dialogueType == TYPE_ITEM_CREATION; }
	public boolean isItemSelectionDialog() { return dialogueType == TYPE_ITEM_SELECTION; }
	public boolean isInformationBox() { return dialogueType == TYPE_INFORMATION_BOX; }

	// Private helper methods

	private int determineDialogueType(int interfaceId) {
		// Check all interface types systematically
		for (int id : NPC_INTERFACES) if (id == interfaceId) return TYPE_NPC_DIALOGUE;
		for (int id : TIMED_NPC_INTERFACES) if (id == interfaceId) return TYPE_NPC_DIALOGUE;

		for (int id : PLAYER_INTERFACES) if (id == interfaceId) return TYPE_PLAYER_DIALOGUE;
		for (int id : TIMED_PLAYER_INTERFACES) if (id == interfaceId) return TYPE_PLAYER_DIALOGUE;

		for (int id : OPTION_INTERFACES) if (id == interfaceId) return TYPE_OPTION;

		for (int id : STATEMENT_INTERFACES) if (id == interfaceId) return TYPE_STATEMENT;
		for (int id : TIMED_STATEMENT_INTERFACES) if (id == interfaceId) return TYPE_STATEMENT;

		for (int id : ITEM_INTERFACES) if (id == interfaceId) return TYPE_ITEM;

		for (int id : INPUT_INTERFACES) if (id == interfaceId) return TYPE_INPUT;

		// Special case for known interface IDs
		if (interfaceId == 6179) return TYPE_INFORMATION_BOX;

		return TYPE_NPC_DIALOGUE; // Default fallback
	}

	private boolean isTimedInterface(int interfaceId) {
		for (int id : TIMED_NPC_INTERFACES) if (id == interfaceId) return true;
		for (int id : TIMED_PLAYER_INTERFACES) if (id == interfaceId) return true;
		for (int id : TIMED_STATEMENT_INTERFACES) if (id == interfaceId) return true;
		return false;
	}

	private boolean isInputInterface(int interfaceId) {
		for (int id : INPUT_INTERFACES) if (id == interfaceId) return true;
		return false;
	}

	private void ensureLineCapacity(int minCapacity) {
		if (lines.length < minCapacity) {
			String[] newLines = new String[minCapacity];
			System.arraycopy(lines, 0, newLines, 0, lines.length);
			for (int i = lines.length; i < minCapacity; i++) {
				newLines[i] = ""; // Initialize with empty strings
			}
			lines = newLines;
		}
	}

	private void ensureOptionCapacity(int minCapacity) {
		if (options.length < minCapacity) {
			String[] newOptions = new String[minCapacity];
			System.arraycopy(options, 0, newOptions, 0, options.length);
			for (int i = options.length; i < minCapacity; i++) {
				newOptions[i] = ""; // Initialize with empty strings
			}
			options = newOptions;
		}
	}

	private void ensureItemCapacity(int minCapacity) {
		if (itemIds.length < minCapacity) {
			int[] newItemIds = new int[minCapacity];
			int[] newItemAmounts = new int[minCapacity];
			System.arraycopy(itemIds, 0, newItemIds, 0, itemIds.length);
			System.arraycopy(itemAmounts, 0, newItemAmounts, 0, itemAmounts.length);
			itemIds = newItemIds;
			itemAmounts = newItemAmounts;
		}
	}

	private void ensureItemTextCapacity(int minCapacity) {
		if (itemTexts.length < minCapacity) {
			String[] newItemTexts = new String[minCapacity];
			System.arraycopy(itemTexts, 0, newItemTexts, 0, itemTexts.length);
			for (int i = itemTexts.length; i < minCapacity; i++) {
				newItemTexts[i] = ""; // Initialize with empty strings
			}
			itemTexts = newItemTexts;
		}
	}

	/**
	 * FIXED: Completely clear dialogue state to prevent stale data
	 */
	private void clearDialogue() {
		currentInterfaceId = -1;
		dialogueType = -1;
		title = "";
		lines = new String[0];
		options = new String[0];
		npcId = -1;
		isPlayerDialogue = false;
		isTimed = false;
		timedUntil = 0;
		isInputDialog = false;
		inputPrompt = "";
		inputText = "";
		itemIds = new int[0];
		itemAmounts = new int[0];
		itemTexts = new String[0];
		itemMaxAmounts = new int[0];
		headAnimationId = -1;
		isItemSelectable = false;
		hasScrolling = false;
		System.out.println("🎭 CLEAR: All dialogue state completely cleared");
	}

	// Packet sending methods for 317 RSPS

	private void sendContinuePacket() {
		try {
			if (client != null && client.stream != null) {
				// Send button click packet for continue
				client.stream.createFrame(185); // ClickButton packet opcode
				client.stream.writeWordBigEndian(currentInterfaceId);
				client.stream.writeWordBigEndian(1); // Continue button action

				System.out.println("🎭 PACKET: Sent continue packet for interface " + currentInterfaceId);
			}
		} catch (Exception e) {
			System.err.println("🎭 ERROR: Failed to send continue packet: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void sendOptionPacket(int option) {
		try {
			if (client != null && client.stream != null) {
				// Get the correct button ID for this option
				int buttonId = getOptionButtonId(option);

				// Send button click packet
				client.stream.createFrame(185); // ClickButton packet opcode
				client.stream.writeWordBigEndian(buttonId);
				client.stream.writeWordBigEndian(0); // No additional data

				System.out.println("🎭 PACKET: Sent option " + option + " packet (button ID: " + buttonId + ")");
			}
		} catch (Exception e) {
			System.err.println("🎭 ERROR: Failed to send option packet: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private int getOptionButtonId(int option) {
		// Return the correct button ID based on option number and current interface
		int interfaceId = currentInterfaceId;

		// 2 option interface (2459)
		if (interfaceId == 2459) {
			return (option == 1) ? OPTIONS_2_1 : OPTIONS_2_2;
		}
		// 3 option interface (2469)
		else if (interfaceId == 2469) {
			switch (option) {
				case 1: return OPTIONS_3_1;
				case 2: return OPTIONS_3_2;
				case 3: return OPTIONS_3_3;
				default: return OPTIONS_3_1;
			}
		}
		// 4 option interface (2480)
		else if (interfaceId == 2480) {
			switch (option) {
				case 1: return OPTIONS_4_1;
				case 2: return OPTIONS_4_2;
				case 3: return OPTIONS_4_3;
				case 4: return OPTIONS_4_4;
				default: return OPTIONS_4_1;
			}
		}
		// 5 option interface (2492)
		else if (interfaceId == 2492) {
			switch (option) {
				case 1: return OPTIONS_5_1;
				case 2: return OPTIONS_5_2;
				case 3: return OPTIONS_5_3;
				case 4: return OPTIONS_5_4;
				case 5: return OPTIONS_5_5;
				default: return OPTIONS_5_1;
			}
		}

		// Fallback - use the most common button IDs
		switch (option) {
			case 1: return OPTIONS_2_1; // 9157
			case 2: return OPTIONS_2_2; // 9158
			case 3: return OPTIONS_3_3; // 9169
			case 4: return OPTIONS_4_4; // 9181
			case 5: return OPTIONS_5_5; // 9194
			default: return OPTIONS_2_1;
		}
	}

	private void sendInputPacket(String input) {
		try {
			if (client != null && client.stream != null && input != null && !input.trim().isEmpty()) {
				// Send string input packet
				client.stream.createFrame(60); // SendString packet opcode
				client.stream.writeString(input.trim());

				System.out.println("🎭 PACKET: Sent input packet: " + input);
			}
		} catch (Exception e) {
			System.err.println("🎭 ERROR: Failed to send input packet: " + e.getMessage());
			e.printStackTrace();
		}
	}
}