package com.bestbudz.engine.core.login;

import static com.bestbudz.engine.config.ColorConfig.*;
import com.bestbudz.engine.config.SettingsConfig;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.Client.LOGIN_BACKGROUND;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.core.GameCanvas;
import static com.bestbudz.ui.handling.input.MouseActions.clickInRegion;
import static com.bestbudz.ui.handling.input.MouseActions.mouseInRegion;
import static com.bestbudz.engine.core.login.Login.login;
import com.bestbudz.ui.handling.input.Keyboard;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.graphics.text.TextClass;
import java.awt.*;

public class LoginRenderer {

	private final Client client;
	public final World[] worlds = {
		new World(1, "BestBudz", "Main world"),
		new World(2, "Pointless", "Economy-free"),
		new World(3, "Development", "Testing"),
		new World(4, "Staff World", "Staff-only")
	};

	private int worldHover = -1;

	private static class Layout {
		final int w, h, cx, cy;
		final Rect worldPanel, accountPanel;
		final Rect usernameField, passwordField, loginBtn, rememberBox;
		final Rect[] worldBtns, accountCards;
		final Rect clearBtn;

		Layout(int screenW, int screenH) {
			w = screenW; h = screenH; cx = w/2; cy = h/2;

			int topMargin = 30;
			int fieldH = 30;
			int fieldW = 200;
			int fieldGap = 20;
			int btnW = 100;
			int btnH = 26;
			int checkboxSize = 13;

			int totalLoginWidth = fieldW + fieldGap + fieldW + fieldGap + btnW + 20;
			int loginStartX = (w - totalLoginWidth) / 2;

			usernameField = new Rect(loginStartX, topMargin, fieldW, fieldH);
			passwordField = new Rect(loginStartX + fieldW + fieldGap, topMargin, fieldW, fieldH);

			int rightBlockX = loginStartX + fieldW + fieldGap + fieldW + fieldGap;
			rememberBox = new Rect(rightBlockX, topMargin - 16, checkboxSize, checkboxSize);
			loginBtn = new Rect(rightBlockX, topMargin + 4, btnW, btnH);

			int worldPanelX = - 15;
			int worldPanelW = 200;
			int worldPanelH = 200;
			int worldPanelY = cy - worldPanelH / 2;
			worldPanel = new Rect(worldPanelX, worldPanelY, worldPanelW, worldPanelH);

			worldBtns = new Rect[4];
			int worldBtnY = worldPanelY + 15;
			int worldBtnH = 35;
			int worldBtnSpacing = 45;

			for (int i = 0; i < 4; i++) {
				worldBtns[i] = new Rect(worldPanelX + 10, worldBtnY + i * worldBtnSpacing, worldPanelW - 20, worldBtnH);
			}

			int bottomMargin = 20;
			int cardW = 120;
			int cardH = 50;
			int cardSpacing = 20;
			int accPanelW = w - 40;
			int accPanelX = 20;

			int availableWidth = accPanelW - 40;
			int columnsCount = Math.max(1, availableWidth / (cardW + cardSpacing));

			int accountCount = AccountManager.accounts != null ? AccountManager.getAccounts().size() : 0;
			int rowsNeeded = accountCount > 0 ? (int) Math.ceil((double) accountCount / columnsCount) : 1;

			int headerHeight = 35;
			int clearButtonHeight = 26;
			int paddingBottom = 15;
			int minPanelHeight = 60;

			int accPanelH;
			if (accountCount == 0) {
				accPanelH = minPanelHeight;
			} else {
				accPanelH = headerHeight + (rowsNeeded * (cardH + 10)) + clearButtonHeight + paddingBottom;
			}

			int accPanelY = h - accPanelH - bottomMargin;
			accountPanel = new Rect(accPanelX, accPanelY, accPanelW, accPanelH);

			if (accountCount > 0) {
				accountCards = new Rect[accountCount];
				int cardsStartX = accountPanel.x + 20;
				int cardsStartY = accountPanel.y + headerHeight;

				for (int i = 0; i < accountCount; i++) {
					int row = i / columnsCount;
					int col = i % columnsCount;
					accountCards[i] = new Rect(
						cardsStartX + col * (cardW + cardSpacing),
						cardsStartY + row * (cardH + 10),
						cardW,
						cardH
					);
				}
			} else {
				accountCards = new Rect[0];
			}

			clearBtn = new Rect(accountPanel.x + accountPanel.w - 90, accountPanel.y + 8, 78, 26);
		}
	}

	public LoginRenderer(Client client) {
		this.client = client;
	}

	public void displayLoginScreen(Graphics2D g, GameCanvas canvas) {
		Layout l = new Layout(canvas.getWidth(), canvas.getHeight());
		initBuffer();
		Graphics2D og = Client.gameScreenBuffer.getImageGraphics();

		try {

			setupRendering(og);
			render(og, l);
			Client.gameScreenBuffer.drawGraphics(0, g, 0);
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
		g.setColor(BG_COLOR);
		g.fillRect(0, 0, l.w, l.h);

		if (LOGIN_BACKGROUND != null) {
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
			g.drawImage(LOGIN_BACKGROUND, (l.w - LOGIN_BACKGROUND.getWidth())/2,
				(l.h - LOGIN_BACKGROUND.getHeight())/2, null);
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		}
	}

	private void drawTopLoginStrip(Graphics2D g, Layout l) {

		boolean userFocus = client.loginScreenCursorPos == 0;
		boolean userHover = mouseIn(g, l.usernameField);
		drawLabel(g, l.usernameField.x, l.usernameField.y - 8, "Stonername");
		drawInputField(g, l.usernameField,
			TextClass.capitalize(Client.myUsername) + getCursor(0), userFocus, userHover);

		boolean passFocus = client.loginScreenCursorPos == 1;
		boolean passHover = mouseIn(g, l.passwordField);
		drawLabel(g, l.passwordField.x, l.passwordField.y - 8, "Puff Puff Password");
		drawInputField(g, l.passwordField,
			getPasswordDisplay() + getCursor(1), passFocus, passHover);

		boolean checkboxHover = mouseIn(g, l.rememberBox);
		drawCheckbox(g, l.rememberBox, "Early Dementia?", Client.rememberMe, checkboxHover);

		boolean loginHover = mouseIn(g, l.loginBtn);
		drawButton(g, l.loginBtn, "GET HIGH", ACCENT_COLOR, loginHover);
	}

	private void drawLeftWorldSelector(Graphics2D g, Layout l) {

		for (int i = 0; i < worlds.length; i++) {
			World w = worlds[i];
			boolean selected = EngineConfig.worldSelected == w.id;
			boolean hovered = worldHover == w.id;
			drawWorldBtn(g, l.worldBtns[i], w, selected, hovered);
		}
	}

	private void drawBottomAccountPanel(Graphics2D g, Layout l) {
		if (AccountManager.accounts == null || AccountManager.getAccounts().isEmpty()) {

			drawPanel(g, l.accountPanel, "Demented Stoners", ACCENT_COLOR);

			g.setColor(WHITE_DIM_COLOR);
			g.setFont(new Font("Arial", Font.ITALIC, 12));
			String noAccountsMsg = "No saved accounts";
			FontMetrics fm = g.getFontMetrics();
			g.drawString(noAccountsMsg,
				l.accountPanel.x + (l.accountPanel.w - fm.stringWidth(noAccountsMsg)) / 2,
				l.accountPanel.y + 35);
			return;
		}

		drawPanel(g, l.accountPanel, "Demented Stoners", ACCENT_COLOR);

		java.util.List<AccountData> accounts = AccountManager.getAccounts();
		int count = Math.min(accounts.size(), l.accountCards.length);

		for (int i = 0; i < count; i++) {
			if (i < l.accountCards.length) {
				drawAccountCard(g, l.accountCards[i], accounts.get(i));
			}
		}

		if (count > 0) {
			boolean clearHover = mouseIn(g, l.clearBtn);
			drawButton(g, l.clearBtn, "Clean", new Color(220, 80, 80), clearHover);
		}
	}

	private void drawPanel(Graphics2D g, Rect r, String title, Color accentColor) {

		GradientPaint gradient = new GradientPaint(
			r.x, r.y, PANEL_COLOR,
			r.x, r.y + r.h, new Color(PANEL_COLOR.getRed() - 5, PANEL_COLOR.getGreen() - 5, PANEL_COLOR.getBlue() - 5, PANEL_COLOR.getAlpha())
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

	private void drawLabel(Graphics2D g, int x, int y, String text) {
		g.setColor(WHITE_DIM_COLOR);
		g.setFont(new Font("Arial", Font.PLAIN, 11));
		g.drawString(text, x, y);
	}

	private void drawInputField(Graphics2D g, Rect r, String text, boolean focus, boolean hover) {

		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		Color bgColor = hover ? INPUT_HOVER_COLOR : INPUT_BG_COLOR;
		g.setColor(bgColor);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);

		if (focus) {
			g.setColor(GOLD_ACCENT_COLOR);
			g.setStroke(new BasicStroke(1.5f));
			g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);
			g.setStroke(new BasicStroke(1f));
		} else if (hover) {
			g.setColor(ACCENT_COLOR);
			g.setStroke(new BasicStroke(1.2f));
			g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);
			g.setStroke(new BasicStroke(1f));
		} else {
			g.setColor(BORDER_COLOR2);
			g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 4, 4);
		}

		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.PLAIN, 12));
		g.drawString(text, drawRect.x + 8, drawRect.y + 20);
	}

	private void drawCheckbox(Graphics2D g, Rect r, String label, boolean checked, boolean hover) {

		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		Color bgColor = checked ? SELECTED_COLOR : (hover ? INPUT_HOVER_COLOR : INPUT_BG_COLOR);
		g.setColor(bgColor);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 3, 3);

		Color borderColor = checked ? GOLD_ACCENT_COLOR : (hover ? ACCENT_COLOR : BORDER_COLOR2);
		g.setColor(borderColor);
		if (hover) {
			g.setStroke(new BasicStroke(1.2f));
		}
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 3, 3);
		g.setStroke(new BasicStroke(1f));

		if (checked) {
			g.setColor(GOLD_ACCENT_COLOR);
			g.setStroke(new BasicStroke(1.5f));

			int centerX = drawRect.x + drawRect.w / 2 + 1;
			int centerY = drawRect.y + drawRect.h / 2 + 1;
			int size = 3;

			int[] xPoints = {centerX, centerX + size, centerX, centerX - size};
			int[] yPoints = {centerY - size, centerY, centerY + size, centerY};

			g.fillPolygon(xPoints, yPoints, 4);
			g.setStroke(new BasicStroke(1f));
		}

		Color textColor = hover ? WHITE_UI_COLOR : WHITE_DIM_COLOR;
		g.setColor(textColor);
		g.setFont(new Font("Arial", Font.PLAIN, 11));
		g.drawString(label, r.x + 18, r.y + 11);
	}

	private void drawButton(Graphics2D g, Rect r, String text, Color color, boolean hover) {

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

	private void drawWorldBtn(Graphics2D g, Rect r, World w, boolean selected, boolean hovered) {

		Rect drawRect = hovered ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		Color bg = selected ? SELECTED_COLOR :
			hovered ? HOVER_COLOR : INPUT_BG_COLOR;

		g.setColor(bg);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		Color borderColor = selected ? ACCENT_COLOR : hovered ? GOLD_ACCENT_COLOR : BORDER_COLOR2;
		g.setColor(borderColor);
		if (selected) {
			g.setStroke(new BasicStroke(1.5f));
		} else if (hovered) {
			g.setStroke(new BasicStroke(1.2f));
		}
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);
		g.setStroke(new BasicStroke(1f));

		g.setColor(selected ? ACCENT_COLOR : hovered ? GOLD_ACCENT_COLOR : WHITE_DIM_COLOR);
		g.fillOval(drawRect.x + 35, drawRect.y + 8, 18, 18);
		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.BOLD, 10));
		g.drawString(String.valueOf(w.id), drawRect.x + 42, drawRect.y + 20);

		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.drawString(w.name, drawRect.x + 58, drawRect.y + 16);
		g.setColor(WHITE_DIM_COLOR);
		g.setFont(new Font("Arial", Font.PLAIN, 10));
		g.drawString(w.desc, drawRect.x + 58, drawRect.y + 28);
	}

	private void drawAccountCard(Graphics2D g, Rect r, AccountData acc) {
		boolean hover = mouseIn(g, r);
		int delSize = 12;
		boolean delHover = mouseIn(g, new Rect(r.x + r.w - delSize - 3, r.y + 3, delSize, delSize));

		Rect drawRect = hover ? new Rect(r.x - 1, r.y - 1, r.w + 2, r.h + 2) : r;

		Color cardBg = hover ? HOVER_COLOR : new Color(35, 55, 45, 180);
		g.setColor(cardBg);
		g.fillRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);
		g.setColor(hover ? GOLD_ACCENT_COLOR : BORDER_COLOR2);
		g.drawRoundRect(drawRect.x, drawRect.y, drawRect.w, drawRect.h, 6, 6);

		g.setColor(delHover ? new Color(255, 100, 100) : new Color(220, 80, 80));
		g.fillOval(drawRect.x + drawRect.w - delSize - 3, drawRect.y + 4, delSize, delSize);
		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.BOLD, 8));
		g.drawString("×", drawRect.x + drawRect.w - delSize , drawRect.y + delSize +1 );

		g.setColor(WHITE_UI_COLOR);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawString(TextClass.capitalize(acc.username), drawRect.x + 6, drawRect.y + 18);
		g.setColor(WHITE_DIM_COLOR);
		g.setFont(new Font("Arial", Font.PLAIN, 11));
		g.drawString("Got High " + acc.uses + " Times", drawRect.x + 6, drawRect.y + 32);
	}

	private void drawStatus(Graphics2D g, Layout l) {
		if (!Client.loginMessage1.isEmpty() || !Client.loginMessage2.isEmpty()) {
			g.setColor(BLUE_ACCENT_COLOR);
			g.setFont(new Font("Arial", Font.PLAIN, 12));
			String msg = !Client.loginMessage1.isEmpty() ? Client.loginMessage1 : Client.loginMessage2;
			FontMetrics fm = g.getFontMetrics();

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
			if (mouseInRegion(l.worldBtns[i].x, l.worldBtns[i].y,
				l.worldBtns[i].x + l.worldBtns[i].w, l.worldBtns[i].y + l.worldBtns[i].h)) {
				worldHover = worlds[i].id;
				break;
			}
		}
	}

	private void handleInput(Layout l, Graphics2D g, GameCanvas canvas) {

		if (clickIn(l.usernameField)) client.loginScreenCursorPos = 0;
		else if (clickIn(l.passwordField)) client.loginScreenCursorPos = 1;

		else if (clickIn(l.loginBtn)) {
			if (!Client.myUsername.isEmpty() && !Client.myPassword.isEmpty()) {
				client.loginFailures = 0;
				login(TextClass.capitalize(Client.myUsername), Client.myPassword, false, g, canvas, client);
			}
		}

		else if (clickIn(l.rememberBox)) {
			Client.rememberMe = !Client.rememberMe;
			if (!Client.rememberMe) {
				Client.myUsername = "";
				Client.myPassword = "";
			}
			SettingHandler.save();
		}

		else {
			for (int i = 0; i < worlds.length; i++) {
				if (clickIn(l.worldBtns[i])) {
					EngineConfig.worldSelected = worlds[i].id;
					SettingsConfig.economyWorld = EngineConfig.worldSelected != 2;
					break;
				}
			}

			if (AccountManager.accounts != null && !AccountManager.getAccounts().isEmpty()) {
				if (clickIn(l.clearBtn)) {
					AccountManager.clearAccountList();

					canvas.repaint();
				} else {

					java.util.List<AccountData> accountsCopy = new java.util.ArrayList<>(AccountManager.getAccounts());
					int count = Math.min(accountsCopy.size(), l.accountCards.length);

					boolean deletionOccurred = false;

					for (int i = 0; i < count; i++) {
						if (i >= l.accountCards.length) break;

						Rect card = l.accountCards[i];
						AccountData acc = accountsCopy.get(i);

						int delSize = 12;
						if (clickInRegion(card.x + card.w - delSize - 3, card.y + 3,
							card.x + card.w - 3, card.y + 3 + delSize)) {
							AccountManager.removeAccount(acc);
							deletionOccurred = true;
							break;
						}

						else if (clickIn(card)) {
							if (!acc.username.isEmpty() && !acc.password.isEmpty()) {
								Client.loginFailures = 0;
								Client.myUsername = acc.username;
								Client.myPassword = acc.password;
								login(acc.username, acc.password, false, g, canvas, client);
								break;
							}
						}
					}

					if (deletionOccurred) {
						canvas.repaint();
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
			} else if (key == 8) {
				if (client.loginScreenCursorPos == 0 && !Client.myUsername.isEmpty()) {
					Client.myUsername = Client.myUsername.substring(0, Client.myUsername.length() - 1);
				} else if (client.loginScreenCursorPos == 1 && !Client.myPassword.isEmpty()) {
					Client.myPassword = Client.myPassword.substring(0, Client.myPassword.length() - 1);
				}
			} else if (key == 9 || key == 10 || key == 13) {
				if (client.loginScreenCursorPos == 0) {
					client.loginScreenCursorPos = 1;
				} else {
					login(Client.myUsername, Client.myPassword, false, g, canvas, client);
				}
			}
		}
	}

	private void setupRendering(Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}

	private void initBuffer() {
		if (Client.gameScreenBuffer == null ||
			Client.gameScreenBuffer.canvasWidth != Client.frameWidth ||
			Client.gameScreenBuffer.canvasHeight != Client.frameHeight) {
			Client.gameScreenBuffer = new ImageProducer(Client.frameWidth, Client.frameHeight);
			Client.gameScreenBuffer.initDrawingArea();
		}

	}

	private String getPasswordDisplay() {
		return TextClass.passwordAsterisks(Client.myPassword);
	}

	private String getCursor(int field) {
		return (client.loginScreenCursorPos == field && Client.loopCycle % 40 < 20) ? "|" : "";
	}

	private boolean mouseIn(Graphics2D g, Rect r) {
		return mouseInRegion(r.x, r.y, r.x + r.w, r.y + r.h);
	}

	private boolean clickIn(Rect r) {
		return clickInRegion(r.x, r.y, r.x + r.w, r.y + r.h);
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

	private static class Rect {
		final int x, y, w, h;
		Rect(int x, int y, int w, int h) { this.x = x; this.y = y; this.w = w; this.h = h; }
	}

	public static class World {
		final int id;
		final String name, desc;
		World(int id, String name, String desc) { this.id = id; this.name = name; this.desc = desc; }
	}
}
