package com.bestbudz.ui.interfaces;

import com.bestbudz.config.Configuration;
import com.bestbudz.engine.Client;
import static com.bestbudz.engine.Client.cacheSprite;
import static com.bestbudz.engine.Client.changeChatArea;
import static com.bestbudz.engine.Client.crossIndex;
import static com.bestbudz.engine.Client.crossType;
import static com.bestbudz.engine.Client.crossX;
import static com.bestbudz.engine.Client.crossY;
import static com.bestbudz.engine.Client.crosses;
import static com.bestbudz.engine.Client.extendChatArea;
import static com.bestbudz.engine.Client.extractInterfaceValues;
import static com.bestbudz.engine.Client.formatBestBucks;
import static com.bestbudz.engine.Client.frameHeight;
import static com.bestbudz.engine.Client.frameWidth;
import static com.bestbudz.engine.Client.inBounds;
import static com.bestbudz.engine.Client.loopCycle;
import static com.bestbudz.engine.Client.menuActionID;
import static com.bestbudz.engine.Client.menuActionName;
import static com.bestbudz.engine.Client.menuOpen;
import static com.bestbudz.engine.Client.minimapInt1;
import static com.bestbudz.engine.Client.newSmallFont;
import static com.bestbudz.engine.Client.prayClicked;
import static com.bestbudz.engine.Client.rightClickMenu;
import static com.bestbudz.engine.Client.showChatComponents;
import static com.bestbudz.engine.Client.smallText;
import static com.bestbudz.engine.Client.stream;
import static com.bestbudz.engine.Client.tabID;
import static com.bestbudz.engine.Client.variousSettings;
import static com.bestbudz.engine.Client.walkableInterfaceMode;
import com.bestbudz.engine.input.MouseState;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.ui.RSInterface;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class StatusOrbs {

	public static boolean counterOn;
	private static boolean counterHover;
	private static boolean worldHover;
	private static boolean pouchHover;
	private static boolean runHover;
	private static boolean prayHover;
	private static boolean hpHover;
	static Client.XPGain mainGain = null;
	public static int digits = 0;
	private static int anInt1142;
	public static boolean isPoisoned;
	public static boolean clickedQuickPrayers;
	static List<Sprite> gainSprites = new ArrayList<>();
	public static Sprite compass;
	public static Sprite[] orbComponents = new Sprite[15];
	public static Sprite[] orbComponents2 = new Sprite[7];
	public static Sprite[] orbComponents3 = new Sprite[10];
	public static int xpCounter = 0;
	public static final LinkedList<Client.XPGain> gains = new LinkedList<Client.XPGain>();

	private static void loadAllOrbs(int xOffset) {
		if (!Configuration.enableStatusOrbs) return;

		// ──────────────── POUCH DRAW ────────────────
		if (Configuration.enablePouch) {
			int setPouchPosX = 62; // Modify
			int pouchPosX = frameWidth - setPouchPosX;
			int pouchTextPosX = frameWidth - (setPouchPosX - 43);
			int pouchFillPosX = frameWidth - (setPouchPosX - 16);
			int pouchIconPosX = frameWidth - (setPouchPosX - 8);

			int setPouchPosY = 165; // Modify
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

			DrawingArea.fillCircle(fillX, fillY, 15, 0x6E6D6D);
			cacheSprite[pouchHover ? 429 : 430].drawSprite(pouchX, pouchY);

			long coins = Long.parseLong(RSInterface.interfaceCache[8135].disabledMessage);
			smallText.method382(getMoneyOrbColor(coins), coinTextX, formatBestBucks(coins), coinTextY, true);

			cacheSprite[428].drawSprite(coinIconX, coinIconY);
		}


		// ──────────────── ORB CONFIG ────────────────

		int orbPosX = 155;
		int textPosX = orbPosX + 15;
		int fillPosX = orbPosX + 27;
		int iconPosX = orbPosX + 33;

		// { X, Y, TextX, TextY, FillX, FillY, IconX, IconY }
		// HP
		// Prayer
		// Run
		final int[][] layout = new int[][]{
			{orbPosX, 45, textPosX, 72, fillPosX, 49, iconPosX, 56}, // HP
			{orbPosX, 85, textPosX, 111, fillPosX, 88, iconPosX - 3, 92}, // Prayer
			{orbPosX, 125, textPosX, 152, fillPosX, 129, iconPosX, 134} // Run
		};

		final int[] currentInterface = { 4016, 4012, 149 };
		final int[] maximumInterface = { 4017, 4013, 149 };

		final int[] spriteID = {
			isPoisoned && hpHover ? 13 : 12,
			prayHover ? 13 : 12,
			runHover ? 13 : 12
		};

		final int[] coloredOrbSprite = {
			0,
			clickedQuickPrayers ? 8 : 1,
			variousSettings[173] == 1 ? 9 : 8
		};

		final int[] orbSprite = {
			14,
			2,
			variousSettings[173] == 1 ? 4 : 3
		};

		long currentHP = extractInterfaceValues(RSInterface.interfaceCache[4016], 0);
		long currentEnergy = extractInterfaceValues(RSInterface.interfaceCache[19177], 0);

		for (int i = 0; i < 3; i++) {
			long current = extractInterfaceValues(RSInterface.interfaceCache[currentInterface[i]], 0);
			long max = extractInterfaceValues(RSInterface.interfaceCache[maximumInterface[i]], 0);
			int level = (int)((current / (double) max) * 100D);
			double percent = (i == 2 ? currentEnergy / 100D : level / 100D);
			int fillHeight = 26 - (int)(26 * percent);

			// ⬤ Draw base orb + fill
			orbComponents[spriteID[i]].drawSprite(layout[i][0] + xOffset, layout[i][1]);
			orbComponents[coloredOrbSprite[i]].drawSprite(layout[i][4] + xOffset, layout[i][5]);
			orbComponents[6].myHeight = fillHeight;
			try {
				orbComponents[6].drawSprite(layout[i][4] + xOffset, layout[i][5]);
			} catch (Exception ignored) {}

			// ⬤ Draw animated or static icon
			int iconX = layout[i][6] + xOffset;
			int iconY = layout[i][7];
			if (level < 25) {
				int cycle = 125 + (int)(125 * Math.sin(loopCycle / 7.0));
				orbComponents[orbSprite[i]].drawSprite1(iconX, iconY, cycle);
			} else {
				orbComponents[orbSprite[i]].drawSprite(iconX, iconY);
			}

			// ⬤ Draw text value
			String text = (i == 2 ? String.valueOf(currentEnergy) :
				i == 0 && Configuration.enable10xDamage ? String.valueOf(currentHP * 10) :
					String.valueOf(current));
			smallText.method382(getOrbTextColor(i == 2 ? currentEnergy : level), layout[i][2] + xOffset, text, layout[i][3], true);
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

	private static void drawCounterOnScreen()
	{
		if (!Configuration.enableStatusOrbs)
		{
			return;
		}
		int x = frameWidth - 110;
		int y = -30;
		digits = xpCounter == 0 ? 1 : 1 + (int) Math.floor(Math.log10(xpCounter));
		int i = smallText.getTextWidth(Integer.toString(xpCounter))
			- smallText.getTextWidth(Integer.toString(xpCounter)) / 2;
		smallText.method382(0xffffff, x - 38 - i - digits - 12, "Gained:", y + 50, true);
		if (xpCounter >= 0)
		{
			smallText.method382(0xffffff, x + 1 - i, "+" + NumberFormat.getIntegerInstance().format(xpCounter), y + 50,
				true);
		}
		int currentIndex = 0;
		int offsetY = 0;
		int stop = 40;
		if (!gains.isEmpty())
		{
			Iterator<Client.XPGain> gained = gains.iterator();
			if ((gains.size() > 1))
			{
				if (mainGain == null)
				{
					Client.XPGain toGain = null;
					while (gained.hasNext())
					{
						Client.XPGain gain = gained.next();

						if (toGain == null)
						{
							toGain = new Client.XPGain(gain.skill, 0);
						}

						Sprite sprite = cacheSprite[gain.getSkill() + 324];

						if (!gainSprites.contains(sprite))
						{
							gainSprites.add(sprite);
						}

						toGain.xp += gain.getXP();
						currentIndex++;
					}

					Collections.reverse(gainSprites);
					mainGain = toGain;
				}

				if (mainGain == null)
				{
					return;
				}

				if (mainGain.getY() < stop)
				{
					if (mainGain.getY() <= 10)
					{
						mainGain.increaseAlpha();
					}
					if (mainGain.getY() >= stop - 10)
					{
						mainGain.decreaseAlpha();
					}
					mainGain.increaseY();
				}
				else if (mainGain.getY() >= stop)
				{
					mainGain = null;
					gains.clear();
					gainSprites.clear();
				}

				if (mainGain == null)
				{
					return;
				}

				if (mainGain.getY() < stop)
				{
					if (variousSettings[1030] == 0)
					{
						for (int ii = 0; ii < gainSprites.size(); ii++)
						{
							Sprite sprite = gainSprites.get(ii);
							sprite.drawSprite1(x - ii * 25 - 75 - sprite.myWidth / 2,
								mainGain.getY() - 5 + offsetY + y + 65 - sprite.myHeight / 2, mainGain.getAlpha());
						}
						newSmallFont
							.drawBasicString(
								"<trans=" + (mainGain.getAlpha()) + ">+"
									+ String.format("%,d", mainGain.getXP()) + "xp",
								x - 55, mainGain.getY() + offsetY + y + 65, 0xFFFFFF, 0);
					}
					else if (variousSettings[1030] == 1)
					{
						for (int ii = 0; ii < gainSprites.size(); ii++)
						{
							Sprite sprite = gainSprites.get(ii);
							sprite.drawSprite1((-mainGain.getY() + frameWidth - 280) - ii * 25 - (sprite.myWidth / 2),
								80 - (sprite.myHeight / 2) + currentIndex * 28, mainGain.getAlpha());
						}
						newSmallFont.drawBasicString(
							"<trans=" + (mainGain.getAlpha()) + ">+" + String.format("%,d", mainGain.getXP())
								+ "xp",
							-mainGain.getY() + frameWidth - 260,
							85 + (walkableInterfaceMode ? 36 : 0) + currentIndex * 28, 0xFFFFFF, 0);
					}
					else if (variousSettings[1030] == 2)
					{
						for (int ii = 0; ii < gainSprites.size(); ii++)
						{
							Sprite sprite = gainSprites.get(ii);
							sprite.drawSprite1(x - ii * 25 - 75 - sprite.myWidth / 2,
								mainGain.getY() - 5 + offsetY + y + 65 - sprite.myHeight / 2, mainGain.getAlpha());
						}
						newSmallFont.drawBasicString(
							"<trans=" + (mainGain.getAlpha()) + ">+" + String.format("%,d", mainGain.getXP())
								+ "xp",
							-mainGain.getY() + frameWidth - 260, mainGain.getY() + offsetY + y + 65, 0xFFFFFF, 0);
					}
				}
			}
			else
			{
				while (gained.hasNext())
				{
					Client.XPGain gain = gained.next();
					if (gain.getY() < stop)
					{
						if (gain.getY() <= 10)
						{
							gain.increaseAlpha();
						}
						if (gain.getY() >= stop - 10)
						{
							gain.decreaseAlpha();
						}
						gain.increaseY();
					}
					else if (gain.getY() >= stop)
					{
						gained.remove();
					}
					Sprite sprite = cacheSprite[gain.getSkill() + 324];
					if (gain.getY() < stop)
					{
						if (variousSettings[1030] == 0)
						{
							sprite.drawSprite1(x - 75 - sprite.myWidth / 2,
								gain.getY() - 5 + offsetY + y + 65 - sprite.myHeight / 2, gain.getAlpha());
							newSmallFont.drawBasicString(
								"<trans=" + (gain.getAlpha()) + ">+" + String.format("%,d", gain.getXP()) + "xp",
								x - 55, gain.getY() + offsetY + y + 65, 0xFFFFFF, 0);
						}
						else if (variousSettings[1030] == 1)
						{
							sprite.drawSprite1((-gain.getY() + frameWidth - 280) - (sprite.myWidth / 2),
								80 - (sprite.myHeight / 2) + currentIndex * 28, gain.getAlpha());
							newSmallFont.drawBasicString(
								"<trans=" + (gain.getAlpha()) + ">+" + String.format("%,d", gain.getXP()) + "xp",
								-gain.getY() + frameWidth - 260,
								85 + (walkableInterfaceMode ? 36 : 0) + currentIndex * 28, 0xFFFFFF, 0);
						}
						else if (variousSettings[1030] == 2)
						{
							sprite.drawSprite1(x - 75 - sprite.myWidth / 2,
								gain.getY() - 5 + offsetY + y + 65 - sprite.myHeight / 2, gain.getAlpha());
							newSmallFont.drawBasicString(
								"<trans=" + (gain.getAlpha()) + ">+" + String.format("%,d", gain.getXP()) + "xp",
								-gain.getY() + frameWidth - 260, gain.getY() + offsetY + y + 65, 0xFFFFFF, 0);
						}
					}
					currentIndex++;
				}
			}
		}
	}

	public static int getOrbTextColor(long statusInt)
	{
		if (statusInt >= 75 && statusInt <= Integer.MAX_VALUE)
			return 0x00FF00;
		else if (statusInt >= 50 && statusInt <= 74)
			return 0xFFFF00;
		else if (statusInt >= 25 && statusInt <= 49)
			return 0xFF981F;
		else
			return 0xFF0000;
	}

	public static void drawGameOverlays() {
		if (counterOn) drawCounterOnScreen();
		if (showChatComponents) Chatbox.drawSplitPrivateChat();
		Client.BannerManager.drawMovingBanner();

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
		if (Configuration.enableStatusOrbs)
		{
			int setXpOrbPosX = 96; // Modify this
			int setXpOrbPosY = 2; // Modify this

			int xpOrbX = frameWidth - setXpOrbPosX;
			int xpOrbY = setXpOrbPosY;

			final boolean hoveringXpOrb =
				MouseState.x >= xpOrbX && MouseState.x <= xpOrbX + 26 &&
					MouseState.y >= xpOrbY && MouseState.y <= xpOrbY + 26;

			orbComponents3[hoveringXpOrb ? 1 : 0].drawSprite(xpOrbX, xpOrbY);
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
		int frameSize = frameWidth - 217;
		int orbPosX = frameSize + 155;

		// Hover detection and context menu building
		hpHover = inBounds(MouseState.x, MouseState.y, orbPosX, 45, 56, 32);
		prayHover = inBounds(MouseState.x, MouseState.y, orbPosX, 85, 56, 32);
		runHover = inBounds(MouseState.x, MouseState.y, orbPosX, 125, 56, 32);
		counterHover = inBounds(MouseState.x, MouseState.y, frameWidth - 96, 2, 26, 26);
		worldHover = inBounds(MouseState.x, MouseState.y, frameWidth - 41, 203, 30, 30);

		if (Configuration.enablePouch) {
			pouchHover = inBounds(MouseState.x, MouseState.y, frameWidth - 65, 165, 62, 31);
		}

		if (leftClick && Configuration.enableStatusOrbs) {
			if (prayHover) {
				stream.createFrame(185);
				stream.writeWord(50010); // toggle quick prayers
			}
			if (runHover) {
				stream.createFrame(185);
				stream.writeWord(152); // toggle run
			}
			if (counterHover) {
				counterOn = !counterOn; // toggle XP counter visibility
			}
			if (pouchHover && Configuration.enablePouch) {
				stream.createFrame(185);
				stream.writeWord(713); // mimic "Withdraw from debit"
			}

		}


		if (!rightClick) return; // Prevents unnecessary context menu building on hover only


		if (changeChatArea) {
			if (MouseState.x >= 256 && MouseState.x <= 264 &&
				MouseState.y >= frameHeight - 170 - extendChatArea &&
				MouseState.y <= frameHeight - 160 - extendChatArea) {
				menuActionName[1] = "Drag to Extend Chat";
				menuActionID[1] = 701;
				Client.menuActionRow = 2;
			}
		}

		if (MouseState.x >= frameWidth - 26 && MouseState.x <= frameWidth - 1 &&
			MouseState.y >= 2 && MouseState.y <= 24) {
			menuActionName[1] = "Too stoned..";
			menuActionID[1] = 1004;
			Client.menuActionRow = 2;
		}

		if (Configuration.enableStatusOrbs) {
			if (counterHover) {
				menuActionName[3] = counterOn ? "See Gains" : "Unsee Gains";
				menuActionID[3] = 474;
				menuActionName[2] = "Reset Gains";
				menuActionID[2] = 475;
				menuActionName[1] = "Gains settings";
				menuActionID[1] = 476;
				Client.menuActionRow = 4;
			}
			if (pouchHover && Configuration.enablePouch) {
				menuActionName[3] = "Withdraw from debit";
				menuActionID[3] = 713;
				menuActionName[2] = "Pay with... ";
				menuActionID[2] = 715;
				menuActionName[1] = "inspect debit card";
				menuActionID[1] = 714;
				Client.menuActionRow = 4;
			}
			if (prayHover) {
				menuActionName[2] = prayClicked ? "Dont channel necromance powers" : "Channel necromance powers";
				menuActionID[2] = 1500;
				menuActionName[1] = "Select necromance powers";
				menuActionID[1] = 1506;
				Client.menuActionRow = 3;
			}
			if (runHover) {
				menuActionName[1] = variousSettings[173] == 0 ? "Turn haste mode on" : "Turn haste mode off";
				menuActionID[1] = 1050;
				Client.menuActionRow = 2;
			}
		}
	}

}
