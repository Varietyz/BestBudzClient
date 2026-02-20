package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;

public class Hitmark extends Client
{

	public static void enhancedHitmarkDraw(int hitLength, int type, int icon, int damage, int drawX, int drawY, int opacity) {
		if (damage > 0) {

			if (icon <= 6 && opacity > 50) {
				hitIcon[icon].drawTransparentSprite(drawX - 34, drawY - 15, opacity);
			}

			if (opacity > 0) {

				hitMarks[type].drawTransparentSprite(drawX - 12, drawY - 12, opacity);
			}

			if (opacity > 100) {

				regularText.drawText(0, String.valueOf(damage), drawY + 5, drawX - 2);
				regularText.drawText(ColorConfig.WHITE_COLOR, String.valueOf(damage), drawY + 4, drawX - 3);
			} else if (opacity > 50) {

				regularText.drawText(0x909090, String.valueOf(damage), drawY + 4, drawX - 3);
			}

		} else {

			if (opacity > 0) {

				hitMarks[0].drawTransparentSprite(drawX - 12, drawY - 12, opacity);
			}
		}
	}

}
