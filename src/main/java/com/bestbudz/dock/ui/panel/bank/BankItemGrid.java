package com.bestbudz.dock.ui.panel.bank;

import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Clean, responsive bank grid
 */
public class BankItemGrid extends JPanel {

	private static final int COLUMNS = 7;
	private static final int ITEM_SIZE = 32;
	private static final int GAP = 2;

	private final List<BankItemPanel> allItems = new ArrayList<>();
	private final BankPanel parentBankPanel;

	// Simple filter and sort options
	private FilterType currentFilter = FilterType.ALL;
	private SortType currentSort = SortType.NONE;
	private String searchText = "";

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

	public BankItemGrid(BankPanel parentBankPanel) {
		this.parentBankPanel = parentBankPanel;
		setLayout(new GridLayout(0, COLUMNS, GAP, GAP));
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setBorder(new EmptyBorder(0, 8, 8, 8));
	}

	public void setFilter(FilterType filter) {
		this.currentFilter = filter;
		applyFiltersAndSort();
	}

	public void setSort(SortType sort) {
		this.currentSort = sort;
		applyFiltersAndSort();
	}

	public void setSearchText(String text) {
		String newSearchText = text.toLowerCase().trim();
		if (!this.searchText.equals(newSearchText)) {
			this.searchText = newSearchText;
			applyFiltersAndSort();

			// Optional: Print search results count for debugging
			if (!searchText.isEmpty()) {
				int resultCount = getFilteredItemCount();
				System.out.println("Search '" + text + "' found " + resultCount + " items");
			}
		}
	}

	public FilterType getCurrentFilter() { return currentFilter; }
	public SortType getCurrentSort() { return currentSort; }
	public String getSearchText() { return searchText; }

	private void applyFiltersAndSort() {
		SwingUtilities.invokeLater(() -> {
			removeAll();

			// Filter and sort items
			List<BankItemPanel> displayItems = allItems.stream()
				.filter(this::passesFilter)
				.filter(this::passesSearch)
				.collect(Collectors.toList());

			sortItems(displayItems);

			// Add to display
			for (BankItemPanel item : displayItems) {
				add(item);
			}

			revalidate();
			repaint();
		});
	}

	private boolean passesFilter(BankItemPanel item) {
		switch (currentFilter) {
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

	private boolean passesSearch(BankItemPanel item) {
		if (searchText.isEmpty()) return true;

		// Get item information for searching
		String itemName = item.getItemDef().name.toLowerCase();
		String itemIdStr = String.valueOf(item.getItemId());

		// Split search text into individual terms for better matching
		String[] searchTerms = searchText.split("\\s+");

		// Check if ALL search terms match (AND logic)
		for (String term : searchTerms) {
			if (term.isEmpty()) continue;

			boolean termMatches = false;

			// Check item name (primary search)
			if (itemName.contains(term)) {
				termMatches = true;
			}

			// Check item ID (secondary search)
			if (!termMatches && itemIdStr.contains(term)) {
				termMatches = true;
			}

			// Check if term is a number and matches amount (for stackable items)
			if (!termMatches && item.getCurrentAmount() > 1) {
				try {
					int searchAmount = Integer.parseInt(term);
					if (item.getCurrentAmount() >= searchAmount) {
						termMatches = true;
					}
				} catch (NumberFormatException e) {
					// Not a number, ignore
				}
			}

			// Check for partial matches with word boundaries (more natural search)
			if (!termMatches) {
				String[] nameWords = itemName.split("\\s+");
				for (String nameWord : nameWords) {
					if (nameWord.startsWith(term)) {
						termMatches = true;
						break;
					}
				}
			}

			// If any term doesn't match, this item fails the search
			if (!termMatches) {
				return false;
			}
		}

		return true; // All terms matched
	}

	// Also add this helper method to BankItemGrid for better search feedback:
	public int getFilteredItemCount() {
		return (int) allItems.stream()
			.filter(this::passesFilter)
			.filter(this::passesSearch)
			.count();
	}


	private void sortItems(List<BankItemPanel> itemsToSort) {
		switch (currentSort) {
			case AMOUNT_DESC:
				itemsToSort.sort((a, b) -> Integer.compare(b.getCurrentAmount(), a.getCurrentAmount()));
				break;
			case AMOUNT_ASC:
				itemsToSort.sort((a, b) -> Integer.compare(a.getCurrentAmount(), b.getCurrentAmount()));
				break;
			case ID_ASC:
				itemsToSort.sort((a, b) -> Integer.compare(a.getItemId(), b.getItemId()));
				break;
			case ID_DESC:
				itemsToSort.sort((a, b) -> Integer.compare(b.getItemId(), a.getItemId()));
				break;
			case NAME_ASC:
				itemsToSort.sort((a, b) -> a.getItemDef().name.compareToIgnoreCase(b.getItemDef().name));
				break;
			case NAME_DESC:
				itemsToSort.sort((a, b) -> b.getItemDef().name.compareToIgnoreCase(a.getItemDef().name));
				break;
			case VALUE_DESC:
				itemsToSort.sort((a, b) -> Long.compare(getActualValue(b), getActualValue(a)));
				break;
			case VALUE_ASC:
				itemsToSort.sort((a, b) -> Long.compare(getActualValue(a), getActualValue(b)));
				break;
			case NONE:
			default:
				break;
		}
	}

	private long getActualValue(BankItemPanel item) {
		if (item.getItemId() == 995) {
			return item.getCurrentAmount();
		}
		long itemValue = item.getItemDef().value;
		return itemValue * item.getCurrentAmount();
	}

	public void clearHoverStates() {
		for (BankItemPanel itemPanel : allItems) {
			if (itemPanel != null) {
				itemPanel.resetBackground();
			}
		}
	}

	public void refreshItemAmounts() {
		SwingUtilities.invokeLater(() -> {
			for (BankItemPanel itemPanel : allItems) {
				if (itemPanel != null) {
					itemPanel.refreshFromGameData();
				}
			}
			repaint();
		});
	}

	public void updateItems() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::updateItems);
			return;
		}

		clearHoverStates();
		removeAll();
		allItems.clear();

		if (!Client.loggedIn) {
			revalidate();
			repaint();
			return;
		}

		try {
			// Get inventory and bank item maps
			Map<Integer, Integer> inventoryItems = getInventoryItems();
			Map<Integer, Integer> bankItems = getBankItems();

			// Add all items to the list
			addInventoryItemsToList(inventoryItems, bankItems);
			addPlaceholderItemsToList(inventoryItems, bankItems);
			addBankItemsToList(bankItems, inventoryItems);

			// Apply current filters and sort
			applyFiltersAndSort();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Map<Integer, Integer> getInventoryItems() {
		Map<Integer, Integer> items = new HashMap<>();
		try {
			RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
			if (inventoryInterface != null && inventoryInterface.inv != null && inventoryInterface.invStackSizes != null) {

				int maxIndex = Math.min(inventoryInterface.inv.length, inventoryInterface.invStackSizes.length);

				for (int i = 0; i < maxIndex; i++) {
					int itemId = inventoryInterface.inv[i] - 1;
					int amount = inventoryInterface.invStackSizes[i];

					if (itemId >= 0 && amount > 0) {
						items.put(itemId, items.getOrDefault(itemId, 0) + amount);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error getting inventory items: " + e.getMessage());
		}
		return items;
	}

	private Map<Integer, Integer> getBankItems() {
		Map<Integer, Integer> items = new HashMap<>();
		try {
			RSInterface bankInterface = RSInterface.interfaceCache[5382];
			if (bankInterface != null && bankInterface.inv != null && bankInterface.invStackSizes != null) {

				int maxIndex = Math.min(bankInterface.inv.length, bankInterface.invStackSizes.length);

				for (int i = 0; i < maxIndex; i++) {
					int itemId = bankInterface.inv[i] - 1;
					int amount = bankInterface.invStackSizes[i];

					if (itemId >= 0 && amount > 0) {
						items.put(itemId, items.getOrDefault(itemId, 0) + amount);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error getting bank items: " + e.getMessage());
		}
		return items;
	}

	private void addInventoryItemsToList(Map<Integer, Integer> inventoryItems, Map<Integer, Integer> bankItems) {
		for (Map.Entry<Integer, Integer> entry : inventoryItems.entrySet()) {
			int itemId = entry.getKey();
			int amount = entry.getValue();
			boolean isAlsoInBank = bankItems.containsKey(itemId);

			BankItemPanel itemPanel = new BankItemPanel(itemId, amount, -1, true, isAlsoInBank, ITEM_SIZE);
			itemPanel.setParentBankPanel(parentBankPanel);
			allItems.add(itemPanel);
		}
	}

	private void addPlaceholderItemsToList(Map<Integer, Integer> inventoryItems, Map<Integer, Integer> bankItems) {
		for (Map.Entry<Integer, Integer> entry : inventoryItems.entrySet()) {
			int itemId = entry.getKey();
			int inventoryAmount = entry.getValue();

			if (!bankItems.containsKey(itemId) && inventoryAmount > 0) {
				BankItemPanel placeholderPanel = new BankItemPanel(itemId, inventoryAmount, -1, false, false, ITEM_SIZE);
				placeholderPanel.setParentBankPanel(parentBankPanel);
				placeholderPanel.setAsPlaceholder(true);
				allItems.add(placeholderPanel);
			}
		}
	}

	private void addBankItemsToList(Map<Integer, Integer> bankItems, Map<Integer, Integer> inventoryItems) {
		for (Map.Entry<Integer, Integer> entry : bankItems.entrySet()) {
			int itemId = entry.getKey();
			int amount = entry.getValue();
			boolean isAlsoInInventory = inventoryItems.containsKey(itemId);

			BankItemPanel itemPanel = new BankItemPanel(itemId, amount, -1, false, isAlsoInInventory, ITEM_SIZE);
			itemPanel.setParentBankPanel(parentBankPanel);
			allItems.add(itemPanel);
		}
	}

	public void clearItems() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::clearItems);
			return;
		}

		removeAll();
		allItems.clear();
		revalidate();
		repaint();
	}
}