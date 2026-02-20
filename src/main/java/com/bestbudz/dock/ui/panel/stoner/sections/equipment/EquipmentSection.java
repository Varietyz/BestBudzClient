package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

public class EquipmentSection implements EquipmentDataManager.EquipmentUpdateListener {

	private JPanel panel;
	private JLabel statsLabel;

	private final EquipmentDataManager dataManager;
	private final EquipmentStatsRenderer statsRenderer;
	private final EquipmentGridLayout gridLayout;
	private final EquipmentClickBatcher clickBatcher;
	private final EquipmentMagebook equipmentMagebook;

	public EquipmentSection() {

		this.clickBatcher = new EquipmentClickBatcher(this::onBatchComplete);
		this.dataManager = new EquipmentDataManager(this);
		this.statsRenderer = new EquipmentStatsRenderer();
		this.gridLayout = new EquipmentGridLayout(clickBatcher);
		this.equipmentMagebook = new EquipmentMagebook();
	}

	public JPanel createPanel(int width) {
		panel = new JPanel(new BorderLayout());
		panel.setBackground(EquipmentConstants.SECTION_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(EquipmentConstants.BORDER, 1),
			BorderFactory.createEmptyBorder(8, 8, 8, 8)
		));
		panel.setPreferredSize(new Dimension(width - 16, 360));
		panel.setMaximumSize(new Dimension(width - 16, 360));

		createComponents(width);
		return panel;
	}

	private void createComponents(int width) {

		JPanel header = createHeader();
		panel.add(header, BorderLayout.NORTH);

		JPanel mainContent = new JPanel(new BorderLayout());
		mainContent.setBackground(EquipmentConstants.SECTION_BG);
		mainContent.setOpaque(true);

		JPanel equipmentGrid = gridLayout.getGridPanel();

		JPanel statsPanel = createStatsPanel();

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, equipmentGrid, statsPanel);
		splitPane.setDividerLocation(170);
		splitPane.setDividerSize(2);
		splitPane.setBorder(null);
		splitPane.setBackground(EquipmentConstants.SECTION_BG);
		splitPane.setOpaque(true);

		mainContent.add(splitPane, BorderLayout.CENTER);

		mainContent.add(equipmentMagebook.getControlsPanel(), BorderLayout.SOUTH);

		panel.add(mainContent, BorderLayout.CENTER);
	}

	private JPanel createHeader() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(EquipmentConstants.SECTION_BG);
		header.setOpaque(true);
		header.setBorder(new EmptyBorder(4, 0, 8, 0));

		JLabel titleLabel = new JLabel("Equipment");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
		titleLabel.setForeground(EquipmentConstants.TEXT_PRIMARY);

		JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		controlsPanel.setBackground(EquipmentConstants.SECTION_BG);
		controlsPanel.setOpaque(true);

		header.add(titleLabel, BorderLayout.WEST);
		header.add(controlsPanel, BorderLayout.EAST);

		return header;
	}

	private JPanel createStatsPanel() {
		JPanel statsContainer = new JPanel(new BorderLayout());
		statsContainer.setBackground(EquipmentConstants.SECTION_BG);
		statsContainer.setOpaque(true);
		statsContainer.setBorder(new EmptyBorder(4, 4, 4, 4));

		JLabel statsTitle = new JLabel("Equipment Stats");
		statsTitle.setFont(new Font("Segoe UI", Font.BOLD, 10));
		statsTitle.setForeground(EquipmentConstants.TEXT_PRIMARY);

		statsLabel = new JLabel(statsRenderer.getLoadingMessage());
		statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		statsLabel.setForeground(EquipmentConstants.TEXT_SECONDARY);
		statsLabel.setVerticalAlignment(SwingConstants.TOP);

		statsContainer.add(statsTitle, BorderLayout.NORTH);
		statsContainer.add(statsLabel, BorderLayout.CENTER);

		return statsContainer;
	}

	@Override
	public void onEquipmentChanged() {
		updateEquipmentSlots();
		updateEquipmentStats();
	}

	@Override
	public void onEquipmentCleared() {
		clearEquipment();
	}

	public void updateData() {
		if (!dataManager.isEquipmentAvailable()) {
			clearEquipment();
			dataManager.stopAutoUpdate();
			equipmentMagebook.setControlsEnabled(false);
			return;
		}

		dataManager.startAutoUpdate();
		equipmentMagebook.updateControlsState();

		SwingUtilities.invokeLater(() -> {
			if (dataManager.hasEquipmentChanged()) {
				updateEquipmentSlots();
				updateEquipmentStats();
			}
		});
	}

	public void onPanelActivated() {
		dataManager.startAutoUpdate();
		equipmentMagebook.updateControlsState();
		updateData();
	}

	public void onPanelDeactivated() {
		dataManager.stopAutoUpdate();
	}

	private void clearEquipment() {
		SwingUtilities.invokeLater(() -> {
			gridLayout.clearAllSlots();
			statsLabel.setText(statsRenderer.getNoDataMessage());
			equipmentMagebook.setControlsEnabled(false);
		});
	}

	private void updateEquipmentSlots() {
		try {
			if (!dataManager.isEquipmentAvailable()) {
				clearEquipment();
				return;
			}

			RSInterface equipmentInterface = dataManager.getEquipmentInterface();
			if (equipmentInterface == null || equipmentInterface.inv == null) {
				clearEquipment();
				return;
			}

			boolean debugRingAmmo = false;
			if (debugRingAmmo) {
				System.out.println("=== Equipment Interface Debug ===");
				System.out.println("Interface 1688 inv length: " + equipmentInterface.inv.length);
				if (equipmentInterface.inv.length > 12) {
					System.out.println("Ring slot (index 12): " + equipmentInterface.inv[12]);
				}
				if (equipmentInterface.inv.length > 13) {
					System.out.println("Ammo slot (index 13): " + equipmentInterface.inv[13]);
				}
			}

			Map<Integer, EquipmentSlotPanel> slotPanels = gridLayout.getSlotPanels();
			for (Map.Entry<Integer, EquipmentSlotPanel> entry : slotPanels.entrySet()) {
				int slotId = entry.getKey();

				Integer equipmentIndex = EquipmentConstants.getEquipmentIndex(slotId);
				if (equipmentIndex != null && equipmentIndex < equipmentInterface.inv.length) {
					int itemId = equipmentInterface.inv[equipmentIndex] - 1;
					int amount = 1;

					if (equipmentInterface.invStackSizes != null && equipmentIndex < equipmentInterface.invStackSizes.length) {
						amount = equipmentInterface.invStackSizes[equipmentIndex];
					}

					if (debugRingAmmo && (slotId == 1654 || slotId == 1655)) {
						System.out.println("Slot " + slotId + " (index " + equipmentIndex + "): itemId=" + itemId + ", amount=" + amount);
					}

					gridLayout.updateSlot(slotId, itemId, amount);
				} else {
					gridLayout.updateSlot(slotId, -1, 0);
				}
			}
		} catch (Exception e) {
			System.err.println("Error updating equipment slots: " + e.getMessage());
			e.printStackTrace();
			clearEquipment();
		}
	}

	private void updateEquipmentStats() {
		String statsHtml = statsRenderer.generateStatsHtml();
		statsLabel.setText(statsHtml);
	}

	private void onBatchComplete() {
		if (Client.myStoner != null) {
			dataManager.forceUpdate();
		}
	}
}
