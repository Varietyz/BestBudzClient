package com.bestbudz.dock.ui.panel.client.bubblebudz.core;

import com.bestbudz.dock.ui.panel.client.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.modes.GameMode;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.modes.GeometricGameMode;
import com.bestbudz.dock.ui.panel.client.bubblebudz.ui.BubbleBudzStyle;
import java.awt.*;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core game logic for Bubble Budz mini-game.
 * Handles bubble spawning, updating, collision detection, and supports both
 * classic circular bubbles and geometric shapes based on the current game mode.
 */
public class BubbleBudzGame {
	private List<Bubble> bubbles = new CopyOnWriteArrayList<>();
	private Random random = new Random();
	private long lastBubbleSpawn = 0;
	private int nextSpawnDelay = 0;
	private GameMode gameMode; // Reference to current game mode

	/**
	 * Set the current game mode. This determines what type of bubbles to spawn.
	 */
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	/**
	 * Update all active bubbles - handles growth, shrinking, and expiration
	 */
	public void updateBubbles() {
		bubbles.removeIf(bubble -> {
			bubble.update();
			return bubble.isExpired();
		});
	}

	/**
	 * Clear all bubbles and reset spawn timing
	 */
	public void clearBubbles() {
		bubbles.clear();
		lastBubbleSpawn = 0;
		nextSpawnDelay = 300 + random.nextInt(400); // Random initial delay
	}

	/**
	 * Get list of all active bubbles
	 */
	public List<Bubble> getBubbles() {
		return bubbles;
	}

	/**
	 * Spawn new bubbles based on timing and current bubble count
	 */
	public void spawnBubbleIfNeeded(Rectangle gameArea) {
		long currentTime = System.currentTimeMillis();

		// Check if we should spawn a new bubble
		if (bubbles.size() < BubbleBudzConfig.MAX_BUBBLES &&
			currentTime - lastBubbleSpawn > nextSpawnDelay) {

			// Determine if we should spawn based on current bubble count
			boolean shouldSpawn = bubbles.size() < BubbleBudzConfig.MIN_BUBBLES ||
				(bubbles.size() < BubbleBudzConfig.MAX_BUBBLES * 0.8 && random.nextFloat() < 0.7f);

			if (shouldSpawn) {
				spawnBubble(gameArea);
				lastBubbleSpawn = currentTime;

				// Set next spawn delay with some randomness
				nextSpawnDelay = 200 + random.nextInt(600);

				// Occasionally spawn clusters (15% chance for quick spawn)
				if (random.nextFloat() < 0.15f) {
					nextSpawnDelay = 50 + random.nextInt(150);
				}
			}
		}
	}

	/**
	 * Spawn a single bubble at a non-overlapping position
	 */
	private void spawnBubble(Rectangle gameArea) {
		if (gameArea == null) return;

		// Random color selection
		Color[] neonColors = BubbleBudzStyle.getNeonColors();
		Color color = neonColors[random.nextInt(neonColors.length)];

		// Random size within configured bounds
		float radius = BubbleBudzConfig.MIN_BUBBLE_RADIUS +
			random.nextFloat() * (BubbleBudzConfig.MAX_BUBBLE_RADIUS - BubbleBudzConfig.MIN_BUBBLE_RADIUS);

		// Find non-overlapping position
		float x, y;
		int attempts = 0;
		do {
			x = gameArea.x + radius + random.nextFloat() * (gameArea.width - radius * 2);
			y = gameArea.y + radius + random.nextFloat() * (gameArea.height - radius * 2);
			attempts++;
		} while (attempts < 50 && isPositionOverlapping(x, y, radius));

		// Only spawn if we found a good position
		if (attempts < 50) {
			// Create appropriate bubble type based on current game mode
			if (gameMode instanceof GeometricGameMode) {
				bubbles.add(new GeometricShape(x, y, radius, color));
				System.out.println("Spawned geometric shape: " +
					((GeometricShape) bubbles.get(bubbles.size() - 1)).getShapeType().getDisplayName());
			} else {
				bubbles.add(new Bubble(x, y, radius, color));
			}
		}
	}

	/**
	 * Check if a position would overlap with existing bubbles
	 */
	private boolean isPositionOverlapping(float x, float y, float radius) {
		for (Bubble bubble : bubbles) {
			float dx = x - bubble.x;
			float dy = y - bubble.y;
			float distance = (float) Math.sqrt(dx * dx + dy * dy);

			// Add some padding between bubbles
			if (distance < radius + bubble.getTargetRadius() + 8) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Find and remove the topmost bubble that was clicked
	 * Returns the clicked bubble for scoring, or null if no hit
	 */
	public Bubble findClickedBubble(int x, int y) {
		// Check from top to bottom (last added = on top)
		for (int i = bubbles.size() - 1; i >= 0; i--) {
			Bubble bubble = bubbles.get(i);

			// Only check bubbles that haven't been clicked yet
			if (!bubble.isClicked() && bubble.contains(x, y)) {
				bubble.setClicked(true);
				bubbles.remove(i);

				// Log what type of bubble was clicked
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

	/**
	 * Static delegate method for drawing regular bubbles
	 * This maintains the existing API while supporting both bubble types
	 */
	public static void drawBubble(Graphics2D g2d, Bubble bubble) {
		Bubble.drawBubble(g2d, bubble);
	}

	/**
	 * Get the current game mode (useful for renderers)
	 */
	public GameMode getGameMode() {
		return gameMode;
	}

	/**
	 * Check if currently in geometric mode (convenience method)
	 */
	public boolean isGeometricMode() {
		return gameMode instanceof GeometricGameMode;
	}
}