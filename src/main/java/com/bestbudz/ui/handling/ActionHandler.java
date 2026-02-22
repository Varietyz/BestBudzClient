package com.bestbudz.ui.handling;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.engine.core.Client;
import static com.bestbudz.ui.handling.input.MouseActions.componentIsClicked;
import com.bestbudz.ui.handling.input.MouseState;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.network.packets.PacketSender.sendStringToServer;
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
import com.bestbudz.graphics.text.TextClass;
import com.bestbudz.world.ObjectDef;
import static com.bestbudz.world.WalkTo.doWalkTo;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.InterfaceProto.*;
import com.bestbudz.net.proto.PlayerProto.*;
import com.bestbudz.net.proto.ChatProto.*;

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
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(714)).build());
		}
		if (l == 715)
		{
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(715)).build());
		}
		if (l == 850)
		{
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(1507)).build());
		}
		if (l == 291)
		{
			sendProto(GamePacket.newBuilder().setBankAllButOne(BankAllButOneAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j)).build());
		}

		if (l == 300)
		{
			sendProto(GamePacket.newBuilder().setBankModifiableX(BankModifiableXAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAmount(modifiableXValue)).build());
		}
		if (l == 474)
		{
			counterOn = !counterOn;
		}
		if (l == 475)
		{
			StatusOrbs.xpCounter = 0;
			sendProto(GamePacket.newBuilder().setResetCounter(ResetCounterRequest.getDefaultInstance()).build());
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
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(5001)).build());
		}
		if (l == 1500)
		{
			prayClicked = !prayClicked;
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(5000)).build());
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
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(7)).build());
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(10)).build());
		}
		if (l == 62 && componentIsClicked(i1, k, j))
		{
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(21)).build());
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(21)).build());
		}
		if (l == 74)
		{
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(10)).build());
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
						sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(28206)).build());
						break;
					case 36007:
						sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(28207)).build());
						break;
					case 36010:
						sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(28208)).build());
						break;

					case 19144:
						sendFrame248(15106, 3213);
						clearInterfaceAnimations(15106);
						inputTaken = true;
						break;

					default:
						sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(k)).build());
						if (k >= 61101 && k <= 61200)
						{
							int selected = k - 61101;
							for (int ii = 0, slot = -1; ii < ItemDef.totalItems && slot < 100; ii++)
							{
								ItemDef def = getItemDefinition(ii);

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

								sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(id)).build());
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
				viewportIndex += i1;
				if (viewportIndex >= 90)
				{
					viewportIndex = 0;
				}
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(1)).build());
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
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(1)).build());
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
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(2)).build());
			}
		}
		if (l == 519)
			if (!menuOpen)
				worldController.setMousePosition(MouseState.y - 4, MouseState.x - 4);
			else
				worldController.setMousePosition(k - 4, j - 4);
		if (l == 1062)
		{
			gameSubState += baseX;
			if (gameSubState >= 113)
			{
				gameSubState = 0;
			}
			componentIsClicked(i1, k, j);
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(5)).build());
		}
		if (l == 679 && !isPlayerBusy)
		{
			sendProto(GamePacket.newBuilder().setChatInterfaceAction(ChatInterfaceAction.newBuilder().setInterfaceId(k).setOpcode(40)).build());
			isPlayerBusy = true;
		}
		if (l == 431)
		{
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(4)).build());
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(5)).build());
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(9)).build());
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
						sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(stonerIndices[j3]).setAction(4)).build());
					}
					if (l == 6)
					{
						viewportIndex += i1;
						if (viewportIndex >= 90)
						{
							viewportIndex = 0;
						}
						sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(1)).build());
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(11)).build());
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(6)).build());
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
			spellTargetMask = k;
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(2)).build());
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
				systemUpdateTimer += i1;
				if (systemUpdateTimer >= 54)
				{
					systemUpdateTimer = 0;
				}
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(3)).build());
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(11)).build());
		}
		if (l == 632)
		{
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(1)).build());
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
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(152)).build());
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
			sendProto(GamePacket.newBuilder().setDockPanelAction(DockPanelAction.newBuilder().setPanelId(k).setAction(j)).build());
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(7)).build());
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(13)).build());
		}
		if (l == 647)
		{
			sendProto(GamePacket.newBuilder().setDockPanelAction(DockPanelAction.newBuilder().setPanelId(k).setAction(j)).build());
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(20)).build());
		}
		if (l == 646)
		{
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(k)).build());
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
						sendStringToServer(0, "");
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
				lastMouseX += i1;
				if (lastMouseX >= 85)
				{
					lastMouseX = 0;
				}
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(2)).build());
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
				interfaceDrawY++;
				if (interfaceDrawY >= 96)
				{
					interfaceDrawY = 0;
				}
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(3)).build());
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
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(6)).build());
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
					entityDef = entityDef.getTransformedEntity();
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(2)).build());
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
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(4)).build());
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
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(5)).build());
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
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(6)).build());
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
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(4)).build());
			}
		}
		if (l == 956 && componentIsClicked(i1, k, j))
		{
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(20)).build());
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(14)).build());
		}
		if (l == 867)
		{
			if ((i1 & 3) == 0)
				renderDistance++;
			if (renderDistance >= 118)
			{
				renderDistance = 0;
			}
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(3)).build());
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
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(12)).build());
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
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(606)).build());
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
				sendProto(GamePacket.newBuilder().setStonerInteraction(StonerInteraction.newBuilder().setStonerIndex(i1).setAction(7)).build());
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
					menuOpenTime = stonersListAsLongs[k3];
					aString1121 = "Send a message to " + stonersList[k3];
				}
			}
		}
		if (l == 454)
		{
			sendProto(GamePacket.newBuilder().setInterfaceAction(InterfaceAction.newBuilder().setInterfaceId(k).setItemId(i1).setSlot(j).setAction(8)).build());
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
					interfaceDrawZ++;
				if (interfaceDrawZ >= 53)
				{
					interfaceDrawZ = 0;
				}
				sendProto(GamePacket.newBuilder().setNpcInteraction(NpcInteraction.newBuilder().setNpcIndex(i1).setAction(5)).build());
			}
		}
		if (l == 113)
		{
			componentIsClicked(i1, k, j);
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(3)).build());
		}
		if (l == 872)
		{
			componentIsClicked(i1, k, j);
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(4)).build());
		}
		if (l == 502)
		{
			componentIsClicked(i1, k, j);
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1 >> 14 & 0x7fff).setX(j + baseX).setY(k + baseY).setAction(1)).build());
		}
		if (l == 1125)
		{
			ItemDef itemDef = getItemDefinition(i1);
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
			sendProto(GamePacket.newBuilder().setClickButton(ClickButton.newBuilder().setButtonId(k)).build());
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
			selectedItemSlot = j;
			selectedItemInterfaceId = k;
			selectedItemIndex = i1;
			selectedItemName = getItemDefinition(i1).name;
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
			sendProto(GamePacket.newBuilder().setObjectInteraction(ObjectInteraction.newBuilder().setObjectId(i1).setX(j + baseX).setY(k + baseY).setAction(12)).build());
		}
		if (l == 1448)
		{
			ItemDef itemDef_1 = getItemDefinition(i1);
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
