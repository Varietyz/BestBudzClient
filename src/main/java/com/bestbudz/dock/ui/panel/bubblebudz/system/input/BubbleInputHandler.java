
package com.bestbudz.dock.ui.panel.bubblebudz.system.input;

import com.bestbudz.dock.ui.panel.bubblebudz.core.BubbleBudzGame;
import com.bestbudz.dock.ui.panel.bubblebudz.core.GameEventManager;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.Bubble;
import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import com.bestbudz.dock.ui.panel.bubblebudz.game.modes.GameMode;
import java.awt.event.MouseEvent;

public class BubbleInputHandler implements InputHandler {
	private final BubbleBudzGame gameLogic;
	private final GameEventManager eventManager;
	private final GameMode gameMode;
	private int panelHeight;

	public BubbleInputHandler(BubbleBudzGame gameLogic, GameEventManager eventManager,
							  GameMode gameMode) {
		this.gameLogic = gameLogic;
		this.eventManager = eventManager;
		this.gameMode = gameMode;
	}

	public void setPanelHeight(int height) {
		this.panelHeight = height;
	}

	@Override
	public boolean handleClick(int x, int y, MouseEvent originalEvent) {
		Bubble clickedBubble = gameLogic.findClickedBubble(x, y);
		if (clickedBubble != null) {

			int totalPoints = gameMode.calculateScore(clickedBubble, panelHeight);
			int oldScore = gameMode.getCurrentScore();
			int newScore = oldScore + totalPoints;

			GeometricShape.ShapeType shapeType = null;
			float rotation = 0f;
			if (clickedBubble instanceof GeometricShape) {
				GeometricShape geoShape = (GeometricShape) clickedBubble;
				shapeType = geoShape.getShapeType();
				rotation = geoShape.getRotation();
			}

			if (shapeType != null) {
				eventManager.publish(new GameEventManager.BubblePoppedEvent(
					clickedBubble.x, clickedBubble.y, clickedBubble.currentRadius,
					totalPoints, clickedBubble.color, shapeType, rotation));
			} else {
				eventManager.publish(new GameEventManager.BubblePoppedEvent(
					clickedBubble.x, clickedBubble.y, clickedBubble.currentRadius,
					totalPoints, clickedBubble.color));
			}

			gameMode.setCurrentScore(newScore);

			eventManager.publish(new GameEventManager.ScoreChangedEvent(oldScore, newScore));

			return true;
		}
		return false;
	}

	@Override
	public void handleHover(int x, int y, MouseEvent originalEvent) {

	}
}
