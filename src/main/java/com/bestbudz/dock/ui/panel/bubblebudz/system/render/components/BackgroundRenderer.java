package com.bestbudz.dock.ui.panel.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.bubblebudz.ui.BubbleBudzStyle;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;

public class BackgroundRenderer implements RenderComponent
{
	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		// Background overlay
		g2d.setColor(BubbleBudzStyle.BG_OVERLAY);
		g2d.fillRect(0, 0, context.width, context.height);

		// Subtle panel background
		g2d.setColor(BubbleBudzStyle.PANEL_BG);
		g2d.fillRect(0, 0, context.width, context.height);
	}

	@Override
	public int getRenderOrder() { return 0; }
}