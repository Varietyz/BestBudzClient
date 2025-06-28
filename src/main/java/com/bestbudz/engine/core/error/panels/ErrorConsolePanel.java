package com.bestbudz.engine.core.error.panels;

import com.bestbudz.engine.core.error.ErrorUtilities;
import static com.bestbudz.ui.handling.input.MouseActions.mouseInRegion;
import java.awt.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.bestbudz.engine.core.error.ErrorEnums.*;

/**
 * Console panel for displaying error logs and diagnostic information
 */
public class ErrorConsolePanel {
	private final ErrorUtilities.ErrorFontManager fontManager;
	private final List<ConsoleEntry> consoleOutput;
	private boolean visible;
	private int scrollOffset;
	private Rect scrollUpButton;
	private Rect scrollDownButton;

	private static final int MAX_CONSOLE_LINES = 100;

	public ErrorConsolePanel(ErrorUtilities.ErrorFontManager fontManager) {
		this.fontManager = fontManager;
		this.consoleOutput = new CopyOnWriteArrayList<>();
		this.visible = false;
		this.scrollOffset = 0;

		// Add initial console messages
		addMessage("Error console initialized", ConsoleLevel.INFO);
		addMessage("Diagnostic mode active", ConsoleLevel.DEBUG);
	}

	/**
	 * Render the console panel
	 */
	public void render(Graphics2D g, Rect bounds) {
		if (!visible) return;

		// Draw panel background
		ErrorUtilities.drawStyledPanel(g, bounds, "Console Output", new Color(100, 100, 100), fontManager.headerFont);

		// Calculate scroll button positions
		calculateScrollButtons(bounds);

		// Draw console content area
		drawConsoleContent(g, bounds);

		// Draw scroll buttons
		drawScrollButtons(g);

		// Draw console stats
		drawConsoleStats(g, bounds);
	}

	/**
	 * Calculate scroll button positions
	 */
	private void calculateScrollButtons(Rect bounds) {
		int scrollBtnSize = 18;
		scrollUpButton = new Rect(
			bounds.x + bounds.w - scrollBtnSize - 10,
			bounds.y + 35,
			scrollBtnSize, scrollBtnSize
		);
		scrollDownButton = new Rect(
			bounds.x + bounds.w - scrollBtnSize - 10,
			bounds.y + bounds.h - scrollBtnSize - 10,
			scrollBtnSize, scrollBtnSize
		);
	}

	/**
	 * Draw console content area
	 */
	private void drawConsoleContent(Graphics2D g, Rect bounds) {
		// Console background
		Rect contentArea = new Rect(bounds.x + 10, bounds.y + 35, bounds.w - 40, bounds.h - 45);
		g.setColor(new Color(18, 18, 20));
		g.fillRoundRect(contentArea.x, contentArea.y, contentArea.w, contentArea.h, 6, 6);

		// Console border
		g.setColor(new Color(40, 40, 44));
		g.drawRoundRect(contentArea.x, contentArea.y, contentArea.w, contentArea.h, 6, 6);

		// Draw console text
		drawConsoleText(g, contentArea);
	}

	/**
	 * Draw console text content
	 */
	private void drawConsoleText(Graphics2D g, Rect contentArea) {
		g.setFont(fontManager.consoleFont);
		FontMetrics fm = g.getFontMetrics();
		int lineHeight = fm.getHeight() + 2;
		int maxLines = (contentArea.h - 20) / lineHeight;

		// Calculate visible range
		int totalLines = consoleOutput.size();
		int startIndex = Math.max(0, totalLines - maxLines - scrollOffset);
		int endIndex = Math.min(totalLines, startIndex + maxLines);

		// Draw visible lines
		int yPos = contentArea.y + 15 + fm.getAscent();
		for (int i = startIndex; i < endIndex; i++) {
			ConsoleEntry entry = consoleOutput.get(i);

			// Format console line
			String timestamp = ErrorUtilities.formatTimestamp(entry.timestamp);
			String prefix = String.format("[%s] %s %s:", timestamp, entry.level.emoji, entry.level.prefix);
			String message = entry.message;

			// Truncate long messages
			int maxWidth = contentArea.w - 20;
			if (fm.stringWidth(prefix + " " + message) > maxWidth) {
				while (fm.stringWidth(prefix + " " + message + "...") > maxWidth && message.length() > 10) {
					message = message.substring(0, message.length() - 1);
				}
				message += "...";
			}

			// Draw timestamp and prefix
			g.setColor(SECONDARY_TEXT);
			g.drawString(prefix, contentArea.x + 10, yPos);

			// Draw message with level color
			g.setColor(entry.level.color);
			int prefixWidth = fm.stringWidth(prefix + " ");
			g.drawString(message, contentArea.x + 10 + prefixWidth, yPos);

			yPos += lineHeight;
		}

		// Draw scroll indicator if needed
		if (scrollOffset > 0 || endIndex < totalLines) {
			drawScrollIndicator(g, contentArea, startIndex, endIndex, totalLines);
		}
	}

	/**
	 * Draw scroll indicator
	 */
	private void drawScrollIndicator(Graphics2D g, Rect contentArea, int startIndex, int endIndex, int totalLines) {
		g.setColor(ACCENT_COLOR);
		g.setFont(new Font(fontManager.detailFont.getName(), Font.PLAIN, 10));

		String indicator = String.format("Lines %d-%d of %d", startIndex + 1, endIndex, totalLines);
		FontMetrics fm = g.getFontMetrics();
		g.drawString(indicator, contentArea.x + contentArea.w - fm.stringWidth(indicator) - 35,
			contentArea.y + contentArea.h - 5);
	}

	/**
	 * Draw scroll buttons
	 */
	private void drawScrollButtons(Graphics2D g) {
		// Scroll up button
		boolean upHover = mouseInRegion(scrollUpButton.x, scrollUpButton.y,
			scrollUpButton.x + scrollUpButton.w, scrollUpButton.y + scrollUpButton.h);
		drawScrollButton(g, scrollUpButton, ActionType.SCROLL_UP.label, upHover);

		// Scroll down button
		boolean downHover = mouseInRegion(scrollDownButton.x, scrollDownButton.y,
			scrollDownButton.x + scrollDownButton.w, scrollDownButton.y + scrollDownButton.h);
		drawScrollButton(g, scrollDownButton, ActionType.SCROLL_DOWN.label, downHover);
	}

	/**
	 * Draw individual scroll button
	 */
	private void drawScrollButton(Graphics2D g, Rect rect, String text, boolean hover) {
		Color buttonColor = hover ? ErrorUtilities.brighter(ACCENT_COLOR) : ACCENT_COLOR;

		g.setColor(buttonColor);
		g.fillRoundRect(rect.x, rect.y, rect.w, rect.h, 4, 4);

		g.setColor(ErrorUtilities.brighter(buttonColor));
		g.drawRoundRect(rect.x, rect.y, rect.w, rect.h, 4, 4);

		g.setColor(PRIMARY_TEXT);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		FontMetrics fm = g.getFontMetrics();
		int textX = rect.x + (rect.w - fm.stringWidth(text)) / 2;
		int textY = rect.y + (rect.h + fm.getAscent()) / 2 - 1;
		g.drawString(text, textX, textY);
	}

	/**
	 * Draw console statistics
	 */
	private void drawConsoleStats(Graphics2D g, Rect bounds) {
		g.setColor(SECONDARY_TEXT);
		g.setFont(new Font(fontManager.detailFont.getName(), Font.PLAIN, 10));

		long errorCount = consoleOutput.stream().mapToLong(e -> e.level == ConsoleLevel.ERROR ? 1 : 0).sum();
		long warningCount = consoleOutput.stream().mapToLong(e -> e.level == ConsoleLevel.WARNING ? 1 : 0).sum();

		String stats = String.format("Total: %d | Errors: %d | Warnings: %d",
			consoleOutput.size(), errorCount, warningCount);
		g.drawString(stats, bounds.x + 15, bounds.y + bounds.h - 8);
	}

	/**
	 * Handle scroll button clicks
	 */
	public boolean handleScrollClick(int x, int y) {
		if (scrollUpButton != null && scrollUpButton.contains(x, y)) {
			scrollUp();
			return true;
		}
		if (scrollDownButton != null && scrollDownButton.contains(x, y)) {
			scrollDown();
			return true;
		}
		return false;
	}

	/**
	 * Scroll console up
	 */
	public void scrollUp() {
		scrollOffset = Math.min(scrollOffset + 1, consoleOutput.size() - 1);
	}

	/**
	 * Scroll console down
	 */
	public void scrollDown() {
		scrollOffset = Math.max(scrollOffset - 1, 0);
	}

	/**
	 * Add message to console
	 */
	public void addMessage(String message, ConsoleLevel level) {
		consoleOutput.add(new ConsoleEntry(message, level));

		// Limit console size
		while (consoleOutput.size() > MAX_CONSOLE_LINES) {
			consoleOutput.remove(0);
		}

		// Auto-scroll to bottom for new messages
		scrollOffset = 0;

		// Also print to system console
		System.out.println(String.format("[%s] %s: %s",
			ErrorUtilities.formatTimestamp(System.currentTimeMillis()),
			level.prefix, message));
	}

	/**
	 * Toggle console visibility
	 */
	public void toggleVisibility() {
		visible = !visible;
		if (visible) {
			scrollOffset = 0; // Reset scroll when showing
		}
	}

	/**
	 * Set console visibility
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
		if (visible) {
			scrollOffset = 0;
		}
	}

	/**
	 * Check if console is visible
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Get all console messages
	 */
	public List<ConsoleEntry> getConsoleOutput() {
		return new java.util.ArrayList<>(consoleOutput);
	}

	/**
	 * Clear console
	 */
	public void clear() {
		consoleOutput.clear();
		scrollOffset = 0;
		addMessage("Console cleared", ConsoleLevel.INFO);
	}

	/**
	 * Get preferred size for this panel
	 */
	public Dimension getPreferredSize() {
		return new Dimension(600, 180);
	}
}