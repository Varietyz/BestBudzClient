package com.bestbudz.dock.ui.panel.bank.state;

import javax.swing.Timer;

public class BankUpdateManager {

	private static final int UPDATE_INTERVAL_MS = 500;
	private static final int FAST_UPDATE_INTERVAL_MS = 100;
	private static final int FAST_MODE_DURATION_MS = 2000;

	private Timer updateTimer;
	private Timer fastUpdateModeTimer;
	private boolean isInFastUpdateMode = false;
	private Runnable updateCallback;

	public BankUpdateManager(Runnable updateCallback) {
		this.updateCallback = updateCallback;
	}

	public void startUpdates() {
		stopAllTimers();

		updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateCallback.run());
		updateTimer.start();
	}

	public void stopUpdates() {
		stopAllTimers();
	}

	public void enableFastUpdateMode() {
		if (!isInFastUpdateMode) {
			isInFastUpdateMode = true;

			if (updateTimer != null && updateTimer.isRunning()) {
				updateTimer.stop();
			}

			updateTimer = new Timer(FAST_UPDATE_INTERVAL_MS, e -> updateCallback.run());
			updateTimer.start();
		}

		if (fastUpdateModeTimer != null && fastUpdateModeTimer.isRunning()) {
			fastUpdateModeTimer.stop();
		}

		fastUpdateModeTimer = new Timer(FAST_MODE_DURATION_MS, e -> disableFastUpdateMode());
		fastUpdateModeTimer.setRepeats(false);
		fastUpdateModeTimer.start();
	}

	public void forceUpdate() {
		if (updateCallback != null) {
			updateCallback.run();
		}
	}

	public void scheduleUpdate(int delayMs) {
		Timer delayedUpdate = new Timer(delayMs, e -> updateCallback.run());
		delayedUpdate.setRepeats(false);
		delayedUpdate.start();
	}

	public void forceUpdateAfterAction() {
		enableFastUpdateMode();

		scheduleUpdate(50);

		scheduleUpdate(300);
	}

	public boolean isInFastUpdateMode() {
		return isInFastUpdateMode;
	}

	private void disableFastUpdateMode() {
		if (isInFastUpdateMode) {
			isInFastUpdateMode = false;

			if (updateTimer != null && updateTimer.isRunning()) {
				updateTimer.stop();
			}

			updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateCallback.run());
			updateTimer.start();
		}
	}

	private void stopAllTimers() {
		if (updateTimer != null && updateTimer.isRunning()) {
			updateTimer.stop();
		}

		if (fastUpdateModeTimer != null && fastUpdateModeTimer.isRunning()) {
			fastUpdateModeTimer.stop();
		}

		isInFastUpdateMode = false;
	}
}
