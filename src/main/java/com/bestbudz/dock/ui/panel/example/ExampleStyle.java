package com.bestbudz.dock.ui.panel.example;

import static com.bestbudz.engine.config.ColorConfig.*;
import com.bestbudz.dock.util.InteractiveButtonUtil;
import com.bestbudz.dock.util.RainbowHoverUtil;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Styling utilities and custom styles for the Example Panel
 *
 * PURPOSE:
 * - Panel-specific styling and theming
 * - Custom component appearances
 * - Consistent visual design
 * - Reusable style methods
 * - Color schemes and themes
 *
 * ORGANIZATION:
 * - Colors: Panel-specific color definitions
 * - Fonts: Typography settings
 * - Borders: Custom border styles
 * - Component Styling: Methods to style specific components
 * - Themes: Different visual themes or modes
 */
public class ExampleStyle {

	// ==========================================
	// PANEL-SPECIFIC COLORS
	// ==========================================

	/** Primary color for this panel's theme */
	public static final Color PRIMARY_COLOR = new Color(80, 120, 200);

	/** Secondary color for accents */
	public static final Color SECONDARY_COLOR = new Color(120, 160, 240);

	/** Success state color */
	public static final Color SUCCESS_COLOR = new Color(80, 200, 120);

	/** Warning state color */
	public static final Color WARNING_COLOR = new Color(240, 180, 60);

	/** Error state color */
	public static final Color ERROR_COLOR = new Color(220, 80, 80);

	/** Low value indicator color */
	public static final Color LOW_VALUE_COLOR = ERROR_COLOR;

	/** Medium value indicator color */
	public static final Color MEDIUM_VALUE_COLOR = WARNING_COLOR;

	/** High value indicator color */
	public static final Color HIGH_VALUE_COLOR = SUCCESS_COLOR;

	/** Disabled component color */
	public static final Color DISABLED_COLOR = new Color(100, 100, 100);

	/** Selected item background */
	public static final Color SELECTED_BACKGROUND = new Color(80, 120, 200, 60);

	/** Header background color */
	public static final Color HEADER_BACKGROUND = new Color(40, 60, 80);

	// ==========================================
	// TYPOGRAPHY
	// ==========================================

	/** Default font for panel text */
	public static final Font DEFAULT_FONT = new Font("SansSerif", Font.PLAIN, 12);

	/** Font for panel headers */
	public static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);

	/** Font for large titles */
	public static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 16);

	/** Font for small details */
	public static final Font SMALL_FONT = new Font("SansSerif", Font.PLAIN, 10);

	/** Monospace font for numbers/data */
	public static final Font MONO_FONT = new Font("Monospaced", Font.PLAIN, 12);

	// ==========================================
	// BORDERS
	// ==========================================

	/** Standard content border */
	public static final Border CONTENT_BORDER = BorderFactory.createCompoundBorder(
		BorderFactory.createLineBorder(BORDER_COLOR, 1),
		new EmptyBorder(4, 6, 4, 6)
	);

	/** Header section border */
	public static final Border HEADER_BORDER = BorderFactory.createCompoundBorder(
		BorderFactory.createLineBorder(BORDER_COLOR, 1),
		new EmptyBorder(6, 8, 6, 8)
	);

	/** Thin separator border */
	public static final Border SEPARATOR_BORDER = BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR);

	/** Raised button-like border */
	public static final Border RAISED_BORDER = BorderFactory.createCompoundBorder(
		BorderFactory.createRaisedBevelBorder(),
		new EmptyBorder(2, 4, 2, 4)
	);

	// ==========================================
	// COMPONENT STYLING METHODS
	// ==========================================

	/**
	 * Apply standard header styling to a label
	 * @param label Label to style
	 */
	public static void styleHeaderLabel(JLabel label) {
		label.setFont(HEADER_FONT);
		label.setForeground(TEXT_PRIMARY_COLOR);
		label.setBackground(HEADER_BACKGROUND);
		label.setOpaque(true);
		label.setBorder(HEADER_BORDER);
		label.setHorizontalAlignment(SwingConstants.CENTER);
	}

	/**
	 * Apply title styling to a label
	 * @param label Label to style
	 */
	public static void styleTitleLabel(JLabel label) {
		label.setFont(TITLE_FONT);
		label.setForeground(PRIMARY_COLOR);
		label.setBorder(new EmptyBorder(4, 0, 8, 0));
	}

	/**
	 * Apply data value styling based on threshold
	 * @param label Label to style
	 * @param value Value to determine color
	 */
	public static void styleValueLabel(JLabel label, int value) {
		label.setFont(MONO_FONT);
		label.setForeground(getValueColor(value));
		label.setHorizontalAlignment(SwingConstants.RIGHT);
	}

	/**
	 * Get color based on value threshold
	 * @param value Value to evaluate
	 * @return Appropriate color
	 */
	public static Color getValueColor(int value) {
		if (value <= ExampleConfig.CRITICAL_THRESHOLD) {
			return ERROR_COLOR;
		} else if (value <= ExampleConfig.LOW_THRESHOLD) {
			return LOW_VALUE_COLOR;
		} else if (value <= ExampleConfig.MEDIUM_THRESHOLD) {
			return MEDIUM_VALUE_COLOR;
		}
		return HIGH_VALUE_COLOR;
	}

	/**
	 * Style a content panel with consistent appearance
	 * @param panel Panel to style
	 */
	public static void styleContentPanel(JPanel panel) {
		panel.setBackground(PANEL_COLOR);
		panel.setBorder(CONTENT_BORDER);
		panel.setOpaque(true);
	}

	/**
	 * Style a list item component
	 * @param component Component to style
	 * @param isSelected Whether the item is selected
	 * @param isEnabled Whether the item is enabled
	 */
	public static void styleListItem(JComponent component, boolean isSelected, boolean isEnabled) {
		component.setFont(DEFAULT_FONT);

		if (!isEnabled) {
			component.setForeground(DISABLED_COLOR);
			component.setBackground(PANEL_COLOR);
		} else if (isSelected) {
			component.setForeground(TEXT_PRIMARY_COLOR);
			component.setBackground(SELECTED_BACKGROUND);
		} else {
			component.setForeground(TEXT_PRIMARY_COLOR);
			component.setBackground(PANEL_COLOR);
		}

		component.setOpaque(true);
		component.setBorder(new EmptyBorder(2, 4, 2, 4));
	}

	/**
	 * Create a styled separator component
	 * @return Styled separator
	 */
	public static JComponent createSeparator() {
		JPanel separator = new JPanel();
		separator.setPreferredSize(new Dimension(0, 1));
		separator.setBackground(BORDER_COLOR);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		return separator;
	}

	/**
	 * Create a styled progress bar
	 * @param value Current value
	 * @param maximum Maximum value
	 * @return Styled progress bar
	 */
	public static JProgressBar createStyledProgressBar(int value, int maximum) {
		JProgressBar progressBar = new JProgressBar(0, maximum);
		progressBar.setValue(value);
		progressBar.setStringPainted(true);
		progressBar.setString(value + " / " + maximum);
		progressBar.setFont(SMALL_FONT);
		progressBar.setForeground(PRIMARY_COLOR);
		progressBar.setBackground(PANEL_COLOR);
		return progressBar;
	}

	// ==========================================
	// CUSTOM BUTTON STYLES
	// ==========================================

	/**
	 * Create a primary action button with custom styling
	 * @param text Button text
	 * @param interfaceId Interface ID for the button
	 * @return Styled button
	 */
	public static JButton createPrimaryButton(String text, int interfaceId) {
		return InteractiveButtonUtil.addCustomButton(text, interfaceId, button -> {
			button.setFont(DEFAULT_FONT);
			button.setForeground(Color.WHITE);
			button.setBackground(PRIMARY_COLOR);
			button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(PRIMARY_COLOR.darker(), 2),
				new EmptyBorder(6, 12, 6, 12)
			));
			button.setFocusPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}, ExampleConfig.ENABLE_RAINBOW_HOVER);
	}

	/**
	 * Create a secondary action button with custom styling
	 * @param text Button text
	 * @param interfaceId Interface ID for the button
	 * @return Styled button
	 */
	public static JButton createSecondaryButton(String text, int interfaceId) {
		return InteractiveButtonUtil.addCustomButton(text, interfaceId, button -> {
			button.setFont(DEFAULT_FONT);
			button.setForeground(TEXT_PRIMARY_COLOR);
			button.setBackground(PANEL_COLOR);
			button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER_COLOR, 1),
				new EmptyBorder(4, 8, 4, 8)
			));
			button.setFocusPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}, ExampleConfig.ENABLE_RAINBOW_HOVER);
	}

	/**
	 * Create a danger/warning button with custom styling
	 * @param text Button text
	 * @param interfaceId Interface ID for the button
	 * @return Styled button
	 */
	public static JButton createDangerButton(String text, int interfaceId) {
		return InteractiveButtonUtil.addCustomButton(text, interfaceId, button -> {
			button.setFont(DEFAULT_FONT);
			button.setForeground(Color.WHITE);
			button.setBackground(ERROR_COLOR);
			button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(ERROR_COLOR.darker(), 2),
				new EmptyBorder(6, 12, 6, 12)
			));
			button.setFocusPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}, ExampleConfig.ENABLE_RAINBOW_HOVER);
	}

	/**
	 * Create a success/confirmation button with custom styling
	 * @param text Button text
	 * @param interfaceId Interface ID for the button
	 * @return Styled button
	 */
	public static JButton createSuccessButton(String text, int interfaceId) {
		return InteractiveButtonUtil.addCustomButton(text, interfaceId, button -> {
			button.setFont(DEFAULT_FONT);
			button.setForeground(Color.WHITE);
			button.setBackground(SUCCESS_COLOR);
			button.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(SUCCESS_COLOR.darker(), 2),
				new EmptyBorder(6, 12, 6, 12)
			));
			button.setFocusPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		}, ExampleConfig.ENABLE_RAINBOW_HOVER);
	}

	// ==========================================
	// LAYOUT HELPERS
	// ==========================================

	/**
	 * Create a titled section panel
	 * @param title Section title
	 * @param content Content component
	 * @return Panel with title and content
	 */
	public static JPanel createTitledSection(String title, JComponent content) {
		JPanel section = new JPanel(new BorderLayout());
		section.setOpaque(false);

		JLabel titleLabel = new JLabel(title);
		styleTitleLabel(titleLabel);

		section.add(titleLabel, BorderLayout.NORTH);
		section.add(content, BorderLayout.CENTER);

		return section;
	}

	/**
	 * Create a horizontal button row with consistent spacing
	 * @param buttons Array of buttons to arrange
	 * @return Panel containing the buttons
	 */
	public static JPanel createButtonRow(JButton... buttons) {
		JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		buttonRow.setOpaque(false);

		for (JButton button : buttons) {
			buttonRow.add(button);
		}

		return buttonRow;
	}

	/**
	 * Create a vertical button column with consistent spacing
	 * @param buttons Array of buttons to arrange
	 * @return Panel containing the buttons
	 */
	public static JPanel createButtonColumn(JButton... buttons) {
		JPanel buttonColumn = new JPanel();
		buttonColumn.setLayout(new BoxLayout(buttonColumn, BoxLayout.Y_AXIS));
		buttonColumn.setOpaque(false);

		for (int i = 0; i < buttons.length; i++) {
			JButton button = buttons[i];
			button.setAlignmentX(Component.CENTER_ALIGNMENT);
			buttonColumn.add(button);

			// Add spacing between buttons (except after the last one)
			if (i < buttons.length - 1) {
				buttonColumn.add(Box.createVerticalStrut(5));
			}
		}

		return buttonColumn;
	}

	/**
	 * Create a two-column layout panel
	 * @param leftComponent Left side component
	 * @param rightComponent Right side component
	 * @return Panel with two-column layout
	 */
	public static JPanel createTwoColumnLayout(JComponent leftComponent, JComponent rightComponent) {
		JPanel twoColumn = new JPanel(new GridLayout(1, 2, 10, 0));
		twoColumn.setOpaque(false);
		twoColumn.add(leftComponent);
		twoColumn.add(rightComponent);
		return twoColumn;
	}

	/**
	 * Create a grid layout for items
	 * @param components Array of components to arrange
	 * @param columns Number of columns
	 * @return Panel with grid layout
	 */
	public static JPanel createGridLayout(JComponent[] components, int columns) {
		int rows = (int) Math.ceil((double) components.length / columns);
		JPanel grid = new JPanel(new GridLayout(rows, columns, 5, 5));
		grid.setOpaque(false);

		for (JComponent component : components) {
			grid.add(component);
		}

		return grid;
	}

	// ==========================================
	// STATUS INDICATORS
	// ==========================================

	/**
	 * Create a status indicator dot
	 * @param status Status level ("success", "warning", "error", "disabled")
	 * @return Colored status indicator
	 */
	public static JComponent createStatusIndicator(String status) {
		JPanel indicator = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color color;
				switch (status.toLowerCase()) {
					case "success":
						color = SUCCESS_COLOR;
						break;
					case "warning":
						color = WARNING_COLOR;
						break;
					case "error":
						color = ERROR_COLOR;
						break;
					default:
						color = DISABLED_COLOR;
						break;
				}

				g2d.setColor(color);
				g2d.fillOval(2, 2, 8, 8);
				g2d.dispose();
			}
		};

		indicator.setPreferredSize(new Dimension(12, 12));
		indicator.setOpaque(false);
		return indicator;
	}

	/**
	 * Create a labeled status component
	 * @param label Status label text
	 * @param status Status level
	 * @return Component with label and status indicator
	 */
	public static JPanel createLabeledStatus(String label, String status) {
		JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
		statusPanel.setOpaque(false);

		statusPanel.add(createStatusIndicator(status));

		JLabel statusLabel = new JLabel(label);
		statusLabel.setFont(SMALL_FONT);
		statusLabel.setForeground(TEXT_PRIMARY_COLOR);
		statusPanel.add(statusLabel);

		return statusPanel;
	}

	// ==========================================
	// ANIMATION HELPERS
	// ==========================================

	/**
	 * Add a fade-in animation to a component
	 * @param component Component to animate
	 */
	public static void addFadeInAnimation(JComponent component) {
		if (!ExampleConfig.ENABLE_ANIMATIONS) return;

		component.setOpaque(false);
		Timer fadeTimer = new Timer(16, null); // ~60 FPS
		final float[] alpha = {0.0f};

		fadeTimer.addActionListener(e -> {
			alpha[0] += 0.05f;
			if (alpha[0] >= 1.0f) {
				alpha[0] = 1.0f;
				fadeTimer.stop();
				component.setOpaque(true);
			}

			// Apply alpha to component (this is a simplified example)
			component.repaint();
		});

		fadeTimer.start();
	}

	/**
	 * Add hover glow effect to a component (alternative to rainbow hover)
	 * @param component Component to enhance
	 */
	public static void addGlowEffect(JComponent component) {
		if (ExampleConfig.ENABLE_RAINBOW_HOVER) {
			// Use rainbow hover instead
			if (component instanceof JButton) {
				RainbowHoverUtil.applyRainbowHover((JButton) component);
			} else {
				RainbowHoverUtil.applyRainbowHover(component, component.getBackground());
			}
		} else {
			// Simple glow effect fallback
			Color originalBg = component.getBackground();
			Color glowColor = PRIMARY_COLOR.brighter();

			component.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					component.setBackground(glowColor);
				}

				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					component.setBackground(originalBg);
				}
			});
		}
	}

	// ==========================================
	// THEME VARIANTS
	// ==========================================

	/**
	 * Apply dark theme variant to a component
	 * @param component Component to theme
	 */
	public static void applyDarkTheme(JComponent component) {
		component.setBackground(BG_COLOR);
		component.setForeground(TEXT_PRIMARY_COLOR);

		if (component instanceof JButton) {
			JButton button = (JButton) component;
			button.setBackground(GRAPHITE_COLOR);
			button.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
		}
	}

	/**
	 * Apply light theme variant to a component
	 * @param component Component to theme
	 */
	public static void applyLightTheme(JComponent component) {
		Color lightBg = new Color(240, 240, 240);
		Color lightFg = new Color(40, 40, 40);

		component.setBackground(lightBg);
		component.setForeground(lightFg);

		if (component instanceof JButton) {
			JButton button = (JButton) component;
			button.setBackground(new Color(220, 220, 220));
			button.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
		}
	}

	/**
	 * Get theme-appropriate color based on current theme
	 * @param darkColor Color for dark theme
	 * @param lightColor Color for light theme
	 * @return Appropriate color for current theme
	 */
	public static Color getThemedColor(Color darkColor, Color lightColor) {
		// You could add theme detection logic here
		// For now, defaults to dark theme
		return darkColor;
	}
}