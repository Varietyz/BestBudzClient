package com.bestbudz.dock.ui.panel.teleports;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.dock.util.RainbowHoverUtil;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Clean Teleport Panel - Responsive and compact teleportation interface
 *
 * Features:
 * - Clean, compact design with proper spacing
 * - Responsive button layout that adapts to panel width
 * - Reliable dropdown categories with proper click handling
 * - No overlapping components or dead zones
 * - Proper visibility handling on minimize/restore
 */
public class TeleportPanel extends JPanel implements UIPanel, DockTextUpdatable {

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
	private final List<TeleportCategory> categories = new ArrayList<>();
	private int currentPanelWidth = 0;

	public TeleportPanel() {
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

		JLabel titleLabel = new JLabel("Teleport Hub");
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
		// Essential locations (always expanded)
		addCategory("Essential", true, new String[][]{
			{"Home", "115132"},
			{"Fisher", "115159"},
		});

		// Combat areas
		addCategory("Combat Training", false, new String[][]{
			{"Cows", "115147"},
			{"Rock Crabs", "115144"},
			{"Hill Giants", "115145"},
			{"Yaks", "115148"},
			{"Brimhaven Dungeon", "115149"},
			{"Taverley Dungeon", "115150"},
			{"Mercenary Tower", "115151"}
		});

		// Wilderness
		addCategory("Wilderness", false, new String[][]{
			{"Lava Dragons", "115152"},
			{"Mithril Dragons", "115153"},
			{"Wild Resource Area", "115154"},
			{"East Dragons", "115164"},
			{"Mage Bank", "115166"}
		});

		// Boss encounters
		addCategory("Boss Encounters", false, new String[][]{
			{"King Black Dragon", "115167"},
			{"Corporeal Beast", "115170"},
			{"Dagannoth Kings", "115171"},
			{"God Wars", "115172"},
			{"Zulrah", "115173"},
			{"Kraken", "115174"},
			{"Giant Mole", "115175"},
			{"Chaos Elemental", "115176"},
			{"Callisto", "115177"},
			{"Scorpia", "115178"},
			{"Venenatis", "115179"},
			{"Chaos Fanatic", "115180"},
			{"Crazy Archaeologist", "115181"},
			{"Sea Troll Queen", "115168"}
		});

		// Runecrafting altars
		addCategory("Altars", false, new String[][]{
			{"Air Altar", "115133"},
			{"Mind Altar", "115134"},
			{"Water Altar", "115135"},
			{"Earth Altar", "115136"},
			{"Fire Altar", "115137"},
			{"Body Altar", "115138"},
			{"Cosmic Altar", "115139"},
			{"Chaos Altar", "115140"},
			{"Nature Altar", "115141"},
			{"Law Altar", "115142"},
			{"Death Altar", "115143"}
		});

		// Minigames
		addCategory("Minigames", false, new String[][]{
			{"Barrows", "115182"},
			{"Warriors Guild", "115183"},
			{"Duel Arena", "115184"},
			{"Pest Control", "115185"},
			{"Fight Caves", "115186"},
			{"Weapon Game", "115187"},
			{"Clan Wars", "115188"}
		});

		addCategory("Cities", true, new String[][]{
			{"Edgeville", "115162"},
			{"Varrock", "115163"},
			{"Al Kharid", "115146"}
		});

		// Special access
		addCategory("Special Access", false, new String[][]{
			{"Membership Area", "115189"},
			{"Staff Zone", "115190"}
		});

		updateStatsLabel();
	}

	private void addCategory(String name, boolean expanded, String[][] teleports) {
		TeleportCategory category = new TeleportCategory(name, expanded, teleports);
		categories.add(category);
		contentPanel.add(category);
	}

	private void updateStatsLabel() {
		int totalTeleports = categories.stream().mapToInt(c -> c.teleports.length).sum();
		statsLabel.setText(totalTeleports + " locations");
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
		for (TeleportCategory category : categories) {
			category.updateButtonLayout(columns);
		}

		revalidate();
		repaint();
	}

	/**
	 * Individual teleport category with clean dropdown functionality
	 */
	private class TeleportCategory extends JPanel {
		private final String name;
		private final String[][] teleports;
		private boolean expanded;
		private JPanel headerPanel;
		private JPanel buttonPanel;
		private JLabel expandIcon;

		public TeleportCategory(String name, boolean expanded, String[][] teleports) {
			this.name = name;
			this.expanded = expanded;
			this.teleports = teleports;

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

			JLabel countLabel = new JLabel("(" + teleports.length + ")");
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
			for (String[] teleport : teleports) {
				JButton button = createTeleportButton(teleport[0], Integer.parseInt(teleport[1]));
				buttonPanel.add(button);
			}

			add(buttonPanel, BorderLayout.CENTER);
		}

		private JButton createTeleportButton(String name, int interfaceId) {
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
						System.err.println("Error handling teleport button click: " + ex.getMessage());
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
		return "Teleports";
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
		return "sprites/teleport-tab.png";
	}

	public int[] getBlockedInterfaces() {
		return new int[] { 115133, 115134, 115135, 115136, 115137, 115138, 115139, 115140 };
	}
}