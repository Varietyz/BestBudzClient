package com.bestbudz.dock.ui.panel.bubblebudz.game.modes;

import com.bestbudz.dock.ui.panel.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.bubblebudz.game.scoring.BubbleBudzScore;

public class ClassicGameMode extends GameMode {

	@Override
	public void initialize() {
		// Classic mode uses default configuration
	}

	@Override
	public boolean isRoundComplete(long currentTime) {
		long roundElapsed = currentTime - roundStartTime;
		return roundElapsed >= BubbleBudzConfig.ROUND_DURATION;
	}

	@Override
	public int calculateScore(Bubble bubble, int panelHeight) {
		return BubbleBudzScore.calculateScore(bubble.currentRadius, panelHeight);
	}

	@Override
	public void onRoundComplete() {
		// Classic mode just resets - logic handled by main game
	}
}