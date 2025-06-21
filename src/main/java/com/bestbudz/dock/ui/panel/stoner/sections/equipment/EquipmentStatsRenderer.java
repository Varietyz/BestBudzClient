package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.ui.RSInterface;

/**
 * Handles rendering and formatting of equipment stats
 */
public class EquipmentStatsRenderer {

	public String generateStatsHtml() {
		try {
			StringBuilder stats = new StringBuilder("<html>");

			// Attack bonuses
			stats.append("<b>Assault Bonuses:</b><br/>");
			for (int statId : EquipmentConstants.ASSAULT_STAT_IDS) {
				addStatLine(stats, "", statId);
			}

			// Defense bonuses
			stats.append("<br/><b>Aegis Bonuses:</b><br/>");
			for (int statId : EquipmentConstants.AEGIS_STAT_IDS) {
				addStatLine(stats, "", statId);
			}

			// Other bonuses
			stats.append("<br/><b>Extra Bonuses:</b><br/>");
			for (int statId : EquipmentConstants.EXTRA_STAT_IDS) {
				addStatLine(stats, "", statId);
			}

			stats.append("</html>");
			return stats.toString();
		} catch (Exception e) {
			return "<html>Error loading stats</html>";
		}
	}

	public String getNoDataMessage() {
		return "<html>No equipment data available</html>";
	}

	public String getLoadingMessage() {
		return "<html>Loading stats...</html>";
	}

	private void addStatLine(StringBuilder stats, String label, int interfaceId) {
		try {
			RSInterface statInterface = RSInterface.interfaceCache[interfaceId];
			if (statInterface != null && statInterface.disabledMessage != null) {
				String value = statInterface.disabledMessage;
				stats.append(label).append(value).append("<br/>");
			}
		} catch (Exception e) {
			// Skip this stat if error
		}
	}
}