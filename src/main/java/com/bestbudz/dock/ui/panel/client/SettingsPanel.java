package com.bestbudz.dock.ui.panel.client;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.ui.handling.SettingHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsPanel implements UIPanel
{

	private final JPanel panel;
	private final List<JCheckBox> checkboxes = new ArrayList<>();
	private Timer saveTimer;

	public SettingsPanel() {
		panel = new JPanel(new BorderLayout(0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setPreferredSize(null);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		panel.setMinimumSize(new Dimension(0, 0));

		JPanel grid = new JPanel(new GridLayout(0, 2, 12, 6));
		grid.setAlignmentX(Component.CENTER_ALIGNMENT);

		addSetting(grid, "Tweening", SettingsConfig.enableTweening);
		addSetting(grid, "Fog", SettingsConfig.enableDistanceFog);
		addSetting(grid, "Mip Mapping", SettingsConfig.enableMipMapping);
		addSetting(grid, "Moving Textures", SettingsConfig.enableMovingTextures);
		addSetting(grid, "Status Orbs", SettingsConfig.enableStatusOrbs);
		addSetting(grid, "Roofs", SettingsConfig.enableRoofs);
		addSetting(grid, "Debit Card", SettingsConfig.enablePouch);
		addSetting(grid, "Kill Feed", SettingsConfig.showKillFeed);
		addSetting(grid, "Hover Menus", SettingsConfig.menuHovers);
		addSetting(grid, "Entity Feed", SettingsConfig.drawEntityFeed);
		addSetting(grid, "HP Bars", SettingsConfig.enableNewHpBars);
		addSetting(grid, "Hitmarkers", SettingsConfig.enableNewHitmarks);
		addSetting(grid, "x10 Damage", SettingsConfig.enable10xDamage);
		addSetting(grid, "Attack Priority", SettingsConfig.entityAttackPriority);
		addSetting(grid, "Time Stamps", SettingsConfig.enableTimeStamps);

		JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		gridWrapper.add(grid);

		JScrollPane scrollPane = new JScrollPane(gridWrapper);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		panel.add(scrollPane, BorderLayout.CENTER);


		//JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		//panel.add(btnRow, BorderLayout.SOUTH);
	}

	private void addSetting(JPanel target, final String label, final boolean initialValue) {
		final JCheckBox box = new JCheckBox(label, initialValue);
		box.addActionListener(e -> toggleSetting(label, box.isSelected()));
		target.add(box);
		checkboxes.add(box);
	}

	private void toggleSetting(String label, boolean value) {
		if (label.equals("Tweening")) SettingsConfig.enableTweening = value;
		else if (label.equals("Fog")) SettingsConfig.enableDistanceFog = value;
		else if (label.equals("Mip Mapping")) SettingsConfig.enableMipMapping = value;
		else if (label.equals("Moving Textures")) SettingsConfig.enableMovingTextures = value;
		else if (label.equals("Status Orbs")) SettingsConfig.enableStatusOrbs = value;
		else if (label.equals("Roofs")) SettingsConfig.enableRoofs = value;
		else if (label.equals("Debit Card")) SettingsConfig.enablePouch = value;
		else if (label.equals("Kill Feed")) SettingsConfig.showKillFeed = value;
		else if (label.equals("Hover Menus")) SettingsConfig.menuHovers = value;
		else if (label.equals("Entity Feed")) SettingsConfig.drawEntityFeed = value;
		else if (label.equals("HP Bars")) SettingsConfig.enableNewHpBars = value;
		else if (label.equals("Hitmarkers")) SettingsConfig.enableNewHitmarks = value;
		else if (label.equals("x10 Damage")) SettingsConfig.enable10xDamage = value;
		else if (label.equals("Attack Priority")) SettingsConfig.entityAttackPriority = value;
		else if (label.equals("Time Stamps")) SettingsConfig.enableTimeStamps = value;

		startAutoSaveTimer(); // 🆕 Trigger debounce save
	}


	private void refreshCheckboxes() {
		for (int i = 0; i < checkboxes.size(); i++) {
			JCheckBox box = checkboxes.get(i);
			String label = box.getText();
			if (label.equals("Tweening")) box.setSelected(SettingsConfig.enableTweening);
			else if (label.equals("Fog")) box.setSelected(SettingsConfig.enableDistanceFog);
			else if (label.equals("Mip Mapping")) box.setSelected(SettingsConfig.enableMipMapping);
			else if (label.equals("Moving Textures")) box.setSelected(SettingsConfig.enableMovingTextures);
			else if (label.equals("Status Orbs")) box.setSelected(SettingsConfig.enableStatusOrbs);
			else if (label.equals("Roofs")) box.setSelected(SettingsConfig.enableRoofs);
			else if (label.equals("Debit Card")) box.setSelected(SettingsConfig.enablePouch);
			else if (label.equals("Kill Feed")) box.setSelected(SettingsConfig.showKillFeed);
			else if (label.equals("Hover Menus")) box.setSelected(SettingsConfig.menuHovers);
			else if (label.equals("Entity Feed")) box.setSelected(SettingsConfig.drawEntityFeed);
			else if (label.equals("HP Bars")) box.setSelected(SettingsConfig.enableNewHpBars);
			else if (label.equals("Hitmarkers")) box.setSelected(SettingsConfig.enableNewHitmarks);
			else if (label.equals("x10 Damage")) box.setSelected(SettingsConfig.enable10xDamage);
			else if (label.equals("Attack Priority")) box.setSelected(SettingsConfig.entityAttackPriority);
			else if (label.equals("Time Stamps")) box.setSelected(SettingsConfig.enableTimeStamps);
}
	}

	private void startAutoSaveTimer() {
		if (saveTimer != null) {
			saveTimer.stop();
		}

		saveTimer = new Timer(750, e -> {
			SettingHandler.save();
			saveTimer.stop(); // one-shot
		});
		saveTimer.setRepeats(false);
		saveTimer.start();
	}


	@Override
	public void updateText()
	{

	}

	@Override
	public String getPanelID() {
		return "Settings";
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void onActivate() {
		refreshCheckboxes();
	}

	@Override
	public void onDeactivate() {}

	@Override
	public void updateDockText(int index, String text)
	{

	}
}
