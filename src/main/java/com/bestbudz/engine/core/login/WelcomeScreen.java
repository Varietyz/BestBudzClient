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
			if (j == 1 && anInt900 == 0)
			{
				class9.disabledMessage = "Loading stoner list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && anInt900 == 1)
			{
				class9.disabledMessage = "Connecting to stonerserver";
				class9.atActionType = 0;
				return;
			}
			if (j == 2 && anInt900 != 2)
			{
				class9.disabledMessage = "Getting high...";
				class9.atActionType = 0;
				return;
			}
			int k = stonersCount;
			if (anInt900 != 2)
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
			if (anInt900 != 2)
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
			if (anInt900 != 2)
				i1 = 0;
			class9.scrollMax = i1 * 15 + 20;
			if (class9.scrollMax <= class9.height)
				class9.scrollMax = class9.height + 1;
			return;
		}
		if (j >= 401 && j <= 500)
		{
			if ((j -= 401) == 0 && anInt900 == 0)
			{
				class9.disabledMessage = "Loading shithead list";
				class9.atActionType = 0;
				return;
			}
			if (j == 1 && anInt900 == 0)
			{
				class9.disabledMessage = "Getting high...";
				class9.atActionType = 0;
				return;
			}
			int j1 = ignoreCount;
			if (anInt900 == 0)
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
			if (aBoolean1031)
			{
				for (int k1 = 0; k1 < 7; k1++)
				{
					int l1 = anIntArray1065[k1];
					if (l1 >= 0 && !IdentityKit.cache[l1].method537())
						return;
				}

				aBoolean1031 = false;
				Model[] aclass30_sub2_sub4_sub6s = new Model[7];
				int i2 = 0;
				for (int j2 = 0; j2 < 7; j2++)
				{
					int k2 = anIntArray1065[j2];
					if (k2 >= 0)
						aclass30_sub2_sub4_sub6s[i2++] = IdentityKit.cache[k2].method538();
				}

				Model model = new Model(i2, aclass30_sub2_sub4_sub6s);
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0)
					{
						model.replaceColor(anIntArrayArray1003[l2][0], anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							model.replaceColor(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}

				model.calculateNormals();
				model.method470(Animation.anims[myStoner.anInt1511].anIntArray353[0]);
				model.applyLighting(64, 850, -30, -50, -30, true);
				class9.anInt233 = 5;
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
			if (aBoolean1031)
			{
				Model characterDisplay = myStoner.buildPlayerModel();
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0)
					{
						characterDisplay.replaceColor(anIntArrayArray1003[l2][0],
							anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}
				int staticFrame = myStoner.anInt1511;
				characterDisplay.calculateNormals();
				characterDisplay.interpolateFrames(Animation.anims[staticFrame].anIntArray353[0], -1, -1, -1);
				rsInterface.anInt233 = 5;
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

			if (aBoolean1031)
			{
				Model characterDisplay = stoner.buildPlayerModel();
				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0)
					{
						characterDisplay.replaceColor(anIntArrayArray1003[l2][0],
							anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}
				int staticFrame = stoner.anInt1511;
				characterDisplay.calculateNormals();
				characterDisplay.interpolateFrames(Animation.anims[staticFrame].anIntArray353[0], -1, -1, -1);
				rsInterface.anInt233 = 5;
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
			if (aBoolean1031)
			{
				Model characterDisplay = npcDisplay.getFinalRenderedModel();

				if (characterDisplay == null)
				{
					return;
				}

				for (int l2 = 0; l2 < 5; l2++)
					if (anIntArray990[l2] != 0)
					{
						characterDisplay.replaceColor(anIntArrayArray1003[l2][0],
							anIntArrayArray1003[l2][anIntArray990[l2]]);
						if (l2 == 1)
							characterDisplay.replaceColor(anIntArray1204[0], anIntArray1204[anIntArray990[l2]]);
					}
				int staticFrame = npcDisplay.anInt1511;
				characterDisplay.calculateNormals();
				characterDisplay.method470(Animation.anims[staticFrame].anIntArray353[0]);
				characterDisplay.applyLighting(64, 850, -30, -50, -30, true);
				rsInterface.anInt233 = 5;
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
			if (aBoolean1047)
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
			if (aBoolean1047)
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
			if (anInt1193 != 0)
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
