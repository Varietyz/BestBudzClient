package com.bestbudz.dock.definitions;

import java.util.Arrays;

public final class ItemBonusData {

	private final int itemId;
	private final short[] bonuses;
	private final boolean hasAnyBonuses;
	private final int hashCode;

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

		this.bonuses = Arrays.copyOf(definition.getBonuses(), definition.getBonuses().length);

		this.hasAnyBonuses = calculateHasAnyBonuses();

		this.hashCode = calculateHashCode();
	}

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

	public int getItemId() {
		return itemId;
	}

	public short[] getBonuses() {
		return Arrays.copyOf(bonuses, bonuses.length);
	}

	public short getBonus(int index) {
		if (index < 0 || index >= bonuses.length) {
			return 0;
		}
		return bonuses[index];
	}

	public int getBonusCount() {
		return bonuses.length;
	}

	public boolean hasAnyBonuses() {
		return hasAnyBonuses;
	}

	public short[] getAssaultBonuses() {
		return getBonusRange(0, Math.min(5, bonuses.length));
	}

	public short[] getDefenseBonuses() {
		int start = Math.min(5, bonuses.length);
		int end = Math.min(10, bonuses.length);
		return getBonusRange(start, end);
	}

	public short[] getOtherBonuses() {
		int start = Math.min(10, bonuses.length);
		return getBonusRange(start, bonuses.length);
	}

	public short[] getBonusRange(int startIndex, int endIndex) {
		if (startIndex < 0) startIndex = 0;
		if (endIndex > bonuses.length) endIndex = bonuses.length;
		if (startIndex >= endIndex) return new short[0];

		return Arrays.copyOfRange(bonuses, startIndex, endIndex);
	}

	public boolean hasBonusAt(int index) {
		return getBonus(index) != 0;
	}

	public int getTotalPositiveBonuses() {
		int total = 0;
		for (short bonus : bonuses) {
			if (bonus > 0) {
				total += bonus;
			}
		}
		return total;
	}

	public int getTotalNegativeBonuses() {
		int total = 0;
		for (short bonus : bonuses) {
			if (bonus < 0) {
				total += bonus;
			}
		}
		return total;
	}

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

	private boolean calculateHasAnyBonuses() {
		for (short bonus : bonuses) {
			if (bonus != 0) {
				return true;
			}
		}
		return false;
	}

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
