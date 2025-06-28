package com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.client.bubblebudz.ui.BubbleBudzRenderer;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;

public class LoadingRenderer implements RenderComponent
{
	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		if (context.isLoading) {
			BubbleBudzRenderer.drawLoading(g2d, context.width, context.height,
				context.loadingMessage, context.loadingStartTime);
		}
	}

	@Override
	public int getRenderOrder() { return 500; }
}
