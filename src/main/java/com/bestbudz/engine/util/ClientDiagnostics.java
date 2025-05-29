package com.bestbudz.engine.util;

import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.input.MouseState;

public class ClientDiagnostics extends Client
{
	public static void drawClientDiagnostics() {
		if (!clientData) return;

		int x = baseX + ((myStoner.x - 6) >> 7);
		int y = baseY + ((myStoner.y - 6) >> 7);
		int col = 0xffff00;

		int fpsCol;
		if (fps < 15) {
			fpsCol = 0xff0000;
		} else if (fps >= 90) {
			fpsCol = 0x00ffff;
		} else if (fps >= 60) {
			fpsCol = 0x00ff00;
		} else {
			fpsCol = 0xffff00;
		}

		smallText.method385(col, "Frame Width: " + (MouseState.x - frameWidth) + ", Frame Height: " + (MouseState.y - frameHeight), frameHeight - 271, 5);
		smallText.method385(col, "Client Zoom: " + cameraZoom, frameHeight - 257, 5);
		smallText.method385(fpsCol, "Fps: " + fps, frameHeight - 243, 5);
		smallText.method385(col, "Memory Usage: " + getUsedMemory() + "k", frameHeight - 229, 5);
		smallText.method385(col, "Mouse X: " + MouseState.x + ", Mouse Y: " + MouseState.y, frameHeight - 215, 5);
		smallText.method385(col, "Coords: " + x + ", " + y, frameHeight - 201, 5);
		smallText.method385(col, "Client Mode: ", frameHeight - 187, 5);
		smallText.method385(col, "Client Resolution: " + frameWidth + "x" + frameHeight, frameHeight - 173, 5);
	}

	public static void drawSystemUpdateCountdown() {
		if (anInt1104 == 0) return;

		int j = anInt1104 / 50;
		int l = j / 60;
		j %= 60;
		String timer = (j < 10) ? l + ":0" + j : l + ":" + j;

		int yOffset = frameHeight - 498;
		smallText.method385(0xffff00, "System update in: " + timer, 329 + yOffset, 4);

		if (++anInt849 > 75) {
			anInt849 = 0;
			stream.createFrame(148);
		}
	}

	public static int getUsedMemory() {
		Runtime runtime = Runtime.getRuntime();
		return (int) ((runtime.totalMemory() - runtime.freeMemory()) / 1024L);
	}
}
