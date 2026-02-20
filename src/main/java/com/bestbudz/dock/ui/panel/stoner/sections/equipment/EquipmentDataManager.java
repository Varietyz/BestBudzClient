package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;

import javax.swing.*;

public class EquipmentDataManager {

	private long lastEquipmentHash = -1;
	private Timer updateTimer;
	private final EquipmentUpdateListener listener;

	public interface EquipmentUpdateListener {
		void onEquipmentChanged();
		void onEquipmentCleared();
	}

	public EquipmentDataManager(EquipmentUpdateListener listener) {
		this.listener = listener;
	}

	public void startAutoUpdate() {
		if (updateTimer == null || !updateTimer.isRunning()) {
			updateTimer = new Timer(EquipmentConstants.UPDATE_INTERVAL_MS, e -> {
				if (Client.loggedIn && Client.myStoner != null && hasEquipmentChanged()) {
					SwingUtilities.invokeLater(() -> {
						if (listener != null) {
							listener.onEquipmentChanged();
						}
					});
				}
			});
			updateTimer.start();
		}
	}

	public void stopAutoUpdate() {
		if (updateTimer != null && updateTimer.isRunning()) {
			updateTimer.stop();
		}
	}

	public void forceUpdate() {
		lastEquipmentHash = -1;
		if (listener != null) {
			listener.onEquipmentChanged();
		}
	}

	public boolean hasEquipmentChanged() {
		if (Client.myStoner == null || !Client.myStoner.visible) {
			if (lastEquipmentHash != -1) {
				lastEquipmentHash = -1;
				return true;
			}
			return false;
		}

		long currentHash = computeEquipmentHash(Client.myStoner.equipment);

		if (lastEquipmentHash != currentHash) {
			lastEquipmentHash = currentHash;
			return true;
		}

		return false;
	}

	private long computeEquipmentHash(int[] equipment) {
		long hash = 0;

		try {
			RSInterface equipmentInterface = RSInterface.interfaceCache[EquipmentConstants.EQUIPMENT_INTERFACE_ID];
			if (equipmentInterface != null && equipmentInterface.inv != null) {

				for (int i = 0; i < Math.min(equipmentInterface.inv.length, 14); i++) {
					hash = hash * 31 + equipmentInterface.inv[i];

					if (equipmentInterface.invStackSizes != null && i < equipmentInterface.invStackSizes.length) {
						hash = hash * 37 + equipmentInterface.invStackSizes[i];
					}
				}
			} else {

				for (int i = 0; i < Math.min(equipment.length, 14); i++) {
					hash = hash * 31 + equipment[i];
				}
			}
		} catch (Exception e) {

			for (int i = 0; i < Math.min(equipment.length, 14); i++) {
				hash = hash * 31 + equipment[i];
			}
		}

		return hash;
	}

	public boolean isEquipmentAvailable() {
		return Client.loggedIn && Client.myStoner != null && Client.myStoner.visible;
	}

	public RSInterface getEquipmentInterface() {
		try {
			return RSInterface.interfaceCache[EquipmentConstants.EQUIPMENT_INTERFACE_ID];
		} catch (Exception e) {
			return null;
		}
	}
}
