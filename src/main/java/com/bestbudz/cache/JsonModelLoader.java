package com.bestbudz.cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.lang.ref.SoftReference;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class JsonModelLoader {

	private static final Map<Integer, SoftReference<JsonObject>> cache = new HashMap<>();
	private static final Set<Integer> knownIds = new HashSet<>();
	private static int maxModelId = 0;
	private static boolean initialized = false;

	public static void initialize() {
		if (initialized) return;
		JsonObject index = JsonCacheLoader.loadJsonObject("models/_index.json");
		if (index != null) {
			for (String key : index.keySet()) {
				try {
					int id = Integer.parseInt(key);
					knownIds.add(id);
					if (id > maxModelId) maxModelId = id;
				} catch (NumberFormatException ignored) {}
			}
		}
		initialized = true;
		System.out.println("[JsonModelLoader] Initialized with " + knownIds.size() + " model IDs (max ID: " + maxModelId + ")");
	}

	public static int getModelCount() {
		return maxModelId + 1;
	}

	public static int getKnownModelCount() {
		return knownIds.size();
	}

	public static boolean exists(int modelId) {
		return knownIds.contains(modelId);
	}

	public static JsonObject loadModelJson(int modelId) {
		// Check soft cache
		SoftReference<JsonObject> ref = cache.get(modelId);
		if (ref != null) {
			JsonObject obj = ref.get();
			if (obj != null) {
				return obj;
			}
		}

		// Load from file
		JsonObject json = JsonCacheLoader.loadJsonObject("models/" + modelId + ".json");
		if (json != null) {
			cache.put(modelId, new SoftReference<>(json));
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
		cache.remove(modelId);
	}
}
