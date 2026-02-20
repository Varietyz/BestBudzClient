package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Entity;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.world.SpotAnimationNode;
import com.bestbudz.engine.core.gamerender.ObjectManager;

import static com.bestbudz.rendering.GameObject.addGameObjectToScene;

public final class AnimationManager extends Client {

	public static void updateSpotAnimations() {
		if (loadingStage != 2) return;

		for (SpotAnimationNode spotAnim = (SpotAnimationNode) spotAnimationQueue.reverseGetFirst();
			 spotAnim != null;
			 spotAnim = (SpotAnimationNode) spotAnimationQueue.reverseGetNext()) {

			if (spotAnim.delay > 0) {
				spotAnim.delay--;
			}

			if (spotAnim.delay == 0) {
				if (spotAnim.previousObjectId < 0 || ObjectManager.isValidObject(spotAnim.previousObjectId, spotAnim.previousObjectRotation)) {
					addGameObjectToScene(spotAnim.y, spotAnim.plane, spotAnim.previousObjectType,
						spotAnim.previousObjectRotation, spotAnim.x, spotAnim.type, spotAnim.previousObjectId);
					spotAnim.unlink();
				}
			} else {
				if (spotAnim.timer > 0) {
					spotAnim.timer--;
				}

				if (spotAnim.timer == 0 &&
					spotAnim.x >= 1 && spotAnim.x <= 102 &&
					spotAnim.y >= 1 && spotAnim.y <= 102 &&
					(spotAnim.objectId < 0 || ObjectManager.isValidObject(spotAnim.objectId, spotAnim.objectRotation))) {

					addGameObjectToScene(spotAnim.y, spotAnim.plane, spotAnim.objectType,
						spotAnim.objectRotation, spotAnim.x, spotAnim.type, spotAnim.objectId);
					spotAnim.timer = -1;

					if ((spotAnim.objectId == spotAnim.previousObjectId && spotAnim.previousObjectId == -1) ||
						(spotAnim.objectId == spotAnim.previousObjectId &&
							spotAnim.objectType == spotAnim.previousObjectType &&
							spotAnim.objectRotation == spotAnim.previousObjectRotation)) {
						spotAnim.unlink();
					}
				}
			}
		}
	}

	public static void processEntityAnimation(Entity entity) {
		if (entity == null) return;

		if (entity.currentAnimation > 13798) {
			entity.currentAnimation = -1;
		}

		entity.animChanged = false;

		if (entity.currentAnimation != -1) {
			Animation animation = Animation.anims[entity.currentAnimation];
			if (animation != null) {
				entity.frameTimer++;
				if (entity.frameIndex < animation.frameCount &&
					entity.frameTimer > animation.getFrameDuration(entity.frameIndex)) {
					entity.frameTimer = 1;
					entity.frameIndex++;
					entity.nextIdleFrame++;
				}

				entity.nextIdleFrame = entity.frameIndex + 1;
				if (entity.nextIdleFrame >= animation.frameCount) {
					entity.nextIdleFrame = 0;
				}

				if (entity.frameIndex >= animation.frameCount) {
					entity.frameTimer = 1;
					entity.frameIndex = 0;
				}
			} else {
				entity.currentAnimation = -1;
			}
		}

		if (entity.spotAnimId != -1 && loopCycle >= entity.spotAnimStart) {
			if (entity.spotAnimFrame < 0) {
				entity.spotAnimFrame = 0;
			}

			Animation spotAnimation = SpotAnim.cache[entity.spotAnimId].animation;
			if (spotAnimation != null) {
				entity.spotAnimTimer++;
				while (entity.spotAnimFrame < spotAnimation.frameCount &&
					entity.spotAnimTimer > spotAnimation.getFrameDuration(entity.spotAnimFrame)) {
					entity.spotAnimTimer -= spotAnimation.getFrameDuration(entity.spotAnimFrame);
					entity.spotAnimFrame++;
				}

				if (entity.spotAnimFrame >= spotAnimation.frameCount) {
					entity.spotAnimId = -1;
				}

				entity.nextIdleFrame = entity.frameIndex + 1;
				if (entity.nextSpotFrame >= spotAnimation.frameCount) {
					entity.nextSpotFrame = -1;
				}
			} else {
				entity.spotAnimId = -1;
			}
		}

		if (entity.anim != -1) {
			Animation mainAnimation = Animation.anims[entity.anim];
			if (mainAnimation != null) {
				if (entity.animDelay <= 1 &&
					mainAnimation.priority == 1 &&
					entity.movementDelay > 0 &&
					entity.animStart <= loopCycle &&
					entity.animEnd < loopCycle) {
					entity.animDelay = 1;
					return;
				}

				if (entity.animDelay == 0) {
					entity.animFrameTimer++;
					while (entity.animFrameIndex < mainAnimation.frameCount &&
						entity.animFrameTimer > mainAnimation.getFrameDuration(entity.animFrameIndex)) {
						entity.animFrameTimer -= mainAnimation.getFrameDuration(entity.animFrameIndex);
						entity.animFrameIndex++;
					}

					if (entity.animFrameIndex >= mainAnimation.frameCount) {
						entity.animFrameIndex -= mainAnimation.loopOffset;
						entity.animLoopCount++;

						if (entity.animLoopCount >= mainAnimation.maxLoops) {
							entity.anim = -1;
							return;
						}

						if (entity.animFrameIndex < 0 || entity.animFrameIndex >= mainAnimation.frameCount) {
							entity.anim = -1;
							return;
						}
					}

					entity.nextAnimationFrame = entity.animFrameIndex + 1;
					if (entity.nextAnimationFrame >= mainAnimation.frameCount) {
						entity.nextAnimationFrame = -1;
					}

					entity.animChanged = mainAnimation.resetOnMove;
				}

				if (entity.animDelay > 0) {
					entity.animDelay--;
				}
			} else {
				entity.anim = -1;
			}
		}
	}

	public static void processPendingAnimations() {
		for (SpotAnimationNode current = (SpotAnimationNode) spotAnimationQueue.reverseGetFirst();
			 current != null;
			 current = (SpotAnimationNode) spotAnimationQueue.reverseGetNext()) {

			if (current.delay == -1) {
				current.timer = 0;
				loadSpotAnimationData(current);
			} else {
				current.unlink();
			}
		}
	}

	public static void createSpotAnimation(int j, int k, int l, int i1, int j1, int k1, int l1, int i2, int j2)
	{
		SpotAnimationNode SpotAnimationNode = null;
		for (SpotAnimationNode existingNode = (SpotAnimationNode) spotAnimationQueue
			.reverseGetFirst(); existingNode != null; existingNode = (SpotAnimationNode) spotAnimationQueue
			.reverseGetNext())
		{
			if (existingNode.plane != l1 || existingNode.x != i2 || existingNode.y != j1
				|| existingNode.type != i1)
				continue;
			SpotAnimationNode = existingNode;
			break;
		}

		if (SpotAnimationNode == null)
		{
			SpotAnimationNode = new SpotAnimationNode();
			SpotAnimationNode.plane = l1;
			SpotAnimationNode.type = i1;
			SpotAnimationNode.x = i2;
			SpotAnimationNode.y = j1;
			loadSpotAnimationData(SpotAnimationNode);
			spotAnimationQueue.insertHead(SpotAnimationNode);
		}
		SpotAnimationNode.objectId = k;
		SpotAnimationNode.objectRotation = k1;
		SpotAnimationNode.objectType = l;
		SpotAnimationNode.timer = j2;
		SpotAnimationNode.delay = j;
	}

	public static void loadSpotAnimationData(SpotAnimationNode spotAnimationNode)
	{
		int i = 0;
		int j = -1;
		int k = 0;
		int l = 0;
		if (spotAnimationNode.type == 0)
			i = worldController.getWallId(spotAnimationNode.plane, spotAnimationNode.x, spotAnimationNode.y);
		if (spotAnimationNode.type == 1)
			i = worldController.getWallDecorationId(spotAnimationNode.plane, spotAnimationNode.x, spotAnimationNode.y);
		if (spotAnimationNode.type == 2)
			i = worldController.getGameObjectId(spotAnimationNode.plane, spotAnimationNode.x, spotAnimationNode.y);
		if (spotAnimationNode.type == 3)
			i = worldController.getGroundDecorationId(spotAnimationNode.plane, spotAnimationNode.x, spotAnimationNode.y);
		if (i != 0)
		{
			int i1 = worldController.getObjectConfig(spotAnimationNode.plane, spotAnimationNode.x, spotAnimationNode.y,
				i);
			j = i >> 14 & 0x7fff;
			k = i1 & 0x1f;
			l = i1 >> 6;
		}
		spotAnimationNode.previousObjectId = j;
		spotAnimationNode.previousObjectRotation = k;
		spotAnimationNode.previousObjectType = l;
	}
}
