package com.bestbudz.entity;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.entity.ParseAndUpdateEntities.npcScreenPos;
import static com.bestbudz.graphics.Hitmark.enhancedHitmarkDraw;
import com.bestbudz.graphics.hpbar.HPbar;
import com.bestbudz.network.Stream;
import static com.bestbudz.ui.interfaces.Chatbox.publicChatMode;

public class UpdateEntities extends Client
{
	public static void updateEntities()
	{
		try
		{
			int chatMessageCount = 0;
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
						entityDef = entityDef.getTransformedEntity();
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
					if (j >= 0 && crosshairType == 10 && targetPlayerIndex == stonerIndices[j])
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIconsHint[stoner.hintIcon].drawSprite(spriteDrawX - 12, spriteDrawY - l);
					}
				}
				else
				{
					EntityDef entityDef_1 = ((Npc) obj).desc;
					if (entityDef_1.mapIconId >= 0 && entityDef_1.mapIconId < headIcons.length)
					{
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);
						if (spriteDrawX > -1)
							headIcons[entityDef_1.mapIconId].drawSprite(spriteDrawX - 12, spriteDrawY - 30);
					}
					if (crosshairType == 1 && targetNpcIndex == npcIndices[j - stonerCount] && loopCycle % 20 < 10)
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
					if (spriteDrawX > -1 && chatMessageCount < maxChatMessages)
					{
						chatMessageWidth[chatMessageCount] = boldText.method384(((Entity) (obj)).textSpoken) / 2;
						chatMessageHeight[chatMessageCount] = boldText.getFontHeight();
						chatMessageX[chatMessageCount] = spriteDrawX;
						chatMessageY[chatMessageCount] = spriteDrawY;
						chatMessageTypes[chatMessageCount] = ((Entity) (obj)).chatType;
						chatMessageEffects[chatMessageCount] = ((Entity) (obj)).chatEffect;
						chatMessageCycles[chatMessageCount] = ((Entity) (obj)).textCycle;
						chatMessageTexts[chatMessageCount++] = ((Entity) (obj)).textSpoken;
						if (chatDisplayMode == 0 && ((Entity) (obj)).chatEffect >= 1 && ((Entity) (obj)).chatEffect <= 3)
						{
							chatMessageHeight[chatMessageCount] += 10;
							chatMessageY[chatMessageCount] += 5;
						}
						if (chatDisplayMode == 0 && ((Entity) (obj)).chatEffect == 4)
							chatMessageWidth[chatMessageCount] = 60;
						if (chatDisplayMode == 0 && ((Entity) (obj)).chatEffect == 5)
							chatMessageHeight[chatMessageCount] += 5;
					}
				}
				if (((Entity) (obj)).loopCycleStatus > loopCycle) {
					try {
						npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height + 15);

						// SIMPLE: Is the entity actually visible on screen?
						int screenWidth = Rasterizer.viewportCenterX * 2;
						int screenHeight = Rasterizer.viewportCenterY * 2;

						boolean entityOnScreen = (spriteDrawX >= 0 && spriteDrawX <= screenWidth-125 &&
							spriteDrawY >= 0 && spriteDrawY <= screenHeight);

						if (entityOnScreen) {
							Entity entity = (Entity) obj;
							long currentHealth = entity.currentHealth;
							long maxHealth = entity.maxHealth;

							if (maxHealth > 0) {
								int barX = spriteDrawX;
								int barY = spriteDrawY - 3;
								HPbar.drawHPBar(barX, barY, currentHealth, maxHealth);
							}
						}
						// If not on screen = no HP bar. Period.

					} catch (Exception e) {
						System.err.println("Error drawing HP bar: " + e.getMessage());
					}
				}
				if (!SettingsConfig.enableNewHitmarks)
				{
					int maxHitmarks = Math.min(7, ((Entity) (obj)).hitsLoopCycle.length); // Prevent array out of bounds
					for (int j1 = 0; j1 < maxHitmarks; j1++) {
						if (j1 < ((Entity) (obj)).hitsLoopCycle.length && ((Entity) (obj)).hitsLoopCycle[j1] > loopCycle) {
							npcScreenPos(((Entity) (obj)), ((Entity) (obj)).height / 2);
							if (spriteDrawX > -1) {
								Entity e = ((Entity) (obj));

								// Calculate position offsets for 7-point spread
								int offsetX = 0, offsetY = 0;
								switch (j1) {
									case 0: // Center
										offsetX = 0; offsetY = 0;
										break;
									case 1: // Top
										offsetX = 0; offsetY = -25;
										break;
									case 2: // Left
										offsetX = -25; offsetY = 0;
										break;
									case 3: // Right
										offsetX = 25; offsetY = 0;
										break;
									case 4: // Top-Left diagonal
										offsetX = -18; offsetY = -18;
										break;
									case 5: // Top-Right diagonal
										offsetX = 18; offsetY = -18;
										break;
									case 6: // Bottom (or could be bottom-left/right)
										offsetX = 0; offsetY = 20;
										break;
								}

								// Apply animation movement with fade
								if (j1 < e.hitmarkMove.length && e.hitmarkMove[j1] > -30) {
									e.hitmarkMove[j1]--; // Animation frame counter
								}
								if (j1 < e.hitmarkTrans.length && e.hitmarkMove[j1] < -26) {
									e.hitmarkTrans[j1] -= 5; // Fade out
								}

								// Calculate movement based on damage type
								int animationFrame = Math.abs(e.hitmarkMove[j1]);
								int movementX = 0, movementY = 0;

								// Check if this is damage (positive value) or defensive (0 or negative)
								boolean isDamage = e.hitArray[j1] > 0;

								if (isDamage) {
									// Damage hitmarks move diagonally up-right (reduced distance)
									movementX = (int)(animationFrame * 0.5); // Move right slower
									movementY = (int)(animationFrame * -0.8); // Move up slower
								} else {
									// Defensive hitmarks (blocks, misses) move straight up (reduced distance)
									movementX = 0; // No horizontal movement
									movementY = (int)(animationFrame * -0.7); // Move straight up slower
								}

								// Calculate final position with spread offset and movement animation
								int finalX = spriteDrawX + offsetX + movementX;
								int finalY = spriteDrawY + offsetY + movementY;

								// Position defensive hitmarks lower on the entity
								if (!isDamage) {
									finalY += 25; // Move defensive hitmarks down by 25 pixels
								}
								int currentOpacity = j1 < e.hitmarkTrans.length ? e.hitmarkTrans[j1] : 255;

								// Draw the enhanced hitmark with appropriate movement
								enhancedHitmarkDraw(String.valueOf(e.hitArray[j1]).length(),
									e.hitMarkTypes[j1], e.hitIcon[j1], e.hitArray[j1],
									finalX, finalY, currentOpacity);
							}
						}
					}
				}
				else
				{// MAIN HITMARK LOGIC
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
								regularText.drawText(0, String.valueOf(((Entity) (obj)).hitArray[j1]), spriteDrawY + 5,
									spriteDrawX - 2);
								regularText.drawText(ColorConfig.WHITE_COLOR, String.valueOf(((Entity) (obj)).hitArray[j1]),
									spriteDrawY + 4, spriteDrawX - 3);
							}
						}
					}
				}
			}
			for (int k = 0; k < chatMessageCount; k++)
			{
				int k1 = chatMessageX[k];
				int l1 = chatMessageY[k];
				int j2 = chatMessageWidth[k];
				int k2 = chatMessageHeight[k];
				boolean flag = true;
				while (flag)
				{
					flag = false;
					for (int l2 = 0; l2 < k; l2++)
						if (l1 + 2 > chatMessageY[l2] - chatMessageHeight[l2] && l1 - k2 < chatMessageY[l2] + 2
							&& k1 - j2 < chatMessageX[l2] + chatMessageWidth[l2]
							&& k1 + j2 > chatMessageX[l2] - chatMessageWidth[l2]
							&& chatMessageY[l2] - chatMessageHeight[l2] < l1)
						{
							l1 = chatMessageY[l2] - chatMessageHeight[l2];
							flag = true;
						}

				}
				spriteDrawX = chatMessageX[k];
				spriteDrawY = chatMessageY[k] = l1;
				String s = chatMessageTexts[k];
				if (chatDisplayMode == 0)
				{
					int i3 = ColorConfig.CHAT_COLOR; // default pastel yellow

					if (chatMessageTypes[k] < 6)
						i3 = configuredChatColors[chatMessageTypes[k]]; // already modernized

					if (chatMessageTypes[k] == 6) {
						// Flashing between yellow and pink
						i3 = currentTick % 20 >= 10 ? ColorConfig.CHAT_COLOR : ColorConfig.CHAT_SOFT_PINK; // pastel yellow ↔ soft pink
					}

					if (chatMessageTypes[k] == 7) {
						// Flashing between mint and cyan
						i3 = currentTick % 20 >= 10 ? 0xB2F2BB : ColorConfig.CHAT_MINT_AQUA; // mint green ↔ aqua mint
					}

					if (chatMessageTypes[k] == 8) {
						// Flashing between lime and peach
						i3 = currentTick % 20 >= 10 ? 0xD0F4DE : 0xFFD180; // light green ↔ peach
					}

					if (chatMessageTypes[k] == 9) {
						// Smooth rainbow scroll (remap red → magenta, yellow → violet, green → blue)
						int j3 = 150 - chatMessageCycles[k];
						if (j3 < 50)
							i3 = ColorConfig.CHAT_SOFT_PINK + 50 * j3; // soft pink → lighter
						else if (j3 < 100)
							i3 = 0xCE93D8 - 0x30000 * (j3 - 50); // pastel violet → lavender
						else if (j3 < 150)
							i3 = ColorConfig.CHAT_BABY_BLUE + 20 * (j3 - 100); // baby blue → mint
					}

					if (chatMessageTypes[k] == 10) {
						// Oscillate between pink and yellow then fade to green
						int k3 = 150 - chatMessageCycles[k];
						if (k3 < 50)
							i3 = ColorConfig.CHAT_SOFT_PINK + 3 * k3; // pastel pink → orange-pink
						else if (k3 < 100)
							i3 = ColorConfig.CHAT_COLOR - 0x20000 * (k3 - 50); // yellow → peach
						else if (k3 < 150)
							i3 = (0xB2FF59 + 0x20000 * (k3 - 100)) - 3 * (k3 - 100); // lime fade
					}

					if (chatMessageTypes[k] == 11) {
						// White fade to mint then fade to blue
						int l3 = 150 - chatMessageCycles[k];
						if (l3 < 50)
							i3 = ColorConfig.WHITE_COLOR - 0x10101 * l3; // white → light gray
						else if (l3 < 100)
							i3 = ColorConfig.CHAT_MINT_AQUA + 0x10101 * (l3 - 50); // mint → frost teal
						else if (l3 < 150)
							i3 = 0x90CAF9 - 0x10000 * (l3 - 100); // soft blue → light mint
					}

					if (chatMessageEffects[k] == 0)
					{
						boldText.drawText(0, s, spriteDrawY + 1, spriteDrawX);
						boldText.drawText(i3, s, spriteDrawY, spriteDrawX);
					}
					if (chatMessageEffects[k] == 1)
					{
						boldText.method386(0, s, spriteDrawX, currentTick, spriteDrawY + 1);
						boldText.method386(i3, s, spriteDrawX, currentTick, spriteDrawY);
					}
					if (chatMessageEffects[k] == 2)
					{
						boldText.method387(spriteDrawX, s, currentTick, spriteDrawY + 1, 0);
						boldText.method387(spriteDrawX, s, currentTick, spriteDrawY, i3);
					}
					if (chatMessageEffects[k] == 3)
					{
						boldText.method388(150 - chatMessageCycles[k], s, currentTick, spriteDrawY + 1, spriteDrawX, 0);
						boldText.method388(150 - chatMessageCycles[k], s, currentTick, spriteDrawY, spriteDrawX, i3);
					}
					if (chatMessageEffects[k] == 4)
					{
						int i4 = boldText.method384(s);
						int k4 = ((150 - chatMessageCycles[k]) * (i4 + 100)) / 150;
						DrawingArea.setDrawingArea(334, spriteDrawX - 50, spriteDrawX + 50, 0);
						boldText.method385(0, s, spriteDrawY + 1, (spriteDrawX + 50) - k4);
						boldText.method385(i3, s, spriteDrawY, (spriteDrawX + 50) - k4);
						DrawingArea.defaultDrawingAreaSize();
					}
					if (chatMessageEffects[k] == 5)
					{
						int j4 = 150 - chatMessageCycles[k];
						int l4 = 0;
						if (j4 < 25)
							l4 = j4 - 25;
						else if (j4 > 125)
							l4 = j4 - 125;
						DrawingArea.setDrawingArea(spriteDrawY + 5, 0, 512, spriteDrawY - boldText.getFontHeight() - 1);
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
