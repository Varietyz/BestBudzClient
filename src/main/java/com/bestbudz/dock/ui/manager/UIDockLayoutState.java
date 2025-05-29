package com.bestbudz.dock.ui.manager;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.frame.UIDockHelper;

import com.bestbudz.ui.handling.SettingHandler;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class UIDockLayoutState {

	public static void load(UIDockFrame frame) {
		String layout = SettingsConfig.uiDockPanels;

		boolean firstBoot = (layout == null || layout.trim().isEmpty());

		// ✅ On first boot, inject and save default layout
		if (firstBoot) {
			layout = "Settings=top,Info Tab=bottom";
			SettingsConfig.uiDockPanels = layout;
			SettingHandler.save(); // only save once
		}

		if (!layout.isEmpty()) {
			String[] entries = layout.split(",");
			for (String entry : entries) {
				String[] parts = entry.split("=");
				if (parts.length != 2) continue;

				String panelID = parts[0].trim();
				String position = parts[1].trim();

				frame.getPanelPositions().put(panelID, position);
				Component panel = frame.getPanelMap().get(panelID);
				if (panel == null) continue;

				if ("top".equalsIgnoreCase(position)) {
					if (!frame.getTopStack().isAncestorOf(panel)) {
						frame.getBottomStack().remove(panel);
						frame.getTopStack().add(panel, panelID);
					}
					if (frame.getTopVisiblePanelID() == null)
						frame.setTopVisiblePanelID(panelID);
				} else if ("bottom".equalsIgnoreCase(position)) {
					if (!frame.getBottomStack().isAncestorOf(panel)) {
						frame.getTopStack().remove(panel);
						frame.getBottomStack().add(panel, panelID);
					}
					if (frame.getBottomVisiblePanelID() == null)
						frame.setBottomVisiblePanelID(panelID);
				}
			}
		}

		if (frame.getTopVisiblePanelID() != null)
			UIDockHelper.switchPanel(frame, frame.getTopStack(), frame.getTopLayout(), frame.getTopVisiblePanelID());

		if (frame.getBottomVisiblePanelID() != null &&
			!frame.getBottomVisiblePanelID().equals(frame.getTopVisiblePanelID()))
			UIDockHelper.switchPanel(frame, frame.getBottomStack(), frame.getBottomLayout(), frame.getBottomVisiblePanelID());

		// Restore divider position
		SwingUtilities.invokeLater(() -> {
			if (SettingsConfig.uiDockDividerRatio > 0f) {
				int total = frame.getSplitPane().getHeight();
				if (total > 0) {
					int loc = Math.round(SettingsConfig.uiDockDividerRatio * total);
					frame.getSplitPane().setDividerLocation(loc);
				}
			}
		});

		frame.updateToggles();
		UIDockHelper.reflowToggleButtons();
	}


	public static void save(UIDockFrame frame) {
		// Save panel positions
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : frame.getPanelPositions().entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
		}
		if (sb.length() > 0) sb.setLength(sb.length() - 1);
		SettingsConfig.uiDockPanels = sb.toString();

		// Save divider location
		if (frame.getSplitPane().getHeight() > 100) {
			int fullHeight = frame.getSplitPane().getHeight();
			int location = frame.getSplitPane().getDividerLocation();
			SettingsConfig.uiDockDividerRatio = Math.max(0f, Math.min(1f, location / (float) fullHeight));
		}

		// Save last active panel
		SettingsConfig.uiDockLastActive = frame.getLastActivePanelID() != null ? frame.getLastActivePanelID() : "";
		UIDockHelper.reflowToggleButtons();
	}
}
