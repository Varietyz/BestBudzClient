package com.bestbudz.engine;

import java.awt.*;

public class GameCanvas extends Canvas {
	public GameCanvas() {
		setPreferredSize(new Dimension(GraphicsConfig.MIN_WIDTH, GraphicsConfig.MIN_HEIGHT));
		setBackground(Color.BLACK);
		setFocusable(true);
		setIgnoreRepaint(true);
		setFocusTraversalKeysEnabled(false);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(1280, 720);
	}



}
