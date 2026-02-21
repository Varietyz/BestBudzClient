package com.bestbudz.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.ref.SoftReference;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public final class JsonModelLoader {

	private static final SoftReference<?>[] cache = new SoftReference[80000];
	private static final Set<Integer> knownIds = new HashSet<>();
	private static boolean initialized = false;

	public static void initialize() {
		if (initialized) return;
		JsonObject index = JsonCacheLoader.loadJsonObject("models/_index.json");
		if (index != null) {
			for (String key : index.keySet()) {
				try {
					knownIds.add(Integer.parseInt(key));
				} catch (NumberFormatException ignored) {}
			}
		}
		initialized = true;
		System.out.println("[JsonModelLoader] Initialized with " + knownIds.size() + " model IDs");
	}

	public static boolean exists(int modelId) {
		return knownIds.contains(modelId);
	}

	public static JsonObject loadModelJson(int modelId) {
		// Check soft cache
		if (modelId >= 0 && modelId < cache.length) {
			SoftReference<?> ref = cache[modelId];
			if (ref != null) {
				Object obj = ref.get();
				if (obj instanceof JsonObject) {
					return (JsonObject) obj;
				}
			}
		}

		// Load from file
		JsonObject json = JsonCacheLoader.loadJsonObject("models/" + modelId + ".json");
		if (json != null && modelId >= 0 && modelId < cache.length) {
			cache[modelId] = new SoftReference<>(json);
		}
		return json;
	}

	public static byte[] getRawBytes(JsonObject json) {
		if (json.has("rawBase64")) {
			String b64 = json.get("rawBase64").getAsString();
			return Base64.getDecoder().decode(b64);
		}
		return null;
	}

	public static int[] jsonArrayToIntArray(JsonArray arr) {
		if (arr == null) return null;
		int[] result = new int[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			result[i] = arr.get(i).getAsInt();
		}
		return result;
	}

	public static void evict(int modelId) {
		if (modelId >= 0 && modelId < cache.length) {
			cache[modelId] = null;
		}
	}
}
