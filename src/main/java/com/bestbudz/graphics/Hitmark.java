package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.graphics.sprite.Sprite;

public class Hitmark extends Client
{

	// Enhanced hitmark drawing function using old system sprites
	public static void enhancedHitmarkDraw(int hitLength, int type, int icon, int damage, int drawX, int drawY, int opacity) {
		if (damage > 0) {
			// Draw damage type icon with animation (kept from enhanced version)
			if (icon <= 6 && opacity > 50) { // Only show icon when visible enough
				hitIcon[icon].drawTransparentSprite(drawX - 34, drawY - 15, opacity);
			}

			// Use the old hitmark sprites instead of the enhanced ones
			if (opacity > 0) {
				// Draw the hitmark sprite background (same as old system)
				hitMarks[type].drawTransparentSprite(drawX - 12, drawY - 12, opacity);
			}

			// Draw damage text with opacity-based visibility (same style as old system)
			if (opacity > 100) {
				// Full opacity - draw shadow and main text
				regularText.drawText(0, String.valueOf(damage), drawY + 5, drawX - 2);
				regularText.drawText(ColorConfig.WHITE_COLOR, String.valueOf(damage), drawY + 4, drawX - 3);
			} else if (opacity > 50) {
				// Faded text for lower opacity - single text, no shadow
				regularText.drawText(0x909090, String.valueOf(damage), drawY + 4, drawX - 3);
			}

		} else {
			// Miss/block indicator - could use hitMarks[0] or a specific miss sprite
			if (opacity > 0) {
				// You might want to use a specific hitmark type for misses/blocks
				hitMarks[0].drawTransparentSprite(drawX - 12, drawY - 12, opacity);
			}
		}
	}

}