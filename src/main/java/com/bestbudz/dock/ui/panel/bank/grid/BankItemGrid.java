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

public class BankItemGrid extends JPanel {

	private static final int COLUMNS = 7;
	private static final int ITEM_SIZE = 32;
	private static final int GAP = 2;

	private final List<BankItemPanel> allItems = new ArrayList<>();
	private final Object parentBankPanel;

	private BankFilterEngine.FilterType currentFilter = BankFilterEngine.FilterType.ALL;
	private BankFilterEngine.SortType currentSort = BankFilterEngine.SortType.NONE;
	private String searchText = "";

	private boolean isUpdating = false;

	public BankItemGrid(Object parentBankPanel) {
		this.parentBankPanel = parentBankPanel;
		setLayout(new GridLayout(0, COLUMNS, GAP, GAP));
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setBorder(new EmptyBorder(0, 8, 8, 8));

		setDoubleBuffered(true);
	}

	public void setFilter(BankFilterEngine.FilterType filter) {
		if (this.currentFilter != filter && !isUpdating) {
			this.currentFilter = filter;
			applyFiltersAndSort();
		}
	}

	public void setSort(BankFilterEngine.SortType sort) {
		if (this.currentSort != sort && !isUpdating) {
			this.currentSort = sort;
			applyFiltersAndSort();
		}
	}

	public void setSearchText(String text) {
		String newSearchText = BankSearchEngine.normalizeSearchText(text);
		if (!this.searchText.equals(newSearchText) && !isUpdating) {
			this.searchText = newSearchText;
			applyFiltersAndSort();

			if (!searchText.isEmpty()) {
				int resultCount = getFilteredItemCount();
				System.out.println("Search '" + text + "' found " + resultCount + " items");
			}
		}
	}

	public int getFilteredItemCount() {
		return (int) allItems.stream()
			.filter(item -> BankFilterEngine.passesFilter(item, currentFilter))
			.filter(item -> BankSearchEngine.passesSearch(item, searchText))
			.count();
	}

	private void applyFiltersAndSort() {
		if (isUpdating) {
			return;
		}

		SwingUtilities.invokeLater(this::performFilterAndSort);
	}

	private void performFilterAndSort() {
		isUpdating = true;

		try {

			clearHoverStates();

			removeAll();

			List<BankItemPanel> displayItems = allItems.stream()
				.filter(item -> BankFilterEngine.passesFilter(item, currentFilter))
				.filter(item -> BankSearchEngine.passesSearch(item, searchText))
				.collect(Collectors.toList());

			BankFilterEngine.sortItems(displayItems, currentSort);

			for (BankItemPanel item : displayItems) {
				if (item != null) {
					item.setParentBankPanel(parentBankPanel);
					add(item);
				}
			}

			updateGridHeight();

			revalidate();
			repaint();

		} finally {
			isUpdating = false;
		}

		if (parentBankPanel != null) {
			try {

				parentBankPanel.getClass().getMethod("forceUpdateAfterClick").invoke(parentBankPanel);
			} catch (Exception e) {

				try {
					parentBankPanel.getClass().getMethod("updateBankData").invoke(parentBankPanel);
				} catch (Exception ex) {
					System.err.println("Could not trigger parent update: " + ex.getMessage());
				}
			}
		}
	}

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

	public void clearHoverStates() {
		for (BankItemPanel itemPanel : allItems) {
			if (itemPanel != null) {

				itemPanel.isHovered = false;
				itemPanel.currentBackground = itemPanel.originalBackground;
			}
		}
	}

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

			repaint();
		});
	}

	public void updateItems() {
		if (!SwingUtilities.isEventDispatchThread()) {
			SwingUtilities.invokeLater(this::updateItems);
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

			if (!Client.loggedIn) {
				invalidate();
				revalidate();
				repaint();
				return;
			}

			Map<Integer, Integer> inventoryItems = getInventoryItems();
			Map<Integer, Integer> bankItems = getBankItems();

			addInventoryItemsToList(inventoryItems, bankItems);
			addPlaceholderItemsToList(inventoryItems, bankItems);
			addBankItemsToList(bankItems, inventoryItems);

			performFilterAndSort();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			isUpdating = false;
		}
	}

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

	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

	public BankFilterEngine.FilterType getCurrentFilter() { return currentFilter; }
	public BankFilterEngine.SortType getCurrentSort() { return currentSort; }
	public String getSearchText() { return searchText; }

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

		int rows = (componentCount + COLUMNS - 1) / COLUMNS;

		int totalHeight = (rows * ITEM_SIZE) + ((rows - 1) * GAP);

		Dimension currentSize = getPreferredSize();
		setPreferredSize(new Dimension(currentSize.width, totalHeight));

		revalidate();
	}

	@Override
	public void setBounds(int x, int y, int width, int height) {
		int componentCount = getComponentCount();

		if (componentCount == 0) {

			int minHeight = ITEM_SIZE + GAP;
			super.setBounds(x, y, width, minHeight);
			return;
		}

		int rows = (componentCount + COLUMNS - 1) / COLUMNS;

		int neededHeight = (rows * ITEM_SIZE) + ((rows - 1) * GAP);

		Insets insets = getInsets();
		neededHeight += insets.top + insets.bottom;

		int actualHeight = Math.max(neededHeight, Math.min(height, neededHeight));

		super.setBounds(x, y, width, actualHeight);
	}

}
