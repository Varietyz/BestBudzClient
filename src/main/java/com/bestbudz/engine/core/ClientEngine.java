package com.bestbudz.engine.core;

import com.bestbudz.ui.handling.input.Keyboard;
import com.bestbudz.engine.core.login.LoginRenderer;
import com.bestbudz.graphics.buffer.ImageProducer;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ClientEngine implements FocusListener, WindowListener {

	public static LoginRenderer loginRenderer;
	protected static int screenGliding;
	final int minDelay;
	boolean shouldDebug;
	public static ImageProducer gameWorldScreen;
	public static boolean awtFocus;
	public static int idleTime;
	private static int delayTime;
	private boolean shouldClearScreen;

	public ClientEngine() {
		delayTime = 20;
		minDelay = 1;
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
	}

	public static final void method4() {
		delayTime = 1000;
	}

	public final void focusGained(FocusEvent focusevent) {
		awtFocus = true;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void focusLost(FocusEvent focusevent) {
		awtFocus = false;
		for (int i = 0; i < 128; i++) {
			Keyboard.keyArray[i] = 0;
		}

	}

	public final void windowOpened(WindowEvent windowevent) {
	}

	public final void windowClosing(WindowEvent windowevent) {
	}

	public final void windowClosed(WindowEvent windowevent) {
	}

	public final void windowIconified(WindowEvent windowevent) {
	}

	public final void windowDeiconified(WindowEvent windowevent) {
	}

	public final void windowActivated(WindowEvent windowevent) {
	}

	public final void windowDeactivated(WindowEvent windowevent) {
	}

	void raiseWelcomeScreen() {
	}

	public void startRunnable(Runnable runnable, int i) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(i);
	}

}