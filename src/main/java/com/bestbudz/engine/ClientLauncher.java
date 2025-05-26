package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.client.frame.UIDockFrame;
import com.bestbudz.client.util.DockSync;
import com.bestbudz.config.SettingHandler;
import com.bestbudz.engine.input.Keyboard;
import com.bestbudz.engine.input.MouseManager;

import com.formdev.flatlaf.FlatDarkLaf;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

			try {
				UIManager.setLookAndFeel(new FlatDarkLaf());
			} catch (Exception ex) {
				System.err.println("Failed to initialize FlatLaf");
			}

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

			Client client = new Client();
			Client.instance = client;
			Client.instance.refreshFrameSize(canvas, canvas.getWidth(), canvas.getHeight());
			canvas.createBufferStrategy(GraphicsConfig.BUFFERS);

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
			Thread gameThread = new Thread(engine, "GameLoop");
			gameThread.start();

			frame.setMinimumSize(new Dimension(1280, 758));

			// 🪟 UI Dock Frame
			UIDockFrame uiDock = new UIDockFrame(frame);
			uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());

			frame.addWindowStateListener(e -> {
				int state = e.getNewState();
				if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) {
					SwingUtilities.invokeLater(() -> {
						// Sync dock to frame
						int dockWidth = 300;
						uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());
						uiDock.setSize(dockWidth, frame.getHeight());
						uiDock.revalidate();
						uiDock.repaint();
					});
				}
			});

			frame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					engine.shutdown(); // 🛑 stop the loop

					try {
						gameThread.join(200); // ⏳ wait up to 200ms for safe stop
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}

					if (UIDockFrame.getInstance() != null) {
						UIDockFrame.getInstance().saveLayoutState();
					}
					SettingHandler.save();
					client.cleanUpForQuit(); // 🧹 safe cleanup
					System.exit(0);
				}
			});

			frame.addComponentListener(new ComponentAdapter() {
				@Override
				public void componentMoved(ComponentEvent e) {
					Point location = frame.getLocationOnScreen();
					int x = location.x + frame.getWidth();
					int y = location.y;
					uiDock.setLocation(x, y);
				}

				@Override
				public void componentResized(ComponentEvent e) {
					uiDock.setLocation(frame.getX() + frame.getWidth(), frame.getY());
					int height = frame.getHeight();
					uiDock.setSize(uiDock.getWidth(), height - 8);

					uiDock.revalidate();
					uiDock.repaint();
				}

			});


		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}
