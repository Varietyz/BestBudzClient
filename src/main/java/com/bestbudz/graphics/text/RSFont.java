package com.bestbudz.graphics.text;

import com.bestbudz.config.ColorConstants;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.graphics.rsDrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import com.bestbudz.network.Stream;
import com.bestbudz.network.StreamLoader;
import java.awt.Color;

public class RSFont extends DrawingArea {

  public static Sprite[] chatImages;
  public static Sprite[] clanImages;
  public static String aRSString_4135;
  public static String startTransparency;
  public static String startDefaultShadow;
  public static String endShadow = "/shad";
  public static String endEffect;
  public static String aRSString_4143;
  public static String endStrikethrough = "/str";
  public static String aRSString_4147;
  public static String startColor;
  public static String lineBreak;
  public static String startStrikethrough;
  public static String endColor;
  public static String startImage;
  public static final String startClanImage;
  public static String endUnderline;
  public static String defaultStrikethrough;
  public static String startShadow;
  public static String startEffect;
  public static String aRSString_4162;
  public static String aRSString_4163;
  public static String endTransparency;
  public static String aRSString_4165;
  public static String startUnderline;
  public static String startDefaultUnderline;
  public static String aRSString_4169;
  public static String[] splitTextStrings;
  public static int defaultColor;
  public static int textShadowColor;
  public static int strikethroughColor;
  public static int defaultTransparency;
  public static int anInt4175;
  public static int underlineColor;
  public static int defaultShadow;
  public static int anInt4178;
  public static int transparency;
  public static int textColor;
  public int baseCharacterHeight = 0;
  public int[] characterDrawYOffsets;
  public int[] characterHeights;
  public int[] characterDrawXOffsets;
  public int[] characterWidths;
  public byte[][] fontPixels;
  public int[] characterScreenWidths;

  public RSFont(boolean TypeFont, String s, StreamLoader archive) {
    fontPixels = new byte[256][];
    characterWidths = new int[256];
    characterHeights = new int[256];
    characterDrawXOffsets = new int[256];
    characterDrawYOffsets = new int[256];
    characterScreenWidths = new int[256];
    Stream stream = new Stream(archive.getDataForName(s + ".dat"));
    Stream stream_1 = new Stream(archive.getDataForName("index.dat"));
    stream_1.currentOffset = stream.readUnsignedWord() + 4;
    int k = stream_1.readUnsignedByte();
    if (k > 0) {
      stream_1.currentOffset += 3 * (k - 1);
    }
    for (int l = 0; l < 256; l++) {
      characterDrawXOffsets[l] = stream_1.readUnsignedByte();
      characterDrawYOffsets[l] = stream_1.readUnsignedByte();
      int i1 = characterWidths[l] = stream_1.readUnsignedWord();
      int j1 = characterHeights[l] = stream_1.readUnsignedWord();
      int k1 = stream_1.readUnsignedByte();
      int l1 = i1 * j1;
      fontPixels[l] = new byte[l1];
      if (k1 == 0) {
        for (int i2 = 0; i2 < l1; i2++) {
          fontPixels[l][i2] = stream.readSignedByte();
        }

      } else if (k1 == 1) {
        for (int j2 = 0; j2 < i1; j2++) {
          for (int l2 = 0; l2 < j1; l2++) {
            fontPixels[l][j2 + l2 * i1] = stream.readSignedByte();
          }
        }
      }
      if (j1 > baseCharacterHeight && l < 128) {
        baseCharacterHeight = j1;
      }
      characterDrawXOffsets[l] = 1;
      characterScreenWidths[l] = i1 + 2;
      int k2 = 0;
      for (int i3 = j1 / 7; i3 < j1; i3++) {
        k2 += fontPixels[l][i3 * i1];
      }

      if (k2 <= j1 / 7) {
        characterScreenWidths[l]--;
        characterDrawXOffsets[l] = 0;
      }
      k2 = 0;
      for (int j3 = j1 / 7; j3 < j1; j3++) {
        k2 += fontPixels[l][(i1 - 1) + j3 * i1];
      }

      if (k2 <= j1 / 7) {
        characterScreenWidths[l]--;
      }
    }

    if (TypeFont) {
      characterScreenWidths[32] = characterScreenWidths[73];
    } else {
      characterScreenWidths[32] = characterScreenWidths[105];
    }
  }

	public static String handleOldSyntax(String text) {
		// remapped color tags to a modern palette
		text = text.replaceAll("@gry@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LIME_YELLOW) + ">");
		text = text.replaceAll("@pt2@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CORAL_PEACH) + ">");
		text = text.replaceAll("@pt1@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LIGHT_ORANGE) + ">");
		text = text.replaceAll("@pt7@", "<col=" + Integer.toHexString(ColorConstants.CHAT_BABY_BLUE) + ">");
		text = text.replaceAll("@pt6@", "<col=" + Integer.toHexString(ColorConstants.CHAT_LIGHT_MAGENTA) + ">");
		text = text.replaceAll("@pt3@", "<col=" + Integer.toHexString(ColorConstants.COLOR_BRIGHT_YELLOW) + ">");
		text = text.replaceAll("@pt4@", "<col=" + Integer.toHexString(ColorConstants.CHAT_MINT_AQUA) + ">");
		text = text.replaceAll("@pt5@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SOFT_ROSE) + ">");
		text = text.replaceAll("@red@", "<col=" + Integer.toHexString(ColorConstants.CHAT_SOFT_PINK) + ">");
		text = text.replaceAll("@gre@", "<col=" + Integer.toHexString(ColorConstants.CHAT_BABY_BLUE) + ">");
		text = text.replaceAll("@blu@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LIME_GREEN) + ">");
		text = text.replaceAll("@yel@", "<col=" + Integer.toHexString(ColorConstants.COLOR_PASTEL_VIOLET) + ">");
		text = text.replaceAll("@cya@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SPRING_GREEN) + ">");
		text = text.replaceAll("@mag@", "<col=" + Integer.toHexString(ColorConstants.COLOR_BRIGHT_YELLOW) + ">");
		text = text.replaceAll("@whi@", "<col=" + Integer.toHexString(ColorConstants.COLOR_FROST_TEAL) + ">");
		text = text.replaceAll("@lre@", "<col=" + Integer.toHexString(ColorConstants.COLOR_DUSTY_PERIWINKLE) + ">");
		text = text.replaceAll("@dre@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SUNFLOWER_YELLOW) + ">");
		text = text.replaceAll("@bla@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LIGHT_AMBER) + ">");
		text = text.replaceAll("@or1@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CYAN_TURQUOISE) + ">");
		text = text.replaceAll("@or2@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LAVENDER) + ">");
		text = text.replaceAll("@or3@", "<col=" + Integer.toHexString(ColorConstants.COLOR_ROSE_PINK) + ">");
		text = text.replaceAll("@gr1@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SOFT_PEACH) + ">");
		text = text.replaceAll("@gr2@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CORAL_ORANGE) + ">");
		text = text.replaceAll("@gr3@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CREAM_ROSE) + ">");

		// uppercase variants
		text = text.replaceAll("@RED@", "<col=" + Integer.toHexString(ColorConstants.CHAT_SOFT_PINK) + ">");
		text = text.replaceAll("@GRE@", "<col=" + Integer.toHexString(ColorConstants.CHAT_BABY_BLUE) + ">");
		text = text.replaceAll("@BLU@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LIME_GREEN) + ">");
		text = text.replaceAll("@YEL@", "<col=" + Integer.toHexString(ColorConstants.COLOR_PASTEL_VIOLET) + ">");
		text = text.replaceAll("@CYA@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SPRING_GREEN) + ">");
		text = text.replaceAll("@MAG@", "<col=" + Integer.toHexString(ColorConstants.COLOR_BRIGHT_YELLOW) + ">");
		text = text.replaceAll("@WHI@", "<col=" + Integer.toHexString(ColorConstants.COLOR_FROST_TEAL) + ">");
		text = text.replaceAll("@LRE@", "<col=" + Integer.toHexString(ColorConstants.COLOR_DUSTY_PERIWINKLE) + ">");
		text = text.replaceAll("@DRE@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SUNFLOWER_YELLOW) + ">");
		text = text.replaceAll("@BLA@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LIGHT_AMBER) + ">");
		text = text.replaceAll("@OR1@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CYAN_TURQUOISE) + ">");
		text = text.replaceAll("@OR2@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LAVENDER) + ">");
		text = text.replaceAll("@OR3@", "<col=" + Integer.toHexString(ColorConstants.COLOR_ROSE_PINK) + ">");
		text = text.replaceAll("@GR1@", "<col=" + Integer.toHexString(ColorConstants.COLOR_SOFT_PEACH) + ">");
		text = text.replaceAll("@GR2@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CORAL_ORANGE) + ">");
		text = text.replaceAll("@GR3@", "<col=" + Integer.toHexString(ColorConstants.COLOR_CREAM_ROSE) + ">");

		// specials
		text = text.replaceAll("@ric@", "<col=" + Integer.toHexString(ColorConstants.COLOR_WARM_PEACH) + ">");
		text = text.replaceAll("@cr1@", "<img=0>");
		text = text.replaceAll("@cr2@", "<img=1>");
		text = text.replaceAll("@cr3@", "<img=2>");
		text = text.replaceAll("@don@", "<img=3>");
		text = text.replaceAll("@mbl@", "<col=" + Integer.toHexString(ColorConstants.COLOR_LAVENDER_PINK) + ">");

		return text;
	}


  public static void nullLoader() {
    startEffect = null;
    endEffect = null;
    aRSString_4135 = null;
    aRSString_4162 = null;
    aRSString_4165 = null;
    aRSString_4147 = null;
    aRSString_4163 = null;
    aRSString_4169 = null;
    startImage = null;
    lineBreak = null;
    startColor = null;
    endColor = null;
    startTransparency = null;
    endTransparency = null;
    startUnderline = null;
    startDefaultUnderline = null;
    endUnderline = null;
    startShadow = null;
    startDefaultShadow = null;
    endShadow = null;
    startStrikethrough = null;
    defaultStrikethrough = null;
    endStrikethrough = null;
    aRSString_4143 = null;
    splitTextStrings = null;
  }

  public void setDefaultTextEffectValues(int color, int shadow, int trans) {
    strikethroughColor = -1;
    underlineColor = -1;
    textShadowColor = defaultShadow = shadow;
    textColor = defaultColor = color;
    transparency = defaultTransparency = trans;
    anInt4178 = 0;
    anInt4175 = 0;
  }

  public void unpackImages(Sprite[] icons, Sprite[] clan) {
    chatImages = icons;
    clanImages = clan;
  }

  public void drawBasicString(String string, int drawX, int drawY) {
    drawY -= baseCharacterHeight;
    int startIndex = -1;
    string = handleOldSyntax(string);
    for (int currentCharacter = 0; currentCharacter < string.length(); currentCharacter++) {
      int character = string.charAt(currentCharacter);
      if (character > 255) {
        character = 32;
      }
      if (character == 60 && isSpecial(string, currentCharacter)) {
        startIndex = currentCharacter;
      } else {
        if (character == 62 && startIndex != -1 && isSpecial(string, startIndex + 1)) {
          String effectString = string.substring(startIndex + 1, currentCharacter);
          startIndex = -1;
          if (effectString.equals(startEffect)) {
            character = 60;
          } else if (effectString.equals(endEffect)) {
		  } else if (effectString.equals(aRSString_4135)) {
            character = 160;
          } else if (effectString.equals(aRSString_4162)) {
            character = 173;
          } else if (effectString.equals(aRSString_4165)) {
            character = 215;
          } else if (effectString.equals(aRSString_4147)) {
            character = 128;
          } else if (effectString.equals(aRSString_4163)) {
            character = 169;
          } else if (effectString.equals(aRSString_4169)) {
            character = 174;
          } else {
            if (effectString.startsWith(startImage)) {
              try {
                int imageId = Integer.parseInt(effectString.substring(4));
                Sprite icon = chatImages[imageId];
                icon.drawSprite(drawX, drawY);
                drawX += icon.myWidth + icon.drawOffsetX;
              } catch (Exception exception)
			  {
				  throw new RuntimeException(exception);
			  }
            } else if (effectString.startsWith(startClanImage)) {
              try {
                int imageId = Integer.parseInt(effectString.substring(5));
                Sprite icon = clanImages[imageId];
                icon.drawSprite(drawX, drawY + 2);
                drawX += 11;
              } catch (Exception exception)
			  {
				  throw new RuntimeException(exception);
			  }
            } else {
              setTextEffects(effectString);
            }
            continue;
          }
        }
        if (startIndex == -1) {
          int width = characterWidths[character];
          int height = characterHeights[character];
          if (character != 32) {
            if (transparency == 256) {
              if (textShadowColor != -1) {
                drawCharacter(
                    character,
                    drawX + characterDrawXOffsets[character] + 1,
                    drawY + characterDrawYOffsets[character] + 1,
                    width,
                    height,
                    textShadowColor,
                    true);
              }
              drawCharacter(
                  character,
                  drawX + characterDrawXOffsets[character],
                  drawY + characterDrawYOffsets[character],
                  width,
                  height,
                  textColor,
                  false);
            } else {
              if (textShadowColor != -1) {
                drawTransparentCharacter(
                    character,
                    drawX + characterDrawXOffsets[character] + 1,
                    drawY + characterDrawYOffsets[character] + 1,
                    width,
                    height,
                    textShadowColor,
                    transparency,
                    true);
              }
              drawTransparentCharacter(
                  character,
                  drawX + characterDrawXOffsets[character],
                  drawY + characterDrawYOffsets[character],
                  width,
                  height,
                  textColor,
                  transparency,
                  false);
            }
          } else if (anInt4178 > 0) {
            anInt4175 += anInt4178;
            drawX += anInt4175 >> 8;
            anInt4175 &= 0xff;
          }
          int lineWidth = characterScreenWidths[character];
          if (strikethroughColor != -1) {
            rsDrawingArea.drawHorizontalLine(
                drawX,
                drawY + (int) ((double) baseCharacterHeight * 0.69999999999999996D),
                lineWidth,
                strikethroughColor);
          }
          if (underlineColor != -1) {
            rsDrawingArea.drawHorizontalLine(
                drawX, drawY + baseCharacterHeight, lineWidth, underlineColor);
          }
          drawX += lineWidth;
        }
      }
    }
  }

  public void drawRAString(String string, int drawX, int drawY, int color, int shadow) {
    if (string != null) {
      setColorAndShadow(color, shadow);
      drawBasicString(string, drawX - getTextWidth(string), drawY);
    }
  }

  private boolean isSpecial(String string, int currentCharacter) {
    String substring = string.substring(currentCharacter);
    if (substring.length() < 6) {
      return false;
    }
    if (substring.startsWith("<col=") || substring.startsWith("</col")) {
      return true;
    }
    if (substring.startsWith("<img=") || substring.startsWith("</img")) {
      return true;
    }
	  if (substring.startsWith("<str=") || substring.startsWith("</str")) {
      return true;
    }
	  return true;
  }

  public void setTextEffects(String string) {
    do {
      try {
        if (string.startsWith(startColor)) {
          String color = string.substring(4);
          textColor =
              color.length() < 6 ? Color.decode(color).getRGB() : Integer.parseInt(color, 16);
        } else if (string.equals(endColor)) {
          textColor = defaultColor;
        } else if (string.startsWith(startTransparency)) {
          transparency = Integer.parseInt(string.substring(6));
        } else if (string.equals(endTransparency)) {
          transparency = defaultTransparency;
        } else if (string.startsWith(startStrikethrough)) {
          strikethroughColor = Integer.parseInt(string.substring(4));
        } else if (string.equals(defaultStrikethrough)) {
          strikethroughColor = 8388608;
        } else if (string.equals(endStrikethrough)) {
          strikethroughColor = -1;
        } else if (string.startsWith(startUnderline)) {
          underlineColor = Integer.parseInt(string.substring(2));
        } else if (string.equals(startDefaultUnderline)) {
          underlineColor = 0;
        } else if (string.equals(endUnderline)) {
          underlineColor = -1;
        } else if (string.startsWith(startShadow)) {
          textShadowColor = Integer.parseInt(string.substring(5));
        } else if (string.equals(startDefaultShadow)) {
          textShadowColor = 0;
        } else if (string.equals(endShadow)) {
          textShadowColor = defaultShadow;
        } else {
          if (!string.equals(lineBreak)) {
            break;
          }
          setDefaultTextEffectValues(defaultColor, defaultShadow, defaultTransparency);
        }
      } catch (Exception exception) {
        break;
      }
      break;
    } while (false);
  }

  public void setColorAndShadow(int color, int shadow) {
    strikethroughColor = -1;
    underlineColor = -1;
    textShadowColor = defaultShadow = shadow;
    textColor = defaultColor = color;
    transparency = defaultTransparency = 256;
    anInt4178 = 0;
    anInt4175 = 0;
  }

  public int getTextWidth(String string) {
    if (string == null) {
      return 0;
    }
    int startIndex = -1;
    int finalWidth = 0;
    string = handleOldSyntax(string);
    for (int currentCharacter = 0; currentCharacter < string.length(); currentCharacter++) {
      int character = string.charAt(currentCharacter);
      if (character > 255) {
        character = 32;
      }
      if (character == 60 && isSpecial(string, currentCharacter)) {
        startIndex = currentCharacter;
      } else {
        if (character == 62 && startIndex != -1 && isSpecial(string, startIndex + 1)) {
          String effectString = string.substring(startIndex + 1, currentCharacter);
          startIndex = -1;
          if (effectString.equals(startEffect)) {
            character = 60;
          } else if (effectString.equals(endEffect)) {
		  } else if (effectString.equals(aRSString_4135)) {
            character = 160;
          } else if (effectString.equals(aRSString_4162)) {
            character = 173;
          } else if (effectString.equals(aRSString_4165)) {
            character = 215;
          } else if (effectString.equals(aRSString_4147)) {
            character = 128;
          } else if (effectString.equals(aRSString_4163)) {
            character = 169;
          } else if (effectString.equals(aRSString_4169)) {
            character = 174;
          } else {
            if (effectString.startsWith(startImage)) {
              try {
                int iconId = Integer.parseInt(effectString.substring(4));
                finalWidth += chatImages[iconId].cropWidth;
              } catch (Exception exception)
			  {
				  throw new RuntimeException(exception);
			  }
            } else if (effectString.startsWith(startClanImage)) {
              try {
                int iconId = Integer.parseInt(effectString.substring(5));
                finalWidth += clanImages[iconId].cropWidth;
              } catch (Exception exception)
			  {
				  throw new RuntimeException(exception);
			  }
            } else {
              setTextEffects(effectString);
            }
            continue;
          }
        }
        if (startIndex == -1) {
          finalWidth += characterScreenWidths[character];
        }
      }
    }
    return finalWidth;
  }

  public void drawBasicString(String string, int drawX, int drawY, int color, int shadow) {
    if (string != null) {
      setColorAndShadow(color, shadow);
      drawBasicString(string, drawX, drawY);
    }
  }

  public void drawCenteredString(String string, int drawX, int drawY, int color, int shadow) {
    if (string != null) {
      setColorAndShadow(color, shadow);
      string = handleOldSyntax(string);
      drawBasicString(string, drawX - getTextWidth(string) / 2, drawY);
    }
  }

  public static void createTransparentCharacterPixels(
      int[] is,
      byte[] is_0_,
      int i,
      int i_1_,
      int i_2_,
      int i_3_,
      int i_4_,
      int i_5_,
      int i_6_,
      int i_7_) {
    i = ((i & 0xff00ff) * i_7_ & ~0xff00ff) + ((i & 0xff00) * i_7_ & 0xff0000) >> 8;
    i_7_ = 256 - i_7_;
    for (int i_8_ = -i_4_; i_8_ < 0; i_8_++) {
      for (int i_9_ = -i_3_; i_9_ < 0; i_9_++) {
        if (is_0_[i_1_++] != 0) {
          int i_10_ = is[i_2_];
          is[i_2_++] =
              ((((i_10_ & 0xff00ff) * i_7_ & ~0xff00ff) + ((i_10_ & 0xff00) * i_7_ & 0xff0000))
                      >> 8)
                  + i;
        } else {
          i_2_++;
        }
      }
      i_2_ += i_5_;
      i_1_ += i_6_;
    }
  }

  public void drawTransparentCharacter(
      int i, int i_11_, int i_12_, int i_13_, int i_14_, int i_15_, int i_16_, boolean bool) {
    int i_17_ = i_11_ + i_12_ * DrawingArea.width;
    int i_18_ = DrawingArea.width - i_13_;
    int i_19_ = 0;
    int i_20_ = 0;
    if (i_12_ < DrawingArea.topY) {
      int i_21_ = DrawingArea.topY - i_12_;
      i_14_ -= i_21_;
      i_12_ = DrawingArea.topY;
      i_20_ += i_21_ * i_13_;
      i_17_ += i_21_ * DrawingArea.width;
    }
    if (i_12_ + i_14_ > DrawingArea.bottomY) {
      i_14_ -= i_12_ + i_14_ - DrawingArea.bottomY;
    }
    if (i_11_ < DrawingArea.topX) {
      int i_22_ = DrawingArea.topX - i_11_;
      i_13_ -= i_22_;
      i_11_ = DrawingArea.topX;
      i_20_ += i_22_;
      i_17_ += i_22_;
      i_19_ += i_22_;
      i_18_ += i_22_;
    }
    if (i_11_ + i_13_ > DrawingArea.bottomX) {
      int i_23_ = i_11_ + i_13_ - DrawingArea.bottomX;
      i_13_ -= i_23_;
      i_19_ += i_23_;
      i_18_ += i_23_;
    }
    if (i_13_ > 0 && i_14_ > 0) {
      createTransparentCharacterPixels(
          DrawingArea.pixels,
          fontPixels[i],
          i_15_,
          i_20_,
          i_17_,
          i_13_,
          i_14_,
          i_18_,
          i_19_,
          i_16_);
    }
  }

  public static void createCharacterPixels(
      int[] is,
      byte[] is_24_,
      int i,
      int i_25_,
      int i_26_,
      int i_27_,
      int i_28_,
      int i_29_,
      int i_30_) {
    int i_31_ = -(i_27_ >> 2);
    i_27_ = -(i_27_ & 0x3);
    for (int i_32_ = -i_28_; i_32_ < 0; i_32_++) {
      for (int i_33_ = i_31_; i_33_ < 0; i_33_++) {
        if (is_24_[i_25_++] != 0) {
          is[i_26_++] = i;
        } else {
          i_26_++;
        }
        if (is_24_[i_25_++] != 0) {
          is[i_26_++] = i;
        } else {
          i_26_++;
        }
        if (is_24_[i_25_++] != 0) {
          is[i_26_++] = i;
        } else {
          i_26_++;
        }
        if (is_24_[i_25_++] != 0) {
          is[i_26_++] = i;
        } else {
          i_26_++;
        }
      }
      for (int i_34_ = i_27_; i_34_ < 0; i_34_++) {
        if (is_24_[i_25_++] != 0) {
          is[i_26_++] = i;
        } else {
          i_26_++;
        }
      }
      i_26_ += i_29_;
      i_25_ += i_30_;
    }
  }

  public void drawCharacter(
      int character, int i_35_, int i_36_, int i_37_, int i_38_, int i_39_, boolean bool) {
    int i_40_ = i_35_ + i_36_ * DrawingArea.width;
    int i_41_ = DrawingArea.width - i_37_;
    int i_42_ = 0;
    int i_43_ = 0;
    if (i_36_ < DrawingArea.topY) {
      int i_44_ = DrawingArea.topY - i_36_;
      i_38_ -= i_44_;
      i_36_ = DrawingArea.topY;
      i_43_ += i_44_ * i_37_;
      i_40_ += i_44_ * DrawingArea.width;
    }
    if (i_36_ + i_38_ > DrawingArea.bottomY) {
      i_38_ -= i_36_ + i_38_ - DrawingArea.bottomY;
    }
    if (i_35_ < DrawingArea.topX) {
      int i_45_ = DrawingArea.topX - i_35_;
      i_37_ -= i_45_;
      i_35_ = DrawingArea.topX;
      i_43_ += i_45_;
      i_40_ += i_45_;
      i_42_ += i_45_;
      i_41_ += i_45_;
    }
    if (i_35_ + i_37_ > DrawingArea.bottomX) {
      int i_46_ = i_35_ + i_37_ - DrawingArea.bottomX;
      i_37_ -= i_46_;
      i_42_ += i_46_;
      i_41_ += i_46_;
    }
    if (i_37_ > 0 && i_38_ > 0) {
      createCharacterPixels(
          DrawingArea.pixels,
          fontPixels[character],
          i_39_,
          i_43_,
          i_40_,
          i_37_,
          i_38_,
          i_41_,
          i_42_);
    }
  }

  static {
    startTransparency = "trans=";
    startStrikethrough = "str=";
    startDefaultShadow = "shad";
    startColor = "col=";
    lineBreak = "br";
    defaultStrikethrough = "str";
    endUnderline = "/u";
    startImage = "img=";
    startClanImage = "clan=";
    startShadow = "shad=";
    startUnderline = "u=";
    endColor = "/col";
    startDefaultUnderline = "u";
    endTransparency = "/trans";
    aRSString_4143 = Integer.toString(100);
    aRSString_4135 = "nbsp";
    aRSString_4169 = "reg";
    aRSString_4165 = "times";
    aRSString_4162 = "shy";
    aRSString_4163 = "copy";
    endEffect = "gt";
    aRSString_4147 = "euro";
    startEffect = "lt";
    defaultTransparency = 256;
    defaultShadow = -1;
    anInt4175 = 0;
    textShadowColor = -1;
    textColor = 0;
    defaultColor = 0;
    strikethroughColor = -1;
    splitTextStrings = new String[100];
    underlineColor = -1;
    anInt4178 = 0;
    transparency = 256;
  }
}
