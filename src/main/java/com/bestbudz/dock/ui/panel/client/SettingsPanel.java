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

public class SettingsPanel implements UIPanel {

	private final JPanel panel;
	private final Map<String, ToggleButton> toggleMap = new HashMap<>();
	private final SettingsPanelConfig config;

	private static final int BORDER_SIZE = 15;
	private static final int GRID_COLUMNS = 2;
	private static final int GRID_H_GAP = 10;
	private static final int GRID_V_GAP = 10;

	private static final Color TOGGLE_OFF_COLOR = new Color(60, 60, 65);
	private static final Color TOGGLE_ON_COLOR = new Color(76, 175, 80);
	private static final Color TEXT_COLOR = new Color(220, 220, 220);

	private static float globalHue = 0.6f;
	private static final float HUE_SHIFT_AMOUNT = 0.08f;
	private static final float SATURATION = 0.3f;
	private static final float BRIGHTNESS = 0.4f;

	public SettingsPanel() {
		this.config = new SettingsPanelConfig();
		this.panel = createMainPanel();
	}

	private JPanel createMainPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout(0, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
		mainPanel.setPreferredSize(null);
		mainPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		mainPanel.setMinimumSize(new Dimension(0, 0));
		mainPanel.setOpaque(false);

		JScrollPane scrollPane = createScrollPane();
		mainPanel.add(scrollPane, BorderLayout.CENTER);

		return mainPanel;
	}

	private JScrollPane createScrollPane() {
		JPanel settingsGrid = createSettingsGrid();

		JPanel gridWrapper = new JPanel(new BorderLayout());
		gridWrapper.add(settingsGrid, BorderLayout.NORTH);
		gridWrapper.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(gridWrapper);
		scrollPane.setBorder(null);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		scrollPane.getHorizontalScrollBar().setEnabled(false);
		scrollPane.getHorizontalScrollBar().setVisible(false);

		scrollPane.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_RIGHT) {
					e.consume();
				}
			}
		});

		scrollPane.getViewport().addChangeListener(e -> {
			Point viewPosition = scrollPane.getViewport().getViewPosition();
			if (viewPosition.x != 0) {
				viewPosition.x = 0;
				scrollPane.getViewport().setViewPosition(viewPosition);
			}
		});

		return scrollPane;
	}

	private JPanel createSettingsGrid() {
		JPanel grid = new JPanel(new GridLayout(0, GRID_COLUMNS, GRID_H_GAP, GRID_V_GAP));
		grid.setAlignmentX(Component.CENTER_ALIGNMENT);
		grid.setOpaque(false);

		grid.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));

		String[] settingNames = config.getSettingNames();
		Arrays.sort(settingNames);

		for (String settingName : settingNames) {
			addSettingToggle(grid, settingName);
		}

		return grid;
	}

	private void addSettingToggle(JPanel parent, String settingName) {
		boolean initialValue = config.getSettingValue(settingName);
		ToggleButton toggle = new ToggleButton(settingName, initialValue);

		toggle.addActionListener(e -> {

			boolean currentValue = config.getSettingValue(settingName);

			boolean newValue = !currentValue;
			handleSettingChange(settingName, newValue);
		});

		JPanel container = new JPanel();
		container.setLayout(new GridBagLayout());
		container.setOpaque(false);
		container.add(toggle);

		parent.add(container);
		toggleMap.put(settingName, toggle);
	}

	private static class ToggleButton extends JButton {
		private boolean selected;
		private boolean hovering = false;
		private Color currentHoverOverlay = null;
		private Color targetHoverOverlay = null;
		private Timer colorTimer;
		private static final int FADE_OUT_DURATION = 500;
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

			int buttonWidth = 120;
			int buttonHeight = 24;

			setPreferredSize(new Dimension(buttonWidth, buttonHeight));
			setMinimumSize(new Dimension(buttonWidth, buttonHeight));
			setMaximumSize(new Dimension(buttonWidth, buttonHeight));
			setCursor(new Cursor(Cursor.HAND_CURSOR));

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

		public boolean isSelected() {
			return selected;
		}

		public void setSelected(boolean selected) {
			if (this.selected != selected) {
				this.selected = selected;
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Color baseColor = selected ? TOGGLE_ON_COLOR : TOGGLE_OFF_COLOR;

			int padding = 2;
			g2.setColor(baseColor);
			g2.fillRoundRect(padding, padding, getWidth() - padding * 2, getHeight() - padding * 2, 6, 6);

			if (currentHoverOverlay != null) {
				g2.setColor(currentHoverOverlay);
				g2.fillRoundRect(padding, padding, getWidth() - padding * 2, getHeight() - padding * 2, 6, 6);
			}

			g2.setColor(selected ? TOGGLE_ON_COLOR.brighter() : new Color(80, 80, 85));
			g2.setStroke(new BasicStroke(1f));
			g2.drawRoundRect(padding, padding, getWidth() - padding * 2 - 1, getHeight() - padding * 2 - 1, 6, 6);

			g2.setColor(selected ? Color.BLACK : TEXT_COLOR);
			FontMetrics fm = g2.getFontMetrics();
			int textX = (getWidth() - fm.stringWidth(getText())) / 2;
			int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
			g2.drawString(getText(), textX, textY);

			g2.dispose();
		}
	}

	private void handleSettingChange(String settingName, boolean newValue) {
		try {
			config.setSettingValue(settingName, newValue);

			ToggleButton toggle = toggleMap.get(settingName);
			if (toggle != null) {
				toggle.setSelected(newValue);
			}
		} catch (IllegalArgumentException e) {

			System.err.println("Unknown setting: " + settingName);

			ToggleButton toggle = toggleMap.get(settingName);
			if (toggle != null) {
				toggle.setSelected(!newValue);
			}
		}
	}

	private void refreshToggles() {
		for (Map.Entry<String, ToggleButton> entry : toggleMap.entrySet()) {
			String settingName = entry.getKey();
			ToggleButton toggle = entry.getValue();

			try {
				boolean currentValue = config.getSettingValue(settingName);

				if (toggle.isSelected() != currentValue) {
					toggle.setSelected(currentValue);
				}
			} catch (IllegalArgumentException e) {
				System.err.println("Failed to refresh setting: " + settingName);
			}
		}
	}

	public SettingsPanelConfig getConfig() {
		return config;
	}

	@Override
	public void updateText() {

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

	}

	@Override
	public void updateDockText(int index, String text) {

	}
}
