package com.bestbudz.dock.ui.panel.bubblebudz.core;

import com.bestbudz.dock.ui.panel.bubblebudz.game.entities.GeometricShape;
import java.awt.Color;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameEventManager {
	private Map<Class<?>, List<GameEventListener>> listeners = new HashMap<>();

	public interface GameEventListener {
		void onEvent(GameEvent event);
	}

	public abstract static class GameEvent {
		private final long timestamp = System.currentTimeMillis();
		public long getTimestamp() { return timestamp; }
	}

	public static class BubblePoppedEvent extends GameEvent {
		public final float x, y, radius;
		public final int score;
		public final Color color;
		public final GeometricShape.ShapeType shapeType;
		public final float rotation; // Add rotation field

		// Constructor for geometric shapes
		public BubblePoppedEvent(float x, float y, float radius, int score, Color color,
								 GeometricShape.ShapeType shapeType, float rotation) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.score = score;
			this.color = color;
			this.shapeType = shapeType;
			this.rotation = rotation;
		}

		// Constructor for regular bubbles
		public BubblePoppedEvent(float x, float y, float radius, int score, Color color) {
			this.x = x;
			this.y = y;
			this.radius = radius;
			this.score = score;
			this.color = color;
			this.shapeType = null;
			this.rotation = 0f;
		}
	}

	public static class ScoreChangedEvent extends GameEvent {
		public final int oldScore, newScore;

		public ScoreChangedEvent(int oldScore, int newScore) {
			this.oldScore = oldScore; this.newScore = newScore;
		}
	}

	public static class RoundCompleteEvent extends GameEvent {
		public final int finalScore;
		public final boolean isNewHighScore;

		public RoundCompleteEvent(int finalScore, boolean isNewHighScore) {
			this.finalScore = finalScore; this.isNewHighScore = isNewHighScore;
		}
	}

	public void subscribe(Class<?> eventType, GameEventListener listener) {
		listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
	}

	public void unsubscribe(Class<?> eventType, GameEventListener listener) {
		List<GameEventListener> eventListeners = listeners.get(eventType);
		if (eventListeners != null) {
			eventListeners.remove(listener);
		}
	}

	public void publish(GameEvent event) {
		List<GameEventListener> eventListeners = listeners.get(event.getClass());
		if (eventListeners != null) {
			for (GameEventListener listener : eventListeners) {
				listener.onEvent(event);
			}
		}
	}
}