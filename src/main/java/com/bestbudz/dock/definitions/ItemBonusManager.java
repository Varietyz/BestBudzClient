package com.bestbudz.dock.definitions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import java.util.Arrays;

public final class ItemBonusManager {

	private static volatile ItemBonusManager instance;
	private static final Object INIT_LOCK = new Object();

	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final AtomicBoolean initialized = new AtomicBoolean(false);

	private final Map<Integer, ItemBonusData> bonusCache = new ConcurrentHashMap<>();
	private volatile String[] bonusNames;
	private volatile int loadedItemCount = 0;
	private volatile long initializationTime = 0;

	public static final int EXPECTED_BONUS_COUNT = 13;
	public static final String XML_RESOURCE_PATH = "/definitions/items/ItemBonusDefinitions.xml";

	private static final String[] DEFAULT_BONUS_NAMES = {
		"Stab", "Slash", "Crush", "Mage", "Sagittarius",
		"Stab", "Slash", "Crush", "Mage", "Sagittarius",
		"Vigour", "Resonance", "Mage Damage"
	};

	private ItemBonusManager() {
		this.bonusNames = Arrays.copyOf(DEFAULT_BONUS_NAMES, DEFAULT_BONUS_NAMES.length);
	}

	public static ItemBonusManager getInstance() {
		if (instance == null) {
			synchronized (INIT_LOCK) {
				if (instance == null) {
					instance = new ItemBonusManager();
				}
			}
		}
		return instance;
	}

	public static boolean initialize() {
		try {
			System.out.println("[ItemBonusManager] Starting initialization...");
			long startTime = System.currentTimeMillis();

			ItemBonusManager manager = getInstance();
			boolean success = manager.performInitialization();

			long duration = System.currentTimeMillis() - startTime;
			if (success) {
				System.out.println("[ItemBonusManager] ✅ Initialized successfully in " + duration + "ms");
				System.out.println("[ItemBonusManager] Loaded " + manager.loadedItemCount + " item bonus definitions");
			} else {
				System.err.println("[ItemBonusManager] ❌ Initialization failed after " + duration + "ms");
			}

			return success;

		} catch (Exception e) {
			System.err.println("[ItemBonusManager] ❌ Critical error during initialization: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private boolean performInitialization() {

		if (initialized.get()) {
			System.out.println("[ItemBonusManager] Already initialized, skipping");
			return true;
		}

		lock.writeLock().lock();
		try {

			if (initialized.get()) {
				return true;
			}

			bonusCache.clear();
			loadedItemCount = 0;

			ItemBonusXMLLoader loader = new ItemBonusXMLLoader();
			ItemBonusLoadResult result = loader.loadFromResource(XML_RESOURCE_PATH);

			if (!result.isSuccess()) {
				System.err.println("[ItemBonusManager] Failed to load XML: " + result.getErrorMessage());
				return false;
			}

			for (ItemBonusDefinition definition : result.getDefinitions()) {
				if (isValidDefinition(definition)) {
					ItemBonusData bonusData = new ItemBonusData(definition);
					bonusCache.put(definition.getId(), bonusData);
					loadedItemCount++;
				} else {
					System.err.println("[ItemBonusManager] ⚠️ Skipping invalid definition for item ID: " + definition.getId());
				}
			}

			initializationTime = System.currentTimeMillis();
			initialized.set(true);

			return true;

		} catch (Exception e) {
			System.err.println("[ItemBonusManager] Error during initialization: " + e.getMessage());
			e.printStackTrace();
			return false;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public static short[] getBonuses(int itemId) {
		ItemBonusManager manager = getInstance();
		if (!manager.initialized.get()) {
			System.err.println("[ItemBonusManager] ⚠️ Manager not initialized, returning null for item " + itemId);
			return null;
		}

		manager.lock.readLock().lock();
		try {
			ItemBonusData bonusData = manager.bonusCache.get(itemId);
			return bonusData != null ? bonusData.getBonuses() : null;

		} finally {
			manager.lock.readLock().unlock();
		}
	}

	public static boolean hasEquipmentBonuses(int itemId) {
		short[] bonuses = getBonuses(itemId);
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

	public static String[] getBonusNames() {
		ItemBonusManager manager = getInstance();
		return Arrays.copyOf(manager.bonusNames, manager.bonusNames.length);
	}

	public static short getBonus(int itemId, int bonusIndex) {
		short[] bonuses = getBonuses(itemId);
		if (bonuses == null || bonusIndex < 0 || bonusIndex >= bonuses.length) {
			return 0;
		}
		return bonuses[bonusIndex];
	}

	public static boolean isInitialized() {
		return getInstance().initialized.get();
	}

	public static String getStatistics() {
		ItemBonusManager manager = getInstance();
		if (!manager.initialized.get()) {
			return "ItemBonusManager: Not initialized";
		}

		manager.lock.readLock().lock();
		try {
			long uptime = System.currentTimeMillis() - manager.initializationTime;
			return String.format("ItemBonusManager: %d items loaded, uptime %dms, memory usage ~%dKB",
				manager.loadedItemCount, uptime, estimateMemoryUsage() / 1024);
		} finally {
			manager.lock.readLock().unlock();
		}
	}

	public static int getLoadedItemCount() {
		return getInstance().loadedItemCount;
	}

	public static boolean reload() {
		System.out.println("[ItemBonusManager] Reloading bonus definitions...");
		ItemBonusManager manager = getInstance();

		manager.lock.writeLock().lock();
		try {
			manager.initialized.set(false);
			return manager.performInitialization();
		} finally {
			manager.lock.writeLock().unlock();
		}
	}

	public static void shutdown() {
		System.out.println("[ItemBonusManager] Shutting down...");
		ItemBonusManager manager = getInstance();

		manager.lock.writeLock().lock();
		try {
			manager.bonusCache.clear();
			manager.loadedItemCount = 0;
			manager.initialized.set(false);
			System.out.println("[ItemBonusManager] ✅ Shutdown complete");
		} finally {
			manager.lock.writeLock().unlock();
		}
	}

	private boolean isValidDefinition(ItemBonusDefinition definition) {
		if (definition == null) {
			return false;
		}

		if (definition.getId() < 0) {
			return false;
		}

		short[] bonuses = definition.getBonuses();
		if (bonuses == null) {
			return false;
		}

		return bonuses.length > 0 && bonuses.length <= 20;
	}

	private static long estimateMemoryUsage() {
		ItemBonusManager manager = getInstance();

		return (long) manager.bonusCache.size() * 100;
	}

}
