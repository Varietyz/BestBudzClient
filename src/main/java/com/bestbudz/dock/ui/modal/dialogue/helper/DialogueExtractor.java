package com.bestbudz.dock.ui.modal.dialogue.helper;

import com.bestbudz.ui.RSInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * CORRECT DialogueExtractor - Matches your server's actual button IDs
 */
public class DialogueExtractor {

	/**
	 * Extract dialogue options - Using correct text extraction
	 */
	public static String[] extractDialogueOptions(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return null;

		int interfaceId = dialogueInterface.id;

		System.out.println("=== EXTRACTING OPTIONS FOR: " + interfaceId + " ===");

		// Handle your server's option interfaces - get text from correct children
		switch (interfaceId) {
			case 2459: // 2 options
				return getOptionsFromChildren(new int[]{2461, 2462});
			case 2469: // 3 options
				return getOptionsFromChildren(new int[]{2471, 2472, 2473});
			case 2480: // 4 options
				return getOptionsFromChildren(new int[]{2482, 2483, 2484, 2485});
			case 2492: // 5 options
				return getOptionsFromChildren(new int[]{2494, 2495, 2496, 2497, 2498});

			// Item interfaces - get text from correct children
			case 8880: // Make-X
				return getOptionsFromChildren(new int[]{8889, 8893, 8897});
			case 6231: // 2 items
				return getOptionsFromChildren(new int[]{6232, 6233});
			case 306: // Single item - no options, should show continue
				return null;
		}

		System.out.println("No options found for interface: " + interfaceId);
		return null;
	}

	/**
	 * Extract option child IDs - CRITICAL: Use your server's ACTUAL button IDs
	 */
	public static int[] extractOptionChildIds(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return null;

		int interfaceId = dialogueInterface.id;

		System.out.println("=== EXTRACTING CHILD IDS FOR: " + interfaceId + " ===");

		// Return the ACTUAL clickable button IDs from your DialogueConstants
		switch (interfaceId) {
			case 2459: // 2 options
				int[] twoOptionIds = {9157, 9158};
				System.out.println("2-option button IDs: " + java.util.Arrays.toString(twoOptionIds));
				return twoOptionIds;

			case 2469: // 3 options
				int[] threeOptionIds = {9167, 9168, 9169};
				System.out.println("3-option button IDs: " + java.util.Arrays.toString(threeOptionIds));
				return threeOptionIds;

			case 2480: // 4 options
				int[] fourOptionIds = {9178, 9179, 9180, 9181};
				System.out.println("4-option button IDs: " + java.util.Arrays.toString(fourOptionIds));
				return fourOptionIds;

			case 2492: // 5 options
				int[] fiveOptionIds = {9190, 9191, 9192, 9193, 9194};
				System.out.println("5-option button IDs: " + java.util.Arrays.toString(fiveOptionIds));
				return fiveOptionIds;

			// For item interfaces, still use the item sprite IDs (these should be correct)
			case 8880: // Make-X
				int[] makeXIds = {8883, 8884, 8885};
				System.out.println("Make-X item button IDs: " + java.util.Arrays.toString(makeXIds));
				return makeXIds;

			case 6231: // 2 items
				int[] twoItemIds = {6235, 6236};
				System.out.println("2-item button IDs: " + java.util.Arrays.toString(twoItemIds));
				return twoItemIds;

			case 306: // Single item
				System.out.println("Single item continue ID: 306");
				return new int[]{306};
		}

		System.out.println("No child IDs found for interface: " + interfaceId);
		return null;
	}

	/**
	 * Extract continue button text
	 */
	public static String extractContinueButtonText(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return null;

		int interfaceId = dialogueInterface.id;

		System.out.println("=== CHECKING CONTINUE FOR: " + interfaceId + " ===");

		// These interfaces should have continue buttons based on your DialogueManager
		if (interfaceId == 4882 || interfaceId == 4887 || interfaceId == 4893 || interfaceId == 4900 || // NPC
			interfaceId == 968 || interfaceId == 973 || interfaceId == 979 || interfaceId == 986 || // Player
			interfaceId == 356 || interfaceId == 359 || interfaceId == 363 || interfaceId == 368 || interfaceId == 374 || // Statement
			interfaceId == 306) { // Single item
			System.out.println("Interface " + interfaceId + " should have continue button");
			return "Continue";
		}

		// Option interfaces and Make-X interfaces don't have continue
		System.out.println("Interface " + interfaceId + " should NOT have continue button");
		return null;
	}

	/**
	 * Extract continue child ID
	 */
	public static int extractContinueChildId(RSInterface dialogueInterface) {
		if (extractContinueButtonText(dialogueInterface) != null) {
			int interfaceId = dialogueInterface.id;
			System.out.println("Continue child ID for " + interfaceId + " is: " + interfaceId);
			return interfaceId;
		}
		return -1;
	}

	/**
	 * Extract all dialogue text - enhanced to handle your server's patterns
	 */
	public static String extractAllDialogueText(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return "Dialogue";

		int interfaceId = dialogueInterface.id;

		System.out.println("=== EXTRACTING TEXT FOR: " + interfaceId + " ===");

		// For item interfaces, create descriptive text
		if (interfaceId == 8880) {
			StringBuilder text = new StringBuilder("What would you like to make? ");
			String[] options = getOptionsFromChildren(new int[]{8889, 8893, 8897});
			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					if (i > 0) text.append(", ");
					text.append(options[i]);
				}
			}
			return text.toString();
		} else if (interfaceId == 6231) {
			StringBuilder text = new StringBuilder("Choose an item: ");
			String[] options = getOptionsFromChildren(new int[]{6232, 6233});
			if (options != null) {
				for (int i = 0; i < options.length; i++) {
					if (i > 0) text.append(" or ");
					text.append(options[i]);
				}
			}
			return text.toString();
		} else if (interfaceId == 306) {
			// Single item - get text from child 308
			return getTextFromChild(308);
		}

		// For regular dialogues, extract text from known patterns based on your DialogueManager
		String text = extractDialogueTextByType(interfaceId);
		if (text != null && !text.trim().isEmpty()) {
			return text;
		}

		// Fallback: try to get text from the interface itself
		if (dialogueInterface.disabledMessage != null && !dialogueInterface.disabledMessage.trim().isEmpty()) {
			return DialogueTextCleaner.cleanAndFilterText(dialogueInterface.disabledMessage.trim());
		}

		return "Dialogue text not found";
	}

	/**
	 * Extract NPC name - enhanced for your server
	 */
	public static String extractNpcName(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return "NPC";

		int interfaceId = dialogueInterface.id;

		// Get NPC name from known dialogue patterns based on your DialogueManager
		String name = extractNpcNameByType(interfaceId);
		if (name != null && !name.trim().isEmpty()) {
			return name;
		}

		// For item interfaces, show descriptive names
		if (interfaceId == 8880) return "Crafting";
		if (interfaceId == 6231) return "Item Selection";
		if (interfaceId == 306) return "Item Information";

		return "NPC";
	}

	// Helper method to check if interface has inputs
	public static String extractInputField(RSInterface dialogueInterface) {
		// Your server doesn't seem to use input fields in dialogue interfaces
		return null;
	}

	public static InputFieldInfo extractInputChildId(RSInterface dialogueInterface) {
		// No input fields in dialogue interfaces based on your server code
		return new InputFieldInfo(-1, false);
	}

	public static boolean isItemInterface(int interfaceId) {
		return interfaceId == 306 || interfaceId == 6231 || interfaceId == 8880 || interfaceId == 6179;
	}

	// Helper methods
	private static String[] getOptionsFromChildren(int[] childIds) {
		List<String> options = new ArrayList<String>();

		for (int childId : childIds) {
			String text = getTextFromChild(childId);
			if (text != null && !text.trim().isEmpty()) {
				options.add(text.trim());
				System.out.println("Found option: '" + text.trim() + "' from child " + childId);
			}
		}

		return options.isEmpty() ? null : options.toArray(new String[options.size()]);
	}

	private static String getTextFromChild(int childId) {
		if (childId > 0 && childId < RSInterface.interfaceCache.length) {
			RSInterface child = RSInterface.interfaceCache[childId];
			if (child != null && child.disabledMessage != null) {
				return child.disabledMessage;
			}
		}
		return null;
	}

	// Extract dialogue text by interface type based on your DialogueManager
	private static String extractDialogueTextByType(int interfaceId) {
		// NPC dialogues - get text from line children (skip name)
		if (interfaceId == 4882) return getTextFromChild(4885);
		if (interfaceId == 4887) return combineText(4890, 4891);
		if (interfaceId == 4893) return combineText(4896, 4897, 4898);
		if (interfaceId == 4900) return combineText(4903, 4904, 4905, 4906);

		// Player dialogues
		if (interfaceId == 968) return getTextFromChild(971);
		if (interfaceId == 973) return combineText(976, 977);
		if (interfaceId == 979) return combineText(982, 983, 984);
		if (interfaceId == 986) return combineText(989, 990, 991, 992);

		// Statements
		if (interfaceId == 356) return getTextFromChild(357);
		if (interfaceId == 359) return combineText(360, 361);
		if (interfaceId == 363) return combineText(364, 365, 366);
		if (interfaceId == 368) return combineText(369, 370, 371, 372);
		if (interfaceId == 374) return combineText(375, 376, 377, 378, 379);

		// Add timed dialogues based on your DialogueManager
		if (interfaceId == 12378) return combineText(12381, 12382);
		if (interfaceId == 12383) return combineText(12386, 12387, 12388);
		if (interfaceId == 11891) return combineText(11894, 11895, 11896, 11897);

		if (interfaceId == 12773) return getTextFromChild(12776);
		if (interfaceId == 12777) return combineText(12780, 12781);
		if (interfaceId == 12782) return combineText(12785, 12786, 12787);
		if (interfaceId == 11884) return combineText(11887, 11888, 11889, 11890);

		if (interfaceId == 12788) return getTextFromChild(12789);
		if (interfaceId == 12790) return combineText(12791, 12792);
		if (interfaceId == 12793) return combineText(12794, 12795, 12796);
		if (interfaceId == 12797) return combineText(12798, 12799, 12800, 12801);
		if (interfaceId == 12802) return combineText(12803, 12804, 12805, 12806, 12807);

		return null;
	}

	// Extract NPC name by interface type based on your DialogueManager
	private static String extractNpcNameByType(int interfaceId) {
		if (interfaceId == 4882) return getTextFromChild(4884);
		if (interfaceId == 4887) return getTextFromChild(4889);
		if (interfaceId == 4893) return getTextFromChild(4895);
		if (interfaceId == 4900) return getTextFromChild(4902);

		// Timed NPC dialogues
		if (interfaceId == 12378) return getTextFromChild(12380);
		if (interfaceId == 12383) return getTextFromChild(12385);
		if (interfaceId == 11891) return getTextFromChild(11893);

		// Player dialogues use player name
		if (interfaceId == 968) return getTextFromChild(970);
		if (interfaceId == 973) return getTextFromChild(975);
		if (interfaceId == 979) return getTextFromChild(981);
		if (interfaceId == 986) return getTextFromChild(988);

		// Timed player dialogues
		if (interfaceId == 12773) return getTextFromChild(12775);
		if (interfaceId == 12777) return getTextFromChild(12779);
		if (interfaceId == 12782) return getTextFromChild(12784);
		if (interfaceId == 11884) return getTextFromChild(11886);

		return null;
	}

	private static String combineText(int... childIds) {
		StringBuilder sb = new StringBuilder();
		for (int childId : childIds) {
			String text = getTextFromChild(childId);
			if (text != null && !text.trim().isEmpty()) {
				if (sb.length() > 0) sb.append(" ");
				sb.append(text.trim());
			}
		}
		return sb.length() > 0 ? sb.toString() : null;
	}

	// Input field info class
	public static class InputFieldInfo {
		public final int childId;
		public final boolean isNumeric;

		public InputFieldInfo(int childId, boolean isNumeric) {
			this.childId = childId;
			this.isNumeric = isNumeric;
		}
	}
}