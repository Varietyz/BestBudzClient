package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.engine.core.Client;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class EquipmentClickBatcher {

	private static final Object clickBatchLock = new Object();
	private static Timer batchTimer = null;
	private static final Map<Integer, Runnable> pendingClicks = new LinkedHashMap<>();

	private final Runnable onBatchComplete;

	public EquipmentClickBatcher(Runnable onBatchComplete) {
		this.onBatchComplete = onBatchComplete;
	}

	public void batchUnequip(int slotId, int equipmentSlot, int itemId) {
		synchronized (clickBatchLock) {

			if (pendingClicks.containsKey(slotId)) {
				return;
			}

			pendingClicks.put(slotId, () -> executeUnequip(equipmentSlot, itemId));

			if (batchTimer == null || !batchTimer.isRunning()) {
				batchTimer = new Timer(EquipmentConstants.BATCH_WINDOW_MS, e -> {
					executeBatch();
				});
				batchTimer.setRepeats(false);
				batchTimer.start();
			}

		}
	}

	private void executeBatch() {
		synchronized (clickBatchLock) {

			if (!pendingClicks.isEmpty()) {
				System.out.println("Executing batch of " + pendingClicks.size() + " equipment changes: " + pendingClicks.keySet());

				for (Runnable click : pendingClicks.values()) {
					click.run();
				}

				pendingClicks.clear();

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
				Client.stream.writeEncryptedOpcode(145);
				Client.stream.writeWordMixed(EquipmentConstants.EQUIPMENT_INTERFACE_ID);
				Client.stream.writeWordMixed(equipmentSlot);
				Client.stream.writeWordMixed(itemId);
			}
		} catch (Exception e) {
			System.err.println("Error executing unequip: " + e.getMessage());
		}
	}
}
