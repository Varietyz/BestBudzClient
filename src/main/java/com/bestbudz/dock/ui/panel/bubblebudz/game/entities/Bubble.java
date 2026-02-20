package com.bestbudz.dock.ui.panel.bubblebudz.game.entities;

import com.bestbudz.dock.ui.panel.bubblebudz.config.BubbleBudzConfig;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Bubble {
	public float x;
	public float y;
	float targetRadius;
	public float currentRadius;
	long spawnTime;
	public Color color;
	float alpha = 1.0f;
	boolean isClicked = false; // Track if bubble was clicked for graceful removal

	public Bubble(float x, float y, float targetRadius, Color color) {
		this.x = x;
		this.y = y;
		this.targetRadius = targetRadius;
		this.currentRadius = 0;
		this.spawnTime = System.currentTimeMillis();
		this.color = color;
	}

	public void update() {
		long elapsed = System.currentTimeMillis() - spawnTime;

		if (elapsed < BubbleBudzConfig.GROWTH_DURATION) {
			// Growing phase - smooth easing
			float progress = (float) elapsed / BubbleBudzConfig.GROWTH_DURATION;
			currentRadius = targetRadius * easeOut(progress);

		} else if (elapsed < BubbleBudzConfig.GROWTH_DURATION + BubbleBudzConfig.SHRINK_DURATION) {
			// Shrinking phase
			float shrinkElapsed = elapsed - BubbleBudzConfig.GROWTH_DURATION;
			float progress = (float) shrinkElapsed / BubbleBudzConfig.SHRINK_DURATION;
			currentRadius = targetRadius * (1.0f - easeIn(progress));
		} else {
			// Fading out
			currentRadius = 0;
		}

		// Gradual fade out near end of life
		if (elapsed > BubbleBudzConfig.BUBBLE_LIFESPAN - 800) {
			float fadeProgress = (elapsed - (BubbleBudzConfig.BUBBLE_LIFESPAN - 800)) / 800f;
			alpha = Math.max(0, 1.0f - fadeProgress);
		}
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - spawnTime > BubbleBudzConfig.BUBBLE_LIFESPAN;
	}

	// Enhanced click detection with generous clickbox
	public boolean contains(int px, int py) {
		if (currentRadius <= 1) return false; // Must be visible

		float dx = px - x;
		float dy = py - y;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);

		// Generous clickbox - much larger than visual for better UX
		float clickRadius = currentRadius + 10; // +10px buffer (was +6)
		return distance <= clickRadius;
	}

	// Smooth easing functions
	private float easeOut(float t) {
		return 1 - (1 - t) * (1 - t);
	}

	private float easeIn(float t) {
		return t * t;
	}

	// Static drawing method - keeps rendering logic with entity
	public static void drawBubble(Graphics2D g2d, Bubble bubble) {
		if (bubble.currentRadius <= 0) return;

		float x = bubble.x - bubble.currentRadius;
		float y = bubble.y - bubble.currentRadius;
		float diameter = bubble.currentRadius * 2;

		// Gradient fill
		RadialGradientPaint gradient = new RadialGradientPaint(
			bubble.x, bubble.y, bubble.currentRadius,
			new float[]{0f, 0.6f, 1f},
			new Color[]{
				new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(200 * bubble.alpha)),
				new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(140 * bubble.alpha)),
				new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(80 * bubble.alpha))
			}
		);

		g2d.setPaint(gradient);
		g2d.fill(new Ellipse2D.Float(x, y, diameter, diameter));

		// Neon border
		g2d.setColor(new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(255 * bubble.alpha)));
		g2d.setStroke(new BasicStroke(2.5f));
		g2d.draw(new Ellipse2D.Float(x, y, diameter, diameter));

		// Outer glow
		g2d.setColor(new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(100 * bubble.alpha)));
		g2d.setStroke(new BasicStroke(4f));
		g2d.draw(new Ellipse2D.Float(x - 3, y - 3, diameter + 6, diameter + 6));

		// Inner highlight
		float highlightRadius = bubble.currentRadius * 0.25f;
		g2d.setColor(new Color(255, 255, 255, (int)(120 * bubble.alpha)));
		g2d.fill(new Ellipse2D.Float(bubble.x - highlightRadius, bubble.y - highlightRadius,
			highlightRadius * 2, highlightRadius * 2));
	}

	// Getter methods for better encapsulation
	public float getTargetRadius() { return targetRadius; }
	public long getSpawnTime() { return spawnTime; }
	public boolean isClicked() { return isClicked; }
	public void setClicked(boolean clicked) { this.isClicked = clicked; }
}