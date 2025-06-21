package com.bestbudz.dock.frame;

import com.bestbudz.dock.ui.manager.UIDockLayoutState;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.DockBlocker;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.engine.config.SettingsConfig;

import com.bestbudz.engine.core.Client;
import java.awt.event.ActionEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class UIDockHelper extends UIDockFrame
{
	private static final int VIEWPORT_WIDTH = 300;
	private static final int CENTER_POSITION = 150;

	public UIDockHelper(JFrame owner)
	{
		super(owner);
	}

	public static void registerPanel(UIDockFrame frame, final UIPanel uiPanel) {
		final String id = uiPanel.getPanelID();
		final Component panel = uiPanel.getComponent();

		frame.getPanelMap().put(id, panel);
		frame.getPanelManager().register(uiPanel);

		if (panel instanceof UIPanel) {
			for (int idx : ((UIPanel) panel).getBlockedInterfaces()) {
				DockBlocker.register(idx);
			}
		}

		ToggleBarHelper.createToggleButton(frame, uiPanel);
	}

	public static void loadDockPanelLayout(UIDockFrame frame) {
		UIDockLayoutState.load(frame);
		setupCircularScrollBars(frame);
	}

	public static void showPanelTop(UIDockFrame frame, String id) {
		frame.setTopVisiblePanelID(id);
		frame.getPanelPositions().put(id, "top");

		Component panel = frame.getPanelMap().get(id);
		if (frame.getBottomStack().isAncestorOf(panel)) {
			frame.getBottomStack().remove(panel);
			frame.getTopStack().add(panel, id);
		}

		switchPanel(frame, frame.getTopStack(), frame.getTopLayout(), id);
		frame.updateToggles();
		reflowToggleButtons();
		centerToggleInCircularView(id);
	}

	public static void showPanelBottom(UIDockFrame frame, String id) {
		frame.setBottomVisiblePanelID(id);
		frame.getPanelPositions().put(id, "bottom");

		Component panel = frame.getPanelMap().get(id);
		if (frame.getTopStack().isAncestorOf(panel)) {
			frame.getTopStack().remove(panel);
			frame.getBottomStack().add(panel, id);
		}

		switchPanel(frame, frame.getBottomStack(), frame.getBottomLayout(), id);
		frame.updateToggles();
		reflowToggleButtons();
		centerToggleInCircularView(id);
	}

	public static void switchPanel(UIDockFrame frame, JPanel stack, CardLayout layout, String id) {
		for (UIPanel p : frame.getPanelManager().getAllPanels()) {
			if (p.getPanelID().equals(id)) {
				p.onActivate();
			} else {
				p.onDeactivate();
			}
		}
		layout.show(stack, id);
		stack.revalidate();
		stack.repaint();
	}

	public static void updateToggles(UIDockFrame frame) {
		ToggleBarHelper.updateToggleStates(frame);
	}

	public static void panelSwitcher(UIDockFrame frame, String id) {
		String position = frame.getPanelPositions().get(id);
		if ("bottom".equalsIgnoreCase(position)) {
			frame.showPanelBottom(id);
		} else {
			frame.showPanelTop(id);
		}
	}

	public static void dockPanelToMatch(UIDockFrame frame, String sourcePanelID, String targetPanelID) {
		String position = frame.getPanelPosition(sourcePanelID);
		if ("bottom".equalsIgnoreCase(position)) {
			frame.showPanelBottom(targetPanelID);
		} else {
			frame.showPanelTop(targetPanelID);
		}
	}

	public static void reflowToggleButtons() {
		UIDockFrame.toggleBarTop.removeAll();
		UIDockFrame.toggleBarBottom.removeAll();

		UIDockFrame.toggleButtons.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.forEach(entry -> {
				String id = entry.getKey();
				JToggleButton toggle = entry.getValue();
				String position = UIDockFrame.panelPositions.getOrDefault(id, "top");

				if ("bottom".equalsIgnoreCase(position)) {
					UIDockFrame.toggleBarBottom.add(toggle);
				} else {
					UIDockFrame.toggleBarTop.add(toggle);
				}
			});

		UIDockFrame.toggleBarTop.revalidate();
		UIDockFrame.toggleBarTop.repaint();
		UIDockFrame.toggleBarBottom.revalidate();
		UIDockFrame.toggleBarBottom.repaint();

		// Setup circular view after reflow
		SwingUtilities.invokeLater(() -> {
			setupCircularView(UIDockFrame.scrollTop, UIDockFrame.toggleBarTop);
			setupCircularView(UIDockFrame.scrollBottom, UIDockFrame.toggleBarBottom);
		});
	}

	/**
	 * Get circular window of buttons based on selected index
	 */
	private static List<JToggleButton> getCircularWindow(List<JToggleButton> allButtons, int selectedIndex, int viewSize) {
		List<JToggleButton> window = new ArrayList<>();
		int totalSize = allButtons.size();

		if (totalSize == 0) return window;

		// Center the selected button with equal buttons on each side
		int halfView = viewSize / 2;

		for (int i = 0; i < viewSize; i++) {
			int index = (selectedIndex - halfView + i + totalSize) % totalSize;
			window.add(allButtons.get(index));
		}

		return window;
	}

	/**
	 * Get preview buttons (buttons not in viewport)
	 */
	private static List<JToggleButton> getPreviewButtons(List<JToggleButton> allButtons, List<JToggleButton> viewportButtons) {
		List<JToggleButton> preview = new ArrayList<>();

		for (JToggleButton button : allButtons) {
			if (!viewportButtons.contains(button)) {
				preview.add(button);
			}
		}

		return preview;
	}

	/**
	 * CENTER SELECTED TOGGLE IN CIRCULAR VIEW
	 */
	public static void centerToggleInCircularView(String buttonId) {
		JToggleButton targetButton = UIDockFrame.toggleButtons.get(buttonId);
		if (targetButton == null) return;

		JScrollPane scrollPane = "bottom".equalsIgnoreCase(UIDockFrame.getPanelPosition(buttonId))
			? UIDockFrame.scrollBottom
			: UIDockFrame.scrollTop;

		SwingUtilities.invokeLater(() -> {
			updateCircularView(scrollPane, targetButton);
		});
	}

	private static void updateCircularView(JScrollPane scrollPane, JToggleButton selectedButton) {
		Component view = scrollPane.getViewport().getView();
		if (!(view instanceof JPanel)) return;

		JPanel container = (JPanel) view;

		// Get all original buttons in order
		List<JToggleButton> allButtons = getAllButtonsInOrder(container);
		if (allButtons.isEmpty()) return;

		// Find selected button index
		int selectedIndex = -1;
		for (int i = 0; i < allButtons.size(); i++) {
			if (getButtonId(allButtons.get(i)).equals(getButtonId(selectedButton))) {
				selectedIndex = i;
				break;
			}
		}

		if (selectedIndex == -1) return;

		// Calculate viewport size based on 300px width
		int viewSize = Math.min(5, allButtons.size()); // Max 5 buttons visible

		// Get circular window centered on selected
		List<JToggleButton> viewportButtons = getCircularWindow(allButtons, selectedIndex, viewSize);

		// Clear and rebuild container with new order
		container.removeAll();
		for (JToggleButton button : viewportButtons) {
			container.add(button);
		}

		container.revalidate();
		container.repaint();

		// Center the selected button in viewport
		SwingUtilities.invokeLater(() -> {
			Rectangle bounds = selectedButton.getBounds();
			int buttonCenter = bounds.x + (bounds.width / 2);
			int targetScrollPosition = buttonCenter - CENTER_POSITION;
			scrollPane.getHorizontalScrollBar().setValue(targetScrollPosition);
		});
	}

	private static List<JToggleButton> getAllButtonsInOrder(JPanel container) {
		List<JToggleButton> buttons = new ArrayList<>();

		// Get all toggle buttons from the original source in sorted order
		UIDockFrame.toggleButtons.entrySet().stream()
			.sorted(Map.Entry.comparingByKey())
			.forEach(entry -> {
				JToggleButton button = entry.getValue();
				Container parent = button.getParent();
				// Add null check to prevent NPE
				if (parent != null && (parent == container || SwingUtilities.isDescendingFrom(parent, container))) {
					buttons.add(button);
				}
			});

		return buttons;
	}

	private static String getButtonId(JToggleButton button) {
		String text = button.getText();
		if (text != null && text.length() > 2) {
			// Remove the arrow prefix (e.g., "↑ ", "↓ ", "• ")
			return text.substring(2);
		}
		return text != null ? text : "";
	}

	/**
	 * Setup circular scroll bars
	 */
	public static void setupCircularScrollBars(UIDockFrame frame) {
		setupCircularScrollBar(UIDockFrame.scrollTop);
		setupCircularScrollBar(UIDockFrame.scrollBottom);
	}

	private static void setupCircularScrollBar(JScrollPane scrollPane) {
		// Hide scrollbar visually but keep functionality
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
		horizontalScrollBar.setPreferredSize(new Dimension(0, 0));

		// Add mouse wheel scrolling with circular buffer rotation
		scrollPane.addMouseWheelListener(new MouseWheelListener() {
			private long lastScrollTime = 0;
			private final int SCROLL_DELAY = 150; // ms between scroll actions

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				long now = System.currentTimeMillis();
				if (now - lastScrollTime < SCROLL_DELAY) return;
				lastScrollTime = now;

				Component view = scrollPane.getViewport().getView();
				if (!(view instanceof JPanel)) return;

				JPanel container = (JPanel) view;
				List<JToggleButton> allButtons = getAllButtonsInOrder(container);
				if (allButtons.isEmpty()) return;

				// Find currently centered button
				JToggleButton currentCenter = findCenterButton(container, scrollPane);
				if (currentCenter == null && !allButtons.isEmpty()) {
					currentCenter = allButtons.get(0); // fallback to first button
				}

				// Get current index
				int currentIndex = 0;
				for (int i = 0; i < allButtons.size(); i++) {
					if (getButtonId(allButtons.get(i)).equals(getButtonId(currentCenter))) {
						currentIndex = i;
						break;
					}
				}

				// Calculate next/previous button based on scroll direction
				int scrollDirection = e.getWheelRotation() > 0 ? 1 : -1;
				int nextIndex = (currentIndex + scrollDirection + allButtons.size()) % allButtons.size();

				// Get the button ID and trigger circular view update
				String nextButtonId = getButtonId(allButtons.get(nextIndex));
				centerToggleInCircularView(nextButtonId);

				e.consume();
			}
		});
	}

	private static JToggleButton findCenterButton(JPanel container, JScrollPane scrollPane) {
		Rectangle viewport = scrollPane.getViewport().getViewRect();
		int centerX = viewport.x + viewport.width / 2;

		JToggleButton closestButton = null;
		int closestDistance = Integer.MAX_VALUE;

		for (Component comp : container.getComponents()) {
			if (comp instanceof JToggleButton) {
				JToggleButton button = (JToggleButton) comp;
				Rectangle bounds = button.getBounds();
				int buttonCenter = bounds.x + bounds.width / 2;
				int distance = Math.abs(buttonCenter - centerX);

				if (distance < closestDistance) {
					closestDistance = distance;
					closestButton = button;
				}
			}
		}

		return closestButton;
	}

	private static void setupCircularView(JScrollPane scrollPane, JPanel originalToggleBar) {
		// Keep the original toggle bar as the view
		scrollPane.setViewportView(originalToggleBar);
	}

	public static DockTextUpdatable getUpdatablePanel(String panelID) {
		UIPanel panel = UIDockFrame.getInstance().getPanel(panelID);
		if (panel instanceof DockTextUpdatable) {
			return (DockTextUpdatable) panel;
		}
		return null;
	}

	public static boolean isPanelVisible(String panelID) {
		UIDockFrame dock = UIDockFrame.getInstance();
		if (dock == null) return false;
		return panelID.equals(dock.getTopVisiblePanelID()) || panelID.equals(dock.getBottomVisiblePanelID());
	}

	public static void updateToggleInteractivity() {
		boolean loggedIn = Client.loggedIn;

		for (Map.Entry<String, JToggleButton> entry : UIDockFrame.toggleButtons.entrySet()) {
			JToggleButton toggle = entry.getValue();

			toggle.setEnabled(loggedIn);
			toggle.setToolTipText(
				loggedIn
					? "Open " + entry.getKey()
					: "Login to use this panel"
			);
		}

		// Also update the login overlay
		UIDockFrame frame = UIDockFrame.getInstance();
		if (frame != null && frame.getLoginOverlay() != null) {
			frame.getLoginOverlay().refreshLoginState();
		}
	}
}