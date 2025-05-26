package com.bestbudz.engine;


import com.bestbudz.graphics.buffer.ImageProducer;

import com.bestbudz.ui.Console;
import com.bestbudz.ui.LoginRenderer;
import com.bestbudz.ui.MouseDetection;
import com.bestbudz.ui.RSInterface;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
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

public class ClientEngine implements Runnable, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, FocusListener, WindowListener {


	final int[] keyArray = new int[128];
	private final int[] charQueue = new int[128];
	public static final Console console = new Console();
	public LoginRenderer loginRenderer;
	public static int mouseX;
	public static int mouseY;
	public long aLong29;
	public int clickMode3;
	public static int saveClickX;
	public static int saveClickY;
	public boolean mouseWheelDown;
	public int mouseWheelX;
	public int mouseWheelY;
	protected int screenGliding;
	final int minDelay;
	boolean shouldDebug;
	static ImageProducer fullGameScreen;
	public final MouseDetection mouseDetection;

	boolean awtFocus;
	int idleTime;
	static int clickMode2;
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
		mouseDetection = new MouseDetection((Client)this);

		new Thread(mouseDetection, "MouseDetectionThread").start();

	}

	private void exit() {
		anInt4 = -2;

		cleanUpForQuit();

			try {
				Thread.sleep(500L);
			} catch (Exception exception)
			{
				throw new RuntimeException(exception);
			}
			try {
				System.exit(0);
			} catch (Throwable throwable)
			{
				throw new RuntimeException(throwable);
			}

	}

	final void method4() {
		delayTime = 1000;
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
				if (Client.cameraZoom > (GraphicsConfig.CAMERA_ZOOM - 400)) {
					Client.cameraZoom -= 50;
				}
			} else {
				if (Client.cameraZoom < (GraphicsConfig.CAMERA_ZOOM + 300)) {
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
		int offsetX;
		int offsetY;
		int childID = 0;
		int tabInterfaceID = Client.tabInterfaceIDs[Client.tabID];
		if (tabInterfaceID != -1) {
			RSInterface tab = RSInterface.interfaceCache[tabInterfaceID];
			offsetX = Client.frameWidth - 197;
			offsetY =Client.frameHeight - 37 - 267;
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
			RSInterface rsi = RSInterface.interfaceCache[Client.openInterfaceID];
			if (Client.openInterfaceID == 5292) {
				offsetX = (Client.frameWidth / 2) - 356;
				offsetY = (Client.frameHeight / 2) - 230;
			} else {
				offsetX = x;
				offsetY = y;
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
		if (type == MouseEvent.BUTTON3) {
			clickMode1 = 2;
			clickMode2 = 2;
		} else if (type == MouseEvent.BUTTON1) {
			clickMode1 = 1;
			clickMode2 = 1;
		}
		clickMode3 = clickMode2;
		saveClickX = x;
		saveClickY = y;

		synchronized (mouseDetection.syncObject) {
			int idx = mouseDetection.coordsIndex;
			if (idx < mouseDetection.coordsX.length) {
				mouseDetection.coordsX[idx] = x;
				mouseDetection.coordsY[idx] = y;
				mouseDetection.coordsIndex = idx + 1;
			}
		}


	}

	public final void mouseReleased(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		idleTime = 0;
		clickMode2 = 0;
		mouseWheelDown = false;
		clickMode3 = 0;

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

		System.currentTimeMillis();
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


	void cleanUpForQuit() {
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

	@Override
	public void run()
	{

	}
}