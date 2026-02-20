package com.bestbudz.dock.ui.panel.bank.util;

import com.bestbudz.dock.definitions.ItemBonusManager;

/**
 * Handles tooltip construction for bank items
 */
public class BankTooltipBuilder {

	/**
	 * Builds a complete tooltip for a bank item
	 */
	public static String buildTooltip(int itemId, int amount, boolean isPlaceholder,
									  boolean isInventoryItem, boolean isAlsoInBank) {
		if (isPlaceholder) {
			return buildPlaceholderTooltip(itemId);
		}

		return buildItemTooltip(itemId, amount, isInventoryItem, isAlsoInBank);
	}

	private static String buildPlaceholderTooltip(int itemId) {
		return "<html><b>Placeholder Item</b><br/>Item ID: " + itemId +
			"<br/>This item is only in inventory</html>";
	}

	private static String buildItemTooltip(int itemId, int amount, boolean isInventoryItem, boolean isAlsoInBank) {
		StringBuilder tooltip = new StringBuilder("<html>");
		ItemDefCache.CachedItemDef itemDef = ItemDefCache.get(itemId);

		tooltip.append("<b>").append(itemDef.name).append("</b><br/>");

		if (amount > 1) {
			tooltip.append("Amount: ").append(formatAmount(amount)).append("<br/>");
		}

		tooltip.append("Item ID: ").append(itemId).append("<br/>");

		// Add equipment bonuses section
		addEquipmentBonuses(tooltip, itemId);

		addValueInformation(tooltip, itemId, amount, itemDef);
		addLocationInformation(tooltip, isInventoryItem, isAlsoInBank);
		addStackableInformation(tooltip, itemDef);

		tooltip.append("</html>");
		return tooltip.toString();
	}

	private static void addEquipmentBonuses(StringBuilder tooltip, int itemId) {
		try {
			short[] bonuses = ItemBonusManager.getBonuses(itemId);
			if (bonuses == null || bonuses.length == 0) {
				return;
			}

			boolean hasAnyBonuses = ItemBonusManager.hasEquipmentBonuses(itemId);
			if (!hasAnyBonuses) {
				return;
			}

			tooltip.append("<br/><b>Bonuses:</b><br/>");
			String[] bonusNames = ItemBonusManager.getBonusNames();

			boolean hasAssaultBonuses = addBonusSection(tooltip, bonuses, bonusNames, 0, 5, "Assault:");
			boolean hasAegisBonuses = addBonusSection(tooltip, bonuses, bonusNames, 5, 10, "Aegis:");
			boolean hasOtherBonuses = addBonusSection(tooltip, bonuses, bonusNames, 10, bonuses.length, "Extra:");

			if (hasAssaultBonuses || hasAegisBonuses || hasOtherBonuses) {
				tooltip.append("<br/>");
			}
		} catch (Exception e) {
			System.err.println("Error adding equipment bonuses to bank tooltip: " + e.getMessage());
		}
	}

	private static boolean addBonusSection(StringBuilder tooltip, short[] bonuses, String[] bonusNames,
										   int startIndex, int endIndex, String sectionName) {
		boolean addedAny = false;
		StringBuilder sectionText = new StringBuilder();

		for (int i = startIndex; i < endIndex && i < bonuses.length; i++) {
			if (bonuses[i] != 0) {
				if (!addedAny) {
					sectionText.append("<i>").append(sectionName).append("</i><br/>");
					addedAny = true;
				}

				String bonusName = (bonusNames != null && i < bonusNames.length)
					? bonusNames[i]
					: "Bonus " + i;

				String bonusText = formatBonus(bonuses[i]);
				sectionText.append("&nbsp;&nbsp;").append(bonusName).append(": ").append(bonusText).append("<br/>");
			}
		}

		if (addedAny) {
			tooltip.append(sectionText);
		}

		return addedAny;
	}

	private static String formatBonus(short bonus) {
		if (bonus > 0) {
			return "<font color='#00ff00'>+" + bonus + "</font>";
		} else if (bonus < 0) {
			return "<font color='#ff6666'>" + bonus + "</font>";
		} else {
			return "0";
		}
	}

	private static void addValueInformation(StringBuilder tooltip, int itemId, int amount,
											ItemDefCache.CachedItemDef itemDef) {
		if (itemDef.value > 1) {
			if (itemId == 995) {
				tooltip.append("Value: ").append(formatAmount(amount)).append(" gp");
			} else {
				tooltip.append("Unit Value: ").append(formatAmount(itemDef.value)).append(" gp<br/>");
				if (amount > 1) {
					long totalValue = (long) itemDef.value * amount;
					tooltip.append("Total Value: ").append(formatAmount((int) Math.min(totalValue, Integer.MAX_VALUE))).append(" gp");
				}
			}
			tooltip.append("<br/>");
		}
	}

	private static void addLocationInformation(StringBuilder tooltip, boolean isInventoryItem, boolean isAlsoInBank) {
		if (isInventoryItem) {
			tooltip.append("<font color='orange'>In Inventory</font>");
			if (isAlsoInBank) {
				tooltip.append(" & <font color='yellow'>In Bank</font>");
			}
		} else {
			tooltip.append("<font color='yellow'>In Bank</font>");
		}
	}

	private static void addStackableInformation(StringBuilder tooltip, ItemDefCache.CachedItemDef itemDef) {
		if (itemDef.stackable) {
			tooltip.append("<br/><font color='gray'>Stackable</font>");
		}
	}

	private static String formatAmount(int amount) {
		if (amount >= 1000000) {
			return (amount / 1000000) + "M";
		} else if (amount >= 1000) {
			return (amount / 1000) + "K";
		}
		return String.valueOf(amount);
	}
}