package com.bestbudz.entity;

import com.bestbudz.cache.Signlink;
import com.bestbudz.data.items.ItemDef;
import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.engine.core.Client;

import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.graphics.text.TextInput;
import static com.bestbudz.ui.interfaces.Chatbox.pushMessage;
import com.bestbudz.graphics.text.TextClass;
import com.bestbudz.net.proto.EntityUpdateProto.*;
import com.bestbudz.net.proto.CommonProto.*;
import java.util.List;

public class UpdateStoners extends Client
{
	public static void updateStonersProto(StonerUpdate update)
	{
		removedNpcCount = 0;
		updatedNpcCount = 0;
		stonerCount = 0;

		// 1. Local player movement
		applyLocalMovement(update.getLocalMovement());

		// 2. Local player mask
		if (update.hasLocalMask()) {
			applyStonerMask(update.getLocalMask(), myStonerIndex, myStoner);
		}

		// 3. Sync existing tracked stoners
		for (StonerSync sync : update.getSyncedStonersList()) {
			int idx = sync.getIndex();
			Stoner stoner = stonerArray[idx];
			if (stoner == null) continue;

			switch (sync.getMovementCase()) {
				case STANDING:
					stonerIndices[stonerCount++] = idx;
					stoner.lastUpdateCycle = loopCycle;
					break;
				case WALK:
					stonerIndices[stonerCount++] = idx;
					stoner.lastUpdateCycle = loopCycle;
					stoner.moveInDir(false, sync.getWalk().getDirection());
					break;
				case RUN:
					stonerIndices[stonerCount++] = idx;
					stoner.lastUpdateCycle = loopCycle;
					stoner.moveInDir(true, sync.getRun().getDirection1());
					stoner.moveInDir(true, sync.getRun().getDirection2());
					break;
				case REMOVED:
					removedNpcIndices[removedNpcCount++] = idx;
					continue; // skip mask
				case NO_UPDATE:
					stonerIndices[stonerCount++] = idx;
					stoner.lastUpdateCycle = loopCycle;
					break;
				default:
					stonerIndices[stonerCount++] = idx;
					stoner.lastUpdateCycle = loopCycle;
					break;
			}

			if (sync.hasMask()) {
				applyStonerMask(sync.getMask(), idx, stoner);
			}
		}

		// 4. Process removed indices
		for (int idx : update.getRemovedIndicesList()) {
			if (idx >= 0 && idx < stonerArray.length) {
				removedNpcIndices[removedNpcCount++] = idx;
			}
		}

		// 5. Add new stoners
		for (StonerAdd add : update.getAddedStonersList()) {
			int idx = add.getIndex();
			if (stonerArray[idx] == null) {
				stonerArray[idx] = new Stoner();
			}
			stonerIndices[stonerCount++] = idx;
			Stoner stoner = stonerArray[idx];
			stoner.lastUpdateCycle = loopCycle;
			stoner.setPos(myStoner.smallX[0] + add.getDeltaX(),
						  myStoner.smallY[0] + add.getDeltaY(), true);

			if (add.hasMask()) {
				applyStonerMask(add.getMask(), idx, stoner);
			}
		}

		// 6. Clean up removed
		for (int k = 0; k < removedNpcCount; k++) {
			int l = removedNpcIndices[k];
			if (l >= 0 && l < stonerArray.length && stonerArray[l] != null
				&& stonerArray[l].lastUpdateCycle != loopCycle)
				stonerArray[l] = null;
		}

		for (int i1 = 0; i1 < stonerCount; i1++)
			if (stonerArray[stonerIndices[i1]] == null) {
				Signlink.reporterror(myUsername + " null entry in pl list - pos:" + i1 + " size:" + stonerCount);
				throw new RuntimeException("eek");
			}
	}

	private static void applyLocalMovement(LocalStonerMovement move) {
		switch (move.getMovementCase()) {
			case NO_UPDATE:
				return;
			case STANDING:
				// standing with update - mask will be applied separately
				return;
			case WALK:
				myStoner.moveInDir(false, move.getWalk().getDirection());
				return;
			case RUN:
				myStoner.moveInDir(true, move.getRun().getDirection1());
				myStoner.moveInDir(true, move.getRun().getDirection2());
				return;
			case PLACEMENT:
				PlacementMovement p = move.getPlacement();
				plane = p.getZ();
				myStoner.setPos(p.getLocalX(), p.getLocalY(), p.getDiscardMovementQueue());
				return;
			default:
				return;
		}
	}

	private static void applyStonerMask(StonerUpdateMask mask, int index, Stoner stoner) {
		if (mask.hasForceMovement()) {
			ForceMovementData fm = mask.getForceMovement();
			stoner.anInt1543 = fm.getStartX();
			stoner.anInt1545 = fm.getStartY();
			stoner.anInt1544 = fm.getEndX();
			stoner.anInt1546 = fm.getEndY();
			stoner.animStart = fm.getSpeed1() + loopCycle;
			stoner.animEnd = fm.getSpeed2() + loopCycle;
			stoner.anInt1549 = fm.getDirection();
			stoner.method446();
		}

		if (mask.hasGraphic()) {
			GraphicData g = mask.getGraphic();
			stoner.spotAnimId = g.getId();
			stoner.anInt1524 = g.getHeight();
			stoner.spotAnimStart = loopCycle + g.getDelay();
			stoner.spotAnimFrame = 0;
			stoner.spotAnimTimer = 0;
			if (stoner.spotAnimStart > loopCycle)
				stoner.spotAnimFrame = -1;
			if (stoner.spotAnimId == 65535)
				stoner.spotAnimId = -1;
		}

		if (mask.hasAnimation()) {
			AnimationData a = mask.getAnimation();
			int animId = a.getId();
			if (animId == 65535) animId = -1;
			int delay = a.getDelay();
			if (animId == stoner.anim && animId != -1) {
				int i3 = Animation.anims[animId].anInt365;
				if (i3 == 1) {
					stoner.animFrameIndex = 0;
					stoner.animFrameTimer = 0;
					stoner.animDelay = delay;
					stoner.animLoopCount = 0;
				}
				if (i3 == 2)
					stoner.animLoopCount = 0;
			} else if (animId == -1 || stoner.anim == -1
				|| Animation.anims[animId].anInt359 >= Animation.anims[stoner.anim].anInt359) {
				stoner.anim = animId;
				stoner.animFrameIndex = 0;
				stoner.animFrameTimer = 0;
				stoner.animDelay = delay;
				stoner.animLoopCount = 0;
				stoner.movementDelay = stoner.smallXYIndex;
			}
		}

		if (mask.hasForceChat()) {
			stoner.textSpoken = mask.getForceChat();
			if (stoner.textSpoken.charAt(0) == '~') {
				stoner.textSpoken = stoner.textSpoken.substring(1);
				pushMessage(stoner.textSpoken, 2, stoner.name, stoner.title, stoner.titleColor);
			} else if (stoner == myStoner) {
				pushMessage(stoner.textSpoken, 2, stoner.name, stoner.title, stoner.titleColor);
			}
			stoner.chatType = 0;
			stoner.chatEffect = 0;
			stoner.textCycle = 150;
		}

		if (mask.hasChat()) {
			StonerChatData chat = mask.getChat();
			int rights = chat.getRights();
			byte[] textBytes = chat.getText().toByteArray();

			if (stoner.name != null && stoner.visible) {
				long nameHash = TextClass.longForName(stoner.name);
				boolean ignored = false;
				if (rights <= 1) {
					for (int i4 = 0; i4 < ignoreCount; i4++) {
						if (ignoreListAsLongs[i4] == nameHash) {
							ignored = true;
							break;
						}
					}
				}
				if (!ignored && publicChatFilter == 0) {
					try {
						// Decode the chat text (bytes are in reversed format from server)
						incomingPacketBuffer.position = 0;
						// Copy reversed bytes into buffer for decoding
						for (int b = textBytes.length - 1; b >= 0; b--)
							incomingPacketBuffer.buffer[incomingPacketBuffer.position++] = textBytes[b];
						incomingPacketBuffer.position = 0;
						String s = TextInput.method525(textBytes.length, incomingPacketBuffer);
						stoner.textSpoken = s;
						stoner.chatType = chat.getColor();
						stoner.privelage = rights;
						stoner.chatEffect = chat.getEffects();
						stoner.textCycle = 150;
						if (rights >= 1)
							pushMessage(s, 1, "@cr" + rights + "@" + stoner.name, stoner.title, stoner.titleColor);
						else
							pushMessage(s, 2, stoner.name, stoner.title, stoner.titleColor);
					} catch (Exception e) {
						Signlink.reporterror("cde2");
						e.printStackTrace();
					}
				}
			}
		}

		if (mask.hasFaceEntity()) {
			stoner.interactingEntity = mask.getFaceEntity();
			if (stoner.interactingEntity == 65535)
				stoner.interactingEntity = -1;
		}

		if (mask.hasAppearance()) {
			applyAppearance(mask.getAppearance(), index, stoner);
		}

		if (mask.hasFaceDirection()) {
			FaceDirection fd = mask.getFaceDirection();
			stoner.anInt1538 = fd.getX();
			stoner.anInt1539 = fd.getY();
		}

		if (mask.hasHit1()) {
			HitData h = mask.getHit1();
			stoner.updateHitData(h.getDamage(), h.getHitType(), loopCycle, h.getHitUpdateType());
			stoner.loopCycleStatus = loopCycle + 300;
			stoner.currentHealth = h.getHp();
			stoner.maxHealth = h.getMaxHp();
		}

		if (mask.hasHit2()) {
			HitData h = mask.getHit2();
			stoner.updateHitData(h.getDamage(), h.getHitType(), loopCycle, h.getHitUpdateType());
			stoner.loopCycleStatus = loopCycle + 300;
			stoner.currentHealth = h.getHp();
			stoner.maxHealth = h.getMaxHp();
		}
	}

	private static void applyAppearance(StonerAppearance app, int index, Stoner stoner) {
		stoner.gender = app.getGender();
		stoner.headIcon = app.getHeadicon();
		stoner.skullIcon = app.getHeadicon(); // same as headIcon (server sends same value)
		stoner.hintIcon = app.getRights();
		stoner.desc = null;
		stoner.team = 0;

		List<Integer> equip = app.getEquipmentList();
		if (!equip.isEmpty() && equip.get(0) == 65535) {
			// NPC appearance
			stoner.equipment[0] = 65535;
			stoner.desc = EntityDef.forID(app.getNpcAppearanceId());
		} else {
			// Normal equipment
			for (int j = 0; j < 12 && j < equip.size(); j++) {
				stoner.equipment[j] = equip.get(j);
				if (stoner.equipment[j] >= 512 && stoner.equipment[j] - 512 < ItemDef.totalItems) {
					int teamId = getItemDefinition(stoner.equipment[j] - 512).team;
					if (teamId != 0) stoner.team = teamId;
				}
			}
		}

		// Colors
		List<Integer> colors = app.getColorsList();
		for (int l = 0; l < 5 && l < colors.size(); l++) {
			int c = colors.get(l);
			if (c < 0 || c >= Client.validInterfaceRegions[l].length) c = 0;
			stoner.bodyColors[l] = c;
		}

		// Emotes
		stoner.standAnimation = app.getStandEmote();
		if (stoner.standAnimation == 65535) stoner.standAnimation = -1;
		stoner.anInt1512 = app.getStandTurnEmote();
		if (stoner.anInt1512 == 65535) stoner.anInt1512 = -1;
		stoner.walkAnimation = app.getWalkEmote();
		if (stoner.walkAnimation == 65535) stoner.walkAnimation = -1;
		stoner.turnRightAnimation = app.getTurn180Emote();
		if (stoner.turnRightAnimation == 65535) stoner.turnRightAnimation = -1;
		stoner.turnAroundAnimation = app.getTurn90CwEmote();
		if (stoner.turnAroundAnimation == 65535) stoner.turnAroundAnimation = -1;
		stoner.turnLeftAnimation = app.getTurn90CcwEmote();
		if (stoner.turnLeftAnimation == 65535) stoner.turnLeftAnimation = -1;
		stoner.anInt1505 = app.getRunEmote();
		if (stoner.anInt1505 == 65535) stoner.anInt1505 = -1;

		// Name & title
		stoner.name = TextClass.fixName(app.getDisplayName());
		if (app.hasTitle()) {
			stoner.titleColor = app.getTitle().getColorHex();
			stoner.title = TextClass.fixName(app.getTitle().getTitle());
			stoner.titlePrefix = app.getTitle().getIsSuffix();
		} else {
			stoner.titleColor = "0";
			stoner.title = "";
			stoner.titlePrefix = false;
		}

		stoner.combatLevel = app.getCombatGrade();
		stoner.visible = true;

		// Rebuild model cache key
		stoner.modelCacheKey = 0L;
		for (int k1 = 0; k1 < 12; k1++) {
			stoner.modelCacheKey <<= 4;
			if (stoner.equipment[k1] >= 256)
				stoner.modelCacheKey += stoner.equipment[k1] - 256;
		}
		if (stoner.equipment[0] >= 256)
			stoner.modelCacheKey += stoner.equipment[0] - 256 >> 4;
		if (stoner.equipment[1] >= 256)
			stoner.modelCacheKey += stoner.equipment[1] - 256 >> 8;
		for (int i2 = 0; i2 < 5; i2++) {
			stoner.modelCacheKey <<= 3;
			stoner.modelCacheKey += stoner.bodyColors[i2];
		}
		stoner.modelCacheKey <<= 1;
		stoner.modelCacheKey += stoner.gender;
	}

}
