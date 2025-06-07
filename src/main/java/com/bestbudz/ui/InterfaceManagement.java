package com.bestbudz.ui;

import com.bestbudz.data.ItemDef;
import com.bestbudz.data.Skills;
import com.bestbudz.dock.frame.UIDockFrame;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.drawContextMenu;
import static com.bestbudz.ui.handling.RightClickMenu.drawTooltip;
import static com.bestbudz.ui.handling.RightClickMenu.processRightClick;
import static com.bestbudz.ui.handling.RightClickMenu.rightClickMenu;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.ui.handling.input.MouseActions.getMousePositions;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.rendering.animation.Animation;
import static com.bestbudz.ui.BuildInterface.buildInterfaceMenu;
import static com.bestbudz.ui.BuildScreenMenu.build3dScreenMenu;
import static com.bestbudz.ui.DialogHandling.handleBackDialogOrChatbox;
import static com.bestbudz.ui.DrawInterface.drawInterface;
import static com.bestbudz.ui.TabArea.commitTabState;
import static com.bestbudz.ui.TabArea.drawTabArea;
import static com.bestbudz.engine.core.login.WelcomeScreen.clearWelcomeState;
import static com.bestbudz.engine.util.ClientDiagnostics.drawClientFPS;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.ui.interfaces.StatusOrbs.drawGameOverlays;
import static com.bestbudz.world.InLocation.inBarrows;
import static com.bestbudz.world.InLocation.inCyclops;
import static com.bestbudz.world.InLocation.inGWD;
import static com.bestbudz.world.InLocation.inPcBoat;
import static com.bestbudz.world.InLocation.inPcGame;
import static com.bestbudz.world.InLocation.inPvP;
import static com.bestbudz.world.InLocation.inSafe;
import static com.bestbudz.world.InLocation.inWGGame;
import static com.bestbudz.world.InLocation.inWGLobby;
import static com.bestbudz.world.InLocation.inWilderness;
import com.bestbudz.world.VarBit;
import java.awt.Graphics2D;
import java.util.Set;

public class InterfaceManagement extends Client
{
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
		if (invOverlayInterfaceID != -1) updateInterfaceAnimations(anInt945, invOverlayInterfaceID);

		drawTabArea();
		handleBackDialogOrChatbox();
		renderChatIfInvalidated();

		if (loadingStage == 2) renderGameFrame(g);
		if (anInt1054 != -1) tabAreaAltered = true;
		if (tabAreaAltered) commitTabState();

		anInt945 = 0;
	}

	public static void clearTopInterfaces() {
		System.out.println("🎭 INTERFACE: clearTopInterfaces called");
		UIDockFrame.handleCloseInterface();

		// EXISTING: Your normal clear logic
		stream.createFrame(130);
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			aBoolean1149 = false;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
			aBoolean1149 = false;
		}
		openInterfaceID = -1;
		fullscreenInterfaceID = -1;
	}


	public static void drawInterfaceRecursive(int i, int j, int k, int l, RSInterface class9, int i1, boolean flag, int j1)
	{
		int anInt992;
		if (aBoolean972)
			anInt992 = 32;
		else
			anInt992 = 0;
		aBoolean972 = false;
		if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16)
		{
			class9.scrollPosition -= anInt1213 * 4;
			if (flag)
			{
			}
		}
		else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j)
		{
			class9.scrollPosition += anInt1213 * 4;
			if (flag)
			{
			}
		}
		else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < (i1 + j) - 16 && anInt1213 > 0)
		{
			int l1 = ((j - 32) * j) / j1;
			if (l1 < 8)
				l1 = 8;
			int i2 = l - i1 - 16 - l1 / 2;
			int j2 = j - 32 - l1;
			class9.scrollPosition = ((j1 - j) * i2) / j2;
			if (flag)
			{
			}
			aBoolean972 = true;
		}
	}

	public static void doTextField(RSInterface rsint)
	{

		int rsid = rsint.parentID;

		if (rsid == 6142)
		{
			for (int slot = 0; slot < 100; slot++)
			{
				RSInterface.interfaceCache[61266 + slot].disabledSprite = new Sprite(32, 32);
				RSInterface.interfaceCache[61101 + slot].disabledMessage = "";
			}

			int found = 0;

			for (int i = 0, slot = 0; i < ItemDef.totalItems && slot < 100; i++)
			{
				if (ItemDef.getItemDefinition(i).name == null || ItemDef.getItemDefinition(i).certTemplateID == i - 1
					|| ItemDef.getItemDefinition(i).certID == i - 1)
				{
					continue;
				}

				if (ItemDef.getItemDefinition(i).name.toLowerCase().contains(
					RSInterface.interfaceCache[RSInterface.currentInputField.id].disabledMessage.toLowerCase()))
				{
					RSInterface.interfaceCache[61266 + slot].enabledSprite = ItemDef.getSprite(i, 1, 0);
					RSInterface.interfaceCache[61266 + slot].disabledSprite = ItemDef.getSprite(i, 1, 0);
					RSInterface.interfaceCache[61101 + slot++].disabledMessage = ItemDef.getItemDefinition(i).name;
					found++;
				}
			}

			RSInterface.interfaceCache[61100].scrollMax = 40 * found + 12;
		}
	}

	public static void handleInputFieldSubmission() {
		if (RSInterface.currentInputField == null) return;

		if (RSInterface.currentInputField.onlyNumbers) {
			long amount = 0;
			try {
				amount = Long.parseLong(message.replaceAll(",", ""));
				amount = Math.max(-Integer.MAX_VALUE, Math.min(amount, Integer.MAX_VALUE));
			} catch (Exception ignored) {}

			if (amount > 0) {
				stream.createFrame(208);
				stream.writeDWord((int) amount);
			}
		} else {
			stream.createFrame(150);
			stream.writeWordBigEndian(RSInterface.currentInputField.disabledMessage.length() + 3);
			stream.writeWord(RSInterface.currentInputField.id);
			stream.writeString(RSInterface.currentInputField.disabledMessage);
		}

		RSInterface.currentInputField.disabledMessage = "";
		RSInterface.currentInputField = null;
	}

	public static void handle3DOrInterfaceScreenMenu(Set<Integer> handled) {
		if (!getMousePositions()) return;

		int w = 512, h = 334;
		int x = (frameWidth / 2) - 256;
		int y = (frameHeight / 2) - 167;
		int x2 = x + w;
		int y2 = y + h;
		int count = 3;

		for (int i = 0; i < count; i++) {
			if (x2 > frameWidth - 225) {
				x -= 30;
				x2 -= 30;
				if (x < 0) x = 0;
			}
			if (y2 > frameHeight - 182) {
				y -= 30;
				y2 -= 30;
				if (y < 0) y = 0;
			}
		}

		if (openInterfaceID == 5292) {
			if (MouseState.x >= x - 100 && MouseState.x <= x2 + 100 &&
				MouseState.y >= y - 63 && MouseState.y <= y2 + 63) {
				if (handled.add(openInterfaceID))
					buildInterfaceMenu(x - 100, RSInterface.interfaceCache[openInterfaceID], y - 63, 0);
			} else {
				build3dScreenMenu();
			}
		} else if (openInterfaceID != -1 &&
			MouseState.x >= x && MouseState.x <= x2 &&
			MouseState.y >= y && MouseState.y <= y2) {
			if (handled.add(openInterfaceID))
				buildInterfaceMenu(x, RSInterface.interfaceCache[openInterfaceID], y, 0);
		} else {
			build3dScreenMenu();
		}

		anInt1026 = anInt886;
		anInt1129 = anInt1315;
		anInt886 = 0;
		anInt1315 = 0;
	}


	public static void handleOverlayInterfaces(Set<Integer> handled) {

			int yOffset = 37;

			if (MouseState.x > frameWidth - 197 && MouseState.x < frameWidth - 7 &&
				MouseState.y > frameHeight - yOffset - 267 && MouseState.y < frameHeight - yOffset - 7 &&
				showTabComponents) {

				int id = (invOverlayInterfaceID != -1) ? invOverlayInterfaceID : tabInterfaceIDs[tabID];
				if (id != -1 && handled.add(id))
					buildInterfaceMenu(frameWidth - 197, RSInterface.interfaceCache[id], frameHeight - yOffset - 267, 0);
			}


		if (anInt886 != anInt1048) {
			tabAreaAltered = true;
			anInt1048 = anInt886;
		}
		if (anInt1315 != anInt1044) {
			tabAreaAltered = true;
			anInt1044 = anInt1315;
		}
		anInt886 = 0;
		anInt1315 = 0;
	}

	public static String interfaceIntToString(long j)
	{
		if (j < 0x3b9ac9ff)
			return String.valueOf(j);
		else
			return "*";
	}

	public static void renderFullscreenInterface(Graphics2D g) {
		if (loadingStage == 2) {
			updateInterfaceAnimations(anInt945, fullscreenInterfaceID);
			if (openInterfaceID != -1) updateInterfaceAnimations(anInt945, openInterfaceID);
			anInt945 = 0;
			resetAllImageProducers();

			Rasterizer.anIntArray1472 = fullScreenTextureArray;
			DrawingArea.setAllPixelsToZero();
			welcomeScreenRaised = true;

			if (openInterfaceID != -1) renderFullscreenChild(openInterfaceID);
			renderFullscreenChild(fullscreenInterfaceID);

			if (menuOpen) {
				processRightClick();
				rightClickMenu(0, 0);
			} else {
				processRightClick();
				drawTooltip();
			}
		}
		drawCount++;
		gameWorldScreen.drawGraphics(0, g, 0);
		MouseState.leftClicked = false;
		MouseState.rightClicked = false;

	}

	public static void renderFullscreenChild(int interfaceID) {
		RSInterface rsi = RSInterface.interfaceCache[interfaceID];
		if (rsi.width == 512 && rsi.height == 334 && rsi.type == 0) {
			rsi.width = 765;
			rsi.height = 503;
		}
		drawInterface(0, 0, rsi, 8);
	}

	private static void drawContextualInterfaces() {
		int absX = baseX + ((myStoner.x - 6) >> 7);
		int absY = baseY + ((myStoner.y - 6) >> 7);
		anInt1018 = getRegionInterface(absX, absY, plane);

		if (anInt1018 == -1) return;

		RSInterface rsi = RSInterface.interfaceCache[anInt1018];
		updateInterfaceAnimations(anInt945, anInt1018);

		switch (anInt1018) {
			case 11146:
				drawInterface(0, 0, rsi, -5);
				break;
			case 23300:
				drawInterface(0, frameWidth - rsi.width - 253, rsi, 0);
				break;
			case 2804:
			case 11479:
				drawInterface(0, frameWidth / 2 - 1010, rsi, 80);
				break;
			case 41270:
			case 41250:
				drawInterface(0, 0, rsi, 8);
				break;
			case 201:
				drawInterface(0, frameWidth - 520, rsi, -110);
				break;
			case 59000:
				drawInterface(0, 0, rsi, frameHeight - 495);
				break;
			case 21119:
			case 21100:
				drawInterface(0, 0, rsi, 5);
				break;
			case 51200:
				drawInterface(0, frameWidth - 770, rsi, 25);
				break;
			case 61750:
				drawInterface(0, frameWidth - 800, rsi, 5);
				break;
			case 4535:
				drawInterface(0, -418, rsi, -285);
				break;
			case 15892:
			case 15917:
			case 15931:
			case 15962:
				drawInterface(0, (anInt1018 == 15892 ? -325 : -349), rsi, 25);
				break;
			default:
				drawInterface(0, (frameWidth / 2) + 80, rsi, (frameHeight / 2) - 550);
				break;
		}
	}

	private static final Set<Integer> BLOCKED_INTERFACE_IDS = Set.of(
		-1, 61000, 61500, 62000, 63000, 64000, 65000
	);

	public static void drawOpenInterface() {
		if (BLOCKED_INTERFACE_IDS.contains(openInterfaceID)) return;

		// DEBUG: Log all interface opens
		//System.out.println("🎭 INTERFACE: Drawing interface ID: " + openInterfaceID);

		// EXISTING: Your normal interface drawing code continues as fallback...
		updateInterfaceAnimations(anInt945, openInterfaceID);

		if (openInterfaceID == 5292) {
			drawInterface(0, (frameWidth / 2) - 356, RSInterface.interfaceCache[openInterfaceID], (frameHeight / 2) - 230);
			return;
		}


		RSInterface rsi = RSInterface.interfaceCache[openInterfaceID];
		if (rsi == null) {
			System.err.println("[WARN] Tried to draw interface " + openInterfaceID + " but it's null");
			return;
		}

		int w = 512, h = 334;
		int x = (frameWidth / 2) - 256;
		int y = (frameHeight / 2) - 167;
		int count = 3;

		for (int i = 0; i < count; i++) {
			if (x + w > (frameWidth - 225)) x = Math.max(0, x - 30);
			if (y + h > (frameHeight - 182)) y = Math.max(0, y - 30);
		}

		drawInterface(0, x, rsi, y);
	}

	public static void drawMiscOverlays() {
		if (anInt1055 == 1) multiOverlay.drawSprite(frameWidth - 165, 160);
	}


	public static int getRegionInterface(int x, int y, int plane) {

		if (inBarrows(x, y)) return 59000;
		if (inGWD(x, y)) return 61750;
		if (inWGLobby(x, y)) return 41250;
		if (inWGGame(x, y)) return 41270;
		if (inCyclops(x, y, plane)) return 51200;
		if (inPcBoat(x, y)) return 21119;
		if (inPcGame(x, y)) return 21100;
		if (inWilderness(x, y) && SettingsConfig.economyWorld) return 23300;
		if (inPvP(x, y) && !SettingsConfig.economyWorld) return 60250;
		if (inSafe(x, y) && !SettingsConfig.economyWorld) return 60350;
		if (SettingsConfig.snow) return 11877;
		return -1;
	}

	public static long extractInterfaceValues(RSInterface class9, int j)
	{
		// Add null check at the beginning
		if (class9 == null) {
			return -2; // Return early if interface is null
		}

		if (class9.valueIndexArray == null || j >= class9.valueIndexArray.length)
			return -2;
		try
		{
			int[] ai = class9.valueIndexArray[j];
			long k = 0;
			int l = 0;
			int i1 = 0;
			do
			{
				int j1 = ai[l++];
				long k1 = 0;
				byte byte0 = 0;
				if (j1 == 0)
					return k;
				if (j1 == 1)
					k1 = currentStats[ai[l++]];
				if (j1 == 2)
					k1 = maxStats[ai[l++]];
				if (j1 == 3)
					k1 = currentExp[ai[l++]];
				if (j1 == 4)
				{
					RSInterface class9_1 = RSInterface.interfaceCache[ai[l++]];
					// Add null check for interface lookup
					if (class9_1 == null) {
						l++; // Skip the next parameter
						k1 = 0; // Set to 0 and continue
					} else {
						int k2 = ai[l++];
						if (k2 >= 0 && k2 < ItemDef.totalItems && (!ItemDef.getItemDefinition(k2).membersObject || isMembers))
						{
							for (int j3 = 0; j3 < class9_1.inv.length; j3++)
							{
								if (class9_1.inv[j3] == k2 + 1)
								{
									k1 += class9_1.invStackSizes[j3];
								}
							}
						}
					}
				}
				if (j1 == 5)
					k1 = variousSettings[ai[l++]];
				if (j1 == 6)
					k1 = Skills.EXP_FOR_LEVEL[maxStats[ai[l++]] - 1];
				if (j1 == 7)
					k1 = (variousSettings[ai[l++]] * 100) / 46875;
				if (j1 == 8)
					k1 = myStoner.combatLevel;
				if (j1 == 9)
				{
					for (int l1 = 0; l1 < Skills.SKILLS_COUNT; l1++)
						if (Skills.SKILL_ENABLED[l1])
							k1 += maxStats[l1];

				}
				if (j1 == 10)
				{
					RSInterface class9_2 = RSInterface.interfaceCache[ai[l++]];
					// Add null check for interface lookup
					if (class9_2 == null) {
						l++; // Skip the next parameter
						k1 = 0; // Set to 0 and continue
					} else {
						int l2 = ai[l++] + 1;
						if (l2 >= 0 && l2 < ItemDef.totalItems && isMembers)
						{
							for (int k3 = 0; k3 < class9_2.inv.length; k3++)
							{
								if (class9_2.inv[k3] != l2)
									continue;
								k1 = 0x3b9ac9ff;
								break;
							}
						}
					}
				}
				if (j1 == 11)
					k1 = energy;
				if (j1 == 12)
					k1 = weight;
				if (j1 == 13)
				{
					int i2 = variousSettings[ai[l++]];
					int i3 = ai[l++];
					k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if (j1 == 14)
				{
					int j2 = ai[l++];
					VarBit varBit = VarBit.cache[j2];
					int l3 = varBit.anInt648;
					int i4 = varBit.anInt649;
					int j4 = varBit.anInt650;
					int k4 = anIntArray1232[j4 - i4];
					k1 = variousSettings[l3] >> i4 & k4;
				}
				if (j1 == 15)
					byte0 = 1;
				if (j1 == 16)
					byte0 = 2;
				if (j1 == 17)
					byte0 = 3;
				if (j1 == 18)
					k1 = (myStoner.x >> 7) + baseX;
				if (j1 == 19)
					k1 = (myStoner.y >> 7) + baseY;
				if (j1 == 20)
					k1 = ai[l++];
				if (j1 == 21)
					k1 = tabAmounts[ai[l++]];
				if (j1 == 22)
				{
					RSInterface class9_1 = RSInterface.interfaceCache[ai[l++]];
					// Add null check for interface lookup
					if (class9_1 == null) {
						k1 = 0; // Set to 0 and continue
					} else {
						int initAmount = class9_1.inv.length;
						for (int j3 = 0; j3 < class9_1.inv.length; j3++)
						{
							if (class9_1.inv[j3] <= 0)
							{
								initAmount--;
							}
						}
						k1 += initAmount;
					}
				}
				if (j1 == 23)
				{
					for (int l1 = 0; l1 < Skills.SKILLS_COUNT; l1++)
						if (Skills.SKILL_ENABLED[l1])
							k1 += currentExp[l1];
				}
				if (byte0 == 0)
				{
					if (i1 == 0)
						k += k1;
					if (i1 == 1)
						k -= k1;
					if (i1 == 2 && k1 != 0)
						k /= k1;
					if (i1 == 3)
						k *= k1;
					i1 = 0;
				}
				else
				{
					i1 = byte0;
				}
			} while (true);
		}
		catch (Exception _ex)
		{
			return -1;
		}
	}

	public static boolean interfaceIsSelected(RSInterface class9)
	{
		if (class9.anIntArray245 == null)
			return false;
		for (int i = 0; i < class9.anIntArray245.length; i++)
		{
			long j = extractInterfaceValues(class9, i);
			int k = class9.anIntArray212[i];
			if (class9.anIntArray245[i] == 2)
			{
				if (j >= k)
					return false;
			}
			else if (class9.anIntArray245[i] == 3)
			{
				if (j <= k)
					return false;
			}
			else if (class9.anIntArray245[i] == 4)
			{
				if (j == k)
					return false;
			}
			else if (j != k)
				return false;
		}

		return true;
	}

	public static void drawScrollbar(int height, int pos, int y, int x, int maxScroll, boolean transparent)
	{

		scrollBar1.drawSprite(x, y);
		scrollBar2.drawSprite(x, (y + height) - 16);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x000001, 16);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x3d3426, 15);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x342d21, 13);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x2e281d, 11);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x29241b, 10);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x252019, 9);
		DrawingArea.drawPixels(height - 32, y + 16, x, 0x000001, 1);
		int k1 = ((height - 32) * height) / maxScroll;
		if (k1 < 8)
		{
			k1 = 8;
		}
		int l1 = ((height - 32 - k1) * pos) / (maxScroll - height);
		DrawingArea.drawPixels(k1, y + 16 + l1, x, barFillColor, 16);
		DrawingArea.method341(y + 16 + l1, 0x000001, k1, x);
		DrawingArea.method341(y + 16 + l1, 0x817051, k1, x + 1);
		DrawingArea.method341(y + 16 + l1, 0x73654a, k1, x + 2);
		DrawingArea.method341(y + 16 + l1, 0x6a5c43, k1, x + 3);
		DrawingArea.method341(y + 16 + l1, 0x6a5c43, k1, x + 4);
		DrawingArea.method341(y + 16 + l1, 0x655841, k1, x + 5);
		DrawingArea.method341(y + 16 + l1, 0x655841, k1, x + 6);
		DrawingArea.method341(y + 16 + l1, 0x61553e, k1, x + 7);
		DrawingArea.method341(y + 16 + l1, 0x61553e, k1, x + 8);
		DrawingArea.method341(y + 16 + l1, 0x5d513c, k1, x + 9);
		DrawingArea.method341(y + 16 + l1, 0x5d513c, k1, x + 10);
		DrawingArea.method341(y + 16 + l1, 0x594e3a, k1, x + 11);
		DrawingArea.method341(y + 16 + l1, 0x594e3a, k1, x + 12);
		DrawingArea.method341(y + 16 + l1, 0x514635, k1, x + 13);
		DrawingArea.method341(y + 16 + l1, 0x4b4131, k1, x + 14);
		DrawingArea.method339(y + 16 + l1, 0x000001, 15, x);
		DrawingArea.method339(y + 17 + l1, 0x000001, 15, x);
		DrawingArea.method339(y + 17 + l1, 0x655841, 14, x);
		DrawingArea.method339(y + 17 + l1, 0x6a5c43, 13, x);
		DrawingArea.method339(y + 17 + l1, 0x6d5f48, 11, x);
		DrawingArea.method339(y + 17 + l1, 0x73654a, 10, x);
		DrawingArea.method339(y + 17 + l1, 0x76684b, 7, x);
		DrawingArea.method339(y + 17 + l1, 0x7b6a4d, 5, x);
		DrawingArea.method339(y + 17 + l1, 0x7e6e50, 4, x);
		DrawingArea.method339(y + 17 + l1, 0x817051, 3, x);
		DrawingArea.method339(y + 17 + l1, 0x000001, 2, x);
		DrawingArea.method339(y + 18 + l1, 0x000001, 16, x);
		DrawingArea.method339(y + 18 + l1, 0x564b38, 15, x);
		DrawingArea.method339(y + 18 + l1, 0x5d513c, 14, x);
		DrawingArea.method339(y + 18 + l1, 0x625640, 11, x);
		DrawingArea.method339(y + 18 + l1, 0x655841, 10, x);
		DrawingArea.method339(y + 18 + l1, 0x6a5c43, 7, x);
		DrawingArea.method339(y + 18 + l1, 0x6e6046, 5, x);
		DrawingArea.method339(y + 18 + l1, 0x716247, 4, x);
		DrawingArea.method339(y + 18 + l1, 0x7b6a4d, 3, x);
		DrawingArea.method339(y + 18 + l1, 0x817051, 2, x);
		DrawingArea.method339(y + 18 + l1, 0x000001, 1, x);
		DrawingArea.method339(y + 19 + l1, 0x000001, 16, x);
		DrawingArea.method339(y + 19 + l1, 0x514635, 15, x);
		DrawingArea.method339(y + 19 + l1, 0x564b38, 14, x);
		DrawingArea.method339(y + 19 + l1, 0x5d513c, 11, x);
		DrawingArea.method339(y + 19 + l1, 0x61553e, 9, x);
		DrawingArea.method339(y + 19 + l1, 0x655841, 7, x);
		DrawingArea.method339(y + 19 + l1, 0x6a5c43, 5, x);
		DrawingArea.method339(y + 19 + l1, 0x6e6046, 4, x);
		DrawingArea.method339(y + 19 + l1, 0x73654a, 3, x);
		DrawingArea.method339(y + 19 + l1, 0x817051, 2, x);
		DrawingArea.method339(y + 19 + l1, 0x000001, 1, x);
		DrawingArea.method339(y + 20 + l1, 0x000001, 16, x);
		DrawingArea.method339(y + 20 + l1, 0x4b4131, 15, x);
		DrawingArea.method339(y + 20 + l1, 0x544936, 14, x);
		DrawingArea.method339(y + 20 + l1, 0x594e3a, 13, x);
		DrawingArea.method339(y + 20 + l1, 0x5d513c, 10, x);
		DrawingArea.method339(y + 20 + l1, 0x61553e, 8, x);
		DrawingArea.method339(y + 20 + l1, 0x655841, 6, x);
		DrawingArea.method339(y + 20 + l1, 0x6a5c43, 4, x);
		DrawingArea.method339(y + 20 + l1, 0x73654a, 3, x);
		DrawingArea.method339(y + 20 + l1, 0x817051, 2, x);
		DrawingArea.method339(y + 20 + l1, 0x000001, 1, x);
		DrawingArea.method341(y + 16 + l1, 0x000001, k1, x + 15);
		DrawingArea.method339(y + 15 + l1 + k1, 0x000001, 16, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x000001, 15, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x3f372a, 14, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x443c2d, 10, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x483e2f, 9, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x4a402f, 7, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x4b4131, 4, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x564b38, 3, x);
		DrawingArea.method339(y + 14 + l1 + k1, 0x000001, 2, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x000001, 16, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x443c2d, 15, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x4b4131, 11, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x514635, 9, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x544936, 7, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x564b38, 6, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x594e3a, 4, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x625640, 3, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x6a5c43, 2, x);
		DrawingArea.method339(y + 13 + l1 + k1, 0x000001, 1, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x000001, 16, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x443c2d, 15, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x4b4131, 14, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x544936, 12, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x564b38, 11, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x594e3a, 10, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x5d513c, 7, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x61553e, 4, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x6e6046, 3, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x7b6a4d, 2, x);
		DrawingArea.method339(y + 12 + l1 + k1, 0x000001, 1, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x000001, 16, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x4b4131, 15, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x514635, 14, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x564b38, 13, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x594e3a, 11, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x5d513c, 9, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x61553e, 7, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x655841, 5, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x6a5c43, 4, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x73654a, 3, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x7b6a4d, 2, x);
		DrawingArea.method339(y + 11 + l1 + k1, 0x000001, 1, x);

	}

	public static boolean updateInterfaceAnimations(int i, int j)
	{
		boolean flag1 = false;

		// Check if interface cache exists and index is valid
		if (RSInterface.interfaceCache == null || j < 0 || j >= RSInterface.interfaceCache.length) {
			return false;
		}

		RSInterface class9 = RSInterface.interfaceCache[j];
		if (class9 == null || class9.children == null)
		{
			return false;
		}

		for (int k = 0; k < class9.children.length; k++)
		{
			if (class9.children[k] == -1)
				break;

			// Check if child index is valid
			if (class9.children[k] < 0 || class9.children[k] >= RSInterface.interfaceCache.length) {
				continue;
			}

			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[k]];

			// Check if child interface is null
			if (class9_1 == null) {
				continue;
			}

			if (class9_1.type == 1)
				flag1 |= updateInterfaceAnimations(i, class9_1.id);
			if (class9_1.type == 6 && (class9_1.anInt257 != -1 || class9_1.anInt258 != -1))
			{
				boolean flag2 = interfaceIsSelected(class9_1);
				int l;
				if (flag2)
					l = class9_1.anInt258;
				else
					l = class9_1.anInt257;
				if (l != -1)
				{
					// Check if Animation.anims array exists and index is valid
					if (Animation.anims == null || l < 0 || l >= Animation.anims.length) {
						continue;
					}

					Animation animation = Animation.anims[l];

					// Check if animation is null
					if (animation == null) {
						continue;
					}

					for (class9_1.anInt208 += i; class9_1.anInt208 > animation.method258(class9_1.anInt246); )
					{
						class9_1.anInt208 -= animation.method258(class9_1.anInt246) + 1;
						class9_1.anInt246++;
						if (class9_1.anInt246 >= animation.anInt352)
						{
							class9_1.anInt246 -= animation.anInt356;
							if (class9_1.anInt246 < 0 || class9_1.anInt246 >= animation.anInt352)
								class9_1.anInt246 = 0;
						}
						flag1 = true;
					}
				}
			}
		}

		return flag1;
	}

	public static void clearInterfaceAnimations(int i)
	{
		RSInterface class9 = RSInterface.interfaceCache[i];
		if (class9 == null || class9.children == null)
		{
			return;
		}
		for (int j = 0; j < class9.children.length; j++)
		{
			if (class9.children[j] == -1)
				break;
			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[j]];
			if (class9_1.type == 1)
				clearInterfaceAnimations(class9_1.id);
			class9_1.anInt246 = 0;
			class9_1.anInt208 = 0;
		}
	}



}
