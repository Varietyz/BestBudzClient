package com.bestbudz.world;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.input.Keyboard.keyArray;
import com.bestbudz.ui.handling.input.MouseState;

public class WalkTo extends Client
{
	public static void handleWalkToObject() {
		if (WorldController.anInt470 == -1) return;

		int x = WorldController.anInt470;
		int y = WorldController.anInt471;
		WorldController.anInt470 = -1;

		if (doWalkTo(0, 0, 0, 0, myStoner.smallY[0], 0, 0, y, myStoner.smallX[0], true, x)) {
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 1;
			crossIndex = 0;
		}
	}

	public static boolean doWalkTo(int i, int j, int k, int i1, int j1, int k1, int l1, int i2, int j2, boolean flag,
								   int k2)
	{
		int byte0 = 104;
		int byte1 = 104;
		for (int l2 = 0; l2 < byte0; l2++)
		{
			for (int i3 = 0; i3 < byte1; i3++)
			{
				anIntArrayArray901[l2][i3] = 0;
				anIntArrayArray825[l2][i3] = 0x5f5e0ff;
			}
		}
		int j3 = j2;
		int k3 = j1;
		if (j2 < 0 || k3 < 0
			|| j2 >= anIntArrayArray901.length
			|| k3 >= anIntArrayArray901[j2].length) {
			return false;
		}

		k2 = Math.max(0, Math.min(103, k2));
		i2 = Math.max(0, Math.min(103, i2));

		anIntArrayArray901[j2][j1] = 99;
		anIntArrayArray825[j2][j1] = 0;
		int l3 = 0;
		int i4 = 0;
		bigX[l3] = j2;
		bigY[l3++] = j1;
		boolean flag1 = false;
		int j4 = bigX.length;
		int[][] ai = aClass11Array1230[plane].anIntArrayArray294;
		while (i4 != l3)
		{
			j3 = bigX[i4];
			k3 = bigY[i4];
			i4 = (i4 + 1) % j4;
			if (j3 == k2 && k3 == i2)
			{
				flag1 = true;
				break;
			}
			if (i1 != 0)
			{
				if ((i1 < 5 || i1 == 10) && aClass11Array1230[plane].method219(k2, j3, k3, j, i1 - 1, i2))
				{
					flag1 = true;
					break;
				}
				if (i1 < 10 && aClass11Array1230[plane].method220(k2, i2, k3, i1 - 1, j, j3))
				{
					flag1 = true;
					break;
				}
			}
			if (k1 != 0 && k != 0 && aClass11Array1230[plane].method221(i2, k2, j3, k, l1, k1, k3))
			{
				flag1 = true;
				break;
			}
			int l4 = anIntArrayArray825[j3][k3] + 1;
			if (j3 > 0 && anIntArrayArray901[j3 - 1][k3] == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0)
			{
				bigX[l3] = j3 - 1;
				bigY[l3] = k3;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3] = 2;
				anIntArrayArray825[j3 - 1][k3] = l4;
			}
			if (j3 < byte0 - 1 && anIntArrayArray901[j3 + 1][k3] == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0)
			{
				bigX[l3] = j3 + 1;
				bigY[l3] = k3;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3] = 8;
				anIntArrayArray825[j3 + 1][k3] = l4;
			}
			if (k3 > 0 && anIntArrayArray901[j3][k3 - 1] == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0)
			{
				bigX[l3] = j3;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3][k3 - 1] = 1;
				anIntArrayArray825[j3][k3 - 1] = l4;
			}
			if (k3 < byte1 - 1 && anIntArrayArray901[j3][k3 + 1] == 0 && (ai[j3][k3 + 1] & 0x1280120) == 0)
			{
				bigX[l3] = j3;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3][k3 + 1] = 4;
				anIntArrayArray825[j3][k3 + 1] = l4;
			}
			if (j3 > 0 && k3 > 0 && anIntArrayArray901[j3 - 1][k3 - 1] == 0 && (ai[j3 - 1][k3 - 1] & 0x128010e) == 0
				&& (ai[j3 - 1][k3] & 0x1280108) == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0)
			{
				bigX[l3] = j3 - 1;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3 - 1] = 3;
				anIntArrayArray825[j3 - 1][k3 - 1] = l4;
			}
			if (j3 < byte0 - 1 && k3 > 0 && anIntArrayArray901[j3 + 1][k3 - 1] == 0
				&& (ai[j3 + 1][k3 - 1] & 0x1280183) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
				&& (ai[j3][k3 - 1] & 0x1280102) == 0)
			{
				bigX[l3] = j3 + 1;
				bigY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3 - 1] = 9;
				anIntArrayArray825[j3 + 1][k3 - 1] = l4;
			}
			if (j3 > 0 && k3 < byte1 - 1 && anIntArrayArray901[j3 - 1][k3 + 1] == 0
				&& (ai[j3 - 1][k3 + 1] & 0x1280138) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0
				&& (ai[j3][k3 + 1] & 0x1280120) == 0)
			{
				bigX[l3] = j3 - 1;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 - 1][k3 + 1] = 6;
				anIntArrayArray825[j3 - 1][k3 + 1] = l4;
			}
			if (j3 < byte0 - 1 && k3 < byte1 - 1 && anIntArrayArray901[j3 + 1][k3 + 1] == 0
				&& (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
				&& (ai[j3][k3 + 1] & 0x1280120) == 0)
			{
				bigX[l3] = j3 + 1;
				bigY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				anIntArrayArray901[j3 + 1][k3 + 1] = 12;
				anIntArrayArray825[j3 + 1][k3 + 1] = l4;
			}
		}
		anInt1264 = 0;
		if (!flag1)
		{
			if (flag)
			{
				int i5 = 100;
				for (int k5 = 1; k5 < 2; k5++)
				{
					for (int i6 = k2 - k5; i6 <= k2 + k5; i6++)
					{
						for (int l6 = i2 - k5; l6 <= i2 + k5; l6++)
						{
							if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104 && anIntArrayArray825[i6][l6] < i5)
							{
								i5 = anIntArrayArray825[i6][l6];
								j3 = i6;
								k3 = l6;
								anInt1264 = 1;
								flag1 = true;
							}
						}
					}
					if (flag1)
						break;
				}
			}
			if (!flag1)
				return false;
		}
		i4 = 0;
		bigX[i4] = j3;
		bigY[i4++] = k3;
		int l5;
		for (int j5 = l5 = anIntArrayArray901[j3][k3]; j3 != j2 || k3 != j1; j5 = anIntArrayArray901[j3][k3])
		{
			if (j5 != l5)
			{
				l5 = j5;
				bigX[i4] = j3;
				bigY[i4++] = k3;
			}
			if ((j5 & 2) != 0)
				j3++;
			else if ((j5 & 8) != 0)
				j3--;
			if ((j5 & 1) != 0)
				k3++;
			else if ((j5 & 4) != 0)
				k3--;
		}
		if (i4 > 0)
		{
			int k4 = i4;
			if (k4 > 25)
				k4 = 25;
			i4--;
			int k6 = bigX[i4];
			int i7 = bigY[i4];
			anInt1288 += k4;
			if (anInt1288 >= 92)
			{
				stream.createFrame(36);
				stream.writeDWord(0);
				anInt1288 = 0;
			}
			if (i == 0)
			{
				stream.createFrame(164);
				stream.writeWordBigEndian(k4 + k4 + 3);
			}
			if (i == 1)
			{
				stream.createFrame(248);
				stream.writeWordBigEndian(k4 + k4 + 3 + 14);
			}
			if (i == 2)
			{
				stream.createFrame(98);
				stream.writeWordBigEndian(k4 + k4 + 3);
			}
			stream.method433(k6 + baseX);
			destX = bigX[0];
			destY = bigY[0];
			for (int j7 = 1; j7 < k4; j7++)
			{
				i4--;
				stream.writeWordBigEndian(bigX[i4] - k6);
				stream.writeWordBigEndian(bigY[i4] - i7);
			}
			stream.method431(i7 + baseY);
			stream.method424(keyArray[5] != 1 ? 0 : 1);
			return true;
		}
		return i != 1;
	}
}
