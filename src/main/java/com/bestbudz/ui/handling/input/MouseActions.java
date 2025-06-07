package com.bestbudz.ui.handling.input;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.deterquarryMenuSize;
import static com.bestbudz.ui.handling.RightClickMenu.processRightClick;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.ui.handling.ActionHandler.doAction;
import static com.bestbudz.network.packets.SendFrames.processBankSwap;
import static com.bestbudz.network.packets.SendFrames.swapInventoryItem;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.world.ObjectDef;
import static com.bestbudz.world.WalkTo.doWalkTo;

public class MouseActions extends Client
{
	public static boolean getMousePositions()
	{


		if (showTabComponents)
		{
			if (frameWidth > 1000)
			{
				return (MouseState.x < frameWidth - 420 || MouseState.x > frameWidth || MouseState.y < frameHeight - 37
					|| MouseState.y > frameHeight)
					&& (MouseState.x <= frameWidth - 225 || MouseState.x >= frameWidth
					|| MouseState.y <= frameHeight - 37 - 274 || MouseState.y >= frameHeight);
			}
			else
			{
				return (MouseState.x < frameWidth - 210 || MouseState.x > frameWidth || MouseState.y < frameHeight - 74
					|| MouseState.y > frameHeight)
					&& (MouseState.x <= frameWidth - 225 || MouseState.x >= frameWidth
					|| MouseState.y <= frameHeight - 74 - 274 || MouseState.y >= frameHeight);
			}
		}
		return true;
	}



	public static void handleDragAndDrop() {
		if (activeInterfaceType == 0) return;

		dragCycle++;
		if (Math.abs(MouseState.x - pressX) > 5 || Math.abs(MouseState.y - pressY) > 5) {
			aBoolean1242 = true;
		}

		if (!MouseState.leftDown) {
			if (activeInterfaceType == 3) inputTaken = true;
			activeInterfaceType = 0;

			if (aBoolean1242 && dragCycle >= 15) {
				processDragItemMovement();
			} else if ((MouseState.leftClicked || menuHasAddStoner(menuActionRow - 1)) && menuActionRow > 2) {
				deterquarryMenuSize();
			} else if (menuActionRow > 0) {
				doAction(menuActionRow - 1);
			}
			atBoxLoopCycle = 10;
		}
	}

	private static void processDragItemMovement() {
		lastActiveInvInterface = -1;
		processRightClick();

		if (focusedDragWidget == 5382) {
			processBankSwap();
			return;
		}

		if (lastActiveInvInterface == focusedDragWidget && mouseInvInterfaceIndex != dragFromSlot) {
			swapInventoryItem();
		}
	}

	public static void handleClickPacket(boolean leftClick, boolean rightClick) {
		if (!leftClick && !rightClick) return;

		int clampedX = Math.max(0, Math.min(764, MouseState.x));
		int clampedY = Math.max(0, Math.min(502, MouseState.y));
		int packed = clampedY * 765 + clampedX;

		int rightClickFlag = rightClick ? 1 : 0;

		long delta = (System.currentTimeMillis() - aLong1220) / 50L;
		delta = Math.min(delta, 4095L);
		aLong1220 = System.currentTimeMillis();

		int payload = (int)((delta << 20) | (rightClickFlag << 19) | packed);
		stream.createFrame(241);
		stream.writeDWord(payload);

		if (flagged) {
			stream.createFrame(45);
			stream.writeWordBigEndian(0);
			stream.writeWord(packed);
		}
	}

	public static boolean componentIsClicked(int i, int j, int k)
	{
		int i1 = i >> 14 & 0x7fff;
		int j1 = worldController.method304(plane, k, j, i);
		if (j1 == -1)
			return false;
		int k1 = j1 & 0x1f;
		int l1 = j1 >> 6 & 3;
		if (k1 == 10 || k1 == 11 || k1 == 22)
		{
			ObjectDef class46 = ObjectDef.forID(i1);
			int i2;
			int j2;
			if (l1 == 0 || l1 == 2)
			{
				i2 = class46.anInt744;
				j2 = class46.anInt761;
			}
			else
			{
				i2 = class46.anInt761;
				j2 = class46.anInt744;
			}
			int k2 = class46.anInt768;
			if (l1 != 0)
				k2 = (k2 << l1 & 0xf) + (k2 >> 4 - l1);
			doWalkTo(2, 0, j2, 0, myStoner.smallY[0], i2, k2, j, myStoner.smallX[0], false, k);
		}
		else
		{
			doWalkTo(2, l1, 0, k1 + 1, myStoner.smallY[0], 0, 0, j, myStoner.smallX[0], false, k);
		}
		crossX = MouseState.x;
		crossY = MouseState.y;
		crossType = 2;
		crossIndex = 0;
		return true;
	}

	public static boolean mouseInRegion(int x1, int y1, int x2, int y2)
	{
		return MouseState.x >= x1 && MouseState.x <= x2 && MouseState.y >= y1 && MouseState.y <= y2;
	}

	public static boolean clickInRegion(int x1, int y1, int x2, int y2)
	{
		return MouseState.x >= x1 && MouseState.x <= x2 && MouseState.y >= y1 && MouseState.y <= y2;
	}

	public static boolean isClickInsideOrbBounds() {
		if (!SettingsConfig.enableStatusOrbs) return false;

		boolean insideOrbRegion =
				(SettingsConfig.enablePouch && inBounds(MouseState.x, MouseState.y, frameWidth - 65, 165, 62, 31)); // Pouch

		// ⛔ Do NOT block logout button (top-right)
		boolean isLogoutArea = inBounds(MouseState.x, MouseState.y, frameWidth - 26, 2, 25, 22);

		return insideOrbRegion && !isLogoutArea;
	}

	public static void handleMenuCloseOnOutsideClick() {
		int x = MouseState.x;
		int y = MouseState.y;

		switch (menuScreenArea) {
			case 0: x -= 4;  y -= 4;   break;
			case 1: x -= 519; y -= 168; break;
			case 2: x -= 17;  y -= 338; break;
			case 3: x -= 519; y -= 0;   break;
		}

		if (x < menuOffsetX - 10 || x > menuOffsetX + menuWidth + 10 || y < menuOffsetY - 10 || y > menuOffsetY + menuHeight + 10) {
			menuOpen = false;
			if (menuScreenArea == 2) inputTaken = true;
		}
	}

	public static void handleMenuClick() {
		int mouseX = MouseState.x;
		int mouseY = MouseState.y;

		switch (menuScreenArea) {
			case 0: mouseX -= 4;   mouseY -= 4;   break;
			case 1: mouseX -= 519; mouseY -= 168; break;
			case 2: mouseX -= 5;   mouseY -= 338; break;
			case 3: mouseX -= 519; mouseY -= 0;   break;
		}

		int actionIndex = -1;
		for (int i = 0; i < menuActionRow; i++) {
			int entryY = menuOffsetY + 31 + (menuActionRow - 1 - i) * 15;
			if (mouseX > menuOffsetX && mouseX < menuOffsetX + menuWidth && mouseY > entryY - 13 && mouseY < entryY + 3) {
				actionIndex = i;
				break;
			}
		}

		if (actionIndex != -1) doAction(actionIndex);
		menuOpen = false;
		if (menuScreenArea == 2) inputTaken = true;
	}

	public static void handleWidgetDragStart(boolean leftClick, boolean rightClick) {
		if (!leftClick || menuActionRow <= 0) return;

		int actionId = menuActionID[menuActionRow - 1];
		if (actionId != 632 && actionId != 78 && actionId != 867 && actionId != 431 && actionId != 53 &&
			actionId != 74 && actionId != 454 && actionId != 539 && actionId != 493 &&
			actionId != 847 && actionId != 447 && actionId != 1125) return;

		int slot = menuActionCmd2[menuActionRow - 1];
		int interfaceId = menuActionCmd3[menuActionRow - 1];
		RSInterface widget = RSInterface.interfaceCache[interfaceId];

		if (widget.aBoolean259 || widget.aBoolean235) {
			aBoolean1242 = false;
			dragCycle = 0;
			focusedDragWidget = interfaceId;
			dragFromSlot = slot;
			activeInterfaceType = 2;
			pressX = MouseState.x;
			pressY = MouseState.y;

			if (widget.parentID == openInterfaceID) activeInterfaceType = 1;
			if (widget.parentID == backDialogID) activeInterfaceType = 3;
		}
	}

	public static void handleFallbackAction(boolean leftClick, boolean rightClick) {
		if (!rightClick) return;

		if ((leftClick || menuHasAddStoner(menuActionRow - 1)) && menuActionRow > 2) {
			doAction(menuActionRow - 1);
		}

		if (menuActionRow > 0) {
			deterquarryMenuSize();
		}
	}

	public static void processMainScreenClick(boolean leftClick, boolean rightClick)
	{

		if (anInt1021 != 0)
			return;
		if (leftClick)
		{
			int i;
			int j;

			i = MouseState.x - (frameWidth - 182 + 24);
			j = MouseState.y - 8;



			{
				i -= 73;
				j -= 75;
				int k = minimapInt1 + minimapInt2 & 0x7ff;
				int i1 = Rasterizer.anIntArray1470[k];
				int j1 = Rasterizer.anIntArray1471[k];
				int k1 = j * i1 + i * j1 >> 11;
				int l1 = j * j1 - i * i1 >> 11;
				int i2 = myStoner.x + k1 >> 7;
				int j2 = myStoner.y - l1 >> 7;


				boolean flag1 = doWalkTo(
					1,
					myStoner.smallX[0],
					myStoner.smallY[0],
					0,
					j1,
					j2,
					0,
					0,
					0,
					true,
					i2
				);

				if (flag1)
				{
					stream.writeWordBigEndian(i);
					stream.writeWordBigEndian(j);
					stream.writeWord(minimapInt1);
					stream.writeWordBigEndian(57);
					stream.writeWordBigEndian(minimapInt2);
					stream.writeWordBigEndian(minimapInt3);
					stream.writeWordBigEndian(89);
					stream.writeWord(myStoner.x);
					stream.writeWord(myStoner.y);
					stream.writeWordBigEndian(anInt1264);
					stream.writeWordBigEndian(63);
				}

				anInt1117++;
				if (anInt1117 > 1151)
				{
					anInt1117 = 0;
					stream.createFrame(246);
					stream.writeWordBigEndian(0);
					int l = stream.currentOffset;
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWordBigEndian(101);
					stream.writeWordBigEndian(197);
					stream.writeWord((int) (Math.random() * 65536D));
					stream.writeWordBigEndian((int) (Math.random() * 256D));
					stream.writeWordBigEndian(67);
					stream.writeWord(14214);
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWord(29487);
					stream.writeWord((int) (Math.random() * 65536D));
					if ((int) (Math.random() * 2D) == 0)
						stream.writeWordBigEndian(220);
					stream.writeWordBigEndian(180);
					stream.writeBytes(stream.currentOffset - l);
				}
			}
		}
	}

	public static boolean menuHasAddStoner(int j)
	{
		if (j < 0)
			return false;
		int k = menuActionID[j];
		if (k >= 2000)
			k -= 2000;
		return k == 337;
	}
}
