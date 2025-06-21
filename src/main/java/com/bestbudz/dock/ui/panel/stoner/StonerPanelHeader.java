package com.bestbudz.dock.ui.panel.stoner;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Header component for the Stoner Panel
 * Contains title and control buttons
 */
public class StonerPanelHeader extends JPanel {

	private static final Color HEADER_BG = new Color(45, 45, 45);
	private static final Color TEXT_PRIMARY = new Color(255, 255, 255);

	private JLabel titleLabel;
	private StonerControlsManager controlsManager;

	public StonerPanelHeader(StonerControlsManager controlsManager) {
		this.controlsManager = controlsManager;
		setLayout(new BorderLayout());
		setBackground(HEADER_BG);
		setOpaque(true);
		setBorder(new EmptyBorder(8, 12, 8, 12));

		createComponents();
	}

	private void createComponents() {
		// Title
		titleLabel = new JLabel("My Stoner");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(TEXT_PRIMARY);
		titleLabel.setOpaque(false);

		// Controls
		JPanel controlsPanel = controlsManager.getControlsPanel();

		add(titleLabel, BorderLayout.WEST);
		add(controlsPanel, BorderLayout.EAST);
	}

	public void updateStats(int stonerCount) {
		// Optional: Could show online/offline status instead of count
		SwingUtilities.invokeLater(() -> {
			if (stonerCount > 0) {
				titleLabel.setText("My Stoner");
			} else {
				titleLabel.setText("My Stoner (Offline)");
			}
		});
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
	}
}