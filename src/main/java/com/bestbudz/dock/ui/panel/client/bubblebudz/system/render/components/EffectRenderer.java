package com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.EffectManager;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;
import java.awt.Shape;

public class EffectRenderer implements RenderComponent
{
	private final EffectManager effectManager;

	public EffectRenderer(EffectManager effectManager) {
		this.effectManager = effectManager;
	}

	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		if (!context.isLoading && context.gameArea != null) {
			// Clip to game area
			Shape oldClip = g2d.getClip();
			g2d.clipRect(context.gameArea.x, context.gameArea.y,
				context.gameArea.width, context.gameArea.height);

			effectManager.renderEffects(g2d);

			g2d.setClip(oldClip);
		}
	}

	@Override
	public int getRenderOrder() { return 300; }
}