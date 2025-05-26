package com.bestbudz.client.ui.panel;

import java.awt.Component;

public interface UIPanel {
	String getPanelID();
	Component getComponent();
	void onActivate();
	void onDeactivate();
	default String getPanelIconPath() {
		return null; // panels can optionally override
	}

}
