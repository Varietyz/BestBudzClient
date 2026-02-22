package com.bestbudz.world;

import bestbudz.config.VarpConfig;
import bestbudz.config.VarpEntry;
import com.bestbudz.cache.FlatBufferConfigLoader;
import com.bestbudz.cache.JsonCacheLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.util.Map;

public final class Varp {

    public static Varp[] cache;
    public int configType;
    private Varp()
    {
	}

    public static void unpackConfig()
    {
        // Try FlatBuffer first
        ByteBuffer buf = FlatBufferConfigLoader.load("varps.fb");
        if (buf != null) {
            VarpConfig config = VarpConfig.getRootAsVarpConfig(buf);
            int maxId = 0;
            for (int i = 0; i < config.entriesLength(); i++) {
                int id = config.entries(i).id();
                if (id > maxId) maxId = id;
            }
            int cacheSize = maxId + 1;
            if (cache == null)
                cache = new Varp[cacheSize];
            for (int i = 0; i < config.entriesLength(); i++) {
                VarpEntry fb = config.entries(i);
                int id = fb.id();
                if (cache[id] == null)
                    cache[id] = new Varp();
                cache[id].configType = fb.configType();
            }
            System.out.println("Varps Loaded (FlatBuffer): " + config.entriesLength());
            return;
        }

        // Fallback: JSON
        JsonObject json = JsonCacheLoader.loadJsonObject("varps.json");
        if (json == null) {
            System.err.println("Failed to load varps.json");
            return;
        }

        int maxId = 0;
        for (String key : json.keySet()) {
            int id = Integer.parseInt(key);
            if (id > maxId) maxId = id;
        }

        int cacheSize = maxId + 1;
        if (cache == null)
            cache = new Varp[cacheSize];

        int loaded = 0;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            int id = Integer.parseInt(entry.getKey());
            JsonObject def = entry.getValue().getAsJsonObject();

            if (cache[id] == null)
                cache[id] = new Varp();

            cache[id].configType = def.has("configType") ? def.get("configType").getAsInt() : 0;
            loaded++;
        }

        System.out.println("Varps Loaded (JSON): " + loaded);
    }

}
