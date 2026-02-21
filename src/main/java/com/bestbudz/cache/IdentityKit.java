package com.bestbudz.cache;

import com.bestbudz.rendering.model.Model;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class IdentityKit
{
	public static int length;
	public static IdentityKit[] cache;
	private static Set<Integer> originalColors = new HashSet<>();
	private static Set<Integer> replacementColors = new HashSet<>();
	private static Set<Integer> additionalColors = new HashSet<>();

	private final int[] anIntArray659;
	private final int[] anIntArray660;
	private final int[] anIntArray661 = {
		-1, -1, -1, -1, -1
	};
	public int anInt657;
	public boolean aBoolean662;
	private int[] anIntArray658;

	private IdentityKit()
	{
		anInt657 = -1;
		anIntArray659 = new int[6];
		anIntArray660 = new int[6];
		aBoolean662 = false;
	}

	public static void unpackConfig()
	{
		JsonObject json = JsonCacheLoader.loadJsonObject("identity_kits.json");
		if (json == null) {
			System.err.println("Failed to load identity_kits.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		length = maxId + 1;
		if (cache == null)
			cache = new IdentityKit[length];

		int loaded = 0;
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			JsonObject def = entry.getValue().getAsJsonObject();

			if (cache[id] == null)
				cache[id] = new IdentityKit();

			cache[id].readFromJson(def);
			loaded++;
		}

		System.out.println("IdentityKits Loaded (JSON): " + loaded);
	}

	private void readFromJson(JsonObject json) {
		if (json.has("bodyPart")) {
			anInt657 = json.get("bodyPart").getAsInt();
		}
		if (json.has("models")) {
			JsonArray arr = json.getAsJsonArray("models");
			anIntArray658 = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				anIntArray658[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("nonSelectable")) {
			aBoolean662 = json.get("nonSelectable").getAsBoolean();
		}
		if (json.has("originalColors")) {
			JsonArray arr = json.getAsJsonArray("originalColors");
			for (int i = 0; i < Math.min(arr.size(), 6); i++) {
				anIntArray659[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("replacementColors")) {
			JsonArray arr = json.getAsJsonArray("replacementColors");
			for (int i = 0; i < Math.min(arr.size(), 6); i++) {
				anIntArray660[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("headModels")) {
			JsonArray arr = json.getAsJsonArray("headModels");
			for (int i = 0; i < Math.min(arr.size(), 5); i++) {
				anIntArray661[i] = arr.get(i).getAsInt();
			}
		}

		collectColors();
	}

	private void collectColors()
	{

		for (int color : anIntArray659) {
			if (color != 0) {
				originalColors.add(color);
			}
		}

		for (int color : anIntArray660) {
			if (color != 0) {
				replacementColors.add(color);
			}
		}

		for (int color : anIntArray661) {
			if (color != -1) {
				additionalColors.add(color);
			}
		}
	}

	public boolean method537()
	{
		if (anIntArray658 == null)
			return false;
		boolean flag = true;
		for (int i : anIntArray658)
			if (!Model.isModelCached(i))
				flag = false;

		return !flag;
	}

	public Model method538()
	{
		if (anIntArray658 == null)
			return null;
		Model[] aclass30_sub2_sub4_sub6s = new Model[anIntArray658.length];
		for (int i = 0; i < anIntArray658.length; i++)
			aclass30_sub2_sub4_sub6s[i] = Model.loadModelFromCache(anIntArray658[i]);

		Model model;
		if (aclass30_sub2_sub4_sub6s.length == 1)
			model = aclass30_sub2_sub4_sub6s[0];
		else
			model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
		for (int j = 0; j < 6; j++)
		{
			if (anIntArray659[j] == 0)
				break;
			Objects.requireNonNull(model).replaceColor(anIntArray659[j], anIntArray660[j]);
		}

		return model;
	}

	public boolean method539()
	{
		boolean flag1 = true;
		for (int i = 0; i < 5; i++)
			if (anIntArray661[i] != -1 && !Model.isModelCached(anIntArray661[i]))
				flag1 = false;

		return flag1;
	}

	public Model method540()
	{
		Model[] aclass30_sub2_sub4_sub6s = new Model[5];
		int j = 0;
		for (int k = 0; k < 5; k++)
			if (anIntArray661[k] != -1)
				aclass30_sub2_sub4_sub6s[j++] = Model.loadModelFromCache(anIntArray661[k]);

		Model model = new Model(j, aclass30_sub2_sub4_sub6s);
		for (int l = 0; l < 6; l++)
		{
			if (anIntArray659[l] == 0)
				break;
			model.replaceColor(anIntArray659[l], anIntArray660[l]);
		}

		return model;
	}
}
