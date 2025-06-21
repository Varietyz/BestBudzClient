package com.bestbudz.dock.definitions;

import java.util.Arrays;

/**
 * Immutable data container for item equipment bonuses
 * Optimized for memory efficiency and fast access
 */
public final class ItemBonusData {

	private final int itemId;
	private final short[] bonuses;
	private final boolean hasAnyBonuses;
	private final int hashCode;

	/**
	 * Create bonus data from a definition
	 *
	 * @param definition the item bonus definition
	 * @throws IllegalArgumentException if definition is invalid
	 */
	public ItemBonusData(ItemBonusDefinition definition) {
		if (definition == null) {
			throw new IllegalArgumentException("ItemBonusDefinition cannot be null");
		}

		if (definition.getId() < 0) {
			throw new IllegalArgumentException("Item ID cannot be negative: " + definition.getId());
		}

		if (definition.getBonuses() == null) {
			throw new IllegalArgumentException("Bonuses array cannot be null for item " + definition.getId());
		}

		this.itemId = definition.getId();

		// Create defensive copy of bonuses array
		this.bonuses = Arrays.copyOf(definition.getBonuses(), definition.getBonuses().length);

		// Pre-calculate if this item has any non-zero bonuses
		this.hasAnyBonuses = calculateHasAnyBonuses();

		// Pre-calculate hash code for efficient HashMap operations
		this.hashCode = calculateHashCode();
	}

	/**
	 * Create bonus data directly (for testing or special cases)
	 *
	 * @param itemId the item ID
	 * @param bonuses the bonus array
	 */
	public ItemBonusData(int itemId, short[] bonuses) {
		if (itemId < 0) {
			throw new IllegalArgumentException("Item ID cannot be negative: " + itemId);
		}
		if (bonuses == null) {
			throw new IllegalArgumentException("Bonuses array cannot be null");
		}

		this.itemId = itemId;
		this.bonuses = Arrays.copyOf(bonuses, bonuses.length);
		this.hasAnyBonuses = calculateHasAnyBonuses();
		this.hashCode = calculateHashCode();
	}

	/**
	 * Get the item ID
	 *
	 * @return the item ID
	 */
	public int getItemId() {
		return itemId;
	}

	/**
	 * Get a defensive copy of the bonuses array
	 *
	 * @return copy of the bonuses array
	 */
	public short[] getBonuses() {
		return Arrays.copyOf(bonuses, bonuses.length);
	}

	/**
	 * Get a specific bonus value by index
	 *
	 * @param index the bonus index (0-based)
	 * @return the bonus value, or 0 if index is out of bounds
	 */
	public short getBonus(int index) {
		if (index < 0 || index >= bonuses.length) {
			return 0;
		}
		return bonuses[index];
	}

	/**
	 * Get the number of bonus slots
	 *
	 * @return the length of the bonuses array
	 */
	public int getBonusCount() {
		return bonuses.length;
	}

	/**
	 * Check if this item has any non-zero bonuses
	 * This is pre-calculated for performance
	 *
	 * @return true if item has any equipment bonuses
	 */
	public boolean hasAnyBonuses() {
		return hasAnyBonuses;
	}

	/**
	 * Get assault bonuses (typically indices 0-4)
	 *
	 * @return array of assault bonuses
	 */
	public short[] getAssaultBonuses() {
		return getBonusRange(0, Math.min(5, bonuses.length));
	}

	/**
	 * Get defense bonuses (typically indices 5-9)
	 *
	 * @return array of defense bonuses
	 */
	public short[] getDefenseBonuses() {
		int start = Math.min(5, bonuses.length);
		int end = Math.min(10, bonuses.length);
		return getBonusRange(start, end);
	}

	/**
	 * Get other bonuses (typically indices 10+)
	 *
	 * @return array of other bonuses
	 */
	public short[] getOtherBonuses() {
		int start = Math.min(10, bonuses.length);
		return getBonusRange(start, bonuses.length);
	}

	/**
	 * Get a range of bonuses
	 *
	 * @param startIndex inclusive start index
	 * @param endIndex exclusive end index
	 * @return array of bonuses in the specified range
	 */
	public short[] getBonusRange(int startIndex, int endIndex) {
		if (startIndex < 0) startIndex = 0;
		if (endIndex > bonuses.length) endIndex = bonuses.length;
		if (startIndex >= endIndex) return new short[0];

		return Arrays.copyOfRange(bonuses, startIndex, endIndex);
	}

	/**
	 * Check if a specific bonus type is non-zero
	 *
	 * @param index the bonus index to check
	 * @return true if the bonus at the given index is non-zero
	 */
	public boolean hasBonusAt(int index) {
		return getBonus(index) != 0;
	}

	/**
	 * Calculate total positive bonuses (sum of all positive values)
	 *
	 * @return sum of all positive bonus values
	 */
	public int getTotalPositiveBonuses() {
		int total = 0;
		for (short bonus : bonuses) {
			if (bonus > 0) {
				total += bonus;
			}
		}
		return total;
	}

	/**
	 * Calculate total negative bonuses (sum of all negative values)
	 *
	 * @return sum of all negative bonus values (will be negative or zero)
	 */
	public int getTotalNegativeBonuses() {
		int total = 0;
		for (short bonus : bonuses) {
			if (bonus < 0) {
				total += bonus;
			}
		}
		return total;
	}

	/**
	 * Get a formatted string representation of the bonuses
	 *
	 * @param bonusNames array of bonus names (optional)
	 * @return formatted string of bonuses
	 */
	public String getFormattedBonuses(String[] bonusNames) {
		StringBuilder sb = new StringBuilder();
		sb.append("Item ").append(itemId).append(" bonuses: ");

		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] != 0) {
				if (sb.length() > ("Item " + itemId + " bonuses: ").length()) {
					sb.append(", ");
				}

				String bonusName = (bonusNames != null && i < bonusNames.length)
					? bonusNames[i]
					: "Bonus" + i;

				sb.append(bonusName).append(": ");
				if (bonuses[i] > 0) {
					sb.append("+");
				}
				sb.append(bonuses[i]);
			}
		}

		if (sb.length() == ("Item " + itemId + " bonuses: ").length()) {
			sb.append("none");
		}

		return sb.toString();
	}

	/**
	 * Calculate if this item has any non-zero bonuses
	 */
	private boolean calculateHasAnyBonuses() {
		for (short bonus : bonuses) {
			if (bonus != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Calculate hash code for efficient HashMap operations
	 */
	private int calculateHashCode() {
		int result = Integer.hashCode(itemId);
		result = 31 * result + Arrays.hashCode(bonuses);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		ItemBonusData that = (ItemBonusData) obj;
		return itemId == that.itemId && Arrays.equals(bonuses, that.bonuses);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		return String.format("ItemBonusData{itemId=%d, bonuses=%s, hasAnyBonuses=%s}",
			itemId, Arrays.toString(bonuses), hasAnyBonuses);
	}

	/**
	 * Create a builder for constructing ItemBonusData instances
	 * Useful for testing or programmatic creation
	 */
	public static class Builder {
		private int itemId;
		private short[] bonuses;

		public Builder(int itemId) {
			this.itemId = itemId;
			this.bonuses = new short[ItemBonusManager.EXPECTED_BONUS_COUNT];
		}

		public Builder setBonus(int index, short value) {
			if (index >= 0 && index < bonuses.length) {
				bonuses[index] = value;
			}
			return this;
		}

		public Builder setBonuses(short[] bonuses) {
			this.bonuses = Arrays.copyOf(bonuses, bonuses.length);
			return this;
		}

		public Builder setAssaultBonus(int index, short value) {
			if (index >= 0 && index < 5) {
				setBonus(index, value);
			}
			return this;
		}

		public Builder setDefenseBonus(int index, short value) {
			if (index >= 0 && index < 5) {
				setBonus(5 + index, value);
			}
			return this;
		}

		public ItemBonusData build() {
			return new ItemBonusData(itemId, bonuses);
		}
	}
}