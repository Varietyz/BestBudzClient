package com.bestbudz.world;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.core.Client;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.LRUCache;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;

public final class ObjectDef {
	Client client;
	public static final Model[] tempModels = new Model[4];
	public static boolean lowMem;
	public static JsonObject[] jsonDefs;
	public static int totalObjects;
	public static Client clientInstance;
	public static int cacheIndex;
	public static int cacheHits = 0;
	public static int cacheMisses = 0;
	public static LRUCache<Model> mruNodes2 = new LRUCache<>(64);
	public static ObjectDef[] cache;
	public static LRUCache<Model> mruNodes1 = new LRUCache<>(1024);

	public boolean aBoolean736;
	public byte aByte737;
	public int anInt738;
	public String name;
	public int anInt740;
	public byte aByte742;
	public int sizeX;
	public int anInt745;
	public int anInt746;
	public int[] originalModelColors;
	public int anInt748;
	public int configId;
	public boolean aBoolean751;
	public int type;
	public boolean blocksProjectiles;
	public int anInt758;
	public int[] childIds;
	public int anInt760;
	public int sizeY;
	public boolean rotateHeights;
	public boolean blocksMovement;
	public boolean aBoolean766;
	public boolean clipFlag;
	public int anInt768;
	public boolean aBoolean769;
	public int anInt772;
	public int[] anIntArray773;
	public int varbitId;
	public int wallThickness;
	public int[] anIntArray776;
	public byte[] description;
	public boolean hasActions;
	public boolean castsShadow;
	public int animationId;
	public int anInt783;
	public int[] modifiedModelColors;
	public String[] actions;

	public ObjectDef() {
		type = -1;
	}

	public static ObjectDef forID(int i) {
		if (i > totalObjects - 1)
			i = totalObjects - 1;
		for (int j = 0; j < cache.length; j++)
			if (cache[j].type == i) {
				cacheHits++;
				return cache[j];
			}
		cacheMisses++;
		cacheIndex = (cacheIndex + 1) % cache.length;
		ObjectDef objectDef = cache[cacheIndex];
		objectDef.type = i;
		objectDef.setDefaults();
		if (jsonDefs != null && i >= 0 && i < jsonDefs.length && jsonDefs[i] != null) {
			objectDef.readFromJson(jsonDefs[i]);
		}

		if (i == 11407 || i == 11408) {
			objectDef = forID(11404);
			objectDef.type = i;
			objectDef.modifiedModelColors = new int[]{7105, 8137, 7130, 5043, 7082};
			if (i == 11407)
				objectDef.originalModelColors = new int[]{51126, 51149, 51149, 51113, 51113};
			else
				objectDef.originalModelColors = new int[]{117, 127, 127, 96, 96};
			return objectDef;
		}

		switch (i) {
			case 26621:
			case 26620:
			case 26619:
			case 26618:
			case 26561:
			case 26562:
			case 7836:
			case 7837:
			case 7838:
			case 7839:
			case 7848:
			case 7847:
			case 7849:
			case 7850:
			case 8150:
			case 8151:
			case 8152:
			case 8153:
			case 8550:
			case 8551:
			case 8552:
			case 8553:
			case 8554:
			case 8555:
			case 8556:
			case 8557:
			case 20973:
			case 26606:
			case 26611:
			case 26613:
			case 26626:
			case 26608:
			case 26607:
			case 26603:
			case 26580:
			case 26604:
			case 26609:
			case 26600:
			case 26601:
			case 26610:
			case 26616:
			case 26612:
			case 26605:
			case 26602:
				objectDef.hasActions = true;
				break;
			case 22472:
				objectDef.name = "Tablet";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Create";
				break;

			case 8720:
			case 26492:
			case 26796:
			case 26797:
			case 26798:
			case 26799:
			case 26800:
			case 26801:
			case 26802:
			case 26803:
			case 26804:
			case 26805:
			case 26806:
			case 26807:
			case 26808:
			case 26809:
			case 26810:
			case 26811:
			case 26812:
			case 26813:
			case 26814:
			case 26815:
			case 26816:
			case 26817:
			case 26818:
			case 26819:
			case 26820:
			case 26821:
				objectDef.name = "Chill Booth";
				objectDef.actions = new String[5];
				objectDef.actions[1] = "Open shop";
				objectDef.hasActions = true;
				break;

			case 2097:
				objectDef.name = "Anvil";
				objectDef.description = "It's a solid iron block hammer thingy.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Hammer";
				objectDef.hasActions = true;
				break;

			case 2030:
				objectDef.name = "Forge";
				objectDef.description = "Wondering if i could get my blunt lit here..".getBytes();
				objectDef.hasActions = true;
				break;

			case 26181:
				objectDef.name = "Stove";
				objectDef.description = "Looks like someone attempted to cook meth on this stove.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Cook";
				objectDef.hasActions = true;
				break;

			case 10377:
				objectDef.name = "Giant's Glory Hole";
				objectDef.description = "HOLY FUCK! There is a lot a semen in there.".getBytes();
				break;

			case 10379:
				objectDef.name = "Weed Stash";
				objectDef.description = "You sniff the boxes & It's Cheese Haze!".getBytes();
				break;

			case 10380:
				objectDef.name = "Trim Waste";
				objectDef.description = "Boxes filled with trimmed cannabis leaves and branches.".getBytes();
				break;

			case 14854:
			case 14855:
			case 14856:
				objectDef.name = "Fancy Rocks";
				objectDef.description = "There are tons of gems in these rocks.".getBytes();
				objectDef.hasActions = true;
				break;

			case 14901:
				objectDef.name = "Fire Essence";
				objectDef.description = "It compels you to keep burning wood, but it also pays you!.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Offer to";
				objectDef.hasActions = true;
				break;

			case 10376:
				objectDef.name = "Cocaïne Stall";
				objectDef.description = "You look and wonder.. Where are the topless women?".getBytes();
				break;

			case 26149:
				objectDef.name = "Budz Core";
				objectDef.description = "The BestBudz core, essence of all magic in the game.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Blood Trial";
				objectDef.hasActions = true;
				break;

			case 6114:
				objectDef.name = "Blood Trial";
				objectDef.description = "Attempt to flee from the Blood Trial.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Flee";
				objectDef.hasActions = true;
				break;

			case 2072:
				objectDef.name = "Game Crate";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Search";
				break;

			case 9472:
			case 9371:
				objectDef.name = "Stoner Market";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Info";
				objectDef.actions[1] = "Edit Shop";
				objectDef.actions[2] = "Explore Shops";
				break;

			case 18772:
				objectDef.name = "Misery Box";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Open";
				break;

			case 5249:
				objectDef.name = "Fyah";
				objectDef.description = "Bun dem!".getBytes();
				break;

			case 13712:
				objectDef.name = "Tin";
				objectDef.description = "It's a Tin ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 13709:
				objectDef.name = "Copper";
				objectDef.description = "It's a Copper ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 13711:
				objectDef.name = "Iron";
				objectDef.description = "It's a Iron ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 13714:
				objectDef.name = "Coal";
				objectDef.description = "It's a Coal ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 13718:
				objectDef.name = "Mithril";
				objectDef.description = "It's a Mithril ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 14168:
				objectDef.name = "Adamantite";
				objectDef.description = "It's a Adamantite ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 13707:
				objectDef.name = "Gold";
				objectDef.description = "It's a Gold ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 14175:
				objectDef.name = "Runite";
				objectDef.description = "It's a Runite ore.".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Collect";
				break;

			case 574:
				objectDef.name = "Mage Spells";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Change";
				break;

			case 575:
				objectDef.name = "Altar";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Restore";
				break;

			case 576:
				objectDef.name = "Highscores";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "View";
				break;

			case 13618:
				objectDef.name = "Wyverns";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Teleport-to";
				break;

			case 13619:
				objectDef.name = "Fountain of Rune";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Teleport-to";
				break;

			case 4090:
				objectDef.name = "Blood altar";
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Craft";
				break;

			case 11744:
				objectDef.name = "Stand";
				objectDef.description = "Remember the days you had to run to the bank to withdraw/deposit?".getBytes();
				objectDef.actions = new String[5];
				objectDef.actions[0] = "Bank";
				objectDef.hasActions = true;
				break;
		}

		return objectDef;
	}

	public static void nullLoader() {
		if (mruNodes1 != null) mruNodes1.clear();
		if (mruNodes2 != null) mruNodes2.clear();
		jsonDefs = null;
		cache = null;
	}

	public static void unpackConfig() {
		JsonObject json = JsonCacheLoader.loadJsonObject("objects.json");
		if (json == null) {
			System.err.println("Failed to load objects.json");
			return;
		}

		int maxId = 0;
		for (String key : json.keySet()) {
			int id = Integer.parseInt(key);
			if (id > maxId) maxId = id;
		}

		totalObjects = maxId + 1;
		jsonDefs = new JsonObject[totalObjects];

		int loaded = 0;
		for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
			int id = Integer.parseInt(entry.getKey());
			jsonDefs[id] = entry.getValue().getAsJsonObject();
			loaded++;
		}

		System.out.println("Objects Loaded (JSON): " + loaded);

		int cacheSize = Math.max(64, Math.min(512, totalObjects / 50));
		cache = new ObjectDef[cacheSize];
		for (int k = 0; k < cacheSize; k++)
			cache[k] = new ObjectDef();
	}

	public void readFromJson(JsonObject json) {
		if (json.has("modelIds")) {
			JsonArray arr = json.getAsJsonArray("modelIds");
			anIntArray773 = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				anIntArray773[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("modelTypes")) {
			JsonArray arr = json.getAsJsonArray("modelTypes");
			anIntArray776 = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				anIntArray776[i] = arr.get(i).getAsInt();
			}
		}
		if (json.has("name")) {
			if (json.get("name").isJsonNull()) name = null;
			else name = json.get("name").getAsString();
		}
		if (json.has("description")) {
			if (json.get("description").isJsonNull()) description = null;
			else description = json.get("description").getAsString().getBytes();
		}
		if (json.has("sizeX")) sizeX = json.get("sizeX").getAsInt();
		if (json.has("sizeY")) sizeY = json.get("sizeY").getAsInt();
		if (json.has("clipFlag")) clipFlag = json.get("clipFlag").getAsBoolean();
		if (json.has("blocksProjectiles")) blocksProjectiles = json.get("blocksProjectiles").getAsBoolean();
		if (json.has("hasActions")) hasActions = json.get("hasActions").getAsBoolean();
		if (json.has("rotateHeights")) rotateHeights = json.get("rotateHeights").getAsBoolean();
		if (json.has("nonFlatShading")) aBoolean769 = json.get("nonFlatShading").getAsBoolean();
		if (json.has("blocksMovement")) blocksMovement = json.get("blocksMovement").getAsBoolean();
		if (json.has("animationId")) {
			animationId = json.get("animationId").getAsInt();
			if (animationId == 65535) animationId = -1;
		}
		if (json.has("wallThickness")) wallThickness = json.get("wallThickness").getAsInt();
		if (json.has("ambient")) aByte737 = (byte) json.get("ambient").getAsInt();
		if (json.has("contrast")) aByte742 = (byte) json.get("contrast").getAsInt();
		if (json.has("actions")) {
			JsonArray arr = json.getAsJsonArray("actions");
			actions = new String[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				if (arr.get(i).isJsonNull()) actions[i] = null;
				else {
					String s = arr.get(i).getAsString();
					actions[i] = s.equalsIgnoreCase("hidden") ? null : s;
				}
			}
		}
		if (json.has("originalColors")) {
			JsonArray arr = json.getAsJsonArray("originalColors");
			originalModelColors = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) originalModelColors[i] = arr.get(i).getAsInt();
		}
		if (json.has("modifiedColors")) {
			JsonArray arr = json.getAsJsonArray("modifiedColors");
			modifiedModelColors = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) modifiedModelColors[i] = arr.get(i).getAsInt();
		}
		if (json.has("mapScene")) anInt746 = json.get("mapScene").getAsInt();
		if (json.has("mirrored")) aBoolean751 = json.get("mirrored").getAsBoolean();
		if (json.has("castsShadow")) castsShadow = json.get("castsShadow").getAsBoolean();
		if (json.has("scaleX")) anInt748 = json.get("scaleX").getAsInt();
		if (json.has("scaleY")) anInt772 = json.get("scaleY").getAsInt();
		if (json.has("scaleZ")) anInt740 = json.get("scaleZ").getAsInt();
		if (json.has("mapFunction")) anInt758 = json.get("mapFunction").getAsInt();
		if (json.has("surroundings")) anInt768 = json.get("surroundings").getAsInt();
		if (json.has("translateX")) anInt738 = json.get("translateX").getAsInt();
		if (json.has("translateY")) anInt745 = json.get("translateY").getAsInt();
		if (json.has("translateZ")) anInt783 = json.get("translateZ").getAsInt();
		if (json.has("obstructsGround")) aBoolean736 = json.get("obstructsGround").getAsBoolean();
		if (json.has("removeClipping")) aBoolean766 = json.get("removeClipping").getAsBoolean();
		if (json.has("supportItems")) anInt760 = json.get("supportItems").getAsInt();
		if (json.has("varbitId")) {
			varbitId = json.get("varbitId").getAsInt();
			if (varbitId == 65535) varbitId = -1;
		}
		if (json.has("configId")) {
			configId = json.get("configId").getAsInt();
			if (configId == 65535) configId = -1;
		}
		if (json.has("childIds")) {
			JsonArray arr = json.getAsJsonArray("childIds");
			childIds = new int[arr.size()];
			for (int i = 0; i < arr.size(); i++) {
				childIds[i] = arr.get(i).getAsInt();
				if (childIds[i] == 65535) childIds[i] = -1;
			}
		}

		// Post-processing (same as binary readValues)
		if (!Objects.equals(name, "null") && name != null) {
			hasActions = anIntArray773 != null && (anIntArray776 == null || anIntArray776[0] == 10);
			if (actions != null)
				hasActions = true;
		}
		if (aBoolean766) {
			clipFlag = false;
			blocksProjectiles = false;
		}
		if (anInt760 == -1)
			anInt760 = clipFlag ? 1 : 0;
	}

	public void setDefaults() {
		anIntArray773 = null;
		anIntArray776 = null;
		name = null;
		description = null;
		modifiedModelColors = null;
		originalModelColors = null;
		sizeX = 1;
		sizeY = 1;
		clipFlag = true;
		blocksProjectiles = true;
		hasActions = false;
		rotateHeights = false;
		aBoolean769 = false;
		blocksMovement = false;
		animationId = -1;
		wallThickness = 16;
		aByte737 = 0;
		aByte742 = 0;
		actions = null;
		anInt746 = -1;
		anInt758 = -1;
		aBoolean751 = false;
		castsShadow = true;
		anInt748 = 128;
		anInt772 = 128;
		anInt740 = 128;
		anInt768 = 0;
		anInt738 = 0;
		anInt745 = 0;
		anInt783 = 0;
		aBoolean736 = false;
		aBoolean766 = false;
		anInt760 = -1;
		varbitId = -1;
		configId = -1;
		childIds = null;
	}

	public boolean hasType(int i) {
		if (anIntArray776 == null) {
			if (anIntArray773 == null)
				return true;
			if (i != 10)
				return true;
			for (int j : anIntArray773) {
				if (!Model.isModelCached(j & 0xffff))
					return false;
			}
			return true;
		}
		for (int j = 0; j < anIntArray776.length; j++)
			if (anIntArray776[j] == i)
				return Model.isModelCached(anIntArray773[j] & 0xffff);
		return true;
	}

	public Model getModel(int i, int j, int k, int l, int i1, int j1, int k1) {
		Model model = getModel(i, k1, j);
		if (model == null)
			return null;
		if (rotateHeights || aBoolean769)
			model = new Model(rotateHeights, aBoolean769, model);
		if (rotateHeights) {
			int l1 = (k + l + i1 + j1) / 4;
			for (int i2 = 0; i2 < model.vertexCount; i2++) {
				int j2 = model.verticesX[i2];
				int k2 = model.verticesZ[i2];
				int l2 = k + ((l - k) * (j2 + 64)) / 128;
				int i3 = j1 + ((i1 - j1) * (j2 + 64)) / 128;
				int j3 = l2 + ((i3 - l2) * (k2 + 64)) / 128;
				model.verticesY[i2] += j3 - l1;
			}
			model.calculateVerticalBounds();
		}
		return model;
	}

	public boolean isModelLoaded() {
		if (anIntArray773 == null)
			return true;
		for (int j : anIntArray773) {
			if (!Model.isModelCached(j & 0xffff))
				return false;
		}
		return true;
	}

	public ObjectDef getChildObject() {
		int i = -1;
		if (varbitId != -1) {
			if (varbitId < VarBit.cache.length && VarBit.cache[varbitId] != null) {
				VarBit varBit = VarBit.cache[varbitId];
				int j = varBit.baseVar;
				int k = varBit.startBit;
				int l = varBit.endBit;
				int i1 = Client.bitMasks[l - k];
				if (j >= 0 && j < client.variousSettings.length) {
					i = client.variousSettings[j] >> k & i1;
				}
			}
		} else if (configId != -1 && configId < client.variousSettings.length) {
			i = client.variousSettings[configId];
		}
		if (i < 0 || i >= childIds.length || childIds[i] == -1)
			return null;
		return forID(childIds[i]);
	}

	public Model getModel(int j, int k, int l) {
		Model model = null;
		long l1;
		if (anIntArray776 == null) {
			if (j != 10)
				return null;
			l1 = ((long) type << 8) + l + ((long) (k + 1) << 32);
			Model model_1 = mruNodes2.get(l1);
			if (model_1 != null)
				return model_1;
			if (anIntArray773 == null)
				return null;
			boolean flag1 = aBoolean751 ^ (l > 3);
			int k1 = anIntArray773.length;
			for (int i2 = 0; i2 < k1; i2++) {
				int l2 = anIntArray773[i2];
				if (flag1)
					l2 += 0x10000;
				model = mruNodes1.get(l2);
				if (model == null) {
					model = Model.loadModelFromCache(l2 & 0xffff);
					if (model == null)
						return null;
					if (flag1)
						model.mirrorZ();
					mruNodes1.put(l2, model);
				}
				if (k1 > 1)
					tempModels[i2] = model;
			}

			if (k1 > 1)
				model = new Model(k1, tempModels);
		} else {
			int i1 = -1;
			for (int j1 = 0; j1 < anIntArray776.length; j1++) {
				if (anIntArray776[j1] != j)
					continue;
				i1 = j1;
				break;
			}

			if (i1 == -1)
				return null;
			l1 = ((long) type << 8) + ((long) i1 << 3) + l + ((long) (k + 1) << 32);
			Model model_2 = mruNodes2.get(l1);
			if (model_2 != null)
				return model_2;
			int j2 = anIntArray773[i1];
			boolean flag3 = aBoolean751 ^ (l > 3);
			if (flag3)
				j2 += 0x10000;
			model = mruNodes1.get(j2);
			if (model == null) {
				model = Model.loadModelFromCache(j2 & 0xffff);
				if (model == null)
					return null;
				if (flag3)
					model.mirrorZ();
				mruNodes1.put(j2, model);
			}
		}

		boolean needsScale = anInt748 != 128 || anInt772 != 128 || anInt740 != 128;
		boolean needsTranslate = anInt738 != 0 || anInt745 != 0 || anInt783 != 0;

		Model model_3 = new Model(modifiedModelColors == null, SequenceFrame.isInvalidFrame(k),
			l == 0 && k == -1 && !needsScale && !needsTranslate, Objects.requireNonNull(model));
		if (k != -1) {
			model_3.calculateNormals();
			model_3.applyTransformation(k);
			model_3.anIntArrayArray1658 = null;
			model_3.anIntArrayArray1657 = null;
		}

		for (int rot = 0; rot < l; rot++)
			model_3.rotateY180();

		if (modifiedModelColors != null) {
			for (int k2 = 0; k2 < modifiedModelColors.length; k2++)
				model_3.replaceColor(modifiedModelColors[k2], originalModelColors[k2]);
		}
		if (needsScale)
			model_3.modelScale(anInt748, anInt740, anInt772);
		if (needsTranslate)
			model_3.translateCoords(anInt738, anInt745, anInt783);
		model_3.applyLighting(74, 1000, -90, -580, -90, !aBoolean769);
		if (anInt760 == 1)
			model_3.anInt1654 = model_3.modelHeight;
		mruNodes2.put(l1, model_3);
		return model_3;
	}
}
