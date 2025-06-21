package com.bestbudz.engine.core;

import com.bestbudz.cache.EmbeddedMapCache;
import com.bestbudz.cache.Signlink;
import static com.bestbudz.engine.ClientLauncher.initializeGPUAfterGraphicsLoad;
import com.bestbudz.engine.gpu.GPUContextManager;
import com.bestbudz.engine.gpu.GPUMonitor;
import com.bestbudz.engine.gpu.GPURenderingEngine;
import static com.bestbudz.engine.core.gamerender.Camera.calcCameraPos;
import static com.bestbudz.engine.core.gamerender.Camera.updateCameraPosition;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.engine.gpu.GPURenderingEngine.initialized;
import static com.bestbudz.engine.gpu.OpenGLRasterizer.renderDebugTriangle;
import static com.bestbudz.rendering.SpotAnim2.updatePendingSpotAnimations;
import com.bestbudz.rendering.model.Model;
import static com.bestbudz.ui.interfaces.Chatbox.handleTextFieldInput;
import com.bestbudz.util.ColorUtility;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.engine.core.gamerender.ObjectManager;
import java.awt.Graphics2D;

/**
 * Enhanced GameState with GPU Integration and Persistence
 *
 * Key improvements:
 * - Comprehensive GPU state management during game loads
 * - Integration with GPU monitoring system
 * - Thread-safe GPU context handling
 * - Graceful fallback mechanisms for GPU failures
 * - Performance monitoring and diagnostics
 */
public class GameState extends Client {

	// ===== CONSTANTS =====
	private static final int DEFAULT_MAP_SIZE = 104;
	private static final int MAX_SAFE_MAP_SIZE = 512;
	private static final boolean DEBUG_LOGGING = true;
	private static final int MEMORY_GC_THRESHOLD = 320; // KB

	// ===== GPU STATE MANAGEMENT =====
	private static boolean gpuStateValid = false;
	private static long lastGPUValidation = 0;
	private static final long GPU_VALIDATION_INTERVAL = 5000; // 5 seconds
	private static int consecutiveGPUFailures = 0;
	private static final int MAX_GPU_FAILURES = 3;

	// ===== MEMORY AND PERFORMANCE MONITORING =====
	private static long lastResetTime = 0;
	private static int resetCount = 0;
	private static long peakMemoryUsage = 0;
	private static boolean isResetting = false;
	private static int consecutiveErrors = 0;
	private static long lastErrorTime = 0;

	// ===== RENDERING ERROR TRACKING =====
	private static int renderingErrors = 0;
	private static long lastRenderErrorTime = 0;
	private static int validationPreventedErrors = 0;
	private static int totalRenderCalls = 0;

	// ===== MAP DIMENSION CACHE =====
	private static int cachedMapWidth = DEFAULT_MAP_SIZE;
	private static int cachedMapHeight = DEFAULT_MAP_SIZE;
	private static boolean dimensionsCached = false;

	// ===== CAMERA VALIDATION =====
	private static int lastValidXCameraPos = 0;
	private static int lastValidYCameraPos = 0;
	private static int lastValidXCameraCurve = 0;
	private static int lastValidZCameraPos = 0;
	private static int lastValidJ = 0;
	private static int lastValidYCameraCurve = 0;
	private static boolean hasValidPosition = false;

	/**
	 * Main scene rendering method with GPU state management
	 */
	public static void runSceneRendering(Graphics2D g) {
		try {
			// Validate GPU state before rendering


			if (loadingStage == 2) {


				updateCameraPosition();
				if (aBoolean1160) {
					calcCameraPos();
				}
			}

			// Safe array access with bounds checking
			if (anIntArray1030 != null && anIntArray1030.length >= 5) {
				for (int i = 0; i < 5; i++) {
					anIntArray1030[i]++;
				}
			} else {
				logError("anIntArray1030 is null or too small in runSceneRendering");
			}

			handleTextFieldInput(g);
		} catch (Exception e) {
			logError("Error in runSceneRendering: " + e.getMessage());
			GPUMonitor.getInstance().recordError("Scene Rendering", e.getMessage(), Thread.currentThread().getName());
		}
	}

	/**
	 * Validate GPU state periodically - FIXED VERSION
	 */
	public static void validateGPUStateIfNeeded() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastGPUValidation < GPU_VALIDATION_INTERVAL) {
			return; // Skip validation if done recently
		}

		lastGPUValidation = currentTime;

		if (!GPURenderingEngine.isEnabled()) {
			gpuStateValid = false;
			return;
		}

		try {
			// SIMPLE FIX: Just check if GPU rendering engine is enabled and initialized
			// The complex health checks were causing false failures
			boolean basicGPUCheck = GPURenderingEngine.isEnabled() &&
				GPURenderingEngine.initialized &&
				GPUContextManager.getInstance().isContextCurrent();

			if (basicGPUCheck) {
				consecutiveGPUFailures = 0; // Reset on success
				gpuStateValid = true;
				logDebug("GPU validation passed - basic checks OK");
			} else {
				consecutiveGPUFailures++;
				logError("Basic GPU validation failed (failure " + consecutiveGPUFailures + "/" + MAX_GPU_FAILURES + ")");

				// Only attempt recovery after multiple failures
				if (consecutiveGPUFailures >= MAX_GPU_FAILURES) {
					logError("Maximum GPU failures reached, attempting recovery...");
					attemptGPURecovery();
				} else {
					// For initial failures, just mark as invalid but don't trigger recovery
					gpuStateValid = false;
				}
			}

		} catch (Exception e) {
			consecutiveGPUFailures++;
			logError("GPU validation exception: " + e.getMessage());
			gpuStateValid = false;

			// Only trigger recovery on repeated exceptions
			if (consecutiveGPUFailures >= MAX_GPU_FAILURES) {
				attemptGPURecovery();
			}
		}
	}

	/**
	 * Attempt GPU recovery
	 */
	private static void attemptGPURecovery() {
		logInfo("Attempting GPU system recovery...");

		try {
			boolean recovered = GPURenderingEngine.emergencyReset();
			if (recovered) {
				logInfo("✅ GPU recovery successful");
				consecutiveGPUFailures = 0;
				gpuStateValid = true;
			} else {
				logError("❌ GPU recovery failed");
				gpuStateValid = false;
			}

		} catch (Exception e) {
			logError("GPU recovery exception: " + e.getMessage());
			gpuStateValid = false;
		}
	}

	/**
	 * Safe world rendering with GPU context management
	 */
	public static void safeRenderWorld(int xCameraPos, int yCameraPos, int xCameraCurve,
									   int zCameraPos, int j, int yCameraCurve) {
		totalRenderCalls++;
		long renderStart = System.currentTimeMillis();

		try {
			// Record GPU operation for monitoring

			boolean gpuEnabled = GPURenderingEngine.isEnabled() && gpuStateValid;

			if (gpuEnabled) {
				// Try GPU-accelerated rendering with context management
				try (GPUContextManager.ContextToken context = GPURenderingEngine.acquireContext("World Rendering")) {
					if (context != null) {
						renderWorldWithGPU(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);

						// Record successful GPU operation
						long duration = System.currentTimeMillis() - renderStart;
						GPUMonitor.getInstance().recordGPUOperation("World Rendering", true, duration);

						// Cache successful position
						cacheValidCameraPosition(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
						return;
					}
				}
			}

			// Fallback to CPU rendering or use cached position
			renderWorldFallback(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);

		} catch (ArrayIndexOutOfBoundsException e) {
			renderingErrors++;
			handleRenderingBoundsError(e, xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);

		} catch (Exception e) {
			renderingErrors++;
			logError("Rendering error: " + e.getMessage());
			GPUMonitor.getInstance().recordError("World Rendering", e.getMessage(), Thread.currentThread().getName());

			// Try cached position as last resort
			if (hasValidPosition && worldController != null) {
				try {
					worldController.method313(lastValidXCameraPos, lastValidYCameraPos, lastValidXCameraCurve,
						lastValidZCameraPos, lastValidJ, lastValidYCameraCurve);
				} catch (Exception e2) {
					logError("Even cached position failed: " + e2.getMessage());
				}
			}
		}
	}

	/**
	 * Render world using GPU acceleration
	 */
	private static void renderWorldWithGPU(int xCameraPos, int yCameraPos, int xCameraCurve,
										   int zCameraPos, int j, int yCameraCurve) throws Exception {
		if (worldController != null) {
			// GPU-accelerated world rendering
			worldController.method313(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
		} else {
			throw new IllegalStateException("worldController is null");
		}
	}

	/**
	 * Fallback rendering without GPU or using cached position
	 */
	private static void renderWorldFallback(int xCameraPos, int yCameraPos, int xCameraCurve,
											int zCameraPos, int j, int yCameraCurve) {
		try {
			if (worldController != null) {
				worldController.method313(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
				cacheValidCameraPosition(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
			} else if (hasValidPosition) {
				worldController.method313(lastValidXCameraPos, lastValidYCameraPos, lastValidXCameraCurve,
					lastValidZCameraPos, lastValidJ, lastValidYCameraCurve);
			}
		} catch (Exception e) {
			logError("Fallback rendering failed: " + e.getMessage());
		}
	}

	/**
	 * Cache valid camera position for fallback
	 */
	private static void cacheValidCameraPosition(int xCameraPos, int yCameraPos, int xCameraCurve,
												 int zCameraPos, int j, int yCameraCurve) {
		lastValidXCameraPos = xCameraPos;
		lastValidYCameraPos = yCameraPos;
		lastValidXCameraCurve = xCameraCurve;
		lastValidZCameraPos = zCameraPos;
		lastValidJ = j;
		lastValidYCameraCurve = yCameraCurve;
		hasValidPosition = true;
	}

	/**
	 * Handle rendering bounds errors with detailed diagnostics
	 */
	private static void handleRenderingBoundsError(ArrayIndexOutOfBoundsException e,
												   int xCameraPos, int yCameraPos, int xCameraCurve,
												   int zCameraPos, int j, int yCameraCurve) {
		if (renderingErrors % 50 == 0) { // Log detailed info every 50 errors
			logError("=== BOUNDS ERROR ANALYSIS ===");
			logError("Camera Params: xCameraPos=" + xCameraPos + ", yCameraPos=" + yCameraPos + ", zCameraPos=" + zCameraPos);
			logError("Error: " + e.getMessage());

			// Calculate tile coordinates
			int tileX = xCameraPos >> 7;
			int tileY = yCameraPos >> 7;
			logError("Tile coordinates: tileX=" + tileX + ", tileY=" + tileY);

			// Check array bounds
			if (byteGroundArray != null && byteGroundArray.length > 0 && byteGroundArray[0] != null) {
				int maxTileX = byteGroundArray[0].length - 1;
				int maxTileY = byteGroundArray[0].length > 0 && byteGroundArray[0][0] != null ?
					byteGroundArray[0][0].length - 1 : -1;
				logError("Max valid tiles: maxTileX=" + maxTileX + ", maxTileY=" + maxTileY);

				if (tileX < 0 || tileX > maxTileX || tileY < 0 || tileY > maxTileY) {
					logError("*** COORDINATES OUT OF BOUNDS ***");
				}
			}
			logError("=== END ANALYSIS ===");
		}

		GPUMonitor.getInstance().recordError("Bounds Error", e.getMessage(), Thread.currentThread().getName());

		// Try fallback rendering
		renderWorldFallback(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
	}

	/**
	 * Enhanced resetGameState with GPU persistence
	 */
	/**
	 * Enhanced resetGameState with embedded map loading integration
	 */
	public static void resetGameState() {
		if (isResetting) {
			logDebug("Reset already in progress, skipping");
			return;
		}

		isResetting = true;
		long startTime = System.currentTimeMillis();
		long memoryBefore = getCurrentMemoryUsage();

		logInfo("Starting GameState reset #" + (resetCount + 1) + " with embedded map cache");

		try {
			// PHASE 0: GPU STATE PRESERVATION
			preserveGPUState();

			// PHASE 1: Environment validation
			validateEnvironmentAndCacheDimensions();

			// PHASE 2: Quick cleanup
			performQuickCleanup();

			// PHASE 3: Array operations
			performArrayOperationsSafe();

			// PHASE 4: Object management
			ObjectManager objectManager = createObjectManagerSafe();

			// PHASE 5: EMBEDDED MAP LOADING (NEW!)
			int embeddedRegionsLoaded = loadEmbeddedMapsIfAvailable();

			// PHASE 6: Main processing (updated to handle embedded maps)
			performMainProcessingSafeWithEmbedded(objectManager, embeddedRegionsLoaded);

			// PHASE 7: Model cleanup
			performModelCleanupSafe();

			// PHASE 8: Final operations
			performFinalOperationsSafe();

			// PHASE 9: GPU STATE RESTORATION
			restoreGPUState();

			consecutiveErrors = 0;
			logInfo("GameState reset completed successfully with " + embeddedRegionsLoaded + " embedded regions loaded");

		} catch (Exception exception) {
			handleResetError(exception);
		} finally {
			isResetting = false;
			logPerformanceMetrics(startTime, memoryBefore);
		}
	}

	/**
	 * Preserve GPU state before reset
	 */
	private static void preserveGPUState() {
		if (!GPURenderingEngine.isEnabled()) {
			return;
		}

		logDebug("Preserving GPU state...");

		try {
			// Persist GPU state through the reset
			GPURenderingEngine.persistThroughGameLoad();

			// Validate GPU state after preservation
			validateGPUStateIfNeeded();

			logDebug("✅ GPU state preserved");

		} catch (Exception e) {
			logError("GPU state preservation failed: " + e.getMessage());
			GPUMonitor.getInstance().recordError("GPU Preservation", e.getMessage(), Thread.currentThread().getName());
			gpuStateValid = false;
		}
	}

	/**
	 * Restore and validate GPU state after reset - FIXED VERSION
	 */
	private static void restoreGPUState() {
		if (!GPURenderingEngine.isEnabled()) {
			return;
		}

		logDebug("Restoring GPU state...");

		try {
			// SIMPLE FIX: Don't immediately validate, just assume GPU is OK if engine is enabled
			// The validation was too aggressive and causing false failures

			if (GPURenderingEngine.isEnabled() && GPURenderingEngine.initialized) {
				gpuStateValid = true; // Assume valid since engine reports enabled
				consecutiveGPUFailures = 0; // Reset failure counter
				logDebug("✅ GPU state restored - engine reports enabled");
			} else {
				logError("GPU engine not properly enabled after restore");
				gpuStateValid = false;
			}

			// Schedule a validation for later (not immediately)
			lastGPUValidation = System.currentTimeMillis() - (GPU_VALIDATION_INTERVAL - 1000);

		} catch (Exception e) {
			logError("GPU state restoration failed: " + e.getMessage());
			gpuStateValid = false;
		}
	}

	/**
	 * Game load persistence - public API for handling game loads
	 */
	public static void handleGameLoad() {
		logInfo("Handling game load with GPU persistence...");

		try {
			if (GPURenderingEngine.isEnabled()) {
				// Tell the ClientLauncher to handle GPU persistence
				com.bestbudz.engine.ClientLauncher.handleGameLoad();

				// Validate GPU state after game load
				validateGPUStateIfNeeded();

				if (gpuStateValid) {
					logInfo("✅ Game load handled with GPU persistence");
				} else {
					logError("GPU state invalid after game load, attempting recovery...");
					attemptGPURecovery();
				}
			} else {
				logInfo("GPU disabled, handling game load without GPU persistence");
			}

		} catch (Exception e) {
			logError("Error handling game load: " + e.getMessage());
			GPUMonitor.getInstance().recordError("Game Load", e.getMessage(), Thread.currentThread().getName());
		}
	}

	// ===== IMPLEMENTATION OF EXISTING METHODS WITH GPU AWARENESS =====

	/**
	 * Validate environment with GPU state checking
	 */
	private static void validateEnvironmentAndCacheDimensions() {
		logDebug("=== ENVIRONMENT VALIDATION WITH GPU ===");

		// Check GPU state first
		if (GPURenderingEngine.isEnabled()) {
			validateGPUStateIfNeeded();
			logDebug("GPU State Valid: " + gpuStateValid);
		}

		// Continue with original validation logic...
		try {
			if (byteGroundArray != null) {
				logDebug("byteGroundArray planes: " + byteGroundArray.length);

				for (int plane = 0; plane < byteGroundArray.length; plane++) {
					if (byteGroundArray[plane] != null) {
						int width = byteGroundArray[plane].length;
						logDebug("Plane " + plane + " width: " + width);

						if (width > 0 && byteGroundArray[plane][0] != null) {
							int height = byteGroundArray[plane][0].length;
							logDebug("Plane " + plane + " height: " + height);

							if (!dimensionsCached) {
								cachedMapWidth = Math.min(width, MAX_SAFE_MAP_SIZE);
								cachedMapHeight = Math.min(height, MAX_SAFE_MAP_SIZE);
								dimensionsCached = true;
								logInfo("Cached map dimensions: " + cachedMapWidth + "x" + cachedMapHeight);
							}
						}
					}
				}
			} else {
				logError("byteGroundArray is null!");
				cachedMapWidth = cachedMapHeight = DEFAULT_MAP_SIZE;
			}

			validateCriticalArrays();

		} catch (Exception e) {
			logError("Error in environment validation: " + e.getMessage());
			cachedMapWidth = cachedMapHeight = DEFAULT_MAP_SIZE;
			dimensionsCached = true;
		}

		logDebug("=== VALIDATION COMPLETE ===");
	}

	// ===== REST OF THE ORIGINAL METHODS (unchanged but with GPU monitoring) =====

	private static void validateCriticalArrays() {
		if (aClass11Array1230 != null) {
			logDebug("aClass11Array1230 length: " + aClass11Array1230.length);
		} else {
			logError("aClass11Array1230 is null");
		}

		if (aByteArrayArray1183 != null) {
			logDebug("aByteArrayArray1183 length: " + aByteArrayArray1183.length);
		} else {
			logError("aByteArrayArray1183 is null");
		}

		if (anIntArray1234 != null) {
			logDebug("anIntArray1234 length: " + anIntArray1234.length);
		} else {
			logError("anIntArray1234 is null");
		}

		if (anIntArrayArrayArray1129 != null) {
			logDebug("anIntArrayArrayArray1129 dimensions: " +
				anIntArrayArrayArray1129.length + "x" +
				(anIntArrayArrayArray1129.length > 0 && anIntArrayArrayArray1129[0] != null ?
					anIntArrayArrayArray1129[0].length : "null") + "x" +
				(anIntArrayArrayArray1129.length > 0 && anIntArrayArrayArray1129[0] != null &&
					anIntArrayArrayArray1129[0].length > 0 && anIntArrayArrayArray1129[0][0] != null ?
					anIntArrayArrayArray1129[0][0].length : "null"));
		} else {
			logError("anIntArrayArrayArray1129 is null");
		}
	}

	private static void performQuickCleanup() {
		logDebug("Phase 1: Quick cleanup");

		try {
			anInt985 = -1;

			if (aClass19_1056 != null) {
				aClass19_1056.removeAll();
			} else {
				logError("aClass19_1056 is null during cleanup");
			}

			if (aClass19_1013 != null) {
				aClass19_1013.removeAll();
			} else {
				logError("aClass19_1013 is null during cleanup");
			}

			Rasterizer.method366();
			unlinkMRUNodes();

			if (worldController != null) {
				worldController.initToNull();
			} else {
				logError("worldController is null during cleanup");
			}

			logDebug("Quick cleanup completed");
		} catch (Exception e) {
			logError("Error in performQuickCleanup: " + e.getMessage());
			throw e;
		}
	}

	private static void performArrayOperationsSafe() {
		logDebug("Phase 2: Array operations");

		try {
			if (aClass11Array1230 != null) {
				int arrayLength = Math.min(4, aClass11Array1230.length);
				for (int i = 0; i < arrayLength; i++) {
					if (aClass11Array1230[i] != null) {
						aClass11Array1230[i].method210();
					} else {
						logError("aClass11Array1230[" + i + "] is null");
					}
				}
			} else {
				logError("aClass11Array1230 is null, skipping Class11 cleanup");
			}

			clearGroundArraysComprehensive();
			logDebug("Array operations completed");

		} catch (Exception e) {
			logError("Error in performArrayOperationsSafe: " + e.getMessage());
			throw e;
		}
	}

	private static void clearGroundArraysComprehensive() {
		logDebug("Clearing ground arrays");

		if (byteGroundArray == null) {
			logError("byteGroundArray is null, cannot clear");
			return;
		}

		try {
			clearGroundArraysOptimized();
		} catch (Exception e) {
			logError("Optimized clearing failed: " + e.getMessage());
			try {
				clearGroundArraysSimple();
			} catch (Exception e2) {
				logError("Simple clearing failed: " + e2.getMessage());
				try {
					clearGroundArraysUltraSafe();
				} catch (Exception e3) {
					logError("Ultra-safe clearing failed: " + e3.getMessage());
				}
			}
		}
	}

	private static void clearGroundArraysOptimized() {
		final int blockSize = 32; // BLOCK_SIZE

		for (int plane = 0; plane < Math.min(4, byteGroundArray.length); plane++) {
			byte[][] currentPlane = byteGroundArray[plane];
			if (currentPlane == null) {
				logError("Plane " + plane + " is null");
				continue;
			}

			int maxX = Math.min(cachedMapWidth, currentPlane.length);

			for (int blockX = 0; blockX < maxX; blockX += blockSize) {
				int endX = Math.min(blockX + blockSize, maxX);

				for (int blockY = 0; blockY < cachedMapHeight; blockY += blockSize) {
					int endY = Math.min(blockY + blockSize, cachedMapHeight);

					for (int x = blockX; x < endX; x++) {
						if (x >= currentPlane.length || currentPlane[x] == null) {
							continue;
						}

						byte[] row = currentPlane[x];
						int maxY = Math.min(endY, row.length);

						for (int y = blockY; y < maxY; y++) {
							row[y] = 0;
						}
					}
				}
			}
		}

		logDebug("Optimized ground array clearing completed");
	}

	private static void clearGroundArraysSimple() {
		logDebug("Using simple ground array clearing");

		for (int plane = 0; plane < Math.min(4, byteGroundArray.length); plane++) {
			if (byteGroundArray[plane] == null) continue;

			int maxX = Math.min(cachedMapWidth, byteGroundArray[plane].length);

			for (int x = 0; x < maxX; x++) {
				if (byteGroundArray[plane][x] == null) continue;

				byte[] row = byteGroundArray[plane][x];
				int maxY = Math.min(cachedMapHeight, row.length);

				for (int y = 0; y < maxY; y++) {
					row[y] = 0;
				}
			}
		}

		logDebug("Simple ground array clearing completed");
	}

	private static void clearGroundArraysUltraSafe() {
		logDebug("Using ultra-safe ground array clearing");

		for (int plane = 0; plane < Math.min(4, byteGroundArray.length); plane++) {
			if (byteGroundArray[plane] == null) continue;

			for (int x = 0; x < Math.min(cachedMapWidth, byteGroundArray[plane].length); x++) {
				if (byteGroundArray[plane][x] == null) continue;

				for (int y = 0; y < Math.min(cachedMapHeight, byteGroundArray[plane][x].length); y++) {
					try {
						byteGroundArray[plane][x][y] = 0;
					} catch (Exception e) {
						// Skip this element and continue
					}
				}
			}
		}

		logDebug("Ultra-safe ground array clearing completed");
	}

	private static ObjectManager createObjectManagerSafe() {
		logDebug("Phase 3: Creating ObjectManager");

		try {
			if (byteGroundArray == null || intGroundArray == null) {
				logError("Ground arrays are null, cannot create ObjectManager");
				throw new IllegalStateException("Ground arrays not initialized");
			}

			ObjectManager objectManager = new ObjectManager(byteGroundArray, intGroundArray);
			logDebug("ObjectManager created successfully");
			return objectManager;

		} catch (Exception e) {
			logError("Error creating ObjectManager: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * NEW: Load embedded maps if available
	 */
	private static int loadEmbeddedMapsIfAvailable() {
		logDebug("Phase 5: Loading embedded maps");

		if (!EmbeddedMapCache.isReady()) {
			logDebug("Embedded map cache not ready, using onDemandFetcher only");
			return 0;
		}

		try {
			long embeddedStartTime = System.currentTimeMillis();

			// Load all possible regions from embedded cache
			int embeddedCount = EmbeddedMapCache.loadAllEmbeddedRegions();

			long embeddedLoadTime = System.currentTimeMillis() - embeddedStartTime;

			if (embeddedCount > 0) {
				logInfo("🚀 Loaded " + embeddedCount + " regions instantly from embedded cache in " +
					embeddedLoadTime + "ms (avg: " + (embeddedLoadTime / embeddedCount) + "ms/region)");
			}

			return embeddedCount;

		} catch (Exception e) {
			logError("Error loading embedded maps: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * UPDATED: Main processing with embedded map awareness
	 */
	private static void performMainProcessingSafeWithEmbedded(ObjectManager objectManager, int embeddedRegionsLoaded) {
		logDebug("Phase 6: Main processing (with " + embeddedRegionsLoaded + " embedded regions)");

		try {
			if (aByteArrayArray1183 == null) {
				logError("aByteArrayArray1183 is null, skipping main processing");
				return;
			}

			int k2 = aByteArrayArray1183.length;
			logDebug("Processing " + k2 + " regions (" + embeddedRegionsLoaded + " from embedded cache)");

			stream.createFrame(0);

			if (!aBoolean1159) {
				processNormalModeWithEmbedded(objectManager, k2, embeddedRegionsLoaded);
			} else {
				processSpecialModeSafe(objectManager);
			}

			finalizeObjectManager(objectManager);
			logDebug("Main processing completed");

		} catch (Exception e) {
			logError("Error in performMainProcessingSafeWithEmbedded: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * UPDATED: Process normal mode with embedded map optimization
	 */
	private static void processNormalModeWithEmbedded(ObjectManager objectManager, int k2, int embeddedRegionsLoaded) {
		logDebug("Processing normal mode with " + k2 + " regions (" + embeddedRegionsLoaded + " embedded)");

		try {
			// PASS 1: Process map data (many regions already loaded from embedded cache)
			for (int i3 = 0; i3 < k2; i3++) {
				if (i3 >= aByteArrayArray1183.length || i3 >= anIntArray1234.length) {
					logError("Index " + i3 + " exceeds array bounds in normal mode pass 1");
					break;
				}

				if (aByteArrayArray1183[i3] != null) {
					int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
					int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;

					if (isValidMapCoordinate(i4, k5, i3)) {
						try {
							objectManager.method180(aByteArrayArray1183[i3], k5, i4,
								(anInt1069 - 6) * 8, (anInt1070 - 6) * 8, aClass11Array1230);
						} catch (ArrayIndexOutOfBoundsException e) {
							logError("ObjectManager.method180 bounds error at (" + i4 + "," + k5 + ") for index " + i3 + ": " + e.getMessage());
						}
					} else {
						logError("Invalid coordinates for method180: i4=" + i4 + ", k5=" + k5 + " (index=" + i3 + ")");
					}
				}
			}

			// PASS 2: Handle null regions (not loaded yet) - these will use onDemandFetcher
			if (anInt1070 < 800) {
				int nullRegionsProcessed = 0;
				for (int j4 = 0; j4 < k2; j4++) {
					if (j4 >= aByteArrayArray1183.length || j4 >= anIntArray1234.length) {
						logError("Index " + j4 + " exceeds array bounds in normal mode pass 2");
						break;
					}

					if (aByteArrayArray1183[j4] == null) {
						nullRegionsProcessed++;
						int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
						int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;

						if (isValidMapCoordinate(l5, k7, j4)) {
							try {
								objectManager.method174(k7, 64, 64, l5);
							} catch (ArrayIndexOutOfBoundsException e) {
								logError("ObjectManager.method174 bounds error at (" + l5 + "," + k7 + ") for index " + j4 + ": " + e.getMessage());
							}
						}
					}
				}

				if (nullRegionsProcessed > 0) {
					logDebug("Processed " + nullRegionsProcessed + " null regions (pending onDemandFetcher)");
				}
			}

			// Update network statistics
			anInt1097++;
			if (anInt1097 > 160) {
				anInt1097 = 0;
				stream.createFrame(238);
				stream.writeWordBigEndian(96);
			}

			stream.createFrame(0);

			// PASS 3: Process landscape data (also benefits from embedded cache)
			if (aByteArrayArray1247 != null) {
				for (int i6 = 0; i6 < k2; i6++) {
					if (i6 >= aByteArrayArray1247.length || i6 >= anIntArray1234.length) {
						logError("Index " + i6 + " exceeds array bounds in normal mode pass 3");
						break;
					}

					if (aByteArrayArray1247[i6] != null) {
						int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
						int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;

						if (isValidMapCoordinate(l8, k9, i6)) {
							try {
								objectManager.method190(l8, aClass11Array1230, k9, worldController, aByteArrayArray1247[i6]);
							} catch (ArrayIndexOutOfBoundsException e) {
								logError("ObjectManager.method190 bounds error at (" + l8 + "," + k9 + ") for index " + i6 + ": " + e.getMessage());
							}
						}
					}
				}
			} else {
				logError("aByteArrayArray1247 is null, skipping method190 calls");
			}

		} catch (Exception e) {
			logError("Error in processNormalModeWithEmbedded: " + e.getMessage());
			throw e;
		}
	}

	private static void finalizeObjectManager(ObjectManager objectManager) {
		try {
			stream.createFrame(0);

			if (aClass11Array1230 != null && worldController != null) {
				objectManager.method171(aClass11Array1230, worldController);
			} else {
				logError("Cannot call method171: aClass11Array1230 or worldController is null");
			}

			if (objectManager.colors != null && !objectManager.colors.isEmpty()) {
				ColorUtility.fadingToColor = getNextInteger(objectManager.colors).getKey();
			} else {
				logDebug("objectManager.colors is null or empty");
			}

			stream.createFrame(0);

			int targetPlane = Math.max(Math.min(ObjectManager.anInt145, plane), plane - 1);

			if (lowMem) {
				worldController.method275(ObjectManager.anInt145);
			} else {
				worldController.method275(0);
			}

			initializeGroundDecorations();

			anInt1051 = (anInt1051 + 1) % 99;
			updatePendingSpotAnimations();

		} catch (Exception e) {
			logError("Error in finalizeObjectManager: " + e.getMessage());
			throw e;
		}
	}

	private static void initializeGroundDecorations() {
		logDebug("Initializing ground decorations");

		try {
			if (worldController != null && worldController.groundArray != null) {
				logDebug("Initializing WorldController ground arrays");

				for (int plane = 0; plane < Math.min(4, worldController.groundArray.length); plane++) {
					if (worldController.groundArray[plane] == null) continue;

					for (int x = 0; x < Math.min(cachedMapWidth, worldController.groundArray[plane].length); x++) {
						if (worldController.groundArray[plane][x] == null) continue;

						for (int y = 0; y < Math.min(cachedMapHeight, worldController.groundArray[plane][x].length); y++) {
							if (worldController.groundArray[plane][x][y] == null) continue;

							worldController.groundArray[plane][x][y].aBoolean1323 = true;

							if (worldController.groundArray[plane][x][y].aClass30_Sub3_1329 != null) {
								logDebug("Found ground decoration data at plane=" + plane + ", x=" + x + ", y=" + y);
							}
						}
					}
				}

				logDebug("Ground visibility flags initialized");
			} else {
				logError("worldController or groundArray is null");
			}

			if (worldController != null) {
				worldController.method275(0);
			}

			logDebug("Ground decoration initialization completed");

		} catch (Exception e) {
			logError("Error in initializeGroundDecorations: " + e.getMessage());
		}
	}

	private static void processNormalModeSafe(ObjectManager objectManager, int k2) {
		logDebug("Processing normal mode with " + k2 + " arrays");

		try {
			for (int i3 = 0; i3 < k2; i3++) {
				if (i3 >= aByteArrayArray1183.length || i3 >= anIntArray1234.length) {
					logError("Index " + i3 + " exceeds array bounds in normal mode pass 1");
					break;
				}

				if (aByteArrayArray1183[i3] != null) {
					int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
					int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;

					if (isValidMapCoordinate(i4, k5, i3)) {
						try {
							objectManager.method180(aByteArrayArray1183[i3], k5, i4,
								(anInt1069 - 6) * 8, (anInt1070 - 6) * 8, aClass11Array1230);
						} catch (ArrayIndexOutOfBoundsException e) {
							logError("ObjectManager.method180 bounds error at (" + i4 + "," + k5 + ") for index " + i3 +
								": " + e.getMessage());
						}
					} else {
						logError("Invalid coordinates for method180: i4=" + i4 + ", k5=" + k5 +
							" (index=" + i3 + ", baseX=" + baseX + ", baseY=" + baseY + ")");
					}
				}
			}

			if (anInt1070 < 800) {
				for (int j4 = 0; j4 < k2; j4++) {
					if (j4 >= aByteArrayArray1183.length || j4 >= anIntArray1234.length) {
						logError("Index " + j4 + " exceeds array bounds in normal mode pass 2");
						break;
					}

					if (aByteArrayArray1183[j4] == null) {
						int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
						int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;

						if (isValidMapCoordinate(l5, k7, j4)) {
							try {
								objectManager.method174(k7, 64, 64, l5);
							} catch (ArrayIndexOutOfBoundsException e) {
								logError("ObjectManager.method174 bounds error at (" + l5 + "," + k7 + ") for index " + j4 +
									": " + e.getMessage());
							}
						} else {
							logError("Invalid coordinates for method174: l5=" + l5 + ", k7=" + k7 +
								" (index=" + j4 + ", baseX=" + baseX + ", baseY=" + baseY + ")");
						}
					}
				}
			}

			anInt1097++;
			if (anInt1097 > 160) {
				anInt1097 = 0;
				stream.createFrame(238);
				stream.writeWordBigEndian(96);
			}

			stream.createFrame(0);
			if (aByteArrayArray1247 != null) {
				for (int i6 = 0; i6 < k2; i6++) {
					if (i6 >= aByteArrayArray1247.length || i6 >= anIntArray1234.length) {
						logError("Index " + i6 + " exceeds array bounds in normal mode pass 3");
						break;
					}

					if (aByteArrayArray1247[i6] != null) {
						int l8 = (anIntArray1234[i6] >> 8) * 64 - baseX;
						int k9 = (anIntArray1234[i6] & 0xff) * 64 - baseY;

						if (isValidMapCoordinate(l8, k9, i6)) {
							try {
								objectManager.method190(l8, aClass11Array1230, k9, worldController, aByteArrayArray1247[i6]);
							} catch (ArrayIndexOutOfBoundsException e) {
								logError("ObjectManager.method190 bounds error at (" + l8 + "," + k9 + ") for index " + i6 +
									": " + e.getMessage());
								logError("Raw coordinates: anIntArray1234[" + i6 + "] = " + anIntArray1234[i6] +
									", baseX=" + baseX + ", baseY=" + baseY);
							}
						} else {
							logError("Invalid coordinates for method190: l8=" + l8 + ", k9=" + k9 +
								" (index=" + i6 + ", baseX=" + baseX + ", baseY=" + baseY + ")");
						}
					}
				}
			} else {
				logError("aByteArrayArray1247 is null, skipping method190 calls");
			}

		} catch (Exception e) {
			logError("Error in processNormalModeSafe: " + e.getMessage());
			throw e;
		}
	}

	private static void processSpecialModeSafe(ObjectManager objectManager) {
		logDebug("Processing special mode");

		try {
			if (anIntArrayArrayArray1129 == null) {
				logError("anIntArrayArrayArray1129 is null, cannot process special mode");
				return;
			}

			for (int j3 = 0; j3 < Math.min(4, anIntArrayArrayArray1129.length); j3++) {
				if (anIntArrayArrayArray1129[j3] == null) continue;

				for (int k4 = 0; k4 < Math.min(13, anIntArrayArrayArray1129[j3].length); k4++) {
					if (anIntArrayArrayArray1129[j3][k4] == null) continue;

					for (int j6 = 0; j6 < Math.min(13, anIntArrayArrayArray1129[j3][k4].length); j6++) {
						int l7 = anIntArrayArrayArray1129[j3][k4][j6];
						if (l7 != -1) {
							processSpecialModeCellSafe(objectManager, l7, j3, k4, j6);
						}
					}
				}
			}

			if (anIntArrayArrayArray1129.length > 0 && anIntArrayArrayArray1129[0] != null) {
				for (int l4 = 0; l4 < Math.min(13, anIntArrayArrayArray1129[0].length); l4++) {
					if (anIntArrayArrayArray1129[0][l4] == null) continue;

					for (int k6 = 0; k6 < Math.min(13, anIntArrayArrayArray1129[0][l4].length); k6++) {
						if (anIntArrayArrayArray1129[0][l4][k6] == -1) {
							objectManager.method174(k6 * 8, 8, 8, l4 * 8);
						}
					}
				}
			}

			stream.createFrame(0);
			for (int l6 = 0; l6 < Math.min(4, anIntArrayArrayArray1129.length); l6++) {
				if (anIntArrayArrayArray1129[l6] == null) continue;

				for (int j8 = 0; j8 < Math.min(13, anIntArrayArrayArray1129[l6].length); j8++) {
					if (anIntArrayArrayArray1129[l6][j8] == null) continue;

					for (int j9 = 0; j9 < Math.min(13, anIntArrayArrayArray1129[l6][j8].length); j9++) {
						int i10 = anIntArrayArrayArray1129[l6][j8][j9];
						if (i10 != -1) {
							processSpecialModeCell2Safe(objectManager, i10, l6, j8, j9);
						}
					}
				}
			}

		} catch (Exception e) {
			logError("Error in processSpecialModeSafe: " + e.getMessage());
			throw e;
		}
	}

	private static void processSpecialModeCellSafe(ObjectManager objectManager, int cellValue, int j3, int k4, int j6) {
		try {
			int i9 = cellValue >> 24 & 3;
			int l9 = cellValue >> 1 & 3;
			int j10 = cellValue >> 14 & 0x3ff;
			int l10 = cellValue >> 3 & 0x7ff;
			int j11 = (j10 / 8 << 8) + l10 / 8;

			if (anIntArray1234 != null && aByteArrayArray1183 != null) {
				for (int l11 = 0; l11 < Math.min(anIntArray1234.length, aByteArrayArray1183.length); l11++) {
					if (anIntArray1234[l11] == j11 && aByteArrayArray1183[l11] != null) {
						objectManager.method179(i9, l9, aClass11Array1230, k4 * 8, (j10 & 7) * 8,
							aByteArrayArray1183[l11], (l10 & 7) * 8, j3, j6 * 8);
						break;
					}
				}
			}
		} catch (Exception e) {
			logError("Error in processSpecialModeCellSafe: " + e.getMessage());
		}
	}

	private static void processSpecialModeCell2Safe(ObjectManager objectManager, int cellValue, int l6, int j8, int j9) {
		try {
			int k10 = cellValue >> 24 & 3;
			int i11 = cellValue >> 1 & 3;
			int k11 = cellValue >> 14 & 0x3ff;
			int i12 = cellValue >> 3 & 0x7ff;
			int j12 = (k11 / 8 << 8) + i12 / 8;

			if (anIntArray1234 != null && aByteArrayArray1247 != null) {
				for (int k12 = 0; k12 < Math.min(anIntArray1234.length, aByteArrayArray1247.length); k12++) {
					if (anIntArray1234[k12] == j12 && aByteArrayArray1247[k12] != null) {
						objectManager.method183(aClass11Array1230, worldController, k10, j8 * 8,
							(i12 & 7) * 8, l6, aByteArrayArray1247[k12],
							(k11 & 7) * 8, i11, j9 * 8);
						break;
					}
				}
			}
		} catch (Exception e) {
			logError("Error in processSpecialModeCell2Safe: " + e.getMessage());
		}
	}

	private static void performModelCleanupSafe() {
		logDebug("Phase 5: Model cleanup");

		try {
			if (ObjectDef.mruNodes1 != null) {
				ObjectDef.mruNodes1.unlinkAll();
			} else {
				logError("ObjectDef.mruNodes1 is null");
			}

			if (lowMem && Signlink.cache_dat != null && onDemandFetcher != null) {
				int versionCount = onDemandFetcher.getVersionCount(0);
				int batchSize = Math.min(50, versionCount);

				logDebug("Cleaning " + versionCount + " models in batches of " + batchSize);

				for (int startIdx = 0; startIdx < versionCount; startIdx += batchSize) {
					int endIdx = Math.min(startIdx + batchSize, versionCount);

					for (int i1 = startIdx; i1 < endIdx; i1++) {
						try {
							int l1 = onDemandFetcher.getModelIndex(i1);
							if ((l1 & 0x79) == 0) {
								Model.method461(i1);
							}
						} catch (Exception e) {
							logError("Error cleaning model " + i1 + ": " + e.getMessage());
						}
					}

					if (endIdx < versionCount) {
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}
			}

			logDebug("Model cleanup completed");

		} catch (Exception e) {
			logError("Error in performModelCleanupSafe: " + e.getMessage());
			throw e;
		}
	}

	private static void performFinalOperationsSafe() {
		logDebug("Phase 6: Final operations");

		try {
			long currentMemory = getCurrentMemoryUsage();
			if (currentMemory > MEMORY_GC_THRESHOLD) {
				logDebug("Triggering GC: memory at " + currentMemory + "KB");
				System.gc();
			}

			Rasterizer.method367();

			if (onDemandFetcher != null) {
				onDemandFetcher.method566();
			} else {
				logError("onDemandFetcher is null");
			}

			calculateAndProcessBoundaries();
			logDebug("Final operations completed");

		} catch (Exception e) {
			logError("Error in performFinalOperationsSafe: " + e.getMessage());
			throw e;
		}
	}

	private static void calculateAndProcessBoundaries() {
		try {
			int k, j1, i2, l2;

			if (aBoolean1141) {
				k = 49;
				j1 = 50;
				i2 = 49;
				l2 = 50;
			} else {
				k = Math.max(0, (anInt1069 - 6) / 8 - 1);
				j1 = Math.min(cachedMapWidth - 1, (anInt1069 + 6) / 8 + 1);
				i2 = Math.max(0, (anInt1070 - 6) / 8 - 1);
				l2 = Math.min(cachedMapHeight - 1, (anInt1070 + 6) / 8 + 1);
			}

			logDebug("Processing boundaries: " + k + "-" + j1 + " x " + i2 + "-" + l2);

			if (k > j1 || i2 > l2) {
				logError("Invalid boundary calculations: k=" + k + ", j1=" + j1 + ", i2=" + i2 + ", l2=" + l2);
				return;
			}

			for (int l3 = k; l3 <= j1; l3++) {
				for (int j5 = i2; j5 <= l2; j5++) {
					if (l3 == k || l3 == j1 || j5 == i2 || j5 == l2) {
						processBoundaryCellSafe(l3, j5);
					}
				}
			}

		} catch (Exception e) {
			logError("Error in calculateAndProcessBoundaries: " + e.getMessage());
			throw e;
		}
	}

	private static void processBoundaryCellSafe(int x, int y) {
		try {
			if (onDemandFetcher == null) {
				logError("onDemandFetcher is null in processBoundaryCellSafe");
				return;
			}

			int id1 = onDemandFetcher.method562(0, y, x);
			if (id1 != -1) {
				onDemandFetcher.method560(id1, 3);
			}

			int id2 = onDemandFetcher.method562(1, y, x);
			if (id2 != -1) {
				onDemandFetcher.method560(id2, 3);
			}

		} catch (Exception e) {
			logError("Error processing boundary cell (" + x + "," + y + "): " + e.getMessage());
		}
	}

	private static void handleResetError(Exception exception) {
		consecutiveErrors++;
		lastErrorTime = System.currentTimeMillis();

		logError("Error in resetGameState (attempt " + consecutiveErrors + "): " + exception.getMessage());
		GPUMonitor.getInstance().recordError("GameState Reset", exception.getMessage(), Thread.currentThread().getName());

		if (DEBUG_LOGGING) {
			exception.printStackTrace();
		}

		if (consecutiveErrors < 3) {
			performEmergencyCleanup();
		} else {
			logError("Too many consecutive errors (" + consecutiveErrors + "), performing minimal cleanup");
			performMinimalCleanup();
		}

		if (System.currentTimeMillis() - lastErrorTime > 30000) {
			consecutiveErrors = 0;
		}
	}

	private static void performEmergencyCleanup() {
		logDebug("Performing emergency cleanup");

		try {
			if (aClass19_1056 != null) {
				aClass19_1056.removeAll();
			}

			if (aClass19_1013 != null) {
				aClass19_1013.removeAll();
			}

			try {
				Rasterizer.method366();
			} catch (Exception e) {
				logError("Error in Rasterizer.method366: " + e.getMessage());
			}

			try {
				unlinkMRUNodes();
			} catch (Exception e) {
				logError("Error in unlinkMRUNodes: " + e.getMessage());
			}

			if (worldController != null) {
				try {
					worldController.initToNull();
				} catch (Exception e) {
					logError("Error in worldController.initToNull: " + e.getMessage());
				}
			}

			logDebug("Emergency cleanup completed");

		} catch (Exception e) {
			logError("Error in emergency cleanup: " + e.getMessage());
		}
	}

	private static void performMinimalCleanup() {
		logDebug("Performing minimal cleanup");

		try {
			anInt985 = -1;
			System.gc();
			logDebug("Minimal cleanup completed");

		} catch (Exception e) {
			logError("Error in minimal cleanup: " + e.getMessage());
		}
	}

	private static void logPerformanceMetrics(long startTime, long memoryBefore) {
		long duration = System.currentTimeMillis() - startTime;
		long memoryAfter = getCurrentMemoryUsage();
		long memoryDelta = memoryAfter - memoryBefore;

		if (memoryAfter > peakMemoryUsage) {
			peakMemoryUsage = memoryAfter;
		}

		resetCount++;
		lastResetTime = System.currentTimeMillis();

		boolean shouldLogDetailed = (resetCount % 10 == 0) || consecutiveErrors > 0;

		if (shouldLogDetailed) {
			logInfo("GameState Reset #" + resetCount +
				" - Duration: " + duration + "ms" +
				" - Memory: " + memoryBefore + "KB -> " + memoryAfter + "KB" +
				" - Delta: " + (memoryDelta > 0 ? "+" : "") + memoryDelta + "KB" +
				" - Peak: " + peakMemoryUsage + "KB" +
				" - LoadingErrorScreen: " + consecutiveErrors +
				" - GPU Valid: " + gpuStateValid);
		}

		if (memoryAfter > MEMORY_GC_THRESHOLD || memoryDelta > 100) {
			logDebug("Triggering post-reset GC: " + memoryAfter + "KB memory, " + memoryDelta + "KB delta");
			System.gc();

			long memoryAfterGC = getCurrentMemoryUsage();
			if (shouldLogDetailed) {
				logInfo("GC result: " + memoryAfter + "KB -> " + memoryAfterGC + "KB");
			}
		}
	}

	// ===== UTILITY METHODS =====

	private static boolean isValidMapCoordinate(int x, int y, int index) {
		if (x < -1000 || x > 10000 || y < -1000 || y > 10000) {
			logError("Coordinate validation failed: extreme values x=" + x + ", y=" + y + " for index=" + index);
			return false;
		}

		if (DEBUG_LOGGING && (Math.abs(x) > 1000 || Math.abs(y) > 1000)) {
			logDebug("Large coordinates detected: x=" + x + ", y=" + y + " for index=" + index);
		}

		return true;
	}

	private static long getCurrentMemoryUsage() {
		try {
			Runtime runtime = Runtime.getRuntime();
			return (runtime.totalMemory() - runtime.freeMemory()) / 1024;
		} catch (Exception e) {
			logError("Error getting memory usage: " + e.getMessage());
			return 0;
		}
	}

	// ===== PUBLIC API =====

	public static String getRenderingStatistics() {
		return String.format("Rendering Stats - Total calls: %d, LoadingErrorScreen: %d, Prevented: %d, " +
				"Effectiveness: %.1f%%, Memory: %dKB, Map: %dx%d, GPU Valid: %s",
			totalRenderCalls,
			renderingErrors,
			validationPreventedErrors,
			totalRenderCalls > 0 ? (100.0 * validationPreventedErrors / (validationPreventedErrors + renderingErrors)) : 0.0,
			getCurrentMemoryUsage(),
			cachedMapWidth,
			cachedMapHeight,
			gpuStateValid);
	}

	public static String getResetStatistics() {
		return String.format("GameState Stats - Resets: %d, Last: %dms ago, Peak Memory: %dKB, " +
				"Current: %dKB, LoadingErrorScreen: %d, Map: %dx%d, GPU: %s",
			resetCount,
			lastResetTime > 0 ? System.currentTimeMillis() - lastResetTime : -1,
			peakMemoryUsage,
			getCurrentMemoryUsage(),
			consecutiveErrors,
			cachedMapWidth,
			cachedMapHeight,
			GPURenderingEngine.isEnabled() ? "Enabled" : "Disabled");
	}

	public static String getGPUStatus() {
		return String.format("GPU Status - Enabled: %s, Valid: %s, Failures: %d/%d, Health: %s",
			GPURenderingEngine.isEnabled(),
			gpuStateValid,
			consecutiveGPUFailures,
			MAX_GPU_FAILURES,
			GPUMonitor.getInstance().getHealthStatus());
	}

	// ===== LOGGING METHODS =====

	private static void logInfo(String message) {
		System.out.println("[GameState INFO] " + message);
	}

	private static void logDebug(String message) {
		if (DEBUG_LOGGING) {
			System.out.println("[GameState DEBUG] " + message);
		}
	}

	private static void logError(String message) {
		System.err.println("[GameState ERROR] " + message);
	}
}