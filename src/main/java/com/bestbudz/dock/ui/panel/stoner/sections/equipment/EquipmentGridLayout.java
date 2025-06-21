package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the layout and positioning of equipment slots in the grid
 */
public class EquipmentGridLayout {

	private final JPanel gridPanel;
	private final Map<Integer, EquipmentSlotPanel> slotPanels = new HashMap<>();
	private final EquipmentClickBatcher clickBatcher;

	public EquipmentGridLayout(EquipmentClickBatcher clickBatcher) {
		this.clickBatcher = clickBatcher;
		this.gridPanel = createGridPanel();
		createSlotPanels();
	}

	private JPanel createGridPanel() {
		JPanel grid = new JPanel();
		grid.setLayout(new GridBagLayout());
		grid.setBackground(EquipmentConstants.SECTION_BG);
		grid.setOpaque(true);
		grid.setBorder(new EmptyBorder(4, 4, 4, 4));
		grid.setPreferredSize(new Dimension(160, 220));
		return grid;
	}

	private void createSlotPanels() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(1, 1, 1, 1);

		// Equipment layout (classic RuneScape style)
		// Row 0: Helmet
		addSlotPanel(1645, 1, 0, gbc);

		// Row 1: Cape, Amulet, Ammo
		addSlotPanel(1646, 0, 1, gbc);
		addSlotPanel(1647, 1, 1, gbc);
		addSlotPanel(1655, 2, 1, gbc);

		// Row 2: Weapon, Body, Shield
		addSlotPanel(1648, 0, 2, gbc);
		addSlotPanel(1649, 1, 2, gbc);
		addSlotPanel(1650, 2, 2, gbc);

		// Row 3: Legs
		addSlotPanel(1651, 1, 3, gbc);

		// Row 4: Hands, Feet, Ring
		addSlotPanel(1652, 0, 4, gbc);
		addSlotPanel(1653, 1, 4, gbc);
		addSlotPanel(1654, 2, 4, gbc);
	}

	private void addSlotPanel(int slotId, int gridX, int gridY, GridBagConstraints gbc) {
		EquipmentSlotPanel slotPanel = new EquipmentSlotPanel(slotId, clickBatcher);
		slotPanels.put(slotId, slotPanel);

		gbc.gridx = gridX;
		gbc.gridy = gridY;
		gridPanel.add(slotPanel, gbc);
	}

	public JPanel getGridPanel() {
		return gridPanel;
	}

	public Map<Integer, EquipmentSlotPanel> getSlotPanels() {
		return slotPanels;
	}

	public void clearAllSlots() {
		for (EquipmentSlotPanel slotPanel : slotPanels.values()) {
			slotPanel.clearItem();
		}
	}

	public void updateSlot(int slotId, int itemId, int amount) {
		EquipmentSlotPanel slotPanel = slotPanels.get(slotId);
		if (slotPanel != null) {
			if (itemId >= 0 && amount > 0) {
				slotPanel.setItem(itemId, amount);
			} else {
				slotPanel.clearItem();
			}
		}
	}
}