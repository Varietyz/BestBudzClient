package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.engine.core.Client;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Handles batching of equipment clicks for tick-accurate switching
 */
public class EquipmentClickBatcher {

	private static final Object clickBatchLock = new Object();
	private static Timer batchTimer = null;
	private static final Map<Integer, Runnable> pendingClicks = new LinkedHashMap<>();

	private final Runnable onBatchComplete;

	public EquipmentClickBatcher(Runnable onBatchComplete) {
		this.onBatchComplete = onBatchComplete;
	}

	/**
	 * Batch an unequip action for the specified slot
	 */
	public void batchUnequip(int slotId, int equipmentSlot, int itemId) {
		synchronized (clickBatchLock) {
			// If this slot is already in the batch, do nothing (no visual feedback)
			if (pendingClicks.containsKey(slotId)) {
				return; // Silent ignore - slot already batched
			}

			// Add this click to the pending batch
			pendingClicks.put(slotId, () -> executeUnequip(equipmentSlot, itemId));

			// If no timer is running, start the shared batch window
			if (batchTimer == null || !batchTimer.isRunning()) {
				batchTimer = new Timer(EquipmentConstants.BATCH_WINDOW_MS, e -> {
					executeBatch();
				});
				batchTimer.setRepeats(false);
				batchTimer.start();
			}
			// If timer is already running, this new slot just gets added to the existing batch
		}
	}

	private void executeBatch() {
		synchronized (clickBatchLock) {
			// Execute all batched clicks at once
			if (!pendingClicks.isEmpty()) {
				System.out.println("Executing batch of " + pendingClicks.size() + " equipment changes: " + pendingClicks.keySet());

				// Execute all pending clicks in rapid succession (same tick)
				for (Runnable click : pendingClicks.values()) {
					click.run();
				}

				// Clear the batch
				pendingClicks.clear();

				// Schedule UI update after all packets sent
				if (onBatchComplete != null) {
					SwingUtilities.invokeLater(() -> {
						Timer updateTimer = new Timer(EquipmentConstants.UI_UPDATE_DELAY_MS, updateEvent -> {
							onBatchComplete.run();
						});
						updateTimer.setRepeats(false);
						updateTimer.start();
					});
				}
			}
		}
	}

	private void executeUnequip(int equipmentSlot, int itemId) {
		try {
			if (Client.stream != null) {
				Client.stream.createFrame(145);
				Client.stream.method432(EquipmentConstants.EQUIPMENT_INTERFACE_ID); // Interface 1688
				Client.stream.method432(equipmentSlot);  // Equipment slot
				Client.stream.method432(itemId);         // Item ID
			}
		} catch (Exception e) {
			System.err.println("Error executing unequip: " + e.getMessage());
		}
	}
}