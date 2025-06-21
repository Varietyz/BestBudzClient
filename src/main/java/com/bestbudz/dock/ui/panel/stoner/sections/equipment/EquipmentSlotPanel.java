package com.bestbudz.dock.ui.panel.stoner.sections.equipment;

import com.bestbudz.dock.definitions.ItemBonusManager;
import com.bestbudz.dock.util.SpriteUtil;
import com.bestbudz.data.items.ItemDef;
import static com.bestbudz.data.items.GetItemDef.getItemDefinition;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Individual equipment slot panel with item display and interaction
 * Enhanced with item bonus tooltips
 */
public class EquipmentSlotPanel extends JPanel {

	private final int slotId;
	private final EquipmentClickBatcher clickBatcher;

	private int currentItemId = -1;
	private int currentAmount = 0;
	private ImageIcon itemIcon;
	private boolean isHovered = false;

	public interface UnequipHandler {
		void onUnequip(int slotId, int itemId);
	}

	public EquipmentSlotPanel(int slotId, EquipmentClickBatcher clickBatcher) {
		this.slotId = slotId;
		this.clickBatcher = clickBatcher;

		initializePanel();
		setupMouseHandlers();
		updateTooltip();
	}

	private void initializePanel() {
		setPreferredSize(new Dimension(38, 38));
		setMinimumSize(new Dimension(38, 38));
		setMaximumSize(new Dimension(38, 38));
		setBackground(EquipmentConstants.SLOT_BG);
		setBorder(BorderFactory.createLineBorder(EquipmentConstants.BORDER, 1));
		setOpaque(true);

		String slotName = EquipmentConstants.getSlotName(slotId);
		setToolTipText(slotName + " (Slot " + slotId + ")");
	}

	private void setupMouseHandlers() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// Changed from right-click to left-click for removing items
				if (currentItemId >= 0 && SwingUtilities.isLeftMouseButton(e)) {
					unequipItem();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				isHovered = true;
				setBackground(EquipmentConstants.SLOT_HOVER);
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				isHovered = false;
				setBackground(EquipmentConstants.SLOT_BG);
				repaint();
			}
		});
	}

	private void unequipItem() {
		try {
			if (currentItemId < 0) return;

			Integer equipmentSlot = EquipmentConstants.getEquipmentIndex(slotId);
			if (equipmentSlot == null) {
				System.err.println("No equipment slot found for interface " + slotId);
				return;
			}

			// Use the click batcher for batched execution
			clickBatcher.batchUnequip(slotId, equipmentSlot, currentItemId);

		} catch (Exception e) {
			System.err.println("Error batching unequip: " + e.getMessage());
		}
	}

	public void setItem(int itemId, int amount) {
		if (this.currentItemId == itemId && this.currentAmount == amount) {
			return;
		}

		this.currentItemId = itemId;
		this.currentAmount = amount;
		loadItemIcon();
		updateTooltip();
		repaint();
	}

	public void clearItem() {
		if (this.currentItemId == -1) {
			return;
		}

		this.currentItemId = -1;
		this.currentAmount = 0;
		this.itemIcon = null;

		String slotName = EquipmentConstants.getSlotName(slotId);
		setToolTipText(slotName + " (Empty)");
		repaint();
	}

	private void loadItemIcon() {
		try {
			int spriteItemId = currentItemId;

			if (currentItemId == 995) {
				spriteItemId = getCoinSpriteId(currentAmount);
			}

			String spritePath = "sprites/items/" + spriteItemId + ".png";
			itemIcon = SpriteUtil.loadIconScaled(spritePath, 32);

			if (itemIcon == null) {
				itemIcon = SpriteUtil.loadIconScaled("sprites/items/default.png", 32);
			}
		} catch (Exception e) {
			System.err.println("Error loading sprite for item " + currentItemId + ": " + e.getMessage());
			itemIcon = null;
		}
	}

	private int getCoinSpriteId(int amount) {
		if (amount >= 10000) return 1004;
		else if (amount >= 1000) return 1003;
		else if (amount >= 250) return 1002;
		else if (amount >= 100) return 1001;
		else if (amount >= 25) return 1000;
		else if (amount >= 5) return 999;
		else if (amount >= 4) return 998;
		else if (amount >= 3) return 997;
		else if (amount >= 2) return 996;
		else return 995;
	}

	private void updateTooltip() {
		if (currentItemId < 0) {
			String slotName = EquipmentConstants.getSlotName(slotId);
			setToolTipText(slotName + " (Empty)");
			return;
		}

		try {
			ItemDef itemDef = getItemDefinition(currentItemId);
			if (itemDef != null) {
				StringBuilder tooltip = new StringBuilder("<html>");
				tooltip.append("<b>").append(itemDef.name).append("</b><br/>");

				if (currentAmount > 1) {
					tooltip.append("Amount: ").append(formatAmount(currentAmount)).append("<br/>");
				}

				tooltip.append("Item ID: ").append(currentItemId).append("<br/>");

				// Add equipment bonuses section using ItemBonusManager
				addEquipmentBonuses(tooltip);

				if (itemDef.value > 1) {
					if (currentItemId == 995) {
						tooltip.append("Value: ").append(formatAmount(currentAmount)).append(" gp<br/>");
					} else {
						tooltip.append("Unit Value: ").append(formatAmount(itemDef.value)).append(" gp<br/>");
						if (currentAmount > 1) {
							long totalValue = (long) itemDef.value * currentAmount;
							tooltip.append("Total Value: ").append(formatAmount((int) Math.min(totalValue, Integer.MAX_VALUE))).append(" gp<br/>");
						}
					}
				}

				String slotName = EquipmentConstants.getSlotName(slotId);
				tooltip.append("Slot: ").append(slotName).append("<br/>");
				tooltip.append("<i>Left-click to remove</i>");
				tooltip.append("</html>");

				setToolTipText(tooltip.toString());
			}
		} catch (Exception e) {
			setToolTipText("Item " + currentItemId + " (Left-click to remove)");
		}
	}

	/**
	 * Adds equipment bonuses to the tooltip using ItemBonusManager
	 */
	private void addEquipmentBonuses(StringBuilder tooltip) {
		try {
			// Get item bonuses from the ItemBonusManager
			short[] bonuses = ItemBonusManager.getBonuses(currentItemId);

			if (bonuses == null || bonuses.length == 0) {
				return; // No bonuses to display
			}

			// Check if item has any non-zero bonuses
			boolean hasAnyBonuses = ItemBonusManager.hasEquipmentBonuses(currentItemId);
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
			System.err.println("Error adding equipment bonuses to tooltip: " + e.getMessage());
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

	// Remove the old getItemBonuses method since we're now using ItemBonusManager directly
	// Also remove the BONUS_NAMES constant since we get them from ItemBonusManager

	private String formatAmount(int amount) {
		if (amount >= 1000000) return (amount / 1000000) + "M";
		else if (amount >= 1000) return (amount / 1000) + "K";
		return String.valueOf(amount);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		if (itemIcon != null && currentItemId >= 0) {
			// Draw item icon
			int x = (getWidth() - itemIcon.getIconWidth()) / 2;
			int y = (getHeight() - itemIcon.getIconHeight()) / 2;
			itemIcon.paintIcon(this, g2d, x, y);

			// Draw amount if > 1
			if (currentAmount > 1) {
				drawAmountLabel(g2d);
			}
		} else if (currentItemId >= 0) {
			// Draw item ID as fallback
			g2d.setColor(EquipmentConstants.TEXT_SECONDARY);
			g2d.setFont(new Font("Segoe UI", Font.PLAIN, 8));
			String text = String.valueOf(currentItemId);
			FontMetrics fm = g2d.getFontMetrics();
			int x = (getWidth() - fm.stringWidth(text)) / 2;
			int y = getHeight() / 2 + fm.getAscent() / 2;
			g2d.drawString(text, x, y);
		}

		g2d.dispose();
	}

	private void drawAmountLabel(Graphics2D g2d) {
		int fontSize = Math.max(8, getWidth() / 4);
		g2d.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
		FontMetrics fm = g2d.getFontMetrics();

		String amountText = formatAmount(currentAmount);
		int textWidth = fm.stringWidth(amountText);
		int textHeight = fm.getHeight();

		int padding = 2;
		int x = getWidth() - textWidth - padding;
		int y = getHeight() - padding;

		// Background
		g2d.setColor(new Color(0, 0, 0, 200));
		g2d.fillRoundRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight, 4, 4);

		g2d.setColor(new Color(80, 80, 80));
		g2d.drawRoundRect(x - 2, y - textHeight + 2, textWidth + 4, textHeight, 4, 4);

		// Amount color coding
		Color amountColor;
		if (currentAmount >= 10000000) amountColor = new Color(0, 255, 0);
		else if (currentAmount >= 1000000) amountColor = new Color(150, 255, 150);
		else if (currentAmount >= 100000) amountColor = new Color(255, 255, 0);
		else if (currentAmount >= 1000) amountColor = new Color(255, 255, 150);
		else amountColor = Color.WHITE;

		g2d.setColor(amountColor);
		g2d.drawString(amountText, x, y - 1);
	}
}