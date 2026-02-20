package com.bestbudz.dock.ui.panel.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.bubblebudz.core.BubbleBudzGame;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;
import java.awt.Shape;

public class BubbleRenderer implements RenderComponent
{
	private final BubbleBudzGame gameLogic;

	public BubbleRenderer(BubbleBudzGame gameLogic) {
		this.gameLogic = gameLogic;
	}

	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		if (!context.isLoading && context.gameArea != null) {
			// Clip to game area
			Shape oldClip = g2d.getClip();
			g2d.clipRect(context.gameArea.x, context.gameArea.y,
				context.gameArea.width, context.gameArea.height);

			// Draw bubbles
			for (Bubble bubble : gameLogic.getBubbles()) {
				BubbleBudzGame.drawBubble(g2d, bubble);
			}

			g2d.setClip(oldClip);
		}
	}

	@Override
	public int getRenderOrder() { return 200; }
}