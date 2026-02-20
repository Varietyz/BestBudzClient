package com.bestbudz.dock.ui.panel.social.stoners;

import com.bestbudz.engine.core.Client;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class StonerListController {
	private final DefaultListModel<StonerEntry> listModel;
	private final JList<StonerEntry> stonerList;
	private final MessageSender messageSender;

	private static final Pattern PET_PATTERN = Pattern.compile("^Pet\\s+\\d+.*$", Pattern.CASE_INSENSITIVE);

	public StonerListController(DefaultListModel<StonerEntry> listModel,
								JList<StonerEntry> stonerList,
								MessageSender messageSender) {
		this.listModel = listModel;
		this.stonerList = stonerList;
		this.messageSender = messageSender;

		setupEventHandlers();
	}

	private void setupEventHandlers() {

		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int index = stonerList.locationToIndex(e.getPoint());
					if (index >= 0 && index < listModel.size()) {
						StonerEntry selected = listModel.get(index);
						if (selected != null && selected.isHigh) {
							MessageDialog.show(stonerList, selected.name, messageSender);
						}
					}
				}
			}
		});

		stonerList.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int index = stonerList.locationToIndex(e.getPoint());
				stonerList.setSelectedIndex(index);

				if (index >= 0 && index < listModel.size()) {
					StonerEntry entry = listModel.get(index);
					if (entry.isHigh) {
						stonerList.setToolTipText(
							"<html><font color='#5FD17A'>Send message to " + entry.name + "</font></html>"
						);
					} else {
						stonerList.setToolTipText(null);
					}
				} else {
					stonerList.setToolTipText(null);
				}
			}
		});

		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseExited(MouseEvent e) {
				stonerList.clearSelection();
			}
		});
	}

	private boolean shouldExcludeUser(String username) {
		if (username == null || username.trim().isEmpty()) {
			return true;
		}

		String trimmedName = username.trim();

		if (Client.myStoner != null && trimmedName.equals(Client.myStoner.name)) {
			return true;
		}

		if (PET_PATTERN.matcher(trimmedName).matches()) {
			return true;
		}

		return false;
	}

	public void refreshList() {
		if (!Client.loggedIn || Client.stonersList == null) return;

		listModel.clear();
		int count = Math.min(Client.stonersCount, Client.stonersList.length);
		for (int i = 0; i < count; i++) {
			String name = Client.stonersList[i];

			if (shouldExcludeUser(name)) {
				continue;
			}

			int node = Client.stonersNodeIDs[i];
			boolean high = node == Client.nodeID;

			String title = UserTitleUtil.getTitleForUser(name);
			String titleColor = UserTitleUtil.getTitleColorForUser(name);

			listModel.addElement(new StonerEntry(name, high, title, titleColor));
		}
	}

	public void updateDockText(int index, String text) {
		if (index < 0 || index >= listModel.size()) return;

		if (shouldExcludeUser(text)) {
			return;
		}

		StonerEntry entry = listModel.get(index);

		String title = UserTitleUtil.getTitleForUser(text);
		String titleColor = UserTitleUtil.getTitleColorForUser(text);

		listModel.set(index, new StonerEntry(text, entry.isHigh, title, titleColor));
	}
}
