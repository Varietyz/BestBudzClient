package com.bestbudz.dock.ui.panel.bank;

import com.bestbudz.data.ItemDef;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple ItemDef cache - ONLY caches item definitions, nothing else
 */
public class ItemDefCache {

	private static final ConcurrentHashMap<Integer, CachedItemDef> cache = new ConcurrentHashMap<>();
	private static final CachedItemDef UNKNOWN_ITEM = new CachedItemDef("Unknown Item", 1, false);

	public static class CachedItemDef {
		public final String name;
		public final int value;
		public final boolean stackable;

		public CachedItemDef(String name, int value, boolean stackable) {
			this.name = name;
			this.value = value;
			this.stackable = stackable;
		}
	}

	/**
	 * Get cached item definition. Loads if not cached.
	 */
	public static CachedItemDef get(int itemId) {
		return cache.computeIfAbsent(itemId, ItemDefCache::load);
	}

	private static CachedItemDef load(int itemId) {
		try {
			ItemDef def = ItemDef.getItemDefinition(itemId);
			if (def != null) {
				return new CachedItemDef(
					def.name != null ? def.name : "Unknown Item",
					Math.max(1, def.value),
					def.stackable
				);
			}
		} catch (Exception e) {
			// Ignore errors, return unknown
		}
		return UNKNOWN_ITEM;
	}

	public static void clear() {
		cache.clear();
	}
}