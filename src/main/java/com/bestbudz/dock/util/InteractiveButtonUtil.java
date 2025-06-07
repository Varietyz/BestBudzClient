package com.bestbudz.dock.util;

import com.bestbudz.engine.core.Client;
import static com.bestbudz.engine.config.ColorConfig.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/* EXAMPLE USAGE
// Text button
JButton myButton = InteractiveButtonUtil.addInteractiveButton("Attack Mode", 94147);

// Sprite button
JButton skillButton = InteractiveButtonUtil.addSpriteButton("sprites/skills/assault.png", 94147);

// Both text and sprite
JButton comboButton = InteractiveButtonUtil.addInteractiveButton("Assault", "sprites/skills/assault.png", 94147);
 */
public class InteractiveButtonUtil {

	/**
	 * Creates an interactive button with text label
	 * @param labelText The text to display on the button
	 * @param serverButtonId The server button ID to send when clicked
	 * @return Configured JButton ready for use
	 */
	public static JButton addInteractiveButton(String labelText, int serverButtonId) {
		return createInteractiveButton(labelText, null, serverButtonId);
	}

	/**
	 * Creates an interactive button with sprite icon
	 * @param spritePath Path to sprite image (e.g., "sprites/example.png")
	 * @param serverButtonId The server button ID to send when clicked
	 * @return Configured JButton ready for use
	 */
	public static JButton addSpriteButton(String spritePath, int serverButtonId) {
		ImageIcon icon = SpriteUtil.loadIconScaled(spritePath, 24);
		return createInteractiveButton(null, icon, serverButtonId);
	}

	/**
	 * Creates an interactive button with both text and sprite
	 * @param labelText The text to display on the button
	 * @param spritePath Path to sprite image
	 * @param serverButtonId The server button ID to send when clicked
	 * @return Configured JButton ready for use
	 */
	public static JButton addInteractiveButton(String labelText, String spritePath, int serverButtonId) {
		ImageIcon icon = SpriteUtil.loadIconScaled(spritePath, 24);
		return createInteractiveButton(labelText, icon, serverButtonId);
	}

	/**
	 * Core button creation method
	 */
	private static JButton createInteractiveButton(String labelText, ImageIcon icon, int serverButtonId) {
		JButton button = new JButton();

		// Set text and/or icon
		if (labelText != null) {
			button.setText(labelText);
		}
		if (icon != null) {
			button.setIcon(icon);
		}

		// Apply standard styling
		styleButton(button);

		// Add click behavior
		button.addActionListener(ButtonHandler.createButtonListener(serverButtonId));

		return button;
	}

	/**
	 * Applies consistent styling to buttons
	 */
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

		// Apply rainbow hover effect with proper error handling
		applyHoverEffect(button);
	}

	/**
	 * Applies hover effects to buttons with proper fallback handling
	 */
	private static void applyHoverEffect(JButton button) {
		try {
			// Try to apply rainbow hover effect
			RainbowHoverUtil.applyRainbowHover(button);
		} catch (NoClassDefFoundError | Exception e) {
			// Fallback to basic hover if RainbowHoverUtil is not available or fails
			System.out.println("Rainbow hover not available, using basic hover: " + e.getMessage());
			addBasicHover(button);
		}
	}

	/**
	 * Adds basic hover effects as fallback
	 */
	private static void addBasicHover(JButton button) {
		Color originalBg = button.getBackground();
		Color hoverBg = TILE_HOVER_COLOR;
		Color pressedBg = TILE_PRESSED_COLOR;

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				// Only apply basic hover if rainbow hover is not active
				if (!RainbowHoverUtil.hasRainbowHover(button)) {
					button.setBackground(hoverBg);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// Only reset if rainbow hover is not active
				if (!RainbowHoverUtil.hasRainbowHover(button)) {
					button.setBackground(originalBg);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// Apply pressed color even with rainbow hover (temporary override)
				button.setBackground(pressedBg);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// Return to appropriate hover state
				if (RainbowHoverUtil.hasRainbowHover(button)) {
					// Let rainbow hover handle the release state
					// The rainbow hover will restore its color
				} else {
					// Check if mouse is still over the button
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

	/**
	 * Creates a button with custom styling (for special cases)
	 * @param labelText Button text
	 * @param serverButtonId Server button ID
	 * @param customStyling Custom styling function
	 * @return Configured button
	 */
	public static JButton addCustomButton(String labelText, int serverButtonId, ButtonStyler customStyling) {
		JButton button = new JButton(labelText);
		customStyling.style(button);
		button.addActionListener(ButtonHandler.createButtonListener(serverButtonId));
		return button;
	}

	/**
	 * Creates a button with custom styling and optional rainbow hover
	 * @param labelText Button text
	 * @param serverButtonId Server button ID
	 * @param customStyling Custom styling function
	 * @param enableRainbowHover Whether to apply rainbow hover effect
	 * @return Configured button
	 */
	public static JButton addCustomButton(String labelText, int serverButtonId, ButtonStyler customStyling, boolean enableRainbowHover) {
		JButton button = new JButton(labelText);
		customStyling.style(button);
		button.addActionListener(ButtonHandler.createButtonListener(serverButtonId));

		if (enableRainbowHover) {
			applyHoverEffect(button);
		}

		return button;
	}

	/**
	 * Interface for custom button styling
	 */
	public interface ButtonStyler {
		void style(JButton button);
	}

	/**
	 * Utility method to manually apply rainbow hover to an existing button
	 * (useful for buttons created outside this utility)
	 */
	public static void applyRainbowHoverToButton(JButton button) {
		applyHoverEffect(button);
	}

	/**
	 * Utility method to remove rainbow hover from a button
	 */
	public static void removeRainbowHoverFromButton(JButton button) {
		try {
			RainbowHoverUtil.removeRainbowHover(button);
		} catch (Exception e) {
			System.out.println("Error removing rainbow hover: " + e.getMessage());
		}
	}
}