package com.bestbudz.engine.core;

import com.bestbudz.cache.Signlink;
import com.bestbudz.data.AccountManager;
import com.bestbudz.data.items.ItemDef;
import static com.bestbudz.engine.core.LoadingErrorScreen.addConsoleMessage;
import com.bestbudz.engine.core.gamerender.ColorPalette;
import com.bestbudz.engine.core.loading.LoadingEnums;
import com.bestbudz.engine.core.loading.LoadingVisual;
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
import com.bestbudz.network.CacheManager;
import com.bestbudz.rendering.InteractiveObject;
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
import com.bestbudz.world.CollisionMap;
import com.bestbudz.world.Floor;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.world.VarBit;
import com.bestbudz.world.Varp;
import com.bestbudz.engine.core.gamerender.WorldController;
import com.bestbudz.engine.core.loading.LoadingEnums.LoadingPhase;
import com.bestbudz.engine.core.loading.LoadingEnums.LogLevel;

import java.awt.Graphics2D;
import java.lang.ref.WeakReference;
import java.util.concurrent.*;
import java.util.*;

public class GameLoader extends Client {

	private static final Map<String, WeakReference<Sprite>> spriteCache = new ConcurrentHashMap<>();
	private static volatile boolean isLoading = false;
	private static long loadStartTime = 0;
	private static LoadingVisual loadingScreen;

	private static class LoadingMetrics {

		private int totalFilesRead = 0;
		private int totalBytesRead = 0;
		private int totalFilesWritten = 0;
		private int totalBytesWritten = 0;

		private int spritesLoaded = 0;
		private int spritesProcessed = 0;
		private int backgroundsLoaded = 0;
		private int configsLoaded = 0;
		private int modelsProcessed = 0;
		private int texturesLoaded = 0;
		private int interfacesLoaded = 0;

		private int cacheHits = 0;
		private int cacheMisses = 0;
		private int cacheEvictions = 0;

		private long initialMemory = 0;
		private long peakMemory = 0;
		private long currentMemory = 0;

		private final Map<String, Long> phaseTimes = new ConcurrentHashMap<>();
		private final Map<String, Long> operationTimes = new ConcurrentHashMap<>();

		private int errors = 0;
		private int warnings = 0;

		public void startPhase(String phase) {
			phaseTimes.put(phase + "_start", System.currentTimeMillis());
		}

		public void endPhase(String phase) {
			long startTime = phaseTimes.getOrDefault(phase + "_start", System.currentTimeMillis());
			long duration = System.currentTimeMillis() - startTime;
			phaseTimes.put(phase + "_duration", duration);
		}

		public void startOperation(String operation) {
			operationTimes.put(operation + "_start", System.currentTimeMillis());
		}

		public void endOperation(String operation) {
			long startTime = operationTimes.getOrDefault(operation + "_start", System.currentTimeMillis());
			long duration = System.currentTimeMillis() - startTime;
			operationTimes.put(operation + "_duration", duration);
		}

		public void updateMemoryMetrics() {
			Runtime runtime = Runtime.getRuntime();
			long totalMemory = runtime.totalMemory();
			long freeMemory = runtime.freeMemory();
			currentMemory = totalMemory - freeMemory;

			if (initialMemory == 0) {
				initialMemory = currentMemory;
			}

			if (currentMemory > peakMemory) {
				peakMemory = currentMemory;
			}
		}

		public String getMemoryReport() {
			double initialMB = initialMemory / (1024.0 * 1024.0);
			double currentMB = currentMemory / (1024.0 * 1024.0);
			double peakMB = peakMemory / (1024.0 * 1024.0);
			double allocatedMB = (currentMemory - initialMemory) / (1024.0 * 1024.0);

			return String.format("Memory: %.1fMB → %.1fMB (peak: %.1fMB, allocated: +%.1fMB)",
				initialMB, currentMB, peakMB, allocatedMB);
		}

		public String getIOReport() {
			double readMB = totalBytesRead / (1024.0 * 1024.0);
			double writeMB = totalBytesWritten / (1024.0 * 1024.0);

			return String.format("I/O: %d files read (%.1fMB), %d files written (%.1fMB)",
				totalFilesRead, readMB, totalFilesWritten, writeMB);
		}

		public String getCacheReport() {
			int totalRequests = cacheHits + cacheMisses;
			double hitRate = totalRequests > 0 ? (cacheHits * 100.0 / totalRequests) : 0;

			return String.format("Cache: %d hits, %d misses (%.1f%% hit rate), %d evictions",
				cacheHits, cacheMisses, hitRate, cacheEvictions);
		}

		public String getProcessingReport() {
			return String.format("Processed: %d sprites, %d backgrounds, %d configs, %d models, %d textures, %d interfaces",
				spritesProcessed, backgroundsLoaded, configsLoaded, modelsProcessed, texturesLoaded, interfacesLoaded);
		}
	}

	private static final LoadingMetrics metrics = new LoadingMetrics();

	static class OptimizedSpriteFactory {
		private static com.google.gson.JsonObject mediaSpriteIndex;

		private static com.google.gson.JsonObject getMediaSpriteIndex() {
			if (mediaSpriteIndex == null) {
				mediaSpriteIndex = com.bestbudz.cache.JsonCacheLoader.loadJsonObject("media_sprites/_index.json");
				if (mediaSpriteIndex == null) {
					throw new RuntimeException("Failed to load media_sprites/_index.json");
				}
			}
			return mediaSpriteIndex;
		}

		public static Sprite createSprite(String name, int index) {
			String key = name + "_" + index;
			long startTime = System.nanoTime();

			WeakReference<Sprite> ref = spriteCache.get(key);
			if (ref != null) {
				Sprite cached = ref.get();
				if (cached != null) {
					metrics.cacheHits++;
					return cached;
				} else {
					spriteCache.remove(key);
					metrics.cacheEvictions++;
				}
			}

			metrics.cacheMisses++;

			try {
				com.google.gson.JsonObject idx = getMediaSpriteIndex();
				if (!idx.has(key)) {
					throw new RuntimeException("Sprite not found in media index: " + key);
				}
				com.google.gson.JsonObject meta = idx.getAsJsonObject(key);
				String file = meta.get("file").getAsString();
				int width = meta.get("width").getAsInt();
				int height = meta.get("height").getAsInt();
				int offsetX = meta.has("offsetX") ? meta.get("offsetX").getAsInt() : 0;
				int offsetY = meta.has("offsetY") ? meta.get("offsetY").getAsInt() : 0;
				int fullWidth = meta.has("fullWidth") ? meta.get("fullWidth").getAsInt() : width;
				int fullHeight = meta.has("fullHeight") ? meta.get("fullHeight").getAsInt() : height;

				byte[] pngData = com.bestbudz.cache.JsonCacheLoader.loadFileBytes("media_sprites/" + file);
				if (pngData == null) {
					throw new RuntimeException("Missing PNG file: media_sprites/" + file);
				}

				java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().createImage(pngData);
				javax.swing.ImageIcon icon = new javax.swing.ImageIcon(image);
				int imgW = icon.getIconWidth();
				int imgH = icon.getIconHeight();

				Sprite sprite = new Sprite(imgW, imgH);
				sprite.drawOffsetX = offsetX;
				sprite.drawOffsetY = offsetY;
				sprite.originalWidth = fullWidth;
				sprite.originalHeight = fullHeight;
				java.awt.image.PixelGrabber pg = new java.awt.image.PixelGrabber(
					image, 0, 0, imgW, imgH, sprite.myPixels, 0, imgW);
				pg.grabPixels();
				sprite.setTransparency(255, 0, 255);

				spriteCache.put(key, new WeakReference<>(sprite));
				metrics.spritesLoaded++;
				metrics.totalFilesRead++;
				if (sprite.myPixels != null) {
					metrics.totalBytesRead += sprite.myPixels.length * 4;
				}
				return sprite;
			} catch (Exception e) {
				metrics.errors++;
				throw new RuntimeException("Failed to load sprite from PNG: " + key, e);
			}
		}

		public static void cleanupCache() {
			int initialSize = spriteCache.size();
			spriteCache.entrySet().removeIf(entry -> entry.getValue().get() == null);
			int cleaned = initialSize - spriteCache.size();
			if (cleaned > 0) {
				metrics.cacheEvictions += cleaned;
				loadingScreen.addLogEntry(String.format("Cache cleanup: removed %d dead references", cleaned), LogLevel.INFO);
			}
		}
	}

	static void startUp(Graphics2D g, Client client) {
		isLoading = true;
		loadStartTime = System.currentTimeMillis();
		metrics.updateMemoryMetrics();

		loadingScreen = new LoadingVisual();
		loadingScreen.showLoader();
		loadingScreen.addLogEntry("🚀 GameLoader initialization started", LogLevel.INFO);
		loadingScreen.addLogEntry("💾 " + metrics.getMemoryReport(), LogLevel.INFO);

		try {

			metrics.startPhase("initialization");
			loadingScreen.setPhase(LoadingPhase.INITIALIZING);
			loadingScreen.updateDetail("Setting up core system...");

			metrics.startOperation("account_manager");
			AccountManager.loadAccount();
			metrics.endOperation("account_manager");
			metrics.configsLoaded++;
			long accountTime = metrics.operationTimes.get("account_manager_duration");
			loadingScreen.updateDetailProgress(20);
			loadingScreen.addLogEntry(String.format("✅ Account manager loaded (%dms)", accountTime), LogLevel.SUCCESS);

			metrics.startOperation("sprite_loader");
			SpriteLoader.loadSprites();
			metrics.endOperation("sprite_loader");
			long spriteLoaderTime = metrics.operationTimes.get("sprite_loader_duration");
			loadingScreen.updateDetailProgress(40);
			loadingScreen.addLogEntry(String.format("✅ Sprite loader initialized (%dms)", spriteLoaderTime), LogLevel.SUCCESS);

			cacheSprite = SpriteLoader.sprites;
			if (cacheSprite != null) {
				loadingScreen.addLogEntry(String.format("📦 Cache sprite array: %d entries allocated", cacheSprite.length), LogLevel.INFO);
			}

			metrics.startOperation("login_renderer");
			loginRenderer = new LoginRenderer(client);
			metrics.endOperation("login_renderer");
			long loginTime = metrics.operationTimes.get("login_renderer_duration");
			loadingScreen.updateDetailProgress(60);
			loadingScreen.addLogEntry(String.format("✅ Login renderer created (%dms)", loginTime), LogLevel.SUCCESS);

			// Binary cache decompressors removed — all data now loaded from JSON

			metrics.endPhase("initialization");
			long initTime = metrics.phaseTimes.get("initialization_duration");
			loadingScreen.updateProgress(LoadingPhase.INITIALIZING.endProgress);
			loadingScreen.addLogEntry(String.format("🎯 Initialization phase completed (%dms)", initTime), LogLevel.SUCCESS);

			metrics.startPhase("fonts");
			loadingScreen.updateDetail("Initializing modern font system...");
			loadingScreen.addLogEntry("🎨 Initializing modern font system...", LogLevel.INFO);

			TextDrawingArea aTextDrawingArea_1273;
			try {
				metrics.startOperation("font_creation");
				FontSystem.GameFonts gameFonts = FontSystem.GameFonts.create();
				metrics.endOperation("font_creation");
				long fontCreateTime = metrics.operationTimes.get("font_creation_duration");
				loadingScreen.updateDetailProgress(25);
				loadingScreen.addLogEntry(String.format("📝 Font objects created (%dms)", fontCreateTime), LogLevel.INFO);

				metrics.startOperation("font_assignment");
				smallText = gameFonts.smallText;
				regularText = gameFonts.regularText;
				boldText = gameFonts.boldText;
				newSmallFont = gameFonts.newSmallFont;
				newRegularFont = gameFonts.newRegularFont;
				newBoldFont = gameFonts.newBoldFont;
				newFancyFont = gameFonts.newFancyFont;
				aTextDrawingArea_1273 = gameFonts.fancyTextArea;
				metrics.endOperation("font_assignment");
				long assignTime = metrics.operationTimes.get("font_assignment_duration");
				loadingScreen.updateDetailProgress(50);
				loadingScreen.addLogEntry(String.format("📋 Font assignment completed (%dms)", assignTime), LogLevel.INFO);

				metrics.startOperation("font_validation");
				if (!gameFonts.validateFonts()) {
					throw new RuntimeException("Font validation failed");
				}
				metrics.endOperation("font_validation");
				long validationTime = metrics.operationTimes.get("font_validation_duration");
				loadingScreen.updateDetailProgress(75);
				loadingScreen.addLogEntry(String.format("✅ Font validation passed (%dms)", validationTime), LogLevel.SUCCESS);

				TextController.defaultColor = 0xFFFFFF;
				TextController.defaultShadow = -1;
				TextController.textColor = TextController.defaultColor;
				TextController.textShadowColor = TextController.defaultShadow;
				loadingScreen.updateDetailProgress(100);

				metrics.endPhase("fonts");
				long fontTime = metrics.phaseTimes.get("fonts_duration");
				loadingScreen.addLogEntry(String.format("🎨 Font system ready! (%dms total)", fontTime), LogLevel.SUCCESS);
				loadingScreen.addLogEntry("✨ High-quality anti-aliased text rendering enabled", LogLevel.INFO);
				loadingScreen.addLogEntry("✨ Full Unicode character support enabled", LogLevel.INFO);

			} catch (Exception e) {
				metrics.errors++;
				metrics.endPhase("fonts");
				loadingScreen.addLogEntry("❌ Critical error during font initialization: " + e.getMessage(), LogLevel.ERROR);
				e.printStackTrace();
				throw new RuntimeException("Font system initialization failed", e);
			}

			metrics.startPhase("assets");
			loadingScreen.setPhase(LoadingPhase.LOADING_ASSETS);
			loadingScreen.updateDetail("Loading game assets...");

			// Archives no longer needed — all data loaded from extracted JSON/PNG
			// archiveLoader (config), archiveLoader_1 (interface),
			// archiveLoader_2 (media), archiveLoader_3 (textures) — removed

			loadingScreen.updateProgress(35);
			loadingScreen.updateDetail("Setting up world system...");

			metrics.startOperation("world_setup");
			byteGroundArray = new byte[4][208][208];
			intGroundArray = new int[4][209][209];
			worldController = new WorldController(intGroundArray);

			int worldControllerSize = 4 * 208 * 208 + 4 * 209 * 209 * 4;
			metrics.totalBytesWritten += worldControllerSize;

			for (int j = 0; j < 4; j++) {
				collisionMaps[j] = new CollisionMap();
			}
			metrics.endOperation("world_setup");
			long worldTime = metrics.operationTimes.get("world_setup_duration");
			loadingScreen.addLogEntry(String.format("🌍 World controller initialized (%dms, ~%.1fMB allocated)",
				worldTime, worldControllerSize / (1024.0 * 1024.0)), LogLevel.SUCCESS);

			metrics.startOperation("midi_index");
			cacheManager = new CacheManager();
			cacheManager.start();
			metrics.endOperation("midi_index");
			long midiTime = metrics.operationTimes.get("midi_index_duration");
			loadingScreen.addLogEntry(String.format("🎵 MIDI index loaded (%dms)", midiTime), LogLevel.SUCCESS);

			metrics.startOperation("animation_setup");
			SequenceFrame.animationlist = new SequenceFrame[2500][0];
			int modelCount = cacheManager.getModelCount();
			Model.initializeCache(modelCount);
			metrics.modelsProcessed = modelCount;
			metrics.endOperation("animation_setup");
			long animTime = metrics.operationTimes.get("animation_setup_duration");
			loadingScreen.addLogEntry(String.format("🎭 Animation system ready (%dms, %d models registered)",
				animTime, modelCount), LogLevel.SUCCESS);

			metrics.endPhase("assets");
			long assetsTime = metrics.phaseTimes.get("assets_duration");
			loadingScreen.updateProgress(LoadingPhase.LOADING_ASSETS.endProgress);
			loadingScreen.addLogEntry(String.format("📦 Asset loading phase completed (%dms)", assetsTime), LogLevel.SUCCESS);

			metrics.startPhase("processing");
			loadingScreen.setPhase(LoadingPhase.PROCESSING_DATA);
			loadingScreen.updateDetail("Loading sprite collections...");

			loadSpriteArrayWithProgress(newHitMarks, "newhitmarks", 5);
			loadSpriteArrayWithProgress(channelButtons, "cbuttons", 10);
			loadSpriteArrayWithProgress(fixedGameComponents, "fixed", 15);
			loadSpriteArrayWithProgress(skillIcons, "skillicons", 20);
			loadSpriteArrayWithProgress(gameComponents, "fullscreen", 25);
			loadSpriteArrayWithProgress(orbComponents, "orbs3", 30);
			loadSpriteArrayWithProgress(orbComponents2, "orbs4", 35);
			loadSpriteArrayWithProgress(orbComponents3, "orbs5", 40);
			loadSpriteArrayWithProgress(redStones, "redstone1", 45);
			loadSpriteArrayWithProgress(hpBars, "hpbars", 50);

			loadingScreen.updateDetail("Processing bulk data...");
			performSafeBulkCopies();
			loadingScreen.updateDetailProgress(60);

			loadingScreen.updateDetail("Loading individual sprites...");
			metrics.startOperation("individual_sprites");

			try {
				multiOverlay = createSpriteOrThrow("overlay_multiway", 0);
			} catch (Exception e) {
				multiOverlay = null;
				loadingScreen.addLogEntry("overlay_multiway sprite not found, skipping", LogLevel.WARNING);
				metrics.warnings++;
			}
			mapBack = Background.loadFromExtracted("mapback", 0);
			metrics.backgroundsLoaded++;

			StatusOrbs.compass = createSpriteOrThrow("compass", 0);
			mapFlag = createSpriteOrThrow("mapmarker", 0);
			mapMarker = createSpriteOrThrow("mapmarker", 1);
			loadingScreen.updateDetailProgress(70);

			int sideIconsLoaded = 0;
			for (int j3 = 0; j3 < 16; j3++) {
				sideIcons[j3] = createSpriteOrThrow("sideicons", j3);
				if (sideIcons[j3] != null) sideIconsLoaded++;
			}

			int crossesLoaded = 0;
			for (int k4 = 0; k4 < 8; k4++) {
				crosses[k4] = createSpriteOrThrow("cross", k4);
				if (crosses[k4] != null) crossesLoaded++;
			}
			loadingScreen.updateDetailProgress(80);

			scrollBar1 = createSpriteOrThrow("scrollbar", 0);
			scrollBar2 = createSpriteOrThrow("scrollbar", 1);

			metrics.endOperation("individual_sprites");
			long individualTime = metrics.operationTimes.get("individual_sprites_duration");
			loadingScreen.addLogEntry(String.format("🎯 Individual sprites loaded (%dms): %d side icons, %d crosses, 5 core sprites",
				individualTime, sideIconsLoaded, crossesLoaded), LogLevel.SUCCESS);

			loadOptionalSpritesBatch();
			loadingScreen.updateDetailProgress(90);

			metrics.startOperation("screen_frames");
			Sprite sprite = createSpriteOrThrow("screenframe", 0);
			leftFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.drawOpaque(0, 0);
			sprite = createSpriteOrThrow("screenframe", 1);
			topFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.drawOpaque(0, 0);
			metrics.endOperation("screen_frames");
			long frameTime = metrics.operationTimes.get("screen_frames_duration");
			loadingScreen.updateDetailProgress(100);
			loadingScreen.addLogEntry(String.format("🖼️ Screen frames created (%dms)", frameTime), LogLevel.SUCCESS);

			metrics.endPhase("processing");
			long processingTime = metrics.phaseTimes.get("processing_duration");
			loadingScreen.updateProgress(LoadingPhase.PROCESSING_DATA.endProgress);
			loadingScreen.addLogEntry(String.format("⚙️ Data processing phase completed (%dms)", processingTime), LogLevel.SUCCESS);

			metrics.startPhase("finalization");
			loadingScreen.setPhase(LoadingPhase.FINALIZING);
			loadingScreen.updateDetail("Applying visual effects...");

			metrics.startOperation("color_adjustments");
			applyColorAdjustmentsOptimized();
			metrics.endOperation("color_adjustments");
			long colorTime = metrics.operationTimes.get("color_adjustments_duration");
			loadingScreen.updateDetailProgress(10);
			loadingScreen.addLogEntry(String.format("🎨 Color adjustments applied (%dms)", colorTime), LogLevel.SUCCESS);

			loadingScreen.updateDetail("Initializing rendering system...");

			metrics.startOperation("texture_loading");
			Rasterizer.loadTextures();
			metrics.endOperation("texture_loading");
			long textureTime = metrics.operationTimes.get("texture_loading_duration");
			metrics.texturesLoaded++;
			loadingScreen.updateDetailProgress(25);
			loadingScreen.addLogEntry(String.format("🏞️ Textures loaded (%dms)", textureTime), LogLevel.SUCCESS);

			metrics.startOperation("color_palette");
			ColorPalette.generateColorPalette(0.80000000000000004D);
			metrics.endOperation("color_palette");
			long paletteTime = metrics.operationTimes.get("color_palette_duration");
			loadingScreen.updateDetailProgress(40);
			loadingScreen.addLogEntry(String.format("🎨 Color palette generated (%dms)", paletteTime), LogLevel.SUCCESS);

			metrics.startOperation("texture_pool");
			Rasterizer.initializeTexturePool();
			metrics.endOperation("texture_pool");
			long poolTime = metrics.operationTimes.get("texture_pool_duration");
			loadingScreen.updateDetailProgress(55);
			loadingScreen.addLogEntry(String.format("🏊 Texture pool initialized (%dms)", poolTime), LogLevel.SUCCESS);

			loadingScreen.updateDetail("Loading game configuration...");
			loadConfigurationsWithMetrics();

			loadingScreen.updateDetail("Finalizing interface system...");
			metrics.startOperation("interface_setup");
			TextDrawingArea[] aclass30_sub2_sub1_sub4s = {smallText, regularText, boldText, aTextDrawingArea_1273};
			RSInterface.unpack(aclass30_sub2_sub1_sub4s);
			metrics.interfacesLoaded++;
			metrics.endOperation("interface_setup");
			long interfaceTime = metrics.operationTimes.get("interface_setup_duration");
			loadingScreen.updateDetailProgress(98);
			loadingScreen.addLogEntry(String.format("🖥️ Interface system ready (%dms)", interfaceTime), LogLevel.SUCCESS);

			loadingScreen.updateDetail("Calculating map boundaries...");
			metrics.startOperation("map_bounds");
			calculateMapBoundsOptimized();
			metrics.endOperation("map_bounds");
			long boundsTime = metrics.operationTimes.get("map_bounds_duration");
			loadingScreen.updateDetailProgress(99);
			loadingScreen.addLogEntry(String.format("🗺️ Map boundaries calculated (%dms)", boundsTime), LogLevel.SUCCESS);

			loadingScreen.updateDetail("Completing initialization...");
			metrics.startOperation("final_setup");
			gameScreenBuffer.initDrawingArea();
			gameScreenBuffer.drawGraphics(0, g, 0);
			setBounds();
			InteractiveObject.clientInstance = client;
			ObjectDef.clientInstance = client;
			EntityDef.clientInstance = client;
			metrics.endOperation("final_setup");
			long finalTime = metrics.operationTimes.get("final_setup_duration");
			loadingScreen.updateDetailProgress(100);
			loadingScreen.addLogEntry(String.format("🔧 Final setup completed (%dms)", finalTime), LogLevel.SUCCESS);

			metrics.endPhase("finalization");
			long finalizationTime = metrics.phaseTimes.get("finalization_duration");
			loadingScreen.updateProgress(LoadingPhase.FINALIZING.endProgress);
			loadingScreen.addLogEntry(String.format("🏁 Finalization phase completed (%dms)", finalizationTime), LogLevel.SUCCESS);

			loadingScreen.setPhase(LoadingPhase.COMPLETE);
			long totalLoadTime = System.currentTimeMillis() - loadStartTime;
			metrics.updateMemoryMetrics();

			loadingScreen.addLogEntry("📊 LOADING COMPLETE - COMPREHENSIVE METRICS REPORT", LogLevel.SUCCESS);
			loadingScreen.addLogEntry("============================================================", LogLevel.INFO);
			loadingScreen.addLogEntry(String.format("⏱️ Total loading time: %dms (%.2fs)", totalLoadTime, totalLoadTime / 1000.0), LogLevel.SUCCESS);

			loadingScreen.addLogEntry("🕐 Phase Breakdown:", LogLevel.INFO);
			loadingScreen.addLogEntry(String.format("  • Initialization: %dms", metrics.phaseTimes.getOrDefault("initialization_duration", 0L)), LogLevel.INFO);
			loadingScreen.addLogEntry(String.format("  • Font System: %dms", metrics.phaseTimes.getOrDefault("fonts_duration", 0L)), LogLevel.INFO);
			loadingScreen.addLogEntry(String.format("  • Asset Loading: %dms", metrics.phaseTimes.getOrDefault("assets_duration", 0L)), LogLevel.INFO);
			loadingScreen.addLogEntry(String.format("  • Data Processing: %dms", metrics.phaseTimes.getOrDefault("processing_duration", 0L)), LogLevel.INFO);
			loadingScreen.addLogEntry(String.format("  • Finalization: %dms", metrics.phaseTimes.getOrDefault("finalization_duration", 0L)), LogLevel.INFO);

			loadingScreen.addLogEntry("📈 Processing Statistics:", LogLevel.INFO);
			loadingScreen.addLogEntry(metrics.getProcessingReport(), LogLevel.INFO);

			loadingScreen.addLogEntry("💾 I/O Statistics:", LogLevel.INFO);
			loadingScreen.addLogEntry(metrics.getIOReport(), LogLevel.INFO);

			loadingScreen.addLogEntry("🧠 Memory Statistics:", LogLevel.INFO);
			loadingScreen.addLogEntry(metrics.getMemoryReport(), LogLevel.INFO);

			loadingScreen.addLogEntry("⚡ Cache Performance:", LogLevel.INFO);
			loadingScreen.addLogEntry(metrics.getCacheReport(), LogLevel.INFO);

			if (metrics.errors > 0 || metrics.warnings > 0) {
				loadingScreen.addLogEntry("⚠️ Issues Summary:", LogLevel.WARNING);
				loadingScreen.addLogEntry(String.format("  • Errors: %d, Warnings: %d", metrics.errors, metrics.warnings), LogLevel.WARNING);
			} else {
				loadingScreen.addLogEntry("✅ No errors or warnings encountered!", LogLevel.SUCCESS);
			}

			loadingScreen.addLogEntry("============================================================", LogLevel.INFO);
			loadingScreen.updateProgress(100);
			loadingScreen.updateDetail("Game ready!");
			if (loadingScreen != null) {
				loadingScreen.closeLoader();
				loadingScreen = null;
			}
			com.bestbudz.engine.ClientLauncher.onGameLoadingComplete();

		} catch (Exception exception) {
			exception.printStackTrace();
			metrics.errors++;
			long failTime = System.currentTimeMillis() - loadStartTime;
			if (loadingScreen != null) {
				loadingScreen.reportError("Loading failed: " + exception.getMessage());
				loadingScreen.updateStatus("Loading Failed");
				loadingScreen.addLogEntry(String.format("💥 LOADING FAILED after %dms", failTime), LogLevel.ERROR);
				loadingScreen.addLogEntry("📊 Partial metrics at failure:", LogLevel.INFO);
				loadingScreen.addLogEntry(metrics.getIOReport(), LogLevel.INFO);
				loadingScreen.addLogEntry(metrics.getMemoryReport(), LogLevel.INFO);
				loadingScreen.addLogEntry(String.format("Errors: %d, Warnings: %d", metrics.errors, metrics.warnings), LogLevel.ERROR);
			}
			Signlink.reporterror("loaderror " + inputBuffer + " " + minimapState);
			loadingError = true;
		} finally {
			isLoading = false;
			if (System.currentTimeMillis() % 10000 < 1000) {
				OptimizedSpriteFactory.cleanupCache();
			}
		}
	}

	private static void loadConfigurationsWithMetrics() {
		String[] configTypes = {"Animation", "ObjectDef", "Floor", "OverlayFloor", "ItemDef", "EntityDef", "IdentityKit", "SpotAnim", "Varp", "VarBit"};
		int[] progressSteps = {60, 65, 70, 75, 80, 85, 90, 93, 95, 97};

		for (int i = 0; i < configTypes.length; i++) {
			String configType = configTypes[i];
			metrics.startOperation("config_" + configType.toLowerCase());

			try {
				switch (configType) {
					case "Animation":
						Animation.unpackConfig();
						break;
					case "ObjectDef":
						ObjectDef.unpackConfig();
						break;
					case "Floor":
						Floor.unpackConfig();
						break;
					case "OverlayFloor":
						OverlayFloor.unpackConfig();
						break;
					case "ItemDef":
						ItemDef.unpackConfig();
						break;
					case "EntityDef":
						EntityDef.unpackConfig();
						break;
					case "IdentityKit":
						IdentityKit.unpackConfig();
						break;
					case "SpotAnim":
						SpotAnim.loadConfigurations();
						break;
					case "Varp":
						Varp.unpackConfig();
						break;
					case "VarBit":
						VarBit.unpackConfig();
						break;
				}

				metrics.endOperation("config_" + configType.toLowerCase());
				metrics.configsLoaded++;
				long configTime = metrics.operationTimes.get("config_" + configType.toLowerCase() + "_duration");
				loadingScreen.updateDetailProgress(progressSteps[i]);
				loadingScreen.addLogEntry(String.format("⚙️ %s configuration loaded (%dms)", configType, configTime), LogLevel.SUCCESS);

			} catch (Exception e) {
				metrics.errors++;
				loadingScreen.addLogEntry(String.format("❌ Failed to load %s config: %s", configType, e.getMessage()), LogLevel.ERROR);
			}
		}

		ItemDef.isMembers = isMembers;
		loadingScreen.addLogEntry("🎫 Members flag configured", LogLevel.INFO);
	}

	private static Sprite createSpriteOrThrow(String name, int index) {
		return OptimizedSpriteFactory.createSprite(name, index);
	}


	private static void loadSpriteArrayWithProgress(Sprite[] array, String name, int progressValue) {
		loadingScreen.updateDetail("Loading " + name + " sprites...");
		metrics.startOperation("sprite_array_" + name);

		int initialSpritesLoaded = metrics.spritesLoaded;
		loadSpriteArraySafe(array, name);
		int spritesLoadedThisArray = metrics.spritesLoaded - initialSpritesLoaded;

		metrics.endOperation("sprite_array_" + name);
		long arrayTime = metrics.operationTimes.get("sprite_array_" + name + "_duration");
		loadingScreen.updateDetailProgress(progressValue);
		loadingScreen.addLogEntry(String.format("🎨 %s array completed (%dms, %d sprites)",
			name, arrayTime, spritesLoadedThisArray), LogLevel.SUCCESS);
	}

	private static void loadSpriteArraySafe(Sprite[] array, String name) {
		if (array == null) {
			loadingScreen.addLogEntry("Skipping null sprite array: " + name, LogLevel.WARNING);
			metrics.warnings++;
			return;
		}

		int loadedCount = 0;
		int totalCount = array.length;
		int totalPixels = 0;
		long startTime = System.currentTimeMillis();

		for (int index = 0; index < array.length; index++) {
			try {
				Sprite sprite = OptimizedSpriteFactory.createSprite(name, index);
				if (sprite != null && sprite.myPixels != null && sprite.myWidth > 0 && sprite.myHeight > 0) {
					array[index] = sprite;
					loadedCount++;
					totalPixels += sprite.myPixels.length;
					metrics.spritesProcessed++;

					if (totalCount > 10) {
						int progress = (int) ((double) loadedCount / totalCount * 100);
						loadingScreen.updateDetailProgress(progress);
					}
				} else {
					loadingScreen.addLogEntry(String.format("⚠️ Corrupted sprite: %s[%d] - stopping array load", name, index), LogLevel.WARNING);
					metrics.warnings++;
					break;
				}
			} catch (Exception e) {
				loadingScreen.addLogEntry(String.format("❌ Failed to load sprite %s[%d] - %s", name, index, e.getMessage()), LogLevel.ERROR);
				metrics.errors++;
				break;
			}
		}

		long duration = System.currentTimeMillis() - startTime;
		double avgTimePerSprite = loadedCount > 0 ? (double) duration / loadedCount : 0;
		double totalSizeMB = totalPixels * 4.0 / (1024.0 * 1024.0);

		if (loadedCount == totalCount) {
			loadingScreen.addLogEntry(String.format("✅ %s: %d/%d sprites (%.1fMB, avg %.1fms/sprite)",
				name, loadedCount, totalCount, totalSizeMB, avgTimePerSprite), LogLevel.SUCCESS);
		} else {
			loadingScreen.addLogEntry(String.format("⚠️ %s: %d/%d sprites (%.1fMB, avg %.1fms/sprite) - PARTIAL",
				name, loadedCount, totalCount, totalSizeMB, avgTimePerSprite), LogLevel.WARNING);
		}
	}

	private static void performSafeBulkCopies() {
		loadingScreen.updateDetail("Processing currency sprites...");
		metrics.startOperation("bulk_copy_currencies");

		if (currencies > 0 && cacheSprite != null && currencyImage != null && cacheSprite.length > 407) {
			int copyLength = Math.min(currencies, Math.min(currencyImage.length, cacheSprite.length - 407));
			if (copyLength > 0) {
				boolean hasValidSprites = true;
				int validatedSprites = 0;

				for (int i = 0; i < copyLength; i++) {
					if (cacheSprite[407 + i] == null) {
						hasValidSprites = false;
						break;
					}
					validatedSprites++;
				}

				if (hasValidSprites) {
					System.arraycopy(cacheSprite, 407, currencyImage, 0, copyLength);
					metrics.spritesProcessed += copyLength;
					metrics.endOperation("bulk_copy_currencies");
					long currencyTime = metrics.operationTimes.get("bulk_copy_currencies_duration");
					loadingScreen.addLogEntry(String.format("✅ Currency sprites: %d copied (%dms, %d validated)",
						copyLength, currencyTime, validatedSprites), LogLevel.SUCCESS);
				} else {
					loadingScreen.addLogEntry(String.format("⚠️ Currency sprites: source data corrupted at sprite %d", validatedSprites), LogLevel.WARNING);
					metrics.warnings++;
				}
			}
		} else {
			loadingScreen.addLogEntry(String.format("⚠️ Currency sprites: insufficient data (currencies=%d, cacheLen=%d)",
				currencies, cacheSprite != null ? cacheSprite.length : 0), LogLevel.WARNING);
			metrics.warnings++;
		}

		loadingScreen.updateDetailProgress(33);
		loadingScreen.updateDetail("Processing hit mark sprites...");
		metrics.startOperation("bulk_copy_hitmarks");

		if (cacheSprite != null && hitMark != null && cacheSprite.length > 484) {
			int copyLength = Math.min(9, Math.min(hitMark.length, cacheSprite.length - 475));
			if (copyLength > 0 && validateSpriteRange(cacheSprite, 475, copyLength)) {
				System.arraycopy(cacheSprite, 475, hitMark, 0, copyLength);
				metrics.spritesProcessed += copyLength;
				metrics.endOperation("bulk_copy_hitmarks");
				long hitmarkTime = metrics.operationTimes.get("bulk_copy_hitmarks_duration");
				loadingScreen.addLogEntry(String.format("✅ Hit mark sprites: %d copied (%dms)", copyLength, hitmarkTime), LogLevel.SUCCESS);
			} else {
				loadingScreen.addLogEntry("⚠️ Hit mark sprites: corrupted source data", LogLevel.WARNING);
				metrics.warnings++;
			}
		}

		loadingScreen.updateDetailProgress(66);
		loadingScreen.updateDetail("Processing hit icon sprites...");
		metrics.startOperation("bulk_copy_hiticons");

		if (cacheSprite != null && hitIcon != null && cacheSprite.length > 489) {
			int copyLength = Math.min(6, Math.min(hitIcon.length, cacheSprite.length - 484));
			if (copyLength > 0 && validateSpriteRange(cacheSprite, 484, copyLength)) {
				System.arraycopy(cacheSprite, 484, hitIcon, 0, copyLength);
				metrics.spritesProcessed += copyLength;
				metrics.endOperation("bulk_copy_hiticons");
				long hiticonTime = metrics.operationTimes.get("bulk_copy_hiticons_duration");
				loadingScreen.addLogEntry(String.format("✅ Hit icon sprites: %d copied (%dms)", copyLength, hiticonTime), LogLevel.SUCCESS);
			} else {
				loadingScreen.addLogEntry("⚠️ Hit icon sprites: corrupted source data", LogLevel.WARNING);
				metrics.warnings++;
			}
		}

		loadingScreen.updateDetailProgress(100);
		loadingScreen.addLogEntry("📦 Bulk copy operations completed", LogLevel.INFO);
	}

	private static Sprite createSpriteOrNull(String name, int index) {
		try {
			long startTime = System.nanoTime();
			Sprite sprite = OptimizedSpriteFactory.createSprite(name, index);

			if (sprite == null || sprite.myPixels == null || sprite.myWidth <= 0 || sprite.myHeight <= 0) {
				metrics.warnings++;
				addConsoleMessage("Corrupted sprite detected: " + name + "[" + index + "] - skipping");
				return null;
			}

			long duration = (System.nanoTime() - startTime) / 1000000;
			if (duration > 10) {
				loadingScreen.addLogEntry(String.format("🐌 Slow sprite load: %s[%d] (%.1fms)",
					name, index, duration / 1000.0), LogLevel.WARNING);
			}

			return sprite;
		} catch (Exception e) {
			metrics.errors++;
			addConsoleMessage("Failed to getPooledStream sprite " + name + "[" + index + "]: " + e.getMessage());
			return null;
		}
	}

	private static void loadOptionalSpritesBatch() {
		metrics.startOperation("optional_sprites");

		loadingScreen.updateDetail("Loading map scenes...");
		int mapSceneCount = loadBackgroundArrayWithProgress(mapScenes, "mapscene", 100, 20);
		metrics.backgroundsLoaded += mapSceneCount;

		loadingScreen.updateDetail("Loading map functions...");
		int mapFunctionCount = loadSpriteArrayWithProgress(mapFunctions, "mapfunction", 100, 40);

		loadingScreen.updateDetail("Loading hit marks...");
		int hitMarkCount = loadSpriteArrayWithProgress(hitMarks, "hitmarks", 20, 60);

		loadingScreen.updateDetail("Loading hint icons...");
		int hintIconCount = loadSpriteArrayWithProgress(headIconsHint, "headicons_hint", 6, 80);

		loadingScreen.updateDetail("Loading prayer icons...");
		int prayerIconCount = loadSpriteArrayWithProgress(headIcons, "headicons_prayer", 8, 90);

		loadingScreen.updateDetail("Loading skull icons...");
		int skullIconCount = loadSpriteArrayWithProgress(skullIcons, "headicons_pk", 3, 100);

		metrics.endOperation("optional_sprites");
		long optionalTime = metrics.operationTimes.get("optional_sprites_duration");
		int totalLoaded = mapSceneCount + mapFunctionCount + hitMarkCount + hintIconCount + prayerIconCount + skullIconCount;
		loadingScreen.addLogEntry(String.format("🎯 Optional sprites loaded: %d total (%dms)", totalLoaded, optionalTime), LogLevel.SUCCESS);
		loadingScreen.addLogEntry(String.format("  • Map scenes: %d, Functions: %d, Hit marks: %d", mapSceneCount, mapFunctionCount, hitMarkCount), LogLevel.INFO);
		loadingScreen.addLogEntry(String.format("  • Hint icons: %d, Prayer icons: %d, Skull icons: %d", hintIconCount, prayerIconCount, skullIconCount), LogLevel.INFO);
	}

	private static int loadSpriteArrayWithProgress(Sprite[] array, String name, int maxCount, int progressValue) {
		if (array == null || array.length == 0) {
			loadingScreen.addLogEntry("Skipping empty/null array: " + name, LogLevel.WARNING);
			metrics.warnings++;
			return 0;
		}

		int loaded = 0;
		int totalPixels = 0;
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < Math.min(maxCount, array.length); i++) {
			Sprite sprite = createSpriteOrNull(name, i);
			if (sprite != null) {
				array[i] = sprite;
				loaded++;
				totalPixels += sprite.myPixels.length;

				if (maxCount > 10) {
					int progress = (int) ((double) i / Math.min(maxCount, array.length) * progressValue);
					loadingScreen.updateDetailProgress(progress);
				}
			} else {
				break;
			}
		}

		long duration = System.currentTimeMillis() - startTime;
		double sizeMB = totalPixels * 4.0 / (1024.0 * 1024.0);
		loadingScreen.updateDetailProgress(progressValue);

		if (loaded > 0) {
			loadingScreen.addLogEntry(String.format("📊 %s: %d sprites loaded (%.1fMB, %.1fms avg)",
				name, loaded, sizeMB, (double) duration / loaded), LogLevel.INFO);
		}

		return loaded;
	}

	private static int loadBackgroundArrayWithProgress(Background[] array, String name, int maxCount, int progressValue) {
		if (array == null || array.length == 0) {
			loadingScreen.addLogEntry("Skipping empty/null background array: " + name, LogLevel.WARNING);
			metrics.warnings++;
			return 0;
		}

		int loaded = 0;
		int totalBytes = 0;
		long startTime = System.currentTimeMillis();

		for (int i = 0; i < Math.min(maxCount, array.length); i++) {
			try {
				Background bg = Background.loadFromExtracted(name, i);
				if (bg != null && bg.textureData != null) {
					array[i] = bg;
					loaded++;
					totalBytes += bg.textureData.length;

					if (maxCount > 10) {
						int progress = (int) ((double) i / Math.min(maxCount, array.length) * progressValue);
						loadingScreen.updateDetailProgress(progress);
					}
				} else {
					break;
				}
			} catch (Exception e) {
				loadingScreen.addLogEntry(String.format("❌ Failed to load background %s[%d]: %s", name, i, e.getMessage()), LogLevel.ERROR);
				metrics.errors++;
				break;
			}
		}

		long duration = System.currentTimeMillis() - startTime;
		double sizeMB = totalBytes / (1024.0 * 1024.0);
		loadingScreen.updateDetailProgress(progressValue);

		if (loaded > 0) {
			loadingScreen.addLogEntry(String.format("🖼️ %s backgrounds: %d loaded (%.1fMB, %.1fms avg)",
				name, loaded, sizeMB, (double) duration / loaded), LogLevel.INFO);
		}

		return loaded;
	}

	private static boolean validateSpriteRange(Sprite[] sprites, int start, int length) {
		for (int i = start; i < start + length && i < sprites.length; i++) {
			if (sprites[i] == null) {
				return false;
			}
		}
		return true;
	}

	private static void applyColorAdjustmentsOptimized() {
		loadingScreen.updateDetail("Calculating color adjustments...");
		metrics.startOperation("color_calculation");

		int i5 = (int) (Math.random() * 21D) - 10;
		int j5 = (int) (Math.random() * 21D) - 10;
		int k5 = (int) (Math.random() * 21D) - 10;
		int l5 = (int) (Math.random() * 41D) - 20;
		int colorAdjust1 = i5 + l5;
		int colorAdjust2 = j5 + l5;
		int colorAdjust3 = k5 + l5;

		metrics.endOperation("color_calculation");
		long calcTime = metrics.operationTimes.get("color_calculation_duration");
		loadingScreen.addLogEntry(String.format("🎨 Color adjustments calculated: R%+d, G%+d, B%+d (%dms)",
			colorAdjust1, colorAdjust2, colorAdjust3, calcTime), LogLevel.INFO);

		int functionsProcessed = 0;
		int scenesProcessed = 0;
		int totalPixelsProcessed = 0;
		long startTime = System.currentTimeMillis();

		loadingScreen.updateDetail("Applying color adjustments...");
		metrics.startOperation("color_application");

		for (int i6 = 0; i6 < 100; i6++) {
			if (mapFunctions[i6] != null && mapFunctions[i6].myPixels != null) {
				mapFunctions[i6].adjustBrightness(colorAdjust1, colorAdjust2, colorAdjust3);
				functionsProcessed++;
				totalPixelsProcessed += mapFunctions[i6].myPixels.length;
			}
			if (mapScenes[i6] != null && mapScenes[i6].textureData != null) {
				mapScenes[i6].method360(colorAdjust1, colorAdjust2, colorAdjust3);
				scenesProcessed++;
				totalPixelsProcessed += mapScenes[i6].textureData.length;
			}

			if (i6 % 10 == 0) {
				loadingScreen.updateDetailProgress(i6);
			}
		}

		metrics.endOperation("color_application");
		long applyTime = metrics.operationTimes.get("color_application_duration");
		double pixelsMB = totalPixelsProcessed / (1024.0 * 1024.0);

		loadingScreen.addLogEntry(String.format("🎨 Color adjustments applied (%dms):", applyTime), LogLevel.SUCCESS);
		loadingScreen.addLogEntry(String.format("  • Functions: %d processed, Scenes: %d processed", functionsProcessed, scenesProcessed), LogLevel.INFO);
		loadingScreen.addLogEntry(String.format("  • Total pixels processed: %.1fMB", pixelsMB), LogLevel.INFO);
	}

	private static void calculateMapBoundsOptimized() {
		if (mapBack == null) {
			loadingScreen.addLogEntry("Warning: mapBack is null, skipping map bounds calculation", LogLevel.WARNING);
			metrics.warnings++;
			return;
		}

		loadingScreen.updateDetail("Calculating map boundaries...");
		metrics.startOperation("map_bounds_calculation");

		byte[] mapData = mapBack.textureData;
		int mapWidth = mapBack.backgroundWidth;
		int processedBytes = 0;

		loadingScreen.addLogEntry(String.format("🗺️ Map data: %dx%d grid, %d bytes",
			mapWidth, mapData.length / mapWidth, mapData.length), LogLevel.INFO);

		metrics.startOperation("horizontal_bounds");
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
			skillExperienceTable[j6] = k6;
			questStates[j6] = i7 - k6;
			processedBytes += 34;

			if (j6 % 8 == 0) {
				loadingScreen.updateDetailProgress((int) ((double) j6 / 33 * 50));
			}
		}
		metrics.endOperation("horizontal_bounds");
		long hTime = metrics.operationTimes.get("horizontal_bounds_duration");

		metrics.startOperation("vertical_bounds");
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
			professionGrades[l6 - 1] = j7 - 24;
			friendsListIds[l6 - 1] = l7 - j7;
			processedBytes += 153;

			if (l6 % 15 == 0) {
				loadingScreen.updateDetailProgress(50 + (int) ((double) l6 / 152 * 50));
			}
		}
		metrics.endOperation("vertical_bounds");
		long vTime = metrics.operationTimes.get("vertical_bounds_duration");

		metrics.endOperation("map_bounds_calculation");
		long totalTime = metrics.operationTimes.get("map_bounds_calculation_duration");

		loadingScreen.addLogEntry(String.format("🗺️ Map boundaries calculated (%dms total):", totalTime), LogLevel.SUCCESS);
		loadingScreen.addLogEntry(String.format("  • Horizontal bounds: %dms, Vertical bounds: %dms", hTime, vTime), LogLevel.INFO);
		loadingScreen.addLogEntry(String.format("  • Processed %d bytes of map data", processedBytes), LogLevel.INFO);
	}

	public static boolean isCurrentlyLoading() {
		return isLoading;
	}

	public static LoadingVisual getLoadingScreen() {
		return loadingScreen;
	}

	public static void updateLoadingStatus(String status) {
		if (loadingScreen != null) {
			loadingScreen.updateStatus(status);
		}
	}

	public static void addLoadingLogEntry(String message, LoadingEnums.LogLevel level) {
		if (loadingScreen != null) {
			loadingScreen.addLogEntry(message, level);
		}
	}

	public static void cleanupCaches() {
		int beforeSize = spriteCache.size();
		OptimizedSpriteFactory.cleanupCache();
		int afterSize = spriteCache.size();
		int cleaned = beforeSize - afterSize;

		if (loadingScreen != null && cleaned > 0) {
			loadingScreen.addLogEntry(String.format("🧹 Cache cleanup: %d entries removed", cleaned), LogLevel.INFO);
		}
	}

	public static String getCacheStats() {
		int cacheSize = spriteCache.size();
		int activeRefs = (int) spriteCache.values().stream()
			.mapToLong(ref -> ref.get() != null ? 1 : 0)
			.sum();
		double hitRate = metrics.cacheHits + metrics.cacheMisses > 0 ?
			(metrics.cacheHits * 100.0 / (metrics.cacheHits + metrics.cacheMisses)) : 0;

		return String.format("Cache: %d entries (%d active), %.1f%% hit rate, %d evictions",
			cacheSize, activeRefs, hitRate, metrics.cacheEvictions);
	}

	public static String getLoadingMetricsReport() {
		StringBuilder report = new StringBuilder();
		report.append("=== GAMELOADER METRICS REPORT ===\n");
		report.append(metrics.getProcessingReport()).append("\n");
		report.append(metrics.getIOReport()).append("\n");
		report.append(metrics.getMemoryReport()).append("\n");
		report.append(metrics.getCacheReport()).append("\n");

		if (metrics.errors > 0 || metrics.warnings > 0) {
			report.append(String.format("Issues: %d errors, %d warnings\n", metrics.errors, metrics.warnings));
		}

		report.append("Phase Timings:\n");
		for (Map.Entry<String, Long> entry : metrics.phaseTimes.entrySet()) {
			if (entry.getKey().endsWith("_duration")) {
				String phaseName = entry.getKey().replace("_duration", "");
				report.append(String.format("  %s: %dms\n", phaseName, entry.getValue()));
			}
		}

		return report.toString();
	}

	public static String getPerformanceMetrics() {
		metrics.updateMemoryMetrics();
		Runtime runtime = Runtime.getRuntime();
		long totalMemory = runtime.totalMemory();
		long freeMemory = runtime.freeMemory();
		long maxMemory = runtime.maxMemory();

		double totalMB = totalMemory / (1024.0 * 1024.0);
		double freeMB = freeMemory / (1024.0 * 1024.0);
		double maxMB = maxMemory / (1024.0 * 1024.0);
		double usedMB = totalMB - freeMB;
		double usagePercent = (usedMB / maxMB) * 100;

		return String.format("Memory: %.1f/%.1fMB (%.1f%% used), Cache: %d entries",
			usedMB, maxMB, usagePercent, spriteCache.size());
	}

	public static void reportLoadingError(String operation, Exception e) {
		metrics.errors++;
		if (loadingScreen != null) {
			loadingScreen.addLogEntry(String.format("❌ Error in %s: %s", operation, e.getMessage()), LogLevel.ERROR);
			loadingScreen.addLogEntry(String.format("📊 Current metrics: %s", metrics.getProcessingReport()), LogLevel.INFO);
		}
	}

	public static void reportLoadingWarning(String operation, String warning) {
		metrics.warnings++;
		if (loadingScreen != null) {
			loadingScreen.addLogEntry(String.format("⚠️ Warning in %s: %s", operation, warning), LogLevel.WARNING);
		}
	}

	public static void startCustomOperation(String operationName) {
		metrics.startOperation("custom_" + operationName);
	}

	public static void endCustomOperation(String operationName) {
		metrics.endOperation("custom_" + operationName);
		Long duration = metrics.operationTimes.get("custom_" + operationName + "_duration");
		if (duration != null && loadingScreen != null) {
			loadingScreen.addLogEntry(String.format("⏱️ %s completed in %dms", operationName, duration), LogLevel.INFO);
		}
	}

	public static void updateAndReportMemoryUsage(String context) {
		metrics.updateMemoryMetrics();
		if (loadingScreen != null) {
			loadingScreen.addLogEntry(String.format("💾 %s - %s", context, metrics.getMemoryReport()), LogLevel.INFO);
		}
	}

	public static void updateLoadingScreenMetrics() {
		if (loadingScreen != null) {
			loadingScreen.updateItemCount("Sprites", metrics.spritesLoaded);
			loadingScreen.updateItemCount("Configs", metrics.configsLoaded);
			loadingScreen.updateItemCount("Models", metrics.modelsProcessed);
			loadingScreen.updateItemCount("Textures", metrics.texturesLoaded);
			loadingScreen.updateItemCount("Interfaces", metrics.interfacesLoaded);
			loadingScreen.updateItemCount("Backgrounds", metrics.backgroundsLoaded);

			loadingScreen.updateBytesProcessed(metrics.totalBytesRead);
			loadingScreen.updateFilesProcessed(metrics.totalFilesRead);
			loadingScreen.updateCacheStats(metrics.cacheHits, metrics.cacheMisses);
		}
	}

	public static Sprite loadSpriteWithMetrics(String name, int index) {
		long startTime = System.nanoTime();
		try {
			Sprite sprite = OptimizedSpriteFactory.createSprite(name, index);
			long duration = (System.nanoTime() - startTime) / 1000000;

			if (loadingScreen != null && duration > 5) {
				loadingScreen.addLogEntry(String.format("🎨 Sprite %s[%d] loaded (%dms)", name, index, duration), LogLevel.INFO);
			}

			updateLoadingScreenMetrics();
			return sprite;
		} catch (Exception e) {
			long duration = (System.nanoTime() - startTime) / 1000000;
			reportLoadingError("sprite loading: " + name + "[" + index + "]", e);
			throw e;
		}
	}

	public static void loadConfigWithMetrics(String configName, Runnable configLoader) {
		long startTime = System.currentTimeMillis();
		try {
			configLoader.run();
			long duration = System.currentTimeMillis() - startTime;
			metrics.configsLoaded++;

			if (loadingScreen != null) {
				loadingScreen.addLogEntry(String.format("⚙️ %s config loaded (%dms)", configName, duration), LogLevel.SUCCESS);
				updateLoadingScreenMetrics();
			}
		} catch (Exception e) {
			long duration = System.currentTimeMillis() - startTime;
			reportLoadingError("config loading: " + configName, e);
			throw e;
		}
	}

	public void raiseWelcomeScreen() {
		welcomeScreenRaised = true;
		if (loadingScreen != null) {
			loadingScreen.addLogEntry("🎊 Welcome screen raised", LogLevel.INFO);
		}
	}
}
