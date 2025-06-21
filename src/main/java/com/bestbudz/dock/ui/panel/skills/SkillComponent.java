package com.bestbudz.dock.ui.panel.skills;

import javax.swing.*;

public class SkillComponent {
	JPanel tile;
	JLabel iconLabel;
	JLabel gradeLabel;
	JLabel xpLabel;
	JButton overlayButton;
	JLabel advanceLabel; // Add reference to advancement label

	SkillComponent(JPanel tile, JLabel iconLabel, JLabel gradeLabel, JLabel xpLabel, JButton overlayButton, JLabel advanceLabel) {
		this.tile = tile;
		this.iconLabel = iconLabel;
		this.gradeLabel = gradeLabel;
		this.xpLabel = xpLabel;
		this.overlayButton = overlayButton;
		this.advanceLabel = advanceLabel;
	}
}