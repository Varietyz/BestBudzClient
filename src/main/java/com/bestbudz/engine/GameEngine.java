package com.bestbudz.engine;

import com.bestbudz.graphics.DrawingArea;
import java.awt.*;
import java.awt.image.BufferStrategy;

public class GameEngine implements Runnable {
	private final GameCanvas canvas;
	private final Client client;
	private volatile boolean running = true;

	public GameEngine(GameCanvas canvas, Client client) {
		this.canvas = canvas;
		this.client = client;
	}
	@Override
	public void run() {
		boolean started = false;
		final long LOGIC_TICK_NS = GraphicsConfig.LOGIC_TICK_MS * 1_000_000L;
		final long FRAME_TIME_NS = 1_000_000_000L / GraphicsConfig.TARGET_FPS;

		long lastLogic = System.nanoTime();
		long lastFrame = System.nanoTime();

		while (running) {
			// Wait until canvas is visible
			if (!canvas.isDisplayable()) {
				try { Thread.sleep(10); } catch (InterruptedException ignored) {}
				continue;
			}

			int lockedW = canvas.getWidth();
			int lockedH = canvas.getHeight();

			if (lockedW != Client.screenAreaWidth || lockedH != Client.screenAreaHeight) {
				BufferStrategy bs = canvas.getBufferStrategy();
				if (bs != null) bs.dispose();

				client.refreshFrameSize(canvas, lockedW, lockedH);
				Client.setBounds();
				client.calcCameraPos();

				DrawingArea.setDrawingArea(Client.frameHeight, 0, Client.frameWidth, 0);
				DrawingArea.setAllPixelsToZero(); // Force-clear the new buffer

				try {
					canvas.createBufferStrategy(GraphicsConfig.BUFFERS);
				} catch (IllegalStateException ignored) {}

				continue;
			}




			BufferStrategy bufferStrategy = canvas.getBufferStrategy();
			if (bufferStrategy == null) {
				try {
					canvas.createBufferStrategy(GraphicsConfig.BUFFERS);
				} catch (IllegalStateException ignored) {}
				continue;
			}

			long frameStart = System.nanoTime();
			boolean contentsLost = false;

			do {
				boolean contentsRestored;
				do {
					Graphics2D g = null;
					try {
						g = (Graphics2D) bufferStrategy.getDrawGraphics();
						if (!started) {
							client.startUp(g);

							started = true;
						}
						DrawingArea.setAllPixelsToZero();



						long now = System.nanoTime();
						int logicRuns = 0;
						while (now - lastLogic >= LOGIC_TICK_NS && logicRuns < 5) {
							client.processGameLoop(g, canvas);
							lastLogic += LOGIC_TICK_NS;
							logicRuns++;
						}

						client.processDrawing(g, canvas);
					} finally {
						if (g != null) g.dispose();
					}
					contentsRestored = bufferStrategy.contentsRestored();
				} while (contentsRestored);

				try {
					bufferStrategy.show();
				} catch (IllegalStateException ex) {
					try {
						canvas.createBufferStrategy(GraphicsConfig.BUFFERS);
					} catch (IllegalStateException ignored) {}
					continue;
				}

				contentsLost = bufferStrategy.contentsLost();
			} while (contentsLost);

			long frameEnd = System.nanoTime();
			long elapsed = frameEnd - frameStart;
			long sleepNs = FRAME_TIME_NS - elapsed;
			if (sleepNs > 0) {
				try {
					Thread.sleep(sleepNs / 1_000_000L, (int)(sleepNs % 1_000_000L));
				} catch (InterruptedException ignored) {}
			}
		}
	}



}
