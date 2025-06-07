package com.bestbudz.ui.handling;

import com.bestbudz.data.ItemDef;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.input.MouseActions.componentIsClicked;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.network.packets.SendFrames.sendString;
import static com.bestbudz.ui.DialogHandling.promptUserForInput;
import static com.bestbudz.ui.DialogHandling.sendFrame248;
import static com.bestbudz.ui.InterfaceManagement.clearInterfaceAnimations;
import static com.bestbudz.ui.InterfaceManagement.clearTopInterfaces;
import static com.bestbudz.engine.core.login.Login.setNorth;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.entity.Npc;
import com.bestbudz.entity.Stoner;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.ui.interfaces.Chatbox;
import static com.bestbudz.ui.interfaces.Chatbox.privateChatMode;
import static com.bestbudz.ui.interfaces.Chatbox.publicChatMode;
import static com.bestbudz.ui.interfaces.Chatbox.pushMessage;
import com.bestbudz.ui.interfaces.StatusOrbs;
import static com.bestbudz.ui.interfaces.StatusOrbs.counterOn;
import com.bestbudz.util.TextClass;
import com.bestbudz.world.ObjectDef;
import static com.bestbudz.world.WalkTo.doWalkTo;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class ActionHandler extends Client
{
	public static void doAction(int i)
	{

		if (i < 0)
			return;
		if (inputDialogState != 0)
		{
			inputDialogState = 0;
			inputTaken = true;
		}
		int j = menuActionCmd2[i];
		int k = menuActionCmd3[i];
		int l = menuActionID[i];
		int i1 = menuActionCmd1[i];

		if (l >= 2000)
			l -= 2000;
		if (l == 713)
		{
			inputTaken = true;
			messagePromptRaised = true;
			amountOrNameInput = "";
			promptInput = "";
			inputDialogState = 0;
			stonersListAction = 557;
			aString1121 = "How much do u need to take out?";
		}
		if (l == 714)
		{
			stream.createFrame(185);
			stream.writeWord(714);
		}
		if (l == 715)
		{
			stream.createFrame(185);
			stream.writeWord(715);
		}
		if (l == 850)
		{
			stream.createFrame(185);
			stream.writeWord(1507);
		}
		if (l == 291)
		{
			stream.createFrame(140);
			stream.method432(j);
			stream.writeWord(k);
			stream.method432(i1);
		}

		if (l == 300)
		{
			stream.createFrame(141);
			stream.method432(j);
			stream.writeWord(k);
			stream.method432(i1);
			stream.writeDWord(modifiableXValue);
		}
		if (l == 474)
		{
			counterOn = !counterOn;
		}
		if (l == 475)
		{
			StatusOrbs.xpCounter = 0;
			stream.createFrame(148);
		}
		if (l == 476)
		{
			openInterfaceID = 32800;
		}
		if (l == 696)
		{
			setNorth();
		}
		if (l == 1506)
		{
			stream.createFrame(185);
			stream.writeWord(5001);
		}
		if (l == 1500)
		{
			prayClicked = !prayClicked;
			stream.createFrame(185);
			stream.writeWord(5000);
		}
		if (l == 104)
		{
			RSInterface class9_1 = RSInterface.interfaceCache[k];
			spellID = class9_1.id;
		}
		if (l == 582)
		{
			Npc npc = npcArray[i1];
			if (npc != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, npc.smallY[0], myStoner.smallX[0], false, npc.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(57);
				stream.method432(anInt1285);
				stream.method432(i1);
				stream.method431(anInt1283);
				stream.method432(anInt1284);
			}
		}
		if (l == 234)
		{
			boolean flag1 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag1)
				flag1 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(236);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method431(j + baseX);
		}
		if (l == 62 && componentIsClicked(i1, k, j))
		{
			stream.createFrame(192);
			stream.writeWord(anInt1284);
			stream.method431(i1 >> 14 & 0x7fff);
			stream.method433(k + baseY);
			stream.method431(anInt1283);
			stream.method433(j + baseX);
			stream.writeWord(anInt1285);
		}
		if (l == 511)
		{
			boolean flag2 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag2)
				flag2 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(25);
			stream.method431(anInt1284);
			stream.method432(anInt1285);
			stream.writeWord(i1);
			stream.method432(k + baseY);
			stream.method433(anInt1283);
			stream.writeWord(j + baseX);
		}
		if (l == 74)
		{
			stream.createFrame(122);
			stream.method433(k);
			stream.method432(j);
			stream.method431(i1);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 315)
		{
			RSInterface class9 = RSInterface.interfaceCache[k];
			boolean flag8 = true;
			if (class9.contentType > 0)
				flag8 = promptUserForInput(class9);
			if (flag8)
			{

				switch (k)
				{
					case 37510:
						break;
					case 37513:
						break;
					case 37516:
						break;
					case 37519:
						break;
					case 37522:
						break;
					case 37525:
						break;
					case 37528:
						break;
					case 37531:
						break;
					case 37534:
						break;
					case 37537:
						break;
					case 37540:
						break;
					case 37543:
						break;
					case 36004:
						stream.createFrame(185);
						stream.writeWord(28206);
						break;
					case 36007:
						stream.createFrame(185);
						stream.writeWord(28207);
						break;
					case 36010:
						stream.createFrame(185);
						stream.writeWord(28208);
						break;

					case 19144:
						sendFrame248(15106, 3213);
						clearInterfaceAnimations(15106);
						inputTaken = true;
						break;

					default:
						stream.createFrame(185);
						stream.writeWord(k);
						if (k >= 61101 && k <= 61200)
						{
							int selected = k - 61101;
							for (int ii = 0, slot = -1; ii < ItemDef.totalItems && slot < 100; ii++)
							{
								ItemDef def = ItemDef.getItemDefinition(ii);

								if (def.name == null || def.certTemplateID == ii - 1 || def.certID == ii - 1
									|| RSInterface.interfaceCache[61254].disabledMessage.length() == 0)
								{
									continue;
								}

								if (def.name.toLowerCase()
									.contains(RSInterface.interfaceCache[61254].disabledMessage.toLowerCase()))
								{
									slot++;
								}

								if (slot != selected)
								{
									continue;
								}

								int id = def.id;
								long num = Long
									.valueOf(RSInterface.interfaceCache[61255].disabledMessage.replaceAll(",", ""));

								if (num > Integer.MAX_VALUE)
								{
									num = Integer.MAX_VALUE;
								}

								stream.createFrame(149);
								stream.writeWord(id);
								stream.writeDWord((int) num);
								stream.writeWordBigEndian(variousSettings[1075]);
								break;
							}
						}
						break;

				}
			}
		}
		if (l == 561)
		{
			Stoner stoner = stonerArray[i1];
			if (stoner != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, stoner.smallY[0], myStoner.smallX[0], false,
					stoner.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				anInt1188 += i1;
				if (anInt1188 >= 90)
				{
					stream.createFrame(136);
					anInt1188 = 0;
				}
				stream.createFrame(128);
				stream.writeWord(i1);
			}
		}
		if (l == 20)
		{
			Npc class30_sub2_sub4_sub1_sub1_1 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_1 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_1.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub1_1.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(155);
				stream.method431(i1);
			}
		}
		if (l == 779)
		{
			Stoner class30_sub2_sub4_sub1_sub2_1 = stonerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_1 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_1.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_1.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(153);
				stream.method431(i1);
			}
		}
		if (l == 519)
			if (!menuOpen)
				worldController.method312(MouseState.y - 4, MouseState.x - 4);
			else
				worldController.method312(k - 4, j - 4);
		if (l == 1062)
		{
			anInt924 += baseX;
			if (anInt924 >= 113)
			{
				stream.createFrame(183);
				stream.writeDWordBigEndian(0xe63271);
				anInt924 = 0;
			}
			componentIsClicked(i1, k, j);
			stream.createFrame(228);
			stream.method432(i1 >> 14 & 0x7fff);
			stream.method432(k + baseY);
			stream.writeWord(j + baseX);
		}
		if (l == 679 && !aBoolean1149)
		{
			stream.createFrame(40);
			stream.writeWord(k);
			aBoolean1149 = true;
		}
		if (l == 431)
		{
			stream.createFrame(129);
			stream.method432(j);
			stream.writeWord(k);
			stream.method432(i1);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 337 || l == 42 || l == 792 || l == 322)
		{
			String s = menuActionName[i];
			int k1 = s.indexOf("@whi@");
			if (k1 != -1)
			{
				long l3 = TextClass.longForName(s.substring(k1 + 5).trim());
				if (l == 337)
					return;
				if (l == 42)
					return;
				if (l == 792)
					return;
				if (l == 322)
					return;
			}
		}
		if (l == 53)
		{
			stream.createFrame(135);
			stream.method431(j);
			stream.method432(k);
			stream.method431(i1);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 539)
		{
			stream.createFrame(16);
			stream.method432(i1);
			stream.method433(j);
			stream.method433(k);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 484 || l == 6)
		{
			String s1 = menuActionName[i];
			int l1 = s1.indexOf("@whi@");
			if (l1 != -1)
			{
				s1 = s1.substring(l1 + 5).trim();
				String s7 = TextClass.fixName(TextClass.nameForLong(TextClass.longForName(s1)));
				boolean flag9 = false;
				for (int j3 = 0; j3 < stonerCount; j3++)
				{
					Stoner class30_sub2_sub4_sub1_sub2_7 = stonerArray[stonerIndices[j3]];
					if (class30_sub2_sub4_sub1_sub2_7 == null || class30_sub2_sub4_sub1_sub2_7.name == null
						|| !class30_sub2_sub4_sub1_sub2_7.name.equalsIgnoreCase(s7))
						continue;
					doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_7.smallY[0],
						myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_7.smallX[0]);
					if (l == 484)
					{
						stream.createFrame(139);
						stream.method431(stonerIndices[j3]);
					}
					if (l == 6)
					{
						anInt1188 += i1;
						if (anInt1188 >= 90)
						{
							stream.createFrame(136);
							anInt1188 = 0;
						}
						stream.createFrame(128);
						stream.writeWord(stonerIndices[j3]);
					}
					flag9 = true;
					break;
				}

				if (!flag9)
					pushMessage("Too stoned to find " + s7, 0, "");
			}
		}
		if (l == 870)
		{
			stream.createFrame(53);
			stream.writeWord(j);
			stream.method432(anInt1283);
			stream.method433(i1);
			stream.writeWord(anInt1284);
			stream.method431(anInt1285);
			stream.writeWord(k);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 847)
		{
			stream.createFrame(87);
			stream.method432(i1);
			stream.writeWord(k);
			stream.method432(j);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 626)
		{
			RSInterface class9_1 = RSInterface.interfaceCache[k];
			spellSelected = 1;
			spellID = class9_1.id;
			anInt1137 = k;
			spellUsableOn = class9_1.spellUsableOn;
			itemSelected = 0;
			String s4 = class9_1.selectedActionName;
			if (s4.indexOf(" ") != -1)
				s4 = s4.substring(0, s4.indexOf(" "));
			String s8 = class9_1.selectedActionName;
			if (s8.indexOf(" ") != -1)
				s8 = s8.substring(s8.indexOf(" ") + 1);
			spellTooltip = s4 + " " + class9_1.spellName + " " + s8;
			if (spellUsableOn == 16)
			{
				tabID = 3;
				tabAreaAltered = true;
			}
			return;
		}
		if (l == 78)
		{
			stream.createFrame(117);
			stream.method433(k);
			stream.method433(i1);
			stream.method431(j);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 27)
		{
			Stoner class30_sub2_sub4_sub1_sub2_2 = stonerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_2 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_2.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_2.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				anInt986 += i1;
				if (anInt986 >= 54)
				{
					stream.createFrame(189);
					stream.writeWordBigEndian(234);
					anInt986 = 0;
				}
				stream.createFrame(73);
				stream.method431(i1);
			}
		}
		if (l == 213)
		{
			boolean flag3 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag3)
				flag3 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(79);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method432(j + baseX);
		}
		if (l == 632)
		{
			stream.createFrame(145);
			stream.method432(k);
			stream.method432(j);
			stream.method432(i1);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 1050)
		{
			stream.createFrame(185);
			stream.writeWord(152);
		}
		if (l == 1004)
		{
			if (tabInterfaceIDs[14] != -1)
			{

					if (tabID == 14)
					{
						showTabComponents = !showTabComponents;
					}
					else
					{
						showTabComponents = true;
					}

				tabID = 14;
				tabAreaAltered = true;
			}
		}
		if (l == 1003)
		{
			clanChatMode = 2;
			inputTaken = true;
		}
		if (l == 1002)
		{
			clanChatMode = 1;
			inputTaken = true;
		}
		if (l == 1001)
		{
			clanChatMode = 0;
			inputTaken = true;
		}
		if (l == 1000)
		{
			Chatbox.cButtonCPos = 4;
			chatTypeView = 11;
			inputTaken = true;
		}
		if (l == 999)
		{
			Chatbox.cButtonCPos = 0;
			chatTypeView = 0;
			inputTaken = true;
		}
		if (l == 998)
		{
			Chatbox.cButtonCPos = 1;
			chatTypeView = 5;
			inputTaken = true;
		}
		if (l == 997)
		{
			publicChatMode = 3;
			inputTaken = true;
		}
		if (l == 996)
		{
			publicChatMode = 2;
			inputTaken = true;
		}
		if (l == 995)
		{
			publicChatMode = 1;
			inputTaken = true;
		}
		if (l == 994)
		{
			publicChatMode = 0;
			inputTaken = true;
		}
		if (l == 993)
		{
			Chatbox.cButtonCPos = 2;
			chatTypeView = 1;
			inputTaken = true;
		}
		if (l == 992)
		{
			privateChatMode = 2;
			inputTaken = true;
		}
		if (l == 991)
		{
			privateChatMode = 1;
			inputTaken = true;
		}
		if (l == 990)
		{
			privateChatMode = 0;
			inputTaken = true;
		}
		if (l == 989)
		{
			Chatbox.cButtonCPos = 3;
			chatTypeView = 2;
			inputTaken = true;
		}
		if (l == 987)
		{
			tradeMode = 2;
			inputTaken = true;
		}
		if (l == 986)
		{
			tradeMode = 1;
			inputTaken = true;
		}
		if (l == 985)
		{
			tradeMode = 0;
			inputTaken = true;
		}
		if (l == 984)
		{
			Chatbox.cButtonCPos = 5;
			chatTypeView = 3;
			inputTaken = true;
		}
		if (l == 980)
		{
			Chatbox.cButtonCPos = 6;
			chatTypeView = 4;
			inputTaken = true;
		}
		if (l == 647)
		{
			stream.createFrame(213);
			stream.writeWord(k);
			stream.writeWord(j);
			switch (k)
			{
				case 43704:
					if (j == 0)
					{
						inputTaken = true;
						inputDialogState = 0;
						messagePromptRaised = true;
						promptInput = "";
						stonersListAction = 8;
						aString1121 = "Enter the name of your cult";
					}
					break;
			}
		}
		if (l == 493)
		{
			stream.createFrame(75);
			stream.method433(k);
			stream.method431(j);
			stream.method432(i1);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 652)
		{
			boolean flag4 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag4)
				flag4 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(156);
			stream.method432(j + baseX);
			stream.method431(k + baseY);
			stream.method433(i1);
		}
		if (l == 647)
		{
			stream.createFrame(213);
			stream.writeWord(k);
			stream.writeWord(j);
			switch (k)
			{
				case 43704:
					if (j == 0)
					{
						inputTaken = true;
						inputDialogState = 0;
						messagePromptRaised = true;
						promptInput = "";
						stonersListAction = 8;
						aString1121 = "Enter the name of your cult";
					}
					break;
			}
		}
		if (l == 94)
		{
			boolean flag5 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag5)
				flag5 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(181);
			stream.method431(k + baseY);
			stream.writeWord(i1);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
		}
		if (l == 646)
		{
			stream.createFrame(185);
			stream.writeWord(k);
			RSInterface class9_2 = RSInterface.interfaceCache[k];
			if (class9_2.valueIndexArray != null && class9_2.valueIndexArray[0][0] == 5)
			{
				int i2 = class9_2.valueIndexArray[0][1];
				if (variousSettings[i2] != class9_2.anIntArray212[0])
				{
					variousSettings[i2] = class9_2.anIntArray212[0];
					updateConfigValues(i2);
				}
			}
			switch (k)
			{
				case 18129:
					if (RSInterface.interfaceCache[18135].disabledMessage.toLowerCase().contains("join"))
					{
						inputTaken = true;
						inputDialogState = 0;
						messagePromptRaised = true;
						promptInput = "";
						stonersListAction = 6;
						aString1121 = "Enter the name of the cult you wish to join";
					}
					else
					{
						sendString(0, "");
					}
					break;
				case 18132:
					openInterfaceID = 43700;
					break;
				case 43926:
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					stonersListAction = 9;
					aString1121 = "Enter a name to add";
					break;
				case 43927:
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					stonersListAction = 10;
					aString1121 = "Enter a name to add";
					break;
			}
		}
		if (l == 225)
		{
			Npc class30_sub2_sub4_sub1_sub1_2 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_2 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_2.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub1_2.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				anInt1226 += i1;
				if (anInt1226 >= 85)
				{
					stream.createFrame(230);
					stream.writeWordBigEndian(239);
					anInt1226 = 0;
				}
				stream.createFrame(17);
				stream.method433(i1);
			}
		}
		if (l == 965)
		{
			Npc class30_sub2_sub4_sub1_sub1_3 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_3 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_3.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub1_3.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				anInt1134++;
				if (anInt1134 >= 96)
				{
					stream.createFrame(152);
					stream.writeWordBigEndian(88);
					anInt1134 = 0;
				}
				stream.createFrame(21);
				stream.writeWord(i1);
			}
		}
		if (l == 413)
		{
			Npc class30_sub2_sub4_sub1_sub1_4 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_4 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_4.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub1_4.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(131);
				stream.method433(i1);
				stream.method432(anInt1137);
			}
		}
		if (l == 200)
			clearTopInterfaces();
		if (l == 1025)
		{
			Npc class30_sub2_sub4_sub1_sub1_5 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_5 != null)
			{
				EntityDef entityDef = class30_sub2_sub4_sub1_sub1_5.desc;
				if (entityDef.childrenIDs != null)
					entityDef = entityDef.method161();
				if (entityDef != null)
				{
					String s9;
					if (entityDef.description != null)
						s9 = new String(entityDef.description);
					else
						s9 = "It's a " + entityDef.name + ".";
					pushMessage(s9, 0, "");
				}
			}
		}
		if (l == 900)
		{
			componentIsClicked(i1, k, j);
			stream.createFrame(252);
			stream.method433(i1 >> 14 & 0x7fff);
			stream.method431(k + baseY);
			stream.method432(j + baseX);
		}
		if (l == 412)
		{
			Npc class30_sub2_sub4_sub1_sub1_6 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_6 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_6.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub1_6.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(72);
				stream.method432(i1);
			}
		}
		if (l == 365)
		{
			Stoner class30_sub2_sub4_sub1_sub2_3 = stonerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_3 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_3.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_3.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(249);
				stream.method432(i1);
				stream.method431(anInt1137);
			}
		}
		if (l == 729)
		{
			Stoner class30_sub2_sub4_sub1_sub2_4 = stonerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_4 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_4.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_4.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(39);
				stream.method431(i1);
			}
		}
		if (l == 577)
		{
			Stoner class30_sub2_sub4_sub1_sub2_5 = stonerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_5 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_5.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_5.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(139);
				stream.method431(i1);
			}
		}
		if (l == 956 && componentIsClicked(i1, k, j))
		{
			stream.createFrame(35);
			stream.method431(j + baseX);
			stream.method432(anInt1137);
			stream.method432(k + baseY);
			stream.method431(i1 >> 14 & 0x7fff);
		}
		if (l == 567)
		{
			boolean flag6 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag6)
				flag6 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(23);
			stream.method431(k + baseY);
			stream.method431(i1);
			stream.method431(j + baseX);
		}
		if (l == 867)
		{
			if ((i1 & 3) == 0)
				anInt1175++;
			if (anInt1175 >= 59)
			{
				stream.createFrame(200);
				stream.writeWord(25501);
				anInt1175 = 0;
			}
			stream.createFrame(43);
			stream.method431(k);
			stream.method432(i1);
			stream.method432(j);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 543)
		{
			stream.createFrame(237);
			stream.writeWord(j);
			stream.method432(i1);
			stream.writeWord(k);
			stream.method432(anInt1137);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 606)
		{
			stream.createFrame(185);
			stream.writeWord(606);
		}
		if (l == 491)
		{
			Stoner class30_sub2_sub4_sub1_sub2_6 = stonerArray[i1];
			if (class30_sub2_sub4_sub1_sub2_6 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub2_6.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub2_6.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				stream.createFrame(14);
				stream.method432(anInt1284);
				stream.writeWord(i1);
				stream.writeWord(anInt1285);
				stream.method431(anInt1283);
			}
		}
		if (l == 639)
		{
			String s3 = menuActionName[i];
			int k2 = s3.indexOf("@whi@");
			if (k2 != -1)
			{
				long l4 = TextClass.longForName(s3.substring(k2 + 5).trim());
				int k3 = -1;
				for (int i4 = 0; i4 < stonersCount; i4++)
				{
					if (stonersListAsLongs[i4] != l4)
						continue;
					k3 = i4;
					break;
				}

				if (k3 != -1 && stonersNodeIDs[k3] > 0)
				{
					inputTaken = true;
					inputDialogState = 0;
					messagePromptRaised = true;
					promptInput = "";
					stonersListAction = 3;
					aLong953 = stonersListAsLongs[k3];
					aString1121 = "Send a message to " + stonersList[k3];
				}
			}
		}
		if (l == 454)
		{
			stream.createFrame(41);
			stream.writeWord(i1);
			stream.method432(j);
			stream.method432(k);
			atBoxLoopCycle = 0;
			atBoxInterface = k;
			atBoxIndex = j;
			atBoxInterfaceType = 2;
			if (RSInterface.interfaceCache[k].parentID == openInterfaceID)
				atBoxInterfaceType = 1;
			if (RSInterface.interfaceCache[k].parentID == backDialogID)
				atBoxInterfaceType = 3;
		}
		if (l == 478)
		{
			Npc class30_sub2_sub4_sub1_sub1_7 = npcArray[i1];
			if (class30_sub2_sub4_sub1_sub1_7 != null)
			{
				doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, class30_sub2_sub4_sub1_sub1_7.smallY[0],
					myStoner.smallX[0], false, class30_sub2_sub4_sub1_sub1_7.smallX[0]);
				crossX = MouseState.x;
				crossY = MouseState.y;
				crossType = 2;
				crossIndex = 0;
				if ((i1 & 3) == 0)
					anInt1155++;
				if (anInt1155 >= 53)
				{
					stream.createFrame(85);
					stream.writeWordBigEndian(66);
					anInt1155 = 0;
				}
				stream.createFrame(18);
				stream.method431(i1);
			}
		}
		if (l == 113)
		{
			componentIsClicked(i1, k, j);
			stream.createFrame(70);
			stream.method431(j + baseX);
			stream.writeWord(k + baseY);
			stream.method433(i1 >> 14 & 0x7fff);
		}
		if (l == 872)
		{
			componentIsClicked(i1, k, j);
			stream.createFrame(234);
			stream.method433(j + baseX);
			stream.method432(i1 >> 14 & 0x7fff);
			stream.method433(k + baseY);
		}
		if (l == 502)
		{
			componentIsClicked(i1, k, j);
			stream.createFrame(132);
			stream.method433(j + baseX);
			stream.writeWord(i1 >> 14 & 0x7fff);
			stream.method432(k + baseY);
		}
		if (l == 1125)
		{
			ItemDef itemDef = ItemDef.getItemDefinition(i1);
			RSInterface class9_4 = RSInterface.interfaceCache[k];
			String s5;
			if (class9_4 == null)
			{
				return;
			}
			if (class9_4 != null && class9_4.invStackSizes != null && class9_4.invStackSizes[j] >= 0x186a0)
			{
				DecimalFormatSymbols separator = new DecimalFormatSymbols();
				separator.setGroupingSeparator(',');
				DecimalFormat formatter = new DecimalFormat("#,###,###,###", separator);
				s5 = formatter.format(class9_4.invStackSizes[j]) + " x " + itemDef.name;
			}
			else if (itemDef.description != null)
				s5 = new String(itemDef.description);
			else
				s5 = "It's a " + itemDef.name + ".";
			pushMessage(s5, 0, "");
		}
		if (l == 169)
		{
			stream.createFrame(185);
			stream.writeWord(k);
			RSInterface class9_3 = RSInterface.interfaceCache[k];
			if (class9_3.valueIndexArray != null && class9_3.valueIndexArray[0][0] == 5)
			{
				int l2 = class9_3.valueIndexArray[0][1];
				variousSettings[l2] = 1 - variousSettings[l2];
				updateConfigValues(l2);
			}
		}
		if (l == 447)
		{
			itemSelected = 1;
			anInt1283 = j;
			anInt1284 = k;
			anInt1285 = i1;
			selectedItemName = ItemDef.getItemDefinition(i1).name;
			spellSelected = 0;
			return;
		}
		if (l == 1226)
		{
			int j1 = i1 >> 14 & 0x7fff;
			ObjectDef class46 = ObjectDef.forID(j1);
			String s10;
			if (class46.description != null)
				s10 = new String(class46.description);
			else
				s10 = "It's a " + class46.name + ".";
			pushMessage(s10, 0, "");
		}
		if (l == 244)
		{
			boolean flag7 = doWalkTo(2, 0, 0, 0, myStoner.smallY[0], 0, 0, k, myStoner.smallX[0], false, j);
			if (!flag7)
				flag7 = doWalkTo(2, 0, 1, 0, myStoner.smallY[0], 1, 0, k, myStoner.smallX[0], false, j);
			crossX = MouseState.x;
			crossY = MouseState.y;
			crossType = 2;
			crossIndex = 0;
			stream.createFrame(253);
			stream.method431(j + baseX);
			stream.method433(k + baseY);
			stream.method432(i1);
		}
		if (l == 1448)
		{
			ItemDef itemDef_1 = ItemDef.getItemDefinition(i1);
			String s6;
			if (itemDef_1.description != null)
			{
				s6 = new String(itemDef_1.description);
			}
			else
			{
				s6 = "It's a " + itemDef_1.name + ".";
			}
			pushMessage(s6, 0, "");
		}
		itemSelected = 0;
		spellSelected = 0;

	}


}
