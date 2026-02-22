package com.bestbudz.world;

import bestbudz.config.VarbitConfig;
import bestbudz.config.VarbitEntry;
import com.bestbudz.cache.FlatBufferConfigLoader;
import com.bestbudz.cache.JsonCacheLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
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
		// Try FlatBuffer first
		ByteBuffer buf = FlatBufferConfigLoader.load("varbits.fb");
		if (buf != null) {
			VarbitConfig config = VarbitConfig.getRootAsVarbitConfig(buf);
			int maxId = 0;
			for (int i = 0; i < config.entriesLength(); i++) {
				int id = config.entries(i).id();
				if (id > maxId) maxId = id;
			}
			int cacheSize = maxId + 1;
			if (cache == null)
				cache = new VarBit[cacheSize];
			for (int i = 0; i < config.entriesLength(); i++) {
				VarbitEntry fb = config.entries(i);
				int id = fb.id();
				if (cache[id] == null)
					cache[id] = new VarBit();
				cache[id].baseVar = fb.baseVar();
				cache[id].startBit = fb.startBit();
				cache[id].endBit = fb.endBit();
			}
			System.out.println("VarBits Loaded (FlatBuffer): " + config.entriesLength());
			return;
		}

		// Fallback: JSON
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
