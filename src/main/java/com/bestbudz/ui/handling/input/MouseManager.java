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

	// Middle mouse button state for camera rotation
	private static boolean middleMouseDown = false;
	private static int lastMiddleMouseX = 0;
	private static int lastMiddleMouseY = 0;
	private static final float CAMERA_ROTATION_SENSITIVITY = 5.0f; // Much higher sensitivity

	public static void processMiddleMouseCamera() {
		// Simplified - we're now doing direct control, so no velocity processing needed
		// This method can be empty or removed since we're bypassing the velocity system
	}

	public void mouseWheelMoved(MouseWheelEvent event) {
		int rotation = event.getWheelRotation();
		MouseScrollHandler.handle(event);

		// Chat area scrolling
		if (MouseState.x > 0 && MouseState.x < 512 && MouseState.y > Client.frameHeight - 165 - Client.extendChatArea && MouseState.y < Client.frameHeight - 25) {
			int scrollPos = Client.loadingProgress;
			scrollPos -= rotation * 30;
			if (scrollPos < 0)
				scrollPos = 0;
			if (scrollPos > Client.anInt1211 - 110)
				scrollPos = Client.anInt1211 - 110;
			if (Client.loadingProgress != scrollPos) {
				Client.loadingProgress = scrollPos;
				Client.inputTaken = true;
			}
		}

		// Camera zoom with Ctrl+Wheel
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
		System.out.println("Mouse pressed: button=" + e.getButton() + " at (" + e.getX() + "," + e.getY() + ")");

		MouseState.x = e.getX();
		MouseState.y = e.getY();

		if (e.getButton() == MouseEvent.BUTTON1) {
			System.out.println("Left mouse pressed");
			MouseState.leftDown = true;
			MouseState.leftClicked = true;
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			System.out.println("Right mouse pressed");
			MouseState.rightDown = true;
			MouseState.rightClicked = true;
		}

		// Handle middle mouse button press for camera rotation
		if (e.getButton() == MouseEvent.BUTTON2) {
			System.out.println("MIDDLE MOUSE DETECTED (BUTTON2)!");
			middleMouseDown = true;
			lastMiddleMouseX = e.getX();
			lastMiddleMouseY = e.getY();
		}

		MouseState.pressed = true;
		MouseState.clickEvent = true;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released: button=" + e.getButton());

		MouseState.pressed = false;
		MouseState.released = true;
		MouseState.clickEvent = false;

		if (e.getButton() == MouseEvent.BUTTON1) {
			MouseState.leftClicked = false;
			MouseState.leftDown = false;
		}

		if (e.getButton() == MouseEvent.BUTTON3) {
			MouseState.rightClicked = false;
			MouseState.rightDown = false;
		}

		// Handle middle mouse button release
		if (e.getButton() == MouseEvent.BUTTON2) {
			System.out.println("MIDDLE MOUSE RELEASED!");
			middleMouseDown = false;
		}
	}

	public void mouseEntered(MouseEvent mouseevent) {
	}

	public void mouseExited(MouseEvent mouseevent) {
		MouseState.idleTime = 0;
		MouseState.x = -1;
		MouseState.y = -1;
		// Reset middle mouse state when leaving the component
		middleMouseDown = false;
	}

	public void mouseDragged(MouseEvent e) {
		System.out.println("Mouse dragged to (" + e.getX() + "," + e.getY() + ") - middleDown=" + middleMouseDown);

		int x = e.getX();
		int y = e.getY();

		// Handle middle mouse camera rotation FIRST
		if (middleMouseDown) {
			int deltaX = x - lastMiddleMouseX;
			int deltaY = y - lastMiddleMouseY;

			System.out.println("!!! MIDDLE MOUSE CAMERA DRAG: deltaX=" + deltaX + ", deltaY=" + deltaY);

			// Apply camera rotation based on mouse movement
			applyMouseCameraRotation(deltaX, deltaY);

			lastMiddleMouseX = x;
			lastMiddleMouseY = y;
			return; // Don't update regular mouse state during camera rotation
		}

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
		// Existing wheel drag functionality
	}

	/**
	 * Apply mouse movement to camera velocities (integrates with existing system)
	 */
	private static void applyMouseCameraRotation(int deltaX, int deltaY) {
		// Direct camera control - bypass velocity system for immediate response

		// Horizontal movement controls yaw (left/right rotation)
		if (Math.abs(deltaX) > 0) {
			// Directly modify minimap rotation for immediate yaw control
			int yawChange = deltaX * (int)CAMERA_ROTATION_SENSITIVITY;
			Client.minimapRotation = (Client.minimapRotation + yawChange) & 0x7ff;
			System.out.println("Direct yaw change: " + yawChange + ", new rotation: " + Client.minimapRotation);
		}

		// Vertical movement controls pitch (up/down angle)
		if (Math.abs(deltaY) > 0) {
			// Directly modify camera height for immediate pitch control
			int pitchChange = deltaY * (int)(CAMERA_ROTATION_SENSITIVITY * 0.8f); // Slightly less sensitive for pitch
			Client.minCameraHeight += pitchChange;

			// Clamp to valid ranges
			if (Client.minCameraHeight < 128) Client.minCameraHeight = 128;
			if (Client.minCameraHeight > 383) Client.minCameraHeight = 383;

			System.out.println("Direct pitch change: " + pitchChange + ", new height: " + Client.minCameraHeight);
		}
	}

	/**
	 * Check if middle mouse button is currently being used for camera rotation
	 */
	public static boolean isMiddleMouseCameraActive() {
		return middleMouseDown;
	}

	/**
	 * Debug method to check mouse listener status
	 */
	public static void debugMouseListenerStatus() {
		System.out.println("=== MOUSE LISTENER DEBUG ===");
		System.out.println("MouseManager class loaded: " + MouseManager.class.getName());
		System.out.println("Middle mouse down state: " + middleMouseDown);
		System.out.println("Current mouse position: " + MouseState.x + "," + MouseState.y);
		System.out.println("===========================");
	}
}