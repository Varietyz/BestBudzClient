package com.bestbudz.dock.ui.panel.bubblebudz.system.effects;

import com.bestbudz.dock.ui.panel.bubblebudz.core.GameEventManager;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.types.pop.PopEffectData;
import com.bestbudz.dock.ui.panel.bubblebudz.system.effects.types.score.ScoreEffectData;

public class EffectEventListener implements GameEventManager.GameEventListener {
	private final EffectManager effectManager;

	public EffectEventListener(EffectManager effectManager) {
		this.effectManager = effectManager;
	}

	public void subscribeToEvents(GameEventManager eventManager) {
		eventManager.subscribe(GameEventManager.BubblePoppedEvent.class, this);
		eventManager.subscribe(GameEventManager.ScoreChangedEvent.class, this);
		eventManager.subscribe(GameEventManager.RoundCompleteEvent.class, this);
	}

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

	public void onBubblePopped(GameEventManager.BubblePoppedEvent event) {
		System.out.println("EffectEventListener: Creating effects for bubble pop at (" +
			event.x + "," + event.y + ") with score " + event.score);

		try {
			EffectData popEffectData;

			if (event.shapeType != null) {

				popEffectData = new PopEffectData(event.x, event.y, event.radius, event.color,
					event.shapeType, event.rotation);
			} else {
				popEffectData = new EffectData(event.x, event.y, event.radius, event.color);
			}

			effectManager.addEffect("pop", popEffectData);

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

	public void onScoreChanged(GameEventManager.ScoreChangedEvent event) {

	}

	public void onRoundComplete(GameEventManager.RoundCompleteEvent event) {

	}
}
