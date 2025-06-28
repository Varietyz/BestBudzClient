package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.shapes;

import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.GeometricShape;
import java.awt.*;
import java.awt.geom.*;

/**
 * Handles smooth morphing between circular bubbles and geometric shapes.
 * Separates morphing logic for reuse and cleaner code.
 */
public class ShapeMorpher {

	/**
	 * Creates a shape that interpolates between a circle and target geometric shape
	 */
	public static Shape createMorphedShape(GeometricShape.ShapeType targetShape,
										   float radius, float progress) {
		if (progress <= 0f) {
			// Pure circle
			return new Ellipse2D.Float(-radius, -radius, radius * 2, radius * 2);
		} else if (progress >= 1f) {
			// Pure geometric shape
			return ShapeFactory.createShape(targetShape, radius);
		} else {
			// Interpolate between circle and shape
			return interpolateShapes(targetShape, radius, progress);
		}
	}

	/**
	 * Smooth interpolation between circular and geometric shapes
	 */
	private static Shape interpolateShapes(GeometricShape.ShapeType targetShape,
										   float radius, float progress) {
		float[] targetVertices = ShapeFactory.getShapeVertices(targetShape, radius);
		Path2D morphPath = new Path2D.Float();

		// Number of points for smooth interpolation
		int numPoints = Math.max(8, targetVertices.length / 2);

		for (int i = 0; i < numPoints; i++) {
			// Circle point
			double circleAngle = 2 * Math.PI * i / numPoints;
			float circleX = (float) (radius * Math.cos(circleAngle));
			float circleY = (float) (radius * Math.sin(circleAngle));

			// Target shape point
			float targetX = circleX;
			float targetY = circleY;

			if (i * 2 < targetVertices.length) {
				targetX = targetVertices[i * 2];
				targetY = targetVertices[i * 2 + 1];
			} else if (targetVertices.length >= 2) {
				// Wrap around for shapes with fewer vertices
				int targetIndex = (i * 2) % targetVertices.length;
				targetX = targetVertices[targetIndex];
				targetY = targetVertices[(targetIndex + 1) % targetVertices.length];
			}

			// Smooth interpolation
			float interpX = circleX + (targetX - circleX) * easeInOut(progress);
			float interpY = circleY + (targetY - circleY) * easeInOut(progress);

			if (i == 0) {
				morphPath.moveTo(interpX, interpY);
			} else {
				morphPath.lineTo(interpX, interpY);
			}
		}

		morphPath.closePath();
		return morphPath;
	}

	private static float easeInOut(float t) {
		return t < 0.5f ? 2 * t * t : 1 - 2 * (1 - t) * (1 - t);
	}
}