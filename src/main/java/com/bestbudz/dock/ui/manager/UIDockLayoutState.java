package com.bestbudz.dock.ui.manager;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.dock.frame.UIDockHelper;
import com.bestbudz.dock.config.RegisteredPanels;

import com.bestbudz.ui.handling.SettingHandler;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class UIDockLayoutState {

	/**
	 * Load default panel layout using centralized configuration
	 */
	public static void load(UIDockFrame frame) {
		// Set up default panel layout every time using config
		setupDefaultLayout(frame);

		// Only restore divider position if it was previously set
		SwingUtilities.invokeLater(() -> {
			if (SettingsConfig.uiDockDividerRatio > 0f) {
				int total = frame.getSplitPane().getHeight();
				if (total > 0) {
					int loc = Math.round(SettingsConfig.uiDockDividerRatio * total);
					frame.getSplitPane().setDividerLocation(loc);
				}
			} else {
				// Default divider position (40% top, 60% bottom)
				frame.getSplitPane().setResizeWeight(0.4);
			}
		});

		frame.updateToggles();
		UIDockHelper.reflowToggleButtons();
	}

	/**
	 * Configure the default panel layout using centralized configuration
	 */
	private static void setupDefaultLayout(UIDockFrame frame) {
		// Clear any existing panel positions
		frame.getPanelPositions().clear();

		// Get layout configuration from RegisteredPanels
		String[] topPanels = RegisteredPanels.DefaultLayout.TOP_PANELS;
		String[] bottomPanels = RegisteredPanels.DefaultLayout.BOTTOM_PANELS;

		// Validate layout completeness if enabled
		RegisteredPanels.validateLayoutCompleteness();

		// Set up top panels
		for (String panelID : topPanels) {
			setupPanelInPosition(frame, panelID, "top");
		}

		// Set up bottom panels
		for (String panelID : bottomPanels) {
			setupPanelInPosition(frame, panelID, "bottom");
		}

		// Handle any panels that weren't explicitly configured
		handleUnconfiguredPanels(frame, topPanels, bottomPanels);

		// Show the default visible panels
		if (frame.getTopVisiblePanelID() != null) {
			UIDockHelper.switchPanel(frame, frame.getTopStack(), frame.getTopLayout(), frame.getTopVisiblePanelID());
		}

		if (frame.getBottomVisiblePanelID() != null &&
			!frame.getBottomVisiblePanelID().equals(frame.getTopVisiblePanelID())) {
			UIDockHelper.switchPanel(frame, frame.getBottomStack(), frame.getBottomLayout(), frame.getBottomVisiblePanelID());
		}
	}

	/**
	 * Setup a panel in the specified position
	 */
	private static void setupPanelInPosition(UIDockFrame frame, String panelID, String position) {
		frame.getPanelPositions().put(panelID, position);
		Component panel = frame.getPanelMap().get(panelID);

		if (panel != null) {
			if ("top".equals(position)) {
				// Remove from bottom if it's there
				frame.getBottomStack().remove(panel);
				// Add to top
				frame.getTopStack().add(panel, panelID);

				// Set first panel as visible
				if (frame.getTopVisiblePanelID() == null) {
					frame.setTopVisiblePanelID(panelID);
				}
			} else if ("bottom".equals(position)) {
				// Remove from top if it's there
				frame.getTopStack().remove(panel);
				// Add to bottom
				frame.getBottomStack().add(panel, panelID);

				// Set first panel as visible
				if (frame.getBottomVisiblePanelID() == null) {
					frame.setBottomVisiblePanelID(panelID);
				}
			}
		} else if (RegisteredPanels.PanelConfig.WARN_UNLISTED_PANELS) {
			System.out.println("Warning: Panel '" + panelID + "' is configured in layout but not found in panel map.");
		}
	}

	/**
	 * Handle panels that exist but weren't explicitly configured in the layout
	 */
	private static void handleUnconfiguredPanels(UIDockFrame frame, String[] topPanels, String[] bottomPanels) {
		// Get all panel IDs from the frame
		for (String panelID : frame.getPanelMap().keySet()) {
			// Skip if already configured
			if (frame.getPanelPositions().containsKey(panelID)) {
				continue;
			}

			// Check if it's in our configured arrays
			boolean inTopConfig = java.util.Arrays.asList(topPanels).contains(panelID);
			boolean inBottomConfig = java.util.Arrays.asList(bottomPanels).contains(panelID);

			if (!inTopConfig && !inBottomConfig) {
				// Panel exists but not configured - use default position
				String defaultPosition = RegisteredPanels.PanelConfig.DEFAULT_PANEL_POSITION;
				setupPanelInPosition(frame, panelID, defaultPosition);

				if (RegisteredPanels.PanelConfig.WARN_UNLISTED_PANELS) {
					System.out.println("Info: Panel '" + panelID + "' not in layout config, placed in " + defaultPosition + " dock.");
				}
			}
		}
	}

}