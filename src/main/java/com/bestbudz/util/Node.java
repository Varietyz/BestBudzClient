package com.bestbudz.util;

public class Node {

  public long id;
  public Node prev;

  public final void unlink() {
    if (next == null) {
    } else {
      next.prev = prev;
      prev.next = next;
      prev = null;
      next = null;
    }
  }
  public Node next;
}
