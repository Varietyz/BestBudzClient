package com.bestbudz.ui.handling.input;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;
import java.awt.event.MouseWheelEvent;

public class MouseScrollHandler {

	public static void handle(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();

		int tabId = Client.tabInterfaceIDs[Client.tabID];
		if (tabId != -1) {
			int offsetX = Client.frameWidth - 197;
			int offsetY = Client.frameHeight - 37 - 267;
			RSInterface tab = RSInterface.interfaceCache[tabId];
			if (tab != null && tab.children != null) {
				scrollInterface(tab, offsetX, offsetY, rotation);
			}
		}

		if (Client.openInterfaceID != -1) {
			int x = (Client.frameWidth / 2) - 256;
			int y = (Client.frameHeight / 2) - 167;
			int w = 512;
			int h = 334;
			int offsetX;
			int offsetY;

			for (int i = 0; i < (!Client.changeTabArea ? 4 : 3); i++) {
				if (x + w > Client.frameWidth - 225) x = Math.max(0, x - 30);
				if (y + h > Client.frameHeight - 182) y = Math.max(0, y - 30);
			}

			if (Client.openInterfaceID == 5292) {
				offsetX = (Client.frameWidth / 2) - 356;
				offsetY = (Client.frameHeight / 2) - 230;
			} else {
				offsetX = x;
				offsetY = y;
			}

			RSInterface rsi = RSInterface.interfaceCache[Client.openInterfaceID];
			if (rsi != null && rsi.children != null) {
				scrollInterface(rsi, offsetX, offsetY, rotation);
			}
		}
	}


	private static void scrollInterface(RSInterface parent, int offsetX, int offsetY, int rotation) {
		for (int i = 0; i < parent.children.length; i++) {
			RSInterface child = RSInterface.interfaceCache[parent.children[i]];
			if (child.scrollMax > 0) {
				int x = offsetX + parent.childX[i];
				int y = offsetY + parent.childY[i];
				int width = child.width;
				int height = child.height;

				if (isMouseWithin(x, y, width, height)) {
					if (child.scrollPosition > 0 || rotation > 0) {
						child.scrollPosition += rotation * 30;
					}
					return;
				}
			}
		}
	}


	private static boolean isMouseWithin(int x, int y, int width, int height) {
		return MouseState.x > x && MouseState.y > y &&
			MouseState.x < x + width && MouseState.y < y + height;
	}

}
