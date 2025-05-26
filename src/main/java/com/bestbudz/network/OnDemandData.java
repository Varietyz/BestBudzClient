package com.bestbudz.network;

import com.bestbudz.util.NodeSub;

public final class OnDemandData extends NodeSub {

  public int dataType;
  public byte[] buffer;
  public int ID;
  boolean incomplete;
  int loopCycle;
  public OnDemandData() {
    incomplete = true;
  }
}
