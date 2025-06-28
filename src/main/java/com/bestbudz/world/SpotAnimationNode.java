package com.bestbudz.world;

import com.bestbudz.util.Node;

public final class SpotAnimationNode extends Node {

  public int objectId;
  public int objectType;
  public int objectRotation;
  public int delay;
  public int plane;
  public int type;
  public int x;
  public int y;
  public int previousObjectId;
  public int previousObjectType;
  public int previousObjectRotation;
  public int timer;
  public SpotAnimationNode() {
    delay = -1;
  }
}
