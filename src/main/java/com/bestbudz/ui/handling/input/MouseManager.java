package com.bestbudz.ui.handling.input;

import com.bestbudz.engine.core.Client;
import com.bestbudz.engine.config.EngineConfig;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public class MouseManager implements MouseListener, MouseMotionListener, MouseWheelListener
{
	public int mouseWheelX;
	public int mouseWheelY;
	public static boolean mouseWheelDown;

	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		MouseScrollHandler.handle(event);
		if (MouseState.x > 0 && MouseState.x < 512 && MouseState.y > Client.frameHeight - 165 - Client.extendChatArea && MouseState.y < Client.frameHeight - 25) {
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
				if (Client.cameraZoom > (EngineConfig.CAMERA_ZOOM - 400)) {
					Client.cameraZoom -= 50;
				}
			} else {
				if (Client.cameraZoom < (EngineConfig.CAMERA_ZOOM + 300)) {
					Client.cameraZoom += 50;
				}
			}
		}
	}

	public void mouseClicked(MouseEvent mouseevent) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		MouseState.x = e.getX();
		MouseState.y = e.getY();

		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseState.leftDown = true;
			MouseState.leftClicked = true;
}

		if (e.getButton() == MouseEvent.BUTTON3) {
			MouseState.rightDown = true;
			MouseState.rightClicked = true;
}

		MouseState.pressed = true;
		MouseState.clickEvent = true; // REQUIRED for packet triggering

}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Coordinates might not change on release, but print for tracking

		MouseState.pressed = false;
		MouseState.released = true;
		MouseState.clickEvent = false;

		// Release only affects the clicked flags if that button was released
		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseState.leftClicked = false;
			MouseState.leftDown = false;
	}

		if (e.getButton() == MouseEvent.BUTTON3) {
			MouseState.rightClicked = false;
			MouseState.rightDown = false;
		}

	}


	public void mouseEntered(MouseEvent mouseevent) {
	}

	public void mouseExited(MouseEvent mouseevent) {
		MouseState.idleTime = 0;
		MouseState.x = -1;
		MouseState.y = -1;
	}

	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		if (System.currentTimeMillis() - MouseState.lastMoveTime >= 250L || Math.abs(MouseState.x - x) > 5 || Math.abs(MouseState.y - y) > 5) {
			MouseState.idleTime = 0;
			MouseState.x = x;
			MouseState.y = y;
		}
		if (mouseWheelDown) {
			y = mouseWheelX - e.getX();
			int k = mouseWheelY - e.getY();
			mouseWheelDragged(y, -k);
			mouseWheelX = e.getX();
			mouseWheelY = e.getY();
			return;
		}
		MouseState.idleTime = 0;
		MouseState.x = x;
		MouseState.y = y;
	}

	public void mouseMoved(MouseEvent mouseevent) {
		int x = mouseevent.getX();
		int y = mouseevent.getY();

		System.currentTimeMillis();
		MouseState.idleTime = 0;
		MouseState.x = x;
		MouseState.y = y;
	}

	void mouseWheelDragged(int param1, int param2) {

	}
}
