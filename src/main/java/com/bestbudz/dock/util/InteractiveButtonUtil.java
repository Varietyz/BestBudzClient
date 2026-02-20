package com.bestbudz.dock.util;

import static com.bestbudz.engine.config.ColorConfig.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InteractiveButtonUtil {

	public static JButton addInteractiveButton(String labelText, int serverButtonId) {
		return createInteractiveButton(labelText, null, serverButtonId);
	}

	public static JButton addSpriteButton(String spritePath, int serverButtonId) {
		ImageIcon icon = SpriteUtil.loadIconScaled(spritePath, 24);
		return createInteractiveButton(null, icon, serverButtonId);
	}

	public static JButton addInteractiveButton(String labelText, String spritePath, int serverButtonId) {
		ImageIcon icon = SpriteUtil.loadIconScaled(spritePath, 24);
		return createInteractiveButton(labelText, icon, serverButtonId);
	}

	private static JButton createInteractiveButton(String labelText, ImageIcon icon, int serverButtonId) {
		JButton button = new JButton();

		if (labelText != null) {
			button.setText(labelText);
		}
		if (icon != null) {
			button.setIcon(icon);
		}

		styleButton(button);

		button.addActionListener(ButtonHandler.createButtonListener(serverButtonId));

		return button;
	}

	private static void styleButton(JButton button) {
		button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		button.setForeground(TEXT_PRIMARY_COLOR);
		button.setBackground(GRAPHITE_COLOR);
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			BorderFactory.createEmptyBorder(4, 8, 4, 8)
		));
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		applyHoverEffect(button);
	}

	private static void applyHoverEffect(JButton button) {
		try {

			RainbowHoverUtil.applyRainbowHover(button);
		} catch (NoClassDefFoundError | Exception e) {

			System.out.println("Rainbow hover not available, using basic hover: " + e.getMessage());
			addBasicHover(button);
		}
	}

	private static void addBasicHover(JButton button) {
		Color originalBg = button.getBackground();
		Color hoverBg = TILE_HOVER_COLOR;
		Color pressedBg = TILE_PRESSED_COLOR;

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {

				if (!RainbowHoverUtil.hasRainbowHover(button)) {
					button.setBackground(hoverBg);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {

				if (!RainbowHoverUtil.hasRainbowHover(button)) {
					button.setBackground(originalBg);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

				button.setBackground(pressedBg);
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (RainbowHoverUtil.hasRainbowHover(button)) {

				} else {

					Point mousePoint = e.getPoint();
					if (button.contains(mousePoint)) {
						button.setBackground(hoverBg);
					} else {
						button.setBackground(originalBg);
					}
				}
			}
		});
	}

	public static JButton addCustomButton(String labelText, int serverButtonId, ButtonStyler customStyling) {
		JButton button = new JButton(labelText);
		customStyling.style(button);
		button.addActionListener(ButtonHandler.createButtonListener(serverButtonId));
		return button;
	}

	public static JButton addCustomButton(String labelText, int serverButtonId, ButtonStyler customStyling, boolean enableRainbowHover) {
		JButton button = new JButton(labelText);
		customStyling.style(button);
		button.addActionListener(ButtonHandler.createButtonListener(serverButtonId));

		if (enableRainbowHover) {
			applyHoverEffect(button);
		}

		return button;
	}

	public interface ButtonStyler {
		void style(JButton button);
	}

	public static void applyRainbowHoverToButton(JButton button) {
		applyHoverEffect(button);
	}

	public static void removeRainbowHoverFromButton(JButton button) {
		try {
			RainbowHoverUtil.removeRainbowHover(button);
		} catch (Exception e) {
			System.out.println("Error removing rainbow hover: " + e.getMessage());
		}
	}
}
