package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.graphics.sprite.Sprite;

public class Hitmark extends Client
{
	public static void hitmarkDraw(int hitLength, int type, int icon, int damage, int move, int opacity)
	{
		if (damage > 0)
		{
			Sprite end1 = null, middle = null, end2 = null;
			int x = 0;
			switch (hitLength)
			{
				case 1:
					x = 8;
					break;
				case 2:
					x = 4;
					break;
				case 3:
					x = 1;
					break;
			}
			switch (type)
			{
				case 1:
					end1 = hitMark[0];
					middle = hitMark[1];
					end2 = hitMark[2];
					break;
				case 3:
					end1 = hitMark[3];
					middle = hitMark[4];
					end2 = hitMark[5];
					break;
				case 2:
					end1 = hitMark[6];
					middle = hitMark[7];
					end2 = hitMark[8];
					break;
			}
			if (icon <= 6)
				hitIcon[icon].drawTransparentSprite(spriteDrawX - 34 + x, spriteDrawY - 14 + move, opacity);
			end1.drawTransparentSprite(spriteDrawX - 12 + x, spriteDrawY - 12 + move, opacity);
			x += 4;
			for (int i = 0; i < hitLength * 2; i++)
			{
				middle.drawTransparentSprite(spriteDrawX - 12 + x, spriteDrawY - 12 + move, opacity);
				x += 4;
			}
			end2.drawTransparentSprite(spriteDrawX - 12 + x, spriteDrawY - 12 + move, opacity);
			if (opacity > 100)
				smallText.drawText(ColorConfig.WHITE_COLOR, String.valueOf(damage), spriteDrawY + 3 + move, spriteDrawX + 4);
		}
		else
		{
			cacheSprite[474].drawTransparentSprite(spriteDrawX - 12, spriteDrawY - 14 + move, opacity);
		}
	}
}
