package com.bestbudz.engine.core;

import static com.bestbudz.engine.config.ColorConfig.*;
import com.bestbudz.cache.Signlink;
import com.bestbudz.graphics.buffer.ImageProducer;
import static com.bestbudz.ui.handling.input.MouseActions.clickInRegion;
import static com.bestbudz.ui.handling.input.MouseActions.mouseInRegion;
import com.bestbudz.ui.handling.input.Keyboard;
import com.bestbudz.ui.handling.input.MouseState;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class LoadingErrorScreen extends Client {

	private static final List<String> consoleOutput = new CopyOnWriteArrayList<>();
	private static final int MAX_CONSOLE_LINES = 100;
	private static boolean consoleVisible = false;
	private static int consoleScrollOffset = 0;

	private static boolean showSelfHealOptions = false;
	private static String lastHealingAction = "";
	private static long lastHealingTime = 0;

	private static ErrorType currentErrorType = ErrorType.UNKNOWN;
	private static String customErrorMessage = "";
	private static Exception lastException = null;

	public enum ErrorType {
		LOADING_ERROR("Loading Error", "Game failed to load properly"),
		GENERIC_ERROR("Generic Error", "Unable to load game"),
		ALREADY_LOADED("Instance Error", "Game already running"),
		CACHE_ERROR("Cache Error", "Cache files corrupted or missing"),
		GRAPHICS_ERROR("Graphics Error", "Failed to initialize graphics"),
		NETWORK_ERROR("Network Error", "Connection or download failed"),
		MEMORY_ERROR("Memory Error", "Insufficient memory available"),
		UNKNOWN("Unknown Error", "An unexpected error occurred");

		final String title;
		final String description;

		ErrorType(String title, String description) {
			this.title = title;
			this.description = description;
		}
	}

	private static class ErrorLayout {
		final int w, h, cx, cy;
		final Rect errorPanel, consolePanel, actionsPanel;
		final Rect toggleConsoleBtn, clearCacheBtn, restartBtn, exitBtn;
		final Rect copyLogsBtn, deleteCacheBtn, forceRestartBtn;
		final Rect consoleScrollUp, consoleScrollDown;

		ErrorLayout(int screenW, int screenH) {
			w = screenW; h = screenH; cx = w/2; cy = h/2;

			int panelW = Math.min(800, w - 100);
			int panelH = Math.min(500, h - 100);
			errorPanel = new Rect(cx - panelW/2, cy - panelH/2, panelW, panelH);

			int consolePanelH = 200;
			consolePanel = new Rect(errorPanel.x, errorPanel.y + errorPanel.h + 20,
				errorPanel.w, consolePanelH);

			int actionsPanelW = 200;
			actionsPanel = new Rect(errorPanel.x + errorPanel.w + 20, errorPanel.y,
				actionsPanelW, errorPanel.h);

			int btnW = 180;
			int btnH = 35;
			int btnSpacing = 45;
			int startY = actionsPanel.y + 20;

			toggleConsoleBtn = new Rect(actionsPanel.x + 10, startY, btnW, btnH);
			clearCacheBtn = new Rect(actionsPanel.x + 10, startY + btnSpacing, btnW, btnH);
			restartBtn = new Rect(actionsPanel.x + 10, startY + btnSpacing * 2, btnW, btnH);
			exitBtn = new Rect(actionsPanel.x + 10, startY + btnSpacing * 3, btnW, btnH);

			copyLogsBtn = new Rect(actionsPanel.x + 10, startY + btnSpacing * 4, btnW, btnH);
			deleteCacheBtn = new Rect(actionsPanel.x + 10, startY + btnSpacing * 5, btnW, btnH);
			forceRestartBtn = new Rect(actionsPanel.x + 10, startY + btnSpacing * 6, btnW, btnH);

			int scrollBtnSize = 20;
			consoleScrollUp = new Rect(consolePanel.x + consolePanel.w - scrollBtnSize - 5,
				consolePanel.y + 5, scrollBtnSize, scrollBtnSize);
			consoleScrollDown = new Rect(consolePanel.x + consolePanel.w - scrollBtnSize - 5,
				consolePanel.y + consolePanel.h - scrollBtnSize - 5,
				scrollBtnSize, scrollBtnSize);
		}
	}

	private static class Rect {
		final int x, y, w, h;
		Rect(int x, int y, int w, int h) { this.x = x; this.y = y; this.w = w; this.h = h; }
	}

	public static void showErrorScreen(Graphics2D g) {
		showErrorScreen(g, null);
	}

	public static void showErrorScreen(Graphics2D g, GameCanvas canvas) {

		analyzeCurrentError();

		int screenW = getScreenWidth(g, canvas);
		int screenH = getScreenHeight(g, canvas);

		initializeErrorBuffer(g, screenW, screenH);

		if (Client.gameScreenBuffer == null) {

			renderErrorScreenDirect(g, screenW, screenH);
			return;
		}

		Graphics2D bufferGraphics = null;
		try {
			bufferGraphics = Client.gameScreenBuffer.getImageGraphics();
			if (bufferGraphics != null) {
				setupErrorRendering(bufferGraphics);
				renderErrorScreen(bufferGraphics, g, screenW, screenH);

				processErrorInput(screenW, screenH);

				Client.gameScreenBuffer.drawGraphics(0, g, 0);
			} else {

				renderErrorScreenDirect(g, screenW, screenH);
				processErrorInput(screenW, screenH);
			}

		} catch (Exception e) {

			renderSimpleErrorFallback(g, screenW, screenH);
			addConsoleMessage("Error screen rendering failed: " + e.getMessage());

		} finally {
			if (bufferGraphics != null) {
				bufferGraphics.dispose();
			}
		}
	}

	private static void initializeErrorBuffer(Graphics2D g, int screenW, int screenH) {
		try {
			if (Client.gameScreenBuffer == null ||
				Client.gameScreenBuffer.canvasWidth != screenW ||
				Client.gameScreenBuffer.canvasHeight != screenH) {
				Client.gameScreenBuffer = new ImageProducer(screenW, screenH);
				Client.gameScreenBuffer.initDrawingArea();
			}
		} catch (Exception e) {
			addConsoleMessage("Failed to initialize error buffer: " + e.getMessage());

		}
	}

	private static void renderErrorScreenDirect(Graphics2D g, int screenW, int screenH) {
		try {
			setupErrorRendering(g);
			renderErrorScreen(g, g, screenW, screenH);
		} catch (Exception e) {
			renderSimpleErrorFallback(g, screenW, screenH);
			addConsoleMessage("Direct error rendering failed: " + e.getMessage());
		}
	}

	private static void renderSimpleErrorFallback(Graphics2D g, int screenW, int screenH) {
		try {

			g.setColor(new Color(20, 20, 30));
			g.fillRect(0, 0, screenW, screenH);

			g.setColor(Color.WHITE);
			g.setFont(new Font("Arial", Font.BOLD, 20));
			String title = "Loading Error";
			FontMetrics fm = g.getFontMetrics();
			g.drawString(title, (screenW - fm.stringWidth(title))/2, screenH/2 - 50);

			g.setFont(new Font("Arial", Font.PLAIN, 14));
			String msg = currentErrorType.description;
			fm = g.getFontMetrics();
			g.drawString(msg, (screenW - fm.stringWidth(msg))/2, screenH/2 - 20);

			g.setFont(new Font("Arial", Font.PLAIN, 12));
			String instruction = "Press F5 to restart or ESC to exit";
			fm = g.getFontMetrics();
			g.drawString(instruction, (screenW - fm.stringWidth(instruction))/2, screenH/2 + 20);

		} catch (Exception e) {

			try {
				g.setColor(new Color(40, 0, 0));
				g.fillRect(0, 0, screenW, screenH);
			} catch (Exception ignored) {

			}
		}
	}

	private static int getScreenWidth(Graphics2D g, GameCanvas canvas) {
		try {
			if (canvas != null && canvas.getWidth() > 0) {
				return canvas.getWidth();
			}
			if (g != null && g.getClipBounds() != null) {
				return g.getClipBounds().width;
			}
			if (Client.frameWidth > 0) {
				return Client.frameWidth;
			}
		} catch (Exception e) {

		}
		return 765;
	}

	private static int getScreenHeight(Graphics2D g, GameCanvas canvas) {
		try {
			if (canvas != null && canvas.getHeight() > 0) {
				return canvas.getHeight();
			}
			if (g != null && g.getClipBounds() != null) {
				return g.getClipBounds().height;
			}
			if (Client.frameHeight > 0) {
				return Client.frameHeight;
			}
		} catch (Exception e) {

		}
		return 503;
	}

	private static void setupErrorRendering(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

	private static void analyzeCurrentError() {
		if (loadingError) {
			currentErrorType = ErrorType.LOADING_ERROR;
		} else if (genericLoadingError) {
			currentErrorType = ErrorType.GENERIC_ERROR;
		} else if (gameAlreadyLoaded) {
			currentErrorType = ErrorType.ALREADY_LOADED;
		} else {
			currentErrorType = ErrorType.UNKNOWN;
		}
	}

	private static void renderErrorScreen(Graphics2D g, Graphics2D originalG, int screenW, int screenH) {
		try {
			ErrorLayout layout = new ErrorLayout(screenW, screenH);

			drawErrorBackground(g, layout);

			drawErrorPanel(g, layout);

			drawActionButtons(g, layout);

			if (consoleVisible) {
				drawConsolePanel(g, layout);
			}

			if (showSelfHealOptions) {
				drawSelfHealingOptions(g, layout);
			}

			drawStatusMessages(g, layout);

		} catch (Exception e) {
			addConsoleMessage("Error in renderErrorScreen: " + e.getMessage());

			renderSimpleErrorFallback(g, screenW, screenH);
		}
	}

	private static void drawErrorBackground(Graphics2D g, ErrorLayout layout) {

		g.setColor(BG_COLOR);
		g.fillRect(0, 0, layout.w, layout.h);

		if (LOGIN_BACKGROUND != null) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
			g.drawImage(LOGIN_BACKGROUND,
				(layout.w - LOGIN_BACKGROUND.getWidth())/2,
				(layout.h - LOGIN_BACKGROUND.getHeight())/2, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}

		g.setColor(new Color(0, 0, 0, 30));
		for (int i = 0; i < layout.w; i += 40) {
			for (int j = 0; j < layout.h; j += 40) {
				g.fillRect(i, j, 1, 1);
			}
		}
	}

	private static void drawErrorPanel(Graphics2D g, ErrorLayout layout) {
		Rect r = layout.errorPanel;

		GradientPaint gradient = new GradientPaint(
			r.x, r.y, PANEL_COLOR,
			r.x, r.y + r.h, new Color(PANEL_COLOR.getRed() - 10,
			PANEL_COLOR.getGreen() - 10,
			PANEL_COLOR.getBlue() - 10,
			PANEL_COLOR.getAlpha())
		);
		g.setPaint(gradient);
		g.fillRoundRect(r.x, r.y, r.w, r.h, 12, 12);

		g.setColor(new Color(220, 80, 80));
		g.setStroke(new BasicStroke(2f));
		g.drawRoundRect(r.x, r.y, r.w, r.h, 12, 12);
		g.setStroke(new BasicStroke(1f));

		drawErrorIcon(g, r.x + 30, r.y + 30);

		g.setColor(new Color(255, 100, 100));
		g.setFont(new Font("Arial", Font.BOLD, 24));
		g.drawString(currentErrorType.title, r.x + 80, r.y + 50);

		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		g.drawString(currentErrorType.description, r.x + 80, r.y + 75);

		drawDetailedErrorInfo(g, r);

		drawSystemInfo(g, r);
	}

	private static void drawErrorIcon(Graphics2D g, int x, int y) {

		g.setColor(new Color(255, 200, 0));
		int[] xPoints = {x + 20, x + 35, x + 5};
		int[] yPoints = {y + 5, y + 35, y + 35};
		g.fillPolygon(xPoints, yPoints, 3);

		g.setColor(new Color(200, 0, 0));
		g.setFont(new Font("Arial", Font.BOLD, 20));
		g.drawString("!", x + 17, y + 30);
	}

	private static void drawDetailedErrorInfo(Graphics2D g, Rect r) {
		int startY = r.y + 120;
		g.setColor(WHITE_DIM_COLOR);
		g.setFont(new Font("Arial", Font.PLAIN, 12));

		List<String> errorLines = getErrorDetails();
		for (int i = 0; i < errorLines.size() && i < 8; i++) {
			g.drawString(errorLines.get(i), r.x + 30, startY + (i * 18));
		}
	}

	private static List<String> getErrorDetails() {
		List<String> details = new ArrayList<>();

		switch (currentErrorType) {
			case LOADING_ERROR:
				details.add("The game failed to load properly due to:");
				details.add("• Corrupted cache files");
				details.add("• Network connectivity issues");
				details.add("• Insufficient system resources");
				details.add("• Java compatibility problems");
				break;

			case CACHE_ERROR:
				details.add("Cache system encountered problems:");
				details.add("• Cache files may be corrupted");
				details.add("• Insufficient disk space");
				details.add("• File permission issues");
				details.add("• Use 'Clear Cache' to resolve");
				break;

			case ALREADY_LOADED:
				details.add("Another instance is running:");
				details.add("• Close all browser windows");
				details.add("• End any Java processes");
				details.add("• Restart your computer if needed");
				break;

			case NETWORK_ERROR:
				details.add("Network connectivity problems:");
				details.add("• Check internet connection");
				details.add("• Verify firewall settings");
				details.add("• Try different network/VPN");
				break;

			default:
				details.add("An unexpected error occurred:");
				details.add("• Try restarting the application");
				details.add("• Clear cache if problem persists");
				details.add("• Check system requirements");
		}

		return details;
	}

	private static void drawSystemInfo(Graphics2D g, Rect r) {
		int startY = r.y + r.h - 80;
		g.setColor(new Color(150, 150, 150));
		g.setFont(new Font("Arial", Font.PLAIN, 10));

		Runtime runtime = Runtime.getRuntime();
		long memoryUsed = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
		long memoryMax = runtime.maxMemory() / 1024 / 1024;

		g.drawString("Java: " + System.getProperty("java.version"), r.x + 30, startY);
		g.drawString("Memory: " + memoryUsed + "MB / " + memoryMax + "MB", r.x + 30, startY + 15);
		g.drawString("OS: " + System.getProperty("os.name"), r.x + 30, startY + 30);

		g.drawString("Data: FlatBuffer (internal)", r.x + 30, startY + 45);
	}

	private static void drawActionButtons(Graphics2D g, ErrorLayout layout) {

		drawPanel(g, layout.actionsPanel, "", new Color(80, 120, 200));

		boolean toggleHover = mouseInRegion(layout.toggleConsoleBtn.x, layout.toggleConsoleBtn.y,
			layout.toggleConsoleBtn.x + layout.toggleConsoleBtn.w,
			layout.toggleConsoleBtn.y + layout.toggleConsoleBtn.h);
		drawButton(g, layout.toggleConsoleBtn, consoleVisible ? "Hide Console" : "Show Console",
			ACCENT_COLOR, toggleHover);

		boolean clearHover = mouseInRegion(layout.clearCacheBtn.x, layout.clearCacheBtn.y,
			layout.clearCacheBtn.x + layout.clearCacheBtn.w,
			layout.clearCacheBtn.y + layout.clearCacheBtn.h);
		drawButton(g, layout.clearCacheBtn, "Clear Cache", new Color(200, 140, 0), clearHover);

		boolean exitHover = mouseInRegion(layout.exitBtn.x, layout.exitBtn.y,
			layout.exitBtn.x + layout.exitBtn.w,
			layout.exitBtn.y + layout.exitBtn.h);
		drawButton(g, layout.exitBtn, "Exit", new Color(180, 60, 60), exitHover);
	}

	private static void drawSelfHealingOptions(Graphics2D g, ErrorLayout layout) {

		boolean copyHover = mouseInRegion(layout.copyLogsBtn.x, layout.copyLogsBtn.y,
			layout.copyLogsBtn.x + layout.copyLogsBtn.w,
			layout.copyLogsBtn.y + layout.copyLogsBtn.h);
		drawButton(g, layout.copyLogsBtn, "Copy Logs", new Color(100, 100, 180), copyHover);

		boolean deleteHover = mouseInRegion(layout.deleteCacheBtn.x, layout.deleteCacheBtn.y,
			layout.deleteCacheBtn.x + layout.deleteCacheBtn.w,
			layout.deleteCacheBtn.y + layout.deleteCacheBtn.h);
		drawButton(g, layout.deleteCacheBtn, "Delete Cache Files", new Color(200, 80, 80), deleteHover);

	}

	private static void drawConsolePanel(Graphics2D g, ErrorLayout layout) {
		Rect r = layout.consolePanel;

		drawPanel(g, r, "Console Output", new Color(100, 100, 100));

		g.setColor(new Color(20, 20, 20));
		g.fillRoundRect(r.x + 10, r.y + 30, r.w - 40, r.h - 40, 4, 4);

		g.setColor(new Color(0, 255, 0));
		g.setFont(new Font("Consolas", Font.PLAIN, 10));

		int lineHeight = 12;
		int maxLines = (r.h - 50) / lineHeight;
		int startIndex = Math.max(0, consoleOutput.size() - maxLines - consoleScrollOffset);
		int endIndex = Math.min(consoleOutput.size(), startIndex + maxLines);

		for (int i = startIndex; i < endIndex; i++) {
			String line = consoleOutput.get(i);
			if (line.length() > 80) {
				line = line.substring(0, 77) + "...";
			}
			g.drawString(line, r.x + 15, r.y + 45 + ((i - startIndex) * lineHeight));
		}

		drawScrollButtons(g, layout);
	}

	private static void drawScrollButtons(Graphics2D g, ErrorLayout layout) {
		boolean upHover = mouseInRegion(layout.consoleScrollUp.x, layout.consoleScrollUp.y,
			layout.consoleScrollUp.x + layout.consoleScrollUp.w,
			layout.consoleScrollUp.y + layout.consoleScrollUp.h);
		drawSmallButton(g, layout.consoleScrollUp, "▲", ACCENT_COLOR, upHover);

		boolean downHover = mouseInRegion(layout.consoleScrollDown.x, layout.consoleScrollDown.y,
			layout.consoleScrollDown.x + layout.consoleScrollDown.w,
			layout.consoleScrollDown.y + layout.consoleScrollDown.h);
		drawSmallButton(g, layout.consoleScrollDown, "▼", ACCENT_COLOR, downHover);
	}

	private static void drawStatusMessages(Graphics2D g, ErrorLayout layout) {
		if (!lastHealingAction.isEmpty() &&
			System.currentTimeMillis() - lastHealingTime < 5000) {

			g.setColor(new Color(0, 200, 0));
			g.setFont(new Font("Arial", Font.BOLD, 12));
			String message = "Action completed: " + lastHealingAction;
			FontMetrics fm = g.getFontMetrics();
			g.drawString(message, layout.cx - fm.stringWidth(message)/2, layout.h - 30);
		}
	}

	private static void processErrorInput(int screenW, int screenH) {
		try {
			if (MouseState.leftClicked) {
				handleErrorInput(screenW, screenH);
				MouseState.leftClicked = false;
			}

			processKeyboardInput();
		} catch (Exception e) {
			addConsoleMessage("Error processing input: " + e.getMessage());
		}
	}

	private static void handleErrorInput(int screenW, int screenH) {
		try {
			ErrorLayout layout = new ErrorLayout(screenW, screenH);

			if (clickInRegion(layout.toggleConsoleBtn.x, layout.toggleConsoleBtn.y,
				layout.toggleConsoleBtn.x + layout.toggleConsoleBtn.w,
				layout.toggleConsoleBtn.y + layout.toggleConsoleBtn.h)) {
				consoleVisible = !consoleVisible;
				consoleScrollOffset = 0;
			}

			else if (clickInRegion(layout.clearCacheBtn.x, layout.clearCacheBtn.y,
				layout.clearCacheBtn.x + layout.clearCacheBtn.w,
				layout.clearCacheBtn.y + layout.clearCacheBtn.h)) {
				performClearCache();
			}

			else if (clickInRegion(layout.exitBtn.x, layout.exitBtn.y,
				layout.exitBtn.x + layout.exitBtn.w,
				layout.exitBtn.y + layout.exitBtn.h)) {
				System.exit(0);
			}

			if (showSelfHealOptions) {
				handleSelfHealingInput(layout);
			}

			if (consoleVisible) {
				handleConsoleScrolling(layout);
			}

		} catch (Exception e) {
			addConsoleMessage("Error handling input: " + e.getMessage());
		}
	}

	private static void handleSelfHealingInput(ErrorLayout layout) {

		if (clickInRegion(layout.copyLogsBtn.x, layout.copyLogsBtn.y,
			layout.copyLogsBtn.x + layout.copyLogsBtn.w,
			layout.copyLogsBtn.y + layout.copyLogsBtn.h)) {
			copyLogsToClipboard();
		}

		else if (clickInRegion(layout.deleteCacheBtn.x, layout.deleteCacheBtn.y,
			layout.deleteCacheBtn.x + layout.deleteCacheBtn.w,
			layout.deleteCacheBtn.y + layout.deleteCacheBtn.h)) {
			performDeleteCacheFiles();
		}

	}

	private static void handleConsoleScrolling(ErrorLayout layout) {

		if (clickInRegion(layout.consoleScrollUp.x, layout.consoleScrollUp.y,
			layout.consoleScrollUp.x + layout.consoleScrollUp.w,
			layout.consoleScrollUp.y + layout.consoleScrollUp.h)) {
			consoleScrollOffset = Math.min(consoleScrollOffset + 1, consoleOutput.size() - 1);
		}

		else if (clickInRegion(layout.consoleScrollDown.x, layout.consoleScrollDown.y,
			layout.consoleScrollDown.x + layout.consoleScrollDown.w,
			layout.consoleScrollDown.y + layout.consoleScrollDown.h)) {
			consoleScrollOffset = Math.max(consoleScrollOffset - 1, 0);
		}
	}

	private static void processKeyboardInput() {
		int key;
		while ((key = Keyboard.readChar(-796)) != -1) {
			switch (key) {
				case KeyEvent.VK_F1:
					consoleVisible = !consoleVisible;
					break;
				case KeyEvent.VK_ESCAPE:
					System.exit(0);
					break;
			}
		}
	}

	private static void performClearCache() {
		try {
			addConsoleMessage("Clearing caches...");
			GameLoader.cleanupCaches();
			lastHealingAction = "Caches cleared successfully";
			lastHealingTime = System.currentTimeMillis();
			addConsoleMessage("Caches cleared successfully");
		} catch (Exception e) {
			addConsoleMessage("Failed to clear caches: " + e.getMessage());
		}
	}

	private static void performDeleteCacheFiles() {
		try {
			addConsoleMessage("Deleting user data files...");

			String cacheDir = Signlink.findCacheDir();
			Path cachePath = Paths.get(cacheDir);

			deleteFileIfExists(cachePath.resolve("settings.json"));
			deleteFileIfExists(cachePath.resolve("accounts.json"));
			deleteFileIfExists(cachePath.resolve("uid.json"));
			deleteFileIfExists(cachePath.resolve("appearance.json"));
			deleteFileIfExists(cachePath.resolve("bubblebudz.json"));

			lastHealingAction = "User data files deleted";
			lastHealingTime = System.currentTimeMillis();
			addConsoleMessage("User data files deleted successfully");

		} catch (Exception e) {
			addConsoleMessage("Failed to delete user data files: " + e.getMessage());
		}
	}

	private static void copyLogsToClipboard() {
		try {
			StringBuilder logs = new StringBuilder();
			logs.append("=== BestBudz Error Report ===\n");
			logs.append("Error Type: ").append(currentErrorType.title).append("\n");
			logs.append("Description: ").append(currentErrorType.description).append("\n");
			logs.append("Timestamp: ").append(new java.util.Date()).append("\n");
			logs.append("Java Version: ").append(System.getProperty("java.version")).append("\n");
			logs.append("OS: ").append(System.getProperty("os.name")).append("\n");

			Runtime runtime = Runtime.getRuntime();
			logs.append("Memory: ").append((runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024)
				.append("MB / ").append(runtime.maxMemory() / 1024 / 1024).append("MB\n");

			logs.append("Data Source: FlatBuffer (internal)\n");

			logs.append("\n=== Console Output ===\n");
			for (String line : consoleOutput) {
				logs.append(line).append("\n");
			}

			java.awt.datatransfer.StringSelection stringSelection =
				new java.awt.datatransfer.StringSelection(logs.toString());
			java.awt.Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(stringSelection, null);

			lastHealingAction = "Logs copied to clipboard";
			lastHealingTime = System.currentTimeMillis();
			addConsoleMessage("Error logs copied to clipboard");

		} catch (Exception e) {
			addConsoleMessage("Failed to copy logs: " + e.getMessage());
		}
	}

	private static void deleteFileIfExists(Path file) {
		try {
			if (Files.exists(file)) {
				Files.delete(file);
				addConsoleMessage("Deleted: " + file.getFileName());
			}
		} catch (IOException e) {
			addConsoleMessage("Failed to delete: " + file.getFileName() + " - " + e.getMessage());
		}
	}

	public static void addConsoleMessage(String message) {
		String timestamp = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
		String formattedMessage = "[" + timestamp + "] " + message;

		consoleOutput.add(formattedMessage);

		while (consoleOutput.size() > MAX_CONSOLE_LINES) {
			consoleOutput.remove(0);
		}

		consoleScrollOffset = 0;

		System.out.println(formattedMessage);
	}

	private static void drawPanel(Graphics2D g, Rect r, String title, Color accentColor) {

		GradientPaint gradient = new GradientPaint(
			r.x, r.y, PANEL_COLOR,
			r.x, r.y + r.h, new Color(PANEL_COLOR.getRed() - 5,
			PANEL_COLOR.getGreen() - 5,
			PANEL_COLOR.getBlue() - 5,
			PANEL_COLOR.getAlpha())
		);
		g.setPaint(gradient);
		g.fillRoundRect(r.x, r.y, r.w, r.h, 8, 8);

		g.setColor(accentColor);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(r.x, r.y, r.w, r.h, 8, 8);
		g.setStroke(new BasicStroke(1f));

		g.setColor(accentColor);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString(title, r.x + 20, r.y + 20);
	}

	private static void drawButton(Graphics2D g, Rect r, String text, Color color, boolean hover) {

		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;
		Color buttonColor = hover ? brighter(color) : color;

		GradientPaint gradient = new GradientPaint(
			drawRect.x, drawRect.y, buttonColor,
			drawRect.x, drawRect.y + drawRect.h, darker(buttonColor)
		);
		g.setPaint(gradient);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		g.setColor(brighter(buttonColor));
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		FontMetrics fm = g.getFontMetrics();
		int tx = drawRect.x + (drawRect.w - fm.stringWidth(text))/2;
		int ty = drawRect.y + (drawRect.h + fm.getAscent())/2 - 1;
		g.drawString(text, tx, ty);
	}

	private static void drawSmallButton(Graphics2D g, Rect r, String text, Color color, boolean hover) {
		Color buttonColor = hover ? brighter(color) : color;

		g.setColor(buttonColor);
		g.fillRoundRect(r.x, r.y, r.w, r.h, 4, 4);

		g.setColor(brighter(buttonColor));
		g.drawRoundRect(r.x, r.y, r.w, r.h, 4, 4);

		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		FontMetrics fm = g.getFontMetrics();
		int tx = r.x + (r.w - fm.stringWidth(text))/2;
		int ty = r.y + (r.h + fm.getAscent())/2 - 1;
		g.drawString(text, tx, ty);
	}

	private static Color brighter(Color c) {
		return new Color(Math.min(255, c.getRed() + 40),
			Math.min(255, c.getGreen() + 40),
			Math.min(255, c.getBlue() + 40));
	}

	private static Color darker(Color c) {
		return new Color(Math.max(0, c.getRed() - 30),
			Math.max(0, c.getGreen() - 30),
			Math.max(0, c.getBlue() - 30));
	}

	static {
		addConsoleMessage("Error Screen initialized");
		addConsoleMessage("Console capture active");
	}
}
