package com.bestbudz.dock.ui.panel.stoner;


import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Stoner;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the list of stoner components and their updates
 */
public class StonerListManager {

	private static final Color TEXT_SECONDARY = new Color(170, 170, 170);

	private JPanel contentPanel;
	private final List<StonerComponent> stonerComponents = new ArrayList<>();
	private int panelWidth;

	public StonerListManager(int panelWidth) {
		this.panelWidth = panelWidth;
		createContentPanel();
	}

	private void createContentPanel() {
		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setBackground(com.bestbudz.engine.config.ColorConfig.MAIN_FRAME_COLOR);
		contentPanel.setBorder(new javax.swing.border.EmptyBorder(4, 8, 8, 8));
	}

	public void updateStonerList() {
		SwingUtilities.invokeLater(() -> {
			contentPanel.removeAll();
			stonerComponents.clear();

			if (!Client.loggedIn) {
				addEmptyState();
				return;
			}

			// Add local player first
			if (Client.myStoner != null) {
				StonerComponent myComponent = new StonerComponent(Client.myStoner, true, panelWidth);
				stonerComponents.add(myComponent);
				contentPanel.add(myComponent);
				contentPanel.add(Box.createVerticalStrut(4));
			}

			// Add other stoners
			for (int i = 0; i < Client.stonerCount && i < Client.stonerArray.length; i++) {
				int index = Client.stonerIndices[i];
				if (index >= 0 && index < Client.stonerArray.length) {
					Stoner stoner = Client.stonerArray[index];
					if (stoner != null && stoner != Client.myStoner) {
						StonerComponent component = new StonerComponent(stoner, false, panelWidth);
						stonerComponents.add(component);
						contentPanel.add(component);
						contentPanel.add(Box.createVerticalStrut(4));
					}
				}
			}

			contentPanel.revalidate();
			contentPanel.repaint();
		});
	}

	private void addEmptyState() {
		JLabel emptyLabel = new JLabel("No stoners nearby");
		emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
		emptyLabel.setForeground(TEXT_SECONDARY);
		emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
		emptyLabel.setBorder(new javax.swing.border.EmptyBorder(20, 0, 20, 0));
		contentPanel.add(emptyLabel);
	}

	public void updatePanelWidth(int newWidth) {
		this.panelWidth = newWidth;
		for (StonerComponent comp : stonerComponents) {
			comp.updateLayout(newWidth);
		}
	}

	public void refreshComponents() {
		for (StonerComponent comp : stonerComponents) {
			comp.refresh();
		}
	}

	public int getStonerCount() {
		return stonerComponents.size();
	}

	public JPanel getContentPanel() {
		return contentPanel;
	}
}
