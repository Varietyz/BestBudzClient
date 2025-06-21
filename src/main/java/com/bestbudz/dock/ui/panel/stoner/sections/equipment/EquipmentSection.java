package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;

/**
 * Main equipment panel - now focused on coordination and UI layout
 */
public class EquipmentSection implements EquipmentDataManager.EquipmentUpdateListener {

	private JPanel panel;
	private JLabel statsLabel;

	// Separated concerns
	private final EquipmentDataManager dataManager;
	private final EquipmentStatsRenderer statsRenderer;
	private final EquipmentGridLayout gridLayout;
	private final EquipmentClickBatcher clickBatcher;
	private final EquipmentMagebook equipmentMagebook;

	public EquipmentSection() {
		// Initialize components with proper dependency injection
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
		panel.setPreferredSize(new Dimension(width - 16, 360)); // Increased height for buttons
		panel.setMaximumSize(new Dimension(width - 16, 360));

		createComponents(width);
		return panel;
	}

	private void createComponents(int width) {
		// Header with title and controls
		JPanel header = createHeader();
		panel.add(header, BorderLayout.NORTH);

		// Main content area
		JPanel mainContent = new JPanel(new BorderLayout());
		mainContent.setBackground(EquipmentConstants.SECTION_BG);
		mainContent.setOpaque(true);

		// Equipment grid (left side) - now managed by EquipmentGridLayout
		JPanel equipmentGrid = gridLayout.getGridPanel();

		// Stats panel (right side)
		JPanel statsPanel = createStatsPanel();

		// Split main content
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, equipmentGrid, statsPanel);
		splitPane.setDividerLocation(170);
		splitPane.setDividerSize(2);
		splitPane.setBorder(null);
		splitPane.setBackground(EquipmentConstants.SECTION_BG);
		splitPane.setOpaque(true);

		mainContent.add(splitPane, BorderLayout.CENTER);

		// Add magebook controls at the bottom
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

		// Control buttons
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

	// EquipmentDataManager.EquipmentUpdateListener implementation
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

			// Debug: Print equipment interface data for ring and ammo slots
			boolean debugRingAmmo = false; // Set to true for debugging
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

			// Update each slot using the grid layout manager
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

					// Debug specific slots
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
			e.printStackTrace(); // Add stack trace for debugging
			clearEquipment();
		}
	}

	private void updateEquipmentStats() {
		String statsHtml = statsRenderer.generateStatsHtml();
		statsLabel.setText(statsHtml);
	}

	// Callback for when click batch is complete
	private void onBatchComplete() {
		if (Client.myStoner != null) {
			dataManager.forceUpdate();
		}
	}
}