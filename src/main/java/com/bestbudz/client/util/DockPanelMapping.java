package com.bestbudz.client.util;

import com.bestbudz.client.ui.panel.AchievementsPanel;
import com.bestbudz.client.ui.panel.QuestTabPanel;
import com.bestbudz.client.ui.panel.StonersPanel;
import com.bestbudz.client.ui.panel.UIPanel;

public enum DockPanelMapping {
	QUEST_TAB(29501, 29601, "Info Tab", QuestTabPanel.class),
	ACHIEVEMENTS(31006, 31086, "Achievements", AchievementsPanel.class),
	EQUIPMENT(15106, 15150, "Equipment", com.bestbudz.client.ui.panel.EquipmentPanel.class),
	STONERS(5065, 5100, "Stoners", StonersPanel.class);

	public final int startFrame;
	public final int endFrame;
	public final String panelID;
	public final Class<? extends UIPanel> panelClass;

	DockPanelMapping(int start, int end, String panelID, Class<? extends UIPanel> panelClass) {
		this.startFrame = start;
		this.endFrame = end;
		this.panelID = panelID;
		this.panelClass = panelClass;
	}

	public boolean contains(int frame) {
		return frame >= startFrame && frame < endFrame;
	}

	public int indexFor(int frame) {
		return frame - startFrame;
	}

	public static DockPanelMapping fromFrame(int frame) {
		for (DockPanelMapping mapping : values()) {
			if (mapping.contains(frame)) return mapping;
		}
		return null;
	}
}
