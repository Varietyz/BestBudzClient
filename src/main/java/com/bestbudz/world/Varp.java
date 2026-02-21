package com.bestbudz.world;

import com.bestbudz.cache.JsonCacheLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public final class Varp {

    public static Varp[] cache;
    public int configType;
    private Varp()
    {
	}

    public static void unpackConfig()
    {
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
