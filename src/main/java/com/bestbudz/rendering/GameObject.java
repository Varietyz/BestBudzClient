package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.engine.core.gamerender.ObjectManager;

public class GameObject extends Client
{
	public static void addGameObjectToScene(int i, int j, int k, int l, int i1, int j1, int k1)
	{
		if (i1 >= 1 && i >= 1 && i1 <= 102 && i <= 102)
		{
			if (lowMem && j != plane)
				return;
			int i2 = 0;
			if (j1 == 0)
				i2 = worldController.getWallId(j, i1, i);
			if (j1 == 1)
				i2 = worldController.getWallDecorationId(j, i1, i);
			if (j1 == 2)
				i2 = worldController.getGameObjectId(j, i1, i);
			if (j1 == 3)
				i2 = worldController.getGroundDecorationId(j, i1, i);
			if (i2 != 0)
			{
				int i3 = worldController.getObjectConfig(j, i1, i, i2);
				int j2 = i2 >> 14 & 0x7fff;
				int k2 = i3 & 0x1f;
				int l2 = i3 >> 6;
				if (j1 == 0)
				{
					worldController.removeWall(i1, j, i, (byte) -119);
					ObjectDef class46 = ObjectDef.forID(j2);
					if (class46.clipFlag)
						collisionMaps[j].removeWall(l2, k2, class46.blocksProjectiles, i1, i);
				}
				if (j1 == 1)
					worldController.removeWallDecoration(i, j, i1);
				if (j1 == 2)
				{
					worldController.removeGameObject(j, i1, i);
					ObjectDef class46_1 = ObjectDef.forID(j2);
					if (i1 + class46_1.sizeX > 103 || i + class46_1.sizeX > 103 || i1 + class46_1.sizeY > 103
						|| i + class46_1.sizeY > 103)
						return;
					if (class46_1.clipFlag)
						collisionMaps[j].removeObject(l2, class46_1.sizeX, i1, i, class46_1.sizeY,
							class46_1.blocksProjectiles);
				}
				if (j1 == 3)
				{
					worldController.removeGroundDecoration(j, i, i1);
					ObjectDef class46_2 = ObjectDef.forID(j2);
					if (class46_2.clipFlag && class46_2.hasActions)
						collisionMaps[j].removeFloorDecoration(i, i1);
				}
			}
			if (k1 >= 0)
			{
				int j3 = j;
				if (j3 < 3 && (byteGroundArray[1][i1][i] & 2) == 2)
					j3++;
				ObjectManager.placeWorldObject(worldController, k, i, l, j3, collisionMaps[j], intGroundArray, i1, k1, j);
			}
		}
	}
}
