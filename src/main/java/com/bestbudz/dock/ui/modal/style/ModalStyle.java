package com.bestbudz.dock.ui.modal.style;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Enhanced RuneScape-style green/gold theme for dialogue modals
 */
public class ModalStyle {

	// Enhanced RuneScape-inspired color palette
	public static final Color PRIMARY_GREEN = new Color(47, 81, 47);        // Dark forest green
	public static final Color SECONDARY_GREEN = new Color(65, 105, 65);     // Medium green
	public static final Color ACCENT_GOLD = new Color(255, 215, 0);         // Bright gold
	public static final Color DARK_GREEN = new Color(25, 45, 25);           // Very dark green
	public static final Color LIGHT_GREEN = new Color(144, 238, 144);       // Light green accent
	public static final Color HOVER_GREEN = new Color(80, 120, 80);         // Hover state green

	// Background colors - darker for better contrast
	public static final Color BACKGROUND_MAIN = new Color(20, 20, 20);      // Almost black
	public static final Color BACKGROUND_PANEL = new Color(35, 35, 35);     // Dark gray
	public static final Color BACKGROUND_CONTENT = new Color(28, 28, 28);   // Slightly lighter
	public static final Color BACKGROUND_SELECTED = new Color(50, 70, 50);  // Selected item background

	// Text colors optimized for readability
	public static final Color TEXT_PRIMARY = new Color(255, 255, 255);      // Pure white
	public static final Color TEXT_SECONDARY = new Color(200, 220, 200);    // Light gray-green
	public static final Color TEXT_ACCENT = new Color(255, 223, 50);        // Bright yellow-gold
	public static final Color TEXT_NPC = new Color(255, 255, 180);          // Light yellow for NPCs
	public static final Color TEXT_PLAYER = new Color(180, 255, 180);       // Light green for player
	public static final Color TEXT_OPTION = new Color(220, 220, 255);       // Light blue for options

	// Button states with better visual feedback
	public static final Color BUTTON_NORMAL = PRIMARY_GREEN;
	public static final Color BUTTON_HOVER = HOVER_GREEN;
	public static final Color BUTTON_PRESSED = DARK_GREEN;
	public static final Color BUTTON_DISABLED = new Color(60, 60, 60);
	public static final Color BUTTON_OPTION = new Color(60, 80, 60);        // Option buttons

	// Enhanced fonts for better readability
	public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
	public static final Font FONT_CONTENT = new Font("Segoe UI", Font.PLAIN, 13);
	public static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 12);
	public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
	public static final Font FONT_OPTION = new Font("Segoe UI", Font.PLAIN, 12);

	// Enhanced borders with RuneScape styling
	public static final Border BORDER_MAIN = BorderFactory.createCompoundBorder(
		BorderFactory.createLineBorder(ACCENT_GOLD, 2),
		BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(DARK_GREEN, 1),
			BorderFactory.createEmptyBorder(8, 8, 8, 8)
		)
	);

	public static final Border BORDER_PANEL = BorderFactory.createCompoundBorder(
		BorderFactory.createLineBorder(PRIMARY_GREEN, 1),
		BorderFactory.createEmptyBorder(5, 5, 5, 5)
	);

	public static final Border BORDER_CONTENT = BorderFactory.createEmptyBorder(12, 15, 12, 15);

	public static final Border BORDER_OPTION = BorderFactory.createCompoundBorder(
		BorderFactory.createLineBorder(SECONDARY_GREEN, 1),
		BorderFactory.createEmptyBorder(6, 12, 6, 12)
	);

	// Responsive dimensions based on content
	public static final Dimension MODAL_SIZE_SMALL = new Dimension(320, 180);
	public static final Dimension MODAL_SIZE_MEDIUM = new Dimension(420, 280);
	public static final Dimension MODAL_SIZE_LARGE = new Dimension(520, 380);
	public static final Dimension MODAL_SIZE_XLARGE = new Dimension(620, 480);

	// Spacing constants
	public static final int SPACING_TINY = 3;
	public static final int SPACING_SMALL = 6;
	public static final int SPACING_MEDIUM = 10;
	public static final int SPACING_LARGE = 15;
	public static final int SPACING_XLARGE = 20;

	/**
	 * Apply main modal styling to a dialog with enhanced appearance
	 */
	public static void styleMainDialog(JDialog dialog) {
		dialog.setUndecorated(true);
		dialog.setBackground(BACKGROUND_MAIN);

		// Create root panel with enhanced styling
		JPanel rootPanel = new JPanel(new BorderLayout());
		rootPanel.setBackground(BACKGROUND_MAIN);
		rootPanel.setBorder(BORDER_MAIN);

		dialog.setContentPane(rootPanel);

		// Make draggable with visual feedback
		makeDraggable(dialog, rootPanel);

		// Add subtle shadow effect (if supported)
		try {
			dialog.getRootPane().putClientProperty("apple.awt.draggableWindowBackground", false);
		} catch (Exception ignored) {}
	}

	/**
	 * Create enhanced title panel with character info support
	 */
	public static JPanel createTitlePanel(String title) {
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBackground(BACKGROUND_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_GOLD),
			BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_SMALL)
		));

		// Title container with gradient-like effect
		JPanel titleContainer = new JPanel(new BorderLayout());
		titleContainer.setOpaque(false);

		JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
		titleLabel.setFont(FONT_TITLE);
		titleLabel.setForeground(TEXT_ACCENT);

		titleContainer.add(titleLabel, BorderLayout.CENTER);
		panel.add(titleContainer, BorderLayout.CENTER);

		// Enhanced close button
		JButton closeButton = createCloseButton();
		panel.add(closeButton, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Create styled content panel with better spacing
	 */
	public static JPanel createContentPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(BACKGROUND_CONTENT);
		panel.setBorder(BORDER_CONTENT);
		return panel;
	}

	/**
	 * Create enhanced button panel
	 */
	public static JPanel createButtonPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_MEDIUM, SPACING_SMALL));
		panel.setBackground(BACKGROUND_PANEL);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(2, 0, 0, 0, ACCENT_GOLD),
			BorderFactory.createEmptyBorder(SPACING_SMALL, 0, SPACING_SMALL, 0)
		));
		return panel;
	}

	/**
	 * Create styled primary button with enhanced hover effects
	 */
	public static JButton createPrimaryButton(String text) {
		JButton button = new JButton(text);
		styleButton(button, BUTTON_NORMAL, BUTTON_HOVER, BUTTON_PRESSED);
		button.setFont(FONT_BUTTON);
		button.setForeground(TEXT_PRIMARY);
		button.setPreferredSize(new Dimension(100, 30));

		// Add subtle border radius effect
		button.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createRaisedBevelBorder(),
			BorderFactory.createEmptyBorder(4, 16, 4, 16)
		));

		return button;
	}

	/**
	 * Create enhanced option button with proper numbering and styling
	 */
	public static JButton createOptionButton(String text, int optionNumber) {
		// Clean up text and add proper numbering
		String cleanText = text.trim();
		if (!cleanText.startsWith(optionNumber + ".")) {
			cleanText = optionNumber + ". " + cleanText;
		}

		JButton button = new JButton(cleanText);
		styleButton(button, BUTTON_OPTION, BUTTON_HOVER, BUTTON_PRESSED);
		button.setFont(FONT_OPTION);
		button.setForeground(TEXT_OPTION);
		button.setHorizontalAlignment(SwingConstants.LEFT);

		// Responsive sizing based on text length
		FontMetrics fm = button.getFontMetrics(FONT_OPTION);
		int textWidth = fm.stringWidth(cleanText);
		int buttonWidth = Math.max(280, textWidth + 40);

		button.setPreferredSize(new Dimension(buttonWidth, 32));
		button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		button.setBorder(BORDER_OPTION);

		// Add keyboard shortcut indicator
		button.setToolTipText("Press " + optionNumber + " to select");

		return button;
	}

	/**
	 * Create styled text label with HTML support
	 */
	public static JLabel createTextLabel(String text, Color textColor) {
		// Wrap text in HTML for better formatting and word wrapping
		String wrappedText = "<html><div style='text-align: center; width: 280px;'>" +
			escapeHtml(text) + "</div></html>";

		JLabel label = new JLabel(wrappedText);
		label.setFont(FONT_CONTENT);
		label.setForeground(textColor);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createEmptyBorder(SPACING_TINY, 0, SPACING_TINY, 0));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		return label;
	}

	/**
	 * Create character info label with enhanced styling
	 */
	public static JLabel createCharacterLabel(String text, boolean isPlayer) {
		JLabel label = new JLabel(text);
		label.setFont(FONT_SMALL);
		label.setForeground(isPlayer ? TEXT_PLAYER : TEXT_NPC);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createMatteBorder(0, 0, 1, 0, isPlayer ? TEXT_PLAYER : TEXT_NPC),
			BorderFactory.createEmptyBorder(SPACING_SMALL, 0, SPACING_SMALL, 0)
		));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);

		return label;
	}

	/**
	 * Create enhanced input field with better styling
	 */
	public static JTextField createInputField(String placeholder) {
		JTextField field = new JTextField();
		field.setFont(FONT_CONTENT);
		field.setBackground(BACKGROUND_PANEL);
		field.setForeground(TEXT_PRIMARY);
		field.setCaretColor(TEXT_ACCENT);
		field.setSelectionColor(SECONDARY_GREEN);
		field.setSelectedTextColor(TEXT_PRIMARY);

		field.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(PRIMARY_GREEN, 2),
			BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM)
		));

		field.setPreferredSize(new Dimension(250, 30));

		// Enhanced placeholder functionality
		if (placeholder != null && !placeholder.isEmpty()) {
			field.setText(placeholder);
			field.setForeground(TEXT_SECONDARY);

			field.addFocusListener(new java.awt.event.FocusAdapter() {
				public void focusGained(java.awt.event.FocusEvent evt) {
					if (field.getText().equals(placeholder)) {
						field.setText("");
						field.setForeground(TEXT_PRIMARY);
						field.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(ACCENT_GOLD, 2),
							BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM)
						));
					}
				}
				public void focusLost(java.awt.event.FocusEvent evt) {
					if (field.getText().isEmpty()) {
						field.setText(placeholder);
						field.setForeground(TEXT_SECONDARY);
						field.setBorder(BorderFactory.createCompoundBorder(
							BorderFactory.createLineBorder(PRIMARY_GREEN, 2),
							BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM)
						));
					}
				}
			});
		}

		return field;
	}

	/**
	 * Create enhanced item display panel with interaction support
	 */
	/**
	 * FIXED: Enhanced createItemPanel method for ModalStyle.java
	 * Replace the createItemPanel method in your ModalStyle class with this improved version
	 */
	public static JPanel createItemPanel(int itemId, int amount, String itemText) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(BACKGROUND_CONTENT);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(ACCENT_GOLD, 1),
			BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_SMALL, SPACING_SMALL, SPACING_SMALL)
		));

		Dimension panelSize = new Dimension(90, 110);
		panel.setPreferredSize(panelSize);
		panel.setMinimumSize(panelSize);
		panel.setMaximumSize(panelSize);

		// Item icon area (placeholder - would normally show actual item sprite)
		JPanel iconPanel = new JPanel();
		iconPanel.setBackground(BACKGROUND_PANEL);
		iconPanel.setBorder(BorderFactory.createLineBorder(SECONDARY_GREEN, 1));
		iconPanel.setPreferredSize(new Dimension(64, 64));
		iconPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

		// FIXED: Show item ID more prominently when no item name is available
		String displayText = "";
		if (itemText != null && !itemText.trim().isEmpty() && !itemText.equals("Item " + itemId)) {
			displayText = itemText;
		} else {
			displayText = "ID: " + itemId;
		}

		JLabel iconLabel = new JLabel("<html><center>" + displayText + "</center></html>", SwingConstants.CENTER);
		iconLabel.setFont(FONT_SMALL);
		iconLabel.setForeground(TEXT_ACCENT);
		iconPanel.add(iconLabel);

		panel.add(iconPanel);

		// Amount label (if more than 1)
		if (amount > 1) {
			JLabel amountLabel = new JLabel("x" + formatNumber(amount), SwingConstants.CENTER);
			amountLabel.setFont(new Font(FONT_SMALL.getName(), Font.BOLD, FONT_SMALL.getSize()));
			amountLabel.setForeground(TEXT_ACCENT);
			amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			panel.add(Box.createVerticalStrut(2));
			panel.add(amountLabel);
		}

		// FIXED: Better item text handling
		if (itemText != null && !itemText.trim().isEmpty()) {
			// Only show item text as separate label if it's actually descriptive
			if (!itemText.startsWith("ID:") && !itemText.equals("Item " + itemId)) {
				String displayItemText = itemText.length() > 12 ? itemText.substring(0, 9) + "..." : itemText;
				JLabel textLabel = new JLabel(displayItemText, SwingConstants.CENTER);
				textLabel.setFont(FONT_SMALL);
				textLabel.setForeground(TEXT_PRIMARY);
				textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
				textLabel.setToolTipText(itemText); // Full text in tooltip
				panel.add(Box.createVerticalStrut(2));
				panel.add(textLabel);
			}
		}

		return panel;
	}



	/**
	 * Apply enhanced button styling with improved hover effects
	 */
	private static void styleButton(JButton button, Color normal, Color hover, Color pressed) {
		button.setBackground(normal);
		button.setFocusPainted(false);
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setOpaque(true);

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				button.setBackground(hover);
				// Add subtle scale effect
				button.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createRaisedBevelBorder(),
					button.getBorder()
				));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				button.setBackground(normal);
				// Reset border
				if (button.getBorder() instanceof javax.swing.border.CompoundBorder) {
					javax.swing.border.CompoundBorder cb = (javax.swing.border.CompoundBorder) button.getBorder();
					if (cb.getInsideBorder() != null) {
						button.setBorder(cb.getInsideBorder());
					}
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				button.setBackground(pressed);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				button.setBackground(hover);
			}
		});
	}

	/**
	 * Create enhanced close button
	 */
	private static JButton createCloseButton() {
		JButton closeButton = new JButton("×");
		closeButton.setFont(new Font("SansSerif", Font.BOLD, 18));
		closeButton.setForeground(TEXT_ACCENT);
		closeButton.setBackground(BACKGROUND_PANEL);
		closeButton.setBorder(BorderFactory.createEmptyBorder(SPACING_SMALL, SPACING_MEDIUM, SPACING_SMALL, SPACING_MEDIUM));
		closeButton.setFocusPainted(false);
		closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		closeButton.setToolTipText("Close dialogue (ESC)");

		closeButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				closeButton.setBackground(BUTTON_HOVER);
				closeButton.setForeground(TEXT_PRIMARY);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				closeButton.setBackground(BACKGROUND_PANEL);
				closeButton.setForeground(TEXT_ACCENT);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				closeButton.setBackground(BUTTON_PRESSED);
			}
		});

		return closeButton;
	}

	/**
	 * Make dialog draggable with enhanced feedback
	 */
	private static void makeDraggable(JDialog dialog, JPanel panel) {
		final Point[] dragOffset = {null};
		final Color[] originalColor = {panel.getBackground()};

		MouseAdapter dragListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				dragOffset[0] = e.getPoint();
				// Visual feedback when dragging starts
				panel.setBackground(BACKGROUND_SELECTED);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (dragOffset[0] != null) {
					Point location = dialog.getLocation();
					location.translate(e.getX() - dragOffset[0].x, e.getY() - dragOffset[0].y);
					dialog.setLocation(location);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				dragOffset[0] = null;
				// Reset visual feedback
				panel.setBackground(originalColor[0]);
			}
		};

		panel.addMouseListener(dragListener);
		panel.addMouseMotionListener(dragListener);
	}

	/**
	 * Get appropriate modal size based on content complexity
	 */
	public static Dimension getModalSize(int dialogueType, int lineCount, int optionCount) {
		// Calculate base size requirements
		int minWidth = 320;
		int minHeight = 180;

		// Adjust for dialogue type
		switch (dialogueType) {
			case 2: // Options
				minHeight += optionCount * 35 + 20; // Each option needs ~35px
				minWidth = Math.max(minWidth, 400);
				break;
			case 4: // Items
			case 7: // Item creation
			case 8: // Item selection
				minWidth = Math.max(minWidth, 450);
				minHeight = Math.max(minHeight, 250);
				break;
			case 5: // Input
				minHeight = Math.max(minHeight, 200);
				break;
		}

		// Adjust for line count
		minHeight += lineCount * 20;

		// Apply size categories
		if (minWidth <= 350 && minHeight <= 200) {
			return MODAL_SIZE_SMALL;
		} else if (minWidth <= 450 && minHeight <= 300) {
			return MODAL_SIZE_MEDIUM;
		} else if (minWidth <= 550 && minHeight <= 400) {
			return MODAL_SIZE_LARGE;
		} else {
			return MODAL_SIZE_XLARGE;
		}
	}

	/**
	 * Center dialog on screen or parent with smart positioning
	 */
	public static void centerDialog(JDialog dialog, Window parent) {
		if (parent != null && parent.isVisible()) {
			dialog.setLocationRelativeTo(parent);
		} else {
			// Center on screen
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension dialogSize = dialog.getSize();
			int x = (screenSize.width - dialogSize.width) / 2;
			int y = (screenSize.height - dialogSize.height) / 2;
			dialog.setLocation(x, y);
		}
	}

	/**
	 * Position dialog near mouse cursor with screen boundary checking
	 */
	public static void positionNearMouse(JDialog dialog, Point mousePos) {
		if (mousePos == null) {
			centerDialog(dialog, null);
			return;
		}

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension dialogSize = dialog.getSize();

		int x = mousePos.x + 15; // Offset to avoid cursor
		int y = mousePos.y + 15;

		// Keep dialog on screen
		if (x + dialogSize.width > screenSize.width) {
			x = mousePos.x - dialogSize.width - 15;
		}
		if (y + dialogSize.height > screenSize.height) {
			y = mousePos.y - dialogSize.height - 15;
		}

		// Ensure minimum distance from screen edges
		x = Math.max(10, Math.min(x, screenSize.width - dialogSize.width - 10));
		y = Math.max(10, Math.min(y, screenSize.height - dialogSize.height - 10));

		dialog.setLocation(x, y);
	}

	/**
	 * Format numbers with appropriate suffixes (1.2k, 1.5m, etc.)
	 */
	private static String formatNumber(int number) {
		if (number < 1000) {
			return String.valueOf(number);
		} else if (number < 1000000) {
			return String.format("%.1fk", number / 1000.0);
		} else if (number < 1000000000) {
			return String.format("%.1fm", number / 1000000.0);
		} else {
			return String.format("%.1fb", number / 1000000000.0);
		}
	}

	/**
	 * Escape HTML characters for safe display
	 */
	private static String escapeHtml(String text) {
		if (text == null) return "";
		return text.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}

	/**
	 * Create a separator component for visual organization
	 */
	public static Component createSeparator() {
		JSeparator separator = new JSeparator();
		separator.setForeground(SECONDARY_GREEN);
		separator.setBackground(BACKGROUND_CONTENT);
		separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
		return separator;
	}

	/**
	 * Add subtle animation effects to components
	 */
	public static void addHoverEffect(JComponent component, Color normalColor, Color hoverColor) {
		component.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				component.setBackground(hoverColor);
				component.repaint();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				component.setBackground(normalColor);
				component.repaint();
			}
		});
	}

	/**
	 * Create a titled border for grouping related elements
	 */
	public static Border createTitledBorder(String title) {
		return BorderFactory.createTitledBorder(
			BorderFactory.createLineBorder(PRIMARY_GREEN, 1),
			title,
			javax.swing.border.TitledBorder.LEFT,
			javax.swing.border.TitledBorder.TOP,
			FONT_SMALL,
			TEXT_SECONDARY
		);
	}

	/**
	 * Apply consistent spacing to a container
	 */
	public static void applySpacing(Container container, int spacing) {
		if (container.getLayout() instanceof BoxLayout) {
			Component[] components = container.getComponents();
			for (int i = 0; i < components.length - 1; i++) {
				container.add(Box.createVerticalStrut(spacing), i * 2 + 1);
			}
		}
	}

	/**
	 * Create a loading/progress indicator
	 */
	public static JLabel createLoadingIndicator(String text) {
		JLabel label = new JLabel(text + "...", SwingConstants.CENTER);
		label.setFont(FONT_CONTENT);
		label.setForeground(TEXT_SECONDARY);

		// Add a simple animation timer for the dots
		Timer timer = new Timer(500, e -> {
			String currentText = label.getText();
			if (currentText.endsWith("...")) {
				label.setText(text + ".");
			} else if (currentText.endsWith("..")) {
				label.setText(text + "...");
			} else {
				label.setText(text + "..");
			}
		});
		timer.start();

		// Store timer reference so it can be stopped
		label.putClientProperty("timer", timer);

		return label;
	}

	/**
	 * Stop a loading indicator's animation
	 */
	public static void stopLoadingIndicator(JLabel indicator) {
		Timer timer = (Timer) indicator.getClientProperty("timer");
		if (timer != null) {
			timer.stop();
		}
	}

	/**
	 * Create a panel with a subtle gradient effect
	 */
	public static JPanel createGradientPanel(Color startColor, Color endColor) {
		return new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g;
				g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

				GradientPaint gradient = new GradientPaint(
					0, 0, startColor,
					0, getHeight(), endColor
				);
				g2d.setPaint(gradient);
				g2d.fillRect(0, 0, getWidth(), getHeight());
			}
		};
	}
}