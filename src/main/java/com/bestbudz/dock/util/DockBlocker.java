package com.bestbudz.dock.util;

import java.util.HashSet;
import java.util.Set;

public class DockBlocker {

	private static final Set<Integer> blockedIDs = new HashSet<>();

	public static void register(int interfaceId) {
		blockedIDs.add(interfaceId);
		System.out.println("[DockBlocker] Blocking RSInterface ID: " + interfaceId);
	}

	public static void registerAll(int... ids) {
		for (int id : ids) {
			register(id);
		}
	}

	public static boolean isDocked(int interfaceId) {
		return blockedIDs.contains(interfaceId);
	}

	public static void clear() {
		blockedIDs.clear();
	}
}
