// GameModeUIRenderer.java
package com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.components;

import com.bestbudz.dock.ui.panel.client.bubblebudz.ui.managers.GameModeManager;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderComponent;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.render.RenderContext;
import java.awt.Graphics2D;

/**
 * Render component for game mode selection UI
 */
public class GameModeUIRenderer implements RenderComponent {
	private final GameModeManager gameModeManager;

	public GameModeUIRenderer(GameModeManager gameModeManager) {
		this.gameModeManager = gameModeManager;
	}

	@Override
	public void render(Graphics2D g2d, RenderContext context) {
		if (!context.isLoading) {
			gameModeManager.render(g2d, context.width, context.height);
		}
	}

	@Override
	public int getRenderOrder() {
		return 350; // Render after bubbles but before effects
	}
}