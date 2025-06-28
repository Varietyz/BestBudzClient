package com.bestbudz.engine.core.login.logout;

import com.bestbudz.data.items.ItemDef;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.entity.Stoner;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.world.ObjectDef;

public class Reset extends Client
{
	public static void unlinkMRUNodes()
	{
		ObjectDef.mruNodes1.unlinkAll();
		ObjectDef.mruNodes2.unlinkAll();
		EntityDef.mruNodes.unlinkAll();
		ItemDef.mruNodes2.unlinkAll();
		ItemDef.mruNodes1.unlinkAll();
		Stoner.mruNodes.unlinkAll();
		//SpotAnim.aMRUNodes_415.unlinkAll();
	}

	public static void resetImageProducers2()
	{
		if (fullscreenImageProducer != null)
			return;
		nullLoader();
		gameWorldScreen = null;
		minimapImageProducer = null;
		inventoryImageProducer = null;
		gameScreenBuffer = null;
		gameImageProducer = null;
		chatImageProducer = null;
		tabImageProducer = null;
		sidebarImageProducer = null;
		mapBackImageProducer = null;
		minimapBackImageProducer = null;
		fullscreenImageProducer = new ImageProducer(519, 165);
		overlayImageProducer = new ImageProducer(249, 168);
		DrawingArea.setAllPixelsToZero();
		fixedGameComponents[0].drawSprite(0, 0);
		gameAreaImageProducer = new ImageProducer(249, 335);
		mainGameRendering = new ImageProducer(frameWidth, frameHeight);
		DrawingArea.setAllPixelsToZero();
		loginImageProducer = new ImageProducer(249, 45);
		welcomeScreenRaised = true;
	}

	public static void resetAllImageProducers()
	{
		if (gameWorldScreen != null)
		{
			return;
		}
		fullscreenImageProducer = null;
		overlayImageProducer = null;
		gameAreaImageProducer = null;
		mainGameRendering = null;
		loginImageProducer = null;
		minimapImageProducer = null;
		inventoryImageProducer = null;
		gameScreenBuffer = null;
		gameImageProducer = null;
		chatImageProducer = null;
		tabImageProducer = null;
		sidebarImageProducer = null;
		mapBackImageProducer = null;
		minimapBackImageProducer = null;
		gameWorldScreen = new ImageProducer(canvas.getWidth(), canvas.getHeight());
		welcomeScreenRaised = true;
	}

	public static void nullLoader()
	{
		regionLoaded = false;
		loginBackground1 = null;
		loginBackground2 = null;
		aBackgroundArray1152s = null;
		configValues = null;
		regionUpdateIndices = null;
		regionUpdateX = null;
		regionUpdateY = null;
		anIntArray1190 = null;
		anIntArray1191 = null;
		skillLevels = null;
		skillExperiences = null;
		aClass30_Sub2_Sub1_Sub1_1201 = null;
		aClass30_Sub2_Sub1_Sub1_1202 = null;
	}
}
