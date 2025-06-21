package com.bestbudz.dock.ui.panel.skills;

import com.bestbudz.dock.util.RainbowHoverUtil;
import static com.bestbudz.engine.config.ColorConfig.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SkillUIFactory {

	public static JButton createToggleButton(String text) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		button.setForeground(TEXT_SECONDARY_COLOR);
		button.setBackground(GRAPHITE_COLOR);
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(BORDER_COLOR, 1),
			new EmptyBorder(2, 4, 2, 4)
		));
		button.setFocusPainted(false);
		button.setPreferredSize(new Dimension(55, 18));

		RainbowHoverUtil.applyRainbowHover(button);

		return button;
	}

	public static JPanel createModernTile() {
		JPanel tile = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				g2d.setColor(BORDER_COLOR);
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

				g2d.dispose();
			}
		};

		tile.setOpaque(false);
		tile.setBackground(GRAPHITE_COLOR);
		tile.setLayout(null);
		return tile;
	}

	public static JPanel createGoldenTile() {
		JPanel tile = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setColor(getBackground());
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

				g2d.setColor(GOLD_ACCENT_COLOR);
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

				g2d.dispose();
			}
		};

		tile.setOpaque(false);
		tile.setBackground(TOTAL_GRADE_BG_COLOR);
		tile.setLayout(null);
		return tile;
	}

	public static void styleScrollBar(JScrollPane scrollPane) {
		JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
		verticalBar.setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = LIGHT_GRAY_COLOR;
				this.trackColor = MAIN_FRAME_COLOR;
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
		verticalBar.setPreferredSize(new Dimension(8, 0));
	}

	public static JSlider createColorSlider() {
		JSlider colorSlider = new JSlider(0, 360, 240);
		colorSlider.setOpaque(false);
		colorSlider.setPreferredSize(new Dimension(60, 16));

		colorSlider.setUI(new javax.swing.plaf.basic.BasicSliderUI(colorSlider) {
			@Override
			public void paintThumb(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2d.setColor(XP_TEXT_COLOR);
				g2d.fillOval(thumbRect.x, thumbRect.y + 2, 8, 8);
				g2d.dispose();
			}

			@Override
			public void paintTrack(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(GRAPHITE_COLOR);
				g2d.fillRoundRect(trackRect.x, trackRect.y + 4, trackRect.width, 4, 2, 2);
				g2d.dispose();
			}
		});

		return colorSlider;
	}
}