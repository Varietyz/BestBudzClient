package com.bestbudz.dock.ui.panel.bubblebudz.game.modes;

import com.bestbudz.dock.ui.panel.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.bubblebudz.game.scoring.BubbleBudzScore;

/**
 * Game mode featuring randomized geometric shapes instead of circular bubbles.
 * Shapes have different scoring multipliers based on complexity.
 */
public class GeometricGameMode extends GameMode {

	@Override
	public void initialize() {
		// Geometric mode could have slightly longer rounds to accommodate learning curve
		// Or different spawn rates - this can be configured as needed
	}

	@Override
	public boolean isRoundComplete(long currentTime) {
		long roundElapsed = currentTime - roundStartTime;
		return roundElapsed >= BubbleBudzConfig.ROUND_DURATION;
	}

	@Override
	public int calculateScore(Bubble bubble, int panelHeight) {
		int baseScore = BubbleBudzScore.calculateScore(bubble.currentRadius, panelHeight);

		// Apply shape multiplier if this is a geometric shape
		if (bubble instanceof GeometricShape) {
			GeometricShape shape = (GeometricShape) bubble;
			return (int) (baseScore * shape.getScoreMultiplier());
		}

		return baseScore;
	}

	@Override
	public void onRoundComplete() {
		// Could add special effects or bonuses for geometric mode
	}
}