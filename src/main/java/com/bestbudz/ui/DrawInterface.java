package com.bestbudz.ui;

import static com.bestbudz.cache.SpriteDumper.dumpItemSprites;
import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.dock.util.DockBlocker;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.RightClickMenu.drawTooltip;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.ui.InterfaceManagement.drawScrollbar;
import static com.bestbudz.ui.InterfaceManagement.extractInterfaceValues;
import static com.bestbudz.ui.InterfaceManagement.interfaceIntToString;
import static com.bestbudz.ui.InterfaceManagement.interfaceIsSelected;
import static com.bestbudz.engine.core.login.WelcomeScreen.drawStonersListOrWelcomeScreen;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.graphics.text.TextController;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import static com.bestbudz.util.FormatHelpers.formatKMValue;
import static com.bestbudz.util.FormatHelpers.intToKOrMil;
import static com.bestbudz.util.FormatHelpers.intToKOrMilLongName;
import com.bestbudz.graphics.text.TextClass;

public class DrawInterface extends Client
{
	public static void drawInterface(int j, int k, RSInterface class9, int l)
	{
		// Early safety checks to prevent NPE
		if (class9 == null ||
			RSInterface.interfaceCache == null ||
			class9.children == null ||
			DockBlocker.isDocked(class9.id) ||
			DockBlocker.isDocked(class9.parentID)) {
			return;
		}

		if (class9.parentID == 197)
		{
			k = frameWidth - 120;
			l = 170;
		}
		if (class9.type != 0 || class9.children == null)
		{
			return;
		}
		if (class9.isMouseoverTriggered && anInt1026 != class9.id && anInt1048 != class9.id && anInt1039 != class9.id)
			return;
		int i1 = DrawingArea.topX;
		int j1 = DrawingArea.topY;
		int k1 = DrawingArea.bottomX;
		int l1 = DrawingArea.bottomY;
		DrawingArea.setDrawingArea(l + class9.height, k, k + class9.width, l);
		int i2 = class9.children.length;
		for (int j2 = 0; j2 < i2; j2++)
		{
			// Add simple bounds checking before accessing interface cache
			if (class9.children[j2] == -1 ||
				class9.children[j2] < 0 ||
				class9.children[j2] >= RSInterface.interfaceCache.length) {
				continue;
			}

			int k2 = class9.childX[j2] + k;
			int l2 = (class9.childY[j2] + l) - j;
			RSInterface child = RSInterface.interfaceCache[class9.children[j2]];

			// Skip if child is null
			if (child == null) {
				continue;
			}

			// Rest of the original code remains unchanged...
			k2 += child.anInt263;
			l2 += child.anInt265;
			if (child.contentType > 0)
				drawStonersListOrWelcomeScreen(child);
			int[] IDs = {1196, 1199, 1206, 1215, 1224, 1231, 1240, 1249, 1258, 1267, 1274, 1283, 1573, 1290, 1299,
				1308, 1315, 1324, 1333, 1340, 1349, 1358, 1367, 1374, 1381, 1388, 1397, 1404, 1583, 12038, 1414,
				1421, 1430, 1437, 1446, 1453, 1460, 1469, 15878, 1602, 1613, 1624, 7456, 1478, 1485, 1494, 1503,
				1512, 1521, 1530, 1544, 1553, 1563, 1593, 1635, 12426, 12436, 12446, 12456, 6004, 18471,
				12940, 12988, 13036, 12902, 12862, 13046, 12964, 13012, 13054, 12920, 12882, 13062, 12952, 13000,
				13070, 12912, 12872, 13080, 12976, 13024, 13088, 12930, 12892, 13096};
			for (int m5 = 0; m5 < IDs.length; m5++)
			{
				if (child.id == IDs[m5] + 1)
				{
					if (m5 > 61)
						drawBlackBox(k2 + 1, l2);
					else
						drawBlackBox(k2, l2 + 1);
				}
			}
			int[] runeChildren = {1202, 1203, 1209, 1210, 1211, 1218, 1219, 1220, 1227, 1228, 1234, 1235, 1236, 1243,
				1244, 1245, 1252, 1253, 1254, 1261, 1262, 1263, 1270, 1271, 1277, 1278, 1279, 1286, 1287, 1293,
				1294, 1295, 1302, 1303, 1304, 1311, 1312, 1318, 1319, 1320, 1327, 1328, 1329, 1336, 1337, 1343,
				1344, 1345, 1352, 1353, 1354, 1361, 1362, 1363, 1370, 1371, 1377, 1378, 1384, 1385, 1391, 1392,
				1393, 1400, 1401, 1407, 1408, 1410, 1417, 1418, 1424, 1425, 1426, 1433, 1434, 1440, 1441, 1442,
				1449, 1450, 1456, 1457, 1463, 1464, 1465, 1472, 1473, 1474, 1481, 1482, 1488, 1489, 1490, 1497,
				1498, 1499, 1506, 1507, 1508, 1515, 1516, 1517, 1524, 1525, 1526, 1533, 1534, 1535, 1547, 1548,
				1549, 1556, 1557, 1558, 1566, 1567, 1568, 1576, 1577, 1578, 1586, 1587, 1588, 1596, 1597, 1598,
				1605,
				1606, 1607, 1616, 1617, 1618, 1627, 1628, 1629, 1638, 1639, 1640, 6007, 6008, 6011, 8673, 8674,
				12041, 12042, 12429, 12430, 12431, 12439, 12440, 12441, 12449, 12450, 12451, 12459, 12460, 15881,
				15882, 15885, 18474, 18475, 18478};
			for (int r = 0; r < runeChildren.length; r++)
				if (child.id == runeChildren[r])
				{
					child.modelZoom = 775;
					break;
				}
			if (child.type == 0)
			{
				if (child.scrollPosition > child.scrollMax - child.height)
					child.scrollPosition = child.scrollMax - child.height;
				if (child.scrollPosition < 0)
					child.scrollPosition = 0;
				drawInterface(child.scrollPosition, k2, child, l2);
				if (child.id == 18143)
				{
					int clanMates = 0;
					for (int i = 18155; i < 18244; i++)
					{
						RSInterface line = RSInterface.interfaceCache[i];
						if (line.disabledMessage.length() > 0)
						{
							clanMates++;
						}
					}
					child.scrollMax = (clanMates * 14) + child.height + 1;
				}
				if (child.scrollMax > child.height)
					drawScrollbar(child.height, child.scrollPosition, l2, k2 + child.width, child.scrollMax, false);
			}
			else if (child.type != 1) // Start of inventory block
				if (child.type == 2)
				{
					int slot = 0;
					int tabAm = 0;
					int tabSlot = -1;
					int hh = 2;
					if (child.contentType == 206)
					{
						int tabHeight = 0;
						for (int i = 0; i < tabAmounts.length; i++)
						{
							if (tabSlot + 1 < tabAmounts.length && tabAmounts[tabSlot + 1] > 0)
							{
								tabAm += tabAmounts[++tabSlot];
								tabHeight += (tabAmounts[tabSlot] >> 3) + (tabAmounts[tabSlot] % 8 == 0 ? 0 : 1);
								if (tabSlot + 1 < tabAmounts.length && tabAmounts[tabSlot + 1] > 0
									&& variousSettings[1000] == 0 && variousSettings[1012] == 0)
								{
									DrawingArea.method339((l2 + tabHeight * (32 + child.invSpritePadY) + hh) - 1,
										0x191919, ((32 + child.invSpritePadX) << 3) - 10, k2);
									DrawingArea.method339((l2 + tabHeight * (32 + child.invSpritePadY) + hh), 0x191919,
										((32 + child.invSpritePadX) << 3) - 10, k2);
								}
								hh += 8;
							}

							if (i > 0)
							{
								int itemSlot = tabAm - tabAmounts[i];
								int xOffset = (frameWidth - 237 - RSInterface.interfaceCache[5292].width) / 2;
								int yOffset = 36 + ((frameHeight - 503) / 2);
								int x = xOffset + 77;
								int y = yOffset + 25;
								try
								{
									int item = RSInterface.interfaceCache[5382].inv[itemSlot];

									if (EngineConfig.ITEM_DUMPING) dumpItemSprites(); // OPENING BANK TRIGGERS DUMP

									if (tabAmounts[i] > 0 && item > 0)
									{
										Sprite icon = null;
										if (variousSettings[1011] == 0)
										{
											icon = ItemDef.getSprite(item - 1, child.invStackSizes[itemSlot], 0);
										}
										if (variousSettings[1011] == 1)
										{
											icon = cacheSprite[118 + i];
										}
										if (variousSettings[1011] == 2)
										{
											icon = cacheSprite[127 + i];
										}
										if (icon != null)
										{
											icon.drawSprite1((x + 4) + 40 * i,
												(y + 2), 255, true);
										}
										RSInterface.interfaceCache[50013 + i * 4].anInt265 = 0;
										RSInterface.interfaceCache[50014 + i * 4].anInt265 = 0;
										RSInterface.interfaceCache[50014 + i * 4].tooltip = "Check section @or2@" + i;
										RSInterface.interfaceCache[50014 + i * 4].disabledSprite = cacheSprite[109];
									}
									else if (tabAmounts[i - 1] <= 0)
									{
										RSInterface.interfaceCache[50013 + i * 4].anInt265 = -500;
										if (i > 1)
										{
											RSInterface.interfaceCache[50014 + i * 4].anInt265 = -500;
										}
										else
										{
											cacheSprite[114].drawSprite1(
												(x) + 40 * i,
												(y), 255, true);
										}
										RSInterface.interfaceCache[50014 + i * 4].tooltip = "New section";
									}
									else
									{
										RSInterface.interfaceCache[50013 + i * 4].anInt265 = -500;
										RSInterface.interfaceCache[50014 + i * 4].anInt265 = 0;
										RSInterface.interfaceCache[50014 + i * 4].tooltip = "New section";
										RSInterface.interfaceCache[50014 + i * 4].disabledSprite = cacheSprite[112];
										cacheSprite[114].drawSprite1((x) + 40 * i,
											(y), 255, true);
									}
								}
								catch (Exception e)
								{
									System.out.println("Bank section icon error: tab [" + i + "], amount [" + tabAm
										+ "], tabAmount [" + tabAmounts[i] + "], itemSlot [" + itemSlot + "]");
								}
							}
						}
						DrawingArea.bottomY += 3;
					}

					tabAm = tabAmounts[0];
					tabSlot = 0;
					hh = 0;

					int dragX = 0, dragY = 0;
					Sprite draggedItem = null;

					int newSlot = 0;
					if (child.contentType == 206 && variousSettings[1000] != 0 && variousSettings[1012] == 0)
					{
						for (int i = 0; i < tabAmounts.length; i++)
						{
							if (i == variousSettings[1000])
							{
								break;
							}
							newSlot += tabAmounts[i];
						}
						slot = newSlot;
					}

					heightLoop:
					for (int height = 0; height < child.height; height++)
					{
						for (int width = 0; width < child.width; width++)
						{
							int w = k2 + width * (32 + child.invSpritePadX);
							int h = l2 + height * (32 + child.invSpritePadY) + hh;
							if (child.contentType == 206 && variousSettings[1012] == 0)
							{
								if (variousSettings[1000] == 0)
								{
									if (slot == tabAm)
									{
										if (tabSlot + 1 < tabAmounts.length)
										{
											tabAm += tabAmounts[++tabSlot];
											if (tabSlot > 0 && tabAmounts[tabSlot - 1] % 8 == 0)
											{
												height--;
											}
											hh += 8;
										}
										break;
									}
								}
								else if (variousSettings[1000] <= 9)
								{
									if (slot >= tabAmounts[variousSettings[1000]] + newSlot)
									{
										break heightLoop;
									}
								}
							}
							if (slot < 20)
							{
								w += child.spritesX[slot];
								h += child.spritesY[slot];
							}
							int itemId = child.inv[slot] - 1;
							if (variousSettings[1012] == 1 && child.contentType == 206)
							{
								itemId = bankInvTemp[slot] - 1;
							}
							if (itemId + 1 > 0)
							{
								if (child.id == 3900)
								{
									if (stock == null)
									{
										stock = cacheSprite[76];
									}
									stock.drawSprite(w - 7, h - 4);
								}
								int x = 0;
								int y = 0;
								if (w > DrawingArea.topX - 32 && w < DrawingArea.bottomX && h > DrawingArea.topY - 32
									&& h < DrawingArea.bottomY
									|| activeInterfaceType != 0 && dragFromSlot == slot)
								{
									int color = 0;
									if (itemSelected == 1 && anInt1283 == slot && anInt1284 == child.id)
									{
										color = ColorConfig.WHITE_COLOR;
									}
									Sprite itemSprite = ItemDef.getSprite(itemId,
										variousSettings[1012] == 1 && child.contentType == 206 ? bankStackTemp[slot]
											: child.invStackSizes[slot],
										color);

									if (itemSprite != null)
									{
										if (activeInterfaceType != 0 && dragFromSlot == slot
											&& focusedDragWidget == child.id)
										{
											draggedItem = itemSprite;
											x = MouseState.x - pressX;
											y = MouseState.y - pressY;
											if (x < 5 && x > -5)
												x = 0;
											if (y < 5 && y > -5)
												y = 0;
											if (dragCycle < 10)
											{
												x = 0;
												y = 0;
											}
											dragX = w + x;
											dragY = h + y;
											if (h + y < DrawingArea.topY && class9.scrollPosition > 0)
											{
												int i10 = (anInt945 * (DrawingArea.topY - h - y)) / 3;
												if (i10 > anInt945 * 10)
													i10 = anInt945 * 10;
												if (i10 > class9.scrollPosition)
													i10 = class9.scrollPosition;
												class9.scrollPosition -= i10;
												pressY += i10;
											}

											if (h + y + 32 > DrawingArea.bottomY
												&& class9.scrollPosition < class9.scrollMax - class9.height)
											{
												int j10 = (anInt945 * ((h + y + 32) - DrawingArea.bottomY)) / 3;
												if (j10 > anInt945 * 10)
													j10 = anInt945 * 10;
												if (j10 > class9.scrollMax - class9.height - class9.scrollPosition)
													j10 = class9.scrollMax - class9.height - class9.scrollPosition;
												class9.scrollPosition += j10;
												pressY -= j10;
											}
										}
										else if (atBoxInterfaceType != 0 && atBoxIndex == slot
											&& atBoxInterface == child.id)
										{
											itemSprite.drawSprite1(w, h);
										}
										else
										{
											itemSprite.drawSprite(w, h);
										}
										if (child.parentID != 42752)
										{
											if (itemSprite.cropWidth == 33 || child.invStackSizes[slot] != 1)
											{
												int amount = child.invStackSizes[slot];

												if (amount == 0)
												{
													smallText.drawStackText(0, "EMPTY", h + 45 + y, w + 3 + x);
													smallText.drawStackText(ColorConfig.WHITE_COLOR, "EMPTY", h + 44 + y, w + 2 + x);
												}
												else if (amount >= 1000000000)
												{
													smallText.drawStackText(0, intToKOrMil(amount), h + 10 + y, w + x + 1);
													smallText.drawStackText(0x00FF80, intToKOrMil(amount), h + 9 + y,
														w + x);
												}
												else if (amount >= 10000000)
												{
													smallText.drawStackText(0, intToKOrMil(amount), h + 10 + y, w + x + 1);
													smallText.drawStackText(0x00FF80, intToKOrMil(amount), h + 9 + y,
														w + x);
												}
												else if (amount >= 100000)
												{
													smallText.drawStackText(0, intToKOrMil(amount), h + 10 + y, w + x + 1);
													smallText.drawStackText(ColorConfig.WHITE_COLOR, intToKOrMil(amount), h + 9 + y,
														w + x);
												}
												else if (amount >= 1)
												{
													smallText.drawStackText(0, intToKOrMil(amount), h + 10 + y, w + x + 1);
													smallText.drawStackText(0xFFFF00, intToKOrMil(amount), h + 9 + y,
														w + x);
												}
												else
												{
													smallText.drawStackText(0, intToKOrMil(amount), h + 10 + y, w + 1 + x);
													smallText.drawStackText(0xFFFF00, intToKOrMil(amount), h + 9 + y,
														w + x);
												}
											}
										}
									}
								}
							}
							else if (child.sprites != null && slot < 20)
							{
								Sprite childSprite = child.sprites[slot];
								if (childSprite != null)
									childSprite.drawSprite(w, h);
							}
							slot++;
						}
					}
					if (draggedItem != null)
					{
						draggedItem.drawSprite1(dragX, dragY, 200 + (int) (50 * Math.sin(loopCycle / 10.0)),
							child.contentType == 206);
					}
				}
				else if (child.type == 3)
				{
					boolean flag = anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id;
					int color;
					if (interfaceIsSelected(child))
					{
						color = child.anInt219;
						if (flag && child.anInt239 != 0)
							color = child.anInt239;
					}
					else
					{
						color = child.textColor;
						if (flag && child.textHoverColor != 0)
							color = child.textHoverColor;
					}
					if (child.opacity == 0)
					{
						if (child.aBoolean227)
							DrawingArea.drawPixels(child.height, l2, k2, color, child.width);
						else
							DrawingArea.fillPixels(k2, child.width, child.height, color, l2);
					}
					else if (child.aBoolean227)
						DrawingArea.method335(color, l2, child.width, child.height, 256 - (child.opacity & 0xff), k2);
					else
						DrawingArea.method338(l2, child.height, 256 - (child.opacity & 0xff), color, child.width, k2);
				}
				else if (child.type == 4)
				{
					TextDrawingArea textDrawingArea = child.textDrawingAreas;
					String message = child.disabledMessage;
					boolean hovered = anInt1039 == child.id || anInt1048 == child.id || anInt1026 == child.id;
					int color;
					if (interfaceIsSelected(child))
					{
						color = child.anInt219;
						if (hovered && child.anInt239 != 0)
							color = child.anInt239;
						if (child.enabledMessage.length() > 0)
							message = child.enabledMessage;
					}
					else
					{
						color = child.textColor;
						if (hovered && child.textHoverColor != 0)
							color = child.textHoverColor;
					}
					if (child.atActionType == 6 && aBoolean1149)
					{
						message = "Getting high...";
						color = child.textColor;
					}
					if (child.id >= 28000 && child.id < 28036)
					{
						if (RSInterface.interfaceCache[3900].invStackSizes[child.id - 28000] > 0)
						{
							String[] data = message.split(",");
							int currency = 0;
							if (data.length > 1)
							{
								currency = Integer.parseInt(data[1]);
							}
							if (currencyImage[currency] == null)
							{
								currencyImage[currency] = cacheSprite[407 + currency];
							}
							currencyImage[currency].drawSprite(k2 - 5, l2);
							int value = Integer.parseInt(data[0]);
							if (value >= 10_000_000)
							{
								smallText.drawRightAlignedString((value / 1_000_000) + "M", k2 + 36,
									l2 + 1 + DrawingArea.height, 0);
								smallText.drawRightAlignedString((value / 1_000_000) + "M", k2 + 35,
									l2 + DrawingArea.height, 0x00ff80);
							}
							else if (value >= 1_000_000)
							{
								smallText.drawRightAlignedString(
									String.valueOf(String.format("%.1f", (value / 1_000_000.0))).replace(".0", "")
										+ "M",
									k2 + 36, l2 + 1 + DrawingArea.height, 0);
								smallText.drawRightAlignedString(
									String.valueOf(String.format("%.1f", (value / 1_000_000.0))).replace(".0", "")
										+ "M",
									k2 + 35, l2 + DrawingArea.height, ColorConfig.WHITE_COLOR);
							}
							else if (value >= 100_000)
							{
								smallText.drawRightAlignedString((value / 1_000) + "K", k2 + 36,
									l2 + 1 + DrawingArea.height, 0);
								smallText.drawRightAlignedString((value / 1_000) + "K", k2 + 35,
									l2 + DrawingArea.height, ColorConfig.WHITE_COLOR);
							}
							else if (value >= 10_000)
							{
								smallText.drawRightAlignedString((value / 1_000) + "K", k2 + 36,
									l2 + 1 + DrawingArea.height, 0);
								smallText.drawRightAlignedString((value / 1_000) + "K", k2 + 35,
									l2 + DrawingArea.height, 0xffff00);
							}
							else if (value <= 0)
							{
								smallText.drawRightAlignedString("NADA", k2 + 34, l2 + 1 + DrawingArea.height, 0);
								smallText.drawRightAlignedString("NADA", k2 + 33, l2 + DrawingArea.height, 0xffff00);
							}
							else
							{
								smallText.drawRightAlignedString(value + "", k2 + 36, l2 + 1 + DrawingArea.height, 0);
								smallText.drawRightAlignedString(value + "", k2 + 35, l2 + DrawingArea.height,
									0xFFFF00);
							}
						}
						continue;
					}
					if ((backDialogID != -1 || dialogID != -1
						|| child.disabledMessage.contains("Get on with it!"))
						&& (class9.id == backDialogID || class9.id == dialogID))
					{
						if (color == 0xffff00)
						{
							color = 255;
						}
						if (color == 49152)
						{
							color = ColorConfig.WHITE_COLOR;
						}
					}
					if ((child.parentID == 1151) || (child.parentID == 12855))
					{
						switch (color)
						{
							case 16773120:
								color = 0xFE981F;
								break;
							case 7040819:
								color = 0xAF6A1A;
								break;
						}
					}
					int fontHeight = textDrawingArea.getFontHeight();
					for (int l6 = l2 + fontHeight; message.length() > 0; l6 += fontHeight)
					{
						if (message.indexOf("%") != -1)
						{
							do
							{
								int k7 = message.indexOf("%1");
								if (k7 == -1)
									break;
								if (child.id < 4000 || child.id > 5000 && child.id != 13921 && child.id != 13922
									&& child.id != 12171 && child.id != 12172)
									message = message.substring(0, k7) + formatKMValue(extractInterfaceValues(child, 0))
										+ message.substring(k7 + 2);
								else
									message = message.substring(0, k7)
										+ interfaceIntToString(extractInterfaceValues(child, 0))
										+ message.substring(k7 + 2);
							} while (true);
							do
							{
								int l7 = message.indexOf("%2");
								if (l7 == -1)
									break;
								message = message.substring(0, l7)
									+ interfaceIntToString(extractInterfaceValues(child, 1))
									+ message.substring(l7 + 2);
							} while (true);
							do
							{
								int i8 = message.indexOf("%3");
								if (i8 == -1)
									break;
								message = message.substring(0, i8)
									+ interfaceIntToString(extractInterfaceValues(child, 2))
									+ message.substring(i8 + 2);
							} while (true);
							do
							{
								int j8 = message.indexOf("%4");
								if (j8 == -1)
									break;
								message = message.substring(0, j8)
									+ interfaceIntToString(extractInterfaceValues(child, 3))
									+ message.substring(j8 + 2);
							} while (true);
							do
							{
								int k8 = message.indexOf("%5");
								if (k8 == -1)
									break;
								message = message.substring(0, k8)
									+ interfaceIntToString(extractInterfaceValues(child, 4))
									+ message.substring(k8 + 2);
							} while (true);
						}
						int l8 = message.indexOf("\\n");
						String s1;
						if (l8 != -1)
						{
							s1 = message.substring(0, l8);
							message = message.substring(l8 + 2);
						}
						else
						{
							s1 = message;
							message = "";
						}
						TextController font;
						if (textDrawingArea == smallText)
						{
							font = newSmallFont;
						}
						else if (textDrawingArea == regularText)
						{
							font = newRegularFont;
						}
						else if (textDrawingArea == boldText)
						{
							font = newBoldFont;
						}
						else
						{
							font = newFancyFont;
						}

						if (child.centerText)
						{
							font.drawCenteredString(s1, k2 + child.width / 2, l6, color, child.textShadow ? 0 : -1);
						}
						else
						{
							font.drawBasicString(s1, k2, l6, color, child.textShadow ? 0 : -1);
						}
					}
				}
				else if (child.type == 5)
				{
					Sprite sprite;
					if (interfaceIsSelected(child))
						sprite = child.enabledSprite;
					else
						sprite = child.disabledSprite;
					if (spellSelected == 1 && child.id == spellID && spellID != 0 && sprite != null)
					{

						sprite.drawSprite(k2, l2, ColorConfig.WHITE_COLOR);
						if (child.drawsTransparent)
						{
							sprite.drawTransparentSprite(k2, l2, 170);
						}
						else
						{
							sprite.drawSprite(k2, l2);
						}
					}
					else
					{
						if (sprite != null)
							if (child.drawsTransparent)
							{
								sprite.drawTransparentSprite(k2, l2, 170);
							}
							else
							{
								sprite.drawSprite(k2, l2);
							}
					}
				}
				else if (child.type == 6)
				{
					int k3 = Rasterizer.viewportCenterX;
					int j4 = Rasterizer.viewportCenterY;
					Rasterizer.viewportCenterX = k2 + child.width / 2;
					Rasterizer.viewportCenterY = l2 + child.height / 2;
					int i5 = Rasterizer.sinTable[child.modelRotation1] * child.modelZoom >> 16;
					int l5 = Rasterizer.cosTable[child.modelRotation1] * child.modelZoom >> 16;
					boolean flag2 = interfaceIsSelected(child);
					int i7;
					if (flag2)
						i7 = child.anInt258;
					else
						i7 = child.anInt257;
					Model model;
					if (i7 == -1)
					{
						model = child.method209(-1, -1, flag2);
					}
					else
					{
						Animation animation = Animation.anims[i7];
						model = child.method209(animation.anIntArray354[child.anInt246],
							animation.anIntArray353[child.anInt246], flag2);
					}
					if (model != null)
						model.method482(child.modelRotation2, 0, child.modelRotation1, 0, i5, l5);
					Rasterizer.viewportCenterX = k3;
					Rasterizer.viewportCenterY = j4;
				}
				else if (child.type == 7)
				{
					TextDrawingArea textDrawingArea_1 = child.textDrawingAreas;
					int k4 = 0;
					for (int j5 = 0; j5 < child.height; j5++)
					{
						for (int i6 = 0; i6 < child.width; i6++)
						{
							if (child.inv[k4] > 0)
							{
								ItemDef itemDef = getItemDefinition(child.inv[k4] - 1);
								String s2 = itemDef.name;
								if (itemDef.stackable || child.invStackSizes[k4] != 1)
									s2 = s2 + " x" + intToKOrMilLongName(child.invStackSizes[k4]);
								int i9 = k2 + i6 * (115 + child.invSpritePadX);
								int k9 = l2 + j5 * (12 + child.invSpritePadY);
								if (child.centerText)
									textDrawingArea_1.method382(child.textColor, i9 + child.width / 2, s2, k9,
										1);

								else
									textDrawingArea_1.method389(1, i9, child.textColor, s2, k9);
							}
							k4++;
						}
					}
				}
				else if (child.type == 9)
				{
					drawTooltip(k2, l2, child.popupString);
				}

				else if (child.type == 16)
				{
					int x = frameWidth - child.width - k2;
					int y = frameHeight - class9.height + l2;

					boolean hover = false;

					int xx = class9.childX[j2] + (0);
					int yy = class9.childY[j2] + (0);

					if (tabInterfaceIDs[tabID] == child.parentID)
					{
						xx = frameWidth - 197;
						yy = frameWidth >= 1000 ? frameHeight - 280 : frameHeight - 262 + yy - j;
					}

					if (MouseState.x >= xx && MouseState.x <= xx + child.width && MouseState.y >= yy
						&& MouseState.y <= yy + child.height)
					{
						hover = true;
					}

					if (MouseState.x >= xx && MouseState.x <= xx + child.width && MouseState.y >= yy
						&& MouseState.y <= yy + child.height)
					{
						if (RSInterface.currentInputField != child)
						{
							if (MouseState.leftClicked && !menuOpen)
							{
								RSInterface.currentInputField = child;
							}
						}
					}

					int color;

					if (RSInterface.currentInputField == child)
					{
						color = child.anInt219;

						if (hover)
						{
							color = child.anInt239;
						}
					}
					else
					{
						color = child.textColor;

						if (hover)
						{
							color = child.textHoverColor;
						}
					}

					DrawingArea.drawPixels(child.height, l2, k2, color, child.width);
					DrawingArea.fillPixels(k2, child.width, child.height, 0, l2);

					x = k2;
					y = l2;

					StringBuilder builder = new StringBuilder();

					String message = child.disabledMessage;

					if (child.enabledMessage != null && RSInterface.currentInputField != child)
					{
						message = child.enabledMessage;
					}

					if (child.displayAsterisks)
					{
						boldText.method389(1, (x + 8), ColorConfig.WHITE_COLOR,
							builder.append(TextClass.passwordAsterisks(message))
								.append(((RSInterface.currentInputField == child ? 1 : 0)
									& (loopCycle % 40 < 20 ? 1 : 0)) != 0 ? "|" : "")
								.toString(),
							(y + (child.height / 2) + 6));
					}
					else
					{
						boldText.method389(1, (x + 8), ColorConfig.WHITE_COLOR, builder.append(message).append(
								((RSInterface.currentInputField == child ? 1 : 0) & (loopCycle % 40 < 20 ? 1 : 0)) != 0
									? "|"
									: "")
							.toString(), (y + (child.height / 2) + 6));
					}
				}
		}
		DrawingArea.setDrawingArea(l1, i1, k1, j1);
	}

	public static void drawBlackBox(int xPos, int yPos)
	{
		DrawingArea.drawPixels(71, yPos - 1, xPos - 2, 0x726451, 1);
		DrawingArea.drawPixels(69, yPos, xPos + 174, 0x726451, 1);
		DrawingArea.drawPixels(1, yPos - 2, xPos - 2, 0x726451, 178);
		DrawingArea.drawPixels(1, yPos + 68, xPos, 0x726451, 174);
		DrawingArea.drawPixels(71, yPos - 1, xPos - 1, 0x2E2B23, 1);
		DrawingArea.drawPixels(71, yPos - 1, xPos + 175, 0x2E2B23, 1);
		DrawingArea.drawPixels(1, yPos - 1, xPos, 0x2E2B23, 175);
		DrawingArea.drawPixels(1, yPos + 69, xPos, 0x2E2B23, 175);
		DrawingArea.method335(0, yPos, 174, 68, 220, xPos);
	}


}
