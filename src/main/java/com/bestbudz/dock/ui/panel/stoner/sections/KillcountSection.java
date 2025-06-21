package com.bestbudz.dock.ui.panel.stoner.sections;

import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.*;

public class KillcountSection
{

	private static final Color SECTION_BG = new Color(35, 35, 35);
	private static final Color BORDER = new Color(60, 60, 60);
	private static final Color TEXT_PRIMARY = new Color(255, 255, 255);

	private JPanel panel;
	private JLabel contentLabel;

	public JPanel createPanel(int width) {
		panel = new JPanel(new BorderLayout());
		panel.setBackground(SECTION_BG);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER, 1),
			BorderFactory.createEmptyBorder(12, 12, 12, 12)
		));
		panel.setPreferredSize(new Dimension(width - 16, 80));
		panel.setMaximumSize(new Dimension(width - 16, 80));

		JLabel titleLabel = new JLabel("Kill Count");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
		titleLabel.setForeground(TEXT_PRIMARY);

		contentLabel = new JLabel("Loading...");
		contentLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		contentLabel.setForeground(new Color(170, 170, 170));

		panel.add(titleLabel, BorderLayout.NORTH);
		panel.add(contentLabel, BorderLayout.CENTER);

		return panel;
	}

	public void updateData() {
		if (Client.myStoner == null) {
			contentLabel.setText("No data available");
			return;
		}
		contentLabel.setText("Kill count data coming soon!");
	}
}