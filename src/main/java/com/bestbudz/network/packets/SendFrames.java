package com.bestbudz.network.packets;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.input.MouseState;
import com.bestbudz.ui.RSInterface;
import com.bestbudz.graphics.text.TextClass;

public class SendFrames extends Client
{
	public static void updateStrings(String str, int i)
	{
		switch (i)
		{
			case 1675:
				sendFrame126(str, 17508);
				break;
			case 1676:
				sendFrame126(str, 17509);
				break;
			case 1677:
				sendFrame126(str, 17510);
				break;
			case 1678:
				sendFrame126(str, 17511);
				break;
			case 1679:
				sendFrame126(str, 17512);
				break;
			case 1680:
				sendFrame126(str, 17513);
				break;
			case 1681:
				sendFrame126(str, 17514);
				break;
			case 1682:
				sendFrame126(str, 17515);
				break;
			case 1683:
				sendFrame126(str, 17516);
				break;
			case 1684:
				sendFrame126(str, 17517);
				break;
			case 1686:
				sendFrame126(str, 17518);
				break;
			case 1687:
				sendFrame126(str, 17519);
				break;
		}
	}

	public static void sendFrame126(String str, int i)
	{
		RSInterface.interfaceCache[i].disabledMessage = str;
		if (RSInterface.interfaceCache[i].parentID == tabInterfaceIDs[tabID])
		{
		}
	}

	public static void sendString(int identifier, String text)
	{
		text = identifier + "," + text;
		stream.createFrame(127);
		stream.writeWordBigEndian(text.length() + 1);
		stream.writeString(text);
	}

	public static void sendStringAsLong(String string)
	{
		stream.createFrame(60);
		stream.writeQWord(TextClass.longForName(string));
	}

	public static void processBankSwap() {
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
				stream.createFrame(214);
				stream.method433(focusedDragWidget);
				stream.method424(2);
				stream.method433(dragFromSlot);
				stream.method431(i);
				return;
			}
		}
	}

	public static void swapInventoryItem() {
		RSInterface rsi = RSInterface.interfaceCache[focusedDragWidget];
		boolean bankMode = (anInt913 == 1 && rsi.contentType == 206);
		if (rsi.inv[mouseInvInterfaceIndex] <= 0) bankMode = false;

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

		stream.createFrame(214);
		stream.method433(focusedDragWidget);
		stream.method424(bankMode ? 1 : 0);
		stream.method433(dragFromSlot);
		stream.method431(mouseInvInterfaceIndex);
	}

}
