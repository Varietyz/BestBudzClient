package com.bestbudz.client.ui.panel;

import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.client.ui.panel.UIPanel;
import com.bestbudz.engine.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * EquipmentPanel replicates the RSInterface-based equipment tab as a Swing panel.
 * All layout is constrained to 300px max width. No horizontal scrolling required.
 */
public class EquipmentPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private final JLabel[] bonusLabels = new JLabel[13];
	private final JLabel[] rightStats = new JLabel[3];

	public EquipmentPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(8, 8, 8, 8));
		setPreferredSize(null);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(0, 0));

		// Title Bar
		JLabel title = new JLabel("Equipment Bonuses");
		title.setFont(new Font("Dialog", Font.BOLD, 14));
		title.setForeground(new Color(0xFF981F));

		JPanel titleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		titleBar.setOpaque(false);
		titleBar.add(title);
		add(titleBar, BorderLayout.NORTH);

		// Stats (Left Side, vertically stacked)
		JPanel statSection = new JPanel();
		statSection.setLayout(new BoxLayout(statSection, BoxLayout.Y_AXIS));
		statSection.setOpaque(false);

		String[] sectionTitles = { "Assault bonus", "Aegis bonus", "Other bonuses" };
		String[][] groups = {
			{ "Attack", "Strength", "Ranged", "Magic", "Special" },
			{ "Defence", "Absorb Melee", "Absorb Ranged", "Absorb Magic" },
			{ "Weight", "Prayer" }
		};

		int idx = 0;
		for (int s = 0; s < sectionTitles.length; s++) {
			JLabel header = new JLabel(sectionTitles[s]);
			header.setFont(new Font("Dialog", Font.BOLD, 12));
			header.setForeground(new Color(0xFF981F));
			header.setAlignmentX(Component.LEFT_ALIGNMENT);
			statSection.add(header);
			statSection.add(Box.createVerticalStrut(2));

			for (String stat : groups[s]) {
				JLabel line = new JLabel(stat + ": +0");
				line.setForeground(new Color(0xFF9200));
				line.setFont(new Font("Dialog", Font.PLAIN, 11));
				line.setAlignmentX(Component.LEFT_ALIGNMENT);
				statSection.add(line);
				bonusLabels[idx++] = line;
			}

			statSection.add(Box.createVerticalStrut(4));
		}

		add(statSection, BorderLayout.CENTER);

		// Right side (Character preview + summary stats)
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setOpaque(false);
		rightPanel.setPreferredSize(new Dimension(110, 180));

		JLabel charPreview = new JLabel("Character Preview", SwingConstants.CENTER);
		charPreview.setPreferredSize(new Dimension(100, 150));
		charPreview.setAlignmentX(Component.CENTER_ALIGNMENT);

		rightPanel.add(charPreview);
		rightPanel.add(Box.createVerticalStrut(6));

		for (int i = 0; i < rightStats.length; i++) {
			rightStats[i] = new JLabel("Stat +0");
			rightStats[i].setForeground(new Color(0xFF9200));
			rightStats[i].setFont(new Font("Dialog", Font.PLAIN, 11));
			rightStats[i].setAlignmentX(Component.CENTER_ALIGNMENT);
			rightPanel.add(rightStats[i]);
		}

		add(rightPanel, BorderLayout.EAST);

		// Footer buttons
		JPanel buttonRow = new JPanel();
		buttonRow.setLayout(new GridLayout(2, 2, 5, 5));
		buttonRow.setOpaque(false);
		buttonRow.setMaximumSize(new Dimension(284, 50));

		buttonRow.add(makeTextButton("Show Equipment"));
		buttonRow.add(makeTextButton("Items Kept"));
		buttonRow.add(makeTextButton("Price Checker"));
		buttonRow.add(makeTextButton("XP Toggle"));

		JPanel southWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		southWrapper.setOpaque(false);
		southWrapper.add(buttonRow);

		add(southWrapper, BorderLayout.SOUTH);
	}

	private JButton makeTextButton(String label) {
		JButton btn = new JButton(label);
		btn.setFont(new Font("Dialog", Font.PLAIN, 11));
		btn.setFocusPainted(false);
		btn.setContentAreaFilled(false);
		btn.setBorderPainted(true);
		btn.setForeground(Color.WHITE);
		btn.setPreferredSize(new Dimension(130, 22));
		return btn;
	}

	@Override
	public void updateText()
	{

	}

	@Override
	public String getPanelID() {
		return "Equipment";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (!Client.loggedIn) return;

		String[] updated = {
			"+12", "+8", "+14", "+10", "+5",  // Assault
			"+6", "+1%", "+2%", "+3%",        // Aegis
			"0kg", "+9"                       // Other
		};

		for (int i = 0; i < updated.length && i < bonusLabels.length; i++) {
			String base = bonusLabels[i].getText().split(":")[0];
			bonusLabels[i].setText(base + ": " + updated[i]);
		}

		rightStats[0].setText("Melee DPS: 103");
		rightStats[1].setText("Ranged DPS: 87");
		rightStats[2].setText("Magic DPS: 99");
	}

	@Override
	public void onDeactivate() {
		// Optional cleanup
	}

	@Override
	public void updateDockText(int index, String text) {
		if (!Client.loggedIn) return;
		if (index >= 0 && index < bonusLabels.length) {
			String label = bonusLabels[index].getText().split(":")[0];
			bonusLabels[index].setText(label + ": " + text);
		}
	}
}
