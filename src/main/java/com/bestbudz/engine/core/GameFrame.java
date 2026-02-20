package com.bestbudz.engine.core;

import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.engine.core.gamerender.Camera.setCameraPos;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.engine.core.login.WelcomeScreen.clearWelcomeState;
import static com.bestbudz.engine.core.login.logout.Reset.resetImageProducers2;
import static com.bestbudz.engine.util.ClientDiagnostics.drawClientFPS;
import static com.bestbudz.entity.ParseAndUpdateEntities.parseStoners;
import static com.bestbudz.entity.ParseAndUpdateEntities.renderNPCs;
import static com.bestbudz.entity.UpdateEntities.updateEntities;
import static com.bestbudz.graphics.ClearExpiredProjectiles.clearExpiredProjectiles;
import static com.bestbudz.graphics.HeadIcon.drawHeadIcon;
import static com.bestbudz.graphics.MovingTextures.updateMovingTextures;
import static com.bestbudz.rendering.Animable.processGraphicEffects;
import static com.bestbudz.rendering.Roofing.getRoofPlane;
import static com.bestbudz.rendering.Roofing.selectRoofPlane;
import com.bestbudz.rendering.model.Model;
import static com.bestbudz.ui.DialogHandling.handleBackDialogOrChatbox;
import static com.bestbudz.ui.InterfaceManagement.drawContextualInterfaces;
import static com.bestbudz.ui.InterfaceManagement.drawMiscOverlays;
import static com.bestbudz.ui.InterfaceManagement.drawOpenInterface;
import static com.bestbudz.ui.InterfaceManagement.renderFullscreenInterface;
import static com.bestbudz.ui.InterfaceManagement.updateInterfaceAnimations;
import static com.bestbudz.ui.NotificationMessages.displayKillFeed;
import static com.bestbudz.ui.TabArea.commitTabState;
import static com.bestbudz.ui.TabArea.drawTabArea;
import static com.bestbudz.ui.handling.RightClickMenu.drawContextMenu;
import static com.bestbudz.ui.handling.input.Keyboard.console;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.interfaces.StatusOrbs.drawGameOverlays;
import static com.bestbudz.ui.interfaces.StatusOrbs.drawGameUIorbs;
import com.bestbudz.util.ColorUtility;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;
import java.awt.Graphics2D;

public class GameFrame extends Client{
	public static void renderGameFrame(Graphics2D g)
	{
		currentTick++;
		parseStoners(true);
		renderNPCs(true);
		parseStoners(false);
		renderNPCs(false);
		clearExpiredProjectiles();
		processGraphicEffects();
		if (!cutsceneActive)
		{
			int i = minCameraHeight;
			if (stonerHeight / 256 > i)
			{
				i = stonerHeight / 256;
			}
			if (cameraShakeEnabled[4] && cameraShakeMagnitude[4] + 128 > i)
			{
				i = cameraShakeMagnitude[4] + 128;
			}
			int calc = minimapRotation + cameraRotation & 0x7ff;

			int cameraViewDistance = cameraZoom + i;

			if (frameWidth >= 1024) {
				cameraViewDistance += (cameraZoom - frameHeight / 200);
			}

			setCameraPos(
				cameraViewDistance,
				i, cameraX, getTerrainHeight(plane, myStoner.y, myStoner.x) - 50, calc, cameraZ);
		}
		int j;
		if (!cutsceneActive)
			j = getRoofPlane();
		else
			j = selectRoofPlane();
		int l = xCameraPos;
		int i1 = zCameraPos;
		int j1 = yCameraPos;
		int k1 = yCameraCurve;
		int l1 = xCameraCurve;
		int k2 = Rasterizer.textureAccessCounter;
		for (int i2 = 0; i2 < 5; i2++)
			if (cameraShakeEnabled[i2])
			{
				int j2 = (int) ((Math.random() * (double) (cameraShakeAmplitude[i2] * 2 + 1) - (double) cameraShakeAmplitude[i2])
					+ Math.sin((double) cameraShakeCounters[i2] * ((double) cameraShakeSpeed[i2] / 100D))
					* (double) cameraShakeMagnitude[i2]);
				if (i2 == 0)
					xCameraPos += j2;
				if (i2 == 1)
					zCameraPos += j2;
				if (i2 == 2)
					yCameraPos += j2;
				if (i2 == 3)
					xCameraCurve = xCameraCurve + j2 & 0x7ff;
				if (i2 == 4)
				{
					yCameraCurve += j2;
					if (yCameraCurve < 128)
						yCameraCurve = 128;
					if (yCameraCurve > 383)
						yCameraCurve = 383;
				}
			}
		Model.aBoolean1684 = true;
		Model.modelCount = 0;
		Model.anInt1685 = MouseState.x;
		Model.anInt1686 = MouseState.y;
		DrawingArea.setAllPixelsToZero();
		if (SettingsConfig.enableDistanceFog)
		{
			DrawingArea.drawPixels(frameHeight, 0, 0, ColorUtility.fadingToColor, frameWidth);
		}
		GameState.safeRenderWorld(xCameraPos, yCameraPos, xCameraCurve, zCameraPos, j, yCameraCurve);
		worldController.clearObj5Cache();
		if (SettingsConfig.enableDistanceFog) {
			fogHandler.renderFog(mainGameRendering.canvasRaster, mainGameRendering.depthBuffer);
		}

		updateEntities();
		drawHeadIcon();
		updateMovingTextures(k2);

		if (SettingsConfig.showKillFeed)
		{
			displayKillFeed();
		}

		drawGameUIorbs();
		drawTabArea();

		draw3dScreen();
		if (console.openConsole)
		{
			console.drawConsole(frameWidth, 334);
		}
		mainGameRendering.drawGraphics(0, g,
			0);

		xCameraPos = l;
		zCameraPos = i1;
		yCameraPos = j1;
		yCameraCurve = k1;
		xCameraCurve = l1;

	}

	public static void draw3dScreen() {
		drawGameOverlays();
		drawContextualInterfaces();
		drawOpenInterface();
		drawContextMenu();
		drawMiscOverlays();
		if (EngineConfig.FPS_ON) drawClientFPS();
	}

	public static void drawGameScreen(Graphics2D g) {
		if (fullscreenInterfaceID != -1 && (loadingStage == 2 || gameWorldScreen != null)) {
			renderFullscreenInterface(g);
			return;
		}

		if (drawCount != 0) resetImageProducers2();
		if (welcomeScreenRaised) clearWelcomeState();
		if (invOverlayInterfaceID != -1) updateInterfaceAnimations(gameTickCounter, invOverlayInterfaceID);

		drawTabArea();
		handleBackDialogOrChatbox();
		renderChatIfInvalidated();

		if (loadingStage == 2) renderGameFrame(g);
		if (selectedTabIndex != -1) tabAreaAltered = true;
		if (tabAreaAltered) commitTabState();

		gameTickCounter = 0;
	}

}
