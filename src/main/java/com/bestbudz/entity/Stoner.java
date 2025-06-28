package com.bestbudz.entity;

import com.bestbudz.cache.IdentityKit;
import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.engine.core.Client;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.network.Stream;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.SpotAnim;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.MRUNodes;
import com.bestbudz.graphics.text.TextClass;

public final class Stoner extends Entity
{
	public static MRUNodes mruNodes = new MRUNodes(260);
	public final int[] bodyColors;
	public final int[] equipment;
	public int privelage;
	public String title, titleColor;
	public boolean titlePrefix;
	public EntityDef desc;
	public boolean lowDetailMode;
	public int team;
	public String name;
	public int combatLevel;
	public int headIcon;
	public int skullIcon;
	public int hintIcon;
	public int spotAnimStartCycle;
	public int spotAnimEndCycle;
	public int spotAnimHeight;
	public boolean visible;
	public int spotAnimX;
	public int spotAnimZ;
	public int spotAnimDestY;
	public Model spotAnimationModel;
	public int spotAnimMinX;
	public int spotAnimMinY;
	public int spotAnimMaxX;
	public int spotAnimMaxY;
	public int skill;
	int gender;
	private long lastModelKey;
	private long modelCacheKey;
	public Stoner() {
		lastModelKey = -1L;
		lowDetailMode = false;
		bodyColors = new int[5];
		visible = false;
		equipment = new int[12];
	}

@Override
	public Model getModel() {
		if (!visible)
			return null;
		Model model = buildPlayerModel();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		model.aBoolean1659 = true;
		if (lowDetailMode)
			return model;
		if (super.spotAnimId != -1 && super.spotAnimFrame != -1) {
			SpotAnim spotAnim = SpotAnim.cache[super.spotAnimId];
			Model model_2 = spotAnim.getModel();
			if (model_2 != null) {
				Model model_3 = new Model(true, SequenceFrame.isInvalidFrame(super.spotAnimFrame), false, model_2);
				model_3.translateCoords(0, -super.anInt1524, 0);
				model_3.calculateNormals();
				model_3.applyTransformation(spotAnim.animation.frameIds[super.spotAnimFrame]);
				model_3.anIntArrayArray1658 = null;
				model_3.anIntArrayArray1657 = null;
				if (spotAnim.resizeX != 128 || spotAnim.resizeY != 128)
					model_3.modelScale(spotAnim.resizeX, spotAnim.resizeX, spotAnim.resizeY);
				model_3.applyLighting(84 + spotAnim.ambient, 1550 + spotAnim.contrast, -50, -110, -50, true);
				Model[] aclass30_sub2_sub4_sub6_1s = { model, model_3 };
				model = new Model(aclass30_sub2_sub4_sub6_1s);
			} else {
				return null;
			}
		}
		if (spotAnimationModel != null) {
			if (Client.loopCycle >= spotAnimEndCycle)
				spotAnimationModel = null;
			if (Client.loopCycle >= spotAnimStartCycle && Client.loopCycle < spotAnimEndCycle) {
				Model model_1 = spotAnimationModel;
				model_1.translateCoords(spotAnimX - super.x, spotAnimZ - spotAnimHeight, spotAnimDestY - super.y);
				if (super.turnDirection == 512) {
					model_1.rotateY180();
					model_1.rotateY180();
					model_1.rotateY180();
				} else if (super.turnDirection == 1024) {
					model_1.rotateY180();
					model_1.rotateY180();
				} else if (super.turnDirection == 1536)
					model_1.rotateY180();
				Model[] aclass30_sub2_sub4_sub6s = { model, model_1 };
				model = new Model(aclass30_sub2_sub4_sub6s);
				if (super.turnDirection == 512)
					model_1.rotateY180();
				else if (super.turnDirection == 1024) {
					model_1.rotateY180();
					model_1.rotateY180();
				} else if (super.turnDirection == 1536) {
					model_1.rotateY180();
					model_1.rotateY180();
					model_1.rotateY180();
				}
				model_1.translateCoords(super.x - spotAnimX, spotAnimHeight - spotAnimZ, super.y - spotAnimDestY);
			}
		}
		model.aBoolean1659 = true;
		return model;
	}

	public void updateStoner(Stream stream)
	{
		stream.position = 0;
		gender = stream.readUnsignedByte();
		headIcon = stream.readUnsignedByte();
		skullIcon = stream.readUnsignedByte();
		hintIcon = stream.readUnsignedByte();
		desc = null;
		team = 0;
		for (int j = 0; j < 12; j++)
		{
			int k = stream.readUnsignedByte();
			if (k == 0)
			{
				equipment[j] = 0;
				continue;
			}
			int i1 = stream.readUnsignedByte();
			equipment[j] = (k << 8) + i1;
			if (j == 0 && equipment[0] == 65535)
			{
				desc = EntityDef.forID(stream.readUnsignedWord());
				break;
			}
			if (equipment[j] >= 512 && equipment[j] - 512 < ItemDef.totalItems)
			{
				int l1 = getItemDefinition(equipment[j] - 512).team;
				if (l1 != 0)
					team = l1;
			}
		}

		for (int l = 0; l < 5; l++)
		{
			int j1 = stream.readUnsignedByte();
			if (j1 < 0 || j1 >= Client.validInterfaceRegions[l].length)
				j1 = 0;
			bodyColors[l] = j1;
		}

		super.standAnimation = stream.readUnsignedWord();
		if (super.standAnimation == 65535)
			super.standAnimation = -1;
		super.anInt1512 = stream.readUnsignedWord();
		if (super.anInt1512 == 65535)
			super.anInt1512 = -1;
		super.walkAnimation = stream.readUnsignedWord();
		if (super.walkAnimation == 65535)
			super.walkAnimation = -1;
		super.turnRightAnimation = stream.readUnsignedWord();
		if (super.turnRightAnimation == 65535)
			super.turnRightAnimation = -1;
		super.turnAroundAnimation = stream.readUnsignedWord();
		if (super.turnAroundAnimation == 65535)
			super.turnAroundAnimation = -1;
		super.turnLeftAnimation = stream.readUnsignedWord();
		if (super.turnLeftAnimation == 65535)
			super.turnLeftAnimation = -1;
		super.anInt1505 = stream.readUnsignedWord();
		if (super.anInt1505 == 65535)
			super.anInt1505 = -1;
		name = TextClass.fixName(stream.readString());

		titleColor = stream.readString();
		title = TextClass.fixName(stream.readString());
		titlePrefix = stream.readUnsignedByte() == 1;

		combatLevel = stream.readDWord();
		visible = true;
		modelCacheKey = 0L;
		for (int k1 = 0; k1 < 12; k1++)
		{
			modelCacheKey <<= 4;
			if (equipment[k1] >= 256)
				modelCacheKey += equipment[k1] - 256;
		}

		if (equipment[0] >= 256)
			modelCacheKey += equipment[0] - 256 >> 4;
		if (equipment[1] >= 256)
			modelCacheKey += equipment[1] - 256 >> 8;
		for (int i2 = 0; i2 < 5; i2++)
		{
			modelCacheKey <<= 3;
			modelCacheKey += bodyColors[i2];
		}

		modelCacheKey <<= 1;
		modelCacheKey += gender;
	}

	public Model buildPlayerModel() {
		int nextAnim = -1;
		int currCycle = -1;
		int nextCycle = -1;
		long l = modelCacheKey;
		int k = -1;
		int i1 = -1;
		int j1 = -1;
		int k1 = -1;

		if (desc != null) {
			if (super.anim >= 0 && super.animDelay == 0) {
				final Animation seq = Animation.anims[super.anim];
				k = seq.frameIds[super.animFrameIndex];
				if (super.nextAnimationFrame != -1) {
					nextAnim = seq.frameIds[super.nextAnimationFrame];
					currCycle = seq.anIntArray355[super.animFrameIndex];
					nextCycle = super.animFrameTimer;
				}
			} else if (super.currentAnimation >= 0) {
				final Animation seq = Animation.anims[super.currentAnimation];
				k = seq.frameIds[super.frameIndex];
				if (super.nextIdleFrame != -1) {
					nextAnim = seq.frameIds[super.nextIdleFrame];
					currCycle = seq.anIntArray355[super.frameIndex];
					nextCycle = super.frameTimer;
				}
			}
			return desc.getAnimatedModel(-1, k, nextAnim, currCycle, nextCycle, null);
		}

		// Cache animation reference to avoid repeated array access
		Animation animation = null;
		if (super.anim >= 0 && super.animDelay == 0) {
			animation = Animation.anims[super.anim];
			k = animation.frameIds[super.animFrameIndex];
			if (super.nextAnimationFrame != -1) {
				nextAnim = animation.frameIds[super.nextAnimationFrame];
				currCycle = animation.anIntArray355[super.animFrameIndex];
				nextCycle = super.animFrameTimer;
			}
			if (super.currentAnimation >= 0 && super.currentAnimation != super.standAnimation) {
				i1 = Animation.anims[super.currentAnimation].frameIds[super.frameIndex];
			}
			if (animation.anInt360 >= 0) {
				j1 = animation.anInt360;
				l += (long) j1 - equipment[5] << 40;
			}
			if (animation.anInt361 >= 0) {
				k1 = animation.anInt361;
				l += (long) k1 - equipment[3] << 48;
			}
		} else if (super.currentAnimation >= 0) {
			final Animation seq = Animation.anims[super.currentAnimation];
			k = seq.frameIds[super.frameIndex];
			if (super.nextIdleFrame != -1) {
				nextAnim = seq.frameIds[super.nextIdleFrame];
				currCycle = seq.anIntArray355[super.frameIndex];
				nextCycle = super.frameTimer;
			}
		}

		Model model_1 = (Model) mruNodes.insertFromCache(l);
		if (model_1 == null) {
			boolean flag = false;
			for (int i2 = 0; i2 < 12; i2++) {
				int k2 = equipment[i2];
				if (k1 >= 0 && i2 == 3)
					k2 = k1;
				if (j1 >= 0 && i2 == 5)
					k2 = j1;
				if (k2 >= 256 && k2 < 512 && IdentityKit.cache[k2 - 256].method537())
					flag = true;
				if (k2 >= 512 && !getItemDefinition(k2 - 512).method195(gender))
					flag = true;
			}

			if (flag) {
				if (lastModelKey != -1L)
					model_1 = (Model) mruNodes.insertFromCache(lastModelKey);
				if (model_1 == null)
					return null;
			}
		}

		if (model_1 == null) {
			final Model[] modelParts = new Model[12];
			int j2 = 0;
			for (int l2 = 0; l2 < 12; l2++) {
				int i3 = equipment[l2];
				if (k1 >= 0 && l2 == 3)
					i3 = k1;
				if (j1 >= 0 && l2 == 5)
					i3 = j1;
				if (i3 >= 256 && i3 < 512) {
					final Model model_3 = IdentityKit.cache[i3 - 256].method538();
					if (model_3 != null)
						modelParts[j2++] = model_3;
				}
				if (i3 >= 512) {
					final Model model_4 = getItemDefinition(i3 - 512).getInventoryModel(gender);
					if (model_4 != null)
						modelParts[j2++] = model_4;
				}
			}

			model_1 = new Model(j2, modelParts);
			for (int j3 = 0; j3 < 5; j3++)
				if (bodyColors[j3] != 0) {
					model_1.replaceColor(Client.validInterfaceRegions[j3][0],
						Client.validInterfaceRegions[j3][bodyColors[j3]]);
					if (j3 == 1)
						model_1.replaceColor(Client.chatSystemColors[0], Client.chatSystemColors[bodyColors[j3]]);
				}

			model_1.calculateNormals();
			model_1.modelScale(132, 132, 132);
			model_1.applyLighting(84, 1000, -90, -580, -90, true);
			mruNodes.removeFromCache(model_1, l);
			lastModelKey = l;
		}

		if (lowDetailMode)
			return model_1;

		final Model model_2 = Model.aModel_1621;
		model_2.copyModel(model_1, SequenceFrame.isInvalidFrame(k) & SequenceFrame.isInvalidFrame(i1));
		if (k != -1 && i1 != -1) {
			model_2.applyMixedTransformation(Animation.anims[super.anim].anIntArray357, i1, k);
		} else if (k != -1) {
			model_2.interpolateFrames(k, nextAnim, nextCycle, currCycle);
		}
		model_2.calculateBounds();
		model_2.anIntArrayArray1658 = null;
		model_2.anIntArrayArray1657 = null;
		return model_2;
	}

	public boolean isVisible() {
		return visible;
	}

	public Model getUnanimatedModel() {
		if (!visible)
			return null;
		if (desc != null)
			return desc.getModelForInterface();
		boolean flag = false;
		for (int i = 0; i < 12; i++) {
			int j = equipment[i];
			if (j >= 256 && j < 512 && !IdentityKit.cache[j - 256].method539())
				flag = true;
			if (j >= 512 && !getItemDefinition(j - 512).method192(gender))
				flag = true;
		}

		if (flag)
			return null;
		Model[] aclass30_sub2_sub4_sub6s = new Model[12];
		int k = 0;
		for (int l = 0; l < 12; l++) {
			int i1 = equipment[l];
			if (i1 >= 256 && i1 < 512) {
				Model model_1 = IdentityKit.cache[i1 - 256].method540();
				aclass30_sub2_sub4_sub6s[k++] = model_1;
			}
			if (i1 >= 512) {
				Model model_2 = getItemDefinition(i1 - 512).getWornModel(gender);
				if (model_2 != null)
					aclass30_sub2_sub4_sub6s[k++] = model_2;
			}
		}

		Model model = new Model(k, aclass30_sub2_sub4_sub6s);
		for (int j1 = 0; j1 < 5; j1++)
			if (bodyColors[j1] != 0) {
				model.replaceColor(Client.validInterfaceRegions[j1][0], Client.validInterfaceRegions[j1][bodyColors[j1]]);
				if (j1 == 1)
					model.replaceColor(Client.chatSystemColors[0], Client.chatSystemColors[bodyColors[j1]]);
			}

		return model;
	}

}
