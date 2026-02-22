package com.bestbudz.world;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.WorldController;
import static com.bestbudz.ui.handling.input.Keyboard.keyArray;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.PlayerProto.*;

public class WalkTo extends Client
{
	public static void handleWalkToObject() {
		if (WorldController.selectedTileX == -1) return;

		int x = WorldController.selectedTileX;
		int y = WorldController.selectedTileY;
		WorldController.selectedTileX = -1;

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

		// Generation counter: increment instead of clearing 21,632 array cells
		walkGeneration++;

		int j3 = j2;
		int k3 = j1;
		if (j2 < 0 || k3 < 0
			|| j2 >= walkDirection.length
			|| k3 >= walkDirection[j2].length) {
			return false;
		}

		k2 = Math.max(0, Math.min(103, k2));
		i2 = Math.max(0, Math.min(103, i2));

		walkDirection[j2][j1] = 99;
		walkDistance[j2][j1] = 0;
		walkVisitGen[j2][j1] = walkGeneration;
		int l3 = 0;
		int i4 = 0;
		walkQueueX[l3] = j2;
		walkQueueY[l3++] = j1;
		boolean flag1 = false;
		int j4 = walkQueueX.length;
		int[][] ai = collisionMaps[plane].collisionFlags;
		while (i4 != l3)
		{
			j3 = walkQueueX[i4];
			k3 = walkQueueY[i4];
			i4 = (i4 + 1) % j4;
			if (j3 == k2 && k3 == i2)
			{
				flag1 = true;
				break;
			}
			if (i1 != 0)
			{
				if ((i1 < 5 || i1 == 10) && collisionMaps[plane].canReachWall(k2, j3, k3, j, i1 - 1, i2))
				{
					flag1 = true;
					break;
				}
				if (i1 < 10 && collisionMaps[plane].canReachWallDecoration(k2, i2, k3, i1 - 1, j, j3))
				{
					flag1 = true;
					break;
				}
			}
			if (k1 != 0 && k != 0 && collisionMaps[plane].canReachObject(i2, k2, j3, k, l1, k1, k3))
			{
				flag1 = true;
				break;
			}
			int l4 = walkDistance[j3][k3] + 1;
			if (j3 > 0 && walkVisitGen[j3 - 1][k3] != walkGeneration && (ai[j3 - 1][k3] & 0x1280108) == 0)
			{
				walkQueueX[l3] = j3 - 1;
				walkQueueY[l3] = k3;
				l3 = (l3 + 1) % j4;
				walkDirection[j3 - 1][k3] = 2;
				walkDistance[j3 - 1][k3] = l4;
				walkVisitGen[j3 - 1][k3] = walkGeneration;
			}
			if (j3 < byte0 - 1 && walkVisitGen[j3 + 1][k3] != walkGeneration && (ai[j3 + 1][k3] & 0x1280180) == 0)
			{
				walkQueueX[l3] = j3 + 1;
				walkQueueY[l3] = k3;
				l3 = (l3 + 1) % j4;
				walkDirection[j3 + 1][k3] = 8;
				walkDistance[j3 + 1][k3] = l4;
				walkVisitGen[j3 + 1][k3] = walkGeneration;
			}
			if (k3 > 0 && walkVisitGen[j3][k3 - 1] != walkGeneration && (ai[j3][k3 - 1] & 0x1280102) == 0)
			{
				walkQueueX[l3] = j3;
				walkQueueY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				walkDirection[j3][k3 - 1] = 1;
				walkDistance[j3][k3 - 1] = l4;
				walkVisitGen[j3][k3 - 1] = walkGeneration;
			}
			if (k3 < byte1 - 1 && walkVisitGen[j3][k3 + 1] != walkGeneration && (ai[j3][k3 + 1] & 0x1280120) == 0)
			{
				walkQueueX[l3] = j3;
				walkQueueY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				walkDirection[j3][k3 + 1] = 4;
				walkDistance[j3][k3 + 1] = l4;
				walkVisitGen[j3][k3 + 1] = walkGeneration;
			}
			if (j3 > 0 && k3 > 0 && walkVisitGen[j3 - 1][k3 - 1] != walkGeneration && (ai[j3 - 1][k3 - 1] & 0x128010e) == 0
				&& (ai[j3 - 1][k3] & 0x1280108) == 0 && (ai[j3][k3 - 1] & 0x1280102) == 0)
			{
				walkQueueX[l3] = j3 - 1;
				walkQueueY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				walkDirection[j3 - 1][k3 - 1] = 3;
				walkDistance[j3 - 1][k3 - 1] = l4;
				walkVisitGen[j3 - 1][k3 - 1] = walkGeneration;
			}
			if (j3 < byte0 - 1 && k3 > 0 && walkVisitGen[j3 + 1][k3 - 1] != walkGeneration
				&& (ai[j3 + 1][k3 - 1] & 0x1280183) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
				&& (ai[j3][k3 - 1] & 0x1280102) == 0)
			{
				walkQueueX[l3] = j3 + 1;
				walkQueueY[l3] = k3 - 1;
				l3 = (l3 + 1) % j4;
				walkDirection[j3 + 1][k3 - 1] = 9;
				walkDistance[j3 + 1][k3 - 1] = l4;
				walkVisitGen[j3 + 1][k3 - 1] = walkGeneration;
			}
			if (j3 > 0 && k3 < byte1 - 1 && walkVisitGen[j3 - 1][k3 + 1] != walkGeneration
				&& (ai[j3 - 1][k3 + 1] & 0x1280138) == 0 && (ai[j3 - 1][k3] & 0x1280108) == 0
				&& (ai[j3][k3 + 1] & 0x1280120) == 0)
			{
				walkQueueX[l3] = j3 - 1;
				walkQueueY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				walkDirection[j3 - 1][k3 + 1] = 6;
				walkDistance[j3 - 1][k3 + 1] = l4;
				walkVisitGen[j3 - 1][k3 + 1] = walkGeneration;
			}
			if (j3 < byte0 - 1 && k3 < byte1 - 1 && walkVisitGen[j3 + 1][k3 + 1] != walkGeneration
				&& (ai[j3 + 1][k3 + 1] & 0x12801e0) == 0 && (ai[j3 + 1][k3] & 0x1280180) == 0
				&& (ai[j3][k3 + 1] & 0x1280120) == 0)
			{
				walkQueueX[l3] = j3 + 1;
				walkQueueY[l3] = k3 + 1;
				l3 = (l3 + 1) % j4;
				walkDirection[j3 + 1][k3 + 1] = 12;
				walkDistance[j3 + 1][k3 + 1] = l4;
				walkVisitGen[j3 + 1][k3 + 1] = walkGeneration;
			}
		}
		lastTickCount = 0;
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
							if (i6 >= 0 && l6 >= 0 && i6 < 104 && l6 < 104
								&& walkVisitGen[i6][l6] == walkGeneration && walkDistance[i6][l6] < i5)
							{
								i5 = walkDistance[i6][l6];
								j3 = i6;
								k3 = l6;
								lastTickCount = 1;
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
		walkQueueX[i4] = j3;
		walkQueueY[i4++] = k3;
		int l5;
		for (int j5 = l5 = walkDirection[j3][k3]; j3 != j2 || k3 != j1; j5 = walkDirection[j3][k3])
		{
			if (j5 != l5)
			{
				l5 = j5;
				walkQueueX[i4] = j3;
				walkQueueY[i4++] = k3;
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
			int k6 = walkQueueX[i4];
			int i7 = walkQueueY[i4];
			lastMouseY += k4;
			if (lastMouseY >= 92)
			{
				lastMouseY = 0;
			}
			destX = walkQueueX[0];
			destY = walkQueueY[0];
			MovementRequest.Builder moveBuilder = MovementRequest.newBuilder()
				.setDestX(k6 + baseX)
				.setDestY(i7 + baseY)
				.setForced(keyArray[5] == 1)
				.setOpcode(i == 0 ? 164 : i == 1 ? 248 : 98);
			for (int j7 = 1; j7 < k4; j7++)
			{
				i4--;
				moveBuilder.addPathX(walkQueueX[i4] - k6);
				moveBuilder.addPathY(walkQueueY[i4] - i7);
			}
			sendProto(GamePacket.newBuilder().setMovementRequest(moveBuilder).build());
			return true;
		}
		return i != 1;
	}
}
