package com.bestbudz.entity;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.engine.core.gamerender.WorldController;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.net.proto.EntityUpdateProto.*;
import com.bestbudz.net.proto.CommonProto.*;
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
				if (l < 104 && i1 < 104) {
					if (renderGrid[l][i1] == currentTick)
						continue;
					renderGrid[l][i1] = currentTick;
				}
			}
			if (!npc.desc.clickable)
				k += 0x80000000;
			worldController.addLargeObject(plane, npc.orientation, getTerrainHeight(plane, npc.y, npc.x), k, npc.y,
				(npc.size - 1) * 64 + 60, npc.x, npc, npc.animChanged);
		}
	}

	public static void updateNPCsProto(NpcUpdate update) {
		removedNpcCount = 0;
		updatedNpcCount = 0;
		npcCount = 0;

		// 1. Sync existing tracked NPCs
		for (NpcSync sync : update.getSyncedNpcsList()) {
			int idx = sync.getIndex();
			Npc npc = npcArray[idx];
			if (npc == null) continue;

			switch (sync.getMovementCase()) {
				case STANDING:
					npcIndices[npcCount++] = idx;
					npc.lastUpdateCycle = loopCycle;
					break;
				case WALK:
					npcIndices[npcCount++] = idx;
					npc.lastUpdateCycle = loopCycle;
					npc.moveInDir(false, sync.getWalk().getDirection());
					break;
				case REMOVED:
					removedNpcIndices[removedNpcCount++] = idx;
					continue;
				case NO_UPDATE:
					npcIndices[npcCount++] = idx;
					npc.lastUpdateCycle = loopCycle;
					break;
				default:
					npcIndices[npcCount++] = idx;
					npc.lastUpdateCycle = loopCycle;
					break;
			}

			if (sync.hasMask()) {
				applyNpcMask(sync.getMask(), npc);
			}
		}

		// 2. Process removed indices
		for (int idx : update.getRemovedIndicesList()) {
			if (idx >= 0 && idx < npcArray.length) {
				removedNpcIndices[removedNpcCount++] = idx;
			}
		}

		// 3. Add new NPCs
		for (NpcAdd add : update.getAddedNpcsList()) {
			int idx = add.getIndex();
			if (npcArray[idx] == null)
				npcArray[idx] = new Npc();
			Npc npc = npcArray[idx];
			npcIndices[npcCount++] = idx;
			npc.lastUpdateCycle = loopCycle;
			npc.desc = EntityDef.forID(add.getNpcId());
			npc.size = npc.desc.size;
			npc.mapIconScale = npc.desc.mapIconScale;
			npc.walkAnimation = npc.desc.walkAnim;
			npc.turnRightAnimation = npc.desc.turnRightAnim;
			npc.turnAroundAnimation = npc.desc.turnAroundAnim;
			npc.turnLeftAnimation = npc.desc.turnLeftAnim;
			npc.standAnimation = npc.desc.standAnim;
			npc.setPos(myStoner.smallX[0] + add.getDeltaX(),
					   myStoner.smallY[0] + add.getDeltaY(), true);

			if (add.hasMask()) {
				applyNpcMask(add.getMask(), npc);
			}
		}

		// 4. Clean up removed
		for (int k = 0; k < removedNpcCount; k++) {
			int l = removedNpcIndices[k];
			if (l >= 0 && l < npcArray.length && npcArray[l] != null
				&& npcArray[l].lastUpdateCycle != loopCycle) {
				npcArray[l].desc = null;
				npcArray[l] = null;
			}
		}

		for (int i1 = 0; i1 < npcCount; i1++)
			if (npcArray[npcIndices[i1]] == null) {
				Signlink.reporterror(myUsername + " null entry in npc list - pos:" + i1 + " size:" + npcCount);
				throw new RuntimeException("eek");
			}
	}

	private static void applyNpcMask(NpcUpdateMask mask, Npc npc) {
		if (mask.hasAnimation()) {
			AnimationData a = mask.getAnimation();
			int animId = a.getId();
			if (animId == 65535) animId = -1;
			int delay = a.getDelay();
			if (animId == npc.anim && animId != -1) {
				int replayMode = Animation.anims[animId].anInt365;
				if (replayMode == 1) {
					npc.animFrameIndex = 0;
					npc.animFrameTimer = 0;
					npc.animDelay = delay;
					npc.animLoopCount = 0;
				}
				if (replayMode == 2)
					npc.animLoopCount = 0;
			} else if (animId == -1 || npc.anim == -1
				|| Animation.anims[animId].anInt359 >= Animation.anims[npc.anim].anInt359) {
				npc.anim = animId;
				npc.animFrameIndex = 0;
				npc.animFrameTimer = 0;
				npc.animDelay = delay;
				npc.animLoopCount = 0;
				npc.movementDelay = npc.smallXYIndex;
			}
		}

		if (mask.hasHit1()) {
			HitData h = mask.getHit1();
			npc.updateHitData(h.getDamage(), h.getHitType(), loopCycle, h.getHitUpdateType());
			npc.loopCycleStatus = loopCycle + 300;
			npc.currentHealth = h.getHp();
			npc.maxHealth = h.getMaxHp();
		}

		if (mask.hasGraphic()) {
			GraphicData g = mask.getGraphic();
			npc.spotAnimId = g.getId();
			npc.anInt1524 = g.getHeight();
			npc.spotAnimStart = loopCycle + g.getDelay();
			npc.spotAnimFrame = 0;
			npc.spotAnimTimer = 0;
			if (npc.spotAnimStart > loopCycle)
				npc.spotAnimFrame = -1;
			if (npc.spotAnimId == 65535)
				npc.spotAnimId = -1;
		}

		if (mask.hasFaceEntity()) {
			npc.interactingEntity = mask.getFaceEntity();
			if (npc.interactingEntity == 65535)
				npc.interactingEntity = -1;
		}

		if (mask.hasForceChat()) {
			npc.textSpoken = mask.getForceChat();
			npc.textCycle = 100;
		}

		if (mask.hasHit2()) {
			HitData h = mask.getHit2();
			npc.updateHitData(h.getDamage(), h.getHitType(), loopCycle, h.getHitUpdateType());
			npc.loopCycleStatus = loopCycle + 300;
			npc.currentHealth = h.getHp();
			npc.maxHealth = h.getMaxHp();
		}

		if (mask.hasTransformId()) {
			int transformId = mask.getTransformId();
			npc.desc = EntityDef.forID(transformId);
			npc.size = npc.desc.size;
			npc.mapIconScale = npc.desc.mapIconScale;
			npc.walkAnimation = npc.desc.walkAnim;
			npc.turnRightAnimation = npc.desc.turnRightAnim;
			npc.turnAroundAnimation = npc.desc.turnAroundAnim;
			npc.turnLeftAnimation = npc.desc.turnLeftAnim;
			npc.standAnimation = npc.desc.standAnim;
		}

		if (mask.hasFaceDirection()) {
			FaceDirection fd = mask.getFaceDirection();
			npc.anInt1538 = fd.getX();
			npc.anInt1539 = fd.getY();
		}
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
			stoner.lowDetailMode = lowMem && !flag
				&& stoner.currentAnimation == stoner.standAnimation;
			int j1 = stoner.x >> 7;
			int k1 = stoner.y >> 7;
			if (j1 < 0 || j1 >= 208 || k1 < 0 || k1 >= 208)
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
				if (j1 < 104 && k1 < 104) {
					if (renderGrid[j1][k1] == currentTick)
					{
						continue;
					}
					renderGrid[j1][k1] = currentTick;
				}
			}
			stoner.spotAnimHeight = getTerrainHeight(plane, stoner.y, stoner.x);
			worldController.addLargeObject(plane, stoner.orientation, stoner.spotAnimHeight, i1, stoner.y, 60, stoner.x, stoner,
				stoner.animChanged);
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

		if (l <= 50)
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}

		int screenX = Rasterizer.viewportCenterX + (i << WorldController.viewDistance) / l;
		int screenY = Rasterizer.viewportCenterY + (i1 << WorldController.viewDistance) / l;

		int screenWidth = Rasterizer.viewportCenterX * 2;
		int screenHeight = Rasterizer.viewportCenterY * 2;

		if (Math.abs(screenX) > screenWidth * 5 || Math.abs(screenY) > screenHeight * 5)
		{
			spriteDrawX = -1;
			spriteDrawY = -1;
			return;
		}

		spriteDrawX = screenX;
		spriteDrawY = screenY;
	}

}
