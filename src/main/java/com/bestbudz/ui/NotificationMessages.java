package com.bestbudz.ui;

import com.bestbudz.data.ItemDef;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.gamerender.DrawingArea;

public class NotificationMessages extends Client
{
	public static void pushKill(String killer, String victim, int weapon, boolean poison)
	{
		for (int index = killsDisplayed - 1; index > 0; index--)
		{
			feedKiller[index] = feedKiller[index - 1];
			feedVictim[index] = feedVictim[index - 1];
			feedWeapon[index] = feedWeapon[index - 1];
			feedPoison[index] = feedPoison[index - 1];
			feedAlpha[index] = feedAlpha[index - 1];
			feedYPos[index] = feedYPos[index - 1];
		}
		feedKiller[0] = killer;
		feedVictim[0] = victim;
		feedWeapon[0] = weapon;
		feedPoison[0] = poison;
		feedAlpha[0] = 0;
		feedYPos[0] = 0;
	}

	public static void displayKillFeed()
	{
		int x = 5;
		for (int index = 0; index < killsDisplayed; index++)
		{
			if (feedKiller[index] != null && feedVictim[index] != null)
			{
				if (feedKiller[index].length() > 0)
				{
					if (feedWeapon[index] == -1)
					{
						return;
					}
					if (feedKiller[index].equalsIgnoreCase(myUsername))
					{
						feedKiller[index] = "You";
					}
					if (feedVictim[index].equalsIgnoreCase(myUsername))
					{
						feedVictim[index] = "You";
					}
					if (feedYPos[index] < (index + 1) * 22)
					{
						feedYPos[index] += 1;
						if (index == 0)
						{
							feedAlpha[index] += 256 / 22;
						}
					}
					else if (feedYPos[index] == (index + 1) * 22)
					{
						if (feedAlpha[index] > 200)
						{
							feedAlpha[index] -= 1;
						}
						else if (feedAlpha[index] <= 200 && feedAlpha[index] > 0)
						{
							feedAlpha[index] -= 5;
						}
						if (feedAlpha[index] < 0)
						{
							feedAlpha[index] = 0;
						}
						if (feedAlpha[index] == 0)
						{
							clearKill(index);
						}
					}
					if (feedAlpha[index] != 0)
					{
						String killerText = "[" + feedKiller[index] + "] ";
						String victimText = " [" + feedVictim[index] + "]";
						String posionText = " <col=00ff00>[BADTRIP]</col>";
						DrawingArea.drawAlphaGradient(x, feedYPos[index],
							newSmallFont.getTextWidth(
								killerText + victimText + (feedPoison[index] ? posionText : "")) + 22,
							19, 0, 0, feedAlpha[index]);
						newSmallFont.drawBasicString("<trans=" + feedAlpha[index] + ">" + killerText, x + 3,
							feedYPos[index] + 14, ColorConfig.WHITE_COLOR, 0);
						newSmallFont.drawBasicString(
							"<trans=" + feedAlpha[index] + ">" + victimText + (feedPoison[index] ? posionText : ""),
							x + 3 + newSmallFont.getTextWidth(killerText) + 16, feedYPos[index] + 14, ColorConfig.WHITE_COLOR, 0);
						if (feedWeapon[index] != -1 && feedWeapon[index] != 65535)
						{
							feedImage[index] = ItemDef.getSprite(feedWeapon[index], 0, 0x000000, 2);
						}
						if (feedImage[index] != null)
						{
							feedImage[index].drawTransparentSprite(newSmallFont.getTextWidth(killerText),
								feedYPos[index] - 6, feedAlpha[index]);
						}
					}
				}
			}
		}
	}

	public static void clearKill(int index)
	{
		feedKiller[index] = null;
		feedVictim[index] = null;
		feedWeapon[index] = -1;
		feedPoison[index] = false;
		feedAlpha[index] = -1;
		feedYPos[index] = -1;
	}

}
