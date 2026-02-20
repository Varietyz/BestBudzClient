package com.bestbudz.dock.ui.panel.bank.grid;

import com.bestbudz.dock.ui.panel.bank.util.BankTooltipBuilder;
import com.bestbudz.dock.ui.panel.bank.util.ItemDefCache;
import com.bestbudz.engine.core.Client;
import com.bestbudz.network.packets.PacketSender;
import com.bestbudz.ui.RSInterface;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class BankItemPanel extends JPanel {

	private final int itemId;
	private int amount;
	private final int gridIndex;
	private final boolean isInventoryItem;
	private boolean isAlsoInBank;
	private final int itemSize;
	private boolean isPlaceholder = false;
	private ImageIcon itemIcon;
	public Color currentBackground;
	public Color originalBackground;
	private Object parentBankPanel;
	public boolean isHovered = false;

	private final ItemDefCache.CachedItemDef itemDef;
	private String cachedTooltip;
	private String cachedAmountText;
	private Color cachedAmountColor;

	private final BankItemRenderer renderer;
	private BufferedImage cachedImage;

	public BankItemPanel(int itemId, int amount, int gridIndex, boolean isInventoryItem, boolean isAlsoInBank, int size) {
		this.itemId = itemId;
		this.amount = amount;
		this.gridIndex = gridIndex;
		this.isInventoryItem = isInventoryItem;
		this.isAlsoInBank = isAlsoInBank;
		this.itemSize = size;

		this.itemDef = ItemDefCache.get(itemId);
		this.renderer = new BankItemRenderer(size);

		Dimension fixedSize = new Dimension(size, size);
		setPreferredSize(fixedSize);
		setMinimumSize(fixedSize);
		setMaximumSize(fixedSize);

		setOpaque(true);
		setDoubleBuffered(false);

		setupBackground();
		setupClickHandlers();
		loadSprite();
		updateCachedStrings();
		renderToCache();
	}

	public BankItemPanel(int itemId, int amount, int gridIndex, boolean isInventoryItem, boolean isAlsoInBank) {
		this(itemId, amount, gridIndex, isInventoryItem, isAlsoInBank, 32);
	}

	public void setParentBankPanel(Object parentBankPanel) {
		this.parentBankPanel = parentBankPanel;
	}

	public void setAsPlaceholder(boolean isPlaceholder) {
		if (this.isPlaceholder != isPlaceholder) {
			this.isPlaceholder = isPlaceholder;
			setupBackground();
			updateCachedStrings();
			renderToCache();
			repaint();
		}
	}

	public void updateAmount(int newAmount) {
		if (this.amount == newAmount) {
			return;
		}

		int oldAmount = this.amount;
		this.amount = newAmount;
		updateCachedStrings();

		if (itemId == 995) {
			int oldSpriteId = BankItemRenderer.getCoinSpriteId(oldAmount);
			int newSpriteId = BankItemRenderer.getCoinSpriteId(newAmount);
			if (oldSpriteId != newSpriteId) {
				loadSprite();
			}
		}

		renderToCache();
		repaint();
	}

	public void updateInventoryColors() {
		if (isInventoryItem) {
			setupBackground();
			renderToCache();
			repaint();
		}
	}

	public void updateAlsoInBank(boolean alsoInBank) {
		if (this.isAlsoInBank != alsoInBank) {
			this.isAlsoInBank = alsoInBank;
			setupBackground();
			updateCachedStrings();
			renderToCache();
			repaint();
		}
	}

	public void refreshFromGameData() {
		try {
			int newAmount = 0;

			if (isInventoryItem) {
				newAmount = getInventoryAmount();
			} else if (!isPlaceholder) {
				newAmount = getBankAmount();
			}

			updateAmount(newAmount);
		} catch (Exception e) {
			System.err.println("Error refreshing item data: " + e.getMessage());
		}
	}

	public void resetBackground() {

		isHovered = false;
		currentBackground = originalBackground;
	}

	public void forceRedraw() {
		renderToCache();
		repaint();
	}

	public int getCurrentAmount() { return this.amount; }
	public int getItemId() { return this.itemId; }
	public boolean isPlaceholder() { return this.isPlaceholder; }
	public boolean isAlsoInBank() { return this.isAlsoInBank; }
	public boolean isInventoryItem() { return this.isInventoryItem; }
	public ItemDefCache.CachedItemDef getItemDef() { return this.itemDef; }

	private void renderToCache() {

		cachedImage = renderer.renderItem(itemId, amount, isPlaceholder, isInventoryItem,
			itemIcon, cachedAmountText, cachedAmountColor);
	}

	private void updateCachedStrings() {
		updateTooltip();
		updateAmountText();
	}

	private void updateTooltip() {
		String newTooltip = BankTooltipBuilder.buildTooltip(itemId, amount, isPlaceholder, isInventoryItem, isAlsoInBank);
		if (!newTooltip.equals(cachedTooltip)) {
			cachedTooltip = newTooltip;
			setToolTipText(cachedTooltip);
		}
	}

	private void updateAmountText() {
		String newAmountText = null;
		Color newAmountColor = null;

		if (amount > 1) {
			newAmountText = BankItemRenderer.formatAmount(amount);
			newAmountColor = BankItemRenderer.getAmountColor(amount);
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

		Color borderColor = new Color(
			Math.min(255, bgColor.getRed() + 30),
			Math.min(255, bgColor.getGreen() + 30),
			Math.min(255, bgColor.getBlue() + 30)
		);
		setBorder(BorderFactory.createLineBorder(borderColor, 1));
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
				if (!isPlaceholder && !isHovered) {
					isHovered = true;

					currentBackground = new Color(
						Math.min(255, originalBackground.getRed() + 60),
						Math.min(255, originalBackground.getGreen() + 40),
						Math.min(255, originalBackground.getBlue() + 20)
					);

					repaint();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (isHovered) {
					isHovered = false;

					currentBackground = originalBackground;

					repaint();
				}
			}
		});
	}

	private void loadSprite() {
		itemIcon = BankItemRenderer.loadItemIcon(itemId, amount, itemSize);
	}

	private int getInventoryAmount() {
		RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
		if (inventoryInterface == null || inventoryInterface.inv == null || inventoryInterface.invStackSizes == null) {
			return 0;
		}

		int newAmount = 0;
		int maxIndex = Math.min(inventoryInterface.inv.length, inventoryInterface.invStackSizes.length);

		for (int i = 0; i < maxIndex; i++) {
			int checkItemId = inventoryInterface.inv[i] - 1;
			int checkAmount = inventoryInterface.invStackSizes[i];

			if (checkItemId == this.itemId && checkAmount > 0) {
				newAmount += checkAmount;
			}
		}

		return newAmount;
	}

	private int getBankAmount() {
		RSInterface bankInterface = RSInterface.interfaceCache[5382];
		if (bankInterface == null || bankInterface.inv == null || bankInterface.invStackSizes == null) {
			return 0;
		}

		int newAmount = 0;
		int maxIndex = Math.min(bankInterface.inv.length, bankInterface.invStackSizes.length);

		for (int i = 0; i < maxIndex; i++) {
			int checkItemId = bankInterface.inv[i] - 1;
			int checkAmount = bankInterface.invStackSizes[i];

			if (checkItemId == this.itemId && checkAmount > 0) {
				newAmount += checkAmount;
			}
		}

		return newAmount;
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

	private void handleLeftClick() {
		if (parentBankPanel == null) return;

		try {
			int requestedAmount = (Integer) parentBankPanel.getClass().getMethod("getLeftClickAmount").invoke(parentBankPanel);

			if (isInventoryItem && amount > 0) {
				depositItem(requestedAmount);
			} else if (!isPlaceholder && amount > 0) {
				withdrawItem(requestedAmount);
			}
		} catch (Exception e) {
			System.err.println("Error getting left click amount: " + e.getMessage());
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

	@Override
	public void paintComponent(Graphics g) {

		g.setColor(currentBackground);
		g.fillRect(0, 0, getWidth(), getHeight());

		Color borderColor = new Color(
			Math.min(255, currentBackground.getRed() + 30),
			Math.min(255, currentBackground.getGreen() + 30),
			Math.min(255, currentBackground.getBlue() + 30)
		);
		g.setColor(borderColor);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

		if (cachedImage != null) {
			g.drawImage(cachedImage, 0, 0, null);
		}
	}

	@Override
	public void removeNotify() {
		if (renderer != null) {
			renderer.dispose();
		}
		super.removeNotify();
	}
}
