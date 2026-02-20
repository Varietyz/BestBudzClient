package com.bestbudz.dock.ui.panel.bank.grid;

import com.bestbudz.dock.util.SpriteUtil;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles all rendering logic for bank item panels
 */
public class BankItemRenderer {

	private final int itemSize;
	private BufferedImage backBuffer;
	private Graphics2D backGraphics;

	public BankItemRenderer(int itemSize) {
		this.itemSize = itemSize;
		initializeBackBuffer();
	}

	private void initializeBackBuffer() {
		if (backBuffer != null && backGraphics != null) {
			backGraphics.dispose();
		}

		// Create back buffer that's WIDER to fill the full panel
		backBuffer = new BufferedImage(itemSize + 4, itemSize, BufferedImage.TYPE_INT_ARGB);
		backGraphics = backBuffer.createGraphics();

		// High quality rendering
		backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		backGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		backGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		backGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	/**
	 * Renders a complete bank item to the back buffer
	 */
	public BufferedImage renderItem(int itemId, int amount, boolean isPlaceholder,
									boolean isInventoryItem,
									ImageIcon itemIcon, String amountText, Color amountColor) {

		if (backGraphics == null) return null;

		// CLEAR the background to TRANSPARENT - NO BACKGROUND IN CACHE!
		backGraphics.setComposite(AlphaComposite.Clear);
		backGraphics.fillRect(0, 0, itemSize + 4, itemSize);

		// Set opacity for item content
		float opacity = 1.0f;
		if (isPlaceholder) {
			opacity = 0.4f;
		} else if (amount == 0) {
			opacity = 0.15f;
		}

		backGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

		// Draw item icon CENTERED - NO BACKGROUND
		drawItemIcon(itemId, itemIcon);

		// Reset opacity for overlays
		backGraphics.setComposite(AlphaComposite.SrcOver);

		if (amount > 0 && !isPlaceholder) {
			if (amountText != null) {
				drawAmountLabel(amountText, amountColor);
			}

			if (isInventoryItem) {
				drawInventoryIndicator();
			}
		}

		if (isPlaceholder) {
			drawPlaceholderIndicator();
		}

		return backBuffer;
	}

	private void drawItemIcon(int itemId, ImageIcon itemIcon) {
		if (itemIcon != null) {
			// CENTER the fucking icon in the WIDER image
			int x = ((itemSize + 4) - itemIcon.getIconWidth()) / 2;
			int y = (itemSize - itemIcon.getIconHeight()) / 2;
			itemIcon.paintIcon(null, backGraphics, x, y);
		} else {
			backGraphics.setColor(Color.WHITE);
			backGraphics.setFont(new Font("Arial", Font.PLAIN, Math.max(8, itemSize / 4)));
			String text = String.valueOf(itemId);
			FontMetrics fm = backGraphics.getFontMetrics();
			int x = ((itemSize + 4) - fm.stringWidth(text)) / 2;
			int y = itemSize / 2 + fm.getAscent() / 2;
			backGraphics.drawString(text, x, y);
		}
	}

	private void drawAmountLabel(String amountText, Color amountColor) {
		int fontSize = Math.max(8, itemSize / 4);
		backGraphics.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
		FontMetrics fm = backGraphics.getFontMetrics();

		int textWidth = fm.stringWidth(amountText);
		int textHeight = fm.getHeight();

		int padding = 2;
		int x = (itemSize + 4) - textWidth - padding; // Use wider width
		int y = itemSize - padding;

		backGraphics.setColor(new Color(0, 0, 0, 200));
		backGraphics.fillRoundRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight, 4, 4);

		backGraphics.setColor(new Color(80, 80, 80));
		backGraphics.drawRoundRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight, 4, 4);

		backGraphics.setColor(amountColor);
		backGraphics.drawString(amountText, x, y - 1);
	}

	private void drawInventoryIndicator() {
		int dotSize = Math.max(4, itemSize / 8);
		backGraphics.setColor(new Color(255, 165, 0, 200));
		backGraphics.fillOval(2, 2, dotSize, dotSize);
		backGraphics.setColor(new Color(255, 200, 100));
		backGraphics.drawOval(2, 2, dotSize, dotSize);
	}

	private void drawPlaceholderIndicator() {
		backGraphics.setColor(new Color(100, 100, 100, 150));
		backGraphics.setFont(new Font("Arial", Font.BOLD, Math.max(8, itemSize / 6)));
		FontMetrics fm = backGraphics.getFontMetrics();
		String text = "P";
		int x = (itemSize + 4) - fm.stringWidth(text) - 2; // Use wider width
		int y = fm.getHeight();
		backGraphics.drawString(text, x, y);
	}

	/**
	 * Loads an icon for the specified item
	 */
	public static ImageIcon loadItemIcon(int itemId, int amount, int iconSize) {
		try {
			int spriteItemId = itemId;

			if (itemId == 995) {
				spriteItemId = getCoinSpriteId(amount);
			}

			String spritePath = "sprites/items/" + spriteItemId + ".png";
			// Load icon without the -4 reduction to get proper size
			ImageIcon icon = SpriteUtil.loadIconScaled(spritePath, iconSize - 8);

			if (icon == null) {
				icon = SpriteUtil.loadIconScaled("sprites/items/default.png", iconSize - 8);
			}

			return icon;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Gets the correct sprite ID for coin amounts (public for external access)
	 */
	public static int getCoinSpriteId(int amount) {
		if (amount >= 10000) {
			return 1004;
		} else if (amount >= 1000) {
			return 1003;
		} else if (amount >= 250) {
			return 1002;
		} else if (amount >= 100) {
			return 1001;
		} else if (amount >= 25) {
			return 1000;
		} else if (amount >= 5) {
			return 999;
		} else if (amount >= 4) {
			return 998;
		} else if (amount >= 3) {
			return 997;
		} else if (amount >= 2) {
			return 996;
		} else {
			return 995;
		}
	}

	/**
	 * Formats an amount for display
	 */
	public static String formatAmount(int amount) {
		if (amount >= 1000000) {
			return (amount / 1000000) + "M";
		} else if (amount >= 1000) {
			return (amount / 1000) + "K";
		}
		return String.valueOf(amount);
	}

	/**
	 * Gets the color for an amount based on value
	 */
	public static Color getAmountColor(int amount) {
		if (amount >= 10000000) {
			return new Color(0, 255, 0);
		} else if (amount >= 1000000) {
			return new Color(150, 255, 150);
		} else if (amount >= 100000) {
			return new Color(255, 255, 0);
		} else if (amount >= 1000) {
			return new Color(255, 255, 150);
		} else {
			return Color.WHITE;
		}
	}

	/**
	 * Cleans up renderer resources
	 */
	public void dispose() {
		if (backGraphics != null) {
			backGraphics.dispose();
			backGraphics = null;
		}
		backBuffer = null;
	}
}