package com.bestbudz.engine.core.login;

import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.cache.IdentityKit;
import com.bestbudz.entity.Stoner;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.ui.RSInterface;
import static com.bestbudz.ui.interfaces.Chatbox.reportAbuseInput;
import com.bestbudz.graphics.text.TextClass;

public class WelcomeScreen extends Client
{
	public static void drawStonersListOrWelcomeScreen(RSInterface class9)
	{
		int j = class9.contentType;
		if (j >= 1 && j <= 100 || j >= 701 && j <= 800)
		{
			if (j == 1 && friendsListAction == 0)
			{
				class9.disabledMessage = "Loading stoner list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && friendsListAction == 1)
			{
				class9.disabledMessage = "Connecting to stonerserver";
				class9.atActionType = 0;
				return;
			}
			if (j == 2 && friendsListAction != 2)
			{
				class9.disabledMessage = "Getting high...";
				class9.atActionType = 0;
				return;
			}
			int k = stonersCount;
			if (friendsListAction != 2)
				k = 0;
			if (j > 700)
				j -= 601;
			else
				j--;
			if (j >= k)
			{
				class9.disabledMessage = "";
				class9.atActionType = 0;
				return;
			}
			else
			{
				class9.disabledMessage = stonersList[j];
				class9.atActionType = 1;
				return;
			}
		}
		if (j >= 101 && j <= 200 || j >= 801 && j <= 900)
		{
			int l = stonersCount;
			if (friendsListAction != 2)
				l = 0;
			if (j > 800)
				j -= 701;
			else
				j -= 101;
			if (j >= l)
			{
				class9.disabledMessage = "";
				class9.atActionType = 0;
				return;
			}
			if (stonersNodeIDs[j] == 0)
				class9.disabledMessage = "@red@Asleep";
			else if (stonersNodeIDs[j] == nodeID)
				class9.disabledMessage = "@gre@High";
			else
				class9.disabledMessage = "@red@Asleep";
			class9.atActionType = 1;
			return;
		}
		if (j == 203)
		{
			int i1 = stonersCount;
			if (friendsListAction != 2)
				i1 = 0;
			class9.scrollMax = i1 * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j >= 401 && j <= 500)
		{
			if ((j -= 401) == 0 && friendsListAction == 0)
			{
				class9.disabledMessage = "Loading shithead list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && friendsListAction == 0)
			{
				class9.disabledMessage = "Getting high...";
				class9.atActionType = 0;
				return;
			}
			int j1 = ignoreCount;
			if (friendsListAction == 0)
				j1 = 0;
			if (j >= j1)
			{
				class9.disabledMessage = "";
				class9.atActionType = 0;
				return;
			}
			else
			{
				class9.disabledMessage = TextClass.fixName(TextClass.nameForLong(ignoreListAsLongs[j]));
				class9.atActionType = 1;
				return;
			}
		}
		if (j == 901)
		{
			class9.disabledMessage = stonersCount + " / 200";
			return;
		}
		if (j == 902)
		{
			class9.disabledMessage = ignoreCount + " / 100";
			return;
		}
		if (j == 503)
		{
			class9.scrollMax = ignoreCount * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j == 327)
		{
			class9.modelRotation1 = 150;
			class9.modelRotation2 = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			if (dragModeActive)
			{
				for (int k1 = 0; k1 < 7; k1++)
				{
					int l1 = musicTrackIds[k1];
					if (l1 >= 0 && !IdentityKit.cache[l1].method537())
						return;
				}

				dragModeActive = false;
				Model[] aclass30_sub2_sub4_sub6s = new Model[7];
				int i2 = 0;
				for (int j2 = 0; j2 < 7; j2++)
				{
					int k2 = musicTrackIds[j2];
					if (k2 >= 0)
						aclass30_sub2_sub4_sub6s[i2++] = IdentityKit.cache[k2].method538();
				}

				Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
				for (int l2 = 0; l2 < 5; l2++)
					if (cameraShakeOffsets[l2] != 0)
					{
						model.replaceColor(validInterfaceRegions[l2][0], validInterfaceRegions[l2][cameraShakeOffsets[l2]]);
						if (l2 == 1)
							model.replaceColor(chatSystemColors[0], chatSystemColors[cameraShakeOffsets[l2]]);
					}

				model.calculateNormals();
				model.applyTransformation(Animation.anims[myStoner.standAnimation].frameIds[0]);
				model.applyLighting(64, 850, -30, -50, -30, true);
				class9.modelType = 5;
				class9.mediaID = 0;
				RSInterface.method208(aBoolean994, model);
			}
			return;
		}
		if (j == 328)
		{
			RSInterface rsInterface = class9;
			int verticleTilt = 150;
			int animationSpeed = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			rsInterface.modelRotation1 = verticleTilt;
			rsInterface.modelRotation2 = animationSpeed;
			if (dragModeActive)
			{
				Model characterDisplay = myStoner.buildPlayerModel();
				for (int l2 = 0; l2 < 5; l2++)
					if (cameraShakeOffsets[l2] != 0)
					{
						characterDisplay.replaceColor(validInterfaceRegions[l2][0],
							validInterfaceRegions[l2][cameraShakeOffsets[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(chatSystemColors[0], chatSystemColors[cameraShakeOffsets[l2]]);
					}
				int staticFrame = myStoner.standAnimation;
				characterDisplay.calculateNormals();
				characterDisplay.interpolateFrames(Animation.anims[staticFrame].frameIds[0], -1, -1, -1);
				rsInterface.modelType = 5;
				rsInterface.mediaID = 0;
				RSInterface.method208(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 330)
		{
			if (stonerIndex >= stonerArray.length || stonerIndex < 0)
			{
				return;
			}

			Stoner stoner = stonerArray[stonerIndex];

			if (stoner == null)
			{
				return;
			}

			RSInterface rsInterface = class9;

			int verticleTilt = 150;
			int animationSpeed = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			rsInterface.modelRotation1 = verticleTilt;
			rsInterface.modelRotation2 = animationSpeed;

			if (dragModeActive)
			{
				Model characterDisplay = stoner.buildPlayerModel();
				for (int l2 = 0; l2 < 5; l2++)
					if (cameraShakeOffsets[l2] != 0)
					{
						characterDisplay.replaceColor(validInterfaceRegions[l2][0],
							validInterfaceRegions[l2][cameraShakeOffsets[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(chatSystemColors[0], chatSystemColors[cameraShakeOffsets[l2]]);
					}
				int staticFrame = stoner.standAnimation;
				characterDisplay.calculateNormals();
				characterDisplay.interpolateFrames(Animation.anims[staticFrame].frameIds[0], -1, -1, -1);
				rsInterface.modelType = 5;
				rsInterface.mediaID = 0;
				RSInterface.method208(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 329)
		{
			if (npcDisplay == null)
			{
				return;
			}
			RSInterface rsInterface = class9;
			int verticleTilt = 150;
			int animationSpeed = (int) (Math.sin((double) loopCycle / 40D) * 256D) & 0x7ff;
			rsInterface.modelRotation1 = verticleTilt;
			rsInterface.modelRotation2 = animationSpeed;
			if (dragModeActive)
			{
				Model characterDisplay = npcDisplay.getModel();

				if (characterDisplay == null)
				{
					return;
				}

				for (int l2 = 0; l2 < 5; l2++)
					if (cameraShakeOffsets[l2] != 0)
					{
						characterDisplay.replaceColor(validInterfaceRegions[l2][0],
							validInterfaceRegions[l2][cameraShakeOffsets[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(chatSystemColors[0], chatSystemColors[cameraShakeOffsets[l2]]);
					}
				int staticFrame = npcDisplay.standAnimation;
				characterDisplay.calculateNormals();
				characterDisplay.applyTransformation(Animation.anims[staticFrame].frameIds[0]);
				characterDisplay.applyLighting(64, 850, -30, -50, -30, true);
				rsInterface.modelType = 5;
				rsInterface.mediaID = 0;
				RSInterface.method208(aBoolean994, characterDisplay);
			}
			return;
		}
		if (j == 324)
		{
			if (aClass30_Sub2_Sub1_Sub1_931 == null)
			{
				aClass30_Sub2_Sub1_Sub1_931 = class9.disabledSprite;
				aClass30_Sub2_Sub1_Sub1_932 = class9.enabledSprite;
			}
			if (welcomeScreenVisible)
			{
				class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
			else
			{
				class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
		}
		if (j == 325)
		{
			if (aClass30_Sub2_Sub1_Sub1_931 == null)
			{
				aClass30_Sub2_Sub1_Sub1_931 = class9.disabledSprite;
				aClass30_Sub2_Sub1_Sub1_932 = class9.enabledSprite;
			}
			if (welcomeScreenVisible)
			{
				class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_931;
				return;
			}
			else
			{
				class9.disabledSprite = aClass30_Sub2_Sub1_Sub1_932;
				return;
			}
		}
		if (j == 600)
		{
			class9.disabledMessage = reportAbuseInput;
			if (loopCycle % 20 < 10)
			{
				class9.disabledMessage += "|";
				return;
			}
			else
			{
				class9.disabledMessage += " ";
				return;
			}
		}
		if (j == 613)
			if (myPrivilege >= 1)
			{
				if (canMute)
				{
					class9.textColor = 0xff0000;
					class9.disabledMessage = "Moderator option: Mute stoner for 48 hours: <ON>";
				}
				else
				{
					class9.textColor = ColorConfig.WHITE_COLOR;
					class9.disabledMessage = "Moderator option: Mute stoner for 48 hours: <OFF>";
				}
			}
			else
			{
				class9.disabledMessage = "";
			}
		if (j == 650 || j == 655)
			if (minimapRegionX != 0)
			{
				String s;
				if (daysSinceLastLogin == 0)
					s = "earlier today";
				else if (daysSinceLastLogin == 1)
					s = "yesterday";
				else
					s = daysSinceLastLogin + " days ago";
				class9.disabledMessage = "You last logged in " + s + " from: " + Signlink.dns;
			}
			else
			{
				class9.disabledMessage = "";
			}
		if (j == 651)
		{
			if (unreadMessages == 0)
			{
				class9.disabledMessage = "0 unread messages";
				class9.textColor = 0xffff00;
			}
			if (unreadMessages == 1)
			{
				class9.disabledMessage = "1 unread message";
				class9.textColor = 65280;
			}
			if (unreadMessages > 1)
			{
				class9.disabledMessage = unreadMessages + " unread messages";
				class9.textColor = 65280;
			}
		}
		if (j == 652)
			if (daysSinceRecovChange == 201)
			{
				if (membersInt == 1)
					class9.disabledMessage = "@yel@This is a non-members world: @whi@Since you are a member we";
				else
					class9.disabledMessage = "";
			}
			else if (daysSinceRecovChange == 200)
			{
				class9.disabledMessage = "You have not yet set any password recovery questions.";
			}
			else
			{
				String s1;
				if (daysSinceRecovChange == 0)
					s1 = "Earlier today";
				else if (daysSinceRecovChange == 1)
					s1 = "Yesterday";
				else
					s1 = daysSinceRecovChange + " days ago";
				class9.disabledMessage = s1 + " you changed your recovery questions";
			}
		if (j == 653)
			if (daysSinceRecovChange == 201)
			{
				if (membersInt == 1)
					class9.disabledMessage = "@whi@recommend you use a members world instead. You may use";
				else
					class9.disabledMessage = "";
			}
			else if (daysSinceRecovChange == 200)
				class9.disabledMessage = "We strongly recommend you do so now to secure your account.";
			else
				class9.disabledMessage = "If you do not remember making this change then cancel it immediately";
		if (j == 654)
		{
			if (daysSinceRecovChange == 201)
				if (membersInt == 1)
				{
					class9.disabledMessage = "@whi@this world but member benefits are unavailable whilst here.";
					return;
				}
				else
				{
					class9.disabledMessage = "";
					return;
				}
			if (daysSinceRecovChange == 200)
			{
				class9.disabledMessage = "Do this from the 'account management' area on our front webpage";
				return;
			}
			class9.disabledMessage = "Do this from the 'account management' area on our front webpage";
		}
	}

	public static void clearWelcomeState() {
		welcomeScreenRaised = false;
		inputTaken = true;
		tabAreaAltered = true;
	}

}
