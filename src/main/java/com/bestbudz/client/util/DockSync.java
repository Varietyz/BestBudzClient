package com.bestbudz.client.util;

import java.awt.Insets;
import javax.swing.JFrame;

// In a neutral utility
public class DockSync {
	private static JFrame dockFrame;

	public static void setDock(JFrame frame) {
		dockFrame = frame;
	}

	public static void refreshHeight(int newHeight) {
		if (dockFrame != null) {
			Insets insets = dockFrame.getInsets();
			dockFrame.setSize(dockFrame.getWidth(), newHeight + insets.top + insets.bottom + 30);
			dockFrame.revalidate();
			dockFrame.repaint();
		}
	}


}

