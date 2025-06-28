package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.shapes;

import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.GeometricShape;
import java.awt.*;
import java.awt.geom.*;
import java.util.function.BiConsumer;

/**
 * Factory for creating geometric shapes and retrieving their vertices.
 * Centralized shape logic for reuse across the entire game.
 */
public class ShapeFactory {

	/**
	 * Creates a Shape object for the given type and radius
	 */
	public static Shape createShape(GeometricShape.ShapeType shapeType, float radius) {
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

	/**
	 * Gets vertices for shape outlines and sparkle placement
	 */
	public static float[] getShapeVertices(GeometricShape.ShapeType shapeType, float radius) {
		switch (shapeType) {
			case TRIANGLE:
				return new float[] {
					0, -radius,
					-radius * 0.866f, radius * 0.5f,
					radius * 0.866f, radius * 0.5f
				};
			case SQUARE:
				float size = radius * 0.707f;
				return new float[] {
					-size, -size, size, -size, size, size, -size, size
				};
			case PENTAGON:
				return getPolygonVertices(5, radius);
			case HEXAGON:
				return getPolygonVertices(6, radius);
			case OCTAGON:
				return getPolygonVertices(8, radius);
			case STAR:
				return getStarVertices(radius);
			case DIAMOND:
				return new float[] {
					0, -radius, radius * 0.6f, 0, 0, radius, -radius * 0.6f, 0
				};
			case CROSS:
				return new float[] {
					0, -radius, radius, 0, 0, radius, -radius, 0
				};
			default:
				return new float[] { 0, -radius };
		}
	}


	// ========== STANDARD SHAPES (unchanged) ==========

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

	// Helper methods
	private static float[] getPolygonVertices(int sides, float radius) {
		float[] vertices = new float[sides * 2];
		for (int i = 0; i < sides; i++) {
			double angle = 2 * Math.PI * i / sides - Math.PI / 2;
			vertices[i * 2] = (float) (radius * Math.cos(angle));
			vertices[i * 2 + 1] = (float) (radius * Math.sin(angle));
		}
		return vertices;
	}

	private static float[] getStarVertices(float radius) {
		float[] vertices = new float[10];
		for (int i = 0; i < 5; i++) {
			double angle = Math.PI * i * 2 / 5 - Math.PI / 2;
			vertices[i * 2] = (float) (radius * Math.cos(angle));
			vertices[i * 2 + 1] = (float) (radius * Math.sin(angle));
		}
		return vertices;
	}
}