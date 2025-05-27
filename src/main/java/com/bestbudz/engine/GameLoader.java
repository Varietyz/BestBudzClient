package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.config.ClientConstants;
import com.bestbudz.data.AccountManager;
import com.bestbudz.data.ItemDef;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.entity.IdentityKit;
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
import com.bestbudz.rendering.Rasterizer;
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
import com.bestbudz.world.WorldController;
import java.awt.Graphics2D;

public class GameLoader extends Client{

	static void startUp(Graphics2D g, Client client)
	{
		// Load sprites early as many systems depend on them
		AccountManager.loadAccount();
		SpriteLoader.loadSprites();
		cacheSprite = SpriteLoader.sprites;
		loginRenderer = new LoginRenderer(client);
		// Initialize decompressors first
		if (Signlink.cache_dat != null)
		{
			for (int i = 0; i < 6; i++)
				decompressors[i] = new Decompressor(Signlink.cache_dat, Signlink.cache_idx[i], i + 1);
		}

		try
		{
			// Load title stream first as fonts depend on it
			titleStreamLoader = streamLoaderForName(1, "title screen", "title", expectedCRCs[1], 25,g);

			// Initialize fonts immediately after title stream loads
			smallText = new TextDrawingArea(false, "p11_full", titleStreamLoader);
			regularText = new TextDrawingArea(false, "p12_full", titleStreamLoader);
			boldText = new TextDrawingArea(false, "b12_full", titleStreamLoader);
			newSmallFont = new RSFont(false, "p11_full", titleStreamLoader);
			newRegularFont = new RSFont(false, "p12_full", titleStreamLoader);
			newBoldFont = new RSFont(false, "b12_full", titleStreamLoader);
			newFancyFont = new RSFont(true, "q8_full", titleStreamLoader);
			TextDrawingArea aTextDrawingArea_1273 = new TextDrawingArea(true, "q8_full", titleStreamLoader);

			// Load remaining streams - prioritize media stream as it's used most
			StreamLoader streamLoader_2 = streamLoaderForName(4, "2d graphics", "media", expectedCRCs[4], 40,g);
			StreamLoader streamLoader = streamLoaderForName(2, "config", "config", expectedCRCs[2], 30,g );
			StreamLoader streamLoader_1 = streamLoaderForName(3, "interface", "interface", expectedCRCs[3], 35,g );
			StreamLoader streamLoader_3 = streamLoaderForName(6, "textures", "textures", expectedCRCs[6], 45,g);
			streamLoaderForName(8, "sound effects", "sounds", expectedCRCs[8], 55,g);

			// Initialize world systems early
			byteGroundArray = new byte[4][208][208];
			intGroundArray = new int[4][209][209];
			worldController = new WorldController(intGroundArray);
			for (int j = 0; j < 4; j++)
				aClass11Array1230[j] = new Class11();

			minimapImage = new Sprite(512, 512);
			StreamLoader streamLoader_6 = streamLoaderForName(5, "update list", "versionlist", expectedCRCs[5], 60,g);
			onDemandFetcher = new OnDemandFetcher();
			onDemandFetcher.start(streamLoader_6, client);
			SequenceFrame.animationlist = new SequenceFrame[2500][0];
			Model.method459(onDemandFetcher.getModelCount(), onDemandFetcher);

			// Pre-allocate clan icons array
			Sprite[] clanIcons = new Sprite[10];

			// Load critical sprites first (needed for UI) - optimized with early termination
			loadSpriteArrayOptimized(newHitMarks, streamLoader_2, "newhitmarks");
			loadSpriteArrayOptimized(channelButtons, streamLoader_2, "cbuttons");
			loadSpriteArrayOptimized(fixedGameComponents, streamLoader_2, "fixed");
			loadSpriteArrayOptimized(skillIcons, streamLoader_2, "skillicons");
			loadSpriteArrayOptimized(gameComponents, streamLoader_2, "fullscreen");

			// Load orb components efficiently
			loadSpriteArrayOptimized(orbComponents, streamLoader_2, "orbs3");
			loadSpriteArrayOptimized(orbComponents2, streamLoader_2, "orbs4");
			loadSpriteArrayOptimized(orbComponents3, streamLoader_2, "orbs5");
			loadSpriteArrayOptimized(redStones, streamLoader_2, "redstone1");
			loadSpriteArrayOptimized(hpBars, streamLoader_2, "hpbars");
			loadSpriteArrayOptimized(clanIcons, streamLoader_2, "clanicons");

			// Efficient bulk copy operations
			if (currencies >= 0) System.arraycopy(cacheSprite, 407, currencyImage, 0, currencies);
			System.arraycopy(cacheSprite, 475, hitMark, 0, 9);
			System.arraycopy(cacheSprite, 484, hitIcon, 0, 6);

			// Unpack font images once - do this early as UI depends on it
			newSmallFont.unpackImages(modIcons, clanIcons);
			newRegularFont.unpackImages(modIcons, clanIcons);
			newBoldFont.unpackImages(modIcons, clanIcons);
			newFancyFont.unpackImages(modIcons, clanIcons);

			// Load individual sprites
			multiOverlay = new Sprite(streamLoader_2, "overlay_multiway", 0);
			mapBack = new Background(streamLoader_2, "mapback", 0);
			StatusOrbs.compass = new Sprite(streamLoader_2, "compass", 0);
			mapFlag = new Sprite(streamLoader_2, "mapmarker", 0);
			mapMarker = new Sprite(streamLoader_2, "mapmarker", 1);

			// Load side icons efficiently
			for (int j3 = 0; j3 <= 16; j3++)
			{
				sideIcons[j3] = new Sprite(streamLoader_2, "sideicons", j3);
			}

			// Load crosses
			for (int k4 = 0; k4 < 8; k4++)
				crosses[k4] = new Sprite(streamLoader_2, "cross", k4);

			// Load map dots
			mapDotItem = new Sprite(streamLoader_2, "mapdots", 0);
			mapDotNPC = new Sprite(streamLoader_2, "mapdots", 1);
			mapDotStoner = new Sprite(streamLoader_2, "mapdots", 2);
			mapDotStoner = new Sprite(streamLoader_2, "mapdots", 3);
			mapDotTeam = new Sprite(streamLoader_2, "mapdots", 4);
			mapDotClan = new Sprite(streamLoader_2, "mapdots", 5);
			scrollBar1 = new Sprite(streamLoader_2, "scrollbar", 0);
			scrollBar2 = new Sprite(streamLoader_2, "scrollbar", 1);

			// Load optional sprites efficiently with batching
			loadOptionalSpritesBatch(streamLoader_2);

			// Load screen frames
			Sprite sprite = new Sprite(streamLoader_2, "screenframe", 0);
			leftFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.method346(0, 0);
			sprite = new Sprite(streamLoader_2, "screenframe", 1);
			topFrame = new ImageProducer(sprite.myWidth, sprite.myHeight);
			sprite.method346(0, 0);

			// Apply color adjustments efficiently with cached calculations
			applyColorAdjustmentsOptimized();

			// Initialize rendering systems
			Rasterizer.method368(streamLoader_3);
			Rasterizer.generateColorPalette(0.80000000000000004D);
			Rasterizer.method367();

			// Load configurations efficiently - group related configs
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

			TextDrawingArea[] aclass30_sub2_sub1_sub4s = {smallText, regularText, boldText, aTextDrawingArea_1273};
			RSInterface.unpack(streamLoader_1, aclass30_sub2_sub1_sub4s, streamLoader_2);

			// Optimize map bounds calculation with efficient array access
			calculateMapBoundsOptimized();

			aRSImageProducer_1109.initDrawingArea();
			aRSImageProducer_1109.drawGraphics(0, g,0);
			setBounds();
			Animable_Sub5.clientInstance = client;
			ObjectDef.clientInstance = client;
			EntityDef.clientInstance = client;
			return;
		}
		catch (Exception exception)
		{
			exception.printStackTrace();
			Signlink.reporterror("loaderror " + aString1049 + " " + anInt1079);
		}
		loadingError = true;
	}

	// Highly optimized sprite loading with minimal exception overhead and early termination
	private static void loadSpriteArrayOptimized(Sprite[] array, StreamLoader loader, String name) {
		for (int index = 0; index < array.length; index++) {
			try {
				array[index] = new Sprite(loader, name, index);
			} catch (Exception e) {
				// Early termination saves significant time for missing sprites
				break;
			}
		}
	}

	// Batch load all optional sprites to minimize method call overhead
	private static void loadOptionalSpritesBatch(StreamLoader streamLoader_2) {
		// Load map scenes with early termination
		for (int k3 = 0; k3 < 100; k3++) {
			try {
				mapScenes[k3] = new Background(streamLoader_2, "mapscene", k3);
			} catch (Exception e) {
				break;
			}
		}

		// Load map functions with early termination
		for (int l3 = 0; l3 < 100; l3++) {
			try {
				mapFunctions[l3] = new Sprite(streamLoader_2, "mapfunction", l3);
			} catch (Exception e) {
				break;
			}
		}

		// Load hit marks with early termination
		for (int i4 = 0; i4 < 20; i4++) {
			try {
				hitMarks[i4] = new Sprite(streamLoader_2, "hitmarks", i4);
			} catch (Exception e) {
				break;
			}
		}

		// Load head icons hint with early termination
		for (int h1 = 0; h1 < 6; h1++) {
			try {
				headIconsHint[h1] = new Sprite(streamLoader_2, "headicons_hint", h1);
			} catch (Exception e) {
				break;
			}
		}

		// Load head icons prayer and pk with early termination
		for (int j4 = 0; j4 < 8; j4++) {
			try {
				headIcons[j4] = new Sprite(streamLoader_2, "headicons_prayer", j4);
			} catch (Exception e) {
				break;
			}
		}
		for (int j45 = 0; j45 < 3; j45++) {
			try {
				skullIcons[j45] = new Sprite(streamLoader_2, "headicons_pk", j45);
			} catch (Exception e) {
				break;
			}
		}

		// Load mod icons with early termination
		for (int l4 = 0; l4 < ClientConstants.ICON_AMOUNT; l4++) {
			try {
				modIcons[l4] = new Sprite(streamLoader_2, "mod_icons", l4);
			} catch (Exception e) {
				break;
			}
		}
	}

	// Optimized color adjustments with pre-calculated values and efficient loops
	private static void applyColorAdjustmentsOptimized() {
		// Pre-calculate color adjustments once
		int i5 = (int) (Math.random() * 21D) - 10;
		int j5 = (int) (Math.random() * 21D) - 10;
		int k5 = (int) (Math.random() * 21D) - 10;
		int l5 = (int) (Math.random() * 41D) - 20;
		int colorAdjust1 = i5 + l5;
		int colorAdjust2 = j5 + l5;
		int colorAdjust3 = k5 + l5;

		// Apply color adjustments efficiently with single loop
		for (int i6 = 0; i6 < 100; i6++)
		{
			if (mapFunctions[i6] != null)
				mapFunctions[i6].method344(colorAdjust1, colorAdjust2, colorAdjust3);
			if (mapScenes[i6] != null)
				mapScenes[i6].method360(colorAdjust1, colorAdjust2, colorAdjust3);
		}
	}

	// Ultra-optimized map bounds calculation with cached array references and minimal operations
	private static void calculateMapBoundsOptimized() {
		byte[] mapData = mapBack.aByteArray1450;
		int mapWidth = mapBack.anInt1452;

		// First loop - optimized with cached base offset calculation
		for (int j6 = 0; j6 < 33; j6++)
		{
			int k6 = 999;
			int i7 = 0;
			int baseOffset = j6 * mapWidth;
			for (int k7 = 0; k7 < 34; k7++)
			{
				if (mapData[k7 + baseOffset] == 0)
				{
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

		// Second loop - optimized with cached base offset calculation
		for (int l6 = 1; l6 < 153; l6++)
		{
			int j7 = 999;
			int l7 = 0;
			int baseOffset = l6 * mapWidth;
			for (int j8 = 24; j8 < 177; j8++)
			{
				if (mapData[j8 + baseOffset] == 0 && (j8 > 34 || l6 > 34))
				{
					if (j7 == 999)
					{
						j7 = j8;
					}
					continue;
				}
				if (j7 == 999)
				{
					continue;
				}
				l7 = j8;
				break;
			}
			anIntArray1052[l6 - 1] = j7 - 24;
			anIntArray1229[l6 - 1] = l7 - j7;
		}
	}

	public void raiseWelcomeScreen()
	{
		welcomeScreenRaised = true;
	}
}