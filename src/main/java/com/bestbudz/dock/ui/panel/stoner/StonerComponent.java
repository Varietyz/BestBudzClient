package com.bestbudz.dock.ui.panel.stoner;

import com.bestbudz.entity.Stoner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StonerComponent extends JPanel {

	private static final Color CARD_BG = new Color(35, 35, 35);
	private static final Color ACCENT = new Color(185, 160, 66);
	private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
	private static final Color TEXT_SECONDARY = new Color(170, 170, 170);
	private static final Color BORDER = new Color(60, 60, 60);
	private static final Color COMBAT_ACTIVE = new Color(220, 53, 69);
	private static final Color COMBAT_IDLE = new Color(40, 167, 69);

	private final Stoner stoner;
	private JLabel statusLabel;
	private JLabel healthLabel;
	private JLabel locationLabel;
	private JPanel statusIndicator;

	public StonerComponent(Stoner stoner, boolean isLocalPlayer, int panelWidth) {
		this.stoner = stoner;

		setupLayout(panelWidth);
		createComponents();
		updateData();
	}

	private void setupLayout(int panelWidth) {
		setLayout(new BorderLayout());
		setBackground(CARD_BG);
		setOpaque(true);
		setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER, 1),
			new EmptyBorder(12, 12, 12, 12)
		));
		setPreferredSize(new Dimension(panelWidth - 16, 80));
	}

	private void createComponents() {

		JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		topRow.setOpaque(false);

		statusIndicator = new JPanel();
		statusIndicator.setPreferredSize(new Dimension(12, 12));
		statusIndicator.setOpaque(true);

		JLabel nameLabel = new JLabel("You");
		nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
		nameLabel.setForeground(ACCENT.brighter());
		nameLabel.setOpaque(false);

		topRow.add(statusIndicator);
		topRow.add(Box.createHorizontalStrut(8));
		topRow.add(nameLabel);

		JPanel infoPanel = new JPanel(new GridLayout(3, 1, 0, 4));
		infoPanel.setOpaque(false);
		infoPanel.setBorder(new EmptyBorder(8, 0, 0, 0));

		statusLabel = createInfoLabel();
		healthLabel = createInfoLabel();
		locationLabel = createInfoLabel();

		infoPanel.add(statusLabel);
		infoPanel.add(healthLabel);
		infoPanel.add(locationLabel);

		add(topRow, BorderLayout.NORTH);
		add(infoPanel, BorderLayout.CENTER);
	}

	private JLabel createInfoLabel() {
		JLabel label = new JLabel();
		label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		label.setForeground(TEXT_SECONDARY);
		label.setOpaque(false);
		return label;
	}

	private void updateData() {
		if (stoner == null) {
			showNoData();
			return;
		}

		updateStatus();
		updateHealth();
		updateLocation();
		updateStatusIndicator();
	}

	private void updateStatus() {
		if (stoner.currentHealth > 0) {
			if (stoner.interactingEntity != -1) {
				statusLabel.setText("Status: Combat");
			} else {
				statusLabel.setText("Status: Idle");
			}
		} else {
			statusLabel.setText("Status: Unknown");
		}
	}

	private void updateHealth() {
		healthLabel.setText("Health: " + stoner.currentHealth + "/" + stoner.maxHealth);
	}

	private void updateLocation() {
		if (stoner.x > 0 && stoner.y > 0) {
			int regionX = (stoner.x >> 7);
			int regionY = (stoner.y >> 7);
			locationLabel.setText("Location: (" + regionX + "," + regionY + ")");
		} else {
			locationLabel.setText("Location: Unknown");
		}
	}

	private void updateStatusIndicator() {
		Color indicatorColor;
		if (stoner == null || stoner.currentHealth <= 0) {
			indicatorColor = Color.GRAY;
		} else if (stoner.interactingEntity != -1) {
			indicatorColor = COMBAT_ACTIVE;
		} else {
			indicatorColor = COMBAT_IDLE;
		}
		statusIndicator.setBackground(indicatorColor);
	}

	private void showNoData() {
		statusLabel.setText("Status: No data");
		healthLabel.setText("Health: --/--");
		locationLabel.setText("Location: --");
		statusIndicator.setBackground(Color.GRAY);
	}

	public void updateLayout(int panelWidth) {
		setPreferredSize(new Dimension(panelWidth - 16, 80));
	}

	public void refresh() {
		updateData();
		repaint();
	}
}
