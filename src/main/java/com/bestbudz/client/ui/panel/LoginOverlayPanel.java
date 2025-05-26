package com.bestbudz.client.ui.panel;

import com.bestbudz.engine.Client;
import com.bestbudz.client.util.SpriteUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

/**
 * Enhanced login overlay panel with canvas rendering and user-friendly color customization.
 * Covers the entire dock when user is not logged in with minimal transparency.
 */
public class LoginOverlayPanel extends Canvas {

	private boolean wasLoggedIn = true; // Assume logged in initially to avoid flicker
	private BufferedImage logoImage;
	private final int PANEL_WIDTH = 290;
	private final int PANEL_HEIGHT = 240;
	private final int LOGO_SIZE = 80;
	private final int PADDING = 30;
	private final int ELEMENT_SPACING = 25;

	// ===============================================
	// COLOR CUSTOMIZATION - Easy to modify section
	// ===============================================

	// Background overlay color (main screen behind panel)
	private static final Color OVERLAY_BACKGROUND = new Color(60, 63, 65, 205); // Dark blue-gray, 80% opacity

	// Panel colors
	private static final Color PANEL_GRADIENT_TOP = new Color(45, 45, 55, 230);    // Top of panel gradient
	private static final Color PANEL_GRADIENT_BOTTOM = new Color(35, 35, 45, 240); // Bottom of panel gradient
	private static final Color PANEL_BORDER = new Color(80, 120, 200, 180);        // Panel border color
	private static final Color PANEL_HIGHLIGHT = new Color(255, 255, 255, 25);     // Inner panel highlight

	// Panel glow effect
	private static final Color PANEL_GLOW = new Color(100, 150, 255);              // Blue glow around panel
	private static final int GLOW_INTENSITY = 8;                                   // How many glow layers (higher = more glow)

	// Text colors
	private static final Color BRAND_TEXT_COLOR = new Color(100, 150, 255, 200);   // "BestBudz" brand text
	private static final Color TITLE_TEXT_COLOR = new Color(240, 240, 245);        // "Login Required" title
	private static final Color SUBTITLE_TEXT_COLOR = new Color(180, 180, 190);     // Subtitle text
	private static final Color TEXT_SHADOW_COLOR = new Color(0, 0, 0, 100);        // Text shadow color
	private static final Color SUBTITLE_SHADOW_COLOR = new Color(0, 0, 0, 80);     // Subtitle shadow color

	// Font settings
	private static final String FONT_FAMILY = "SansSerif";
	private static final int BRAND_FONT_SIZE = 18;
	private static final int TITLE_FONT_SIZE = 22;
	private static final int SUBTITLE_FONT_SIZE = 15;

	// Text content (easy to modify)
	private static final String BRAND_TEXT = "BestBudz";
	private static final String TITLE_TEXT = "Login Required";
	private static final String SUBTITLE_TEXT = "Please log in to access dock panels";

	// ===============================================
	// END COLOR CUSTOMIZATION SECTION
	// ===============================================

	public LoginOverlayPanel() {
		setupCanvas();
		loadLogo();
		updateVisibility(); // Initial state check
	}

	/**
	 * Sets up the canvas appearance and mouse blocking
	 */
	private void setupCanvas() {
		setVisible(false);
		setBackground(Color.BLACK); // Canvas background (won't show due to custom paint)

		// Block all mouse events when overlay is active
		MouseListener blockingListener = new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) { e.consume(); }
			@Override public void mousePressed(MouseEvent e) { e.consume(); }
			@Override public void mouseReleased(MouseEvent e) { e.consume(); }
			@Override public void mouseEntered(MouseEvent e) { e.consume(); }
			@Override public void mouseExited(MouseEvent e) { e.consume(); }
		};

		MouseMotionListener blockingMotionListener = new MouseMotionListener() {
			@Override public void mouseDragged(MouseEvent e) { e.consume(); }
			@Override public void mouseMoved(MouseEvent e) { e.consume(); }
		};

		addMouseListener(blockingListener);
		addMouseMotionListener(blockingMotionListener);
	}

	/**
	 * Load the logo image from resources using sprite utility
	 */
	private void loadLogo() {
		try {
			ImageIcon logoIcon = SpriteUtil.loadIconScaled("logo.png", LOGO_SIZE);
			Image img = logoIcon.getImage();

			// Convert to BufferedImage
			logoImage = new BufferedImage(LOGO_SIZE, LOGO_SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = logoImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
		} catch (Exception e) {
			System.err.println("Failed to load logo: " + e.getMessage());
			logoImage = null;
		}
	}

	/**
	 * Updates overlay visibility based on current login state.
	 * Called by UIDockHelper.updateToggleInteractivity()
	 */
	private void updateVisibility() {
		boolean loggedIn = Client.loggedIn;

		if (loggedIn != wasLoggedIn) {
			wasLoggedIn = loggedIn;
			setVisible(!loggedIn);

			if (isVisible()) {
				repaint();
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		if (!Client.loggedIn && isVisible()) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Background overlay
			g2d.setColor(OVERLAY_BACKGROUND);
			g2d.fillRect(0, 0, getWidth(), getHeight());

			// Calculate center position for login panel
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;
			int panelX = centerX - (PANEL_WIDTH / 2);
			int panelY = centerY - (PANEL_HEIGHT / 2);

			// Draw login panel background with rounded corners
			drawLoginPanel(g2d, panelX, panelY);

			// Draw logo and text content with proper positioning
			drawLoginContent(g2d, centerX, panelY);

			g2d.dispose();
		}
	}

	/**
	 * Draws the styled login panel background
	 */
	private void drawLoginPanel(Graphics2D g2d, int x, int y) {
		// Outer glow effect
		for (int i = 0; i < GLOW_INTENSITY; i++) {
			int alpha = 15 - (i * 2);
			if (alpha > 0) {
				g2d.setColor(new Color(PANEL_GLOW.getRed(), PANEL_GLOW.getGreen(), PANEL_GLOW.getBlue(), alpha));
				g2d.fillRoundRect(x - i, y - i, PANEL_WIDTH + (i * 2), PANEL_HEIGHT + (i * 2), 20 + i, 20 + i);
			}
		}

		// Main panel background
		GradientPaint gradient = new GradientPaint(
			x, y, PANEL_GRADIENT_TOP,
			x, y + PANEL_HEIGHT, PANEL_GRADIENT_BOTTOM
		);
		g2d.setPaint(gradient);
		g2d.fillRoundRect(x, y, PANEL_WIDTH, PANEL_HEIGHT, 15, 15);

		// Panel border
		g2d.setColor(PANEL_BORDER);
		g2d.setStroke(new BasicStroke(2f));
		g2d.drawRoundRect(x, y, PANEL_WIDTH, PANEL_HEIGHT, 15, 15);

		// Inner highlight
		g2d.setColor(PANEL_HIGHLIGHT);
		g2d.setStroke(new BasicStroke(1f));
		g2d.drawRoundRect(x + 1, y + 1, PANEL_WIDTH - 2, PANEL_HEIGHT - 2, 14, 14);
	}

	/**
	 * Draws the complete login content with optimized spacing
	 */
	private void drawLoginContent(Graphics2D g2d, int centerX, int panelY) {
		int currentY = panelY + PADDING;

		// Logo positioning
		if (logoImage != null) {
			int logoX = centerX - (LOGO_SIZE / 2);
			int logoY = currentY;
			g2d.drawImage(logoImage, logoX, logoY, null);
			currentY += LOGO_SIZE + ELEMENT_SPACING;
		}

		// Brand text below logo
		if (logoImage != null) {
			g2d.setFont(new Font(FONT_FAMILY, Font.BOLD, BRAND_FONT_SIZE));
			FontMetrics brandFm = g2d.getFontMetrics();
			int brandX = centerX - (brandFm.stringWidth(BRAND_TEXT) / 2);

			// Brand text
			g2d.setColor(BRAND_TEXT_COLOR);
			g2d.drawString(BRAND_TEXT, brandX, currentY);
			currentY += brandFm.getHeight() + ELEMENT_SPACING;
		}

		// Main title
		g2d.setFont(new Font(FONT_FAMILY, Font.BOLD, TITLE_FONT_SIZE));
		FontMetrics titleFm = g2d.getFontMetrics();
		int titleX = centerX - (titleFm.stringWidth(TITLE_TEXT) / 2);

		// Text shadow
		g2d.setColor(TEXT_SHADOW_COLOR);
		g2d.drawString(TITLE_TEXT, titleX + 1, currentY + 1);

		// Main text
		g2d.setColor(TITLE_TEXT_COLOR);
		g2d.drawString(TITLE_TEXT, titleX, currentY);
		currentY += titleFm.getHeight() + (ELEMENT_SPACING / 2);

		// Subtitle
		g2d.setFont(new Font(FONT_FAMILY, Font.PLAIN, SUBTITLE_FONT_SIZE));
		FontMetrics subFm = g2d.getFontMetrics();
		int subX = centerX - (subFm.stringWidth(SUBTITLE_TEXT) / 2);

		// Subtitle shadow
		g2d.setColor(SUBTITLE_SHADOW_COLOR);
		g2d.drawString(SUBTITLE_TEXT, subX + 1, currentY + 1);

		// Subtitle text
		g2d.setColor(SUBTITLE_TEXT_COLOR);
		g2d.drawString(SUBTITLE_TEXT, subX, currentY);
	}

	/**
	 * Public method called by UIDockHelper to refresh login state
	 */
	public void refreshLoginState() {
		updateVisibility();
	}

	/**
	 * Refresh the logo if needed
	 */
	public void refreshLogo() {
		loadLogo();
		if (isVisible()) {
			repaint();
		}
	}

	/**
	 * Clean up resources
	 */
	public void dispose() {
		if (logoImage != null) {
			logoImage.flush();
			logoImage = null;
		}
	}
}