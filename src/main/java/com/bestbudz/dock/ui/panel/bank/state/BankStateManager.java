package com.bestbudz.dock.ui.panel.bank.state;

import com.bestbudz.ui.RSInterface;
import java.util.Arrays;

/**
 * Manages bank and inventory state tracking for efficient change detection
 */
public class BankStateManager {

	private int[] lastBankStateHash = null;
	private int[] lastInventoryStateHash = null;
	private int lastItemCount = -1;
	private int lastInventoryItemCount = -1;

	/**
	 * Checks if inventory contents have changed since last check
	 */
	public boolean hasInventoryChanged() {
		try {
			RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
			if (inventoryInterface == null || inventoryInterface.inv == null) {
				return lastInventoryStateHash != null;
			}

			int[] currentHash = computeStateHash(inventoryInterface.inv, inventoryInterface.invStackSizes);

			if (lastInventoryStateHash == null || !Arrays.equals(lastInventoryStateHash, currentHash)) {
				lastInventoryStateHash = currentHash;
				return true;
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * Checks if bank contents have changed since last check
	 */
	public boolean hasBankChanged() {
		try {
			RSInterface bankInterface = RSInterface.interfaceCache[5382];
			if (bankInterface == null || bankInterface.inv == null) {
				return lastBankStateHash != null;
			}

			int[] currentHash = computeStateHash(bankInterface.inv, bankInterface.invStackSizes);

			if (lastBankStateHash == null || !Arrays.equals(lastBankStateHash, currentHash)) {
				lastBankStateHash = currentHash;
				return true;
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * Checks if item counts have changed
	 */
	public boolean hasItemCountChanged() {
		RSInterface bankInterface = RSInterface.interfaceCache[5382];
		RSInterface inventoryInterface = RSInterface.interfaceCache[3214];

		int bankItemCount = countItems(bankInterface);
		int inventoryItemCount = countItems(inventoryInterface);

		boolean changed = bankItemCount != lastItemCount || inventoryItemCount != lastInventoryItemCount;

		if (changed) {
			lastItemCount = bankItemCount;
			lastInventoryItemCount = inventoryItemCount;
		}

		return changed;
	}

	/**
	 * Gets current bank item count
	 */
	public int getBankItemCount() {
		RSInterface bankInterface = RSInterface.interfaceCache[5382];
		return countItems(bankInterface);
	}

	/**
	 * Gets current inventory item count
	 */
	public int getInventoryItemCount() {
		RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
		return countItems(inventoryInterface);
	}

	/**
	 * Resets all state tracking (call when logging out)
	 */
	public void reset() {
		lastBankStateHash = null;
		lastInventoryStateHash = null;
		lastItemCount = -1;
		lastInventoryItemCount = -1;
	}

	/**
	 * Forces next check to detect changes (useful after actions)
	 */
	public void invalidate() {
		lastBankStateHash = null;
		lastInventoryStateHash = null;
	}

	/**
	 * Computes a hash of the state for efficient comparison
	 */
	private int[] computeStateHash(int[] items, int[] amounts) {
		if (items == null || amounts == null) {
			return new int[0];
		}

		int maxIndex = Math.min(items.length, amounts.length);
		int[] hash = new int[maxIndex];

		for (int i = 0; i < maxIndex; i++) {
			// Combine item ID and amount into a single hash value
			hash[i] = items[i] * 31 + amounts[i];
		}

		return hash;
	}

	/**
	 * Counts non-empty items in an interface
	 */
	private int countItems(RSInterface itemInterface) {
		if (itemInterface == null || itemInterface.inv == null || itemInterface.invStackSizes == null) {
			return 0;
		}

		int count = 0;
		int maxIndex = Math.min(itemInterface.inv.length, itemInterface.invStackSizes.length);

		for (int i = 0; i < maxIndex; i++) {
			if (itemInterface.inv[i] > 0 && itemInterface.invStackSizes[i] > 0) {
				count++;
			}
		}

		return count;
	}
}