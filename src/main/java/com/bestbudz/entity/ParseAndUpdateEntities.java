package com.bestbudz.entity;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.engine.core.gamerender.WorldController;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class ParseAndUpdateEntities extends Client
{
	public static void renderNPCs(boolean flag)
	{
		for (int j = 0; j < npcCount; j++)
		{
			Npc npc = npcArray[npcIndices[j]];
			int k = 0x20000000 + (npcIndices[j] << 14);
			if (npc == null || !npc.isVisible() || npc.desc.renderPriority != flag)
				continue;
			int l = npc.x >> 7;
			int i1 = npc.y >> 7;
			if (l < 0 || l >= 208 || i1 < 0 || i1 >= 208)
				continue;
			if (npc.size == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64)
			{
				if (renderGrid[l][i1] == currentTick)
					continue;
				renderGrid[l][i1] = currentTick;
			}
			if (!npc.desc.clickable)
				k += 0x80000000;
			worldController.addLargeObject(plane, npc.orientation, getTerrainHeight(plane, npc.y, npc.x), k, npc.y,
				(npc.size - 1) * 64 + 60, npc.x, npc, npc.animChanged);
		}
	}

	public static void updateNPCs(Stream stream, int i)
	{
		removedNpcCount = 0;
		updatedNpcCount = 0;
		parseServerUpdate(stream);
		parseNewNPCs(i, stream);
		parseNpcUpdateMasks(stream);
		for (int k = 0; k < removedNpcCount; k++)
		{
			int l = removedNpcIndices[k];
			if (npcArray[l].lastUpdateCycle != loopCycle)
			{
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		if (stream.position != i)
		{
			Signlink.reporterror(
				myUsername + " size mismatch in getnpcpos - pos:" + stream.position + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < npcCount; i1++)
			if (npcArray[npcIndices[i1]] == null)
			{
				Signlink.reporterror(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
				throw new RuntimeException("eek");
			}

	}

	public static void parseNewNPCs(int i, Stream stream)
	{
		while (stream.bitOffset + 21 < i * 8)
		{
			int k = stream.readBits(14);
			if (k == 16383)
				break;
			if (npcArray[k] == null)
				npcArray[k] = new Npc();
			Npc npc = npcArray[k];
			npcIndices[npcCount++] = k;
			npc.lastUpdateCycle = loopCycle;
			int l = stream.readBits(5);
			if (l > 15)
				l -= 32;
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(1);
			npc.desc = EntityDef.forID(stream.readBits(npcBits));
			int k1 = stream.readBits(1);
			if (k1 == 1)
				updatedNpcIndices[updatedNpcCount++] = k;
			npc.size = npc.desc.size;
			npc.mapIconScale = npc.desc.mapIconScale;
			npc.walkAnimation = npc.desc.walkAnim;
			npc.turnRightAnimation = npc.desc.turnRightAnim;
			npc.turnAroundAnimation = npc.desc.turnAroundAnim;
			npc.turnLeftAnimation = npc.desc.turnLeftAnim;
			npc.standAnimation = npc.desc.standAnim;
			npc.setPos(myStoner.smallX[0] + i1, myStoner.smallY[0] + l, j1 == 1);
		}
		stream.finishBitAccess();
	}

	public static void parseStoners(boolean flag)
	{
		if (myStoner.x >> 7 == destX && myStoner.y >> 7 == destY)
		{
			destX = 0;
		}
		int j = stonerCount;
		if (flag)
		{
			j = 1;
		}
		for (int l = 0; l < j; l++)
		{
			Stoner stoner;
			int i1;
			if (flag)
			{
				stoner = myStoner;
				i1 = myStonerIndex << 14;
			}
			else
			{
				stoner = stonerArray[stonerIndices[l]];
				i1 = stonerIndices[l] << 14;
			}
			if (stoner == null || !stoner.isVisible())
			{
				continue;
			}
			stoner.lowDetailMode = (lowMem && stonerCount > 50 || stonerCount > 200) && !flag
				&& stoner.currentAnimation == stoner.standAnimation;
			int j1 = stoner.x >> 7;
			int k1 = stoner.y >> 7;
			if (j1 < 0 || j1 >= 204 || k1 < 0 || k1 >= 104)
			{
				continue;
			}
			if (stoner.spotAnimationModel != null && loopCycle >= stoner.spotAnimStartCycle && loopCycle < stoner.spotAnimEndCycle)
			{
				stoner.lowDetailMode = false;
				stoner.spotAnimHeight = getTerrainHeight(plane, stoner.y, stoner.x);
				worldController.addMultiTileObject(plane, stoner.y, stoner, stoner.orientation, stoner.spotAnimMaxY, stoner.x,
					stoner.spotAnimHeight, stoner.spotAnimMinX, stoner.spotAnimMaxX, i1, stoner.spotAnimMinY);
				continue;
			}
			if ((stoner.x & 0x7f) == 64 && (stoner.y & 0x7f) == 64)
			{
				if (renderGrid[j1][k1] == currentTick)
				{
					continue;
				}
				renderGrid[j1][k1] = currentTick;
			}
			stoner.spotAnimHeight = getTerrainHeight(plane, stoner.y, stoner.x);
			worldController.addLargeObject(plane, stoner.orientation, stoner.spotAnimHeight, i1, stoner.y, 60, stoner.x, stoner,
				stoner.animChanged);
		}
	}

	public static void parseNpcUpdateMasks(Stream stream)
	{
		for (int j = 0; j < updatedNpcCount; j++)
		{
			int k = updatedNpcIndices[j];
			Npc npc = npcArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x10) != 0)
			{
				int i1 = stream.readWordLittleEndian();
				if (i1 == 65535)
					i1 = -1;
				int i2 = stream.readUnsignedByte();
				if (i1 == npc.anim && i1 != -1)
				{
					int l2 = Animation.anims[i1].anInt365;
					if (l2 == 1)
					{
						npc.animFrameIndex = 0;
						npc.animFrameTimer = 0;
						npc.animDelay = i2;
						npc.animLoopCount = 0;
					}
					if (l2 == 2)
						npc.animLoopCount = 0;
				}
				else if (i1 == -1 || npc.anim == -1
					|| Animation.anims[i1].anInt359 >= Animation.anims[npc.anim].anInt359)
				{
					npc.anim = i1;
					npc.animFrameIndex = 0;
					npc.animFrameTimer = 0;
					npc.animDelay = i2;
					npc.animLoopCount = 0;
					npc.movementDelay = npc.smallXYIndex;
				}
			}
			if ((l & 8) != 0)
			{
				int j1 = stream.readByteSubtract128();
				int j2 = stream.readByteNegated();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(j2, j1, loopCycle, icon);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.readQWord();
				npc.maxHealth = stream.readQWord();
			}
			if ((l & 0x80) != 0)
			{
				npc.spotAnimId = stream.readUnsignedWord();
				int k1 = stream.readDWord();
				npc.anInt1524 = k1 >> 16;
				npc.spotAnimStart = loopCycle + (k1 & 0xffff);
				npc.spotAnimFrame = 0;
				npc.spotAnimTimer = 0;
				if (npc.spotAnimStart > loopCycle)
					npc.spotAnimFrame = -1;
				if (npc.spotAnimId == 65535)
					npc.spotAnimId = -1;
			}
			if ((l & 0x20) != 0)
			{
				npc.interactingEntity = stream.readUnsignedWord();
				if (npc.interactingEntity == 65535)
					npc.interactingEntity = -1;
			}
			if ((l & 1) != 0)
			{
				npc.textSpoken = stream.readString();
				npc.textCycle = 100;
			}
			if ((l & 0x40) != 0)
			{
				int l1 = stream.readByteNegated();
				int k2 = stream.readByte128Minus();
				int icon = stream.readUnsignedByte();
				npc.updateHitData(k2, l1, loopCycle, icon);
				npc.loopCycleStatus = loopCycle + 300;
				npc.currentHealth = stream.readQWord();
				npc.maxHealth = stream.readQWord();
			}
			if ((l & 2) != 0)
			{
				npc.desc = EntityDef.forID(stream.readWordMixedLE());
				npc.size = npc.desc.size;
				npc.mapIconScale = npc.desc.mapIconScale;
				npc.walkAnimation = npc.desc.walkAnim;
				npc.turnRightAnimation = npc.desc.turnRightAnim;
				npc.turnAroundAnimation = npc.desc.turnAroundAnim;
				npc.turnLeftAnimation = npc.desc.turnLeftAnim;
				npc.standAnimation = npc.desc.standAnim;
			}
			if ((l & 4) != 0)
			{
				npc.anInt1538 = stream.readWordLittleEndian();
				npc.anInt1539 = stream.readWordLittleEndian();
			}
		}
	}

	public static void npcScreenPos(Entity entity, int i)
	{
		calcEntityScreenPos(entity.x, i, entity.y);
	}

	public static void calcEntityScreenPos(int i, int j, int l)
	{
		if (i < 128 || l < 128 || i > 13056 || l > 13056)
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}

		int i1 = getTerrainHeight(plane, l, i) - j;
		i -= xCameraPos;
		i1 -= zCameraPos;
		l -= yCameraPos;

		int j1 = Model.modelIntArray1[yCameraCurve];
		int k1 = Model.modelIntArray2[yCameraCurve];
		int l1 = Model.modelIntArray1[xCameraCurve];
		int i2 = Model.modelIntArray2[xCameraCurve];
		int j2 = l * l1 + i * i2 >> 16;
		l = l * i2 - i * l1 >> 16;
		i = j2;
		j2 = i1 * k1 - l * j1 >> 16;
		l = i1 * j1 + l * k1 >> 16;
		i1 = j2;

		// ONLY prevent mirroring, allow off-screen extension
		if (l <= 50) // Behind camera
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}

		// Calculate screen position
		int screenX = Rasterizer.viewportCenterX + (i << WorldController.viewDistance) / l;
		int screenY = Rasterizer.viewportCenterY + (i1 << WorldController.viewDistance) / l;

		// Only check for extreme mirroring (not normal off-screen)
		int screenWidth = Rasterizer.viewportCenterX * 2;
		int screenHeight = Rasterizer.viewportCenterY * 2;

		// Detect obvious mirroring (coordinates way beyond reasonable bounds)
		if (Math.abs(screenX) > screenWidth * 5 || Math.abs(screenY) > screenHeight * 5)
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}

		// Allow any reasonable coordinate, even if off-screen
		spriteDrawX = screenX;
		spriteDrawY = screenY;
	}

	public static void parseServerUpdate(Stream stream)
	{
		stream.initBitAccess();
		int k = stream.readBits(8);
		if (k < npcCount)
		{
			for (int l = k; l < npcCount; l++)
				removedNpcIndices[removedNpcCount++] = npcIndices[l];

		}
		if (k > npcCount)
		{
			Signlink.reporterror(myUsername + " Too many npcs");
			throw new RuntimeException("eek");
		}
		npcCount = 0;
		for (int i1 = 0; i1 < k; i1++)
		{
			int j1 = npcIndices[i1];
			Npc npc = npcArray[j1];
			int k1 = stream.readBits(1);
			if (k1 == 0)
			{
				npcIndices[npcCount++] = j1;
				npc.lastUpdateCycle = loopCycle;
			}
			else
			{
				int l1 = stream.readBits(2);
				if (l1 == 0)
				{
					npcIndices[npcCount++] = j1;
					npc.lastUpdateCycle = loopCycle;
					updatedNpcIndices[updatedNpcCount++] = j1;
				}
				else if (l1 == 1)
				{
					npcIndices[npcCount++] = j1;
					npc.lastUpdateCycle = loopCycle;
					int i2 = stream.readBits(3);
					npc.moveInDir(false, i2);
					int k2 = stream.readBits(1);
					if (k2 == 1)
						updatedNpcIndices[updatedNpcCount++] = j1;
				}
				else if (l1 == 2)
				{
					npcIndices[npcCount++] = j1;
					npc.lastUpdateCycle = loopCycle;
					int j2 = stream.readBits(3);
					npc.moveInDir(true, j2);
					int l2 = stream.readBits(3);
					npc.moveInDir(true, l2);
					int i3 = stream.readBits(1);
					if (i3 == 1)
						updatedNpcIndices[updatedNpcCount++] = j1;
				}
				else if (l1 == 3)
					removedNpcIndices[removedNpcCount++] = j1;
			}
		}
	}

}
