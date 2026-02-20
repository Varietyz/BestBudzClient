package com.bestbudz.dock.ui.panel.bubblebudz.ui.components;

import com.bestbudz.dock.ui.panel.bubblebudz.ui.BubbleBudzStyle;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Button component for opening the game mode selection overlay
 */
public class GameModeButton extends MouseAdapter {
	private Rectangle bounds;
	private boolean isHovered = false;
	private boolean isPressed = false;
	private GameModeButtonListener listener;

	public interface GameModeButtonListener {
		void onButtonClicked();
	}

	public GameModeButton(GameModeButtonListener listener) {
		this.listener = listener;
	}

	public void setBounds(int x, int y, int width, int height) {
		this.bounds = new Rectangle(x, y, width, height);
	}

	public void render(Graphics2D g2d, String currentMode) {
		if (bounds == null) return;

		// Button background with glow effect when hovered
		Color bgColor = isPressed ? new Color(50, 50, 80, 200) :
			isHovered ? new Color(30, 30, 60, 180) :
				new Color(20, 20, 40, 160);

		g2d.setColor(bgColor);
		g2d.fill(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 4, 4)); // Changed from 8,8

		// Border
		Color borderColor = isHovered ? BubbleBudzStyle.TEXT_NEON_CYAN : BubbleBudzStyle.TEXT_SECONDARY;
		g2d.setColor(borderColor);
		g2d.setStroke(new BasicStroke(1f)); // Changed from 1.5f
		g2d.draw(new RoundRectangle2D.Float(bounds.x, bounds.y, bounds.width, bounds.height, 4, 4)); // Changed from 8,8

		// Button text - compact format
		g2d.setFont(new Font("SansSerif", Font.PLAIN, 9)); // Changed from Font.BOLD, 11
		FontMetrics fm = g2d.getFontMetrics();

		String text = currentMode; // Changed from "Mode: " + currentMode
		int textX = bounds.x + (bounds.width - fm.stringWidth(text) - 8) / 2; // Leave space for arrow
		int textY = bounds.y + (bounds.height + fm.getAscent()) / 2 - 1; // Changed from -2

		g2d.setColor(isHovered ? BubbleBudzStyle.TEXT_NEON_CYAN : BubbleBudzStyle.TEXT_PRIMARY);
		g2d.drawString(text, textX, textY);

		// Small arrow indicator
		int arrowX = bounds.x + bounds.width - 8; // Changed from -12
		int arrowY = bounds.y + bounds.height / 2;
		drawDownArrow(g2d, arrowX, arrowY, 3); // Changed from 4
	}

	private void drawDownArrow(Graphics2D g2d, int x, int y, int size) {
		int[] xPoints = {x - size, x + size, x};
		int[] yPoints = {y - size/2, y - size/2, y + size/2};
		g2d.fillPolygon(xPoints, yPoints, 3);
	}

	public boolean contains(int x, int y) {
		return bounds != null && bounds.contains(x, y);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (contains(e.getX(), e.getY())) {
			isPressed = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isPressed && contains(e.getX(), e.getY())) {
			if (listener != null) {
				listener.onButtonClicked();
			}
		}
		isPressed = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		boolean wasHovered = isHovered;
		isHovered = contains(e.getX(), e.getY());

		// Could trigger repaint here if needed
		if (wasHovered != isHovered) {
			// Trigger repaint through listener if needed
		}
	}

	public void updateHover(int x, int y) {
		isHovered = contains(x, y);
	}
}