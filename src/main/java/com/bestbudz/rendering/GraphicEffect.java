package com.bestbudz.rendering;

import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;

public final class GraphicEffect extends Animable {

	private static final int ROTATION_90 = 90;
	private static final int ROTATION_180 = 180;
	private static final int ROTATION_270 = 270;
	private static final int DEFAULT_SCALE = 128;
	private static final int LIGHTING_AMBIENT_BASE = 64;
	private static final int LIGHTING_DIFFUSE_BASE = 850;

	public final int plane;
	public final int x;
	public final int y;
	public final int z;
	public final int endCycle;
	public boolean finished;

	private final SpotAnim spotAnim;
	private int frameIndex;
	private int frameTimer;
	private int nextFrameIndex = -1;

	public GraphicEffect(int i, int j, int l, int i1, int j1, int k1, int l1) {
		spotAnim = SpotAnim.cache[i1];
		plane = i;
		x = l1;
		y = k1;
		z = j1;
		endCycle = j + l;
		finished = false;

		updateNextAnimFrame();
	}

	public Model getModel() {
		final Model baseModel = spotAnim.getModel();
		if (baseModel == null) {
			return null;
		}

		final Animation animation = spotAnim.animation;
		final int currentFrame = animation.frameIds[frameIndex];

		final Model model = new Model(true, SequenceFrame.isInvalidFrame(currentFrame), false, baseModel);

		if (!finished) {
			model.calculateNormals();

			if (nextFrameIndex != -1) {
				final int nextFrame = animation.frameIds[nextFrameIndex];
				final int frameDuration = animation.anIntArray355[frameIndex];
				model.interpolateFrames(currentFrame, nextFrame, frameTimer, frameDuration);
			} else {
				model.applyTransformation(currentFrame);
			}

			model.anIntArrayArray1658 = null;
			model.anIntArrayArray1657 = null;
		}

		applyModelTransformations(model);

		return model;
	}

	private void applyModelTransformations(Model model) {

		if (spotAnim.resizeX != DEFAULT_SCALE || spotAnim.resizeY != DEFAULT_SCALE) {
			model.modelScale(spotAnim.resizeX, spotAnim.resizeX, spotAnim.resizeY);
		}

		final int rotation = spotAnim.rotation;
		if (rotation != 0) {
			final int rotationCount = rotation / ROTATION_90;
			for (int i = 0; i < rotationCount; i++) {
				model.rotateY180();
			}
		}

		model.applyLighting(
			LIGHTING_AMBIENT_BASE + spotAnim.ambient,
			LIGHTING_DIFFUSE_BASE + spotAnim.contrast,
			-30, -50, -30, true
		);
	}

	public void update(int cycles) {
		frameTimer += cycles;

		final Animation animation = spotAnim.animation;

		while (frameTimer > animation.getFrameDuration(frameIndex)) {
			frameTimer -= animation.getFrameDuration(frameIndex) + 1;
			frameIndex++;

			if (frameIndex >= animation.frameCount) {
				frameIndex = 0;
				finished = true;
			}

			updateNextAnimFrame();
		}
	}

	private void updateNextAnimFrame() {
		nextFrameIndex = frameIndex + 1;

		if (nextFrameIndex >= spotAnim.animation.frameCount) {
			nextFrameIndex = -1;
		}
	}
}
