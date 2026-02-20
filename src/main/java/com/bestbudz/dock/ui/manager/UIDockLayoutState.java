package com.bestbudz.dock.ui.manager;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.frame.UIDockHelper;
import com.bestbudz.dock.config.RegisteredPanels;

import javax.swing.*;
import java.awt.*;

public class UIDockLayoutState {

	public static void load(UIDockFrame frame) {

		setupDefaultLayout(frame);

		SwingUtilities.invokeLater(() -> {
			if (SettingsConfig.uiDockDividerRatio > 0f) {
				int total = frame.getSplitPane().getHeight();
				if (total > 0) {
					int loc = Math.round(SettingsConfig.uiDockDividerRatio * total);
					frame.getSplitPane().setDividerLocation(loc);
				}
			} else {

				frame.getSplitPane().setResizeWeight(0.4);
			}
		});

		frame.updateToggles();
		UIDockHelper.reflowToggleButtons();
	}

	private static void setupDefaultLayout(UIDockFrame frame) {

		frame.getPanelPositions().clear();

		String[] topPanels = RegisteredPanels.DefaultLayout.TOP_PANELS;
		String[] bottomPanels = RegisteredPanels.DefaultLayout.BOTTOM_PANELS;

		RegisteredPanels.validateLayoutCompleteness();

		for (String panelID : topPanels) {
			setupPanelInPosition(frame, panelID, "top");
		}

		for (String panelID : bottomPanels) {
			setupPanelInPosition(frame, panelID, "bottom");
		}

		handleUnconfiguredPanels(frame, topPanels, bottomPanels);

		if (frame.getTopVisiblePanelID() != null) {
			UIDockHelper.switchPanel(frame, frame.getTopStack(), frame.getTopLayout(), frame.getTopVisiblePanelID());
		}

		if (frame.getBottomVisiblePanelID() != null &&
			!frame.getBottomVisiblePanelID().equals(frame.getTopVisiblePanelID())) {
			UIDockHelper.switchPanel(frame, frame.getBottomStack(), frame.getBottomLayout(), frame.getBottomVisiblePanelID());
		}
	}

	private static void setupPanelInPosition(UIDockFrame frame, String panelID, String position) {
		frame.getPanelPositions().put(panelID, position);
		Component panel = frame.getPanelMap().get(panelID);

		if (panel != null) {
			if ("top".equals(position)) {

				frame.getBottomStack().remove(panel);

				frame.getTopStack().add(panel, panelID);

				if (frame.getTopVisiblePanelID() == null) {
					frame.setTopVisiblePanelID(panelID);
				}
			} else if ("bottom".equals(position)) {

				frame.getTopStack().remove(panel);

				frame.getBottomStack().add(panel, panelID);

				if (frame.getBottomVisiblePanelID() == null) {
					frame.setBottomVisiblePanelID(panelID);
				}
			}
		} else if (RegisteredPanels.PanelConfig.WARN_UNLISTED_PANELS) {
			System.out.println("Warning: Panel '" + panelID + "' is configured in layout but not found in panel map.");
		}
	}

	private static void handleUnconfiguredPanels(UIDockFrame frame, String[] topPanels, String[] bottomPanels) {

		for (String panelID : frame.getPanelMap().keySet()) {

			if (frame.getPanelPositions().containsKey(panelID)) {
				continue;
			}

			boolean inTopConfig = java.util.Arrays.asList(topPanels).contains(panelID);
			boolean inBottomConfig = java.util.Arrays.asList(bottomPanels).contains(panelID);

			if (!inTopConfig && !inBottomConfig) {

				String defaultPosition = RegisteredPanels.PanelConfig.DEFAULT_PANEL_POSITION;
				setupPanelInPosition(frame, panelID, defaultPosition);

				if (RegisteredPanels.PanelConfig.WARN_UNLISTED_PANELS) {
					System.out.println("Info: Panel '" + panelID + "' not in layout config, placed in " + defaultPosition + " dock.");
				}
			}
		}
	}

}
