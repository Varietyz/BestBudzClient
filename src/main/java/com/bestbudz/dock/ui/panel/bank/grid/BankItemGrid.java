package com.bestbudz.dock.ui.panel.bank.grid;

import com.bestbudz.dock.ui.panel.bank.search.BankFilterEngine;
import com.bestbudz.dock.ui.panel.bank.search.BankSearchEngine;
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
 * Fixed bank item grid - addresses painting issues during filtering/sorting/searching
 */
public class BankItemGrid extends JPanel {

	private static final int COLUMNS = 7;
	private static final int ITEM_SIZE = 32;
	private static final int GAP = 2;

	private final List<BankItemPanel> allItems = new ArrayList<>();
	private final Object parentBankPanel;

	// Current filters and settings
	private BankFilterEngine.FilterType currentFilter = BankFilterEngine.FilterType.ALL;
	private BankFilterEngine.SortType currentSort = BankFilterEngine.SortType.NONE;
	private String searchText = "";

	// Track if we're currently updating to prevent recursive calls
	private boolean isUpdating = false;

	public BankItemGrid(Object parentBankPanel) {
		this.parentBankPanel = parentBankPanel;
		setLayout(new GridLayout(0, COLUMNS, GAP, GAP));
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setBorder(new EmptyBorder(0, 8, 8, 8));

		// Ensure double buffering is enabled for smooth repaints
		setDoubleBuffered(true);
	}

	/**
	 * Sets the current filter and applies changes
	 */
	public void setFilter(BankFilterEngine.FilterType filter) {
		if (this.currentFilter != filter && !isUpdating) {
			this.currentFilter = filter;
			applyFiltersAndSort();
		}
	}

	/**
	 * Sets the current sort and applies changes
	 */
	public void setSort(BankFilterEngine.SortType sort) {
		if (this.currentSort != sort && !isUpdating) {
			this.currentSort = sort;
			applyFiltersAndSort();
		}
	}

	/**
	 * Sets the search text and applies changes
	 */
	public void setSearchText(String text) {
		String newSearchText = BankSearchEngine.normalizeSearchText(text);
		if (!this.searchText.equals(newSearchText) && !isUpdating) {
			this.searchText = newSearchText;
			applyFiltersAndSort();

			// Optional: Print search results count for debugging
			if (!searchText.isEmpty()) {
				int resultCount = getFilteredItemCount();
				System.out.println("Search '" + text + "' found " + resultCount + " items");
			}
		}
	}

	/**
	 * Gets count of items that pass current filters
	 */
	public int getFilteredItemCount() {
		return (int) allItems.stream()
			.filter(item -> BankFilterEngine.passesFilter(item, currentFilter))
			.filter(item -> BankSearchEngine.passesSearch(item, searchText))
			.count();
	}

	/**
	 * Applies current filters and sorting with simple coordination
	 */
	private void applyFiltersAndSort() {
		if (isUpdating) {
			return;
		}

		SwingUtilities.invokeLater(this::performFilterAndSort);
	}

	/**
	 * Simple filter and sort operation
	 */
	private void performFilterAndSort() {
		isUpdating = true;

		try {
			// Clear hover states
			clearHoverStates();

			// Remove all components
			removeAll();

			// Filter and sort items
			List<BankItemPanel> displayItems = allItems.stream()
				.filter(item -> BankFilterEngine.passesFilter(item, currentFilter))
				.filter(item -> BankSearchEngine.passesSearch(item, searchText))
				.collect(Collectors.toList());

			BankFilterEngine.sortItems(displayItems, currentSort);

			// Add items back
			for (BankItemPanel item : displayItems) {
				if (item != null) {
					item.setParentBankPanel(parentBankPanel);
					add(item);
				}
			}

			updateGridHeight();

			// Simple revalidation
			revalidate();
			repaint();

		} finally {
			isUpdating = false;
		}

		// FORCE THE SAME UPDATE CYCLE THAT HAPPENS WHEN CLICKING AN ITEM
		// This is what fixes the display - trigger parent update
		if (parentBankPanel != null) {
			try {
				// Call the parent's force update method (same as clicking does)
				parentBankPanel.getClass().getMethod("forceUpdateAfterClick").invoke(parentBankPanel);
			} catch (Exception e) {
				// Fallback - try the generic update method
				try {
					parentBankPanel.getClass().getMethod("updateBankData").invoke(parentBankPanel);
				} catch (Exception ex) {
					System.err.println("Could not trigger parent update: " + ex.getMessage());
				}
			}
		}
	}

	/**
	 * Finds the scroll pane parent
	 */
	private Container getScrollPaneParent() {
		Container parent = getParent();
		while (parent != null) {
			if (parent instanceof JScrollPane) {
				return parent;
			}
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * Clears hover states on all items - NO REPAINTS
	 */
	public void clearHoverStates() {
		for (BankItemPanel itemPanel : allItems) {
			if (itemPanel != null) {
				// Just reset the state, no fucking repaints
				itemPanel.isHovered = false;
				itemPanel.currentBackground = itemPanel.originalBackground;
			}
		}
	}

	/**
	 * Refreshes item amounts from game data
	 */
	public void refreshItemAmounts() {
		if (isUpdating) {
			return;
		}

		SwingUtilities.invokeLater(() -> {
			for (BankItemPanel itemPanel : allItems) {
				if (itemPanel != null) {
					itemPanel.refreshFromGameData();
				}
			}

			// Force repaint after amount refresh
			repaint();
		});
	}

	/**
	 * Updates all items from game data with improved painting coordination
	 */
	public void updateItems() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::updateItems);
			return;
		}

		if (isUpdating) {
			return; // Prevent recursive updates
		}

		isUpdating = true;

		try {
			clearHoverStates();
			removeAll();
			allItems.clear();

			if (!Client.loggedIn) {
				invalidate();
				revalidate();
				repaint();
				return;
			}

			// Get inventory and bank item maps
			Map<Integer, Integer> inventoryItems = getInventoryItems();
			Map<Integer, Integer> bankItems = getBankItems();

			// Add all items to the list
			addInventoryItemsToList(inventoryItems, bankItems);
			addPlaceholderItemsToList(inventoryItems, bankItems);
			addBankItemsToList(bankItems, inventoryItems);

			// Apply current filters and sort
			performFilterAndSort();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isUpdating = false;
		}
	}

	/**
	 * Clears all items with proper cleanup
	 */
	public void clearItems() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::clearItems);
			return;
		}

		if (isUpdating) {
			return;
		}

		isUpdating = true;

		try {
			clearHoverStates();
			removeAll();
			allItems.clear();

			invalidate();
			revalidate();
			repaint();
		} finally {
			isUpdating = false;
		}
	}

	/**
	 * Override paintComponent to ensure proper background painting
	 */
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	// Getters for current state
	public BankFilterEngine.FilterType getCurrentFilter() { return currentFilter; }
	public BankFilterEngine.SortType getCurrentSort() { return currentSort; }
	public String getSearchText() { return searchText; }

	// Private helper methods for data extraction (unchanged)
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

	private void updateGridHeight() {
		int componentCount = getComponentCount();
		if (componentCount == 0) {
			return;
		}

		// Calculate rows needed
		int rows = (componentCount + COLUMNS - 1) / COLUMNS; // Ceiling division

		// Calculate exact height needed for those rows
		int totalHeight = (rows * ITEM_SIZE) + ((rows - 1) * GAP);

		// Set the grid's preferred height
		Dimension currentSize = getPreferredSize();
		setPreferredSize(new Dimension(currentSize.width, totalHeight));

		// Force the parent to respect this new size
		revalidate();
	}

	// Add this method to BankItemGrid class - ONLY THIS, NOTHING ELSE

	@Override
	public void setBounds(int x, int y, int width, int height) {
		int componentCount = getComponentCount();

		if (componentCount == 0) {
			// Empty grid - minimum height for 1 row
			int minHeight = ITEM_SIZE + GAP;
			super.setBounds(x, y, width, minHeight);
			return;
		}

		// Calculate rows needed for current content
		int rows = (componentCount + COLUMNS - 1) / COLUMNS; // Ceiling division

		// Calculate EXACT height needed for these rows
		int neededHeight = (rows * ITEM_SIZE) + ((rows - 1) * GAP);

		// Add border/padding
		Insets insets = getInsets();
		neededHeight += insets.top + insets.bottom;

		// Use the smaller of: what's requested vs what's actually needed
		int actualHeight = Math.max(neededHeight, Math.min(height, neededHeight));

		super.setBounds(x, y, width, actualHeight);
	}

}