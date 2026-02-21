package com.bestbudz.network;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.engine.core.Client;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Simple index lookup class for map, model, animation, and MIDI indices.
 * All game data is now loaded from extracted JSON cache — no server downloading.
 */
public final class CacheManager {

	private int[] mapKeys = new int[0];
	private int[] landscapeIndices = new int[0];
	private int[] objectIndices = new int[0];
	private int[] midiIndices = new int[0];
	private byte[] modelIndices = new byte[0];
	private int[] animationIndices = new int[0];

	public String statusString = "";
	public int onDemandCycle;
	public int connectionErrors;

	public void start(Client client) {
		JsonObject mapIndexJson = JsonCacheLoader.loadJsonObject("map_index.json");
		if (mapIndexJson != null && mapIndexJson.has("regions")) {
			JsonArray regions = mapIndexJson.getAsJsonArray("regions");
			int mapCount = regions.size();

			mapKeys = new int[mapCount];
			landscapeIndices = new int[mapCount];
			objectIndices = new int[mapCount];

			for (int i = 0; i < mapCount; i++) {
				JsonObject region = regions.get(i).getAsJsonObject();
				mapKeys[i] = region.get("regionId").getAsInt();
				landscapeIndices[i] = region.get("landscapeFile").getAsInt();
				objectIndices[i] = region.get("objectsFile").getAsInt();
			}
			System.out.println("Maps Loaded: " + mapCount);
		}

		JsonObject midiIndexJson = JsonCacheLoader.loadJsonObject("midi_index.json");
		if (midiIndexJson != null && midiIndexJson.has("tracks")) {
			JsonArray tracks = midiIndexJson.getAsJsonArray("tracks");
			int midiCount = tracks.size();
			midiIndices = new int[midiCount];
			for (int i = 0; i < midiCount; i++) {
				midiIndices[i] = tracks.get(i).getAsBoolean() ? 1 : 0;
			}
		}
	}

	public int getMapIndex(int i, int k, int l) {
		int combined = (l << 8) + k;
		for (int j = 0; j < mapKeys.length; j++) {
			if (mapKeys[j] == combined) {
				if (i == 0) {
					return landscapeIndices[j] > 3535 ? -1 : landscapeIndices[j];
				} else {
					return objectIndices[j] > 3535 ? -1 : objectIndices[j];
				}
			}
		}
		return -1;
	}

	public boolean hasObjectData(int i) {
		for (int k = 0; k < mapKeys.length; k++) {
			if (objectIndices[k] == i) return true;
		}
		return false;
	}

	public int getModelCount() {
		return 29191;
	}

	public int getModelIndex(int i) {
		if (i < 0 || i >= modelIndices.length) return 0;
		return modelIndices[i] & 0xff;
	}

	public int getAnimCount() {
		return animationIndices.length;
	}

	public int getVersionCount(int j) {
		return 0;
	}

	public boolean isMidiEnabled(int i) {
		if (i < 0 || i >= midiIndices.length) return false;
		return midiIndices[i] == 1;
	}

	public int getNodeCount() {
		return 0;
	}

	public void disable() {}

	public void clearPriority() {}

	public void enqueueRequest(int dataType, int id) {
		// All data now loaded from JSON — no server downloading
	}

	public void requestFile(int i, int j) {
		// All data now loaded from JSON — no server downloading
	}

	public void requestModel(int i) {
		// Models now loaded from JSON
	}
}
