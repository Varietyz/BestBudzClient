package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class Roofing extends Client
{
	public static int selectRoofPlane()
{
	if (!SettingsConfig.enableRoofs)
	{
		return plane;
	}
	int j = getTerrainHeight(plane, yCameraPos, xCameraPos);
	if (j - zCameraPos < 800 && (byteGroundArray[plane][xCameraPos >> 7][yCameraPos >> 7] & 4) != 0)
		return plane;
	else
		return 3;
}
	public static int getRoofPlane()
	{
		if (!SettingsConfig.enableRoofs)
		{
			return plane;
		}
		int j = 3;
		if (yCameraCurve < 310)
		{
			int k = xCameraPos >> 7;
			int l = yCameraPos >> 7;
			int i1 = myStoner.x >> 7;
			int j1 = myStoner.y >> 7;
			if ((byteGroundArray[plane][k][l] & 4) != 0)
				j = plane;
			int k1;
			if (i1 > k)
				k1 = i1 - k;
			else
				k1 = k - i1;
			int l1;
			if (j1 > l)
				l1 = j1 - l;
			else
				l1 = l - j1;
			if (k1 > l1)
			{
				int i2 = (l1 * 0x10000) / k1;
				int k2 = 32768;
				while (k != i1)
				{
					if (k < i1)
						k++;
					else if (k > i1)
						k--;
					if ((byteGroundArray[plane][k][l] & 4) != 0)
						j = plane;
					k2 += i2;
					if (k2 >= 0x10000)
					{
						k2 -= 0x10000;
						if (l < j1)
							l++;
						else if (l > j1)
							l--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
					}
				}
			}
			else
			{
				int j2 = (k1 * 0x10000) / l1;
				int l2 = 32768;
				while (l != j1)
				{
					if (l < j1)
						l++;
					else if (l > j1)
						l--;
					if ((byteGroundArray[plane][k][l] & 4) != 0)
						j = plane;
					l2 += j2;
					if (l2 >= 0x10000)
					{
						l2 -= 0x10000;
						if (k < i1)
							k++;
						else if (k > i1)
							k--;
						if ((byteGroundArray[plane][k][l] & 4) != 0)
							j = plane;
					}
				}
			}
		}
		if ((byteGroundArray[plane][myStoner.x >> 7][myStoner.y >> 7] & 4) != 0)
			j = plane;
		return j;
	}

}
