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
	private static final String[] modeNames = {"All", "Game", "Public", "Private", "Cult", "Dealing", "Report Sucker"};
	public static final String[] chatNames= new String[500];
	public static final String[] chatMessages = new String[500];
	public static int cButtonHPos;
	public static int cButtonCPos;
	private static final String[] chatTitles = new String[500];
	private static final String[] chatColors = new String[500];
	public static int[] chatTypes = new int[500];
	private static int[] chatRights = new int[500];
	public static int publicChatMode;
	public static int privateChatMode;
	private static int setChannel;
	public static String reportAbuseInput;
	public static int splitPrivateChat;
	private static final String[] clanTitles = new String[500];

	private static final int[] modeX = {164, 230, 296, 362};
	private static final int[] modeNamesX = {26, 86, 150, 212, 286, 349, 427};
	private static final int[] modeNamesY = {158, 158, 153, 153, 153, 153, 158};
	private static final int[] channelButtonsX = {5, 71, 137, 203, 269, 335, 404};

	public Chatbox(){
		cButtonHPos = -1;
		cButtonCPos = 0;
		reportAbuseInput = "";
	}

	private static boolean handleChatTabToggle(int yOffset) {
		int[][] bounds = {
			{5, 61}, {71, 127}, {137, 193}, {203, 259},
			{269, 325}, {335, 391}, {404, 515}
		};

		for (int i = 0; i < 6; i++) {
			int x0 = bounds[i][0];
			int x1 = bounds[i][1];
			if (MouseState.x >= x0 && MouseState.x <= x1 &&
				MouseState.y >= yOffset + 482 && MouseState.y <= yOffset + 505) {

				if (setChannel != i) {
					cButtonCPos = i;
					chatTypeView = (i == 1 || i == 4) ? 5 : (i == 2 ? 1 : (i == 3 ? 2 : (i == 5 ? 3 : 0)));
					setChannel = i;
					Client.inputTaken = true;
				} else {
					showChatComponents = !showChatComponents;
				}
				return true;
			}
		}

		// Handle Report Button
		int x0 = bounds[6][0];
		int x1 = bounds[6][1];
		if (MouseState.x >= x0 && MouseState.x <= x1 &&
			MouseState.y >= yOffset + 482 && MouseState.y <= yOffset + 505) {

			if (openInterfaceID == -1) {
				Client.clearTopInterfaces();
				reportAbuseInput = "";
				Client.canMute = false;
				for (int i = 0; i < RSInterface.interfaceCache.length; i++) {
					if (RSInterface.interfaceCache[i] == null) continue;
					if (RSInterface.interfaceCache[i].parentID == 41750) {
						Client.reportAbuseInterfaceID = openInterfaceID = RSInterface.interfaceCache[i].parentID;
						break;
					}
				}
			} else {
				pushMessage("Close yo shit before reporting a sucker xD", 0, "");
			}
		}
		return false;
	}

	public static void processChatModeClick(boolean leftClick, boolean rightClick) {
		int yOffset = frameHeight - 503;

		updateChatHoverState(yOffset);

		if (!leftClick) return;

		handleChatInputFieldSubmission();

		if (handleChatTabToggle(yOffset)) {
			stream.createFrame(95);
			stream.writeWordBigEndian(publicChatMode);
			stream.writeWordBigEndian(privateChatMode);
			stream.writeWordBigEndian(tradeMode);
		}
	}

	private static void updateChatHoverState(int yOffset) {
		int[][] regions = {
			{5, 61}, {71, 127}, {137, 193}, {203, 259},
			{269, 325}, {335, 391}, {404, 515}
		};

		for (int i = 0; i < regions.length; i++) {
			int[] bounds = regions[i];
			if (MouseState.x >= bounds[0] && MouseState.x <= bounds[1]
				&& MouseState.y >= yOffset + 482 && MouseState.y <= yOffset + 503) {
				cButtonHPos = i;
				Client.inputTaken = true;
				return;
			}
		}
		cButtonHPos = -1;
		Client.inputTaken = true;
	}

	public static void handleChatAreaMenu(Set<Integer> handled) {
		if (!showChatComponents) return;

		int chatYStart = frameHeight - (165 + extendChatArea);
		if (MouseState.x > 0 && MouseState.x < 490 &&
			MouseState.y > chatYStart && MouseState.y < frameHeight - 40) {
			if (backDialogID != -1 && handled.add(backDialogID)) {
				buildInterfaceMenu(20, RSInterface.interfaceCache[backDialogID], frameHeight - 145, 0);
			} else {
				buildChatAreaMenu(MouseState.y - (frameHeight - 165));
			}
		}

		if (backDialogID != -1 && anInt886 != anInt1039) {
			Client.inputTaken = true;
			anInt1039 = anInt886;
		}
		if (backDialogID != -1 && anInt1315 != anInt1500) {
			Client.inputTaken = true;
			anInt1500 = anInt1315;
		}
	}

	private static void handleChatInputFieldSubmission() {
		if (RSInterface.currentInputField == null) return;

		if (RSInterface.currentInputField.onlyNumbers) {
			try {
				long amount = Long.parseLong(Client.message.replaceAll(",", ""));
				amount = Math.max(-Integer.MAX_VALUE, Math.min(Integer.MAX_VALUE, amount));
				if (amount > 0) {
					stream.createFrame(208);
					stream.writeDWord((int) amount);
				}
			} catch (Exception ignored) {}
		} else {
			stream.createFrame(150);
			stream.writeWordBigEndian(RSInterface.currentInputField.disabledMessage.length() + 3);
			stream.writeWord(RSInterface.currentInputField.id);
			stream.writeString(RSInterface.currentInputField.disabledMessage);
		}

		RSInterface.currentInputField.disabledMessage = "";
		RSInterface.currentInputField = null;
	}



	public static void drawChannelButtons()
	{
		final int yOffset = frameHeight - 165;
		fixedGameComponents[3].drawSprite(0, 143 + yOffset);
		String[] text = {"On", "Stoners", "Off", "Hide"};
		int[] textColor = {65280, 0xffff00, 0xff0000, 65535};
		switch (cButtonCPos)
		{
			case 0:
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
				channelButtons[1].drawSprite(channelButtonsX[cButtonCPos], 143 + yOffset);
				break;
		}
		if (cButtonHPos == cButtonCPos)
		{
			switch (cButtonHPos)
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
				case 6:
				case 7:
					channelButtons[2].drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
					break;
			}
		}
		else
		{
			switch (cButtonHPos)
			{
				case 0:
				case 1:
				case 2:
				case 3:
				case 4:
				case 5:
					channelButtons[0].drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
					break;
				case 6:
					channelButtons[3].drawSprite(channelButtonsX[cButtonHPos], 143 + yOffset);
					break;
			}
		}
		int[] modes = {publicChatMode, privateChatMode, clanChatMode, tradeMode};
		for (int i = 0; i < modeNamesX.length; i++)
		{
			smallText.method389(true, modeNamesX[i], 0xffffff, modeNames[i], modeNamesY[i] + yOffset);
		}
		for (int i = 0; i < modeX.length; i++)
		{
			smallText.method382(textColor[modes[i]], modeX[i], text[modes[i]], 164 + yOffset, true);
		}
	}

	private static boolean chatStateCheck()
	{
		return messagePromptRaised || inputDialogState != 0 || aString844 != null || backDialogID != -1
			|| dialogID != -1;
	}

	public static void extendChatArea() {
		int offsetY = frameHeight - 160;
		int x = 256;
		int y = offsetY - 10 - extendChatArea;

		// Start drag if mouse is pressed within the drag box
		if (!isExtendingChatArea && MouseState.leftDown && MouseState.x >= x && MouseState.x <= x + 8
			&& MouseState.y >= y && MouseState.y <= y + 9) {
			isExtendingChatArea = true;
		}

		// Update drag movement
		if (isExtendingChatArea) {
			int height = offsetY - MouseState.y;
			if (height < frameHeight - 170) {
				extendChatArea = Math.max(0, height);
			}
		}

		// Stop drag on mouse release
		if (MouseState.released) {
			isExtendingChatArea = false;
		}
	}

	public static void drawChatArea()
	{
		int yOffset = frameHeight - 165;
		Rasterizer.anIntArray1472 = anIntArray1180;
		if (chatStateCheck())
		{
			showChatComponents = true;
			fixedGameComponents[2].drawSprite(0, yOffset);
		}
		if (showChatComponents)
		{
			if (changeChatArea && !chatStateCheck())
			{
				orbComponents3[6].drawTransparentSprite(256, yOffset - extendChatArea - 3, 112);
				DrawingArea.drawAlphaGradient(7, 7 + yOffset - extendChatArea, 505, 130 + extendChatArea, 0, 0, 40);
				DrawingArea.drawAlphaHorizontalLine(7, 6 + yOffset - extendChatArea, 405, 0x6d6a57, 256);
			}
			else
			{
				fixedGameComponents[2].drawSprite(0, yOffset);
			}
		}
		if (!showChatComponents || changeChatArea)
		{
			DrawingArea.drawAlphaPixels(7, frameHeight - 23, 506, 24, 0, 100);
		}
		drawChannelButtons();
		TextDrawingArea textDrawingArea = regularText;
		if (messagePromptRaised)
		{
			extendChatArea = 0;
			newBoldFont.drawCenteredString(Client.aString1121, 259, 60 + yOffset, 0, -1);
			newBoldFont.drawCenteredString(Client.promptInput + "*", 259, 80 + yOffset, 128, -1);
		}
		else if (inputDialogState == 1)
		{
			extendChatArea = 0;
			newBoldFont.drawCenteredString("Enter amount:", 259, yOffset + 60, 0, -1);
			newBoldFont.drawCenteredString(Client.amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
		}
		else if (inputDialogState == 2)
		{
			extendChatArea = 0;
			newBoldFont.drawCenteredString("Enter Name:", 259, 60 + yOffset, 0, -1);
			newBoldFont.drawCenteredString(Client.amountOrNameInput + "*", 259, 80 + yOffset, 128, -1);
		}
		else if (aString844 != null)
		{
			extendChatArea = 0;
			newBoldFont.drawCenteredString(aString844, 259, 60 + yOffset, 0, -1);
			newBoldFont.drawCenteredString("Click to continue", 259, 80 + yOffset, 128, -1);
		}
		else if (backDialogID != -1)
		{
			extendChatArea = 0;
			Client.drawInterface(0, 20, RSInterface.interfaceCache[backDialogID], 20 + yOffset);
		}
		else if (dialogID != -1)
		{
			extendChatArea = 0;
			Client.drawInterface(0, 20, RSInterface.interfaceCache[dialogID], 20 + yOffset);
		}
		else if (showChatComponents)
		{
			int j77 = -3;
			int j = 0;
			int shadow = changeChatArea ? 0 : -1;
			DrawingArea.setDrawingArea(121 + yOffset, 7, 498, 7 + yOffset - extendChatArea);
			for (int k = 0; k < 500; k++)
			{
				if (chatMessages[k] != null)
				{
					String title;
					if (chatTitles[k] != null)
					{
						title = "<col=" + chatColors[k] + ">" + chatTitles[k] + "</col> ";
					}
					else
					{
						title = "";
					}
					int chatType = chatTypes[k];
					int yPos = (70 - j77 * 14) + anInt1089 + 5;
					String s1 = chatNames[k];
					String timeStamp = getTime();
					byte stonerRights = 0;
					if (s1 != null && s1.startsWith("@cr"))
					{
						int test1 = Integer.parseInt("" + s1.charAt(3));
						if (s1.charAt(4) != '@')
						{
							test1 = Integer.parseInt(s1.charAt(3) + "" + s1.charAt(4));
							s1 = s1.substring(6);
						}
						else
						{
							s1 = s1.substring(5);
						}
						stonerRights = (byte) test1;
					}
					if (chatType == 0)
					{
						if (chatTypeView == 5 || chatTypeView == 0)
						{
							newRegularFont.drawBasicString(chatMessages[k], 11, yPos + yOffset,
								changeChatArea ? 0xFFFFFF : 0, shadow);
							j++;
							j77++;
						}
					}
					if ((chatType == 1 || chatType == 2)
						&& (chatType == 1 || publicChatMode == 0 || publicChatMode == 1 && Client.isStonerOrSelf(s1)))
					{
						if (chatTypeView == 1 || chatTypeView == 0)
						{
							int xPos = 11;
							if (Configuration.enableTimeStamps)
							{
								newRegularFont.drawBasicString(timeStamp, xPos, yPos + yOffset,
									changeChatArea ? 0xFFFFFF : 0, shadow);
								xPos += newRegularFont.getTextWidth(timeStamp);
							}
							if (stonerRights == 0)
							{
								modIcons[11].drawSprite(xPos - 1, yPos - 12 + yOffset);
								xPos += 17;
							}
							if (stonerRights >= 1)
							{
								modIcons[stonerRights - 1].drawSprite(xPos - 1, yPos - 12 + yOffset);
								xPos += 17;
							}
							newRegularFont.drawBasicString(title + s1 + " said: ", xPos - 1, yPos + yOffset,
								changeChatArea ? 0xFFFFFF : 0, shadow);
							xPos += newRegularFont.getTextWidth(title + s1) + 8;
							newRegularFont.drawBasicString(chatMessages[k], xPos + 23, yPos + yOffset,
								changeChatArea ? 0x7FA9FF : 255, shadow);
							j++;
							j77++;
						}
					}
					if ((chatType == 3 || chatType == 7) && (splitPrivateChat == 0 || chatTypeView == 2)
						&& (chatType == 7 || privateChatMode == 0 || privateChatMode == 1 && isStonerOrSelf(s1)))
					{
						if (chatTypeView == 2 || chatTypeView == 0)
						{
							int k1 = 11;
							newRegularFont.drawBasicString("", k1, yPos + yOffset, changeChatArea ? 0xFFFFFF : 0,
								shadow);
							k1 += textDrawingArea.getTextWidth("");
							if (stonerRights >= 1)
							{
								modIcons[stonerRights - 1].drawSprite(k1, yPos - 12 + yOffset);
								k1 += 12;
							}
							newRegularFont.drawBasicString(s1 + " said: ", k1, yPos + yOffset,
								changeChatArea ? 0xFFFFFF : 0,
								shadow);
							k1 += textDrawingArea.getTextWidth(s1) + 8;
							newRegularFont.drawBasicString(chatMessages[k], k1 + 23, yPos + yOffset,
								changeChatArea ? 0xFF5256 : 0x800000, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 4 && (tradeMode == 0 || tradeMode == 1 && isStonerOrSelf(s1)))
					{
						if (chatTypeView == 3 || chatTypeView == 0)
						{
							newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 11, yPos + yOffset,
								changeChatArea ? 0xFF5256 : 0x800080, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 5 && splitPrivateChat == 0 && privateChatMode < 2)
					{
						if (chatTypeView == 2 || chatTypeView == 0)
						{
							newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 8, yPos + yOffset,
								changeChatArea ? 0xFF5256 : 0x800000, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 6 && (splitPrivateChat == 0 || chatTypeView == 2) && privateChatMode < 2)
					{
						if (chatTypeView == 2 || chatTypeView == 0)
						{
							newRegularFont.drawBasicString("You told " + s1 + ":", 11, yPos + yOffset,
								changeChatArea ? 0xFFFFFF : 0, shadow);
							newRegularFont.drawBasicString(chatMessages[k],
								15 + textDrawingArea.getTextWidth("You told :" + s1), yPos + yOffset,
								changeChatArea ? 0xFF5256 : 0x800000, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 8 && (tradeMode == 0 || tradeMode == 1 && isStonerOrSelf(s1)))
					{
						if (chatTypeView == 3 || chatTypeView == 0)
						{
							newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 11, yPos + yOffset,
								changeChatArea ? 0xFF5256 : 0x800000, shadow);
							j++;
							j77++;
						}
					}
					if (chatType == 11)
					{
						if (chatTypeView == 11)
						{
							newRegularFont.drawBasicString(s1 + " " + chatMessages[k], 8, yPos + yOffset,
								changeChatArea ? 0xFF5256 : 0x800000, shadow);
							j++;
							j77++;
						}
					}
				}
			}
			DrawingArea.defaultDrawingAreaSize();
			anInt1211 = j * 14 + 7 + 5;
			if (anInt1211 < 111)
			{
				anInt1211 = 111;
			}
			Client.drawScrollbar(114 + extendChatArea, anInt1211 - anInt1089 - 113, 7 + yOffset - extendChatArea, 496,
				anInt1211 + extendChatArea, changeChatArea);
			String title;
			if (myStoner != null && myStoner.title != null)
			{
				title = "<col=" + myStoner.titleColor + ">" + myStoner.title + " </col>";
			}
			else
			{
				title = "";
			}
			String stonerName;
			if (myStoner != null && myStoner.name != null)
			{
				stonerName = myStoner.name;
			}
			else
			{
				stonerName = TextClass.fixName(myUsername);
			}
			DrawingArea.setDrawingArea(140 + yOffset, 8, 509, 120 + yOffset);
			int xPos = 0;
			int yPos = 0;
			if (myPrivilege == 0)
			{
				cacheSprite[347].drawSprite(
					textDrawingArea
						.getTextWidth(myStoner.title + (title.equals(null) ? "" : " ") + stonerName + ": ") + 4,
					124 + yOffset);
				newRegularFont.drawBasicString(title + stonerName, 8, 136 + yOffset - 2,
					changeChatArea ? 0xFFFFFF : 0, changeChatArea ? 0 : -1);
				textDrawingArea.method385(changeChatArea ? 0xFFFFFF : 0, ": ", 136 + yOffset - 2, 17 + textDrawingArea
					.getTextWidth(myStoner.title + (title.equals(null) ? "" : " ") + 8 + stonerName));
				newRegularFont.drawBasicString(inputString + "*",
					22 + textDrawingArea
						.getTextWidth(myStoner.title + (title.equals(null) ? "" : " ") + stonerName + ": "),
					136 + yOffset - 2, changeChatArea ? 0x7FA9FF : 255, changeChatArea ? 0 : -1);
			}
			else if (myPrivilege >= 1)
			{
				modIcons[myPrivilege - 1].drawSprite(10 + xPos, 122 + yPos + yOffset);
				cacheSprite[347].drawSprite(
					textDrawingArea.getTextWidth(
						myStoner.title + (title.equals(null) ? "" : " ") + stonerName + ": ") + 18,
					124 + yOffset);
				xPos += 15;
				newRegularFont.drawBasicString(title + stonerName, 23, 136 + yOffset - 2,
					changeChatArea ? 0xFFFFFF : 0, changeChatArea ? 0 : -1);
				textDrawingArea.method385(changeChatArea ? 0xFFFFFF : 0, ": ", 136 + yOffset - 2, 38
					+ textDrawingArea.getTextWidth(myStoner.title + (title.equals(null) ? "" : " ") + stonerName));
				newRegularFont.drawBasicString(inputString + "*",
					23 + textDrawingArea.getTextWidth(
						myStoner.title + (title.equals(null) ? "" : " ") + stonerName + ": ") + 13,
					136 + yOffset - 2, changeChatArea ? 0x7FA9FF : 255, changeChatArea ? 0 : -1);
			}
			DrawingArea.defaultDrawingAreaSize();
			for (int i = 0; i < 505; i++)
			{
				int opacity = 100 - (int) (i / 5.05);
				DrawingArea.method340(0, 1, 121 + yOffset, opacity + 5, 7 + i);
			}
		}
		if (menuOpen)
		{
			Client.rightClickMenu(0, 0);
		}

		Rasterizer.anIntArray1472 = anIntArray1182;
	}

	public static void rightClickChatButtons()
	{
		if (MouseState.y >= frameHeight - 22 && MouseState.y <= frameHeight)
		{
			if (MouseState.x >= 5 && MouseState.x <= 61)
			{
				menuActionName[1] = "See All";
				menuActionID[1] = 999;
				menuActionRow = 2;
			}
			else if (MouseState.x >= 71 && MouseState.x <= 127)
			{
				menuActionName[1] = "See Game";
				menuActionID[1] = 998;
				menuActionRow = 2;
			}
			else if (MouseState.x >= 137 && MouseState.x <= 193)
			{
				menuActionName[1] = "Blind Public";
				menuActionID[1] = 997;
				menuActionName[2] = "Ignore Public";
				menuActionID[2] = 996;
				menuActionName[3] = "Stoners Public";
				menuActionID[3] = 995;
				menuActionName[4] = "Public";
				menuActionID[4] = 994;
				menuActionName[5] = "See Only Public";
				menuActionID[5] = 993;
				menuActionRow = 6;
			}
			else if (MouseState.x >= 203 && MouseState.x <= 259)
			{
				menuActionName[1] = "No PM";
				menuActionID[1] = 992;
				menuActionName[2] = "Stoners PM";
				menuActionID[2] = 991;
				menuActionName[3] = "Social PM";
				menuActionID[3] = 990;
				menuActionName[4] = "See Only PM";
				menuActionID[4] = 989;
				menuActionRow = 5;
			}
			else if (MouseState.x >= 269 && MouseState.x <= 325)
			{
				menuActionName[1] = "No Cult Chat";
				menuActionID[1] = 1003;
				menuActionName[2] = "Stoners Cult Chat";
				menuActionID[2] = 1002;
				menuActionName[3] = "Cult Chat";
				menuActionID[3] = 1001;
				menuActionName[4] = "See Only Cult Chat";
				menuActionID[4] = 1000;
				menuActionRow = 5;
			}
			else if (MouseState.x >= 335 && MouseState.x <= 391)
			{
				menuActionName[1] = "Dont Deal";
				menuActionID[1] = 987;
				menuActionName[2] = "Deal With Stoners";
				menuActionID[2] = 986;
				menuActionName[3] = "Deal Anyone";
				menuActionID[3] = 985;
				menuActionName[4] = "See Only Deals";
				menuActionID[4] = 984;
				menuActionRow = 5;
			}
			else if (MouseState.x >= 404 && MouseState.x <= 515)
			{
				menuActionName[1] = "Report Sucker";
				menuActionID[1] = 606;
				menuActionRow = 2;
			}
		}
	}

	public static int getChatColor()
	{
		int convertHexCode = Integer.parseInt(chatColorHex, 16);
		return convertHexCode;
	}

	public static void changeChat(String color, String name)
	{
		chatColorHex = color;
		sendFrame126("Color chosen: @or2@" + name, 37506);
		SettingHandler.save();
	}
	public static void drawSplitPrivateChat()
	{
		if (splitPrivateChat == 0)
		{
			return;
		}
		TextDrawingArea textDrawingArea = regularText;
		int i = 0;
		if (anInt1104 != 0)
		{
			i = 1;
		}
		for (int j = 0; j < 100; j++)
		{
			if (chatMessages[j] != null)
			{
				int k = chatTypes[j];
				String s = chatNames[j];
				byte byte1 = 0;
				if (s != null && s.startsWith("@cr1@"))
				{
					s = s.substring(5);
					byte1 = 1;
				}
				if (s != null && s.startsWith("@cr2@"))
				{
					s = s.substring(5);
					byte1 = 2;
				}
				if (s != null && s.startsWith("@cr3@"))
				{
					s = s.substring(5);
					byte1 = 3;
				}
				if (s != null && s.startsWith("@cr4@"))
				{
					s = s.substring(5);
					byte1 = 4;
				}
				if (s != null && s.startsWith("@cr5@"))
				{
					s = s.substring(5);
					byte1 = 5;
				}
				if (s != null && s.startsWith("@cr6@"))
				{
					s = s.substring(5);
					byte1 = 6;
				}
				if (s != null && s.startsWith("@cr7@"))
				{
					s = s.substring(5);
					byte1 = 7;
				}
				if (s != null && s.startsWith("@cr8@"))
				{
					s = s.substring(5);
					byte1 = 8;
				}
				if (s != null && s.startsWith("@cr9@"))
				{
					s = s.substring(5);
					byte1 = 9;
				}
				if (s != null && s.startsWith("@cr10@"))
				{
					s = s.substring(6);
					byte1 = 10;
				}
				if (s != null && s.startsWith("@cr11@"))
				{
					s = s.substring(6);
					byte1 = 11;
				}
				if (s != null && s.startsWith("@cr12@"))
				{
					s = s.substring(6);
					byte1 = 12;
				}
				if (s != null && s.startsWith("@cr13@"))
				{
					s = s.substring(6);
					byte1 = 13;
				}
				if (s != null && s.startsWith("@cr14@"))
				{
					s = s.substring(6);
					byte1 = 14;
				}
				if ((k == 3 || k == 7)
					&& (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isStonerOrSelf(s)))
				{
					int l = 329 - i * 13;

					l = frameHeight - 170 - i * 13 - extendChatArea;

					int k1 = 4;
					textDrawingArea.method385(0, "Stoner ", l, k1);
					textDrawingArea.method385(getChatColor(), "Stoner ", l - 1, k1);
					k1 += textDrawingArea.getTextWidth("Stoner ");
					if (byte1 >= 1)
					{
						modIcons[byte1 - 1].drawSprite(k1 - 3, l - 15);
						k1 += 12;
					}
					textDrawingArea.method385(0, s + " said: " + chatMessages[j], l, k1);
					textDrawingArea.method385(getChatColor(), s + " said: " + chatMessages[j], l - 1, k1);
					if (++i >= 5)
					{
						return;
					}
				}
				if (k == 5 && privateChatMode < 2)
				{
					int i1 = 329 - i * 13;

					i1 = frameHeight - 170 - i * 13 - extendChatArea;

					textDrawingArea.method385(0, chatMessages[j], i1, 4);
					textDrawingArea.method385(getChatColor(), chatMessages[j], i1 - 1, 4);
					if (++i >= 5)
					{
						return;
					}
				}
				if (k == 6 && privateChatMode < 2)
				{
					int j1 = 329 - i * 13;

					j1 = frameHeight - 170 - i * 13 - extendChatArea;

					textDrawingArea.method385(0, "You told " + s + ": " + chatMessages[j], j1, 4);
					textDrawingArea.method385(getChatColor(), "You told " + s + ": " + chatMessages[j], j1 - 1, 4);
					if (++i >= 5)
					{
						return;
					}
				}
			}


		}
	}

	public static void buildSplitPrivateChatMenu()
	{
		if (splitPrivateChat == 0)
			return;
		int i = 0;
		if (anInt1104 != 0)
			i = 1;
		for (int j = 0; j < 100; j++)
			if (chatMessages[j] != null)
			{
				int k = chatTypes[j];
				String s = chatNames[j];
				if (s != null && s.startsWith("@cr"))
				{
					if (s.charAt(4) != '@')
					{
						s = s.substring(6);
					}
					else
					{
						s = s.substring(5);
					}
				}
				if ((k == 3 || k == 7)
					&& (k == 7 || privateChatMode == 0 || privateChatMode == 1 && isStonerOrSelf(s)))
				{
					int offSet = 0;
					int l = 329 - i * 13;

					l = frameHeight - 170 - i * 13 - extendChatArea;

					if (MouseState.x > 4 && MouseState.y - offSet > l - 10 && MouseState.y - offSet <= l + 3)
					{
						int i1 = regularText.getTextWidth("From:  " + s + chatMessages[j]) + 25;
						if (i1 > 450)
							i1 = 450;
						if (MouseState.x < 4 + i1)
						{
							if (myPrivilege >= 1)
							{
								menuActionName[menuActionRow] = "Report sucker @whi@" + s;
								menuActionID[menuActionRow] = 2606;
								menuActionRow++;
							}
							menuActionName[menuActionRow] = "Fuck off @whi@" + s;
							menuActionID[menuActionRow] = 2042;
							menuActionRow++;
							menuActionName[menuActionRow] = "Add stoner @whi@" + s;
							menuActionID[menuActionRow] = 2337;
							menuActionRow++;
						}
					}
					if (++i >= 5)
						return;
				}
				if ((k == 5 || k == 6) && privateChatMode < 2 && ++i >= 5)
					return;
			}

	}

	public static void buildStonerChat(int j)
	{
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++)
		{
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 2)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (s != null && s.startsWith("@cr"))
			{
				if (s.charAt(4) != '@')
				{
					s = s.substring(6);
				}
				else
				{
					s = s.substring(5);
				}
			}
			if ((j1 == 5 || j1 == 6) && (splitPrivateChat == 0 || chatTypeView == 2)
				&& (j1 == 6 || privateChatMode == 0 || privateChatMode == 1 && isStonerOrSelf(s)))
				l++;
			if ((j1 == 3 || j1 == 7) && (splitPrivateChat == 0 || chatTypeView == 2)
				&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1)
				{
					if (myPrivilege >= 1)
					{
						menuActionName[menuActionRow] = "Report sucker @gry@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Fuck you @red@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add Stoner @gre@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	public static void buildDuelorTrade(int j)
	{
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++)
		{
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 3 && chatTypeView != 4)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (s != null && s.startsWith("@cr"))
			{
				if (s.charAt(4) != '@')
				{
					s = s.substring(6);
				}
				else
				{
					s = s.substring(5);
				}
			}
			if (chatTypeView == 3 && j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1)
				{
					menuActionName[menuActionRow] = "Accept deal @whi@" + s;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if (chatTypeView == 4 && j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1)
				{
					menuActionName[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 12)
			{
				if (j > k1 - 14 && j <= k1)
				{
					menuActionName[menuActionRow] = "Go-to @blu@" + s;
					menuActionID[menuActionRow] = 915;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	public static void buildChatAreaMenu(int j)
	{
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++)
		{
			if (chatMessages[i1] == null)
				continue;
			int j1 = chatTypes[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			String s = chatNames[i1];
			if (chatTypeView == 1)
			{
				buildPublicChat(j);
				break;
			}
			if (chatTypeView == 2)
			{
				buildStonerChat(j);
				break;
			}
			if (chatTypeView == 3 || chatTypeView == 4)
			{
				buildDuelorTrade(j);
				break;
			}
			if (chatTypeView == 5)
			{
				break;
			}
			if (s != null && s.startsWith("@cr"))
			{
				if (s.charAt(4) != '@')
				{
					s = s.substring(6);
				}
				else
				{
					s = s.substring(5);
				}
			}
			if (j1 == 0)
				l++;
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1 && !s.equals(myStoner.name))
				{
					if (myPrivilege >= 1)
					{
						menuActionName[menuActionRow] = "Report sucker @gry@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Fuck you @red@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add stoner @gre@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 3 || j1 == 7) && splitPrivateChat == 0
				&& (j1 == 7 || privateChatMode == 0 || privateChatMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1)
				{
					if (myPrivilege >= 1)
					{
						menuActionName[menuActionRow] = "Report sucker @gry@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Fuck you @red@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add stoner @gre@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
			if (j1 == 4 && (tradeMode == 0 || tradeMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1)
				{
					menuActionName[menuActionRow] = "Accept deal @whi@" + s;
					menuActionID[menuActionRow] = 484;
					menuActionRow++;
				}
				l++;
			}
			if ((j1 == 5 || j1 == 6) && splitPrivateChat == 0 && privateChatMode < 2)
				l++;
			if (j1 == 8 && (tradeMode == 0 || tradeMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1)
				{
					menuActionName[menuActionRow] = "Accept challenge @whi@" + s;
					menuActionID[menuActionRow] = 6;
					menuActionRow++;
				}
				l++;
			}
		}
	}

	public static void buildPublicChat(int j)
	{
		int l = 0;
		for (int i1 = 0; i1 < 500; i1++)
		{
			if (chatMessages[i1] == null)
				continue;
			if (chatTypeView != 1)
				continue;
			int j1 = chatTypes[i1];
			String s = chatNames[i1];
			int k1 = (70 - l * 14 + 42) + anInt1089 + 4 + 5;
			if (s != null && s.startsWith("@cr"))
			{
				if (s.charAt(4) != '@')
				{
					s = s.substring(6);
				}
				else
				{
					s = s.substring(5);
				}
			}
			if ((j1 == 1 || j1 == 2) && (j1 == 1 || publicChatMode == 0 || publicChatMode == 1 && isStonerOrSelf(s)))
			{
				if (j > k1 - 14 && j <= k1 && !s.equals(myStoner.name))
				{
					if (myPrivilege >= 1)
					{
						menuActionName[menuActionRow] = "Report sucker @gry@" + s;
						menuActionID[menuActionRow] = 606;
						menuActionRow++;
					}
					menuActionName[menuActionRow] = "Fuck you @red@" + s;
					menuActionID[menuActionRow] = 42;
					menuActionRow++;
					menuActionName[menuActionRow] = "Add stoner @gre@" + s;
					menuActionID[menuActionRow] = 337;
					menuActionRow++;
				}
				l++;
			}
		}
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
	public static void replyToPM()
	{
		String name = null;
		for (int k = 0; k < 100; k++)
		{
			if (chatMessages[k] == null)
			{
				continue;
			}
			int l = chatTypes[k];
			if ((l == 3) || (l == 7))
			{
				name = Chatbox.chatNames[k];
				break;
			}
		}
		if (name == null)
		{
			pushMessage("No one needs you right now, ease up and smoke weed.", 0, "");
			return;
		}
		if (name != null)
		{
			if (name.indexOf("@") == 0)
			{
				name = name.substring(5);
			}
		}
		long nameAsLong = TextClass.longForName(name.trim());
		int k3 = -1;
		for (int i4 = 0; i4 < stonersCount; i4++)
		{
			if (stonersListAsLongs[i4] != nameAsLong)
			{
				continue;
			}
			k3 = i4;
			break;
		}
		if (k3 != -1)
		{
			if (stonersNodeIDs[k3] > 0)
			{
				Client.inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				Client.promptInput = "";
				Client.stonersListAction = 3;
				Client.aLong953 = stonersListAsLongs[k3];
				Client.aString1121 = "Say what u want to " + stonersList[k3];
			}
			else
			{
				pushMessage("That stoner is way too stoned.", 0, "");
			}
		}
	}
}
