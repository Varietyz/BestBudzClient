package com.bestbudz.dock.ui.panel.bubblebudz.system.render;

import java.awt.Graphics2D;

public interface RenderComponent {
	void render(Graphics2D g2d, RenderContext context);
	int getRenderOrder();
}