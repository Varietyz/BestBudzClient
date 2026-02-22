package com.bestbudz.ui;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.rightClickMenu;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.BuildInterface.buildInterfaceMenu;
import static com.bestbudz.ui.DrawInterface.drawInterface;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import java.util.Set;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.PlayerProto.*;

public class TabArea extends Client
{
	public static void setTab(int id)
	{
		tabID = id;
		tabAreaAltered = true;
	}

	public static void drawSideIcons()
	{
			int[] iconId = {0, -1, -1, 3, 4, -1, 6, 15, -1, -1, -1, -1, 12, -1};
			int[] iconX = {243, -1, -1, 272, 303, -1, 337, 368, -1, -1, -1, -1, 402, 441};
			int[] iconY = {30, -1, -1, 34, 36, -1, 33, 31, -1, -1, -1, -1, 32, 27};
			cacheSprite[370].drawSprite(frameWidth - 27, frameHeight - 29);
			int maxIcons = Math.min(sideIconsTab.length, iconId.length);
			for (int i = 0; i < maxIcons; i++) {
				if (tabInterfaceIDs[sideIconsTab[i]] != -1) {
					if (iconId[i] != -1) {
						sideIcons[iconId[i]].drawSprite(frameWidth - 461 + iconX[i], frameHeight - iconY[i]);
					}
				}
			}
	}

	private static void drawRedStones()
	{
		int[] stoneX = {225, -1, -1, 193, 161, -1, 129, 97, -1, -1, -1, -1, 65, 33};
			if (tabInterfaceIDs[tabID] != -1 && tabID != 14 && showTabComponents)
			{
				redStones[4].drawSprite(frameWidth - stoneX[tabID], frameHeight - 37);
			}
	}

	public static void drawTabArea()
	{
		int tabPanelWidth = 190;
		int tabPanelHeight = 257;

		Rasterizer.scanlineOffsets = viewportPixels2;

			if (showTabComponents)
			{
				DrawingArea.drawAlphaRectangle(transparentTabArea ? 0x020202 : 0x191919, frameHeight - 300, tabPanelWidth, tabPanelHeight, transparentTabArea ? 180 : 256,
					frameWidth - 197);
			}
			for (int x = frameWidth - 225, y = frameHeight - 37, index = 0; x <= frameWidth - 30
				&& index < 14; x += 32, index++)
			{
				redStones[5].drawSprite(x, y);
			}

		if (invOverlayInterfaceID == -1)
		{
			drawRedStones();
			drawSideIcons();
		}
		if (showTabComponents)
		{

				int x = frameWidth - 197;
				int y = frameHeight - 303;

			if (invOverlayInterfaceID != -1)
			{
				drawInterface(0, x, RSInterface.interfaceCache[invOverlayInterfaceID], y);
			}
			else if (tabInterfaceIDs[tabID] != -1)
			{
				drawInterface(0, x, RSInterface.interfaceCache[tabInterfaceIDs[tabID]], y);
			}
		}
		if (menuOpen)
		{
			rightClickMenu(0, 0);
		}
		Rasterizer.scanlineOffsets = viewportPixels3;
	}

	public static void processTabClick(boolean leftClick, boolean rightClick)
	{
				if (MouseState.y >= frameHeight - 37 && MouseState.y <= frameHeight)
				{
					if (MouseState.x >= frameWidth - 225 && MouseState.x <= frameWidth - 196)
					{
						if (tabID == 0)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 0;
						tabAreaAltered = true;
					}

					if (MouseState.x >= frameWidth - 195 && MouseState.x <= frameWidth - 164)
					{
						if (tabID == 3)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 3;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 163 && MouseState.x <= frameWidth - 132)
					{
						if (tabID == 4)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 4;
						tabAreaAltered = true;
					}

					if (MouseState.x >= frameWidth - 131 && MouseState.x <= frameWidth - 100)
					{
						if (tabID == 6)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 6;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 99 && MouseState.x <= frameWidth - 68)
					{
						if (tabID == 7)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 7;
						tabAreaAltered = true;
					}

					if (MouseState.x >= frameWidth - 67 && MouseState.x <= frameWidth - 36)
					{
						if (tabID == 12)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 12;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 32 && MouseState.x <= frameWidth)
					{
						if (tabID == 13)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 13;
						tabAreaAltered = true;
					}
				}

	}

	public static boolean handleResizableTabAreaClick(boolean leftClick, boolean rightClick) {
		final int barHeight = 37;
		final int barX = frameWidth - 225;

		if (MouseState.y >= frameHeight - barHeight && MouseState.x >= barX) {
			processTabClick(leftClick, rightClick);
			return true;
		}
		return false;
	}

	public static void handleTabAreaInterfaces(Set<Integer> handled) {
		int yOffset = 37;
		int x0 = frameWidth - 197;
		int y0 = frameHeight - yOffset - 267;
		int x1 = frameWidth - 7;
		int y1 = frameHeight - yOffset - 7;

		if (MouseState.x >= x0 && MouseState.x <= x1 && MouseState.y >= y0 && MouseState.y <= y1 && showTabComponents) {

			int id = (invOverlayInterfaceID != -1) ? invOverlayInterfaceID : tabInterfaceIDs[tabID];
			if (id != -1 && handled.add(id))
				buildInterfaceMenu(x0, RSInterface.interfaceCache[id], y0, 0);
		}
	}

	public static void commitTabState() {
		if (selectedTabIndex != -1 && selectedTabIndex == tabID) {
			selectedTabIndex = -1;
			sendProto(GamePacket.newBuilder().setFlashingSideIcon(FlashingSideIcon.newBuilder().setIcon(tabID)).build());
		}
		tabAreaAltered = false;
		loginImageProducer.initDrawingArea();
		mainGameRendering.initDrawingArea();
	}
}
