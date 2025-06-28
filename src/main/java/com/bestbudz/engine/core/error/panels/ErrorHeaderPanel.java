package com.bestbudz.engine.core.error.panels;

import com.bestbudz.engine.core.error.ErrorUtilities;
import java.awt.*;

import static com.bestbudz.engine.core.error.ErrorEnums.*;

/**
 * Header panel for error screen containing icon, title, and description
 */
public class ErrorHeaderPanel {
	private final ErrorUtilities.ErrorFontManager fontManager;
	private ErrorType currentErrorType;
	private final ErrorUtilities.SystemInfo systemInfo;

	public ErrorHeaderPanel(ErrorUtilities.ErrorFontManager fontManager) {
		this.fontManager = fontManager;
		this.currentErrorType = ErrorType.UNKNOWN;
		this.systemInfo = new ErrorUtilities.SystemInfo();
	}

	/**
	 * Render the header panel
	 */
	public void render(Graphics2D g, Rect bounds) {
		// Draw panel background
		ErrorUtilities.drawStyledPanel(g, bounds, null, ERROR_COLOR, fontManager.headerFont);

		// Draw error icon
		int iconSize = 60;
		int iconX = bounds.x + 30;
		int iconY = bounds.y + 30;
		ErrorUtilities.drawErrorIcon(g, iconX, iconY, iconSize);

		// Draw error title
		g.setColor(ERROR_COLOR);
		g.setFont(fontManager.titleFont);
		g.drawString(currentErrorType.title, iconX + iconSize + 20, iconY + 35);

		// Draw error description
		g.setColor(PRIMARY_TEXT);
		g.setFont(fontManager.bodyFont);
		g.drawString(currentErrorType.description, iconX + iconSize + 20, iconY + 60);

		// Draw detailed error information
		drawErrorDetails(g, bounds);

		// Draw system information
		drawSystemInfo(g, bounds);
	}

	/**
	 * Draw detailed error information
	 */
	private void drawErrorDetails(Graphics2D g, Rect bounds) {
		int startY = bounds.y + 140;
		int lineHeight = 18;

		g.setColor(SECONDARY_TEXT);
		g.setFont(fontManager.detailFont);

		// Error type specific details
		g.setColor(PRIMARY_TEXT);
		g.drawString("What might have caused this:", bounds.x + 30, startY);

		g.setColor(SECONDARY_TEXT);
		for (int i = 0; i < currentErrorType.details.length && i < 6; i++) {
			String detail = "• " + currentErrorType.details[i];
			g.drawString(detail, bounds.x + 50, startY + 25 + (i * lineHeight));
		}
	}

	/**
	 * Draw system information
	 */
	private void drawSystemInfo(Graphics2D g, Rect bounds) {
		int startY = bounds.y + bounds.h - 100;

		// System info header
		g.setColor(ACCENT_COLOR);
		g.setFont(fontManager.detailFont);
		g.drawString("System Information:", bounds.x + 30, startY);

		// System details
		g.setColor(SECONDARY_TEXT);
		g.setFont(new Font(fontManager.detailFont.getName(), Font.PLAIN, 10));

		g.drawString("Java: " + systemInfo.javaVersion, bounds.x + 30, startY + 20);
		g.drawString("Memory: " + systemInfo.memoryInfo, bounds.x + 30, startY + 35);
		g.drawString("OS: " + systemInfo.osName, bounds.x + 30, startY + 50);
		g.drawString("Cache: " + systemInfo.cacheStats, bounds.x + 30, startY + 65);
	}

	/**
	 * Update the current error type
	 */
	public void setErrorType(ErrorType errorType) {
		this.currentErrorType = errorType;
	}

	/**
	 * Get current error type
	 */
	public ErrorType getCurrentErrorType() {
		return currentErrorType;
	}

	/**
	 * Get preferred size for this panel
	 */
	public Dimension getPreferredSize() {
		return new Dimension(600, 350);
	}
}