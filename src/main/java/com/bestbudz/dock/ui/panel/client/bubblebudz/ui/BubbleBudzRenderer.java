package com.bestbudz.dock.ui.panel.client.bubblebudz.ui;

import com.bestbudz.dock.ui.panel.client.bubblebudz.config.BubbleBudzConfig;
import com.bestbudz.dock.util.SpriteUtil;
import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BubbleBudzRenderer
{
	public static BufferedImage loadLogo() {
		try {
			ImageIcon icon = SpriteUtil.loadIconScaled("loading/bubblebudz.png", BubbleBudzConfig.LOGO_SIZE);
			Image img = icon.getImage();
			BufferedImage logoImage = new BufferedImage(BubbleBudzConfig.LOGO_SIZE, BubbleBudzConfig.LOGO_SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = logoImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
			return logoImage;
		} catch (Exception e) {
			return null;
		}
	}

	public static Rectangle calculateGameArea(int width, int height) {
		if (width <= 0 || height <= 0) return null;

		// Simple full-screen game area with proper margins
		return new Rectangle(
			BubbleBudzConfig.PADDING,
			80, // Leave space for header
			width - BubbleBudzConfig.PADDING * 2,
			height - 160 // Leave space for header and footer
		);
	}

	public static void drawHeader(Graphics2D g2d, int width, BufferedImage logoImage,
								  long roundStartTime, int currentScore, int bestScore) {
		int centerX = width / 2;
		int y = 25;

		// Logo and title
		if (logoImage != null) {
			g2d.drawImage(logoImage, BubbleBudzConfig.PADDING + 15, y + 5, null);
		}

		g2d.setFont(BubbleBudzStyle.FONT_BRAND);
		g2d.setColor(BubbleBudzStyle.TEXT_NEON_GREEN);
		g2d.drawString("BUBBLE BUDZ", BubbleBudzConfig.PADDING + 10, y);

		// Timer (top right)
		long roundElapsed = System.currentTimeMillis() - roundStartTime;
		int timeLeft = Math.max(0, (int)((BubbleBudzConfig.ROUND_DURATION - roundElapsed) / 1000));

		g2d.setFont(BubbleBudzStyle.FONT_TIMER);
		g2d.setColor(timeLeft > 5 ? BubbleBudzStyle.TEXT_NEON_GREEN : BubbleBudzStyle.TEXT_NEON_PINK);
		String timerText = "Time: " + timeLeft + "s";
		FontMetrics fm = g2d.getFontMetrics();
		g2d.drawString(timerText, width - BubbleBudzConfig.PADDING - 20 - fm.stringWidth(timerText), y + 15);

		// Current Score (center top)
		g2d.setFont(BubbleBudzStyle.FONT_SCORE);
		String scoreText = "Score: " + currentScore;
		fm = g2d.getFontMetrics();
		int scoreX = centerX - fm.stringWidth(scoreText) / 2;

		// Glow effect
		g2d.setColor(new Color(BubbleBudzStyle.SCORE_GLOW.getRed(), BubbleBudzStyle.SCORE_GLOW.getGreen(), BubbleBudzStyle.SCORE_GLOW.getBlue(), 150));
		for (int i = 1; i <= 2; i++) {
			g2d.drawString(scoreText, scoreX + i, y + 45 + i);
		}
		g2d.setColor(BubbleBudzStyle.SCORE_GLOW);
		g2d.drawString(scoreText, scoreX, y + 45);

		// Best Score (under current score)
		if (bestScore > 0) {
			g2d.setFont(BubbleBudzStyle.FONT_BEST);
			g2d.setColor(BubbleBudzStyle.TEXT_SECONDARY);
			String bestText = "Best: " + bestScore;
			fm = g2d.getFontMetrics();
			int bestX = centerX - fm.stringWidth(bestText) / 2;
			g2d.drawString(bestText, bestX, y + 65);
		}
	}

	public static void drawFooter(Graphics2D g2d, int width, int height) {
		int centerX = width / 2;
		int footerStartY = height - 45; // Start higher to fit within bounds
		int y = footerStartY;

		// Calculate viewport bonus for instructions
		float heightMultiplier = Math.max(1.0f, (float)height / BubbleBudzConfig.BASE_VIEWPORT_HEIGHT);

		// Instructions - keep it simple and short for narrow viewports
		g2d.setFont(BubbleBudzStyle.FONT_INSTRUCTION);
		g2d.setColor(BubbleBudzStyle.TEXT_PRIMARY);

		String instruction1 = "Click small bubbles for max points";
		String instruction2 = heightMultiplier > 1.1f ?
			String.format("Viewport bonus: +%.0f%% • 30s rounds", (heightMultiplier - 1.0f) * 50) :
			"30-second rounds • Persistent scores";

		FontMetrics fm = g2d.getFontMetrics();

		// First instruction line
		int x1 = centerX - fm.stringWidth(instruction1) / 2;
		x1 = Math.max(BubbleBudzConfig.PADDING, Math.min(x1, width - BubbleBudzConfig.PADDING - fm.stringWidth(instruction1)));
		g2d.drawString(instruction1, x1, y);
		y += fm.getHeight() + 2;

		// Second instruction line - check width and truncate if needed
		if (fm.stringWidth(instruction2) > width - BubbleBudzConfig.PADDING * 2) {
			instruction2 = heightMultiplier > 1.1f ?
				String.format("+%.0f%% bonus • 30s rounds", (heightMultiplier - 1.0f) * 50) :
				"30s rounds";
		}

		int x2 = centerX - fm.stringWidth(instruction2) / 2;
		x2 = Math.max(BubbleBudzConfig.PADDING, Math.min(x2, width - BubbleBudzConfig.PADDING - fm.stringWidth(instruction2)));

		// Only draw if we have space
		if (y < height - 20) {
			g2d.drawString(instruction2, x2, y);
			y += fm.getHeight() + 3;
		}

		// Game info - only if we have space and use smaller font
		if (y < height - 12) {
			g2d.setFont(new Font("SansSerif", Font.PLAIN, 9)); // Smaller font
			g2d.setColor(BubbleBudzStyle.TEXT_SECONDARY);
			String gameInfo = "Bubble Budz Mini-Game";
			fm = g2d.getFontMetrics();

			// Check if game info fits
			if (fm.stringWidth(gameInfo) <= width - BubbleBudzConfig.PADDING * 2) {
				int infoX = centerX - fm.stringWidth(gameInfo) / 2;
				infoX = Math.max(BubbleBudzConfig.PADDING, Math.min(infoX, width - BubbleBudzConfig.PADDING - fm.stringWidth(gameInfo)));
				g2d.drawString(gameInfo, infoX, y);
			}
		}
	}

	public static void drawLoading(Graphics2D g2d, int width, int height, String loadingMessage, long loadingStartTime) {
		g2d.setColor(new Color(5, 5, 15, 250));
		g2d.fillRect(0, 0, width, height);

		int centerX = width / 2;
		int centerY = height / 2;

		// Neon spinner
		long elapsed = System.currentTimeMillis() - loadingStartTime;
		double angle = Math.toRadians((elapsed / 8.0) % 360);

		g2d.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(BubbleBudzStyle.TEXT_NEON_CYAN);
		g2d.rotate(angle, centerX, centerY - 10);
		g2d.drawArc(centerX - 25, centerY - 35, 50, 50, 0, 270);
		g2d.rotate(-angle, centerX, centerY - 10);

		// Loading text
		g2d.setFont(BubbleBudzStyle.FONT_LOADING);
		String text = loadingMessage.isEmpty() ? "Loading..." : loadingMessage;
		FontMetrics fm = g2d.getFontMetrics();
		int textX = centerX - fm.stringWidth(text) / 2;
		g2d.setColor(BubbleBudzStyle.TEXT_PRIMARY);
		g2d.drawString(text, textX, centerY + 30);
	}
}