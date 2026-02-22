package com.bestbudz.ui;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.data.Skills;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.login.logout.Reset.resetAllImageProducers;
import static com.bestbudz.ui.handling.RightClickMenu.drawTooltip;
import static com.bestbudz.ui.handling.RightClickMenu.processRightClick;
import static com.bestbudz.ui.handling.RightClickMenu.rightClickMenu;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.ui.handling.input.MouseActions.getMousePositions;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.rendering.animation.Animation;
import static com.bestbudz.ui.BuildInterface.buildInterfaceMenu;
import static com.bestbudz.ui.BuildScreenMenu.build3dScreenMenu;
import static com.bestbudz.ui.DrawInterface.drawInterface;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.world.InLocation.inBarrows;
import static com.bestbudz.world.InLocation.inCyclops;
import static com.bestbudz.world.InLocation.inGWD;
import static com.bestbudz.world.InLocation.inPcBoat;
import static com.bestbudz.world.InLocation.inPcGame;
import static com.bestbudz.world.InLocation.inPvP;
import static com.bestbudz.world.InLocation.inSafe;
import static com.bestbudz.world.InLocation.inWGGame;
import static com.bestbudz.world.InLocation.inWGLobby;

import com.bestbudz.world.VarBit;
import java.awt.Graphics2D;
import java.util.Set;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.InterfaceProto.*;

public class InterfaceManagement extends Client
{

	public static void clearTopInterfaces() {
		System.out.println("🎭 INTERFACE: clearTopInterfaces called");

		sendProto(GamePacket.newBuilder().setCloseInterfaceRequest(CloseInterfaceRequest.getDefaultInstance()).build());
		if (invOverlayInterfaceID != -1) {
			invOverlayInterfaceID = -1;
			isPlayerBusy = false;
			tabAreaAltered = true;
		}
		if (backDialogID != -1) {
			backDialogID = -1;
			inputTaken = true;
			isPlayerBusy = false;
		}
		openInterfaceID = -1;
		fullscreenInterfaceID = -1;
	}

	public static void drawInterfaceRecursive(int i, int j, int k, int l, RSInterface class9, int i1, boolean flag, int j1)
	{
		int anInt992;
		if (menuVisible)
			anInt992 = 32;
		else
			anInt992 = 0;
		menuVisible = false;
		if (k >= i && k < i + 16 && l >= i1 && l < i1 + 16)
		{
			class9.scrollPosition -= inputClickCount * 4;
			if (flag)
			{
			}
		}
		else if (k >= i && k < i + 16 && l >= (i1 + j) - 16 && l < i1 + j)
		{
			class9.scrollPosition += inputClickCount * 4;
			if (flag)
			{
			}
		}
		else if (k >= i - anInt992 && k < i + 16 + anInt992 && l >= i1 + 16 && l < (i1 + j) - 16 && inputClickCount > 0)
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
			menuVisible = true;
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
				if (getItemDefinition(i).name == null || getItemDefinition(i).certTemplateID == i - 1
					|| getItemDefinition(i).certID == i - 1)
				{
					continue;
				}

				if (getItemDefinition(i).name.toLowerCase().contains(
					RSInterface.interfaceCache[RSInterface.currentInputField.id].disabledMessage.toLowerCase()))
				{
					RSInterface.interfaceCache[61266 + slot].enabledSprite = ItemDef.getSprite(i, 1, 0);
					RSInterface.interfaceCache[61266 + slot].disabledSprite = ItemDef.getSprite(i, 1, 0);
					RSInterface.interfaceCache[61101 + slot++].disabledMessage = getItemDefinition(i).name;
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
				sendProto(GamePacket.newBuilder().setChatInterfaceAction(ChatInterfaceAction.newBuilder().setInterfaceId((int) amount).setOpcode(208)).build());
			}
		} else {
			sendProto(GamePacket.newBuilder().setInputFieldAction(InputFieldAction.newBuilder().setText(RSInterface.currentInputField.disabledMessage).setId(RSInterface.currentInputField.id)).build());
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

		interfaceHoverTime = hoveredInterfaceId;
		anInt1129 = specialHoverInterfaceId;
		hoveredInterfaceId = 0;
		specialHoverInterfaceId = 0;
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

		if (hoveredInterfaceId != inputLength) {
			tabAreaAltered = true;
			inputLength = hoveredInterfaceId;
		}
		if (specialHoverInterfaceId != anInt1044) {
			tabAreaAltered = true;
			anInt1044 = specialHoverInterfaceId;
		}
		hoveredInterfaceId = 0;
		specialHoverInterfaceId = 0;
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
			updateInterfaceAnimations(gameTickCounter, fullscreenInterfaceID);
			if (openInterfaceID != -1) updateInterfaceAnimations(gameTickCounter, openInterfaceID);
			gameTickCounter = 0;
			resetAllImageProducers();

			Rasterizer.scanlineOffsets = fullScreenTextureArray;
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

	public static void drawContextualInterfaces() {
		int absX = baseX + ((myStoner.x - 6) >> 7);
		int absY = baseY + ((myStoner.y - 6) >> 7);
		mouseClickState = getRegionInterface(absX, absY, plane);

		if (mouseClickState == -1) return;

		RSInterface rsi = RSInterface.interfaceCache[mouseClickState];
		updateInterfaceAnimations(gameTickCounter, mouseClickState);

		switch (mouseClickState) {
			case 11146:
				drawInterface(0, 0, rsi, -5);
				break;
			case 23300:

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
				drawInterface(0, (mouseClickState == 15892 ? -325 : -349), rsi, 25);
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

		updateInterfaceAnimations(gameTickCounter, openInterfaceID);

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
		if (tabHoverTime == 1 && multiOverlay != null) multiOverlay.drawSprite(frameWidth - 165, 160);
	}

	public static int getRegionInterface(int x, int y, int plane) {

		if (inBarrows(x, y)) return 59000;
		if (inGWD(x, y)) return 61750;
		if (inWGLobby(x, y)) return 41250;
		if (inWGGame(x, y)) return 41270;
		if (inCyclops(x, y, plane)) return 51200;
		if (inPcBoat(x, y)) return 21119;
		if (inPcGame(x, y)) return 21100;

		if (inPvP(x, y) && !SettingsConfig.economyWorld) return 60250;
		if (inSafe(x, y) && !SettingsConfig.economyWorld) return 60350;
		if (SettingsConfig.snow) return 11877;
		return -1;
	}

	public static long extractInterfaceValues(RSInterface class9, int j)
	{

		if (class9 == null) {
			return -2;
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

					if (class9_1 == null) {
						l++;
						k1 = 0;
					} else {
						int k2 = ai[l++];
						if (k2 >= 0 && k2 < ItemDef.totalItems && (!getItemDefinition(k2).membersObject || isMembers))
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
				if (j1 == 5) {
					int idx = ai[l++];
					k1 = (idx >= 0 && idx < variousSettings.length) ? variousSettings[idx] : 0;
				}
				if (j1 == 6)
					k1 = Skills.EXP_FOR_LEVEL[maxStats[ai[l++]] - 1];
				if (j1 == 7) {
					int idx = ai[l++];
					k1 = (idx >= 0 && idx < variousSettings.length) ? (variousSettings[idx] * 100) / 46875 : 0;
				}
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

					if (class9_2 == null) {
						l++;
						k1 = 0;
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
					int idx = ai[l++];
					int i2 = (idx >= 0 && idx < variousSettings.length) ? variousSettings[idx] : 0;
					int i3 = ai[l++];
					k1 = (i2 & 1 << i3) == 0 ? 0 : 1;
				}
				if (j1 == 14)
				{
					int j2 = ai[l++];
					if (j2 >= 0 && j2 < VarBit.cache.length && VarBit.cache[j2] != null) {
						VarBit varBit = VarBit.cache[j2];
						int l3 = varBit.baseVar;
						int i4 = varBit.startBit;
						int j4 = varBit.endBit;
						int k4 = bitMasks[j4 - i4];
						if (l3 >= 0 && l3 < variousSettings.length) {
							k1 = variousSettings[l3] >> i4 & k4;
						}
					}
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

					if (class9_1 == null) {
						k1 = 0;
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
		return;
	}

	public static boolean updateInterfaceAnimations(int i, int j)
	{
		boolean flag1 = false;

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

			if (class9.children[k] < 0 || class9.children[k] >= RSInterface.interfaceCache.length) {
				continue;
			}

			RSInterface class9_1 = RSInterface.interfaceCache[class9.children[k]];

			if (class9_1 == null) {
				continue;
			}

			if (class9_1.type == 1)
				flag1 |= updateInterfaceAnimations(i, class9_1.id);
			if (class9_1.type == 6 && (class9_1.verticalOffset != -1 || class9_1.anInt258 != -1))
			{
				boolean flag2 = interfaceIsSelected(class9_1);
				int l;
				if (flag2)
					l = class9_1.anInt258;
				else
					l = class9_1.verticalOffset;
				if (l != -1)
				{

					if (Animation.anims == null || l < 0 || l >= Animation.anims.length) {
						continue;
					}

					Animation animation = Animation.anims[l];

					if (animation == null) {
						continue;
					}

					for (class9_1.anInt208 += i; class9_1.anInt208 > animation.getFrameDuration(class9_1.anInt246); )
					{
						class9_1.anInt208 -= animation.getFrameDuration(class9_1.anInt246) + 1;
						class9_1.anInt246++;
						if (class9_1.anInt246 >= animation.frameCount)
						{
							class9_1.anInt246 -= animation.loopOffset;
							if (class9_1.anInt246 < 0 || class9_1.anInt246 >= animation.frameCount)
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
