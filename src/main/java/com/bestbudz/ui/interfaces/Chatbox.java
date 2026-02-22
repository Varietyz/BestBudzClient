package com.bestbudz.ui.interfaces;

import static com.bestbudz.data.items.GetItemDef.getItemDefinition;
import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.SettingsConfig;
import static com.bestbudz.network.packets.PacketParser.sendPacket;
import com.bestbudz.ui.handling.input.Keyboard;
import static com.bestbudz.ui.handling.input.Keyboard.console;
import static com.bestbudz.engine.core.login.Login.updateConfigValues;
import static com.bestbudz.network.packets.PacketSender.setInterfaceText;
import static com.bestbudz.network.packets.PacketSender.sendStringToServer;
import static com.bestbudz.network.packets.PacketSender.sendStonerNameAsLong;
import com.bestbudz.graphics.sprite.SpriteLoader;
import com.bestbudz.graphics.text.TextDrawingArea;
import static com.bestbudz.ui.InterfaceManagement.doTextField;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.graphics.text.TextInput;
import com.bestbudz.graphics.text.TextClass;
import com.bestbudz.world.ObjectDef;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import com.bestbudz.net.proto.WrapperProto.GamePacket;
import com.bestbudz.net.proto.InterfaceProto.*;
import com.bestbudz.net.proto.ChatProto.*;
import com.google.protobuf.ByteString;

public class Chatbox extends Client{
	public static final String[] chatNames= new String[500];
	public static final String[] chatMessages = new String[5000];
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
			inputPromptText = s;
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
			inputPromptText = s;
		}
		if (backDialogID == -1) {
			Client.inputTaken = true;
		}

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

	public static void handleTextFieldInput(Graphics2D g)
	{
		do
		{
			int j = Keyboard.readChar(-796);
			if (j == -1)
				break;
			if (j == 96)
				break;
			if (RSInterface.currentInputField != null)
			{
				boolean update = false;

				RSInterface rsTextField = RSInterface.interfaceCache[RSInterface.currentInputField.id];

				String message = TextClass.capitalize(rsTextField.disabledMessage);

				if (j == 8 && message.length() > 0)
				{
					message = message.substring(0, message.length() - 1);
					if (message.length() > 0 && RSInterface.currentInputField.onlyNumbers
						&& !RSInterface.currentInputField.displayAsterisks)
					{
						long num = Long.valueOf(message.replaceAll(",", ""));

						if (num > Integer.MAX_VALUE)
						{
							num = Integer.MAX_VALUE;
							rsTextField.disabledMessage = num + "";
						}

						message = NumberFormat.getInstance(Locale.US).format(num);
					}
					update = true;
				}

				if ((RSInterface.currentInputField.onlyNumbers ? (j >= 48 && j <= 57) : (j >= 32 && j <= 122)))
				{
					message += (char) j;
					if (RSInterface.currentInputField.onlyNumbers && !RSInterface.currentInputField.displayAsterisks)
					{
						long num = Long.valueOf(message.replaceAll(",", ""));

						if (num > Integer.MAX_VALUE)
						{
							num = Integer.MAX_VALUE;
							rsTextField.disabledMessage = num + "";
						}

						message = NumberFormat.getInstance(Locale.US).format(num);
					}
					update = true;
				}

				rsTextField.disabledMessage = message;

				if ((j == 13 || j == 10) && rsTextField.disabledMessage.length() > 0)
				{
					if (RSInterface.currentInputField.onlyNumbers)
					{
						long amount = 0;

						try
						{
							amount = Long.parseLong(message.replaceAll(",", ""));
							if (amount < -Integer.MAX_VALUE)
							{
								amount = -Integer.MAX_VALUE;
							}
							else if (amount > Integer.MAX_VALUE)
							{
								amount = Integer.MAX_VALUE;
							}
						}
						catch (Exception ignored)
						{
						}

						if (amount > 0)
						{
							sendProto(GamePacket.newBuilder().setChatInterfaceAction(ChatInterfaceAction.newBuilder().setInterfaceId((int) amount).setOpcode(208)).build());
						}
					}
					else
					{
						sendProto(GamePacket.newBuilder().setInputFieldAction(InputFieldAction.newBuilder().setText(RSInterface.currentInputField.disabledMessage).setId(RSInterface.currentInputField.id)).build());
					}
					RSInterface.currentInputField.disabledMessage = "";
					RSInterface.currentInputField = null;
				}

				if (update)
				{
					doTextField(rsTextField);
				}
			}
			else if (console.openConsole)
			{
				if (j == 8 && console.consoleInput.length() > 0)
				{
					console.consoleInput = console.consoleInput.substring(0, console.consoleInput.length() - 1);
				}
				if (j >= 32 && j <= 122)
				{
					console.consoleInput += (char) j;
				}
				if ((j == 13 || j == 10) && console.consoleInput.length() > 0)
				{
					console.sendConsoleMessage(console.consoleInput, true);
					console.sendConsoleCommands(console.consoleInput);
					console.consoleInput = "";
					inputTaken = true;
				}
			}
			else if (openInterfaceID != -1 && openInterfaceID == reportAbuseInterfaceID)
			{
				if (j == 8 && reportAbuseInput.length() > 0)
					reportAbuseInput = reportAbuseInput.substring(0, reportAbuseInput.length() - 1);
				if ((j >= 97 && j <= 122 || j >= 65 && j <= 90 || j >= 48 && j <= 57 || j == 32)
					&& reportAbuseInput.length() < 12)
					reportAbuseInput += (char) j;
			}
			else if (messagePromptRaised)
			{
				if (j >= 32 && j <= 122)
				{
					promptInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && promptInput.length() > 0)
				{
					promptInput = promptInput.substring(0, promptInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10)
				{
					messagePromptRaised = false;
					inputTaken = true;
					if (stonersListAction == 1)
					{
						long l = TextClass.longForName(promptInput);
						return;
					}
					if (stonersListAction == 2 && stonersCount > 0)
					{
						long l1 = TextClass.longForName(promptInput);
						return;
					}
					if (stonersListAction == 557 && promptInput.length() > 0)
					{
						inputString = "::withdrawmp " + promptInput;
						sendPacket(103);
					}
					if (stonersListAction == 51504 && promptInput.length() > 0)
					{
						inputString = "::find " + promptInput;
						sendPacket(103);
					}
					if (stonersListAction == 59800 && promptInput.length() > 0)
					{
						inputString = "::droptablesearch " + promptInput;
						sendPacket(103);
					}
					if (stonersListAction == 3 && promptInput.length() > 0)
					{
						sendProto(GamePacket.newBuilder().setPrivateMessageSend(PrivateMessageSend.newBuilder().setRecipientHash(menuOpenTime).setMessage(ByteString.copyFrom(promptInput.getBytes()))).build());
						promptInput = TextInput.processText(promptInput);
						pushMessage(promptInput, 6, TextClass.fixName(TextClass.nameForLong(menuOpenTime)));
						if (privateChatMode == 2)
						{
							privateChatMode = 1;
							sendProto(GamePacket.newBuilder().setChatModeUpdate(ChatModeUpdate.newBuilder().setPublicMode(publicChatMode).setPrivateMode(privateChatMode).setTradeMode(tradeMode)).build());
						}
					}
					if (stonersListAction == 4 && ignoreCount < 100)
					{
						return;
					}
					if (stonersListAction == 5 && ignoreCount > 0)
					{
						return;
					}
					if (stonersListAction == 6)
					{
						sendStonerNameAsLong(promptInput);
					}
					else if (stonersListAction == 8)
					{
						sendStringToServer(1, promptInput);
					}
					else if (stonersListAction == 9)
					{
						sendStringToServer(2, promptInput);
					}
					else if (stonersListAction == 10)
					{
						sendStringToServer(3, promptInput);
					}
				}
			}
			else if (inputDialogState == 1)
			{
				if (j >= 48 && j <= 57 && amountOrNameInput.length() < 10)
				{
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if ((!amountOrNameInput.toLowerCase().contains("k") && !amountOrNameInput.toLowerCase().contains("m")
					&& !amountOrNameInput.toLowerCase().contains("b")) && (j == 107 || j == 109) || j == 98)
				{
					amountOrNameInput += (char) j;
					inputTaken = true;
				}
				if (j == 8 && amountOrNameInput.length() > 0)
				{
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
				}
				if (j == 13 || j == 10)
				{
					if (amountOrNameInput.length() > 0)
					{
						int length = amountOrNameInput.length();
						char lastChar = amountOrNameInput.charAt(length - 1);

						if (lastChar == 'k')
						{
							amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000";
						}
						else if (lastChar == 'm')
						{
							amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000000";
						}
						else if (lastChar == 'b')
						{
							amountOrNameInput = amountOrNameInput.substring(0, length - 1) + "000000000";
						}

						long amount = 0;

						try
						{
							amount = Long.parseLong(amountOrNameInput);
							if (amount < 0)
							{
								amount = 0;
							}
							else if (amount > Integer.MAX_VALUE)
							{
								amount = Integer.MAX_VALUE;
							}
						}
						catch (Exception ignored)
						{
						}

						if (amount > 0)
						{
							sendProto(GamePacket.newBuilder().setChatInterfaceAction(ChatInterfaceAction.newBuilder().setInterfaceId((int) amount).setOpcode(208)).build());
							if (openInterfaceID == 5292)
							{
								modifiableXValue = (int) amount;
							}
						}
					}

					inputDialogState = 0;
					inputTaken = true;
				}
			}
			else if (inputDialogState == 2)
			{
				if (j >= 32 && j <= 122 && amountOrNameInput.length() < 12)
				{
					amountOrNameInput += (char) j;
					inputTaken = true;
					if (openInterfaceID == 5292 && variousSettings[1012] == 1)
					{
						RSInterface bank = RSInterface.interfaceCache[5382];
						Arrays.fill(bankInvTemp, 0);
						Arrays.fill(bankStackTemp, 0);
						for (int slot = 0, bankSlot = 0; slot < bank.inv.length; slot++)
						{
							if (bank.inv[slot] - 1 > 0)
							{
								if (getItemDefinition(bank.inv[slot] - 1).name.toLowerCase()
									.contains(amountOrNameInput.toLowerCase()))
								{
									bankInvTemp[bankSlot] = bank.inv[slot];
									bankStackTemp[bankSlot++] = bank.invStackSizes[slot];
								}
							}
						}
					}
				}
				if (j == 8 && amountOrNameInput.length() > 0)
				{
					amountOrNameInput = amountOrNameInput.substring(0, amountOrNameInput.length() - 1);
					inputTaken = true;
					if (openInterfaceID == 5292 && variousSettings[1012] == 1)
					{
						RSInterface bank = RSInterface.interfaceCache[5382];
						Arrays.fill(bankInvTemp, 0);
						Arrays.fill(bankStackTemp, 0);
						for (int slot = 0, bankSlot = 0; slot < bank.inv.length; slot++)
						{
							if (bank.inv[slot] - 1 > 0)
							{
								if (getItemDefinition(bank.inv[slot] - 1).name.toLowerCase()
									.contains(amountOrNameInput.toLowerCase()))
								{
									bankInvTemp[bankSlot] = bank.inv[slot];
									bankStackTemp[bankSlot++] = bank.invStackSizes[slot];
								}
							}
						}
					}
				}
				if (j == 13 || j == 10)
				{
					if (amountOrNameInput.length() > 0)
					{
						sendProto(GamePacket.newBuilder().setStringInput(StringInput.newBuilder().setText(amountOrNameInput)).build());
					}
					if (openInterfaceID == 5292 && variousSettings[1012] == 1)
					{
						RSInterface bank = RSInterface.interfaceCache[5382];
						Arrays.fill(bankInvTemp, 0);
						Arrays.fill(bankStackTemp, 0);
						int results = 0;
						for (int slot = 0, bankSlot = 0; slot < bank.inv.length; slot++)
						{
							if (bank.inv[slot] - 1 > 0)
							{
								if (getItemDefinition(bank.inv[slot] - 1).name.toLowerCase()
									.contains(amountOrNameInput.toLowerCase()))
								{
									bankInvTemp[bankSlot] = bank.inv[slot];
									bankStackTemp[bankSlot++] = bank.invStackSizes[slot];
									results++;
								}
							}
						}

						pushMessage("Found " + results + " result" + (results > 1 ? "s" : "") + " for '"
							+ amountOrNameInput + "'.", 0, "");
					}
					inputDialogState = 0;
					inputTaken = true;
				}
			}
			else if (backDialogID == -1)
			{
				if (j >= 32 && j <= 122)
				{
					inputString += (char) j;
					inputTaken = true;
				}
				if (j == 8 && inputString.length() > 0)
				{
					inputString = inputString.substring(0, inputString.length() - 1);
					inputTaken = true;
				}
				if ((j == 13 || j == 10) && inputString.length() > 0)
				{
					if ((myPrivilege == 2 || myPrivilege == 3 || myPrivilege == 4) || server.equals("127.0.0.1"))
					{
						if (inputString.startsWith("//setspecto"))
						{
							int amt = Integer.parseInt(inputString.substring(12));
							experienceDrops[300] = amt;
							if (variousSettings[300] != amt)
							{
								variousSettings[300] = amt;
								updateConfigValues(300);
								if (dialogID != -1)
								{
									inputTaken = true;
								}
							}
						}
						if (inputString.equals("::reint") || inputString.equals("::Reint"))
						{
							SpriteLoader.loadSprites();
							TextDrawingArea[] fonts = {smallText, regularText, boldText, smallText};
							RSInterface.unpack(fonts);
							setInterfaceText("0", 8135);
						}
						if (inputString.equals("::objs"))
						{
							for (int i = 0; i < ObjectDef.totalObjects; i++)
							{
								ObjectDef def = ObjectDef.forID(i);

								if (def == null)
								{
									continue;
								}

								System.out.println("[Chatbox] "+i + " " + def.animationId);
							}
						}
						if (inputString.equals("::textids"))
						{
							for (int index = 0; index < RSInterface.interfaceCache.length; index++)
							{
								if (RSInterface.interfaceCache[index] != null)
								{
									RSInterface.interfaceCache[index].disabledMessage = String.valueOf(index);
								}
							}
						}
						if (inputString.equals("::filtergray"))
						{
							filterGrayScale = !filterGrayScale;
						}
						if (inputString.equals("::fps"))
						{
							fpsOn = !fpsOn;
						}
						if (inputString.equals("::data"))
						{
							clientData = !clientData;
						}
						if (inputString.equals("::hovers"))
						{
							SettingsConfig.menuHovers = !SettingsConfig.menuHovers;
						}
					}
					if (inputString.startsWith("/"))
						inputString = "::" + inputString;
					if (inputString.startsWith("::"))
					{
						sendProto(GamePacket.newBuilder().setCommandMessage(CommandMessage.newBuilder().setCommand(inputString.substring(2))).build());
					}
					else
					{
						String s = inputString.toLowerCase();
						int j2 = 0;
						if (s.startsWith("yellow:"))
						{
							j2 = 0;
							inputString = inputString.substring(7);
						}
						else if (s.startsWith("red:"))
						{
							j2 = 1;
							inputString = inputString.substring(4);
						}
						else if (s.startsWith("green:"))
						{
							j2 = 2;
							inputString = inputString.substring(6);
						}
						else if (s.startsWith("cyan:"))
						{
							j2 = 3;
							inputString = inputString.substring(5);
						}
						else if (s.startsWith("purple:"))
						{
							j2 = 4;
							inputString = inputString.substring(7);
						}
						else if (s.startsWith("white:"))
						{
							j2 = 5;
							inputString = inputString.substring(6);
						}
						else if (s.startsWith("flash1:"))
						{
							j2 = 6;
							inputString = inputString.substring(7);
						}
						else if (s.startsWith("flash2:"))
						{
							j2 = 7;
							inputString = inputString.substring(7);
						}
						else if (s.startsWith("flash3:"))
						{
							j2 = 8;
							inputString = inputString.substring(7);
						}
						else if (s.startsWith("glow1:"))
						{
							j2 = 9;
							inputString = inputString.substring(6);
						}
						else if (s.startsWith("glow2:"))
						{
							j2 = 10;
							inputString = inputString.substring(6);
						}
						else if (s.startsWith("glow3:"))
						{
							j2 = 11;
							inputString = inputString.substring(6);
						}
						s = inputString.toLowerCase();
						int i3 = 0;
						if (s.startsWith("wave:"))
						{
							i3 = 1;
							inputString = inputString.substring(5);
						}
						else if (s.startsWith("wave2:"))
						{
							i3 = 2;
							inputString = inputString.substring(6);
						}
						else if (s.startsWith("shake:"))
						{
							i3 = 3;
							inputString = inputString.substring(6);
						}
						else if (s.startsWith("scroll:"))
						{
							i3 = 4;
							inputString = inputString.substring(7);
						}
						else if (s.startsWith("slide:"))
						{
							i3 = 5;
							inputString = inputString.substring(6);
						}
						sendProto(GamePacket.newBuilder().setPublicChatOut(PublicChatOut.newBuilder().setColor(j2).setEffects(i3).setText(ByteString.copyFrom(inputString.getBytes()))).build());
						inputString = TextInput.processText(inputString);
						myStoner.textSpoken = inputString;
						myStoner.chatType = j2;
						myStoner.chatEffect = i3;
						myStoner.textCycle = 150;
						if (myPrivilege >= 1)
							pushMessage(myStoner.textSpoken, 2, "@cr" + myPrivilege + "@" + myStoner.name,
								myStoner.title, myStoner.titleColor);
						else
							pushMessage(myStoner.textSpoken, 2, myStoner.name, myStoner.title, myStoner.titleColor);
						if (publicChatMode == 2)
						{
							publicChatMode = 3;
							sendProto(GamePacket.newBuilder().setChatModeUpdate(ChatModeUpdate.newBuilder().setPublicMode(publicChatMode).setPrivateMode(privateChatMode).setTradeMode(tradeMode)).build());
						}
					}
					inputString = "";
					inputTaken = true;
				}
			}
		} while (true);
	}

}
