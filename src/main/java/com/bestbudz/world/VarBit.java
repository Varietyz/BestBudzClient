package com.bestbudz.world;

import com.bestbudz.cache.JsonCacheLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public final class VarBit
{

	public static VarBit[] cache;
	public int baseVar;
	public int startBit;
	public int endBit;
	private VarBit()
	{
	}

	public static void unpackConfig()
	{
		JsonObject json = JsonCacheLoader.loadJsonObject("varbits.json");
		if (json == null) {
			System.err.println("Failed to load varbits.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		int cacheSize = maxId + 1;
		if (cache == null)
			cache = new VarBit[cacheSize];

		int loaded = 0;
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			JsonObject def = entry.getValue().getAsJsonObject();

			if (cache[id] == null)
				cache[id] = new VarBit();

			cache[id].baseVar = def.has("baseVar") ? def.get("baseVar").getAsInt() : 0;
			cache[id].startBit = def.has("startBit") ? def.get("startBit").getAsInt() : 0;
			cache[id].endBit = def.has("endBit") ? def.get("endBit").getAsInt() : 0;
			loaded++;
		}

		System.out.println("VarBits Loaded (JSON): " + loaded);
	}
}
