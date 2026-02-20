package com.bestbudz.dock.ui.panel.bank;

import com.bestbudz.dock.ui.panel.bank.components.*;
import com.bestbudz.dock.ui.panel.bank.grid.BankItemGrid;
import com.bestbudz.dock.ui.panel.bank.state.BankStateManager;
import com.bestbudz.dock.ui.panel.bank.state.BankUpdateManager;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.*;

public class BankPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private BankHeader header;
	private BankFilterPanel filterPanel;
	private BankActionPanel actionPanel;
	private BankItemGrid bankGrid;
	private JScrollPane gridScrollPane;

	private BankStateManager stateManager;
	private BankUpdateManager updateManager;

	private boolean isFilteringInProgress = false;

	public BankPanel() {
		setLayout(new BorderLayout());
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(300, 500));
		setMinimumSize(new Dimension(300, 100));
		setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

		initializeManagers();
		initializeComponents();
		setupComponentCallbacks();
		layoutComponents();
	}

	private void initializeManagers() {
		stateManager = new BankStateManager();
		updateManager = new BankUpdateManager(this::updateBankData);
	}

	private void initializeComponents() {
		header = new BankHeader();
		filterPanel = new BankFilterPanel();
		actionPanel = new BankActionPanel();
		bankGrid = new BankItemGrid(this);
	}

	private void setupComponentCallbacks() {

		filterPanel.setFilterChangeCallback(filter -> {
			coordinateFilterOperation(() -> bankGrid.setFilter(filter));
		});

		filterPanel.setSortChangeCallback(sort -> {
			coordinateFilterOperation(() -> bankGrid.setSort(sort));
		});

		filterPanel.setSearchChangeCallback(searchText -> {
			coordinateFilterOperation(() -> bankGrid.setSearchText(searchText));
		});

		actionPanel.setActionCallback(() -> {
			updateManager.enableFastUpdateMode();
			forceUpdateAfterClick();
		});
	}

	private void coordinateFilterOperation(Runnable operation) {
		if (isFilteringInProgress) {
			return;
		}

		isFilteringInProgress = true;

		try {

			Component glassPane = getRootPane() != null ? getRootPane().getGlassPane() : null;
			boolean wasGlassPaneVisible;

			if (glassPane != null) {
				wasGlassPaneVisible = glassPane.isVisible();
				if (!wasGlassPaneVisible) {
					glassPane.setVisible(true);
					glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			}
			else
			{
				wasGlassPaneVisible = false;
			}

			setIgnoreRepaint(true);

			try {

				operation.run();

				invalidate();
				revalidate();

				SwingUtilities.invokeLater(() -> {
					setIgnoreRepaint(false);
					repaint();

					if (glassPane != null && !wasGlassPaneVisible) {
						glassPane.setVisible(false);
						glassPane.setCursor(Cursor.getDefaultCursor());
					}
				});

			} finally {
				if (glassPane != null && !wasGlassPaneVisible) {
					SwingUtilities.invokeLater(() -> {
						glassPane.setVisible(false);
						glassPane.setCursor(Cursor.getDefaultCursor());
					});
				}
			}

		} finally {
			SwingUtilities.invokeLater(() -> {
				isFilteringInProgress = false;
				setIgnoreRepaint(false);
			});
		}
	}

	private void layoutComponents() {

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		topPanel.setOpaque(true);
		topPanel.add(header, BorderLayout.NORTH);

		JPanel controlsContainer = new JPanel(new BorderLayout());
		controlsContainer.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		controlsContainer.add(filterPanel, BorderLayout.CENTER);
		controlsContainer.add(actionPanel, BorderLayout.SOUTH);
		topPanel.add(controlsContainer, BorderLayout.SOUTH);

		gridScrollPane = createScrollPane();

		add(topPanel, BorderLayout.NORTH);
		add(gridScrollPane, BorderLayout.CENTER);
	}

	private JScrollPane createScrollPane() {
		JScrollPane scrollPane = new JScrollPane(bankGrid);
		scrollPane.setBorder(null);
		scrollPane.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		scrollPane.getViewport().setBackground(ColorConfig.MAIN_FRAME_COLOR);
		scrollPane.getViewport().setOpaque(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setOpaque(true);
		scrollPane.setDoubleBuffered(true);

		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

		return scrollPane;
	}

	public void updateBankData() {
		if (!Client.loggedIn) {
			handleLoggedOutState();
			return;
		}

		try {

			boolean bankChanged = stateManager.hasBankChanged();
			boolean inventoryChanged = stateManager.hasInventoryChanged();
			boolean itemCountChanged = stateManager.hasItemCountChanged();

			if (bankChanged || inventoryChanged || itemCountChanged) {
				SwingUtilities.invokeLater(() -> {
					updateItemCount();

					if (bankChanged || inventoryChanged) {

						bankGrid.updateItems();
					} else {

						bankGrid.refreshItemAmounts();
					}

					invalidate();
					revalidate();
					repaint();
				});
			}

		} catch (Exception e) {
			SwingUtilities.invokeLater(() -> {
				header.updateItemCount(-1);
				repaint();
			});
		}
	}

	private void handleLoggedOutState() {
		SwingUtilities.invokeLater(() -> {
			header.updateItemCount(0);
			bankGrid.clearItems();
			stateManager.reset();
			repaint();
		});
	}

	private void updateItemCount() {
		int bankItemCount = stateManager.getBankItemCount();
		header.updateItemCount(bankItemCount);
	}

	public void forceUpdateAfterClick() {

		if (bankGrid != null) {
			bankGrid.clearHoverStates();
		}

		updateManager.forceUpdateAfterAction();

		SwingUtilities.invokeLater(() -> {
			invalidate();
			revalidate();
			repaint();
		});
	}

	public void forceUpdate() {
		updateManager.forceUpdate();
	}

	public int getLeftClickAmount() {
		return header.getLeftClickAmount();
	}

	@Override
	protected void paintComponent(Graphics g) {

		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		super.paintComponent(g);
	}

	@Override
	public void paint(Graphics g) {

		if (getIgnoreRepaint() && isFilteringInProgress) {
			return;
		}

		Graphics gCopy = g.create();
		try {
			super.paint(gCopy);
		} finally {
			gCopy.dispose();
		}
	}

	@Override
	public String getPanelID() {
		return "Bank";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		setVisible(true);
		setOpaque(true);

		if (getParent() != null) {
			hideOtherPanels();
			getParent().setComponentZOrder(this, 0);
			getParent().validate();
			getParent().repaint();
		}

		updateManager.startUpdates();
		updateBankData();

		invalidate();
		revalidate();
		repaint();
	}

	@Override
	public void onDeactivate() {

		updateManager.stopUpdates();

		setVisible(false);

		if (bankGrid != null) {
			bankGrid.clearHoverStates();
		}
	}

	@Override
	public void updateText() {
		repaint();
	}

	@Override
	public void updateDockText(int index, String text) {
		if (text != null && text.contains("Withdraw mode:")) {
			String mode = text.substring(text.indexOf(":") + 1).trim();
			SwingUtilities.invokeLater(() -> {
				header.updateWithdrawMode(mode);
				repaint();
			});
		}
	}

	private void hideOtherPanels() {
		Container parent = getParent();
		if (parent != null) {
			for (Component comp : parent.getComponents()) {
				if (comp != this && comp.isVisible()) {
					comp.setVisible(false);
				}
			}
			setVisible(true);
		}
	}
}
