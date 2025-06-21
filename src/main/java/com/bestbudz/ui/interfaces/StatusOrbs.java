package com.bestbudz.ui.interfaces;

import com.bestbudz.data.XP;
import static com.bestbudz.ui.handling.RightClickMenu.rightClickMenu;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.Client.cacheSprite;
import static com.bestbudz.engine.core.Client.crossIndex;
import static com.bestbudz.engine.core.Client.crossType;
import static com.bestbudz.engine.core.Client.crossX;
import static com.bestbudz.engine.core.Client.crossY;
import static com.bestbudz.engine.core.Client.crosses;
import static com.bestbudz.engine.core.Client.frameWidth;
import static com.bestbudz.engine.core.Client.inBounds;
import static com.bestbudz.engine.core.Client.menuActionID;
import static com.bestbudz.engine.core.Client.menuActionName;
import static com.bestbudz.engine.core.Client.menuOpen;
import static com.bestbudz.engine.core.Client.minimapInt1;
import static com.bestbudz.engine.core.Client.smallText;
import static com.bestbudz.engine.core.Client.stream;
import static com.bestbudz.engine.core.Client.tabID;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.ui.RSInterface;
import static com.bestbudz.util.FormatHelpers.formatBestBucks;
import java.util.LinkedList;

public class StatusOrbs {

	public static boolean counterOn;
	private static boolean pouchHover;
	private static int anInt1142;
	public static Sprite compass;
	public static Sprite[] orbComponents = new Sprite[15];
	public static Sprite[] orbComponents2 = new Sprite[7];
	public static Sprite[] orbComponents3 = new Sprite[10];
	public static int xpCounter = 0;
	public static final LinkedList<XP.XPGain> gains = new LinkedList<XP.XPGain>();

	private static void loadAllOrbs(int xOffset) {
		if (!SettingsConfig.enableStatusOrbs) return;

		// ──────────────── POUCH DRAW ────────────────
		if (SettingsConfig.enablePouch) {
			int setPouchPosX = 62; // Modify
			int pouchPosX = frameWidth - setPouchPosX;
			int pouchTextPosX = frameWidth - (setPouchPosX - 30);
			int pouchFillPosX = frameWidth - (setPouchPosX - 16);
			int pouchIconPosX = frameWidth - (setPouchPosX - 8);

			int setPouchPosY = 45; // Modify
			int pouchTextPosY = setPouchPosY + 25;
			int pouchFillPosY = setPouchPosY + 15;
			int pouchIconPosY = setPouchPosY + 7;

			// { X, Y, TextX, TextY, FillX, FillY, CoinIconX, CoinIconY }
			final int[] pouchLayout = new int[] { pouchPosX, setPouchPosY, pouchTextPosX, pouchTextPosY, pouchFillPosX, pouchFillPosY, pouchIconPosX, pouchIconPosY };

			int pouchX       = pouchLayout[0];
			int pouchY       = pouchLayout[1];
			int coinTextX    = pouchLayout[2];
			int coinTextY    = pouchLayout[3];
			int fillX        = pouchLayout[4];
			int fillY        = pouchLayout[5];
			int coinIconX    = pouchLayout[6];
			int coinIconY    = pouchLayout[7];

			DrawingArea.fillRectangle(fillX, fillY - 2,45 , 16, 0x020202, 185 );

			long coins = Long.parseLong(RSInterface.interfaceCache[8135].disabledMessage);
			smallText.method382(getMoneyOrbColor(coins), coinTextX, formatBestBucks(coins), coinTextY, 1);

			cacheSprite[428].drawSprite(coinIconX, coinIconY);
		}

	}

	private static int getMoneyOrbColor(long amount)
	{
		if (amount >= 0 && amount <= 99999)
		{
			return 0xFFFF00;
		}
		else if (amount >= 100000 && amount <= 9999999)
		{
			return 0xFFFFFF;
		}
		else
		{
			return 0x00FF80;
		}
	}





	public static void drawGameOverlays() {
		if (crossType == 1) {
			crosses[crossIndex / 100].drawSprite(crossX - 8, crossY - 8);
			if (++anInt1142 > 67) {
				anInt1142 = 0;
				stream.createFrame(78);
			}
		} else if (crossType == 2) {
			crosses[4 + crossIndex / 100].drawSprite(crossX - 8, crossY - 8);
		}
	}

	public static void drawGameUIorbs()
	{
		{
			gameOrbUIsetup();
		}
	}


	private static void gameOrbUIsetup()
	{
		if (SettingsConfig.enableStatusOrbs)
		{

			loadAllOrbs(frameWidth - 217);
		}
// ──────────────── COMPASS ────────────────
		{
			int setCompassPosX = 62;
			int setCompassPosY = 10;

			int compassX = frameWidth - setCompassPosX;
			int compassY = setCompassPosY;

			compass.drawRotatedSpriteAt(compassX, compassY, minimapInt1, 256);

		}



		boolean hoveringLogout =
			MouseState.x >= frameWidth - 26 && MouseState.x <= frameWidth - 1 &&
				MouseState.y >= 2 && MouseState.y <= 24;
		if (hoveringLogout)
		{
			cacheSprite[348].drawARGBSprite(frameWidth - 23, 0, 205);
		}
		else
		{
			cacheSprite[348].drawSprite(frameWidth - 23, 0);
		}
		if (tabID == 14)
		{
			cacheSprite[349].drawSprite(frameWidth - 23, 0);
		}
		if (menuOpen)
		{
			rightClickMenu(0, 0);
		}
	}

	public static void handleMinimapInteractions(boolean leftClick, boolean rightClick) {

		if (SettingsConfig.enablePouch) {
			pouchHover = inBounds(MouseState.x, MouseState.y, frameWidth - 65, 165, 62, 31);
		}

		if (leftClick && SettingsConfig.enableStatusOrbs) {
			if (pouchHover && SettingsConfig.enablePouch) {
				stream.createFrame(185);
				stream.writeWord(713); // mimic "Withdraw from debit"
			}

		}


		if (MouseState.x >= frameWidth - 26 && MouseState.x <= frameWidth - 1 &&
			MouseState.y >= 2 && MouseState.y <= 24) {
			menuActionName[1] = "Too stoned..";
			menuActionID[1] = 1004;
			Client.menuActionRow = 2;
		}

		if (MouseState.x >= frameWidth - 45 && MouseState.x <= frameWidth - 1 &&
			MouseState.y >= 57 && MouseState.y <= 73) {
			if (SettingsConfig.enablePouch) {
				menuActionName[3] = "Withdraw from debit";
				menuActionID[3] = 713;
				menuActionName[2] = "Pay with... ";
				menuActionID[2] = 715;
				menuActionName[1] = "inspect debit card";
				menuActionID[1] = 714;
				Client.menuActionRow = 4;
			}
		}
	}

}
