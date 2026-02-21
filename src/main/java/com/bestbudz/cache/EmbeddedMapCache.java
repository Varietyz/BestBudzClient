package com.bestbudz.cache;

import static com.bestbudz.engine.core.Client.terrainData;
import static com.bestbudz.engine.core.Client.objectData;
import static com.bestbudz.engine.core.Client.mapRegionIds;
import static com.bestbudz.engine.core.Client.terrainIndices;
import static com.bestbudz.engine.core.Client.objectIndices;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;
import java.util.stream.IntStream;

public class EmbeddedMapCache {

	private static final ConcurrentHashMap<Integer, byte[]> embeddedMapFiles = new ConcurrentHashMap<>(2048, 0.9f, 32);
	private static final ConcurrentHashMap<Integer, RegionData> embeddedRegions = new ConcurrentHashMap<>(1024, 0.9f, 16);

	private static final AtomicInteger totalFilesLoaded = new AtomicInteger(0);
	private static final AtomicInteger regionsFromIndex = new AtomicInteger(0);
	private static volatile boolean initialized = false;

	private static final AtomicLong totalLoadTime = new AtomicLong(0);
	private static final AtomicLong totalBytesLoaded = new AtomicLong(0);
	private static final AtomicInteger failedLoads = new AtomicInteger(0);

	private static final Object initializationLock = new Object();

	private static class RegionData {
		final byte[] mapData;
		final byte[] landscapeData;
		final int mapFileId;
		final int landscapeFileId;

		RegionData(byte[] mapData, byte[] landscapeData, int mapFileId, int landscapeFileId) {
			this.mapData = mapData;
			this.landscapeData = landscapeData;
			this.mapFileId = mapFileId;
			this.landscapeFileId = landscapeFileId;
		}
	}

	public static void initialize() {
		if (initialized) return;

		synchronized (initializationLock) {
			if (initialized) return;

			System.out.println("[EmbeddedMapCache] Starting JSON-based initialization...");
			long startTime = System.currentTimeMillis();

			try {
				loadMapFilesFromJson();
				loadMapIndexFromJson();

				initialized = true;
				long totalTime = System.currentTimeMillis() - startTime;
				totalLoadTime.set(totalTime);

				System.out.println("[EmbeddedMapCache] Initialization complete: " + totalTime + "ms, " +
					totalFilesLoaded.get() + " files, " + regionsFromIndex.get() + " regions");

			} catch (Exception e) {
				System.err.println("[EmbeddedMapCache] Initialization failed: " + e.getMessage());
				e.printStackTrace();
				initialized = true;
			}
		}
	}

	private static void loadMapFilesFromJson() {
		// Load the maps index to discover available file IDs
		JsonObject mapsIndex = JsonCacheLoader.loadJsonObject("maps/_index.json");
		if (mapsIndex == null) {
			System.out.println("[EmbeddedMapCache] No maps/_index.json found, trying direct file scan");
			return;
		}

		Set<Integer> fileIds = new HashSet<>();
		for (String key : mapsIndex.keySet()) {
			try {
				fileIds.add(Integer.parseInt(key));
			} catch (NumberFormatException ignored) {}
		}

		// Also collect file IDs from the map_index.json
		JsonObject mapIndex = JsonCacheLoader.loadJsonObject("map_index.json");
		if (mapIndex != null && mapIndex.has("regions")) {
			JsonArray regions = mapIndex.getAsJsonArray("regions");
			for (JsonElement el : regions) {
				JsonObject region = el.getAsJsonObject();
				if (region.has("landscapeFile")) fileIds.add(region.get("landscapeFile").getAsInt());
				if (region.has("objectsFile")) fileIds.add(region.get("objectsFile").getAsInt());
			}
		}

		System.out.println("[EmbeddedMapCache] Loading " + fileIds.size() + " map files...");

		for (int fileId : fileIds) {
			try {
				byte[] data = JsonCacheLoader.loadFileBytes("maps/landscape_" + fileId + ".dat");
				if (data != null) {
					embeddedMapFiles.put(fileId, data);
					totalFilesLoaded.incrementAndGet();
					totalBytesLoaded.addAndGet(data.length);
				}
			} catch (Exception e) {
				failedLoads.incrementAndGet();
			}
		}
	}

	private static void loadMapIndexFromJson() {
		JsonObject mapIndex = JsonCacheLoader.loadJsonObject("map_index.json");
		if (mapIndex == null || !mapIndex.has("regions")) {
			System.out.println("[EmbeddedMapCache] No map_index.json found");
			return;
		}

		JsonArray regions = mapIndex.getAsJsonArray("regions");
		AtomicInteger successCount = new AtomicInteger(0);

		for (JsonElement el : regions) {
			JsonObject region = el.getAsJsonObject();
			int regionId = region.get("regionId").getAsInt();
			int landscapeFileId = region.get("landscapeFile").getAsInt();
			int objectsFileId = region.get("objectsFile").getAsInt();

			byte[] mapData = embeddedMapFiles.get(landscapeFileId);
			byte[] landscapeData = embeddedMapFiles.get(objectsFileId);

			if (mapData != null && landscapeData != null) {
				embeddedRegions.put(regionId,
					new RegionData(mapData, landscapeData, landscapeFileId, objectsFileId));
				successCount.incrementAndGet();
			}
		}

		regionsFromIndex.set(successCount.get());
		System.out.println("[EmbeddedMapCache] " + successCount.get() + "/" + regions.size() + " regions indexed");
	}

	public static int loadAllEmbeddedRegions() {
		if (!initialized || mapRegionIds == null || terrainData == null) {
			return 0;
		}

		int totalRegions = mapRegionIds.length;
		if (totalRegions == 0) return 0;

		AtomicInteger loadedCount = new AtomicInteger(0);

		IntStream.range(0, totalRegions)
			.parallel()
			.forEach(i -> {
				int regionId = mapRegionIds[i];
				if (tryLoadEmbeddedRegionFromCache(regionId, i) || tryLoadEmbeddedRegionDirect(regionId, i)) {
					loadedCount.incrementAndGet();
				}
			});

		System.out.println("[EmbeddedMapCache] Loaded " + loadedCount.get() + "/" + totalRegions + " regions");
		return loadedCount.get();
	}

	private static boolean tryLoadEmbeddedRegionFromCache(int regionId, int arrayIndex) {
		RegionData regionData = embeddedRegions.get(regionId);
		if (regionData == null) return false;
		return setRegionDataFast(arrayIndex, regionData);
	}

	private static boolean tryLoadEmbeddedRegionDirect(int regionId, int arrayIndex) {
		if (arrayIndex >= terrainIndices.length || arrayIndex >= objectIndices.length) {
			return false;
		}

		int mapFileId = terrainIndices[arrayIndex];
		int landscapeFileId = objectIndices[arrayIndex];

		if (mapFileId == -1 || landscapeFileId == -1) {
			return true;
		}

		byte[] mapData = embeddedMapFiles.get(mapFileId);
		byte[] landscapeData = embeddedMapFiles.get(landscapeFileId);

		if (mapData != null && landscapeData != null) {
			RegionData regionData = new RegionData(mapData, landscapeData, mapFileId, landscapeFileId);
			return setRegionDataFast(arrayIndex, regionData);
		}

		return false;
	}

	private static synchronized boolean setRegionDataFast(int arrayIndex, RegionData regionData) {
		try {
			if (arrayIndex >= terrainData.length || arrayIndex >= objectData.length ||
				arrayIndex >= terrainIndices.length || arrayIndex >= objectIndices.length) {
				return false;
			}

			terrainData[arrayIndex] = regionData.mapData;
			objectData[arrayIndex] = regionData.landscapeData;
			terrainIndices[arrayIndex] = regionData.mapFileId;
			objectIndices[arrayIndex] = regionData.landscapeFileId;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public static byte[] getEmbeddedFileData(int fileId) {
		if (!initialized) return null;
		return embeddedMapFiles.get(fileId);
	}

	public static boolean isRegionEmbedded(int regionId) {
		return embeddedRegions.containsKey(regionId);
	}

	public static boolean isReady() {
		return initialized && totalFilesLoaded.get() > 0;
	}

	public static String getCacheStats() {
		return String.format("[EmbeddedMapCache] %d files (%d KB), %d regions, %dms",
			totalFilesLoaded.get(), totalBytesLoaded.get() / 1024, regionsFromIndex.get(),
			totalLoadTime.get());
	}

	public static int getLoadedFileCount() {
		return totalFilesLoaded.get();
	}

	public static int getIndexedRegionCount() {
		return regionsFromIndex.get();
	}

	public static String getPerformanceStats() {
		long totalFiles = totalFilesLoaded.get();
		long totalTime = totalLoadTime.get();
		double throughput = totalFiles > 0 && totalTime > 0 ? (totalFiles * 1000.0 / totalTime) : 0.0;

		return String.format("[EmbeddedMapCache] %.0f files/sec, %dms total",
			throughput, totalTime);
	}
}
