package com.bestbudz.util;

public class Node {

  public long id;
  public Node prev;
  public Node next;
  public final void unlink() {
    if (next == null) {
    } else {
      next.prev = prev;
      prev.next = next;
      prev = null;
      next = null;
    }
  }
}
