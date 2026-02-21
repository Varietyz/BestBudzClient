package com.bestbudz.engine.core.error;

import com.bestbudz.cache.Signlink;
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

public class ErrorUtilities {

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

				this.titleFont = new Font("Arial", Font.BOLD, 28);
				this.headerFont = new Font("Arial", Font.BOLD, 18);
				this.bodyFont = new Font("Arial", Font.PLAIN, 14);
				this.detailFont = new Font("Arial", Font.PLAIN, 12);
				this.consoleFont = new Font("Monospaced", Font.PLAIN, 11);
				this.buttonFont = new Font("Arial", Font.BOLD, 12);
			}
		}
	}

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

			int panelW = Math.min(700, screenW - 100);
			int panelH = Math.min(450, screenH - 150);

			mainPanel = new Rect(centerX - panelW/2, centerY - panelH/2, panelW, panelH);

			int consolePanelH = 180;
			consolePanel = new Rect(mainPanel.x, mainPanel.y + mainPanel.h + 20,
				mainPanel.w, consolePanelH);

			int actionsPanelW = 200;
			actionsPanel = new Rect(mainPanel.x + mainPanel.w + 20, mainPanel.y,
				actionsPanelW, mainPanel.h);

			actionButtons = calculateButtonPositions();

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

			for (int i = 0; i < actions.length - 2; i++) {
				buttons[i] = new Rect(actionsPanel.x + 10, currentY, btnW, btnH);
				currentY += btnH + btnSpacing;
			}

			return buttons;
		}
	}

	public static void drawStyledButton(Graphics2D g, Rect rect, String text, Color color, boolean hover, Font font) {

		Color buttonColor = hover ? brighter(color) : color;
		Color borderColor = hover ? brighter(brighter(color)) : brighter(color);

		GradientPaint gradient = new GradientPaint(
			rect.x, rect.y, buttonColor,
			rect.x, rect.y + rect.h, darker(buttonColor)
		);
		g.setPaint(gradient);
		g.fillRoundRect(rect.x, rect.y, rect.w, rect.h, 8, 8);

		g.setColor(borderColor);
		g.setStroke(new BasicStroke(hover ? 2f : 1f));
		g.drawRoundRect(rect.x, rect.y, rect.w, rect.h, 8, 8);
		g.setStroke(new BasicStroke(1f));

		g.setColor(PRIMARY_TEXT);
		g.setFont(font);
		FontMetrics fm = g.getFontMetrics();
		int textX = rect.x + (rect.w - fm.stringWidth(text)) / 2;
		int textY = rect.y + (rect.h + fm.getAscent()) / 2 - 2;
		g.drawString(text, textX, textY);
	}

	public static void drawStyledPanel(Graphics2D g, Rect rect, String title, Color accentColor, Font titleFont) {

		GradientPaint gradient = new GradientPaint(
			rect.x, rect.y, CARD_BACKGROUND,
			rect.x, rect.y + rect.h, darker(CARD_BACKGROUND)
		);
		g.setPaint(gradient);
		g.fillRoundRect(rect.x, rect.y, rect.w, rect.h, 12, 12);

		g.setColor(new Color(40, 40, 44));
		g.setStroke(new BasicStroke(2f));
		g.drawRoundRect(rect.x, rect.y, rect.w, rect.h, 12, 12);
		g.setStroke(new BasicStroke(1f));

		if (title != null && !title.isEmpty()) {
			g.setColor(accentColor);
			g.setFont(titleFont);
			g.drawString(title, rect.x + 20, rect.y + 30);
		}
	}

	public static void drawErrorIcon(Graphics2D g, int x, int y, int size) {

		g.setColor(new Color(ERROR_COLOR.getRed(), ERROR_COLOR.getGreen(), ERROR_COLOR.getBlue(), 100));
		g.fillOval(x, y, size, size);

		g.setColor(ERROR_COLOR);
		g.setStroke(new BasicStroke(3f));
		g.drawOval(x, y, size, size);
		g.setStroke(new BasicStroke(1f));

		g.setColor(PRIMARY_TEXT);
		g.setStroke(new BasicStroke(4f));
		int offset = size / 4;
		g.drawLine(x + offset, y + offset, x + size - offset, y + size - offset);
		g.drawLine(x + size - offset, y + offset, x + offset, y + size - offset);
		g.setStroke(new BasicStroke(1f));
	}

	public static String formatTimestamp(long timestamp) {
		return new SimpleDateFormat("HH:mm:ss").format(new Date(timestamp));
	}

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

	public static class SystemInfo {
		public final String javaVersion;
		public final String osName;
		public final String memoryInfo;

		public SystemInfo() {
			this.javaVersion = System.getProperty("java.version");
			this.osName = System.getProperty("os.name");

			Runtime runtime = Runtime.getRuntime();
			long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
			long memoryMax = runtime.maxMemory() / 1024 / 1024;
			this.memoryInfo = memoryUsed + "MB / " + memoryMax + "MB";
		}
	}

	public static class SelfHealingActions {

		public static String deleteCacheFiles() {
			try {
				String cacheDir = Signlink.findCacheDir();
				Path cachePath = Paths.get(cacheDir);

				deleteFileIfExists(cachePath.resolve("settings.json"));
				deleteFileIfExists(cachePath.resolve("accounts.json"));
				deleteFileIfExists(cachePath.resolve("uid.json"));
				deleteFileIfExists(cachePath.resolve("appearance.json"));
				deleteFileIfExists(cachePath.resolve("bubblebudz.json"));

				return "User data files deleted successfully";
			} catch (Exception e) {
				return "Failed to delete user data files: " + e.getMessage();
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

				logs.append("\n=== Console Output ===\n");
				for (ConsoleEntry entry : consoleOutput) {
					logs.append("[").append(formatTimestamp(entry.timestamp)).append("] ");
					logs.append(entry.level.prefix).append(": ").append(entry.message).append("\n");
				}

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
}
