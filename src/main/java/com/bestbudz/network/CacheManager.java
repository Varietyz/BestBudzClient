package com.bestbudz.network;

import com.bestbudz.cache.JsonCacheLoader;
import com.bestbudz.cache.JsonModelLoader;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Lightweight MIDI index lookup. All game data is loaded from extracted JSON cache.
 */
public final class CacheManager {

	private int[] midiIndices = new int[0];

	public void start() {
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

	public int getModelCount() {
		return JsonCacheLoader.isAvailable("models/_index.json")
			? JsonModelLoader.getModelCount()
			: 0;
	}

	public boolean isMidiEnabled(int i) {
		if (i < 0 || i >= midiIndices.length) return false;
		return midiIndices[i] == 1;
	}
}
