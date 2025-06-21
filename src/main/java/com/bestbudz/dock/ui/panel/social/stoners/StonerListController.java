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

	// Pattern to match "Pet" followed by space and numbers (e.g., "Pet 54213")
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
		// Left-click -> Show message popup
		stonerList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					int index = stonerList.locationToIndex(e.getPoint());
					if (index >= 0 && index < listModel.size()) {
						StonerEntry selected = listModel.get(index);
						if (selected != null && selected.isHigh) { // Only allow messaging if they're high
							MessageDialog.show(stonerList, selected.name, messageSender);
						}
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

				// Update tooltip
				if (index >= 0 && index < listModel.size()) {
					StonerEntry entry = listModel.get(index);
					if (entry.isHigh) {
						stonerList.setToolTipText(
							"<html><font color='#5FD17A'>Send message to " + entry.name + "</font></html>"
						);
					} else {
						stonerList.setToolTipText(null); // no tooltip if asleep
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

	/**
	 * Check if a username should be excluded from the list
	 */
	private boolean shouldExcludeUser(String username) {
		if (username == null || username.trim().isEmpty()) {
			return true;
		}

		String trimmedName = username.trim();

		// Exclude current user (assuming Client has a way to get current username)
		// You might need to adjust this based on how you get the current user's name
		if (Client.myStoner != null && trimmedName.equals(Client.myStoner.name)) {
			return true;
		}

		// Alternative way to check current user if myStoner is not available
		// Uncomment and adjust as needed based on your Client implementation
		// if (trimmedName.equals(Client.localPlayer.name)) {
		//     return true;
		// }

		// Exclude usernames starting with "Pet" followed by space and numbers
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

			// Skip excluded users
			if (shouldExcludeUser(name)) {
				continue;
			}

			int node = Client.stonersNodeIDs[i];
			boolean high = node == Client.nodeID;

			// Get title information from the actual Stoner object
			String title = UserTitleUtil.getTitleForUser(name);
			String titleColor = UserTitleUtil.getTitleColorForUser(name);

			listModel.addElement(new StonerEntry(name, high, title, titleColor));
		}
	}

	public void updateDockText(int index, String text) {
		if (index < 0 || index >= listModel.size()) return;

		// Skip excluded users
		if (shouldExcludeUser(text)) {
			return;
		}

		StonerEntry entry = listModel.get(index);

		// Get updated title information from the actual Stoner object
		String title = UserTitleUtil.getTitleForUser(text);
		String titleColor = UserTitleUtil.getTitleColorForUser(text);

		listModel.set(index, new StonerEntry(text, entry.isHigh, title, titleColor));
	}
}