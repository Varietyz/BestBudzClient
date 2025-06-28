package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.types.score;


import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.Effect;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.EffectData;
import com.bestbudz.dock.ui.panel.client.bubblebudz.ui.BubbleBudzStyle;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.ArrayList;

public class ScoreEffect implements Effect
{
	private final float x, y;
	private final int score;
	private final Color baseColor;
	private final long startTime;
	private final List<GoldenParticle> particles;

	// Animation phases (in milliseconds)
	private static final int DELAY_PHASE = 150;        // Wait after pop
	private static final int REVEAL_PHASE = 300;       // Score appears & scales
	private static final int EXPLOSION_PHASE = 300;    // Particles explode
	private static final int TOTAL_DURATION = 900;

	// Golden ratio constants
	private static final float GOLDEN_RATIO = 1.618f;
	private static final float GOLDEN_ANGLE = 137.5f; // degrees

	// Scale progression
	private float currentScale = 0.0f;
	private float currentAlpha = 0.0f;

	public ScoreEffect(EffectData data, int score) {
		if (data instanceof ScoreEffectData) {
			ScoreEffectData scoreData = (ScoreEffectData) data;
			this.score = scoreData.score;
		} else {
			this.score = score;
		}

		this.x = data.x;
		this.y = data.y;
		this.baseColor = data.color;
		this.startTime = System.currentTimeMillis();
		this.particles = createGoldenParticles();
	}

	private List<GoldenParticle> createGoldenParticles() {
		List<GoldenParticle> particles = new ArrayList<>();

		// Number of particles based on score (more points = more particles)
		int particleCount = Math.min(8 + (score / 10), 16);

		for (int i = 0; i < particleCount; i++) {
			// Golden angle distribution
			float angle = i * GOLDEN_ANGLE;

			// Golden ratio distance rings
			float ring = (i % 3) + 1; // 3 rings
			float distance = 20.0f * ring * GOLDEN_RATIO;

			// Calculate position using golden spiral
			float radians = (float) Math.toRadians(angle);
			float spiralDistance = distance * (1.0f + ring * 0.2f);

			float targetX = x + (float) Math.cos(radians) * spiralDistance;
			float targetY = y + (float) Math.sin(radians) * spiralDistance;

			// Particle type based on position in sequence
			ParticleType type = ParticleType.values()[i % ParticleType.values().length];

			particles.add(new GoldenParticle(x, y, targetX, targetY, type, baseColor));
		}

		return particles;
	}

	@Override
	public void update() {
		long elapsed = System.currentTimeMillis() - startTime;

		if (elapsed < DELAY_PHASE) {
			// Phase 1: Delay - nothing visible yet
			currentScale = 0.0f;
			currentAlpha = 0.0f;

		} else if (elapsed < DELAY_PHASE + REVEAL_PHASE) {
			// Phase 2: Score reveal and scale up
			float revealProgress = (elapsed - DELAY_PHASE) / (float) REVEAL_PHASE;

			// Smooth scale from tiny to slightly larger than normal
			currentScale = easeOut(revealProgress) * 1.2f; // 20% larger than normal
			currentAlpha = easeOut(revealProgress);

		} else if (elapsed < TOTAL_DURATION) {
			// Phase 3: Explosion phase
			float explosionProgress = (elapsed - DELAY_PHASE - REVEAL_PHASE) / (float) EXPLOSION_PHASE;

			// Text fades and continues moving
			currentAlpha = 1.0f - easeIn(explosionProgress);
			currentScale = 1.2f + (easeOut(explosionProgress) * 0.3f); // Slight final scale

			// Update particles
			for (GoldenParticle particle : particles) {
				particle.update(explosionProgress);
			}
		}
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() - startTime > TOTAL_DURATION;
	}

	@Override
	public void render(Graphics2D g2d) {
		long elapsed = System.currentTimeMillis() - startTime;

		// Only render after delay phase
		if (elapsed < DELAY_PHASE) return;

		// Save original transform
		AffineTransform originalTransform = g2d.getTransform();

		// Render score text
		if (currentAlpha > 0.01f) {
			renderScoreText(g2d);
		}

		// Render particles during explosion phase
		if (elapsed >= DELAY_PHASE + REVEAL_PHASE) {
			renderParticles(g2d);
		}

		// Restore transform
		g2d.setTransform(originalTransform);
	}

	private void renderScoreText(Graphics2D g2d) {
		// Calculate text properties
		String scoreText = "+" + score;
		Font baseFont = BubbleBudzStyle.FONT_INSTRUCTION;
		Font scaledFont = baseFont.deriveFont(baseFont.getSize() * currentScale);

		g2d.setFont(scaledFont);
		FontMetrics fm = g2d.getFontMetrics();

		int textWidth = fm.stringWidth(scoreText);
		int textHeight = fm.getAscent();
		int textX = (int) (x - textWidth / 2);
		int textY = (int) (y + textHeight / 2);

		// Text with glow effect
		Color textColor = new Color(BubbleBudzStyle.SCORE_GLOW.getRed(),
			BubbleBudzStyle.SCORE_GLOW.getGreen(),
			BubbleBudzStyle.SCORE_GLOW.getBlue(),
			(int) (255 * currentAlpha));

		// Glow
		Color glowColor = new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(),
			(int) (100 * currentAlpha));
		g2d.setColor(glowColor);
		for (int i = 1; i <= 2; i++) {
			g2d.drawString(scoreText, textX + i, textY + i);
		}

		// Main text
		g2d.setColor(textColor);
		g2d.drawString(scoreText, textX, textY);
	}

	private void renderParticles(Graphics2D g2d) {
		for (GoldenParticle particle : particles) {
			particle.render(g2d);
		}
	}

	// Smooth easing functions
	private float easeOut(float t) {
		return 1 - (1 - t) * (1 - t);
	}

	private float easeIn(float t) {
		return t * t;
	}

	// Particle types for geometric variety
	private enum ParticleType {
		TRIANGLE, SQUARE, HEXAGON, CIRCLE
	}

	// Individual particle class
	private static class GoldenParticle {
		private final float startX, startY;
		private final float targetX, targetY;
		private final ParticleType type;
		private final Color color;

		private float currentX, currentY;
		private float alpha = 1.0f;
		private float scale = 1.0f;
		private float rotation = 0.0f;

		public GoldenParticle(float startX, float startY, float targetX, float targetY,
							  ParticleType type, Color color) {
			this.startX = startX;
			this.startY = startY;
			this.currentX = startX;
			this.currentY = startY;
			this.targetX = targetX;
			this.targetY = targetY;
			this.type = type;
			this.color = color;
		}

		public void update(float explosionProgress) {
			// Move towards target using easing
			float easedProgress = easeOut(explosionProgress);
			currentX = startX + (targetX - startX) * easedProgress;
			currentY = startY + (targetY - startY) * easedProgress;

			// Fade out
			alpha = 1.0f - explosionProgress;

			// Scale slightly during movement
			scale = 1.0f + easedProgress * 0.5f;

			// Rotate for dynamic effect
			rotation = explosionProgress * 180.0f; // Half rotation
		}

		public void render(Graphics2D g2d) {
			if (alpha <= 0.01f) return;

			// Save transform
			AffineTransform oldTransform = g2d.getTransform();

			// Apply transformations
			g2d.translate(currentX, currentY);
			g2d.rotate(Math.toRadians(rotation));
			g2d.scale(scale, scale);

			// Set color with alpha
			Color renderColor = new Color(color.getRed(), color.getGreen(), color.getBlue(),
				(int) (255 * alpha * 0.8f));
			g2d.setColor(renderColor);
			g2d.setStroke(new BasicStroke(1.5f));

			// Render based on type
			int size = 4;
			switch (type) {
				case TRIANGLE:
					int[] xPoints = {0, -size, size};
					int[] yPoints = {-size, size, size};
					g2d.drawPolygon(xPoints, yPoints, 3);
					break;

				case SQUARE:
					g2d.drawRect(-size/2, -size/2, size, size);
					break;

				case HEXAGON:
					drawHexagon(g2d, size);
					break;

				case CIRCLE:
					g2d.drawOval(-size/2, -size/2, size, size);
					break;
			}

			// Restore transform
			g2d.setTransform(oldTransform);
		}

		private void drawHexagon(Graphics2D g2d, int size) {
			int[] xPoints = new int[6];
			int[] yPoints = new int[6];

			for (int i = 0; i < 6; i++) {
				double angle = i * Math.PI / 3;
				xPoints[i] = (int) (size * Math.cos(angle));
				yPoints[i] = (int) (size * Math.sin(angle));
			}

			g2d.drawPolygon(xPoints, yPoints, 6);
		}

		private float easeOut(float t) {
			return 1 - (1 - t) * (1 - t);
		}
	}
}