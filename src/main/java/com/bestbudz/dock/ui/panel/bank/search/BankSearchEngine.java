package com.bestbudz.dock.ui.panel.bank.search;

import com.bestbudz.dock.ui.panel.bank.grid.BankItemPanel;

/**
 * Handles search logic for bank items
 */
public class BankSearchEngine {

	/**
	 * Tests if an item passes the current search criteria
	 */
	public static boolean passesSearch(BankItemPanel item, String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return true;
		}

		String normalizedSearchText = searchText.toLowerCase().trim();

		// Get item information for searching
		String itemName = item.getItemDef().name.toLowerCase();
		String itemIdStr = String.valueOf(item.getItemId());

		// Split search text into individual terms for better matching
		String[] searchTerms = normalizedSearchText.split("\\s+");

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

	/**
	 * Validates and normalizes search text
	 */
	public static String normalizeSearchText(String searchText) {
		if (searchText == null) {
			return "";
		}
		return searchText.toLowerCase().trim();
	}

	/**
	 * Checks if search text contains only numeric characters
	 */
	public static boolean isNumericSearch(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return false;
		}

		try {
			Integer.parseInt(searchText.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Extracts search terms from search text
	 */
	public static String[] getSearchTerms(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return new String[0];
		}

		return searchText.toLowerCase().trim().split("\\s+");
	}

	/**
	 * Checks if a term matches the beginning of any word in the item name
	 */
	public static boolean matchesWordStart(String itemName, String term) {
		if (itemName == null || term == null) {
			return false;
		}

		String[] nameWords = itemName.toLowerCase().split("\\s+");
		for (String nameWord : nameWords) {
			if (nameWord.startsWith(term.toLowerCase())) {
				return true;
			}
		}
		return false;
	}
}