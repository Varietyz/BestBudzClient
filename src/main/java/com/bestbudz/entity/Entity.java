package com.bestbudz.entity;

import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.rendering.Animable;
import com.bestbudz.rendering.animation.Animation;

public class Entity extends Animable {

  public final int[] smallX;
  public final int[] smallY;
  public final int[] hitArray;
  public final int[] hitMarkTypes;
  public final int[] hitsLoopCycle;
  public final int[] hitmarkMove = new int[4];
  public final int[] hitmarkTrans = new int[4];
  public final int[] hitIcon = new int[4];
  public int interactingEntity;
  public int anInt1503;
  public int mapIconScale;
  public int anInt1505;
  public String textSpoken;
  public int height;
  public int turnDirection;
  public int standAnimation;
  public int anInt1512;
  public int chatType;
  public int currentAnimation;
  public int frameIndex;
  public int frameTimer;
  public int spotAnimId;
  public int spotAnimFrame;
  public int spotAnimTimer;
  public int spotAnimStart;
  public int anInt1524;
  public int smallXYIndex;
  public int anim;
  public int animFrameIndex;
  public int animFrameTimer;
  public int animDelay;
  public int animLoopCount;
  public int chatEffect;
  public int loopCycleStatus;
  public long currentHealth;
  public long maxHealth;
  public int textCycle;
  public int lastUpdateCycle;
  public int anInt1538;
  public int anInt1539;
  public int size;
  public boolean animChanged;
  public int movementDelay;
  public int anInt1543;
  public int anInt1544;
  public int anInt1545;
  public int anInt1546;
  public int animStart;
  public int animEnd;
  public int anInt1549;
  Entity() {
    smallX = new int[10];
    smallY = new int[10];
    interactingEntity = -1;
    mapIconScale = 32;
    anInt1505 = -1;
    height = 200;
    standAnimation = -1;
    anInt1512 = -1;
    hitArray = new int[4];
    hitMarkTypes = new int[4];
    hitsLoopCycle = new int[4];
    currentAnimation = -1;
    spotAnimId = -1;
    anim = -1;
    loopCycleStatus = -1000;
    textCycle = 100;
    size = 1;
    animChanged = false;
    aBooleanArray1553 = new boolean[10];
    walkAnimation = -1;
    turnRightAnimation = -1;
    turnAroundAnimation = -1;
    turnLeftAnimation = -1;
  }

  public final void setPos(int i, int j, boolean flag) {
    if (anim != -1 && Animation.anims[anim].anInt364 == 1) anim = -1;
    if (!flag) {
      int k = i - smallX[0];
      int l = j - smallY[0];
      if (k >= -8 && k <= 8 && l >= -8 && l <= 8) {
        if (smallXYIndex < 9) smallXYIndex++;
        for (int i1 = smallXYIndex; i1 > 0; i1--) {
          smallX[i1] = smallX[i1 - 1];
          smallY[i1] = smallY[i1 - 1];
          aBooleanArray1553[i1] = aBooleanArray1553[i1 - 1];
        }

        smallX[0] = i;
        smallY[0] = j;
        aBooleanArray1553[0] = false;
        return;
      }
    }
    smallXYIndex = 0;
    movementDelay = 0;
    anInt1503 = 0;
    smallX[0] = i;
    smallY[0] = j;
    x = smallX[0] * 128 + size * 64;
    y = smallY[0] * 128 + size * 64;
  }

  public final void method446() {
    smallXYIndex = 0;
    movementDelay = 0;
  }

  public final void updateHitData(int markType, int damage, int l, int icon) {
    for (int i1 = 0; i1 < 4; i1++)
      if (hitsLoopCycle[i1] <= l) {
        hitIcon[i1] = icon;
        hitmarkMove[i1] = 5;
        hitmarkTrans[i1] = 230;
        hitArray[i1] = SettingsConfig.enable10xDamage ? damage * 10 : damage;
        if (damage > 0 && SettingsConfig.enable10xDamage) {
          hitArray[i1] += new java.util.Random().nextInt(9);
        }
        hitMarkTypes[i1] = markType;
        hitsLoopCycle[i1] = l + 70;
        return;
      }
  }

  public final void moveInDir(boolean flag, int i) {
    int j = smallX[0];
    int k = smallY[0];
    if (i == 0) {
      j--;
      k++;
    }
    if (i == 1) k++;
    if (i == 2) {
      j++;
      k++;
    }
    if (i == 3) j--;
    if (i == 4) j++;
    if (i == 5) {
      j--;
      k--;
    }
    if (i == 6) k--;
    if (i == 7) {
      j++;
      k--;
    }
    if (anim != -1 && Animation.anims[anim].anInt364 == 1) anim = -1;
    if (smallXYIndex < 9) smallXYIndex++;
    for (int l = smallXYIndex; l > 0; l--) {
      smallX[l] = smallX[l - 1];
      smallY[l] = smallY[l - 1];
      aBooleanArray1553[l] = aBooleanArray1553[l - 1];
    }
    smallX[0] = j;
    smallY[0] = k;
    aBooleanArray1553[0] = flag;
  }

  public boolean isVisible() {
    return false;
  }
  public int x;
  public int y;
  public int orientation;
  public final boolean[] aBooleanArray1553;
  public int walkAnimation;
  public int turnRightAnimation;
  public int turnAroundAnimation;
  public int turnLeftAnimation;

  public int nextAnimationFrame;
  public int nextIdleFrame;
  public int nextSpotFrame;
}
