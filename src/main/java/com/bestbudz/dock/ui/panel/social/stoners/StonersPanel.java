package com.bestbudz.dock.ui.panel.social.stoners;

import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.core.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StonersPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private final Timer refreshTimer;
	private final DefaultListModel<StonerEntry> listModel = new DefaultListModel<>();
	private final JList<StonerEntry> stonerList;
	private final StonerListController listController;
	private final PrivateMessageService messageService;

	public StonersPanel() {
		setLayout(new BorderLayout());
		setOpaque(false);
		setBorder(new EmptyBorder(6, 6, 6, 6));
		setPreferredSize(null);
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		setMinimumSize(new Dimension(0, 0));

		// Initialize services
		messageService = new PrivateMessageService(this);

		// List
		stonerList = new JList<>(listModel);
		stonerList.setOpaque(false);
		stonerList.setCellRenderer(new StonerCellRenderer());
		stonerList.setFixedCellHeight(22);
		stonerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stonerList.setVisibleRowCount(20);

		// Initialize controller
		listController = new StonerListController(listModel, stonerList, messageService);

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
	}

	@Override
	public void updateText() {
		// Implementation remains the same
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
		listController.refreshList();
		refreshTimer.start(); // 🔄 begin auto-refresh
	}

	@Override
	public void onDeactivate() {
		refreshTimer.stop(); // 🛑 disable while hidden
	}

	@Override
	public void updateDockText(int index, String text) {
		listController.updateDockText(index, text);
	}
}