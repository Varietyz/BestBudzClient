package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.shapes;

import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.GeometricShape;
import java.awt.*;

/**
 * Centralized shape rendering system for consistent visual appearance
 * across bubbles, effects, and UI elements.
 */
public class ShapeRenderer {

	/**
	 * Renders a shape with the standard neon style (fill + border + glow)
	 */
	public static void renderNeonShape(Graphics2D g2d, Shape shape, Color color,
									   float alpha, float strokeWidth) {
		// Fill with alpha
		Color fillColor = new Color(color.getRed(), color.getGreen(), color.getBlue(),
			(int)(255 * alpha * 0.6f));
		g2d.setColor(fillColor);
		g2d.fill(shape);

		// Main border
		Color borderColor = new Color(color.getRed(), color.getGreen(), color.getBlue(),
			(int)(255 * alpha));
		g2d.setColor(borderColor);
		g2d.setStroke(new BasicStroke(strokeWidth));
		g2d.draw(shape);
	}

	/**
	 * Renders shape outline only (for effects)
	 */
	public static void renderShapeOutline(Graphics2D g2d, Shape shape, Color color,
										  float alpha, float strokeWidth) {
		Color outlineColor = new Color(color.getRed(), color.getGreen(), color.getBlue(),
			(int)(255 * alpha));
		g2d.setColor(outlineColor);
		g2d.setStroke(new BasicStroke(strokeWidth));
		g2d.draw(shape);
	}

	/**
	 * Renders sparkles at shape vertices
	 */
	public static void renderShapeSparkles(Graphics2D g2d, GeometricShape.ShapeType shapeType,
										   float radius, Color color, float alpha) {
		if (alpha <= 0.01f) return;

		float sparkleAlpha = Math.max(0, alpha - 0.2f);
		if (sparkleAlpha <= 0) return;

		// Use shape-specific sparkle rendering
		switch (shapeType) {
			case POTLEAF:
				renderCannabisSparkles(g2d, radius, sparkleAlpha);
				break;
			default:
				renderDefaultSparkles(g2d, shapeType, radius, sparkleAlpha);
				break;
		}
	}

	private static void renderDefaultSparkles(Graphics2D g2d, GeometricShape.ShapeType shapeType,
											  float radius, float sparkleAlpha) {
		g2d.setColor(new Color(255, 255, 255, (int)(200 * sparkleAlpha)));
		g2d.setStroke(new BasicStroke(2f));

		float[] vertices = ShapeFactory.getShapeVertices(shapeType, radius);

		for (int i = 0; i < vertices.length; i += 2) {
			float vx = vertices[i];
			float vy = vertices[i + 1];

			float sparkleSize = 4f;
			g2d.drawLine((int)(vx - sparkleSize), (int)vy, (int)(vx + sparkleSize), (int)vy);
			g2d.drawLine((int)vx, (int)(vy - sparkleSize), (int)vx, (int)(vy + sparkleSize));
		}
	}

	private static void renderCannabisSparkles(Graphics2D g2d, float radius, float sparkleAlpha) {
		// Green sparkles for cannabis
		g2d.setColor(new Color(100, 255, 100, (int)(200 * sparkleAlpha)));
		g2d.setStroke(new BasicStroke(2f));

		float[] vertices = ShapeFactory.getShapeVertices(GeometricShape.ShapeType.POTLEAF, radius);

		// Larger sparkles for cannabis finger tips
		for (int i = 0; i < vertices.length; i += 2) {
			float vx = vertices[i];
			float vy = vertices[i + 1];

			float sparkleSize = 5f;

			// Cross sparkle pattern
			g2d.drawLine((int)(vx - sparkleSize), (int)vy, (int)(vx + sparkleSize), (int)vy);
			g2d.drawLine((int)vx, (int)(vy - sparkleSize), (int)vx, (int)(vy + sparkleSize));

			// Add small diamond sparkles
			int[] xPoints = {(int)vx, (int)(vx + 3), (int)vx, (int)(vx - 3)};
			int[] yPoints = {(int)(vy - 3), (int)vy, (int)(vy + 3), (int)vy};
			g2d.drawPolygon(xPoints, yPoints, 4);
		}
	}
}