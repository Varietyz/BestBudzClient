package com.bestbudz.dock.ui.panel.bank.state;

import javax.swing.Timer;

/**
 * Manages update timing and coordination for bank panel
 */
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

	/**
	 * Starts normal update cycle
	 */
	public void startUpdates() {
		stopAllTimers();

		updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateCallback.run());
		updateTimer.start();
	}

	/**
	 * Stops all update timers
	 */
	public void stopUpdates() {
		stopAllTimers();
	}

	/**
	 * Enables fast update mode for responsive UI after user actions
	 */
	public void enableFastUpdateMode() {
		if (!isInFastUpdateMode) {
			isInFastUpdateMode = true;

			// Stop normal timer and start fast timer
			if (updateTimer != null && updateTimer.isRunning()) {
				updateTimer.stop();
			}

			updateTimer = new Timer(FAST_UPDATE_INTERVAL_MS, e -> updateCallback.run());
			updateTimer.start();
		}

		// Reset the fast mode timer
		if (fastUpdateModeTimer != null && fastUpdateModeTimer.isRunning()) {
			fastUpdateModeTimer.stop();
		}

		fastUpdateModeTimer = new Timer(FAST_MODE_DURATION_MS, e -> disableFastUpdateMode());
		fastUpdateModeTimer.setRepeats(false);
		fastUpdateModeTimer.start();
	}

	/**
	 * Forces an immediate update
	 */
	public void forceUpdate() {
		if (updateCallback != null) {
			updateCallback.run();
		}
	}

	/**
	 * Schedules a delayed update
	 */
	public void scheduleUpdate(int delayMs) {
		Timer delayedUpdate = new Timer(delayMs, e -> updateCallback.run());
		delayedUpdate.setRepeats(false);
		delayedUpdate.start();
	}

	/**
	 * Enables fast updates and schedules multiple refresh points
	 */
	public void forceUpdateAfterAction() {
		enableFastUpdateMode();

		// Immediate refresh
		scheduleUpdate(50);

		// Server response refresh
		scheduleUpdate(300);
	}

	/**
	 * Checks if currently in fast update mode
	 */
	public boolean isInFastUpdateMode() {
		return isInFastUpdateMode;
	}

	private void disableFastUpdateMode() {
		if (isInFastUpdateMode) {
			isInFastUpdateMode = false;

			// Stop fast timer and restart normal timer
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