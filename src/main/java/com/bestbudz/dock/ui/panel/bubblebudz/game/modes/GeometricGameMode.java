package com.bestbudz.dock.ui.panel.bubblebudz.game.modes;

import com.bestbudz.dock.ui.panel.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.bubblebudz.game.scoring.BubbleBudzScore;

public class GeometricGameMode extends GameMode {

	@Override
	public void initialize() {

	}

	@Override
	public boolean isRoundComplete(long currentTime) {
		long roundElapsed = currentTime - roundStartTime;
		return roundElapsed >= BubbleBudzConfig.ROUND_DURATION;
	}

	@Override
	public int calculateScore(Bubble bubble, int panelHeight) {
		int baseScore = BubbleBudzScore.calculateScore(bubble.currentRadius, panelHeight);

		if (bubble instanceof GeometricShape) {
			GeometricShape shape = (GeometricShape) bubble;
			return (int) (baseScore * shape.getScoreMultiplier());
		}

		return baseScore;
	}

	@Override
	public void onRoundComplete() {

	}
}
