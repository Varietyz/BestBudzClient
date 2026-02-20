package com.bestbudz.dock.ui.panel.bubblebudz.system.render;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class RenderContext {
	public final int width, height;
	public final Rectangle gameArea;
	public final BufferedImage logoImage;
	public final long roundStartTime;
	public final int currentScore;
	public final int bestScore;
	public final boolean isLoading;
	public final String loadingMessage;
	public final long loadingStartTime;

	public RenderContext(int width, int height, Rectangle gameArea, BufferedImage logoImage,
						 long roundStartTime, int currentScore, int bestScore,
						 boolean isLoading, String loadingMessage, long loadingStartTime) {
		this.width = width;
		this.height = height;
		this.gameArea = gameArea;
		this.logoImage = logoImage;
		this.roundStartTime = roundStartTime;
		this.currentScore = currentScore;
		this.bestScore = bestScore;
		this.isLoading = isLoading;
		this.loadingMessage = loadingMessage;
		this.loadingStartTime = loadingStartTime;
	}
}