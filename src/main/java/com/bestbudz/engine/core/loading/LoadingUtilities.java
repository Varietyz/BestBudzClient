package com.bestbudz.engine.core.loading;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.InputStream;

import static com.bestbudz.engine.core.loading.LoadingEnums.*;

public class LoadingUtilities {

	public static class FontManager {
		public Font titleFont;
		public Font statusFont;
		public Font detailFont;
		public Font monospaceFont;

		public FontManager() {
			try {
				GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
				String[] fontNames = ge.getAvailableFontFamilyNames();

				boolean hasSegoe = java.util.Arrays.asList(fontNames).contains("Segoe UI");
				boolean hasSF = java.util.Arrays.asList(fontNames).contains("SF Pro Display");

				String primaryFont = hasSF ? "SF Pro Display" : (hasSegoe ? "Segoe UI" : "Arial");
				String monospaceFont = java.util.Arrays.asList(fontNames).contains("JetBrains Mono") ?
					"JetBrains Mono" : "Consolas";

				this.titleFont = new Font(primaryFont, Font.BOLD, 24);
				this.statusFont = new Font(primaryFont, Font.PLAIN, 16);
				this.detailFont = new Font(primaryFont, Font.PLAIN, 12);
				this.monospaceFont = new Font(monospaceFont, Font.PLAIN, 11);

			} catch (Exception e) {

				this.titleFont = new Font("Arial", Font.BOLD, 24);
				this.statusFont = new Font("Arial", Font.PLAIN, 16);
				this.detailFont = new Font("Arial", Font.PLAIN, 12);
				this.monospaceFont = new Font("Monospaced", Font.PLAIN, 11);
			}
		}
	}

	public static class LogoManager {
		private BufferedImage originalLogoImage;

		public LogoManager() {
			loadLogo();
		}

		private void loadLogo() {
			try {
				String[] logoLocations = {
					"/logo.png", "/images/logo.png", "/assets/logo.png", "/resources/logo.png",
					"logo.png", "images/logo.png", "assets/logo.png"
				};

				for (String location : logoLocations) {
					InputStream logoStream = getClass().getResourceAsStream(location);
					if (logoStream != null) {
						try {
							originalLogoImage = ImageIO.read(logoStream);
							logoStream.close();
							return;
						} catch (Exception e) {
							logoStream.close();
						}
					}
				}
				originalLogoImage = null;
			} catch (Exception e) {
				originalLogoImage = null;
			}
		}

		public BufferedImage getScaledLogo(int size) {
			if (originalLogoImage == null) return null;

			BufferedImage scaledImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = scaledImage.createGraphics();
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.drawImage(originalLogoImage, 0, 0, size, size, null);
			g2d.dispose();
			return scaledImage;
		}

		public boolean hasLogo() {
			return originalLogoImage != null;
		}

		public BufferedImage getOriginalLogo() {
			return originalLogoImage;
		}
	}

	public static JProgressBar createStyledProgressBar() {
		JProgressBar progressBar = new JProgressBar(0, 100) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				g2d.setColor(new Color(40, 40, 44));
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());

				if (getValue() > 0) {
					int progressWidth = (int) ((double) getValue() / getMaximum() * getWidth());

					GradientPaint gradient = new GradientPaint(
						0, 0, ACCENT_COLOR,
						progressWidth, 0, ACCENT_COLOR.brighter()
					);
					g2d.setPaint(gradient);
					g2d.fillRoundRect(0, 0, progressWidth, getHeight(), getHeight(), getHeight());
				}

				g2d.dispose();
			}
		};

		progressBar.setStringPainted(false);
		progressBar.setBorderPainted(false);
		progressBar.setOpaque(false);
		return progressBar;
	}

	public static void styleScrollBar(JScrollPane scrollPane) {
		scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
			@Override
			protected void configureScrollBarColors() {
				this.thumbColor = new Color(60, 60, 64);
				this.trackColor = new Color(30, 30, 32);
			}

			@Override
			protected JButton createDecreaseButton(int orientation) {
				return createZeroSizeButton();
			}

			@Override
			protected JButton createIncreaseButton(int orientation) {
				return createZeroSizeButton();
			}

			private JButton createZeroSizeButton() {
				JButton button = new JButton();
				button.setPreferredSize(new Dimension(0, 0));
				return button;
			}
		});
	}

	public static String formatElapsedTime(long startTime) {
		long elapsed = System.currentTimeMillis() - startTime;
		int seconds = (int) (elapsed / 1000) % 60;
		int minutes = (int) (elapsed / 60000);
		return String.format("%02d:%02d", minutes, seconds);
	}

	public static String formatElapsedTime(long timestamp, long startTime) {
		long elapsed = timestamp - startTime;
		int seconds = (int) (elapsed / 1000) % 60;
		int minutes = (int) (elapsed / 60000);
		return String.format("%02d:%02d", minutes, seconds);
	}
}
