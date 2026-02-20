package com.bestbudz.graphics.text;

import java.awt.*;

public class FontSystem {

	public static TextDrawingArea createModernTextDrawingArea(boolean fancy, int size) {
		String fontName = getFontName(fancy, size);
		int style = getFontStyle(fancy, size);

		TextDrawingArea textArea = new TextDrawingArea();
		textArea.initializeModernFont(fontName, style, size);

		System.out.println("✅ Non-anti-aliased TextDrawingArea created: " + fontName + " " + size);
		return textArea;
	}

	public static TextController createModernRSFont(boolean fancy, int size) {
		String fontName = getFontName(fancy, size);
		int style = getFontStyle(fancy, size);

		TextController textController = new TextController();
		textController.initializeFont(fontName, style, size);
		return textController;
	}

	private static String getFontName(boolean fancy, int size) {
		if (fancy) {

			String[] preferredFonts = {"JetBrains Mono", "Monospaced", "Lucida Console", "Consolas"};
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			String[] availableFonts = ge.getAvailableFontFamilyNames();

			for (String preferred : preferredFonts) {
				for (String available : availableFonts) {
					if (available.equals(preferred)) {
						return preferred;
					}
				}
			}
		}

		return "JetBrains Mono";
	}

	private static int getFontStyle(boolean fancy, int size) {
		if (fancy) {
			return Font.ITALIC;
		} else if (size >= 12) {
			return Font.BOLD;
		} else {
			return Font.PLAIN;
		}
	}

	public static class GameFonts {
		public final TextDrawingArea smallText;
		public final TextDrawingArea regularText;
		public final TextDrawingArea boldText;
		public final TextController newSmallFont;
		public final TextController newRegularFont;
		public final TextController newBoldFont;
		public final TextController newFancyFont;
		public final TextDrawingArea fancyTextArea;

		private GameFonts() {
			System.out.println("🎨 Creating pixel-perfect fonts (NO anti-aliasing)...");

			com.bestbudz.engine.core.gamerender.DrawingArea.initTextRendering();

			smallText = createModernTextDrawingArea(false, 10);
			regularText = createModernTextDrawingArea(false, 11);
			boldText = createModernTextDrawingArea(false, 13);
			newSmallFont = createModernRSFont(false, 10);
			newRegularFont = createModernRSFont(false, 11);
			newBoldFont = createModernRSFont(false, 13);
			newFancyFont = createModernRSFont(true, 12);
			fancyTextArea = createModernTextDrawingArea(true, 12);

			System.out.println("✅ Pixel-perfect fonts created successfully (NO anti-aliasing)!");
		}

		public static GameFonts create() {
			return new GameFonts();
		}

		public boolean validateFonts() {
			boolean allValid = true;

			if (boldText == null || boldText.getFont() == null) {
				System.err.println("❌ boldText validation failed");
				allValid = false;
			}

			if (smallText == null || smallText.getFont() == null) {
				System.err.println("❌ smallText validation failed");
				allValid = false;
			}

			if (regularText == null || regularText.getFont() == null) {
				System.err.println("❌ regularText validation failed");
				allValid = false;
			}

			if (newBoldFont == null) {
				System.err.println("❌ newBoldFont validation failed");
				allValid = false;
			}

			if (allValid) {
				System.out.println("✅ All fonts validated successfully (pixel-perfect, no anti-aliasing)!");
			}

			return allValid;
		}
	}
}
