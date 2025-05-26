package com.bestbudz.client.ui.panel;


import javax.swing.*;
import java.awt.*;

public class InventoryPanel implements UIPanel {

	private final JPanel panel;

	public InventoryPanel() {

		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel label = new JLabel("Inventory goes here...");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(label, BorderLayout.CENTER);
	}

	@Override
	public String getPanelID() {
		return "inventory";
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void onActivate() {
		System.out.println("Inventory panel activated.");
	}

	@Override
	public void onDeactivate() {
		System.out.println("Inventory panel deactivated.");
	}
}
