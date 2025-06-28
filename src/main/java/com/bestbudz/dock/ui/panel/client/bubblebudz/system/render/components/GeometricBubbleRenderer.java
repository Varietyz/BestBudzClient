package com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.client.bubblebudz.core.BubbleBudzGame;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.client.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;
import java.awt.Shape;

/**
 * Renderer that can handle both regular bubbles and geometric shapes.
 * Automatically detects shape type and renders appropriately.
 */
public class GeometricBubbleRenderer implements RenderComponent {
	private final BubbleBudzGame gameLogic;

	public GeometricBubbleRenderer(BubbleBudzGame gameLogic) {
		this.gameLogic = gameLogic;
	}

	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		if (!context.isLoading && context.gameArea != null) {
			// Clip to game area
			Shape oldClip = g2d.getClip();
			g2d.clipRect(context.gameArea.x, context.gameArea.y,
				context.gameArea.width, context.gameArea.height);

			// Draw bubbles (both regular and geometric)
			for (Bubble bubble : gameLogic.getBubbles()) {
				if (bubble instanceof GeometricShape) {
					GeometricShape.drawGeometricShape(g2d, (GeometricShape) bubble);
				} else {
					BubbleBudzGame.drawBubble(g2d, bubble);
				}
			}

			g2d.setClip(oldClip);
		}
	}

	@Override
	public int getRenderOrder() { return 200; }
}