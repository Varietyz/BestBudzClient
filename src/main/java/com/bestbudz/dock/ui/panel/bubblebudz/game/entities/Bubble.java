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
	boolean isClicked = false;

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

			float progress = (float) elapsed / BubbleBudzConfig.GROWTH_DURATION;
			currentRadius = targetRadius * easeOut(progress);

		} else if (elapsed < BubbleBudzConfig.GROWTH_DURATION + BubbleBudzConfig.SHRINK_DURATION) {

			float shrinkElapsed = elapsed - BubbleBudzConfig.GROWTH_DURATION;
			float progress = (float) shrinkElapsed / BubbleBudzConfig.SHRINK_DURATION;
			currentRadius = targetRadius * (1.0f - easeIn(progress));
		} else {

			currentRadius = 0;
		}

		if (elapsed > BubbleBudzConfig.BUBBLE_LIFESPAN - 800) {
			float fadeProgress = (elapsed - (BubbleBudzConfig.BUBBLE_LIFESPAN - 800)) / 800f;
			alpha = Math.max(0, 1.0f - fadeProgress);
		}
	}

	public boolean isExpired() {
		return System.currentTimeMillis() - spawnTime > BubbleBudzConfig.BUBBLE_LIFESPAN;
	}

	public boolean contains(int px, int py) {
		if (currentRadius <= 1) return false;

		float dx = px - x;
		float dy = py - y;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);

		float clickRadius = currentRadius + 10;
		return distance <= clickRadius;
	}

	private float easeOut(float t) {
		return 1 - (1 - t) * (1 - t);
	}

	private float easeIn(float t) {
		return t * t;
	}

	public static void drawBubble(Graphics2D g2d, Bubble bubble) {
		if (bubble.currentRadius <= 0) return;

		float x = bubble.x - bubble.currentRadius;
		float y = bubble.y - bubble.currentRadius;
		float diameter = bubble.currentRadius * 2;

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

		g2d.setColor(new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(255 * bubble.alpha)));
		g2d.setStroke(new BasicStroke(2.5f));
		g2d.draw(new Ellipse2D.Float(x, y, diameter, diameter));

		g2d.setColor(new Color(bubble.color.getRed(), bubble.color.getGreen(), bubble.color.getBlue(), (int)(100 * bubble.alpha)));
		g2d.setStroke(new BasicStroke(4f));
		g2d.draw(new Ellipse2D.Float(x - 3, y - 3, diameter + 6, diameter + 6));

		float highlightRadius = bubble.currentRadius * 0.25f;
		g2d.setColor(new Color(255, 255, 255, (int)(120 * bubble.alpha)));
		g2d.fill(new Ellipse2D.Float(bubble.x - highlightRadius, bubble.y - highlightRadius,
			highlightRadius * 2, highlightRadius * 2));
	}

	public float getTargetRadius() { return targetRadius; }
	public long getSpawnTime() { return spawnTime; }
	public boolean isClicked() { return isClicked; }
	public void setClicked(boolean clicked) { this.isClicked = clicked; }
}
