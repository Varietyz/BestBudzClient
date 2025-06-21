package com.bestbudz.dock.definitions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Map;
import java.util.Arrays;

/**
 * Thread-safe singleton manager for item equipment bonuses
 * Provides fast O(1) lookup for item bonus data loaded from XML definitions
 */
public final class ItemBonusManager {

	// Singleton instance
	private static volatile ItemBonusManager instance;
	private static final Object INIT_LOCK = new Object();

	// Thread safety
	private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final AtomicBoolean initialized = new AtomicBoolean(false);

	// Data storage
	private final Map<Integer, ItemBonusData> bonusCache = new ConcurrentHashMap<>();
	private volatile String[] bonusNames;
	private volatile int loadedItemCount = 0;
	private volatile long initializationTime = 0;

	// Constants
	public static final int EXPECTED_BONUS_COUNT = 13;
	public static final String XML_RESOURCE_PATH = "/definitions/items/ItemBonusDefinitions.xml";

	// Default bonus names (matching server-side EquipmentConstants.BONUS_NAMES)
	private static final String[] DEFAULT_BONUS_NAMES = {
		"Stab", "Slash", "Crush", "Mage", "Sagittarius", // Assault
		"Stab", "Slash", "Crush", "Mage", "Sagittarius", // Aegis
		"Vigour", "Resonance", "Mage Damage"
	};

	/**
	 * Private constructor for singleton pattern
	 */
	private ItemBonusManager() {
		this.bonusNames = Arrays.copyOf(DEFAULT_BONUS_NAMES, DEFAULT_BONUS_NAMES.length);
	}

	/**
	 * Get singleton instance with thread-safe lazy initialization
	 */
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

	/**
	 * Initialize the bonus manager - should be called during application startup
	 * This method is idempotent and thread-safe
	 *
	 * @return true if initialization was successful, false otherwise
	 */
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

	/**
	 * Internal initialization logic
	 */
	private boolean performInitialization() {
		// Check if already initialized
		if (initialized.get()) {
			System.out.println("[ItemBonusManager] Already initialized, skipping");
			return true;
		}

		lock.writeLock().lock();
		try {
			// Double-check after acquiring write lock
			if (initialized.get()) {
				return true;
			}

			// Clear any existing data
			bonusCache.clear();
			loadedItemCount = 0;

			// Load bonus definitions from XML
			ItemBonusXMLLoader loader = new ItemBonusXMLLoader();
			ItemBonusLoadResult result = loader.loadFromResource(XML_RESOURCE_PATH);

			if (!result.isSuccess()) {
				System.err.println("[ItemBonusManager] Failed to load XML: " + result.getErrorMessage());
				return false;
			}

			// Process loaded definitions
			for (ItemBonusDefinition definition : result.getDefinitions()) {
				if (isValidDefinition(definition)) {
					ItemBonusData bonusData = new ItemBonusData(definition);
					bonusCache.put(definition.getId(), bonusData);
					loadedItemCount++;
				} else {
					System.err.println("[ItemBonusManager] ⚠️ Skipping invalid definition for item ID: " + definition.getId());
				}
			}

			// Mark as initialized
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

	/**
	 * Get equipment bonuses for a specific item ID
	 *
	 * @param itemId the item ID to look up
	 * @return array of bonus values, or null if item has no bonuses
	 */
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

	/**
	 * Check if an item has any non-zero equipment bonuses
	 *
	 * @param itemId the item ID to check
	 * @return true if item has equipment bonuses, false otherwise
	 */
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

	/**
	 * Get the names of all bonus types
	 *
	 * @return array of bonus names
	 */
	public static String[] getBonusNames() {
		ItemBonusManager manager = getInstance();
		return Arrays.copyOf(manager.bonusNames, manager.bonusNames.length);
	}

	/**
	 * Get a specific bonus value for an item
	 *
	 * @param itemId the item ID
	 * @param bonusIndex the index of the bonus (0-based)
	 * @return the bonus value, or 0 if not found or invalid index
	 */
	public static short getBonus(int itemId, int bonusIndex) {
		short[] bonuses = getBonuses(itemId);
		if (bonuses == null || bonusIndex < 0 || bonusIndex >= bonuses.length) {
			return 0;
		}
		return bonuses[bonusIndex];
	}

	/**
	 * Check if the bonus manager is initialized and ready
	 *
	 * @return true if initialized, false otherwise
	 */
	public static boolean isInitialized() {
		return getInstance().initialized.get();
	}

	/**
	 * Get statistics about the loaded bonus data
	 *
	 * @return formatted statistics string
	 */
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

	/**
	 * Get the number of loaded item definitions
	 *
	 * @return count of loaded items
	 */
	public static int getLoadedItemCount() {
		return getInstance().loadedItemCount;
	}

	/**
	 * Reload bonus definitions from XML (for development/testing)
	 * This will clear existing data and reload from the XML file
	 *
	 * @return true if reload was successful
	 */
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

	/**
	 * Clear all loaded data and reset the manager
	 */
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

	/**
	 * Validate a bonus definition
	 */
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

		// Allow different bonus array sizes for flexibility
		return bonuses.length > 0 && bonuses.length <= 20;
	}

	/**
	 * Estimate memory usage of the bonus cache
	 */
	private static long estimateMemoryUsage() {
		ItemBonusManager manager = getInstance();
		// Rough estimate: each ItemBonusData object ~100 bytes + array overhead
		return (long) manager.bonusCache.size() * 100;
	}

}