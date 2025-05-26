package com.bestbudz.client.ui.manager;

import com.bestbudz.client.ui.panel.UIPanel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UIPanelManager {
	private final Map<String, UIPanel> panelMap = new HashMap<>();

	public void register(UIPanel panel) {
		panelMap.put(panel.getPanelID(), panel);
	}

	public void unregister(String id) {
		UIPanel panel = panelMap.remove(id);
		if (panel != null) {
			panel.onDeactivate();
		}
	}

	public UIPanel getPanel(String id) {
		return panelMap.get(id);
	}

	public Collection<UIPanel> getAllPanels() {
		return panelMap.values();
	}
}
