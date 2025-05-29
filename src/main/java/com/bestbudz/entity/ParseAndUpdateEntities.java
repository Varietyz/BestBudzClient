package com.bestbudz.entity;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.network.Stream;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class ParseAndUpdateEntities extends Client
{
	public static void renderNPCs(boolean flag)
	{
		for (int j = 0; j < npcCount; j++)
		{
			Npc npc = npcArray[npcIndices[j]];
			int k = 0x20000000 + (npcIndices[j] << 14);
			if (npc == null || !npc.isVisible() || npc.desc.aBoolean93 != flag)
				continue;
			int l = npc.x >> 7;
			int i1 = npc.y >> 7;
			if (l < 0 || l >= 208 || i1 < 0 || i1 >= 208)
				continue;
			if (npc.anInt1540 == 1 && (npc.x & 0x7f) == 64 && (npc.y & 0x7f) == 64)
			{
				if (anIntArrayArray929[l][i1] == anInt1265)
					continue;
				anIntArrayArray929[l][i1] = anInt1265;
			}
			if (!npc.desc.aBoolean84)
				k += 0x80000000;
			worldController.method285(plane, npc.anInt1552, getTerrainHeight(plane, npc.y, npc.x), k, npc.y,
				(npc.anInt1540 - 1) * 64 + 60, npc.x, npc, npc.aBoolean1541);
		}
	}

	public static void updateNPCs(Stream stream, int i)
	{
		anInt839 = 0;
		anInt893 = 0;
		parseServerUpdate(stream);
		parseNewNPCs(i, stream);
		parseNpcUpdateMasks(stream);
		for (int k = 0; k < anInt839; k++)
		{
			int l = anIntArray840[k];
			if (npcArray[l].anInt1537 != loopCycle)
			{
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		if (stream.currentOffset != i)
		{
			Signlink.reporterror(
				myUsername + " size mismatch in getnpcpos - pos:" + stream.currentOffset + " psize:" + i);
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
		while (stream.bitPosition + 21 < i * 8)
		{
			int k = stream.readBits(14);
			if (k == 16383)
				break;
			if (npcArray[k] == null)
				npcArray[k] = new Npc();
			Npc npc = npcArray[k];
			npcIndices[npcCount++] = k;
			npc.anInt1537 = loopCycle;
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
				anIntArray894[anInt893++] = k;
			npc.anInt1540 = npc.desc.aByte68;
			npc.anInt1504 = npc.desc.anInt79;
			npc.anInt1554 = npc.desc.walkAnim;
			npc.anInt1555 = npc.desc.anInt58;
			npc.anInt1556 = npc.desc.anInt83;
			npc.anInt1557 = npc.desc.anInt55;
			npc.anInt1511 = npc.desc.standAnim;
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
			stoner.aBoolean1699 = (lowMem && stonerCount > 50 || stonerCount > 200) && !flag
				&& stoner.anInt1517 == stoner.anInt1511;
			int j1 = stoner.x >> 7;
			int k1 = stoner.y >> 7;
			if (j1 < 0 || j1 >= 204 || k1 < 0 || k1 >= 104)
			{
				continue;
			}
			if (stoner.aModel_1714 != null && loopCycle >= stoner.spotAnimStartCycle && loopCycle < stoner.spotAnimEndCycle)
			{
				stoner.aBoolean1699 = false;
				stoner.spotAnimY = getTerrainHeight(plane, stoner.y, stoner.x);
				worldController.method286(plane, stoner.y, stoner, stoner.anInt1552, stoner.anInt1722, stoner.x,
					stoner.spotAnimY, stoner.anInt1719, stoner.anInt1721, i1, stoner.anInt1720);
				continue;
			}
			if ((stoner.x & 0x7f) == 64 && (stoner.y & 0x7f) == 64)
			{
				if (anIntArrayArray929[j1][k1] == anInt1265)
				{
					continue;
				}
				anIntArrayArray929[j1][k1] = anInt1265;
			}
			stoner.spotAnimY = getTerrainHeight(plane, stoner.y, stoner.x);
			worldController.method285(plane, stoner.anInt1552, stoner.spotAnimY, i1, stoner.y, 60, stoner.x, stoner,
				stoner.aBoolean1541);
		}
	}
}
