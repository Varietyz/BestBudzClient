package com.bestbudz.dock.ui.panel.social.stoners;

public class StonerEntry {
	final String name;
	final boolean isHigh;
	final String title;
	final String titleColor;

	public StonerEntry(String name, boolean isHigh) {
		this(name, isHigh, null, null);
	}

	public StonerEntry(String name, boolean isHigh, String title, String titleColor) {
		this.name = name;
		this.isHigh = isHigh;
		this.title = title;
		this.titleColor = titleColor;
	}
}