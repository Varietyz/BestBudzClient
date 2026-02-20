package com.bestbudz.dock.definitions;

import java.util.Arrays;

public class ItemBonusDefinition {

	private int id;
	private short[] bonuses;

	public ItemBonusDefinition() {
		this.id = -1;
		this.bonuses = null;
	}

	public ItemBonusDefinition(int id, short[] bonuses) {
		this.id = id;
		this.bonuses = bonuses != null ? Arrays.copyOf(bonuses, bonuses.length) : null;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public short[] getBonuses() {
		return bonuses != null ? Arrays.copyOf(bonuses, bonuses.length) : null;
	}

	public void setBonuses(short[] bonuses) {
		this.bonuses = bonuses != null ? Arrays.copyOf(bonuses, bonuses.length) : null;
	}

	public short getBonus(int index) {
		if (bonuses == null || index < 0 || index >= bonuses.length) {
			return 0;
		}
		return bonuses[index];
	}

	public void setBonus(int index, short value) {
		if (bonuses != null && index >= 0 && index < bonuses.length) {
			bonuses[index] = value;
		}
	}

	public int getBonusCount() {
		return bonuses != null ? bonuses.length : 0;
	}

	public boolean isValid() {
		return id >= 0 && bonuses != null && bonuses.length > 0;
	}

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

	public void initializeBonuses(int size) {
		if (size > 0) {
			this.bonuses = new short[size];
		}
	}

	public ItemBonusDefinition(ItemBonusDefinition other) {
		if (other != null) {
			this.id = other.id;
			this.bonuses = other.bonuses != null ?
				Arrays.copyOf(other.bonuses, other.bonuses.length) : null;
		}
	}

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

		if (bonuses.length > 50) {
			return "Bonuses array too large (" + bonuses.length + ") for item " + id;
		}

		for (int i = 0; i < bonuses.length; i++) {
			if (bonuses[i] < -32767 || bonuses[i] > 32767) {
				return "Bonus value out of range at index " + i + " for item " + id + ": " + bonuses[i];
			}
		}

		return null;
	}

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
