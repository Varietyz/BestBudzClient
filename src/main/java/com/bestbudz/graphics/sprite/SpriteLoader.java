package com.bestbudz.graphics.sprite;

import com.bestbudz.cache.JsonCacheLoader;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

public class SpriteLoader {

	public static void loadSprites() {
		try {
			JsonObject index = JsonCacheLoader.loadJsonObject("sprites/_index.json");
			if (index == null) {
				System.err.println("[SpriteLoader] Failed to load sprites/_index.json");
				return;
			}

			// Find max ID to size the arrays
			int maxId = 0;
			for (String key : index.keySet()) {
				int id = Integer.parseInt(key);
				if (id > maxId) maxId = id;
			}

			int totalSprites = maxId + 1;
			if (cache == null) {
				cache = new SpriteLoader[totalSprites];
				sprites = new Sprite[totalSprites];
			}

			int loaded = 0;
			for (Map.Entry<String, JsonElement> entry : index.entrySet()) {
				int id = Integer.parseInt(entry.getKey());
				JsonObject meta = entry.getValue().getAsJsonObject();

				String file = meta.get("file").getAsString();
				int offsetX = meta.has("offsetX") ? meta.get("offsetX").getAsInt() : 0;
				int offsetY = meta.has("offsetY") ? meta.get("offsetY").getAsInt() : 0;

				byte[] pngData = JsonCacheLoader.loadFileBytes("sprites/" + file);
				if (pngData == null) {
					System.err.println("[SpriteLoader] Missing PNG: sprites/" + file);
					continue;
				}

				if (cache[id] == null) {
					cache[id] = new SpriteLoader();
				}
				cache[id].id = id;
				cache[id].drawOffsetX = offsetX;
				cache[id].drawOffsetY = offsetY;
				cache[id].spriteData = pngData;

				createSprite(cache[id]);
				loaded++;
			}

			System.out.println("Sprites Loaded: " + loaded);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createSprite(SpriteLoader sprite) {
		sprites[sprite.id] = new Sprite(sprite.spriteData);
		sprites[sprite.id].drawOffsetX = sprite.drawOffsetX;
		sprites[sprite.id].drawOffsetY = sprite.drawOffsetY;
	}

	public SpriteLoader() {
		name = "name";
		id = -1;
		drawOffsetX = 0;
		drawOffsetY = 0;
		spriteData = null;
	}

	public static SpriteLoader[] cache;
	public static Sprite[] sprites = null;
	public String name;
	public int id;
	public int drawOffsetX;
	public int drawOffsetY;
	public byte[] spriteData;
}
