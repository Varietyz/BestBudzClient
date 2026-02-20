package com.bestbudz.dock.ui.panel.bubblebudz.ui.managers;

import com.bestbudz.dock.ui.panel.bubblebudz.ui.components.GameModeButton;
import com.bestbudz.dock.ui.panel.bubblebudz.ui.overlays.GameModeOverlay;
import com.bestbudz.dock.ui.panel.bubblebudz.game.modes.GameMode;
import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Manages game mode selection UI components
 */
public class GameModeManager implements
	GameModeButton.GameModeButtonListener,
	GameModeOverlay.GameModeOverlayListener {

	private final GameModeButton button;
	private final GameModeOverlay overlay;
	private GameModeChangeListener listener;
	private String currentModeName = "Classic";

	public interface GameModeChangeListener {
		void onGameModeChanged(GameMode gameMode, String modeName);
	}

	public GameModeManager(GameModeChangeListener listener) {
		this.listener = listener;
		this.button = new GameModeButton(this);
		this.overlay = new GameModeOverlay(this);
	}

	public void render(Graphics2D g2d, int panelWidth, int panelHeight) {
		// Position button between timer and viewport edge
		int buttonWidth = 60;
		int buttonHeight = 16;
		int buttonX = panelWidth - buttonWidth - 10;
		int buttonY = 5; // Changed from 45 - position above timer, below logo/title

		button.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		button.render(g2d, currentModeName);

		// Render overlay if visible
		overlay.render(g2d, currentModeName);
	}

	public void handleMousePressed(MouseEvent e) {
		button.mousePressed(e);
		overlay.mousePressed(e);
	}

	public void handleMouseReleased(MouseEvent e) {
		button.mouseReleased(e);
	}

	public void handleMouseMoved(MouseEvent e) {
		button.updateHover(e.getX(), e.getY());
		overlay.updateHover(e.getX(), e.getY());
	}

	public boolean isOverlayVisible() {
		return overlay.isVisible();
	}

	// GameModeButton.GameModeButtonListener implementation
	@Override
	public void onButtonClicked() {
		// Get panel dimensions - this should be passed from the main panel
		// For now, using reasonable defaults
		overlay.show(400, 300);
	}

	// GameModeOverlay.GameModeOverlayListener implementation
	@Override
	public void onGameModeSelected(GameMode gameMode, String modeName) {
		this.currentModeName = modeName;
		if (listener != null) {
			listener.onGameModeChanged(gameMode, modeName);
		}
	}

	@Override
	public void onOverlayClosed() {
		// Overlay closed, no action needed
	}

	public void setCurrentMode(String modeName) {
		this.currentModeName = modeName;
	}

	public void showOverlay(int panelWidth, int panelHeight) {
		overlay.show(panelWidth, panelHeight);
	}
}