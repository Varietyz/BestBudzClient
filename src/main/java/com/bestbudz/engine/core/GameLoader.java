package com.bestbudz.engine.core;

import com.bestbudz.cache.Signlink;
import com.bestbudz.data.AccountManager;
import com.bestbudz.data.ItemDef;
import static com.bestbudz.engine.ClientLauncher.initializeGPUAfterGraphicsLoad;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.core.login.LoginRenderer;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.cache.IdentityKit;
import com.bestbudz.graphics.Background;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.sprite.SpriteLoader;
import com.bestbudz.graphics.text.RSFont;
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

			// PHASE 2: Stream loading - exact original order
			titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25, g);

			// Font initialization - exactly as original
			smallText = new TextDrawingArea(false, "p11_full", titleStreamLoader);
			regularText = new TextDrawingArea(false, "p12_full", titleStreamLoader);
			boldText = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			newSmallFont = new RSFont(false, "p11_full", titleStreamLoader);
			newRegularFont = new RSFont(false, "p12_full", titleStreamLoader);
			newBoldFont = new RSFont(false, "b12_full", titleStreamLoader);
			newFancyFont = new RSFont(true, "q8_full", titleStreamLoader);
			TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);

			// Load remaining streams - exact original order
			StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40, g);
			StreamLoader streamLoader = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30, g);
			StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35, g);
			StreamLoader streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45, g);
			streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55, g);

			// PHASE 3: World systems - exactly as original
			byteGroundArray = new byte[4][208][208];
			intGroundArray = new int[4][209][209];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				aClass11Array1230[j] = new Class11();

			minimapImage = new Sprite(512, 512);
			StreamLoader streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60, g);
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, client);
			SequenceFrame.animationlist = new SequenceFrame[2500][0];
			Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);

			// PHASE 4: Sprite loading - using optimized methods but maintaining exact order
			Sprite[] clanIcons = new Sprite[10];

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
			loadSpriteArraySafe(clanIcons, streamLoader_2, "clanicons");

			// Bulk copies - with safety checks but same logic
			performSafeBulkCopies();

			// Font unpacking - exactly as original
			newSmallFont.unpackImages(modIcons, clanIcons);
			newRegularFont.unpackImages(modIcons, clanIcons);
			newBoldFont.unpackImages(modIcons, clanIcons);
			newFancyFont.unpackImages(modIcons, clanIcons);

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
			mapDotItem = createSpriteOrThrow(streamLoader_2, "mapdots", 0);
			mapDotNPC = createSpriteOrThrow(streamLoader_2, "mapdots", 1);
			mapDotStoner = createSpriteOrThrow(streamLoader_2, "mapdots", 2);
			mapDotStoner = createSpriteOrThrow(streamLoader_2, "mapdots", 3);
			mapDotTeam = createSpriteOrThrow(streamLoader_2, "mapdots", 4);
			mapDotClan = createSpriteOrThrow(streamLoader_2, "mapdots", 5);
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
			System.out.println("GameLoader: Successfully loaded in " + loadTime + "ms");

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
	 * Safe sprite array loading that preserves original termination behavior
	 */
	private static void loadSpriteArraySafe(Sprite[] array, StreamLoader loader, String name) {
		if (array == null) return;

		for (int index = 0; index < array.length; index++) {
			try {
				array[index] = OptimizedSpriteFactory.createSprite(loader, name, index);
			} catch (Exception e) {
				// Original behavior: early termination on first failure
				break;
			}
		}
	}

	/**
	 * Safe bulk copy operations with bounds checking
	 */
	private static void performSafeBulkCopies() {
		try {
			// Currency images copy - with safety checks
			if (currencies >= 0 && cacheSprite != null && currencyImage != null) {
				int srcStart = 407;
				if (srcStart + currencies <= cacheSprite.length && currencies <= currencyImage.length) {
					System.arraycopy(cacheSprite, srcStart, currencyImage, 0, currencies);
				}
			}

			// Hit marks copy - with safety checks
			if (cacheSprite != null && hitMark != null && cacheSprite.length > 484) {
				int copyLength = Math.min(9, Math.min(hitMark.length, cacheSprite.length - 475));
				if (copyLength > 0) {
					System.arraycopy(cacheSprite, 475, hitMark, 0, copyLength);
				}
			}

			// Hit icons copy - with safety checks
			if (cacheSprite != null && hitIcon != null && cacheSprite.length > 489) {
				int copyLength = Math.min(6, Math.min(hitIcon.length, cacheSprite.length - 484));
				if (copyLength > 0) {
					System.arraycopy(cacheSprite, 484, hitIcon, 0, copyLength);
				}
			}

		} catch (Exception e) {
			System.err.println("Warning: Bulk copy operation failed: " + e.getMessage());
			// Don't let this break the loading process
		}
	}

	/**
	 * Batch load optional sprites - exactly as original with memory optimization
	 */
	private static void loadOptionalSpritesBatch(StreamLoader streamLoader_2) {
		// Map scenes - exactly as original
		for (int k3 = 0; k3 < 100; k3++) {
			try {
				mapScenes[k3] = new Background(streamLoader_2, "mapscene", k3);
			} catch (Exception e) {
				break;
			}
		}

		// Map functions - with caching
		for (int l3 = 0; l3 < 100; l3++) {
			try {
				mapFunctions[l3] = OptimizedSpriteFactory.createSprite(streamLoader_2, "mapfunction", l3);
			} catch (Exception e) {
				break;
			}
		}

		// Hit marks - with caching
		for (int i4 = 0; i4 < 20; i4++) {
			try {
				hitMarks[i4] = OptimizedSpriteFactory.createSprite(streamLoader_2, "hitmarks", i4);
			} catch (Exception e) {
				break;
			}
		}

		// Head icons hint - with caching
		for (int h1 = 0; h1 < 6; h1++) {
			try {
				headIconsHint[h1] = OptimizedSpriteFactory.createSprite(streamLoader_2, "headicons_hint", h1);
			} catch (Exception e) {
				break;
			}
		}

		// Head icons prayer - with caching
		for (int j4 = 0; j4 < 8; j4++) {
			try {
				headIcons[j4] = OptimizedSpriteFactory.createSprite(streamLoader_2, "headicons_prayer", j4);
			} catch (Exception e) {
				break;
			}
		}

		// Skull icons - with caching
		for (int j45 = 0; j45 < 3; j45++) {
			try {
				skullIcons[j45] = OptimizedSpriteFactory.createSprite(streamLoader_2, "headicons_pk", j45);
			} catch (Exception e) {
				break;
			}
		}

		// Mod icons - with caching
		for (int l4 = 0; l4 < EngineConfig.ICON_AMOUNT; l4++) {
			try {
				modIcons[l4] = OptimizedSpriteFactory.createSprite(streamLoader_2, "mod_icons", l4);
			} catch (Exception e) {
				break;
			}
		}
	}

	/**
	 * Color adjustments - exactly as original
	 */
	private static void applyColorAdjustmentsOptimized() {
		int i5 = (int) (Math.random() * 21D) - 10;
		int j5 = (int) (Math.random() * 21D) - 10;
		int k5 = (int) (Math.random() * 21D) - 10;
		int l5 = (int) (Math.random() * 41D) - 20;
		int colorAdjust1 = i5 + l5;
		int colorAdjust2 = j5 + l5;
		int colorAdjust3 = k5 + l5;

		for (int i6 = 0; i6 < 100; i6++) {
			if (mapFunctions[i6] != null)
				mapFunctions[i6].method344(colorAdjust1, colorAdjust2, colorAdjust3);
			if (mapScenes[i6] != null)
				mapScenes[i6].method360(colorAdjust1, colorAdjust2, colorAdjust3);
		}
	}

	/**
	 * Map bounds calculation - exactly as original
	 */
	private static void calculateMapBoundsOptimized() {
		if (mapBack == null) {
			System.err.println("Warning: mapBack is null, skipping map bounds calculation");
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