package com.bestbudz.network.packets;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.graphics.text.TextClass;

public class PacketSender extends Client
{
	public static void updateInterfaceText(String str, int i)
	{
		switch (i)
		{
			case 1675:
				setInterfaceText(str, 17508);
				break;
			case 1676:
				setInterfaceText(str, 17509);
				break;
			case 1677:
				setInterfaceText(str, 17510);
				break;
			case 1678:
				setInterfaceText(str, 17511);
				break;
			case 1679:
				setInterfaceText(str, 17512);
				break;
			case 1680:
				setInterfaceText(str, 17513);
				break;
			case 1681:
				setInterfaceText(str, 17514);
				break;
			case 1682:
				setInterfaceText(str, 17515);
				break;
			case 1683:
				setInterfaceText(str, 17516);
				break;
			case 1684:
				setInterfaceText(str, 17517);
				break;
			case 1686:
				setInterfaceText(str, 17518);
				break;
			case 1687:
				setInterfaceText(str, 17519);
				break;
		}
	}

	public static void setInterfaceText(String str, int i)
	{
		RSInterface.interfaceCache[i].disabledMessage = str;
		if (RSInterface.interfaceCache[i].parentID == tabInterfaceIDs[tabID])
		{
		}
	}

	public static void sendStringToServer(int identifier, String text)
	{
		text = identifier + "," + text;
		stream.writeEncryptedOpcode(127);
		stream.writeByte(text.length() + 1);
		stream.writeString(text);
	}

	public static void sendStonerNameAsLong(String string)
	{
		stream.writeEncryptedOpcode(60);
		stream.writeQWord(TextClass.longForName(string));
	}

	public static void handleBankSlotSwap() {
		int xOffset = (frameWidth - 237 - RSInterface.interfaceCache[5292].width) / 2;
		int yOffset = 36 + ((frameHeight - 503) / 2);
		int[] slots = new int[10];
		for (int i = 0; i < 10; i++) {
			slots[i] = xOffset + 76 + (i * 40);
		}
		int yTop = yOffset + 22;
		int yBottom = yOffset + 62;

		for (int i = 0; i < 10; i++) {
			if (MouseState.x >= slots[i] && MouseState.x <= (slots[i] + 41) &&
				MouseState.y >= yTop && MouseState.y <= yBottom) {
				stream.writeEncryptedOpcode(214);
				stream.writeWordMixedLE(focusedDragWidget);
				stream.writeByteNegated(2);
				stream.writeWordMixedLE(dragFromSlot);
				stream.writeWordLittleEndian(i);
				return;
			}
		}
	}

	public static void handleInventorySwap() {
		RSInterface rsi = RSInterface.interfaceCache[focusedDragWidget];

		boolean bankMode = false;

		if (rsi.aBoolean235) {
			int a = dragFromSlot;
			int b = mouseInvInterfaceIndex;
			rsi.inv[b] = rsi.inv[a];
			rsi.invStackSizes[b] = rsi.invStackSizes[a];
			rsi.inv[a] = -1;
			rsi.invStackSizes[a] = 0;
		} else if (!bankMode) {

			rsi.swapBoxItems(dragFromSlot, mouseInvInterfaceIndex);
		}

		stream.writeEncryptedOpcode(214);
		stream.writeWordMixedLE(focusedDragWidget);
		stream.writeByteNegated(0);
		stream.writeWordMixedLE(dragFromSlot);
		stream.writeWordLittleEndian(mouseInvInterfaceIndex);
	}

}
