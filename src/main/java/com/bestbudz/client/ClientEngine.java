package com.bestbudz.client;

import com.bestbudz.client.frame.ClientFrame;
import com.bestbudz.config.FrameConfig;
import com.bestbudz.graphics.buffer.ImageProducer;
import com.bestbudz.ui.Console;
import com.bestbudz.ui.LoginRenderer;
import com.bestbudz.ui.RSInterface;
import java.applet.Applet;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.SwingUtilities;
import javax.swing.UnsupportedLookAndFeelException;

public class ClientEngine extends Applet implements Runnable, ActionListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, WindowListener {

	private static final long serialVersionUID = 1L;
	public final int LEFT = 0;
	public final int RIGHT = 1;
	public final int DRAG = 2;
	public final int RELEASED = 3;
	public final int MOVE = 4;
	final int[] keyArray = new int[128];
	private final long[] aLongArray7 = new long[10];
	private final int[] charQueue = new int[128];
	public Console console = new Console();
	public LoginRenderer loginRenderer;
	public Graphics graphics;
	public int mouseX;
	public int mouseY;
	public long aLong29;
	public int clickMode3;
	public int saveClickX;
	public int saveClickY;
	public boolean isApplet;
	public boolean mouseWheelDown;
	public int mouseWheelX;
	public int mouseWheelY;
	protected int screenGliding;
	int minDelay;
	int fps;
	boolean shouldDebug;
	int myWidth;
	int myHeight;
	ImageProducer fullGameScreen;
	ClientFrame gameFrame;
	boolean awtFocus;
	int idleTime;
	int clickMode2;
	int clickMode1;
	private int anInt4;
	private int delayTime;
	private boolean shouldClearScreen;
	private int clickX;
	private int clickY;
	private int readIndex;
	private int writeIndex;

	ClientEngine() {
		delayTime = 20;
		minDelay = 1;
		shouldDebug = false;
		shouldClearScreen = true;
		awtFocus = true;
	}

	public void refreshFrameSize(boolean undecorated, int width, int height, boolean resizable, boolean full) {
		System.out.println("[LOG] refreshFrameSize called: "
			+ "undecorated=" + undecorated
			+ ", width=" + width
			+ ", height=" + height
			+ ", resizable=" + resizable
			+ ", full=" + full
		);
		Runnable task = () -> {
			boolean createdByApplet = (isApplet && !undecorated);

			if (gameFrame != null)
				gameFrame.dispose();

			if (!createdByApplet)
			{
				try
				{
					gameFrame = new ClientFrame(this, width, height, resizable, undecorated);
				}
				catch (UnsupportedLookAndFeelException e)
				{
					throw new RuntimeException(e);
				}
				catch (ClassNotFoundException e)
				{
					throw new RuntimeException(e);
				}
				catch (InstantiationException e)
				{
					throw new RuntimeException(e);
				}
				catch (IllegalAccessException e)
				{
					throw new RuntimeException(e);
				}
				gameFrame.setLocationRelativeTo(null);
				gameFrame.addWindowListener(this);
			}

			graphics = (createdByApplet ? this : gameFrame).getGraphics();
			if (!createdByApplet)
			{
				getGameComponent().addMouseWheelListener(this);
				getGameComponent().addMouseListener(this);
				getGameComponent().addMouseMotionListener(this);
				getGameComponent().addKeyListener(this);
				getGameComponent().addFocusListener(this);
			}
		};
		if( SwingUtilities.isEventDispatchThread() )
			task.run();
		else
			SwingUtilities.invokeLater( task );
	}

	public boolean appletClient() {
		return gameFrame == null && isApplet;
	}

	final void createClientFrame(int w, int h) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		System.out.println("[LOG] createClientFrame called: w=" + w + ", h=" + h);
		isApplet = false;
		myWidth = w;
		myHeight = h;

		gameFrame = new ClientFrame(this, myWidth, myHeight, Client.frameMode == Client.ScreenMode.RESIZABLE, Client.frameMode == Client.ScreenMode.FULLSCREEN);
		graphics = getGameComponent().getGraphics();
		fullGameScreen = new ImageProducer(myWidth, myHeight);
		startRunnable(this, 1);
	}

	final void initClientFrame(int w, int h) {
		isApplet = true;
		myWidth = FrameConfig.MIN_WIDTH;
		myHeight = FrameConfig.MIN_HEIGHT;
		graphics = getGameComponent().getGraphics();
		fullGameScreen = new ImageProducer(myWidth, myHeight);
		startRunnable(this, 1);
	}

	public void run() {
		getGameComponent().addMouseListener(this);
		getGameComponent().addMouseMotionListener(this);
		getGameComponent().addKeyListener(this);
		getGameComponent().addFocusListener(this);
		getGameComponent().addMouseWheelListener(this);
		if (gameFrame != null) {
			gameFrame.addWindowListener(this);
		}
		drawLoadingText(0, "Loading...");
		startUp();
		int i = 0;
		int j = 256;
		int k = 1;
		int l = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < 10; j1++) {
			aLongArray7[j1] = System.currentTimeMillis();
		}
		do {
			if (anInt4 < 0) {
				break;
			}
			if (anInt4 > 0) {
				anInt4--;
				if (anInt4 == 0) {
					exit();
					return;
				}
			}
			int k1 = j;
			int i2 = k;
			j = 300;
			k = 1;
			long l2 = System.currentTimeMillis();
			if (aLongArray7[i] == 0L) {
				j = k1;
				k = i2;
			} else if (l2 > aLongArray7[i]) {
				j = (int) ((long) (2560L * delayTime) / (l2 - aLongArray7[i]));
			}
			if (j < 25) {
				j = 25;
			}
			if (j > 256) {
				j = 256;
				k = (int) ((long) delayTime - (l2 - aLongArray7[i]) / 10L);
			}
			if (k > delayTime) {
				k = delayTime;
			}
			aLongArray7[i] = l2;
			i = (i + 1) % 10;
			if (k > 1) {
				for (int j2 = 0; j2 < 10; j2++) {
					if (aLongArray7[j2] != 0L) {
						aLongArray7[j2] += k;
					}
				}

			}
			if (k < minDelay) {
				k = minDelay;
			}
			try {
				Thread.sleep(k);
			} catch (InterruptedException interruptedexception) {
				i1++;
			}
			for (; l < 256; l += j) {
				clickMode3 = clickMode1;
				saveClickX = clickX;
				saveClickY = clickY;
				clickMode1 = 0;
				processGameLoop();
				readIndex = writeIndex;
			}

			l &= 0xff;
			if (delayTime > 0) {
				fps = (1000 * j) / (delayTime * 256);
			}
			processDrawing();
			if (shouldDebug) {
				System.out.println("ntime:" + l2);
				for (int k2 = 0; k2 < 10; k2++) {
					int i3 = ((i - k2 - 1) + 20) % 10;
					System.out.println("otim" + i3 + ":" + aLongArray7[i3]);
				}

				System.out.println("fps:" + fps + " ratio:" + j + " count:" + l);
				System.out.println("del:" + k + " deltime:" + delayTime + " mindel:" + minDelay);
				System.out.println("intex:" + i1 + " opos:" + i);
				shouldDebug = false;
				i1 = 0;
			}
		} while (true);
		if (anInt4 == -1) {
			exit();
		}
	}

	private void exit() {
		anInt4 = -2;

		cleanUpForQuit();

		if (gameFrame != null) {
			try {
				Thread.sleep(500L);
			} catch (Exception exception) {
			}
			try {
				System.exit(0);
			} catch (Throwable throwable) {
			}
		}
	}

	final void method4(int i) {
		delayTime = 1000 / i;
	}

	public final void start() {
		if (anInt4 >= 0) {
			anInt4 = 0;
		}
	}

	public final void stop() {
		if (anInt4 >= 0) {
			anInt4 = 4000 / delayTime;
		}
	}

	public final void destroy() {
		anInt4 = -1;
		try {
			Thread.sleep(5000L);
		} catch (Exception exception) {
		}
		if (anInt4 == -1) {
			exit();
		}
	}

	public final void paint(Graphics g) {
		if (graphics == null) {
			graphics = g;
		}
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void update(Graphics g) {
		if (graphics == null) {
			graphics = g;
		}
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		handleInterfaceScrolling(event);
		if (mouseX > 0 && mouseX < 512 && mouseY > Client.frameHeight - 165 - Client.extendChatArea && mouseY < Client.frameHeight - 25) {
			int scrollPos = Client.anInt1089;
			scrollPos -= rotation * 30;
			if (scrollPos < 0)
				scrollPos = 0;
			if (scrollPos > Client.anInt1211 - 110)
				scrollPos = Client.anInt1211 - 110;
			if (Client.anInt1089 != scrollPos) {
				Client.anInt1089 = scrollPos;
				Client.inputTaken = true;
			}
		}
		if (event.isControlDown()) {
			if (rotation == -1) {
				if (Client.cameraZoom > 200) {
					Client.cameraZoom -= 50;
				}
			} else {
				if (Client.cameraZoom < 900) {
					Client.cameraZoom += 50;
				}
			}
		}
	}

	public void handleInterfaceScrolling(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		int positionX = 0;
		int positionY = 0;
		int width = 0;
		int height = 0;
		int offsetX = 0;
		int offsetY = 0;
		int childID = 0;
		int tabInterfaceID = Client.tabInterfaceIDs[Client.tabID];
		if (tabInterfaceID != -1) {
			RSInterface tab = RSInterface.interfaceCache[tabInterfaceID];
			offsetX = Client.frameMode == Client.ScreenMode.FIXED ? Client.frameWidth - 218 : (Client.frameMode == Client.ScreenMode.FIXED ? 28 : Client.frameWidth - 197);
			offsetY = Client.frameMode == Client.ScreenMode.FIXED ? Client.frameHeight - 298 : (Client.frameMode == Client.ScreenMode.FIXED ? 37 : Client.frameHeight - (Client.frameWidth >= 1000 ? 37 : 74) - 267);
			if (tab.children == null) {
				return;
			}
			for (int index = 0; index < tab.children.length; index++) {
				if (RSInterface.interfaceCache[tab.children[index]].scrollMax > 0) {
					childID = index;
					positionX = tab.childX[index];
					positionY = tab.childY[index];
					width = RSInterface.interfaceCache[tab.children[index]].width;
					height = RSInterface.interfaceCache[tab.children[index]].height;
					break;
				}
			}
			if ((mouseX > (offsetX + positionX)) && (mouseY > (offsetY + positionY)) && (mouseX < (offsetX + positionX + width)) && (mouseY < (offsetY + positionY + height))) {
				if (RSInterface.interfaceCache[tab.children[childID]].scrollPosition > 0) {
					RSInterface.interfaceCache[tab.children[childID]].scrollPosition += rotation * 30;
					return;
				} else {
					if (rotation > 0) {
						RSInterface.interfaceCache[tab.children[childID]].scrollPosition += rotation * 30;
						return;
					}
				}
			}
		}
		if (Client.openInterfaceID != -1) {
			int w = 512, h = 334;
			int x = (Client.frameWidth / 2) - 256;
			int y = (Client.frameHeight / 2) - 167;
			int count = !Client.changeTabArea ? 4 : 3;
			if (Client.frameMode != Client.ScreenMode.FIXED) {
				for (int i = 0; i < count; i++) {
					if (x + w > (Client.frameWidth - 225)) {
						x = x - 30;
						if (x < 0) {
							x = 0;
						}
					}
					if (y + h > (Client.frameHeight - 182)) {
						y = y - 30;
						if (y < 0) {
							y = 0;
						}
					}
				}
			}
			RSInterface rsi = RSInterface.interfaceCache[Client.openInterfaceID];
			if (Client.openInterfaceID == 5292) {
				offsetX = Client.frameMode == Client.ScreenMode.FIXED ? 4 : (Client.frameWidth / 2) - 356;
				offsetY = Client.frameMode == Client.ScreenMode.FIXED ? 4 : (Client.frameHeight / 2) - 230;
			} else {
				offsetX = Client.frameMode == Client.ScreenMode.FIXED ? 4 : x;
				offsetY = Client.frameMode == Client.ScreenMode.FIXED ? 4 : y;
			}
			for (int index = 0; index < rsi.children.length; index++) {
				if (RSInterface.interfaceCache[rsi.children[index]].scrollMax > 0) {
					childID = index;
					positionX = rsi.childX[index];
					positionY = rsi.childY[index];
					width = RSInterface.interfaceCache[rsi.children[index]].width;
					height = RSInterface.interfaceCache[rsi.children[index]].height;
					break;
				}
			}
			if ((mouseX > (offsetX + positionX)) && (mouseY > (offsetY + positionY)) && (mouseX < (offsetX + positionX + width)) && (mouseY < (offsetY + positionY + height))) {
				if (RSInterface.interfaceCache[rsi.children[childID]].scrollPosition > 0) {
					RSInterface.interfaceCache[rsi.children[childID]].scrollPosition += rotation * 30;
				} else {
					if (rotation > 0) {
						RSInterface.interfaceCache[rsi.children[childID]].scrollPosition += rotation * 30;
					}
				}
			}
		}
	}

	public final void mouseClicked(MouseEvent mouseevent) {
	}

	public final void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		int type = e.getButton();
		if (gameFrame != null) {
			Insets insets = gameFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
		idleTime = 0;
		clickX = x;
		clickY = y;
		aLong29 = System.currentTimeMillis();
		if (type == 2) {
			mouseWheelDown = true;
			mouseWheelX = x;
			mouseWheelY = y;
			return;
		}
		if (e.isMetaDown()) {
			clickMode1 = 2;
			clickMode2 = 2;
		} else {
			clickMode1 = 1;
			clickMode2 = 1;
		}
	}

	public final void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (gameFrame != null) {
			Insets insets = gameFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
		idleTime = 0;
		clickMode2 = 0;
		mouseWheelDown = false;
		Client.instance.isExtendingChatArea = false;
	}

	public final void mouseEntered(MouseEvent mouseevent) {
	}

	public final void mouseExited(MouseEvent mouseevent) {
		idleTime = 0;
		mouseX = -1;
		mouseY = -1;
	}

	public final void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (gameFrame != null) {
			Insets insets = gameFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
	     if (System.currentTimeMillis() - aLong29 >= 250L || Math.abs(saveClickX - x) > 5 || Math.abs(saveClickY - y) > 5) {
	         idleTime = 0;
	         mouseX = x;
	         mouseY = y;
	     }
		if (mouseWheelDown) {
			y = mouseWheelX - e.getX();
			int k = mouseWheelY - e.getY();
			mouseWheelDragged(y, -k);
			mouseWheelX = e.getX();
			mouseWheelY = e.getY();
			return;
		}
		idleTime = 0;
		mouseX = x;
		mouseY = y;
	}

	public final void mouseMoved(MouseEvent mouseevent) {
		int x = mouseevent.getX();
		int y = mouseevent.getY();
		if (gameFrame != null) {
			Insets insets = gameFrame.getInsets();
			x -= insets.left;
			y -= insets.top;
		}
	     if (System.currentTimeMillis() - aLong29 >= 250L || Math.abs(saveClickX - x) > 5 || Math.abs(saveClickY - y) > 5) {
	         idleTime = 0;
	         mouseX = x;
	         mouseY = y;
	     }
		idleTime = 0;
		mouseX = x;
		mouseY = y;
	}

	void mouseWheelDragged(int param1, int param2) {

	}

	public final void keyTyped(KeyEvent keyevent) {
	}

	public final void keyPressed(KeyEvent keyevent) {
		idleTime = 0;
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

	public final void keyReleased(KeyEvent keyevent) {
		idleTime = 0;
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

	public final int readChar(int dummy) {
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

	public boolean isFocused() {
		return awtFocus;
	}

	public final void focusGained(FocusEvent focusevent) {
		awtFocus = true;
		shouldClearScreen = true;
		raiseWelcomeScreen();
	}

	public final void focusLost(FocusEvent focusevent) {
		awtFocus = false;
		for (int i = 0; i < 128; i++) {
			keyArray[i] = 0;
		}

	}

	public final void windowOpened(WindowEvent windowevent) {
	}

	public final void windowClosing(WindowEvent windowevent) {
		destroy();

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

	void startUp() {
	}

	void processGameLoop() {
	}

	void cleanUpForQuit() {
	}

	void processDrawing() {
	}

	void raiseWelcomeScreen() {
	}

	Component getGameComponent() {
		if (gameFrame != null && !isApplet) {
			return gameFrame;
		} else {
			return this;
		}
	}

	public void startRunnable(Runnable runnable, int i) {
		Thread thread = new Thread(runnable);
		thread.start();
		thread.setPriority(i);
	}

	void drawLoadingText(int percentage, String loadingText) {
		while (graphics == null) {
			graphics = (isApplet ? this : gameFrame).getGraphics();
			try {
				getGameComponent().repaint();
			} catch (Exception _ex) {
			}
			try {
				Thread.sleep(1000L);
			} catch (Exception _ex) {
			}
		}
		Font font = new Font("Helvetica", 1, 13);
		FontMetrics fontmetrics = getGameComponent().getFontMetrics(font);
		Font font1 = new Font("Helvetica", 0, 13);
		FontMetrics fontmetrics1 = getGameComponent().getFontMetrics(font1);
		if (shouldClearScreen) {
			graphics.setColor(Color.black);
			graphics.fillRect(0, 0, FrameConfig.MIN_WIDTH, FrameConfig.MIN_HEIGHT);
			shouldClearScreen = false;
		}
		Color color = new Color(140, 17, 17);
		int y = FrameConfig.MIN_HEIGHT / 2 - 18;
		graphics.setColor(color);
		graphics.drawRect(FrameConfig.MIN_WIDTH / 2 - 152, y, 304, 34);
		graphics.fillRect(FrameConfig.MIN_WIDTH / 2 - 150, y + 2, percentage * 3, 30);
		graphics.setColor(Color.black);
		graphics.fillRect((FrameConfig.MIN_WIDTH / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
		graphics.setFont(font);
		graphics.setColor(Color.white);
		graphics.drawString(loadingText, (FrameConfig.MIN_WIDTH - fontmetrics.stringWidth(loadingText)) / 2, y + 22);
		graphics.drawString("", (FrameConfig.MIN_WIDTH - fontmetrics1.stringWidth("")) / 2, y - 8);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		if (cmd != null)
		{
			if (cmd.equalsIgnoreCase("Order weed in BENELUX"))
			{
				Client.instance.launchURL("https://wiet-forum.nl/");
			}
			else if (cmd.equalsIgnoreCase("Origins of 420"))
			{
				Client.instance.launchURL("https://420waldos.com/");
			}
			else if (cmd.equalsIgnoreCase("History of weed"))
			{
				Client.instance.launchURL("https://www.history.com/topics/crime/history-of-marijuana");
			}
			else if (cmd.equalsIgnoreCase("Discord stoner community"))
			{
				Client.instance.launchURL("https://discord.com/invite/c8NNM652qv");
			}
			else if (cmd.equalsIgnoreCase("Search item IDs"))
			{
				Client.instance.launchURL("https://www.itemdb.biz/");
			}
		}
	}
}