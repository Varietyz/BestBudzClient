package com.bestbudz.dock.ui.panel.bubblebudz.game.modes;

import com.bestbudz.dock.ui.panel.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;

public abstract class GameMode {
	protected BubbleBudzConfig config;
	protected long roundStartTime;
	protected int currentScore;

	public GameMode() {
		this.config = new BubbleBudzConfig(); // Default config
	}

	public abstract void initialize();
	public abstract boolean isRoundComplete(long currentTime);
	public abstract int calculateScore(Bubble bubble, int panelHeight);
	public abstract void onRoundComplete();

	public void setRoundStartTime(long time) { this.roundStartTime = time; }
	public void setCurrentScore(int score) { this.currentScore = score; }
	public long getRoundStartTime() { return roundStartTime; }
	public int getCurrentScore() { return currentScore; }
}
