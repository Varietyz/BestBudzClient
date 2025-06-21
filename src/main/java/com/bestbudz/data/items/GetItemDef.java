package com.bestbudz.data.items;

import com.bestbudz.entity.pets.PetItemCreator;
import java.util.Set;
import java.util.HashSet;

/**
 * Refactored GetItemDef class that uses the ItemDefinitions enum for O(1) lookup performance
 * and much easier maintenance. All item definitions are now centralized in the enum.
 */
public class GetItemDef
{

	// Set of IDs that should have "Unpack" action - for better performance than multiple comparisons
	private static final Set<Integer> UNPACK_ITEMS = new HashSet<>();

	static {
		// Initialize unpack items set
		int[] unpackIds = {
			12690, 12962, 12972, 12974, 12984, 12986, 12988, 12990, 13000, 13002,
			13012, 13014, 13024, 13026, 9666, 9670, 12865, 12867, 12869, 12871,
			12966, 12964, 12968, 12970, 12976, 12978, 12980, 12982, 12992, 12994,
			12996, 12998, 13004, 13006, 13008, 13010, 13016, 13018, 13020, 13022,
			13028, 13030, 13032, 13034, 13036, 13038, 12960, 13173, 13175
		};

		for (int id : unpackIds) {
			UNPACK_ITEMS.add(id);
		}
	}

	/**
	 * Main method to get item definitions. Now uses enum lookup for O(1) performance
	 * instead of the massive switch statement.
	 *
	 * @param id The item ID to get definition for
	 * @return ItemDef instance with all properties applied
	 */
	public static ItemDef getItemDefinition(int id) {
		if (PetItemCreator.isVariantPetItem(id)) {
			ItemDef variantItem = PetItemCreator.createVariantPetItem(id);
			if (variantItem != null) {
				return variantItem;
			}
		}

		// Check cache first - same as original implementation
		for (int j = 0; j < 10; j++) {
			if (ItemDef.cache[j].id == id) {
				return ItemDef.cache[j];
			}
		}

		// Load from stream - same as original implementation
		ItemDef.cacheIndex = (ItemDef.cacheIndex + 1) % 10;
		ItemDef itemDef = ItemDef.cache[ItemDef.cacheIndex];
		ItemDef.stream.currentOffset = ItemDef.streamIndices[id];
		itemDef.id = id;
		itemDef.setDefaults();
		itemDef.readValues(ItemDef.stream);

		// Handle members check - same as original implementation
		if (!ItemDef.isMembers && itemDef.membersObject) {
			itemDef.name = "BestBudz Members Bug";
			itemDef.description = "Membership is bugged.".getBytes();
			itemDef.groundActions = null;
			itemDef.itemActions = null;
			itemDef.team = 0;
			return itemDef;
		}

		// Apply custom definitions using enum lookup - O(1) performance!
		applyCustomDefinition(itemDef, id);

		// Handle special cases that don't fit the standard pattern
		handleSpecialCases(itemDef, id);

		// Handle certification - same as original implementation
		if (itemDef.certTemplateID != -1) {
			itemDef.toNote();
		}

		return itemDef;
	}

	/**
	 * Apply custom item definition from enum if it exists.
	 * This replaces the massive switch statement with a single O(1) lookup.
	 *
	 * @param itemDef The ItemDef to modify
	 * @param id The item ID
	 */
	private static void applyCustomDefinition(ItemDef itemDef, int id) {
		ItemDefinitions definition = ItemDefinitions.getById(id);
		if (definition == null) {
			return; // No custom definition for this item
		}

		// Apply name
		if (definition.getName() != null) {
			itemDef.name = definition.getName();
		}

		// Apply actions
		if (definition.getActions() != null) {
			itemDef.itemActions = definition.getActions().clone();
		}

		// Apply description
		if (definition.getDescription() != null) {
			itemDef.description = definition.getDescription().getBytes();
		}

		// Apply model properties
		if (definition.getModelID() != null) {
			itemDef.modelID = definition.getModelID();
		}

		if (definition.getModelZoom() != null) {
			itemDef.modelZoom = definition.getModelZoom();
		}

		if (definition.getModelRotationY() != null) {
			itemDef.modelRotationY = definition.getModelRotationY();
		}

		if (definition.getModelRotationX() != null) {
			itemDef.modelRotationX = definition.getModelRotationX();
		}

		if (definition.getModelOffset1() != null) {
			itemDef.modelOffset1 = definition.getModelOffset1();
		}

		if (definition.getModelOffset2() != null) {
			itemDef.modelOffset2 = definition.getModelOffset2();
		}

		if (definition.getAnInt165() != null) {
			itemDef.anInt165 = definition.getAnInt165();
		}

		if (definition.getAnInt200() != null) {
			itemDef.anInt200 = definition.getAnInt200();
		}

		if (definition.getStackable() != null) {
			itemDef.stackable = definition.getStackable();
		}
	}

	/**
	 * Handle special cases that don't fit the standard enum pattern.
	 * This keeps the special logic separate and maintainable.
	 *
	 * @param itemDef The ItemDef to modify
	 * @param id The item ID
	 */
	private static void handleSpecialCases(ItemDef itemDef, int id) {
		switch (id) {
			// Special case: Check charges action modification
			case 2568:
				ensureItemActionsArray(itemDef);
				itemDef.itemActions[2] = "Check charges";
				break;

			// Special case: Mercenary gem with specific action
			case 4155:
				itemDef.name = "Mercenary gem";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Check-task";
				break;

			// Armor sets with unpack action
			case 6828:
				itemDef.name = "Armour set 1";
				ensureItemActionsArray(itemDef);
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6829:
				itemDef.name = "Armour set 2";
				ensureItemActionsArray(itemDef);
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6830:
				itemDef.name = "Armour set 3";
				ensureItemActionsArray(itemDef);
				itemDef.itemActions[0] = "Unpack";
				break;

			case 6831:
				itemDef.name = "Armour set 4";
				ensureItemActionsArray(itemDef);
				itemDef.itemActions[0] = "Unpack";
				break;

			default:
				// Handle generic unpack items using the set for O(1) lookup
				if (UNPACK_ITEMS.contains(id)) {
					ensureItemActionsArray(itemDef);
					itemDef.itemActions[0] = "Unpack";
				}
				break;
		}
	}

	/**
	 * Utility method to ensure itemActions array exists before modifying it.
	 * Prevents NullPointerException when setting actions.
	 *
	 * @param itemDef The ItemDef to check/modify
	 */
	private static void ensureItemActionsArray(ItemDef itemDef) {
		if (itemDef.itemActions == null) {
			itemDef.itemActions = new String[5];
		}
	}


}