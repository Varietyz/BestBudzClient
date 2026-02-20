package com.bestbudz.dock.definitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ItemBonusLoadResult {

	private final boolean success;
	private final List<ItemBonusDefinition> definitions;
	private final List<String> warnings;
	private final String errorMessage;
	private final long timestamp;

	private ItemBonusLoadResult(boolean success, List<ItemBonusDefinition> definitions,
								List<String> warnings, String errorMessage) {
		this.success = success;
		this.definitions = definitions != null ? new ArrayList<>(definitions) : new ArrayList<>();
		this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
		this.errorMessage = errorMessage;
		this.timestamp = System.currentTimeMillis();
	}

	public static ItemBonusLoadResult success(List<ItemBonusDefinition> definitions) {
		return new ItemBonusLoadResult(true, definitions, null, null);
	}

	public static ItemBonusLoadResult success(List<ItemBonusDefinition> definitions, List<String> warnings) {
		return new ItemBonusLoadResult(true, definitions, warnings, null);
	}

	public static ItemBonusLoadResult failure(String errorMessage) {
		return new ItemBonusLoadResult(false, null, null, errorMessage);
	}

	public static ItemBonusLoadResult failure(String errorMessage, List<ItemBonusDefinition> partialDefinitions, List<String> warnings) {
		return new ItemBonusLoadResult(false, partialDefinitions, warnings, errorMessage);
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isFailure() {
		return !success;
	}

	public List<ItemBonusDefinition> getDefinitions() {
		return Collections.unmodifiableList(definitions);
	}

	public int getDefinitionCount() {
		return definitions.size();
	}

	public List<String> getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	public boolean hasWarnings() {
		return !warnings.isEmpty();
	}

	public int getWarningCount() {
		return warnings.size();
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public long getTimestamp() {
		return timestamp;
	}

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
			for (int i = 0; i < warnings.size() && i < 10; i++) {
				sb.append("    - ").append(warnings.get(i)).append("\n");
			}
			if (warnings.size() > 10) {
				sb.append("    ... and ").append(warnings.size() - 10).append(" more warnings\n");
			}
		}

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

	public ItemBonusStatistics getStatistics() {
		return new ItemBonusStatistics(definitions);
	}

	public ItemBonusDefinition findDefinition(int itemId) {
		for (ItemBonusDefinition definition : definitions) {
			if (definition.getId() == itemId) {
				return definition;
			}
		}
		return null;
	}

	public boolean hasDefinition(int itemId) {
		return findDefinition(itemId) != null;
	}

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
