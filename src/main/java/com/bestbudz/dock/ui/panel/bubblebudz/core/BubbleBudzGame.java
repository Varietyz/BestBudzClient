package com.bestbudz.dock.ui.panel.bubblebudz.core;

import com.bestbudz.dock.ui.panel.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.bubblebudz.game.modes.GameMode;
import com.bestbudz.dock.ui.panel.bubblebudz.game.modes.GeometricGameMode;
import com.bestbudz.dock.ui.panel.bubblebudz.ui.BubbleBudzStyle;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class BubbleBudzGame {
	private List<Bubble> bubbles = new CopyOnWriteArrayList<>();
	private Random random = new Random();
	private long lastBubbleSpawn = 0;
	private int nextSpawnDelay = 0;
	private GameMode gameMode;

	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	public void updateBubbles() {
		bubbles.removeIf(bubble -> {
			bubble.update();
			return bubble.isExpired();
		});
	}

	public void clearBubbles() {
		bubbles.clear();
		lastBubbleSpawn = 0;
		nextSpawnDelay = 300 + random.nextInt(400);
	}

	public List<Bubble> getBubbles() {
		return bubbles;
	}

	public void spawnBubbleIfNeeded(Rectangle gameArea) {
		long currentTime = System.currentTimeMillis();

		if (bubbles.size() < BubbleBudzConfig.MAX_BUBBLES &&
			currentTime - lastBubbleSpawn > nextSpawnDelay) {

			boolean shouldSpawn = bubbles.size() < BubbleBudzConfig.MIN_BUBBLES ||
				(bubbles.size() < BubbleBudzConfig.MAX_BUBBLES * 0.8 && random.nextFloat() < 0.7f);

			if (shouldSpawn) {
				spawnBubble(gameArea);
				lastBubbleSpawn = currentTime;

				nextSpawnDelay = 200 + random.nextInt(600);

				if (random.nextFloat() < 0.15f) {
					nextSpawnDelay = 50 + random.nextInt(150);
				}
			}
		}
	}

	private void spawnBubble(Rectangle gameArea) {
		if (gameArea == null) return;

		Color[] neonColors = BubbleBudzStyle.getNeonColors();
		Color color = neonColors[random.nextInt(neonColors.length)];

		float radius = BubbleBudzConfig.MIN_BUBBLE_RADIUS +
			random.nextFloat() * (BubbleBudzConfig.MAX_BUBBLE_RADIUS - BubbleBudzConfig.MIN_BUBBLE_RADIUS);

		float x, y;
		int attempts = 0;
		do {
			x = gameArea.x + radius + random.nextFloat() * (gameArea.width - radius * 2);
			y = gameArea.y + radius + random.nextFloat() * (gameArea.height - radius * 2);
			attempts++;
		} while (attempts < 50 && isPositionOverlapping(x, y, radius));

		if (attempts < 50) {

			if (gameMode instanceof GeometricGameMode) {
				bubbles.add(new GeometricShape(x, y, radius, color));
				System.out.println("Spawned geometric shape: " +
					((GeometricShape) bubbles.get(bubbles.size() - 1)).getShapeType().getDisplayName());
			} else {
				bubbles.add(new Bubble(x, y, radius, color));
			}
		}
	}

	private boolean isPositionOverlapping(float x, float y, float radius) {
		for (Bubble bubble : bubbles) {
			float dx = x - bubble.x;
			float dy = y - bubble.y;
			float distance = (float) Math.sqrt(dx * dx + dy * dy);

			if (distance < radius + bubble.getTargetRadius() + 8) {
				return true;
			}
		}
		return false;
	}

	public Bubble findClickedBubble(int x, int y) {

		for (int i = bubbles.size() - 1; i >= 0; i--) {
			Bubble bubble = bubbles.get(i);

			if (!bubble.isClicked() && bubble.contains(x, y)) {
				bubble.setClicked(true);
				bubbles.remove(i);

				if (bubble instanceof GeometricShape) {
					GeometricShape shape = (GeometricShape) bubble;
					System.out.println("Clicked " + shape.getShapeType().getDisplayName() +
						" (multiplier: " + shape.getScoreMultiplier() + "x)");
				}

				return bubble;
			}
		}
		return null;
	}

	public static void drawBubble(Graphics2D g2d, Bubble bubble) {
		Bubble.drawBubble(g2d, bubble);
	}

	public GameMode getGameMode() {
		return gameMode;
	}

	public boolean isGeometricMode() {
		return gameMode instanceof GeometricGameMode;
	}
}
