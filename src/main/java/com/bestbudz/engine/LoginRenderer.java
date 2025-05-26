package com.bestbudz.engine;

import static com.bestbudz.engine.Client.BACKGROUND;
import static com.bestbudz.engine.Client.LOGIN_BACKGROUND;
import com.bestbudz.config.ClientConstants;
import com.bestbudz.config.Configuration;
import com.bestbudz.config.SettingHandler;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import com.bestbudz.engine.input.Keyboard;
import com.bestbudz.engine.input.MouseState;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.util.TextClass;
import java.awt.Graphics2D;
import java.util.Objects;

public class LoginRenderer {

  private final Client client;
  private final int[] worldsAmount = {1, 2, 3, 4};
  private final String[] world = {
    "BestBudz", "Pointless", "Development World", "Staff World"
  };
  private int worldHover = -1;

  public LoginRenderer(Client client) {
    this.client = client;
  }

	public void displayLoginScreen(Graphics2D g, GameCanvas canvas) {
		final int centerX = canvas.getWidth() / 2;
		final int centerY = canvas.getHeight() / 2;

		// Initialize or resize off-screen buffer
		if (Client.aRSImageProducer_1109 == null
			|| Client.aRSImageProducer_1109.canvasWidth != Client.frameWidth
			|| Client.aRSImageProducer_1109.canvasHeight != Client.frameHeight) {
			Client.aRSImageProducer_1109 = new ImageProducer(Client.frameWidth, Client.frameHeight);
		}
		Client.aRSImageProducer_1109.initDrawingArea();

		// Get graphics context for off-screen image buffer
		Graphics2D offscreenG = Client.aRSImageProducer_1109.getImageGraphics();

		if (client.loginScreenState == 0) {
			offscreenG.drawImage(
				LOGIN_BACKGROUND,
				centerX - LOGIN_BACKGROUND.getWidth() / 2,
				centerY - LOGIN_BACKGROUND.getHeight() / 2,
				null
			);

			Client.cacheSprite[
				client.mouseInRegion(centerX - 23, centerY + 160, centerX + 30, centerY + 197)
					? 3 : 2]
				.drawSprite(centerX - 23, centerY + 161);

			if (client.mouseInRegion(centerX - 64, centerY + 34, centerX + 69, centerY + 77)) {
				Client.cacheSprite[7].drawSprite(centerX - 64, centerY + 34);
			}
			if (client.mouseInRegion(centerX - 108, centerY - 82, centerX + 143, centerY - 55)) {
				Client.cacheSprite[8].drawSprite(centerX - 108, centerY - 82);
			}
			if (client.mouseInRegion(centerX - 108, centerY - 36, centerX + 143, centerY - 9)) {
				Client.cacheSprite[8].drawSprite(centerX - 108, centerY - 36);
			}
			if (client.mouseInRegion(centerX - 138, centerY + 2, centerX - 121, centerY + 19)) {
				Client.cacheSprite[9].drawSprite(centerX - 138, centerY);
			}
			if (Client.rememberMe) {
				Client.cacheSprite[10].drawSprite(centerX - 138, centerY);
			}

			displayAccounts();

			if (client.mouseInRegion(centerX - 373, centerY + 203, centerX - 297, centerY + 240)) {
				Client.cacheSprite[420].drawSprite(centerX - 373, centerY + 204);
			} else {
				Client.cacheSprite[419].drawSprite(centerX - 373, centerY + 204);
			}

			Client.boldText.method389(true, centerX - 326, 0xFFFFFF, "" + ClientConstants.worldSelected, centerY + 228);

			if (client.mouseInRegion(centerX - 373, centerY + 203, centerX - 297, centerY + 240)) {
				client.drawTooltip(MouseState.x + 11, MouseState.y - 20, "Habitat Selection");
			}

			Client.smallText.method389(true, centerX + 75, 0xB0AFAB, "Client version " + ClientConstants.CLIENT_VERSION, centerY + 100);
			Client.smallText.method389(true, centerX - 115, 0xB0AFAB, "Easy login?", centerY + 16);
			Client.smallText.method389(
				true,
				centerX + 40,
				client.mouseInRegion(centerX + 40, centerY + 7, centerX + 127, centerY + 17) ? 0xFFFFFF : 0xB0AFAB,
				"Too high for this.",
				centerY + 16);
			Client.smallText.method389(
				true,
				centerX - 102,
				0xB0AFAB,
				TextClass.capitalize(Client.myUsername) + ((client.loginScreenCursorPos == 0 && Client.loopCycle % 40 < 20) ? "|" : ""),
				centerY - 63);

			if (client.mouseInRegion(centerX - 138, centerY - 37, centerX - 112, centerY - 11)) {
				Client.smallText.method389(
					true,
					centerX - 102,
					0xB0AFAB,
					Client.myPassword + ((client.loginScreenCursorPos == 1 && Client.loopCycle % 40 < 20) ? "|" : ""),
					centerY - 17);
			} else {
				Client.smallText.method389(
					true,
					centerX - 102,
					0xB0AFAB,
					TextClass.passwordAsterisks(Client.myPassword) + ((client.loginScreenCursorPos == 1 && Client.loopCycle % 40 < 20) ? "|" : ""),
					centerY - 17);
			}

			if (!client.loginMessage1.isEmpty()) {
				Client.smallText.method382(0xB0AFAB, centerX + 4, client.loginMessage1, centerY + 135, true);
				Client.smallText.method382(0xB0AFAB, centerX + 4, client.loginMessage2, centerY + 145, true);
			} else {
				Client.smallText.method382(0xB0AFAB, centerX + 4, client.loginMessage2, centerY + 140, true);
			}

		} else if (client.loginScreenState == 1) {
			offscreenG.drawImage(
				BACKGROUND,
				centerX - BACKGROUND.getWidth() / 2,
				centerY - BACKGROUND.getHeight() / 2,
				null);

			if (client.mouseInRegion(centerX - 373, centerY + 203, centerX - 297, centerY + 240)) {
				client.drawTooltip(MouseState.x + 11, MouseState.y - 20, "Habitat Selection");
				Client.cacheSprite[420].drawSprite(centerX - 373, centerY + 204);
			} else {
				Client.cacheSprite[419].drawSprite(centerX - 373, centerY + 204);
			}

			Client.cacheSprite[418].drawSprite(centerX - 189, centerY - 60);
			Client.boldText.method389(true, centerX - 326, 0xFFFFFF, "" + ClientConstants.worldSelected, centerY + 228);

			for (int index = 0, y = centerY + 117; index < worldsAmount.length; index++, y += 24) {
				displayWorlds(centerX + 180, y, 0xFFFFFF, worldsAmount[index], world[index]);
			}

			if (client.mouseInRegion(centerX - 373, centerY + 203, centerX - 297, centerY + 240)) {
				client.drawTooltip(MouseState.x + 11, MouseState.y - 20, "Habitat Selection");
			}
		}

		// Draw the off-screen buffer to the actual canvas
		Client.aRSImageProducer_1109.drawGraphics(0, g,0);

		// Dispose offscreen graphics after drawing everything
		offscreenG.dispose();
	}


	public void displayWorlds(int x, int y, int textColor, int world, String worldType) {
    if (ClientConstants.worldSelected == world) {
      DrawingArea.drawAlphaPixels(x - 315, y - 136, 338, 16, 0xB24D00, 150);
    } else if (worldHover == world) {
      DrawingArea.drawAlphaPixels(x - 315, y - 136, 338, 16, 0x606060, 150);
    }
    Client.newRegularFont.drawBasicString(Integer.toString(world), x - 337, y - 123, 0xFFFFFF, 0);
    Client.newRegularFont.drawBasicString(worldType, x - 315, y - 123, 0xFFFFFF, 10);
  }

  public void displayAccounts() {
    final int centerX = Client.frameWidth / 2, centerY = Client.frameHeight / 2;
    int y = centerY - 68;
    if (AccountManager.accounts != null) {
      for (int index = 0; index < 3; index++, y += 47) {
        if (index >= AccountManager.getAccounts().size()) {
          break;
        }
        Client.cacheSprite[398].drawARGBSprite(centerX + 315, y + 21);
        Client.cacheSprite[395].drawARGBSprite(centerX + 193, y + 21);
        Client.cacheSprite[399].drawARGBSprite(centerX + 185, y + 14);
        if (client.mouseInRegion(centerX + 192, y + 21, centerX + 312, y + 40)) {
          Client.cacheSprite[396].drawARGBSprite(centerX + 193, y + 21);
        }
        if (client.mouseInRegion(centerX + 315, y + 21, centerX + 332, y + 40)) {
          Client.cacheSprite[397].drawARGBSprite(centerX + 315, y + 21);
        }
        AccountData account = AccountManager.getAccounts().get(index);
        if (account != null) {
          int rights = account.rank - 1;
          Client.newRegularFont.drawCenteredString(
              (rights != -1 ? "<img=" + rights + "> " : "")
                  + TextClass.capitalize(account.username),
              centerX + 260,
              y + 35,
              (client.mouseInRegion(centerX + 212, y + 21, centerX + 312, y + 40))
                  ? 0xFFFFFF
                  : 0xB0AFAB,
              0);
        }
        if (Objects.requireNonNull(account).uses != 0
            && MouseState.x >= centerX + 192
            && MouseState.x <= centerX + 212
            && MouseState.y >= y + 21
            && MouseState.y <= y + 40) {
          client.drawTooltip(
              MouseState.x + 11, MouseState.y - 20, "Account uses: " + account.uses);
        }
        Client.newRegularFont.drawCenteredString(
            "Clear Account List",
            centerX + 260,
            centerY - 57,
            (client.mouseInRegion(centerX + 210, centerY - 67, centerX + 311, centerY - 57))
                ? 0xFFFFFF
                : 0xB0AFAB,
            0);
      }
    }
  }

  public void processLoginScreen(Graphics2D g, GameCanvas canvas) {
	  boolean leftClick = MouseState.leftClicked;
	  boolean rightClick = MouseState.rightClicked;
	  MouseState.leftClicked = false;
	  MouseState.rightClicked = false;

    int centerX = Client.frameWidth / 2, centerY = Client.frameHeight / 2;
    if (client.loginScreenState == 0) {
      if (leftClick
          && client.clickInRegion(centerX - 108, centerY - 82, centerX + 143, centerY - 55))
        client.loginScreenCursorPos = 0;
      if (leftClick
          && client.clickInRegion(centerX - 108, centerY - 36, centerX + 143, centerY - 9))
        client.loginScreenCursorPos = 1;
      if (leftClick
          && client.clickInRegion(centerX - 373, centerY + 203, centerX - 297, centerY + 240)) {
        client.loginScreenState = 1;
      }
      if (leftClick
          && client.clickInRegion(centerX - 138, centerY + 2, centerX - 121, centerY + 19)) {
        Client.rememberMe = !Client.rememberMe;
        if (!Client.rememberMe) {
          Client.myUsername = "";
          Client.myPassword = "";
        }
        SettingHandler.save();
      }
      int y = centerY - 68;
      if (AccountManager.accounts != null) {
        for (int index = 0; index < AccountManager.getAccounts().size(); index++, y += 47) {
          AccountData account = AccountManager.getAccounts().get(index);
          if (leftClick
              && client.clickInRegion(centerX + 212, y + 21, centerX + 312, y + 40)) {
            if (!account.username.isEmpty() && !account.password.isEmpty()) {
              client.loginFailures = 0;
              if (!Objects.equals(Client.myUsername, account.username) || !Objects.equals(Client.myPassword, account.password)) {
                Client.myUsername = account.username;
                Client.myPassword = account.password;
              }
              client.login(account.username, account.password, false, g, canvas);
              if (Client.loggedIn) {
                return;
              }
            }
          }
          if (leftClick
              && client.clickInRegion(centerX + 315, y + 21, centerX + 330, y + 40)) {
            AccountManager.removeAccount(account);
          }
        }
        if (leftClick
            && client.clickInRegion(centerX + 210, centerY - 67, centerX + 311, centerY - 57)) {
          AccountManager.clearAccountList();
        }
      }

      if (leftClick
          && client.clickInRegion(centerX - 64, centerY + 34, centerX + 69, centerY + 77)) {
        if (!Client.myUsername.isEmpty() && !Client.myPassword.isEmpty()) {
          client.loginFailures = 0;
          client.login(TextClass.capitalize(Client.myUsername), Client.myPassword, false, g ,canvas);
          if (Client.loggedIn) {
            return;
          }
        } else {
          client.loginMessage1 = "";
          client.loginMessage2 = "Any username and password will do.";
        }
      }
      do {
        int l1 = Keyboard.readChar(-796);
        if (l1 == -1) break;
        boolean flag1 = false;
        for (int i2 = 0; i2 < Client.validUserPassChars.length(); i2++) {
          if (l1 != Client.validUserPassChars.charAt(i2)) continue;
          flag1 = true;
          break;
        }
        if (client.loginScreenCursorPos == 0) {
          if (l1 == 8 && !Client.myUsername.isEmpty())
            Client.myUsername = Client.myUsername.substring(0, Client.myUsername.length() - 1);
          if (l1 == 9 || l1 == 10 || l1 == 13) client.loginScreenCursorPos = 1;
          if (flag1) Client.myUsername += (char) l1;
          if (Client.myUsername.length() > 12)
            Client.myUsername = TextClass.capitalize(Client.myUsername.substring(0, 12));
        } else if (client.loginScreenCursorPos == 1) {
          if (l1 == 8 && !Client.myPassword.isEmpty())
            Client.myPassword = Client.myPassword.substring(0, Client.myPassword.length() - 1);
          if (l1 == 9 || l1 == 10 || l1 == 13)
            client.login(Client.myUsername, Client.myPassword, false,g, canvas);
          if (flag1) Client.myPassword += (char) l1;
          if (Client.myPassword.length() > 20)
            Client.myPassword = Client.myPassword.substring(0, 20);
        }
      } while (true);
      return;
    }
    if (client.loginScreenState == 1) {
      for (int index = 0, y = centerY - 19; index < worldsAmount.length; index++, y += 20) {
        if (client.mouseInRegion(centerX - 136, y, centerX + 204, y + 20)) {
          worldHover = index + 1;
        }
        if (leftClick
            && client.clickInRegion(centerX - 136, y, centerX + 204, y + 20)) {
          ClientConstants.worldSelected = index + 1;
          Configuration.economyWorld = ClientConstants.worldSelected != 2;
          Client.rebuildFrameSize(Client.frameWidth, Client.frameHeight);
        }
        if (leftClick
            && client.clickInRegion(centerX - 373, centerY + 203, centerX - 297, centerY + 240)) {
          client.loginScreenState = 0;
        }
      }
      if (!client.mouseInRegion(
          centerX - 136, centerY - 19, centerX + 204, (20 * worldsAmount.length))) {
        worldHover = -1;
      }
    }
  }
}
