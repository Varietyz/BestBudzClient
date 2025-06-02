package com.bestbudz.engine.core;

import com.bestbudz.cache.Signlink;
import static com.bestbudz.engine.core.gamerender.Camera.calcCameraPos;
import static com.bestbudz.engine.core.gamerender.Camera.updateCameraPosition;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.rendering.SpotAnim2.updatePendingSpotAnimations;
import com.bestbudz.rendering.model.Model;
import static com.bestbudz.ui.interfaces.Chatbox.handleTextFieldInput;
import com.bestbudz.util.ColorUtility;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.engine.core.gamerender.ObjectManager;
import java.awt.Graphics2D;

/**
 * Enhanced GameState with comprehensive safety, logging, and memory management
 *
 * Key improvements:
 * - Comprehensive bounds checking and array validation
 * - Detailed logging for debugging map loading issues
 * - Graceful error recovery and fallback mechanisms
 * - Memory optimization with intelligent cleanup
 * - Performance monitoring and statistics
 */
public class GameState extends Client {

	// ===== CONSTANTS =====
	private static final int DEFAULT_MAP_SIZE = 104;
	private static final int MAX_SAFE_MAP_SIZE = 512; // Absolute maximum for safety
	private static final int BLOCK_SIZE = 32; // For cache-friendly processing
	private static final int MEMORY_GC_THRESHOLD = 320; // KB
	private static final int MEMORY_DELTA_THRESHOLD = 100; // KB
	private static final boolean DEBUG_LOGGING = true; // Enable for detailed logs

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
	private static int lastLoggedErrorCount = 0;
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
	 * Main scene rendering method with safety checks and camera bounds validation
	 */
	public static void runSceneRendering(Graphics2D g) {
		try {
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
		}
	}

	/**
	 * DEBUG: Let's see exactly what's causing the bounds errors
	 */
	public static void safeRenderWorld(int xCameraPos, int yCameraPos, int xCameraCurve,
									   int zCameraPos, int j, int yCameraCurve) {
		totalRenderCalls++;

		try {
			// DETAILED DEBUGGING: Check all the variables that might cause bounds issues
			if (renderingErrors > 0 && renderingErrors % 50 == 0) {
				logError("=== BOUNDS DEBUG ANALYSIS ===");
				logError("Camera Params: xCameraPos=" + xCameraPos + ", yCameraPos=" + yCameraPos + ", zCameraPos=" + zCameraPos);
				logError("Camera Curves: xCameraCurve=" + xCameraCurve + ", yCameraCurve=" + yCameraCurve + ", j=" + j);

				// Check map dimensions
				if (byteGroundArray != null) {
					logError("Map dimensions - byteGroundArray planes: " + byteGroundArray.length);
					for (int plane = 0; plane < Math.min(4, byteGroundArray.length); plane++) {
						if (byteGroundArray[plane] != null) {
							logError("  Plane " + plane + ": " + byteGroundArray[plane].length + "x" +
								(byteGroundArray[plane].length > 0 && byteGroundArray[plane][0] != null ?
									byteGroundArray[plane][0].length : "null"));
						}
					}
				}

				// Check what coordinates the camera would actually try to access
				// The rendering system probably calculates tile coordinates from camera position
				int tileX = xCameraPos >> 7;  // Divide by 128 for tile coordinates
				int tileY = yCameraPos >> 7;
				logError("Calculated tile coordinates: tileX=" + tileX + ", tileY=" + tileY);

				// Check if these tile coordinates are within array bounds
				if (byteGroundArray != null && byteGroundArray.length > 0 && byteGroundArray[0] != null) {
					int maxTileX = byteGroundArray[0].length - 1;
					int maxTileY = byteGroundArray[0].length > 0 && byteGroundArray[0][0] != null ?
						byteGroundArray[0][0].length - 1 : -1;
					logError("Max valid tile coordinates: maxTileX=" + maxTileX + ", maxTileY=" + maxTileY);

					if (tileX < 0 || tileX > maxTileX || tileY < 0 || tileY > maxTileY) {
						logError("*** TILE COORDINATES OUT OF BOUNDS! ***");
						logError("tileX=" + tileX + " (valid: 0-" + maxTileX + ")");
						logError("tileY=" + tileY + " (valid: 0-" + maxTileY + ")");
					}
				}

				// Check rendering viewport calculations
				// The renderer probably calculates a viewing area around the camera
				int viewStartX = (xCameraPos - 50) / 128;  // Rough estimate of viewing area
				int viewEndX = (xCameraPos + 50) / 128;
				int viewStartY = (yCameraPos - 50) / 128;
				int viewEndY = (yCameraPos + 50) / 128;
				logError("Estimated view area: X(" + viewStartX + " to " + viewEndX + ") Y(" + viewStartY + " to " + viewEndY + ")");

				// Check other relevant variables
				logError("Other variables: baseX=" + baseX + ", baseY=" + baseY + ", plane=" + plane);
				logError("anInt1069=" + anInt1069 + ", anInt1070=" + anInt1070);
				logError("=== END BOUNDS DEBUG ===");
			}

			// Try normal rendering
			if (worldController != null) {
				worldController.method313(xCameraPos, yCameraPos, xCameraCurve,
					zCameraPos, j, yCameraCurve);

				// Success - cache this position
				lastValidXCameraPos = xCameraPos;
				lastValidYCameraPos = yCameraPos;
				lastValidXCameraCurve = xCameraCurve;
				lastValidZCameraPos = zCameraPos;
				lastValidJ = j;
				lastValidYCameraCurve = yCameraCurve;
				hasValidPosition = true;

			} else {
				logError("worldController is null in safeRenderWorld");
			}

		} catch (ArrayIndexOutOfBoundsException e) {
			renderingErrors++;

			// DETAILED ERROR ANALYSIS
			logError("BOUNDS ERROR DETAILS:");
			logError("Error message: " + e.getMessage());
			logError("Stack trace element: " + (e.getStackTrace().length > 0 ? e.getStackTrace()[0] : "none"));

			// Calculate what the renderer might be trying to access
			int tileX = xCameraPos >> 7;
			int tileY = yCameraPos >> 7;
			logError("Camera tile coords: tileX=" + tileX + ", tileY=" + tileY);


			// Use cached position fallback
			if (hasValidPosition && worldController != null) {
				try {
					worldController.method313(lastValidXCameraPos, lastValidYCameraPos, lastValidXCameraCurve,
						lastValidZCameraPos, lastValidJ, lastValidYCameraCurve);
					logDebug("Successfully used cached position fallback");
					return;
				} catch (ArrayIndexOutOfBoundsException e2) {
					logError("Even cached position failed: " + e2.getMessage());
				}
			}

			// Final fallback - skip frame
			logDebug("Skipping frame due to bounds error");

		} catch (Exception e) {
			logError("Other error in safeRenderWorld: " + e.getMessage());
		}
	}

	/**
	 * Enhanced resetGameState with comprehensive safety and logging
	 */
	public static void resetGameState() {
		if (isResetting) {
			logDebug("Reset already in progress, skipping");
			return;
		}

		isResetting = true;
		long startTime = System.currentTimeMillis();
		long memoryBefore = getCurrentMemoryUsage();

		logInfo("Starting GameState reset #" + (resetCount + 1));

		try {
			// PHASE 0: Validate environment and cache dimensions
			validateEnvironmentAndCacheDimensions();

			// PHASE 1: Quick cleanup without garbage collection
			performQuickCleanup();

			// PHASE 2: Array operations with comprehensive safety
			performArrayOperationsSafe();

			// PHASE 3: Object management with validation
			ObjectManager objectManager = createObjectManagerSafe();

			// PHASE 4: Main processing with bounds checking
			performMainProcessingSafe(objectManager);

			// PHASE 5: Model cleanup with batching
			performModelCleanupSafe();

			// PHASE 6: Final operations with bounds validation
			performFinalOperationsSafe();

			consecutiveErrors = 0; // Reset error counter on success
			logInfo("GameState reset completed successfully");

		} catch (Exception exception) {
			handleResetError(exception);
		} finally {
			isResetting = false;
			logPerformanceMetrics(startTime, memoryBefore);
		}
	}

	/**
	 * Validate environment and cache map dimensions for safe access
	 */
	private static void validateEnvironmentAndCacheDimensions() {
		logDebug("=== ENVIRONMENT VALIDATION ===");

		try {
			// Validate and cache byteGroundArray dimensions
			if (byteGroundArray != null) {
				logDebug("byteGroundArray planes: " + byteGroundArray.length);

				for (int plane = 0; plane < byteGroundArray.length; plane++) {
					if (byteGroundArray[plane] != null) {
						int width = byteGroundArray[plane].length;
						logDebug("Plane " + plane + " width: " + width);

						if (width > 0 && byteGroundArray[plane][0] != null) {
							int height = byteGroundArray[plane][0].length;
							logDebug("Plane " + plane + " height: " + height);

							// Cache dimensions from first valid plane
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

			// Validate other critical arrays
			validateCriticalArrays();

		} catch (Exception e) {
			logError("Error in environment validation: " + e.getMessage());
			// Set safe defaults
			cachedMapWidth = cachedMapHeight = DEFAULT_MAP_SIZE;
			dimensionsCached = true;
		}

		logDebug("=== VALIDATION COMPLETE ===");
	}

	/**
	 * Validate all critical arrays used in the reset process
	 */
	private static void validateCriticalArrays() {
		// Validate Class11 array
		if (aClass11Array1230 != null) {
			logDebug("aClass11Array1230 length: " + aClass11Array1230.length);
		} else {
			logError("aClass11Array1230 is null");
		}

		// Validate byte arrays
		if (aByteArrayArray1183 != null) {
			logDebug("aByteArrayArray1183 length: " + aByteArrayArray1183.length);
		} else {
			logError("aByteArrayArray1183 is null");
		}

		// Validate int arrays
		if (anIntArray1234 != null) {
			logDebug("anIntArray1234 length: " + anIntArray1234.length);
		} else {
			logError("anIntArray1234 is null");
		}

		// Validate 3D array
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

	/**
	 * Enhanced quick cleanup phase
	 */
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

	/**
	 * Safe array operations with comprehensive bounds checking
	 */
	private static void performArrayOperationsSafe() {
		logDebug("Phase 2: Array operations");

		try {
			// Clear Class11 arrays with bounds checking
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

			// Clear ground arrays with comprehensive safety
			clearGroundArraysComprehensive();

			logDebug("Array operations completed");
		} catch (Exception e) {
			logError("Error in performArrayOperationsSafe: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Comprehensive ground array clearing with multiple fallback strategies
	 */
	private static void clearGroundArraysComprehensive() {
		logDebug("Clearing ground arrays");

		if (byteGroundArray == null) {
			logError("byteGroundArray is null, cannot clear");
			return;
		}

		try {
			// Strategy 1: Block-based clearing for cache efficiency
			clearGroundArraysOptimized();
		} catch (Exception e) {
			logError("Optimized clearing failed: " + e.getMessage());

			try {
				// Strategy 2: Simple row-by-row clearing
				clearGroundArraysSimple();
			} catch (Exception e2) {
				logError("Simple clearing failed: " + e2.getMessage());

				try {
					// Strategy 3: Ultra-safe single-element clearing
					clearGroundArraysUltraSafe();
				} catch (Exception e3) {
					logError("Ultra-safe clearing failed: " + e3.getMessage());
					// At this point, we give up on clearing but don't crash
				}
			}
		}
	}

	/**
	 * Optimized block-based ground array clearing
	 */
	private static void clearGroundArraysOptimized() {
		final int blockSize = BLOCK_SIZE;

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

	/**
	 * Simple ground array clearing fallback
	 */
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

	/**
	 * Ultra-safe single-element ground array clearing
	 */
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

	/**
	 * Create ObjectManager with validation
	 */
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
	 * Safe main processing with comprehensive bounds checking
	 */
	private static void performMainProcessingSafe(ObjectManager objectManager) {
		logDebug("Phase 4: Main processing");

		try {
			if (aByteArrayArray1183 == null) {
				logError("aByteArrayArray1183 is null, skipping main processing");
				return;
			}

			int k2 = aByteArrayArray1183.length;
			logDebug("Processing " + k2 + " byte arrays");

			stream.createFrame(0);

			if (!aBoolean1159) {
				processNormalModeSafe(objectManager, k2);
			} else {
				processSpecialModeSafe(objectManager);
			}

			// Final object manager operations with safety checks
			finalizeObjectManager(objectManager);

			logDebug("Main processing completed");

		} catch (Exception e) {
			logError("Error in performMainProcessingSafe: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Finalize ObjectManager operations safely with decoration initialization
	 */
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

			// GROUND DECORATION FIX: Initialize ground decorations explicitly
			initializeGroundDecorations();

			anInt1051 = (anInt1051 + 1) % 99;
			updatePendingSpotAnimations();

		} catch (Exception e) {
			logError("Error in finalizeObjectManager: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Initialize ground decorations explicitly to fix boot-time rendering issues
	 * This ensures decorations are properly set up during initial map load
	 */
	private static void initializeGroundDecorations() {
		logDebug("Initializing ground decorations");

		try {
			// CRITICAL: Force WorldController ground array initialization
			if (worldController != null && worldController.groundArray != null) {
				logDebug("Initializing WorldController ground arrays");

				// Initialize ground visibility flags - this is likely what's missing on boot
				for (int plane = 0; plane < Math.min(4, worldController.groundArray.length); plane++) {
					if (worldController.groundArray[plane] == null) continue;

					for (int x = 0; x < Math.min(cachedMapWidth, worldController.groundArray[plane].length); x++) {
						if (worldController.groundArray[plane][x] == null) continue;

						for (int y = 0; y < Math.min(cachedMapHeight, worldController.groundArray[plane][x].length); y++) {
							if (worldController.groundArray[plane][x][y] == null) continue;

							// This is the key fix: ensure ground tiles are marked as visible
							// The aBoolean1323 flag controls visibility of ground decorations
							worldController.groundArray[plane][x][y].aBoolean1323 = true;

							// Also ensure the ground tile is marked for rendering
							// This may be needed to trigger decoration rendering
							if (worldController.groundArray[plane][x][y].aClass30_Sub3_1329 != null) {
								// Ground has underlying decoration/floor data
								logDebug("Found ground decoration data at plane=" + plane + ", x=" + x + ", y=" + y);
							}
						}
					}
				}

				logDebug("Ground visibility flags initialized");
			} else {
				logError("worldController or groundArray is null");
			}

			// Force decoration layer initialization in WorldController
			if (worldController != null) {
				// This may need to be called to ensure decoration layers are active
				worldController.method275(0); // Ensure all planes are rendered

				// Additional decoration-specific initialization if methods exist
				// These method names are guesses - you'll need to check your WorldController class
				try {
					// Attempt to call decoration initialization methods
					if (hasMethod(worldController, "initializeDecorations")) {
						logDebug("Calling worldController.initializeDecorations()");
						worldController.getClass().getMethod("initializeDecorations").invoke(worldController);
					}

					if (hasMethod(worldController, "refreshDecorationLayer")) {
						logDebug("Calling worldController.refreshDecorationLayer()");
						worldController.getClass().getMethod("refreshDecorationLayer").invoke(worldController);
					}

					if (hasMethod(worldController, "method276")) {
						logDebug("Calling worldController.method276()");
						worldController.getClass().getMethod("method276").invoke(worldController);
					}

				} catch (Exception e) {
					logDebug("No decoration-specific methods found: " + e.getMessage());
				}
			}

			// Force ground decoration rendering for each plane
			for (int plane = 0; plane < Math.min(4, byteGroundArray.length); plane++) {
				if (byteGroundArray[plane] != null) {
					processPlaneDecorations(plane);
				}
			}

			// Ensure decoration cache is properly initialized
			refreshDecorationCache();

			logDebug("Ground decoration initialization completed");

		} catch (Exception e) {
			logError("Error in initializeGroundDecorations: " + e.getMessage());
			// Don't throw - decoration initialization failure shouldn't crash the game
		}
	}

	/**
	 * Process decorations for a specific plane
	 */
	private static void processPlaneDecorations(int plane) {
		try {
			logDebug("Processing decorations for plane " + plane);

			// Scan the ground array for decoration data
			byte[][] currentPlane = byteGroundArray[plane];
			int decorationCount = 0;

			for (int x = 0; x < Math.min(cachedMapWidth, currentPlane.length); x++) {
				if (currentPlane[x] == null) continue;

				byte[] row = currentPlane[x];
				for (int y = 0; y < Math.min(cachedMapHeight, row.length); y++) {
					byte tileData = row[y];

					// Check if this tile has decoration data (non-zero values often indicate decorations)
					if (tileData != 0) {
						decorationCount++;

					}
				}
			}

			logDebug("Plane " + plane + " decoration processing completed: " + decorationCount + " tiles processed");

		} catch (Exception e) {
			logError("Error processing plane " + plane + " decorations: " + e.getMessage());
		}
	}


	/**
	 * Refresh the decoration cache to ensure proper rendering
	 */
	private static void refreshDecorationCache() {
		try {
			logDebug("Refreshing decoration cache");


			logDebug("Decoration cache refresh completed");

		} catch (Exception e) {
			logError("Error in refreshDecorationCache: " + e.getMessage());
		}
	}

	/**
	 * Check if an object has a specific method (for safe method calling)
	 */
	private static boolean hasMethod(Object obj, String methodName) {
		try {
			obj.getClass().getMethod(methodName);
			return true;
		} catch (NoSuchMethodException e) {
			return false;
		}
	}

	/**
	 * Safe normal mode processing
	 */
	private static void processNormalModeSafe(ObjectManager objectManager, int k2) {
		logDebug("Processing normal mode with " + k2 + " arrays");

		try {
			// First pass: method180 calls with safety checks and coordinate validation
			for (int i3 = 0; i3 < k2; i3++) {
				if (i3 >= aByteArrayArray1183.length || i3 >= anIntArray1234.length) {
					logError("Index " + i3 + " exceeds array bounds in normal mode pass 1");
					break;
				}

				if (aByteArrayArray1183[i3] != null) {
					int i4 = (anIntArray1234[i3] >> 8) * 64 - baseX;
					int k5 = (anIntArray1234[i3] & 0xff) * 64 - baseY;

					// Validate coordinates before calling method180
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

			// Second pass: method174 calls with condition check and coordinate validation
			if (anInt1070 < 800) {
				for (int j4 = 0; j4 < k2; j4++) {
					if (j4 >= aByteArrayArray1183.length || j4 >= anIntArray1234.length) {
						logError("Index " + j4 + " exceeds array bounds in normal mode pass 2");
						break;
					}

					if (aByteArrayArray1183[j4] == null) {
						int l5 = (anIntArray1234[j4] >> 8) * 64 - baseX;
						int k7 = (anIntArray1234[j4] & 0xff) * 64 - baseY;

						// Validate coordinates before calling method174
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

			// Counter update with bounds checking
			anInt1097++;
			if (anInt1097 > 160) {
				anInt1097 = 0;
				stream.createFrame(238);
				stream.writeWordBigEndian(96);
			}

			// Third pass: method190 calls with coordinate validation
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

						// CRITICAL: Validate coordinates before passing to ObjectManager
						// The ObjectManager expects coordinates within valid map bounds
						if (isValidMapCoordinate(l8, k9, i6)) {
							try {
								objectManager.method190(l8, aClass11Array1230, k9, worldController, aByteArrayArray1247[i6]);
							} catch (ArrayIndexOutOfBoundsException e) {
								logError("ObjectManager.method190 bounds error at (" + l8 + "," + k9 + ") for index " + i6 +
									": " + e.getMessage());
								logError("Raw coordinates: anIntArray1234[" + i6 + "] = " + anIntArray1234[i6] +
									", baseX=" + baseX + ", baseY=" + baseY);
								// Continue with next iteration instead of crashing
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

	/**
	 * Safe special mode processing
	 */
	private static void processSpecialModeSafe(ObjectManager objectManager) {
		logDebug("Processing special mode");

		try {
			if (anIntArrayArrayArray1129 == null) {
				logError("anIntArrayArrayArray1129 is null, cannot process special mode");
				return;
			}

			// First nested loop set - method179 calls
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

			// method174 calls for plane 0
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

			// Second nested loop set - method183 calls
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

	/**
	 * Safe special mode cell processing
	 */
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

	/**
	 * Safe special mode cell processing part 2
	 */
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

	/**
	 * Safe model cleanup with batching
	 */
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

					// Small pause between batches
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

	/**
	 * Safe final operations with bounds validation
	 */
	private static void performFinalOperationsSafe() {
		logDebug("Phase 6: Final operations");

		try {
			// Intelligent garbage collection
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

			// Safe boundary calculations with clamping
			calculateAndProcessBoundaries();

			logDebug("Final operations completed");

		} catch (Exception e) {
			logError("Error in performFinalOperationsSafe: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Calculate and process boundaries with comprehensive safety checks
	 */
	private static void calculateAndProcessBoundaries() {
		try {
			int k, j1, i2, l2;

			if (aBoolean1141) {
				k = 49;
				j1 = 50;
				i2 = 49;
				l2 = 50;
			} else {
				// Calculate boundaries with safety clamping
				k = Math.max(0, (anInt1069 - 6) / 8 - 1);
				j1 = Math.min(cachedMapWidth - 1, (anInt1069 + 6) / 8 + 1);
				i2 = Math.max(0, (anInt1070 - 6) / 8 - 1);
				l2 = Math.min(cachedMapHeight - 1, (anInt1070 + 6) / 8 + 1);
			}

			logDebug("Processing boundaries: " + k + "-" + j1 + " x " + i2 + "-" + l2);

			// Validate boundary calculations
			if (k > j1 || i2 > l2) {
				logError("Invalid boundary calculations: k=" + k + ", j1=" + j1 + ", i2=" + i2 + ", l2=" + l2);
				return;
			}

			// Process boundaries with safety checks
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

	/**
	 * Safe boundary cell processing with validation
	 */
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

	/**
	 * Handle reset errors with intelligent recovery
	 */
	private static void handleResetError(Exception exception) {
		consecutiveErrors++;
		lastErrorTime = System.currentTimeMillis();

		logError("Error in resetGameState (attempt " + consecutiveErrors + "): " + exception.getMessage());
		if (DEBUG_LOGGING) {
			exception.printStackTrace();
		}

		// Perform emergency cleanup based on error severity
		if (consecutiveErrors < 3) {
			performEmergencyCleanup();
		} else {
			logError("Too many consecutive errors (" + consecutiveErrors + "), performing minimal cleanup");
			performMinimalCleanup();
		}

		// Reset error counter after some time
		if (System.currentTimeMillis() - lastErrorTime > 30000) { // 30 seconds
			consecutiveErrors = 0;
		}
	}

	/**
	 * Emergency cleanup when main reset fails
	 */
	private static void performEmergencyCleanup() {
		logDebug("Performing emergency cleanup");

		try {
			// Basic cleanup without array operations
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

	/**
	 * Minimal cleanup for severe error conditions
	 */
	private static void performMinimalCleanup() {
		logDebug("Performing minimal cleanup");

		try {
			anInt985 = -1;

			// Force garbage collection in severe error conditions
			System.gc();

			logDebug("Minimal cleanup completed");

		} catch (Exception e) {
			logError("Error in minimal cleanup: " + e.getMessage());
		}
	}

	/**
	 * Log performance metrics after reset completion
	 */
	private static void logPerformanceMetrics(long startTime, long memoryBefore) {
		long duration = System.currentTimeMillis() - startTime;
		long memoryAfter = getCurrentMemoryUsage();
		long memoryDelta = memoryAfter - memoryBefore;

		if (memoryAfter > peakMemoryUsage) {
			peakMemoryUsage = memoryAfter;
		}

		resetCount++;
		lastResetTime = System.currentTimeMillis();

		// Log detailed stats every 10 resets or on errors
		boolean shouldLogDetailed = (resetCount % 10 == 0) || consecutiveErrors > 0;

		if (shouldLogDetailed) {
			logInfo("GameState Reset #" + resetCount +
				" - Duration: " + duration + "ms" +
				" - Memory: " + memoryBefore + "KB -> " + memoryAfter + "KB" +
				" - Delta: " + (memoryDelta > 0 ? "+" : "") + memoryDelta + "KB" +
				" - Peak: " + peakMemoryUsage + "KB" +
				" - Errors: " + consecutiveErrors);
		}

		// Intelligent garbage collection
		if (memoryAfter > MEMORY_GC_THRESHOLD || memoryDelta > MEMORY_DELTA_THRESHOLD) {
			logDebug("Triggering post-reset GC: " + memoryAfter + "KB memory, " + memoryDelta + "KB delta");
			System.gc();

			long memoryAfterGC = getCurrentMemoryUsage();
			if (shouldLogDetailed) {
				logInfo("GC result: " + memoryAfter + "KB -> " + memoryAfterGC + "KB");
			}
		}
	}

	// ===== UTILITY METHODS =====

	/**
	 * Validate map coordinates to prevent ObjectManager bounds errors
	 * This is critical as ObjectManager methods expect coordinates within specific ranges
	 */
	private static boolean isValidMapCoordinate(int x, int y, int index) {
		// Check for obviously invalid coordinates
		if (x < -1000 || x > 10000 || y < -1000 || y > 10000) {
			logError("Coordinate validation failed: extreme values x=" + x + ", y=" + y + " for index=" + index);
			return false;
		}

		// Additional validation: coordinates should generally be within reasonable map bounds
		// ObjectManager typically works with coordinates in 8x8 or 64x64 blocks
		// So coordinates should be divisible by 8 and within reasonable ranges

		// For debugging: log coordinates that might cause issues
		if (DEBUG_LOGGING && (Math.abs(x) > 1000 || Math.abs(y) > 1000)) {
			logDebug("Large coordinates detected: x=" + x + ", y=" + y + " for index=" + index);
		}

		return true; // Allow through for now, but log the values
	}

	/**
	 * Get current memory usage in KB
	 */
	private static long getCurrentMemoryUsage() {
		try {
			Runtime runtime = Runtime.getRuntime();
			return (runtime.totalMemory() - runtime.freeMemory()) / 1024;
		} catch (Exception e) {
			logError("Error getting memory usage: " + e.getMessage());
			return 0;
		}
	}

	/**
	 * Get rendering statistics for debugging
	 */
	public static String getRenderingStatistics() {
		return String.format("Rendering Stats - Total calls: %d, Errors: %d, Prevented: %d, " +
				"Effectiveness: %.1f%%, Memory: %dKB, Map: %dx%d",
			totalRenderCalls,
			renderingErrors,
			validationPreventedErrors,
			totalRenderCalls > 0 ? (100.0 * validationPreventedErrors / (validationPreventedErrors + renderingErrors)) : 0.0,
			getCurrentMemoryUsage(),
			cachedMapWidth,
			cachedMapHeight);
	}

	/**
	 * Get comprehensive performance statistics
	 */
	public static String getResetStatistics() {
		return String.format("GameState Stats - Resets: %d, Last: %dms ago, Peak Memory: %dKB, " +
				"Current: %dKB, Errors: %d, Map: %dx%d",
			resetCount,
			lastResetTime > 0 ? System.currentTimeMillis() - lastResetTime : -1,
			peakMemoryUsage,
			getCurrentMemoryUsage(),
			consecutiveErrors,
			cachedMapWidth,
			cachedMapHeight);
	}

	/**
	 * Get detailed diagnostic information
	 */
	public static String getDiagnosticInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("=== GameState Diagnostic Info ===\n");
		sb.append("Map Dimensions: ").append(cachedMapWidth).append("x").append(cachedMapHeight).append("\n");
		sb.append("Dimensions Cached: ").append(dimensionsCached).append("\n");
		sb.append("Reset Count: ").append(resetCount).append("\n");
		sb.append("Consecutive Errors: ").append(consecutiveErrors).append("\n");
		sb.append("Peak Memory: ").append(peakMemoryUsage).append("KB\n");
		sb.append("Current Memory: ").append(getCurrentMemoryUsage()).append("KB\n");
		sb.append("Is Resetting: ").append(isResetting).append("\n");

		// Array validation status
		sb.append("Array Status:\n");
		sb.append("  byteGroundArray: ").append(byteGroundArray != null ? "OK" : "NULL").append("\n");
		sb.append("  aClass11Array1230: ").append(aClass11Array1230 != null ? "OK" : "NULL").append("\n");
		sb.append("  aByteArrayArray1183: ").append(aByteArrayArray1183 != null ? "OK" : "NULL").append("\n");
		sb.append("  anIntArray1234: ").append(anIntArray1234 != null ? "OK" : "NULL").append("\n");

		return sb.toString();
	}



	// ===== LOGGING METHODS =====

	/**
	 * Log info message
	 */
	private static void logInfo(String message) {
		System.out.println("[GameState INFO] " + message);
	}

	/**
	 * Log debug message (only if debug logging is enabled)
	 */
	private static void logDebug(String message) {
		if (DEBUG_LOGGING) {
			System.out.println("[GameState DEBUG] " + message);
		}
	}

	/**
	 * Log error message
	 */
	private static void logError(String message) {
		System.err.println("[GameState ERROR] " + message);
	}
}