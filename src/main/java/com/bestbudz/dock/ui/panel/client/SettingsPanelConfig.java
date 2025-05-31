package com.bestbudz.dock.ui.panel.client;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.SettingHandler;

import javax.swing.Timer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Configuration manager for the Settings Panel.
 * Handles setting definitions, value management, and persistence.
 */
public class SettingsPanelConfig {

	private final Map<String, SettingDefinition> settings = new HashMap<>();
	private Timer saveTimer;
	private static final int SAVE_DELAY_MS = 750;

	/**
	 * Represents a single setting with its getter, setter, and optional change handler.
	 */
	public static class SettingDefinition {
		private final Supplier<Boolean> getter;
		private final Consumer<Boolean> setter;
		private final Runnable onChangeCallback;

		public SettingDefinition(Supplier<Boolean> getter, Consumer<Boolean> setter) {
			this(getter, setter, null);
		}

		public SettingDefinition(Supplier<Boolean> getter, Consumer<Boolean> setter, Runnable onChangeCallback) {
			this.getter = getter;
			this.setter = setter;
			this.onChangeCallback = onChangeCallback;
		}

		public boolean getValue() {
			return getter.get();
		}

		public void setValue(boolean value) {
			setter.accept(value);
			if (onChangeCallback != null) {
				onChangeCallback.run();
			}
		}
	}

	public SettingsPanelConfig() {
		initializeSettings();
	}

	/**
	 * Initialize all setting definitions with their getters, setters, and callbacks.
	 */
	private void initializeSettings() {
		// Graphics settings
		addSetting("Tweening",
			() -> SettingsConfig.enableTweening,
			value -> SettingsConfig.enableTweening = value);

		addSetting("Fog",
			() -> SettingsConfig.enableDistanceFog,
			value -> SettingsConfig.enableDistanceFog = value);

		addSetting("Mip Mapping",
			() -> SettingsConfig.enableMipMapping,
			value -> SettingsConfig.enableMipMapping = value);

		addSetting("Moving Textures",
			() -> SettingsConfig.enableMovingTextures,
			value -> SettingsConfig.enableMovingTextures = value);

		addSetting("Roofs",
			() -> SettingsConfig.enableRoofs,
			value -> SettingsConfig.enableRoofs = value);

		addSetting("Ground Decor",
			() -> SettingsConfig.enableGroundDecorations,
			value -> SettingsConfig.enableGroundDecorations = value,
			() -> Client.loadingStage = 1); // Requires reload

		addSetting("Flat Shading",
			() -> SettingsConfig.enableFlatShading,
			value -> SettingsConfig.enableFlatShading = value);

		// UI settings
		addSetting("Status Orbs",
			() -> SettingsConfig.enableStatusOrbs,
			value -> SettingsConfig.enableStatusOrbs = value);

		addSetting("Kill Feed",
			() -> SettingsConfig.showKillFeed,
			value -> SettingsConfig.showKillFeed = value);

		addSetting("Hover Menus",
			() -> SettingsConfig.menuHovers,
			value -> SettingsConfig.menuHovers = value);

		addSetting("Entity Feed",
			() -> SettingsConfig.drawEntityFeed,
			value -> SettingsConfig.drawEntityFeed = value);

		addSetting("HP Bars",
			() -> SettingsConfig.enableNewHpBars,
			value -> SettingsConfig.enableNewHpBars = value);

		addSetting("Hitmarkers",
			() -> SettingsConfig.enableNewHitmarks,
			value -> SettingsConfig.enableNewHitmarks = value);

		addSetting("Time Stamps",
			() -> SettingsConfig.enableTimeStamps,
			value -> SettingsConfig.enableTimeStamps = value);

		// Gameplay settings
		addSetting("Debit Card",
			() -> SettingsConfig.enablePouch,
			value -> SettingsConfig.enablePouch = value);

		addSetting("x10 Damage",
			() -> SettingsConfig.enable10xDamage,
			value -> SettingsConfig.enable10xDamage = value);

		addSetting("Attack Priority",
			() -> SettingsConfig.entityAttackPriority,
			value -> SettingsConfig.entityAttackPriority = value);
	}

	/**
	 * Add a setting definition without a change callback.
	 */
	private void addSetting(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
		settings.put(name, new SettingDefinition(getter, setter));
	}

	/**
	 * Add a setting definition with a change callback.
	 */
	private void addSetting(String name, Supplier<Boolean> getter, Consumer<Boolean> setter, Runnable onChangeCallback) {
		settings.put(name, new SettingDefinition(getter, setter, onChangeCallback));
	}

	/**
	 * Get all available setting names.
	 */
	public String[] getSettingNames() {
		return settings.keySet().toArray(new String[0]);
	}

	/**
	 * Get the current value of a setting.
	 */
	public boolean getSettingValue(String name) {
		SettingDefinition setting = settings.get(name);
		if (setting == null) {
			throw new IllegalArgumentException("Unknown setting: " + name);
		}
		return setting.getValue();
	}

	/**
	 * Set the value of a setting and trigger auto-save.
	 */
	public void setSettingValue(String name, boolean value) {
		SettingDefinition setting = settings.get(name);
		if (setting == null) {
			throw new IllegalArgumentException("Unknown setting: " + name);
		}

		setting.setValue(value);
		startAutoSaveTimer();
	}

	/**
	 * Check if a setting exists.
	 */
	public boolean hasSetting(String name) {
		return settings.containsKey(name);
	}

	/**
	 * Start the debounced auto-save timer.
	 */
	private void startAutoSaveTimer() {
		if (saveTimer != null) {
			saveTimer.stop();
		}

		saveTimer = new Timer(SAVE_DELAY_MS, e -> {
			SettingHandler.save();
			saveTimer.stop();
		});
		saveTimer.setRepeats(false);
		saveTimer.start();
	}

	/**
	 * Force immediate save of settings.
	 */
	public void saveImmediately() {
		if (saveTimer != null) {
			saveTimer.stop();
		}
		SettingHandler.save();
	}
}