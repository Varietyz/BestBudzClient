package com.bestbudz.dock.ui.panel.stoner;

import com.bestbudz.engine.config.ColorConfig;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Factory for creating consistent UI elements
 */
public class StonerUIFactory {

	private static final Color TEXT_SECONDARY = new Color(170, 170, 170);
	private static final Color BORDER = new Color(60, 60, 60);

	public static JButton createControlButton(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		button.setPreferredSize(new Dimension(75, 20));
		button.setForeground(TEXT_SECONDARY);
		button.setBackground(ColorConfig.GRAPHITE_COLOR);
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER, 1),
			new EmptyBorder(2, 4, 2, 4)
		));
		button.setFocusPainted(false);
		button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		return button;
	}

	public static void styleScrollPane(JScrollPane scrollPane) {
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.getViewport().setBackground(ColorConfig.MAIN_FRAME_COLOR);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				// Make thumb and track completely transparent/invisible
				this.thumbColor = new Color(0, 0, 0, 0); // Fully transparent
				this.trackColor = new Color(0, 0, 0, 0); // Fully transparent
			}

			@Override
			protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
				// Don't paint the track - leave it invisible
			}

			@Override
			protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
				// Don't paint the thumb - leave it invisible
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createInvisibleButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createInvisibleButton();
			}

			private JButton createInvisibleButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				button.setMinimumSize(new Dimension(0, 0));
				button.setMaximumSize(new Dimension(0, 0));
				return button;
			}
		});

		// Set scrollbar width to 0 to completely hide it
		verticalBar.setPreferredSize(new Dimension(0, 0));
		verticalBar.setSize(new Dimension(0, 0));
	}
}