package com.bestbudz.tools;

import bestbudz.config.*;
import com.google.flatbuffers.FlatBufferBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Build-time tool that converts JSON config files to FlatBuffer binary format.
 * Usage: java ConfigPacker [outputDir]
 */
public final class ConfigPacker {

	private final Path inputDir;
	private final Path outputDir;

	public ConfigPacker(Path dir) {
		this.inputDir = dir;
		this.outputDir = dir;
	}

	public static void main(String[] args) throws Exception {
		Path dir = args.length > 0 ? Paths.get(args[0]) : Paths.get("extracted_cache");
		System.out.println("ConfigPacker: input/output dir = " + dir.toAbsolutePath());

		ConfigPacker packer = new ConfigPacker(dir);

		packer.packVarps();
		packer.packVarbits();
		packer.packFloors();
		packer.packOverlays();
		packer.packIdentityKits();
		packer.packAnimations();
		packer.packSpotAnims();
		packer.packItems();
		packer.packEntities();
		packer.packObjects();
		packer.packInterfaces();

		System.out.println("ConfigPacker: All configs packed successfully!");
	}

	// ── Helpers ────────────────────────────────────────────────────────────

	private JsonObject loadJson(String filename) throws IOException {
		Path p = inputDir.resolve(filename);
		if (!Files.exists(p)) {
			System.err.println("  WARNING: " + filename + " not found, skipping");
			return null;
		}
		String content = new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
		return JsonParser.parseString(content).getAsJsonObject();
	}

	private void writeOutput(String filename, FlatBufferBuilder builder) throws IOException {
		byte[] buf = builder.sizedByteArray();
		Path out = outputDir.resolve(filename);
		Files.write(out, buf);
		System.out.println("  -> " + filename + " (" + buf.length + " bytes)");
	}

	/** Sorted entries by integer key for FlatBuffers binary search. */
	private List<Map.Entry<Integer, JsonObject>> sortedEntries(JsonObject json) {
		List<Map.Entry<Integer, JsonObject>> list = new ArrayList<>();
		for (Map.Entry<String, JsonElement> e : json.entrySet()) {
			list.add(new AbstractMap.SimpleEntry<>(Integer.parseInt(e.getKey()), e.getValue().getAsJsonObject()));
		}
		list.sort(Comparator.comparingInt(Map.Entry::getKey));
		return list;
	}

	private int optInt(JsonObject o, String key, int def) {
		return o.has(key) ? o.get(key).getAsInt() : def;
	}

	private boolean optBool(JsonObject o, String key, boolean def) {
		return o.has(key) ? o.get(key).getAsBoolean() : def;
	}

	private String optStr(JsonObject o, String key) {
		if (!o.has(key) || o.get(key).isJsonNull()) return null;
		return o.get(key).getAsString();
	}

	private int createOptString(FlatBufferBuilder b, String s) {
		return s != null ? b.createString(s) : 0;
	}

	private int[] readIntArray(JsonObject o, String key) {
		if (!o.has(key)) return null;
		JsonArray arr = o.getAsJsonArray(key);
		int[] result = new int[arr.size()];
		for (int i = 0; i < arr.size(); i++) result[i] = arr.get(i).getAsInt();
		return result;
	}

	private int createIntVector(FlatBufferBuilder b, int[] data) {
		if (data == null) return 0;
		b.startVector(4, data.length, 4);
		for (int i = data.length - 1; i >= 0; i--) b.addInt(data[i]);
		return b.endVector();
	}

	/** Create a string vector, handling null entries by storing empty string. */
	private int createStringVector(FlatBufferBuilder b, JsonObject o, String key) {
		if (!o.has(key)) return 0;
		JsonArray arr = o.getAsJsonArray(key);
		int[] offsets = new int[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			if (arr.get(i).isJsonNull()) {
				offsets[i] = b.createString("");
			} else {
				String s = arr.get(i).getAsString();
				offsets[i] = b.createString(s.equalsIgnoreCase("hidden") ? "" : s);
			}
		}
		return createOffsetVector(b, offsets);
	}

	/** Create a vector of offsets (for string vectors, table vectors, etc). */
	private int createOffsetVector(FlatBufferBuilder b, int[] offsets) {
		b.startVector(4, offsets.length, 4);
		for (int i = offsets.length - 1; i >= 0; i--) b.addOffset(offsets[i]);
		return b.endVector();
	}

	// ── Packers ───────────────────────────────────────────────────────────

	private void packVarps() throws IOException {
		System.out.println("Packing varps...");
		JsonObject json = loadJson("varps.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(64 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			offsets[i] = VarpEntry.createVarpEntry(b,
				e.getKey(),
				optInt(e.getValue(), "configType", 0));
		}

		int vec = VarpConfig.createEntriesVector(b, offsets);
		b.finish(VarpConfig.createVarpConfig(b, vec));
		writeOutput("varps.fb", b);
	}

	private void packVarbits() throws IOException {
		System.out.println("Packing varbits...");
		JsonObject json = loadJson("varbits.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(256 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();
			offsets[i] = VarbitEntry.createVarbitEntry(b,
				e.getKey(),
				optInt(def, "baseVar", 0),
				optInt(def, "startBit", 0),
				optInt(def, "endBit", 0));
		}

		int vec = VarbitConfig.createEntriesVector(b, offsets);
		b.finish(VarbitConfig.createVarbitConfig(b, vec));
		writeOutput("varbits.fb", b);
	}

	private void packFloors() throws IOException {
		System.out.println("Packing floors (underlay)...");
		JsonObject json = loadJson("floors_underlay.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(32 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();
			offsets[i] = FloorEntry.createFloorEntry(b,
				e.getKey(),
				optInt(def, "rgb", 0),
				optInt(def, "textureId", -1),
				optBool(def, "occlude", true),
				optInt(def, "secondaryRgb", 0),
				def.has("secondaryRgb"));
		}

		int vec = FloorConfig.createEntriesVector(b, offsets);
		b.finish(FloorConfig.createFloorConfig(b, vec));
		writeOutput("floors_underlay.fb", b);
	}

	private void packOverlays() throws IOException {
		System.out.println("Packing floors (overlay)...");
		JsonObject json = loadJson("floors_overlay.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(32 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();
			offsets[i] = OverlayEntry.createOverlayEntry(b,
				e.getKey(),
				optInt(def, "rgb", 0),
				optInt(def, "textureId", -1),
				def.has("hideUnderlay"),
				def.has("hideUnderlay"),
				optInt(def, "secondaryRgb", 0),
				optInt(def, "blendMode", 0),
				optBool(def, "boolean_10", false),
				def.has("boolean_10"),
				optInt(def, "int_11", 0),
				optBool(def, "boolean_12", false),
				optInt(def, "tertiaryRgb", 0),
				optInt(def, "int_14", 0),
				optInt(def, "int_15", 0),
				optInt(def, "int_16", 0));
		}

		int vec = OverlayConfig.createEntriesVector(b, offsets);
		b.finish(OverlayConfig.createOverlayConfig(b, vec));
		writeOutput("floors_overlay.fb", b);
	}

	private void packIdentityKits() throws IOException {
		System.out.println("Packing identity kits...");
		JsonObject json = loadJson("identity_kits.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(64 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			int modelsOff = createIntVector(b, readIntArray(def, "models"));
			int origColorsOff = createIntVector(b, readIntArray(def, "originalColors"));
			int replColorsOff = createIntVector(b, readIntArray(def, "replacementColors"));
			int headModelsOff = createIntVector(b, readIntArray(def, "headModels"));

			IdentityKitEntry.startIdentityKitEntry(b);
			IdentityKitEntry.addId(b, e.getKey());
			IdentityKitEntry.addBodyPart(b, optInt(def, "bodyPart", -1));
			if (modelsOff != 0) IdentityKitEntry.addModels(b, modelsOff);
			IdentityKitEntry.addNonSelectable(b, optBool(def, "nonSelectable", false));
			IdentityKitEntry.addDisabled(b, optBool(def, "disabled", false));
			if (origColorsOff != 0) IdentityKitEntry.addOriginalColors(b, origColorsOff);
			if (replColorsOff != 0) IdentityKitEntry.addReplacementColors(b, replColorsOff);
			if (headModelsOff != 0) IdentityKitEntry.addHeadModels(b, headModelsOff);
			offsets[i] = IdentityKitEntry.endIdentityKitEntry(b);
		}

		int vec = IdentityKitConfig.createEntriesVector(b, offsets);
		b.finish(IdentityKitConfig.createIdentityKitConfig(b, vec));
		writeOutput("identity_kits.fb", b);
	}

	private void packAnimations() throws IOException {
		System.out.println("Packing animations...");
		JsonObject json = loadJson("animations.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(2 * 1024 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			int frameIdsOff = createIntVector(b, readIntArray(def, "frameIds"));
			int frameDurationsOff = createIntVector(b, readIntArray(def, "frameDurations"));
			int interleaveOff = createIntVector(b, readIntArray(def, "interleaveOrder"));

			AnimationEntry.startAnimationEntry(b);
			AnimationEntry.addId(b, e.getKey());
			AnimationEntry.addFrameCount(b, optInt(def, "frameCount", 0));
			if (frameIdsOff != 0) AnimationEntry.addFrameIds(b, frameIdsOff);
			if (frameDurationsOff != 0) AnimationEntry.addFrameDurations(b, frameDurationsOff);
			AnimationEntry.addLoopOffset(b, optInt(def, "loopOffset", -1));
			if (interleaveOff != 0) AnimationEntry.addInterleaveOrder(b, interleaveOff);
			AnimationEntry.addResetOnMove(b, optBool(def, "resetOnMove", false));
			AnimationEntry.addPriority(b, optInt(def, "priority", 5));
			AnimationEntry.addPlayerOffhand(b, optInt(def, "playerOffhand", -1));
			AnimationEntry.addPlayerMainhand(b, optInt(def, "playerMainhand", -1));
			AnimationEntry.addMaxLoops(b, optInt(def, "maxLoops", 99));
			AnimationEntry.addAnimatingPrecedence(b, optInt(def, "animatingPrecedence", -1));
			AnimationEntry.addWalkingPrecedence(b, optInt(def, "walkingPrecedence", -1));
			AnimationEntry.addReplayMode(b, optInt(def, "replayMode", 1));
			offsets[i] = AnimationEntry.endAnimationEntry(b);
		}

		int vec = AnimationConfig.createEntriesVector(b, offsets);
		b.finish(AnimationConfig.createAnimationConfig(b, vec));
		writeOutput("animations.fb", b);
	}

	private void packSpotAnims() throws IOException {
		System.out.println("Packing spot anims (graphics)...");
		JsonObject json = loadJson("graphics.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(256 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			int origColorsOff = createIntVector(b, readIntArray(def, "originalColors"));
			int replColorsOff = createIntVector(b, readIntArray(def, "replacementColors"));

			SpotAnimEntry.startSpotAnimEntry(b);
			SpotAnimEntry.addId(b, e.getKey());
			SpotAnimEntry.addModelId(b, optInt(def, "modelId", 0));
			SpotAnimEntry.addAnimationId(b, optInt(def, "animationId", -1));
			SpotAnimEntry.addResizeX(b, optInt(def, "resizeX", 128));
			SpotAnimEntry.addResizeY(b, optInt(def, "resizeY", 128));
			SpotAnimEntry.addRotation(b, optInt(def, "rotation", 0));
			SpotAnimEntry.addAmbient(b, optInt(def, "ambient", 0));
			SpotAnimEntry.addContrast(b, optInt(def, "contrast", 0));
			if (origColorsOff != 0) SpotAnimEntry.addOriginalColors(b, origColorsOff);
			if (replColorsOff != 0) SpotAnimEntry.addReplacementColors(b, replColorsOff);
			offsets[i] = SpotAnimEntry.endSpotAnimEntry(b);
		}

		int vec = SpotAnimConfig.createEntriesVector(b, offsets);
		b.finish(SpotAnimConfig.createSpotAnimConfig(b, vec));
		writeOutput("graphics.fb", b);
	}

	private void packItems() throws IOException {
		System.out.println("Packing items...");
		JsonObject json = loadJson("items.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(4 * 1024 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			// Pre-create string and vector offsets (must be done before startTable)
			int nameOff = createOptString(b, optStr(def, "name"));
			int descOff = createOptString(b, optStr(def, "description"));
			int groundActionsOff = createStringVector(b, def, "groundActions");
			int itemActionsOff = createStringVector(b, def, "itemActions");
			int equipActionsOff = createStringVector(b, def, "equipActions");
			int origColorsOff = createIntVector(b, readIntArray(def, "originalColors"));
			int modColorsOff = createIntVector(b, readIntArray(def, "modifiedColors"));
			int stackIdsOff = createIntVector(b, readIntArray(def, "stackIDs"));
			int stackAmtsOff = createIntVector(b, readIntArray(def, "stackAmounts"));

			offsets[i] = ItemEntry.createItemEntry(b,
				e.getKey(),
				optInt(def, "modelID", 0),
				nameOff,
				descOff,
				optInt(def, "modelZoom", 2000),
				optInt(def, "modelRotationY", 0),
				optInt(def, "modelRotationX", 0),
				optInt(def, "modelOffset1", 0),
				optInt(def, "modelOffset2", 0),
				optBool(def, "stackable", false),
				optInt(def, "value", 1),
				optBool(def, "members", false),
				optInt(def, "maleWearModel1", -1),
				optInt(def, "maleWearModel2", -1),
				optInt(def, "maleWearModel3", -1),
				optInt(def, "femaleWearModel1", -1),
				optInt(def, "femaleWearModel2", -1),
				optInt(def, "femaleWearModel3", -1),
				optInt(def, "maleHeadModel1", -1),
				optInt(def, "maleHeadModel2", -1),
				optInt(def, "femaleHeadModel1", -1),
				optInt(def, "femaleHeadModel2", -1),
				optInt(def, "modelRotation2", 0),
				optInt(def, "certID", -1),
				optInt(def, "certTemplateID", -1),
				optInt(def, "scaleX", 128),
				optInt(def, "scaleY", 128),
				optInt(def, "scaleZ", 128),
				optInt(def, "lightModifier", 0),
				optInt(def, "shadowModifier", 0),
				optInt(def, "team", 0),
				groundActionsOff,
				itemActionsOff,
				equipActionsOff,
				origColorsOff,
				modColorsOff,
				stackIdsOff,
				stackAmtsOff);
		}

		int vec = ItemConfig.createEntriesVector(b, offsets);
		b.finish(ItemConfig.createItemConfig(b, vec));
		writeOutput("items.fb", b);
	}

	private void packEntities() throws IOException {
		System.out.println("Packing entities (NPCs)...");
		JsonObject json = loadJson("npcs.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(4 * 1024 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			int nameOff = createOptString(b, optStr(def, "name"));
			int descOff = createOptString(b, optStr(def, "description"));
			int actionsOff = createStringVector(b, def, "actions");
			int modelsOff = createIntVector(b, readIntArray(def, "models"));
			int origColorsOff = createIntVector(b, readIntArray(def, "originalColors"));
			int newColorsOff = createIntVector(b, readIntArray(def, "newColors"));
			int chatHeadOff = createIntVector(b, readIntArray(def, "chatHeadModels"));
			int childIdsOff = createIntVector(b, readIntArray(def, "childrenIDs"));

			// Handle 65535 -> -1 conversion for anim fields
			int turnRight = optInt(def, "turnRightAnim", -1);
			if (turnRight == 65535) turnRight = -1;
			int turnAround = optInt(def, "turnAroundAnim", -1);
			if (turnAround == 65535) turnAround = -1;
			int turnLeft = optInt(def, "turnLeftAnim", -1);
			if (turnLeft == 65535) turnLeft = -1;
			int varbitId = optInt(def, "varbitId", -1);
			if (varbitId == 65535) varbitId = -1;
			int configId = optInt(def, "configId", -1);
			if (configId == 65535) configId = -1;

			EntityEntry.startEntityEntry(b);
			EntityEntry.addId(b, e.getKey());
			if (nameOff != 0) EntityEntry.addName(b, nameOff);
			if (descOff != 0) EntityEntry.addDescription(b, descOff);
			if (modelsOff != 0) EntityEntry.addModels(b, modelsOff);
			EntityEntry.addSize(b, optInt(def, "size", 1));
			EntityEntry.addStandAnim(b, optInt(def, "standAnim", -1));
			EntityEntry.addWalkAnim(b, optInt(def, "walkAnim", -1));
			EntityEntry.addTurnRightAnim(b, turnRight);
			EntityEntry.addTurnAroundAnim(b, turnAround);
			EntityEntry.addTurnLeftAnim(b, turnLeft);
			if (actionsOff != 0) EntityEntry.addActions(b, actionsOff);
			if (origColorsOff != 0) EntityEntry.addOriginalColors(b, origColorsOff);
			if (newColorsOff != 0) EntityEntry.addNewColors(b, newColorsOff);
			if (chatHeadOff != 0) EntityEntry.addChatHeadModels(b, chatHeadOff);
			EntityEntry.addVisibleOnMinimap(b, optBool(def, "visibleOnMinimap", true));
			EntityEntry.addCombatLevel(b, optInt(def, "combatLevel", -1));
			EntityEntry.addModelHeight(b, optInt(def, "modelHeight", 128));
			EntityEntry.addModelWidth(b, optInt(def, "modelWidth", 128));
			EntityEntry.addRenderPriority(b, optBool(def, "renderPriority", false));
			EntityEntry.addLightModifier(b, optInt(def, "lightModifier", 0));
			EntityEntry.addShadowModifier(b, optInt(def, "shadowModifier", 0));
			EntityEntry.addMapIconId(b, optInt(def, "mapIconId", -1));
			EntityEntry.addMapIconScale(b, optInt(def, "mapIconScale", 32));
			EntityEntry.addVarbitId(b, varbitId);
			EntityEntry.addConfigId(b, configId);
			if (childIdsOff != 0) EntityEntry.addChildrenIds(b, childIdsOff);
			EntityEntry.addClickable(b, optBool(def, "clickable", true));
			offsets[i] = EntityEntry.endEntityEntry(b);
		}

		int vec = EntityConfig.createEntriesVector(b, offsets);
		b.finish(EntityConfig.createEntityConfig(b, vec));
		writeOutput("npcs.fb", b);
	}

	private void packObjects() throws IOException {
		System.out.println("Packing objects...");
		JsonObject json = loadJson("objects.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(4 * 1024 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			int nameOff = createOptString(b, optStr(def, "name"));
			int descOff = createOptString(b, optStr(def, "description"));
			int actionsOff = createStringVector(b, def, "actions");
			int modelIdsOff = createIntVector(b, readIntArray(def, "modelIds"));
			int modelTypesOff = createIntVector(b, readIntArray(def, "modelTypes"));
			int origColorsOff = createIntVector(b, readIntArray(def, "originalColors"));
			int modColorsOff = createIntVector(b, readIntArray(def, "modifiedColors"));
			int childIdsOff = createIntVector(b, readIntArray(def, "childIds"));

			int animId = optInt(def, "animationId", -1);
			if (animId == 65535) animId = -1;
			int varbitId = optInt(def, "varbitId", -1);
			if (varbitId == 65535) varbitId = -1;
			int configId = optInt(def, "configId", -1);
			if (configId == 65535) configId = -1;

			ObjectEntry.startObjectEntry(b);
			ObjectEntry.addId(b, e.getKey());
			if (nameOff != 0) ObjectEntry.addName(b, nameOff);
			if (descOff != 0) ObjectEntry.addDescription(b, descOff);
			if (modelIdsOff != 0) ObjectEntry.addModelIds(b, modelIdsOff);
			if (modelTypesOff != 0) ObjectEntry.addModelTypes(b, modelTypesOff);
			ObjectEntry.addSizeX(b, optInt(def, "sizeX", 1));
			ObjectEntry.addSizeY(b, optInt(def, "sizeY", 1));
			ObjectEntry.addClipFlag(b, optBool(def, "clipFlag", true));
			ObjectEntry.addBlocksProjectiles(b, optBool(def, "blocksProjectiles", true));
			ObjectEntry.addHasActions(b, optBool(def, "hasActions", false));
			ObjectEntry.addRotateHeights(b, optBool(def, "rotateHeights", false));
			ObjectEntry.addNonFlatShading(b, optBool(def, "nonFlatShading", false));
			ObjectEntry.addBlocksMovement(b, optBool(def, "blocksMovement", false));
			ObjectEntry.addAnimationId(b, animId);
			ObjectEntry.addWallThickness(b, optInt(def, "wallThickness", 16));
			ObjectEntry.addAmbient(b, optInt(def, "ambient", 0));
			ObjectEntry.addContrast(b, optInt(def, "contrast", 0));
			if (actionsOff != 0) ObjectEntry.addActions(b, actionsOff);
			if (origColorsOff != 0) ObjectEntry.addOriginalColors(b, origColorsOff);
			if (modColorsOff != 0) ObjectEntry.addModifiedColors(b, modColorsOff);
			ObjectEntry.addMapScene(b, optInt(def, "mapScene", -1));
			ObjectEntry.addMirrored(b, optBool(def, "mirrored", false));
			ObjectEntry.addCastsShadow(b, optBool(def, "castsShadow", true));
			ObjectEntry.addScaleX(b, optInt(def, "scaleX", 128));
			ObjectEntry.addScaleY(b, optInt(def, "scaleY", 128));
			ObjectEntry.addScaleZ(b, optInt(def, "scaleZ", 128));
			ObjectEntry.addMapFunction(b, optInt(def, "mapFunction", -1));
			ObjectEntry.addSurroundings(b, optInt(def, "surroundings", 0));
			ObjectEntry.addTranslateX(b, optInt(def, "translateX", 0));
			ObjectEntry.addTranslateY(b, optInt(def, "translateY", 0));
			ObjectEntry.addTranslateZ(b, optInt(def, "translateZ", 0));
			ObjectEntry.addObstructsGround(b, optBool(def, "obstructsGround", false));
			ObjectEntry.addRemoveClipping(b, optBool(def, "removeClipping", false));
			ObjectEntry.addSupportItems(b, optInt(def, "supportItems", -1));
			ObjectEntry.addVarbitId(b, varbitId);
			ObjectEntry.addConfigId(b, configId);
			if (childIdsOff != 0) ObjectEntry.addChildIds(b, childIdsOff);
			offsets[i] = ObjectEntry.endObjectEntry(b);
		}

		int vec = ObjectConfig.createEntriesVector(b, offsets);
		b.finish(ObjectConfig.createObjectConfig(b, vec));
		writeOutput("objects.fb", b);
	}

	private void packInterfaces() throws IOException {
		System.out.println("Packing interfaces...");
		JsonObject json = loadJson("interfaces.json");
		if (json == null) return;

		FlatBufferBuilder b = new FlatBufferBuilder(4 * 1024 * 1024);
		List<Map.Entry<Integer, JsonObject>> entries = sortedEntries(json);
		int[] offsets = new int[entries.size()];

		for (int i = 0; i < entries.size(); i++) {
			Map.Entry<Integer, JsonObject> e = entries.get(i);
			JsonObject def = e.getValue();

			// Pre-create all string/vector offsets
			int disabledMsgOff = createOptString(b, optStr(def, "disabledMessage"));
			int enabledMsgOff = createOptString(b, optStr(def, "enabledMessage"));
			int disabledSpriteOff = createOptString(b, optStr(def, "disabledSprite"));
			int enabledSpriteOff = createOptString(b, optStr(def, "enabledSprite"));
			int selectedActionOff = createOptString(b, optStr(def, "selectedActionName"));
			int spellNameOff = createOptString(b, optStr(def, "spellName"));
			int tooltipOff = createOptString(b, optStr(def, "tooltip"));
			int actionsOff = createStringVector(b, def, "actions");

			int childrenOff = createIntVector(b, readIntArray(def, "children"));
			int childXOff = createIntVector(b, readIntArray(def, "childX"));
			int childYOff = createIntVector(b, readIntArray(def, "childY"));
			int compOpsOff = createIntVector(b, readIntArray(def, "comparatorOps"));
			int compValsOff = createIntVector(b, readIntArray(def, "comparatorValues"));

			// Value indexes: serialize as string vector for simplicity ("1,2,3" per inner array)
			int valueIdxOff = 0;
			if (def.has("valueIndexes") && def.get("valueIndexes").isJsonArray()) {
				JsonArray outer = def.getAsJsonArray("valueIndexes");
				int[] viOffsets = new int[outer.size()];
				for (int j = 0; j < outer.size(); j++) {
					if (outer.get(j).isJsonNull() || !outer.get(j).isJsonArray()) {
						viOffsets[j] = b.createString("");
						continue;
					}
					JsonArray inner = outer.get(j).getAsJsonArray();
					StringBuilder sb = new StringBuilder();
					for (int m = 0; m < inner.size(); m++) {
						if (m > 0) sb.append(',');
						sb.append(inner.get(m).getAsInt());
					}
					viOffsets[j] = b.createString(sb.toString());
				}
				valueIdxOff = createOffsetVector(b, viOffsets);
			}

			// Sprite slots
			int spriteSlotsOff = 0;
			if (def.has("spriteSlots") && def.get("spriteSlots").isJsonArray()) {
				JsonArray slots = def.getAsJsonArray("spriteSlots");
				int[] slotOffsets = new int[slots.size()];
				for (int j = 0; j < slots.size(); j++) {
					JsonObject slot = slots.get(j).getAsJsonObject();
					int spriteRefOff = createOptString(b, optStr(slot, "sprite"));
					slotOffsets[j] = SpriteSlot.createSpriteSlot(b,
						optInt(slot, "slot", 0),
						optInt(slot, "x", 0),
						optInt(slot, "y", 0),
						spriteRefOff);
				}
				spriteSlotsOff = InterfaceEntry.createSpriteSlotsVector(b, slotOffsets);
			}

			InterfaceEntry.startInterfaceEntry(b);
			InterfaceEntry.addId(b, e.getKey());
			InterfaceEntry.addParentId(b, optInt(def, "parentID", -1));
			InterfaceEntry.addType(b, optInt(def, "type", 0));
			InterfaceEntry.addAtActionType(b, optInt(def, "atActionType", 0));
			InterfaceEntry.addContentType(b, optInt(def, "contentType", 0));
			InterfaceEntry.addWidth(b, optInt(def, "width", 0));
			InterfaceEntry.addHeight(b, optInt(def, "height", 0));
			InterfaceEntry.addOpacity(b, optInt(def, "opacity", 0));
			int hoverType = optInt(def, "hoverType", 0);
			InterfaceEntry.addHoverType(b, hoverType > 0 ? hoverType : -1);
			InterfaceEntry.addScrollMax(b, optInt(def, "scrollMax", 0));
			InterfaceEntry.addMouseoverTriggered(b, optBool(def, "mouseoverTriggered", false));
			if (childrenOff != 0) InterfaceEntry.addChildren(b, childrenOff);
			if (childXOff != 0) InterfaceEntry.addChildX(b, childXOff);
			if (childYOff != 0) InterfaceEntry.addChildY(b, childYOff);
			if (compOpsOff != 0) InterfaceEntry.addComparatorOps(b, compOpsOff);
			if (compValsOff != 0) InterfaceEntry.addComparatorValues(b, compValsOff);
			if (valueIdxOff != 0) InterfaceEntry.addValueIndexes(b, valueIdxOff);
			InterfaceEntry.addReplaceItems(b, optBool(def, "replaceItems", false));
			InterfaceEntry.addIsBank(b, optBool(def, "isBank", false));
			InterfaceEntry.addUsableItems(b, optBool(def, "usableItems", false));
			InterfaceEntry.addABoolean235(b, optBool(def, "aBoolean235", false));
			InterfaceEntry.addInvSpritePadX(b, optInt(def, "invSpritePadX", 0));
			InterfaceEntry.addInvSpritePadY(b, optInt(def, "invSpritePadY", 0));
			if (spriteSlotsOff != 0) InterfaceEntry.addSpriteSlots(b, spriteSlotsOff);
			if (actionsOff != 0) InterfaceEntry.addActions(b, actionsOff);
			InterfaceEntry.addFilled(b, optBool(def, "filled", false));
			InterfaceEntry.addCenterText(b, optBool(def, "centerText", false));
			InterfaceEntry.addFontId(b, optInt(def, "fontId", -1));
			InterfaceEntry.addTextShadow(b, optBool(def, "textShadow", false));
			if (disabledMsgOff != 0) InterfaceEntry.addDisabledMessage(b, disabledMsgOff);
			if (enabledMsgOff != 0) InterfaceEntry.addEnabledMessage(b, enabledMsgOff);
			InterfaceEntry.addTextColor(b, optInt(def, "textColor", 0));
			InterfaceEntry.addEnabledColor(b, optInt(def, "enabledColor", 0));
			InterfaceEntry.addTextHoverColor(b, optInt(def, "textHoverColor", 0));
			InterfaceEntry.addEnabledHoverColor(b, optInt(def, "enabledHoverColor", 0));
			if (disabledSpriteOff != 0) InterfaceEntry.addDisabledSprite(b, disabledSpriteOff);
			if (enabledSpriteOff != 0) InterfaceEntry.addEnabledSprite(b, enabledSpriteOff);
			InterfaceEntry.addModelType(b, optInt(def, "modelType", 0));
			InterfaceEntry.addMediaId(b, optInt(def, "mediaID", 0));
			InterfaceEntry.addEnabledModelType(b, optInt(def, "enabledModelType", 0));
			InterfaceEntry.addEnabledMediaId(b, optInt(def, "enabledMediaID", 0));
			InterfaceEntry.addVerticalOffset(b, optInt(def, "verticalOffset", -1));
			InterfaceEntry.addEnabledVerticalOffset(b, optInt(def, "enabledVerticalOffset", -1));
			InterfaceEntry.addModelZoom(b, optInt(def, "modelZoom", 0));
			InterfaceEntry.addModelRotation1(b, optInt(def, "modelRotation1", 0));
			InterfaceEntry.addModelRotation2(b, optInt(def, "modelRotation2", 0));
			if (selectedActionOff != 0) InterfaceEntry.addSelectedActionName(b, selectedActionOff);
			if (spellNameOff != 0) InterfaceEntry.addSpellName(b, spellNameOff);
			InterfaceEntry.addSpellUsableOn(b, optInt(def, "spellUsableOn", 0));
			if (tooltipOff != 0) InterfaceEntry.addTooltip(b, tooltipOff);
			offsets[i] = InterfaceEntry.endInterfaceEntry(b);
		}

		int vec = InterfaceConfig.createEntriesVector(b, offsets);
		b.finish(InterfaceConfig.createInterfaceConfig(b, vec));
		writeOutput("interfaces.fb", b);
	}
}
