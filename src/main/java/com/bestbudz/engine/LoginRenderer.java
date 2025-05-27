package com.bestbudz.engine;

import static com.bestbudz.engine.Client.LOGIN_BACKGROUND;
import com.bestbudz.config.ClientConstants;
import com.bestbudz.config.Configuration;
import com.bestbudz.config.SettingHandler;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import static com.bestbudz.engine.Client.frameWidth;
import com.bestbudz.engine.input.Keyboard;
import com.bestbudz.engine.input.MouseState;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.util.TextClass;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

public class LoginRenderer {
	private final Client client;
	private final World[] worlds = {
		new World(1, "BestBudz", "Main world"),
		new World(2, "Pointless", "Economy-free"),
		new World(3, "Development", "Testing"),
		new World(4, "Staff World", "Staff-only")
	};
	private int worldHover = -1;

	// Three-region layout: Top login strip, Left world panel, Bottom accounts
	private static class Layout {
		final int w, h, cx, cy;
		final Rect worldPanel, accountPanel;
		final Rect usernameField, passwordField, loginBtn, rememberBox;
		final Rect[] worldBtns, accountCards;
		final Rect clearBtn;

		Layout(int screenW, int screenH) {
			w = screenW; h = screenH; cx = w/2; cy = h/2;

			// TOP REGION - Login strip across top of screen with extra padding
			int topMargin = 30; // Increased padding from top
			int fieldH = 30;
			int fieldW = 200;
			int fieldGap = 20;
			int btnW = 100;
			int btnH = 26; // More compact login button height
			int checkboxSize = 13;

			// Position login elements horizontally inline
			int totalLoginWidth = fieldW + fieldGap + fieldW + fieldGap + btnW + 20; // Extra space for remember/login block
			int loginStartX = (w - totalLoginWidth) / 2;

			usernameField = new Rect(loginStartX, topMargin, fieldW, fieldH);
			passwordField = new Rect(loginStartX + fieldW + fieldGap, topMargin, fieldW, fieldH);

			// Remember checkbox and login button positioned to the right of password field, inline
			int rightBlockX = loginStartX + fieldW + fieldGap + fieldW + fieldGap;
			rememberBox = new Rect(rightBlockX, topMargin - 16, checkboxSize, checkboxSize); // Slightly below field top
			loginBtn = new Rect(rightBlockX, topMargin + 4, btnW, btnH); // Below remember checkbox

			// LEFT REGION - World selector panel centered on left side, touching left border
			int worldPanelX = - 15; // Touch the left frame border
			int worldPanelW = 200; // Slightly wider to accommodate border positioning
			int worldPanelH = 200;
			int worldPanelY = cy - worldPanelH / 2; // Center vertically on left side
			worldPanel = new Rect(worldPanelX, worldPanelY, worldPanelW, worldPanelH);

			// World buttons positioned directly without panel container
			worldBtns = new Rect[4];
			int worldBtnY = worldPanelY + 15;
			int worldBtnH = 35;
			int worldBtnSpacing = 45;

			for (int i = 0; i < 4; i++) {
				worldBtns[i] = new Rect(worldPanelX + 10, worldBtnY + i * worldBtnSpacing, worldPanelW - 20, worldBtnH); // Maintain internal padding
			}

			// BOTTOM REGION - Account panel spanning full width at bottom
			int bottomMargin = 20;
			int accPanelH = 100;
			int accPanelW = w - 40; // Full width minus margins
			int accPanelX = 20;
			int accPanelY = h - accPanelH - bottomMargin;
			accountPanel = new Rect(accPanelX, accPanelY, accPanelW, accPanelH);

			// Account cards in horizontal layout
			accountCards = new Rect[8];
			int cardW = 120;
			int cardH = 50;
			int cardSpacing = 20;
			int cardsStartX = accountPanel.x + 20;
			int cardsStartY = accountPanel.y + 35;

			for (int i = 0; i < 8; i++) {
				int row = i / 4;
				int col = i % 4;
				accountCards[i] = new Rect(
					cardsStartX + col * (cardW + cardSpacing),
					cardsStartY + row * (cardH + 10),
					cardW,
					cardH
				);
			}

			// Clear button in top-right of account panel
			clearBtn = new Rect(accountPanel.x + accountPanel.w - 90, accountPanel.y + 8, 78, 26);
		}
	}

	// Enhanced color scheme matching the green/gold/blue background
	private static final Color BG = new Color(15, 25, 20);
	private static final Color PANEL = new Color(25, 40, 35, 220);
	private static final Color BORDER = new Color(120, 140, 100);
	private static final Color ACCENT = new Color(80, 180, 120);
	private static final Color GOLD_ACCENT = new Color(220, 180, 80);
	private static final Color BLUE_ACCENT = new Color(80, 140, 200);
	private static final Color TEXT = new Color(240, 250, 245);
	private static final Color TEXT_DIM = new Color(180, 200, 185);
	private static final Color TEXT_BLACK = new Color(0, 0, 0);
	private static final Color INPUT_BG = new Color(35, 50, 40);
	private static final Color INPUT_HOVER = new Color(45, 65, 50);
	private static final Color HOVER = new Color(45, 70, 55);
	private static final Color SELECTED = new Color(60, 120, 80, 220);

	public LoginRenderer(Client client) {
		this.client = client;
	}

	public void displayLoginScreen(Graphics2D g, GameCanvas canvas) {
		Layout l = new Layout(canvas.getWidth(), canvas.getHeight());
		initBuffer();
		Graphics2D og = Client.aRSImageProducer_1109.getImageGraphics();

		try {
			setupRendering(og);
			render(og, l);
			Client.aRSImageProducer_1109.drawGraphics(0, g, 0);
		} finally {
			og.dispose();
		}
	}

	private void render(Graphics2D g, Layout l) {
		drawBackground(g, l);
		drawTopLoginStrip(g, l);
		drawLeftWorldSelector(g, l);
		drawBottomAccountPanel(g, l);
		drawStatus(g, l);
	}

	private void drawBackground(Graphics2D g, Layout l) {
		g.setColor(BG);
		g.fillRect(0, 0, l.w, l.h);

		if (LOGIN_BACKGROUND != null) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
			g.drawImage(LOGIN_BACKGROUND, (l.w - LOGIN_BACKGROUND.getWidth())/2,
				(l.h - LOGIN_BACKGROUND.getHeight())/2, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	}

	private void drawTopLoginStrip(Graphics2D g, Layout l) {
		// Draw login fields without panel container - just the elements

		// Username field
		boolean userFocus = client.loginScreenCursorPos == 0;
		boolean userHover = mouseIn(g, l.usernameField);
		drawLabel(g, l.usernameField.x, l.usernameField.y - 8, "Stonername");
		drawInputField(g, l.usernameField,
			TextClass.capitalize(Client.myUsername) + getCursor(0), userFocus, userHover);

		// Password field
		boolean passFocus = client.loginScreenCursorPos == 1;
		boolean passHover = mouseIn(g, l.passwordField);
		drawLabel(g, l.passwordField.x, l.passwordField.y - 8, "Puff Puff Password");
		drawInputField(g, l.passwordField,
			getPasswordDisplay() + getCursor(1), passFocus, passHover);

		// Remember checkbox
		boolean checkboxHover = mouseIn(g, l.rememberBox);
		drawCheckbox(g, l.rememberBox, "Early Dementia?", Client.rememberMe, checkboxHover);

		// Login button
		boolean loginHover = mouseIn(g, l.loginBtn);
		drawButton(g, l.loginBtn, "GET HIGH", ACCENT, loginHover);
	}

	private void drawLeftWorldSelector(Graphics2D g, Layout l) {
		// Draw world buttons
		for (int i = 0; i < worlds.length; i++) {
			World w = worlds[i];
			boolean selected = ClientConstants.worldSelected == w.id;
			boolean hovered = worldHover == w.id;
			drawWorldBtn(g, l.worldBtns[i], w, selected, hovered);
		}
	}

	private void drawBottomAccountPanel(Graphics2D g, Layout l) {
		if (AccountManager.accounts == null || AccountManager.getAccounts().isEmpty()) return;

		// Draw full-width account panel at bottom
		drawPanel(g, l.accountPanel, "Demented Stoners", ACCENT);

		int count = Math.min(8, AccountManager.getAccounts().size());
		for (int i = 0; i < count; i++) {
			drawAccountCard(g, l.accountCards[i], AccountManager.getAccounts().get(i));
		}

		boolean clearHover = mouseIn(g, l.clearBtn);
		drawButton(g, l.clearBtn, "Clean", new Color(220, 80, 80), clearHover);
	}

	private void drawPanel(Graphics2D g, Rect r, String title, Color accentColor) {
		// Panel background with gradient
		GradientPaint gradient = new GradientPaint(
			r.x, r.y, PANEL,
			r.x, r.y + r.h, new Color(PANEL.getRed() - 5, PANEL.getGreen() - 5, PANEL.getBlue() - 5, PANEL.getAlpha())
		);
		g.setPaint(gradient);
		g.fillRoundRect(r.x, r.y, r.w, r.h, 8, 8);

		// Border with accent color
		g.setColor(accentColor);
		g.setStroke(new BasicStroke(1.5f));
		g.drawRoundRect(r.x, r.y, r.w, r.h, 8, 8);
		g.setStroke(new BasicStroke(1f));

		// Title
		g.setColor(accentColor);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString(title, r.x + 20, r.y + 20);
	}

	private void drawLabel(Graphics2D g, int x, int y, String text) {
		g.setColor(TEXT_DIM);
		g.setFont(new Font("Arial", Font.PLAIN, 11));
		g.drawString(text, x, y);
	}

	private void drawInputField(Graphics2D g, Rect r, String text, boolean focus, boolean hover) {
		// Create hover effect with slight zoom
		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		// Field background
		Color bgColor = hover ? INPUT_HOVER : INPUT_BG;
		g.setColor(bgColor);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);

		// Field border with focus/hover highlight
		if (focus) {
			g.setColor(GOLD_ACCENT);
			g.setStroke(new BasicStroke(1.5f));
			g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);
			g.setStroke(new BasicStroke(1f));
		} else if (hover) {
			g.setColor(ACCENT);
			g.setStroke(new BasicStroke(1.2f));
			g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);
			g.setStroke(new BasicStroke(1f));
		} else {
			g.setColor(BORDER);
			g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);
		}

		// Text
		g.setColor(TEXT);
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.drawString(text, drawRect.x + 8, drawRect.y + 20);
	}

	private void drawCheckbox(Graphics2D g, Rect r, String label, boolean checked, boolean hover) {
		// Create hover effect with slight zoom
		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		// Checkbox background
		Color bgColor = checked ? SELECTED : (hover ? INPUT_HOVER : INPUT_BG);
		g.setColor(bgColor);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 3, 3);

		// Checkbox border
		Color borderColor = checked ? GOLD_ACCENT : (hover ? ACCENT : BORDER);
		g.setColor(borderColor);
		if (hover) {
			g.setStroke(new BasicStroke(1.2f));
		}
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 3, 3);
		g.setStroke(new BasicStroke(1f));

		if (checked) {
			g.setColor(GOLD_ACCENT);
			g.setStroke(new BasicStroke(1.5f));

			// Create diamond shape - center based on drawRect
			int centerX = drawRect.x + drawRect.w / 2 + 1;
			int centerY = drawRect.y + drawRect.h / 2 + 1;
			int size = 3;

			// Diamond points: top, right, bottom, left
			int[] xPoints = {centerX, centerX + size, centerX, centerX - size};
			int[] yPoints = {centerY - size, centerY, centerY + size, centerY};

			g.fillPolygon(xPoints, yPoints, 4);
			g.setStroke(new BasicStroke(1f));
		}

		// Label with hover effect
		Color textColor = hover ? TEXT : TEXT_DIM;
		g.setColor(textColor);
		g.setFont(new Font("Arial", Font.PLAIN, 11));
		g.drawString(label, r.x + 18, r.y + 11);
	}

	private void drawButton(Graphics2D g, Rect r, String text, Color color, boolean hover) {
		// Create hover effect with slight zoom
		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;
		Color buttonColor = hover ? brighter(color) : color;

		// Button gradient
		GradientPaint gradient = new GradientPaint(
			drawRect.x, drawRect.y, buttonColor,
			drawRect.x, drawRect.y + drawRect.h, darker(buttonColor)
		);
		g.setPaint(gradient);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		// Button border
		g.setColor(brighter(buttonColor));
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		// Button text
		g.setColor(TEXT);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		FontMetrics fm = g.getFontMetrics();
		int tx = drawRect.x + (drawRect.w - fm.stringWidth(text))/2;
		int ty = drawRect.y + (drawRect.h + fm.getAscent())/2 - 1;
		g.drawString(text, tx, ty);
	}

	private void drawWorldBtn(Graphics2D g, Rect r, World w, boolean selected, boolean hovered) {
		// Create hover effect with slight zoom
		Rect drawRect = hovered ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		Color bg = selected ? SELECTED :
			hovered ? HOVER : INPUT_BG;

		g.setColor(bg);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		Color borderColor = selected ? ACCENT : hovered ? GOLD_ACCENT : BORDER;
		g.setColor(borderColor);
		if (selected) {
			g.setStroke(new BasicStroke(1.5f));
		} else if (hovered) {
			g.setStroke(new BasicStroke(1.2f));
		}
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);
		g.setStroke(new BasicStroke(1f));

		// World icon with proper padding for extended layout
		g.setColor(selected ? ACCENT : hovered ? GOLD_ACCENT : TEXT_DIM);
		g.fillOval(drawRect.x + 35, drawRect.y + 8, 18, 18);
		g.setColor(TEXT);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.drawString(String.valueOf(w.id), drawRect.x + 42, drawRect.y + 20);

		// World info with proper padding for extended layout
		g.setColor(TEXT);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.drawString(w.name, drawRect.x + 58, drawRect.y + 16);
		g.setColor(TEXT_DIM);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.drawString(w.desc, drawRect.x + 58, drawRect.y + 28);
	}

	private void drawAccountCard(Graphics2D g, Rect r, AccountData acc) {
		boolean hover = mouseIn(g, r);
		int delSize = 12;
		boolean delHover = mouseIn(g, new Rect(r.x + r.w - delSize - 3, r.y + 3, delSize, delSize));

		// Create hover effect with slight zoom
		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		Color cardBg = hover ? HOVER : new Color(35, 55, 45, 180);
		g.setColor(cardBg);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);
		g.setColor(hover ? GOLD_ACCENT : BORDER);
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		// Delete button
		g.setColor(delHover ? new Color(255, 100, 100) : new Color(220, 80, 80));
		g.fillOval(drawRect.x + drawRect.w - delSize - 3, drawRect.y + 4, delSize, delSize);
		g.setColor(TEXT);
		g.setFont(new Font("Arial", Font.BOLD, 8));
		g.drawString("×", drawRect.x + drawRect.w - delSize , drawRect.y + delSize +1 );

		// Account info
		g.setColor(TEXT);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString(TextClass.capitalize(acc.username), drawRect.x + 6, drawRect.y + 18);
		g.setColor(TEXT_DIM);
		g.setFont(new Font("Arial", Font.PLAIN, 11));
		g.drawString("Got High " + acc.uses + " Times", drawRect.x + 6, drawRect.y + 32);
	}

	private void drawStatus(Graphics2D g, Layout l) {
		if (!client.loginMessage1.isEmpty() || !client.loginMessage2.isEmpty()) {
			g.setColor(BLUE_ACCENT);
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			String msg = !client.loginMessage1.isEmpty() ? client.loginMessage1 : client.loginMessage2;
			FontMetrics fm = g.getFontMetrics();
			// Position message centered horizontally and aligned with accounts title level
			g.drawString(msg, (l.w - fm.stringWidth(msg))/2, l.accountPanel.y + 25);
		}
	}

	public void processLoginScreen(Graphics2D g, GameCanvas canvas) {
		Layout l = new Layout(canvas.getWidth(), canvas.getHeight());
		updateWorldHover(l);

		if (MouseState.leftClicked) {
			handleInput(l, g, canvas);
			MouseState.leftClicked = false;
		}

		processKeyboard(g, canvas);
	}

	private void updateWorldHover(Layout l) {
		worldHover = -1;
		for (int i = 0; i < worlds.length; i++) {
			if (client.mouseInRegion(l.worldBtns[i].x, l.worldBtns[i].y,
				l.worldBtns[i].x + l.worldBtns[i].w, l.worldBtns[i].y + l.worldBtns[i].h)) {
				worldHover = worlds[i].id;
				break;
			}
		}
	}

	private void handleInput(Layout l, Graphics2D g, GameCanvas canvas) {
		// Input fields
		if (clickIn(l.usernameField)) client.loginScreenCursorPos = 0;
		else if (clickIn(l.passwordField)) client.loginScreenCursorPos = 1;

			// Login button
		else if (clickIn(l.loginBtn)) {
			if (!Client.myUsername.isEmpty() && !Client.myPassword.isEmpty()) {
				client.loginFailures = 0;
				client.login(TextClass.capitalize(Client.myUsername), Client.myPassword, false, g, canvas);
			}
		}

		// Remember checkbox
		else if (clickIn(l.rememberBox)) {
			Client.rememberMe = !Client.rememberMe;
			if (!Client.rememberMe) {
				Client.myUsername = "";
				Client.myPassword = "";
			}
			SettingHandler.save();
		}

		// World selection
		else {
			for (int i = 0; i < worlds.length; i++) {
				if (clickIn(l.worldBtns[i])) {
					ClientConstants.worldSelected = worlds[i].id;
					Configuration.economyWorld = ClientConstants.worldSelected != 2;
					Client.rebuildFrameSize(Client.frameWidth, Client.frameHeight);
					break;
				}
			}

			// Account handling
			if (AccountManager.accounts != null) {
				if (clickIn(l.clearBtn)) {
					AccountManager.clearAccountList();
				} else {
					int count = Math.min(8, AccountManager.getAccounts().size());
					for (int i = 0; i < count; i++) {
						Rect card = l.accountCards[i];
						AccountData acc = AccountManager.getAccounts().get(i);

						// Delete button
						int delSize = 16;
						if (client.clickInRegion(card.x + card.w - delSize - 3, card.y + 3,
							card.x + card.w - 3, card.y + 3 + delSize)) {
							AccountManager.removeAccount(acc);
						}
						// Account login
						else if (clickIn(card)) {
							if (!acc.username.isEmpty() && !acc.password.isEmpty()) {
								client.loginFailures = 0;
								Client.myUsername = acc.username;
								Client.myPassword = acc.password;
								client.login(acc.username, acc.password, false, g, canvas);
							}
						}
					}
				}
			}
		}
	}

	private void processKeyboard(Graphics2D g, GameCanvas canvas) {
		int key;
		while ((key = Keyboard.readChar(-796)) != -1) {
			if (Client.validUserPassChars.indexOf(key) != -1) {
				if (client.loginScreenCursorPos == 0) {
					Client.myUsername += (char) key;
					if (Client.myUsername.length() > 12) {
						Client.myUsername = Client.myUsername.substring(0, 12);
					}
				} else if (client.loginScreenCursorPos == 1) {
					Client.myPassword += (char) key;
					if (Client.myPassword.length() > 20) {
						Client.myPassword = Client.myPassword.substring(0, 20);
					}
				}
			} else if (key == 8) { // Backspace
				if (client.loginScreenCursorPos == 0 && !Client.myUsername.isEmpty()) {
					Client.myUsername = Client.myUsername.substring(0, Client.myUsername.length() - 1);
				} else if (client.loginScreenCursorPos == 1 && !Client.myPassword.isEmpty()) {
					Client.myPassword = Client.myPassword.substring(0, Client.myPassword.length() - 1);
				}
			} else if (key == 9 || key == 10 || key == 13) { // Tab/Enter
				if (client.loginScreenCursorPos == 0) {
					client.loginScreenCursorPos = 1;
				} else {
					client.login(Client.myUsername, Client.myPassword, false, g, canvas);
				}
			}
		}
	}

	// Helper methods
	private void setupRendering(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private void initBuffer() {
		if (Client.aRSImageProducer_1109 == null ||
			Client.aRSImageProducer_1109.canvasWidth != Client.frameWidth ||
			Client.aRSImageProducer_1109.canvasHeight != Client.frameHeight) {
			Client.aRSImageProducer_1109 = new ImageProducer(Client.frameWidth, Client.frameHeight);
		}
		Client.aRSImageProducer_1109.initDrawingArea();
	}

	private String getPasswordDisplay() {
		return TextClass.passwordAsterisks(Client.myPassword);
	}

	private String getCursor(int field) {
		return (client.loginScreenCursorPos == field && Client.loopCycle % 40 < 20) ? "|" : "";
	}

	private boolean mouseIn(Graphics2D g, Rect r) {
		return client.mouseInRegion(r.x, r.y, r.x + r.w, r.y + r.h);
	}

	private boolean clickIn(Rect r) {
		return client.clickInRegion(r.x, r.y, r.x + r.w, r.y + r.h);
	}

	private Color brighter(Color c) {
		return new Color(Math.min(255, c.getRed() + 40),
			Math.min(255, c.getGreen() + 40),
			Math.min(255, c.getBlue() + 40));
	}

	private Color darker(Color c) {
		return new Color(Math.max(0, c.getRed() - 30),
			Math.max(0, c.getGreen() - 30),
			Math.max(0, c.getBlue() - 30));
	}

	// Helper classes
	private static class Rect {
		final int x, y, w, h;
		Rect(int x, int y, int w, int h) { this.x = x; this.y = y; this.w = w; this.h = h; }
	}

	private static class World {
		final int id;
		final String name, desc;
		World(int id, String name, String desc) { this.id = id; this.name = name; this.desc = desc; }
	}
}