package com.bestbudz.dock.ui.panel.bank.components;

import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.engine.config.ColorConfig;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

/**
 * Amount input and withdraw mode control for bank panel
 */
public class BankAmountControl extends JPanel {

	private static final int DOCK_TOGGLE_WITHDRAW_MODE = 115249;

	private JTextField amountField;
	private JLabel withdrawModeLabel;
	private int leftClickAmount = 1;

	public BankAmountControl() {
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 4));
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);

		initializeComponents();
	}

	private void initializeComponents() {
		// Amount input field
		amountField = new JTextField("1", 4);
		amountField.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		amountField.setBackground(new Color(55, 55, 55));
		amountField.setForeground(Color.WHITE);
		amountField.setCaretColor(Color.WHITE);
		amountField.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
		amountField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateLeftClickAmount();
			}
		});
		amountField.addActionListener(e -> updateLeftClickAmount());

		// Withdraw mode label (Item/Note)
		withdrawModeLabel = new JLabel("Item");
		withdrawModeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		withdrawModeLabel.setForeground(new Color(200, 200, 200));
		withdrawModeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		withdrawModeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				toggleWithdrawMode();
			}
		});

		JLabel amountLabel = new JLabel("Amount:");
		amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		amountLabel.setForeground(new Color(180, 180, 180));

		add(amountLabel);
		add(amountField);
		add(withdrawModeLabel);
	}

	/**
	 * Updates the left-click amount based on text field input
	 */
	private void updateLeftClickAmount() {
		try {
			String text = amountField.getText().trim().toLowerCase();
			if (text.equals("all") || text.equals("-1")) {
				leftClickAmount = -1;
			} else {
				leftClickAmount = Math.max(1, Integer.parseInt(text));
				if (leftClickAmount != Integer.parseInt(text)) {
					amountField.setText(String.valueOf(leftClickAmount));
				}
			}
		} catch (NumberFormatException e) {
			leftClickAmount = 1;
			amountField.setText("1");
		}
	}

	/**
	 * Toggles between Item and Note withdraw mode
	 */
	private void toggleWithdrawMode() {
		try {
			ButtonHandler.sendClick(DOCK_TOGGLE_WITHDRAW_MODE);
		} catch (Exception e) {
			System.err.println("Error toggling withdraw mode: " + e.getMessage());
		}
	}

	/**
	 * Updates the withdraw mode display text
	 */
	public void updateWithdrawMode(String mode) {
		SwingUtilities.invokeLater(() -> {
			withdrawModeLabel.setText(mode);
			repaint();
		});
	}

	/**
	 * Gets the current left-click amount setting
	 */
	public int getLeftClickAmount() {
		return leftClickAmount;
	}

	/**
	 * Sets the amount field value
	 */
	public void setAmount(String amount) {
		SwingUtilities.invokeLater(() -> {
			amountField.setText(amount);
			updateLeftClickAmount();
		});
	}

	/**
	 * Gets the amount field component for direct access
	 */
	public JTextField getAmountField() {
		return amountField;
	}
}