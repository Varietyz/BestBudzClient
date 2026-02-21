package com.bestbudz.util;

import java.util.LinkedHashMap;
import java.util.Map;

public final class LRUCache<V> {

	private final LinkedHashMap<Long, V> map;

	public LRUCache(int capacity) {
		map = new LinkedHashMap<Long, V>(capacity, 0.75f, true) {
			@Override
			protected boolean removeEldestEntry(Map.Entry<Long, V> eldest) {
				return size() > capacity;
			}
		};
	}

	public V get(long key) {
		return map.get(key);
	}

	public void put(long key, V value) {
		map.put(key, value);
	}

	public void remove(long key) {
		map.remove(key);
	}

	public void clear() {
		map.clear();
	}
}
