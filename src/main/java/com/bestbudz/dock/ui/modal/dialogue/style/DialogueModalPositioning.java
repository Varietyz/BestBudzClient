package com.bestbudz.dock.ui.modal.dialogue.style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Handles all window positioning and dragging functionality
 * Separated for better organization and reusability
 */
public class DialogueModalPositioning {

	private DialogueModalPositioning() {
		// Utility class - prevent instantiation
	}

	/**
	 * Make the dialog draggable by adding mouse listeners
	 */
	public static void makeDraggable(JDialog dialog, JPanel panel, JLabel titleLabel) {
		final Point[] mouseDownCompCoords = new Point[1];

		MouseAdapter mouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mouseDownCompCoords[0] = e.getPoint();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				Point currCoords = e.getLocationOnScreen();
				dialog.setLocation(currCoords.x - mouseDownCompCoords[0].x,
					currCoords.y - mouseDownCompCoords[0].y);
			}
		};

		panel.addMouseListener(mouseAdapter);
		panel.addMouseMotionListener(mouseAdapter);

		if (titleLabel != null) {
			titleLabel.addMouseListener(mouseAdapter);
			titleLabel.addMouseMotionListener(mouseAdapter);
		}
	}

	/**
	 * Center dialog on screen with multi-monitor support
	 */
	public static void centerOnScreen(JDialog dialog) {
		Window parentWindow = dialog.getOwner();

		if (parentWindow != null && parentWindow.isDisplayable()) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] screens = ge.getScreenDevices();
			GraphicsDevice targetScreen = null;

			Rectangle parentBounds = parentWindow.getBounds();
			Point parentCenter = new Point(
				parentBounds.x + parentBounds.width / 2,
				parentBounds.y + parentBounds.height / 2
			);

			// Find the screen containing the parent window
			for (GraphicsDevice screen : screens) {
				Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
				if (screenBounds.contains(parentCenter)) {
					targetScreen = screen;
					break;
				}
			}

			if (targetScreen != null) {
				Rectangle screenBounds = targetScreen.getDefaultConfiguration().getBounds();
				Dimension dialogSize = dialog.getSize();

				int x = screenBounds.x + (screenBounds.width - dialogSize.width) / 2;
				int y = screenBounds.y + (screenBounds.height - dialogSize.height) / 2;

				dialog.setLocation(x, y);
			} else {
				centerRelativeToParent(dialog, parentWindow);
			}
		} else {
			centerOnScreenFallback(dialog);
		}

		ensureModalOnScreen(dialog);
	}

	/**
	 * Ensure modal stays completely on screen
	 */
	public static void ensureModalOnScreen(JDialog dialog) {
		Rectangle modalBounds = dialog.getBounds();
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle screenBounds = ge.getMaximumWindowBounds();

		boolean boundsChanged = false;

		// Check right edge
		if (modalBounds.x + modalBounds.width > screenBounds.x + screenBounds.width) {
			modalBounds.x = screenBounds.x + screenBounds.width - modalBounds.width;
			boundsChanged = true;
		}

		// Check bottom edge
		if (modalBounds.y + modalBounds.height > screenBounds.y + screenBounds.height) {
			modalBounds.y = screenBounds.y + screenBounds.height - modalBounds.height;
			boundsChanged = true;
		}

		// Check left edge
		if (modalBounds.x < screenBounds.x) {
			modalBounds.x = screenBounds.x;
			boundsChanged = true;
		}

		// Check top edge
		if (modalBounds.y < screenBounds.y) {
			modalBounds.y = screenBounds.y;
			boundsChanged = true;
		}

		if (boundsChanged) {
			dialog.setBounds(modalBounds);
		}
	}

	/**
	 * Center dialog relative to parent window
	 */
	private static void centerRelativeToParent(JDialog dialog, Window parentWindow) {
		Dimension parentSize = parentWindow.getSize();
		Point parentLocation = parentWindow.getLocation();
		Dimension dialogSize = dialog.getSize();

		int x = parentLocation.x + (parentSize.width - dialogSize.width) / 2;
		int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;

		dialog.setLocation(x, y);
	}

	/**
	 * Fallback screen centering when no parent is available
	 */
	private static void centerOnScreenFallback(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = dialog.getSize();

		int x = (screenSize.width - dialogSize.width) / 2;
		int y = (screenSize.height - dialogSize.height) / 2;

		dialog.setLocation(x, y);
	}

	/**
	 * Calculate optimal position relative to a component (e.g., for context menus)
	 */
	public static Point calculateOptimalPosition(Component relativeTo, Dimension dialogSize) {
		if (relativeTo == null) {
			// Fallback to screen center
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			return new Point(
				(screenSize.width - dialogSize.width) / 2,
				(screenSize.height - dialogSize.height) / 2
			);
		}

		Point componentLocation = relativeTo.getLocationOnScreen();
		Dimension componentSize = relativeTo.getSize();

		// Try to position to the right of the component
		int x = componentLocation.x + componentSize.width + 10;
		int y = componentLocation.y;

		// Check if it fits on screen, adjust if necessary
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Rectangle screenBounds = ge.getMaximumWindowBounds();

		// If too far right, position to the left
		if (x + dialogSize.width > screenBounds.x + screenBounds.width) {
			x = componentLocation.x - dialogSize.width - 10;
		}

		// If still doesn't fit, center horizontally
		if (x < screenBounds.x) {
			x = componentLocation.x + (componentSize.width - dialogSize.width) / 2;
		}

		// Ensure vertical position is on screen
		if (y + dialogSize.height > screenBounds.y + screenBounds.height) {
			y = screenBounds.y + screenBounds.height - dialogSize.height;
		}
		if (y < screenBounds.y) {
			y = screenBounds.y;
		}

		return new Point(x, y);
	}
}