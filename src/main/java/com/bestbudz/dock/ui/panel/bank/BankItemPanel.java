package com.bestbudz.dock.ui.panel.bank;

import com.bestbudz.dock.definitions.ItemBonusManager;
import com.bestbudz.dock.util.SpriteUtil;
import com.bestbudz.engine.core.Client;
import com.bestbudz.network.packets.PacketSender;
import com.bestbudz.ui.RSInterface;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

/**
 * Completely flicker-free bank item panel with manual paint control
 */
public class BankItemPanel extends JPanel {

	private final int itemId;
	private int amount;
	private final int gridIndex;
	private final boolean isInventoryItem;
	private boolean isAlsoInBank;
	private final int itemSize;
	private boolean isPlaceholder = false;
	private ImageIcon itemIcon;
	private Color currentBackground;
	private Color originalBackground;
	private BankPanel parentBankPanel;
	private boolean isHovered = false;

	// Cached item definition
	private final ItemDefCache.CachedItemDef itemDef;

	// Cached display data
	private String cachedTooltip;
	private String cachedAmountText;
	private Color cachedAmountColor;

	// Complete double buffering system
	private BufferedImage backBuffer;
	private Graphics2D backGraphics;
	private boolean needsRedraw = true;
	private boolean isUpdating = false;

	public BankItemPanel(int itemId, int amount, int gridIndex, boolean isInventoryItem, boolean isAlsoInBank, int size) {
		this.itemId = itemId;
		this.amount = amount;
		this.gridIndex = gridIndex;
		this.isInventoryItem = isInventoryItem;
		this.isAlsoInBank = isAlsoInBank;
		this.itemSize = size;

		// Cache item definition once
		this.itemDef = ItemDefCache.get(itemId);

		Dimension fixedSize = new Dimension(size, size);
		setPreferredSize(fixedSize);
		setMinimumSize(fixedSize);
		setMaximumSize(fixedSize);

		// CRITICAL: Completely disable Swing's automatic painting
		setOpaque(false);  // Don't let Swing paint background
		setDoubleBuffered(false);  // We handle our own buffering

		initializeBackBuffer();
		setupBackground();
		setupClickHandlers();
		loadSprite();
		updateCachedStrings();
		renderBackBuffer();
	}

	public BankItemPanel(int itemId, int amount, int gridIndex, boolean isInventoryItem, boolean isAlsoInBank) {
		this(itemId, amount, gridIndex, isInventoryItem, isAlsoInBank, 32);
	}

	private void initializeBackBuffer() {
		if (backBuffer != null && backGraphics != null) {
			backGraphics.dispose();
		}

		backBuffer = new BufferedImage(itemSize, itemSize, BufferedImage.TYPE_INT_ARGB);
		backGraphics = backBuffer.createGraphics();

		// High quality rendering
		backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		backGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		backGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		backGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	}

	public void setParentBankPanel(BankPanel parentBankPanel) {
		this.parentBankPanel = parentBankPanel;
	}

	public void setAsPlaceholder(boolean isPlaceholder) {
		if (this.isPlaceholder != isPlaceholder) {
			this.isPlaceholder = isPlaceholder;
			setupBackground();
			updateCachedStrings();
			scheduleRedraw();
		}
	}

	private void scheduleRedraw() {
		if (!isUpdating) {
			needsRedraw = true;
			// Use invokeLater to batch redraws and prevent multiple repaints
			SwingUtilities.invokeLater(() -> {
				if (needsRedraw) {
					renderBackBuffer();
					repaintNow();
				}
			});
		}
	}

	private void repaintNow() {
		if (isDisplayable() && isVisible()) {
			// Force immediate repaint without going through Swing's queue
			Graphics g = getGraphics();
			if (g != null) {
				try {
					paint(g);
				} finally {
					g.dispose();
				}
			}
		}
	}

	private void updateCachedStrings() {
		updateTooltip();
		updateAmountText();
	}

	// Then update the updateTooltip() method in BankItemPanel:
	private void updateTooltip() {
		String newTooltip;

		if (isPlaceholder) {
			newTooltip = "<html><b>Placeholder Item</b><br/>Item ID: " + itemId + "<br/>This item is only in inventory</html>";
		} else {
			StringBuilder tooltip = new StringBuilder("<html>");
			tooltip.append("<b>").append(itemDef.name).append("</b><br/>");

			if (amount > 1) {
				tooltip.append("Amount: ").append(formatAmount(amount)).append("<br/>");
			}

			tooltip.append("Item ID: ").append(itemId).append("<br/>");

			// Add equipment bonuses section using ItemBonusManager
			addEquipmentBonuses(tooltip);

			if (itemDef.value > 1) {
				if (itemId == 995) {
					tooltip.append("Value: ").append(formatAmount(amount)).append(" gp");
				} else {
					tooltip.append("Unit Value: ").append(formatAmount(itemDef.value)).append(" gp<br/>");
					if (amount > 1) {
						long totalValue = (long) itemDef.value * amount;
						tooltip.append("Total Value: ").append(formatAmount((int) Math.min(totalValue, Integer.MAX_VALUE))).append(" gp");
					}
				}
				tooltip.append("<br/>");
			}

			if (isInventoryItem) {
				tooltip.append("<font color='orange'>In Inventory</font>");
				if (isAlsoInBank) {
					tooltip.append(" & <font color='yellow'>In Bank</font>");
				}
			} else {
				tooltip.append("<font color='yellow'>In Bank</font>");
			}

			if (itemDef.stackable) {
				tooltip.append("<br/><font color='gray'>Stackable</font>");
			}

			tooltip.append("</html>");
			newTooltip = tooltip.toString();
		}

		if (!newTooltip.equals(cachedTooltip)) {
			cachedTooltip = newTooltip;
			setToolTipText(cachedTooltip);
		}
	}

// Add these new methods to BankItemPanel:
	/**
	 * Adds equipment bonuses to the tooltip using ItemBonusManager
	 */
	private void addEquipmentBonuses(StringBuilder tooltip) {
		try {
			// Get item bonuses from the ItemBonusManager
			short[] bonuses = ItemBonusManager.getBonuses(itemId);

			if (bonuses == null || bonuses.length == 0) {
				return; // No bonuses to display
			}

			// Check if item has any non-zero bonuses
			boolean hasAnyBonuses = ItemBonusManager.hasEquipmentBonuses(itemId);
			if (!hasAnyBonuses) {
				return; // All bonuses are zero
			}

			tooltip.append("<br/><b>Bonuses:</b><br/>");

			// Get bonus names for proper labeling
			String[] bonusNames = ItemBonusManager.getBonusNames();

			// Add assault bonuses (indices 0-4)
			boolean hasAssaultBonuses = addBonusSection(tooltip, bonuses, bonusNames, 0, 5, "Assault:");

			// Add aegis bonuses (indices 5-9)
			boolean hasAegisBonuses = addBonusSection(tooltip, bonuses, bonusNames, 5, 10, "Aegis:");

			// Add other bonuses (indices 10+)
			boolean hasOtherBonuses = addBonusSection(tooltip, bonuses, bonusNames, 10, bonuses.length, "Extra:");

			// Add a line break if we added any bonus sections
			if (hasAssaultBonuses || hasAegisBonuses || hasOtherBonuses) {
				tooltip.append("<br/>");
			}

		} catch (Exception e) {
			System.err.println("Error adding equipment bonuses to bank tooltip: " + e.getMessage());
		}
	}

	/**
	 * Adds a section of bonuses to the tooltip
	 * @return true if any bonuses were added to this section
	 */
	private boolean addBonusSection(StringBuilder tooltip, short[] bonuses, String[] bonusNames,
									int startIndex, int endIndex, String sectionName) {
		boolean addedAny = false;
		StringBuilder sectionText = new StringBuilder();

		for (int i = startIndex; i < endIndex && i < bonuses.length; i++) {
			if (bonuses[i] != 0) {
				if (!addedAny) {
					sectionText.append("<i>").append(sectionName).append("</i><br/>");
					addedAny = true;
				}

				String bonusName = (bonusNames != null && i < bonusNames.length)
					? bonusNames[i]
					: "Bonus " + i;

				String bonusText = formatBonus(bonuses[i]);
				sectionText.append("&nbsp;&nbsp;").append(bonusName).append(": ").append(bonusText).append("<br/>");
			}
		}

		if (addedAny) {
			tooltip.append(sectionText);
		}

		return addedAny;
	}

	/**
	 * Formats a bonus value with proper color and sign
	 */
	private String formatBonus(short bonus) {
		if (bonus > 0) {
			return "<font color='#00ff00'>+" + bonus + "</font>";
		} else if (bonus < 0) {
			return "<font color='#ff6666'>" + bonus + "</font>";
		} else {
			return "0";
		}
	}

	private void updateAmountText() {
		String newAmountText = null;
		Color newAmountColor = null;

		if (amount > 1) {
			newAmountText = formatAmount(amount);

			if (amount >= 10000000) {
				newAmountColor = new Color(0, 255, 0);
			} else if (amount >= 1000000) {
				newAmountColor = new Color(150, 255, 150);
			} else if (amount >= 100000) {
				newAmountColor = new Color(255, 255, 0);
			} else if (amount >= 1000) {
				newAmountColor = new Color(255, 255, 150);
			} else {
				newAmountColor = Color.WHITE;
			}
		}

		boolean changed = false;
		if ((cachedAmountText == null && newAmountText != null) ||
			(cachedAmountText != null && !cachedAmountText.equals(newAmountText))) {
			changed = true;
		}
		if ((cachedAmountColor == null && newAmountColor != null) ||
			(cachedAmountColor != null && !cachedAmountColor.equals(newAmountColor))) {
			changed = true;
		}

		if (changed) {
			cachedAmountText = newAmountText;
			cachedAmountColor = newAmountColor;
			scheduleRedraw();
		}
	}

	public void updateAmount(int newAmount) {
		if (this.amount == newAmount) {
			return;
		}

		isUpdating = true;
		try {
			int oldAmount = this.amount;
			this.amount = newAmount;
			updateCachedStrings();

			// Reload sprite for coins when needed
			if (itemId == 995) {
				int oldSpriteId = getCoinSpriteId(oldAmount);
				int newSpriteId = getCoinSpriteId(newAmount);
				if (oldSpriteId != newSpriteId) {
					loadSprite();
				}
			}
		} finally {
			isUpdating = false;
		}

		scheduleRedraw();
	}

	public void updateInventoryColors() {
		if (isInventoryItem) {
			setupBackground();
			scheduleRedraw();
		}
	}

	public void updateAlsoInBank(boolean alsoInBank) {
		if (this.isAlsoInBank != alsoInBank) {
			this.isAlsoInBank = alsoInBank;
			setupBackground();
			updateCachedStrings();
			scheduleRedraw();
		}
	}

	// Getters
	public int getCurrentAmount() { return this.amount; }
	public int getItemId() { return this.itemId; }
	public boolean isPlaceholder() { return this.isPlaceholder; }
	public boolean isAlsoInBank() { return this.isAlsoInBank; }
	public boolean isInventoryItem() { return this.isInventoryItem; }
	public ItemDefCache.CachedItemDef getItemDef() { return this.itemDef; }

	public void refreshFromGameData() {
		try {
			int newAmount = 0;

			if (isInventoryItem) {
				RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
				if (inventoryInterface != null && inventoryInterface.inv != null && inventoryInterface.invStackSizes != null) {
					int maxIndex = Math.min(inventoryInterface.inv.length, inventoryInterface.invStackSizes.length);

					for (int i = 0; i < maxIndex; i++) {
						int checkItemId = inventoryInterface.inv[i] - 1;
						int checkAmount = inventoryInterface.invStackSizes[i];

						if (checkItemId == this.itemId && checkAmount > 0) {
							newAmount += checkAmount;
						}
					}
				}
			} else if (!isPlaceholder) {
				RSInterface bankInterface = RSInterface.interfaceCache[5382];
				if (bankInterface != null && bankInterface.inv != null && bankInterface.invStackSizes != null) {
					int maxIndex = Math.min(bankInterface.inv.length, bankInterface.invStackSizes.length);

					for (int i = 0; i < maxIndex; i++) {
						int checkItemId = bankInterface.inv[i] - 1;
						int checkAmount = bankInterface.invStackSizes[i];

						if (checkItemId == this.itemId && checkAmount > 0) {
							newAmount += checkAmount;
						}
					}
				}
			}

			updateAmount(newAmount);
		} catch (Exception e) {
			System.err.println("Error refreshing item data: " + e.getMessage());
		}
	}

	private void setupBackground() {
		Color bgColor;

		if (isPlaceholder) {
			bgColor = new Color(25, 25, 25);
		} else if (isInventoryItem) {
			int inventoryCount = getActiveInventoryCount();

			if (inventoryCount >= 28) {
				bgColor = new Color(80, 30, 30);
			} else if (inventoryCount >= 26) {
				bgColor = new Color(80, 70, 30);
			} else {
				bgColor = new Color(60, 45, 30);
			}
		} else {
			if (isAlsoInBank) {
				bgColor = new Color(50, 50, 60);
			} else {
				bgColor = new Color(45, 45, 45);
			}
		}

		originalBackground = bgColor;
		currentBackground = isHovered ? bgColor.brighter() : bgColor;

		// Set border but don't use Swing's background painting
		Color borderColor = new Color(
			Math.min(255, bgColor.getRed() + 30),
			Math.min(255, bgColor.getGreen() + 30),
			Math.min(255, bgColor.getBlue() + 30)
		);
		setBorder(BorderFactory.createLineBorder(borderColor, 1));
	}

	private int getActiveInventoryCount() {
		try {
			RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
			if (inventoryInterface == null || inventoryInterface.inv == null || inventoryInterface.invStackSizes == null) {
				return 0;
			}

			int count = 0;
			int maxIndex = Math.min(inventoryInterface.inv.length, inventoryInterface.invStackSizes.length);

			for (int i = 0; i < maxIndex; i++) {
				int itemId = inventoryInterface.inv[i] - 1;
				int amount = inventoryInterface.invStackSizes[i];

				if (itemId >= 0 && amount > 0) {
					count++;
				}
			}

			return count;
		} catch (Exception e) {
			return 0;
		}
	}

	public void resetBackground() {
		if (isHovered) {
			isHovered = false;
			currentBackground = originalBackground;
			scheduleRedraw();
		}
	}

	private void setupClickHandlers() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!Client.loggedIn || isPlaceholder) return;

				try {
					if (SwingUtilities.isLeftMouseButton(e)) {
						handleLeftClick();
					} else if (SwingUtilities.isRightMouseButton(e)) {
						showContextMenu(e.getX(), e.getY());
					}
				} catch (Exception ex) {
					System.err.println("Error handling item click: " + ex.getMessage());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (isParentPanelActive() && !isPlaceholder && !isHovered) {
					isHovered = true;
					currentBackground = originalBackground.brighter();
					scheduleRedraw();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (isHovered) {
					isHovered = false;
					currentBackground = originalBackground;
					scheduleRedraw();
				}
			}
		});
	}

	private boolean isParentPanelActive() {
		return parentBankPanel != null && parentBankPanel.isVisible() && parentBankPanel.isDisplayable();
	}

	private void handleLeftClick() {
		if (parentBankPanel == null) return;

		int requestedAmount = parentBankPanel.getLeftClickAmount();

		if (isInventoryItem && amount > 0) {
			depositItem(requestedAmount);
		} else if (!isPlaceholder && amount > 0) {
			withdrawItem(requestedAmount);
		}
	}

	private void showContextMenu(int x, int y) {
		JPopupMenu popup = new JPopupMenu();
		popup.setBackground(new Color(50, 50, 50));
		popup.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));

		if (isInventoryItem && amount > 0) {
			addMenuItem(popup, "Deposit 1", () -> depositItem(1));
			addMenuItem(popup, "Deposit 5", () -> depositItem(5));
			addMenuItem(popup, "Deposit 10", () -> depositItem(10));
			addMenuItem(popup, "Deposit 50", () -> depositItem(50));
			addMenuItem(popup, "Deposit All", () -> depositItem(2147000000));
		} else if (!isPlaceholder && amount > 0) {
			addMenuItem(popup, "Withdraw 1", () -> withdrawItem(1));
			addMenuItem(popup, "Withdraw 5", () -> withdrawItem(5));
			addMenuItem(popup, "Withdraw 10", () -> withdrawItem(10));
			addMenuItem(popup, "Withdraw 50", () -> withdrawItem(50));
			addMenuItem(popup, "Withdraw All", () -> withdrawItem(2147000000));
		}

		if (popup.getComponentCount() > 0) {
			popup.show(this, x, y);
		}
	}

	private void addMenuItem(JPopupMenu popup, String text, Runnable action) {
		JMenuItem item = new JMenuItem(text);
		item.setBackground(new Color(50, 50, 50));
		item.setForeground(Color.WHITE);
		item.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		item.addActionListener(e -> action.run());
		popup.add(item);
	}

	private void depositItem(int amount) {
		try {
			PacketSender.sendStringToServer(99998, itemId + "," + amount);
			com.bestbudz.dock.util.ButtonHandler.sendClick(115301);
		} catch (Exception e) {
			System.err.println("Error depositing item: " + e.getMessage());
		}
	}

	private void withdrawItem(int amount) {
		try {
			PacketSender.sendStringToServer(99999, itemId + "," + amount);
			com.bestbudz.dock.util.ButtonHandler.sendClick(115300);
		} catch (Exception e) {
			System.err.println("Error withdrawing item: " + e.getMessage());
		}
	}

	// COMPLETELY override all paint methods to prevent Swing interference
	@Override
	public void update(Graphics g) {
		paint(g);  // Skip Swing's clear
	}

	@Override
	public void paintComponent(Graphics g) {
		// Don't call super - we handle everything
	}

	@Override
	public void paint(Graphics g) {
		if (backBuffer != null) {
			g.drawImage(backBuffer, 0, 0, null);
		}
	}

	@Override
	public void paintChildren(Graphics g) {
		// No children to paint
	}

	// Render everything to back buffer
	private void renderBackBuffer() {
		if (backGraphics == null) return;

		needsRedraw = false;

		// Clear with current background
		backGraphics.setComposite(AlphaComposite.Src);
		backGraphics.setColor(currentBackground);
		backGraphics.fillRect(0, 0, itemSize, itemSize);

		// Draw border manually since we disabled Swing's border painting
		Color borderColor = new Color(
			Math.min(255, currentBackground.getRed() + 30),
			Math.min(255, currentBackground.getGreen() + 30),
			Math.min(255, currentBackground.getBlue() + 30)
		);
		backGraphics.setColor(borderColor);
		backGraphics.drawRect(0, 0, itemSize - 1, itemSize - 1);

		float opacity = 1.0f;
		if (isPlaceholder) {
			opacity = 0.4f;
		} else if (amount == 0) {
			opacity = 0.15f;
		}

		backGraphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

		// Draw item icon
		if (itemIcon != null) {
			int x = (itemSize - itemIcon.getIconWidth()) / 2;
			int y = (itemSize - itemIcon.getIconHeight()) / 2;
			itemIcon.paintIcon(null, backGraphics, x, y);
		} else {
			backGraphics.setColor(Color.WHITE);
			backGraphics.setFont(new Font("Arial", Font.PLAIN, Math.max(8, itemSize / 4)));
			String text = String.valueOf(itemId);
			FontMetrics fm = backGraphics.getFontMetrics();
			int x = (itemSize - fm.stringWidth(text)) / 2;
			int y = itemSize / 2 + fm.getAscent() / 2;
			backGraphics.drawString(text, x, y);
		}

		// Reset opacity for overlays
		backGraphics.setComposite(AlphaComposite.SrcOver);

		if (amount > 0 && !isPlaceholder) {
			if (cachedAmountText != null) {
				drawAmountLabel(backGraphics);
			}

			if (isInventoryItem) {
				drawInventoryIndicator(backGraphics);
			}
		}

		if (isPlaceholder) {
			drawPlaceholderIndicator(backGraphics);
		}
	}

	private void drawPlaceholderIndicator(Graphics2D g2d) {
		g2d.setColor(new Color(100, 100, 100, 150));
		g2d.setFont(new Font("Arial", Font.BOLD, Math.max(8, itemSize / 6)));
		FontMetrics fm = g2d.getFontMetrics();
		String text = "P";
		int x = itemSize - fm.stringWidth(text) - 2;
		int y = fm.getHeight();
		g2d.drawString(text, x, y);
	}

	private void drawInventoryIndicator(Graphics2D g2d) {
		int dotSize = Math.max(4, itemSize / 8);
		g2d.setColor(new Color(255, 165, 0, 200));
		g2d.fillOval(2, 2, dotSize, dotSize);
		g2d.setColor(new Color(255, 200, 100));
		g2d.drawOval(2, 2, dotSize, dotSize);
	}

	private void drawAmountLabel(Graphics2D g2d) {
		int fontSize = Math.max(8, itemSize / 4);
		g2d.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
		FontMetrics fm = g2d.getFontMetrics();

		int textWidth = fm.stringWidth(cachedAmountText);
		int textHeight = fm.getHeight();

		int padding = 2;
		int x = itemSize - textWidth - padding;
		int y = itemSize - padding;

		g2d.setColor(new Color(0, 0, 0, 200));
		g2d.fillRoundRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight, 4, 4);

		g2d.setColor(new Color(80, 80, 80));
		g2d.drawRoundRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight, 4, 4);

		g2d.setColor(cachedAmountColor);
		g2d.drawString(cachedAmountText, x, y - 1);
	}

	private String formatAmount(int amount) {
		if (amount >= 1000000) {
			return (amount / 1000000) + "M";
		} else if (amount >= 1000) {
			return (amount / 1000) + "K";
		}
		return String.valueOf(amount);
	}

	private void loadSprite() {
		try {
			int spriteItemId = itemId;

			if (itemId == 995) {
				spriteItemId = getCoinSpriteId(amount);
			}

			String spritePath = "sprites/items/" + spriteItemId + ".png";
			itemIcon = SpriteUtil.loadIconScaled(spritePath, itemSize - 4);

			if (itemIcon == null) {
				itemIcon = SpriteUtil.loadIconScaled("sprites/items/default.png", itemSize - 4);
			}
		} catch (Exception e) {
			itemIcon = null;
		}
		scheduleRedraw();
	}

	private int getCoinSpriteId(int amount) {
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

	// Clean up resources
	@Override
	public void removeNotify() {
		if (backGraphics != null) {
			backGraphics.dispose();
			backGraphics = null;
		}
		backBuffer = null;
		super.removeNotify();
	}
}