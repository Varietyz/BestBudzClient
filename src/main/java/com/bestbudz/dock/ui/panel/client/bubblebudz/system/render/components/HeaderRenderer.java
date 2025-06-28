package com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.client.bubblebudz.ui.BubbleBudzRenderer;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;

public class HeaderRenderer implements RenderComponent
{
	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		if (!context.isLoading) {
			BubbleBudzRenderer.drawHeader(g2d, context.width, context.logoImage,
				context.roundStartTime, context.currentScore, context.bestScore);
		}
	}

	@Override
	public int getRenderOrder() { return 100; }
}