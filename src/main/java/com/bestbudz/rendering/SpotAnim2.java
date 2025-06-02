package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Entity;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.world.Class30_Sub1;
import com.bestbudz.engine.core.gamerender.ObjectManager;

import java.util.logging.Level;
import java.util.logging.Logger;

import static com.bestbudz.rendering.GameObject.addGameObjectToScene;

/**
 * Handles spot animation updates and entity animation processing.
 * Optimized for better performance and reliability.
 */
public final class SpotAnim2 extends Client {

	private static final Logger LOGGER = Logger.getLogger(SpotAnim2.class.getName());

	// Constants for magic numbers
	private static final int MAX_ANIMATION_ID = 13798;
	private static final int MIN_COORDINATE = 1;
	private static final int MAX_COORDINATE = 102;
	private static final int LOADING_STAGE_READY = 2;

	/**
	 * Updates all spot animation timers and processes animations that are ready.
	 */
	public static void updateSpotAnimationTimers() {
		if (loadingStage != LOADING_STAGE_READY) {
			return;
		}

		Class30_Sub1 current = (Class30_Sub1) aClass19_1179.reverseGetFirst();
		while (current != null) {
			Class30_Sub1 next = (Class30_Sub1) aClass19_1179.reverseGetNext();

			try {
				processSpotAnimationTimer(current);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error processing spot animation timer", e);
				// Continue processing other animations even if one fails
			}

			current = next;
		}
	}

	/**
	 * Processes a single spot animation timer.
	 */
	private static void processSpotAnimationTimer(Class30_Sub1 spotAnim) {
		if (spotAnim.anInt1294 > 0) {
			spotAnim.anInt1294--;
		}

		if (spotAnim.anInt1294 == 0) {
			processReadySpotAnimation(spotAnim);
		} else {
			processDelayedSpotAnimation(spotAnim);
		}
	}

	/**
	 * Processes spot animations that are ready to be displayed.
	 */
	private static void processReadySpotAnimation(Class30_Sub1 spotAnim) {
		if (spotAnim.anInt1299 < 0 ||
			ObjectManager.method178(spotAnim.anInt1299, spotAnim.anInt1301)) {

			addGameObjectToScene(
				spotAnim.anInt1298, spotAnim.anInt1295, spotAnim.anInt1300,
				spotAnim.anInt1301, spotAnim.anInt1297, spotAnim.anInt1296,
				spotAnim.anInt1299
			);
			spotAnim.unlink();
		}
	}

	/**
	 * Processes spot animations that are still on delay.
	 */
	private static void processDelayedSpotAnimation(Class30_Sub1 spotAnim) {
		if (spotAnim.anInt1302 > 0) {
			spotAnim.anInt1302--;
		}

		if (spotAnim.anInt1302 == 0 &&
			isValidCoordinate(spotAnim.anInt1297) &&
			isValidCoordinate(spotAnim.anInt1298) &&
			(spotAnim.anInt1291 < 0 ||
				ObjectManager.method178(spotAnim.anInt1291, spotAnim.anInt1293))) {

			addGameObjectToScene(
				spotAnim.anInt1298, spotAnim.anInt1295, spotAnim.anInt1292,
				spotAnim.anInt1293, spotAnim.anInt1297, spotAnim.anInt1296,
				spotAnim.anInt1291
			);
			spotAnim.anInt1302 = -1;

			if (shouldUnlinkSpotAnimation(spotAnim)) {
				spotAnim.unlink();
			}
		}
	}

	/**
	 * Validates if a coordinate is within acceptable bounds.
	 */
	private static boolean isValidCoordinate(int coordinate) {
		return coordinate >= MIN_COORDINATE && coordinate <= MAX_COORDINATE;
	}

	/**
	 * Determines if a spot animation should be unlinked.
	 */
	private static boolean shouldUnlinkSpotAnimation(Class30_Sub1 spotAnim) {
		return (spotAnim.anInt1291 == spotAnim.anInt1299 && spotAnim.anInt1299 == -1) ||
			(spotAnim.anInt1291 == spotAnim.anInt1299 &&
				spotAnim.anInt1292 == spotAnim.anInt1300 &&
				spotAnim.anInt1293 == spotAnim.anInt1301);
	}

	/**
	 * Animates an entity by updating its animation frames and states.
	 *
	 * @param entity the entity to animate
	 */
	public static void animateEntity(Entity entity) {
		if (entity == null) {
			return;
		}

		try {
			processBasicAnimation(entity);
			processSpotAnimation(entity);
			processMainAnimation(entity);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, "Error animating entity", e);
		}
	}

	/**
	 * Processes the basic animation state of an entity.
	 */
	private static void processBasicAnimation(Entity entity) {
		// Validate animation ID
		if (entity.anInt1517 > MAX_ANIMATION_ID) {
			entity.anInt1517 = -1;
		}

		entity.aBoolean1541 = false;

		if (entity.anInt1517 == -1) {
			return;
		}

		Animation animation = Animation.anims[entity.anInt1517];
		if (animation == null) {
			entity.anInt1517 = -1;
			return;
		}

		entity.anInt1519++;

		if (entity.anInt1518 < animation.anInt352 &&
			entity.anInt1519 > animation.method258(entity.anInt1518)) {

			entity.anInt1519 = 1;
			entity.anInt1518++;
			entity.nextIdleAnimFrame++;
		}

		entity.nextIdleAnimFrame = entity.anInt1518 + 1;
		if (entity.nextIdleAnimFrame >= animation.anInt352) {
			entity.nextIdleAnimFrame = 0;
		}

		if (entity.anInt1518 >= animation.anInt352) {
			entity.anInt1519 = 1;
			entity.anInt1518 = 0;
		}
	}

	/**
	 * Processes spot animation for an entity.
	 */
	private static void processSpotAnimation(Entity entity) {
		if (entity.anInt1520 == -1 || loopCycle < entity.anInt1523) {
			return;
		}

		if (entity.anInt1521 < 0) {
			entity.anInt1521 = 0;
		}

		Animation spotAnimation = SpotAnim.cache[entity.anInt1520].aAnimation_407;
		if (spotAnimation == null) {
			entity.anInt1520 = -1;
			return;
		}

		entity.anInt1522++;

		while (entity.anInt1521 < spotAnimation.anInt352 &&
			entity.anInt1522 > spotAnimation.method258(entity.anInt1521)) {
			entity.anInt1522 -= spotAnimation.method258(entity.anInt1521);
			entity.anInt1521++;
		}

		if (entity.anInt1521 >= spotAnimation.anInt352) {
			entity.anInt1520 = -1;
		}

		entity.nextIdleAnimFrame = entity.anInt1518 + 1;

		if (entity.nextSpotAnimFrame >= spotAnimation.anInt352) {
			entity.nextSpotAnimFrame = -1;
		}
	}

	/**
	 * Processes the main animation for an entity.
	 */
	private static void processMainAnimation(Entity entity) {
		if (entity.anim == -1) {
			return;
		}

		Animation mainAnimation = Animation.anims[entity.anim];
		if (mainAnimation == null) {
			entity.anim = -1;
			return;
		}

		// Handle special case for certain animation types
		if (entity.anInt1529 <= 1 &&
			mainAnimation.anInt363 == 1 &&
			entity.anInt1542 > 0 &&
			entity.anInt1547 <= loopCycle &&
			entity.anInt1548 < loopCycle) {
			entity.anInt1529 = 1;
			return;
		}

		if (entity.anInt1529 == 0) {
			processMainAnimationFrames(entity, mainAnimation);
		}

		if (entity.anInt1529 > 0) {
			entity.anInt1529--;
		}
	}

	/**
	 * Processes the frame updates for main animation.
	 */
	private static void processMainAnimationFrames(Entity entity, Animation animation) {
		entity.anInt1528++;

		while (entity.anInt1527 < animation.anInt352 &&
			entity.anInt1528 > animation.method258(entity.anInt1527)) {
			entity.anInt1528 -= animation.method258(entity.anInt1527);
			entity.anInt1527++;
		}

		if (entity.anInt1527 >= animation.anInt352) {
			entity.anInt1527 -= animation.anInt356;
			entity.anInt1530++;

			if (entity.anInt1530 >= animation.anInt362) {
				entity.anim = -1;
				return;
			}

			if (entity.anInt1527 < 0 || entity.anInt1527 >= animation.anInt352) {
				entity.anim = -1;
				return;
			}
		}

		entity.nextAnimFrame = entity.anInt1527 + 1;
		if (entity.nextAnimFrame >= animation.anInt352) {
			entity.nextAnimFrame = -1;
		}

		entity.aBoolean1541 = animation.aBoolean358;
	}

	/**
	 * Updates pending spot animations that are ready to be processed.
	 */
	public static void updatePendingSpotAnimations() {
		Class30_Sub1 current = (Class30_Sub1) aClass19_1179.reverseGetFirst();

		while (current != null) {
			Class30_Sub1 next = (Class30_Sub1) aClass19_1179.reverseGetNext();

			try {
				if (current.anInt1294 == -1) {
					current.anInt1302 = 0;
					processSpotAnimations(current);
				} else {
					current.unlink();
				}
			} catch (Exception e) {
				LOGGER.log(Level.WARNING, "Error updating pending spot animation", e);
			}

			current = next;
		}
	}
}