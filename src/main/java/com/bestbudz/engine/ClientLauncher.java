package com.bestbudz.engine;

import com.bestbudz.cache.Signlink;
import com.bestbudz.config.SettingHandler;
import com.bestbudz.graphics.DrawingArea;
import com.bestbudz.rendering.Rasterizer;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.net.InetAddress;
import javax.swing.*;

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
			frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			//frame.setSize(Client.frameWidth, Client.frameHeight);
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


			client.setCanvas(canvas);
			canvas.addKeyListener(client);
			canvas.addMouseListener(client);
			canvas.addMouseMotionListener(client);
			canvas.addMouseWheelListener(client);
			canvas.addFocusListener(client);
			canvas.requestFocusInWindow();

			GameEngine engine = new GameEngine(canvas, client);
			new Thread(engine, "GameLoop").start();

			frame.setMinimumSize(new Dimension(1280, 720));



		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}

