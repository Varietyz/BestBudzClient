package com.bestbudz.dock.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Result container for item bonus loading operations
 * Provides success/failure status with detailed information about the loading process
 */
public final class ItemBonusLoadResult {

	private final boolean success;
	private final List<ItemBonusDefinition> definitions;
	private final List<String> warnings;
	private final String errorMessage;
	private final long timestamp;

	/**
	 * Private constructor - use static factory methods
	 */
	private ItemBonusLoadResult(boolean success, List<ItemBonusDefinition> definitions,
								List<String> warnings, String errorMessage) {
		this.success = success;
		this.definitions = definitions != null ? new ArrayList<>(definitions) : new ArrayList<>();
		this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
		this.errorMessage = errorMessage;
		this.timestamp = System.currentTimeMillis();
	}

	/**
	 * Create a successful result
	 *
	 * @param definitions the loaded item bonus definitions
	 * @return successful ItemBonusLoadResult
	 */
	public static ItemBonusLoadResult success(List<ItemBonusDefinition> definitions) {
		return new ItemBonusLoadResult(true, definitions, null, null);
	}

	/**
	 * Create a successful result with warnings
	 *
	 * @param definitions the loaded item bonus definitions
	 * @param warnings list of warning messages encountered during loading
	 * @return successful ItemBonusLoadResult with warnings
	 */
	public static ItemBonusLoadResult success(List<ItemBonusDefinition> definitions, List<String> warnings) {
		return new ItemBonusLoadResult(true, definitions, warnings, null);
	}

	/**
	 * Create a failure result
	 *
	 * @param errorMessage the error message describing what went wrong
	 * @return failed ItemBonusLoadResult
	 */
	public static ItemBonusLoadResult failure(String errorMessage) {
		return new ItemBonusLoadResult(false, null, null, errorMessage);
	}

	/**
	 * Create a failure result with partial data and warnings
	 *
	 * @param errorMessage the error message describing what went wrong
	 * @param partialDefinitions any definitions that were successfully parsed before failure
	 * @param warnings list of warning messages encountered
	 * @return failed ItemBonusLoadResult with partial data
	 */
	public static ItemBonusLoadResult failure(String errorMessage, List<ItemBonusDefinition> partialDefinitions, List<String> warnings) {
		return new ItemBonusLoadResult(false, partialDefinitions, warnings, errorMessage);
	}

	/**
	 * Check if the loading operation was successful
	 *
	 * @return true if successful, false if failed
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Check if the loading operation failed
	 *
	 * @return true if failed, false if successful
	 */
	public boolean isFailure() {
		return !success;
	}

	/**
	 * Get the loaded item bonus definitions
	 * Returns an empty list if loading failed or no definitions were found
	 *
	 * @return unmodifiable list of item bonus definitions
	 */
	public List<ItemBonusDefinition> getDefinitions() {
		return Collections.unmodifiableList(definitions);
	}

	/**
	 * Get the number of successfully loaded definitions
	 *
	 * @return count of loaded definitions
	 */
	public int getDefinitionCount() {
		return definitions.size();
	}

	/**
	 * Get warning messages from the loading process
	 * Warnings indicate non-fatal issues that didn't prevent loading
	 *
	 * @return unmodifiable list of warning messages
	 */
	public List<String> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	/**
	 * Check if there were any warnings during loading
	 *
	 * @return true if warnings were encountered
	 */
	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	/**
	 * Get the number of warnings
	 *
	 * @return count of warning messages
	 */
	public int getWarningCount() {
		return warnings.size();
	}

	/**
	 * Get the error message if loading failed
	 *
	 * @return error message, or null if loading was successful
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Get the timestamp when this result was created
	 *
	 * @return timestamp in milliseconds since epoch
	 */
	public long getTimestamp() {
		return timestamp;
	}

	/**
	 * Get a summary of the loading result
	 *
	 * @return formatted summary string
	 */
	public String getSummary() {
		if (success) {
			StringBuilder sb = new StringBuilder();
			sb.append("✅ Successfully loaded ").append(definitions.size()).append(" item bonus definitions");

			if (hasWarnings()) {
				sb.append(" (").append(warnings.size()).append(" warnings)");
			}

			return sb.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("❌ Failed to load item bonus definitions: ").append(errorMessage);

			if (!definitions.isEmpty()) {
				sb.append(" (").append(definitions.size()).append(" partial definitions loaded)");
			}

			if (hasWarnings()) {
				sb.append(" (").append(warnings.size()).append(" warnings)");
			}

			return sb.toString();
		}
	}

	/**
	 * Get detailed information about the result
	 *
	 * @return detailed formatted string with all available information
	 */
	public String getDetailedInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("ItemBonusLoadResult:\n");
		sb.append("  Status: ").append(success ? "SUCCESS" : "FAILURE").append("\n");
		sb.append("  Definitions loaded: ").append(definitions.size()).append("\n");
		sb.append("  Warnings: ").append(warnings.size()).append("\n");
		sb.append("  Timestamp: ").append(new java.util.Date(timestamp)).append("\n");

		if (!success && errorMessage != null) {
			sb.append("  Error: ").append(errorMessage).append("\n");
		}

		if (hasWarnings()) {
			sb.append("  Warning details:\n");
			for (int i = 0; i < warnings.size() && i < 10; i++) { // Limit to first 10 warnings
				sb.append("    - ").append(warnings.get(i)).append("\n");
			}
			if (warnings.size() > 10) {
				sb.append("    ... and ").append(warnings.size() - 10).append(" more warnings\n");
			}
		}

		// Show some sample definitions if available
		if (!definitions.isEmpty()) {
			sb.append("  Sample definitions:\n");
			int sampleCount = Math.min(5, definitions.size());
			for (int i = 0; i < sampleCount; i++) {
				ItemBonusDefinition def = definitions.get(i);
				sb.append("    - Item ").append(def.getId())
					.append(" (").append(def.getBonusCount()).append(" bonuses")
					.append(def.hasAnyBonuses() ? ", has non-zero bonuses)" : ", all zero)")
					.append("\n");
			}
			if (definitions.size() > sampleCount) {
				sb.append("    ... and ").append(definitions.size() - sampleCount).append(" more\n");
			}
		}

		return sb.toString();
	}

	/**
	 * Get statistics about the loaded definitions
	 *
	 * @return ItemBonusStatistics object with detailed stats
	 */
	public ItemBonusStatistics getStatistics() {
		return new ItemBonusStatistics(definitions);
	}

	/**
	 * Find a specific definition by item ID
	 *
	 * @param itemId the item ID to search for
	 * @return the ItemBonusDefinition if found, null otherwise
	 */
	public ItemBonusDefinition findDefinition(int itemId) {
		for (ItemBonusDefinition definition : definitions) {
			if (definition.getId() == itemId) {
				return definition;
			}
		}
		return null;
	}

	/**
	 * Check if a specific item ID has a definition in this result
	 *
	 * @param itemId the item ID to check
	 * @return true if the item has a definition
	 */
	public boolean hasDefinition(int itemId) {
		return findDefinition(itemId) != null;
	}

	/**
	 * Get all unique item IDs in this result
	 *
	 * @return list of item IDs
	 */
	public List<Integer> getItemIds() {
		List<Integer> itemIds = new ArrayList<>();
		for (ItemBonusDefinition definition : definitions) {
			itemIds.add(definition.getId());
		}
		return itemIds;
	}

	@Override
	public String toString() {
		return getSummary();
	}

	/**
	 * Statistics container for bonus definitions
	 */
	public static class ItemBonusStatistics {
		private final int totalItems;
		private final int itemsWithBonuses;
		private final int itemsWithoutBonuses;
		private final int minItemId;
		private final int maxItemId;
		private final double averageBonusesPerItem;

		private ItemBonusStatistics(List<ItemBonusDefinition> definitions) {
			this.totalItems = definitions.size();

			int withBonuses = 0;
			int minId = Integer.MAX_VALUE;
			int maxId = Integer.MIN_VALUE;
			int totalBonusCount = 0;

			for (ItemBonusDefinition def : definitions) {
				if (def.hasAnyBonuses()) {
					withBonuses++;
				}

				int id = def.getId();
				if (id < minId) minId = id;
				if (id > maxId) maxId = id;

				totalBonusCount += def.getBonusCount();
			}

			this.itemsWithBonuses = withBonuses;
			this.itemsWithoutBonuses = totalItems - withBonuses;
			this.minItemId = totalItems > 0 ? minId : 0;
			this.maxItemId = totalItems > 0 ? maxId : 0;
			this.averageBonusesPerItem = totalItems > 0 ? (double) totalBonusCount / totalItems : 0.0;
		}

		public int getTotalItems() { return totalItems; }
		public int getItemsWithBonuses() { return itemsWithBonuses; }
		public int getItemsWithoutBonuses() { return itemsWithoutBonuses; }
		public int getMinItemId() { return minItemId; }
		public int getMaxItemId() { return maxItemId; }
		public double getAverageBonusesPerItem() { return averageBonusesPerItem; }

		public double getPercentageWithBonuses() {
			return totalItems > 0 ? (double) itemsWithBonuses / totalItems * 100.0 : 0.0;
		}

		@Override
		public String toString() {
			return String.format("ItemBonusStatistics{total=%d, withBonuses=%d (%.1f%%), range=%d-%d, avgBonuses=%.1f}",
				totalItems, itemsWithBonuses, getPercentageWithBonuses(), minItemId, maxItemId, averageBonusesPerItem);
		}
	}
}