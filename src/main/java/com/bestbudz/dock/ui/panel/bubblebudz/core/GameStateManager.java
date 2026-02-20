package com.bestbudz.dock.ui.panel.bubblebudz.core;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameStateManager {
	public enum GameState { PLAYING, LOADING }

	private GameState currentState = GameState.PLAYING;
	private long stateStartTime = System.currentTimeMillis();
	private List<GameStateListener> listeners = new CopyOnWriteArrayList<>();

	public interface GameStateListener {
		void onStateChanged(GameState oldState, GameState newState);
	}

	public void transitionTo(GameState newState) {
		GameState oldState = currentState;
		currentState = newState;
		stateStartTime = System.currentTimeMillis();

		for (GameStateListener listener : listeners) {
			listener.onStateChanged(oldState, newState);
		}
	}

	public GameState getCurrentState() { return currentState; }
	public long getStateStartTime() { return stateStartTime; }

	public void addStateListener(GameStateListener listener) {
		listeners.add(listener);
	}

	public void removeStateListener(GameStateListener listener) {
		listeners.remove(listener);
	}
}