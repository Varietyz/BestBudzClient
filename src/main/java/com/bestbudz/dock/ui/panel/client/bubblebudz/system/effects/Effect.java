package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects;

import java.awt.Graphics2D;

public interface Effect {
	void update();
	boolean isExpired();
	void render(Graphics2D g2d);
}