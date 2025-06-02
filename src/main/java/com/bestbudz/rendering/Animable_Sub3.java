package com.bestbudz.rendering;

import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;

/**
 * Optimized Animable_Sub3
 */
public final class Animable_Sub3 extends Animable {

	// Constants for better readability and performance
	private static final int ROTATION_90 = 90;
	private static final int ROTATION_180 = 180;
	private static final int ROTATION_270 = 270;
	private static final int DEFAULT_SCALE = 128;
	private static final int LIGHTING_AMBIENT_BASE = 64;
	private static final int LIGHTING_DIFFUSE_BASE = 850;

	public final int anInt1560;
	public final int anInt1561;
	public final int anInt1562;
	public final int anInt1563;
	public final int anInt1564;
	public boolean aBoolean1567;

	private final SpotAnim aSpotAnim_1568;
	private int anInt1569;
	private int anInt1570;
	private int nextAnimFrameId = -1;

	public Animable_Sub3(int i, int j, int l, int i1, int j1, int k1, int l1) {
		aSpotAnim_1568 = SpotAnim.cache[i1];
		anInt1560 = i;
		anInt1561 = l1;
		anInt1562 = k1;
		anInt1563 = j1;
		anInt1564 = j + l;
		aBoolean1567 = false;

		updateNextAnimFrame();
	}

	public Model getFinalRenderedModel() {
		final Model baseModel = aSpotAnim_1568.getModel();
		if (baseModel == null) {
			return null;
		}

		// Cache animation reference for better performance
		final Animation animation = aSpotAnim_1568.aAnimation_407;
		final int currentFrame = animation.anIntArray353[anInt1569];

		// Create model copy
		final Model model = new Model(true, SequenceFrame.method532(currentFrame), false, baseModel);

		if (!aBoolean1567) {
			model.calculateNormals();

			if (nextAnimFrameId != -1) {
				final int nextFrame = animation.anIntArray353[nextAnimFrameId];
				final int frameDuration = animation.anIntArray355[anInt1569];
				model.interpolateFrames(currentFrame, nextFrame, anInt1570, frameDuration);
			} else {
				model.method470(currentFrame);
			}

			// Clear unused arrays for better memory usage
			model.anIntArrayArray1658 = null;
			model.anIntArrayArray1657 = null;
		}

		// Apply transformations efficiently
		applyModelTransformations(model);

		return model;
	}

	/**
	 * Applies scaling, rotation, and lighting transformations to the model.
	 */
	private void applyModelTransformations(Model model) {
		// Apply scaling if needed
		if (aSpotAnim_1568.anInt410 != DEFAULT_SCALE || aSpotAnim_1568.anInt411 != DEFAULT_SCALE) {
			model.modelScale(aSpotAnim_1568.anInt410, aSpotAnim_1568.anInt410, aSpotAnim_1568.anInt411);
		}

		// Apply rotation efficiently
		final int rotation = aSpotAnim_1568.anInt412;
		if (rotation != 0) {
			final int rotationCount = rotation / ROTATION_90;
			for (int i = 0; i < rotationCount; i++) {
				model.method473();
			}
		}

		// Apply lighting
		model.applyLighting(
			LIGHTING_AMBIENT_BASE + aSpotAnim_1568.anInt413,
			LIGHTING_DIFFUSE_BASE + aSpotAnim_1568.anInt414,
			-30, -50, -30, true
		);
	}

	public void method454(int cycles) {
		anInt1570 += cycles;

		final Animation animation = aSpotAnim_1568.aAnimation_407;

		// Process frame advancement
		while (anInt1570 > animation.method258(anInt1569)) {
			anInt1570 -= animation.method258(anInt1569) + 1;
			anInt1569++;

			if (anInt1569 >= animation.anInt352) {
				anInt1569 = 0;
				aBoolean1567 = true;
			}

			updateNextAnimFrame();
		}
	}

	/**
	 * Always calculates for consistent smooth animations.
	 */
	private void updateNextAnimFrame() {
		nextAnimFrameId = anInt1569 + 1;

		if (nextAnimFrameId >= aSpotAnim_1568.aAnimation_407.anInt352) {
			nextAnimFrameId = -1;
		}
	}
}