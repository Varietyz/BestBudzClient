package com.bestbudz.entity;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.ui.TextInput;
import static com.bestbudz.ui.interfaces.Chatbox.pushMessage;
import com.bestbudz.util.TextClass;

public class UpdateStoners extends Client
{
	public static void updateStoners(int i, Stream stream)
	{
		anInt839 = 0;
		anInt893 = 0;
		parseLocalStonerUpdate(stream);
		updateLocalStonerList(stream);
		parseProjectiles(stream, i);
		parseStonerUpdateMasks(stream);
		for (int k = 0; k < anInt839; k++)
		{
			int l = anIntArray840[k];
			if (stonerArray[l].anInt1537 != loopCycle)
				stonerArray[l] = null;
		}

		if (stream.currentOffset != i)
		{
			Signlink.reporterror("Error packet size mismatch in getstoner pos:" + stream.currentOffset + " psize:" + i);
			throw new RuntimeException("eek");
		}
		for (int i1 = 0; i1 < stonerCount; i1++)
			if (stonerArray[stonerIndices[i1]] == null)
			{
				Signlink.reporterror(myUsername + " null entry in pl list - pos:" + i1 + " size:" + stonerCount);
				throw new RuntimeException("eek");
			}

	}

	public static void parseLocalStonerUpdate(Stream stream)
	{
		stream.initBitAccess();
		int j = stream.readBits(1);
		if (j == 0)
			return;
		int k = stream.readBits(2);
		if (k == 0)
		{
			anIntArray894[anInt893++] = myStonerIndex;
			return;
		}
		if (k == 1)
		{
			int l = stream.readBits(3);
			myStoner.moveInDir(false, l);
			int k1 = stream.readBits(1);
			if (k1 == 1)
				anIntArray894[anInt893++] = myStonerIndex;
			return;
		}
		if (k == 2)
		{
			int i1 = stream.readBits(3);
			myStoner.moveInDir(true, i1);
			int l1 = stream.readBits(3);
			myStoner.moveInDir(true, l1);
			int j2 = stream.readBits(1);
			if (j2 == 1)
				anIntArray894[anInt893++] = myStonerIndex;
			return;
		}
		if (k == 3)
		{
			plane = stream.readBits(2);
			int j1 = stream.readBits(1);
			int i2 = stream.readBits(1);
			if (i2 == 1)
				anIntArray894[anInt893++] = myStonerIndex;
			int k2 = stream.readBits(7);
			int l2 = stream.readBits(7);
			myStoner.setPos(l2, k2, j1 == 1);
		}
	}

	public static void updateLocalStonerList(Stream stream)
	{
		int j = stream.readBits(8);
		if (j < stonerCount)
		{
			for (int k = j; k < stonerCount; k++)
				anIntArray840[anInt839++] = stonerIndices[k];

		}
		if (j > stonerCount)
		{
			Signlink.reporterror(myUsername + " Too many stoners");
			throw new RuntimeException("eek");
		}
		stonerCount = 0;
		for (int l = 0; l < j; l++)
		{
			int i1 = stonerIndices[l];
			Stoner stoner = stonerArray[i1];
			int j1 = stream.readBits(1);
			if (j1 == 0)
			{
				stonerIndices[stonerCount++] = i1;
				stoner.anInt1537 = loopCycle;
			}
			else
			{
				int k1 = stream.readBits(2);
				if (k1 == 0)
				{
					stonerIndices[stonerCount++] = i1;
					stoner.anInt1537 = loopCycle;
					anIntArray894[anInt893++] = i1;
				}
				else if (k1 == 1)
				{
					stonerIndices[stonerCount++] = i1;
					stoner.anInt1537 = loopCycle;
					int l1 = stream.readBits(3);
					stoner.moveInDir(false, l1);
					int j2 = stream.readBits(1);
					if (j2 == 1)
						anIntArray894[anInt893++] = i1;
				}
				else if (k1 == 2)
				{
					stonerIndices[stonerCount++] = i1;
					stoner.anInt1537 = loopCycle;
					int i2 = stream.readBits(3);
					stoner.moveInDir(true, i2);
					int k2 = stream.readBits(3);
					stoner.moveInDir(true, k2);
					int l2 = stream.readBits(1);
					if (l2 == 1)
						anIntArray894[anInt893++] = i1;
				}
				else if (k1 == 3)
					anIntArray840[anInt839++] = i1;
			}
		}
	}

	public static void parseStonerUpdateMasks(Stream stream)
	{
		for (int j = 0; j < anInt893; j++)
		{
			int k = anIntArray894[j];
			Stoner stoner = stonerArray[k];
			int l = stream.readUnsignedByte();
			if ((l & 0x40) != 0)
				l += stream.readUnsignedByte() << 8;
			appendStonerUpdateMask(l, k, stream, stoner);
		}
	}

	private static void appendStonerUpdateMask(int i, int j, Stream stream, Stoner stoner)
	{
		if ((i & 0x400) != 0)
		{
			stoner.anInt1543 = stream.method428();
			stoner.anInt1545 = stream.method428();
			stoner.anInt1544 = stream.method428();
			stoner.anInt1546 = stream.method428();
			stoner.anInt1547 = stream.method436() + loopCycle;
			stoner.anInt1548 = stream.method435() + loopCycle;
			stoner.anInt1549 = stream.method428();
			stoner.method446();
		}
		if ((i & 0x100) != 0)
		{
			stoner.anInt1520 = stream.method434();
			int k = stream.readDWord();
			stoner.anInt1524 = k >> 16;
			stoner.anInt1523 = loopCycle + (k & 0xffff);
			stoner.anInt1521 = 0;
			stoner.anInt1522 = 0;
			if (stoner.anInt1523 > loopCycle)
				stoner.anInt1521 = -1;
			if (stoner.anInt1520 == 65535)
				stoner.anInt1520 = -1;
		}
		if ((i & 8) != 0)
		{
			int l = stream.method434();
			if (l == 65535)
				l = -1;
			int i2 = stream.method427();
			if (l == stoner.anim && l != -1)
			{
				int i3 = Animation.anims[l].anInt365;
				if (i3 == 1)
				{
					stoner.anInt1527 = 0;
					stoner.anInt1528 = 0;
					stoner.anInt1529 = i2;
					stoner.anInt1530 = 0;
				}
				if (i3 == 2)
					stoner.anInt1530 = 0;
			}
			else if (l == -1 || stoner.anim == -1
				|| Animation.anims[l].anInt359 >= Animation.anims[stoner.anim].anInt359)
			{
				stoner.anim = l;
				stoner.anInt1527 = 0;
				stoner.anInt1528 = 0;
				stoner.anInt1529 = i2;
				stoner.anInt1530 = 0;
				stoner.anInt1542 = stoner.smallXYIndex;
			}
		}
		if ((i & 4) != 0)
		{
			stoner.textSpoken = stream.readString();
			if (stoner.textSpoken.charAt(0) == '~')
			{
				stoner.textSpoken = stoner.textSpoken.substring(1);
				pushMessage(stoner.textSpoken, 2, stoner.name, stoner.title, stoner.titleColor);
			}
			else if (stoner == myStoner)
				pushMessage(stoner.textSpoken, 2, stoner.name, stoner.title, stoner.titleColor);
			stoner.anInt1513 = 0;
			stoner.anInt1531 = 0;
			stoner.textCycle = 150;
		}
		if ((i & 0x80) != 0)
		{
			int i1 = stream.method434();
			int j2 = stream.readUnsignedByte();
			int j3 = stream.method427();
			int k3 = stream.currentOffset;
			if (stoner.name != null && stoner.visible)
			{
				long l3 = TextClass.longForName(stoner.name);
				boolean flag = false;
				if (j2 <= 1)
				{
					for (int i4 = 0; i4 < ignoreCount; i4++)
					{
						if (ignoreListAsLongs[i4] != l3)
							continue;
						flag = true;
						break;
					}

				}
				if (!flag && anInt1251 == 0)
					try
					{
						aStream_834.currentOffset = 0;
						stream.method442(j3, 0, aStream_834.buffer);
						aStream_834.currentOffset = 0;
						String s = TextInput.method525(j3, aStream_834);
						stoner.textSpoken = s;
						stoner.anInt1513 = i1 >> 8;
						stoner.privelage = j2;
						stoner.anInt1531 = i1 & 0xff;
						stoner.textCycle = 150;
						if (j2 >= 1)
							pushMessage(s, 1, "@cr" + j2 + "@" + stoner.name, stoner.title, stoner.titleColor);
						else
							pushMessage(s, 2, stoner.name, stoner.title, stoner.titleColor);
					}
					catch (Exception exception)
					{
						Signlink.reporterror("cde2");
						exception.printStackTrace();
					}
			}
			stream.currentOffset = k3 + j3;
		}
		if ((i & 1) != 0)
		{
			stoner.interactingEntity = stream.method434();
			if (stoner.interactingEntity == 65535)
				stoner.interactingEntity = -1;
		}
		if ((i & 0x10) != 0)
		{
			int j1 = stream.method427();
			byte[] abyte0 = new byte[j1];
			Stream stream_1 = new Stream(abyte0);
			stream.readBytes(j1, 0, abyte0);
			aStreamArray895s[j] = stream_1;
			stoner.updateStoner(stream_1);
		}
		if ((i & 2) != 0)
		{
			stoner.anInt1538 = stream.method436();
			stoner.anInt1539 = stream.method434();
		}
		if ((i & 0x20) != 0)
		{
			int k1 = stream.readUnsignedByte();
			int k2 = stream.method426();
			int icon = stream.readUnsignedByte();
			stoner.updateHitData(k2, k1, loopCycle, icon);
			stoner.loopCycleStatus = loopCycle + 300;
			stoner.currentHealth = stream.method427();
			stoner.maxHealth = stream.readUnsignedByte();
		}
		if ((i & 0x200) != 0)
		{
			int l1 = stream.readUnsignedByte();
			int l2 = stream.method428();
			int icon = stream.readUnsignedByte();
			stoner.updateHitData(l2, l1, loopCycle, icon);
			stoner.loopCycleStatus = loopCycle + 300;
			stoner.currentHealth = stream.readUnsignedByte();
			stoner.maxHealth = stream.method427();
		}
	}
}
