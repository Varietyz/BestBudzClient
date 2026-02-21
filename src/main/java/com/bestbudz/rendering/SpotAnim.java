package com.bestbudz.rendering;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public final class SpotAnim {

	public static SpotAnim[] cache;
	private final int[] originalColors;
	private final int[] replacementColors;
	public Animation animation;
	public int resizeX;
	public int resizeY;
	public int rotation;
	public int ambient;
	public int contrast;
	private int id;
	private int modelId;
	private int animationId;

	private SpotAnim() {
		animationId = -1;
		originalColors = new int[6];
		replacementColors = new int[6];
		resizeX = 128;
		resizeY = 128;
	}

	public static void loadConfigurations() {
		JsonObject json = JsonCacheLoader.loadJsonObject("graphics.json");
		if (json == null) {
			System.err.println("Failed to load graphics.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		int length = maxId + 1;
		if (cache == null) {
			cache = new SpotAnim[length + 50000];
		}

		int loaded = 0;
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			JsonObject def = entry.getValue().getAsJsonObject();

			if (cache[id] == null) {
				cache[id] = new SpotAnim();
			}
			cache[id].id = id;
			cache[id].readFromJson(def);
			loaded++;
		}

		System.out.println("Graphics Loaded (JSON): " + loaded);
	}

	private void readFromJson(JsonObject json) {
		if (json.has("modelId")) {
			modelId = json.get("modelId").getAsInt();
		}
		if (json.has("animationId")) {
			animationId = json.get("animationId").getAsInt();
			if (Animation.anims != null && animationId >= 0 && animationId < Animation.anims.length) {
				animation = Animation.anims[animationId];
			}
		}
		if (json.has("resizeX")) {
			resizeX = json.get("resizeX").getAsInt();
		}
		if (json.has("resizeY")) {
			resizeY = json.get("resizeY").getAsInt();
		}
		if (json.has("rotation")) {
			rotation = json.get("rotation").getAsInt();
		}
		if (json.has("ambient")) {
			ambient = json.get("ambient").getAsInt();
		}
		if (json.has("contrast")) {
			contrast = json.get("contrast").getAsInt();
		}
		if (json.has("originalColors")) {
			JsonArray arr = json.getAsJsonArray("originalColors");
			for (int i = 0; i < Math.min(arr.size(), 6); i++) {
				originalColors[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("replacementColors")) {
			JsonArray arr = json.getAsJsonArray("replacementColors");
			for (int i = 0; i < Math.min(arr.size(), 6); i++) {
				replacementColors[i] = arr.get(i).getAsInt();
			}
		}
	}

	public Model getModel() {
		Model model = Model.loadModelFromCache(modelId);
		if (model == null) {
			return null;
		}

		for (int i = 0; i < 6; i++) {
			if (originalColors[i] != 0) {
				model.replaceColor(originalColors[i], replacementColors[i]);
			}
		}

		return model;
	}
}
