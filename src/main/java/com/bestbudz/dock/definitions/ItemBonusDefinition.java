package com.bestbudz.dock.definitions;

import java.util.Arrays;

/**
 * Data class representing a single item bonus definition from XML
 * Maps directly to the XML structure for easy parsing
 */
public class ItemBonusDefinition {

	private int id;
	private short[] bonuses;

	/**
	 * Default constructor for XML parsing
	 */
	public ItemBonusDefinition() {
		this.id = -1;
		this.bonuses = null;
	}

	/**
	 * Constructor for programmatic creation
	 *
	 * @param id the item ID
	 * @param bonuses the bonus values array
	 */
	public ItemBonusDefinition(int id, short[] bonuses) {
		this.id = id;
		this.bonuses = bonuses != null ? Arrays.copyOf(bonuses, bonuses.length) : null;
	}

	/**
	 * Get the item ID
	 *
	 * @return the item ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Set the item ID
	 *
	 * @param id the item ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the bonuses array
	 *
	 * @return the bonuses array (defensive copy)
	 */
	public short[] getBonuses() {
		return bonuses != null ? Arrays.copyOf(bonuses, bonuses.length) : null;
	}

	/**
	 * Set the bonuses array
	 *
	 * @param bonuses the bonuses array
	 */
	public void setBonuses(short[] bonuses) {
		this.bonuses = bonuses != null ? Arrays.copyOf(bonuses, bonuses.length) : null;
	}

	/**
	 * Get a specific bonus value
	 *
	 * @param index the bonus index
	 * @return the bonus value, or 0 if index is invalid
	 */
	public short getBonus(int index) {
		if (bonuses == null || index < 0 || index >= bonuses.length) {
			return 0;
		}
		return bonuses[index];
	}

	/**
	 * Set a specific bonus value
	 *
	 * @param index the bonus index
	 * @param value the bonus value
	 */
	public void setBonus(int index, short value) {
		if (bonuses != null && index >= 0 && index < bonuses.length) {
			bonuses[index] = value;
		}
	}

	/**
	 * Get the number of bonuses
	 *
	 * @return the bonuses array length, or 0 if null
	 */
	public int getBonusCount() {
		return bonuses != null ? bonuses.length : 0;
	}

	/**
	 * Check if this definition is valid
	 *
	 * @return true if the definition has valid data
	 */
	public boolean isValid() {
		return id >= 0 && bonuses != null && bonuses.length > 0;
	}

	/**
	 * Check if this item has any non-zero bonuses
	 *
	 * @return true if any bonus is non-zero
	 */
	public boolean hasAnyBonuses() {
		if (bonuses == null) {
			return false;
		}

		for (short bonus : bonuses) {
			if (bonus != 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Initialize bonuses array with a specific size
	 * Useful for XML parsers that need to set individual bonus values
	 *
	 * @param size the size of the bonuses array
	 */
	public void initializeBonuses(int size) {
		if (size > 0) {
			this.bonuses = new short[size];
		}
	}

	/**
	 * Copy constructor
	 *
	 * @param other the definition to copy from
	 */
	public ItemBonusDefinition(ItemBonusDefinition other) {
		if (other != null) {
			this.id = other.id;
			this.bonuses = other.bonuses != null ?
				Arrays.copyOf(other.bonuses, other.bonuses.length) : null;
		}
	}

	/**
	 * Create a copy of this definition
	 *
	 * @return a new ItemBonusDefinition with the same data
	 */
	public ItemBonusDefinition copy() {
		return new ItemBonusDefinition(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;

		ItemBonusDefinition that = (ItemBonusDefinition) obj;
		return id == that.id && Arrays.equals(bonuses, that.bonuses);
	}

	@Override
	public int hashCode() {
		int result = Integer.hashCode(id);
		result = 31 * result + Arrays.hashCode(bonuses);
		return result;
	}

	@Override
	public String toString() {
		return String.format("ItemBonusDefinition{id=%d, bonuses=%s}",
			id, Arrays.toString(bonuses));
	}

	/**
	 * Get a formatted string representation
	 *
	 * @param bonusNames optional array of bonus names for better formatting
	 * @return formatted string
	 */
	public String toFormattedString(String[] bonusNames) {
		if (!isValid()) {
			return "ItemBonusDefinition{invalid}";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Item ").append(id).append(": ");

		boolean hasAny = false;
		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] != 0) {
				if (hasAny) {
					sb.append(", ");
				}

				String bonusName = (bonusNames != null && i < bonusNames.length)
					? bonusNames[i]
					: "bonus" + i;

				sb.append(bonusName).append("=");
				if (bonuses[i] > 0) {
					sb.append("+");
				}
				sb.append(bonuses[i]);
				hasAny = true;
			}
		}

		if (!hasAny) {
			sb.append("no bonuses");
		}

		return sb.toString();
	}

	/**
	 * Validate the definition and return validation errors
	 *
	 * @return null if valid, error message if invalid
	 */
	public String validate() {
		if (id < 0) {
			return "Item ID cannot be negative: " + id;
		}

		if (bonuses == null) {
			return "Bonuses array is null for item " + id;
		}

		if (bonuses.length == 0) {
			return "Bonuses array is empty for item " + id;
		}

		if (bonuses.length > 50) {  // Reasonable upper limit
			return "Bonuses array too large (" + bonuses.length + ") for item " + id;
		}

		// Check for extreme values that might indicate data corruption
		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] < -32767 || bonuses[i] > 32767) {
				return "Bonus value out of range at index " + i + " for item " + id + ": " + bonuses[i];
			}
		}

		return null; // Valid
	}

	/**
	 * Builder pattern for creating ItemBonusDefinition instances
	 */
	public static class Builder {
		private int id = -1;
		private short[] bonuses;

		public Builder() {
			this.bonuses = new short[ItemBonusManager.EXPECTED_BONUS_COUNT];
		}

		public Builder(int bonusCount) {
			this.bonuses = new short[Math.max(1, bonusCount)];
		}

		public Builder setId(int id) {
			this.id = id;
			return this;
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

		public Builder setAssaultBonuses(short stab, short slash, short crush, short mage, short range) {
			setBonus(0, stab);
			setBonus(1, slash);
			setBonus(2, crush);
			setBonus(3, mage);
			setBonus(4, range);
			return this;
		}

		public Builder setDefenseBonuses(short stab, short slash, short crush, short mage, short range) {
			setBonus(5, stab);
			setBonus(6, slash);
			setBonus(7, crush);
			setBonus(8, mage);
			setBonus(9, range);
			return this;
		}

		public Builder setStrengthBonus(short strength) {
			setBonus(10, strength);
			return this;
		}

		public Builder setPrayerBonus(short prayer) {
			setBonus(11, prayer);
			return this;
		}

		public ItemBonusDefinition build() {
			return new ItemBonusDefinition(id, bonuses);
		}
	}
}