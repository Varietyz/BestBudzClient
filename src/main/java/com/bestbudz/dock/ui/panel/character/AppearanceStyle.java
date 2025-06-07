package com.bestbudz.dock.ui.panel.character;

import com.bestbudz.dock.util.RainbowHoverUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import static com.bestbudz.engine.config.ColorConfig.*;

public class AppearanceStyle {

	public static JLabel createSectionHeader(String text) {
		JLabel header = new JLabel(text);
		header.setFont(new Font("SansSerif", Font.BOLD, 13));
		header.setForeground(GOLD_ACCENT_COLOR);
		header.setBorder(new EmptyBorder(5, 5, 2, 5));
		header.setAlignmentX(Component.LEFT_ALIGNMENT);
		return header;
	}

	public static JPanel createGenderSection(Runnable maleAction, Runnable femaleAction) {
		JPanel section = new JPanel(new BorderLayout());
		section.setOpaque(false);
		section.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

		JLabel label = new JLabel(AppearanceConfig.UI.GENDER_SECTION);
		label.setFont(new Font("SansSerif", Font.BOLD, 12));
		label.setForeground(ACCENT_COLOR);
		label.setBorder(new EmptyBorder(5, 5, 5, 10));

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		buttons.setOpaque(false);
		buttons.add(createGenderButton(AppearanceConfig.UI.MALE_LABEL, maleAction));
		buttons.add(createGenderButton(AppearanceConfig.UI.FEMALE_LABEL, femaleAction));

		section.add(label, BorderLayout.WEST);
		section.add(buttons, BorderLayout.CENTER);
		return section;
	}

	public static JPanel createPartRow(String name, Runnable backAction, Runnable forwardAction) {
		return createThreeColumnRow(name, backAction, forwardAction, ACCENT_COLOR);
	}

	public static JPanel createColorRow(String name, Runnable backAction, Runnable forwardAction) {
		return createThreeColumnRow(name, backAction, forwardAction, GOLD_ACCENT_COLOR);
	}

	private static JPanel createThreeColumnRow(String labelText, Runnable backAction, Runnable forwardAction, Color btnColor) {
		JPanel row = new JPanel(new GridBagLayout());
		row.setOpaque(false);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		row.setBorder(new EmptyBorder(2, 0, 2, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0, 5, 0, 5);
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;

		JLabel label = new JLabel(labelText);
		label.setFont(new Font("SansSerif", Font.PLAIN, 11));
		label.setForeground(TEXT_PRIMARY_COLOR);
		label.setPreferredSize(new Dimension(60, 25));

		gbc.gridx = 0;
		row.add(label, gbc);

		JButton backBtn = createNavigationButton("◀", backAction, btnColor);
		gbc.gridx = 1;
		row.add(backBtn, gbc);

		JButton forwardBtn = createNavigationButton("▶", forwardAction, btnColor);
		gbc.gridx = 2;
		row.add(forwardBtn, gbc);

		return row;
	}

	private static JButton createGenderButton(String text, Runnable action) {
		JButton button = new JButton(text) {
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color bg = getModel().isPressed() ? GRAPHITE_COLOR.darker()
					: getModel().isRollover() ? GRAPHITE_COLOR.brighter() : GRAPHITE_COLOR;
				g2d.setColor(bg);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
				g2d.setColor(BORDER_COLOR);
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("SansSerif", Font.BOLD, 10));
		button.setForeground(TEXT_PRIMARY_COLOR);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(70, 25));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(e -> {
			button.setEnabled(false);
			SwingUtilities.invokeLater(() -> {
				try {
					action.run();
				} finally {
					new Timer(500, evt -> button.setEnabled(true)).start();
				}
			});
		});
		RainbowHoverUtil.applyRainbowHover(button);
		return button;
	}

	private static JButton createNavigationButton(String text, Runnable action, Color bgColor) {
		JButton button = new JButton(text) {
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				Color bg = getModel().isPressed() ? bgColor.darker().darker()
					: getModel().isRollover() ? bgColor.brighter() : bgColor;
				g2d.setColor(bg);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
				g2d.setColor(bgColor.darker());
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 4, 4);
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		button.setFont(new Font("SansSerif", Font.BOLD, 12));
		button.setForeground(Color.WHITE);
		button.setOpaque(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(30, 25));
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		button.addActionListener(e -> {
			button.setEnabled(false);
			SwingUtilities.invokeLater(() -> {
				try {
					action.run();
				} finally {
					new Timer(200, evt -> button.setEnabled(true)).start();
				}
			});
		});
		RainbowHoverUtil.applyRainbowHover(button);
		return button;
	}

	public static Component createVerticalSpacing(int height) {
		return Box.createVerticalStrut(height);
	}

	public static Component createVerticalGlue() {
		return Box.createVerticalGlue();
	}

	public static void styleScrollPane(JScrollPane scrollPane) {
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
	}

	public static JPanel createContentPanel() {
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(MAIN_FRAME_COLOR);
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		return contentPanel;
	}

	public static void updateStatusLabel(JLabel statusLabel, String message, boolean isError) {
		if (statusLabel != null) {
			statusLabel.setText(message);
			statusLabel.setForeground(isError ? new Color(255, 100, 100) : TEXT_SECONDARY_COLOR);
			Timer timer = new Timer(3000, e -> {
				if (statusLabel != null) {
					statusLabel.setText("Changes applied immediately");
					statusLabel.setForeground(TEXT_SECONDARY_COLOR);
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}
}
