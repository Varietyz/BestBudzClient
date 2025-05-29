package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.world.ObjectManager;

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
				i2 = worldController.method300(j, i1, i);
			if (j1 == 1)
				i2 = worldController.method301(j, i1, i);
			if (j1 == 2)
				i2 = worldController.method302(j, i1, i);
			if (j1 == 3)
				i2 = worldController.method303(j, i1, i);
			if (i2 != 0)
			{
				int i3 = worldController.method304(j, i1, i, i2);
				int j2 = i2 >> 14 & 0x7fff;
				int k2 = i3 & 0x1f;
				int l2 = i3 >> 6;
				if (j1 == 0)
				{
					worldController.method291(i1, j, i, (byte) -119);
					ObjectDef class46 = ObjectDef.forID(j2);
					if (class46.aBoolean767)
						aClass11Array1230[j].method215(l2, k2, class46.aBoolean757, i1, i);
				}
				if (j1 == 1)
					worldController.method292(i, j, i1);
				if (j1 == 2)
				{
					worldController.method293(j, i1, i);
					ObjectDef class46_1 = ObjectDef.forID(j2);
					if (i1 + class46_1.anInt744 > 103 || i + class46_1.anInt744 > 103 || i1 + class46_1.anInt761 > 103
						|| i + class46_1.anInt761 > 103)
						return;
					if (class46_1.aBoolean767)
						aClass11Array1230[j].method216(l2, class46_1.anInt744, i1, i, class46_1.anInt761,
							class46_1.aBoolean757);
				}
				if (j1 == 3)
				{
					worldController.method294(j, i, i1);
					ObjectDef class46_2 = ObjectDef.forID(j2);
					if (class46_2.aBoolean767 && class46_2.hasActions)
						aClass11Array1230[j].method218(i, i1);
				}
			}
			if (k1 >= 0)
			{
				int j3 = j;
				if (j3 < 3 && (byteGroundArray[1][i1][i] & 2) == 2)
					j3++;
				ObjectManager.method188(worldController, k, i, l, j3, aClass11Array1230[j], intGroundArray, i1, k1, j);
			}
		}
	}
}
