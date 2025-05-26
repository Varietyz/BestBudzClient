package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.config.SettingHandler;
import com.bestbudz.engine.input.Keyboard;
import com.bestbudz.engine.input.MouseManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;

public final class ClientLauncher {
	public static void main(String[] args) {
		try {
			SettingHandler.load();
			Client.nodeID = 10;
			Client.portOff = 0;
			Client.setHighMem();
			Client.isMembers = true;
			Signlink.storeid = 32;
			Signlink.startpriv(InetAddress.getLocalHost());

			GraphicsConfig.MIN_WIDTH = Client.frameWidth;
			GraphicsConfig.MIN_HEIGHT = Client.frameHeight;

			final JFrame frame = new JFrame(GraphicsConfig.TITLE);
			GameCanvas canvas = new GameCanvas();
			frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			frame.setResizable(true);
			canvas.setPreferredSize(
				new Dimension(Client.frameWidth, Client.frameHeight)
			);

			frame.add(canvas);
			frame.pack();
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);

			while (!canvas.isDisplayable()) {
				try { Thread.sleep(10); } catch (InterruptedException ignored) {}
			}

			canvas.createBufferStrategy(GraphicsConfig.BUFFERS);

			Client client = new Client();
			Client.instance = client;

			MouseManager mouseManager = new MouseManager();
			Keyboard keyboard = new Keyboard();

			client.setCanvas(canvas);

			canvas.addKeyListener(keyboard);
			canvas.addMouseListener(mouseManager);
			canvas.addMouseMotionListener(mouseManager);
			canvas.addMouseWheelListener(mouseManager);
			canvas.addFocusListener(client);
			canvas.requestFocusInWindow();

			GameEngine engine = new GameEngine(canvas, client);
			new Thread(engine, "GameLoop").start();

			frame.setMinimumSize(new Dimension(1280, 720));

			// 1 ▸ Clean exit on window close
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					client.cleanUpForQuit();
					System.exit(0);
				}
			});

			// 2 ▸ Clean exit on JVM shutdown
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					client.cleanUpForQuit();
				} catch (Exception ignored) {}
			}));

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
