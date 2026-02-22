package com.bestbudz.data.items;

import bestbudz.config.ItemEntry;
import com.bestbudz.entity.pets.PetItemCreator;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

public class GetItemDef
{

	private static final Set<Integer> UNPACK_ITEMS = new HashSet<>();

	private static final Map<Integer, ItemDef> variantItemCache = new HashMap<>();

	private static boolean variantItemsPreloaded = false;

	static {

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

		preloadVariantItems();
	}

	private static void preloadVariantItems() {
		if (variantItemsPreloaded) return;

		System.out.println("Pre-loading variant pet items...");

		for (int itemId = 13224; itemId <= PetItemCreator.totalItemsAvailable; itemId++) {
			if (PetItemCreator.isVariantPetItem(itemId)) {
				ItemDef variantItem = PetItemCreator.createVariantPetItem(itemId);
				if (variantItem != null) {
					variantItemCache.put(itemId, variantItem);
					System.out.println("Pre-loaded variant item " + itemId + " (" + variantItem.name + ")");
				}
			}
		}

		variantItemsPreloaded = true;
		System.out.println("Pre-loaded " + variantItemCache.size() + " variant items - no more recreation needed!");
	}

	public static ItemDef getItemDefinition(int id) {

		if (PetItemCreator.isVariantPetItem(id)) {
			return getVariantItemDefinition(id);
		}

		for (int j = 0; j < ItemDef.cache.length; j++) {
			if (ItemDef.cache[j].id == id) {
				ItemDef.cacheHits++;
				return ItemDef.cache[j];
			}
		}

		ItemDef.cacheMisses++;
		ItemDef.cacheIndex = (ItemDef.cacheIndex + 1) % ItemDef.cache.length;
		ItemDef itemDef = ItemDef.cache[ItemDef.cacheIndex];
		itemDef.id = id;
		itemDef.setDefaults();
		if (ItemDef.fbConfig != null) {
			ItemEntry fb = ItemDef.fbConfig.entriesByKey(id);
			if (fb != null) {
				itemDef.loadFromFlatBuffer(fb);
			}
		} else if (ItemDef.jsonDefs != null && id >= 0 && id < ItemDef.jsonDefs.length && ItemDef.jsonDefs[id] != null) {
			itemDef.readFromJson(ItemDef.jsonDefs[id]);
		}

		if (!ItemDef.isMembers && itemDef.membersObject) {
			itemDef.name = "BestBudz Members Bug";
			itemDef.description = "Membership is bugged.".getBytes();
			itemDef.groundActions = null;
			itemDef.itemActions = null;
			itemDef.team = 0;
			return itemDef;
		}

		applyCustomDefinition(itemDef, id);

		handleSpecialCases(itemDef, id);

		if (itemDef.certTemplateID != -1) {
			itemDef.toNote();
		}

		return itemDef;
	}

	private static ItemDef getVariantItemDefinition(int id) {

		if (!variantItemsPreloaded) {
			preloadVariantItems();
		}

		ItemDef cached = variantItemCache.get(id);
		if (cached != null) {
			return cached;
		}

		System.out.println("Warning: Variant item " + id + " not found in cache, creating on demand");
		ItemDef variantItem = PetItemCreator.createVariantPetItem(id);
		if (variantItem != null) {
			variantItemCache.put(id, variantItem);
		}
		return variantItem;
	}

	private static void applyCustomDefinition(ItemDef itemDef, int id) {
		ItemDefinitions definition = ItemDefinitions.getById(id);
		if (definition == null) {
			return;
		}

		if (definition.getName() != null) {
			itemDef.name = definition.getName();
		}

		if (definition.getActions() != null) {
			itemDef.itemActions = definition.getActions().clone();
		}

		if (definition.getDescription() != null) {
			itemDef.description = definition.getDescription().getBytes();
		}

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

	private static void handleSpecialCases(ItemDef itemDef, int id) {
		switch (id) {

			case 2568:
				ensureItemActionsArray(itemDef);
				itemDef.itemActions[2] = "Check charges";
				break;

			case 4155:
				itemDef.name = "Mercenary gem";
				itemDef.itemActions = new String[5];
				itemDef.itemActions[0] = "Check-task";
				break;

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

				if (UNPACK_ITEMS.contains(id)) {
					ensureItemActionsArray(itemDef);
					itemDef.itemActions[0] = "Unpack";
				}
				break;
		}
	}

	private static void ensureItemActionsArray(ItemDef itemDef) {
		if (itemDef.itemActions == null) {
			itemDef.itemActions = new String[5];
		}
	}

	public static void clearVariantCache() {
		variantItemCache.clear();
		variantItemsPreloaded = false;
		System.out.println("Cleared variant item cache");
	}

	public static void printCacheStats() {
		System.out.println("Variant item cache: " + variantItemCache.size() + " items cached");
		System.out.println("Preloaded: " + variantItemsPreloaded);
	}
}
