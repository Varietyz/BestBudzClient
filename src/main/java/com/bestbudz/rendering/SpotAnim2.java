package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Entity;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.world.Class30_Sub1;
import com.bestbudz.engine.core.gamerender.ObjectManager;

import static com.bestbudz.rendering.GameObject.addGameObjectToScene;

public final class SpotAnim2 extends Client {

	public static void updateSpotAnimationTimers() {
		if (loadingStage != 2) return;

		for (Class30_Sub1 spotAnim = (Class30_Sub1) aClass19_1179.reverseGetFirst();
			 spotAnim != null;
			 spotAnim = (Class30_Sub1) aClass19_1179.reverseGetNext()) {

			if (spotAnim.anInt1294 > 0) {
				spotAnim.anInt1294--;
			}

			if (spotAnim.anInt1294 == 0) {
				if (spotAnim.anInt1299 < 0 || ObjectManager.method178(spotAnim.anInt1299, spotAnim.anInt1301)) {
					addGameObjectToScene(spotAnim.anInt1298, spotAnim.anInt1295, spotAnim.anInt1300,
						spotAnim.anInt1301, spotAnim.anInt1297, spotAnim.anInt1296, spotAnim.anInt1299);
					spotAnim.unlink();
				}
			} else {
				if (spotAnim.anInt1302 > 0) {
					spotAnim.anInt1302--;
				}

				if (spotAnim.anInt1302 == 0 &&
					spotAnim.anInt1297 >= 1 && spotAnim.anInt1297 <= 102 &&
					spotAnim.anInt1298 >= 1 && spotAnim.anInt1298 <= 102 &&
					(spotAnim.anInt1291 < 0 || ObjectManager.method178(spotAnim.anInt1291, spotAnim.anInt1293))) {

					addGameObjectToScene(spotAnim.anInt1298, spotAnim.anInt1295, spotAnim.anInt1292,
						spotAnim.anInt1293, spotAnim.anInt1297, spotAnim.anInt1296, spotAnim.anInt1291);
					spotAnim.anInt1302 = -1;

					if ((spotAnim.anInt1291 == spotAnim.anInt1299 && spotAnim.anInt1299 == -1) ||
						(spotAnim.anInt1291 == spotAnim.anInt1299 &&
							spotAnim.anInt1292 == spotAnim.anInt1300 &&
							spotAnim.anInt1293 == spotAnim.anInt1301)) {
						spotAnim.unlink();
					}
				}
			}
		}
	}

	public static void animateEntity(Entity entity) {
		if (entity == null) return;

		// Basic animation processing
		if (entity.anInt1517 > 13798) {
			entity.anInt1517 = -1;
		}

		entity.aBoolean1541 = false;

		if (entity.anInt1517 != -1) {
			Animation animation = Animation.anims[entity.anInt1517];
			if (animation != null) {
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
			} else {
				entity.anInt1517 = -1;
			}
		}

		// Spot animation processing
		if (entity.anInt1520 != -1 && loopCycle >= entity.anInt1523) {
			if (entity.anInt1521 < 0) {
				entity.anInt1521 = 0;
			}

			Animation spotAnimation = SpotAnim.cache[entity.anInt1520].aAnimation_407;
			if (spotAnimation != null) {
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
			} else {
				entity.anInt1520 = -1;
			}
		}

		// Main animation processing
		if (entity.anim != -1) {
			Animation mainAnimation = Animation.anims[entity.anim];
			if (mainAnimation != null) {
				if (entity.anInt1529 <= 1 &&
					mainAnimation.anInt363 == 1 &&
					entity.anInt1542 > 0 &&
					entity.anInt1547 <= loopCycle &&
					entity.anInt1548 < loopCycle) {
					entity.anInt1529 = 1;
					return;
				}

				if (entity.anInt1529 == 0) {
					entity.anInt1528++;
					while (entity.anInt1527 < mainAnimation.anInt352 &&
						entity.anInt1528 > mainAnimation.method258(entity.anInt1527)) {
						entity.anInt1528 -= mainAnimation.method258(entity.anInt1527);
						entity.anInt1527++;
					}

					if (entity.anInt1527 >= mainAnimation.anInt352) {
						entity.anInt1527 -= mainAnimation.anInt356;
						entity.anInt1530++;

						if (entity.anInt1530 >= mainAnimation.anInt362) {
							entity.anim = -1;
							return;
						}

						if (entity.anInt1527 < 0 || entity.anInt1527 >= mainAnimation.anInt352) {
							entity.anim = -1;
							return;
						}
					}

					entity.nextAnimFrame = entity.anInt1527 + 1;
					if (entity.nextAnimFrame >= mainAnimation.anInt352) {
						entity.nextAnimFrame = -1;
					}

					entity.aBoolean1541 = mainAnimation.aBoolean358;
				}

				if (entity.anInt1529 > 0) {
					entity.anInt1529--;
				}
			} else {
				entity.anim = -1;
			}
		}
	}

	public static void updatePendingSpotAnimations() {
		for (Class30_Sub1 current = (Class30_Sub1) aClass19_1179.reverseGetFirst();
			 current != null;
			 current = (Class30_Sub1) aClass19_1179.reverseGetNext()) {

			if (current.anInt1294 == -1) {
				current.anInt1302 = 0;
				processSpotAnimations(current);
			} else {
				current.unlink();
			}
		}
	}
}