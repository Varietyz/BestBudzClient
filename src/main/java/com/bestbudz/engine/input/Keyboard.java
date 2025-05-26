package com.bestbudz.engine.input;

import com.bestbudz.engine.Client;
import com.bestbudz.ui.Console;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public final class Keyboard implements KeyListener {
	public static final Console console = new Console();
	public static final int[] keyArray = new int[128];
	private static final int[] charQueue = new int[128];
	private static int readIndex;
	private static int writeIndex;

	@Override
	public void keyTyped(KeyEvent keyevent) {
	}

	@Override
	public void keyPressed(KeyEvent keyevent) {
		Client.idleTime = 0;
		int i = keyevent.getKeyCode();
		int j = keyevent.getKeyChar();
		if (j == 96) {
			console.openConsole = !console.openConsole;
		}
		if (keyevent.isControlDown()) {
			Client.controlIsDown = true;
		}
		if (i == KeyEvent.VK_F1) {
			Client.setTab(3);
		} else if (i == KeyEvent.VK_ESCAPE) {
			Client.setTab(10);
		} else if (i == KeyEvent.VK_F2) {
			Client.setTab(4);
		} else if (i == KeyEvent.VK_F3) {
			Client.setTab(5);
		} else if (i == KeyEvent.VK_F4) {
			Client.setTab(6);
		} else if (i == KeyEvent.VK_F5) {
			Client.setTab(0);
		}
		if (j < 30)
			j = 0;
		if (i == 37)
			j = 1;
		if (i == 39)
			j = 2;
		if (i == 38)
			j = 3;
		if (i == 40)
			j = 4;
		if (i == 17)
			j = 5;
		if (i == 8)
			j = 8;
		if (i == 127)
			j = 8;
		if (i == 9)
			j = 9;
		if (i == 10)
			j = 10;
		if (i >= 112 && i <= 123)
			j = (1008 + i) - 112;
		if (i == 36)
			j = 1000;
		if (i == 35)
			j = 1001;
		if (i == 33)
			j = 1002;
		if (i == 34)
			j = 1003;
		if (j > 0 && j < 128)
			keyArray[j] = 1;
		if (j > 4) {
			charQueue[writeIndex] = j;
			writeIndex = writeIndex + 1 & 0x7f;
		}
	}

	@Override
	public void keyReleased(KeyEvent keyevent) {
		Client.idleTime = 0;
		int i = keyevent.getKeyCode();
		char c = keyevent.getKeyChar();
		if (i == KeyEvent.VK_CONTROL) {
			Client.controlIsDown = false;
		}
		if (c < '\036')
			c = '\0';
		if (i == 37)
			c = '\001';
		if (i == 39)
			c = '\002';
		if (i == 38)
			c = '\003';
		if (i == 40)
			c = '\004';
		if (i == 17)
			c = '\005';
		if (i == 8)
			c = '\b';
		if (i == 127)
			c = '\b';
		if (i == 9)
			c = '\t';
		if (i == 10)
			c = '\n';
		if (c > 0 && c < '\200')
			keyArray[c] = 0;
	}

	public static int readChar(int dummy) {
		while (dummy >= 0) {
			for (int j = 1; j > 0; j++)
				;
		}
		int k = -1;
		if (writeIndex != readIndex) {
			k = charQueue[readIndex];
			readIndex = readIndex + 1 & 0x7f;
		}
		return k;
	}
}
