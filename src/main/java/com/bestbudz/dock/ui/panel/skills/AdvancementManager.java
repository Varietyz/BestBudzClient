package com.bestbudz.dock.ui.panel.skills;

import static com.bestbudz.engine.config.ColorConfig.*;
import javax.swing.*;
import java.awt.*;

public class AdvancementManager {

	public static int parseAdvancementLevel(String text) {
		if (text == null || text.isEmpty()) {
			System.out.println("parseAdvancementLevel: text is null or empty");
			return 0;
		}

		System.out.println("parseAdvancementLevel: parsing text: '" + text + "'");

		try {

			String advancePattern = "Advance lv: @dre@";
			int advanceIndex = text.indexOf(advancePattern);

			System.out.println("parseAdvancementLevel: advancePattern found at index: " + advanceIndex);

			if (advanceIndex != -1) {

				int startIndex = advanceIndex + advancePattern.length();
				int endIndex = startIndex;

				while (endIndex < text.length() && Character.isDigit(text.charAt(endIndex))) {
					endIndex++;
				}

				if (endIndex > startIndex) {
					String advanceStr = text.substring(startIndex, endIndex);
					int result = Integer.parseInt(advanceStr);
					System.out.println("parseAdvancementLevel: successfully parsed: " + result);
					return result;
				} else {
					System.out.println("parseAdvancementLevel: no digits found after pattern");
				}
			} else {

				String[] alternativePatterns = {
					"Advance lv: ",
					"\\nAdvance lv: @dre@",
					"Advance grade: @dre@",
					"\\nAdvance grade: @dre@"
				};

				for (String pattern : alternativePatterns) {
					int patternIndex = text.indexOf(pattern);
					if (patternIndex != -1) {
						System.out.println("parseAdvancementLevel: found alternative pattern: " + pattern + " at index: " + patternIndex);
						int startIndex = patternIndex + pattern.length();
						int endIndex = startIndex;

						while (endIndex < text.length() && Character.isDigit(text.charAt(endIndex))) {
							endIndex++;
						}

						if (endIndex > startIndex) {
							String advanceStr = text.substring(startIndex, endIndex);
							int result = Integer.parseInt(advanceStr);
							System.out.println("parseAdvancementLevel: successfully parsed with alternative pattern: " + result);
							return result;
						}
					}
				}

				System.out.println("parseAdvancementLevel: no advancement pattern found");
			}

			return 0;
		} catch (NumberFormatException e) {
			System.out.println("parseAdvancementLevel: NumberFormatException parsing: " + text);
			return 0;
		}
	}

	public static Color getAdvancementColor(int level) {
		if (level <= 0) return ADVANCE_COLORS[0];
		if (level >= ADVANCE_COLORS.length) return ADVANCE_COLORS[ADVANCE_COLORS.length - 1];
		return ADVANCE_COLORS[Math.min(level, ADVANCE_COLORS.length - 1)];
	}

	public static JLabel createAdvancementBadge(int advanceLevel) {
		JLabel badge = new JLabel(String.valueOf(advanceLevel)) {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color advanceColor = getAdvancementColor(advanceLevel);
				Color transparentColor = new Color(
					advanceColor.getRed(),
					advanceColor.getGreen(),
					advanceColor.getBlue(),
					140
				);
				Color transparentDarker = new Color(
					advanceColor.darker().getRed(),
					advanceColor.darker().getGreen(),
					advanceColor.darker().getBlue(),
					140
				);

				GradientPaint gradient = new GradientPaint(
					0, 0, transparentColor,
					0, getHeight(), transparentDarker
				);
				g2d.setPaint(gradient);
				g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

				g2d.setColor(new Color(255, 255, 255, 30));
				g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 3, 4, 4);

				g2d.dispose();
				super.paintComponent(g);
			}
		};

		badge.setFont(new Font("Segoe UI", Font.BOLD, 9));
		badge.setForeground(Color.WHITE);
		badge.setHorizontalAlignment(SwingConstants.CENTER);
		badge.setVerticalAlignment(SwingConstants.CENTER);
		badge.setBounds(114, -2, 18, 16);
		badge.setOpaque(false);

		String levelName = getAdvancementLevelName(advanceLevel);
		badge.setToolTipText("<html><b>Advancement Level " + advanceLevel + "</b><br>" + levelName + "</html>");

		return badge;
	}

	public static String getAdvancementLevelName(int level) {
		switch (level) {
			case 1: return "Novice";
			case 2: return "Adept";
			case 3: return "Expert";
			case 4: return "Master";
			case 5: return "Grandmaster";
			default: return level > 5 ? "Legendary" : "None";
		}
	}
}
