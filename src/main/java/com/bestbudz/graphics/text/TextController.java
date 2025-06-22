package com.bestbudz.graphics.text;

import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.graphics.sprite.Sprite;
import java.awt.*;

public class TextController extends DrawingArea {

	public static Sprite[] chatImages;
	public static Sprite[] clanImages;

	// Text effect strings
	public static String startColor = "col=";
	public static String endColor = "/col";
	public static String startImage = "img=";
	public static String startClanImage = "clan=";
	public static String startTransparency = "trans=";
	public static String endTransparency = "/trans";
	public static String startStrikethrough = "str=";
	public static String endStrikethrough = "/str";
	public static String startUnderline = "u=";
	public static String endUnderline = "/u";
	public static String startShadow = "shad=";
	public static String endShadow = "/shad";
	public static String lineBreak = "br";

	// Text state
	public static int defaultColor = 0;
	public static int textColor = 0;
	public static int textShadowColor = -1;
	public static int defaultShadow = -1;
	public static int strikethroughColor = -1;
	public static int underlineColor = -1;
	public static int transparency = 256;
	public static int defaultTransparency = 256;

	// Modern fields
	private Font currentFont;
	public int baseCharacterHeight = 0;

	public TextController() {
		// Simple constructor
	}

	public void initializeFont(String fontName, int style, int size) {
		this.currentFont = new Font(fontName, style, size);
		this.baseCharacterHeight = DrawingArea.getTextHeight(currentFont);

		System.out.println("✅ TextController initialized: " + fontName + " " + size + " (height: " + baseCharacterHeight + ")");
	}

	public static String handleOldSyntax(String text) {
		// Convert old @color@ syntax to new <col=color> syntax
		text = text.replaceAll("@gry@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LIME_YELLOW) + ">");
		text = text.replaceAll("@pt2@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CORAL_PEACH) + ">");
		text = text.replaceAll("@pt1@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LIGHT_ORANGE) + ">");
		text = text.replaceAll("@pt7@", "<col=" + Integer.toHexString(ColorConfig.CHAT_BABY_BLUE) + ">");
		text = text.replaceAll("@pt6@", "<col=" + Integer.toHexString(ColorConfig.CHAT_LIGHT_MAGENTA) + ">");
		text = text.replaceAll("@pt3@", "<col=" + Integer.toHexString(ColorConfig.COLOR_BRIGHT_YELLOW) + ">");
		text = text.replaceAll("@pt4@", "<col=" + Integer.toHexString(ColorConfig.CHAT_MINT_AQUA) + ">");
		text = text.replaceAll("@pt5@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SOFT_ROSE) + ">");
		text = text.replaceAll("@red@", "<col=" + Integer.toHexString(ColorConfig.CHAT_SOFT_PINK) + ">");
		text = text.replaceAll("@gre@", "<col=" + Integer.toHexString(ColorConfig.CHAT_BABY_BLUE) + ">");
		text = text.replaceAll("@blu@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LIME_GREEN) + ">");
		text = text.replaceAll("@yel@", "<col=" + Integer.toHexString(ColorConfig.COLOR_PASTEL_VIOLET) + ">");
		text = text.replaceAll("@cya@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SPRING_GREEN) + ">");
		text = text.replaceAll("@mag@", "<col=" + Integer.toHexString(ColorConfig.COLOR_BRIGHT_YELLOW) + ">");
		text = text.replaceAll("@whi@", "<col=" + Integer.toHexString(ColorConfig.COLOR_FROST_TEAL) + ">");
		text = text.replaceAll("@lre@", "<col=" + Integer.toHexString(ColorConfig.COLOR_DUSTY_PERIWINKLE) + ">");
		text = text.replaceAll("@dre@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SUNFLOWER_YELLOW) + ">");
		text = text.replaceAll("@bla@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LIGHT_AMBER) + ">");
		text = text.replaceAll("@or1@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CYAN_TURQUOISE) + ">");
		text = text.replaceAll("@or2@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LAVENDER) + ">");
		text = text.replaceAll("@or3@", "<col=" + Integer.toHexString(ColorConfig.COLOR_ROSE_PINK) + ">");
		text = text.replaceAll("@gr1@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SOFT_PEACH) + ">");
		text = text.replaceAll("@gr2@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CORAL_ORANGE) + ">");
		text = text.replaceAll("@gr3@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CREAM_ROSE) + ">");

		// Uppercase petvariants
		text = text.replaceAll("@RED@", "<col=" + Integer.toHexString(ColorConfig.CHAT_SOFT_PINK) + ">");
		text = text.replaceAll("@GRE@", "<col=" + Integer.toHexString(ColorConfig.CHAT_BABY_BLUE) + ">");
		text = text.replaceAll("@BLU@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LIME_GREEN) + ">");
		text = text.replaceAll("@YEL@", "<col=" + Integer.toHexString(ColorConfig.COLOR_PASTEL_VIOLET) + ">");
		text = text.replaceAll("@CYA@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SPRING_GREEN) + ">");
		text = text.replaceAll("@MAG@", "<col=" + Integer.toHexString(ColorConfig.COLOR_BRIGHT_YELLOW) + ">");
		text = text.replaceAll("@WHI@", "<col=" + Integer.toHexString(ColorConfig.COLOR_FROST_TEAL) + ">");
		text = text.replaceAll("@LRE@", "<col=" + Integer.toHexString(ColorConfig.COLOR_DUSTY_PERIWINKLE) + ">");
		text = text.replaceAll("@DRE@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SUNFLOWER_YELLOW) + ">");
		text = text.replaceAll("@BLA@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LIGHT_AMBER) + ">");
		text = text.replaceAll("@OR1@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CYAN_TURQUOISE) + ">");
		text = text.replaceAll("@OR2@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LAVENDER) + ">");
		text = text.replaceAll("@OR3@", "<col=" + Integer.toHexString(ColorConfig.COLOR_ROSE_PINK) + ">");
		text = text.replaceAll("@GR1@", "<col=" + Integer.toHexString(ColorConfig.COLOR_SOFT_PEACH) + ">");
		text = text.replaceAll("@GR2@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CORAL_ORANGE) + ">");
		text = text.replaceAll("@GR3@", "<col=" + Integer.toHexString(ColorConfig.COLOR_CREAM_ROSE) + ">");

		// Special colors
		text = text.replaceAll("@ric@", "<col=" + Integer.toHexString(ColorConfig.COLOR_WARM_PEACH) + ">");
		text = text.replaceAll("@mbl@", "<col=" + Integer.toHexString(ColorConfig.COLOR_LAVENDER_PINK) + ">");

		// Images
		text = text.replaceAll("@cr1@", "<img=0>");
		text = text.replaceAll("@cr2@", "<img=1>");
		text = text.replaceAll("@cr3@", "<img=2>");
		text = text.replaceAll("@don@", "<img=3>");

		return text;
	}

	/**
	 * Get text width using modern FontMetrics
	 */
	public int getTextWidth(String string) {
		if (string == null || string.isEmpty()) {
			return 0;
		}

		if (currentFont == null) {
			System.err.println("❌ ERROR: currentFont is null in getTextWidth()");
			return string.length() * 8; // Emergency fallback
		}

		string = handleOldSyntax(string);
		int totalWidth = 0;
		boolean inMarkup = false;
		int markupStart = -1;

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch > 255) ch = ' ';

			if (ch == '<' && isSpecial(string, i)) {
				inMarkup = true;
				markupStart = i;
			} else if (ch == '>' && inMarkup && markupStart != -1) {
				String effectString = string.substring(markupStart + 1, i);
				inMarkup = false;
				markupStart = -1;

				if (effectString.startsWith(startImage)) {
					try {
						int imageId = Integer.parseInt(effectString.substring(4));
						if (chatImages != null && imageId >= 0 && imageId < chatImages.length && chatImages[imageId] != null) {
							totalWidth += chatImages[imageId].cropWidth;
						}
					} catch (Exception ignored) {}
				} else if (effectString.startsWith(startClanImage)) {
					try {
						int imageId = Integer.parseInt(effectString.substring(5));
						if (clanImages != null && imageId >= 0 && imageId < clanImages.length && clanImages[imageId] != null) {
							totalWidth += clanImages[imageId].cropWidth;
						}
					} catch (Exception ignored) {}
				}
				continue;
			}

			if (!inMarkup) {
				totalWidth += DrawingArea.getTextWidth(String.valueOf(ch), currentFont);
			}
		}

		return totalWidth;
	}

	/**
	 * Main text drawing method
	 */
	public void drawBasicString(String string, int drawX, int drawY) {
		if (string == null || string.isEmpty()) {
			return;
		}

		if (currentFont == null) {
			System.err.println("❌ ERROR: currentFont is null in drawBasicString()");
			return;
		}

		int adjustedY = drawY - baseCharacterHeight;
		drawProcessedString(string, drawX, adjustedY, textColor, textShadowColor);
	}

	public void drawBasicString(String string, int drawX, int drawY, int color, int shadow) {
		if (string != null) {
			setColorAndShadow(color, shadow);
			drawBasicString(string, drawX - 3, drawY - 2);
		}
	}

	private void drawProcessedString(String string, int x, int y, int color, int shadow) {
		string = handleOldSyntax(string);

		int currentX = x;
		int currentColor = color;
		boolean inMarkup = false;
		int markupStart = -1;

		for (int i = 0; i < string.length(); i++) {
			char ch = string.charAt(i);
			if (ch > 255) ch = ' ';

			if (ch == '<' && isSpecial(string, i)) {
				inMarkup = true;
				markupStart = i;
				continue;
			} else if (ch == '>' && inMarkup && markupStart != -1) {
				String markup = string.substring(markupStart + 1, i);
				inMarkup = false;
				markupStart = -1;

				if (markup.startsWith(startColor)) {
					try {
						String colorStr = markup.substring(4);
						currentColor = colorStr.length() < 6 ? Color.decode(colorStr).getRGB() : Integer.parseInt(colorStr, 16);
					} catch (Exception ignored) {}
				} else if (markup.equals(endColor)) {
					currentColor = color;
				} else if (markup.startsWith(startImage)) {
					try {
						int imageId = Integer.parseInt(markup.substring(4));
						if (chatImages != null && imageId >= 0 && imageId < chatImages.length && chatImages[imageId] != null) {
							chatImages[imageId].drawSprite(currentX, y + baseCharacterHeight);
							currentX += chatImages[imageId].cropWidth;
						}
					} catch (Exception ignored) {}
				} else if (markup.startsWith(startClanImage)) {
					try {
						int imageId = Integer.parseInt(markup.substring(5));
						if (clanImages != null && imageId >= 0 && imageId < clanImages.length && clanImages[imageId] != null) {
							clanImages[imageId].drawSprite(currentX, y + baseCharacterHeight + 2);
							currentX += 11;
						}
					} catch (Exception ignored) {}
				}
				continue;
			}

			if (!inMarkup) {
				if (ch != ' ') {
					// Draw shadow
					if (shadow != -1) {
						DrawingArea.drawText(String.valueOf(ch), currentX + 1, y + baseCharacterHeight + 1, shadow, currentFont);
					}
					// Draw main text
					DrawingArea.drawText(String.valueOf(ch), currentX, y + baseCharacterHeight, currentColor, currentFont);

					// Draw effects
					int charWidth = DrawingArea.getTextWidth(String.valueOf(ch), currentFont);
					if (strikethroughColor != -1) {
						DrawingArea.drawHorizontalLine(currentX, y + (int)(baseCharacterHeight * 0.7), charWidth, strikethroughColor);
					}
					if (underlineColor != -1) {
						DrawingArea.drawHorizontalLine(currentX, y + baseCharacterHeight, charWidth, underlineColor);
					}
				}

				currentX += DrawingArea.getTextWidth(String.valueOf(ch), currentFont);
			}
		}
	}

	public void drawRAString(String string, int drawX, int drawY, int color, int shadow) {
		if (string != null) {
			setColorAndShadow(color, shadow);
			drawBasicString(string, drawX - getTextWidth(string), drawY);
		}
	}

	public void drawCenteredString(String string, int drawX, int drawY, int color, int shadow) {
		if (string != null) {
			setColorAndShadow(color, shadow);
			string = handleOldSyntax(string);
			drawBasicString(string, drawX - getTextWidth(string) / 2, drawY);
		}
	}

	private boolean isSpecial(String string, int currentCharacter) {
		String substring = string.substring(currentCharacter);
		if (substring.length() < 6) {
			return false;
		}
		return substring.startsWith("<col=") || substring.startsWith("</col") ||
			substring.startsWith("<img=") || substring.startsWith("</img") ||
			substring.startsWith("<str=") || substring.startsWith("</str") ||
			substring.startsWith("<clan=");
	}

	public void setColorAndShadow(int color, int shadow) {
		strikethroughColor = -1;
		underlineColor = -1;
		textShadowColor = defaultShadow = shadow;
		textColor = defaultColor = color;
		transparency = defaultTransparency = 256;
	}

	public void setDefaultTextEffectValues(int color, int shadow, int trans) {
		strikethroughColor = -1;
		underlineColor = -1;
		textShadowColor = defaultShadow = shadow;
		textColor = defaultColor = color;
		transparency = defaultTransparency = trans;
	}

	// Getter for font access
	public Font getFont() {
		return currentFont;
	}
}