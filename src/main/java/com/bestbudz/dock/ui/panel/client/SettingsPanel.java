package com.bestbudz.dock.ui.panel.client;

import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.RainbowHoverUtil;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Settings panel UI component.
 * Provides a scrollable grid of toggle buttons for various game settings.
 */
public class SettingsPanel implements UIPanel {

	private final JPanel panel;
	private final Map<String, ToggleButton> toggleMap = new HashMap<>();
	private final SettingsPanelConfig config;

	// Layout constants
	private static final int BORDER_SIZE = 15;
	private static final int GRID_COLUMNS = 2;
	private static final int GRID_H_GAP = 10;
	private static final int GRID_V_GAP = 10;

	// Modern toggle button colors
	private static final Color TOGGLE_OFF_COLOR = new Color(60, 60, 65);
	private static final Color TOGGLE_ON_COLOR = new Color(76, 175, 80);
	private static final Color TEXT_COLOR = new Color(220, 220, 220);
	private static final Color BACKGROUND_COLOR = new Color(45, 45, 50);

	// Dynamic color generation parameters - more visible with frequent shifts
	private static float globalHue = 0.6f; // Start with a nice blue-ish hue
	private static final float HUE_SHIFT_AMOUNT = 0.08f; // More frequent color shifts
	private static final float SATURATION = 0.3f; // Higher saturation for more visible colors
	private static final float BRIGHTNESS = 0.4f; // Brighter for better visibility

	public SettingsPanel() {
		this.config = new SettingsPanelConfig();
		this.panel = createMainPanel();
	}

	/**
	 * Create and configure the main panel.
	 */
	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
		mainPanel.setPreferredSize(null);
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.setMinimumSize(new Dimension(0, 0));
		mainPanel.setOpaque(false); // Let parent handle background

		JScrollPane scrollPane = createScrollPane();
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		return mainPanel;
	}

	/**
	 * Create the scroll pane containing the settings grid.
	 */
	private JScrollPane createScrollPane() {
		JPanel settingsGrid = createSettingsGrid();

		// Create a wrapper that enforces proper sizing
		JPanel gridWrapper = new JPanel(new BorderLayout());
		gridWrapper.add(settingsGrid, BorderLayout.NORTH); // North instead of center to prevent stretching
		gridWrapper.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(gridWrapper);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		// Disable horizontal scrolling completely
		scrollPane.getHorizontalScrollBar().setEnabled(false);
		scrollPane.getHorizontalScrollBar().setVisible(false);

		// Override the scroll behavior to prevent horizontal movement
		scrollPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
					e.consume(); // Consume left/right arrow key events
				}
			}
		});

		// Ensure the viewport respects the horizontal constraint
		scrollPane.getViewport().addChangeListener(e -> {
			Point viewPosition = scrollPane.getViewport().getViewPosition();
			if (viewPosition.x != 0) {
				viewPosition.x = 0;
				scrollPane.getViewport().setViewPosition(viewPosition);
			}
		});

		return scrollPane;
	}


	/**
	 * Create the grid panel containing all setting toggle buttons.
	 */
	private JPanel createSettingsGrid() {
		JPanel grid = new JPanel(new GridLayout(0, GRID_COLUMNS, GRID_H_GAP, GRID_V_GAP));
		grid.setAlignmentX(Component.CENTER_ALIGNMENT);
		grid.setOpaque(false);
		// Add some right margin to prevent scrollbar overlap
		grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

		// Sort settings alphabetically before adding them
		String[] settingNames = config.getSettingNames();
		Arrays.sort(settingNames);

		for (String settingName : settingNames) {
			addSettingToggle(grid, settingName);
		}

		return grid;
	}

	/**
	 * Create and add a toggle button for a specific setting.
	 */
	private void addSettingToggle(JPanel parent, String settingName) {
		boolean initialValue = config.getSettingValue(settingName);
		ToggleButton toggle = new ToggleButton(settingName, initialValue);

		// Add action listener
		toggle.addActionListener(e -> {
			// Get current value from config (source of truth)
			boolean currentValue = config.getSettingValue(settingName);
			// Toggle to opposite
			boolean newValue = !currentValue;
			handleSettingChange(settingName, newValue);
		});

		// Wrap in a container panel to handle bounds
		JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());
		container.setOpaque(false);
		container.add(toggle);

		parent.add(container);
		toggleMap.put(settingName, toggle);
	}

	/**
	 * Generate the next hover color by shifting through the HSB color space
	 */
	private static synchronized Color getNextHoverColor() {
		// Create color from HSB values
		Color color = Color.getHSBColor(globalHue, SATURATION, BRIGHTNESS);

		// Shift hue for next time
		globalHue += HUE_SHIFT_AMOUNT;
		if (globalHue >= 1.0f) {
			globalHue -= 1.0f; // Wrap around the color wheel
		}

		return color;
	}

	/**
	 * Custom toggle button with separate toggle state and hover effects
	 */
	private static class ToggleButton extends JButton {
		private boolean selected;
		private boolean hovering = false;
		private Color currentHoverOverlay = null; // null means no hover overlay
		private Color targetHoverOverlay = null;
		private Timer colorTimer;
		private static final int FADE_OUT_DURATION = 500; // 0.5 seconds for fade out
		private static final int COLOR_ANIMATION_FPS = 60;

		public ToggleButton(String text, boolean selected) {
			super(text);
			this.selected = selected;

			setFont(new Font("Arial", Font.PLAIN, 12));
			setForeground(TEXT_COLOR);
			setFocusPainted(false);
			setBorderPainted(false);
			setContentAreaFilled(false);
			setOpaque(false);

			// Make buttons uniform width for better grid alignment
			int buttonWidth = 120;
			int buttonHeight = 24;

			setPreferredSize(new Dimension(buttonWidth, buttonHeight));
			setMinimumSize(new Dimension(buttonWidth, buttonHeight));
			setMaximumSize(new Dimension(buttonWidth, buttonHeight));
			setCursor(new Cursor(Cursor.HAND_CURSOR));

			// Add hover effects - separate from toggle state
			RainbowHoverUtil.applyRainbowHover(this, new RainbowHoverUtil.HoverOverlayRenderer() {
				@Override
				public void setHoverOverlay(Color overlayColor) {
					currentHoverOverlay = overlayColor;
				}

				@Override
				public void repaintComponent() {
					repaint();
				}
			});
		}

		private void animateHoverFadeOut() {
			if (colorTimer != null && colorTimer.isRunning()) {
				colorTimer.stop();
			}

			if (currentHoverOverlay == null) {
				return; // Nothing to fade out
			}

			Color startColor = currentHoverOverlay;
			long startTime = System.currentTimeMillis();

			colorTimer = new Timer(1000 / COLOR_ANIMATION_FPS, null);
			colorTimer.addActionListener(e -> {
				long elapsed = System.currentTimeMillis() - startTime;
				float progress = Math.min(1.0f, (float) elapsed / FADE_OUT_DURATION);

				// Smooth easing function (ease-out cubic)
				progress = (float) (1 - Math.pow(1 - progress, 3));

				// Fade out the overlay by reducing alpha
				int alpha = (int) ((1.0f - progress) * 255);
				if (alpha <= 0) {
					currentHoverOverlay = null;
					colorTimer.stop();
				} else {
					// Create new color with faded alpha
					currentHoverOverlay = new Color(
						startColor.getRed(),
						startColor.getGreen(),
						startColor.getBlue(),
						alpha
					);
				}
				repaint();
			});

			colorTimer.start();
		}

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			if (this.selected != selected) {
				this.selected = selected;
				repaint(); // Simple repaint - no color state management needed
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Draw base color based purely on selection state
			Color baseColor = selected ? TOGGLE_ON_COLOR : TOGGLE_OFF_COLOR;

			int padding = 2;
			g2.setColor(baseColor);
			g2.fillRoundRect(padding, padding, getWidth() - padding * 2, getHeight() - padding * 2, 6, 6);

			// Draw hover overlay if present
			if (currentHoverOverlay != null) {
				g2.setColor(currentHoverOverlay);
				g2.fillRoundRect(padding, padding, getWidth() - padding * 2, getHeight() - padding * 2, 6, 6);
			}

			// Draw border
			g2.setColor(selected ? TOGGLE_ON_COLOR.brighter() : new Color(80, 80, 85));
			g2.setStroke(new BasicStroke(1f));
			g2.drawRoundRect(padding, padding, getWidth() - padding * 2 - 1, getHeight() - padding * 2 - 1, 6, 6);

			// Draw text
			g2.setColor(selected ? Color.BLACK : TEXT_COLOR);
			FontMetrics fm = g2.getFontMetrics();
			int textX = (getWidth() - fm.stringWidth(getText())) / 2;
			int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
			g2.drawString(getText(), textX, textY);

			g2.dispose();
		}
	}

	/**
	 * Handle setting value change from UI interaction.
	 */
	private void handleSettingChange(String settingName, boolean newValue) {
		try {
			config.setSettingValue(settingName, newValue);
			// Update the toggle button's visual state immediately
			ToggleButton toggle = toggleMap.get(settingName);
			if (toggle != null) {
				toggle.setSelected(newValue);
			}
		} catch (IllegalArgumentException e) {
			// This shouldn't happen with proper initialization, but handle gracefully
			System.err.println("Unknown setting: " + settingName);
			// Revert toggle state
			ToggleButton toggle = toggleMap.get(settingName);
			if (toggle != null) {
				toggle.setSelected(!newValue);
			}
		}
	}

	/**
	 * Refresh all toggle states from the current config values.
	 * This is useful when settings might have been changed externally.
	 */
	private void refreshToggles() {
		for (Map.Entry<String, ToggleButton> entry : toggleMap.entrySet()) {
			String settingName = entry.getKey();
			ToggleButton toggle = entry.getValue();

			try {
				boolean currentValue = config.getSettingValue(settingName);
				// Only update if different to avoid unnecessary events
				if (toggle.isSelected() != currentValue) {
					toggle.setSelected(currentValue);
				}
			} catch (IllegalArgumentException e) {
				System.err.println("Failed to refresh setting: " + settingName);
			}
		}
	}

	/**
	 * Get the configuration manager for this panel.
	 * Useful for external access to settings.
	 */
	public SettingsPanelConfig getConfig() {
		return config;
	}

	// UIPanel interface implementation

	@Override
	public void updateText() {
		// No text updates needed for this panel
	}

	@Override
	public String getPanelID() {
		return "Settings";
	}

	@Override
	public Component getComponent() {
		return panel;
	}

	@Override
	public void onActivate() {
		refreshToggles();
	}

	@Override
	public void onDeactivate() {
		// Optionally force save when deactivating
		// config.saveImmediately();
	}

	@Override
	public void updateDockText(int index, String text) {
		// Not used by this panel
	}
}