package com.bestbudz.rendering;

import com.bestbudz.engine.Client;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.world.VarBit;

public final class Animable_Sub5 extends Animable {
Client client;
  public static Client clientInstance;
  private final int[] anIntArray1600;
  private final int anInt1601;
  private final int anInt1602;
  private final int anInt1603;
  private final int anInt1604;
  private final int anInt1605;
  private final int anInt1606;
  private final int anInt1610;
  private final int anInt1611;
  private final int anInt1612;
  private int anInt1599;
  private Animation aAnimation_1607;
  private int anInt1608;

  public Animable_Sub5(int i, int j, int k, int l, int i1, int j1, int k1, int l1, boolean flag) {
    anInt1610 = i;
    anInt1611 = k;
    anInt1612 = j;
    anInt1603 = j1;
    anInt1604 = l;
    anInt1605 = i1;
    anInt1606 = k1;
    if (l1 != -1) {
      aAnimation_1607 = Animation.anims[l1];
      anInt1599 = 0;
      anInt1608 = Client.loopCycle;
      if (flag && aAnimation_1607.anInt356 != -1) {
        anInt1599 = (int) (Math.random() * (double) aAnimation_1607.anInt352);
        anInt1608 -= (int) (Math.random() * (double) aAnimation_1607.method258(anInt1599));
      }
    }
    ObjectDef class46 = ObjectDef.forID(anInt1610);
    anInt1601 = class46.anInt774;
    anInt1602 = class46.anInt749;
    anIntArray1600 = class46.childrenIDs;
  }

	/* Animable_Sub5.java */
	private ObjectDef method457() {
		int idx = -1;                               // result of the VarBit / config lookup
		int[] settings = (client != null) ? client.variousSettings : null;

		/* ---------- guarded VarBit lookup ---------- */
		if (anInt1601 != -1
			&& anInt1601 < VarBit.cache.length
			&& VarBit.cache[anInt1601] != null) {

			VarBit vb   = VarBit.cache[anInt1601];
			int confId  = vb.anInt648;
			int least   = vb.anInt649;
			int most    = vb.anInt650;

			if (settings != null && confId >= 0 && confId < settings.length) {
				int mask = Client.anIntArray1232[most - least];
				idx = (settings[confId] >> least) & mask;
			}
		}

		/* ---------- direct config lookup ----------- */
		else if (anInt1602 != -1
			&& settings != null
			&& anInt1602 < settings.length) {
			idx = settings[anInt1602];
		}

		/* ---------- validity check ----------------- */
		if (idx < 0 || idx >= anIntArray1600.length)
			return null;

		int objId = anIntArray1600[idx];
		return objId == -1 ? null : ObjectDef.forID(objId);
	}


  public Model getFinalRenderedModel() {
    int j = -1;
    if (aAnimation_1607 != null) {
      int k = Client.loopCycle - anInt1608;
      if (k > 100 && aAnimation_1607.anInt356 > 0) {
        k = 100;
      }
      while (k > aAnimation_1607.method258(anInt1599)) {
        k -= aAnimation_1607.method258(anInt1599);
        anInt1599++;
        if (anInt1599 < aAnimation_1607.anInt352) continue;
        anInt1599 -= aAnimation_1607.anInt356;
        if (anInt1599 >= 0 && anInt1599 < aAnimation_1607.anInt352) continue;
        aAnimation_1607 = null;
        break;
      }
      anInt1608 = Client.loopCycle - k;
      if (aAnimation_1607 != null) {
        j = aAnimation_1607.anIntArray353[anInt1599];
      }
    }
    ObjectDef class46;
    if (anIntArray1600 != null) class46 = method457();
    else class46 = ObjectDef.forID(anInt1610);
    if (class46 == null) {
      return null;
    } else {
      return class46.method578(anInt1611, anInt1612, anInt1603, anInt1604, anInt1605, anInt1606, j);
    }
  }
}
