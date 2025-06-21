package com.bestbudz.graphics.text;

import com.bestbudz.engine.config.ColorConfig;
import static com.bestbudz.engine.core.Client.newBoldFont;
import com.bestbudz.engine.core.gamerender.DrawingArea;
import com.bestbudz.network.StreamLoader;
import java.awt.*;

public final class TextDrawingArea extends DrawingArea {

	private Font modernFont;

	public TextDrawingArea() {
		// Modern constructor
	}

	public void initializeModernFont(String fontName, int style, int size) {
		this.modernFont = new Font(fontName, style, size);

		// Initialize text rendering in DrawingArea if needed
		DrawingArea.initTextRendering();

		System.out.println("✅ TextDrawingArea initialized: " + fontName + " " + size);
	}

	// Legacy constructor - shows error for old usage
	public TextDrawingArea(boolean flag, String s, StreamLoader streamLoader) {
		System.err.println("❌ ERROR: Legacy TextDrawingArea constructor called for: " + s);
		throw new RuntimeException("Legacy font loading not supported - use FontSystem.createModernTextDrawingArea()");
	}

	/**
	 * Get text width using modern FontMetrics
	 */
	public int getTextWidth(String text) {
		if (text == null || text.isEmpty()) return 0;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null - font not properly initialized");
			return text.length() * 8; // Emergency fallback
		}

		// Remove color codes for accurate measurement
		String cleanText = removeColorCodes(text);
		return DrawingArea.getTextWidth(cleanText, modernFont);
	}

	public int method384(String text) {
		return getTextWidth(text);
	}

	/**
	 * Main text drawing method - matches original TextController.drawText(color, text, y, x) parameter order
	 */
	public void drawText(int color, String text, int y, int x) {
		if (text == null || text.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in drawText()");
			return;
		}

		// CRITICAL: The original parameter order is (color, text, y, x) not (color, text, x, y)
		// Also need to center the text horizontally using the stored half-width
		String cleanText = removeColorCodes(text);
		int textWidth = DrawingArea.getTextWidth(cleanText, modernFont);
		int centeredX = x - (textWidth / 2); // Center horizontally

		drawColorCodedText(text, centeredX, y, color, true);
	}

	/**
	 * Draw text with color code processing
	 */
	private void drawColorCodedText(String text, int x, int y, int defaultColor, boolean shadow) {
		int currentColor = defaultColor;
		int currentX = x;

		for (int i = 0; i < text.length(); i++) {
			// Handle @color@ codes
			if (text.charAt(i) == '@' && i + 4 < text.length() && text.charAt(i + 4) == '@') {
				String colorCode = text.substring(i + 1, i + 4);
				int newColor = getColorByName(colorCode);
				if (newColor != -1) {
					currentColor = newColor;
				}
				i += 4; // Skip past @color@
				continue;
			}

			// Handle <col=...> codes
			if (text.charAt(i) == '<' && text.substring(i).startsWith("<col=")) {
				int endTag = text.indexOf('>', i);
				if (endTag > i) {
					try {
						String colorStr = text.substring(i + 5, endTag); // Extract color value
						currentColor = colorStr.length() < 6 ?
							Color.decode("#" + colorStr).getRGB() :
							Integer.parseInt(colorStr, 16);
					} catch (Exception ignored) {}
					i = endTag; // Skip to end of tag
					continue;
				}
			}

			// Handle </col> codes
			if (text.charAt(i) == '<' && text.substring(i).startsWith("</col>")) {
				currentColor = defaultColor;
				i += 5; // Skip past </col>
				continue;
			}

			// Draw the character
			char ch = text.charAt(i);
			if (ch != ' ') {
				if (shadow) {
					DrawingArea.drawText(String.valueOf(ch), currentX + 1, y + 1, 0, modernFont);
				}
				DrawingArea.drawText(String.valueOf(ch), currentX, y, currentColor, modernFont);
			}

			// Advance position
			currentX += DrawingArea.getTextWidth(String.valueOf(ch), modernFont);
		}
	}

	/**
	 * Get color by name for @color@ codes
	 */

	/**
	 * Draw text with font height adjustment
	 */
	public void method385(int color, String text, int x, int y) {
		if (text == null || text.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in method385()");
			return;
		}

		int fontHeight = DrawingArea.getTextHeight(modernFont);
		int adjustedY = y - fontHeight;

		// Draw with shadow
		DrawingArea.drawText(text, x + 1, adjustedY + 1, 0, modernFont);
		DrawingArea.drawText(text, x, adjustedY, color, modernFont);
	}

	/**
	 * Wave effect text
	 */
	public void method386(int color, String text, int x, int y, int wave) {
		if (text == null || text.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in method386()");
			return;
		}

		int startX = x - getTextWidth(text) / 2;
		int fontHeight = DrawingArea.getTextHeight(modernFont);
		int baseY = y - fontHeight;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch != ' ') {
				int charX = startX;
				int charY = baseY + (int) (Math.sin((double) i / 2D + (double) wave / 5D) * 5D);

				// Draw char with shadow
				DrawingArea.drawText(String.valueOf(ch), charX + 1, charY + 1, 0, modernFont);
				DrawingArea.drawText(String.valueOf(ch), charX, charY, color, modernFont);
			}

			// Advance by character width
			startX += DrawingArea.getTextWidth(String.valueOf(ch), modernFont);
		}
	}

	/**
	 * Shake effect text
	 */
	public void method387(int x, String text, int waveOffset, int y, int color) {
		if (text == null || text.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in method387()");
			return;
		}

		int startX = x - getTextWidth(text) / 2;
		int fontHeight = DrawingArea.getTextHeight(modernFont);
		int baseY = y - fontHeight;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch != ' ') {
				int charX = startX + (int) (Math.sin((double) i / 5D + (double) waveOffset / 5D) * 5D);
				int charY = baseY + (int) (Math.sin((double) i / 3D + (double) waveOffset / 5D) * 5D);

				// Draw char with shadow
				DrawingArea.drawText(String.valueOf(ch), charX + 1, charY + 1, 0, modernFont);
				DrawingArea.drawText(String.valueOf(ch), charX, charY, color, modernFont);
			}

			// Advance by character width
			startX += DrawingArea.getTextWidth(String.valueOf(ch), modernFont);
		}
	}

	/**
	 * Fade effect text
	 */
	public void method388(int waveAmount, String text, int waveOffset, int y, int x, int color) {
		if (text == null || text.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in method388()");
			return;
		}

		int startX = x - getTextWidth(text) / 2;
		int fontHeight = DrawingArea.getTextHeight(modernFont);
		int baseY = y - fontHeight;

		double decay = 7D - (double) waveAmount / 8D;
		if (decay < 0.0D) decay = 0.0D;

		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch != ' ') {
				int charX = startX;
				int charY = baseY + (int) (Math.sin((double) i / 1.5D + (double) waveOffset) * decay);

				// Draw char with shadow
				DrawingArea.drawText(String.valueOf(ch), charX + 1, charY + 1, 0, modernFont);
				DrawingArea.drawText(String.valueOf(ch), charX, charY, color, modernFont);
			}

			// Advance by character width
			startX += DrawingArea.getTextWidth(String.valueOf(ch), modernFont);
		}
	}

	/**
	 * Remove color codes from text for width calculation
	 */
	private String removeColorCodes(String text) {
		if (text == null) return null;

		StringBuilder result = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) == '@' && i + 4 < text.length() && text.charAt(i + 4) == '@') {
				i += 4; // Skip @color@ codes
			} else if (text.charAt(i) == '<' && text.indexOf('>', i) > i) {
				// Skip <col=...> and other markup
				int endTag = text.indexOf('>', i);
				if (endTag > i) {
					i = endTag; // Skip to end of tag
				}
			} else {
				result.append(text.charAt(i));
			}
		}
		return result.toString();
	}

	public void drawStackText(int color, String text, int y, int x) {
		if (text == null || text.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in drawStackText()");
			return;
		}

		// ========== EASY ADJUSTMENT PARAMETERS ==========
		// Modify these values to fine-tune positioning:

		// Text positioning adjustments
		int textOffsetX = 8;    // Move text left(-) or right(+)
		int textOffsetY = 0;    // Move text up(-) or down(+)

		// Box positioning adjustments (relative to text)
		int boxOffsetX = 1;    // Box position relative to text X
		int boxOffsetY = 7;    // Box position relative to text Y (positive = box below text)

		// Box size adjustments
		int boxPaddingX = 2;    // Extra width (1px each side = 2 total)
		int boxPaddingY = -3;    // Extra height (1px each side = 2 total)

		// Background settings
		int backgroundColor = 0x000000;  // Black (try 0x333333 for gray)
		int backgroundAlpha = 150;       // Transparency (0-255, 160 = ~63%)

		// ================================================

		// Get text dimensions
		int textWidth = getTextWidth(text);
		int textHeight = DrawingArea.getTextHeight(modernFont);

		// Calculate text position with adjustments
		int alignedX = x - textWidth + textOffsetX;
		int adjustedY = y + textOffsetY;

		// Ensure text doesn't go off-screen
		if (alignedX < 0) {
			alignedX = 0;
		}

		// Calculate background box position and size
		int boxX = alignedX + boxOffsetX;
		int boxY = adjustedY - textHeight + boxOffsetY;
		int boxWidth = textWidth + boxPaddingX;
		int boxHeight = textHeight + boxPaddingY;

		// Ensure box doesn't go off-screen
		if (boxX < 0) {
			boxWidth += boxX;
			boxX = 0;
		}

		// Draw semi-transparent background
		DrawingArea.fillRectangle(boxX + 20, boxY - 6, boxWidth, boxHeight, backgroundColor, backgroundAlpha);

		// Draw the text
		DrawingArea.drawText(text, alignedX + 20, adjustedY - 6, color, modernFont);
	}


	/**
	 * Apply wave effect to text
	 */
	private void drawWaveText(String text, int x, int y, int color, int cycle) {
		int currentColor = color;
		int currentX = x;

		for (int i = 0; i < text.length(); i++) {
			// Handle color codes (same as before)
			if (text.charAt(i) == '@' && i + 4 < text.length() && text.charAt(i + 4) == '@') {
				String colorCode = text.substring(i + 1, i + 4);
				int newColor = getColorByName(colorCode);
				if (newColor != -1) {
					currentColor = newColor;
				}
				i += 4;
				continue;
			}

			char ch = text.charAt(i);
			if (ch != ' ') {
				// Wave effect: offset Y based on character position and cycle
				int waveY = y + (int)(Math.sin((cycle + i * 20) * 0.1) * 3);

				// Draw with shadow
				DrawingArea.drawText(String.valueOf(ch), currentX + 1, waveY + 1, 0, modernFont);
				DrawingArea.drawText(String.valueOf(ch), currentX, waveY, currentColor, modernFont);
			}

			currentX += DrawingArea.getTextWidth(String.valueOf(ch), modernFont);
		}
	}

	/**
	 * Apply alpha to a color
	 */
	private int applyAlpha(int color, int alpha) {
		if (alpha >= 255) return color;
		if (alpha <= 0) return 0;

		int r = (color >> 16) & 0xFF;
		int g = (color >> 8) & 0xFF;
		int b = color & 0xFF;

		// Simple alpha blend with black background
		r = (r * alpha) / 255;
		g = (g * alpha) / 255;
		b = (b * alpha) / 255;

		return (r << 16) | (g << 8) | b;
	}
	private int getColorByName(String colorName) {
		switch (colorName) {
			case "mbl": return ColorConfig.COLOR_LAVENDER_PINK;
			case "mye": return ColorConfig.CHAT_MINT_AQUA;
			case "mre": return ColorConfig.CHAT_LIGHT_MAGENTA;
			case "369": return ColorConfig.COLOR_LILAC;
			case "mon": return ColorConfig.COLOR_PEACH_ORANGE;
			case "gry": return ColorConfig.COLOR_LIME_YELLOW;
			case "red": return ColorConfig.CHAT_SOFT_PINK;
			case "gre": return ColorConfig.CHAT_BABY_BLUE;
			case "blu": return ColorConfig.COLOR_LIME_GREEN;
			case "yel": return ColorConfig.COLOR_PASTEL_VIOLET;
			case "cya": return ColorConfig.COLOR_SPRING_GREEN;
			case "mag": return ColorConfig.COLOR_BRIGHT_YELLOW;
			case "whi": return ColorConfig.COLOR_FROST_TEAL;
			case "bla": return ColorConfig.COLOR_LIGHT_AMBER;
			case "lre": return ColorConfig.COLOR_DUSTY_PERIWINKLE;
			case "dre": return ColorConfig.COLOR_SUNFLOWER_YELLOW;
			case "dbl": return ColorConfig.COLOR_MINTY_GREEN;
			case "or1": return ColorConfig.COLOR_CYAN_TURQUOISE;
			case "or2": return ColorConfig.COLOR_LAVENDER;
			case "or3": return ColorConfig.COLOR_ROSE_PINK;
			case "gr1": return ColorConfig.COLOR_SOFT_PEACH;
			case "gr2": return ColorConfig.COLOR_CORAL_ORANGE;
			case "gr3": return ColorConfig.COLOR_CREAM_ROSE;
			default: return -1;
		}
	}

	public void drawRightAlignedString(String s, int x, int y, int color) {
		drawString(s, x - getTextWidth(s), y, color);
	}

	/**
	 * Standard string drawing
	 */
	public void drawString(String s, int x, int y, int color) {
		if (s == null || s.isEmpty()) return;

		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in drawString()");
			return;
		}

		int fontHeight = DrawingArea.getTextHeight(modernFont);
		int adjustedY = y - fontHeight;

		// Draw with shadow
		DrawingArea.drawText(s, x + 1, adjustedY + 1, 0, modernFont);
		DrawingArea.drawText(s, x, adjustedY, color, modernFont);
	}

	public void method389(int textShadow, int x, int textColor, String text, int y) {
		newBoldFont.drawBasicString(text, x, y, textColor, textShadow);
	}

	public void method382(int textColor, int x, String text, int y, int textShadow) {
		newBoldFont.drawBasicString(text, x - text.length(), y, textColor, textShadow);
	}

	// Getter for font access
	public Font getFont() {
		return modernFont;
	}

	/**
	 * Get font height - replaces anInt1497
	 */
	public int getFontHeight() {
		if (modernFont == null) {
			System.err.println("❌ ERROR: modernFont is null in getFontHeight()");
			return 15; // Emergency fallback
		}
		return DrawingArea.getTextHeight(modernFont);
	}

	/**
	 * Legacy compatibility - returns font height
	 */
	public int getAnInt1497() {
		return getFontHeight();
	}
}