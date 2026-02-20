package com.bestbudz.engine.core.loading.panels;

import com.bestbudz.engine.core.loading.LoadingUtilities;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicLong;

import static com.bestbudz.engine.core.loading.LoadingEnums.*;

public class HeaderPanel extends JPanel {
	private final JLabel logoLabel;
	private final JLabel titleLabel;
	private final JLabel timeLabel;
	private final LoadingUtilities.LogoManager logoManager;
	private final AtomicLong startTime;
	private final LoadingUtilities.FontManager fontManager;

	public HeaderPanel(LoadingUtilities.FontManager fontManager, AtomicLong startTime) {
		this.fontManager = fontManager;
		this.startTime = startTime;
		this.logoManager = new LoadingUtilities.LogoManager();

		setLayout(new BorderLayout(16, 0));
		setOpaque(false);

		logoLabel = createLogoLabel();

		titleLabel = new JLabel("Arcade RSPS", JLabel.LEFT);
		titleLabel.setFont(fontManager.titleFont);
		titleLabel.setForeground(PRIMARY_TEXT);

		timeLabel = new JLabel("00:00", JLabel.LEFT);
		timeLabel.setFont(fontManager.monospaceFont);
		timeLabel.setForeground(SECONDARY_TEXT);

		layoutComponents();
	}

	private JLabel createLogoLabel() {
		int logoSize = 200;
		int logoOffset = 200;

		return new JLabel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(logoSize + logoOffset, logoSize);
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				if (logoManager.hasLogo()) {
					BufferedImage scaledLogo = logoManager.getScaledLogo(logoSize);
					if (scaledLogo != null) {
						int x = (getWidth() - logoSize) / 2;
						int y = (getHeight() - logoSize) / 2;
						g2d.drawImage(scaledLogo, x, y, null);
					}
				} else {
					drawFallbackLogo(g2d, logoSize);
				}
				g2d.dispose();
			}

			private void drawFallbackLogo(Graphics2D g2d, int logoSize) {
				int circleSize = logoSize - 20;
				int circleX = (getWidth() - circleSize) / 2;
				int circleY = (getHeight() - circleSize) / 2;

				g2d.setColor(ACCENT_COLOR.darker());
				g2d.fillOval(circleX, circleY, circleSize, circleSize);
				g2d.setColor(ACCENT_COLOR);
				g2d.setStroke(new BasicStroke(3f));
				g2d.drawOval(circleX, circleY, circleSize, circleSize);

				g2d.setFont(new Font("Arial", Font.BOLD, logoSize / 6));
				g2d.setColor(Color.WHITE);
				FontMetrics fm = g2d.getFontMetrics();
				String text = "BB";
				int textX = (getWidth() - fm.stringWidth(text)) / 2;
				int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
				g2d.drawString(text, textX, textY);
			}
		};
	}

	private void layoutComponents() {

		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
		titlePanel.setOpaque(false);
		titlePanel.setAlignmentY(Component.CENTER_ALIGNMENT);

		titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		timeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

		titlePanel.add(titleLabel);
		titlePanel.add(Box.createVerticalStrut(4));
		titlePanel.add(timeLabel);

		add(titlePanel, BorderLayout.CENTER);
		add(logoLabel, BorderLayout.EAST);
	}

	public void updateElapsedTime() {
		SwingUtilities.invokeLater(() -> {
			String formattedTime = LoadingUtilities.formatElapsedTime(startTime.get());
			timeLabel.setText(formattedTime);
		});
	}

	public void updateTitle(String title) {
		SwingUtilities.invokeLater(() -> titleLabel.setText(title));
	}
}
