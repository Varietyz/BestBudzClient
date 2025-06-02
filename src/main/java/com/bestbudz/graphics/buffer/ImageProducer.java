package com.bestbudz.graphics.buffer;

import com.bestbudz.engine.core.gamerender.DrawingArea;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public final class ImageProducer {

  public final float[] depthBuffer;
  public final int[] canvasRaster;
  public final int canvasWidth;
  public final int canvasHeight;
  private final BufferedImage bufferedImage;

  public ImageProducer(int canvasWidth, int canvasHeight) {
    this.canvasWidth = canvasWidth;
    this.canvasHeight = canvasHeight;
    depthBuffer = new float[canvasWidth * canvasHeight];
    bufferedImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
    canvasRaster = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
    initDrawingArea();
  }

  public void drawGraphics(int y, Graphics2D graphics, int x) {
    graphics.drawImage(bufferedImage, x, y, null);
  }

  public void initDrawingArea() {
    DrawingArea.initDrawingArea(
        canvasHeight, canvasWidth, canvasRaster, bufferedImage != null ? depthBuffer : null);
  }

  public Graphics2D getImageGraphics() {
    return bufferedImage.createGraphics();
  }
}
