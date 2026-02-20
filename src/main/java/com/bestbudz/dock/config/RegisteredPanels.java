package com.bestbudz.dock.config;

import com.bestbudz.dock.ui.panel.bank.BankPanel;
import com.bestbudz.dock.ui.panel.character.AppearancePanel;
import com.bestbudz.dock.ui.panel.client.SettingsPanel;
import com.bestbudz.dock.ui.panel.debug.DiagnosticPanel;
import com.bestbudz.dock.ui.panel.emote.EmotePanel;
import com.bestbudz.dock.ui.panel.game.AchievementsPanel;
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

	public static final List<Supplier<UIPanel>> PANEL_SUPPLIERS = Arrays.asList(
		SettingsPanel::new,

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

	public static class DefaultLayout {

		public static final String[] TOP_PANELS = {
			"Skills",
			"Bank",
			"Teleports",
			"Emotes",
			"Shops",
			"Stoners"
		};

		public static final String[] BOTTOM_PANELS = {
			"Chat",
			"My Info",
			"Appearance",
			"Settings",
			"Achievements",

			"Debug"

		};

		public static String[] getAllPanelNames() {
			String[] allPanels = new String[TOP_PANELS.length + BOTTOM_PANELS.length];
			System.arraycopy(TOP_PANELS, 0, allPanels, 0, TOP_PANELS.length);
			System.arraycopy(BOTTOM_PANELS, 0, allPanels, TOP_PANELS.length, BOTTOM_PANELS.length);
			return allPanels;
		}

		public static boolean isTopPanel(String panelName) {
			return Arrays.asList(TOP_PANELS).contains(panelName);
		}

		public static boolean isBottomPanel(String panelName) {
			return Arrays.asList(BOTTOM_PANELS).contains(panelName);
		}

		public static String getDefaultPosition(String panelName) {
			if (isTopPanel(panelName)) {
				return "top";
			} else if (isBottomPanel(panelName)) {
				return "bottom";
			} else {
				return "top";
			}
		}
	}

	public static class PanelConfig {

		public static final boolean VALIDATE_LAYOUT_COMPLETENESS = true;

		public static final boolean WARN_UNLISTED_PANELS = true;

		public static final String DEFAULT_PANEL_POSITION = "top";
	}

	public static UIPanel[] createAllPanels() {
		return PANEL_SUPPLIERS.stream()
			.map(Supplier::get)
			.toArray(UIPanel[]::new);
	}

	public static int getPanelCount() {
		return PANEL_SUPPLIERS.size();
	}

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
