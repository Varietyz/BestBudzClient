
package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.engine.core.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Handles magebook controls and spellbook functionality
 */
public class EquipmentMagebook
{

	// Magebook interface IDs
	private static final int FOCUSED_MAGEBOOK_ID = 115074;
	private static final int AOE_MAGEBOOK_ID = 115075;

	private JPanel controlsPanel;

	public EquipmentMagebook() {
		createControlsPanel();
	}

	private void createControlsPanel() {
		controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
		controlsPanel.setBackground(EquipmentConstants.SECTION_BG);
		controlsPanel.setOpaque(true);
		controlsPanel.setBorder(new EmptyBorder(4, 0, 0, 0));

		// Focused Magebook button
		JButton focusedButton = createMagebookButton("Focused", FOCUSED_MAGEBOOK_ID);
		controlsPanel.add(focusedButton);

		// AoE Magebook button
		JButton aoeButton = createMagebookButton("AoE", AOE_MAGEBOOK_ID);
		controlsPanel.add(aoeButton);
	}

	private JButton createMagebookButton(String text, int interfaceId) {
		JButton button = new JButton(text + " Magebook");

		// Style the button to match the design
		button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		button.setForeground(EquipmentConstants.TEXT_PRIMARY);
		button.setBackground(EquipmentConstants.SLOT_BG);
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(EquipmentConstants.BORDER, 1),
			BorderFactory.createEmptyBorder(4, 8, 4, 8)
		));
		button.setFocusPainted(false);
		button.setOpaque(true);

		// Hover effects
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				button.setBackground(EquipmentConstants.SLOT_HOVER);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				button.setBackground(EquipmentConstants.SLOT_BG);
			}
		});

		// Click action using proper ButtonHandler
		button.addActionListener(e -> {
			SwingUtilities.invokeLater(() -> {
				try {
					ButtonHandler.createButtonListener(interfaceId).actionPerformed(e);
					System.out.println("Casting " + text + " magebook with interface ID: " + interfaceId);
				} catch (Exception ex) {
					System.err.println("Error handling " + text + " magebook button click: " + ex.getMessage());
				}
			});
		});

		return button;
	}

	/**
	 * Gets the controls panel containing the magebook buttons
	 * @return JPanel containing the magebook controls
	 */
	public JPanel getControlsPanel() {
		return controlsPanel;
	}

	/**
	 * Enables or disables all magebook controls
	 * @param enabled true to enable controls, false to disable
	 */
	public void setControlsEnabled(boolean enabled) {
		SwingUtilities.invokeLater(() -> {
			Component[] components = controlsPanel.getComponents();
			for (Component component : components) {
				if (component instanceof JButton) {
					component.setEnabled(enabled);
				}
			}
		});
	}

	/**
	 * Updates the visual state of the controls based on client state
	 */
	public void updateControlsState() {
		boolean isLoggedIn = Client.loggedIn && Client.myStoner != null;
		setControlsEnabled(isLoggedIn);
	}
}