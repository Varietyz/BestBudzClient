package com.bestbudz.ui;

import static com.bestbudz.cache.ResetIDKits.resetIdentityKits;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.ui.InterfaceManagement.clearTopInterfaces;
import static com.bestbudz.ui.InterfaceManagement.drawInterfaceRecursive;
import com.bestbudz.entity.IdentityKit;
import static com.bestbudz.ui.InterfaceManagement.updateInterfaceAnimations;
import static com.bestbudz.ui.interfaces.Chatbox.reportAbuseInput;
import com.bestbudz.ui.interfaces.CustomInterfaces;
import com.bestbudz.util.TextClass;

public class DialogHandling extends Client
{
	public static void handleBackDialogOrChatbox() {
		if (backDialogID == -1) {
			aClass9_1059.scrollPosition = anInt1211 - anInt1089 - 110;
			if (MouseState.x >= 496 && MouseState.x <= 511 && MouseState.y > frameHeight - 158) {
				drawInterfaceRecursive(494, 110, MouseState.x,
					MouseState.y - (frameHeight - 158), aClass9_1059, 0,
					false, anInt1211);
			}
			int idealScroll = anInt1211 - 110 - aClass9_1059.scrollPosition;
			idealScroll = Math.max(0, Math.min(idealScroll, anInt1211 - 110));
			if (anInt1089 != idealScroll) {
				anInt1089 = idealScroll;
				inputTaken = true;
			}
		} else {
			if (updateInterfaceAnimations(anInt945, backDialogID)) inputTaken = true;
		}
		if (atBoxInterfaceType == 3 || activeInterfaceType == 3 || aString844 != null || (menuOpen && menuScreenArea == 2))
			inputTaken = true;
	}

	public static void handleBoxDialogue() {
		if (atBoxInterfaceType == 0) return;
		atBoxLoopCycle++;
		if (atBoxLoopCycle < 15) return;
		if (atBoxInterfaceType == 3) inputTaken = true;
		atBoxInterfaceType = 0;
	}
	public static boolean promptUserForInput(RSInterface class9)
	{
		int j = class9.contentType;
		System.out.println(anInt900);
		if (anInt900 == 2)
		{
			if (j == 51504)
			{
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				stonersListAction = 51504;
				aString1121 = "Enter the stoner's profile you want to view.";
			}
			if (j == 201)
			{
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				stonersListAction = 1;
				aString1121 = "Enter name of stoner to add to list";
			}
			if (j == 59800)
			{
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				stonersListAction = 59800;
				aString1121 = "Enter the item name you are looking for";
			}
			if (j == 202)
			{
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				stonersListAction = 2;
				aString1121 = "Enter name of stoner to delete from list";
			}
		}
		if (j == 0xBABE)
		{
			int subShopIndex = (8 + (CustomInterfaces.shopCategories.length * 3)) - 1;
			for (int index = 0, frame = 0; index < (CustomInterfaces.shopCategories.length * 3); index += 3)
			{
				if (class9.id == (55000 + frame + 9))
				{
					RSInterface.interfaceCache[55000].children[subShopIndex] = 55000 + frame + 13;
					return true;
				}
				frame += (CustomInterfaces.shopContent[index / 3].length * 8) + 5;
			}
			return true;
		}
		if (j == 205)
		{
			anInt1011 = 250;
			return true;
		}
		if (j == 501)
		{
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			stonersListAction = 4;
			aString1121 = "Enter name of stoner to add to list";
		}
		if (j == 502)
		{
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			stonersListAction = 5;
			aString1121 = "Enter name of stoner to delete from list";
		}
		if (j == 550)
		{
			inputTaken = true;
			inputDialogState = 0;
			messagePromptRaised = true;
			promptInput = "";
			stonersListAction = 6;
			aString1121 = "Enter the name of the chat you wish to join";
		}
		if (j >= 300 && j <= 313)
		{
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = anIntArray1065[k];
			if (i2 != -1)
			{
				do
				{
					if (j1 == 0 && --i2 < 0)
						i2 = IdentityKit.length - 1;
					if (j1 == 1 && ++i2 >= IdentityKit.length)
						i2 = 0;
				} while (IdentityKit.cache[i2].aBoolean662
					|| IdentityKit.cache[i2].anInt657 != k + (aBoolean1047 ? 0 : 7));
				anIntArray1065[k] = i2;
				aBoolean1031 = true;
			}
		}
		if (j >= 314 && j <= 323)
		{
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = anIntArray990[l];
			if (k1 == 0 && --j2 < 0)
				j2 = anIntArrayArray1003[l].length - 1;
			if (k1 == 1 && ++j2 >= anIntArrayArray1003[l].length)
				j2 = 0;
			anIntArray990[l] = j2;
			aBoolean1031 = true;
		}
		if (j == 324 && !aBoolean1047)
		{
			aBoolean1047 = true;
			resetIdentityKits();
		}
		if (j == 325 && aBoolean1047)
		{
			aBoolean1047 = false;
			resetIdentityKits();
		}
		if (j == 326)
		{
			stream.createFrame(101);
			stream.writeWordBigEndian(aBoolean1047 ? 0 : 1);
			for (int i1 = 0; i1 < 7; i1++)
				stream.writeWordBigEndian(anIntArray1065[i1]);

			for (int l1 = 0; l1 < 5; l1++)
				stream.writeWordBigEndian(anIntArray990[l1]);

			return true;
		}
		if (j == 613)
			canMute = !canMute;
		if (j >= 601 && j <= 612)
		{
			clearTopInterfaces();
			if (reportAbuseInput.length() > 0)
			{
				stream.createFrame(218);
				stream.writeQWord(TextClass.longForName(reportAbuseInput));
				stream.writeWordBigEndian(j - 601);
				stream.writeWordBigEndian(canMute ? 1 : 0);
			}
		}
		return false;
	}
	public static void sendFrame36(int id, int state)
	{
		anIntArray1045[id] = state;
		if (variousSettings[id] != state)
		{
			variousSettings[id] = state;
			updateConfigValues(id);
			if (dialogID != -1)
				inputTaken = true;
		}
	}

	public static void sendFrame248(int interfaceID, int sideInterfaceID)
	{
		if (backDialogID != -1)
		{
			backDialogID = -1;
			inputTaken = true;
		}
		if (inputDialogState != 0)
		{
			inputDialogState = 0;
			inputTaken = true;
		}
		openInterfaceID = interfaceID;
		invOverlayInterfaceID = sideInterfaceID;
		tabAreaAltered = true;
		aBoolean1149 = false;
	}
}
