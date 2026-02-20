package com.bestbudz.rendering;

import com.bestbudz.engine.core.Client;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.world.VarBit;

public final class InteractiveObject extends Animable {
Client client;
  public static Client clientInstance;
  private final int[] childIds;
  private final int varbitId;
  private final int configId;
  private final int orientation;
  private final int type;
  private final int width;
  private final int height;
  private final int objectId;
  private final int tileX;
  private final int tileY;
  private int frameIndex;
  private Animation animation;
  private int animationStart;

  public InteractiveObject(int i, int j, int k, int l, int i1, int j1, int k1, int l1, boolean flag) {
    objectId = i;
    tileX = k;
    tileY = j;
    orientation = j1;
    type = l;
    width = i1;
    height = k1;
    if (l1 != -1) {
      animation = Animation.anims[l1];
		frameIndex = 0;
      animationStart = Client.loopCycle;
      if (flag && animation.loopOffset != -1) {
        frameIndex = (int) (Math.random() * (double) animation.frameCount);
        animationStart -= (int) (Math.random() * (double) animation.getFrameDuration(frameIndex));
      }
    }
    ObjectDef objectDef = ObjectDef.forID(objectId);
    varbitId = objectDef.varbitId;
    configId = objectDef.configId;
    childIds = objectDef.childIds;
  }

	private ObjectDef getTransformedDef() {
		int idx = -1;
		int[] settings = (client != null) ? client.variousSettings : null;

		if (varbitId != -1
			&& varbitId < VarBit.cache.length
			&& VarBit.cache[varbitId] != null) {

			VarBit vb   = VarBit.cache[varbitId];
			int confId  = vb.baseVar;
			int least   = vb.startBit;
			int most    = vb.endBit;

			if (settings != null && confId >= 0 && confId < settings.length) {
				int mask = Client.bitMasks[most - least];
				idx = (settings[confId] >> least) & mask;
			}
		}

		else if (configId != -1
			&& settings != null
			&& configId < settings.length) {
			idx = settings[configId];
		}

		if (idx < 0 || idx >= childIds.length)
			return null;

		int objId = childIds[idx];
		return objId == -1 ? null : ObjectDef.forID(objId);
	}

  public Model getModel() {
    int j = -1;
    if (animation != null) {
      int k = Client.loopCycle - animationStart;
      if (k > 100 && animation.loopOffset > 0) {
        k = 100;
      }
      while (k > animation.getFrameDuration(frameIndex)) {
        k -= animation.getFrameDuration(frameIndex);
        frameIndex++;
        if (frameIndex < animation.frameCount) continue;
        frameIndex -= animation.loopOffset;
        if (frameIndex >= 0 && frameIndex < animation.frameCount) continue;
        animation = null;
        break;
      }
      animationStart = Client.loopCycle - k;
      if (animation != null) {
        j = animation.frameIds[frameIndex];
      }
    }
    ObjectDef objectDef;
    if (childIds != null) objectDef = getTransformedDef();
    else objectDef = ObjectDef.forID(objectId);
    if (objectDef == null) {
      return null;
    } else {
      return objectDef.getModel(tileX, tileY, orientation, type, width, height, j);
    }
  }
}
