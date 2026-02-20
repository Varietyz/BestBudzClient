package com.bestbudz.dock.ui.panel.bank.components;

import com.bestbudz.engine.config.ColorConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

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

	public void updateItemCount(int bankItemCount) {
		SwingUtilities.invokeLater(() -> {
			itemCountLabel.setText(bankItemCount + "/420");
		});
	}

	public void updateWithdrawMode(String mode) {
		amountControl.updateWithdrawMode(mode);
	}

	public int getLeftClickAmount() {
		return amountControl.getLeftClickAmount();
	}

	public BankAmountControl getAmountControl() {
		return amountControl;
	}
}
