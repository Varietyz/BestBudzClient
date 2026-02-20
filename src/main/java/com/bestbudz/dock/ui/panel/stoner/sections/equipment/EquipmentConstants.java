package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EquipmentConstants {

	public static final Color SECTION_BG = new Color(35, 35, 35);
	public static final Color BORDER = new Color(60, 60, 60);
	public static final Color TEXT_PRIMARY = new Color(255, 255, 255);
	public static final Color TEXT_SECONDARY = new Color(170, 170, 170);
	public static final Color SLOT_BG = new Color(25, 25, 25);
	public static final Color SLOT_HOVER = new Color(45, 45, 45);

	public static final int UPDATE_INTERVAL_MS = 250;
	public static final int BATCH_WINDOW_MS = 300;
	public static final int UI_UPDATE_DELAY_MS = 600;

	private static final Map<Integer, String> SLOT_NAMES = new HashMap<>();
	static {
		SLOT_NAMES.put(1645, "Helmet");
		SLOT_NAMES.put(1646, "Cape");
		SLOT_NAMES.put(1647, "Amulet");
		SLOT_NAMES.put(1648, "Weapon");
		SLOT_NAMES.put(1649, "Body");
		SLOT_NAMES.put(1650, "Shield");
		SLOT_NAMES.put(1651, "Legs");
		SLOT_NAMES.put(1652, "Hands");
		SLOT_NAMES.put(1653, "Feet");
		SLOT_NAMES.put(1654, "Ring");
		SLOT_NAMES.put(1655, "Ammo");
	}

	private static final Map<Integer, Integer> SLOT_TO_EQUIPMENT_INDEX = new HashMap<>();
	static {
		SLOT_TO_EQUIPMENT_INDEX.put(1645, 0);
		SLOT_TO_EQUIPMENT_INDEX.put(1646, 1);
		SLOT_TO_EQUIPMENT_INDEX.put(1647, 2);
		SLOT_TO_EQUIPMENT_INDEX.put(1648, 3);
		SLOT_TO_EQUIPMENT_INDEX.put(1649, 4);
		SLOT_TO_EQUIPMENT_INDEX.put(1650, 5);
		SLOT_TO_EQUIPMENT_INDEX.put(1651, 7);
		SLOT_TO_EQUIPMENT_INDEX.put(1652, 9);
		SLOT_TO_EQUIPMENT_INDEX.put(1653, 10);
		SLOT_TO_EQUIPMENT_INDEX.put(1654, 12);
		SLOT_TO_EQUIPMENT_INDEX.put(1655, 13);
	}

	public static final int[] ASSAULT_STAT_IDS = {1675, 1676, 1677, 1678, 1679};
	public static final int[] AEGIS_STAT_IDS = {1680, 1681, 1682, 1683, 1684};
	public static final int[] EXTRA_STAT_IDS = {1686, 1687};

	public static final int EQUIPMENT_INTERFACE_ID = 1688;

	public static String getSlotName(int slotId) {
		return SLOT_NAMES.getOrDefault(slotId, "Equipment Slot");
	}

	public static Integer getEquipmentIndex(int slotId) {
		return SLOT_TO_EQUIPMENT_INDEX.get(slotId);
	}

	public static Map<Integer, String> getAllSlotNames() {
		return new HashMap<>(SLOT_NAMES);
	}

	public static Map<Integer, Integer> getAllSlotMappings() {
		return new HashMap<>(SLOT_TO_EQUIPMENT_INDEX);
	}
}
