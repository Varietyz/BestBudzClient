package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.entity.Entity;
import static com.bestbudz.rendering.GameObject.addGameObjectToScene;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.world.Class30_Sub1;
import com.bestbudz.world.ObjectManager;

public class SpotAnim2 extends Client
{
	public static void updateSpotAnimationTimers()
	{
		if (loadingStage == 2)
		{
			for (Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179
				.reverseGetFirst(); class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179
				.reverseGetNext())
			{
				if (class30_sub1.anInt1294 > 0)
					class30_sub1.anInt1294--;
				if (class30_sub1.anInt1294 == 0)
				{
					if (class30_sub1.anInt1299 < 0
						|| ObjectManager.method178(class30_sub1.anInt1299, class30_sub1.anInt1301))
					{
						addGameObjectToScene(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1300,
							class30_sub1.anInt1301, class30_sub1.anInt1297, class30_sub1.anInt1296,
							class30_sub1.anInt1299);
						class30_sub1.unlink();
					}
				}
				else
				{
					if (class30_sub1.anInt1302 > 0)
						class30_sub1.anInt1302--;
					if (class30_sub1.anInt1302 == 0 && class30_sub1.anInt1297 >= 1 && class30_sub1.anInt1298 >= 1
						&& class30_sub1.anInt1297 <= 102 && class30_sub1.anInt1298 <= 102
						&& (class30_sub1.anInt1291 < 0
						|| ObjectManager.method178(class30_sub1.anInt1291, class30_sub1.anInt1293)))
					{
						addGameObjectToScene(class30_sub1.anInt1298, class30_sub1.anInt1295, class30_sub1.anInt1292,
							class30_sub1.anInt1293, class30_sub1.anInt1297, class30_sub1.anInt1296,
							class30_sub1.anInt1291);
						class30_sub1.anInt1302 = -1;
						if (class30_sub1.anInt1291 == class30_sub1.anInt1299 && class30_sub1.anInt1299 == -1)
							class30_sub1.unlink();
						else if (class30_sub1.anInt1291 == class30_sub1.anInt1299
							&& class30_sub1.anInt1292 == class30_sub1.anInt1300
							&& class30_sub1.anInt1293 == class30_sub1.anInt1301)
							class30_sub1.unlink();
					}
				}
			}

		}
	}

	public static void animateEntity(Entity entity)
	{
		try
		{
			if (entity.anInt1517 > 13798)
			{
				entity.anInt1517 = -1;
			}
			entity.aBoolean1541 = false;
			if (entity.anInt1517 != -1)
			{
				Animation animation = Animation.anims[entity.anInt1517];
				entity.anInt1519++;
				if (entity.anInt1518 < animation.anInt352 && entity.anInt1519 > animation.method258(entity.anInt1518))
				{
					entity.anInt1519 = 1;
					entity.anInt1518++;
					entity.nextIdleAnimFrame++;
				}
				entity.nextIdleAnimFrame = entity.anInt1518 + 1;
				if (entity.nextIdleAnimFrame >= animation.anInt352)
				{
					if (entity.nextIdleAnimFrame >= animation.anInt352)
					{
						entity.nextIdleAnimFrame = 0;
					}
				}
				if (entity.anInt1518 >= animation.anInt352)
				{
					entity.anInt1519 = 1;
					entity.anInt1518 = 0;
				}
			}
			if (entity.anInt1520 != -1 && loopCycle >= entity.anInt1523)
			{
				if (entity.anInt1521 < 0)
					entity.anInt1521 = 0;
				Animation animation_1 = SpotAnim.cache[entity.anInt1520].aAnimation_407;
				for (entity.anInt1522++; entity.anInt1521 < animation_1.anInt352
					&& entity.anInt1522 > animation_1.method258(entity.anInt1521); entity.anInt1521++)
					entity.anInt1522 -= animation_1.method258(entity.anInt1521);

				if (entity.anInt1521 >= animation_1.anInt352
					&& (entity.anInt1521 < 0 || entity.anInt1521 >= animation_1.anInt352))
				{
					entity.anInt1520 = -1;
				}
				if (SettingsConfig.enableTweening)
				{
					entity.nextIdleAnimFrame = entity.anInt1518 + 1;
				}
				if (entity.nextSpotAnimFrame >= animation_1.anInt352)
				{
					entity.nextSpotAnimFrame = -1;
				}
			}
			if (entity.anim != -1 && entity.anInt1529 <= 1)
			{
				Animation animation_2 = Animation.anims[entity.anim];
				if (animation_2.anInt363 == 1 && entity.anInt1542 > 0 && entity.anInt1547 <= loopCycle
					&& entity.anInt1548 < loopCycle)
				{
					entity.anInt1529 = 1;
					return;
				}
			}
			if (entity.anim != -1 && entity.anInt1529 == 0)
			{
				Animation animation_3 = Animation.anims[entity.anim];
				for (entity.anInt1528++; entity.anInt1527 < animation_3.anInt352
					&& entity.anInt1528 > animation_3.method258(entity.anInt1527); entity.anInt1527++)
					entity.anInt1528 -= animation_3.method258(entity.anInt1527);

				if (entity.anInt1527 >= animation_3.anInt352)
				{
					entity.anInt1527 -= animation_3.anInt356;
					entity.anInt1530++;
					if (entity.anInt1530 >= animation_3.anInt362)
						entity.anim = -1;
					if (entity.anInt1527 < 0 || entity.anInt1527 >= animation_3.anInt352)
						entity.anim = -1;
				}
				if (SettingsConfig.enableTweening)
				{
					entity.nextAnimFrame = entity.anInt1527 + 1;
				}
				if (entity.nextAnimFrame >= animation_3.anInt352)
				{
					entity.nextAnimFrame = -1;
				}
				entity.aBoolean1541 = animation_3.aBoolean358;
			}
			if (entity.anInt1529 > 0)
				entity.anInt1529--;
		}
		catch (Exception e)
		{

		}
	}

	public static void updatePendingSpotAnimations()
	{
		Class30_Sub1 class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetFirst();
		for (; class30_sub1 != null; class30_sub1 = (Class30_Sub1) aClass19_1179.reverseGetNext())
			if (class30_sub1.anInt1294 == -1)
			{
				class30_sub1.anInt1302 = 0;
				processSpotAnimations(class30_sub1);
			}
			else
			{
				class30_sub1.unlink();
			}

	}
}
