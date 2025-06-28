package com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects;

import com.bestbudz.dock.ui.panel.client.bubblebudz.core.GameEventManager;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.types.pop.PopEffectData;
import com.bestbudz.dock.ui.panel.client.bubblebudz.system.effects.types.score.ScoreEffectData;

/**
 * Event-driven effect system that automatically creates visual effects
 * in response to game events. This keeps the input handler clean and
 * allows for easy addition of new effect triggers.
 */
public class EffectEventListener implements GameEventManager.GameEventListener {
	private final EffectManager effectManager;

	public EffectEventListener(EffectManager effectManager) {
		this.effectManager = effectManager;
	}

	/**
	 * Subscribe this listener to game events.
	 * Call this during game initialization.
	 */
	public void subscribeToEvents(GameEventManager eventManager) {
		eventManager.subscribe(GameEventManager.BubblePoppedEvent.class, this);
		eventManager.subscribe(GameEventManager.ScoreChangedEvent.class, this);
		eventManager.subscribe(GameEventManager.RoundCompleteEvent.class, this);
	}

	/**
	 * Main event handler - dispatches to specific handlers based on event type
	 */
	@Override
	public void onEvent(GameEventManager.GameEvent event) {
		if (event instanceof GameEventManager.BubblePoppedEvent) {
			onBubblePopped((GameEventManager.BubblePoppedEvent) event);
		} else if (event instanceof GameEventManager.ScoreChangedEvent) {
			onScoreChanged((GameEventManager.ScoreChangedEvent) event);
		} else if (event instanceof GameEventManager.RoundCompleteEvent) {
			onRoundComplete((GameEventManager.RoundCompleteEvent) event);
		}
	}

// Update your EffectEventListener to handle geometric shapes in pop effects

	/**
	 * Handle bubble popped events by creating appropriate visual effects
	 */
	public void onBubblePopped(GameEventManager.BubblePoppedEvent event) {
		System.out.println("EffectEventListener: Creating effects for bubble pop at (" +
			event.x + "," + event.y + ") with score " + event.score);

		try {
			EffectData popEffectData;

			if (event.shapeType != null) {
				// Create geometric pop effect with rotation
				popEffectData = new PopEffectData(event.x, event.y, event.radius, event.color,
					event.shapeType, event.rotation);
			} else {
				popEffectData = new EffectData(event.x, event.y, event.radius, event.color);
			}

			effectManager.addEffect("pop", popEffectData);

			// Create score effect (floating score text with particles)
			ScoreEffectData scoreEffectData = new ScoreEffectData(
				event.x, event.y, event.radius, event.color, event.score);
			System.out.println("Creating score effect...");
			effectManager.addEffect("score", scoreEffectData);

			System.out.println("Effects created successfully!");
		} catch (Exception e) {
			System.err.println("Error creating effects: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Example: Handle score changed events for UI effects
	 */
	public void onScoreChanged(GameEventManager.ScoreChangedEvent event) {
		// Could create UI pulse effects when score increases
	}

	/**
	 * Example: Handle round complete events for celebration effects
	 */
	public void onRoundComplete(GameEventManager.RoundCompleteEvent event) {
		// Could create celebration effects when round ends
	}
}