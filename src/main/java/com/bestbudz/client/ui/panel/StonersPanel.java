package com.bestbudz.client.ui.panel;

import com.bestbudz.client.util.DockTextUpdatable;
import com.bestbudz.engine.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StonersPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private final Timer refreshTimer;

	private final DefaultListModel<StonerEntry> listModel = new DefaultListModel<>();
	private final JList<StonerEntry> stonerList;

	public StonersPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(6, 6, 6, 6));
		setPreferredSize(null);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(0, 0));

		// Title
		JLabel title = new JLabel("Stoners");
		title.setFont(new Font("SansSerif", Font.BOLD, 14));
		title.setForeground(new Color(0xFF9933));
		title.setBorder(new EmptyBorder(0, 0, 6, 0));
		add(title, BorderLayout.NORTH);

		// List
		stonerList = new JList<>(listModel);
		stonerList.setOpaque(false);
		stonerList.setCellRenderer(new StonerCellRenderer());
		stonerList.setFixedCellHeight(22);
		stonerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stonerList.setVisibleRowCount(20);

		// Left-click -> Socialize
		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int index = stonerList.locationToIndex(e.getPoint());
					if (index >= 0 && index < listModel.size()) {
						StonerEntry selected = listModel.get(index);
						if (selected != null) invokeSocializeMenuAction(selected.name);
					}
				}
			}
		});

		// Hover highlight
		stonerList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = stonerList.locationToIndex(e.getPoint());
				stonerList.setSelectedIndex(index);
			}
		});

		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				stonerList.clearSelection();
			}
		});

		JScrollPane scroll = new JScrollPane(stonerList);
		scroll.setOpaque(false);
		scroll.setBorder(null);
		scroll.getViewport().setOpaque(false);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scroll, BorderLayout.CENTER);

		refreshTimer = new Timer(3000, e -> {
			if (isShowing() && Client.loggedIn) {
				onActivate(); // safely refresh list
			}
		});
		refreshTimer.setRepeats(true);

		ToolTipManager.sharedInstance().setInitialDelay(300);
		ToolTipManager.sharedInstance().setDismissDelay(4000);

		stonerList.setToolTipText(""); // activate tooltip system

		stonerList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = stonerList.locationToIndex(e.getPoint());
				if (index >= 0 && index < listModel.size()) {
					StonerEntry entry = listModel.get(index);

					if (entry.isHigh) {
						stonerList.setToolTipText(
							"<html><font color='#5FD17A'>Socialize with " + entry.name + "</font></html>"
						);
					} else {
						stonerList.setToolTipText(null); // no tooltip if asleep
					}
				} else {
					stonerList.setToolTipText(null);
				}
			}
		});



	}

	private void invokeSocializeMenuAction(String name) {
		if (!Client.loggedIn || name == null || name.isEmpty()) return;

		Client.menuActionRow = 1;
		Client.menuActionName[0] = "Socialize with @whi@" + name;
		Client.menuActionID[0] = 639;
		Client.menuActionCmd1[0] = 0;
		Client.menuActionCmd2[0] = 0;
		Client.menuActionCmd3[0] = 0;
		Client.selectedStonerName = name;

		Client.processMenuClick(true, false);
	}

	@Override
	public void updateText()
	{

	}

	@Override
	public String getPanelID() {
		return "Stoners";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		if (!Client.loggedIn || Client.stonersList == null) return;

		listModel.clear();
		int count = Math.min(Client.stonersCount, Client.stonersList.length);
		for (int i = 0; i < count; i++) {
			String name = Client.stonersList[i];
			int node = Client.stonersNodeIDs[i];
			boolean high = node == Client.nodeID;
			listModel.addElement(new StonerEntry(name, high));
		}

		refreshTimer.start(); // 🔄 begin auto-refresh
	}

	@Override
	public void onDeactivate() {
		refreshTimer.stop(); // 🛑 disable while hidden
	}


	@Override
	public void updateDockText(int index, String text) {
		if (index < 0 || index >= listModel.size()) return;
		StonerEntry entry = listModel.get(index);
		listModel.set(index, new StonerEntry(text, entry.isHigh));
	}

	static class StonerEntry {
		final String name;
		final boolean isHigh;

		StonerEntry(String name, boolean isHigh) {
			this.name = name;
			this.isHigh = isHigh;
		}
	}

	static class StonerCellRenderer extends JPanel implements ListCellRenderer<StonerEntry> {
		private final JLabel nameLabel = new JLabel();
		private final JLabel statusLabel = new JLabel();

		private static final Color GREEN = new Color(0x5FD17A);
		private static final Color RED = new Color(0xE06666);
		private static final Color BG_HOVER = new Color(0x393939);
		private static final Color BG_SELECTED = new Color(0x2D2D2D);

		public StonerCellRenderer() {
			setLayout(new BorderLayout());
			setOpaque(true);
			setBorder(new EmptyBorder(2, 8, 2, 8));
			setMaximumSize(new Dimension(280, 22));

			nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
			nameLabel.setForeground(Color.WHITE);

			statusLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
			statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);

			add(nameLabel, BorderLayout.WEST);
			add(statusLabel, BorderLayout.EAST);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends StonerEntry> list, StonerEntry value, int index,
													  boolean isSelected, boolean cellHasFocus) {

			nameLabel.setText(value.name);
			statusLabel.setText(value.isHigh ? "● High" : "Asleep");
			statusLabel.setForeground(value.isHigh ? GREEN : RED);

			if (isSelected) {
				setBackground(BG_SELECTED);
			} else {
				setBackground(new Color(0, 0, 0, 0));
			}

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			return this;
		}
	}
}
