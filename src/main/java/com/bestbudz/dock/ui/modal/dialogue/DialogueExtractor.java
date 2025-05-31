package com.bestbudz.dock.ui.modal.dialogue;

import com.bestbudz.ui.RSInterface;
import java.util.ArrayList;
import java.util.List;

/**
 * DialogueExtractor - Extract text and options directly from RSInterface objects
 * This supplements the DialogueCore mapping system with direct interface reading
 */
public class DialogueExtractor {

	/**
	 * Extract dialogue options from interface
	 */
	public static String[] extractDialogueOptions(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return null;

		int interfaceId = dialogueInterface.id;
		System.out.println("🎭 EXTRACTOR: Extracting options for interface " + interfaceId);

		// Handle option interfaces
		switch (interfaceId) {
			case 2459: // 2 options
				return getOptionsFromChildren(new int[]{2461, 2462});
			case 2469: // 3 options
				return getOptionsFromChildren(new int[]{2471, 2472, 2473});
			case 2480: // 4 options
				return getOptionsFromChildren(new int[]{2482, 2483, 2484, 2485});
			case 2492: // 5 options
				return getOptionsFromChildren(new int[]{2494, 2495, 2496, 2497, 2498});

			// Item interfaces
			case 8880: // Make-X
				return getOptionsFromChildren(new int[]{8889, 8893, 8897});
			case 6231: // 2 items
				return getOptionsFromChildren(new int[]{6232, 6233});
			case 306: // Single item - no options
				return null;
		}

		return null;
	}

	/**
	 * Extract option button IDs
	 */
	public static int[] extractOptionChildIds(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return null;

		int interfaceId = dialogueInterface.id;

		// Return the ACTUAL clickable button IDs
		switch (interfaceId) {
			case 2459: // 2 options
				return new int[]{9157, 9158};
			case 2469: // 3 options
				return new int[]{9167, 9168, 9169};
			case 2480: // 4 options
				return new int[]{9178, 9179, 9180, 9181};
			case 2492: // 5 options
				return new int[]{9190, 9191, 9192, 9193, 9194};
			case 8880: // Make-X
				return new int[]{8883, 8884, 8885};
			case 6231: // 2 items
				return new int[]{6235, 6236};
			case 306: // Single item
				return new int[]{306};
		}

		return null;
	}

	/**
	 * Extract all dialogue text
	 */
	public static String extractAllDialogueText(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return "Dialogue";

		int interfaceId = dialogueInterface.id;
		System.out.println("🎭 EXTRACTOR: Extracting text for interface " + interfaceId);

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
		} else if (interfaceId == 2400) {
			// Mining interface - get text from child 18028
			return getTextFromChild(18028);
		}

		// For regular dialogues, extract text from known patterns
		String text = extractDialogueTextByType(interfaceId);
		if (text != null && !text.trim().isEmpty()) {
			return text;
		}

		// Fallback: try to get text from the interface itself
		if (dialogueInterface.disabledMessage != null && !dialogueInterface.disabledMessage.trim().isEmpty()) {
			return cleanText(dialogueInterface.disabledMessage.trim());
		}

		return "Interface " + interfaceId;
	}

	/**
	 * Extract NPC name
	 */
	public static String extractNpcName(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return "NPC";

		int interfaceId = dialogueInterface.id;

		// Get NPC name from known dialogue patterns
		String name = extractNpcNameByType(interfaceId);
		if (name != null && !name.trim().isEmpty()) {
			return name;
		}

		// For item interfaces, show descriptive names
		if (interfaceId == 8880) return "Crafting";
		if (interfaceId == 6231) return "Item Selection";
		if (interfaceId == 306) return "Item Information";
		if (interfaceId == 2400) return "Mining Interface";

		return "NPC";
	}

	/**
	 * Check if interface is an item interface
	 */
	public static boolean isItemInterface(int interfaceId) {
		return interfaceId == 306 || interfaceId == 6231 || interfaceId == 8880 ||
			interfaceId == 6179 || interfaceId == 2400;
	}

	/**
	 * Extract continue button text
	 */
	public static String extractContinueButtonText(RSInterface dialogueInterface) {
		if (dialogueInterface == null) return null;

		int interfaceId = dialogueInterface.id;

		// These interfaces should have continue buttons
		if (interfaceId == 4882 || interfaceId == 4887 || interfaceId == 4893 || interfaceId == 4900 || // NPC
			interfaceId == 968 || interfaceId == 973 || interfaceId == 979 || interfaceId == 986 || // Player
			interfaceId == 356 || interfaceId == 359 || interfaceId == 363 || interfaceId == 368 || interfaceId == 374 || // Statement
			interfaceId == 306) { // Single item
			return "Continue";
		}

		return null;
	}

	// Helper methods
	private static String[] getOptionsFromChildren(int[] childIds) {
		List<String> options = new ArrayList<String>();

		for (int childId : childIds) {
			String text = getTextFromChild(childId);
			if (text != null && !text.trim().isEmpty()) {
				options.add(cleanText(text.trim()));
				System.out.println("🎭 EXTRACTOR: Found option: '" + text.trim() + "' from child " + childId);
			}
		}

		return options.isEmpty() ? null : options.toArray(new String[options.size()]);
	}

	private static String getTextFromChild(int childId) {
		try {
			if (childId > 0 && childId < RSInterface.interfaceCache.length) {
				RSInterface child = RSInterface.interfaceCache[childId];
				if (child != null && child.disabledMessage != null) {
					return child.disabledMessage;
				}
			}
		} catch (Exception e) {
			System.err.println("🎭 EXTRACTOR: Error getting text from child " + childId + ": " + e.getMessage());
		}
		return null;
	}

	// Extract dialogue text by interface type
	private static String extractDialogueTextByType(int interfaceId) {
		// NPC dialogues
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

		// Timed dialogues
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

	// Extract NPC name by interface type
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

	private static String cleanText(String text) {
		if (text == null) return "";

		// Remove HTML tags
		text = text.replaceAll("<[^>]*>", "");

		// Clean up whitespace
		text = text.replaceAll("\\s+", " ").trim();

		// Remove special characters that might cause issues
		text = text.replaceAll("[\\x00-\\x1F\\x7F]", "");

		return text;
	}

	/**
	 * Enhanced method to supplement DialogueCore with direct interface reading
	 */
	public static void supplementDialogueCore(DialogueCore dialogueCore, int interfaceId) {
		try {
			if (interfaceId <= 0 || interfaceId >= RSInterface.interfaceCache.length) return;

			RSInterface dialogueInterface = RSInterface.interfaceCache[interfaceId];
			if (dialogueInterface == null) return;

			System.out.println("🎭 EXTRACTOR: Supplementing DialogueCore for interface " + interfaceId);

			// Extract and apply text if DialogueCore doesn't have it
			String[] lines = dialogueCore.getLines();
			if (lines.length == 0 || (lines.length == 1 && lines[0].isEmpty())) {
				String extractedText = extractAllDialogueText(dialogueInterface);
				if (extractedText != null && !extractedText.trim().isEmpty()) {
					System.out.println("🎭 EXTRACTOR: Adding extracted text: " + extractedText);
					dialogueCore.handleStringPacket(308, extractedText); // Use frame 308 as default
				}
			}

			// Extract and apply options if DialogueCore doesn't have them
			String[] options = dialogueCore.getOptions();
			if (options.length == 0) {
				String[] extractedOptions = extractDialogueOptions(dialogueInterface);
				if (extractedOptions != null) {
					System.out.println("🎭 EXTRACTOR: Adding extracted options: " + java.util.Arrays.toString(extractedOptions));
					// Apply options to appropriate frames based on interface type
					if (interfaceId == 2459) {
						for (int i = 0; i < extractedOptions.length && i < 2; i++) {
							dialogueCore.handleStringPacket(2461 + i, extractedOptions[i]);
						}
					} else if (interfaceId == 2469) {
						for (int i = 0; i < extractedOptions.length && i < 3; i++) {
							dialogueCore.handleStringPacket(2471 + i, extractedOptions[i]);
						}
					} else if (interfaceId == 2480) {
						for (int i = 0; i < extractedOptions.length && i < 4; i++) {
							dialogueCore.handleStringPacket(2482 + i, extractedOptions[i]);
						}
					} else if (interfaceId == 2492) {
						for (int i = 0; i < extractedOptions.length && i < 5; i++) {
							dialogueCore.handleStringPacket(2494 + i, extractedOptions[i]);
						}
					}
				}
			}

			// Extract and apply NPC name if not present
			if (dialogueCore.getTitle().isEmpty()) {
				String extractedName = extractNpcName(dialogueInterface);
				if (extractedName != null && !extractedName.equals("NPC")) {
					System.out.println("🎭 EXTRACTOR: Adding extracted NPC name: " + extractedName);
					// Apply to appropriate title frame based on interface type
					if (interfaceId == 4882) {
						dialogueCore.handleStringPacket(4884, extractedName);
					} else if (interfaceId == 4887) {
						dialogueCore.handleStringPacket(4889, extractedName);
					} else if (interfaceId == 4893) {
						dialogueCore.handleStringPacket(4895, extractedName);
					} else if (interfaceId == 4900) {
						dialogueCore.handleStringPacket(4902, extractedName);
					}
				}
			}

		} catch (Exception e) {
			System.err.println("🎭 EXTRACTOR: Error supplementing DialogueCore: " + e.getMessage());
		}
	}
}