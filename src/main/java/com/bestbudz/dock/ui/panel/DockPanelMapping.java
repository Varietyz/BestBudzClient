package com.bestbudz.dock.ui.panel;

import com.bestbudz.dock.ui.panel.game.AchievementsPanel;
import com.bestbudz.dock.ui.panel.game.InfoTabPanel;
import com.bestbudz.dock.ui.panel.social.StonersPanel;
import com.bestbudz.dock.util.UIPanel;

public enum DockPanelMapping {
	QUEST_TAB(29501, 29601, "Info Tab", InfoTabPanel.class),
	ACHIEVEMENTS(31006, 31086, "Achievements", AchievementsPanel.class),
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
