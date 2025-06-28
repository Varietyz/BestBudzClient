package com.bestbudz.entity;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.graphics.text.TextInput;
import static com.bestbudz.ui.interfaces.Chatbox.pushMessage;
import com.bestbudz.graphics.text.TextClass;

public class UpdateStoners extends Client
{
	public static void updateStoners(int i, Stream stream)
	{
		removedNpcCount = 0;
		updatedNpcCount = 0;
		parseLocalStonerUpdate(stream);
		updateLocalStonerList(stream);
		parseProjectiles(stream, i);
		parseStonerUpdateMasks(stream);
		for (int k = 0; k < removedNpcCount; k++)
		{
			int l = removedNpcIndices[k];
			if (stonerArray[l].lastUpdateCycle != loopCycle)
				stonerArray[l] = null;
		}

		if (stream.position != i)
		{
			Signlink.reporterror("Error packet size mismatch in getstoner pos:" + stream.position + " psize:" + i);
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
			updatedNpcIndices[updatedNpcCount++] = myStonerIndex;
			return;
		}
		if (k == 1)
		{
			int l = stream.readBits(3);
			myStoner.moveInDir(false, l);
			int k1 = stream.readBits(1);
			if (k1 == 1)
				updatedNpcIndices[updatedNpcCount++] = myStonerIndex;
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
				updatedNpcIndices[updatedNpcCount++] = myStonerIndex;
			return;
		}
		if (k == 3)
		{
			plane = stream.readBits(2);
			int j1 = stream.readBits(1);
			int i2 = stream.readBits(1);
			if (i2 == 1)
				updatedNpcIndices[updatedNpcCount++] = myStonerIndex;
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
				removedNpcIndices[removedNpcCount++] = stonerIndices[k];

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
				stoner.lastUpdateCycle = loopCycle;
			}
			else
			{
				int k1 = stream.readBits(2);
				if (k1 == 0)
				{
					stonerIndices[stonerCount++] = i1;
					stoner.lastUpdateCycle = loopCycle;
					updatedNpcIndices[updatedNpcCount++] = i1;
				}
				else if (k1 == 1)
				{
					stonerIndices[stonerCount++] = i1;
					stoner.lastUpdateCycle = loopCycle;
					int l1 = stream.readBits(3);
					stoner.moveInDir(false, l1);
					int j2 = stream.readBits(1);
					if (j2 == 1)
						updatedNpcIndices[updatedNpcCount++] = i1;
				}
				else if (k1 == 2)
				{
					stonerIndices[stonerCount++] = i1;
					stoner.lastUpdateCycle = loopCycle;
					int i2 = stream.readBits(3);
					stoner.moveInDir(true, i2);
					int k2 = stream.readBits(3);
					stoner.moveInDir(true, k2);
					int l2 = stream.readBits(1);
					if (l2 == 1)
						updatedNpcIndices[updatedNpcCount++] = i1;
				}
				else if (k1 == 3)
					removedNpcIndices[removedNpcCount++] = i1;
			}
		}
	}

	public static void parseStonerUpdateMasks(Stream stream)
	{
		for (int j = 0; j < updatedNpcCount; j++)
		{
			int k = updatedNpcIndices[j];
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
			stoner.anInt1543 = stream.readByte128Minus();
			stoner.anInt1545 = stream.readByte128Minus();
			stoner.anInt1544 = stream.readByte128Minus();
			stoner.anInt1546 = stream.readByte128Minus();
			stoner.animStart = stream.readWordMixedLE() + loopCycle;
			stoner.animEnd = stream.readWordMixed() + loopCycle;
			stoner.anInt1549 = stream.readByte128Minus();
			stoner.method446();
		}
		if ((i & 0x100) != 0)
		{
			stoner.spotAnimId = stream.readWordLittleEndian();
			int k = stream.readDWord();
			stoner.anInt1524 = k >> 16;
			stoner.spotAnimStart = loopCycle + (k & 0xffff);
			stoner.spotAnimFrame = 0;
			stoner.spotAnimTimer = 0;
			if (stoner.spotAnimStart > loopCycle)
				stoner.spotAnimFrame = -1;
			if (stoner.spotAnimId == 65535)
				stoner.spotAnimId = -1;
		}
		if ((i & 8) != 0)
		{
			int l = stream.readWordLittleEndian();
			if (l == 65535)
				l = -1;
			int i2 = stream.readByteNegated();
			if (l == stoner.anim && l != -1)
			{
				int i3 = Animation.anims[l].anInt365;
				if (i3 == 1)
				{
					stoner.animFrameIndex = 0;
					stoner.animFrameTimer = 0;
					stoner.animDelay = i2;
					stoner.animLoopCount = 0;
				}
				if (i3 == 2)
					stoner.animLoopCount = 0;
			}
			else if (l == -1 || stoner.anim == -1
				|| Animation.anims[l].anInt359 >= Animation.anims[stoner.anim].anInt359)
			{
				stoner.anim = l;
				stoner.animFrameIndex = 0;
				stoner.animFrameTimer = 0;
				stoner.animDelay = i2;
				stoner.animLoopCount = 0;
				stoner.movementDelay = stoner.smallXYIndex;
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
			stoner.chatType = 0;
			stoner.chatEffect = 0;
			stoner.textCycle = 150;
		}
		if ((i & 0x80) != 0)
		{
			int i1 = stream.readWordLittleEndian();
			int j2 = stream.readUnsignedByte();
			int j3 = stream.readByteNegated();
			int k3 = stream.position;
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
				if (!flag && publicChatFilter == 0)
					try
					{
						incomingPacketBuffer.position = 0;
						stream.readBytesReversed(j3, 0, incomingPacketBuffer.buffer);
						incomingPacketBuffer.position = 0;
						String s = TextInput.method525(j3, incomingPacketBuffer);
						stoner.textSpoken = s;
						stoner.chatType = i1 >> 8;
						stoner.privelage = j2;
						stoner.chatEffect = i1 & 0xff;
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
			stream.position = k3 + j3;
		}
		if ((i & 1) != 0)
		{
			stoner.interactingEntity = stream.readWordLittleEndian();
			if (stoner.interactingEntity == 65535)
				stoner.interactingEntity = -1;
		}
		if ((i & 0x10) != 0)
		{
			int j1 = stream.readByteNegated();
			byte[] abyte0 = new byte[j1];
			Stream stream_1 = new Stream(abyte0);
			stream.readBytes(j1, 0, abyte0);
			playerUpdateBuffers[j] = stream_1;
			stoner.updateStoner(stream_1);
		}
		if ((i & 2) != 0)
		{
			stoner.anInt1538 = stream.readWordMixedLE();
			stoner.anInt1539 = stream.readWordLittleEndian();
		}
		if ((i & 0x20) != 0)
		{
			int k1 = stream.readUnsignedByte();
			int k2 = stream.readByteSubtract128();
			int icon = stream.readUnsignedByte();
			stoner.updateHitData(k2, k1, loopCycle, icon);
			stoner.loopCycleStatus = loopCycle + 300;
			stoner.currentHealth = stream.readQWord();
			stoner.maxHealth = stream.readQWord();
		}
		if ((i & 0x200) != 0)
		{
			int l1 = stream.readUnsignedByte();
			int l2 = stream.readByte128Minus();
			int icon = stream.readUnsignedByte();
			stoner.updateHitData(l2, l1, loopCycle, icon);
			stoner.loopCycleStatus = loopCycle + 300;
			stoner.currentHealth = stream.readQWord();
			stoner.maxHealth = stream.readQWord();
		}
	}

	public static void parseProjectiles(Stream stream, int i)
	{
		while (stream.bitOffset + 10 < i * 8)
		{
			int j = stream.readBits(11);
			if (j == 2047)
				break;
			if (stonerArray[j] == null)
			{
				stonerArray[j] = new Stoner();
				if (playerUpdateBuffers[j] != null)
					stonerArray[j].updateStoner(playerUpdateBuffers[j]);
			}
			stonerIndices[stonerCount++] = j;
			Stoner stoner = stonerArray[j];
			stoner.lastUpdateCycle = loopCycle;
			int k = stream.readBits(1);
			if (k == 1)
				updatedNpcIndices[updatedNpcCount++] = j;
			int l = stream.readBits(1);
			int i1 = stream.readBits(5);
			if (i1 > 15)
				i1 -= 32;
			int j1 = stream.readBits(5);
			if (j1 > 15)
				j1 -= 32;
			stoner.setPos(myStoner.smallX[0] + j1, myStoner.smallY[0] + i1, l == 1);
		}
		stream.finishBitAccess();
	}
}
