package com.bestbudz.dock.ui.panel.bank.components;

import com.bestbudz.engine.config.ColorConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Header component for bank panel showing title and item count
 */
public class BankHeader extends JPanel {

	private final JLabel titleLabel;
	private final JLabel itemCountLabel;
	private final BankAmountControl amountControl;

	public BankHeader() {
		setLayout(new BorderLayout());
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setBorder(new EmptyBorder(8, 12, 8, 12));

		titleLabel = new JLabel("Bank");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setOpaque(false);

		itemCountLabel = new JLabel("0/420");
		itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		itemCountLabel.setForeground(new Color(218, 165, 32));
		itemCountLabel.setOpaque(false);

		amountControl = new BankAmountControl();

		add(titleLabel, BorderLayout.WEST);
		add(amountControl, BorderLayout.CENTER);
		add(itemCountLabel, BorderLayout.EAST);
	}

	/**
	 * Updates the item count display
	 */
	public void updateItemCount(int bankItemCount) {
		SwingUtilities.invokeLater(() -> {
			itemCountLabel.setText(bankItemCount + "/420");
		});
	}

	/**
	 * Updates the withdraw mode display
	 */
	public void updateWithdrawMode(String mode) {
		amountControl.updateWithdrawMode(mode);
	}

	/**
	 * Gets the current left-click amount setting
	 */
	public int getLeftClickAmount() {
		return amountControl.getLeftClickAmount();
	}

	/**
	 * Gets the amount control component for direct access
	 */
	public BankAmountControl getAmountControl() {
		return amountControl;
	}
}