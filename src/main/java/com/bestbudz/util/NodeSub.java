package com.bestbudz.util;

public class NodeSub extends Node {

  public NodeSub prevNodeSub;
  NodeSub nextNodeSub;

  public NodeSub() {}

  public final void unlinkSub() {
    if (nextNodeSub == null) {
    } else {
      nextNodeSub.prevNodeSub = prevNodeSub;
      prevNodeSub.nextNodeSub = nextNodeSub;
      prevNodeSub = null;
      nextNodeSub = null;
    }
  }
}
