package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import com.bestbudz.rendering.Projectile;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class ClearExpiredProjectiles extends Client
{
	public static void clearExpiredProjectiles()
	{
		for (Projectile class30_sub2_sub4_sub4 = (Projectile) nodeList
			.reverseGetFirst(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (Projectile) nodeList
			.reverseGetNext())
			if (class30_sub2_sub4_sub4.targetX != plane || loopCycle > class30_sub2_sub4_sub4.endTime)
				class30_sub2_sub4_sub4.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub4.startX)
			{
				if (class30_sub2_sub4_sub4.targetId > 0)
				{
					Npc npc = npcArray[class30_sub2_sub4_sub4.targetId - 1];
					if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
						class30_sub2_sub4_sub4.calculateTrajectory(loopCycle, npc.y,
							getTerrainHeight(class30_sub2_sub4_sub4.targetX, npc.y, npc.x)
								- class30_sub2_sub4_sub4.startCycle,
							npc.x);
				}
				if (class30_sub2_sub4_sub4.targetId < 0)
				{
					int j = -class30_sub2_sub4_sub4.targetId - 1;
					Stoner stoner;
					if (j == unknownInt10)
						stoner = myStoner;
					else
						stoner = stonerArray[j];
					if (stoner != null && stoner.x >= 0 && stoner.x < 13312 && stoner.y >= 0 && stoner.y < 13312)
						class30_sub2_sub4_sub4.calculateTrajectory(loopCycle, stoner.y,
							getTerrainHeight(class30_sub2_sub4_sub4.targetX, stoner.y, stoner.x)
								- class30_sub2_sub4_sub4.startCycle,
							stoner.x);
				}
				class30_sub2_sub4_sub4.updatePosition(gameTickCounter);
				worldController.addLargeObject(plane, class30_sub2_sub4_sub4.yaw,
					(int) class30_sub2_sub4_sub4.currentHeight, -1, (int) class30_sub2_sub4_sub4.currentY, 60,
					(int) class30_sub2_sub4_sub4.currentX, class30_sub2_sub4_sub4, false);
			}

	}
}
