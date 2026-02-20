package com.bestbudz.dock.ui.panel.bubblebudz.system.effects.shapes;

import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import java.awt.*;
import java.awt.geom.*;

public class ShapeMorpher {

	public static Shape createMorphedShape(GeometricShape.ShapeType targetShape,
										   float radius, float progress) {
		if (progress <= 0f) {

			return new Ellipse2D.Float(-radius, -radius, radius * 2, radius * 2);
		} else if (progress >= 1f) {

			return ShapeFactory.createShape(targetShape, radius);
		} else {

			return interpolateShapes(targetShape, radius, progress);
		}
	}

	private static Shape interpolateShapes(GeometricShape.ShapeType targetShape,
										   float radius, float progress) {
		float[] targetVertices = ShapeFactory.getShapeVertices(targetShape, radius);
		Path2D morphPath = new Path2D.Float();

		int numPoints = Math.max(8, targetVertices.length / 2);

		for (int i = 0; i < numPoints; i++) {

			double circleAngle = 2 * Math.PI * i / numPoints;
			float circleX = (float) (radius * Math.cos(circleAngle));
			float circleY = (float) (radius * Math.sin(circleAngle));

			float targetX = circleX;
			float targetY = circleY;

			if (i * 2 < targetVertices.length) {
				targetX = targetVertices[i * 2];
				targetY = targetVertices[i * 2 + 1];
			} else if (targetVertices.length >= 2) {

				int targetIndex = (i * 2) % targetVertices.length;
				targetX = targetVertices[targetIndex];
				targetY = targetVertices[(targetIndex + 1) % targetVertices.length];
			}

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
