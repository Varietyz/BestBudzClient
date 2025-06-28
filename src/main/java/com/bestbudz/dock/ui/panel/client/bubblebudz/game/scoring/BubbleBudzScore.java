package com.bestbudz.dock.ui.panel.client.bubblebudz.game.scoring;

import com.bestbudz.dock.ui.panel.client.bubblebudz.config.BubbleBudzConfig;

public class BubbleBudzScore
{
	public static int calculateScore(float currentRadius, int panelHeight) {
		// Calculate score based on visual size for fairness
		int basePoints = Math.max(1, (int)(21 - currentRadius));
		float heightMultiplier = Math.max(1.0f, (float)panelHeight / BubbleBudzConfig.BASE_VIEWPORT_HEIGHT);
		int heightBonus = (int)(basePoints * (heightMultiplier - 1.0f) * 0.5f);
		return basePoints + heightBonus;
	}

}