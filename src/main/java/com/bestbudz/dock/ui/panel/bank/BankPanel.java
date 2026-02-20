package com.bestbudz.dock.ui.panel.bank;

import com.bestbudz.dock.ui.panel.bank.components.*;
import com.bestbudz.dock.ui.panel.bank.grid.BankItemGrid;
import com.bestbudz.dock.ui.panel.bank.search.BankFilterEngine;
import com.bestbudz.dock.ui.panel.bank.state.BankStateManager;
import com.bestbudz.dock.ui.panel.bank.state.BankUpdateManager;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import javax.swing.*;
import java.awt.*;

/**
 * Enhanced Bank Panel with improved painting coordination during filter/sort/search operations
 */
public class BankPanel extends JPanel implements UIPanel, DockTextUpdatable {

	// Core components
	private BankHeader header;
	private BankFilterPanel filterPanel;
	private BankActionPanel actionPanel;
	private BankItemGrid bankGrid;
	private JScrollPane gridScrollPane;

	// State and update management
	private BankStateManager stateManager;
	private BankUpdateManager updateManager;

	// Painting coordination
	private boolean isFilteringInProgress = false;

	public BankPanel() {
		setLayout(new BorderLayout());
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setDoubleBuffered(true); // Enable double buffering for smooth updates
		setPreferredSize(new Dimension(300, 500));
		setMinimumSize(new Dimension(300, 100));
		setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

		initializeManagers();
		initializeComponents();
		setupComponentCallbacks();
		layoutComponents();
	}

	/**
	 * Initializes state and update managers
	 */
	private void initializeManagers() {
		stateManager = new BankStateManager();
		updateManager = new BankUpdateManager(this::updateBankData);
	}

	/**
	 * Creates all UI components
	 */
	private void initializeComponents() {
		header = new BankHeader();
		filterPanel = new BankFilterPanel();
		actionPanel = new BankActionPanel();
		bankGrid = new BankItemGrid(this);
	}

	/**
	 * Sets up communication between components with enhanced coordination
	 */
	private void setupComponentCallbacks() {
		// Enhanced filter panel callbacks with painting coordination
		filterPanel.setFilterChangeCallback(filter -> {
			coordinateFilterOperation(() -> bankGrid.setFilter(filter));
		});

		filterPanel.setSortChangeCallback(sort -> {
			coordinateFilterOperation(() -> bankGrid.setSort(sort));
		});

		filterPanel.setSearchChangeCallback(searchText -> {
			coordinateFilterOperation(() -> bankGrid.setSearchText(searchText));
		});

		// Action panel callback
		actionPanel.setActionCallback(() -> {
			updateManager.enableFastUpdateMode();
			forceUpdateAfterClick();
		});
	}

	/**
	 * Coordinates filter/sort/search operations to prevent painting issues
	 */
	private void coordinateFilterOperation(Runnable operation) {
		if (isFilteringInProgress) {
			return; // Prevent concurrent filtering operations
		}

		isFilteringInProgress = true;

		try {
			// Disable updates during filtering
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

			// Suspend repaints temporarily
			setIgnoreRepaint(true);

			try {
				// Execute the filtering operation
				operation.run();

				// Force layout calculation
				invalidate();
				revalidate();

				// Schedule coordinated repaint
				SwingUtilities.invokeLater(() -> {
					setIgnoreRepaint(false);
					repaint();

					// Restore glass pane state
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

	/**
	 * Arranges components in the panel
	 */
	private void layoutComponents() {
		// Create header section
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		topPanel.setOpaque(true);
		topPanel.add(header, BorderLayout.NORTH);

		// Create controls section
		JPanel controlsContainer = new JPanel(new BorderLayout());
		controlsContainer.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		controlsContainer.add(filterPanel, BorderLayout.CENTER);
		controlsContainer.add(actionPanel, BorderLayout.SOUTH);
		topPanel.add(controlsContainer, BorderLayout.SOUTH);

		// Create scrollable grid
		gridScrollPane = createScrollPane();

		add(topPanel, BorderLayout.NORTH);
		add(gridScrollPane, BorderLayout.CENTER);
	}

	/**
	 * Creates the scroll pane for the item grid with enhanced painting
	 */
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

		// Optimize viewport for better painting
		scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);

		return scrollPane;
	}

	/**
	 * Main update method called by update manager
	 */
	public void updateBankData() {
		if (!Client.loggedIn) {
			handleLoggedOutState();
			return;
		}

		try {
			// Check for changes using state manager
			boolean bankChanged = stateManager.hasBankChanged();
			boolean inventoryChanged = stateManager.hasInventoryChanged();
			boolean itemCountChanged = stateManager.hasItemCountChanged();

			// Update UI if anything changed
			if (bankChanged || inventoryChanged || itemCountChanged) {
				SwingUtilities.invokeLater(() -> {
					updateItemCount();

					if (bankChanged || inventoryChanged) {
						// Full rebuild needed
						bankGrid.updateItems();
					} else {
						// Just amounts changed
						bankGrid.refreshItemAmounts();
					}

					// Coordinated revalidation
					invalidate();
					revalidate();
					repaint();
				});
			}

		} catch (Exception e) {
			SwingUtilities.invokeLater(() -> {
				header.updateItemCount(-1); // Error indicator
				repaint();
			});
		}
	}

	/**
	 * Handles UI state when logged out
	 */
	private void handleLoggedOutState() {
		SwingUtilities.invokeLater(() -> {
			header.updateItemCount(0);
			bankGrid.clearItems();
			stateManager.reset();
			repaint();
		});
	}

	/**
	 * Updates the item count display
	 */
	private void updateItemCount() {
		int bankItemCount = stateManager.getBankItemCount();
		header.updateItemCount(bankItemCount);
	}

	/**
	 * Forces update after user actions with painting coordination
	 */
	public void forceUpdateAfterClick() {
		// Clear any lingering hover states first
		if (bankGrid != null) {
			bankGrid.clearHoverStates();
		}

		// Use update manager for coordinated updates
		updateManager.forceUpdateAfterAction();

		// Schedule a coordinated repaint
		SwingUtilities.invokeLater(() -> {
			invalidate();
			revalidate();
			repaint();
		});
	}

	/**
	 * Forces immediate update
	 */
	public void forceUpdate() {
		updateManager.forceUpdate();
	}

	/**
	 * Gets the current left-click amount setting
	 */
	public int getLeftClickAmount() {
		return header.getLeftClickAmount();
	}

	/**
	 * Override paintComponent for enhanced background painting
	 */
	@Override
	protected void paintComponent(Graphics g) {
		// Always paint background first
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		// Call super to paint children
		super.paintComponent(g);
	}

	/**
	 * Override paint for coordinated component painting
	 */
	@Override
	public void paint(Graphics g) {
		// Don't paint if filtering is in progress and ignoreRepaint is set
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

	// UIPanel interface implementation
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

		// Start updates
		updateManager.startUpdates();
		updateBankData();

		// Coordinated invalidation
		invalidate();
		revalidate();
		repaint();
	}

	@Override
	public void onDeactivate() {
		// Stop updates
		updateManager.stopUpdates();

		setVisible(false);

		if (bankGrid != null) {
			bankGrid.clearHoverStates();
		}
	}

	// DockTextUpdatable interface implementation
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

	/**
	 * Hides other panels in the same container
	 */
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