package com.bestbudz.engine.core.error;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.GameLoader;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.bestbudz.engine.core.error.ErrorEnums.*;

/**
 * Utility methods for error screen components
 */
public class ErrorUtilities {

	/**
	 * Font management for error screen
	 */
	public static class ErrorFontManager {
		public Font titleFont;
		public Font headerFont;
		public Font bodyFont;
		public Font detailFont;
		public Font consoleFont;
		public Font buttonFont;

		public ErrorFontManager() {
			try {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				String[] fontNames = ge.getAvailableFontFamilyNames();

				boolean hasSegoe = java.util.Arrays.asList(fontNames).contains("Segoe UI");
				boolean hasSF = java.util.Arrays.asList(fontNames).contains("SF Pro Display");

				String primaryFont = hasSF ? "SF Pro Display" : (hasSegoe ? "Segoe UI" : "Arial");
				String monospaceFont = java.util.Arrays.asList(fontNames).contains("JetBrains Mono") ?
					"JetBrains Mono" : java.util.Arrays.asList(fontNames).contains("Consolas") ?
					"Consolas" : "Monospaced";

				this.titleFont = new Font(primaryFont, Font.BOLD, 28);
				this.headerFont = new Font(primaryFont, Font.BOLD, 18);
				this.bodyFont = new Font(primaryFont, Font.PLAIN, 14);
				this.detailFont = new Font(primaryFont, Font.PLAIN, 12);
				this.consoleFont = new Font(monospaceFont, Font.PLAIN, 11);
				this.buttonFont = new Font(primaryFont, Font.BOLD, 12);

			} catch (Exception e) {
				// Fallback fonts
				this.titleFont = new Font("Arial", Font.BOLD, 28);
				this.headerFont = new Font("Arial", Font.BOLD, 18);
				this.bodyFont = new Font("Arial", Font.PLAIN, 14);
				this.detailFont = new Font("Arial", Font.PLAIN, 12);
				this.consoleFont = new Font("Monospaced", Font.PLAIN, 11);
				this.buttonFont = new Font("Arial", Font.BOLD, 12);
			}
		}
	}

	/**
	 * Layout calculator for responsive design
	 */
	public static class ErrorLayout {
		public final int screenW, screenH, centerX, centerY;
		public final Rect mainPanel, consolePanel, actionsPanel;
		public final Rect[] actionButtons;
		public final Rect consoleScrollUp, consoleScrollDown;

		public ErrorLayout(int screenW, int screenH) {
			this.screenW = screenW;
			this.screenH = screenH;
			this.centerX = screenW / 2;
			this.centerY = screenH / 2;

			// Calculate responsive panel sizes
			int panelW = Math.min(700, screenW - 100);
			int panelH = Math.min(450, screenH - 150);

			// Main error panel - centered
			mainPanel = new Rect(centerX - panelW/2, centerY - panelH/2, panelW, panelH);

			// Console panel - below main panel when visible
			int consolePanelH = 180;
			consolePanel = new Rect(mainPanel.x, mainPanel.y + mainPanel.h + 20,
				mainPanel.w, consolePanelH);

			// Actions panel - to the right of main panel
			int actionsPanelW = 200;
			actionsPanel = new Rect(mainPanel.x + mainPanel.w + 20, mainPanel.y,
				actionsPanelW, mainPanel.h);

			// Calculate button positions
			actionButtons = calculateButtonPositions();

			// Console scroll buttons
			int scrollBtnSize = 18;
			consoleScrollUp = new Rect(
				consolePanel.x + consolePanel.w - scrollBtnSize - 10,
				consolePanel.y + 35, scrollBtnSize, scrollBtnSize);
			consoleScrollDown = new Rect(
				consolePanel.x + consolePanel.w - scrollBtnSize - 10,
				consolePanel.y + consolePanel.h - scrollBtnSize - 10,
				scrollBtnSize, scrollBtnSize);
		}

		private Rect[] calculateButtonPositions() {
			ActionType[] actions = ActionType.values();
			Rect[] buttons = new Rect[actions.length];

			int btnW = 180;
			int btnH = 35;
			int btnSpacing = 10;
			int currentY = actionsPanel.y + 20;

			for (int i = 0; i < actions.length - 2; i++) { // Exclude scroll buttons
				buttons[i] = new Rect(actionsPanel.x + 10, currentY, btnW, btnH);
				currentY += btnH + btnSpacing;
			}

			return buttons;
		}
	}

	/**
	 * Create styled button matching loading screen style
	 */
	public static void drawStyledButton(Graphics2D g, Rect rect, String text, Color color, boolean hover, Font font) {
		// Create hover effect
		Color buttonColor = hover ? brighter(color) : color;
		Color borderColor = hover ? brighter(brighter(color)) : brighter(color);

		// Button gradient background
		GradientPaint gradient = new GradientPaint(
			rect.x, rect.y, buttonColor,
			rect.x, rect.y + rect.h, darker(buttonColor)
		);
		g.setPaint(gradient);
		g.fillRoundRect(rect.x, rect.y, rect.w, rect.h, 8, 8);

		// Border
		g.setColor(borderColor);
		g.setStroke(new BasicStroke(hover ? 2f : 1f));
		g.drawRoundRect(rect.x, rect.y, rect.w, rect.h, 8, 8);
		g.setStroke(new BasicStroke(1f));

		// Text
		g.setColor(PRIMARY_TEXT);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int textX = rect.x + (rect.w - fm.stringWidth(text)) / 2;
		int textY = rect.y + (rect.h + fm.getAscent()) / 2 - 2;
		g.drawString(text, textX, textY);
	}

	/**
	 * Draw panel with loading screen styling
	 */
	public static void drawStyledPanel(Graphics2D g, Rect rect, String title, Color accentColor, Font titleFont) {
		// Panel background with gradient
		GradientPaint gradient = new GradientPaint(
			rect.x, rect.y, CARD_BACKGROUND,
			rect.x, rect.y + rect.h, darker(CARD_BACKGROUND)
		);
		g.setPaint(gradient);
		g.fillRoundRect(rect.x, rect.y, rect.w, rect.h, 12, 12);

		// Border
		g.setColor(new Color(40, 40, 44));
		g.setStroke(new BasicStroke(2f));
		g.drawRoundRect(rect.x, rect.y, rect.w, rect.h, 12, 12);
		g.setStroke(new BasicStroke(1f));

		// Title if provided
		if (title != null && !title.isEmpty()) {
			g.setColor(accentColor);
			g.setFont(titleFont);
			g.drawString(title, rect.x + 20, rect.y + 30);
		}
	}

	/**
	 * Draw error icon matching the new style
	 */
	public static void drawErrorIcon(Graphics2D g, int x, int y, int size) {
		// Error circle background
		g.setColor(new Color(ERROR_COLOR.getRed(), ERROR_COLOR.getGreen(), ERROR_COLOR.getBlue(), 100));
		g.fillOval(x, y, size, size);

		// Error circle border
		g.setColor(ERROR_COLOR);
		g.setStroke(new BasicStroke(3f));
		g.drawOval(x, y, size, size);
		g.setStroke(new BasicStroke(1f));

		// Error symbol (X)
		g.setColor(PRIMARY_TEXT);
		g.setStroke(new BasicStroke(4f));
		int offset = size / 4;
		g.drawLine(x + offset, y + offset, x + size - offset, y + size - offset);
		g.drawLine(x + size - offset, y + offset, x + offset, y + size - offset);
		g.setStroke(new BasicStroke(1f));
	}

	/**
	 * Format timestamp for console entries
	 */
	public static String formatTimestamp(long timestamp) {
		return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
	}

	/**
	 * Style scroll pane to match loading screen
	 */
	public static void styleScrollPane(JScrollPane scrollPane) {
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(60, 60, 64);
				this.trackColor = new Color(30, 30, 32);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroSizeButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroSizeButton();
			}

			private JButton createZeroSizeButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}
		});
	}

	/**
	 * Color utility methods
	 */
	public static Color brighter(Color c) {
		return new Color(
			Math.min(255, c.getRed() + 40),
			Math.min(255, c.getGreen() + 40),
			Math.min(255, c.getBlue() + 40),
			c.getAlpha()
		);
	}

	public static Color darker(Color c) {
		return new Color(
			Math.max(0, c.getRed() - 30),
			Math.max(0, c.getGreen() - 30),
			Math.max(0, c.getBlue() - 30),
			c.getAlpha()
		);
	}

	/**
	 * System information gathering
	 */
	public static class SystemInfo {
		public final String javaVersion;
		public final String osName;
		public final String memoryInfo;
		public final String cacheStats;

		public SystemInfo() {
			this.javaVersion = System.getProperty("java.version");
			this.osName = System.getProperty("os.name");

			Runtime runtime = Runtime.getRuntime();
			long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
			long memoryMax = runtime.maxMemory() / 1024 / 1024;
			this.memoryInfo = memoryUsed + "MB / " + memoryMax + "MB";

			String cacheStatsTemp;
			try {
				cacheStatsTemp = Signlink.getCacheStatistics();
				if (cacheStatsTemp.length() > 60) {
					cacheStatsTemp = cacheStatsTemp.substring(0, 57) + "...";
				}
			} catch (Exception e) {
				cacheStatsTemp = "Stats unavailable";
			}
			this.cacheStats = cacheStatsTemp;
		}
	}

	/**
	 * Self-healing actions implementation
	 */
	public static class SelfHealingActions {

		public static String clearCache() {
			try {
				Signlink.forceCleanup();
				Signlink.clearCacheDir();
				GameLoader.cleanupCaches();
				return "Cache cleared successfully";
			} catch (Exception e) {
				return "Failed to clear cache: " + e.getMessage();
			}
		}

		public static String deleteCacheFiles() {
			try {
				String cacheDir = Signlink.findCacheDir();
				Path cachePath = Paths.get(cacheDir);

				// Delete main cache files
				deleteFileIfExists(cachePath.resolve("main_file_cache.dat"));
				for (int i = 0; i < 6; i++) {
					deleteFileIfExists(cachePath.resolve("main_file_cache.idx" + i));
				}

				// Delete sprite cache
				deleteFileIfExists(cachePath.resolve("sprites.dat"));

				return "Cache files deleted successfully";
			} catch (Exception e) {
				return "Failed to delete cache files: " + e.getMessage();
			}
		}

		public static String copyLogsToClipboard(ErrorType errorType, List<ConsoleEntry> consoleOutput) {
			try {
				StringBuilder logs = new StringBuilder();
				logs.append("=== BestBudz Error Report ===\n");
				logs.append("Error Type: ").append(errorType.title).append("\n");
				logs.append("Description: ").append(errorType.description).append("\n");
				logs.append("Timestamp: ").append(new Date()).append("\n");

				SystemInfo sysInfo = new SystemInfo();
				logs.append("Java Version: ").append(sysInfo.javaVersion).append("\n");
				logs.append("OS: ").append(sysInfo.osName).append("\n");
				logs.append("Memory: ").append(sysInfo.memoryInfo).append("\n");
				logs.append("Cache Stats: ").append(sysInfo.cacheStats).append("\n");

				logs.append("\n=== Console Output ===\n");
				for (ConsoleEntry entry : consoleOutput) {
					logs.append("[").append(formatTimestamp(entry.timestamp)).append("] ");
					logs.append(entry.level.prefix).append(": ").append(entry.message).append("\n");
				}

				// Copy to system clipboard
				StringSelection stringSelection = new StringSelection(logs.toString());
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);

				return "Logs copied to clipboard";
			} catch (Exception e) {
				return "Failed to copy logs: " + e.getMessage();
			}
		}

		private static void deleteFileIfExists(Path file) throws IOException {
			if (Files.exists(file)) {
				Files.delete(file);
			}
		}
	}

	/**
	 * Cache error detection
	 */
	public static boolean isCacheRelatedError() {
		try {
			String cacheDir = Signlink.findCacheDir();
			Path cachePath = Paths.get(cacheDir);

			// Check if main cache files exist and are readable
			if (!Files.exists(cachePath.resolve("main_file_cache.dat"))) {
				return true;
			}

			// Check if any idx files are missing
			for (int i = 0; i < 6; i++) {
				if (!Files.exists(cachePath.resolve("main_file_cache.idx" + i))) {
					return true;
				}
			}

			// Check for corrupted files (basic check)
			try {
				if (Files.size(cachePath.resolve("main_file_cache.dat")) < 100) {
					return true; // File too small, likely corrupted
				}
			} catch (IOException e) {
				return true;
			}

		} catch (Exception e) {
			return true; // If we can't check, assume cache issue
		}

		return false;
	}
}