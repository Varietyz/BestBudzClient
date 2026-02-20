package com.bestbudz.entity;

import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.SpotAnim;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;

public final class Npc extends Entity
{

	public EntityDef desc;

	public Npc()
	{
	}

	private Model method450()
	{
		if (super.anim >= 0 && super.animDelay == 0)
		{
			int k = Animation.anims[super.anim].frameIds[super.animFrameIndex];
			int i1 = -1;
			if (super.currentAnimation >= 0 && super.currentAnimation != super.standAnimation)
				i1 = Animation.anims[super.currentAnimation].frameIds[super.frameIndex];
			return desc.getAnimatedModel(i1, k, Animation.anims[super.anim].anIntArray357);
		}
		int l = -1;
		if (super.currentAnimation >= 0)
			l = Animation.anims[super.currentAnimation].frameIds[super.frameIndex];
		return desc.getAnimatedModel(-1, l, null);
	}

	public Model getModel()
	{
		if (desc == null)
			return null;
		Model model = method450();
		if (model == null)
			return null;
		super.height = model.modelHeight;
		if (super.spotAnimId != -1 && super.spotAnimFrame != -1)
		{
			SpotAnim spotAnim = SpotAnim.cache[super.spotAnimId];
			Model model_1 = spotAnim.getModel();
			if (model_1 != null)
			{
				int j = spotAnim.animation.frameIds[super.spotAnimFrame];
				Model model_2 = new Model(true, SequenceFrame.isInvalidFrame(j), false, model_1);
				model_2.translateCoords(0, -super.anInt1524, 0);
				model_2.calculateNormals();
				model_2.applyTransformation(j);
				model_2.anIntArrayArray1658 = null;
				model_2.anIntArrayArray1657 = null;
				if (spotAnim.resizeX != 128 || spotAnim.resizeY != 128)
					model_2.modelScale(spotAnim.resizeX, spotAnim.resizeX, spotAnim.resizeY);
				model_2.applyLighting(64 + spotAnim.ambient, 850 + spotAnim.contrast, -30, -50, -30, true);
				Model[] aModel = {
					model, model_2
				};
				model = new Model(aModel);
			}
		}
		if (desc.size == 1)
			model.aBoolean1659 = true;
		return model;
	}

	public boolean isVisible()
	{
		return desc != null;
	}

}
