package com.bestbudz.dock.frame;

import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TogglePreview {
	private static final int SIDE_TAB_WIDTH = 6;
	private static final Color SIDE_TAB_COLOR = new Color(70, 70, 70, 100);
	private static final Color SIDE_TAB_HOVER_COLOR = new Color(90, 90, 90, 140);
	private static final Color PREVIEW_WINDOW_COLOR = new Color(50, 50, 50, 240);

	public static class SingleHoverTab extends JComponent {
		private final List<JScrollPane> scrollPanes;
		private final List<JPanel> toggleBars;
		private boolean isHovered = false;
		private PreviewWindow previewWindow;
		private Timer showTimer;
		private Timer hideTimer;

		public SingleHoverTab(List<JScrollPane> scrollPanes, List<JPanel> toggleBars) {
			this.scrollPanes = scrollPanes;
			this.toggleBars = toggleBars;

			setPreferredSize(new Dimension(SIDE_TAB_WIDTH, 50));
			setOpaque(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			setupMouseListeners();
			setupTimers();
		}

		private void setupMouseListeners() {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					isHovered = true;
					repaint();

					if (hideTimer.isRunning()) {
						hideTimer.stop();
					}

					showTimer.restart();
				}

				@Override
				public void mouseExited(MouseEvent e) {

					if (previewWindow != null && previewWindow.isMouseOver(e.getLocationOnScreen())) {
						return;
					}

					isHovered = false;
					repaint();

					if (showTimer.isRunning()) {
						showTimer.stop();
					}

					hideTimer.restart();
				}
			});
		}

		private void setupTimers() {
			showTimer = new Timer(150, e -> showPreview());
			showTimer.setRepeats(false);

			hideTimer = new Timer(500, e -> {

				if (previewWindow != null) {
					Point mousePos = MouseInfo.getPointerInfo().getLocation();
					if (!isMouseOverTabOrPreview(mousePos)) {
						hidePreview();
					}
				}
			});
			hideTimer.setRepeats(false);
		}

		private boolean isMouseOverTabOrPreview(Point mousePos) {

			Rectangle tabBounds = new Rectangle(getLocationOnScreen(), getSize());
			if (tabBounds.contains(mousePos)) {
				return true;
			}

			if (previewWindow != null && previewWindow.isMouseOver(mousePos)) {
				return true;
			}

			return false;
		}

		private void showPreview() {
			if (previewWindow != null) {
				previewWindow.dispose();
			}

			List<JToggleButton> allHiddenButtons = getAllHiddenButtons();
			if (!allHiddenButtons.isEmpty()) {
				previewWindow = new PreviewWindow(this, allHiddenButtons);
				previewWindow.show();
			}
		}

		public void hidePreview() {
			if (previewWindow != null) {
				previewWindow.hide();
				previewWindow = null;
			}
		}

		private List<JToggleButton> getAllHiddenButtons() {
			List<JToggleButton> hiddenButtons = new ArrayList<>();

			for (int i = 0; i < scrollPanes.size(); i++) {
				JScrollPane scrollPane = scrollPanes.get(i);
				JPanel toggleBar = toggleBars.get(i);

				List<JToggleButton> allButtons = getAllButtonsFromContainer(toggleBar);

				List<JToggleButton> visibleButtons = getVisibleButtons(scrollPane);

				for (JToggleButton button : allButtons) {
					if (!visibleButtons.contains(button)) {
						hiddenButtons.add(button);
					}
				}
			}

			return hiddenButtons;
		}

		private List<JToggleButton> getAllButtonsFromContainer(JPanel toggleBar) {
			List<JToggleButton> buttons = new ArrayList<>();

			UIDockFrame.toggleButtons.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.forEach(entry -> {
					JToggleButton button = entry.getValue();
					Container parent = button.getParent();

					if (parent != null && (parent == toggleBar || SwingUtilities.isDescendingFrom(parent, toggleBar))) {
						buttons.add(button);
					}
				});

			return buttons;
		}

		private List<JToggleButton> getVisibleButtons(JScrollPane scrollPane) {
			List<JToggleButton> visibleButtons = new ArrayList<>();

			Component view = scrollPane.getViewport().getView();
			if (!(view instanceof JPanel)) return visibleButtons;

			JPanel container = (JPanel) view;
			Rectangle viewRect = scrollPane.getViewport().getViewRect();

			for (Component comp : container.getComponents()) {
				if (comp instanceof JToggleButton) {
					JToggleButton button = (JToggleButton) comp;
					Rectangle buttonBounds = button.getBounds();

					if (viewRect.intersects(buttonBounds)) {
						visibleButtons.add(button);
					}
				}
			}

			return visibleButtons;
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			Color tabColor = isHovered ? SIDE_TAB_HOVER_COLOR : SIDE_TAB_COLOR;
			g2d.setColor(tabColor);

			int width = getWidth();
			int height = getHeight();

			g2d.fillRoundRect(0, 0, width, height, 3, 3);

			g2d.setColor(new Color(180, 180, 180, 120));
			int centerX = width / 2;
			int centerY = height / 2;

			g2d.fillOval(centerX - 1, centerY - 4, 1, 1);
			g2d.fillOval(centerX - 1, centerY - 1, 1, 1);
			g2d.fillOval(centerX - 1, centerY + 2, 1, 1);

			g2d.dispose();
		}
	}

	public static class PreviewWindow {
		private final JWindow window;
		private final SingleHoverTab parentTab;
		private final List<JToggleButton> buttons;

		public PreviewWindow(SingleHoverTab parentTab, List<JToggleButton> buttons) {
			this.parentTab = parentTab;
			this.buttons = buttons;
			this.window = new JWindow();

			setupWindow();
		}

		private void setupWindow() {
			window.setAlwaysOnTop(true);
			window.setFocusable(false);

			JPanel contentPanel = new JPanel();
			contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
			contentPanel.setBackground(PREVIEW_WINDOW_COLOR);
			contentPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(100, 100, 100), 1),
				BorderFactory.createEmptyBorder(2, 2, 2, 2)
			));

			for (JToggleButton originalButton : buttons) {
				JButton previewButton = createPreviewButton(originalButton);
				contentPanel.add(previewButton);
			}

			window.add(contentPanel);
			window.pack();

			contentPanel.addMouseWheelListener(new MouseWheelListener() {
				private long lastScrollTime = 0;
				private final int SCROLL_DELAY = 150;

				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					long now = System.currentTimeMillis();
					if (now - lastScrollTime < SCROLL_DELAY) return;
					lastScrollTime = now;

					if (buttons.isEmpty()) return;

					JToggleButton currentSelected = null;
					for (JToggleButton button : buttons) {
						if (button.isSelected()) {
							currentSelected = button;
							break;
						}
					}

					if (currentSelected == null) {
						currentSelected = buttons.get(0);
					}

					List<String> allButtonIds = new ArrayList<>();
					UIDockFrame.toggleButtons.entrySet().stream()
						.sorted(Map.Entry.comparingByKey())
						.forEach(entry -> allButtonIds.add(entry.getKey()));

					String currentId = getButtonId(currentSelected);
					int currentIndex = allButtonIds.indexOf(currentId);
					if (currentIndex == -1) return;

					int scrollDirection = e.getWheelRotation() > 0 ? 1 : -1;
					int nextIndex = (currentIndex + scrollDirection + allButtonIds.size()) % allButtonIds.size();
					String nextButtonId = allButtonIds.get(nextIndex);

					centerButtonInCircularView(nextButtonId);

					e.consume();
				}
			});

			contentPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseExited(MouseEvent e) {

					Point mousePos = e.getLocationOnScreen();
					if (!isMouseOver(mousePos) && !isTabUnder(mousePos)) {
						parentTab.hideTimer.restart();
					}
				}

				@Override
				public void mouseEntered(MouseEvent e) {

					if (parentTab.hideTimer.isRunning()) {
						parentTab.hideTimer.stop();
					}
				}
			});
		}

		private boolean isTabUnder(Point mousePos) {
			Rectangle tabBounds = new Rectangle(parentTab.getLocationOnScreen(), parentTab.getSize());
			return tabBounds.contains(mousePos);
		}

		public boolean isMouseOver(Point screenPoint) {
			if (!window.isVisible()) return false;
			Rectangle windowBounds = new Rectangle(window.getLocationOnScreen(), window.getSize());
			return windowBounds.contains(screenPoint);
		}

		private JButton createPreviewButton(JToggleButton originalButton) {
			JButton previewButton = new JButton(originalButton.getText());
			previewButton.setPreferredSize(new Dimension(70, 16));
			previewButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
			previewButton.setFont(originalButton.getFont().deriveFont(7f));
			previewButton.setForeground(Color.WHITE);
			previewButton.setBackground(new Color(80, 80, 80));
			previewButton.setFocusPainted(false);
			previewButton.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 2));

			if (originalButton.isSelected()) {
				previewButton.setBackground(new Color(90, 130, 180));
				previewButton.setFont(previewButton.getFont().deriveFont(Font.BOLD));
			}

			previewButton.addActionListener(e -> {

				originalButton.doClick();

				String buttonId = getButtonId(originalButton);
				if (buttonId != null) {

					centerButtonInCircularView(buttonId);
				}

				hide();
			});

			previewButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent e) {
					if (!originalButton.isSelected()) {
						previewButton.setBackground(new Color(90, 90, 90));
					}

					if (parentTab.hideTimer.isRunning()) {
						parentTab.hideTimer.stop();
					}
				}

				@Override
				public void mouseExited(MouseEvent e) {
					if (!originalButton.isSelected()) {
						previewButton.setBackground(new Color(80, 80, 80));
					}
				}
			});

			return previewButton;
		}

		private String getButtonId(JToggleButton button) {
			String text = button.getText();
			if (text != null && text.length() > 2) {

				return text.substring(2);
			}
			return text != null ? text : "";
		}

		private void centerButtonInCircularView(String buttonId) {

			JToggleButton targetButton = UIDockFrame.toggleButtons.get(buttonId);
			if (targetButton == null) return;

			SwingUtilities.invokeLater(() -> {
				UIDockHelper.centerToggleInCircularView(buttonId);
			});
		}

		public void show() {
			Point tabLocation = parentTab.getLocationOnScreen();
			Dimension windowSize = window.getSize();

			int x = tabLocation.x - windowSize.width;
			int y = tabLocation.y + (parentTab.getHeight() - windowSize.height) / 2;

			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			Rectangle screenBounds = ge.getMaximumWindowBounds();

			x = Math.max(0, Math.min(x, screenBounds.width - windowSize.width));
			y = Math.max(0, Math.min(y, screenBounds.height - windowSize.height));

			window.setLocation(x, y);
			window.setVisible(true);
		}

		public void hide() {
			window.setVisible(false);
		}

		public void dispose() {
			window.dispose();
		}
	}

	public static class ScrollPaneWithTabs extends JPanel {
		private final JScrollPane scrollPane;

		public ScrollPaneWithTabs(JScrollPane scrollPane, JPanel toggleBar) {
			this.scrollPane = scrollPane;

			setLayout(new BorderLayout());
			setOpaque(false);
			add(scrollPane, BorderLayout.CENTER);
		}

		public JScrollPane getScrollPane() {
			return scrollPane;
		}
	}

	public static class ToggleBarWithSinglePreview extends JPanel {
		private final SingleHoverTab hoverTab;

		public ToggleBarWithSinglePreview(JPanel existingToggleBarsContainer, List<JScrollPane> scrollPanes, List<JPanel> toggleBars) {
			this.hoverTab = new SingleHoverTab(scrollPanes, toggleBars);

			setLayout(new BorderLayout());
			setOpaque(false);

			add(existingToggleBarsContainer, BorderLayout.CENTER);
			add(hoverTab, BorderLayout.EAST);

			hoverTab.setVisible(true);
		}

		public SingleHoverTab getHoverTab() {
			return hoverTab;
		}
	}
}
