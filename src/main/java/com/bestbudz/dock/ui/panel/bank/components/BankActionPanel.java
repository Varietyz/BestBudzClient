package com.bestbudz.dock.ui.panel.bank.components;

import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.*;

public class BankActionPanel extends JPanel {

	private static final int DOCK_DEPOSIT_ALL_INVENTORY = 115247;
	private static final int DOCK_DEPOSIT_ALL_EQUIPMENT = 115248;

	private Runnable actionCallback;

	public BankActionPanel() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 6));
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);

		initializeButtons();
	}

	private void initializeButtons() {
		JButton depositBox = createButton("Deposit Box", this::depositInventory);
		JButton depositEquip = createButton("Deposit Equipment", this::depositEquipment);

		add(depositBox);
		add(depositEquip);
	}

	private JButton createButton(String text, Runnable action) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 10));

		Dimension buttonSize = new Dimension(120, 26);
		button.setPreferredSize(buttonSize);
		button.setMinimumSize(buttonSize);
		button.setMaximumSize(buttonSize);

		button.setBackground(ColorConfig.GRAPHITE_COLOR);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.addActionListener(e -> {
			if (Client.loggedIn) {
				action.run();

				if (actionCallback != null) {
					actionCallback.run();
				}
			}
		});
		return button;
	}

	private void depositInventory() {
		try {
			ButtonHandler.sendClick(DOCK_DEPOSIT_ALL_INVENTORY);
		} catch (Exception e) {
			System.err.println("Error depositing inventory: " + e.getMessage());
		}
	}

	private void depositEquipment() {
		try {
			ButtonHandler.sendClick(DOCK_DEPOSIT_ALL_EQUIPMENT);
		} catch (Exception e) {
			System.err.println("Error depositing equipment: " + e.getMessage());
		}
	}

	public void setActionCallback(Runnable callback) {
		this.actionCallback = callback;
	}
}
