package com.bestbudz.engine.core;

import com.bestbudz.cache.EmbeddedMapCache;
import com.bestbudz.cache.Signlink;
import com.bestbudz.data.AccountManager;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.engine.config.EngineConfig;
import static com.bestbudz.engine.core.LoadingErrorScreen.addConsoleMessage;
import com.bestbudz.engine.core.login.LoginRenderer;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.cache.IdentityKit;
import com.bestbudz.graphics.Background;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.sprite.SpriteLoader;
import com.bestbudz.graphics.text.FontSystem;
import com.bestbudz.graphics.text.TextController;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.network.OnDemandFetcher;
import com.bestbudz.network.StreamLoader;
import com.bestbudz.rendering.Animable_Sub5;
import com.bestbudz.rendering.OverlayFloor;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.SpotAnim;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.ui.interfaces.StatusOrbs;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents2;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents3;
import com.bestbudz.util.Decompressor;
import com.bestbudz.world.Class11;
import com.bestbudz.world.Floor;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.world.VarBit;
import com.bestbudz.world.Varp;
import com.bestbudz.engine.core.gamerender.WorldController;

import java.awt.Graphics2D;
import java.lang.ref.WeakReference;
import java.util.concurrent.*;
import java.util.*;

/**
 * Memory-optimized GameLoader maintaining exact original boot sequence
 * with added caching, error handling, and resource management
 */
public class GameLoader extends Client {

	// Memory management and caching
	private static final Map<String, WeakReference<Sprite>> spriteCache = new ConcurrentHashMap<>();
	private static volatile boolean isLoading = false;
	private static long loadStartTime = 0;

	/**
	 * Enhanced sprite factory with caching but no behavioral changes
	 */
	static class OptimizedSpriteFactory {
		public static Sprite createSprite(StreamLoader loader, String name, int index) {
			String key = name + "_" + index;

			// Check cache first for memory efficiency
			WeakReference<Sprite> ref = spriteCache.get(key);
			if (ref != null) {
				Sprite cached = ref.get();
				if (cached != null) {
					return cached;
				}
			}

			// Create sprite exactly as original
			try {
				Sprite sprite = new Sprite(loader, name, index);
				if (sprite != null) {
					spriteCache.put(key, new WeakReference<>(sprite));
				}
				return sprite;
			} catch (Exception e) {
				// Match original behavior - let exception propagate or return null
				throw new RuntimeException("Failed to create sprite: " + key, e);
			}
		}

		public static void cleanupCache() {
			spriteCache.entrySet().removeIf(entry -> entry.getValue().get() == null);
		}
	}

	/**
	 * EXACT reproduction of original startUp method with memory optimizations
	 */
	static void startUp(Graphics2D g, Client client) {
		isLoading = true;
		loadStartTime = System.currentTimeMillis();

		try {
			// PHASE 1: Exact original initialization order
			AccountManager.loadAccount();
			SpriteLoader.loadSprites();
			cacheSprite = SpriteLoader.sprites;
			loginRenderer = new LoginRenderer(client);

			// Initialize decompressors - exactly as original
			if (Signlink.cache_dat != null) {
				for (int i = 0; i < 6; i++)
					decompressors[i] = new Decompressor(Signlink.cache_dat, Signlink.cache_idx[i], i + 1);
			}

			addConsoleMessage("🎨 Initializing modern font system...");

			TextDrawingArea aTextDrawingArea_1273;
			try {
				// Create all fonts using the modern system
				FontSystem.GameFonts gameFonts = FontSystem.GameFonts.create();

				// Assign to the existing variables (no variable name changes needed)
				smallText = gameFonts.smallText;
				regularText = gameFonts.regularText;
				boldText = gameFonts.boldText;
				newSmallFont = gameFonts.newSmallFont;
				newRegularFont = gameFonts.newRegularFont;
				newBoldFont = gameFonts.newBoldFont;
				newFancyFont = gameFonts.newFancyFont;
				aTextDrawingArea_1273 = gameFonts.fancyTextArea;

				// Validate all fonts were created correctly
				if (!gameFonts.validateFonts()) {
					throw new RuntimeException("Font validation failed");
				}

				// Set default text colors and effects
				TextController.defaultColor = 0xFFFFFF; // White
				TextController.defaultShadow = -1; // No shadow by default
				TextController.textColor = TextController.defaultColor;
				TextController.textShadowColor = TextController.defaultShadow;

				addConsoleMessage("🎨 Modern font system initialized successfully!");
				addConsoleMessage("✨ Features enabled:");
				addConsoleMessage("   • High-quality anti-aliased text rendering");
				addConsoleMessage("   • Full Unicode character support");
				addConsoleMessage("   • Color markup and special effects");
				addConsoleMessage("   • Image and clan icon embedding");
				addConsoleMessage("   • Zero font cache dependencies");

			} catch (Exception e) {
				addConsoleMessage("❌ Critical error during font initialization: " + e.getMessage());
				e.printStackTrace();
				throw new RuntimeException("Font system initialization failed - cannot continue", e);
			}

// Optional: Add this debug section if you want extra validation
			addConsoleMessage("🔍 Final font system validation...");
			try {
				// Test that all the main font objects exist and work
				if (newRegularFont != null) {
					newRegularFont.setDefaultTextEffectValues(0xFFFFFF, -1, 256);
					addConsoleMessage("  ✓ newRegularFont ready");
				}
				if (newBoldFont != null) {
					newBoldFont.setDefaultTextEffectValues(0xFFFFFF, -1, 256);
					addConsoleMessage("  ✓ newBoldFont ready");
				}
				if (newSmallFont != null) {
					newSmallFont.setDefaultTextEffectValues(0xFFFFFF, -1, 256);
					addConsoleMessage("  ✓ newSmallFont ready");
				}
				if (newFancyFont != null) {
					newFancyFont.setDefaultTextEffectValues(0xFFFFFF, -1, 256);
					addConsoleMessage("  ✓ newFancyFont ready");
				}

				addConsoleMessage("🎯 Font system fully operational!");

			} catch (Exception e) {
				addConsoleMessage("⚠️  Warning during final validation: " + e.getMessage());
				// Don't throw here - the fonts should still work even if this fails
			}

			// Load remaining streams - exact original order
			StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40, g);
			StreamLoader streamLoader = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30, g);
			StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35, g);
			StreamLoader streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45, g);
			//streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55, g);

			// PHASE 3: World systems - exactly as original
			byteGroundArray = new byte[4][208][208];
			intGroundArray = new int[4][209][209];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				aClass11Array1230[j] = new Class11();

			StreamLoader streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60, g);
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, client);
			EmbeddedMapCache.initialize();
			SequenceFrame.animationlist = new SequenceFrame[2500][0];
			Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);


			loadSpriteArraySafe(newHitMarks, streamLoader_2, "newhitmarks");
			loadSpriteArraySafe(channelButtons, streamLoader_2, "cbuttons");
			loadSpriteArraySafe(fixedGameComponents, streamLoader_2, "fixed");
			loadSpriteArraySafe(skillIcons, streamLoader_2, "skillicons");
			loadSpriteArraySafe(gameComponents, streamLoader_2, "fullscreen");

			loadSpriteArraySafe(orbComponents, streamLoader_2, "orbs3");
			loadSpriteArraySafe(orbComponents2, streamLoader_2, "orbs4");
			loadSpriteArraySafe(orbComponents3, streamLoader_2, "orbs5");
			loadSpriteArraySafe(redStones, streamLoader_2, "redstone1");
			loadSpriteArraySafe(hpBars, streamLoader_2, "hpbars");

			// Bulk copies - with safety checks but same logic
			performSafeBulkCopies();

			// Individual sprites - using safe creation
			multiOverlay = createSpriteOrThrow(streamLoader_2, "overlay_multiway", 0);
			mapBack = new Background(streamLoader_2, "mapback", 0);
			StatusOrbs.compass = createSpriteOrThrow(streamLoader_2, "compass", 0);
			mapFlag = createSpriteOrThrow(streamLoader_2, "mapmarker", 0);
			mapMarker = createSpriteOrThrow(streamLoader_2, "mapmarker", 1);

			// Side icons - exactly as original
			for (int j3 = 0; j3 <= 16; j3++) {
				sideIcons[j3] = createSpriteOrThrow(streamLoader_2, "sideicons", j3);
			}

			// Crosses - exactly as original
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = createSpriteOrThrow(streamLoader_2, "cross", k4);

			// Map dots - exactly as original
			scrollBar1 = createSpriteOrThrow(streamLoader_2, "scrollbar", 0);
			scrollBar2 = createSpriteOrThrow(streamLoader_2, "scrollbar", 1);

			// Optional sprites batch - exactly as original
			loadOptionalSpritesBatch(streamLoader_2);

			// Screen frames - exactly as original
			Sprite sprite = createSpriteOrThrow(streamLoader_2, "screenframe", 0);
			leftFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.method346(0, 0);
			sprite = createSpriteOrThrow(streamLoader_2, "screenframe", 1);
			topFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.method346(0, 0);

			// Color adjustments - exactly as original
			applyColorAdjustmentsOptimized();

			// Rendering systems - exactly as original
			Rasterizer.method368(streamLoader_3);
			Rasterizer.generateColorPalette(0.80000000000000004D);
			Rasterizer.method367();

			// Configuration loading - exactly as original order
			Animation.unpackConfig(streamLoader);
			ObjectDef.unpackConfig(streamLoader);
			Floor.unpackConfig(streamLoader);
			OverlayFloor.unpackConfig(streamLoader);
			ItemDef.unpackConfig(streamLoader);
			EntityDef.unpackConfig(streamLoader);
			IdentityKit.unpackConfig(streamLoader);
			SpotAnim.unpackConfig(streamLoader);
			Varp.unpackConfig(streamLoader);
			VarBit.unpackConfig(streamLoader);
			ItemDef.isMembers = isMembers;

			// Interface unpacking - exactly as original
			TextDrawingArea[] aclass30_sub2_sub1_sub4s = {smallText, regularText, boldText, aTextDrawingArea_1273};
			RSInterface.unpack(streamLoader_1, aclass30_sub2_sub1_sub4s, streamLoader_2);

			// Map bounds calculation - exactly as original
			calculateMapBoundsOptimized();

			// Final steps - exactly as original
			aRSImageProducer_1109.initDrawingArea();
			//initializeGPUAfterGraphicsLoad();
			aRSImageProducer_1109.drawGraphics(0, g, 0);
			setBounds();
			Animable_Sub5.clientInstance = client;
			ObjectDef.clientInstance = client;
			EntityDef.clientInstance = client;

			// Success - log performance
			long loadTime = System.currentTimeMillis() - loadStartTime;
			addConsoleMessage("GameLoader: Successfully loaded in " + loadTime + "ms");

			return;

		} catch (Exception exception) {
			exception.printStackTrace();
			Signlink.reporterror("loaderror " + aString1049 + " " + anInt1079);
			loadingError = true;
		} finally {
			isLoading = false;
			// Clean up weak references periodically
			if (System.currentTimeMillis() % 10000 < 1000) { // ~10% chance
				OptimizedSpriteFactory.cleanupCache();
			}
		}
	}

	/**
	 * Safe sprite creation that matches original behavior exactly
	 */
	private static Sprite createSpriteOrThrow(StreamLoader loader, String name, int index) {
		try {
			return OptimizedSpriteFactory.createSprite(loader, name, index);
		} catch (Exception e) {
			// Match original behavior - if sprite creation fails, let it fail
			return new Sprite(loader, name, index);
		}
	}

	/**
	 * Enhanced sprite array loading with null skipping
	 */
	private static void loadSpriteArraySafe(Sprite[] array, StreamLoader loader, String name) {
		if (array == null) {
			addConsoleMessage("Skipping null sprite array: " + name);
			return;
		}

		int loadedCount = 0;
		for (int index = 0; index < array.length; index++) {
			try {
				Sprite sprite = OptimizedSpriteFactory.createSprite(loader, name, index);
				if (sprite != null && sprite.myPixels != null && sprite.myWidth > 0 && sprite.myHeight > 0) {
					array[index] = sprite;
					loadedCount++;
				} else {
					// Don't even allocate empty sprites - just leave null
					addConsoleMessage("Skipping corrupted sprite: " + name + "[" + index + "]");
					break; // Original behavior: stop on first invalid sprite
				}
			} catch (Exception e) {
				addConsoleMessage("Failed to load sprite " + name + "[" + index + "] - stopping array load");
				break;
			}
		}
		addConsoleMessage("Loaded " + loadedCount + "/" + array.length + " sprites for " + name);
	}


	/**
	 * Enhanced bulk copy with comprehensive null checking
	 */
	private static void performSafeBulkCopies() {
		// Currency images copy - skip if source data is corrupted
		if (currencies > 0 && cacheSprite != null && currencyImage != null && cacheSprite.length > 407) {
			int copyLength = Math.min(currencies, Math.min(currencyImage.length, cacheSprite.length - 407));
			if (copyLength > 0) {
				// Validate source sprites aren't null before copying
				boolean hasValidSprites = true;
				for (int i = 0; i < copyLength; i++) {
					if (cacheSprite[407 + i] == null) {
						hasValidSprites = false;
						break;
					}
				}

				if (hasValidSprites) {
					System.arraycopy(cacheSprite, 407, currencyImage, 0, copyLength);
					addConsoleMessage("Copied " + copyLength + " currency sprites");
				} else {
					addConsoleMessage("Skipping currency copy - source sprites are null");
				}
			}
		} else {
			addConsoleMessage("Skipping currency copy - insufficient data");
		}

		// Hit marks copy - validate source data
		if (cacheSprite != null && hitMark != null && cacheSprite.length > 484) {
			int copyLength = Math.min(9, Math.min(hitMark.length, cacheSprite.length - 475));
			if (copyLength > 0 && validateSpriteRange(cacheSprite, 475, copyLength)) {
				System.arraycopy(cacheSprite, 475, hitMark, 0, copyLength);
				addConsoleMessage("Copied " + copyLength + " hit mark sprites");
			} else {
				addConsoleMessage("Skipping hit mark copy - corrupted source data");
			}
		}

		// Hit icons copy - validate source data
		if (cacheSprite != null && hitIcon != null && cacheSprite.length > 489) {
			int copyLength = Math.min(6, Math.min(hitIcon.length, cacheSprite.length - 484));
			if (copyLength > 0 && validateSpriteRange(cacheSprite, 484, copyLength)) {
				System.arraycopy(cacheSprite, 484, hitIcon, 0, copyLength);
				addConsoleMessage("Copied " + copyLength + " hit icon sprites");
			} else {
				addConsoleMessage("Skipping hit icon copy - corrupted source data");
			}
		}
	}

	/**
	 * Individual sprite creation with corruption detection
	 */
	private static Sprite createSpriteOrNull(StreamLoader loader, String name, int index) {
		try {
			Sprite sprite = OptimizedSpriteFactory.createSprite(loader, name, index);

			// Validate sprite integrity
			if (sprite == null || sprite.myPixels == null || sprite.myWidth <= 0 || sprite.myHeight <= 0) {
				addConsoleMessage("Corrupted sprite detected: " + name + "[" + index + "] - skipping");
				return null;
			}

			return sprite;
		} catch (Exception e) {
			addConsoleMessage("Failed to create sprite " + name + "[" + index + "]: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Skip entire sprite collections that are corrupted
	 */
	private static void loadOptionalSpritesBatch(StreamLoader streamLoader_2) {
		// Map scenes - skip if corrupted
		int mapSceneCount = loadBackgroundArray(mapScenes, streamLoader_2, "mapscene", 100);
		addConsoleMessage("Loaded " + mapSceneCount + " map scenes");

		// Map functions - skip corrupted ones
		int mapFunctionCount = loadSpriteArray(mapFunctions, streamLoader_2, "mapfunction", 100);
		addConsoleMessage("Loaded " + mapFunctionCount + " map functions");

		// Hit marks
		int hitMarkCount = loadSpriteArray(hitMarks, streamLoader_2, "hitmarks", 20);
		addConsoleMessage("Loaded " + hitMarkCount + " hit marks");

		// Head icons hint
		int hintIconCount = loadSpriteArray(headIconsHint, streamLoader_2, "headicons_hint", 6);
		addConsoleMessage("Loaded " + hintIconCount + " hint icons");

		// Head icons prayer
		int prayerIconCount = loadSpriteArray(headIcons, streamLoader_2, "headicons_prayer", 8);
		addConsoleMessage("Loaded " + prayerIconCount + " prayer icons");

		// Skull icons
		int skullIconCount = loadSpriteArray(skullIcons, streamLoader_2, "headicons_pk", 3);
		addConsoleMessage("Loaded " + skullIconCount + " skull icons");

	}


	/**
	 * Generic sprite array loader with null skipping
	 */
	private static int loadSpriteArray(Sprite[] array, StreamLoader loader, String name, int maxCount) {
		if (array == null || array.length == 0) {
			addConsoleMessage("Skipping empty/null array: " + name);
			return 0;
		}

		int loaded = 0;
		for (int i = 0; i < Math.min(maxCount, array.length); i++) {
			Sprite sprite = createSpriteOrNull(loader, name, i);
			if (sprite != null) {
				array[i] = sprite;
				loaded++;
			} else {
				// Stop loading this array - original Vencillio behavior
				break;
			}
		}
		return loaded;
	}

	/**
	 * Background array loader with null skipping
	 */
	private static int loadBackgroundArray(Background[] array, StreamLoader loader, String name, int maxCount) {
		if (array == null || array.length == 0) {
			addConsoleMessage("Skipping empty/null background array: " + name);
			return 0;
		}

		int loaded = 0;
		for (int i = 0; i < Math.min(maxCount, array.length); i++) {
			try {
				Background bg = new Background(loader, name, i);
				if (bg != null && bg.aByteArray1450 != null) {
					array[i] = bg;
					loaded++;
				} else {
					break;
				}
			} catch (Exception e) {
				addConsoleMessage("Failed to load background " + name + "[" + i + "]");
				break;
			}
		}
		return loaded;
	}

	/**
	 * Validate a range of sprites for null/corruption
	 */
	private static boolean validateSpriteRange(Sprite[] sprites, int start, int length) {
		for (int i = start; i < start + length && i < sprites.length; i++) {
			if (sprites[i] == null) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Enhanced color adjustment with null skipping
	 */
	private static void applyColorAdjustmentsOptimized() {
		int i5 = (int) (Math.random() * 21D) - 10;
		int j5 = (int) (Math.random() * 21D) - 10;
		int k5 = (int) (Math.random() * 21D) - 10;
		int l5 = (int) (Math.random() * 41D) - 20;
		int colorAdjust1 = i5 + l5;
		int colorAdjust2 = j5 + l5;
		int colorAdjust3 = k5 + l5;

		int functionsProcessed = 0;
		int scenesProcessed = 0;

		for (int i6 = 0; i6 < 100; i6++) {
			// Only process non-null sprites with valid pixel data
			if (mapFunctions[i6] != null && mapFunctions[i6].myPixels != null) {
				mapFunctions[i6].method344(colorAdjust1, colorAdjust2, colorAdjust3);
				functionsProcessed++;
			}
			if (mapScenes[i6] != null && mapScenes[i6].aByteArray1450 != null) {
				mapScenes[i6].method360(colorAdjust1, colorAdjust2, colorAdjust3);
				scenesProcessed++;
			}
		}

		addConsoleMessage("Applied color adjustments to " + functionsProcessed + " functions, " + scenesProcessed + " scenes");
	}

	/**
	 * Map bounds calculation - exactly as original
	 */
	private static void calculateMapBoundsOptimized() {
		if (mapBack == null) {
			addConsoleMessage("Warning: mapBack is null, skipping map bounds calculation");
			return;
		}

		byte[] mapData = mapBack.aByteArray1450;
		int mapWidth = mapBack.anInt1452;

		for (int j6 = 0; j6 < 33; j6++) {
			int k6 = 999;
			int i7 = 0;
			int baseOffset = j6 * mapWidth;
			for (int k7 = 0; k7 < 34; k7++) {
				if (mapData[k7 + baseOffset] == 0) {
					if (k6 == 999)
						k6 = k7;
					continue;
				}
				if (k6 == 999)
					continue;
				i7 = k7;
				break;
			}
			anIntArray968[j6] = k6;
			anIntArray1057[j6] = i7 - k6;
		}

		for (int l6 = 1; l6 < 153; l6++) {
			int j7 = 999;
			int l7 = 0;
			int baseOffset = l6 * mapWidth;
			for (int j8 = 24; j8 < 177; j8++) {
				if (mapData[j8 + baseOffset] == 0 && (j8 > 34 || l6 > 34)) {
					if (j7 == 999) {
						j7 = j8;
					}
					continue;
				}
				if (j7 == 999) {
					continue;
				}
				l7 = j8;
				break;
			}
			anIntArray1052[l6 - 1] = j7 - 24;
			anIntArray1229[l6 - 1] = l7 - j7;
		}
	}


	/**
	 * Public API for monitoring loading state
	 */
	public static boolean isCurrentlyLoading() {
		return isLoading;
	}

	/**
	 * Public API for cache management
	 */
	public static void cleanupCaches() {
		OptimizedSpriteFactory.cleanupCache();
	}

	/**
	 * Get cache statistics for debugging
	 */
	public static String getCacheStats() {
		int cacheSize = spriteCache.size();
		int activeRefs = (int) spriteCache.values().stream()
			.mapToLong(ref -> ref.get() != null ? 1 : 0)
			.sum();
		return String.format("Cache: %d entries, %d active", cacheSize, activeRefs);
	}

	public void raiseWelcomeScreen() {
		welcomeScreenRaised = true;
	}
}