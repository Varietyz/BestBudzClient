package com.bestbudz.client.ui.panel;

import java.awt.Component;

public interface UIPanel {
	void updateText();

	String getPanelID();
	Component getComponent();
	void onActivate();
	void onDeactivate();
	default String getPanelIconPath() {
		return null; // panels can optionally override
	}
	default int[] getBlockedInterfaces() {
		return new int[0]; // override in panel
	}
	void updateDockText(int index, String text);
}
