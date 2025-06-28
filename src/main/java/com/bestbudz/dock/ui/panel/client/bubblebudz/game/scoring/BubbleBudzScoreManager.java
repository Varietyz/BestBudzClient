package com.bestbudz.dock.ui.panel.client.bubblebudz.game.scoring;

import com.bestbudz.dock.ui.panel.client.bubblebudz.data.BubbleBudzLoader;
import com.bestbudz.dock.ui.panel.client.bubblebudz.data.BubbleBudzSaving;

public class BubbleBudzScoreManager
{
	private int bestScore = 0;

	public void loadBestScore() {
		bestScore = BubbleBudzLoader.loadBestScore();
	}

	public void saveBestScore() {
		BubbleBudzSaving.saveBestScore(bestScore);
	}

	public int getBestScore() {
		return bestScore;
	}

	public boolean isNewHighScore(int currentScore) {
		// Always reload best score from file before comparison
		loadBestScore();
		return currentScore > bestScore;
	}

	public void updateIfBetter(int currentScore) {
		if (isNewHighScore(currentScore)) {
			bestScore = currentScore;
			saveBestScore();
			System.out.println("New high score achieved: " + bestScore);
		} else {
			System.out.println("Round score " + currentScore + " did not beat best score " + bestScore);
		}
	}
}