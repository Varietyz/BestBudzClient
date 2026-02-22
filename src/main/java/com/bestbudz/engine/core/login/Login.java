package com.bestbudz.engine.core.login;

import static com.bestbudz.cache.ResetIDKits.resetIdentityKits;
import com.bestbudz.cache.Signlink;
import com.bestbudz.data.items.ItemDef;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;
import com.bestbudz.engine.config.NetworkConfig;
import static com.bestbudz.engine.core.GameState.resetGameState;
import com.bestbudz.engine.core.gamerender.ColorPalette;
import static com.bestbudz.engine.core.login.logout.Reset.resetImageProducers2;
import com.bestbudz.ui.handling.SettingHandler;
import com.bestbudz.data.AccountData;
import com.bestbudz.data.AccountManager;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.engine.core.GameCanvas;
import static com.bestbudz.ui.handling.input.Keyboard.console;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.cache.IdentityResolver;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import com.bestbudz.net.proto.LoginProto.LoginRequest;
import com.bestbudz.net.proto.LoginProto.LoginResponse;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.network.Socket;
import static com.bestbudz.ui.DialogHandling.sendFrame36;
import static com.bestbudz.ui.interfaces.Chatbox.splitPrivateChat;
import com.bestbudz.graphics.text.TextClass;
import com.bestbudz.engine.core.gamerender.ObjectManager;
import com.bestbudz.world.Varp;
import java.awt.Graphics2D;
import java.io.IOException;

public class Login extends Client
{
	public static void loginToGameworld(Graphics2D g)
	{
		if (lowMem && loadingStage == 2 && ObjectManager.baseLevel != plane)
		{
			mainGameRendering.drawGraphics(0, g, 0);
			loadingStage = 1;
			lastConnectionTime = System.currentTimeMillis();
		}
		if (loadingStage == 1)
		{
			int j = validateMapData();
			if (j != 0 && System.currentTimeMillis() - lastConnectionTime > 0x57e40L)
			{
				Signlink.reporterror(
					myUsername + " glcfb " + lastInventoryTime + "," + j + "," + lowMem + ","
						+ 0 + "," + plane + "," + inventoryOffsetX + "," + inventoryOffsetY);
				lastConnectionTime = System.currentTimeMillis();
			}
		}
		if (loadingStage == 2 && plane != draggedInterfaceId)
		{
			draggedInterfaceId = plane;

		}
	}

	public static void login(String username, String password, boolean flag, Graphics2D g, GameCanvas canvas, Client client) {
		if (loginInProgress || loginComplete || loggedIn)
			return;

		loginInProgress = true;
		Signlink.errorname = username;

		try {
			if (rememberMe && username != null && password != null) {
				SettingHandler.save();
			}

			if (!flag) {
				loginMessage1 = "";
				loginMessage2 = "Rolling a fat blunt...";

			}

			server = NetworkConfig.SERVER_IPS[worldSelected - 1];
			socketStream = new Socket(client, openSocket(NetworkConfig.SERVER_PORT + portOff));

			// Protobuf login handshake
			int engineVersion = (worldSelected == 3)
				? EngineConfig.DEV_ENGINE_VERSION : EngineConfig.ENGINE_VERSION;
			socketStream.writeProto(GamePacket.newBuilder()
				.setLoginRequest(LoginRequest.newBuilder()
					.setVersion(engineVersion)
					.setUsername(username)
					.setPassword(password)
					.setUid(String.valueOf(IdentityResolver.resolve()))
					.setReconnecting(flag))
				.build());

			GamePacket resp = socketStream.readProto();
			LoginResponse login = resp.getLoginResponse();
			int k = login.getResultValue();

			if (k == LoginResponse.Result.RETRY_VALUE) {
				Thread.sleep(2000L);
				loginInProgress = false;
				login(username, password, flag, g, canvas, client);
				return;
			}

			if (k == LoginResponse.Result.SUCCESS_VALUE) {

				myUsername = username;
				myPassword = password;
				myPrivilege = login.getRights();
				final AccountData account = new AccountData(myPrivilege, username);

				if (rememberMe) {
					AccountManager.addAccount(account);
					AccountManager.saveAccount();
				}
				currentAccount = AccountManager.getAccount(username);
				if (currentAccount == null) currentAccount = account;

				flagged = login.getFlagged();
				lastChatTime = 0L;
				mouseDragTime = 0;
				awtFocus = true;
				windowFocused = true;
				loggedIn = true;
				loginComplete = true;
				loginInProgress = false;
				activeInterfaceType = 0;
				MouseState.leftDown = false;
				MouseState.leftClicked = false;
				MouseState.rightDown = false;
				MouseState.rightClicked = false;
				MouseState.pressed = false;
				MouseState.clickEvent = false;

				stream.position = 0;
				inStream.position = 0;
				pktType = -1;
				regionUpdateCount = -1;
				regionX = -1;
				regionY = -1;
				pktSize = 0;
				idleTimeout = 0;
				systemMessageTimer = 0;
				connectionTimeout = 0;
				crosshairType = 0;
				menuActionRow = 0;
				menuOpen = false;
				idleTime = 0;

				for (int j1 = 0; j1 < 500; j1++) chatMessages[j1] = null;
				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				soundEffectCount = 0;
				setNorth();
				lastActionTime = 0;
				draggedInterfaceId = -1;
				destX = 0;
				destY = 0;
				stonerCount = 0;
				npcCount = 0;

				for (int i2 = 0; i2 < maxStoners; i2++) {
					stonerArray[i2] = null;
				}
				for (int i = 0; i < 17; i++) console.inputConsoleMessages[i] = "";
				for (int k2 = 0; k2 < 16384; k2++) npcArray[k2] = null;

				myStoner = stonerArray[myStonerIndex] = new Stoner();
				nodeList.clear();
				queueSpotAnimation.clear();

				for (int l2 = 0; l2 < 4; l2++)
					for (int i3 = 0; i3 < 104; i3++)
						for (int k3 = 0; k3 < 104; k3++)
							groundArray[l2][i3][k3] = null;

				spotAnimationQueue = new java.util.LinkedList<>();
				fullscreenInterfaceID = -1;
				friendsListAction = 0;
				stonersCount = 0;
				dialogID = -1;
				backDialogID = -1;
				openInterfaceID = -1;
				invOverlayInterfaceID = -1;
				mouseClickState = -1;
				isPlayerBusy = false;
				tabID = 3;
				inputDialogState = 0;
				menuOpen = false;
				messagePromptRaised = false;
				inputPromptText = null;
				tabHoverTime = 0;
				selectedTabIndex = -1;
				welcomeScreenVisible = true;
				resetIdentityKits();

				for (int j3 = 0; j3 < 5; j3++) cameraShakeOffsets[j3] = 0;
				for (int l3 = 0; l3 < 5; l3++) {
					atStonerActions[l3] = null;
					atStonerArray[l3] = false;
				}

				renderDistance = 0;
				interfaceDrawY = 0;
				systemUpdateTimer = 0;
				lastMouseY = 0;
				gameSubState = 0;
				viewportIndex = 0;
				interfaceDrawZ = 0;
				lastMouseX = 0;

				sendFrame36(429, 1);
				resetImageProducers2();
				setBounds();

				return;
			}

			loginInProgress = false;

			switch (login.getResult()) {
				case INVALID_CREDENTIALS:
					loginMessage1 = "How high are you?!";
					loginMessage2 = "Think man, think!.";
					break;
				case BANNED:
					loginMessage1 = "You were naughty.";
					loginMessage2 = "You have been banned, shithead.";
					break;
				case ALREADY_LOGGED_IN:
					loginMessage1 = "All hell loose, you have been hacked!";
					loginMessage2 = "Or you too fuckd up and forgot you are logged in already.";
					break;
				case UPDATED:
					loginMessage1 = "Best Budz has gotten a new addition!";
					loginMessage2 = "Please download the newest dock.";
					break;
				case WORLD_FULL:
					loginMessage1 = "BestBudz is so popular, theres no room for you..";
					loginMessage2 = "Please hold your horses!.";
					break;
				case SERVER_OFFLINE:
					loginMessage1 = "Jay might be in bed.";
					loginMessage2 = "Check discord to be certain.";
					break;
				case LOGIN_LIMIT:
					loginMessage1 = "Sure 9 dock's are enough!?";
					loginMessage2 = "You aint a real stoner bro!.";
					break;
				case BAD_SESSION:
					loginMessage1 = "Unable to connect.";
					loginMessage2 = "Bad session id.";
					break;
				case REJECTED:
					loginMessage1 = "Login server rejected session.";
					loginMessage2 = "Please try again.";
					break;
				case MEMBERS_ONLY:
					loginMessage1 = "You need a members account to login to this world.";
					loginMessage2 = "Please subscribe, or use a different world.";
					break;
				case COULD_NOT_COMPLETE:
					loginMessage1 = "Could not complete login.";
					loginMessage2 = "Please try using a different world.";
					break;
				case SERVER_UPDATING:
					loginMessage1 = "The server is being updated.";
					loginMessage2 = "Please wait 1 minute and try again.";
					break;
				case RECONNECT:
					loggedIn = true;
					loginComplete = true;
					loginInProgress = false;
					lastConnectionTime = System.currentTimeMillis();
					break;
				case TOO_MANY_ATTEMPTS:
					loginMessage1 = "You need to chill, so I am forcing you.";
					loginMessage2 = "Roll a joint and try again.";
					break;
				case MEMBERS_AREA:
					loginMessage1 = "You are standing in a members-only area.";
					loginMessage2 = "To play on this world move to a free area first";
					break;
				case INVALID_SERVER:
					loginMessage1 = "Invalid loginserver requested";
					loginMessage2 = "Please try using a different world.";
					break;
				case TRANSFER_WAIT:
					loginMessage1 = "You have only just left another world";
					loginMessage2 = "Your profile will be transferred shortly.";
					break;
				case INVALID_USERNAME:
					loginMessage1 = "Cannot pick '" + TextClass.capitalize(myUsername) + "'!";
					loginMessage2 = "Take something else.";
					break;
				case NO_PERMISSION:
					loginMessage1 = "You do not have permission to do this!";
					loginMessage2 = "Please try a different world.";
					break;
				case STAFF_ONLY:
					loginMessage1 = "This world is for staff only.";
					loginMessage2 = "Please try a different world.";
					break;
				default:
					System.out.println("response:" + login.getResult());
					loginMessage1 = "Unexpected server response";
					loginMessage2 = "Please try using a different world.";
					break;
			}

		} catch (IOException e) {
			loginMessage1 = "";
			loginMessage2 = "Error getting lit asf.";
			loginInProgress = false;
		} catch (InterruptedException ignored) {
			loginInProgress = false;
		}
	}

	public static void setNorth()
	{
		selectedSpellIndex = 0;
		rightClickMenuOption = 0;
		cameraRotation = 0;
		minimapRotation = 0;
		minimapInt2 = 0;
		minimapInt3 = 0;
	}

	public static void updateConfigValues(int i)
	{
		try
		{

			int k = variousSettings[i];

			if (i == 1050)
			{
				switch (k)
				{
					case 1:
					case 2:
					case 3:
					case 4:
					case 5:
						break;
				}
			}
			int j = Varp.cache[i].configType;
			if (j == 0)
				return;
			if (j == 1)
			{
				if (k == 1)
					ColorPalette.generateColorPalette(0.90000000000000002D);
				if (k == 2)
					ColorPalette.generateColorPalette(0.80000000000000004D);
				if (k == 3)
					ColorPalette.generateColorPalette(0.69999999999999996D);
				if (k == 4)
					ColorPalette.generateColorPalette(0.59999999999999998D);
				ItemDef.mruNodes1.clear();
				welcomeScreenRaised = true;
			}
			if (j == 3)
			{
				return;
			}
			if (j == 4)
			{
				return;
			}
			if (j == 5)
				legacyClickInt = k;
			if (j == 6)
				chatDisplayMode = k;
			if (j == 8)
			{
				splitPrivateChat = k;
				inputTaken = true;
			}
			if (j == 9)
				anInt913 = k;
		}
		catch (Exception e)
		{
		}
	}

	public static void updateEntityTextAndCamera()
	{
		for (int i = -1; i < stonerCount; i++)
		{
			int j;
			if (i == -1)
				j = myStonerIndex;
			else
				j = stonerIndices[i];
			Stoner stoner = stonerArray[j];
			if (stoner != null && stoner.textCycle > 0)
			{
				stoner.textCycle--;
				if (stoner.textCycle == 0)
					stoner.textSpoken = null;
			}
		}
		for (int k = 0; k < npcCount; k++)
		{
			int l = npcIndices[k];
			Npc npc = npcArray[l];
			if (npc != null && npc.textCycle > 0)
			{
				npc.textCycle--;
				if (npc.textCycle == 0)
					npc.textSpoken = null;
			}
		}
	}

	public static int validateMapData()
	{
		// Pure JSON mode — no binary terrain/object data needed.
		// Just verify JSON maps exist for all regions.
		for (int i = 0; i < mapRegionIds.length; i++)
		{
			if (!ObjectManager.isJsonMapAvailable(mapRegionIds[i])
				&& !ObjectManager.isJsonObjectsAvailable(mapRegionIds[i])) {
				// Region has no JSON data at all — treat as water, don't block loading
			}
		}

		if (friendsListVisible)
		{
			return -4;
		}
		else
		{
			loadingStage = 2;
			ObjectManager.baseLevel = plane;
			resetGameState();
			try {
				socketStream.writeProto(GamePacket.newBuilder()
					.setChangeRegion(com.bestbudz.net.proto.PlayerProto.ChangeRegion.getDefaultInstance())
					.build());
			} catch (IOException ignored) {}
			return 0;
		}
	}
}
