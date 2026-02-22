package com.bestbudz.data.items;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.entity.pets.PetItemCreator;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.LRUCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;

public final class ItemDef {

	public static LRUCache<Sprite> mruNodes1 = new LRUCache<>(100);
	public static LRUCache<Model> mruNodes2 = new LRUCache<>(50);
	public static boolean isMembers = true;
	public static int totalItems;
	public static ItemDef[] cache;
	public static int cacheIndex;
	public static int cacheHits = 0;
	public static int cacheMisses = 0;
	public static JsonObject[] jsonDefs;
	public int value;
	public int[] modifiedModelColors;
	public int id;
	public int[] originalModelColors;
	public boolean membersObject;
	public int certTemplateID;
	public int anInt164;
	public int anInt165;
	public String[] groundActions;
	public int modelOffset1;
	public String name;
	public int modelID;
	public int anInt175;
	public boolean stackable;
	public byte[] description;
	public int certID;
	public int modelZoom;
	public int anInt188;
	public String[] itemActions;
	public String[] equipActions;
	public int modelRotationY;
	public int[] stackIDs;
	public int modelOffset2;
	public int anInt197;
	public int modelRotationX;
	public int anInt200;
	public int[] stackAmounts;
	public int team;
	public int anInt204;
	private byte aByte154;
	private int anInt162;
	private int anInt166;
	private int anInt167;
	private int anInt173;
	private int anInt184;
	private int anInt185;
	private int anInt191;
	private int anInt192;
	private int anInt196;
	private byte aByte205;
	public ItemDef() {
		id = -1;
	}

	public static void nullLoader() {
		if (mruNodes2 != null) mruNodes2.clear();
		if (mruNodes1 != null) mruNodes1.clear();
		jsonDefs = null;
		cache = null;
	}

	public static void unpackConfig() {
		JsonObject json = JsonCacheLoader.loadJsonObject("items.json");
		if (json == null) {
			System.err.println("Failed to load items.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		totalItems = maxId + 21;
		jsonDefs = new JsonObject[totalItems + 50000];

		int loaded = 0;
		for (Map.Entry<String, com.google.gson.JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			jsonDefs[id] = entry.getValue().getAsJsonObject();
			loaded++;
		}

		System.out.println("Items Loaded (JSON): " + loaded);

		int cacheSize = Math.max(64, Math.min(512, totalItems / 50));
		cache = new ItemDef[cacheSize];
		for (int k = 0; k < cacheSize; k++)
			cache[k] = new ItemDef();
	}

	public void readFromJson(JsonObject json) {
		if (json.has("modelID")) modelID = json.get("modelID").getAsInt();
		if (json.has("name")) {
			if (json.get("name").isJsonNull()) name = null;
			else name = json.get("name").getAsString();
		}
		if (json.has("description")) {
			if (json.get("description").isJsonNull()) description = null;
			else description = json.get("description").getAsString().getBytes();
		}
		if (json.has("modelZoom")) modelZoom = json.get("modelZoom").getAsInt();
		if (json.has("modelRotationY")) modelRotationY = json.get("modelRotationY").getAsInt();
		if (json.has("modelRotationX")) modelRotationX = json.get("modelRotationX").getAsInt();
		if (json.has("modelOffset1")) modelOffset1 = json.get("modelOffset1").getAsInt();
		if (json.has("modelOffset2")) modelOffset2 = json.get("modelOffset2").getAsInt();
		if (json.has("stackable")) stackable = json.get("stackable").getAsBoolean();
		if (json.has("value")) value = json.get("value").getAsInt();
		if (json.has("members")) membersObject = json.get("members").getAsBoolean();
		if (json.has("maleWearModel1")) anInt165 = json.get("maleWearModel1").getAsInt();
		if (json.has("maleWearModel2")) anInt188 = json.get("maleWearModel2").getAsInt();
		if (json.has("maleWearModel3")) anInt185 = json.get("maleWearModel3").getAsInt();
		if (json.has("femaleWearModel1")) anInt200 = json.get("femaleWearModel1").getAsInt();
		if (json.has("femaleWearModel2")) anInt164 = json.get("femaleWearModel2").getAsInt();
		if (json.has("femaleWearModel3")) anInt162 = json.get("femaleWearModel3").getAsInt();
		if (json.has("maleHeadModel1")) anInt175 = json.get("maleHeadModel1").getAsInt();
		if (json.has("maleHeadModel2")) anInt166 = json.get("maleHeadModel2").getAsInt();
		if (json.has("femaleHeadModel1")) anInt197 = json.get("femaleHeadModel1").getAsInt();
		if (json.has("femaleHeadModel2")) anInt173 = json.get("femaleHeadModel2").getAsInt();
		if (json.has("modelRotation2")) anInt204 = json.get("modelRotation2").getAsInt();
		if (json.has("certID")) certID = json.get("certID").getAsInt();
		if (json.has("certTemplateID")) certTemplateID = json.get("certTemplateID").getAsInt();
		if (json.has("scaleX")) anInt167 = json.get("scaleX").getAsInt();
		if (json.has("scaleY")) anInt192 = json.get("scaleY").getAsInt();
		if (json.has("scaleZ")) anInt191 = json.get("scaleZ").getAsInt();
		if (json.has("lightModifier")) anInt196 = json.get("lightModifier").getAsInt();
		if (json.has("shadowModifier")) anInt184 = json.get("shadowModifier").getAsInt();
		if (json.has("team")) team = json.get("team").getAsInt();

		if (json.has("groundActions")) {
			JsonArray arr = json.getAsJsonArray("groundActions");
			groundActions = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).isJsonNull()) groundActions[i] = null;
				else {
					String s = arr.get(i).getAsString();
					groundActions[i] = s.equalsIgnoreCase("hidden") ? null : s;
				}
			}
		}
		if (json.has("itemActions")) {
			JsonArray arr = json.getAsJsonArray("itemActions");
			itemActions = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).isJsonNull()) itemActions[i] = null;
				else itemActions[i] = arr.get(i).getAsString();
			}
		}
		if (json.has("equipActions")) {
			JsonArray arr = json.getAsJsonArray("equipActions");
			equipActions = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).isJsonNull()) equipActions[i] = null;
				else equipActions[i] = arr.get(i).getAsString();
			}
		}
		if (json.has("originalColors")) {
			JsonArray arr = json.getAsJsonArray("originalColors");
			originalModelColors = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				originalModelColors[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("modifiedColors")) {
			JsonArray arr = json.getAsJsonArray("modifiedColors");
			modifiedModelColors = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				modifiedModelColors[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("stackIDs")) {
			JsonArray arr = json.getAsJsonArray("stackIDs");
			stackIDs = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				stackIDs[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("stackAmounts")) {
			JsonArray arr = json.getAsJsonArray("stackAmounts");
			stackAmounts = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				stackAmounts[i] = arr.get(i).getAsInt();
			}
		}
	}

	public static Sprite getSprite(int id, int size, int color, int zoom) {
		if (id == 0) {
			return Client.cacheSprite[326];
		}
		ItemDef item = getItemDefinition(id);
		if (item.stackIDs == null) {
			size = -1;
		}
		if (size > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++) {
				if (size >= item.stackAmounts[j1] && item.stackAmounts[j1] != 0) {
					i1 = item.stackIDs[j1];
				}
			}
			if (i1 != -1) {
				item = getItemDefinition(i1);
			}
		}
		Model model = item.getStackedModel(1);
		if (model == null)
			return null;
		Sprite image = new Sprite(32, 32);
		int k1 = Rasterizer.viewportCenterX;
		int l1 = Rasterizer.viewportCenterY;
		int[] ai = Rasterizer.scanlineOffsets;
		int[] ai1 = DrawingArea.pixels;
		float[] depthBuffer = DrawingArea.depthBuffer;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Rasterizer.enableDepthBuffer = false;
		DrawingArea.initDrawingArea(32, 32, image.myPixels, new float[32 * 32]);
		DrawingArea.drawPixels(32, 0, 0, 0, 32);
		Rasterizer.initializeViewport();
		int itemZoom = item.modelZoom * zoom - 500;
		int l3 = Rasterizer.sinTable[item.modelRotationY] * itemZoom >> 16;
		int i4 = Rasterizer.cosTable[item.modelRotationY] * itemZoom >> 16;
		model.renderAtFixedPosition(item.modelRotationX, item.anInt204, item.modelRotationY, item.modelOffset1,
				l3 + model.modelHeight / 2 + item.modelOffset2, i4 + item.modelOffset2);
		if (color == 0) {
			for (int index = 31; index >= 0; index--) {
				for (int index2 = 31; index2 >= 0; index2--)
					if (image.myPixels[index + index2 * 32] == 0 && index > 0 && index2 > 0
							&& image.myPixels[(index - 1) + (index2 - 1) * 32] > 0)
						image.myPixels[index + index2 * 32] = 0x302020;
			}
		}
		DrawingArea.initDrawingArea(j2, i2, ai1, depthBuffer);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Rasterizer.viewportCenterX = k1;
		Rasterizer.viewportCenterY = l1;
		Rasterizer.scanlineOffsets = ai;
		Rasterizer.enableDepthBuffer = true;
		if (item.stackable) {
			image.originalWidth = 33;
		} else {
			image.originalWidth = 32;
		}
		image.originalHeight = size;

		return image;
	}

	public static Sprite getSprite(int i, int j, int k) {
		if (k == 0) {
			Sprite sprite = mruNodes1.get(i);
			if (sprite != null && sprite.originalHeight != j && sprite.originalHeight != -1) {
				mruNodes1.remove(i);
				sprite = null;
			}
			if (sprite != null) {
				return sprite;
			}
		}
		ItemDef itemDef = getItemDefinition(i);
		if (itemDef.stackIDs == null)
			j = -1;
		if (j > 1) {
			int i1 = -1;
			for (int j1 = 0; j1 < 10; j1++)
				if (j >= itemDef.stackAmounts[j1]
						&& itemDef.stackAmounts[j1] != 0)
					i1 = itemDef.stackIDs[j1];

			if (i1 != -1)
				itemDef = getItemDefinition(i1);
		}
		Model model = itemDef.getStackedModel(1);
		if (model == null)
			return null;
		Sprite sprite = null;
		if (itemDef.certTemplateID != -1) {
			sprite = getSprite(itemDef.certID, 10, -1);
			if (sprite == null)
				return null;
		}

		int spriteSize = 32;
		Sprite enabledSprite = new Sprite(spriteSize, spriteSize);

		int k1 = Rasterizer.viewportCenterX;
		int l1 = Rasterizer.viewportCenterY;
		int[] ai = Rasterizer.scanlineOffsets;
		int[] ai1 = DrawingArea.pixels;
		float[] depthBuffer = DrawingArea.depthBuffer;
		int i2 = DrawingArea.width;
		int j2 = DrawingArea.height;
		int k2 = DrawingArea.topX;
		int l2 = DrawingArea.bottomX;
		int i3 = DrawingArea.topY;
		int j3 = DrawingArea.bottomY;
		Rasterizer.enableDepthBuffer = false;
		DrawingArea.initDrawingArea(spriteSize, spriteSize, enabledSprite.myPixels, new float[32 * 32]);
		DrawingArea.drawSolidRectangle(spriteSize, 0, 0, 0, spriteSize);
		Rasterizer.initializeViewport();
		int k3 = itemDef.modelZoom;
		if (k == -1)
			k3 = (int) ((double) k3 * 1.5D);
		if (k > 0)
			k3 = (int) ((double) k3 * 1.04D);
		int l3 = Rasterizer.sinTable[itemDef.modelRotationY] * k3 >> 16;
		int i4 = Rasterizer.cosTable[itemDef.modelRotationY] * k3 >> 16;
		Rasterizer.isRenderingItem = true;
		model.renderAtFixedPosition(itemDef.modelRotationX, itemDef.anInt204,
				itemDef.modelRotationY, itemDef.modelOffset1, l3
						+ model.modelHeight / 2 + itemDef.modelOffset2,
				i4
						+ itemDef.modelOffset2);
		Rasterizer.isRenderingItem = false;
		for (int i5 = 31; i5 >= 0; i5--) {
			for (int j4 = 31; j4 >= 0; j4--)
				if (enabledSprite.myPixels[i5 + j4 * 32] == 0)
					if (i5 > 0
							&& enabledSprite.myPixels[(i5 - 1) + j4 * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (j4 > 0
							&& enabledSprite.myPixels[i5 + (j4 - 1) * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (i5 < 31
							&& enabledSprite.myPixels[i5 + 1 + j4 * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;
					else if (j4 < 31
							&& enabledSprite.myPixels[i5 + (j4 + 1) * 32] > 1)
						enabledSprite.myPixels[i5 + j4 * 32] = 1;

		}

		if (k > 0) {
			for (int j5 = 31; j5 >= 0; j5--) {
				for (int k4 = 31; k4 >= 0; k4--)
					if (enabledSprite.myPixels[j5 + k4 * 32] == 0)
						if (j5 > 0
								&& enabledSprite.myPixels[(j5 - 1) + k4 * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (k4 > 0
								&& enabledSprite.myPixels[j5 + (k4 - 1) * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (j5 < 31
								&& enabledSprite.myPixels[j5 + 1 + k4 * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;
						else if (k4 < 31
								&& enabledSprite.myPixels[j5 + (k4 + 1) * 32] == 1)
							enabledSprite.myPixels[j5 + k4 * 32] = k;

			}

		} else if (k == 0) {
			for (int k5 = 31; k5 >= 0; k5--) {
				for (int l4 = 31; l4 >= 0; l4--)
					if (enabledSprite.myPixels[k5 + l4 * 32] == 0
							&& k5 > 0
							&& l4 > 0
							&& enabledSprite.myPixels[(k5 - 1) + (l4 - 1) * 32] > 0)
						enabledSprite.myPixels[k5 + l4 * 32] = 0x302020;

			}

		}
		if (itemDef.certTemplateID != -1) {
			int l5 = Objects.requireNonNull(sprite).originalWidth;
			int j6 = sprite.originalHeight;
			sprite.originalWidth = 32;
			sprite.originalHeight = 32;
			sprite.drawSprite(0, 0);
			sprite.originalWidth = l5;
			sprite.originalHeight = j6;
		}
		if (k == 0)
			mruNodes1.put(i, enabledSprite);
		DrawingArea.initDrawingArea(j2, i2, ai1, depthBuffer);
		DrawingArea.setDrawingArea(j3, k2, l2, i3);
		Rasterizer.viewportCenterX = k1;
		Rasterizer.viewportCenterY = l1;
		Rasterizer.scanlineOffsets = ai;
		Rasterizer.enableDepthBuffer = true;
		if (itemDef.stackable)
			enabledSprite.originalWidth = 33;
		else
			enabledSprite.originalWidth = 32;
		enabledSprite.originalHeight = j;
		return enabledSprite;
	}

	public boolean method192(int j) {
		int k = anInt175;
		int l = anInt166;
		if (j == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return true;
		boolean flag = Model.isModelCached(k);
		if (l != -1 && !Model.isModelCached(l))
			flag = false;
		return flag;
	}

	public Model getWornModel(int gender) {
		int k = anInt175;
		int l = anInt166;
		if (gender == 1) {
			k = anInt197;
			l = anInt173;
		}
		if (k == -1)
			return null;
		Model model = Model.loadModelFromCache(k);
		if (l != -1) {
			Model model_1 = Model.loadModelFromCache(l);
			Model[] aclass30_sub2_sub4_sub6s = { model, model_1 };
			model = new Model(2, aclass30_sub2_sub4_sub6s);
		}
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				Objects.requireNonNull(model).replaceColor(modifiedModelColors[i1],
						originalModelColors[i1]);

		}
		return model;
	}

	public boolean method195(int j) {
		int k = anInt165;
		int l = anInt188;
		int i1 = anInt185;
		if (j == 1) {
			k = anInt200;
			l = anInt164;
			i1 = anInt162;
		}
		if (k == -1)
			return true;
		boolean flag = Model.isModelCached(k);
		if (l != -1 && !Model.isModelCached(l))
			flag = false;
		if (i1 != -1 && !Model.isModelCached(i1))
			flag = false;
		return flag;
	}

	public Model getInventoryModel(int gender) {
		int j = anInt165;
		int k = anInt188;
		int l = anInt185;
		if (gender == 1) {
			j = anInt200;
			k = anInt164;
			l = anInt162;
		}
		if (j == -1)
			return null;
		Model model = Model.loadModelFromCache(j);
		if (k != -1)
			if (l != -1) {
				Model model_1 = Model.loadModelFromCache(k);
				Model model_3 = Model.loadModelFromCache(l);
				Model[] aclass30_sub2_sub4_sub6_1s = { model, model_1, model_3 };
				model = new Model(3, aclass30_sub2_sub4_sub6_1s);
			} else {
				Model model_2 = Model.loadModelFromCache(k);
				Model[] aclass30_sub2_sub4_sub6s = { model, model_2 };
				model = new Model(2, aclass30_sub2_sub4_sub6s);
			}
		if (gender == 0 && aByte205 != 0)
			Objects.requireNonNull(model).translateCoords(0, aByte205, 0);
		if (gender == 1 && aByte154 != 0)
			Objects.requireNonNull(model).translateCoords(0, aByte154, 0);
		if (modifiedModelColors != null) {
			for (int i1 = 0; i1 < modifiedModelColors.length; i1++)
				Objects.requireNonNull(model).replaceColor(modifiedModelColors[i1], originalModelColors[i1]);

		}
		return model;
	}

	public void setDefaults() {
		modelID = 0;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		modelZoom = 2000;
		modelRotationY = 0;
		modelRotationX = 0;
		anInt204 = 0;
		modelOffset1 = 0;
		modelOffset2 = 0;
		stackable = false;
		value = 1;
		membersObject = false;
		groundActions = null;
		itemActions = null;
		equipActions = new String[6];
		anInt165 = -1;
		anInt188 = -1;
		aByte205 = 0;
		anInt200 = -1;
		anInt164 = -1;
		aByte154 = 0;
		anInt185 = -1;
		anInt162 = -1;
		anInt175 = -1;
		anInt166 = -1;
		anInt197 = -1;
		anInt173 = -1;
		stackIDs = null;
		stackAmounts = null;
		certID = -1;
		certTemplateID = -1;
		anInt167 = 128;
		anInt192 = 128;
		anInt191 = 128;
		anInt196 = 0;
		anInt184 = 0;
		team = 0;
	}

	public void toNote() {
		ItemDef itemDef = getItemDefinition(certTemplateID);
		modelID = itemDef.modelID;
		modelZoom = itemDef.modelZoom;
		modelRotationY = itemDef.modelRotationY;
		modelRotationX = itemDef.modelRotationX;

		anInt204 = itemDef.anInt204;
		modelOffset1 = itemDef.modelOffset1;
		modelOffset2 = itemDef.modelOffset2;
		modifiedModelColors = itemDef.modifiedModelColors;
		originalModelColors = itemDef.originalModelColors;
		ItemDef itemDef_1 = getItemDefinition(certID);
		name = itemDef_1.name;
		membersObject = itemDef_1.membersObject;
		value = itemDef_1.value;
		String s = "a";
		char c = itemDef_1.name.charAt(0);
		if (c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U')
			s = "an";
		description = ("Swap this note at any bank for " + s + " " + itemDef_1.name + ".").getBytes();
		stackable = true;
	}

	public Model getStackedModel(int stackSize) {
		if (stackIDs != null && stackSize > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (stackSize >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return getItemDefinition(j).getStackedModel(1);
		}

		Model model = mruNodes2.get(id);
		if (model != null)
			return model;

		model = Model.loadModelFromCache(modelID);
		if (model == null)
			return null;

		if (anInt167 != 128 || anInt192 != 128 || anInt191 != 128)
			model.modelScale(anInt167, anInt191, anInt192);

		if (modifiedModelColors != null && originalModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++) {
				model.replaceColor(modifiedModelColors[l], originalModelColors[l]);

				if (PetItemCreator.isVariantPetItem(id)) {
					System.out.println("Applying item color: " + modifiedModelColors[l] + " -> " + originalModelColors[l] + " for item " + id);
				}
			}
		}

		model.applyLighting(64 + anInt196, 768 + anInt184, -50, -10, -50, true);
		model.aBoolean1659 = true;
		mruNodes2.put(id, model);
		return model;
	}

	public Model method202(int i) {
		if (stackIDs != null && i > 1) {
			int j = -1;
			for (int k = 0; k < 10; k++)
				if (i >= stackAmounts[k] && stackAmounts[k] != 0)
					j = stackIDs[k];

			if (j != -1)
				return getItemDefinition(j).method202(1);
		}
		Model model = Model.loadModelFromCache(modelID);
		if (model == null)
			return null;
		if (modifiedModelColors != null) {
			for (int l = 0; l < modifiedModelColors.length; l++)
				model.replaceColor(modifiedModelColors[l], originalModelColors[l]);

		}
		return model;
	}

	public static boolean isValid(int id) {
		return id > 0 && id < totalItems;
	}

	public static boolean isRenderable(int id) {
		try {
			if (!isValid(id)) return false;
			ItemDef def = getItemDefinition(id);
			return def != null && def.modelID > 0;
		} catch (Exception e) {
			return false;
		}
	}

}
