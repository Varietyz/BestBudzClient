package com.bestbudz.cache;

import static com.bestbudz.engine.core.Client.terrainData;
import static com.bestbudz.engine.core.Client.objectData;
import static com.bestbudz.engine.core.Client.mapRegionIds;
import static com.bestbudz.engine.core.Client.terrainIndices;
import static com.bestbudz.engine.core.Client.objectIndices;
import static com.bestbudz.engine.core.Client.cacheManager;

import java.io.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.*;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

public class EmbeddedMapCache {

	private static final ConcurrentHashMap<Integer, byte[]> embeddedMapFiles = new ConcurrentHashMap<>(2048, 0.9f, 32);
	private static final ConcurrentHashMap<Integer, RegionData> embeddedRegions = new ConcurrentHashMap<>(1024, 0.9f, 16);

	private static final AtomicInteger totalFilesLoaded = new AtomicInteger(0);
	private static final AtomicInteger regionsFromIndex = new AtomicInteger(0);
	private static volatile boolean initialized = false;

	private static final int MAX_THREADS = Math.min(64, Runtime.getRuntime().availableProcessors() * 8);
	private static final int OPTIMAL_BATCH_SIZE = 25;
	private static final int BUFFER_SIZE = 32768;

	private static final AtomicLong totalLoadTime = new AtomicLong(0);
	private static final AtomicLong totalBytesLoaded = new AtomicLong(0);
	private static final AtomicInteger failedLoads = new AtomicInteger(0);

	private static ForkJoinPool blazingPool;
	private static final Object initializationLock = new Object();

	private static final int[] KNOWN_FILE_RANGES = {
		0, 1999,
		2000, 2999,
		3000, 3999,
		4000, 4999,
		5000, 5999
	};

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

			System.out.println("[EmbeddedMapCache] Starting BLAZING FAST initialization...");
			long startTime = System.currentTimeMillis();

			try {

				setupBlazingThreadPool();

				loadFilesBlazingFast();

				loadMapIndexLightning();

				initialized = true;

				long totalTime = System.currentTimeMillis() - startTime;
				totalLoadTime.set(totalTime);

				logBlazingResults(totalTime);

			} catch (Exception e) {
				System.err.println("❌ [EmbeddedMapCache] Blazing initialization failed: " + e.getMessage());
				e.printStackTrace();
				initialized = true;
			} finally {
				shutdownBlazingThreadPool();
			}
		}
	}

	private static void setupBlazingThreadPool() {
		blazingPool = new ForkJoinPool(
			MAX_THREADS,
			pool -> {
				ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
				thread.setName("BlazingLoader-" + thread.getPoolIndex());
				thread.setPriority(Thread.MAX_PRIORITY - 1);
				return thread;
			},
			null,
			true
		);

		System.out.println("[EmbeddedMapCache] Blazing thread pool: " + MAX_THREADS + " threads at high priority");
	}

	private static void loadFilesBlazingFast() {
		System.out.println("[EmbeddedMapCache] Smart range-based loading...");
		long startTime = System.currentTimeMillis();

		List<CompletableFuture<RangeResult>> rangeFutures = new ArrayList<>();

		for (int i = 0; i < KNOWN_FILE_RANGES.length; i += 2) {
			int rangeStart = KNOWN_FILE_RANGES[i];
			int rangeEnd = KNOWN_FILE_RANGES[i + 1];

			CompletableFuture<RangeResult> future = CompletableFuture
				.supplyAsync(() -> processFileRange(rangeStart, rangeEnd), blazingPool);
			rangeFutures.add(future);
		}

		CompletableFuture<Void> allRanges = CompletableFuture.allOf(
			rangeFutures.toArray(new CompletableFuture[0])
		);

		try {
			allRanges.get(10, TimeUnit.SECONDS);

			for (CompletableFuture<RangeResult> future : rangeFutures) {
				RangeResult result = future.get();
				embeddedMapFiles.putAll(result.files);
				totalFilesLoaded.addAndGet(result.filesLoaded);
				totalBytesLoaded.addAndGet(result.bytesLoaded);
				failedLoads.addAndGet(result.failedLoads);
			}

		} catch (Exception e) {
			System.err.println("❌ [EmbeddedMapCache] Range loading failed: " + e.getMessage());
		}

		long loadTime = System.currentTimeMillis() - startTime;
		System.out.println("[EmbeddedMapCache] Range loading: " + loadTime + "ms, " +
			totalFilesLoaded.get() + " files");
	}

	private static RangeResult processFileRange(int rangeStart, int rangeEnd) {
		Map<Integer, byte[]> rangeFiles = new ConcurrentHashMap<>();
		AtomicInteger rangeFilesLoaded = new AtomicInteger(0);
		AtomicLong rangeBytesLoaded = new AtomicLong(0);
		AtomicInteger rangeFailedLoads = new AtomicInteger(0);

		List<CompletableFuture<Void>> batchFutures = new ArrayList<>();

		for (int batchStart = rangeStart; batchStart <= rangeEnd; batchStart += OPTIMAL_BATCH_SIZE) {
			int batchEnd = Math.min(batchStart + OPTIMAL_BATCH_SIZE, rangeEnd + 1);

			int finalBatchStart = batchStart;
			CompletableFuture<Void> batchFuture = CompletableFuture.runAsync(() -> {
				processMicroBatch(finalBatchStart, batchEnd, rangeFiles, rangeFilesLoaded,
					rangeBytesLoaded, rangeFailedLoads);
			}, blazingPool);

			batchFutures.add(batchFuture);
		}

		try {
			CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0]))
				.get(5, TimeUnit.SECONDS);
		} catch (Exception e) {
			System.err.println("❌ [EmbeddedMapCache] Range " + rangeStart + "-" + rangeEnd + " failed: " + e.getMessage());
		}

		return new RangeResult(rangeFiles, rangeFilesLoaded.get(),
			rangeBytesLoaded.get(), rangeFailedLoads.get());
	}

	private static void processMicroBatch(int batchStart, int batchEnd,
										  Map<Integer, byte[]> rangeFiles, AtomicInteger rangeFilesLoaded,
										  AtomicLong rangeBytesLoaded, AtomicInteger rangeFailedLoads) {

		byte[] sharedBuffer = new byte[BUFFER_SIZE];

		for (int fileId = batchStart; fileId < batchEnd; fileId++) {
			try {
				byte[] data = loadFileOptimized(fileId, sharedBuffer);
				if (data != null) {
					rangeFiles.put(fileId, data);
					rangeFilesLoaded.incrementAndGet();
					rangeBytesLoaded.addAndGet(data.length);
				}
			} catch (Exception e) {
				rangeFailedLoads.incrementAndGet();
			}
		}
	}

	private static byte[] loadFileOptimized(int fileId, byte[] sharedBuffer) {
		if (fileId == -1) return null;

		String resourcePath = "/maps/mapdata/" + fileId + ".gz";
		InputStream stream = EmbeddedMapCache.class.getResourceAsStream(resourcePath);
		boolean isCompressed = true;

		if (stream == null) {

			resourcePath = "/maps/mapdata/" + fileId;
			stream = EmbeddedMapCache.class.getResourceAsStream(resourcePath);
			isCompressed = false;

			if (stream == null) {
				return null;
			}
		}

		try {

			byte[] data = readStreamZeroAlloc(stream, sharedBuffer, isCompressed);
			return data;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				stream.close();
			} catch (IOException e) {

			}
		}
	}

	private static byte[] readStreamZeroAlloc(InputStream stream, byte[] sharedBuffer, boolean isCompressed) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream(16384);

		if (isCompressed) {
			try (GZIPInputStream gzipStream = new GZIPInputStream(stream, 8192)) {
				int bytesRead;
				while ((bytesRead = gzipStream.read(sharedBuffer)) != -1) {
					output.write(sharedBuffer, 0, bytesRead);
				}
			}
		} else {
			int bytesRead;
			while ((bytesRead = stream.read(sharedBuffer)) != -1) {
				output.write(sharedBuffer, 0, bytesRead);
			}
		}

		return output.toByteArray();
	}

	private static void loadMapIndexLightning() throws IOException {
		InputStream indexStream = EmbeddedMapCache.class.getResourceAsStream("/maps/map_index");
		if (indexStream == null) {
			System.out.println("[EmbeddedMapCache] No map_index found");
			return;
		}

		try (BufferedInputStream buffered = new BufferedInputStream(indexStream, 32768);
			 DataInputStream dis = new DataInputStream(buffered)) {

			dis.readUnsignedShort();

			List<RegionEntry> entries = new ArrayList<>(1024);

			try {
				while (true) {
					int regionId = dis.readUnsignedShort();
					int mapFileId = dis.readUnsignedShort();
					int landscapeFileId = dis.readUnsignedShort();
					entries.add(new RegionEntry(regionId, mapFileId, landscapeFileId));
				}
			} catch (EOFException e) {

			}

			System.out.println("[EmbeddedMapCache] Lightning processing " + entries.size() + " regions");

			AtomicInteger successCount = new AtomicInteger(0);

			entries.parallelStream().forEach(entry -> {
				byte[] mapData = embeddedMapFiles.get(entry.mapFileId);
				byte[] landscapeData = embeddedMapFiles.get(entry.landscapeFileId);

				if (mapData != null && landscapeData != null) {
					embeddedRegions.put(entry.regionId,
						new RegionData(mapData, landscapeData, entry.mapFileId, entry.landscapeFileId));
					successCount.incrementAndGet();
				}
			});

			regionsFromIndex.set(successCount.get());
			System.out.println("[EmbeddedMapCache] " + successCount.get() + "/" + entries.size() + " regions indexed");
		}
	}

	private static class RangeResult {
		final Map<Integer, byte[]> files;
		final int filesLoaded;
		final long bytesLoaded;
		final int failedLoads;

		RangeResult(Map<Integer, byte[]> files, int filesLoaded, long bytesLoaded, int failedLoads) {
			this.files = files;
			this.filesLoaded = filesLoaded;
			this.bytesLoaded = bytesLoaded;
			this.failedLoads = failedLoads;
		}
	}

	private static class RegionEntry {
		final int regionId;
		final int mapFileId;
		final int landscapeFileId;

		RegionEntry(int regionId, int mapFileId, int landscapeFileId) {
			this.regionId = regionId;
			this.mapFileId = mapFileId;
			this.landscapeFileId = landscapeFileId;
		}
	}

	public static int loadAllEmbeddedRegions() {
		if (!initialized || mapRegionIds == null || terrainData == null) {
			return 0;
		}

		int totalRegions = mapRegionIds.length;
		if (totalRegions == 0) return 0;

		System.out.println("[EmbeddedMapCache] Blazing region loading: " + totalRegions + " regions");
		long startTime = System.currentTimeMillis();

		AtomicInteger loadedCount = new AtomicInteger(0);

		IntStream.range(0, totalRegions)
			.parallel()
			.forEach(i -> {
				int regionId = mapRegionIds[i];
				if (tryLoadEmbeddedRegionFromCache(regionId, i) || tryLoadEmbeddedRegionDirect(regionId, i)) {
					loadedCount.incrementAndGet();
				} else {
					queueRegionForOnDemandFetcher(i);
				}
			});

		long loadTime = System.currentTimeMillis() - startTime;
		System.out.println("[EmbeddedMapCache] Blazing region loading: " + loadTime + "ms, " +
			loadedCount.get() + "/" + totalRegions + " regions");

		logRegionResults(loadedCount.get(), totalRegions);
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

	private static void queueRegionForOnDemandFetcher(int arrayIndex) {
		if (cacheManager == null) return;

		try {
			if (arrayIndex < terrainIndices.length && terrainIndices[arrayIndex] != -1) {
				cacheManager.requestFile(terrainIndices[arrayIndex], 3);
			}
			if (arrayIndex < objectIndices.length && objectIndices[arrayIndex] != -1) {
				cacheManager.requestFile(objectIndices[arrayIndex], 3);
			}
		} catch (Exception e) {

		}
	}

	private static void shutdownBlazingThreadPool() {
		if (blazingPool != null) {
			blazingPool.shutdown();
			try {
				if (!blazingPool.awaitTermination(1, TimeUnit.SECONDS)) {
					blazingPool.shutdownNow();
				}
			} catch (InterruptedException e) {
				blazingPool.shutdownNow();
				Thread.currentThread().interrupt();
			}
		}
	}

	private static void logBlazingResults(long totalTime) {
		double throughputFilesPerSec = totalFilesLoaded.get() > 0 && totalTime > 0 ?
			(totalFilesLoaded.get() * 1000.0 / totalTime) : 0.0;
		double throughputMBPerSec = totalBytesLoaded.get() > 0 && totalTime > 0 ?
			(totalBytesLoaded.get() / 1024.0 / 1024.0) * 1000.0 / totalTime : 0.0;

		System.out.println("🔥 [EmbeddedMapCache] BLAZING FAST initialization complete!");
		System.out.println("⚡ [EmbeddedMapCache] Performance metrics:");
		System.out.println("   • Total time: " + totalTime + "ms");
		System.out.println("   • Files loaded: " + totalFilesLoaded.get());
		System.out.println("   • Data loaded: " + (totalBytesLoaded.get() / 1024) + " KB");
		System.out.println("   • Indexed regions: " + regionsFromIndex.get());
		System.out.println("   • Throughput: " + String.format("%.0f", throughputFilesPerSec) + " files/sec, " + String.format("%.1f", throughputMBPerSec) + " MB/sec");
		System.out.println("   • Failed loads: " + failedLoads.get());
		System.out.println("   • Thread pool: " + MAX_THREADS + " blazing threads");

		if (totalTime < 500) {
			System.out.println("🎯 [EmbeddedMapCache] SUB-500MS TARGET ACHIEVED! ⚡⚡⚡");
		} else if (totalTime < 1000) {
			System.out.println("🎯 [EmbeddedMapCache] SUB-SECOND ACHIEVED! (" + totalTime + "ms)");
		} else {
			System.out.println("⚠️ [EmbeddedMapCache] Target: sub-500ms, actual: " + totalTime + "ms");
		}

		System.out.println("🔥 [EmbeddedMapCache] Ready for INSTANT region loading!");
	}

	private static void logRegionResults(int loadedCount, int totalRegions) {
		if (loadedCount == totalRegions) {
			System.out.println("🎉 [EmbeddedMapCache] ALL REGIONS LOADED - ZERO LAG!");
		} else {
			System.out.println("✅ [EmbeddedMapCache] Loaded " + loadedCount + "/" + totalRegions + " regions");
		}
	}

	public static boolean isRegionEmbedded(int regionId) {
		return embeddedRegions.containsKey(regionId);
	}

	public static boolean isReady() {
		return initialized && totalFilesLoaded.get() > 0;
	}

	public static String getCacheStats() {
		return String.format("[EmbeddedMapCache] BLAZING: %d files (%d KB), %d regions, %dms, %d threads",
			totalFilesLoaded.get(), totalBytesLoaded.get() / 1024, regionsFromIndex.get(),
			totalLoadTime.get(), MAX_THREADS);
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

		return String.format("[EmbeddedMapCache] BLAZING: %.0f files/sec, %d threads, %dms total",
			throughput, MAX_THREADS, totalTime);
	}
}
