package com.bestbudz.client.ui.panel;

import com.bestbudz.config.Configuration;
import com.bestbudz.config.SettingHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SettingsPanel implements UIPanel {

	private final JPanel panel;
	private final List<JCheckBox> checkboxes = new ArrayList<>();
	private Timer saveTimer;

	public SettingsPanel() {
		panel = new JPanel(new BorderLayout(0, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel grid = new JPanel(new GridLayout(0, 2, 12, 6));
		grid.setAlignmentX(Component.CENTER_ALIGNMENT);

		addSetting(grid, "Tweening", Configuration.enableTweening);
		addSetting(grid, "Fog", Configuration.enableDistanceFog);
		addSetting(grid, "Mip Mapping", Configuration.enableMipMapping);
		addSetting(grid, "Moving Textures", Configuration.enableMovingTextures);
		addSetting(grid, "Status Orbs", Configuration.enableStatusOrbs);
		addSetting(grid, "Roofs", Configuration.enableRoofs);
		addSetting(grid, "Debit Card", Configuration.enablePouch);
		addSetting(grid, "Kill Feed", Configuration.showKillFeed);
		addSetting(grid, "Hover Menus", Configuration.menuHovers);
		addSetting(grid, "Entity Feed", Configuration.drawEntityFeed);
		addSetting(grid, "HP Bars", Configuration.enableNewHpBars);
		addSetting(grid, "Hitmarkers", Configuration.enableNewHitmarks);
		addSetting(grid, "x10 Damage", Configuration.enable10xDamage);
		addSetting(grid, "Attack Priority", Configuration.entityAttackPriority);
		addSetting(grid, "Time Stamps", Configuration.enableTimeStamps);

		JPanel gridWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
		gridWrapper.add(grid);
		panel.add(gridWrapper, BorderLayout.CENTER);

		JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panel.add(btnRow, BorderLayout.SOUTH);
	}

	private void addSetting(JPanel target, final String label, final boolean initialValue) {
		final JCheckBox box = new JCheckBox(label, initialValue);
		box.addActionListener(e -> toggleSetting(label, box.isSelected()));
		target.add(box);
		checkboxes.add(box);
	}

	private void toggleSetting(String label, boolean value) {
		if (label.equals("Tweening")) Configuration.enableTweening = value;
		else if (label.equals("Fog")) Configuration.enableDistanceFog = value;
		else if (label.equals("Mip Mapping")) Configuration.enableMipMapping = value;
		else if (label.equals("Moving Textures")) Configuration.enableMovingTextures = value;
		else if (label.equals("Status Orbs")) Configuration.enableStatusOrbs = value;
		else if (label.equals("Roofs")) Configuration.enableRoofs = value;
		else if (label.equals("Debit Card")) Configuration.enablePouch = value;
		else if (label.equals("Kill Feed")) Configuration.showKillFeed = value;
		else if (label.equals("Hover Menus")) Configuration.menuHovers = value;
		else if (label.equals("Entity Feed")) Configuration.drawEntityFeed = value;
		else if (label.equals("HP Bars")) Configuration.enableNewHpBars = value;
		else if (label.equals("Hitmarkers")) Configuration.enableNewHitmarks = value;
		else if (label.equals("x10 Damage")) Configuration.enable10xDamage = value;
		else if (label.equals("Attack Priority")) Configuration.entityAttackPriority = value;
		else if (label.equals("Time Stamps")) Configuration.enableTimeStamps = value;

		startAutoSaveTimer(); // 🆕 Trigger debounce save
	}


	private void refreshCheckboxes() {
		for (int i = 0; i < checkboxes.size(); i++) {
			JCheckBox box = checkboxes.get(i);
			String label = box.getText();
			if (label.equals("Tweening")) box.setSelected(Configuration.enableTweening);
			else if (label.equals("Fog")) box.setSelected(Configuration.enableDistanceFog);
			else if (label.equals("Mip Mapping")) box.setSelected(Configuration.enableMipMapping);
			else if (label.equals("Moving Textures")) box.setSelected(Configuration.enableMovingTextures);
			else if (label.equals("Status Orbs")) box.setSelected(Configuration.enableStatusOrbs);
			else if (label.equals("Roofs")) box.setSelected(Configuration.enableRoofs);
			else if (label.equals("Debit Card")) box.setSelected(Configuration.enablePouch);
			else if (label.equals("Kill Feed")) box.setSelected(Configuration.showKillFeed);
			else if (label.equals("Hover Menus")) box.setSelected(Configuration.menuHovers);
			else if (label.equals("Entity Feed")) box.setSelected(Configuration.drawEntityFeed);
			else if (label.equals("HP Bars")) box.setSelected(Configuration.enableNewHpBars);
			else if (label.equals("Hitmarkers")) box.setSelected(Configuration.enableNewHitmarks);
			else if (label.equals("x10 Damage")) box.setSelected(Configuration.enable10xDamage);
			else if (label.equals("Attack Priority")) box.setSelected(Configuration.entityAttackPriority);
			else if (label.equals("Time Stamps")) box.setSelected(Configuration.enableTimeStamps);
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
	public String getPanelID() {
		return "settings";
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
}
