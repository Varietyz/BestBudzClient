package com.bestbudz.ui.interfaces;

import com.bestbudz.config.Configuration;
import com.bestbudz.config.SettingHandler;
import com.bestbudz.engine.Client;
import static com.bestbudz.engine.Client.aString844;
import static com.bestbudz.engine.Client.anInt1039;
import static com.bestbudz.engine.Client.anInt1089;
import static com.bestbudz.engine.Client.anInt1104;
import static com.bestbudz.engine.Client.anInt1211;
import static com.bestbudz.engine.Client.anInt1315;
import static com.bestbudz.engine.Client.anInt1500;
import static com.bestbudz.engine.Client.anInt886;
import static com.bestbudz.engine.Client.anIntArray1180;
import static com.bestbudz.engine.Client.anIntArray1182;
import static com.bestbudz.engine.Client.backDialogID;
import static com.bestbudz.engine.Client.buildInterfaceMenu;
import static com.bestbudz.engine.Client.cacheSprite;
import static com.bestbudz.engine.Client.changeChatArea;
import static com.bestbudz.engine.Client.channelButtons;
import static com.bestbudz.engine.Client.chatColorHex;
import static com.bestbudz.engine.Client.chatTypeView;
import static com.bestbudz.engine.Client.clanChatMode;
import static com.bestbudz.engine.Client.clanTitle;
import static com.bestbudz.engine.Client.dialogID;
import static com.bestbudz.engine.Client.extendChatArea;
import static com.bestbudz.engine.Client.fixedGameComponents;
import static com.bestbudz.engine.Client.frameHeight;
import static com.bestbudz.engine.Client.inputDialogState;
import static com.bestbudz.engine.Client.inputString;
import static com.bestbudz.engine.Client.isExtendingChatArea;
import static com.bestbudz.engine.Client.isStonerOrSelf;
import static com.bestbudz.engine.Client.menuActionID;
import static com.bestbudz.engine.Client.menuActionName;
import static com.bestbudz.engine.Client.menuActionRow;
import static com.bestbudz.engine.Client.menuOpen;
import static com.bestbudz.engine.Client.messagePromptRaised;
import static com.bestbudz.engine.Client.modIcons;
import static com.bestbudz.engine.Client.myPrivilege;
import static com.bestbudz.engine.Client.myStoner;
import static com.bestbudz.engine.Client.myUsername;
import static com.bestbudz.engine.Client.newBoldFont;
import static com.bestbudz.engine.Client.newRegularFont;
import static com.bestbudz.engine.Client.openInterfaceID;
import static com.bestbudz.engine.Client.regularText;
import static com.bestbudz.engine.Client.rights;
import static com.bestbudz.engine.Client.sendFrame126;
import static com.bestbudz.engine.Client.showChatComponents;
import static com.bestbudz.engine.Client.smallText;
import static com.bestbudz.engine.Client.stonersCount;
import static com.bestbudz.engine.Client.stonersList;
import static com.bestbudz.engine.Client.stonersListAsLongs;
import static com.bestbudz.engine.Client.stonersNodeIDs;
import static com.bestbudz.engine.Client.stream;
import static com.bestbudz.engine.Client.tradeMode;
import com.bestbudz.engine.input.MouseState;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.graphics.text.TextDrawingArea;
import com.bestbudz.rendering.Rasterizer;
import com.bestbudz.ui.RSInterface;
import static com.bestbudz.ui.interfaces.StatusOrbs.orbComponents3;
import static com.bestbudz.util.FormatHelpers.getTime;
import com.bestbudz.util.TextClass;
import java.util.Set;

public class Chatbox {
	public static final String[] chatNames= new String[500];
	public static final String[] chatMessages = new String[500];
	public static int cButtonHPos;
	public static int cButtonCPos;
	public static final String[] chatTitles = new String[500];
	public static final String[] chatColors = new String[500];
	public static int[] chatTypes = new int[500];
	public static int[] chatRights = new int[500];
	public static int publicChatMode;
	public static int privateChatMode;
	public static String reportAbuseInput;
	public static int splitPrivateChat;
	private static final String[] clanTitles = new String[500];

	public Chatbox(){
		cButtonHPos = -1;
		cButtonCPos = 0;
		reportAbuseInput = "";
	}

	public static void pushMessage(String s, int i, String s1, String title, String color)
	{

		if (i == 0 && dialogID != -1)
		{
			aString844 = s;
		}
		if (backDialogID == -1)
			Client.inputTaken = true;
		for (int j = 499; j > 0; j--)
		{
			chatTypes[j] = chatTypes[j - 1];
			chatNames[j] = chatNames[j - 1];
			chatMessages[j] = chatMessages[j - 1];
			chatRights[j] = chatRights[j - 1];
			chatTitles[j] = chatTitles[j - 1];
			chatColors[j] = chatColors[j - 1];
		}
		chatTypes[0] = i;
		chatNames[0] = s1;
		chatMessages[0] = s;
		chatRights[0] = rights;
		chatTitles[0] = title;
		chatColors[0] = color;
	}

	public static void pushMessage(String s, int i, String s1) {
		if (i == 0 && dialogID != -1) {
			aString844 = s;
		}
		if (backDialogID == -1) {
			Client.inputTaken = true;
		}

		// 👇 Null safety
		if (s1 == null)
			s1 = "";
		if (s == null)
			s = "";

		for (int j = 499; j > 0; j--) {
			chatTypes[j] = chatTypes[j - 1];
			chatNames[j] = chatNames[j - 1];
			chatMessages[j] = chatMessages[j - 1];
			chatRights[j] = chatRights[j - 1];
			chatTitles[j] = chatTitles[j - 1];
			chatColors[j] = chatColors[j - 1];
			clanTitles[j] = clanTitles[j - 1];
		}

		chatTypes[0] = i;
		chatNames[0] = s1;
		chatMessages[0] = s;
		chatRights[0] = rights;
		clanTitles[0] = clanTitle;
	}

}
