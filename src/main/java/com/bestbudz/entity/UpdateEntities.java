package com.bestbudz.entity;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.graphics.DrawingArea;
import static com.bestbudz.graphics.Hitmark.hitmarkDraw;
import com.bestbudz.graphics.sprite.Sprite;
import static com.bestbudz.ui.interfaces.Chatbox.publicChatMode;

public class UpdateEntities extends Client
{
	public static void updateEntities()
	{
		try
		{
			int anInt974 = 0;
			for (int j = -1; j < stonerCount + npcCount; j++)
			{
				Object obj;
				if (j == -1)
					obj = myStoner;
				else if (j < stonerCount)
					obj = stonerArray[stonerIndices[j]];
				else
					obj = npcArray[npcIndices[j - stonerCount]];
				if (obj == null || !((Entity) (obj)).isVisible())
					continue;
				if (obj instanceof Npc)
				{
					EntityDef entityDef = ((Npc) obj).desc;
					if (entityDef.childrenIDs != null)
						entityDef = entityDef.method161();
					if (entityDef == null)
						continue;
				}
				if (j < stonerCount)
				{
					int l = 30;
					Stoner stoner = (Stoner) obj;
					if (stoner.headIcon >= 0)
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
						{
							if (stoner.skullIcon < 2)
							{
								skullIcons[stoner.skullIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 25;
							}
							if (stoner.headIcon < 13)
							{
								headIcons[stoner.headIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
								l += 18;
							}
						}
					}
					if (j >= 0 && anInt855 == 10 && anInt933 == stonerIndices[j])
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[stoner.hintIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
					}
				}
				else
				{
					EntityDef entityDef_1 = ((Npc) obj).desc;
					if (entityDef_1.anInt75 >= 0 && entityDef_1.anInt75 < headIcons.length)
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIcons[entityDef_1.anInt75].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
					}
					if (anInt855 == 1 && anInt1222 == npcIndices[j - stonerCount] && loopCycle % 20 < 10)
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[0].drawSprite(spriteDrawX - 12, spriteDrawY - 28);
					}
				}
				if (((Entity) (obj)).textSpoken != null && (j >= stonerCount || publicChatMode == 0
					|| publicChatMode == 3 || publicChatMode == 1 && isStonerOrSelf(((Stoner) obj).name)))
				{
					npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height);
					if (spriteDrawX > -1 && anInt974 < anInt975)
					{
						anIntArray979[anInt974] = boldText.method384(((Entity) (obj)).textSpoken) / 2;
						anIntArray978[anInt974] = boldText.anInt1497;
						anIntArray976[anInt974] = spriteDrawX;
						anIntArray977[anInt974] = spriteDrawY;
						anIntArray980[anInt974] = ((Entity) (obj)).anInt1513;
						anIntArray981[anInt974] = ((Entity) (obj)).anInt1531;
						anIntArray982[anInt974] = ((Entity) (obj)).textCycle;
						aStringArray983[anInt974++] = ((Entity) (obj)).textSpoken;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 >= 1 && ((Entity) (obj)).anInt1531 <= 3)
						{
							anIntArray978[anInt974] += 10;
							anIntArray977[anInt974] += 5;
						}
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 4)
							anIntArray979[anInt974] = 60;
						if (anInt1249 == 0 && ((Entity) (obj)).anInt1531 == 5)
							anIntArray978[anInt974] += 5;
					}
				}
				if (((Entity) (obj)).loopCycleStatus > loopCycle)
				{
					try
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
						{
							int i1 = (((Entity) (obj)).currentHealth * 30) / ((Entity) (obj)).maxHealth;
							if (i1 > 30)
							{
								i1 = 30;
							}
							int barWidth = hpBars[0].cropWidth;
							int percent = (((Entity) (obj)).currentHealth * barWidth) / ((Entity) (obj)).maxHealth;
							if (percent > barWidth)
							{
								percent = barWidth;
							}
							if (!SettingsConfig.enableNewHpBars)
							{
								DrawingArea.drawPixels(5, spriteDrawY - 3, spriteDrawX - 15, 65280, i1);
								DrawingArea.drawPixels(5, spriteDrawY - 3, (spriteDrawX - 15) + i1, 0xff0000, 30 - i1);
							}
							else
							{
								hpBars[1].drawSprite(spriteDrawX - (barWidth / 2), spriteDrawY - 3);
								if (percent > 0)
								{
									Sprite fullBar = new Sprite(hpBars[0], 0, 0, percent, 7);
									fullBar.drawSprite(spriteDrawX - (barWidth / 2), spriteDrawY - 3);
								}
							}
							if (SettingsConfig.drawEntityFeed)
							{

							}
						}
					}
					catch (Exception e)
					{
					}
				}
				if (SettingsConfig.enableNewHitmarks)
				{
					for (int j1 = 0; j1 < 4; j1++)
					{
						if (((Entity) (obj)).hitsLoopCycle[j1] > loopCycle)
						{
							npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
							if (spriteDrawX > -1)
							{
								switch (j1)
								{
									case 1:
										spriteDrawY += 20;
										break;
									case 2:
										spriteDrawY += 40;
										break;
									case 3:
										spriteDrawY += 60;
										break;
									case 4:
										spriteDrawY += 80;
										break;
									case 5:
										spriteDrawY += 100;
										break;
									case 6:
										spriteDrawY += 120;
										break;
								}
								Entity e = ((Entity) (obj));
								if (e.hitmarkMove[j1] > -30)
									e.hitmarkMove[j1]--;
								if (e.hitmarkMove[j1] < -26)
									e.hitmarkTrans[j1] -= 5;
								hitmarkDraw(String.valueOf(e.hitArray[j1]).length(), e.hitMarkTypes[j1], e.hitIcon[j1],
									e.hitArray[j1], e.hitmarkMove[j1], e.hitmarkTrans[j1]);
							}
						}
					}
				}
				else
				{
					for (int j1 = 0; j1 < 4; j1++)
					{
						if (((Entity) (obj)).hitsLoopCycle[j1] > loopCycle)
						{
							npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
							if (spriteDrawX > -1)
							{
								if (j1 == 1)
								{
									spriteDrawY -= 20;
								}
								if (j1 == 2)
								{
									spriteDrawX -= 15;
									spriteDrawY -= 10;
								}
								if (j1 == 3)
								{
									spriteDrawX += 15;
									spriteDrawY -= 10;
								}
								hitMarks[((Entity) (obj)).hitMarkTypes[j1]].drawSprite(spriteDrawX - 12,
									spriteDrawY - 12);
								smallText.drawText(0, String.valueOf(((Entity) (obj)).hitArray[j1]), spriteDrawY + 4,
									spriteDrawX);
								smallText.drawText(ColorConfig.WHITE_COLOR, String.valueOf(((Entity) (obj)).hitArray[j1]),
									spriteDrawY + 3, spriteDrawX - 1);
							}
						}
					}
				}
			}
			for (int k = 0; k < anInt974; k++)
			{
				int k1 = anIntArray976[k];
				int l1 = anIntArray977[k];
				int j2 = anIntArray979[k];
				int k2 = anIntArray978[k];
				boolean flag = true;
				while (flag)
				{
					flag = false;
					for (int l2 = 0; l2 < k; l2++)
						if (l1 + 2 > anIntArray977[l2] - anIntArray978[l2] && l1 - k2 < anIntArray977[l2] + 2
							&& k1 - j2 < anIntArray976[l2] + anIntArray979[l2]
							&& k1 + j2 > anIntArray976[l2] - anIntArray979[l2]
							&& anIntArray977[l2] - anIntArray978[l2] < l1)
						{
							l1 = anIntArray977[l2] - anIntArray978[l2];
							flag = true;
						}

				}
				spriteDrawX = anIntArray976[k];
				spriteDrawY = anIntArray977[k] = l1;
				String s = aStringArray983[k];
				if (anInt1249 == 0)
				{
					int i3 = ColorConfig.CHAT_COLOR; // default pastel yellow

					if (anIntArray980[k] < 6)
						i3 = anIntArray965[anIntArray980[k]]; // already modernized

					if (anIntArray980[k] == 6) {
						// Flashing between yellow and pink
						i3 = anInt1265 % 20 >= 10 ? ColorConfig.CHAT_COLOR : ColorConfig.CHAT_SOFT_PINK; // pastel yellow ↔ soft pink
					}

					if (anIntArray980[k] == 7) {
						// Flashing between mint and cyan
						i3 = anInt1265 % 20 >= 10 ? 0xB2F2BB : ColorConfig.CHAT_MINT_AQUA; // mint green ↔ aqua mint
					}

					if (anIntArray980[k] == 8) {
						// Flashing between lime and peach
						i3 = anInt1265 % 20 >= 10 ? 0xD0F4DE : 0xFFD180; // light green ↔ peach
					}

					if (anIntArray980[k] == 9) {
						// Smooth rainbow scroll (remap red → magenta, yellow → violet, green → blue)
						int j3 = 150 - anIntArray982[k];
						if (j3 < 50)
							i3 = ColorConfig.CHAT_SOFT_PINK + 50 * j3; // soft pink → lighter
						else if (j3 < 100)
							i3 = 0xCE93D8 - 0x30000 * (j3 - 50); // pastel violet → lavender
						else if (j3 < 150)
							i3 = ColorConfig.CHAT_BABY_BLUE + 20 * (j3 - 100); // baby blue → mint
					}

					if (anIntArray980[k] == 10) {
						// Oscillate between pink and yellow then fade to green
						int k3 = 150 - anIntArray982[k];
						if (k3 < 50)
							i3 = ColorConfig.CHAT_SOFT_PINK + 3 * k3; // pastel pink → orange-pink
						else if (k3 < 100)
							i3 = ColorConfig.CHAT_COLOR - 0x20000 * (k3 - 50); // yellow → peach
						else if (k3 < 150)
							i3 = (0xB2FF59 + 0x20000 * (k3 - 100)) - 3 * (k3 - 100); // lime fade
					}

					if (anIntArray980[k] == 11) {
						// White fade to mint then fade to blue
						int l3 = 150 - anIntArray982[k];
						if (l3 < 50)
							i3 = ColorConfig.WHITE_COLOR - 0x10101 * l3; // white → light gray
						else if (l3 < 100)
							i3 = ColorConfig.CHAT_MINT_AQUA + 0x10101 * (l3 - 50); // mint → frost teal
						else if (l3 < 150)
							i3 = 0x90CAF9 - 0x10000 * (l3 - 100); // soft blue → light mint
					}

					if (anIntArray981[k] == 0)
					{
						boldText.drawText(0, s, spriteDrawY + 1, spriteDrawX);
						boldText.drawText(i3, s, spriteDrawY, spriteDrawX);
					}
					if (anIntArray981[k] == 1)
					{
						boldText.method386(0, s, spriteDrawX, anInt1265, spriteDrawY + 1);
						boldText.method386(i3, s, spriteDrawX, anInt1265, spriteDrawY);
					}
					if (anIntArray981[k] == 2)
					{
						boldText.method387(spriteDrawX, s, anInt1265, spriteDrawY + 1, 0);
						boldText.method387(spriteDrawX, s, anInt1265, spriteDrawY, i3);
					}
					if (anIntArray981[k] == 3)
					{
						boldText.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY + 1, spriteDrawX, 0);
						boldText.method388(150 - anIntArray982[k], s, anInt1265, spriteDrawY, spriteDrawX, i3);
					}
					if (anIntArray981[k] == 4)
					{
						int i4 = boldText.method384(s);
						int k4 = ((150 - anIntArray982[k]) * (i4 + 100)) / 150;
						DrawingArea.setDrawingArea(334, spriteDrawX - 50, spriteDrawX + 50, 0);
						boldText.method385(0, s, spriteDrawY + 1, (spriteDrawX + 50) - k4);
						boldText.method385(i3, s, spriteDrawY, (spriteDrawX + 50) - k4);
						DrawingArea.defaultDrawingAreaSize();
					}
					if (anIntArray981[k] == 5)
					{
						int j4 = 150 - anIntArray982[k];
						int l4 = 0;
						if (j4 < 25)
							l4 = j4 - 25;
						else if (j4 > 125)
							l4 = j4 - 125;
						DrawingArea.setDrawingArea(spriteDrawY + 5, 0, 512, spriteDrawY - boldText.anInt1497 - 1);
						boldText.drawText(0, s, spriteDrawY + 1 + l4, spriteDrawX);
						boldText.drawText(i3, s, spriteDrawY + l4, spriteDrawX);
						DrawingArea.defaultDrawingAreaSize();
					}
				}
				else
				{
					boldText.drawText(0, s, spriteDrawY + 1, spriteDrawX);
					boldText.drawText(0xffff00, s, spriteDrawY, spriteDrawX);
				}
			}
		}
		catch (Exception e)
		{
		}
	}
}
