package com.bestbudz.entity;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.rendering.SpotAnim2.animateEntity;
import com.bestbudz.rendering.animation.Animation;

public class EntityMovement extends Client
{
	public static void updateAllStoners()
	{
		for (int i = -1; i < stonerCount; i++)
		{
			int j;
			if (i == -1)
				j = myStonerIndex;
			else
				j = stonerIndices[i];
			Stoner stoner = stonerArray[j];
			if (stoner != null)
				processEntityMovement(stoner);
		}

	}

	public static void updateAllNpcs()
	{
		for (int j = 0; j < npcCount; j++)
		{
			int k = npcIndices[j];
			Npc npc = npcArray[k];
			if (npc != null)
				processEntityMovement(npc);
		}
	}

	public static void processEntityMovement(Entity entity)
	{
		if (entity.x < 128 || entity.y < 128 || entity.x >= 13184 || entity.y >= 13184)
		{
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity == myStoner && (entity.x < 1536 || entity.y < 1536 || entity.x >= 11776 || entity.y >= 11776))
		{
			entity.anim = -1;
			entity.anInt1520 = -1;
			entity.anInt1547 = 0;
			entity.anInt1548 = 0;
			entity.x = entity.smallX[0] * 128 + entity.anInt1540 * 64;
			entity.y = entity.smallY[0] * 128 + entity.anInt1540 * 64;
			entity.method446();
		}
		if (entity.anInt1547 > loopCycle)
		{
			interpolateEntityMovement(entity);
		}
		else if (entity.anInt1548 >= loopCycle)
		{
			handleEntityIntermediateMovement(entity);
		}
		else
		{
			processEntityWalking(entity);
		}
		updateEntityFacing(entity);
		animateEntity(entity);
	}

	public static void interpolateEntityMovement(Entity entity)
	{
		int i = entity.anInt1547 - loopCycle;
		int j = entity.anInt1543 * 128 + entity.anInt1540 * 64;
		int k = entity.anInt1545 * 128 + entity.anInt1540 * 64;
		entity.x += (j - entity.x) / i;
		entity.y += (k - entity.y) / i;
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
	}

	public static void handleEntityIntermediateMovement(Entity entity)
	{
		if (entity.anInt1548 == loopCycle || entity.anim == -1 || entity.anInt1529 != 0
			|| entity.anInt1528 + 1 > Animation.anims[entity.anim].method258(entity.anInt1527))
		{
			int i = entity.anInt1548 - entity.anInt1547;
			int j = loopCycle - entity.anInt1547;
			int k = entity.anInt1543 * 128 + entity.anInt1540 * 64;
			int l = entity.anInt1545 * 128 + entity.anInt1540 * 64;
			int i1 = entity.anInt1544 * 128 + entity.anInt1540 * 64;
			int j1 = entity.anInt1546 * 128 + entity.anInt1540 * 64;
			entity.x = (k * (i - j) + i1 * j) / i;
			entity.y = (l * (i - j) + j1 * j) / i;
		}
		entity.anInt1503 = 0;
		if (entity.anInt1549 == 0)
			entity.turnDirection = 1024;
		if (entity.anInt1549 == 1)
			entity.turnDirection = 1536;
		if (entity.anInt1549 == 2)
			entity.turnDirection = 0;
		if (entity.anInt1549 == 3)
			entity.turnDirection = 512;
		entity.anInt1552 = entity.turnDirection;
	}

	public  static void processEntityWalking(Entity entity)
	{
		try
		{
			entity.anInt1517 = entity.anInt1511;
			if (entity.smallXYIndex == 0)
			{
				entity.anInt1503 = 0;
				return;
			}
			if (entity.anim != -1 && entity.anInt1529 == 0)
			{
				Animation animation = Animation.anims[entity.anim];
				if (entity.anInt1542 > 0 && animation.anInt363 == 0)
				{
					entity.anInt1503++;
					return;
				}
				if (entity.anInt1542 <= 0 && animation.anInt364 == 0)
				{
					entity.anInt1503++;
					return;
				}
			}
			int i = entity.x;
			int j = entity.y;
			int k = entity.smallX[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
			int l = entity.smallY[entity.smallXYIndex - 1] * 128 + entity.anInt1540 * 64;
			if (k - i > 256 || k - i < -256 || l - j > 256 || l - j < -256)
			{
				entity.x = k;
				entity.y = l;
				return;
			}
			if (i < k)
			{
				if (j < l)
					entity.turnDirection = 1280;
				else if (j > l)
					entity.turnDirection = 1792;
				else
					entity.turnDirection = 1536;
			}
			else if (i > k)
			{
				if (j < l)
					entity.turnDirection = 768;
				else if (j > l)
					entity.turnDirection = 256;
				else
					entity.turnDirection = 512;
			}
			else if (j < l)
				entity.turnDirection = 1024;
			else
				entity.turnDirection = 0;
			int i1 = entity.turnDirection - entity.anInt1552 & 0x7ff;
			if (i1 > 1024)
				i1 -= 2048;
			int j1 = entity.anInt1555;
			if (i1 >= -256 && i1 <= 256)
				j1 = entity.anInt1554;
			else if (i1 >= 256 && i1 < 768)
				j1 = entity.anInt1557;
			else if (i1 >= -768 && i1 <= -256)
				j1 = entity.anInt1556;
			if (j1 == -1)
				j1 = entity.anInt1554;
			entity.anInt1517 = j1;
			int k1 = 4;
			if (entity.anInt1552 != entity.turnDirection && entity.interactingEntity == -1 && entity.anInt1504 != 0)
				k1 = 2;
			if (entity.smallXYIndex > 2)
				k1 = 6;
			if (entity.smallXYIndex > 3)
				k1 = 8;
			if (entity.anInt1503 > 0 && entity.smallXYIndex > 1)
			{
				k1 = 8;
				entity.anInt1503--;
			}
			if (entity.aBooleanArray1553[entity.smallXYIndex - 1])
				k1 <<= 1;
			if (k1 >= 8 && entity.anInt1517 == entity.anInt1554 && entity.anInt1505 != -1)
				entity.anInt1517 = entity.anInt1505;
			if (i < k)
			{
				entity.x += k1;
				if (entity.x > k)
					entity.x = k;
			}
			else if (i > k)
			{
				entity.x -= k1;
				if (entity.x < k)
					entity.x = k;
			}
			if (j < l)
			{
				entity.y += k1;
				if (entity.y > l)
					entity.y = l;
			}
			else if (j > l)
			{
				entity.y -= k1;
				if (entity.y < l)
					entity.y = l;
			}
			if (entity.x == k && entity.y == l)
			{
				entity.smallXYIndex--;
				if (entity.anInt1542 > 0)
					entity.anInt1542--;
			}
		}
		catch (Exception e)
		{

		}
	}

	public static void updateEntityFacing(Entity entity)
	{
		if (entity.anInt1504 == 0)
			return;
		if (entity.interactingEntity != -1 && entity.interactingEntity < 32768)
		{
			Npc npc = npcArray[entity.interactingEntity];
			if (npc != null)
			{
				int i1 = entity.x - npc.x;
				int k1 = entity.y - npc.y;
				if (i1 != 0 || k1 != 0)
					entity.turnDirection = (int) (Math.atan2(i1, k1) * 325.94900000000001D) & 0x7ff;
			}
		}
		if (entity.interactingEntity >= 32768)
		{
			int j = entity.interactingEntity - 32768;
			if (j == unknownInt10)
				j = myStonerIndex;
			Stoner stoner = stonerArray[j];
			if (stoner != null)
			{
				int l1 = entity.x - stoner.x;
				int i2 = entity.y - stoner.y;
				if (l1 != 0 || i2 != 0)
					entity.turnDirection = (int) (Math.atan2(l1, i2) * 325.94900000000001D) & 0x7ff;
			}
		}
		if ((entity.anInt1538 != 0 || entity.anInt1539 != 0) && (entity.smallXYIndex == 0 || entity.anInt1503 > 0))
		{
			int k = entity.x - (entity.anInt1538 - baseX - baseX) * 64;
			int j1 = entity.y - (entity.anInt1539 - baseY - baseY) * 64;
			if (k != 0 || j1 != 0)
				entity.turnDirection = (int) (Math.atan2(k, j1) * 325.94900000000001D) & 0x7ff;
			entity.anInt1538 = 0;
			entity.anInt1539 = 0;
		}
		int l = entity.turnDirection - entity.anInt1552 & 0x7ff;
		if (l != 0)
		{
			if (l < entity.anInt1504 || l > 2048 - entity.anInt1504)
				entity.anInt1552 = entity.turnDirection;
			else if (l > 1024)
				entity.anInt1552 -= entity.anInt1504;
			else
				entity.anInt1552 += entity.anInt1504;
			entity.anInt1552 &= 0x7ff;
			if (entity.anInt1517 == entity.anInt1511 && entity.anInt1552 != entity.turnDirection)
			{
				if (entity.anInt1512 != -1)
				{
					entity.anInt1517 = entity.anInt1512;
					return;
				}
				entity.anInt1517 = entity.anInt1554;
			}
		}
	}
}
