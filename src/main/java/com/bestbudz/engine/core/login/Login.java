package com.bestbudz.engine.core.login;

import static com.bestbudz.cache.ResetIDKits.resetIdentityKits;
import com.bestbudz.cache.Signlink;
import com.bestbudz.data.ItemDef;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;
import com.bestbudz.engine.config.NetworkConfig;
import static com.bestbudz.engine.core.GameState.resetGameState;
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
import com.bestbudz.network.RSSocket;
import com.bestbudz.engine.core.gamerender.Rasterizer;
import static com.bestbudz.ui.DialogHandling.sendFrame36;
import static com.bestbudz.ui.interfaces.Chatbox.splitPrivateChat;
import com.bestbudz.util.ISAACRandomGen;
import com.bestbudz.util.NodeList;
import com.bestbudz.util.TextClass;
import com.bestbudz.engine.core.gamerender.ObjectManager;
import com.bestbudz.world.Varp;
import java.awt.Graphics2D;
import java.io.IOException;

public class Login extends Client
{
	public static void loginToGameworld(Graphics2D g)
	{
		if (lowMem && loadingStage == 2 && ObjectManager.anInt131 != plane)
		{
			mainGameRendering.drawGraphics(0, g, 0);
			loadingStage = 1;
			aLong824 = System.currentTimeMillis();
		}
		if (loadingStage == 1)
		{
			int j = validateMapData();
			if (j != 0 && System.currentTimeMillis() - aLong824 > 0x57e40L)
			{
				Signlink.reporterror(
					myUsername + " glcfb " + aLong1215 + "," + j + "," + lowMem + "," + decompressors[0] + ","
						+ onDemandFetcher.getNodeCount() + "," + plane + "," + anInt1069 + "," + anInt1070);
				aLong824 = System.currentTimeMillis();
			}
		}
		if (loadingStage == 2 && plane != anInt985)
		{
			anInt985 = plane;
			//renderMinimap(plane);
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

			// Only show once per manual login
			if (!flag) {
				loginMessage1 = "";
				loginMessage2 = "Rolling a fat blunt...";
				//	loginRenderer.displayLoginScreen(g, canvas);
			}

			server = NetworkConfig.SERVER_IPS[worldSelected - 1];
			socketStream = new RSSocket(client, openSocket(NetworkConfig.SERVER_PORT + portOff));

			long l = TextClass.longForName(username);
			int i = (int) (l >> 16 & 31L);
			stream.currentOffset = 0;
			stream.writeWordBigEndian(14);
			stream.writeWordBigEndian(i);
			socketStream.queueBytes(2, stream.buffer);

			for (int j = 0; j < 8; j++) socketStream.read();
			int k = socketStream.read();
			int i1 = k;

			if (k == 0) {
				socketStream.flushInputStream(inStream.buffer, 8);
				inStream.currentOffset = 0;
				aLong1215 = inStream.readQWord();

				int[] ai = new int[4];
				ai[0] = (int) (Math.random() * 99999999D);
				ai[1] = (int) (Math.random() * 99999999D);
				ai[2] = (int) (aLong1215 >> 32);
				ai[3] = (int) aLong1215;

				stream.currentOffset = 0;
				stream.writeWordBigEndian(100);
				stream.writeDWord(ai[0]);
				stream.writeDWord(ai[1]);
				stream.writeDWord(ai[2]);
				stream.writeDWord(ai[3]);
				stream.writeString(String.valueOf(IdentityResolver.resolve()));
				stream.writeString(username);
				stream.writeString(password);
				stream.doKeys();

				aStream_847.currentOffset = 0;
				aStream_847.writeWordBigEndian(flag ? 18 : 16);
				aStream_847.writeWordBigEndian(stream.currentOffset + 36 + 1 + 1 + 2);
				aStream_847.writeWordBigEndian(255);
				if (worldSelected == 3){
					aStream_847.writeWord(EngineConfig.DEV_ENGINE_VERSION);
				}else{
					aStream_847.writeWord(EngineConfig.ENGINE_VERSION);
				}
				aStream_847.writeWordBigEndian(lowMem ? 1 : 0);

				for (int l1 = 0; l1 < 9; l1++)
					aStream_847.writeDWord(expectedCRCs[l1]);

				aStream_847.writeBytes(stream.buffer, stream.currentOffset, 0);
				stream.encryption = new ISAACRandomGen(ai);

				for (int j2 = 0; j2 < 4; j2++) ai[j2] += 50;
				encryption = new ISAACRandomGen(ai);

				socketStream.queueBytes(aStream_847.currentOffset, aStream_847.buffer);
				k = socketStream.read();
			}

			if (k == 1) {
				Thread.sleep(2000L);
				loginInProgress = false;
				login(username, password, flag, g, canvas, client);
				return;
			}

			if (k == 2) {
				// Login success
				myUsername = username;
				myPassword = password;
				myPrivilege = socketStream.read();
				final AccountData account = new AccountData(myPrivilege, username, password);

				if (rememberMe) {
					AccountManager.addAccount(account);
					AccountManager.saveAccount();
				}
				currentAccount = AccountManager.getAccount(username);
				if (currentAccount == null) currentAccount = account;

				flagged = socketStream.read() == 1;
				aLong1220 = 0L;
				anInt1022 = 0;
				awtFocus = true;
				aBoolean954 = true;
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

				// Game init
				stream.currentOffset = 0;
				inStream.currentOffset = 0;
				pktType = -1;
				anInt841 = -1;
				anInt842 = -1;
				anInt843 = -1;
				pktSize = 0;
				anInt1009 = 0;
				anInt1104 = 0;
				anInt1011 = 0;
				anInt855 = 0;
				menuActionRow = 0;
				menuOpen = false;
				idleTime = 0;

				for (int j1 = 0; j1 < 500; j1++) chatMessages[j1] = null;
				itemSelected = 0;
				spellSelected = 0;
				loadingStage = 0;
				anInt1062 = 0;
				setNorth();
				anInt1021 = 0;
				anInt985 = -1;
				destX = 0;
				destY = 0;
				stonerCount = 0;
				npcCount = 0;

				for (int i2 = 0; i2 < maxStoners; i2++) {
					stonerArray[i2] = null;
					aStreamArray895s[i2] = null;
				}
				for (i = 0; i < 17; i++) console.inputConsoleMessages[i] = "";
				for (int k2 = 0; k2 < 16384; k2++) npcArray[k2] = null;

				myStoner = stonerArray[myStonerIndex] = new Stoner();
				aClass19_1013.removeAll();
				aClass19_1056.removeAll();

				for (int l2 = 0; l2 < 4; l2++)
					for (int i3 = 0; i3 < 104; i3++)
						for (int k3 = 0; k3 < 104; k3++)
							groundArray[l2][i3][k3] = null;

				aClass19_1179 = new NodeList();
				fullscreenInterfaceID = -1;
				anInt900 = 0;
				stonersCount = 0;
				dialogID = -1;
				backDialogID = -1;
				openInterfaceID = -1;
				invOverlayInterfaceID = -1;
				anInt1018 = -1;
				aBoolean1149 = false;
				tabID = 3;
				inputDialogState = 0;
				menuOpen = false;
				messagePromptRaised = false;
				aString844 = null;
				anInt1055 = 0;
				anInt1054 = -1;
				aBoolean1047 = true;
				resetIdentityKits();

				for (int j3 = 0; j3 < 5; j3++) anIntArray990[j3] = 0;
				for (int l3 = 0; l3 < 5; l3++) {
					atStonerActions[l3] = null;
					atStonerArray[l3] = false;
				}

				anInt1175 = 0;
				anInt1134 = 0;
				anInt986 = 0;
				anInt1288 = 0;
				anInt924 = 0;
				anInt1188 = 0;
				anInt1155 = 0;
				anInt1226 = 0;

				sendFrame36(429, 1);
				resetImageProducers2();
				setBounds();

				return;
			}

			// All error handling paths
			loginInProgress = false;

			switch (k) {
				case 3:
					loginMessage1 = "How high are you?!";
					loginMessage2 = "Think man, think!.";
					break;

				case 4:
					loginMessage1 = "You were naughty.";
					loginMessage2 = "You have been banned, shithead.";
					break;

				case 5:
					loginMessage1 = "All hell loose, you have been hacked!";
					loginMessage2 = "Or you too fuckd up and forgot you are logged in already.";
					break;

				case 6:
					loginMessage1 = "Best Budz has gotten a new addition!";
					loginMessage2 = "Please download the newest dock.";
					break;

				case 7:
					loginMessage1 = "BestBudz is so popular, theres no room for you..";
					loginMessage2 = "Please hold your horses!.";
					break;

				case 8:
					loginMessage1 = "Jay might be in bed.";
					loginMessage2 = "Check discord to be certain.";
					break;

				case 9:
					loginMessage1 = "Sure 9 dock's are enough!?";
					loginMessage2 = "You aint a real stoner bro!.";
					break;

				case 10:
					loginMessage1 = "Unable to connect.";
					loginMessage2 = "Bad session id.";
					break;

				case 11:
					loginMessage1 = "Login server rejected session.";
					loginMessage2 = "Please try again.";
					break;

				case 12:
					loginMessage1 = "You need a members account to login to this world.";
					loginMessage2 = "Please subscribe, or use a different world.";
					break;

				case 13:
					loginMessage1 = "Could not complete login.";
					loginMessage2 = "Please try using a different world.";
					break;

				case 14:
					loginMessage1 = "The server is being updated.";
					loginMessage2 = "Please wait 1 minute and try again.";
					break;

				case 15:
					loggedIn = true;
					loginComplete = true;
					loginInProgress = false;
					aLong824 = System.currentTimeMillis();
					break;

				case 16:
					loginMessage1 = "You need to chill, so I am forcing you.";
					loginMessage2 = "Roll a joint and try again.";
					break;

				case 17:
					loginMessage1 = "You are standing in a members-only area.";
					loginMessage2 = "To play on this world move to a free area first";
					break;

				case 20:
					loginMessage1 = "Invalid loginserver requested";
					loginMessage2 = "Please try using a different world.";
					break;

				case 21:
					for (int t = socketStream.read(); t >= 0; t--) {
						loginMessage1 = "You have only just left another world";
						loginMessage2 = "Your profile will be transferred in: " + t + " seconds";
						loginRenderer.displayLoginScreen(g, canvas);
						Thread.sleep(1000L);
					}
					loginInProgress = false;
					login(username, password, flag, g, canvas, client);
					return;

				case 22:
					loginMessage1 = "Cannot pick '" + TextClass.capitalize(myUsername) + "'!";
					loginMessage2 = "Take something else.";
					break;

				case 23:
					loginMessage1 = "You do not have permission to do this!";
					loginMessage2 = "Please try a different world.";
					break;

				case -1:
					if (i1 == 0 && loginFailures < 2) {
						Thread.sleep(2000L);
						loginFailures++;
						loginInProgress = false;
						login(username, password, flag, g, canvas, client);
						return;
					} else {
						loginMessage1 = "No response from loginserver";
						loginMessage2 = "Please wait 1 minute and try again.";
					}
					break;

				default:
					System.out.println("response:" + k);
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
		anInt1278 = 0;
		anInt1131 = 0;
		anInt896 = 0;
		minimapInt1 = 0;
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
			int j = Varp.cache[i].anInt709;
			if (j == 0)
				return;
			if (j == 1)
			{
				if (k == 1)
					Rasterizer.generateColorPalette(0.90000000000000002D);
				if (k == 2)
					Rasterizer.generateColorPalette(0.80000000000000004D);
				if (k == 3)
					Rasterizer.generateColorPalette(0.69999999999999996D);
				if (k == 4)
					Rasterizer.generateColorPalette(0.59999999999999998D);
				ItemDef.mruNodes1.unlinkAll();
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
				anInt1249 = k;
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
		for (int i = 0; i < aByteArrayArray1183.length; i++)
		{
			if (aByteArrayArray1183[i] == null && anIntArray1235[i] != -1)
				return -1;
			if (aByteArrayArray1247[i] == null && anIntArray1236[i] != -1)
				return -2;
		}
		boolean flag = true;
		for (int j = 0; j < aByteArrayArray1183.length; j++)
		{
			byte[] abyte0 = aByteArrayArray1247[j];
			if (abyte0 != null)
			{
				int k = (anIntArray1234[j] >> 8) * 64 - baseX;
				int l = (anIntArray1234[j] & 0xff) * 64 - baseY;
				if (aBoolean1159)
				{
					k = 10;
					l = 10;
				}
				flag &= ObjectManager.method189(k, abyte0, l);
			}
		}
		if (!flag)
			return -3;
		if (aBoolean1080)
		{
			return -4;
		}
		else
		{
			loadingStage = 2;
			ObjectManager.anInt131 = plane;
			resetGameState();
			stream.createFrame(121);
			return 0;
		}
	}
}
