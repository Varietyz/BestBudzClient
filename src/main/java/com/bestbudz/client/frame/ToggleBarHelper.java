package com.bestbudz.client.frame;

import com.bestbudz.client.ui.panel.UIPanel;
import com.bestbudz.config.SettingHandler;

import com.bestbudz.engine.Client;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ToggleBarHelper {

	public static void createToggleButton(UIDockFrame frame, UIPanel uiPanel) {
		String id = uiPanel.getPanelID();
		JToggleButton toggle = new JToggleButton(id);
		toggle.setFocusPainted(false);
		toggle.setComponentPopupMenu(createTogglePopup(frame, id));
		toggle.addActionListener(e -> {
			if (!Client.loggedIn) return;

			boolean isAlreadyVisible = id.equals(frame.getTopVisiblePanelID()) || id.equals(frame.getBottomVisiblePanelID());
			if (isAlreadyVisible) return;

			toggle.setSelected(true);
			frame.panelSwitcher(id);
		});



		frame.getToggleButtons().put(id, toggle);
	}



	private static JPopupMenu createTogglePopup(UIDockFrame frame, String id) {
		JPopupMenu popup = new JPopupMenu();

		JMenuItem topItem = new JMenuItem("Dock to Top");
		topItem.addActionListener(e -> UIDockHelper.showPanelTop(frame, id));

		JMenuItem bottomItem = new JMenuItem("Dock to Bottom");
		bottomItem.addActionListener(e -> UIDockHelper.showPanelBottom(frame, id));

		popup.add(topItem);
		popup.add(bottomItem);
		return popup;
	}

	public static void updateToggleStates(UIDockFrame frame) {
		for (Map.Entry<String, JToggleButton> entry : frame.getToggleButtons().entrySet()) {
			String id = entry.getKey();
			JToggleButton toggle = entry.getValue();

			String position = frame.getPanelPositions().get(id);
			boolean isVisible = id.equals(frame.getTopVisiblePanelID()) || id.equals(frame.getBottomVisiblePanelID());

			String arrow = position == null ? "•" :
				position.equals("bottom") ? "↓" : "↑";

			toggle.setToolTipText(
				(isVisible ? "Currently active • " : "") +
					(position != null ? "Docked to " + position : "Undocked")
			);

			toggle.setText(arrow + " " + id);
			toggle.setEnabled(true);
			toggle.setSelected(isVisible);
			toggle.setFont(toggle.getFont().deriveFont(isVisible ? Font.BOLD : Font.PLAIN));
		}

		SettingHandler.save();
	}


}
