package com.bestbudz.entity.pets;

import com.bestbudz.data.items.ItemDef;
import com.bestbudz.entity.EntityDef;
import static com.bestbudz.entity.pets.petvariants.CorpVariant.corpPet;
import static com.bestbudz.entity.pets.npcvariants.DarkBeastNPCVariant.darkbeastPet;
import static com.bestbudz.entity.pets.petvariants.GraardorVariant.graardorPet;
import static com.bestbudz.entity.pets.petvariants.JadVariant.jadPet;
import static com.bestbudz.entity.pets.petvariants.KBDVariant.kbdPet;
import static com.bestbudz.entity.pets.petvariants.ZilyanaVariant.zilyanaPet;
import java.util.HashMap;
import java.util.Map;

public class PetItemCreator
{

	private static final Map<Integer, Integer> npcToItemMap = new HashMap<>();
	private static final Map<Integer, ModelRotationData> itemRotationMap = new HashMap<>();
	private static final ThreadLocal<Boolean> isCreatingVariant = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return false;
		}
	};

	// Inner class to store rotation data
	public static class ModelRotationData {
		public final int rotationX;
		public final int rotationY;
		public final int rotationZ; // anInt204
		public final int zoom;

		public ModelRotationData(int rotationX, int rotationY, int rotationZ, int zoom) {
			this.rotationX = rotationX;
			this.rotationY = rotationY;
			this.rotationZ = rotationZ;
			this.zoom = zoom;
		}
	}

	static {
		initializeNPCItemMappings();
		initializeRotationMappings();
	}


	private static void initializeNPCItemMappings() {
		// Sequential item IDs starting from 13224
		int itemId = 13224;

		int jadPetStages = 10001;
		int graardorPetStages = 10011;
		int kbdPetStages = 10021;
		int zilyanaPetStages = 10031;
		int corpPetStages = 10041;

		// Jad
		npcToItemMap.put(jadPet, itemId++); // 13224 = Item ID
		npcToItemMap.put(jadPetStages++, itemId++); // 13225
		npcToItemMap.put(jadPetStages++, itemId++); // 13226
		npcToItemMap.put(jadPetStages++, itemId++); // 13227
		npcToItemMap.put(jadPetStages++, itemId++); // 13228
		npcToItemMap.put(jadPetStages++, itemId++); // 13229
		npcToItemMap.put(jadPetStages++, itemId++); // 13230
		npcToItemMap.put(jadPetStages++, itemId++); // 13231
		npcToItemMap.put(jadPetStages++, itemId++); // 13232
		npcToItemMap.put(jadPetStages++, itemId++); // 13233

		// Graardor
		npcToItemMap.put(graardorPet, itemId++); // 13234
		npcToItemMap.put(graardorPetStages++, itemId++); // 13235
		npcToItemMap.put(graardorPetStages++, itemId++); // 13236
		npcToItemMap.put(graardorPetStages++, itemId++); // 13237
		npcToItemMap.put(graardorPetStages++, itemId++); // 13238
		npcToItemMap.put(graardorPetStages++, itemId++); // 13239
		npcToItemMap.put(graardorPetStages++, itemId++); // 13240
		npcToItemMap.put(graardorPetStages++, itemId++); // 13241
		npcToItemMap.put(graardorPetStages++, itemId++); // 13242
		npcToItemMap.put(graardorPetStages++, itemId++); // 13243

		// KBD
		npcToItemMap.put(kbdPet, itemId++); // 13244
		npcToItemMap.put(kbdPetStages++, itemId++); // 13245
		npcToItemMap.put(kbdPetStages++, itemId++); // 13246
		npcToItemMap.put(kbdPetStages++, itemId++); // 13247
		npcToItemMap.put(kbdPetStages++, itemId++); // 13248
		npcToItemMap.put(kbdPetStages++, itemId++); // 13249
		npcToItemMap.put(kbdPetStages++, itemId++); // 13250
		npcToItemMap.put(kbdPetStages++, itemId++); // 13251
		npcToItemMap.put(kbdPetStages++, itemId++); // 13252
		npcToItemMap.put(kbdPetStages++, itemId++); // 13253


		// Zilyana
		npcToItemMap.put(zilyanaPet, itemId++); // 13254
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13255
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13256
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13257
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13258
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13259
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13260
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13261
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13262
		npcToItemMap.put(zilyanaPetStages++, itemId++); // 13263

		// Corp
		npcToItemMap.put(corpPet, itemId++); // 13264
		npcToItemMap.put(corpPetStages++, itemId++); // 13265
		npcToItemMap.put(corpPetStages++, itemId++); // 13266
		npcToItemMap.put(corpPetStages++, itemId++); // 13267
		npcToItemMap.put(corpPetStages++, itemId++); // 13268
		npcToItemMap.put(corpPetStages++, itemId++); // 13269
		npcToItemMap.put(corpPetStages++, itemId++); // 13270
		npcToItemMap.put(corpPetStages++, itemId++); // 13271
		npcToItemMap.put(corpPetStages++, itemId++); // 13272
		npcToItemMap.put(corpPetStages++, itemId++); // 13273

		// Dark Beast NPC
		npcToItemMap.put(darkbeastPet, itemId++); // 13274
	}
	public static int totalItemsAvailable = 13283; // UPDATE FOR ITEM DUMP INCLUSION

	private static void initializeRotationMappings() {
		// Define custom rotations for each item
		// Format: itemId -> ModelRotationData(rotationX, rotationY, rotationZ, zoom)
		int newItems = 13224;
		// Jad
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //pet
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //toy
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //infant
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //hatchling
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //cub
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //youth
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //teen
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //young adult
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //adult
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500)); //prime

		// Graardor
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));

		// KBD
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));

		// Zilyana
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));
		itemRotationMap.put(newItems++, new ModelRotationData(0, 0, 0, 9000));

		// Corp
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 3500));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 8000));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 7500));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 7000));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 6500));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 6000));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 5500));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 5000));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 4500));
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 4000));

		// Dark Beast
		itemRotationMap.put(newItems++, new ModelRotationData(360, 0, 0, 3500));
	}

	public static int getItemForNPC(int npcId) {
		return npcToItemMap.getOrDefault(npcId, -1);
	}

	public static int getNPCForItem(int itemId) {
		for (Map.Entry<Integer, Integer> entry : npcToItemMap.entrySet()) {
			if (entry.getValue() == itemId) {
				return entry.getKey();
			}
		}
		return -1;
	}

	public static boolean isVariantPetItem(int itemId) {
		return npcToItemMap.containsValue(itemId);
	}

	// Add method to get rotation data for an item
	public static ModelRotationData getRotationData(int itemId) {
		return itemRotationMap.get(itemId);
	}

	// Add method to set custom rotation for an item
	public static void setItemRotation(int itemId, int rotationX, int rotationY, int rotationZ, int zoom) {
		itemRotationMap.put(itemId, new ModelRotationData(rotationX, rotationY, rotationZ, zoom));
		System.out.println("Set custom rotation for item " + itemId +
			": X=" + rotationX + ", Y=" + rotationY + ", Z=" + rotationZ + ", Zoom=" + zoom);
	}

	public static ItemDef createVariantPetItem(int itemId) {
		// Prevent recursion
		if (isCreatingVariant.get()) {
			System.out.println("Preventing recursion for item " + itemId);
			return null;
		}

		isCreatingVariant.set(true);
		try {
			return createVariantPetItemInternal(itemId);
		} finally {
			isCreatingVariant.set(false);
		}
	}

	private static ItemDef createVariantPetItemInternal(int itemId) {
		int npcId = getNPCForItem(itemId);
		if (npcId == -1) return null;

		// Get the variant NPC definition
		EntityDef npcDef = EntityDef.forID(npcId);
		if (npcDef == null) return null;

		// Create new item definition WITHOUT calling getItemDefinition recursively
		ItemDef variantItem = new ItemDef();

		// Set basic defaults directly instead of copying from another item
		variantItem.setDefaults();

		// Apply variant-specific properties
		variantItem.id = itemId;
		variantItem.name = npcDef.name;

		String description = "Summon your " + npcDef.name.toLowerCase() + " pet.";
		variantItem.description = description.getBytes();

		variantItem.itemActions = new String[5];
		variantItem.itemActions[4] = "Choose";

		// Use the NPC's model for the item
		if (npcDef.models != null && npcDef.models.length > 0) {
			variantItem.modelID = npcDef.models[0];
		} else {
			variantItem.modelID = 2677; // Fallback model
		}

		// Apply custom rotation and zoom settings
		applyModelRotation(variantItem, itemId);

		// Set reasonable item properties
		variantItem.stackable = false;
		variantItem.value = 1;

		// Apply variant modifications (size and actions only)
		applyVariantModifications(npcDef, variantItem, npcId);

		System.out.println("Created variant item " + itemId + " for NPC " + npcId +
			" with model " + variantItem.modelID +
			" (rotation: X=" + variantItem.modelRotationX +
			", Y=" + variantItem.modelRotationY +
			", Z=" + variantItem.anInt204 +
			", zoom=" + variantItem.modelZoom + ")");

		return variantItem;
	}

	private static void applyModelRotation(ItemDef itemDef, int itemId) {
		ModelRotationData rotationData = getRotationData(itemId);

		if (rotationData != null) {
			// Apply custom rotation settings
			itemDef.modelRotationX = rotationData.rotationX;
			itemDef.modelRotationY = rotationData.rotationY;
			itemDef.anInt204 = rotationData.rotationZ;
			itemDef.modelZoom = rotationData.zoom;

			System.out.println("Applied custom rotation to item " + itemId +
				": X=" + rotationData.rotationX +
				", Y=" + rotationData.rotationY +
				", Z=" + rotationData.rotationZ +
				", Zoom=" + rotationData.zoom);
		} else {
			// Apply default rotation settings
			itemDef.modelRotationX = 0;
			itemDef.modelRotationY = 0;
			itemDef.anInt204 = 0;
			itemDef.modelZoom = 9000;

			System.out.println("Applied default rotation to item " + itemId);
		}
	}

	private static void applyVariantModifications(EntityDef npcDef, ItemDef itemDef, int npcId) {
		// 1. Apply size modifications based on NPC scaling
		if (isScaleVariant(npcId)) {
			applyScaleModifications(itemDef, npcId);
		}

		// 2. Apply special action modifications
		if (hasSpecialActions(npcId)) {
			applySpecialActions(npcDef, itemDef, npcId);
		}
	}

	private static boolean isScaleVariant(int npcId) {
		// Mini/Giant petvariants (size-based)
		return npcId == 10001 || npcId == 10002; // Mini Jad, Giant Jad
	}

	private static void applyScaleModifications(ItemDef itemDef, int npcId) {
		// Adjust item zoom based on NPC scale
		switch (npcId) {
			case 10001: // Mini Jad
				itemDef.modelZoom = (int)(itemDef.modelZoom * 0.7); // Smaller in inventory
				System.out.println("Applied mini scale to item " + itemDef.id);
				break;
			case 10002: // Giant Jad
				itemDef.modelZoom = (int)(itemDef.modelZoom * 1.3); // Bigger in inventory
				System.out.println("Applied giant scale to item " + itemDef.id);
				break;
		}
	}

	private static boolean hasSpecialActions(int npcId) {
		// Variants with unique actions (most petvariants have special actions)
		return npcId >= 10000 && npcId != 10001; // All except mini jad which only has "Examine"
	}

	private static void applySpecialActions(EntityDef npcDef, ItemDef itemDef, int npcId) {
		// Copy special actions from NPC to item if needed
		if (npcDef.actions != null && npcDef.actions[1] != null) {
			itemDef.itemActions[2] = npcDef.actions[1]; // Add NPC action as item action
			System.out.println("Added special action '" + npcDef.actions[1] + "' to item " + itemDef.id);
		}
	}

	// Utility methods for runtime rotation adjustments

	/**
	 * Updates the rotation of an existing variant item
	 */
	public static void updateItemRotation(int itemId, int rotationX, int rotationY, int rotationZ, int zoom) {
		if (!isVariantPetItem(itemId)) {
			System.out.println("Item " + itemId + " is not a variant pet item");
			return;
		}

		setItemRotation(itemId, rotationX, rotationY, rotationZ, zoom);
		System.out.println("Updated rotation for variant item " + itemId);
	}

	/**
	 * Gets the current rotation settings for an item
	 */
	public static void printItemRotation(int itemId) {
		ModelRotationData data = getRotationData(itemId);
		if (data != null) {
			System.out.println("Item " + itemId + " rotation: X=" + data.rotationX +
				", Y=" + data.rotationY + ", Z=" + data.rotationZ + ", Zoom=" + data.zoom);
		} else {
			System.out.println("Item " + itemId + " has no custom rotation data");
		}
	}
}