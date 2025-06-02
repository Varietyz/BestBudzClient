package com.bestbudz.dock.ui.panel.shops;

import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Clean Shop Panel - Responsive and compact shop interface
 *
 * Features:
 * - Clean, compact design with proper spacing
 * - Responsive button layout that adapts to panel width
 * - Reliable dropdown categories with proper click handling
 * - No overlapping components or dead zones
 * - Proper visibility handling on minimize/restore
 */
public class ShopPanel extends JPanel implements UIPanel, DockTextUpdatable {

	// Clean color scheme
	private static final Color CARD_BG = new Color(35, 35, 35);
	private static final Color HEADER_BG = new Color(45, 45, 45);
	private static final Color ACCENT = new Color(185, 160, 66);
	private static final Color TEXT_PRIMARY = new Color(255, 255, 255);
	private static final Color TEXT_SECONDARY = new Color(170, 170, 170);
	private static final Color BORDER = new Color(60, 60, 60);

	private JPanel headerPanel;
	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel statsLabel;
	private final List<ShopCategory> categories = new ArrayList<>();
	private int currentPanelWidth = 0;

	public ShopPanel() {
		setLayout(new BorderLayout());
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		initializeComponents();
		setupCategories();
		setupResponsiveLayout();
	}

	private void initializeComponents() {
		// Header with title and stats
		headerPanel = new JPanel(new BorderLayout()) {
			@Override
			protected void paintComponent(Graphics g) {
				// Ensure solid background to prevent bleed-through
				Graphics2D g2 = (Graphics2D) g.create();
				try {
					g2.setColor(getBackground());
					g2.fillRect(0, 0, getWidth(), getHeight());
				} finally {
					g2.dispose();
				}
				super.paintComponent(g);
			}
		};
		headerPanel.setBackground(HEADER_BG);
		headerPanel.setOpaque(true);
		headerPanel.setBorder(new EmptyBorder(8, 12, 8, 12));

		JLabel titleLabel = new JLabel("Shop Hub");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(TEXT_PRIMARY);
		titleLabel.setOpaque(false);

		statsLabel = new JLabel();
		statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		statsLabel.setForeground(TEXT_SECONDARY);
		statsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		statsLabel.setOpaque(false);

		headerPanel.add(titleLabel, BorderLayout.WEST);
		headerPanel.add(statsLabel, BorderLayout.EAST);

		// Content area with proper scrolling
		contentPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				// Ensure solid background
				Graphics2D g2 = (Graphics2D) g.create();
				try {
					g2.setColor(getBackground());
					g2.fillRect(0, 0, getWidth(), getHeight());
				} finally {
					g2.dispose();
				}
				super.paintComponent(g);
			}
		};
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		contentPanel.setOpaque(true);
		contentPanel.setBorder(new EmptyBorder(4, 8, 8, 8));

		scrollPane = new JScrollPane(contentPanel);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(true);
		scrollPane.getViewport().setBackground(ColorConfig.MAIN_FRAME_COLOR);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);

		// Add components
		add(headerPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}

	private void setupCategories() {
		// Shops
		addCategory("Shops", true, new String[][]{
			{"General Store", "115082"},
			{"Pack store", "115083"},
			{"Professioning shop", "115084"},
			{"BankStanding shop", "115085"},
			{"THC-hempistry shop", "115086"},
			{"Close-combat shop", "115087"},
			{"Sagittarius shop", "115088"},
			{"Mages shop", "115089"},
			{"Pure shop", "115090"},
			{"Fashion shop", "115091"},
			{"Profession capes", "115092"},
			{"Advancement capes", "115093"}
		});

		// Point Shops
		addCategory("Point Shops", false, new String[][]{
			{"Chill points", "115097"},
			{"Weed protect points", "115098"},
			{"Graceful marks", "115099"},
			{"Achievement points", "115100"},
			{"Advance points", "115101"},
			{"Mercenary points", "115102"},
			{"Bounty points", "115103"}
		});

		updateStatsLabel();
	}


	private void addCategory(String name, boolean expanded, String[][] shops) {
		ShopCategory category = new ShopCategory(name, expanded, shops);
		categories.add(category);
		contentPanel.add(category);
	}

	private void updateStatsLabel() {
		int totalShops = categories.stream().mapToInt(c -> c.shops.length).sum();
		statsLabel.setText(totalShops + " shops");
	}

	private void setupResponsiveLayout() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				int newWidth = getWidth();
				if (newWidth != currentPanelWidth && newWidth > 0) {
					currentPanelWidth = newWidth;
					updateButtonLayouts();
				}
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// Fix for panels being hidden after minimize/restore
				setVisible(true);
				updateButtonLayouts();
				revalidate();
				repaint();
			}
		});
	}

	private void updateButtonLayouts() {
		if (currentPanelWidth <= 0) return;

		// Calculate columns based on available width
		int availableWidth = currentPanelWidth - 32; // Account for padding and scrollbar
		int buttonWidth = 90;
		int spacing = 4;
		int columns = Math.max(1, (availableWidth + spacing) / (buttonWidth + spacing));
		columns = Math.min(columns, 4); // Maximum 4 columns

		// Update all category button layouts
		for (ShopCategory category : categories) {
			category.updateButtonLayout(columns);
		}

		revalidate();
		repaint();
	}

	/**
	 * Individual shop category with clean dropdown functionality
	 */
	private class ShopCategory extends JPanel {
		private final String name;
		private final String[][] shops;
		private boolean expanded;
		private JPanel headerPanel;
		private JPanel buttonPanel;
		private JLabel expandIcon;

		public ShopCategory(String name, boolean expanded, String[][] shops) {
			this.name = name;
			this.expanded = expanded;
			this.shops = shops;

			setLayout(new BorderLayout());
			setBackground(CARD_BG);
			setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(BORDER, 1),
				new EmptyBorder(0, 0, 2, 0)
			));
			setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

			createHeader();
			createButtonPanel();
			updateVisibility();
		}

		private void createHeader() {
			headerPanel = new JPanel(new BorderLayout()) {
				@Override
				protected void paintComponent(Graphics g) {
					// Ensure proper background painting without transparency issues
					Graphics2D g2 = (Graphics2D) g.create();
					try {
						g2.setColor(getBackground());
						g2.fillRect(0, 0, getWidth(), getHeight());
					} finally {
						g2.dispose();
					}
					super.paintComponent(g);
				}
			};
			headerPanel.setBackground(HEADER_BG);
			headerPanel.setOpaque(true);
			headerPanel.setBorder(new EmptyBorder(6, 8, 6, 8));
			headerPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			JLabel nameLabel = new JLabel(name);
			nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
			nameLabel.setForeground(TEXT_PRIMARY);
			nameLabel.setOpaque(false);

			JLabel countLabel = new JLabel("(" + shops.length + ")");
			countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
			countLabel.setForeground(TEXT_SECONDARY);
			countLabel.setOpaque(false);

			expandIcon = new JLabel(expanded ? "▼" : "—");
			expandIcon.setFont(new Font("Segoe UI", Font.BOLD, 10));
			expandIcon.setForeground(ACCENT);
			expandIcon.setOpaque(false);

			JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			leftPanel.setOpaque(false);
			leftPanel.add(nameLabel);
			leftPanel.add(Box.createHorizontalStrut(6));
			leftPanel.add(countLabel);

			headerPanel.add(leftPanel, BorderLayout.WEST);
			headerPanel.add(expandIcon, BorderLayout.EAST);

			// Simplified click handler without complex hover effects
			MouseAdapter clickHandler = new MouseAdapter() {
				private Color originalBg = HEADER_BG;

				@Override
				public void mouseClicked(MouseEvent e) {
					// Prevent event propagation that might switch panels
					e.consume();
					toggleExpanded();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					headerPanel.setBackground(ColorConfig.HOVER_COLOR);
					headerPanel.repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					headerPanel.setBackground(originalBg);
					headerPanel.repaint();
				}
			};

			headerPanel.addMouseListener(clickHandler);
			add(headerPanel, BorderLayout.NORTH);
		}

		private void createButtonPanel() {
			buttonPanel = new JPanel();
			buttonPanel.setBackground(CARD_BG);
			buttonPanel.setBorder(new EmptyBorder(4, 6, 6, 6));

			// Create buttons
			for (String[] shop : shops) {
				JButton button = createShopButton(shop[0], Integer.parseInt(shop[1]));
				buttonPanel.add(button);
			}

			add(buttonPanel, BorderLayout.CENTER);
		}

		private JButton createShopButton(String name, int interfaceId) {
			JButton button = new JButton(name) {
				@Override
				protected void paintComponent(Graphics g) {
					// Ensure we paint our own background without interference
					Graphics2D g2 = (Graphics2D) g.create();
					try {
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

						// Paint background based on state
						Color bgColor = getModel().isRollover() ? ACCENT : ColorConfig.GRAPHITE_COLOR;
						g2.setColor(bgColor);
						g2.fillRect(0, 0, getWidth(), getHeight());

						// Paint border
						g2.setColor(BORDER);
						g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

					} finally {
						g2.dispose();
					}
					super.paintComponent(g);
				}
			};

			button.setFont(new Font("Segoe UI", Font.PLAIN, 10));
			button.setForeground(TEXT_PRIMARY);
			button.setOpaque(true);
			button.setContentAreaFilled(false);
			button.setBorderPainted(false);
			button.setFocusPainted(false);
			button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			button.setPreferredSize(new Dimension(85, 24));
			button.setMinimumSize(new Dimension(85, 24));
			button.setMaximumSize(new Dimension(85, 24));

			// Simple click handling without external rainbow effects
			button.addActionListener(e -> {
				// Ensure we stay on EDT and don't trigger panel switches
				SwingUtilities.invokeLater(() -> {
					try {
						ButtonHandler.createButtonListener(interfaceId).actionPerformed(e);
					} catch (Exception ex) {
						System.err.println("Error handling shop button click: " + ex.getMessage());
					}
				});
			});

			return button;
		}

		public void updateButtonLayout(int columns) {
			if (buttonPanel != null) {
				buttonPanel.setLayout(new GridLayout(0, columns, 4, 4));
				buttonPanel.revalidate();
			}
		}

		private void toggleExpanded() {
			expanded = !expanded;
			expandIcon.setText(expanded ? "▼" : "—");
			updateVisibility();
		}

		private void updateVisibility() {
			if (buttonPanel != null) {
				buttonPanel.setVisible(expanded);
			}

			// Update preferred size
			if (expanded) {
				setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
			} else {
				setMaximumSize(new Dimension(Integer.MAX_VALUE, headerPanel.getPreferredSize().height));
			}

			revalidate();
			repaint();
		}
	}

	// UIPanel interface methods
	@Override
	public String getPanelID() {
		return "Shops";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (!Client.loggedIn) return;

		// Ensure this panel is properly shown and focused
		SwingUtilities.invokeLater(() -> {
			setVisible(true);
			setOpaque(true);

			// Bring this panel to front to prevent other panels showing through
			if (getParent() != null) {
				getParent().setComponentZOrder(this, 0);
			}

			updateButtonLayouts();
			scrollPane.getVerticalScrollBar().setValue(0);
			revalidate();
			repaint();
		});
	}

	@Override
	public void onDeactivate() {
		// Clean deactivation
	}

	@Override
	public void updateText() {
		repaint();
	}

	@Override
	public void updateDockText(int index, String text) {
		if (!Client.loggedIn) return;
		// Handle dock text updates if needed
	}

	public String getPanelIconPath() {
		return "sprites/shop-tab.png";
	}

	public int[] getBlockedInterfaces() {
		return new int[] { 115133, 115134, 115135, 115136, 115137, 115138, 115139, 115140 };
	}
}