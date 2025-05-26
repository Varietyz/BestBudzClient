package com.bestbudz.engine;

import com.bestbudz.engine.input.Keyboard;
import com.bestbudz.graphics.buffer.ImageProducer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class ClientEngine implements FocusListener, WindowListener {

	public LoginRenderer loginRenderer;
	public static long aLong29;
	protected int screenGliding;
	final int minDelay;
	boolean shouldDebug;
	static ImageProducer gameWorldScreen;
	boolean awtFocus;
	public static int idleTime;
	private int delayTime;
	private boolean shouldClearScreen;

	ClientEngine() {
		delayTime = 20;
		minDelay = 1;
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
	}

	final void method4() {
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

	void drawLoadingText(Graphics2D g, int percentage, String loadingText) {
		Font font = new Font("Helvetica", Font.BOLD, 13);
		FontMetrics fontmetrics = g.getFontMetrics(font);
		Font font1 = new Font("Helvetica", Font.PLAIN, 13);
		FontMetrics fontmetrics1 = g.getFontMetrics(font1);

		g.setColor(Color.black);
		g.fillRect(0, 0, GraphicsConfig.MIN_WIDTH, GraphicsConfig.MIN_HEIGHT);

		Color color = new Color(140, 17, 17);
		int y = GraphicsConfig.MIN_HEIGHT / 2 - 18;
		g.setColor(color);
		g.drawRect(GraphicsConfig.MIN_WIDTH / 2 - 152, y, 304, 34);
		g.fillRect(GraphicsConfig.MIN_WIDTH / 2 - 150, y + 2, percentage * 3, 30);
		g.setColor(Color.black);
		g.fillRect((GraphicsConfig.MIN_WIDTH / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
		g.setFont(font);
		g.setColor(Color.white);
		g.drawString(loadingText, (GraphicsConfig.MIN_WIDTH - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
		g.drawString("", (GraphicsConfig.MIN_WIDTH - fontmetrics1.stringWidth("")) / 2, y - 8);
	}
}