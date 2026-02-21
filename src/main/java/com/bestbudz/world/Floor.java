package com.bestbudz.world;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.config.SettingsConfig;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public final class Floor {

	public static Floor[] cache;
	public int anInt390;
	public int anInt391;
	public boolean aBoolean393;
	public int anInt394;
	public int anInt395;
	public int anInt396;
	public int anInt397;
	public int anInt398;
	public int anInt399;
	private Floor()
	{
		anInt391 = -1;
		aBoolean393 = true;
	}

	public static void unpackConfig() {
		JsonObject json = JsonCacheLoader.loadJsonObject("floors_underlay.json");
		if (json == null) {
			System.err.println("Failed to load floors_underlay.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		int cacheSize = maxId + 1;
		if (cache == null) {
			cache = new Floor[cacheSize];
		}

		int loaded = 0;
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			JsonObject def = entry.getValue().getAsJsonObject();

			if (cache[id] == null) {
				cache[id] = new Floor();
			}

			cache[id].readFromJson(def);
			loaded++;
		}

		System.out.println("Underlays Loaded (JSON): " + loaded);
	}

	private void readFromJson(JsonObject json) {
		if (json.has("rgb")) {
			anInt390 = json.get("rgb").getAsInt();
			if (SettingsConfig.snow) {
				anInt390 = 0xffffff;
			}
			method262(anInt390);
		}
		if (json.has("textureId")) {
			anInt391 = json.get("textureId").getAsInt();
		}
		if (json.has("occlude")) {
			aBoolean393 = json.get("occlude").getAsBoolean();
		}
		if (json.has("secondaryRgb")) {
			int j = anInt394;
			int k = anInt395;
			int l = anInt396;
			int i1 = anInt397;
			method262(json.get("secondaryRgb").getAsInt());
			anInt394 = j;
			anInt395 = k;
			anInt396 = l;
			anInt397 = i1;
			anInt398 = i1;
		}
	}

	private void method262(int i)
	{
		double d = (double)(i >> 16 & 0xff) / 256D;
		double d1 = (double)(i >> 8 & 0xff) / 256D;
		double d2 = (double)(i & 0xff) / 256D;
		double d3 = d;
		if(d1 < d3)
			d3 = d1;
		if(d2 < d3)
			d3 = d2;
		double d4 = d;
		if(d1 > d4)
			d4 = d1;
		if(d2 > d4)
			d4 = d2;
		double d5 = 0.0D;
		double d6 = 0.0D;
		double d7 = (d3 + d4) / 2D;
		if(d3 != d4)
		{
			if(d7 < 0.5D)
				d6 = (d4 - d3) / (d4 + d3);
			if(d7 >= 0.5D)
				d6 = (d4 - d3) / (2D - d4 - d3);
			if(d == d4)
				d5 = (d1 - d2) / (d4 - d3);
			else
			if(d1 == d4)
				d5 = 2D + (d2 - d) / (d4 - d3);
			else
			if(d2 == d4)
				d5 = 4D + (d - d1) / (d4 - d3);
		}
		d5 /= 6D;
		anInt394 = (int)(d5 * 256D);
		anInt395 = (int)(d6 * 256D);
		anInt396 = (int)(d7 * 256D);
		if(d7 > 0.5D)
			anInt398 = (int)((1.0D - d7) * d6 * 512D);
		else
			anInt398 = (int)(d7 * d6 * 512D);
		if(anInt398 < 1)
			anInt398 = 1;
		anInt397 = (int)(d5 * (double)anInt398);
		int k = anInt394;
		int l = anInt395;
		int i1 = anInt396;
		anInt399 = method263(k, l, i1);
	}

	private int method263(int i, int j, int k)
	{
		if(k > 179)
			j /= 2;
		if(k > 192)
			j /= 2;
		if(k > 217)
			j /= 2;
		if(k > 243)
			j /= 2;
		return (i / 4 << 10) + (j / 32 << 7) + k / 2;
	}
}
