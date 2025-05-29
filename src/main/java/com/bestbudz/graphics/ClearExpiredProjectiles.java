package com.bestbudz.graphics;

import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import com.bestbudz.rendering.Animable_Sub4;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class ClearExpiredProjectiles extends Client
{
	public static void clearExpiredProjectiles()
	{
		for (Animable_Sub4 class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
			.reverseGetFirst(); class30_sub2_sub4_sub4 != null; class30_sub2_sub4_sub4 = (Animable_Sub4) aClass19_1013
			.reverseGetNext())
			if (class30_sub2_sub4_sub4.anInt1597 != plane || loopCycle > class30_sub2_sub4_sub4.anInt1572)
				class30_sub2_sub4_sub4.unlink();
			else if (loopCycle >= class30_sub2_sub4_sub4.anInt1571)
			{
				if (class30_sub2_sub4_sub4.anInt1590 > 0)
				{
					Npc npc = npcArray[class30_sub2_sub4_sub4.anInt1590 - 1];
					if (npc != null && npc.x >= 0 && npc.x < 13312 && npc.y >= 0 && npc.y < 13312)
						class30_sub2_sub4_sub4.method455(loopCycle, npc.y,
							getTerrainHeight(class30_sub2_sub4_sub4.anInt1597, npc.y, npc.x)
								- class30_sub2_sub4_sub4.anInt1583,
							npc.x);
				}
				if (class30_sub2_sub4_sub4.anInt1590 < 0)
				{
					int j = -class30_sub2_sub4_sub4.anInt1590 - 1;
					Stoner stoner;
					if (j == unknownInt10)
						stoner = myStoner;
					else
						stoner = stonerArray[j];
					if (stoner != null && stoner.x >= 0 && stoner.x < 13312 && stoner.y >= 0 && stoner.y < 13312)
						class30_sub2_sub4_sub4.method455(loopCycle, stoner.y,
							getTerrainHeight(class30_sub2_sub4_sub4.anInt1597, stoner.y, stoner.x)
								- class30_sub2_sub4_sub4.anInt1583,
							stoner.x);
				}
				class30_sub2_sub4_sub4.method456(anInt945);
				worldController.method285(plane, class30_sub2_sub4_sub4.anInt1595,
					(int) class30_sub2_sub4_sub4.aDouble1587, -1, (int) class30_sub2_sub4_sub4.aDouble1586, 60,
					(int) class30_sub2_sub4_sub4.aDouble1585, class30_sub2_sub4_sub4, false);
			}

	}
}
