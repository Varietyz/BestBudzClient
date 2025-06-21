package com.bestbudz.engine.util;

import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;

public class ClientDiagnostics extends Client {

	/* Positioning constants */
	private static final int LEFT_MARGIN = 5;
	private static final int TOP_MARGIN = 15;

	/* Enhanced color palette */
	private static final int COL_GOOD = 0x00FF00;
	private static final int COL_WARNING = 0xFFFF00;
	private static final int COL_CRITICAL = 0xFF0000;

	/* ------------------------------------------------------------ */
	/**
	 * Draws the current Frames Per Second (FPS) directly on the client screen.
	 * This method simplifies the display to only show the FPS value,
	 * removing the complex panel and section rendering logic.
	 * The FPS text color dynamically changes based on performance:
	 * - Green: 60 FPS or higher (Good)
	 * - Yellow: Between 15 and 59 FPS (Warning)
	 * - Red: Below 15 FPS (Critical)
	 */
	public static void drawClientFPS() {
		// Only render if client data is enabled (assumed 'clientData' is a boolean field in Client)
		if (!clientData) {
			return;
		}

		// Determine the color of the FPS text based on its value
		int fpsColor = getFpsColor();

		// Format the display text for FPS
		String fpsText = "FPS: " + fps;

		newSmallFont.drawBasicString(fpsText, 5, 15, fpsColor, 1);
	}

	/* --------------------------------------------------------- Utility Methods */

	/**
	 * Determines the appropriate color for the FPS display based on its value.
	 * @return An integer representing the color (RGB).
	 */
	private static int getFpsColor() {
		if (fps < 15) { // If FPS is critically low
			return COL_CRITICAL;
		}
		if (fps >= 60) { // If FPS is good
			return COL_GOOD;
		}
		// If FPS is in a warning range
		return COL_WARNING;
	}
}
