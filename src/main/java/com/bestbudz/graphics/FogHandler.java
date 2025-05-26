package com.bestbudz.graphics;

public class FogHandler {

  private final int fogStartDistance = 2000;
  private final int fogEndDistance = 3000;
  public int fogColor = 0xB3B281;

  public void renderFog(int[] colorBuffer, float[] depthBuffer) {
    float fogLength = fogEndDistance - fogStartDistance;
    for (int index = 0; index < colorBuffer.length; index++) {
      int relative = ((int) depthBuffer[index] - fogStartDistance);
      float factor = Math.min(1f, Math.max(relative / fogLength, 0f));
      colorBuffer[index] = FogUtil.mix(colorBuffer[index], fogColor, factor);
    }
  }
}
