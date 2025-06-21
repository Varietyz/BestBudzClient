package com.bestbudz.dock.config;

import com.bestbudz.dock.ui.panel.bank.BankPanel;
import com.bestbudz.dock.ui.panel.character.AppearancePanel;
import com.bestbudz.dock.ui.panel.client.SettingsPanel;
import com.bestbudz.dock.ui.panel.debug.DiagnosticPanel;
import com.bestbudz.dock.ui.panel.emote.EmotePanel;
import com.bestbudz.dock.ui.panel.game.AchievementsPanel;
import com.bestbudz.dock.ui.panel.game.InfoTabPanel;
import com.bestbudz.dock.ui.panel.skills.SkillsPanel;
import com.bestbudz.dock.ui.panel.shops.ShopPanel;
import com.bestbudz.dock.ui.panel.social.ChatPanel;
import com.bestbudz.dock.ui.panel.social.stoners.StonersPanel;
import com.bestbudz.dock.ui.panel.stoner.StonerPanel;
import com.bestbudz.dock.ui.panel.teleports.TeleportPanel;
import com.bestbudz.dock.util.UIPanel;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class RegisteredPanels {

	/**
	 * List of all panels to register in the dock system.
	 * Add new panels here along with their constructor supplier.
	 */
	public static final List<Supplier<UIPanel>> PANEL_SUPPLIERS = Arrays.asList(
		SettingsPanel::new,
		//InfoTabPanel::new,
		AchievementsPanel::new,
		ChatPanel::new,
		StonersPanel::new,
		SkillsPanel::new,
		TeleportPanel::new,
		ShopPanel::new,
		AppearancePanel::new,
		DiagnosticPanel::new,
		EmotePanel::new,
		BankPanel::new,
		StonerPanel::new
	);

	/**
	 * Default layout configuration for dock panels.
	 * Panels are organized by their intended primary use case.
	 */
	public static class DefaultLayout {

		/**
		 * Top panels - Utility, reference, and informational panels
		 * These are typically used for viewing information or settings
		 */
		public static final String[] TOP_PANELS = {
			"Skills",
			"Bank",
			"Teleports",
			"Emotes",
			"Shops",
			"Stoners"
		};

		/**
		 * Bottom panels - Interactive and action-oriented panels
		 * These are typically used for performing actions or communication
		 */
		public static final String[] BOTTOM_PANELS = {
			"Chat",
			"My Info",
			"Appearance",
			"Settings",
			"Achievements",
			//"Info Tab",
			"Debug"

		};

		/**
		 * Get all configured panel names for validation
		 */
		public static String[] getAllPanelNames() {
			String[] allPanels = new String[TOP_PANELS.length + BOTTOM_PANELS.length];
			System.arraycopy(TOP_PANELS, 0, allPanels, 0, TOP_PANELS.length);
			System.arraycopy(BOTTOM_PANELS, 0, allPanels, TOP_PANELS.length, BOTTOM_PANELS.length);
			return allPanels;
		}

		/**
		 * Check if a panel is configured for the top dock
		 */
		public static boolean isTopPanel(String panelName) {
			return Arrays.asList(TOP_PANELS).contains(panelName);
		}

		/**
		 * Check if a panel is configured for the bottom dock
		 */
		public static boolean isBottomPanel(String panelName) {
			return Arrays.asList(BOTTOM_PANELS).contains(panelName);
		}

		/**
		 * Get the default position for a panel
		 */
		public static String getDefaultPosition(String panelName) {
			if (isTopPanel(panelName)) {
				return "top";
			} else if (isBottomPanel(panelName)) {
				return "bottom";
			} else {
				return "top"; // Default fallback
			}
		}
	}

	/**
	 * Configuration options for panel behavior
	 */
	public static class PanelConfig {

		/**
		 * Whether to validate that all registered panels are included in the layout
		 */
		public static final boolean VALIDATE_LAYOUT_COMPLETENESS = true;

		/**
		 * Whether to show warnings for panels not included in the default layout
		 */
		public static final boolean WARN_UNLISTED_PANELS = true;

		/**
		 * Default position for panels not explicitly configured in the layout
		 */
		public static final String DEFAULT_PANEL_POSITION = "top";
	}

	/**
	 * Create all configured panels
	 */
	public static UIPanel[] createAllPanels() {
		return PANEL_SUPPLIERS.stream()
			.map(Supplier::get)
			.toArray(UIPanel[]::new);
	}

	/**
	 * Get the number of registered panels
	 */
	public static int getPanelCount() {
		return PANEL_SUPPLIERS.size();
	}

	/**
	 * Validate that the layout configuration includes all registered panels
	 */
	public static void validateLayoutCompleteness() {
		if (!PanelConfig.VALIDATE_LAYOUT_COMPLETENESS) {
			return;
		}

		UIPanel[] panels = createAllPanels();
		String[] configuredPanels = DefaultLayout.getAllPanelNames();

		for (UIPanel panel : panels) {
			String panelId = panel.getPanelID();
			boolean found = Arrays.asList(configuredPanels).contains(panelId);

			if (!found && PanelConfig.WARN_UNLISTED_PANELS) {
				System.out.println("Warning: Panel '" + panelId + "' is registered but not included in default layout. " +
					"Will be placed in " + PanelConfig.DEFAULT_PANEL_POSITION + " dock.");
			}
		}
	}
}