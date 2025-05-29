package com.bestbudz.dock.frame;

import com.bestbudz.dock.ui.manager.UIDockLayoutState;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.dock.util.DockBlocker;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.engine.config.SettingsConfig;

import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class UIDockHelper extends UIDockFrame
{

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
	}



	public static void showPanelTop(UIDockFrame frame, String id) {
		frame.setLastActivePanelID(id);
		frame.setTopVisiblePanelID(id);
		frame.getPanelPositions().put(id, "top");
		frame.updateDockPanelConfig();

		Component panel = frame.getPanelMap().get(id);
		if (frame.getBottomStack().isAncestorOf(panel)) {
			frame.getBottomStack().remove(panel);
			frame.getTopStack().add(panel, id);
		}

		switchPanel(frame, frame.getTopStack(), frame.getTopLayout(), id);
		frame.updateToggles();
		frame.saveLayoutState();
		reflowToggleButtons();
		scrollToggleIntoView(id);

	}

	public static void showPanelBottom(UIDockFrame frame, String id) {
		frame.setLastActivePanelID(id);
		frame.setBottomVisiblePanelID(id);
		frame.getPanelPositions().put(id, "bottom");
		frame.updateDockPanelConfig();

		Component panel = frame.getPanelMap().get(id);
		if (frame.getTopStack().isAncestorOf(panel)) {
			frame.getTopStack().remove(panel);
			frame.getBottomStack().add(panel, id);
		}

		switchPanel(frame, frame.getBottomStack(), frame.getBottomLayout(), id);
		frame.updateToggles();
		frame.saveLayoutState();
		reflowToggleButtons();
		scrollToggleIntoView(id);
	}

	public static void updateDockPanelConfig(UIDockFrame frame) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : frame.getPanelPositions().entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
		}
		if (sb.length() > 0) sb.setLength(sb.length() - 1);
		SettingsConfig.uiDockPanels = sb.toString();
	}

	public static void saveLayoutState(UIDockFrame frame) {
		UIDockLayoutState.save(frame);
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


	public static void scrollToggleIntoView(String id) {
		JToggleButton toggle = UIDockFrame.toggleButtons.get(id);
		if (toggle == null) return;

		JScrollPane scroll = "bottom".equalsIgnoreCase(UIDockFrame.getPanelPosition(id))
			? UIDockFrame.scrollBottom
			: UIDockFrame.scrollTop;

		Rectangle bounds = toggle.getBounds();
		Point viewPos = scroll.getViewport().getViewPosition();

		int center = bounds.x - (scroll.getViewport().getWidth() / 2) + (bounds.width / 2);
		scroll.getViewport().setViewPosition(new Point(Math.max(0, center), viewPos.y));
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
