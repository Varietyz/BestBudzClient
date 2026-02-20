package com.bestbudz.entity.pets;

import com.bestbudz.data.items.ItemDef;
import com.bestbudz.entity.EntityDef;
import static com.bestbudz.entity.pets.petvariants.CorpVariant.corpPet;
import static com.bestbudz.entity.pets.npcvariants.AbyssalDemonNPCVariant.abyssalDemonPet;
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

	public static class ModelRotationData {
		public final int rotationX;
		public final int rotationY;
		public final int rotationZ;
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

		int itemId = 13224;

		int jadPetStages = 10001;
		int graardorPetStages = 10011;
		int kbdPetStages = 10021;
		int zilyanaPetStages = 10031;
		int corpPetStages = 10041;

		npcToItemMap.put(jadPet, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);
		npcToItemMap.put(jadPetStages++, itemId++);

		npcToItemMap.put(graardorPet, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);
		npcToItemMap.put(graardorPetStages++, itemId++);

		npcToItemMap.put(kbdPet, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);
		npcToItemMap.put(kbdPetStages++, itemId++);

		npcToItemMap.put(zilyanaPet, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);
		npcToItemMap.put(zilyanaPetStages++, itemId++);

		npcToItemMap.put(corpPet, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);
		npcToItemMap.put(corpPetStages++, itemId++);

		npcToItemMap.put(abyssalDemonPet, itemId++);
	}
	public static int totalItemsAvailable = 13283;

	private static void initializeRotationMappings() {

		int newItems = 13224;

		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));
		itemRotationMap.put(newItems++, new ModelRotationData(300, 100, 0, 9500));

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

	public static ModelRotationData getRotationData(int itemId) {
		return itemRotationMap.get(itemId);
	}

	public static void setItemRotation(int itemId, int rotationX, int rotationY, int rotationZ, int zoom) {
		itemRotationMap.put(itemId, new ModelRotationData(rotationX, rotationY, rotationZ, zoom));
		System.out.println("Set custom rotation for item " + itemId +
			": X=" + rotationX + ", Y=" + rotationY + ", Z=" + rotationZ + ", Zoom=" + zoom);
	}

	public static ItemDef createVariantPetItem(int itemId) {

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

		EntityDef npcDef = EntityDef.forID(npcId);
		if (npcDef == null) return null;

		ItemDef variantItem = new ItemDef();

		variantItem.setDefaults();

		variantItem.id = itemId;
		variantItem.name = npcDef.name;

		String description = "Summon your " + npcDef.name.toLowerCase() + " pet.";
		variantItem.description = description.getBytes();

		variantItem.itemActions = new String[5];
		variantItem.itemActions[4] = "Choose";

		if (npcDef.models != null && npcDef.models.length > 0) {
			variantItem.modelID = npcDef.models[0];
		} else {
			variantItem.modelID = 2677;
		}

		applyModelRotation(variantItem, itemId);

		variantItem.stackable = false;
		variantItem.value = 1;

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

			itemDef.modelRotationX = 0;
			itemDef.modelRotationY = 0;
			itemDef.anInt204 = 0;
			itemDef.modelZoom = 9000;

			System.out.println("Applied default rotation to item " + itemId);
		}
	}

	private static void applyVariantModifications(EntityDef npcDef, ItemDef itemDef, int npcId) {

		if (isScaleVariant(npcId)) {
			applyScaleModifications(itemDef, npcId);
		}

		if (hasSpecialActions(npcId)) {
			applySpecialActions(npcDef, itemDef, npcId);
		}
	}

	private static boolean isScaleVariant(int npcId) {

		return npcId == 10001 || npcId == 10002;
	}

	private static void applyScaleModifications(ItemDef itemDef, int npcId) {

		switch (npcId) {
			case 10001:
				itemDef.modelZoom = (int)(itemDef.modelZoom * 0.7);
				System.out.println("Applied mini scale to item " + itemDef.id);
				break;
			case 10002:
				itemDef.modelZoom = (int)(itemDef.modelZoom * 1.3);
				System.out.println("Applied giant scale to item " + itemDef.id);
				break;
		}
	}

	private static boolean hasSpecialActions(int npcId) {

		return npcId >= 10000 && npcId != 10001;
	}

	private static void applySpecialActions(EntityDef npcDef, ItemDef itemDef, int npcId) {

		if (npcDef.actions != null && npcDef.actions[1] != null) {
			itemDef.itemActions[2] = npcDef.actions[1];
			System.out.println("Added special action '" + npcDef.actions[1] + "' to item " + itemDef.id);
		}
	}

	public static void updateItemRotation(int itemId, int rotationX, int rotationY, int rotationZ, int zoom) {
		if (!isVariantPetItem(itemId)) {
			System.out.println("Item " + itemId + " is not a variant pet item");
			return;
		}

		setItemRotation(itemId, rotationX, rotationY, rotationZ, zoom);
		System.out.println("Updated rotation for variant item " + itemId);
	}

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
