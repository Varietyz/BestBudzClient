package com.bestbudz.client.frame;

import com.bestbudz.client.ui.manager.UIPanelManager;
import com.bestbudz.client.ui.panel.*;

import com.bestbudz.client.ui.panel.UIPanel;

import com.bestbudz.config.Configuration;
import com.bestbudz.config.SettingHandler;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class UIDockFrame extends JFrame {

	private String lastActivePanelID;
	private String topVisiblePanelID;
	private String bottomVisiblePanelID;

	private final Map<String, String> panelPositions = new LinkedHashMap<>();
	private JSplitPane splitPane;

	private final UIPanelManager panelManager;
	private final JPanel toggleBar;

	private final CardLayout topLayout = new CardLayout();
	private final CardLayout bottomLayout = new CardLayout();

	private final JPanel topStack = new JPanel(topLayout);
	private final JPanel bottomStack = new JPanel(bottomLayout);

	private final Map<String, Component> panelMap = new LinkedHashMap<>();
	private final Map<String, JToggleButton> toggleButtons = new LinkedHashMap<>();

	private static UIDockFrame instance;

	public static UIDockFrame getInstance() {
		return instance;
	}

	public UIDockFrame() {
		instance = this;
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setSize(300, 758);
		setLayout(new BorderLayout());
		setResizable(true);
		setUndecorated(true);

		toggleBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 4));
		add(toggleBar, BorderLayout.NORTH);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(topStack), new JScrollPane(bottomStack));
		splitPane.setResizeWeight(0.5);
		add(splitPane, BorderLayout.CENTER);


		panelManager = new UIPanelManager();

		registerPanel(new SettingsPanel());
		registerPanel(new InventoryPanel());
		registerPanel(new QuestTabPanel());
		registerPanel(new AchievementsPanel());

		loadDockPanelLayout(); // 🔄 this will restore visibility

		for (Map.Entry<String, Component> entry : panelMap.entrySet()) {
			String panelID = entry.getKey();
			if (!panelPositions.containsKey(panelID)) {
				panelPositions.put(panelID, "top");
			}
		}



		SwingUtilities.invokeLater(() -> {
			int fullHeight = splitPane.getHeight();
			if (fullHeight > 0) {
				int calculated = Math.round(fullHeight * Configuration.uiDockDividerRatio);
				splitPane.setDividerLocation(calculated);
			}
		});

		SwingUtilities.invokeLater(() -> {
			int fullHeight = splitPane.getHeight();
			if (fullHeight > 0) {
				int calculated = Math.round(fullHeight * Configuration.uiDockDividerRatio);
				splitPane.setDividerLocation(calculated);
			}

			// Add save-on-release listener to divider
			Component divider = ((BasicSplitPaneUI) splitPane.getUI()).getDivider();
			divider.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					saveLayoutState();
					SettingHandler.save();
				}
			});
		});

		setVisible(true);

	}

	private void registerPanel(final UIPanel uiPanel) {
		final String id = uiPanel.getPanelID();
		final Component panel = uiPanel.getComponent();

		panelMap.put(id, panel);
		panelManager.register(uiPanel);

		final JToggleButton toggle = new JToggleButton(id);
		toggle.setFocusPainted(false);
		toggle.setComponentPopupMenu(createTogglePopup(id));
		toggleButtons.put(id, toggle);
		toggle.addActionListener(e -> panelSwitcher(id));
		toggleBar.add(toggle);
	}



	private JPopupMenu createTogglePopup(String id) {
		JPopupMenu popup = new JPopupMenu();

		JMenuItem topItem = new JMenuItem("Dock to Top");
		topItem.addActionListener(e -> showPanelTop(id));

		JMenuItem bottomItem = new JMenuItem("Dock to Bottom");
		bottomItem.addActionListener(e -> showPanelBottom(id));

		popup.add(topItem);
		popup.add(bottomItem);
		return popup;
	}


	private void loadDockPanelLayout() {
		String layout = Configuration.uiDockPanels;
		if (layout != null && !layout.isEmpty()) {
			String[] entries = layout.split(",");
			for (String entry : entries) {
				String[] parts = entry.split("=");
				if (parts.length != 2) continue;

				String panelID = parts[0].trim();
				String position = parts[1].trim();

				panelPositions.put(panelID, position);

				Component panel = panelMap.get(panelID);
				if (panel == null) continue; // ❗ prevent NPE

				if ("top".equalsIgnoreCase(position)) {
					if (!topStack.isAncestorOf(panel)) {
						bottomStack.remove(panel);
						topStack.add(panel, panelID);
					}
					if (topVisiblePanelID == null) topVisiblePanelID = panelID;
				} else if ("bottom".equalsIgnoreCase(position)) {
					if (!bottomStack.isAncestorOf(panel)) {
						topStack.remove(panel);
						bottomStack.add(panel, panelID);
					}
					if (bottomVisiblePanelID == null) bottomVisiblePanelID = panelID;
				}

			}
		}

		// Restore top and bottom stacks with the most recently known visible panels
		if (topVisiblePanelID != null) {
			switchPanel(topStack, topLayout, topVisiblePanelID);
		}
		if (bottomVisiblePanelID != null && !bottomVisiblePanelID.equals(topVisiblePanelID)) {
			switchPanel(bottomStack, bottomLayout, bottomVisiblePanelID);
		}

		updateToggles(); // ensure toggle button state matches
	}


	public void showPanelTop(String id) {
		lastActivePanelID = id;
		topVisiblePanelID = id;
		panelPositions.put(id, "top");
		updateDockPanelConfig();

		Component panel = panelMap.get(id);
		if (bottomStack.isAncestorOf(panel)) {
			bottomStack.remove(panel);
			topStack.add(panel, id);
		}

		switchPanel(topStack, topLayout, id);
		updateToggles();
		saveLayoutState(); // ✅ persist to Configuration
	}

	public void showPanelBottom(String id) {
		lastActivePanelID = id;
		bottomVisiblePanelID = id;
		panelPositions.put(id, "bottom");
		updateDockPanelConfig();

		Component panel = panelMap.get(id);
		if (topStack.isAncestorOf(panel)) {
			topStack.remove(panel);
			bottomStack.add(panel, id);
		}

		switchPanel(bottomStack, bottomLayout, id);
		updateToggles();
		saveLayoutState(); // ✅ persist to Configuration
	}

	private void updateDockPanelConfig() {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, String> entry : panelPositions.entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
		}
		if (sb.length() > 0) sb.setLength(sb.length() - 1); // remove last comma
		Configuration.uiDockPanels = sb.toString();
	}

	public void saveLayoutState() {
		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : panelPositions.entrySet()) {
			sb.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
		}

		if (sb.length() > 0)
			sb.setLength(sb.length() - 1); // trim trailing comma

		Configuration.uiDockPanels = sb.toString();

		if (splitPane.getHeight() > 100) {
			int fullHeight = splitPane.getHeight();
			int location = splitPane.getDividerLocation();
			Configuration.uiDockDividerRatio = Math.max(0f, Math.min(1f, location / (float) fullHeight));
		}

		Configuration.uiDockLastActive = lastActivePanelID != null ? lastActivePanelID : "";
	}



	private void switchPanel(JPanel stack, CardLayout layout, String id) {
		for (UIPanel p : panelManager.getAllPanels()) {
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

	private void updateToggles() {
		for (Map.Entry<String, JToggleButton> entry : toggleButtons.entrySet()) {
			String id = entry.getKey();
			JToggleButton toggle = entry.getValue();

			String position = panelPositions.get(id); // top, bottom, or null
			boolean isVisible = id.equals(topVisiblePanelID) || id.equals(bottomVisiblePanelID);

			// Determine arrow
			String arrow = position == null ? "•" :
				position.equals("bottom") ? "↓" : "↑";

			// Tooltip
			toggle.setToolTipText(
				(isVisible ? "Currently active • " : "") +
					(position != null ? "Docked to " + position : "Undocked")
			);

			// Apply state
			toggle.setText(arrow + " " + id);
			toggle.setEnabled(!isVisible);
			toggle.setSelected(isVisible);
			toggle.setFont(toggle.getFont().deriveFont(isVisible ? Font.BOLD : Font.PLAIN));
		}
		SettingHandler.save(); // ✅ persist to disk
	}


	private String getVisibleCard(JPanel stack) {
		for (Component comp : stack.getComponents()) {
			if (comp.isVisible()) {
				for (Map.Entry<String, Component> entry : panelMap.entrySet()) {
					if (entry.getValue() == comp) {
						return entry.getKey();
					}
				}
			}
		}
		return null;
	}


	public void panelSwitcher(String id) {
		String position = panelPositions.get(id);
		if ("bottom".equalsIgnoreCase(position)) {
			showPanelBottom(id);
		} else {
			showPanelTop(id); // default to top
		}
	}

	public UIPanel getPanel(String id) {
		return panelManager.getPanel(id);
	}

	public String getPanelPosition(String id) {
		return panelPositions.getOrDefault(id, "top");
	}

	public void dockPanelToMatch(String sourcePanelID, String targetPanelID) {
		String position = getPanelPosition(sourcePanelID);
		if ("bottom".equalsIgnoreCase(position)) {
			showPanelBottom(targetPanelID);
		} else {
			showPanelTop(targetPanelID);
		}
	}

}
