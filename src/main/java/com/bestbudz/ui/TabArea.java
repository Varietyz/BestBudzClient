package com.bestbudz.ui;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.rightClickMenu;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.BuildInterface.buildInterfaceMenu;
import static com.bestbudz.ui.DrawInterface.drawInterface;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.rendering.Rasterizer;
import java.util.Set;

public class TabArea extends Client
{
	public static void setTab(int id)
	{
		tabID = id;
		tabAreaAltered = true;
	}

	public static void drawSideIcons()
	{
		int xOffset = frameWidth - 247;
		int yOffset = frameHeight - 336;
		if (!changeTabArea)
		{
			cacheSprite[370].drawSprite(sideIconsX[8] + xOffset + 168, sideIconsY[7] + yOffset);
			for (int i = 0; i < sideIconsTab.length; i++)
			{
				if (tabInterfaceIDs[sideIconsTab[i]] != -1)
				{
					if (sideIconsId[i] != -1)
					{
						sideIcons[sideIconsId[i]].drawSprite(sideIconsX[i] + xOffset, sideIconsY[i] + yOffset);
					}
				}
			}
		}
		else
		{
			int[] iconId = {0, 1, 2, 3, 4, 5, 6, 15, 8, 9, 7, 11, 12, -1};
			int[] iconX = {19, 49, 80, 112, 144, 176, 208, 242, 273, 306, 338, 369, 402, 435};
			int[] iconY = {30, 31, 33, 34, 36, 35, 33, 31, 31, 32, 32, 32, 32, 28, 32};
			cacheSprite[370].drawSprite(frameWidth - 27, frameHeight - 29);
			for (int i = 0; i < sideIconsTab.length; i++)
			{
				if (tabInterfaceIDs[sideIconsTab[i]] != -1)
				{
					if (iconId[i] != -1)
					{
						sideIcons[iconId[i]].drawSprite(frameWidth - 461 + iconX[i], frameHeight - iconY[i]);
					}
				}
			}
		}
	}

	private static void drawRedStones()
	{
		int xOffset = frameWidth - 247;
		int yOffset = frameHeight - 336;
		if (!changeTabArea)
		{
			if (tabInterfaceIDs[tabID] != -1 && tabID != 14)
			{
				redStones[redStonesId[tabID]].drawSprite(redStonesX[tabID] + xOffset, redStonesY[tabID] + yOffset);
			}
		}
		else
		{
			int[] stoneX = {449, 417, 385, 353, 321, 289, 257, 225, 193, 161, 130, 98, 65, 33};
			if (tabInterfaceIDs[tabID] != -1 && tabID != 14 && showTabComponents)
			{
				redStones[4].drawSprite(frameWidth - stoneX[tabID], frameHeight - 37);
			}
		}
	}

	public static void drawTabArea()
	{
		final int xOffset = frameWidth - 241;
		final int yOffset = frameHeight - 336;

		Rasterizer.anIntArray1472 = anIntArray1181;
		if (!changeTabArea)
		{
			DrawingArea.method335(0x191919, frameHeight - 304, 195, 270, transparentTabArea ? 80 : 256,
				frameWidth - 217);
			gameComponents[2].drawSprite(xOffset, yOffset);
		}
		else
		{
			if (showTabComponents)
			{
				DrawingArea.method335(0x191919, frameHeight - 304, 197, 265, transparentTabArea ? 80 : 256,
					frameWidth - 197);
				gameComponents[4].drawSprite(frameWidth - 204, frameHeight - 311);
			}
			for (int x = frameWidth - 449, y = frameHeight - 37, index = 0; x <= frameWidth - 30
				&& index < 14; x += 32, index++)
			{
				redStones[5].drawSprite(x, y);
			}
		}
		if (invOverlayInterfaceID == -1)
		{
			drawRedStones();
			drawSideIcons();
		}
		if (showTabComponents)
		{
			int x = frameWidth - 215;
			int y = frameHeight - 299;
			if (changeTabArea)
			{
				x = frameWidth - 197;
				y = frameHeight - 303;
			}
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
		Rasterizer.anIntArray1472 = anIntArray1182;
	}

	public static void processTabClick(boolean leftClick, boolean rightClick)
	{
		if (leftClick)
		{
			if (!changeTabArea)
			{
				int xOffset = frameWidth - 765;
				int yOffset = frameHeight - 503;
				for (int i = 0; i < tabClickX.length; i++)
				{
					if (MouseState.x >= tabClickStart[i] + xOffset
						&& MouseState.x <= tabClickStart[i] + tabClickX[i] + xOffset
						&& MouseState.y >= tabClickY[i] + yOffset && MouseState.y < tabClickY[i] + 37 + yOffset
						&& tabInterfaceIDs[i] != -1)
					{
						tabID = i;
						tabAreaAltered = true;
						break;
					}
				}
			}
			else if (changeTabArea && frameWidth >= 1000)
			{
				if (MouseState.y >= frameHeight - 37 && MouseState.y <= frameHeight)
				{
					if (MouseState.x >= frameWidth - 449 && MouseState.x <= frameWidth - 418)
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
					if (MouseState.x >= frameWidth - 417 && MouseState.x <= frameWidth - 386)
					{
						if (tabID == 1)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 1;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 385 && MouseState.x <= frameWidth - 354)
					{
						if (tabID == 2)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 2;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 353 && MouseState.x <= frameWidth - 322)
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
					if (MouseState.x >= frameWidth - 321 && MouseState.x <= frameWidth - 290)
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
					if (MouseState.x >= frameWidth - 289 && MouseState.x <= frameWidth - 258)
					{
						if (tabID == 5)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 5;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 257 && MouseState.x <= frameWidth - 226)
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
					if (MouseState.x >= frameWidth - 225 && MouseState.x <= frameWidth - 196)
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
					if (MouseState.x >= frameWidth - 195 && MouseState.x <= frameWidth - 164)
					{
						if (tabID == 8)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 8;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 163 && MouseState.x <= frameWidth - 132)
					{
						if (tabID == 9)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 9;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 131 && MouseState.x <= frameWidth - 100)
					{
						if (tabID == 10)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 10;
						tabAreaAltered = true;
					}
					if (MouseState.x >= frameWidth - 99 && MouseState.x <= frameWidth - 68)
					{
						if (tabID == 11)
						{
							showTabComponents = !showTabComponents;
						}
						else
						{
							showTabComponents = true;
						}
						tabID = 11;
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
		}
	}

	public static boolean handleResizableTabAreaClick(boolean leftClick, boolean rightClick) {
		if (!changeTabArea) return false;

		final int barHeight = (frameWidth >= 1000) ? 37 : 73;
		final int barX = (frameWidth >= 1000) ? frameWidth - 450 : frameWidth - 226;

		if (MouseState.y >= frameHeight - barHeight && MouseState.x >= barX) {
			processTabClick(leftClick, rightClick);
			return true;
		}
		return false;
	}

	public static void handleTabAreaInterfaces(Set<Integer> handled) {
		if (!changeTabArea) return;

		int yOffset = (frameWidth >= 1000) ? 37 : 74;
		int x0 = frameWidth - 197;
		int y0 = frameHeight - yOffset - 267;
		int x1 = frameWidth - 7;
		int y1 = frameHeight - yOffset - 7;

		if (MouseState.x >= x0 && MouseState.x <= x1 &&
			MouseState.y >= y0 && MouseState.y <= y1 &&
			showTabComponents) {

			int id = (invOverlayInterfaceID != -1) ? invOverlayInterfaceID : tabInterfaceIDs[tabID];
			if (id != -1 && handled.add(id))
				buildInterfaceMenu(x0, RSInterface.interfaceCache[id], y0, 0);
		}
	}

	public static void commitTabState() {
		if (anInt1054 != -1 && anInt1054 == tabID) {
			anInt1054 = -1;
			stream.createFrame(120);
			stream.writeWordBigEndian(tabID);
		}
		tabAreaAltered = false;
		aRSImageProducer_1125.initDrawingArea();
		aRSImageProducer_1165.initDrawingArea();
	}
}
