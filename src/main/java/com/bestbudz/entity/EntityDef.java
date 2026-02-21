package com.bestbudz.entity;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.pets.PetVariantManager;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.LRUCache;
import com.bestbudz.world.VarBit;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;
import java.util.Objects;

public final class EntityDef {
	Client client;

	public static JsonObject[] jsonDefs;

  public static EntityDef forID(int i, boolean cached) {

	  if (PetVariantManager.isVariant(i)) {
		  if (cached) {

			  for (int j = 0; j < 20; j++) {
				  if (cache[j].interfaceType == (long) i) {
					  return cache[j];
				  }
			  }
		  }

		  cacheIndex = (cacheIndex + 1) % 20;
		  EntityDef entityDef = cached ? cache[cacheIndex] : new EntityDef();
		  entityDef.interfaceType = i;

		  PetVariantManager.applyVariant(i, entityDef);

		  return entityDef;
	  }

	  if (cached) {
		  for (int j = 0; j < 20; j++) {
			  if (cache[j].interfaceType == (long) i) {
				  return cache[j];
			  }
		  }
	  }

	  cacheIndex = (cacheIndex + 1) % 20;
	  EntityDef entityDef = new EntityDef();
	  if (cached) {
		  cache[cacheIndex] = entityDef;
	  }

	  entityDef.interfaceType = i;
	  if (jsonDefs != null && i >= 0 && i < jsonDefs.length && jsonDefs[i] != null) {
		  entityDef.readFromJson(jsonDefs[i]);
	  }
    switch (i) {
      case 6144:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.models = new int[2];
        entityDef.models[0] = 14533;
        entityDef.models[1] = 14522;
        entityDef.newColors = new int[5];
        entityDef.newColors[0] = 10355;
        entityDef.newColors[1] = 10471;
        entityDef.newColors[2] = 10941;
        entityDef.newColors[3] = 11200;
        entityDef.newColors[4] = 11187;
        entityDef.originalColors = new int[5];
        entityDef.originalColors[0] = 54387;
        entityDef.originalColors[1] = 54503;
        entityDef.originalColors[2] = 54744;
        entityDef.originalColors[3] = 55219;
        entityDef.originalColors[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.size = 3;
        break;

      case 6145:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.models = new int[4];
        entityDef.models[0] = 14533;
        entityDef.models[1] = 14523;
        entityDef.models[2] = 14524;
        entityDef.models[3] = 14525;
        entityDef.newColors = new int[5];
        entityDef.newColors[0] = 63411;
        entityDef.newColors[1] = 63287;
        entityDef.newColors[2] = 63163;
        entityDef.newColors[3] = 63046;
        entityDef.newColors[4] = 63046;
        entityDef.originalColors = new int[5];
        entityDef.originalColors[0] = 54387;
        entityDef.originalColors[1] = 54503;
        entityDef.originalColors[2] = 54744;
        entityDef.originalColors[3] = 55219;
        entityDef.originalColors[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.size = 3;
        break;

      case 6146:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.models = new int[2];
        entityDef.models[0] = 14533;
        entityDef.models[1] = 14522;
        entityDef.newColors = new int[5];
        entityDef.newColors[0] = 10355;
        entityDef.newColors[1] = 10471;
        entityDef.newColors[2] = 10941;
        entityDef.newColors[3] = 11200;
        entityDef.newColors[4] = 11187;
        entityDef.originalColors = new int[5];
        entityDef.originalColors[0] = 54387;
        entityDef.originalColors[1] = 54503;
        entityDef.originalColors[2] = 54744;
        entityDef.originalColors[3] = 55219;
        entityDef.originalColors[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.size = 3;
        break;

      case 6147:
        entityDef.name = "Portal";
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 3928;
        entityDef.standAnim = 3928;
        entityDef.actions = new String[5];
        entityDef.actions[1] = "Assault";
        entityDef.models = new int[4];
        entityDef.models[0] = 14533;
        entityDef.models[1] = 14523;
        entityDef.models[2] = 14524;
        entityDef.models[3] = 14525;
        entityDef.newColors = new int[5];
        entityDef.newColors[0] = 63411;
        entityDef.newColors[1] = 63287;
        entityDef.newColors[2] = 63163;
        entityDef.newColors[3] = 63046;
        entityDef.newColors[4] = 63046;
        entityDef.originalColors = new int[5];
        entityDef.originalColors[0] = 54387;
        entityDef.originalColors[1] = 54503;
        entityDef.originalColors[2] = 54744;
        entityDef.originalColors[3] = 55219;
        entityDef.originalColors[4] = 55203;
        entityDef.modelHeight = 128;
        entityDef.modelWidth = 128;
        entityDef.size = 3;
        break;

      case 1526:
      case 1520:
      case 1534:
      case 1536:
      case 1521:
      case 1519:
      case 1518:
        entityDef.name = "Mystery Fishes";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Splash";
        entityDef.description =
            "All kinds of fish are present, what else could be lurking in the water?".getBytes();
        break;

      case 394:
      case 2182:
		  entityDef.name = "Stander";
		  entityDef.description = "Bank Stander's Bankstanding grade is 420, advances 420.".getBytes();
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Bank";
        break;

      case 1558:
        entityDef.name = "Wah Gwaan";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's Best Budz bredren, Wah Gwaan!".getBytes();
        break;

      case 4000:
        entityDef.name = "Prince black dragon";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {17414, 17415, 17429, 17422};
        entityDef.walkAnim = 4635;
        entityDef.standAnim = 90;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 4001:
        entityDef.name = "Pet general graardor";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {27660, 27665};
        entityDef.walkAnim = 7016;
        entityDef.standAnim = 7017;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 4002:
        entityDef.name = "Pet corporeal beast";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {11056};
        entityDef.walkAnim = 1684;
        entityDef.standAnim = 1678;
        entityDef.modelWidth = 65;
        entityDef.modelHeight = 65;
        break;

      case 4003:
        entityDef.name = "Pet kree'Arra";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {28019, 28021, 28020};
        entityDef.walkAnim = 6977;
        entityDef.standAnim = 6976;
        entityDef.modelWidth = 20;
        entityDef.modelHeight = 20;
        break;

      case 4004:
        entityDef.name = "Pet k'ril tsutsaroth";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {27683, 27681, 27692, 27682, 27690};
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 4070;
        entityDef.standAnim = 6935;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 4009:
        entityDef.name = "Pet commander zilyana";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {27989, 27937, 27985, 27968, 27990};
        entityDef.combatGrade = 0;
        entityDef.walkAnim = 6965;
        entityDef.standAnim = 6966;
        entityDef.modelWidth = 50;
        entityDef.modelHeight = 50;
        break;

      case 4006:
        entityDef.name = "Pet dagannoth supreme";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {9941, 9943};
        entityDef.standAnim = 2850;
        entityDef.walkAnim = 2849;
        entityDef.modelWidth = 60;
        entityDef.modelHeight = 60;
        break;

      case 4007:
        entityDef.name = "Pet dagannoth prime";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {9940, 994, 9942};
        entityDef.walkAnim = 2849;
        entityDef.standAnim = 2850;
        entityDef.modelWidth = 60;
        entityDef.modelHeight = 60;
        break;

      case 4008:
        entityDef.name = "Pet dagannoth rex";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {9941};
        entityDef.walkAnim = 2849;
        entityDef.standAnim = 2850;
        entityDef.modelWidth = 60;
        entityDef.modelHeight = 60;
        break;

      case 4010:
        entityDef.name = "Pet Jad";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.models = new int[] {9319};
        entityDef.walkAnim = 2651;
        entityDef.standAnim = 2650;
        entityDef.modelWidth = 30;
        entityDef.modelHeight = 30;
        break;

      case 963:
        entityDef.models = new int[] {24602, 24605, 24606};
        entityDef.walkAnim = 6236;
        entityDef.standAnim = 6236;
        entityDef.modelWidth = 128;
        entityDef.modelHeight = 128;
        break;

      case 964:
        entityDef.models = new int[] {24597, 24598};
        entityDef.walkAnim = 6238;
        entityDef.standAnim = 6239;
        entityDef.modelWidth = 128;
        entityDef.modelHeight = 128;
        break;

      case 5547:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
		  entityDef.modelWidth = 30;
		  entityDef.modelHeight = 30;
        break;

      case 5559:
      case 5560:
      case 6637:
      case 6638:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.actions[1] = "Metamorphosis";
		  entityDef.modelWidth = 30;
		  entityDef.modelHeight = 30;
        break;

      case 2130:
      case 2131:
      case 2132:
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Pick-up";
        entityDef.actions[1] = "Metamorphosis";
		  entityDef.modelWidth = 90;
		  entityDef.modelHeight = 90;
        break;

      case 1758:
        entityDef.name = "Void Shop";
        entityDef.actions[0] = "Deal";
        entityDef.description = "It's the void knight's shop manager.".getBytes();
        break;

      case 1756:
        entityDef.name = "Void knight";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.description = "It's a void knight.".getBytes();
        break;

      case 4936:
        entityDef.name = "Consumer Mage";
        entityDef.actions = new String[5];
        entityDef.actions[0] = "Speak with";
        entityDef.actions[1] = "Deal";
        entityDef.actions[3] = "To Distant Land";
        entityDef.description = "It's a artificeer.".getBytes();
        break;

    }

    return entityDef;
  }

  public static EntityDef forID(int i) {
    return forID(i, true);
  }

  public static void unpackConfig() {
    JsonObject json = JsonCacheLoader.loadJsonObject("npcs.json");
    if (json == null) {
      System.err.println("Failed to load npcs.json");
      return;
    }

    int maxId = 0;
    for (String key : json.keySet()) {
      int id = Integer.parseInt(key);
      if (id > maxId) maxId = id;
    }

    int totalNPCs = maxId + 1;
    jsonDefs = new JsonObject[totalNPCs + 50000];

    int loaded = 0;
    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
      int id = Integer.parseInt(entry.getKey());
      jsonDefs[id] = entry.getValue().getAsJsonObject();
      loaded++;
    }

    System.out.println("Npcs Loaded (JSON): " + loaded);

    cache = new EntityDef[20];
    for (int k = 0; k < 20; k++) cache[k] = new EntityDef();
    for (int index = 0; index < totalNPCs; index++) {
      EntityDef ed = forID(index);
      if (ed == null) continue;
      if (ed.name == null)
	  {
	  }
    }
  }

  public static void nullLoader() {
    if (mruNodes != null) mruNodes.clear();
    jsonDefs = null;
    cache = null;
  }

  public void readFromJson(JsonObject json) {
    if (json.has("models")) {
      JsonArray arr = json.getAsJsonArray("models");
      models = new int[arr.size()];
      for (int i = 0; i < arr.size(); i++) models[i] = arr.get(i).getAsInt();
    }
    if (json.has("name")) {
      if (json.get("name").isJsonNull()) name = null;
      else name = json.get("name").getAsString();
    }
    if (json.has("description")) {
      if (json.get("description").isJsonNull()) description = null;
      else description = json.get("description").getAsString().getBytes();
    }
    if (json.has("size")) size = (byte) json.get("size").getAsInt();
    if (json.has("standAnim")) standAnim = json.get("standAnim").getAsInt();
    if (json.has("walkAnim")) walkAnim = json.get("walkAnim").getAsInt();
    if (json.has("turnRightAnim")) {
      int v = json.get("turnRightAnim").getAsInt();
      turnRightAnim = (v == 65535) ? -1 : v;
    }
    if (json.has("turnAroundAnim")) {
      int v = json.get("turnAroundAnim").getAsInt();
      turnAroundAnim = (v == 65535) ? -1 : v;
    }
    if (json.has("turnLeftAnim")) {
      int v = json.get("turnLeftAnim").getAsInt();
      turnLeftAnim = (v == 65535) ? -1 : v;
    }
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
      originalColors = new int[arr.size()];
      for (int i = 0; i < arr.size(); i++) originalColors[i] = arr.get(i).getAsInt();
    }
    if (json.has("newColors")) {
      JsonArray arr = json.getAsJsonArray("newColors");
      newColors = new int[arr.size()];
      for (int i = 0; i < arr.size(); i++) newColors[i] = arr.get(i).getAsInt();
    }
    if (json.has("chatHeadModels")) {
      JsonArray arr = json.getAsJsonArray("chatHeadModels");
      chatHeadModels = new int[arr.size()];
      for (int i = 0; i < arr.size(); i++) chatHeadModels[i] = arr.get(i).getAsInt();
    }
    if (json.has("visibleOnMinimap")) visibleOnMinimap = json.get("visibleOnMinimap").getAsBoolean();
    if (json.has("combatLevel")) combatGrade = json.get("combatLevel").getAsInt();
    if (json.has("modelHeight")) modelHeight = json.get("modelHeight").getAsInt();
    if (json.has("modelWidth")) modelWidth = json.get("modelWidth").getAsInt();
    if (json.has("renderPriority")) renderPriority = json.get("renderPriority").getAsBoolean();
    if (json.has("lightModifier")) lightModifier = json.get("lightModifier").getAsInt();
    if (json.has("shadowModifier")) shadowModifier = json.get("shadowModifier").getAsInt();
    if (json.has("mapIconId")) mapIconId = json.get("mapIconId").getAsInt();
    if (json.has("mapIconScale")) mapIconScale = json.get("mapIconScale").getAsInt();
    if (json.has("varbitId")) {
      int v = json.get("varbitId").getAsInt();
      varbitId = (v == 65535) ? -1 : v;
    }
    if (json.has("configId")) {
      int v = json.get("configId").getAsInt();
      configId = (v == 65535) ? -1 : v;
    }
    if (json.has("childrenIDs")) {
      JsonArray arr = json.getAsJsonArray("childrenIDs");
      childrenIDs = new int[arr.size()];
      for (int i = 0; i < arr.size(); i++) {
        childrenIDs[i] = arr.get(i).getAsInt();
        if (childrenIDs[i] == 65535) childrenIDs[i] = -1;
      }
    }
    if (json.has("clickable")) clickable = json.get("clickable").getAsBoolean();
  }

  public Model getModelForInterface() {
    if (childrenIDs != null) {
      EntityDef entityDef = getTransformedEntity();
      if (entityDef == null) return null;
      else return entityDef.getModelForInterface();
    }
    if (chatHeadModels == null) return null;
    boolean flag1 = false;
	  for (int value : chatHeadModels) if (!Model.isModelCached(value)) flag1 = true;

    if (flag1) return null;
    Model[] aclass30_sub2_sub4_sub6s = new Model[chatHeadModels.length];
    for (int j = 0; j < chatHeadModels.length; j++)
      aclass30_sub2_sub4_sub6s[j] = Model.loadModelFromCache(chatHeadModels[j]);

    Model model;
    if (aclass30_sub2_sub4_sub6s.length == 1) model = aclass30_sub2_sub4_sub6s[0];
    else model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
    if (originalColors != null) {
      for (int k = 0; k < originalColors.length; k++)
        Objects.requireNonNull(model).replaceColor(originalColors[k], newColors[k]);
    }
    return model;
  }

  public EntityDef getTransformedEntity() {
    int j = -1;
    if (varbitId != -1) {
      VarBit varBit = VarBit.cache[varbitId];
      int k = varBit.baseVar;
      int l = varBit.startBit;
      int i1 = varBit.endBit;
      int j1 = Client.bitMasks[i1 - l];
      j = client.variousSettings[k] >> l & j1;
    } else if (configId != -1) j = client.variousSettings[configId];
    if (j < 0 || j >= childrenIDs.length || childrenIDs[j] == -1) return null;
    else return forID(childrenIDs[j]);
  }

  public Model getAnimatedModel(
      int j, int currAnim, int nextAnim, int currCycle, int nextCycle, int[] ai) {
    if (childrenIDs != null) {
      final EntityDef type = getTransformedEntity();
      if (type == null) {
        return null;
      } else {
        return type.getAnimatedModel(j, currAnim, ai);
      }
    }
    Model model = mruNodes.get(interfaceType);
    if (model == null) {
      boolean flag = false;
		for (int i : models) if (!Model.isModelCached(i)) flag = true;

      if (flag) {
        return null;
      }
      final Model[] parts = new Model[models.length];
      for (int j1 = 0; j1 < models.length; j1++) {
        parts[j1] = Model.loadModelFromCache(models[j1]);
      }
      if (parts.length == 1) {
        model = parts[0];
      } else {
        model = new Model(parts.length, parts);
      }
      if (newColors != null) {
        for (int k1 = 0; k1 < newColors.length; k1++)
          Objects.requireNonNull(model).replaceColor(newColors[k1], originalColors[k1]);
      }
      Objects.requireNonNull(model).calculateNormals();
      model.applyLighting(64 + lightModifier, 1500 + shadowModifier, -30, -50, -30, true);
      mruNodes.put(interfaceType, model);
    }
    final Model model_1 = Model.aModel_1621;
    model_1.copyModel(model, SequenceFrame.isInvalidFrame(currAnim) & SequenceFrame.isInvalidFrame(j));
    if (currAnim != -1 && j != -1) {
      model_1.modelScale(modelHeight, modelHeight, modelWidth);
    } else if (currAnim != -1) {
      model_1.interpolateFrames(currAnim, nextAnim, nextCycle, currCycle);
    }
    if (modelHeight != 128 || modelWidth != 128) {
      model_1.modelScale(modelHeight, modelWidth, modelHeight);
    }
    model_1.calculateBounds();
    model_1.anIntArrayArray1658 = null;
    model_1.anIntArrayArray1657 = null;
    if (size == 1) model_1.aBoolean1659 = true;
    return model_1;
  }

  public Model getAnimatedModel(int j, int k, int[] ai) {
    if (childrenIDs != null) {
      EntityDef entityDef = getTransformedEntity();
      if (entityDef == null) return null;
      else return entityDef.getAnimatedModel(j, k, ai);
    }
    Model model = mruNodes.get(interfaceType);
    if (model == null) {
      boolean flag = false;
		for (int i : models) if (!Model.isModelCached(i)) flag = true;

      if (flag) return null;
      Model[] aclass30_sub2_sub4_sub6s = new Model[models.length];
      for (int j1 = 0; j1 < models.length; j1++)
        aclass30_sub2_sub4_sub6s[j1] = Model.loadModelFromCache(models[j1]);

      if (aclass30_sub2_sub4_sub6s.length == 1) model = aclass30_sub2_sub4_sub6s[0];
      else model = new Model(aclass30_sub2_sub4_sub6s.length, aclass30_sub2_sub4_sub6s);
      if (originalColors != null) {
        for (int k1 = 0; k1 < originalColors.length; k1++)
          Objects.requireNonNull(model).replaceColor(originalColors[k1], newColors[k1]);
      }
      Objects.requireNonNull(model).calculateNormals();
      model.applyLighting(64 + lightModifier, 1500 + shadowModifier, -30, -50, -30, true);
      mruNodes.put(interfaceType, model);
    }
    Model model_1 = Model.aModel_1621;
    model_1.copyModel(model, SequenceFrame.isInvalidFrame(k) & SequenceFrame.isInvalidFrame(j));
    if (k != -1 && j != -1) model_1.applyMixedTransformation(ai, j, k);
    else if (k != -1) model_1.applyTransformation(k);
    if (modelHeight != 128 || modelWidth != 128) model_1.modelScale(modelHeight, modelHeight, modelWidth);
    model_1.calculateBounds();
    model_1.anIntArrayArray1658 = null;
    model_1.anIntArrayArray1657 = null;
    if (size == 1) model_1.aBoolean1659 = true;
    return model_1;
  }

  public EntityDef() {turnLeftAnim = -1;
    varbitId = -1;
    turnRightAnim = -1;
    configId = -1;
    combatGrade = -1;
    defaultValue = 1834;
    walkAnim = -1;
    size = 1;
    mapIconId = -1;
    standAnim = -1;
    interfaceType = -1L;
    mapIconScale = 32;
    turnAroundAnim = -1;
    clickable = true;
    modelWidth = 128;
    visibleOnMinimap = true;
    modelHeight = 128;
    renderPriority = false;
  }

  public int turnLeftAnim;
  public static int cacheIndex;
  public int varbitId;
  public int turnRightAnim;
  public int configId;
  public int combatGrade;
  public final int defaultValue;
  public String name;
  public String[] actions;
  public int walkAnim;
  public byte size;
  public int[] newColors;
  public int[] chatHeadModels;
  public int mapIconId;
  public int[] originalColors;
  public int standAnim;
  public long interfaceType;
  public int mapIconScale;
  public static EntityDef[] cache;
  public static Client clientInstance;
  public int turnAroundAnim;
  public boolean clickable;
  public int lightModifier;
  public int modelWidth;
  public boolean visibleOnMinimap;
  public int[] childrenIDs;
  public byte[] description;
  public int modelHeight;
  public int shadowModifier;
  public boolean renderPriority;
  public int[] models;
  public static LRUCache<Model> mruNodes = new LRUCache<>(30);
}
