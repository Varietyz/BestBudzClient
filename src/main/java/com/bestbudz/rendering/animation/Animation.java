package com.bestbudz.rendering.animation;

import bestbudz.config.AnimationConfig;
import bestbudz.config.AnimationEntry;
import com.bestbudz.cache.FlatBufferConfigLoader;
import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.rendering.SequenceFrame;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.nio.ByteBuffer;
import java.util.Map;

public final class Animation {

    public static Animation[] anims;
    public int frameCount;
    public int[] frameIds;
    public int[] anIntArray354;
    public int[] anIntArray355;
    public int loopOffset;
    public int[] anIntArray357;
    public boolean resetOnMove;
    public int anInt359;
    public int anInt360;
    public int anInt361;
    public int maxLoops;
    public int priority;
    public int anInt364;
    public int anInt365;
    private Animation() {
        loopOffset = -1;
        resetOnMove = false;
        anInt359 = 5;
		anInt360 = -1;
		anInt361 = -1;
		maxLoops = 99;
		priority = -1;
		anInt364 = -1;
        anInt365 = 1;
    }

    public static void unpackConfig()
    {
        // Try FlatBuffer first
        ByteBuffer buf = FlatBufferConfigLoader.load("animations.fb");
        if (buf != null) {
            AnimationConfig config = AnimationConfig.getRootAsAnimationConfig(buf);
            int maxId = 0;
            for (int i = 0; i < config.entriesLength(); i++) {
                int id = config.entries(i).id();
                if (id > maxId) maxId = id;
            }
            int length = maxId + 1;
            if (anims == null)
                anims = new Animation[length + 5000];
            for (int i = 0; i < config.entriesLength(); i++) {
                AnimationEntry fb = config.entries(i);
                int id = fb.id();
                if (anims[id] == null)
                    anims[id] = new Animation();
                Animation a = anims[id];
                a.loadFromFlatBuffer(fb);
            }
            System.out.println("Animations Loaded (FlatBuffer): " + config.entriesLength());
            return;
        }

        JsonObject json = JsonCacheLoader.loadJsonObject("animations.json");
        if (json == null) {
            System.err.println("Failed to load animations.json");
            return;
        }

        int maxId = 0;
        for (String key : json.keySet()) {
            int id = Integer.parseInt(key);
            if (id > maxId) maxId = id;
        }

        int length = maxId + 1;
        if (anims == null)
            anims = new Animation[length + 5000];

        int loaded = 0;
        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            int id = Integer.parseInt(entry.getKey());
            JsonObject def = entry.getValue().getAsJsonObject();

            if (anims[id] == null)
                anims[id] = new Animation();

            anims[id].readFromJson(def);
            loaded++;
        }

        System.out.println("Animations Loaded (JSON): " + loaded);
    }

    public static int getMaxFrameFileId() {
        int max = 0;
        if (anims == null) return max;
        for (Animation anim : anims) {
            if (anim != null && anim.frameIds != null) {
                for (int frameId : anim.frameIds) {
                    if (frameId < 0) continue;
                    int fileId = frameId >> 16;
                    if (fileId > max) max = fileId;
                }
            }
        }
        return max;
    }

    public int getFrameDuration(int i) {
        int j = anIntArray355[i];
        if(j == 0)
        {
            SequenceFrame class36 = SequenceFrame.getFrame(frameIds[i]);
            if(class36 != null)
                j = anIntArray355[i] = class36.version;
        }
        if(j == 0)
            j = 1;
        return j;
    }

    public void loadFromFlatBuffer(AnimationEntry fb) {
        frameCount = fb.frameCount();
        if (fb.frameIdsLength() > 0) {
            frameIds = new int[fb.frameIdsLength()];
            anIntArray354 = new int[fb.frameIdsLength()];
            anIntArray355 = new int[fb.frameIdsLength()];
            for (int i = 0; i < fb.frameIdsLength(); i++) {
                frameIds[i] = fb.frameIds(i);
                anIntArray354[i] = -1;
            }
            frameCount = fb.frameIdsLength();
        }
        if (fb.frameDurationsLength() > 0 && anIntArray355 != null) {
            for (int i = 0; i < Math.min(fb.frameDurationsLength(), anIntArray355.length); i++)
                anIntArray355[i] = fb.frameDurations(i);
        }
        loopOffset = fb.loopOffset();
        if (fb.interleaveOrderLength() > 0) {
            anIntArray357 = new int[fb.interleaveOrderLength() + 1];
            for (int i = 0; i < fb.interleaveOrderLength(); i++)
                anIntArray357[i] = fb.interleaveOrder(i);
            anIntArray357[fb.interleaveOrderLength()] = 9999999;
        }
        resetOnMove = fb.resetOnMove();
        anInt359 = fb.priority();
        anInt360 = fb.playerOffhand();
        anInt361 = fb.playerMainhand();
        maxLoops = fb.maxLoops();
        priority = fb.animatingPrecedence();
        anInt364 = fb.walkingPrecedence();
        anInt365 = fb.replayMode();

        // Apply defaults same as binary readValues
        if (frameCount == 0) {
            frameCount = 1;
            frameIds = new int[]{-1};
            anIntArray354 = new int[]{-1};
            anIntArray355 = new int[]{-1};
        }
        if (priority == -1)
            priority = anIntArray357 != null ? 2 : 0;
        if (anInt364 == -1)
            anInt364 = anIntArray357 != null ? 2 : 0;
    }

    private void readFromJson(JsonObject json) {
        if (json.has("frameCount")) {
            frameCount = json.get("frameCount").getAsInt();
        }
        if (json.has("frameIds")) {
            JsonArray arr = json.getAsJsonArray("frameIds");
            frameIds = new int[arr.size()];
            anIntArray354 = new int[arr.size()];
            anIntArray355 = new int[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                frameIds[i] = arr.get(i).getAsInt();
                anIntArray354[i] = -1;
            }
            frameCount = arr.size();
        }
        if (json.has("frameDurations")) {
            JsonArray arr = json.getAsJsonArray("frameDurations");
            if (anIntArray355 == null) {
                anIntArray355 = new int[arr.size()];
            }
            for (int i = 0; i < Math.min(arr.size(), anIntArray355.length); i++) {
                anIntArray355[i] = arr.get(i).getAsInt();
            }
        }
        if (json.has("loopOffset")) {
            loopOffset = json.get("loopOffset").getAsInt();
        }
        if (json.has("interleaveOrder")) {
            JsonArray arr = json.getAsJsonArray("interleaveOrder");
            anIntArray357 = new int[arr.size() + 1];
            for (int i = 0; i < arr.size(); i++) {
                anIntArray357[i] = arr.get(i).getAsInt();
            }
            anIntArray357[arr.size()] = 9999999;
        }
        if (json.has("resetOnMove")) {
            resetOnMove = json.get("resetOnMove").getAsBoolean();
        }
        if (json.has("priority")) {
            anInt359 = json.get("priority").getAsInt();
        }
        if (json.has("playerOffhand")) {
            anInt360 = json.get("playerOffhand").getAsInt();
        }
        if (json.has("playerMainhand")) {
            anInt361 = json.get("playerMainhand").getAsInt();
        }
        if (json.has("maxLoops")) {
            maxLoops = json.get("maxLoops").getAsInt();
        }
        if (json.has("animatingPrecedence")) {
            priority = json.get("animatingPrecedence").getAsInt();
        }
        if (json.has("walkingPrecedence")) {
            anInt364 = json.get("walkingPrecedence").getAsInt();
        }
        if (json.has("replayMode")) {
            anInt365 = json.get("replayMode").getAsInt();
        }

        // Apply defaults same as binary readValues
        if (frameCount == 0) {
            frameCount = 1;
            frameIds = new int[1];
            frameIds[0] = -1;
            anIntArray354 = new int[1];
            anIntArray354[0] = -1;
            anIntArray355 = new int[1];
            anIntArray355[0] = -1;
        }
        if (priority == -1)
            if (anIntArray357 != null)
                priority = 2;
            else
                priority = 0;
        if (anInt364 == -1) {
            if (anIntArray357 != null) {
                anInt364 = 2;
                return;
            }
            anInt364 = 0;
        }
    }
}
