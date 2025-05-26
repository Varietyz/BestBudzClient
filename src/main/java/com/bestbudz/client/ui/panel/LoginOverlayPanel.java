package com.bestbudz.client.ui.panel;

import com.bestbudz.engine.Client;
import com.bestbudz.engine.GameCanvas;
import com.bestbudz.client.util.SpriteUtil;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import com.bestbudz.util.TextClass;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * Modern login overlay panel - clean dock-style implementation
 */
public class LoginOverlayPanel extends JPanel {

	private final Client client;
	private Graphics2D activeGraphics;
	private GameCanvas activeCanvas;

	// State
	private boolean isLoading = false;
	private String loadingMessage = "";
	private long loadingStartTime = 0;
	private BufferedImage logoImage;

	// Layout areas (calculated once, used everywhere)
	private Rectangle panelBounds;
	private Rectangle headerArea;
	private Rectangle contentArea;
	private Rectangle footerArea;
	private Rectangle[] accountRects;
	private Rectangle[] deleteRects;
	private Rectangle clearButtonRect;

	// Interaction state
	private int hoveredAccount = -1;
	private boolean hoveredClearButton = false;
	private int scrollOffset = 0;
	private int maxScroll = 0;

	// Constants
	private static final int PANEL_WIDTH = 300;
	private static final int MIN_PANEL_HEIGHT = 750;
	private static final int MAX_PANEL_HEIGHT = 750; // Fixed maximum height
	private static final int MARGIN = 20; // Distance from screen edges
	private static final int PADDING = 20; // Internal padding
	private static final int LOGO_SIZE = 48;
	private static final int ACCOUNT_HEIGHT = 72;
	private static final int ACCOUNT_SPACING = 12;
	private static final int DELETE_BUTTON_SIZE = 24;
	private static final int SCROLL_SPEED = 25;

	// Header/Footer heights
	private static final int HEADER_HEIGHT = 140;
	private static final int FOOTER_HEIGHT = 60;

	// Colors
	private static final Color BG_OVERLAY = new Color(10, 10, 15, 180);
	private static final Color PANEL_BG = new Color(22, 22, 30, 185);
	private static final Color PANEL_BORDER = new Color(55, 95, 155, 0);
	private static final Color ACCOUNT_BG = new Color(32, 32, 42, 255);
	private static final Color ACCOUNT_BG_HOVER = new Color(42, 52, 67, 200);
	private static final Color TEXT_PRIMARY = new Color(245, 245, 250);
	private static final Color TEXT_SECONDARY = new Color(155, 155, 165);
	private static final Color TEXT_BRAND = new Color(75, 125, 215);
	private static final Color DELETE_COLOR = new Color(185, 65, 65);
	private static final Color DELETE_COLOR_HOVER = new Color(215, 85, 85);
	private static final Color CLEAR_COLOR = new Color(125, 65, 65, 255);
	private static final Color CLEAR_COLOR_HOVER = new Color(155, 85, 85, 180);

	// Fonts
	private static final Font FONT_BRAND = new Font("SansSerif", Font.BOLD, 13);
	private static final Font FONT_TITLE = new Font("SansSerif", Font.BOLD, 15);
	private static final Font FONT_SUBTITLE = new Font("SansSerif", Font.PLAIN, 11);
	private static final Font FONT_USERNAME = new Font("SansSerif", Font.BOLD, 12);
	private static final Font FONT_USES = new Font("SansSerif", Font.PLAIN, 10);
	private static final Font FONT_BUTTON = new Font("SansSerif", Font.PLAIN, 11);

	public LoginOverlayPanel(Client client) {
		this.client = client;
		initialize();
	}

	private void initialize() {
		setOpaque(false);
		setLayout(null);
		loadLogo();
		setupMouse();
		setVisible(!Client.loggedIn);
	}

	private void loadLogo() {
		try {
			ImageIcon icon = SpriteUtil.loadIconScaled("logo.png", LOGO_SIZE);
			Image img = icon.getImage();
			logoImage = new BufferedImage(LOGO_SIZE, LOGO_SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = logoImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(img, 0, 0, null);
			g2d.dispose();
		} catch (Exception e) {
			logoImage = null;
		}
	}

	private void setupMouse() {
		MouseAdapter handler = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!isLoading) handleClick(e.getX(), e.getY());
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				if (!isLoading) updateHover(e.getX(), e.getY());
			}

			@Override
			public void mouseExited(MouseEvent e) {
				resetHover();
			}

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (!isLoading && panelBounds != null && panelBounds.contains(e.getPoint())) {
					scroll(e.getWheelRotation() * SCROLL_SPEED);
				}
			}
		};

		addMouseListener(handler);
		addMouseMotionListener(handler);
		addMouseWheelListener(handler);
	}

	private void calculateLayout() {
		if (getWidth() <= 0 || getHeight() <= 0) return;

		List<AccountData> accounts = getAccounts();

		// Calculate required height for all content
		int accountsHeight = 0;
		if (accounts.size() > 0) {
			accountsHeight = accounts.size() * ACCOUNT_HEIGHT + Math.max(0, accounts.size() - 1) * ACCOUNT_SPACING;
		}
		int requiredHeight = HEADER_HEIGHT + accountsHeight + FOOTER_HEIGHT + PADDING * 2;

		// Use fixed height - don't expand beyond MAX_PANEL_HEIGHT
		int panelHeight = Math.min(MAX_PANEL_HEIGHT, Math.max(MIN_PANEL_HEIGHT, requiredHeight));

		// Center the panel horizontally and vertically
		int panelX = (getWidth() - PANEL_WIDTH) / 2;
		int panelY = (getHeight() - panelHeight) / 2;
		panelBounds = new Rectangle(panelX, panelY, PANEL_WIDTH, panelHeight);

		// Define areas within panel
		headerArea = new Rectangle(
			panelBounds.x + PADDING,
			panelBounds.y + PADDING,
			panelBounds.width - PADDING * 2,
			HEADER_HEIGHT
		);

		footerArea = new Rectangle(
			panelBounds.x + PADDING,
			panelBounds.y + panelBounds.height - FOOTER_HEIGHT - PADDING,
			panelBounds.width - PADDING * 2,
			FOOTER_HEIGHT
		);

		contentArea = new Rectangle(
			headerArea.x,
			headerArea.y + headerArea.height,
			headerArea.width,
			footerArea.y - (headerArea.y + headerArea.height)
		);

		// Calculate scroll limits based on fixed content area height
		int visibleHeight = contentArea.height;
		int totalContentHeight = accountsHeight;
		maxScroll = Math.max(0, totalContentHeight - visibleHeight);
		scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

		// Calculate account rectangles
		accountRects = new Rectangle[accounts.size()];
		deleteRects = new Rectangle[accounts.size()];

		for (int i = 0; i < accounts.size(); i++) {
			int accountY = contentArea.y + i * (ACCOUNT_HEIGHT + ACCOUNT_SPACING) - scrollOffset;

			accountRects[i] = new Rectangle(
				contentArea.x,
				accountY,
				contentArea.width,
				ACCOUNT_HEIGHT
			);

			deleteRects[i] = new Rectangle(
				contentArea.x + contentArea.width - DELETE_BUTTON_SIZE - 12,
				accountY + (ACCOUNT_HEIGHT - DELETE_BUTTON_SIZE) / 2,
				DELETE_BUTTON_SIZE,
				DELETE_BUTTON_SIZE
			);
		}

		// Clear button
		if (!accounts.isEmpty()) {
			clearButtonRect = new Rectangle(
				footerArea.x,
				footerArea.y + 15,
				footerArea.width,
				30
			);
		} else {
			clearButtonRect = null;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (!Client.loggedIn && isVisible()) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			// Recalculate layout
			calculateLayout();

			if (panelBounds != null) {
				// Background overlay
				g2d.setColor(BG_OVERLAY);
				g2d.fillRect(0, 0, getWidth(), getHeight());

				// Panel
				drawPanel(g2d);

				if (isLoading) {
					drawLoading(g2d);
				} else {
					drawContent(g2d);
				}
			}

			g2d.dispose();
		}
	}

	private void drawPanel(Graphics2D g2d) {
		// Background
		g2d.setColor(PANEL_BG);
		g2d.fillRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, 12, 12);

		// Border
		g2d.setColor(PANEL_BORDER);
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.drawRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, 12, 12);
	}

	private void drawContent(Graphics2D g2d) {
		drawHeader(g2d);
		drawAccounts(g2d);
		drawFooter(g2d);
	}

	private void drawHeader(Graphics2D g2d) {
		int centerX = headerArea.x + headerArea.width / 2;
		int y = headerArea.y + 12;

		// Logo
		if (logoImage != null) {
			int logoX = centerX - LOGO_SIZE / 2;
			g2d.drawImage(logoImage, logoX, y, null);
			y += LOGO_SIZE + 12;
		}

		// Brand
		g2d.setFont(FONT_BRAND);
		g2d.setColor(TEXT_BRAND);
		String brand = "BestBudz";
		FontMetrics fm = g2d.getFontMetrics();
		int brandX = centerX - fm.stringWidth(brand) / 2;
		g2d.drawString(brand, brandX, y);
		y += fm.getHeight() + 10;

		// Title
		g2d.setFont(FONT_TITLE);
		g2d.setColor(TEXT_PRIMARY);
		String title = "Account Selection";
		fm = g2d.getFontMetrics();
		int titleX = centerX - fm.stringWidth(title) / 2;
		g2d.drawString(title, titleX, y);
		y += fm.getHeight() + 6;

		// Subtitle
		g2d.setFont(FONT_SUBTITLE);
		g2d.setColor(TEXT_SECONDARY);
		String subtitle = "Click an account to login";
		fm = g2d.getFontMetrics();
		int subtitleX = centerX - fm.stringWidth(subtitle) / 2;
		g2d.drawString(subtitle, subtitleX, y);
	}

	private void drawAccounts(Graphics2D g2d) {
		List<AccountData> accounts = getAccounts();

		if (accounts.isEmpty()) {
			g2d.setFont(FONT_SUBTITLE);
			g2d.setColor(TEXT_SECONDARY);
			String empty = "No saved accounts";
			FontMetrics fm = g2d.getFontMetrics();
			int x = contentArea.x + (contentArea.width - fm.stringWidth(empty)) / 2;
			int y = contentArea.y + contentArea.height / 2;
			g2d.drawString(empty, x, y);
			return;
		}

		// Set clipping to content area
		Shape oldClip = g2d.getClip();
		g2d.clipRect(contentArea.x, contentArea.y, contentArea.width, contentArea.height);

		// Draw accounts
		for (int i = 0; i < accounts.size(); i++) {
			Rectangle accountRect = accountRects[i];

			// Only draw if visible in content area
			if (accountRect.intersects(contentArea)) {
				drawAccount(g2d, accounts.get(i), i, accountRect);
			}
		}

		// Draw scroll indicator
		if (maxScroll > 0) {
			drawScrollbar(g2d);
		}

		g2d.setClip(oldClip);
	}

	private void drawAccount(Graphics2D g2d, AccountData account, int index, Rectangle rect) {
		boolean hovered = hoveredAccount == index;

		// Background
		g2d.setColor(hovered ? ACCOUNT_BG_HOVER : ACCOUNT_BG);
		g2d.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

		// Subtle border
		g2d.setColor(new Color(60, 60, 70, 80));
		g2d.setStroke(new BasicStroke(1f));
		g2d.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

		// Avatar
		int avatarSize = 44;
		int avatarX = rect.x + 16;
		int avatarY = rect.y + (rect.height - avatarSize) / 2;

		g2d.setColor(new Color(55, 55, 65));
		g2d.fillOval(avatarX, avatarY, avatarSize, avatarSize);
		g2d.setColor(new Color(75, 75, 85));
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.drawOval(avatarX, avatarY, avatarSize, avatarSize);

		// Rank badge
		if (account.rank > 1) {
			int badgeSize = 16;
			int badgeX = avatarX + avatarSize - badgeSize + 4;
			int badgeY = avatarY - 4;
			g2d.setColor(getRankColor(account.rank));
			g2d.fillOval(badgeX, badgeY, badgeSize, badgeSize);
			g2d.setColor(Color.WHITE);
			g2d.setStroke(new BasicStroke(1.2f));
			g2d.drawOval(badgeX, badgeY, badgeSize, badgeSize);
		}

		// Text
		int textX = avatarX + avatarSize + 16;
		int nameY = rect.y + 28;
		int usesY = rect.y + 48;

		// Username
		g2d.setFont(FONT_USERNAME);
		g2d.setColor(TEXT_PRIMARY);
		String username = TextClass.capitalize(account.username);
		g2d.drawString(username, textX, nameY);

		// Uses
		g2d.setFont(FONT_USES);
		g2d.setColor(TEXT_SECONDARY);
		g2d.drawString("Uses: " + account.uses, textX, usesY);

		// Delete button
		Rectangle deleteRect = deleteRects[index];
		boolean deleteHovered = hovered && deleteRect.contains(getMousePosition() != null ? getMousePosition() : new Point(-1, -1));

		g2d.setColor(deleteHovered ? DELETE_COLOR_HOVER : DELETE_COLOR);
		g2d.fillRoundRect(deleteRect.x, deleteRect.y, deleteRect.width, deleteRect.height, 6, 6);

		// X symbol
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(2.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		int margin = 7;
		g2d.drawLine(deleteRect.x + margin, deleteRect.y + margin,
			deleteRect.x + deleteRect.width - margin, deleteRect.y + deleteRect.height - margin);
		g2d.drawLine(deleteRect.x + deleteRect.width - margin, deleteRect.y + margin,
			deleteRect.x + margin, deleteRect.y + deleteRect.height - margin);
	}

	private void drawScrollbar(Graphics2D g2d) {
		int trackX = contentArea.x + contentArea.width - 4;
		int trackY = contentArea.y + 4;
		int trackHeight = contentArea.height - 8;

		// Track
		g2d.setColor(new Color(40, 40, 50, 100));
		g2d.fillRoundRect(trackX, trackY, 3, trackHeight, 2, 2);

		// Thumb
		if (maxScroll > 0) {
			int thumbHeight = Math.max(20, trackHeight * contentArea.height / (contentArea.height + maxScroll));
			int thumbY = trackY + (int) ((float) scrollOffset / maxScroll * (trackHeight - thumbHeight));

			g2d.setColor(new Color(100, 100, 120, 150));
			g2d.fillRoundRect(trackX, thumbY, 3, thumbHeight, 2, 2);
		}
	}

	private void drawFooter(Graphics2D g2d) {
		if (clearButtonRect == null) return;

		// Clear button
		g2d.setColor(hoveredClearButton ? CLEAR_COLOR_HOVER : CLEAR_COLOR);
		g2d.fillRoundRect(clearButtonRect.x, clearButtonRect.y, clearButtonRect.width, clearButtonRect.height, 8, 8);

		// Border
		g2d.setColor(hoveredClearButton ? new Color(180, 100, 100, 120) : new Color(140, 80, 80, 80));
		g2d.setStroke(new BasicStroke(1f));
		g2d.drawRoundRect(clearButtonRect.x, clearButtonRect.y, clearButtonRect.width, clearButtonRect.height, 8, 8);

		// Text
		g2d.setFont(FONT_BUTTON);
		g2d.setColor(hoveredClearButton ? new Color(255, 200, 200) : new Color(200, 150, 150));
		String text = "Clear All Accounts";
		FontMetrics fm = g2d.getFontMetrics();
		int textX = clearButtonRect.x + (clearButtonRect.width - fm.stringWidth(text)) / 2;
		int textY = clearButtonRect.y + (clearButtonRect.height + fm.getAscent()) / 2 - 2;
		g2d.drawString(text, textX, textY);
	}

	private void drawLoading(Graphics2D g2d) {
		// Overlay
		g2d.setColor(new Color(15, 15, 25, 240));
		g2d.fillRoundRect(panelBounds.x, panelBounds.y, panelBounds.width, panelBounds.height, 12, 12);

		int centerX = panelBounds.x + panelBounds.width / 2;
		int centerY = panelBounds.y + panelBounds.height / 2;

		// Spinner
		long elapsed = System.currentTimeMillis() - loadingStartTime;
		double angle = Math.toRadians((elapsed / 6.0) % 360);

		g2d.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g2d.setColor(TEXT_BRAND);
		g2d.rotate(angle, centerX, centerY - 10);
		g2d.drawArc(centerX - 18, centerY - 28, 36, 36, 0, 280);
		g2d.rotate(-angle, centerX, centerY - 10);

		// Text
		g2d.setFont(FONT_USERNAME);
		g2d.setColor(TEXT_PRIMARY);
		String text = loadingMessage.isEmpty() ? "Logging in..." : loadingMessage;
		FontMetrics fm = g2d.getFontMetrics();
		int textX = centerX - fm.stringWidth(text) / 2;
		g2d.drawString(text, textX, centerY + 15);
	}

	private void updateHover(int x, int y) {
		if (panelBounds == null || !panelBounds.contains(x, y)) {
			resetHover();
			return;
		}

		int oldAccount = hoveredAccount;
		boolean oldClear = hoveredClearButton;

		hoveredAccount = -1;
		hoveredClearButton = false;

		// Check accounts
		if (accountRects != null) {
			for (int i = 0; i < accountRects.length; i++) {
				if (accountRects[i].contains(x, y) && accountRects[i].intersects(contentArea)) {
					hoveredAccount = i;
					break;
				}
			}
		}

		// Check clear button
		if (hoveredAccount == -1 && clearButtonRect != null && clearButtonRect.contains(x, y)) {
			hoveredClearButton = true;
		}

		if (oldAccount != hoveredAccount || oldClear != hoveredClearButton) {
			repaint();
		}
	}

	private void resetHover() {
		if (hoveredAccount != -1 || hoveredClearButton) {
			hoveredAccount = -1;
			hoveredClearButton = false;
			repaint();
		}
	}

	private void handleClick(int x, int y) {
		if (panelBounds == null || !panelBounds.contains(x, y)) return;

		List<AccountData> accounts = getAccounts();
		if (accounts.isEmpty()) return;

		// Check account clicks
		if (accountRects != null) {
			for (int i = 0; i < accountRects.length; i++) {
				if (accountRects[i].contains(x, y) && accountRects[i].intersects(contentArea)) {
					// Check if clicking delete button
					if (deleteRects[i].contains(x, y)) {
						AccountManager.removeAccount(accounts.get(i));
						repaint();
						return;
					}

					// Login with account
					startLogin(accounts.get(i));
					return;
				}
			}
		}

		// Check clear button
		if (clearButtonRect != null && clearButtonRect.contains(x, y)) {
			AccountManager.clearAccountList();
			repaint();
		}
	}

	private void scroll(int delta) {
		if (maxScroll <= 0) return;

		int oldOffset = scrollOffset;
		scrollOffset = Math.max(0, Math.min(scrollOffset + delta, maxScroll));

		if (oldOffset != scrollOffset) {
			repaint();
		}
	}

	private void startLogin(AccountData account) {
		if (account.username == null || account.username.isEmpty() ||
			account.password == null || account.password.isEmpty()) {
			showError("Account missing credentials");
			return;
		}

		isLoading = true;
		loadingMessage = "Logging in as " + TextClass.capitalize(account.username) + "...";
		loadingStartTime = System.currentTimeMillis();

		Timer loadingTimer = new Timer(50, e -> {
			if (isLoading) {
				repaint();
			} else {
				((Timer) e.getSource()).stop();
			}
		});
		loadingTimer.start();
		repaint();

		// Login using original logic
		Client.myUsername = account.username;
		Client.myPassword = account.password;

		try {
			client.loginFailures = 0;

			if (!Client.myUsername.equals(account.username) || !Client.myPassword.equals(account.password)) {
				Client.myUsername = account.username;
				Client.myPassword = account.password;
			}

			if (activeGraphics != null && activeCanvas != null) {
				client.login(account.username, account.password, false, activeGraphics, activeCanvas);
			} else {
				BufferedImage temp = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
				Graphics2D tempG = temp.createGraphics();
				client.login(account.username, account.password, false, tempG, activeCanvas);
				tempG.dispose();
			}

			if (Client.loggedIn) {
				isLoading = false;
				setVisible(false);
				return;
			}
		} catch (Exception e) {
			isLoading = false;
			showError("Login failed: " + e.getMessage());
		}
	}

	private void showError(String message) {
		loadingMessage = message;
		repaint();

		Timer clearTimer = new Timer(3000, e -> {
			loadingMessage = "";
			isLoading = false;
			repaint();
			((Timer) e.getSource()).stop();
		});
		clearTimer.setRepeats(false);
		clearTimer.start();
	}

	private List<AccountData> getAccounts() {
		List<AccountData> accounts = AccountManager.getAccounts();
		return accounts != null ? accounts : new java.util.ArrayList<>();
	}

	private Color getRankColor(int rank) {
		switch (rank) {
			case 2: return new Color(70, 210, 70);   // Moderator
			case 3: return new Color(230, 210, 70);  // Admin
			case 4: return new Color(210, 130, 230); // Developer
			case 5: return new Color(230, 110, 110); // Owner
			default: return new Color(130, 130, 140);
		}
	}

	// Public API
	public void setGraphicsContext(Graphics2D graphics, GameCanvas canvas) {
		this.activeGraphics = graphics;
		this.activeCanvas = canvas;
	}

	public void refreshLoginState() {
		setVisible(!Client.loggedIn);
		if (isVisible()) {
			repaint();
		}
	}

	public void refreshLogo() {
		loadLogo();
		repaint();
	}

	public void dispose() {
		if (logoImage != null) {
			logoImage.flush();
			logoImage = null;
		}
	}
}