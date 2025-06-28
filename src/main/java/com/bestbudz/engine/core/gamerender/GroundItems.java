package com.bestbudz.engine.core.gamerender;

import com.bestbudz.data.items.Item;
import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Stoner;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.GraphicEffect;
import com.bestbudz.rendering.InteractiveObject;
import com.bestbudz.rendering.Projectile;
import static com.bestbudz.rendering.AnimationManager.createSpotAnimation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.NodeList;
import static com.bestbudz.world.GroundItem.spawnGroundItem;
import com.bestbudz.world.Wall;
import com.bestbudz.world.WallDecoration;
import com.bestbudz.world.GroundDecoration;
import com.bestbudz.world.GameObject;
import com.bestbudz.world.ObjectDef;
import static com.bestbudz.world.TerrainHeight.getTerrainHeight;

public class GroundItems extends Client
{
	public static void handleGroundItemUpdate(Stream stream, int j)
	{
		if (j == 84)
		{
			int k = stream.readUnsignedByte();
			int j3 = localRegionX + (k >> 4 & 7);
			int i6 = localRegionY + (k & 7);
			int l8 = stream.readUnsignedWord();
			int k11 = stream.readUnsignedWord();
			int l13 = stream.readUnsignedWord();
			if (j3 >= 0 && i6 >= 0 && j3 < 104 && i6 < 104)
			{
				NodeList groundItemList = groundArray[plane][j3][i6];
				if (groundItemList != null)
				{
					for (Item groundItem = (Item) groundItemList
						.reverseGetFirst(); groundItem != null; groundItem = (Item) groundItemList
						.reverseGetNext())
					{
						if (groundItem.ID != (l8 & 0x7fff) || groundItem.stackSize != k11)
							continue;
						groundItem.stackSize = l13;
						break;
					}

					spawnGroundItem(j3, i6);
				}
			}
			return;
		}
		if (j == 105)
		{
			int l = stream.readUnsignedByte();
			int k3 = localRegionX + (l >> 4 & 7);
			int j6 = localRegionY + (l & 7);
			int i9 = stream.readUnsignedWord();
			int l11 = stream.readUnsignedByte();
			int i14 = l11 >> 4 & 0xf;
			int i16 = l11 & 7;
			if (myStoner.smallX[0] >= k3 - i14 && myStoner.smallX[0] <= k3 + i14 && myStoner.smallY[0] >= j6 - i14
				&& myStoner.smallY[0] <= j6 + i14 && soundProduction && !lowMem && soundEffectCount < 50)
			{
				soundIds[soundEffectCount] = i9;
				soundTypes[soundEffectCount] = i16;
				soundDelays[soundEffectCount] = i9;
				soundEffectCount++;
			}
		}
		if (j == 215)
		{
			int i1 = stream.readWordMixed();
			int l3 = stream.readByte128Minus();
			int k6 = localRegionX + (l3 >> 4 & 7);
			int j9 = localRegionY + (l3 & 7);
			int i12 = stream.readWordMixed();
			int j14 = stream.readUnsignedWord();
			if (k6 >= 0 && j9 >= 0 && k6 < 104 && j9 < 104 && i12 != unknownInt10)
			{
				Item groundItem = new Item();
				groundItem.ID = i1;
				groundItem.stackSize = j14;
				if (groundArray[plane][k6][j9] == null)
					groundArray[plane][k6][j9] = new NodeList();
				groundArray[plane][k6][j9].insertHead(groundItem);
				spawnGroundItem(k6, j9);
			}
			return;
		}
		if (j == 156)
		{
			int j1 = stream.readByteSubtract128();
			int i4 = localRegionX + (j1 >> 4 & 7);
			int l6 = localRegionY + (j1 & 7);
			int k9 = stream.readUnsignedWord();
			if (i4 >= 0 && l6 >= 0 && i4 < 104 && l6 < 104)
			{
				NodeList groundItemList = groundArray[plane][i4][l6];
				if (groundItemList != null)
				{
					for (Item item = (Item) groundItemList.reverseGetFirst(); item != null; item = (Item) groundItemList
						.reverseGetNext())
					{
						if (item.ID != (k9 & 0x7fff))
							continue;
						item.unlink();
						break;
					}

					if (groundItemList.reverseGetFirst() == null)
						groundArray[plane][i4][l6] = null;
					spawnGroundItem(i4, l6);
				}
			}
			return;
		}
		if (j == 160)
		{
			int k1 = stream.readByte128Minus();
			int j4 = localRegionX + (k1 >> 4 & 7);
			int i7 = localRegionY + (k1 & 7);
			int l9 = stream.readByte128Minus();
			int j12 = l9 >> 2;
			int k14 = l9 & 3;
			int j16 = characterModelIndices[j12];
			int j17 = stream.readWordMixed();
			if (j4 >= 0 && i7 >= 0 && j4 < 103 && i7 < 103)
			{
				int j18 = intGroundArray[plane][j4][i7];
				int i19 = intGroundArray[plane][j4 + 1][i7];
				int l19 = intGroundArray[plane][j4 + 1][i7 + 1];
				int k20 = intGroundArray[plane][j4][i7 + 1];
				if (j16 == 0)
				{
					Wall wall = worldController.getWall(plane, j4, i7);
					if (wall != null)
					{
						int k21 = wall.uid >> 14 & 0x7fff;
						if (j12 == 2)
						{
							wall.primaryModel = new InteractiveObject(k21, 4 + k14, 2, i19, l19, j18, k20, j17,
								false);
							wall.secondaryModel = new InteractiveObject(k21, k14 + 1 & 3, 2, i19, l19, j18, k20,
								j17, false);
						}
						else
						{
							wall.primaryModel = new InteractiveObject(k21, k14, j12, i19, l19, j18, k20, j17,
								false);
						}
					}
				}
				if (j16 == 1)
				{
					WallDecoration wallDecoration = worldController.getWallDecoration(j4, i7, plane);
					if (wallDecoration != null)
						wallDecoration.model = new InteractiveObject(wallDecoration.uid >> 14 & 0x7fff, 0, 4, i19, l19,
							j18, k20, j17, false);
				}
				if (j16 == 2)
				{
					GameObject gameObject = worldController.getGameObject(j4, i7, plane);
					if (j12 == 11)
						j12 = 10;
					if (gameObject != null)
						gameObject.model = new InteractiveObject(gameObject.uid >> 14 & 0x7fff, k14, j12, i19,
							l19, j18, k20, j17, false);
				}
				if (j16 == 3)
				{
					GroundDecoration groundDecoration = worldController.getGroundDecoration(i7, j4, plane);
					if (groundDecoration != null)
						groundDecoration.model = new InteractiveObject(groundDecoration.uid >> 14 & 0x7fff, k14, 22, i19,
							l19, j18, k20, j17, false);
				}
			}
			return;
		}
		if (j == 147)
		{
			int l1 = stream.readByte128Minus();
			int k4 = localRegionX + (l1 >> 4 & 7);
			int j7 = localRegionY + (l1 & 7);
			int i10 = stream.readUnsignedWord();
			byte byte0 = stream.readSignedByte128Minus();
			int l14 = stream.readWordLittleEndian();
			byte byte1 = stream.readSignedByteNegated();
			int k17 = stream.readUnsignedWord();
			int k18 = stream.readByte128Minus();
			int j19 = k18 >> 2;
			int i20 = k18 & 3;
			int l20 = characterModelIndices[j19];
			byte byte2 = stream.readSignedByte();
			int l21 = stream.readUnsignedWord();
			byte byte3 = stream.readSignedByteNegated();
			Stoner stoner;
			if (i10 == unknownInt10)
				stoner = myStoner;
			else
				stoner = stonerArray[i10];
			if (stoner != null)
			{
				ObjectDef objectDef = ObjectDef.forID(l21);
				int i22 = intGroundArray[plane][k4][j7];
				int j22 = intGroundArray[plane][k4 + 1][j7];
				int k22 = intGroundArray[plane][k4 + 1][j7 + 1];
				int l22 = intGroundArray[plane][k4][j7 + 1];
				Model model = objectDef.getModel(j19, i20, i22, j22, k22, l22, -1);
				if (model != null)
				{
					createSpotAnimation(k17 + 1, -1, 0, l20, j7, 0, plane, k4, l14 + 1);
					stoner.spotAnimStartCycle = l14 + loopCycle;
					stoner.spotAnimEndCycle = k17 + loopCycle;
					stoner.spotAnimationModel = model;
					int i23 = objectDef.sizeX;
					int j23 = objectDef.sizeY;
					if (i20 == 1 || i20 == 3)
					{
						i23 = objectDef.sizeY;
						j23 = objectDef.sizeX;
					}
					stoner.spotAnimX = k4 * 128 + i23 * 64;
					stoner.spotAnimDestY = j7 * 128 + j23 * 64;
					stoner.spotAnimZ = getTerrainHeight(plane, stoner.spotAnimDestY, stoner.spotAnimX);
					if (byte2 > byte0)
					{
						byte byte4 = byte2;
						byte2 = byte0;
						byte0 = byte4;
					}
					if (byte3 > byte1)
					{
						byte byte5 = byte3;
						byte3 = byte1;
						byte1 = byte5;
					}
					stoner.spotAnimMinX = k4 + byte2;
					stoner.spotAnimMaxX = k4 + byte0;
					stoner.spotAnimMinY = j7 + byte3;
					stoner.spotAnimMaxY = j7 + byte1;
				}
			}
		}
		if (j == 151)
		{
			int i2 = stream.readByteSubtract128();
			int l4 = localRegionX + (i2 >> 4 & 7);
			int k7 = localRegionY + (i2 & 7);
			int j10 = stream.readWordLittleEndian();
			int k12 = stream.readByte128Minus();
			int i15 = k12 >> 2;
			int k16 = k12 & 3;
			int l17 = characterModelIndices[i15];
			if (l4 >= 0 && k7 >= 0 && l4 < 104 && k7 < 104)
				createSpotAnimation(-1, j10, k16, l17, k7, i15, plane, l4, 0);
			return;
		}
		if (j == 4)
		{
			int j2 = stream.readUnsignedByte();
			int i5 = localRegionX + (j2 >> 4 & 7);
			int l7 = localRegionY + (j2 & 7);
			int k10 = stream.readUnsignedWord();
			int l12 = stream.readUnsignedByte();
			int j15 = stream.readUnsignedWord();
			if (i5 >= 0 && l7 >= 0 && i5 < 104 && l7 < 104)
			{
				i5 = i5 * 128 + 64;
				l7 = l7 * 128 + 64;
				GraphicEffect graphicEffect = new GraphicEffect(plane, loopCycle, j15, k10,
					getTerrainHeight(plane, l7, i5) - l12, l7, i5);
				queueSpotAnimation.insertHead(graphicEffect);
			}
			return;
		}
		if (j == 44)
		{
			int k2 = stream.readWordMixedLE();
			int j5 = stream.readUnsignedWord();
			int i8 = stream.readUnsignedByte();
			int l10 = localRegionX + (i8 >> 4 & 7);
			int i13 = localRegionY + (i8 & 7);
			if (l10 >= 0 && i13 >= 0 && l10 < 104 && i13 < 104)
			{
				Item groundItem = new Item();
				groundItem.ID = k2;
				groundItem.stackSize = j5;
				if (groundArray[plane][l10][i13] == null)
					groundArray[plane][l10][i13] = new NodeList();
				groundArray[plane][l10][i13].insertHead(groundItem);
				spawnGroundItem(l10, i13);
			}
			return;
		}
		if (j == 101)
		{
			int l2 = stream.readByteNegated();
			int k5 = l2 >> 2;
			int j8 = l2 & 3;
			int i11 = characterModelIndices[k5];
			int j13 = stream.readUnsignedByte();
			int k15 = localRegionX + (j13 >> 4 & 7);
			int l16 = localRegionY + (j13 & 7);
			if (k15 >= 0 && l16 >= 0 && k15 < 104 && l16 < 104)
				createSpotAnimation(-1, -1, j8, i11, l16, k5, plane, k15, 0);
			return;
		}
		if (j == 117)
		{
			int i3 = stream.readUnsignedByte();
			int l5 = localRegionX + (i3 >> 4 & 7);
			int k8 = localRegionY + (i3 & 7);
			int j11 = l5 + stream.readSignedByte();
			int k13 = k8 + stream.readSignedByte();
			int l15 = stream.readSignedWord();
			int i17 = stream.readUnsignedWord();
			int i18 = stream.readUnsignedByte() * 4;
			int l18 = stream.readUnsignedByte() * 4;
			int k19 = stream.readUnsignedWord();
			int j20 = stream.readUnsignedWord();
			int i21 = stream.readUnsignedByte();
			int j21 = stream.readUnsignedByte();
			if (l5 >= 0 && k8 >= 0 && l5 < 104 && k8 < 104 && j11 >= 0 && k13 >= 0 && j11 < 104 && k13 < 104
				&& i17 != 65535)
			{
				l5 = l5 * 128 + 64;
				k8 = k8 * 128 + 64;
				j11 = j11 * 128 + 64;
				k13 = k13 * 128 + 64;
				Projectile projectile = new Projectile(i21, l18, k19 + loopCycle, j20 + loopCycle,
					j21, plane, getTerrainHeight(plane, k8, l5) - i18, k8, l5, l15, i17);
				projectile.calculateTrajectory(k19 + loopCycle, k13, getTerrainHeight(plane, k13, j11) - l18, j11);
				nodeList.insertHead(projectile);
			}
		}
	}
}

