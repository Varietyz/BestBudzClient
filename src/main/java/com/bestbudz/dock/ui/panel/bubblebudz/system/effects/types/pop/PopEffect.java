package com.bestbudz.dock.ui.panel.bubblebudz.system.effects.types.pop;

import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.Effect;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.EffectData;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.shapes.ShapeFactory;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.shapes.ShapeMorpher;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.shapes.ShapeRenderer;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class PopEffect implements Effect {

	private final float x, y;
	private final float initialRadius;
	private final Color color;
	private final long startTime;

	private float currentRadius;
	private float maxRadius;
	private float alpha;

	private final boolean isGeometric;
	private final GeometricShape.ShapeType shapeType;
	private final float rotation;

	private final boolean shouldMorph;
	private final GeometricShape.ShapeType morphTargetShape;
	private final float morphRotation;
	private float morphProgress;

	private static final int EXPANSION_PHASE = 200;
	private static final int MORPH_PHASE = 250;
	private static final int TOTAL_DURATION = 600;

	private static final Random morphRandom = new Random();

	public PopEffect(EffectData data) {
		this.x = data.x;
		this.y = data.y;
		this.initialRadius = data.radius;
		this.maxRadius = data.radius + 15;
		this.color = data.color;
		this.startTime = System.currentTimeMillis();
		this.currentRadius = data.radius;
		this.alpha = 1.0f;
		this.morphProgress = 0f;

		if (data instanceof PopEffectData) {
			PopEffectData popData = (PopEffectData) data;
			this.isGeometric = popData.isGeometric;
			this.shapeType = popData.shapeType;
			this.rotation = popData.rotation;
			this.shouldMorph = false;
			this.morphTargetShape = null;
			this.morphRotation = 0f;
		} else {

			this.isGeometric = false;
			this.shapeType = null;
			this.rotation = 0f;
			this.shouldMorph = true;

			GeometricShape.ShapeType[] shapes = GeometricShape.ShapeType.values();
			this.morphTargetShape = shapes[morphRandom.nextInt(shapes.length)];
			this.morphRotation = morphRandom.nextFloat() * 360f;
		}
	}

	@Override
	public void update() {
		long elapsed = System.currentTimeMillis() - startTime;
		float totalProgress = elapsed / (float) TOTAL_DURATION;

		if (totalProgress <= 1.0f) {

			currentRadius = initialRadius + (maxRadius - initialRadius) * easeOut(Math.min(1.0f, totalProgress * 1.5f));

			alpha = 1.0f - (totalProgress * totalProgress);

			if (shouldMorph) {
				updateMorphProgress(elapsed);
			}
		}
	}

	private void updateMorphProgress(long elapsed) {
		if (elapsed <= EXPANSION_PHASE) {
			morphProgress = 0f;
		} else if (elapsed <= EXPANSION_PHASE + MORPH_PHASE) {
			float morphElapsed = elapsed - EXPANSION_PHASE;
			morphProgress = easeInOut(morphElapsed / (float) MORPH_PHASE);
		} else {
			morphProgress = 1.0f;
		}
	}

	@Override
	public boolean isExpired() {
		return System.currentTimeMillis() - startTime > TOTAL_DURATION;
	}

	@Override
	public void render(Graphics2D g2d) {
		if (alpha <= 0) return;

		AffineTransform originalTransform = g2d.getTransform();

		if (shouldMorph) {
			renderMorphingPop(g2d);
		} else if (isGeometric) {
			renderGeometricPop(g2d);
		} else {
			renderCircularPop(g2d);
		}

		g2d.setTransform(originalTransform);
	}

	private void renderMorphingPop(Graphics2D g2d) {
		g2d.translate(x, y);

		float gentleRotation = morphRotation * 0.1f * morphProgress;
		g2d.rotate(Math.toRadians(gentleRotation));

		Shape morphedShape = ShapeMorpher.createMorphedShape(morphTargetShape, currentRadius, morphProgress);

		ShapeRenderer.renderShapeOutline(g2d, morphedShape, color, alpha, 3f);

		float alpha2 = Math.max(0, alpha - 0.3f);
		if (alpha2 > 0) {
			Shape largerShape = ShapeMorpher.createMorphedShape(morphTargetShape, currentRadius + 5, morphProgress);
			ShapeRenderer.renderShapeOutline(g2d, largerShape, color, alpha2, 2f);
		}

		float alpha3 = Math.max(0, alpha - 0.1f);
		if (alpha3 > 0) {
			Shape innerShape = ShapeMorpher.createMorphedShape(morphTargetShape, currentRadius - 3, morphProgress);
			ShapeRenderer.renderShapeOutline(g2d, innerShape, Color.WHITE, alpha3 * 0.6f, 1.5f);
		}

		if (morphProgress > 0.3f) {
			ShapeRenderer.renderShapeSparkles(g2d, morphTargetShape, currentRadius, color, alpha * morphProgress);
		}
	}

	private void renderGeometricPop(Graphics2D g2d) {
		g2d.translate(x, y);
		g2d.rotate(Math.toRadians(rotation));

		Shape shape = ShapeFactory.createShape(shapeType, currentRadius);

		ShapeRenderer.renderShapeOutline(g2d, shape, color, alpha, 3f);

		float alpha2 = Math.max(0, alpha - 0.3f);
		if (alpha2 > 0) {
			Shape largerShape = ShapeFactory.createShape(shapeType, currentRadius + 5);
			ShapeRenderer.renderShapeOutline(g2d, largerShape, color, alpha2, 2f);
		}

		float alpha3 = Math.max(0, alpha - 0.1f);
		if (alpha3 > 0) {
			Shape innerShape = ShapeFactory.createShape(shapeType, currentRadius - 3);
			ShapeRenderer.renderShapeOutline(g2d, innerShape, Color.WHITE, alpha3 * 0.6f, 1.5f);
		}

		ShapeRenderer.renderShapeSparkles(g2d, shapeType, currentRadius, color, alpha);
	}

	private void renderCircularPop(Graphics2D g2d) {

		float diameter = currentRadius * 2;
		Shape circle = new Ellipse2D.Float(x - currentRadius, y - currentRadius, diameter, diameter);

		ShapeRenderer.renderShapeOutline(g2d, circle, color, alpha, 3f);

		float alpha2 = Math.max(0, alpha - 0.3f);
		if (alpha2 > 0) {
			Shape largerCircle = new Ellipse2D.Float(x - currentRadius - 5, y - currentRadius - 5,
				diameter + 10, diameter + 10);
			ShapeRenderer.renderShapeOutline(g2d, largerCircle, color, alpha2, 2f);
		}

		float alpha3 = Math.max(0, alpha - 0.1f);
		if (alpha3 > 0) {
			Shape innerCircle = new Ellipse2D.Float(x - currentRadius + 3, y - currentRadius + 3,
				diameter - 6, diameter - 6);
			ShapeRenderer.renderShapeOutline(g2d, innerCircle, Color.WHITE, alpha3 * 0.6f, 1.5f);
		}
	}

	private float easeOut(float t) {
		return 1 - (1 - t) * (1 - t);
	}

	private float easeInOut(float t) {
		return t < 0.5f ? 2 * t * t : 1 - 2 * (1 - t) * (1 - t);
	}
}
