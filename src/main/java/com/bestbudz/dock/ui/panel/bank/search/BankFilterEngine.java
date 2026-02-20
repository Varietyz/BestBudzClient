package com.bestbudz.dock.ui.panel.bank.search;

import com.bestbudz.dock.ui.panel.bank.grid.BankItemPanel;
import java.util.List;
import java.util.Comparator;

/**
 * Handles filtering and sorting logic for bank items
 */
public class BankFilterEngine {

	public enum FilterType {
		ALL("All Items"),
		INVENTORY_ONLY("Inventory Only"),
		BANK_ONLY("Bank Only"),
		HIGH_VALUE("High Value (1M+)"),
		STACKABLE("Stackable (>1)");

		private final String displayName;
		FilterType(String displayName) { this.displayName = displayName; }
		public String getDisplayName() { return displayName; }
	}

	public enum SortType {
		NONE("No Sort"),
		AMOUNT_DESC("Amount (High→Low)"),
		AMOUNT_ASC("Amount (Low→High)"),
		ID_ASC("Item ID (A→Z)"),
		ID_DESC("Item ID (Z→A)"),
		NAME_ASC("Name (A→Z)"),
		NAME_DESC("Name (Z→A)"),
		VALUE_DESC("Value (High→Low)"),
		VALUE_ASC("Value (Low→High)");

		private final String displayName;
		SortType(String displayName) { this.displayName = displayName; }
		public String getDisplayName() { return displayName; }
	}

	/**
	 * Tests if an item passes the specified filter
	 */
	public static boolean passesFilter(BankItemPanel item, FilterType filter) {
		if (filter == null) {
			return true;
		}

		switch (filter) {
			case ALL:
				return true;
			case INVENTORY_ONLY:
				return item.isInventoryItem();
			case BANK_ONLY:
				return !item.isInventoryItem() && !item.isPlaceholder();
			case HIGH_VALUE:
				return item.getItemId() == 995 && item.getCurrentAmount() >= 1000000;
			case STACKABLE:
				return item.getCurrentAmount() > 1;
			default:
				return true;
		}
	}

	/**
	 * Sorts a list of items according to the specified sort type
	 */
	public static void sortItems(List<BankItemPanel> itemsToSort, SortType sortType) {
		if (sortType == null || sortType == SortType.NONE) {
			return;
		}

		Comparator<BankItemPanel> comparator = getComparator(sortType);
		if (comparator != null) {
			itemsToSort.sort(comparator);
		}
	}

	private static Comparator<BankItemPanel> getComparator(SortType sortType) {
		switch (sortType) {
			case AMOUNT_DESC:
				return (a, b) -> Integer.compare(b.getCurrentAmount(), a.getCurrentAmount());
			case AMOUNT_ASC:
				return (a, b) -> Integer.compare(a.getCurrentAmount(), b.getCurrentAmount());
			case ID_ASC:
				return (a, b) -> Integer.compare(a.getItemId(), b.getItemId());
			case ID_DESC:
				return (a, b) -> Integer.compare(b.getItemId(), a.getItemId());
			case NAME_ASC:
				return (a, b) -> a.getItemDef().name.compareToIgnoreCase(b.getItemDef().name);
			case NAME_DESC:
				return (a, b) -> b.getItemDef().name.compareToIgnoreCase(a.getItemDef().name);
			case VALUE_DESC:
				return (a, b) -> Long.compare(getActualValue(b), getActualValue(a));
			case VALUE_ASC:
				return (a, b) -> Long.compare(getActualValue(a), getActualValue(b));
			default:
				return null;
		}
	}

	/**
	 * Calculates the actual value of an item considering amount and type
	 */
	private static long getActualValue(BankItemPanel item) {
		if (item.getItemId() == 995) {
			return item.getCurrentAmount();
		}
		long itemValue = item.getItemDef().value;
		return itemValue * item.getCurrentAmount();
	}

	/**
	 * Gets display names for all filter types
	 */
	public static String[] getFilterDisplayNames() {
		FilterType[] filters = FilterType.values();
		String[] names = new String[filters.length];
		for (int i = 0; i < filters.length; i++) {
			names[i] = filters[i].getDisplayName();
		}
		return names;
	}

	/**
	 * Gets display names for all sort types
	 */
	public static String[] getSortDisplayNames() {
		SortType[] sorts = SortType.values();
		String[] names = new String[sorts.length];
		for (int i = 0; i < sorts.length; i++) {
			names[i] = sorts[i].getDisplayName();
		}
		return names;
	}
}