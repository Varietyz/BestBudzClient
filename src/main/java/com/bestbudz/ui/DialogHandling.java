package com.bestbudz.ui;

import static com.bestbudz.cache.ResetIDKits.resetIdentityKits;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import com.bestbudz.cache.IdentityKit;
import static com.bestbudz.ui.InterfaceManagement.updateInterfaceAnimations;
import com.bestbudz.ui.interfaces.CustomInterfaces;

public class DialogHandling extends Client
{
	public static void handleBackDialogOrChatbox() {
		if (backDialogID == -1) {
			defaultInterface.scrollPosition = anInt1211 - loadingProgress - 110;

			int idealScroll = anInt1211 - 110 - defaultInterface.scrollPosition;
			idealScroll = Math.max(0, Math.min(idealScroll, anInt1211 - 110));
			if (loadingProgress != idealScroll) {
				loadingProgress = idealScroll;
				inputTaken = true;
			}
		} else {
			if (updateInterfaceAnimations(gameTickCounter, backDialogID)) inputTaken = true;
		}
		if (atBoxInterfaceType == 3 || activeInterfaceType == 3 || inputPromptText != null || (menuOpen && menuScreenArea == 2))
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

		System.out.println("=== DIALOG HANDLING DEBUG START ===");
		System.out.println("[Dialog Handling] anInt900: " + friendsListAction);
		System.out.println("[Dialog Handling] contentType (j): " + j);
		System.out.println("[Dialog Handling] interface ID: " + class9.id);
		System.out.println("[Dialog Handling] interface type: " + class9.type);
		System.out.println("[Dialog Handling] inputTaken before: " + inputTaken);
		System.out.println("[Dialog Handling] messagePromptRaised before: " + messagePromptRaised);
		System.out.println("[Dialog Handling] inputDialogState before: " + inputDialogState);
		System.out.println("[Dialog Handling] stonersListAction before: " + stonersListAction);

		if (friendsListAction == 2)
		{
			System.out.println("[Dialog Handling] Processing anInt900 == 2 branch");

			if (j == 51504)
			{
			}
			if (j == 201)
			{
			}
			if (j == 59800)
			{
				System.out.println("[Dialog Handling] MATCHED: contentType 59800 - Item search");
				inputTaken = true;
				inputDialogState = 0;
				messagePromptRaised = true;
				promptInput = "";
				stonersListAction = 59800;
				aString1121 = "Enter the item name you are looking for";
				System.out.println("[Dialog Handling] Set stonersListAction to: " + stonersListAction);
				System.out.println("[Dialog Handling] Set prompt message: " + aString1121);
			}
			if (j == 202)
			{
			}

			if (j != 51504 && j != 201 && j != 59800 && j != 202) {
				System.out.println("[Dialog Handling] WARNING: No contentType match found for j=" + j + " in anInt900==2 branch");
			}
		}

		if (j == 0xBABE)
		{
			System.out.println("[Dialog Handling] MATCHED: contentType 0xBABE (shop handling)");
			int subShopIndex = (8 + (CustomInterfaces.shopCategories.length * 3)) - 1;
			System.out.println("[Dialog Handling] subShopIndex calculated: " + subShopIndex);

			for (int index = 0, frame = 0; index < (CustomInterfaces.shopCategories.length * 3); index += 3)
			{
				if (class9.id == (55000 + frame + 9))
				{
					System.out.println("[Dialog Handling] Shop category matched, setting child: " + (55000 + frame + 13));
					RSInterface.interfaceCache[55000].children[subShopIndex] = 55000 + frame + 13;
					return true;
				}
				frame += (CustomInterfaces.shopContent[index / 3].length * 8) + 5;
			}
			return true;
		}

		if (j == 205)
		{
			System.out.println("[Dialog Handling] MATCHED: contentType 205");
			connectionTimeout = 250;
			System.out.println("[Dialog Handling] Set anInt1011 to: " + connectionTimeout);
			return true;
		}

		if (j == 501)
		{

		}

		if (j == 502)
		{

		}

		if (j == 550)
		{

		}

		if (j >= 300 && j <= 313)
		{
			System.out.println("[Dialog Handling] MATCHED: Identity kit range (300-313), contentType: " + j);
			int k = (j - 300) / 2;
			int j1 = j & 1;
			int i2 = musicTrackIds[k];
			System.out.println("[Dialog Handling] Identity kit - k: " + k + ", j1: " + j1 + ", i2: " + i2);

			if (i2 != -1)
			{
				do
				{
					if (j1 == 0 && --i2 < 0)
						i2 = IdentityKit.length - 1;
					if (j1 == 1 && ++i2 >= IdentityKit.length)
						i2 = 0;
				} while (IdentityKit.cache[i2].aBoolean662
					|| IdentityKit.cache[i2].anInt657 != k + (welcomeScreenVisible ? 0 : 7));
				musicTrackIds[k] = i2;
				dragModeActive = true;
				System.out.println("[Dialog Handling] Identity kit updated - new i2: " + i2);
			}
		}

		if (j >= 314 && j <= 323)
		{
			System.out.println("[Dialog Handling] MATCHED: Color range (314-323), contentType: " + j);
			int l = (j - 314) / 2;
			int k1 = j & 1;
			int j2 = cameraShakeOffsets[l];
			System.out.println("[Dialog Handling] Color - l: " + l + ", k1: " + k1 + ", j2: " + j2);

			if (k1 == 0 && --j2 < 0)
				j2 = validInterfaceRegions[l].length - 1;
			if (k1 == 1 && ++j2 >= validInterfaceRegions[l].length)
				j2 = 0;
			cameraShakeOffsets[l] = j2;
			dragModeActive = true;
			System.out.println("[Dialog Handling] Color updated - new j2: " + j2);
		}

		if (j == 324 && !welcomeScreenVisible)
		{
			System.out.println("[Dialog Handling] MATCHED: contentType 324 - Gender change to male");
			welcomeScreenVisible = true;
			resetIdentityKits();
			System.out.println("[Dialog Handling] Reset identity kits, aBoolean1047 now: " + welcomeScreenVisible);
		}

		if (j == 325 && welcomeScreenVisible)
		{
			System.out.println("[Dialog Handling] MATCHED: contentType 325 - Gender change to female");
			welcomeScreenVisible = false;
			resetIdentityKits();
			System.out.println("[Dialog Handling] Reset identity kits, aBoolean1047 now: " + welcomeScreenVisible);
		}

		if (j == 326)
		{
			System.out.println("[Dialog Handling] MATCHED: contentType 326 - Confirm character creation");
			stream.writeEncryptedOpcode(101);
			stream.writeByte(welcomeScreenVisible ? 0 : 1);
			System.out.println("[Dialog Handling] Writing gender: " + (welcomeScreenVisible ? 0 : 1));

			for (int i1 = 0; i1 < 7; i1++) {
				stream.writeByte(musicTrackIds[i1]);
				System.out.println("[Dialog Handling] Writing identity kit[" + i1 + "]: " + musicTrackIds[i1]);
			}

			for (int l1 = 0; l1 < 5; l1++) {
				stream.writeByte(cameraShakeOffsets[l1]);
				System.out.println("[Dialog Handling] Writing color[" + l1 + "]: " + cameraShakeOffsets[l1]);
			}
			return true;
		}

		if (j == 613) {
			System.out.println("[Dialog Handling] MATCHED: contentType 613 - Toggle mute");
			canMute = !canMute;
			System.out.println("[Dialog Handling] canMute toggled to: " + canMute);
		}

		if (j >= 601 && j <= 612)
		{
		}

		System.out.println("[Dialog Handling] inputTaken after: " + inputTaken);
		System.out.println("[Dialog Handling] messagePromptRaised after: " + messagePromptRaised);
		System.out.println("[Dialog Handling] inputDialogState after: " + inputDialogState);
		System.out.println("[Dialog Handling] stonersListAction after: " + stonersListAction);
		System.out.println("=== DIALOG HANDLING DEBUG END ===");

		return false;
	}
	public static void sendFrame36(int id, int state)
	{
		if (id < 0 || id >= variousSettings.length) return;
		experienceDrops[id] = state;
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
		isPlayerBusy = false;
	}
}
