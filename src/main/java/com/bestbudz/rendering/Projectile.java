package com.bestbudz.rendering;

import com.bestbudz.rendering.model.Model;

public final class Projectile extends Animable {

  public final int startX;
  public final int endTime;
  public final int startCycle;
  public final int targetId;
  public final int targetX;
  private final int startY;
  private final int targetY;
  private final int startHeight;
  private final int angle;
  private final int distance;
  private final SpotAnim spotAnim;
  public double currentX;
  public double currentY;
  public double currentHeight;
  public int yaw;
  private double velocityX;
  private double velocityY;
  private double horizontalSpeed;
  private double velocityZ;
  private double accelerationZ;
  private boolean moving;
  private int frameIndex;
  private int frameTimer;
  private int pitch;

  public Projectile(
      int i, int j, int l, int i1, int j1, int k1, int l1, int i2, int j2, int k2, int l2) {
	  spotAnim = SpotAnim.cache[l2];
    targetX = k1;
    startY = j2;
    targetY = i2;
    startHeight = l1;
    startX = l;
    endTime = i1;
    angle = i;
    distance = j1;
    targetId = k2;
    startCycle = j;
    moving = false;
  }

  public void calculateTrajectory(int i, int j, int k, int l) {
    if (!moving) {
      double d = l - startY;
      double d2 = j - targetY;
      double d3 = Math.sqrt(d * d + d2 * d2);
      currentX = (double) startY + (d * (double) distance) / d3;
      currentY = (double) targetY + (d2 * (double) distance) / d3;
      currentHeight = startHeight;
    }
    double d1 = (endTime + 1) - i;
    velocityX = ((double) l - currentX) / d1;
    velocityY = ((double) j - currentY) / d1;
    horizontalSpeed = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    if (!moving) {
      velocityZ = -horizontalSpeed * Math.tan((double) angle * 0.02454369D);
    }
    accelerationZ = (2D * ((double) k - currentHeight - velocityZ * d1)) / (d1 * d1);
  }

  public Model getModel() {
    Model model = spotAnim.getModel();
    if (model == null) {
      return null;
    }
    int j = -1;
    if (spotAnim.animation != null) {
      j = spotAnim.animation.frameIds[frameIndex];
    }
    Model model_1 = new Model(true, SequenceFrame.isInvalidFrame(j), false, model);
    if (j != -1) {
      model_1.calculateNormals();
      model_1.applyTransformation(j);
      model_1.anIntArrayArray1658 = null;
      model_1.anIntArrayArray1657 = null;
    }
    if (spotAnim.resizeX != 128 || spotAnim.resizeY != 128) {
      model_1.modelScale(spotAnim.resizeX, spotAnim.resizeX, spotAnim.resizeY);
    }
    model_1.rotateX(pitch);
    model_1.applyLighting(
        64 + spotAnim.ambient, 850 + spotAnim.contrast, -30, -50, -30, true);
    return model_1;
  }

  public void updatePosition(int i) {
    moving = true;
    currentX += velocityX * (double) i;
    currentY += velocityY * (double) i;
    currentHeight += velocityZ * (double) i + 0.5D * accelerationZ * (double) i * (double) i;
    velocityZ += accelerationZ * (double) i;
    yaw = (int) (Math.atan2(velocityX, velocityY) * 325.94900000000001D) + 1024 & 0x7ff;
    pitch = (int) (Math.atan2(velocityZ, horizontalSpeed) * 325.94900000000001D) & 0x7ff;
    if (spotAnim.animation != null) {
      for (frameTimer += i; frameTimer > spotAnim.animation.getFrameDuration(frameIndex); ) {
        frameTimer -= spotAnim.animation.getFrameDuration(frameIndex) + 1;
        frameIndex++;
        if (frameIndex >= spotAnim.animation.frameCount) {
          frameIndex = 0;
        }
      }
    }
  }
}
