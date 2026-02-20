package com.bestbudz.engine.util;

import com.bestbudz.engine.core.Client;

public class ClientDiagnostics extends Client {

	private static final int LEFT_MARGIN = 5;
	private static final int TOP_MARGIN = 15;

	private static final int COL_GOOD = 0x00FF00;
	private static final int COL_WARNING = 0xFFFF00;
	private static final int COL_CRITICAL = 0xFF0000;

	public static void drawClientFPS() {

		if (!clientData) {
			return;
		}

		int fpsColor = getFpsColor();

		String fpsText = "FPS: " + fps;

		newSmallFont.drawBasicString(fpsText, 5, 15, fpsColor, 1);
	}

	private static int getFpsColor() {
		if (fps < 15) {
			return COL_CRITICAL;
		}
		if (fps >= 60) {
			return COL_GOOD;
		}

		return COL_WARNING;
	}
}
