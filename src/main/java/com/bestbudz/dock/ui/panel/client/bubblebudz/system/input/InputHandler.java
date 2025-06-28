package com.bestbudz.dock.ui.panel.client.bubblebudz.system.input;

import java.awt.event.MouseEvent;

public interface InputHandler {
	boolean handleClick(int x, int y, MouseEvent originalEvent);
	void handleHover(int x, int y, MouseEvent originalEvent);
}