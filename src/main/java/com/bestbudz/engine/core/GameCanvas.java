package com.bestbudz.engine.core;

import com.bestbudz.engine.config.EngineConfig;
import java.awt.*;

public class GameCanvas extends Canvas {
	public GameCanvas() {
		setPreferredSize(new Dimension(EngineConfig.MIN_WIDTH, EngineConfig.MIN_HEIGHT));
		setBackground(Color.BLACK);
		setFocusable(true);
		setIgnoreRepaint(true);
		setFocusTraversalKeysEnabled(false);

	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(1280, 758);
	}



}
