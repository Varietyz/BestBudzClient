package com.bestbudz.dock.ui.panel.bank.search;

import com.bestbudz.dock.ui.panel.bank.grid.BankItemPanel;

public class BankSearchEngine {

	public static boolean passesSearch(BankItemPanel item, String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return true;
		}

		String normalizedSearchText = searchText.toLowerCase().trim();

		String itemName = item.getItemDef().name.toLowerCase();
		String itemIdStr = String.valueOf(item.getItemId());

		String[] searchTerms = normalizedSearchText.split("\\s+");

		for (String term : searchTerms) {
			if (term.isEmpty()) continue;

			boolean termMatches = false;

			if (itemName.contains(term)) {
				termMatches = true;
			}

			if (!termMatches && itemIdStr.contains(term)) {
				termMatches = true;
			}

			if (!termMatches && item.getCurrentAmount() > 1) {
				try {
					int searchAmount = Integer.parseInt(term);
					if (item.getCurrentAmount() >= searchAmount) {
						termMatches = true;
					}
				} catch (NumberFormatException e) {

				}
			}

			if (!termMatches) {
				String[] nameWords = itemName.split("\\s+");
				for (String nameWord : nameWords) {
					if (nameWord.startsWith(term)) {
						termMatches = true;
						break;
					}
				}
			}

			if (!termMatches) {
				return false;
			}
		}

		return true;
	}

	public static String normalizeSearchText(String searchText) {
		if (searchText == null) {
			return "";
		}
		return searchText.toLowerCase().trim();
	}

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

	public static String[] getSearchTerms(String searchText) {
		if (searchText == null || searchText.trim().isEmpty()) {
			return new String[0];
		}

		return searchText.toLowerCase().trim().split("\\s+");
	}

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
