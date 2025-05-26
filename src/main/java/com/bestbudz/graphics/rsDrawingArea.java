package com.bestbudz.graphics;

public class rsDrawingArea extends DrawingArea {

  public static int height;
  public static int bottomX;
  public static int topX;
  public static int width;
  public static int[] pixels;
  public static int topY;
  public static int bottomY = 0;

  static {
    bottomX = 0;
    topX = 0;
    topY = 0;
  }

  public static void setDrawingArea(int[] is) {
    topX = is[0];
    topY = is[1];
    bottomX = is[2];
    bottomY = is[3];
    clear();
  }

  public static void startSetAreaColor(int drawX, int drawY, int color) {
    if (drawX >= topX && drawY >= topY && drawX < bottomX && drawY < bottomY) {
      pixels[drawX + drawY * width] = color;
    }
  }

  public static void setDrawingArea(int startX, int startYI, int areaWidth, int areaHeight) {
    if (startX < 0) {
      startX = 0;
    }
    if (startYI < 0) {
      startYI = 0;
    }
    if (areaWidth > width) {
      areaWidth = width;
    }
    if (areaHeight > height) {
      areaHeight = height;
    }
    topX = startX;
    topY = startYI;
    bottomX = areaWidth;
    bottomY = areaHeight;
    clear();
  }

  public static void drawVerticalLine(int i, int i_103_, int i_104_, int i_105_) {
    if (i >= topX && i < bottomX) {
      if (i_103_ < topY) {
        i_104_ -= topY - i_103_;
        i_103_ = topY;
      }
      if (i_103_ + i_104_ > bottomY) {
        i_104_ = bottomY - i_103_;
      }
      int i_106_ = i + i_103_ * width;
      for (int i_107_ = 0; i_107_ < i_104_; i_107_++) {
        pixels[i_106_ + i_107_ * width] = i_105_;
      }
    }
  }

  public static void clear() {}
}
