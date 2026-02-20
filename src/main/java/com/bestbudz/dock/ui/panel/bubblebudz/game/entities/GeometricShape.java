package com.bestbudz.dock.ui.panel.bubblebudz.game.entities;

import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.shapes.ShapeFactory;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.shapes.ShapeRenderer;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class GeometricShape extends Bubble {

	public enum ShapeType {
		TRIANGLE(1.2f, "Triangle"),
		SQUARE(1.0f, "Square"),
		PENTAGON(1.3f, "Pentagon"),
		HEXAGON(1.4f, "Hexagon"),
		OCTAGON(1.5f, "Octagon"),
		STAR(1.8f, "Star"),
		DIAMOND(1.1f, "Diamond"),
		CROSS(1.6f, "Cross"),
		POTLEAF(2.0f, "Cannabis Leaf");

		private final float scoreMultiplier;
		private final String displayName;

		ShapeType(float scoreMultiplier, String displayName) {
			this.scoreMultiplier = scoreMultiplier;
			this.displayName = displayName;
		}

		public float getScoreMultiplier() { return scoreMultiplier; }
		public String getDisplayName() { return displayName; }
	}

	private final ShapeType shapeType;
	private final float rotation;
	private static final Random random = new Random();

	public GeometricShape(float x, float y, float targetRadius, Color color) {
		super(x, y, targetRadius, color);
		this.shapeType = ShapeType.values()[random.nextInt(ShapeType.values().length)];
		this.rotation = random.nextFloat() * 360f;
	}

	public GeometricShape(float x, float y, float targetRadius, Color color, ShapeType shapeType) {
		super(x, y, targetRadius, color);
		this.shapeType = shapeType;
		this.rotation = random.nextFloat() * 360f;
	}

	@Override
	public boolean contains(int px, int py) {
		if (currentRadius <= 1) return false;

		float dx = px - x;
		float dy = py - y;
		float distance = (float) Math.sqrt(dx * dx + dy * dy);

		float clickRadius = currentRadius + 12;
		return distance <= clickRadius;
	}

	public ShapeType getShapeType() { return shapeType; }
	public float getScoreMultiplier() { return shapeType.getScoreMultiplier(); }
	public float getRotation() { return rotation; }

	public static void drawGeometricShape(Graphics2D g2d, GeometricShape shape) {
		if (shape.currentRadius <= 0) return;

		AffineTransform originalTransform = g2d.getTransform();

		g2d.translate(shape.x, shape.y);
		g2d.rotate(Math.toRadians(shape.rotation));

		Shape geometricPath = ShapeFactory.createShape(shape.shapeType, shape.currentRadius);

		RadialGradientPaint gradient = new RadialGradientPaint(
			0, 0, shape.currentRadius,
			new float[]{0f, 0.6f, 1f},
			new Color[]{
				new Color(shape.color.getRed(), shape.color.getGreen(), shape.color.getBlue(), (int)(200 * shape.alpha)),
				new Color(shape.color.getRed(), shape.color.getGreen(), shape.color.getBlue(), (int)(140 * shape.alpha)),
				new Color(shape.color.getRed(), shape.color.getGreen(), shape.color.getBlue(), (int)(80 * shape.alpha))
			}
		);

		g2d.setPaint(gradient);
		g2d.fill(geometricPath);

		ShapeRenderer.renderShapeOutline(g2d, geometricPath, shape.color, shape.alpha, 2.5f);

		g2d.setColor(new Color(shape.color.getRed(), shape.color.getGreen(), shape.color.getBlue(), (int)(100 * shape.alpha)));
		g2d.setStroke(new BasicStroke(4f));
		AffineTransform glowTransform = AffineTransform.getScaleInstance(1.2, 1.2);
		Shape glowShape = glowTransform.createTransformedShape(geometricPath);
		g2d.draw(glowShape);

		float highlightRadius = shape.currentRadius * 0.15f;
		g2d.setColor(new Color(255, 255, 255, (int)(120 * shape.alpha)));
		g2d.fill(new Ellipse2D.Float(-highlightRadius, -highlightRadius,
			highlightRadius * 2, highlightRadius * 2));

		g2d.setTransform(originalTransform);
	}

	private static Shape createShapePath(ShapeType shapeType, float radius) {
		switch (shapeType) {
			case TRIANGLE:
				return createTriangle(radius);
			case SQUARE:
				return createSquare(radius);
			case PENTAGON:
				return createPolygon(5, radius);
			case HEXAGON:
				return createPolygon(6, radius);
			case OCTAGON:
				return createPolygon(8, radius);
			case STAR:
				return createStar(radius);
			case DIAMOND:
				return createDiamond(radius);
			case CROSS:
				return createCross(radius);
			default:
				return new Ellipse2D.Float(-radius, -radius, radius * 2, radius * 2);
		}
	}

	private static Shape createTriangle(float radius) {
		Path2D triangle = new Path2D.Float();
		triangle.moveTo(0, -radius);
		triangle.lineTo(-radius * 0.866f, radius * 0.5f);
		triangle.lineTo(radius * 0.866f, radius * 0.5f);
		triangle.closePath();
		return triangle;
	}

	private static Shape createSquare(float radius) {
		float size = radius * 0.707f;
		return new Rectangle2D.Float(-size, -size, size * 2, size * 2);
	}

	private static Shape createPolygon(int sides, float radius) {
		Path2D polygon = new Path2D.Float();
		for (int i = 0; i < sides; i++) {
			double angle = 2 * Math.PI * i / sides - Math.PI / 2;
			float x = (float) (radius * Math.cos(angle));
			float y = (float) (radius * Math.sin(angle));

			if (i == 0) {
				polygon.moveTo(x, y);
			} else {
				polygon.lineTo(x, y);
			}
		}
		polygon.closePath();
		return polygon;
	}

	private static Shape createStar(float radius) {
		Path2D star = new Path2D.Float();
		int points = 5;
		float innerRadius = radius * 0.4f;

		for (int i = 0; i < points * 2; i++) {
			double angle = Math.PI * i / points - Math.PI / 2;
			float r = (i % 2 == 0) ? radius : innerRadius;
			float x = (float) (r * Math.cos(angle));
			float y = (float) (r * Math.sin(angle));

			if (i == 0) {
				star.moveTo(x, y);
			} else {
				star.lineTo(x, y);
			}
		}
		star.closePath();
		return star;
	}

	private static Shape createDiamond(float radius) {
		Path2D diamond = new Path2D.Float();
		diamond.moveTo(0, -radius);
		diamond.lineTo(radius * 0.6f, 0);
		diamond.lineTo(0, radius);
		diamond.lineTo(-radius * 0.6f, 0);
		diamond.closePath();
		return diamond;
	}

	private static Shape createCross(float radius) {
		float thickness = radius * 0.3f;
		Area cross = new Area();

		cross.add(new Area(new Rectangle2D.Float(-thickness, -radius, thickness * 2, radius * 2)));

		cross.add(new Area(new Rectangle2D.Float(-radius, -thickness, radius * 2, thickness * 2)));

		return cross;
	}
}
