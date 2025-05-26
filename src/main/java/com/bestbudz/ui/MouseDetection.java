package com.bestbudz.ui;

import com.bestbudz.engine.Client;
import com.bestbudz.engine.ClientEngine;

public final class MouseDetection implements Runnable {

  public final Object syncObject;
  public final int[] coordsY;
  public final int[] coordsX;
	public boolean running;
  public int coordsIndex;
  public MouseDetection(Client client1) {
    syncObject = new Object();
    coordsY = new int[500];
    running = true;
    coordsX = new int[500];
  }

  public void run() {
    while (running) {
      synchronized (syncObject) {
        if (coordsIndex < 500) {
          coordsX[coordsIndex] = ClientEngine.mouseX;
          coordsY[coordsIndex] = ClientEngine.mouseY;
          coordsIndex++;
        }
      }
      try {
        Thread.sleep(50L);
      } catch (Exception _ex)
	  {
		  throw new RuntimeException(_ex);
	  }
    }
  }
}
