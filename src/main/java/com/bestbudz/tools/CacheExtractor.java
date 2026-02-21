package com.bestbudz.tools;

import com.bestbudz.network.ArchiveLoader;
import com.bestbudz.network.Stream;
import com.bestbudz.util.Decompressor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

/**
 * Standalone cache extraction tool. Reads the RS2/317 game cache and exports
 * all data as JSON, PNG, and raw binary files.
 *
 * Usage: java com.bestbudz.tools.CacheExtractor [--cache=path] [--output=path]
 */
public class CacheExtractor {

    // ─── Configuration ───────────────────────────────────────────────
    private static String cachePath;
    private static String outputPath;

    // ─── Cache infrastructure ────────────────────────────────────────
    private static RandomAccessFile dataFile;
    private static Decompressor[] decompressors;

    // ─── Extraction stats ────────────────────────────────────────────
    private static int itemCount, npcCount, objectCount, animCount, graphicsCount;
    private static int underlayCount, overlayCount, idkCount, varpCount, varbitCount;
    private static int spriteCount, mediaSpriteCount, textureCount;
    private static int modelCount, mapCount, animFrameCount;
    private static int interfaceCount, midiIndexCount, titleSpriteCount, rawArchiveCount;
    private static int animFrameJsonCount, landscapeJsonCount, objectSpawnJsonCount;
    private static int fontJsonCount, wordencJsonCount, soundJsonCount;

    // ─── Known media sprite names (RS2/317 standard set) ────────────
    private static final String[] MEDIA_SPRITE_NAMES = {
        "backleft1", "backleft2", "backright1", "backright2",
        "chatback", "close", "coins", "compass",
        "cross", "cursor", "cursor_cross",
        "equipment", "headicons_hint", "headicons_pk",
        "headicons_prayer", "hitmarks",
        "invback", "leftarrow",
        "logo", "magicon", "magicon2", "mapback",
        "mapdots", "mapfunction", "mapscene",
        "miscgraphics", "miscgraphics2", "miscgraphics3",
        "multiway", "number_button",
        "prayericon", "redstone1", "redstone2", "redstone3",
        "rightarrow", "runes", "scrollbar",
        "sideicons", "skull", "staticons",
        "steelborder", "steelborder2",
        "titlebox", "titlebutton",
        "tradebacking", "wornicons",
        "mapedge", "frame",
        "orb_fill", "hp", "energy",
        // Additional sprites loaded by GameLoader
        "newhitmarks", "cbuttons", "fixed", "skillicons", "fullscreen",
        "orbs3", "orbs4", "orbs5", "hpbars",
        "overlay_multiway", "mapmarker", "screenframe"
    };

    // ═════════════════════════════════════════════════════════════════
    // MAIN
    // ═════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        System.out.println("=== BestBudz Cache Extractor ===");

        parseArgs(args);
        System.out.println("Cache path:  " + cachePath);
        System.out.println("Output path: " + outputPath);

        try {
            openCache();
            System.out.println("Cache opened successfully.");

            // Load archives from idx0
            byte[] configData = decompressors[0].decompress(2);
            byte[] interfaceData = decompressors[0].decompress(3);
            byte[] mediaData = decompressors[0].decompress(4);
            byte[] versionData = decompressors[0].decompress(5);
            byte[] textureData = decompressors[0].decompress(6);

            ArchiveLoader configArchive = new ArchiveLoader(configData);
            ArchiveLoader interfaceArchive = interfaceData != null ? new ArchiveLoader(interfaceData) : null;
            ArchiveLoader mediaArchive = new ArchiveLoader(mediaData);
            ArchiveLoader versionArchive = new ArchiveLoader(versionData);
            ArchiveLoader textureArchive = new ArchiveLoader(textureData);

            System.out.println("Archives loaded.");

            // Phase 2: Config data
            System.out.println("\n--- Phase 2: Config Data ---");
            extractItems(configArchive);
            extractNpcs(configArchive);
            extractObjects(configArchive);
            extractAnimations(configArchive);
            extractGraphics(configArchive);
            extractFloors(configArchive);
            extractOverlayFloors(configArchive);
            extractIdentityKits(configArchive);
            extractVarps(configArchive);
            extractVarBits(configArchive);
            extractMapIndex(versionArchive);
            extractInterfaces(interfaceArchive);
            extractMidiIndex(versionArchive);

            // Phase 3: Image assets
            System.out.println("\n--- Phase 3: Image Assets ---");
            extractCacheSprites();
            extractMediaSprites(mediaArchive);
            extractTextures(textureArchive);
            extractTitleSprites();

            // Phase 4: Binary assets
            System.out.println("\n--- Phase 4: Binary Assets ---");
            extractModels();
            extractMaps();
            extractAnimFrames();
            extractRawArchives();

            // Phase 5: Deep binary-to-JSON parsing
            System.out.println("\n--- Phase 5: Deep Binary Parsing ---");
            parseAnimFramesToJson();
            parseLandscapeMapsToJson();
            parseObjectSpawnsToJson();
            parseFontsToJson();
            parseWordencToJson();
            parseSoundsToJson();

            // Phase 6: Master index
            System.out.println("\n--- Phase 6: Finalization ---");
            writeMasterIndex(startTime);

            long elapsed = System.currentTimeMillis() - startTime;
            System.out.println("\n=== Extraction complete in " + (elapsed / 1000.0) + "s ===");

        } catch (Exception e) {
            System.err.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            closeCache();
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // INFRASTRUCTURE
    // ═════════════════════════════════════════════════════════════════

    private static void parseArgs(String[] args) {
        cachePath = "src/main/resources/caches/bestbudz";
        outputPath = "extracted_cache";

        for (String arg : args) {
            if (arg.startsWith("--cache=")) {
                cachePath = arg.substring(8);
            } else if (arg.startsWith("--output=")) {
                outputPath = arg.substring(9);
            }
        }
    }

    private static void openCache() throws IOException {
        dataFile = new RandomAccessFile(cachePath + "/main_file_cache.dat", "r");
        decompressors = new Decompressor[6];
        for (int i = 0; i < 6; i++) {
            RandomAccessFile idxFile = new RandomAccessFile(
                cachePath + "/main_file_cache.idx" + i, "r");
            decompressors[i] = new Decompressor(dataFile, idxFile, i + 1);
        }
    }

    private static void closeCache() {
        try {
            if (dataFile != null) dataFile.close();
        } catch (IOException ignored) {}
    }

    private static void mkdirs(String path) {
        new File(path).mkdirs();
    }

    private static void writeFile(String path, String content) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8")) {
            osw.write(content);
        }
    }

    private static void writeFile(String path, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(path)) {
            fos.write(data);
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // JSON BUILDER
    // ═════════════════════════════════════════════════════════════════

    private static String jsonEscape(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder(s.length() + 8);
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }

    private static String jsonArray(int[] arr) {
        if (arr == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(arr[i]);
        }
        sb.append(']');
        return sb.toString();
    }

    private static String jsonArray(String[] arr) {
        if (arr == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (int i = 0; i < arr.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(arr[i] == null ? "null" : jsonEscape(arr[i]));
        }
        sb.append(']');
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════
    // PHASE 2: CONFIG DATA EXTRACTORS
    // ═════════════════════════════════════════════════════════════════

    private static void extractItems(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("obj.dat");
        byte[] idxBytes = config.extractFile("obj.idx");
        if (datBytes == null || idxBytes == null) {
            System.out.println("  Items: SKIPPED (missing obj.dat/obj.idx)");
            return;
        }

        Stream idx = new Stream(idxBytes);
        int total = idx.readUnsignedWord();
        int[] offsets = new int[total];
        int pos = 2;
        for (int i = 0; i < total; i++) {
            offsets[i] = pos;
            pos += idx.readUnsignedWord();
        }

        Stream dat = new Stream(datBytes);
        StringBuilder sb = new StringBuilder(total * 200);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            dat.position = offsets[id];
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            // Defaults
            String name = null;
            String description = null;
            int modelID = 0, modelZoom = 2000, modelRotY = 0, modelRotX = 0, modelRot2 = 0;
            int modelOff1 = 0, modelOff2 = 0;
            boolean stackable = false, members = false;
            int value = 1, team = 0;
            int certID = -1, certTemplate = -1;
            int[] origColors = null, modColors = null;
            String[] groundActions = null, itemActions = null;
            int[] stackIDs = null, stackAmounts = null;
            int maleModel1 = -1, maleModel2 = -1, femaleModel1 = -1, femaleModel2 = -1;
            int maleModel3 = -1, femaleModel3 = -1;
            int maleHeadModel1 = -1, maleHeadModel2 = -1, femaleHeadModel1 = -1, femaleHeadModel2 = -1;
            int scaleX = 128, scaleY = 128, scaleZ = 128;
            int lightMod = 0, shadowMod = 0;

            while (true) {
                int opcode = dat.readUnsignedByte();
                if (opcode == 0) break;
                switch (opcode) {
                    case 1: modelID = dat.readUnsignedWord(); break;
                    case 2: name = dat.readString(); break;
                    case 3: description = new String(dat.readBytes()); break;
                    case 4: modelZoom = dat.readUnsignedWord(); break;
                    case 5: modelRotY = dat.readUnsignedWord(); break;
                    case 6: modelRotX = dat.readUnsignedWord(); break;
                    case 7: {
                        modelOff1 = dat.readUnsignedWord();
                        if (modelOff1 > 32767) modelOff1 -= 65536;
                        break;
                    }
                    case 8: {
                        modelOff2 = dat.readUnsignedWord();
                        if (modelOff2 > 32767) modelOff2 -= 65536;
                        break;
                    }
                    case 10: dat.readUnsignedWord(); break;
                    case 11: stackable = true; break;
                    case 12: value = dat.readDWord(); break;
                    case 16: members = true; break;
                    case 23: maleModel1 = dat.readUnsignedWord(); dat.readSignedByte(); break;
                    case 24: maleModel2 = dat.readUnsignedWord(); break;
                    case 25: femaleModel1 = dat.readUnsignedWord(); dat.readSignedByte(); break;
                    case 26: femaleModel2 = dat.readUnsignedWord(); break;
                    case 30: case 31: case 32: case 33: case 34: {
                        if (groundActions == null) groundActions = new String[5];
                        groundActions[opcode - 30] = dat.readString();
                        if ("hidden".equalsIgnoreCase(groundActions[opcode - 30]))
                            groundActions[opcode - 30] = null;
                        break;
                    }
                    case 35: case 36: case 37: case 38: case 39: {
                        if (itemActions == null) itemActions = new String[5];
                        itemActions[opcode - 35] = dat.readString();
                        break;
                    }
                    case 40: {
                        int n = dat.readUnsignedByte();
                        origColors = new int[n];
                        modColors = new int[n];
                        for (int c = 0; c < n; c++) {
                            origColors[c] = dat.readUnsignedWord();
                            modColors[c] = dat.readUnsignedWord();
                        }
                        break;
                    }
                    case 78: maleModel3 = dat.readUnsignedWord(); break;
                    case 79: femaleModel3 = dat.readUnsignedWord(); break;
                    case 90: maleHeadModel1 = dat.readUnsignedWord(); break;
                    case 91: femaleHeadModel1 = dat.readUnsignedWord(); break;
                    case 92: maleHeadModel2 = dat.readUnsignedWord(); break;
                    case 93: femaleHeadModel2 = dat.readUnsignedWord(); break;
                    case 95: modelRot2 = dat.readUnsignedWord(); break;
                    case 97: certID = dat.readUnsignedWord(); break;
                    case 98: certTemplate = dat.readUnsignedWord(); break;
                    case 100: {
                        int n = dat.readUnsignedByte();
                        stackIDs = new int[n];
                        stackAmounts = new int[n];
                        for (int c = 0; c < n; c++) {
                            stackIDs[c] = dat.readUnsignedWord();
                            stackAmounts[c] = dat.readUnsignedWord();
                        }
                        break;
                    }
                    case 110: scaleX = dat.readUnsignedWord(); break;
                    case 111: scaleY = dat.readUnsignedWord(); break;
                    case 112: scaleZ = dat.readUnsignedWord(); break;
                    case 113: lightMod = dat.readSignedByte(); break;
                    case 114: shadowMod = dat.readSignedByte() * 5; break;
                    case 115: team = dat.readUnsignedByte(); break;
                    default: break;
                }
            }

            sb.append("\"name\":").append(jsonEscape(name));
            if (description != null) sb.append(",\"description\":").append(jsonEscape(description));
            sb.append(",\"modelID\":").append(modelID);
            sb.append(",\"modelZoom\":").append(modelZoom);
            sb.append(",\"modelRotationY\":").append(modelRotY);
            sb.append(",\"modelRotationX\":").append(modelRotX);
            if (modelRot2 != 0) sb.append(",\"modelRotation2\":").append(modelRot2);
            if (modelOff1 != 0) sb.append(",\"modelOffset1\":").append(modelOff1);
            if (modelOff2 != 0) sb.append(",\"modelOffset2\":").append(modelOff2);
            sb.append(",\"value\":").append(value);
            if (stackable) sb.append(",\"stackable\":true");
            if (members) sb.append(",\"members\":true");
            if (team != 0) sb.append(",\"team\":").append(team);
            if (certID != -1) sb.append(",\"certID\":").append(certID);
            if (certTemplate != -1) sb.append(",\"certTemplateID\":").append(certTemplate);
            if (origColors != null) {
                sb.append(",\"originalColors\":").append(jsonArray(origColors));
                sb.append(",\"modifiedColors\":").append(jsonArray(modColors));
            }
            if (groundActions != null) sb.append(",\"groundActions\":").append(jsonArray(groundActions));
            if (itemActions != null) sb.append(",\"itemActions\":").append(jsonArray(itemActions));
            if (stackIDs != null) {
                sb.append(",\"stackIDs\":").append(jsonArray(stackIDs));
                sb.append(",\"stackAmounts\":").append(jsonArray(stackAmounts));
            }
            if (maleModel1 != -1) sb.append(",\"maleWearModel1\":").append(maleModel1);
            if (maleModel2 != -1) sb.append(",\"maleWearModel2\":").append(maleModel2);
            if (femaleModel1 != -1) sb.append(",\"femaleWearModel1\":").append(femaleModel1);
            if (femaleModel2 != -1) sb.append(",\"femaleWearModel2\":").append(femaleModel2);
            if (maleModel3 != -1) sb.append(",\"maleWearModel3\":").append(maleModel3);
            if (femaleModel3 != -1) sb.append(",\"femaleWearModel3\":").append(femaleModel3);
            if (maleHeadModel1 != -1) sb.append(",\"maleHeadModel1\":").append(maleHeadModel1);
            if (maleHeadModel2 != -1) sb.append(",\"maleHeadModel2\":").append(maleHeadModel2);
            if (femaleHeadModel1 != -1) sb.append(",\"femaleHeadModel1\":").append(femaleHeadModel1);
            if (femaleHeadModel2 != -1) sb.append(",\"femaleHeadModel2\":").append(femaleHeadModel2);
            if (scaleX != 128) sb.append(",\"scaleX\":").append(scaleX);
            if (scaleY != 128) sb.append(",\"scaleY\":").append(scaleY);
            if (scaleZ != 128) sb.append(",\"scaleZ\":").append(scaleZ);
            if (lightMod != 0) sb.append(",\"lightModifier\":").append(lightMod);
            if (shadowMod != 0) sb.append(",\"shadowModifier\":").append(shadowMod);
            sb.append('}');
        }

        sb.append("\n}\n");
        mkdirs(outputPath);
        writeFile(outputPath + "/items.json", sb.toString());
        itemCount = total;
        System.out.println("  Items: " + total + " extracted");
    }

    private static void extractNpcs(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("npc.dat");
        byte[] idxBytes = config.extractFile("npc.idx");
        if (datBytes == null || idxBytes == null) {
            System.out.println("  NPCs: SKIPPED (missing npc.dat/npc.idx)");
            return;
        }

        Stream idx = new Stream(idxBytes);
        int total = idx.readUnsignedWord();
        int[] offsets = new int[total];
        int pos = 2;
        for (int i = 0; i < total; i++) {
            offsets[i] = pos;
            pos += idx.readUnsignedWord();
        }

        Stream dat = new Stream(datBytes);
        StringBuilder sb = new StringBuilder(total * 150);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            dat.position = offsets[id];
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            String name = null;
            String description = null;
            int[] models = null, chatHeadModels = null;
            int standAnim = -1, walkAnim = -1;
            int turnRight = -1, turnAround = -1, turnLeft = -1;
            byte size = 1;
            int combatGrade = -1;
            int modelHeight = 128, modelWidth = 128;
            int[] origColors = null, newColors = null;
            String[] actions = null;
            boolean visibleOnMinimap = true, clickable = true, renderPriority = false;
            int lightMod = 0, shadowMod = 0;
            int mapIconId = -1, mapIconScale = 32;
            int varbitId = -1, configId = -1;
            int[] childrenIDs = null;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: {
                        int n = dat.readUnsignedByte();
                        models = new int[n];
                        for (int j = 0; j < n; j++) models[j] = dat.readUnsignedWord();
                        break;
                    }
                    case 2: name = dat.readString(); break;
                    case 3: description = new String(dat.readBytes()); break;
                    case 12: size = dat.readSignedByte(); break;
                    case 13: standAnim = dat.readUnsignedWord(); break;
                    case 14: walkAnim = dat.readUnsignedWord(); break;
                    case 17:
                        walkAnim = dat.readUnsignedWord();
                        turnRight = dat.readUnsignedWord();
                        turnAround = dat.readUnsignedWord();
                        turnLeft = dat.readUnsignedWord();
                        break;
                    case 30: case 31: case 32: case 33: case 34:
                    case 35: case 36: case 37: case 38: case 39: {
                        if (actions == null) actions = new String[5];
                        actions[op - 30] = dat.readString();
                        if ("hidden".equalsIgnoreCase(actions[op - 30]))
                            actions[op - 30] = null;
                        break;
                    }
                    case 40: {
                        int n = dat.readUnsignedByte();
                        origColors = new int[n];
                        newColors = new int[n];
                        for (int j = 0; j < n; j++) {
                            origColors[j] = dat.readUnsignedWord();
                            newColors[j] = dat.readUnsignedWord();
                        }
                        break;
                    }
                    case 60: {
                        int n = dat.readUnsignedByte();
                        chatHeadModels = new int[n];
                        for (int j = 0; j < n; j++) chatHeadModels[j] = dat.readUnsignedWord();
                        break;
                    }
                    case 90: dat.readUnsignedWord(); break;
                    case 91: dat.readUnsignedWord(); break;
                    case 92: dat.readUnsignedWord(); break;
                    case 93: visibleOnMinimap = false; break;
                    case 95: combatGrade = dat.readUnsignedWord(); break;
                    case 97: modelHeight = dat.readUnsignedWord(); break;
                    case 98: modelWidth = dat.readUnsignedWord(); break;
                    case 99: renderPriority = true; break;
                    case 100: lightMod = dat.readSignedByte(); break;
                    case 101: shadowMod = dat.readSignedByte() * 5; break;
                    case 102: mapIconId = dat.readUnsignedWord(); break;
                    case 103: mapIconScale = dat.readUnsignedWord(); break;
                    case 106: {
                        varbitId = dat.readUnsignedWord();
                        if (varbitId == 65535) varbitId = -1;
                        configId = dat.readUnsignedWord();
                        if (configId == 65535) configId = -1;
                        int n = dat.readUnsignedByte();
                        childrenIDs = new int[n + 1];
                        for (int j = 0; j <= n; j++) {
                            childrenIDs[j] = dat.readUnsignedWord();
                            if (childrenIDs[j] == 65535) childrenIDs[j] = -1;
                        }
                        break;
                    }
                    case 107: clickable = false; break;
                    default: break;
                }
            }

            sb.append("\"name\":").append(jsonEscape(name));
            if (description != null) sb.append(",\"description\":").append(jsonEscape(description));
            if (models != null) sb.append(",\"models\":").append(jsonArray(models));
            sb.append(",\"size\":").append(size);
            if (standAnim != -1) sb.append(",\"standAnim\":").append(standAnim);
            if (walkAnim != -1) sb.append(",\"walkAnim\":").append(walkAnim);
            if (turnRight != -1) sb.append(",\"turnRightAnim\":").append(turnRight);
            if (turnAround != -1) sb.append(",\"turnAroundAnim\":").append(turnAround);
            if (turnLeft != -1) sb.append(",\"turnLeftAnim\":").append(turnLeft);
            if (combatGrade != -1) sb.append(",\"combatLevel\":").append(combatGrade);
            sb.append(",\"modelHeight\":").append(modelHeight);
            sb.append(",\"modelWidth\":").append(modelWidth);
            if (origColors != null) {
                sb.append(",\"originalColors\":").append(jsonArray(origColors));
                sb.append(",\"newColors\":").append(jsonArray(newColors));
            }
            if (actions != null) sb.append(",\"actions\":").append(jsonArray(actions));
            if (chatHeadModels != null) sb.append(",\"chatHeadModels\":").append(jsonArray(chatHeadModels));
            if (!visibleOnMinimap) sb.append(",\"visibleOnMinimap\":false");
            if (!clickable) sb.append(",\"clickable\":false");
            if (renderPriority) sb.append(",\"renderPriority\":true");
            if (lightMod != 0) sb.append(",\"lightModifier\":").append(lightMod);
            if (shadowMod != 0) sb.append(",\"shadowModifier\":").append(shadowMod);
            if (mapIconId != -1) sb.append(",\"mapIconId\":").append(mapIconId);
            if (mapIconScale != 32) sb.append(",\"mapIconScale\":").append(mapIconScale);
            if (varbitId != -1) sb.append(",\"varbitId\":").append(varbitId);
            if (configId != -1) sb.append(",\"configId\":").append(configId);
            if (childrenIDs != null) sb.append(",\"childrenIDs\":").append(jsonArray(childrenIDs));
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/npcs.json", sb.toString());
        npcCount = total;
        System.out.println("  NPCs: " + total + " extracted");
    }

    private static void extractObjects(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("loc.dat");
        byte[] idxBytes = config.extractFile("loc.idx");
        if (datBytes == null || idxBytes == null) {
            System.out.println("  Objects: SKIPPED (missing loc.dat/loc.idx)");
            return;
        }

        Stream idx = new Stream(idxBytes);
        int total = idx.readUnsignedWord();
        int[] offsets = new int[total];
        int pos = 2;
        for (int i = 0; i < total; i++) {
            offsets[i] = pos;
            pos += idx.readUnsignedWord();
        }

        Stream dat = new Stream(datBytes);
        StringBuilder sb = new StringBuilder(total * 150);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            dat.position = offsets[id];
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            String name = null;
            String description = null;
            int[] modelIds = null, modelTypes = null;
            int sizeX = 1, sizeY = 1;
            boolean clipFlag = true, blocksProjectiles = true;
            boolean hasActions = false, rotateHeights = false;
            boolean blocksMovement = false, castsShadow = true;
            int animationId = -1, wallThickness = 16;
            int[] origColors = null, modColors = null;
            String[] actions = null;
            int scaleX = 128, scaleY = 128, scaleZ = 128;
            int translateX = 0, translateY = 0, translateZ = 0;
            int varbitId = -1, configId = -1;
            int[] childIds = null;
            int mapFunction = -1, mapScene = -1;
            int ambient = 0, contrast = 0;
            boolean mirrored = false, obstructsGround = false;
            int supportItems = -1;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: {
                        int n = dat.readUnsignedByte();
                        if (n > 0) {
                            modelIds = new int[n];
                            modelTypes = new int[n];
                            for (int j = 0; j < n; j++) {
                                modelIds[j] = dat.readUnsignedWord();
                                modelTypes[j] = dat.readUnsignedByte();
                            }
                        }
                        break;
                    }
                    case 2: name = dat.readString(); break;
                    case 3: description = new String(dat.readBytes()); break;
                    case 5: {
                        int n = dat.readUnsignedByte();
                        if (n > 0) {
                            modelIds = new int[n];
                            modelTypes = null;
                            for (int j = 0; j < n; j++)
                                modelIds[j] = dat.readUnsignedWord();
                        }
                        break;
                    }
                    case 14: sizeX = dat.readUnsignedByte(); break;
                    case 15: sizeY = dat.readUnsignedByte(); break;
                    case 17: clipFlag = false; break;
                    case 18: blocksProjectiles = false; break;
                    case 19: hasActions = (dat.readUnsignedByte() == 1); break;
                    case 21: rotateHeights = true; break;
                    case 22: break; // aBoolean769 = false
                    case 23: blocksMovement = true; break;
                    case 24: {
                        animationId = dat.readUnsignedWord();
                        if (animationId == 65535) animationId = -1;
                        break;
                    }
                    case 28: wallThickness = dat.readUnsignedByte(); break;
                    case 29: ambient = dat.readSignedByte(); break;
                    case 39: contrast = dat.readSignedByte(); break;
                    case 30: case 31: case 32: case 33: case 34:
                    case 35: case 36: case 37: case 38: {
                        if (actions == null) actions = new String[5];
                        actions[op - 30] = dat.readString();
                        if ("hidden".equalsIgnoreCase(actions[op - 30]))
                            actions[op - 30] = null;
                        break;
                    }
                    case 40: {
                        int n = dat.readUnsignedByte();
                        origColors = new int[n];
                        modColors = new int[n];
                        for (int j = 0; j < n; j++) {
                            modColors[j] = dat.readUnsignedWord();
                            origColors[j] = dat.readUnsignedWord();
                        }
                        break;
                    }
                    case 60: mapFunction = dat.readUnsignedWord(); break;
                    case 62: mirrored = true; break;
                    case 64: castsShadow = false; break;
                    case 65: scaleX = dat.readUnsignedWord(); break;
                    case 66: scaleY = dat.readUnsignedWord(); break;
                    case 67: scaleZ = dat.readUnsignedWord(); break;
                    case 68: mapScene = dat.readUnsignedWord(); break;
                    case 69: dat.readUnsignedByte(); break;
                    case 70: translateX = dat.readSignedWord(); break;
                    case 71: translateY = dat.readSignedWord(); break;
                    case 72: translateZ = dat.readSignedWord(); break;
                    case 73: obstructsGround = true; break;
                    case 74: break; // aBoolean766 = true
                    case 75: supportItems = dat.readUnsignedByte(); break;
                    case 77: {
                        varbitId = dat.readUnsignedWord();
                        if (varbitId == 65535) varbitId = -1;
                        configId = dat.readUnsignedWord();
                        if (configId == 65535) configId = -1;
                        int n = dat.readUnsignedByte();
                        childIds = new int[n + 1];
                        for (int j = 0; j <= n; j++) {
                            childIds[j] = dat.readUnsignedWord();
                            if (childIds[j] == 65535) childIds[j] = -1;
                        }
                        break;
                    }
                    default: break;
                }
            }

            sb.append("\"name\":").append(jsonEscape(name));
            if (description != null) sb.append(",\"description\":").append(jsonEscape(description));
            if (modelIds != null) sb.append(",\"modelIds\":").append(jsonArray(modelIds));
            if (modelTypes != null) sb.append(",\"modelTypes\":").append(jsonArray(modelTypes));
            sb.append(",\"sizeX\":").append(sizeX).append(",\"sizeY\":").append(sizeY);
            if (!clipFlag) sb.append(",\"clipFlag\":false");
            if (!blocksProjectiles) sb.append(",\"blocksProjectiles\":false");
            if (hasActions) sb.append(",\"hasActions\":true");
            if (rotateHeights) sb.append(",\"rotateHeights\":true");
            if (blocksMovement) sb.append(",\"blocksMovement\":true");
            if (!castsShadow) sb.append(",\"castsShadow\":false");
            if (animationId != -1) sb.append(",\"animationId\":").append(animationId);
            if (wallThickness != 16) sb.append(",\"wallThickness\":").append(wallThickness);
            if (ambient != 0) sb.append(",\"ambient\":").append(ambient);
            if (contrast != 0) sb.append(",\"contrast\":").append(contrast);
            if (origColors != null) {
                sb.append(",\"originalColors\":").append(jsonArray(origColors));
                sb.append(",\"modifiedColors\":").append(jsonArray(modColors));
            }
            if (actions != null) sb.append(",\"actions\":").append(jsonArray(actions));
            if (scaleX != 128) sb.append(",\"scaleX\":").append(scaleX);
            if (scaleY != 128) sb.append(",\"scaleY\":").append(scaleY);
            if (scaleZ != 128) sb.append(",\"scaleZ\":").append(scaleZ);
            if (translateX != 0) sb.append(",\"translateX\":").append(translateX);
            if (translateY != 0) sb.append(",\"translateY\":").append(translateY);
            if (translateZ != 0) sb.append(",\"translateZ\":").append(translateZ);
            if (mirrored) sb.append(",\"mirrored\":true");
            if (obstructsGround) sb.append(",\"obstructsGround\":true");
            if (mapFunction != -1) sb.append(",\"mapFunction\":").append(mapFunction);
            if (mapScene != -1) sb.append(",\"mapScene\":").append(mapScene);
            if (supportItems != -1) sb.append(",\"supportItems\":").append(supportItems);
            if (varbitId != -1) sb.append(",\"varbitId\":").append(varbitId);
            if (configId != -1) sb.append(",\"configId\":").append(configId);
            if (childIds != null) sb.append(",\"childIds\":").append(jsonArray(childIds));
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/objects.json", sb.toString());
        objectCount = total;
        System.out.println("  Objects: " + total + " extracted");
    }

    private static void extractAnimations(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("seq.dat");
        if (datBytes == null) {
            System.out.println("  Animations: SKIPPED (missing seq.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 100);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int frameCount = 0;
            int[] frameIds = null;
            int[] frameDurations = null;
            int loopOffset = -1;
            int[] interleaveOrder = null;
            boolean resetOnMove = false;
            int priority1 = 5, anInt360 = -1, anInt361 = -1;
            int maxLoops = 99, priority2 = -1, anInt364 = -1, anInt365 = 1;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: {
                        frameCount = dat.readUnsignedWord();
                        frameIds = new int[frameCount];
                        frameDurations = new int[frameCount];
                        for (int j = 0; j < frameCount; j++)
                            frameIds[j] = dat.readDWord();
                        for (int j = 0; j < frameCount; j++)
                            frameDurations[j] = dat.readUnsignedByte();
                        break;
                    }
                    case 2: loopOffset = dat.readUnsignedWord(); break;
                    case 3: {
                        int n = dat.readUnsignedByte();
                        interleaveOrder = new int[n];
                        for (int j = 0; j < n; j++)
                            interleaveOrder[j] = dat.readUnsignedByte();
                        break;
                    }
                    case 4: resetOnMove = true; break;
                    case 5: priority1 = dat.readUnsignedByte(); break;
                    case 6: anInt360 = dat.readUnsignedWord(); break;
                    case 7: anInt361 = dat.readUnsignedWord(); break;
                    case 8: maxLoops = dat.readUnsignedByte(); break;
                    case 9: priority2 = dat.readUnsignedByte(); break;
                    case 10: anInt364 = dat.readUnsignedByte(); break;
                    case 11: anInt365 = dat.readUnsignedByte(); break;
                    case 12: dat.readDWord(); break;
                    default: break;
                }
            }

            sb.append("\"frameCount\":").append(frameCount);
            if (frameIds != null) sb.append(",\"frameIds\":").append(jsonArray(frameIds));
            if (frameDurations != null) sb.append(",\"frameDurations\":").append(jsonArray(frameDurations));
            if (loopOffset != -1) sb.append(",\"loopOffset\":").append(loopOffset);
            if (interleaveOrder != null) sb.append(",\"interleaveOrder\":").append(jsonArray(interleaveOrder));
            if (resetOnMove) sb.append(",\"resetOnMove\":true");
            sb.append(",\"priority\":").append(priority1);
            if (anInt360 != -1) sb.append(",\"playerOffhand\":").append(anInt360);
            if (anInt361 != -1) sb.append(",\"playerMainhand\":").append(anInt361);
            sb.append(",\"maxLoops\":").append(maxLoops);
            if (priority2 != -1) sb.append(",\"animatingPrecedence\":").append(priority2);
            if (anInt364 != -1) sb.append(",\"walkingPrecedence\":").append(anInt364);
            sb.append(",\"replayMode\":").append(anInt365);
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/animations.json", sb.toString());
        animCount = total;
        System.out.println("  Animations: " + total + " extracted");
    }

    private static void extractGraphics(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("spotanim.dat");
        if (datBytes == null) {
            System.out.println("  Graphics: SKIPPED (missing spotanim.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 80);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int modelId = 0, animationId = -1;
            int resizeX = 128, resizeY = 128, rotation = 0;
            int ambient = 0, contrast = 0;
            int[] origColors = new int[6], replColors = new int[6];
            int colorCount = 0;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: modelId = dat.readUnsignedWord(); break;
                    case 2: animationId = dat.readUnsignedWord(); break;
                    case 4: resizeX = dat.readUnsignedWord(); break;
                    case 5: resizeY = dat.readUnsignedWord(); break;
                    case 6: rotation = dat.readUnsignedWord(); break;
                    case 7: ambient = dat.readUnsignedWord(); break;
                    case 8: contrast = dat.readUnsignedWord(); break;
                    case 40: {
                        colorCount = dat.readUnsignedByte();
                        for (int j = 0; j < colorCount; j++) {
                            origColors[j] = dat.readUnsignedWord();
                            replColors[j] = dat.readUnsignedWord();
                        }
                        break;
                    }
                    default: break;
                }
            }

            sb.append("\"modelId\":").append(modelId);
            if (animationId != -1) sb.append(",\"animationId\":").append(animationId);
            sb.append(",\"resizeX\":").append(resizeX).append(",\"resizeY\":").append(resizeY);
            if (rotation != 0) sb.append(",\"rotation\":").append(rotation);
            if (ambient != 0) sb.append(",\"ambient\":").append(ambient);
            if (contrast != 0) sb.append(",\"contrast\":").append(contrast);
            if (colorCount > 0) {
                sb.append(",\"originalColors\":").append(jsonArray(Arrays.copyOf(origColors, colorCount)));
                sb.append(",\"replacementColors\":").append(jsonArray(Arrays.copyOf(replColors, colorCount)));
            }
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/graphics.json", sb.toString());
        graphicsCount = total;
        System.out.println("  Graphics: " + total + " extracted");
    }

    private static void extractFloors(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("flo.dat");
        if (datBytes == null) {
            System.out.println("  Floor underlays: SKIPPED (missing flo.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 60);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int rgb = 0;
            int textureId = -1;
            boolean occludes = true;
            String name = null;
            int secondaryRgb = -1;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: rgb = dat.read3Bytes(); break;
                    case 2: textureId = dat.readUnsignedByte(); break;
                    case 3: break; // large texture, no-op
                    case 5: occludes = false; break;
                    case 6: name = dat.readString(); break;
                    case 7: secondaryRgb = dat.read3Bytes(); break;
                    default: break;
                }
            }

            sb.append("\"rgb\":").append(rgb);
            sb.append(",\"rgbHex\":").append(jsonEscape(String.format("#%06X", rgb)));
            if (textureId != -1) sb.append(",\"textureId\":").append(textureId);
            if (!occludes) sb.append(",\"occludes\":false");
            if (name != null) sb.append(",\"name\":").append(jsonEscape(name));
            if (secondaryRgb != -1) {
                sb.append(",\"secondaryRgb\":").append(secondaryRgb);
                sb.append(",\"secondaryRgbHex\":").append(jsonEscape(String.format("#%06X", secondaryRgb)));
            }
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/floors_underlay.json", sb.toString());
        underlayCount = total;
        System.out.println("  Floor underlays: " + total + " extracted");
    }

    private static void extractOverlayFloors(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("flo2.dat");
        if (datBytes == null) {
            System.out.println("  Floor overlays: SKIPPED (missing flo2.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 60);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int rgb = 0, textureId = -1;
            boolean hideUnderlay = true;
            int secondaryRgb = -1, blendColor = -1;
            int scale = 0;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: rgb = dat.read3Bytes(); break;
                    case 2: textureId = dat.readUnsignedByte(); break;
                    case 3: {
                        textureId = dat.readUnsignedWord();
                        if (textureId == 65535) textureId = -1;
                        break;
                    }
                    case 4: break;
                    case 5: hideUnderlay = false; break;
                    case 6: break;
                    case 7: secondaryRgb = dat.read3Bytes(); break;
                    case 8: break;
                    case 9: scale = dat.readUnsignedWord(); break;
                    case 10: break;
                    case 11: dat.readUnsignedByte(); break;
                    case 12: break;
                    case 13: blendColor = dat.read3Bytes(); break;
                    case 14: dat.readUnsignedByte(); break;
                    case 15: {
                        int v = dat.readUnsignedWord();
                        if (v == 65535) v = -1;
                        break;
                    }
                    case 16: dat.readUnsignedByte(); break;
                    default: break;
                }
            }

            sb.append("\"rgb\":").append(rgb);
            sb.append(",\"rgbHex\":").append(jsonEscape(String.format("#%06X", rgb)));
            if (textureId != -1) sb.append(",\"textureId\":").append(textureId);
            if (!hideUnderlay) sb.append(",\"hideUnderlay\":false");
            if (secondaryRgb != -1) sb.append(",\"secondaryRgb\":").append(secondaryRgb);
            if (blendColor != -1) sb.append(",\"blendColor\":").append(blendColor);
            if (scale != 0) sb.append(",\"scale\":").append(scale);
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/floors_overlay.json", sb.toString());
        overlayCount = total;
        System.out.println("  Floor overlays: " + total + " extracted");
    }

    private static void extractIdentityKits(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("idk.dat");
        if (datBytes == null) {
            System.out.println("  Identity kits: SKIPPED (missing idk.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 80);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int bodyPart = -1;
            int[] models = null;
            boolean disabled = false;
            int[] origColors = new int[6], replColors = new int[6];
            int[] headModels = {-1, -1, -1, -1, -1};

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                if (op == 1) bodyPart = dat.readUnsignedByte();
                else if (op == 2) {
                    int n = dat.readUnsignedByte();
                    models = new int[n];
                    for (int j = 0; j < n; j++) models[j] = dat.readUnsignedWord();
                } else if (op == 3) disabled = true;
                else if (op >= 40 && op < 50) origColors[op - 40] = dat.readUnsignedWord();
                else if (op >= 50 && op < 60) replColors[op - 50] = dat.readUnsignedWord();
                else if (op >= 60 && op < 70) headModels[op - 60] = dat.readUnsignedWord();
            }

            sb.append("\"bodyPart\":").append(bodyPart);
            if (models != null) sb.append(",\"models\":").append(jsonArray(models));
            if (disabled) sb.append(",\"disabled\":true");
            sb.append(",\"originalColors\":").append(jsonArray(origColors));
            sb.append(",\"replacementColors\":").append(jsonArray(replColors));
            sb.append(",\"headModels\":").append(jsonArray(headModels));
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/identity_kits.json", sb.toString());
        idkCount = total;
        System.out.println("  Identity kits: " + total + " extracted");
    }

    private static void extractVarps(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("varp.dat");
        if (datBytes == null) {
            System.out.println("  Varps: SKIPPED (missing varp.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 40);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int configType = 0;
            boolean isClientScript = false;

            while (true) {
                int op = dat.readUnsignedByte();
                if (op == 0) break;
                switch (op) {
                    case 1: dat.readUnsignedByte(); break;
                    case 2: dat.readUnsignedByte(); break;
                    case 3: isClientScript = true; break;
                    case 4: break;
                    case 5: configType = dat.readUnsignedWord(); break;
                    case 6: break;
                    case 7: dat.readDWord(); break;
                    case 8: break;
                    case 10: dat.readString(); break;
                    case 11: break;
                    case 12: dat.readDWord(); break;
                    case 13: break;
                    default: break;
                }
            }

            sb.append("\"configType\":").append(configType);
            if (isClientScript) sb.append(",\"isClientScript\":true");
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/varps.json", sb.toString());
        varpCount = total;
        System.out.println("  Varps: " + total + " extracted");
    }

    private static void extractVarBits(ArchiveLoader config) throws IOException {
        byte[] datBytes = config.extractFile("varbit.dat");
        if (datBytes == null) {
            System.out.println("  VarBits: SKIPPED (missing varbit.dat)");
            return;
        }

        Stream dat = new Stream(datBytes);
        int total = dat.readUnsignedWord();
        StringBuilder sb = new StringBuilder(total * 40);
        sb.append("{\n");

        for (int id = 0; id < total; id++) {
            if (id > 0) sb.append(",\n");
            sb.append("  ").append('"').append(id).append("\": {");

            int baseVar = dat.readUnsignedWord();
            int startBit = dat.readUnsignedByte();
            int endBit = dat.readUnsignedByte();

            sb.append("\"baseVar\":").append(baseVar);
            sb.append(",\"startBit\":").append(startBit);
            sb.append(",\"endBit\":").append(endBit);
            sb.append('}');
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/varbits.json", sb.toString());
        varbitCount = total;
        System.out.println("  VarBits: " + total + " extracted");
    }

    private static void extractMapIndex(ArchiveLoader versionlist) throws IOException {
        byte[] mapData = versionlist.extractFile("map_index");
        if (mapData == null) {
            System.out.println("  Map index: SKIPPED (missing map_index)");
            return;
        }

        // Determine entry size: try 7 (with members flag), then 6, then 4
        int entrySize;
        if (mapData.length % 7 == 0) entrySize = 7;
        else if (mapData.length % 6 == 0) entrySize = 6;
        else entrySize = 6; // best guess

        int total = mapData.length / entrySize;
        StringBuilder sb = new StringBuilder(total * 50);
        sb.append("{\n  \"entrySize\": ").append(entrySize).append(",\n  \"regions\": [\n");

        int mpos = 0;
        for (int i = 0; i < total; i++) {
            if (mpos + entrySize > mapData.length) break;
            if (i > 0) sb.append(",\n");
            int regionId = ((mapData[mpos] & 0xff) << 8) | (mapData[mpos + 1] & 0xff); mpos += 2;
            int landscapeFile = ((mapData[mpos] & 0xff) << 8) | (mapData[mpos + 1] & 0xff); mpos += 2;
            int objectsFile = ((mapData[mpos] & 0xff) << 8) | (mapData[mpos + 1] & 0xff); mpos += 2;
            int regionX = (regionId >> 8) & 0xFF;
            int regionY = regionId & 0xFF;

            sb.append("    {\"regionId\":").append(regionId);
            sb.append(",\"regionX\":").append(regionX);
            sb.append(",\"regionY\":").append(regionY);
            sb.append(",\"landscapeFile\":").append(landscapeFile);
            sb.append(",\"objectsFile\":").append(objectsFile);
            if (entrySize == 7) {
                int members = mapData[mpos] & 0xff; mpos++;
                sb.append(",\"members\":").append(members != 0);
            }
            sb.append('}');
        }

        sb.append("\n  ]\n}\n");
        writeFile(outputPath + "/map_index.json", sb.toString());
        System.out.println("  Map index: " + total + " regions extracted");
    }

    // ═════════════════════════════════════════════════════════════════
    // PHASE 3: IMAGE ASSET EXTRACTORS
    // ═════════════════════════════════════════════════════════════════

    private static void extractCacheSprites() throws IOException {
        String idxPath = cachePath + "/sprites.idx";
        String datPath = cachePath + "/sprites.dat";

        if (!new File(idxPath).exists() || !new File(datPath).exists()) {
            System.out.println("  Cache sprites: SKIPPED (missing sprites.idx/sprites.dat)");
            return;
        }

        byte[] idxRaw = readFileBytes(idxPath);
        byte[] datRaw = readFileBytes(datPath);

        byte[] idxData = gzipDecompress(idxRaw);
        byte[] datData = gzipDecompress(datRaw);

        DataInputStream idxStream = new DataInputStream(new ByteArrayInputStream(idxData));
        DataInputStream datStream = new DataInputStream(new ByteArrayInputStream(datData));

        int total = idxStream.readInt();
        String spriteDir = outputPath + "/sprites";
        mkdirs(spriteDir);

        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;

        for (int i = 0; i < total; i++) {
            int spriteId = idxStream.readInt();

            // Parse opcode-based format
            String name = "sprite";
            int drawOffsetX = 0, drawOffsetY = 0;
            byte[] spriteData = null;

            while (true) {
                int opCode = datStream.readByte();
                if (opCode == 0) break;
                if (opCode == 1) datStream.readShort(); // id (redundant)
                else if (opCode == 2) name = datStream.readUTF();
                else if (opCode == 3) drawOffsetX = datStream.readShort();
                else if (opCode == 4) drawOffsetY = datStream.readShort();
                else if (opCode == 5) {
                    int dataLen = idxStream.readInt();
                    spriteData = new byte[dataLen];
                    datStream.readFully(spriteData);
                }
            }

            if (spriteData != null && spriteData.length > 0) {
                String filename = spriteId + ".png";
                // Try to detect format - write raw bytes (they are PNG/JPEG)
                try {
                    writeFile(spriteDir + "/" + filename, spriteData);
                    if (extracted > 0) indexJson.append(",\n");
                    indexJson.append("  ").append('"').append(spriteId).append("\": {");
                    indexJson.append("\"name\":").append(jsonEscape(name));
                    indexJson.append(",\"file\":").append(jsonEscape(filename));
                    indexJson.append(",\"offsetX\":").append(drawOffsetX);
                    indexJson.append(",\"offsetY\":").append(drawOffsetY);
                    indexJson.append(",\"size\":").append(spriteData.length);
                    indexJson.append('}');
                    extracted++;
                } catch (Exception e) {
                    System.err.println("    Warning: Failed to write sprite " + spriteId + ": " + e.getMessage());
                }
            }
        }

        indexJson.append("\n}\n");
        writeFile(spriteDir + "/_index.json", indexJson.toString());
        spriteCount = extracted;
        System.out.println("  Cache sprites: " + extracted + " extracted");
    }

    private static void extractMediaSprites(ArchiveLoader media) throws IOException {
        byte[] indexDat = media.extractFile("index.dat");
        if (indexDat == null) {
            System.out.println("  Media sprites: SKIPPED (missing index.dat in media archive)");
            return;
        }

        String mediaDir = outputPath + "/media_sprites";
        mkdirs(mediaDir);
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;

        for (String spriteName : MEDIA_SPRITE_NAMES) {
            byte[] spriteDat = media.extractFile(spriteName + ".dat");
            if (spriteDat == null) continue;

            try {
                Stream dat = new Stream(spriteDat);
                Stream idx = new Stream(indexDat);
                idx.position = dat.readUnsignedWord();

                int fullWidth = idx.readUnsignedWord();
                int fullHeight = idx.readUnsignedWord();
                int paletteSize = idx.readUnsignedByte();

                int[] palette = new int[paletteSize];
                for (int i = 0; i < paletteSize - 1; i++) {
                    palette[i + 1] = idx.read3Bytes();
                    if (palette[i + 1] == 0) palette[i + 1] = 1;
                }

                // Extract each sub-sprite
                int subIndex = 0;
                while (dat.position < spriteDat.length && idx.position < indexDat.length) {
                    int offsetX, offsetY, width, height, format;
                    try {
                        offsetX = idx.readUnsignedByte();
                        offsetY = idx.readUnsignedByte();
                        width = idx.readUnsignedWord();
                        height = idx.readUnsignedWord();
                        format = idx.readUnsignedByte();
                    } catch (Exception e) {
                        break;
                    }

                    if (width == 0 || height == 0) break;
                    int pixelCount = width * height;
                    if (dat.position + pixelCount > spriteDat.length) break;

                    int[] pixels = new int[pixelCount];
                    if (format == 0) {
                        for (int p = 0; p < pixelCount; p++)
                            pixels[p] = palette[dat.readUnsignedByte()];
                    } else if (format == 1) {
                        for (int x = 0; x < width; x++)
                            for (int y = 0; y < height; y++)
                                pixels[x + y * width] = palette[dat.readUnsignedByte()];
                    } else {
                        break;
                    }

                    // Convert to BufferedImage and save as PNG
                    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                    for (int y = 0; y < height; y++) {
                        for (int x = 0; x < width; x++) {
                            int rgb = pixels[x + y * width];
                            img.setRGB(x, y, rgb == 0 ? 0x00000000 : (0xFF000000 | rgb));
                        }
                    }

                    String filename = spriteName + "_" + subIndex + ".png";
                    ImageIO.write(img, "png", new File(mediaDir + "/" + filename));

                    if (extracted > 0) indexJson.append(",\n");
                    indexJson.append("  ").append('"').append(spriteName).append("_").append(subIndex).append("\": {");
                    indexJson.append("\"name\":").append(jsonEscape(spriteName));
                    indexJson.append(",\"subIndex\":").append(subIndex);
                    indexJson.append(",\"width\":").append(width);
                    indexJson.append(",\"height\":").append(height);
                    indexJson.append(",\"offsetX\":").append(offsetX);
                    indexJson.append(",\"offsetY\":").append(offsetY);
                    indexJson.append(",\"fullWidth\":").append(fullWidth);
                    indexJson.append(",\"fullHeight\":").append(fullHeight);
                    indexJson.append(",\"file\":").append(jsonEscape(filename));
                    indexJson.append('}');
                    extracted++;
                    subIndex++;
                }
            } catch (Exception e) {
                System.err.println("    Warning: Failed to extract media sprite '" + spriteName + "': " + e.getMessage());
            }
        }

        indexJson.append("\n}\n");
        writeFile(mediaDir + "/_index.json", indexJson.toString());
        mediaSpriteCount = extracted;
        System.out.println("  Media sprites: " + extracted + " extracted from " + MEDIA_SPRITE_NAMES.length + " names");
    }

    private static void extractTextures(ArchiveLoader textureArchive) throws IOException {
        byte[] indexDat = textureArchive.extractFile("index.dat");
        if (indexDat == null) {
            System.out.println("  Textures: SKIPPED (missing index.dat in textures archive)");
            return;
        }

        String texDir = outputPath + "/textures";
        mkdirs(texDir);
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;

        for (int texId = 0; texId < 51; texId++) {
            byte[] texDat = textureArchive.extractFile(texId + ".dat");
            if (texDat == null) continue;

            try {
                Stream dat = new Stream(texDat);
                Stream idx = new Stream(indexDat);
                idx.position = dat.readUnsignedWord();

                int fullWidth = idx.readUnsignedWord();
                int fullHeight = idx.readUnsignedWord();
                int paletteSize = idx.readUnsignedByte();

                int[] palette = new int[paletteSize];
                for (int i = 0; i < paletteSize - 1; i++) {
                    palette[i + 1] = idx.read3Bytes();
                    if (palette[i + 1] == 0) palette[i + 1] = 1;
                }

                int offsetX = idx.readUnsignedByte();
                int offsetY = idx.readUnsignedByte();
                int width = idx.readUnsignedWord();
                int height = idx.readUnsignedWord();
                int format = idx.readUnsignedByte();

                if (width == 0 || height == 0) continue;
                int pixelCount = width * height;
                if (dat.position + pixelCount > texDat.length) continue;

                int[] pixels = new int[pixelCount];
                if (format == 0) {
                    for (int p = 0; p < pixelCount; p++)
                        pixels[p] = palette[dat.readUnsignedByte()];
                } else if (format == 1) {
                    for (int x = 0; x < width; x++)
                        for (int y = 0; y < height; y++)
                            pixels[x + y * width] = palette[dat.readUnsignedByte()];
                }

                BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int y = 0; y < height; y++)
                    for (int x = 0; x < width; x++)
                        img.setRGB(x, y, pixels[x + y * width]);

                String filename = texId + ".png";
                ImageIO.write(img, "png", new File(texDir + "/" + filename));

                if (extracted > 0) indexJson.append(",\n");
                indexJson.append("  ").append('"').append(texId).append("\": {");
                indexJson.append("\"width\":").append(width);
                indexJson.append(",\"height\":").append(height);
                indexJson.append(",\"file\":").append(jsonEscape(filename));
                indexJson.append('}');
                extracted++;
            } catch (Exception e) {
                System.err.println("    Warning: Failed to extract texture " + texId + ": " + e.getMessage());
            }
        }

        indexJson.append("\n}\n");
        writeFile(texDir + "/_index.json", indexJson.toString());
        textureCount = extracted;
        System.out.println("  Textures: " + extracted + " extracted");
    }

    // ═════════════════════════════════════════════════════════════════
    // PHASE 4: BINARY ASSET EXTRACTORS
    // ═════════════════════════════════════════════════════════════════

    private static void extractModels() throws IOException {
        String modelDir = outputPath + "/models";
        mkdirs(modelDir);
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;

        // Models are in idx1 (decompressors[1])
        // First, probe a few models to determine format
        int decodedCount = 0;
        for (int modelId = 0; modelId < 80000; modelId++) {
            byte[] rawData = decompressors[1].decompress(modelId);
            if (rawData == null || rawData.length < 2) continue;

            // Model data in this cache is GZIP compressed
            byte[] data;
            try {
                data = gzipDecompress(rawData);
            } catch (Exception e) {
                // Not gzip - try raw
                data = rawData;
            }
            if (data.length < 20) continue;

            try {
                boolean isNewFormat = data[data.length - 1] == -1
                    && data[data.length - 2] == -1
                    && data.length >= 23;

                int vertexCount, triangleCount, textureTriCount;
                String format;

                if (isNewFormat) {
                    // 23-byte footer (RS 525/622 format)
                    Stream s = new Stream(data);
                    s.position = data.length - 23;
                    vertexCount = s.readUnsignedWord();
                    triangleCount = s.readUnsignedWord();
                    textureTriCount = s.readUnsignedByte();
                    format = "new_622";
                } else {
                    // 18-byte footer (old RS 317 format)
                    Stream s = new Stream(data);
                    s.position = data.length - 18;
                    vertexCount = s.readUnsignedWord();
                    triangleCount = s.readUnsignedWord();
                    textureTriCount = s.readUnsignedByte();
                    format = "old_317";
                }

                // Sanity check: vertex + triangle counts must be plausible
                if (vertexCount <= 0 || triangleCount <= 0 || vertexCount > 65535 || triangleCount > 65535) continue;
                // Minimum data: vertex flags + tri types + tri colors(2each) + footer
                int footerSize = isNewFormat ? 23 : 18;
                int minSize = vertexCount + triangleCount * 3 + footerSize;
                if (minSize > data.length) continue;

                // Full geometry decode
                String modelJson = decodeModelToJson(data, modelId, format, vertexCount, triangleCount, textureTriCount);
                if (modelJson != null) {
                    writeFile(modelDir + "/" + modelId + ".json", modelJson);
                    decodedCount++;
                }

                if (extracted > 0) indexJson.append(",\n");
                indexJson.append("  ").append('"').append(modelId).append("\": {");
                indexJson.append("\"vertexCount\":").append(vertexCount);
                indexJson.append(",\"triangleCount\":").append(triangleCount);
                indexJson.append(",\"textureTriangleCount\":").append(textureTriCount);
                indexJson.append(",\"format\":").append(jsonEscape(format));
                indexJson.append(",\"size\":").append(data.length);
                indexJson.append('}');
                extracted++;
            } catch (Exception e) {
                // Silently skip malformed models
            }
        }
        System.out.println("    (Full geometry decoded for " + decodedCount + " models)");

        indexJson.append("\n}\n");
        writeFile(modelDir + "/_index.json", indexJson.toString());
        modelCount = extracted;
        System.out.println("  Models: " + extracted + " extracted");
    }

    private static String decodeModelToJson(byte[] data, int modelId, String format,
                                             int vertexCount, int triangleCount, int texTriCount) {
        try {
            if ("old_317".equals(format)) {
                return decodeOldModelToJson(data, vertexCount, triangleCount, texTriCount);
            } else {
                // For new format models, export raw metadata + binary size
                // Full 622 decode is very complex; export header info
                return decodeNewModelToJson(data, vertexCount, triangleCount, texTriCount);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private static String decodeOldModelToJson(byte[] data, int vertexCount, int triangleCount, int texTriCount) {
        Stream footer = new Stream(data);
        footer.position = data.length - 18;
        footer.readUnsignedWord(); // vertexCount (already have)
        footer.readUnsignedWord(); // triangleCount (already have)
        footer.readUnsignedByte(); // texTriCount (already have)
        int hasTriangleInfo = footer.readUnsignedByte();
        int priorityFlag = footer.readUnsignedByte();
        int hasAlpha = footer.readUnsignedByte();
        int hasTriLabels = footer.readUnsignedByte();
        int hasVertLabels = footer.readUnsignedByte();
        int vertexXSize = footer.readUnsignedWord();
        int vertexYSize = footer.readUnsignedWord();
        int vertexZSize = footer.readUnsignedWord();
        int triVertSize = footer.readUnsignedWord();

        // Calculate offsets (mirrors cacheModelHeader)
        int offset = 0;
        int vertexFlagsOff = offset; offset += vertexCount;
        int triTypesOff = offset; offset += triangleCount;
        int triPrioritiesOff = offset;
        boolean perTriPriority = (priorityFlag == 255);
        if (perTriPriority) offset += triangleCount;
        int triLabelsOff = offset;
        if (hasTriLabels == 1) offset += triangleCount;
        int triInfoOff = offset;
        if (hasTriangleInfo == 1) offset += triangleCount;
        int vertLabelsOff = offset;
        if (hasVertLabels == 1) offset += vertexCount;
        int triAlphaOff = offset;
        if (hasAlpha == 1) offset += triangleCount;
        int triVertOff = offset; offset += triVertSize;
        int triColorsOff = offset; offset += triangleCount * 2;
        int texTriOff = offset; offset += texTriCount * 6;
        int vertXOff = offset; offset += vertexXSize;
        int vertYOff = offset; offset += vertexYSize;
        int vertZOff = offset;

        // Decode vertices (delta-encoded)
        Stream flagStream = new Stream(data); flagStream.position = vertexFlagsOff;
        Stream xStream = new Stream(data); xStream.position = vertXOff;
        Stream yStream = new Stream(data); yStream.position = vertYOff;
        Stream zStream = new Stream(data); zStream.position = vertZOff;

        int[] vx = new int[vertexCount];
        int[] vy = new int[vertexCount];
        int[] vz = new int[vertexCount];
        int cx = 0, cy = 0, cz = 0;
        for (int i = 0; i < vertexCount; i++) {
            int flags = flagStream.readUnsignedByte();
            if ((flags & 1) != 0) cx += xStream.readSmartSigned();
            if ((flags & 2) != 0) cy += yStream.readSmartSigned();
            if ((flags & 4) != 0) cz += zStream.readSmartSigned();
            vx[i] = cx; vy[i] = cy; vz[i] = cz;
        }

        // Decode triangle colors
        Stream colorStream = new Stream(data); colorStream.position = triColorsOff;
        int[] triColors = new int[triangleCount];
        for (int i = 0; i < triangleCount; i++)
            triColors[i] = colorStream.readUnsignedWord();

        // Decode triangle info (render type)
        int[] triInfo = null;
        if (hasTriangleInfo == 1) {
            triInfo = new int[triangleCount];
            Stream infoStream = new Stream(data); infoStream.position = triInfoOff;
            for (int i = 0; i < triangleCount; i++)
                triInfo[i] = infoStream.readUnsignedByte();
        }

        // Decode triangle priorities
        int[] triPriorities = null;
        int globalPriority = perTriPriority ? -1 : priorityFlag;
        if (perTriPriority) {
            triPriorities = new int[triangleCount];
            Stream priStream = new Stream(data); priStream.position = triPrioritiesOff;
            for (int i = 0; i < triangleCount; i++)
                triPriorities[i] = priStream.readUnsignedByte();
        }

        // Decode triangle alpha
        int[] triAlpha = null;
        if (hasAlpha == 1) {
            triAlpha = new int[triangleCount];
            Stream alphaStream = new Stream(data); alphaStream.position = triAlphaOff;
            for (int i = 0; i < triangleCount; i++)
                triAlpha[i] = alphaStream.readUnsignedByte();
        }

        // Decode vertex labels (bone assignments for animation)
        int[] vertLabels = null;
        if (hasVertLabels == 1) {
            vertLabels = new int[vertexCount];
            Stream vlStream = new Stream(data); vlStream.position = vertLabelsOff;
            for (int i = 0; i < vertexCount; i++)
                vertLabels[i] = vlStream.readUnsignedByte();
        }

        // Decode triangle labels (bone assignments for animation)
        int[] triLabels = null;
        if (hasTriLabels == 1) {
            triLabels = new int[triangleCount];
            Stream tlStream = new Stream(data); tlStream.position = triLabelsOff;
            for (int i = 0; i < triangleCount; i++)
                triLabels[i] = tlStream.readUnsignedByte();
        }

        // Decode triangle vertex indices
        Stream triStream = new Stream(data); triStream.position = triVertOff;
        Stream typeStream = new Stream(data); typeStream.position = triTypesOff;
        int[] triA = new int[triangleCount];
        int[] triB = new int[triangleCount];
        int[] triC = new int[triangleCount];
        int a = 0, b = 0, c = 0, last = 0;
        for (int i = 0; i < triangleCount; i++) {
            int type = typeStream.readUnsignedByte();
            if (type == 1) {
                a = triStream.readSmartSigned() + last; last = a;
                b = triStream.readSmartSigned() + last; last = b;
                c = triStream.readSmartSigned() + last; last = c;
            } else if (type == 2) {
                b = c;
                c = triStream.readSmartSigned() + last; last = c;
            } else if (type == 3) {
                a = c;
                c = triStream.readSmartSigned() + last; last = c;
            } else if (type == 4) {
                int tmp = a; a = b; b = tmp;
                c = triStream.readSmartSigned() + last; last = c;
            }
            triA[i] = a; triB[i] = b; triC[i] = c;
        }

        // Decode texture triangles
        int[] texA = null, texB = null, texC = null;
        if (texTriCount > 0) {
            Stream texStream = new Stream(data); texStream.position = texTriOff;
            texA = new int[texTriCount];
            texB = new int[texTriCount];
            texC = new int[texTriCount];
            for (int i = 0; i < texTriCount; i++) {
                texA[i] = texStream.readUnsignedWord();
                texB[i] = texStream.readUnsignedWord();
                texC[i] = texStream.readUnsignedWord();
            }
        }

        // Build JSON
        StringBuilder sb = new StringBuilder(vertexCount * 30 + triangleCount * 30);
        sb.append("{\n");
        sb.append("  \"format\": \"old_317\",\n");
        sb.append("  \"vertexCount\": ").append(vertexCount).append(",\n");
        sb.append("  \"triangleCount\": ").append(triangleCount).append(",\n");
        sb.append("  \"textureTriangleCount\": ").append(texTriCount).append(",\n");
        sb.append("  \"verticesX\": ").append(jsonArray(vx)).append(",\n");
        sb.append("  \"verticesY\": ").append(jsonArray(vy)).append(",\n");
        sb.append("  \"verticesZ\": ").append(jsonArray(vz)).append(",\n");
        sb.append("  \"triangleA\": ").append(jsonArray(triA)).append(",\n");
        sb.append("  \"triangleB\": ").append(jsonArray(triB)).append(",\n");
        sb.append("  \"triangleC\": ").append(jsonArray(triC)).append(",\n");
        sb.append("  \"triangleColors\": ").append(jsonArray(triColors));
        if (triInfo != null) sb.append(",\n  \"triangleInfo\": ").append(jsonArray(triInfo));
        if (triPriorities != null) sb.append(",\n  \"trianglePriorities\": ").append(jsonArray(triPriorities));
        else sb.append(",\n  \"globalPriority\": ").append(globalPriority);
        if (triAlpha != null) sb.append(",\n  \"triangleAlpha\": ").append(jsonArray(triAlpha));
        if (vertLabels != null) sb.append(",\n  \"vertexLabels\": ").append(jsonArray(vertLabels));
        if (triLabels != null) sb.append(",\n  \"triangleLabels\": ").append(jsonArray(triLabels));
        if (texA != null) {
            sb.append(",\n  \"textureTriangleA\": ").append(jsonArray(texA));
            sb.append(",\n  \"textureTriangleB\": ").append(jsonArray(texB));
            sb.append(",\n  \"textureTriangleC\": ").append(jsonArray(texC));
        }
        sb.append("\n}\n");
        return sb.toString();
    }

    private static String decodeNewModelToJson(byte[] data, int vertexCount, int triangleCount, int texTriCount) {
        // Parse the 23-byte footer for metadata
        Stream s = new Stream(data);
        s.position = data.length - 23;
        s.readUnsignedWord(); // vertexCount
        s.readUnsignedWord(); // triangleCount
        s.readUnsignedByte(); // texTriCount
        int flags = s.readUnsignedByte();
        boolean hasInfo = (flags & 1) != 0;
        boolean isExtended = (flags & 8) != 0;
        int priorityFlag = s.readUnsignedByte();
        int hasAlpha = s.readUnsignedByte();
        int hasTriLabels = s.readUnsignedByte();
        int hasVertLabels = s.readUnsignedByte();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"format\": ").append(jsonEscape(isExtended ? "rs622_extended" : "rs525")).append(",\n");
        sb.append("  \"vertexCount\": ").append(vertexCount).append(",\n");
        sb.append("  \"triangleCount\": ").append(triangleCount).append(",\n");
        sb.append("  \"textureTriangleCount\": ").append(texTriCount).append(",\n");
        sb.append("  \"hasTriangleInfo\": ").append(hasInfo).append(",\n");
        sb.append("  \"isExtendedFormat\": ").append(isExtended).append(",\n");
        sb.append("  \"priorityFlag\": ").append(priorityFlag).append(",\n");
        sb.append("  \"hasAlpha\": ").append(hasAlpha != 0).append(",\n");
        sb.append("  \"hasTriangleLabels\": ").append(hasTriLabels != 0).append(",\n");
        sb.append("  \"hasVertexLabels\": ").append(hasVertLabels != 0).append(",\n");
        sb.append("  \"rawSize\": ").append(data.length).append(",\n");
        sb.append("  \"rawBase64\": \"").append(Base64.getEncoder().encodeToString(data)).append("\"\n");
        sb.append("}\n");
        return sb.toString();
    }

    private static void extractMaps() throws IOException {
        // Read the map index to know which files to extract from idx4
        String mapIndexPath = outputPath + "/map_index.json";
        if (!new File(mapIndexPath).exists()) {
            System.out.println("  Maps: SKIPPED (no map_index.json)");
            return;
        }

        String mapDir = outputPath + "/maps";
        mkdirs(mapDir);
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;

        // Re-read map index from the versionlist archive
        byte[] versionData = decompressors[0].decompress(5);
        ArchiveLoader versionArchive = new ArchiveLoader(versionData);
        byte[] mapData = versionArchive.extractFile("map_index");
        if (mapData == null) {
            System.out.println("  Maps: SKIPPED (no map_index)");
            return;
        }

        int entrySize;
        if (mapData.length % 7 == 0) entrySize = 7;
        else if (mapData.length % 6 == 0) entrySize = 6;
        else entrySize = 6;
        int total = mapData.length / entrySize;
        int pos = 0;

        for (int i = 0; i < total; i++) {
            if (pos + entrySize > mapData.length) break;
            int regionId = ((mapData[pos] & 0xff) << 8) | (mapData[pos + 1] & 0xff); pos += 2;
            int landscapeFile = ((mapData[pos] & 0xff) << 8) | (mapData[pos + 1] & 0xff); pos += 2;
            int objectsFile = ((mapData[pos] & 0xff) << 8) | (mapData[pos + 1] & 0xff); pos += 2;
            if (entrySize == 7) pos++;

            // Extract landscape data from idx4 (decompressors[4])
            try {
                byte[] landscape = decompressors[4].decompress(landscapeFile);
                if (landscape != null && landscape.length > 0) {
                    writeFile(mapDir + "/landscape_" + regionId + ".dat", landscape);
                    extracted++;
                }
            } catch (Exception ignored) {}

            // Extract objects data from idx4 (decompressors[4])
            try {
                byte[] objects = decompressors[4].decompress(objectsFile);
                if (objects != null && objects.length > 0) {
                    writeFile(mapDir + "/objects_" + regionId + ".dat", objects);
                    extracted++;
                }
            } catch (Exception ignored) {}
        }

        indexJson.append("  \"totalRegions\": ").append(total).append(",\n");
        indexJson.append("  \"extractedFiles\": ").append(extracted).append("\n");
        indexJson.append("}\n");
        writeFile(mapDir + "/_index.json", indexJson.toString());
        mapCount = extracted;
        System.out.println("  Maps: " + extracted + " files extracted from " + total + " regions");
    }

    private static void extractAnimFrames() throws IOException {
        String frameDir = outputPath + "/anim_frames";
        mkdirs(frameDir);
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;
        int lenientRecovered = 0;

        // Animation frames are in idx2 (decompressors[2])
        // Get raw file handles for lenient fallback
        RandomAccessFile idxFileRef = new RandomAccessFile(cachePath + "/main_file_cache.idx2", "r");
        long idxLength = idxFileRef.length();
        int maxEntryId = (int) (idxLength / 6);

        for (int frameId = 0; frameId < maxEntryId; frameId++) {
            byte[] rawData = decompressors[2].decompress(frameId);

            // Lenient fallback for entries with mismatched sector metadata
            if (rawData == null || rawData.length == 0) {
                rawData = decompressLenient(dataFile, idxFileRef, 3, frameId);
                if (rawData != null && rawData.length > 0) {
                    lenientRecovered++;
                }
            }

            if (rawData == null || rawData.length == 0) continue;

            // Try GZIP decompression (cache data may be compressed)
            byte[] data;
            try {
                data = gzipDecompress(rawData);
            } catch (Exception e) {
                data = rawData;
            }

            writeFile(frameDir + "/" + frameId + ".dat", data);

            if (extracted > 0) indexJson.append(",\n");
            indexJson.append("  ").append('"').append(frameId).append("\": {");
            indexJson.append("\"size\":").append(data.length);
            indexJson.append('}');
            extracted++;
        }

        idxFileRef.close();
        indexJson.append("\n}\n");
        writeFile(frameDir + "/_index.json", indexJson.toString());
        animFrameCount = extracted;
        System.out.println("  Animation frames: " + extracted + " extracted (" + lenientRecovered + " recovered via lenient read)");
    }

    // ═════════════════════════════════════════════════════════════════
    // INTERFACE DEFINITIONS (ARCHIVE 3)
    // ═════════════════════════════════════════════════════════════════

    private static void extractInterfaces(ArchiveLoader interfaceArchive) throws IOException {
        if (interfaceArchive == null) {
            System.out.println("  Interfaces: SKIPPED (archive 3 not found)");
            return;
        }
        byte[] data = interfaceArchive.extractFile("data");
        if (data == null || data.length < 4) {
            System.out.println("  Interfaces: SKIPPED (missing 'data' file)");
            return;
        }

        Stream stream = new Stream(data);
        stream.readUnsignedWord(); // total count (not reliable)

        mkdirs(outputPath);
        StringBuilder sb = new StringBuilder(data.length * 2);
        sb.append("{\n");
        int count = 0;
        int parentId = -1;

        while (stream.position < data.length) {
            try {
                int id = stream.readUnsignedWord();
                if (id == 65535) {
                    parentId = stream.readUnsignedWord();
                    id = stream.readUnsignedWord();
                }

                if (count > 0) sb.append(",\n");
                sb.append("  ").append('"').append(id).append("\": {");

                int type = stream.readUnsignedByte();
                int atActionType = stream.readUnsignedByte();
                int contentType = stream.readUnsignedWord();
                int width = stream.readUnsignedWord();
                int height = stream.readUnsignedWord();
                int opacity = stream.readUnsignedByte();
                int hoverType = stream.readUnsignedByte();
                if (hoverType != 0)
                    hoverType = (hoverType - 1 << 8) + stream.readUnsignedByte();
                else
                    hoverType = -1;

                sb.append("\"parentID\":").append(parentId);
                sb.append(",\"type\":").append(type);
                sb.append(",\"atActionType\":").append(atActionType);
                sb.append(",\"contentType\":").append(contentType);
                sb.append(",\"width\":").append(width);
                sb.append(",\"height\":").append(height);
                if (opacity != 0) sb.append(",\"opacity\":").append(opacity);
                if (hoverType != -1) sb.append(",\"hoverType\":").append(hoverType);

                // Comparator ops
                int compCount = stream.readUnsignedByte();
                if (compCount > 0 && compCount < 1000) {
                    int[] compOps = new int[compCount];
                    int[] compValues = new int[compCount];
                    for (int j = 0; j < compCount; j++) {
                        compOps[j] = stream.readUnsignedByte();
                        compValues[j] = stream.readUnsignedWord();
                    }
                    sb.append(",\"comparatorOps\":").append(jsonArray(compOps));
                    sb.append(",\"comparatorValues\":").append(jsonArray(compValues));
                }

                // Value index arrays
                int valCount = stream.readUnsignedByte();
                if (valCount > 0 && valCount < 1000) {
                    sb.append(",\"valueIndexes\":[");
                    for (int j = 0; j < valCount; j++) {
                        int subCount = stream.readUnsignedWord();
                        if (j > 0) sb.append(',');
                        if (subCount > 0 && subCount < 10000) {
                            int[] vals = new int[subCount];
                            for (int k = 0; k < subCount; k++)
                                vals[k] = stream.readUnsignedWord();
                            sb.append(jsonArray(vals));
                        } else {
                            sb.append("null");
                        }
                    }
                    sb.append(']');
                }

                // Type 0: Layer (container)
                if (type == 0) {
                    int scrollMax = stream.readUnsignedWord();
                    boolean mouseoverTriggered = stream.readUnsignedByte() == 1;
                    sb.append(",\"scrollMax\":").append(scrollMax);
                    if (mouseoverTriggered) sb.append(",\"mouseoverTriggered\":true");
                    int childCount = stream.readUnsignedWord();
                    if (childCount > 0 && childCount < 10000) {
                        int[] children = new int[childCount];
                        int[] childX = new int[childCount];
                        int[] childY = new int[childCount];
                        for (int j = 0; j < childCount; j++) {
                            children[j] = stream.readUnsignedWord();
                            childX[j] = stream.readSignedWord();
                            childY[j] = stream.readSignedWord();
                        }
                        sb.append(",\"children\":").append(jsonArray(children));
                        sb.append(",\"childX\":").append(jsonArray(childX));
                        sb.append(",\"childY\":").append(jsonArray(childY));
                    }
                }

                // Type 1: unused variant
                if (type == 1) {
                    stream.readUnsignedWord();
                    stream.readUnsignedByte();
                }

                // Type 2: Inventory
                if (type == 2) {
                    boolean replaceItems = stream.readUnsignedByte() == 1;
                    boolean isBank = stream.readUnsignedByte() == 1;
                    boolean usableItems = stream.readUnsignedByte() == 1;
                    boolean aBoolean235 = stream.readUnsignedByte() == 1;
                    int padX = stream.readUnsignedByte();
                    int padY = stream.readUnsignedByte();
                    sb.append(",\"replaceItems\":").append(replaceItems);
                    sb.append(",\"isBank\":").append(isBank);
                    sb.append(",\"usableItems\":").append(usableItems);
                    if (aBoolean235) sb.append(",\"aBoolean235\":true");
                    sb.append(",\"invSpritePadX\":").append(padX);
                    sb.append(",\"invSpritePadY\":").append(padY);

                    // 20 sprite slots
                    StringBuilder spriteRefs = new StringBuilder();
                    spriteRefs.append('[');
                    int spriteRefCount = 0;
                    for (int j = 0; j < 20; j++) {
                        int hasSprite = stream.readUnsignedByte();
                        if (hasSprite == 1) {
                            int sx = stream.readSignedWord();
                            int sy = stream.readSignedWord();
                            String spriteRef = stream.readString();
                            if (spriteRefCount > 0) spriteRefs.append(',');
                            spriteRefs.append("{\"slot\":").append(j);
                            spriteRefs.append(",\"x\":").append(sx);
                            spriteRefs.append(",\"y\":").append(sy);
                            spriteRefs.append(",\"sprite\":").append(jsonEscape(spriteRef)).append('}');
                            spriteRefCount++;
                        }
                    }
                    spriteRefs.append(']');
                    if (spriteRefCount > 0) sb.append(",\"spriteSlots\":").append(spriteRefs);

                    // 5 actions
                    String[] actions = new String[5];
                    for (int j = 0; j < 5; j++) {
                        actions[j] = stream.readString();
                        if (actions[j].isEmpty()) actions[j] = null;
                    }
                    sb.append(",\"actions\":").append(jsonArray(actions));
                }

                // Type 3: Rect
                if (type == 3) {
                    boolean filled = stream.readUnsignedByte() == 1;
                    sb.append(",\"filled\":").append(filled);
                }

                // Type 4 or 1: Text font
                if (type == 4 || type == 1) {
                    boolean centerText = stream.readUnsignedByte() == 1;
                    int fontId = stream.readUnsignedByte();
                    boolean textShadow = stream.readUnsignedByte() == 1;
                    sb.append(",\"centerText\":").append(centerText);
                    sb.append(",\"fontId\":").append(fontId);
                    sb.append(",\"textShadow\":").append(textShadow);
                }

                // Type 4: Text content
                if (type == 4) {
                    String disabledMsg = stream.readString();
                    String enabledMsg = stream.readString();
                    sb.append(",\"disabledMessage\":").append(jsonEscape(disabledMsg));
                    if (!enabledMsg.isEmpty()) sb.append(",\"enabledMessage\":").append(jsonEscape(enabledMsg));
                }

                // Type 1, 3, or 4: text color
                if (type == 1 || type == 3 || type == 4) {
                    int textColor = stream.readDWord();
                    sb.append(",\"textColor\":").append(textColor);
                }

                // Type 3 or 4: hover colors
                if (type == 3 || type == 4) {
                    int anInt219 = stream.readDWord();
                    int textHoverColor = stream.readDWord();
                    int anInt239 = stream.readDWord();
                    sb.append(",\"enabledColor\":").append(anInt219);
                    sb.append(",\"textHoverColor\":").append(textHoverColor);
                    sb.append(",\"enabledHoverColor\":").append(anInt239);
                }

                // Type 5: Sprite
                if (type == 5) {
                    String disabledSprite = stream.readString();
                    String enabledSprite = stream.readString();
                    sb.append(",\"disabledSprite\":").append(jsonEscape(disabledSprite));
                    if (!enabledSprite.isEmpty()) sb.append(",\"enabledSprite\":").append(jsonEscape(enabledSprite));
                }

                // Type 6: Model
                if (type == 6) {
                    int m = stream.readUnsignedByte();
                    int modelType = 0, mediaID = 0;
                    if (m != 0) { modelType = 1; mediaID = (m - 1 << 8) + stream.readUnsignedByte(); }
                    int m2 = stream.readUnsignedByte();
                    int anInt255 = 0, anInt256 = 0;
                    if (m2 != 0) { anInt255 = 1; anInt256 = (m2 - 1 << 8) + stream.readUnsignedByte(); }
                    int m3 = stream.readUnsignedByte();
                    int verticalOffset = m3 != 0 ? (m3 - 1 << 8) + stream.readUnsignedByte() : -1;
                    int m4 = stream.readUnsignedByte();
                    int anInt258 = m4 != 0 ? (m4 - 1 << 8) + stream.readUnsignedByte() : -1;
                    int modelZoom = stream.readUnsignedWord();
                    int modelRot1 = stream.readUnsignedWord();
                    int modelRot2 = stream.readUnsignedWord();
                    if (modelType != 0) sb.append(",\"modelType\":").append(modelType).append(",\"mediaID\":").append(mediaID);
                    if (anInt255 != 0) sb.append(",\"enabledModelType\":").append(anInt255).append(",\"enabledMediaID\":").append(anInt256);
                    if (verticalOffset != -1) sb.append(",\"verticalOffset\":").append(verticalOffset);
                    if (anInt258 != -1) sb.append(",\"enabledVerticalOffset\":").append(anInt258);
                    sb.append(",\"modelZoom\":").append(modelZoom);
                    sb.append(",\"modelRotation1\":").append(modelRot1);
                    sb.append(",\"modelRotation2\":").append(modelRot2);
                }

                // Type 7: Inventory variant
                if (type == 7) {
                    boolean centerText = stream.readUnsignedByte() == 1;
                    int fontId = stream.readUnsignedByte();
                    boolean textShadow = stream.readUnsignedByte() == 1;
                    int textColor = stream.readDWord();
                    int padX = stream.readSignedWord();
                    int padY = stream.readSignedWord();
                    boolean isBox = stream.readUnsignedByte() == 1;
                    sb.append(",\"centerText\":").append(centerText);
                    sb.append(",\"fontId\":").append(fontId);
                    sb.append(",\"textShadow\":").append(textShadow);
                    sb.append(",\"textColor\":").append(textColor);
                    sb.append(",\"invSpritePadX\":").append(padX);
                    sb.append(",\"invSpritePadY\":").append(padY);
                    if (isBox) sb.append(",\"isBox\":true");
                    String[] actions = new String[5];
                    for (int j = 0; j < 5; j++) {
                        actions[j] = stream.readString();
                        if (actions[j].isEmpty()) actions[j] = null;
                    }
                    sb.append(",\"actions\":").append(jsonArray(actions));
                }

                // Spell info (atActionType 2 or type 2)
                if (atActionType == 2 || type == 2) {
                    String selectedAction = stream.readString();
                    String spellName = stream.readString();
                    int spellUsableOn = stream.readUnsignedWord();
                    if (!selectedAction.isEmpty()) sb.append(",\"selectedActionName\":").append(jsonEscape(selectedAction));
                    if (!spellName.isEmpty()) sb.append(",\"spellName\":").append(jsonEscape(spellName));
                    if (spellUsableOn != 0) sb.append(",\"spellUsableOn\":").append(spellUsableOn);
                }

                // Type 8: Text
                if (type == 8) {
                    String msg = stream.readString();
                    sb.append(",\"disabledMessage\":").append(jsonEscape(msg));
                }

                // Tooltip
                if (atActionType == 1 || atActionType == 4 || atActionType == 5 || atActionType == 6) {
                    String tooltip = stream.readString();
                    if (!tooltip.isEmpty()) sb.append(",\"tooltip\":").append(jsonEscape(tooltip));
                }

                sb.append('}');
                count++;
            } catch (Exception e) {
                // End of stream or corrupt entry
                break;
            }
        }

        sb.append("\n}\n");
        writeFile(outputPath + "/interfaces.json", sb.toString());
        interfaceCount = count;
        System.out.println("  Interfaces: " + count + " widgets extracted");
    }

    // ═════════════════════════════════════════════════════════════════
    // MIDI INDEX
    // ═════════════════════════════════════════════════════════════════

    private static void extractMidiIndex(ArchiveLoader versionlist) throws IOException {
        byte[] midiData = versionlist.extractFile("midi_index");
        if (midiData == null || midiData.length == 0) {
            System.out.println("  MIDI index: SKIPPED (missing midi_index)");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{\n  \"totalTracks\": ").append(midiData.length).append(",\n  \"tracks\": [");
        int enabled = 0;
        for (int i = 0; i < midiData.length; i++) {
            if (i > 0) sb.append(',');
            boolean isEnabled = midiData[i] == 1;
            sb.append(isEnabled);
            if (isEnabled) enabled++;
        }
        sb.append("],\n  \"enabledCount\": ").append(enabled).append("\n}\n");

        mkdirs(outputPath);
        writeFile(outputPath + "/midi_index.json", sb.toString());
        midiIndexCount = enabled;
        System.out.println("  MIDI index: " + midiData.length + " tracks (" + enabled + " enabled)");
    }

    // ═════════════════════════════════════════════════════════════════
    // TITLE ARCHIVE (ARCHIVE 0/1)
    // ═════════════════════════════════════════════════════════════════

    private static final String[] TITLE_SPRITE_NAMES = {
        "title", "logo", "runes"
    };

    private static final String[] FONT_FILE_NAMES = {
        "p11_full", "p12_full", "b12_full", "q8_full"
    };

    private static void extractTitleSprites() throws IOException {
        // Try archive ID 1 first (1-based), then 0
        byte[] titleData = null;
        int archiveId = -1;
        for (int tryId : new int[]{1, 0}) {
            try {
                titleData = decompressors[0].decompress(tryId);
                if (titleData != null && titleData.length > 0) {
                    archiveId = tryId;
                    break;
                }
            } catch (Exception ignored) {}
        }

        if (titleData == null) {
            System.out.println("  Title archive: SKIPPED (not found at archive 0 or 1)");
            return;
        }

        ArchiveLoader titleArchive = new ArchiveLoader(titleData);
        byte[] indexDat = titleArchive.extractFile("index.dat");

        String titleDir = outputPath + "/title_sprites";
        mkdirs(titleDir);
        int extracted = 0;
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n  \"archiveId\": ").append(archiveId).append(",\n");

        // Extract sprite images using palette format (same as media sprites)
        if (indexDat != null) {
            for (String spriteName : TITLE_SPRITE_NAMES) {
                byte[] spriteDat = titleArchive.extractFile(spriteName + ".dat");
                if (spriteDat == null) continue;
                try {
                    extracted += extractPaletteSpriteSet(spriteDat, indexDat, spriteName, titleDir, indexJson, extracted);
                } catch (Exception e) {
                    System.err.println("    Warning: Failed to extract title sprite '" + spriteName + "': " + e.getMessage());
                }
            }
        }

        // Extract font files as raw .dat
        String fontDir = outputPath + "/fonts";
        mkdirs(fontDir);
        int fontCount = 0;
        for (String fontName : FONT_FILE_NAMES) {
            byte[] fontDat = titleArchive.extractFile(fontName + ".dat");
            if (fontDat != null && fontDat.length > 0) {
                writeFile(fontDir + "/" + fontName + ".dat", fontDat);
                fontCount++;
            }
        }

        // Also dump index.dat for fonts
        if (indexDat != null) {
            writeFile(fontDir + "/index.dat", indexDat);
        }

        if (extracted > 0) indexJson.append(",\n");
        indexJson.append("  \"fontFiles\": ").append(fontCount).append("\n}\n");
        writeFile(titleDir + "/_index.json", indexJson.toString());
        titleSpriteCount = extracted;
        System.out.println("  Title archive: " + extracted + " sprites, " + fontCount + " font files (archive ID " + archiveId + ")");
    }

    /**
     * Extracts a set of sub-sprites from a palette-indexed .dat file using shared index.dat.
     * Returns number of sprites extracted.
     */
    private static int extractPaletteSpriteSet(byte[] spriteDat, byte[] indexDat,
                                                String spriteName, String outDir,
                                                StringBuilder indexJson, int priorCount) throws IOException {
        Stream dat = new Stream(spriteDat);
        Stream idx = new Stream(indexDat);
        idx.position = dat.readUnsignedWord();

        int fullWidth = idx.readUnsignedWord();
        int fullHeight = idx.readUnsignedWord();
        int paletteSize = idx.readUnsignedByte();

        int[] palette = new int[paletteSize];
        for (int i = 0; i < paletteSize - 1; i++) {
            palette[i + 1] = idx.read3Bytes();
            if (palette[i + 1] == 0) palette[i + 1] = 1;
        }

        int subIndex = 0;
        int extracted = 0;
        while (dat.position < spriteDat.length && idx.position < indexDat.length) {
            int offsetX, offsetY, width, height, format;
            try {
                offsetX = idx.readUnsignedByte();
                offsetY = idx.readUnsignedByte();
                width = idx.readUnsignedWord();
                height = idx.readUnsignedWord();
                format = idx.readUnsignedByte();
            } catch (Exception e) { break; }

            if (width == 0 || height == 0) break;
            int pixelCount = width * height;
            if (dat.position + pixelCount > spriteDat.length) break;

            int[] pixels = new int[pixelCount];
            if (format == 0) {
                for (int p = 0; p < pixelCount; p++)
                    pixels[p] = palette[dat.readUnsignedByte()];
            } else if (format == 1) {
                for (int x = 0; x < width; x++)
                    for (int y = 0; y < height; y++)
                        pixels[x + y * width] = palette[dat.readUnsignedByte()];
            } else { break; }

            BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < height; y++)
                for (int x = 0; x < width; x++) {
                    int rgb = pixels[x + y * width];
                    img.setRGB(x, y, rgb == 0 ? 0x00000000 : (0xFF000000 | rgb));
                }

            String filename = spriteName + "_" + subIndex + ".png";
            ImageIO.write(img, "png", new File(outDir + "/" + filename));

            if (priorCount + extracted > 0) indexJson.append(",\n");
            indexJson.append("  ").append('"').append(spriteName).append("_").append(subIndex).append("\": {");
            indexJson.append("\"name\":").append(jsonEscape(spriteName));
            indexJson.append(",\"subIndex\":").append(subIndex);
            indexJson.append(",\"width\":").append(width);
            indexJson.append(",\"height\":").append(height);
            indexJson.append(",\"file\":").append(jsonEscape(filename));
            indexJson.append('}');
            extracted++;
            subIndex++;
        }
        return extracted;
    }

    // ═════════════════════════════════════════════════════════════════
    // RAW ARCHIVE DUMPS
    // ═════════════════════════════════════════════════════════════════

    private static final String[] ARCHIVE_NAMES = {
        "empty_0", "title", "config", "interface", "media",
        "versionlist", "textures", "wordenc", "sounds"
    };

    private static void extractRawArchives() throws IOException {
        String archiveDir = outputPath + "/archives";
        mkdirs(archiveDir);
        StringBuilder indexJson = new StringBuilder();
        indexJson.append("{\n");
        int extracted = 0;

        for (int archiveId = 0; archiveId <= 8; archiveId++) {
            try {
                byte[] data = decompressors[0].decompress(archiveId);
                if (data != null && data.length > 0) {
                    String name = archiveId < ARCHIVE_NAMES.length ? ARCHIVE_NAMES[archiveId] : "archive_" + archiveId;
                    String filename = name + ".dat";
                    writeFile(archiveDir + "/" + filename, data);

                    if (extracted > 0) indexJson.append(",\n");
                    indexJson.append("  ").append('"').append(archiveId).append("\": {");
                    indexJson.append("\"name\":").append(jsonEscape(name));
                    indexJson.append(",\"file\":").append(jsonEscape(filename));
                    indexJson.append(",\"size\":").append(data.length);
                    indexJson.append('}');
                    extracted++;
                }
            } catch (Exception ignored) {}
        }

        indexJson.append("\n}\n");
        writeFile(archiveDir + "/_index.json", indexJson.toString());
        rawArchiveCount = extracted;
        System.out.println("  Raw archives: " + extracted + " dumped from idx0");
    }

    // ═════════════════════════════════════════════════════════════════
    // PHASE 5: DEEP BINARY-TO-JSON PARSING
    // ═════════════════════════════════════════════════════════════════

    /**
     * Parses animation frame .dat files into JSON with full skeleton + frame transform data.
     * Binary format: SkinList header (transform types + child indices) + N frame records.
     */
    private static void parseAnimFramesToJson() throws IOException {
        String frameDir = outputPath + "/anim_frames";
        File dir = new File(frameDir);
        if (!dir.exists()) {
            System.out.println("  Anim frame JSON: SKIPPED (no anim_frames/ directory)");
            return;
        }

        String jsonDir = outputPath + "/anim_frames_json";
        mkdirs(jsonDir);
        int parsed = 0;

        File[] files = dir.listFiles((d, name) -> name.endsWith(".dat") && !name.startsWith("_"));
        if (files == null || files.length == 0) {
            System.out.println("  Anim frame JSON: SKIPPED (no .dat files)");
            return;
        }

        for (File file : files) {
            String name = file.getName();
            String id = name.substring(0, name.length() - 4); // strip .dat
            try {
                byte[] data = readFileBytes(file.getAbsolutePath());
                if (data.length < 4) continue;

                Stream stream = new Stream(data);

                // Parse SkinList header
                int skinCount = stream.readUnsignedWord();
                if (skinCount > 5000) continue;

                int[] transformTypes = new int[skinCount];
                int[][] childIndices = new int[skinCount][];
                for (int j = 0; j < skinCount; j++)
                    transformTypes[j] = stream.readUnsignedWord();
                for (int j = 0; j < skinCount; j++)
                    childIndices[j] = new int[stream.readUnsignedWord()];
                for (int j = 0; j < skinCount; j++)
                    for (int k = 0; k < childIndices[j].length; k++)
                        childIndices[j][k] = stream.readUnsignedWord();

                if (stream.position + 2 > data.length) continue;
                int frameCount = stream.readUnsignedWord();
                if (frameCount <= 0 || frameCount > 10000) continue;

                // Build JSON
                StringBuilder sb = new StringBuilder(1024);
                sb.append("{\n  \"skinList\": {\n");
                sb.append("    \"boneCount\": ").append(skinCount).append(",\n");
                sb.append("    \"transformTypes\": ").append(jsonArray(transformTypes)).append(",\n");
                sb.append("    \"childIndices\": [");
                for (int j = 0; j < skinCount; j++) {
                    if (j > 0) sb.append(',');
                    sb.append(jsonArray(childIndices[j]));
                }
                sb.append("]\n  },\n  \"frameCount\": ").append(frameCount).append(",\n  \"frames\": [\n");

                int validFrames = 0;
                for (int f = 0; f < frameCount; f++) {
                    if (stream.position + 3 > data.length) break;

                    int frameId = stream.readUnsignedWord();
                    int transformCount = stream.readUnsignedByte();

                    if (validFrames > 0) sb.append(",\n");
                    sb.append("    {\"frameId\":").append(frameId);
                    sb.append(",\"transformCount\":").append(transformCount);
                    sb.append(",\"transforms\":[");

                    int tWritten = 0;
                    for (int t = 0; t < transformCount; t++) {
                        if (stream.position >= data.length) break;
                        int flags = stream.readUnsignedByte();

                        if (flags == 0) continue; // no transform for this bone

                        // Determine default based on transform type
                        int defVal = (t < skinCount && transformTypes[t] == 3) ? 128 : 0;
                        int xVal = defVal, yVal = defVal, zVal = defVal;

                        if ((flags & 1) != 0 && stream.position + 2 <= data.length)
                            xVal = readSignedWordBE(stream);
                        if ((flags & 2) != 0 && stream.position + 2 <= data.length)
                            yVal = readSignedWordBE(stream);
                        if ((flags & 4) != 0 && stream.position + 2 <= data.length)
                            zVal = readSignedWordBE(stream);

                        if (tWritten > 0) sb.append(',');
                        sb.append("{\"bone\":").append(t);
                        sb.append(",\"x\":").append(xVal);
                        sb.append(",\"y\":").append(yVal);
                        sb.append(",\"z\":").append(zVal).append('}');
                        tWritten++;
                    }

                    sb.append("]}");
                    validFrames++;
                }

                sb.append("\n  ]\n}\n");
                writeFile(jsonDir + "/" + id + ".json", sb.toString());
                parsed++;
            } catch (Exception e) {
                // Skip corrupt frame files silently
            }
        }

        animFrameJsonCount = parsed;
        System.out.println("  Anim frame JSON: " + parsed + " files parsed");
    }

    /** Mirrors Stream.readSignedWordBE() — values >60000 become negative. */
    private static int readSignedWordBE(Stream stream) {
        int val = stream.readUnsignedWord();
        if (val > 60000) val = -65535 + val;
        return val;
    }

    /** Mirrors Stream.readSmartUnsigned(). */
    private static int readSmartUnsigned(Stream stream) {
        int peek = stream.buffer[stream.position] & 0xff;
        if (peek < 128)
            return stream.readUnsignedByte();
        else
            return stream.readUnsignedWord() - 32768;
    }

    /** Mirrors Stream.readLargeSmart() — accumulates 32767 chunks. */
    private static int readLargeSmart(Stream stream) {
        int baseVal = 0;
        int lastVal;
        while ((lastVal = readSmartUnsigned(stream)) == 32767)
            baseVal += 32767;
        return baseVal + lastVal;
    }

    /**
     * Parses landscape map .dat files into JSON with per-tile data.
     * Binary format: opcode-based per-tile (4 planes x 64x64 tiles).
     * Opcodes: 0=procedural height, 1=explicit height, 2-49=overlay, 50-81=flags, 82-255=underlay.
     */
    private static void parseLandscapeMapsToJson() throws IOException {
        String mapDir = outputPath + "/maps";
        File dir = new File(mapDir);
        if (!dir.exists()) {
            System.out.println("  Landscape JSON: SKIPPED (no maps/ directory)");
            return;
        }

        String jsonDir = outputPath + "/maps_json";
        mkdirs(jsonDir);
        int parsed = 0;

        File[] files = dir.listFiles((d, name) -> name.startsWith("landscape_") && name.endsWith(".dat"));
        if (files == null || files.length == 0) {
            System.out.println("  Landscape JSON: SKIPPED (no landscape_*.dat files)");
            return;
        }

        for (File file : files) {
            String name = file.getName();
            String regionId = name.substring("landscape_".length(), name.length() - 4);
            try {
                byte[] data = readFileBytes(file.getAbsolutePath());
                if (data.length < 10) continue;

                Stream stream = new Stream(data);
                StringBuilder sb = new StringBuilder(64 * 64 * 4 * 20);
                sb.append("{\n  \"regionId\": ").append(regionId).append(",\n  \"planes\": [\n");

                for (int plane = 0; plane < 4; plane++) {
                    if (plane > 0) sb.append(",\n");
                    sb.append("    {\"plane\":").append(plane).append(",\"tiles\":[\n");

                    boolean firstTile = true;
                    for (int x = 0; x < 64; x++) {
                        for (int y = 0; y < 64; y++) {
                            if (stream.position >= data.length) break;

                            int height = 0;
                            int overlayId = 0, overlayRotation = 0, overlayShape = 0;
                            int underlayId = 0;
                            int flags = 0;
                            boolean hasExplicitHeight = false;

                            // Parse opcodes for this tile
                            while (stream.position < data.length) {
                                int opcode = stream.readUnsignedByte();
                                if (opcode == 0) {
                                    // Procedural height — no more data for this tile
                                    break;
                                } else if (opcode == 1) {
                                    // Explicit height
                                    if (stream.position < data.length) {
                                        height = stream.readUnsignedByte();
                                        hasExplicitHeight = true;
                                    }
                                    break; // height opcode always terminates tile
                                } else if (opcode <= 49) {
                                    // Overlay: signed byte for overlayId
                                    if (stream.position < data.length) {
                                        overlayId = stream.readSignedByte();
                                        overlayRotation = (opcode - 2) / 4;
                                        overlayShape = (opcode - 2) & 3;
                                    }
                                } else if (opcode <= 81) {
                                    flags = opcode - 49;
                                } else {
                                    underlayId = opcode - 81;
                                }
                            }

                            // Only write tiles that have non-default data
                            boolean hasData = hasExplicitHeight || overlayId != 0 || underlayId != 0 || flags != 0;
                            if (hasData) {
                                if (!firstTile) sb.append(',');
                                sb.append("{\"x\":").append(x).append(",\"y\":").append(y);
                                if (hasExplicitHeight) sb.append(",\"height\":").append(height);
                                if (overlayId != 0) {
                                    sb.append(",\"overlayId\":").append(overlayId);
                                    sb.append(",\"overlayRotation\":").append(overlayRotation);
                                    sb.append(",\"overlayShape\":").append(overlayShape);
                                }
                                if (underlayId != 0) sb.append(",\"underlayId\":").append(underlayId);
                                if (flags != 0) sb.append(",\"flags\":").append(flags);
                                sb.append('}');
                                firstTile = false;
                            }
                        }
                    }
                    sb.append("\n    ]}");
                }

                sb.append("\n  ]\n}\n");
                writeFile(jsonDir + "/landscape_" + regionId + ".json", sb.toString());
                parsed++;
            } catch (Exception e) {
                // Skip corrupt landscape files
            }
        }

        landscapeJsonCount = parsed;
        System.out.println("  Landscape JSON: " + parsed + " files parsed");
    }

    /**
     * Parses object spawn .dat files into JSON.
     * Binary format: double delta-encoded (object ID deltas + packed position deltas).
     * Uses readLargeSmart for object IDs and readSmartUnsigned for positions.
     */
    private static void parseObjectSpawnsToJson() throws IOException {
        String mapDir = outputPath + "/maps";
        File dir = new File(mapDir);
        if (!dir.exists()) {
            System.out.println("  Object spawn JSON: SKIPPED (no maps/ directory)");
            return;
        }

        String jsonDir = outputPath + "/maps_json";
        mkdirs(jsonDir); // may already exist from landscape parsing
        int parsed = 0;

        File[] files = dir.listFiles((d, name) -> name.startsWith("objects_") && name.endsWith(".dat"));
        if (files == null || files.length == 0) {
            System.out.println("  Object spawn JSON: SKIPPED (no objects_*.dat files)");
            return;
        }

        for (File file : files) {
            String name = file.getName();
            String regionId = name.substring("objects_".length(), name.length() - 4);
            try {
                byte[] data = readFileBytes(file.getAbsolutePath());
                if (data.length < 2) continue;

                Stream stream = new Stream(data);
                StringBuilder sb = new StringBuilder(1024);
                sb.append("{\n  \"regionId\": ").append(regionId).append(",\n  \"objects\": [\n");

                int objectId = -1;
                int objectCount = 0;

                while (stream.position < data.length) {
                    int idDelta = readLargeSmart(stream);
                    if (idDelta == 0) break; // end of objects
                    objectId += idDelta;

                    int packedPos = 0;
                    while (stream.position < data.length) {
                        int posDelta = readSmartUnsigned(stream);
                        if (posDelta == 0) break; // end of positions for this object
                        packedPos += posDelta - 1;

                        int localY = packedPos & 0x3f;
                        int localX = (packedPos >> 6) & 0x3f;
                        int plane = packedPos >> 12;

                        if (stream.position >= data.length) break;
                        int attributes = stream.readUnsignedByte();
                        int type = attributes >> 2;
                        int rotation = attributes & 3;

                        if (objectCount > 0) sb.append(",\n");
                        sb.append("    {\"objectId\":").append(objectId);
                        sb.append(",\"localX\":").append(localX);
                        sb.append(",\"localY\":").append(localY);
                        sb.append(",\"plane\":").append(plane);
                        sb.append(",\"type\":").append(type);
                        sb.append(",\"rotation\":").append(rotation);
                        sb.append('}');
                        objectCount++;
                    }
                }

                sb.append("\n  ]\n}\n");
                writeFile(jsonDir + "/objects_" + regionId + ".json", sb.toString());
                parsed++;
            } catch (Exception e) {
                // Skip corrupt object files
            }
        }

        objectSpawnJsonCount = parsed;
        System.out.println("  Object spawn JSON: " + parsed + " files parsed");
    }

    /**
     * Parses font .dat files into JSON with glyph metadata and pixel data.
     * Binary format mirrors RSFont constructor: font.dat starts with word (index.dat offset),
     * then index.dat at offset+4 has palette size byte, skip palette, then 256 glyphs:
     * offsetX(byte), offsetY(byte), width(word), height(word), format(byte), pixel data from font.dat.
     */
    private static void parseFontsToJson() throws IOException {
        String fontDir = outputPath + "/fonts";
        File dir = new File(fontDir);
        if (!dir.exists()) {
            System.out.println("  Font JSON: SKIPPED (no fonts/ directory)");
            return;
        }

        File indexFile = new File(fontDir + "/index.dat");
        if (!indexFile.exists()) {
            System.out.println("  Font JSON: SKIPPED (no index.dat)");
            return;
        }

        byte[] indexDat = readFileBytes(indexFile.getAbsolutePath());
        String jsonDir = outputPath + "/fonts_json";
        mkdirs(jsonDir);
        int parsed = 0;

        for (String fontName : FONT_FILE_NAMES) {
            File fontFile = new File(fontDir + "/" + fontName + ".dat");
            if (!fontFile.exists()) continue;

            try {
                byte[] fontDat = readFileBytes(fontFile.getAbsolutePath());
                if (fontDat.length < 4) continue;

                Stream dat = new Stream(fontDat);
                Stream idx = new Stream(indexDat);

                // Font .dat starts with word pointing into index.dat.
                // RSFont constructor: stream_1.currentOffset = stream.readUnsignedWord() + 4
                int indexOffset = dat.readUnsignedWord();
                idx.position = indexOffset + 4; // skip fullWidth/fullHeight words

                // Skip palette: read palette size byte, skip (paletteSize-1)*3 bytes
                int paletteSize = idx.readUnsignedByte();
                if (paletteSize > 0) {
                    idx.position += 3 * (paletteSize - 1);
                }

                StringBuilder sb = new StringBuilder(8192);
                sb.append("{\n  \"fontName\": ").append(jsonEscape(fontName)).append(",\n  \"glyphs\": [\n");

                int baseCharHeight = 0;
                int glyphCount = 0;
                for (int g = 0; g < 256; g++) {
                    if (idx.position + 7 > indexDat.length) break;

                    int offsetX = idx.readUnsignedByte();
                    int offsetY = idx.readUnsignedByte();
                    int width = idx.readUnsignedWord();
                    int height = idx.readUnsignedWord();
                    int format = idx.readUnsignedByte();

                    int pixelCount = width * height;

                    if (glyphCount > 0) sb.append(",\n");
                    sb.append("    {\"charCode\":").append(g);
                    if (g >= 32 && g <= 126) sb.append(",\"char\":").append(jsonEscape(String.valueOf((char) g)));
                    sb.append(",\"offsetX\":").append(offsetX);
                    sb.append(",\"offsetY\":").append(offsetY);
                    sb.append(",\"width\":").append(width);
                    sb.append(",\"height\":").append(height);
                    sb.append(",\"format\":").append(format);

                    if (pixelCount > 0 && dat.position + pixelCount <= fontDat.length) {
                        byte[] pixels = new byte[pixelCount];
                        System.arraycopy(dat.buffer, dat.position, pixels, 0, pixelCount);
                        dat.position += pixelCount;
                        sb.append(",\"pixels\":").append(jsonEscape(Base64.getEncoder().encodeToString(pixels)));
                    }

                    sb.append('}');
                    if (height > baseCharHeight && g < 128) baseCharHeight = height;
                    glyphCount++;
                }

                sb.append("\n  ],\n  \"baseCharacterHeight\": ").append(baseCharHeight);
                sb.append("\n}\n");
                writeFile(jsonDir + "/" + fontName + ".json", sb.toString());
                parsed++;
            } catch (Exception e) {
                System.err.println("    Warning: Failed to parse font '" + fontName + "': " + e.getMessage());
            }
        }

        fontJsonCount = parsed;
        System.out.println("  Font JSON: " + parsed + " fonts parsed");
    }

    /**
     * Parses the wordenc archive (archive 7) into JSON.
     * Contains 4 binary files: badenc.txt, domainenc.txt, fragmentsenc.txt, tldlist.txt
     */
    private static void parseWordencToJson() throws IOException {
        byte[] archiveData = null;
        try {
            archiveData = decompressors[0].decompress(7);
        } catch (Exception ignored) {}

        if (archiveData == null || archiveData.length == 0) {
            System.out.println("  Wordenc JSON: SKIPPED (archive 7 not found)");
            return;
        }

        ArchiveLoader wordencArchive = new ArchiveLoader(archiveData);
        String jsonDir = outputPath + "/wordenc_json";
        mkdirs(jsonDir);
        int parsed = 0;

        // 1. badenc.txt — bad words list
        byte[] badData = wordencArchive.extractFile("badenc.txt");
        if (badData != null && badData.length >= 4) {
            try {
                Stream s = new Stream(badData);
                int count = s.readDWord();
                StringBuilder sb = new StringBuilder(count * 40);
                sb.append("{\n  \"type\": \"badWords\",\n  \"count\": ").append(count).append(",\n  \"words\": [\n");

                for (int i = 0; i < count && s.position < badData.length; i++) {
                    int charCount = s.readUnsignedByte();
                    byte[] chars = new byte[charCount];
                    for (int c = 0; c < charCount && s.position < badData.length; c++)
                        chars[c] = (byte) s.readUnsignedByte();

                    int exCount = 0;
                    if (s.position < badData.length) exCount = s.readUnsignedByte();

                    if (i > 0) sb.append(",\n");
                    sb.append("    {\"word\":").append(jsonEscape(new String(chars)));
                    if (exCount > 0) {
                        sb.append(",\"exceptions\":[");
                        for (int e = 0; e < exCount && s.position + 1 < badData.length; e++) {
                            int exA = s.readUnsignedByte();
                            int exB = s.readUnsignedByte();
                            if (e > 0) sb.append(',');
                            sb.append("[").append(exA).append(",").append(exB).append("]");
                        }
                        sb.append(']');
                    }
                    sb.append('}');
                }

                sb.append("\n  ]\n}\n");
                writeFile(jsonDir + "/bad_words.json", sb.toString());
                parsed++;
            } catch (Exception e) {
                System.err.println("    Warning: Failed to parse badenc.txt: " + e.getMessage());
            }
        }

        // 2. domainenc.txt — blocked domains
        byte[] domainData = wordencArchive.extractFile("domainenc.txt");
        if (domainData != null && domainData.length >= 4) {
            try {
                Stream s = new Stream(domainData);
                int count = s.readDWord();
                StringBuilder sb = new StringBuilder(count * 20);
                sb.append("{\n  \"type\": \"blockedDomains\",\n  \"count\": ").append(count).append(",\n  \"domains\": [\n");

                for (int i = 0; i < count && s.position < domainData.length; i++) {
                    int charCount = s.readUnsignedByte();
                    byte[] chars = new byte[charCount];
                    for (int c = 0; c < charCount && s.position < domainData.length; c++)
                        chars[c] = (byte) s.readUnsignedByte();

                    if (i > 0) sb.append(",\n");
                    sb.append("    ").append(jsonEscape(new String(chars)));
                }

                sb.append("\n  ]\n}\n");
                writeFile(jsonDir + "/blocked_domains.json", sb.toString());
                parsed++;
            } catch (Exception e) {
                System.err.println("    Warning: Failed to parse domainenc.txt: " + e.getMessage());
            }
        }

        // 3. fragmentsenc.txt — fragment hashes
        byte[] fragData = wordencArchive.extractFile("fragmentsenc.txt");
        if (fragData != null && fragData.length >= 4) {
            try {
                Stream s = new Stream(fragData);
                int count = s.readDWord();
                StringBuilder sb = new StringBuilder(count * 10);
                sb.append("{\n  \"type\": \"fragments\",\n  \"count\": ").append(count).append(",\n  \"hashes\": [");

                for (int i = 0; i < count && s.position + 1 < fragData.length; i++) {
                    if (i > 0) sb.append(',');
                    sb.append(s.readUnsignedWord());
                }

                sb.append("]\n}\n");
                writeFile(jsonDir + "/fragments.json", sb.toString());
                parsed++;
            } catch (Exception e) {
                System.err.println("    Warning: Failed to parse fragmentsenc.txt: " + e.getMessage());
            }
        }

        // 4. tldlist.txt — TLD list
        byte[] tldData = wordencArchive.extractFile("tldlist.txt");
        if (tldData != null && tldData.length >= 4) {
            try {
                Stream s = new Stream(tldData);
                int count = s.readDWord();
                StringBuilder sb = new StringBuilder(count * 20);
                sb.append("{\n  \"type\": \"tlds\",\n  \"count\": ").append(count).append(",\n  \"tlds\": [\n");

                for (int i = 0; i < count && s.position < tldData.length; i++) {
                    int tldType = s.readUnsignedByte();
                    int charCount = s.readUnsignedByte();
                    byte[] chars = new byte[charCount];
                    for (int c = 0; c < charCount && s.position < tldData.length; c++)
                        chars[c] = (byte) s.readUnsignedByte();

                    if (i > 0) sb.append(",\n");
                    sb.append("    {\"tld\":").append(jsonEscape(new String(chars)));
                    sb.append(",\"type\":").append(tldType).append('}');
                }

                sb.append("\n  ]\n}\n");
                writeFile(jsonDir + "/tld_list.json", sb.toString());
                parsed++;
            } catch (Exception e) {
                System.err.println("    Warning: Failed to parse tldlist.txt: " + e.getMessage());
            }
        }

        wordencJsonCount = parsed;
        System.out.println("  Wordenc JSON: " + parsed + " files parsed from archive 7");
    }

    /**
     * Parses the sounds archive (archive 8) into JSON.
     * Contains synthesizer instrument definitions with envelopes, oscillators, filters.
     * Binary format: sequential sound effects terminated by soundId == 65535.
     */
    private static void parseSoundsToJson() throws IOException {
        byte[] archiveData = null;
        try {
            archiveData = decompressors[0].decompress(8);
        } catch (Exception ignored) {}

        if (archiveData == null || archiveData.length == 0) {
            System.out.println("  Sounds JSON: SKIPPED (archive 8 not found)");
            return;
        }

        ArchiveLoader soundsArchive = new ArchiveLoader(archiveData);
        byte[] soundsDat = soundsArchive.extractFile("sounds.dat");

        if (soundsDat == null || soundsDat.length < 4) {
            System.out.println("  Sounds JSON: SKIPPED (no sounds.dat in archive)");
            return;
        }

        String jsonDir = outputPath + "/sounds_json";
        mkdirs(jsonDir);

        Stream s = new Stream(soundsDat);
        StringBuilder sb = new StringBuilder(soundsDat.length * 2);
        sb.append("{\n  \"sounds\": [\n");
        int soundCount = 0;

        while (s.position + 2 <= soundsDat.length) {
            try {
                // Each sound effect has 10 instrument slots
                StringBuilder soundSb = new StringBuilder(512);
                boolean hasInstrument = false;
                soundSb.append("    {\"instruments\": [");

                for (int inst = 0; inst < 10; inst++) {
                    if (s.position >= soundsDat.length) break;

                    // Each instrument starts with pitch envelope
                    String instJson = parseInstrument(s, soundsDat);
                    if (instJson == null) {
                        // Null instrument (empty slot)
                        if (inst > 0) soundSb.append(',');
                        soundSb.append("null");
                    } else {
                        if (inst > 0) soundSb.append(',');
                        soundSb.append(instJson);
                        hasInstrument = true;
                    }
                }

                soundSb.append("]");

                // Loop start/end
                if (s.position + 4 <= soundsDat.length) {
                    int loopStart = s.readUnsignedWord();
                    int loopEnd = s.readUnsignedWord();
                    if (loopStart > 0 || loopEnd > 0) {
                        soundSb.append(",\"loopStart\":").append(loopStart);
                        soundSb.append(",\"loopEnd\":").append(loopEnd);
                    }
                }

                soundSb.append('}');

                if (hasInstrument) {
                    if (soundCount > 0) sb.append(",\n");
                    sb.append(soundSb);
                    soundCount++;
                }

                // Safety: if we've read past 95% of the buffer without finding data, stop
                if (s.position > soundsDat.length - 2) break;
            } catch (Exception e) {
                break; // End of valid data
            }
        }

        sb.append("\n  ],\n  \"totalSounds\": ").append(soundCount).append("\n}\n");
        writeFile(jsonDir + "/sounds.json", sb.toString());
        soundJsonCount = soundCount;
        System.out.println("  Sounds JSON: " + soundCount + " sound effects parsed");
    }

    /** Parses a single instrument from the sound stream. Returns JSON string or null. */
    private static String parseInstrument(Stream s, byte[] data) {
        if (s.position >= data.length) return null;

        // Pitch envelope
        String pitchEnv = parseEnvelope(s, data);
        if (pitchEnv == null) return null;

        // Volume envelope
        String volumeEnv = parseEnvelope(s, data);

        StringBuilder sb = new StringBuilder(256);
        sb.append("{\"pitchEnvelope\":").append(pitchEnv);
        if (volumeEnv != null) sb.append(",\"volumeEnvelope\":").append(volumeEnv);

        // Modulation envelope
        if (s.position < data.length) {
            String modEnv = parseEnvelope(s, data);
            if (modEnv != null) sb.append(",\"modulationEnvelope\":").append(modEnv);
        }

        // Gating envelope
        if (s.position < data.length) {
            String gateEnv = parseEnvelope(s, data);
            if (gateEnv != null) sb.append(",\"gatingEnvelope\":").append(gateEnv);
        }

        // Oscillator volume
        if (s.position + 2 <= data.length) {
            int oscVolCount = s.readUnsignedByte();
            if (oscVolCount > 0 && oscVolCount <= 10) {
                sb.append(",\"oscillatorVolumes\":[");
                for (int i = 0; i < oscVolCount; i++) {
                    if (s.position + 2 > data.length) break;
                    if (i > 0) sb.append(',');
                    sb.append(s.readUnsignedWord());
                }
                sb.append(']');
            }
        }

        // Oscillator pitch
        if (s.position + 2 <= data.length) {
            int oscPitchCount = s.readUnsignedByte();
            if (oscPitchCount > 0 && oscPitchCount <= 10) {
                sb.append(",\"oscillatorPitches\":[");
                for (int i = 0; i < oscPitchCount; i++) {
                    if (s.position + 2 > data.length) break;
                    if (i > 0) sb.append(',');
                    sb.append(s.readUnsignedWord());
                }
                sb.append(']');
            }
        }

        // Delay + filter
        if (s.position + 4 <= data.length) {
            int delayTime = s.readUnsignedWord();
            int delayDecay = s.readUnsignedWord();
            if (delayTime > 0 || delayDecay > 0) {
                sb.append(",\"delayTime\":").append(delayTime);
                sb.append(",\"delayDecay\":").append(delayDecay);
            }
        }

        // Duration
        if (s.position + 2 <= data.length) {
            int duration = s.readUnsignedWord();
            sb.append(",\"duration\":").append(duration);
        }

        sb.append('}');
        return sb.toString();
    }

    /** Parses a single envelope from the sound stream. Returns JSON string or null. */
    private static String parseEnvelope(Stream s, byte[] data) {
        if (s.position >= data.length) return null;
        int waveformType = s.readUnsignedByte();
        if (waveformType == 0) return null; // no envelope

        StringBuilder sb = new StringBuilder(64);
        sb.append("{\"waveform\":").append(waveformType);

        if (s.position + 8 <= data.length) {
            int rangeStart = s.readDWord();
            int rangeEnd = s.readDWord();
            sb.append(",\"rangeStart\":").append(rangeStart);
            sb.append(",\"rangeEnd\":").append(rangeEnd);
        }

        if (s.position < data.length) {
            int keyframeCount = s.readUnsignedByte();
            if (keyframeCount > 0 && keyframeCount <= 100) {
                sb.append(",\"keyframes\":[");
                for (int i = 0; i < keyframeCount; i++) {
                    if (s.position + 4 > data.length) break;
                    int pos = s.readUnsignedWord();
                    int val = s.readUnsignedWord();
                    if (i > 0) sb.append(',');
                    sb.append("[").append(pos).append(",").append(val).append("]");
                }
                sb.append(']');
            }
        }

        sb.append('}');
        return sb.toString();
    }

    // ═════════════════════════════════════════════════════════════════
    // PHASE 6: MASTER INDEX
    // ═════════════════════════════════════════════════════════════════

    private static void writeMasterIndex(long startTime) throws IOException {
        long elapsed = System.currentTimeMillis() - startTime;
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"cachePath\": ").append(jsonEscape(cachePath)).append(",\n");
        sb.append("  \"extractionDate\": ").append(jsonEscape(new Date().toString())).append(",\n");
        sb.append("  \"extractionTimeMs\": ").append(elapsed).append(",\n");
        sb.append("  \"counts\": {\n");
        sb.append("    \"items\": ").append(itemCount).append(",\n");
        sb.append("    \"npcs\": ").append(npcCount).append(",\n");
        sb.append("    \"objects\": ").append(objectCount).append(",\n");
        sb.append("    \"animations\": ").append(animCount).append(",\n");
        sb.append("    \"graphics\": ").append(graphicsCount).append(",\n");
        sb.append("    \"floorsUnderlay\": ").append(underlayCount).append(",\n");
        sb.append("    \"floorsOverlay\": ").append(overlayCount).append(",\n");
        sb.append("    \"identityKits\": ").append(idkCount).append(",\n");
        sb.append("    \"varps\": ").append(varpCount).append(",\n");
        sb.append("    \"varbits\": ").append(varbitCount).append(",\n");
        sb.append("    \"cacheSprites\": ").append(spriteCount).append(",\n");
        sb.append("    \"mediaSprites\": ").append(mediaSpriteCount).append(",\n");
        sb.append("    \"textures\": ").append(textureCount).append(",\n");
        sb.append("    \"models\": ").append(modelCount).append(",\n");
        sb.append("    \"mapFiles\": ").append(mapCount).append(",\n");
        sb.append("    \"animFrames\": ").append(animFrameCount).append(",\n");
        sb.append("    \"interfaces\": ").append(interfaceCount).append(",\n");
        sb.append("    \"midiTracks\": ").append(midiIndexCount).append(",\n");
        sb.append("    \"titleSprites\": ").append(titleSpriteCount).append(",\n");
        sb.append("    \"rawArchives\": ").append(rawArchiveCount).append(",\n");
        sb.append("    \"animFrameJsons\": ").append(animFrameJsonCount).append(",\n");
        sb.append("    \"landscapeJsons\": ").append(landscapeJsonCount).append(",\n");
        sb.append("    \"objectSpawnJsons\": ").append(objectSpawnJsonCount).append(",\n");
        sb.append("    \"fontJsons\": ").append(fontJsonCount).append(",\n");
        sb.append("    \"wordencJsons\": ").append(wordencJsonCount).append(",\n");
        sb.append("    \"soundJsons\": ").append(soundJsonCount).append("\n");
        sb.append("  },\n");
        sb.append("  \"files\": [\n");
        sb.append("    \"items.json\",\n");
        sb.append("    \"npcs.json\",\n");
        sb.append("    \"objects.json\",\n");
        sb.append("    \"animations.json\",\n");
        sb.append("    \"graphics.json\",\n");
        sb.append("    \"floors_underlay.json\",\n");
        sb.append("    \"floors_overlay.json\",\n");
        sb.append("    \"identity_kits.json\",\n");
        sb.append("    \"varps.json\",\n");
        sb.append("    \"varbits.json\",\n");
        sb.append("    \"map_index.json\",\n");
        sb.append("    \"interfaces.json\",\n");
        sb.append("    \"midi_index.json\",\n");
        sb.append("    \"sprites/\",\n");
        sb.append("    \"media_sprites/\",\n");
        sb.append("    \"textures/\",\n");
        sb.append("    \"title_sprites/\",\n");
        sb.append("    \"fonts/\",\n");
        sb.append("    \"models/\",\n");
        sb.append("    \"maps/\",\n");
        sb.append("    \"anim_frames/\",\n");
        sb.append("    \"archives/\",\n");
        sb.append("    \"anim_frames_json/\",\n");
        sb.append("    \"maps_json/\",\n");
        sb.append("    \"fonts_json/\",\n");
        sb.append("    \"wordenc_json/\",\n");
        sb.append("    \"sounds_json/\"\n");
        sb.append("  ]\n");
        sb.append("}\n");
        writeFile(outputPath + "/_index.json", sb.toString());
        System.out.println("  Master index written.");
    }

    // ═════════════════════════════════════════════════════════════════
    // UTILITY METHODS
    // ═════════════════════════════════════════════════════════════════

    private static byte[] readFileBytes(String path) throws IOException {
        File f = new File(path);
        byte[] data = new byte[(int) f.length()];
        try (FileInputStream fis = new FileInputStream(f)) {
            int read = 0;
            while (read < data.length) {
                int n = fis.read(data, read, data.length - read);
                if (n == -1) break;
                read += n;
            }
        }
        return data;
    }

    private static byte[] gzipDecompress(byte[] compressed) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed))) {
            byte[] buf = new byte[8192];
            int n;
            while ((n = gis.read(buf)) != -1) {
                baos.write(buf, 0, n);
            }
        }
        return baos.toByteArray();
    }

    /**
     * Lenient decompression that relaxes block metadata validation.
     * Standard Decompressor.decompress() rejects entries where the sector chain
     * has mismatched entryId/fileId (common with on-demand fetched data).
     * This method reads the data anyway, trusting the idx pointer and size.
     */
    private static byte[] decompressLenient(RandomAccessFile dataFileRef, RandomAccessFile idxFileRef, int idxId, int entryId) {
        try {
            byte[] buf = new byte[520];

            idxFileRef.seek((long) entryId * 6);
            if (idxFileRef.read(buf, 0, 6) != 6) return null;

            int dataSize = ((buf[0] & 0xff) << 16) + ((buf[1] & 0xff) << 8) + (buf[2] & 0xff);
            int firstBlock = ((buf[3] & 0xff) << 16) + ((buf[4] & 0xff) << 8) + (buf[5] & 0xff);

            if (dataSize <= 0 || firstBlock <= 0 || firstBlock > dataFileRef.length() / 520) return null;

            byte[] result = new byte[dataSize];
            int bytesRead = 0;
            int currentBlock = firstBlock;

            while (bytesRead < dataSize) {
                if (currentBlock == 0 || currentBlock > dataFileRef.length() / 520) break;

                dataFileRef.seek((long) currentBlock * 520);
                int chunkSize = Math.min(512, dataSize - bytesRead);
                int totalToRead = chunkSize + 8;

                if (dataFileRef.read(buf, 0, totalToRead) != totalToRead) break;

                // Read next block pointer but skip entryId/fileId validation
                int nextBlock = ((buf[4] & 0xff) << 16) + ((buf[5] & 0xff) << 8) + (buf[6] & 0xff);

                System.arraycopy(buf, 8, result, bytesRead, chunkSize);
                bytesRead += chunkSize;
                currentBlock = nextBlock;
            }

            return bytesRead == dataSize ? result : null;
        } catch (IOException e) {
            return null;
        }
    }
}
