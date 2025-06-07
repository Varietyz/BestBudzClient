package com.bestbudz.dock.ui.panel.bank;

import com.bestbudz.dock.util.ButtonHandler;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;
import com.bestbudz.engine.config.ColorConfig;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.RSInterface;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Arrays;

/**
 * High-performance Bank Panel with intelligent updates and minimal redraws
 */
public class BankPanel extends JPanel implements UIPanel, DockTextUpdatable {

	// Button IDs - ORIGINAL WORKING VALUES
	private static final int DOCK_DEPOSIT_ALL_INVENTORY = 115247;
	private static final int DOCK_DEPOSIT_ALL_EQUIPMENT = 115248;
	private static final int DOCK_TOGGLE_WITHDRAW_MODE = 115249;

	// Update control
	private static final int UPDATE_INTERVAL_MS = 500; // Slower, more efficient updates
	private static final int FAST_UPDATE_INTERVAL_MS = 100; // For when rapid updates are needed

	// UI Components
	private JLabel titleLabel;
	private JLabel itemCountLabel;
	private JTextField amountField;
	private JLabel withdrawModeLabel;
	private BankItemGrid bankGrid;
	private Timer updateTimer;

	// State tracking for efficient updates
	private int lastItemCount = -1;
	private int lastInventoryItemCount = -1;
	private int[] lastBankStateHash = null;
	private int[] lastInventoryStateHash = null;
	private boolean isInFastUpdateMode = false;
	private Timer fastUpdateModeTimer;

	// Filter components
	private JComboBox<BankItemGrid.FilterType> filterComboBox;
	private JComboBox<BankItemGrid.SortType> sortComboBox;
	private JTextField searchField;

	// Left-click settings
	private int leftClickAmount = 1;

	public BankPanel() {
		setLayout(new BorderLayout());
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setPreferredSize(new Dimension(300, 500));
		setMinimumSize(new Dimension(300, 100));
		setMaximumSize(new Dimension(300, Integer.MAX_VALUE));

		initComponents();
	}

	private void initComponents() {
		// Header
		JPanel header = createHeaderPanel();

		// Filter panel
		JPanel filterPanel = createFilterPanel();

		// Action buttons
		JPanel actionPanel = createActionPanel();

		// Single unified item grid for both inventory and bank items
		bankGrid = new BankItemGrid(this);

		// Create scroll pane for the unified grid
		JScrollPane gridScrollPane = new JScrollPane(bankGrid);
		gridScrollPane.setBorder(null);
		gridScrollPane.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		gridScrollPane.getViewport().setBackground(ColorConfig.MAIN_FRAME_COLOR);
		gridScrollPane.getViewport().setOpaque(true);
		gridScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		gridScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		gridScrollPane.getVerticalScrollBar().setUnitIncrement(16);
		gridScrollPane.setOpaque(true);

		// Create a combined top panel
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		topPanel.setOpaque(true);
		topPanel.add(header, BorderLayout.NORTH);

		JPanel controlsContainer = new JPanel(new BorderLayout());
		controlsContainer.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		controlsContainer.add(filterPanel, BorderLayout.CENTER);
		controlsContainer.add(actionPanel, BorderLayout.SOUTH);
		topPanel.add(controlsContainer, BorderLayout.SOUTH);

		add(topPanel, BorderLayout.NORTH);
		add(gridScrollPane, BorderLayout.CENTER);
	}

	private JPanel createHeaderPanel() {
		JPanel header = new JPanel(new BorderLayout());
		header.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		header.setOpaque(true);
		header.setBorder(new EmptyBorder(8, 12, 8, 12));

		titleLabel = new JLabel("Bank");
		titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setOpaque(false);

		itemCountLabel = new JLabel("0/816");
		itemCountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		itemCountLabel.setForeground(new Color(218, 165, 32));
		itemCountLabel.setOpaque(false);

		JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 4));
		controlPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		controlPanel.setOpaque(true);

		// Amount input field
		amountField = new JTextField("1", 4);
		amountField.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		amountField.setBackground(new Color(55, 55, 55));
		amountField.setForeground(Color.WHITE);
		amountField.setCaretColor(Color.WHITE);
		amountField.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
		amountField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				updateLeftClickAmount();
			}
		});
		amountField.addActionListener(e -> updateLeftClickAmount());

		// Withdraw mode label (Item/Note)
		withdrawModeLabel = new JLabel("Item");
		withdrawModeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
		withdrawModeLabel.setForeground(new Color(200, 200, 200));
		withdrawModeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		withdrawModeLabel.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(java.awt.event.MouseEvent e) {
				toggleWithdrawMode();
			}
		});

		JLabel amountLabel = new JLabel("Amount:");
		amountLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		amountLabel.setForeground(new Color(180, 180, 180));

		controlPanel.add(amountLabel);
		controlPanel.add(amountField);
		controlPanel.add(withdrawModeLabel);

		header.add(titleLabel, BorderLayout.WEST);
		header.add(controlPanel, BorderLayout.CENTER);
		header.add(itemCountLabel, BorderLayout.EAST);

		return header;
	}

	private JPanel createFilterPanel() {
		JPanel filterPanel = new JPanel(new GridLayout(3, 1, 2, 2));
		filterPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		filterPanel.setOpaque(true);
		filterPanel.setBorder(new EmptyBorder(4, 8, 4, 8));

		// Filter dropdown
		JPanel filterRow = new JPanel(new BorderLayout(4, 0));
		filterRow.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		filterRow.setOpaque(true);

		JLabel filterLabel = new JLabel("Filter:");
		filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		filterLabel.setForeground(new Color(180, 180, 180));

		filterComboBox = new JComboBox<>(BankItemGrid.FilterType.values());
		filterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		filterComboBox.setBackground(new Color(55, 55, 55));
		filterComboBox.setForeground(Color.WHITE);
		filterComboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
		filterComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof BankItemGrid.FilterType) {
					setText(((BankItemGrid.FilterType) value).getDisplayName());
				}
				setBackground(isSelected ? new Color(70, 70, 70) : new Color(55, 55, 55));
				setForeground(Color.WHITE);
				return this;
			}
		});
		filterComboBox.addActionListener(e -> {
			BankItemGrid.FilterType selectedFilter = (BankItemGrid.FilterType) filterComboBox.getSelectedItem();
			if (selectedFilter != null && bankGrid != null) {
				bankGrid.setFilter(selectedFilter);
			}
		});

		filterRow.add(filterLabel, BorderLayout.WEST);
		filterRow.add(filterComboBox, BorderLayout.CENTER);

		// Sort dropdown
		JPanel sortRow = new JPanel(new BorderLayout(4, 0));
		sortRow.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		sortRow.setOpaque(true);

		JLabel sortLabel = new JLabel("Sort:");
		sortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		sortLabel.setForeground(new Color(180, 180, 180));

		sortComboBox = new JComboBox<>(BankItemGrid.SortType.values());
		sortComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		sortComboBox.setBackground(new Color(55, 55, 55));
		sortComboBox.setForeground(Color.WHITE);
		sortComboBox.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
		sortComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof BankItemGrid.SortType) {
					setText(((BankItemGrid.SortType) value).getDisplayName());
				}
				setBackground(isSelected ? new Color(70, 70, 70) : new Color(55, 55, 55));
				setForeground(Color.WHITE);
				return this;
			}
		});
		sortComboBox.addActionListener(e -> {
			BankItemGrid.SortType selectedSort = (BankItemGrid.SortType) sortComboBox.getSelectedItem();
			if (selectedSort != null && bankGrid != null) {
				bankGrid.setSort(selectedSort);
			}
		});

		sortRow.add(sortLabel, BorderLayout.WEST);
		sortRow.add(sortComboBox, BorderLayout.CENTER);

		// Search field with FIXED real-time search
		JPanel searchRow = new JPanel(new BorderLayout(4, 0));
		searchRow.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		searchRow.setOpaque(true);

		JLabel searchLabel = new JLabel("Search:");
		searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		searchLabel.setForeground(new Color(180, 180, 180));

		searchField = new JTextField();
		searchField.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		searchField.setBackground(new Color(55, 55, 55));
		searchField.setForeground(Color.WHITE);
		searchField.setCaretColor(Color.WHITE);
		searchField.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

		// REAL-TIME SEARCH - Fixed implementation
		Timer searchDelayTimer = new Timer(200, e -> applySearch()); // 200ms delay for better performance
		searchDelayTimer.setRepeats(false); // Only fire once per delay period

		searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
			@Override
			public void insertUpdate(javax.swing.event.DocumentEvent e) {
				triggerSearch();
			}

			@Override
			public void removeUpdate(javax.swing.event.DocumentEvent e) {
				triggerSearch();
			}

			@Override
			public void changedUpdate(javax.swing.event.DocumentEvent e) {
				triggerSearch();
			}

			private void triggerSearch() {
				// Stop any existing timer and restart it
				if (searchDelayTimer.isRunning()) {
					searchDelayTimer.stop();
				}
				searchDelayTimer.start();
			}
		});

		// Also keep the Enter key and focus lost handlers for immediate search
		searchField.addActionListener(e -> {
			if (searchDelayTimer.isRunning()) {
				searchDelayTimer.stop(); // Cancel delayed search
			}
			applySearch(); // Immediate search on Enter
		});

		searchField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (searchDelayTimer.isRunning()) {
					searchDelayTimer.stop(); // Cancel delayed search
				}
				applySearch(); // Immediate search on focus lost
			}
		});

		searchRow.add(searchLabel, BorderLayout.WEST);
		searchRow.add(searchField, BorderLayout.CENTER);

		filterPanel.add(filterRow);
		filterPanel.add(sortRow);
		filterPanel.add(searchRow);

		return filterPanel;
	}

	private void applySearch() {
		if (bankGrid != null) {
			String searchText = searchField.getText().trim();
			bankGrid.setSearchText(searchText);

			// Optional: Visual feedback for active search
			if (!searchText.isEmpty()) {
				searchField.setBackground(new Color(45, 60, 45)); // Slight green tint when searching
			} else {
				searchField.setBackground(new Color(55, 55, 55)); // Normal background
			}
		}
	}

	private JPanel createActionPanel() {
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 6));
		actionPanel.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		actionPanel.setOpaque(true);

		JButton depositBox = createButton("Deposit Box", this::depositInventory);
		JButton depositEquip = createButton("Deposit Equipment", this::depositEquipment);

		actionPanel.add(depositBox);
		actionPanel.add(depositEquip);

		return actionPanel;
	}

	private JButton createButton(String text, Runnable action) {
		JButton button = new JButton(text);
		button.setFont(new Font("Segoe UI", Font.PLAIN, 10));

		Dimension buttonSize = new Dimension(120, 26);
		button.setPreferredSize(buttonSize);
		button.setMinimumSize(buttonSize);
		button.setMaximumSize(buttonSize);

		button.setBackground(ColorConfig.GRAPHITE_COLOR);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.addActionListener(e -> {
			if (Client.loggedIn) {
				action.run();
			}
		});
		return button;
	}

	private void updateLeftClickAmount() {
		try {
			String text = amountField.getText().trim().toLowerCase();
			if (text.equals("all") || text.equals("-1")) {
				leftClickAmount = -1;
			} else {
				leftClickAmount = Math.max(1, Integer.parseInt(text));
				if (leftClickAmount != Integer.parseInt(text)) {
					amountField.setText(String.valueOf(leftClickAmount));
				}
			}
		} catch (NumberFormatException e) {
			leftClickAmount = 1;
			amountField.setText("1");
		}
	}

	private void toggleWithdrawMode() {
		try {
			ButtonHandler.sendClick(DOCK_TOGGLE_WITHDRAW_MODE);
		} catch (Exception e) {
			System.err.println("Error toggling withdraw mode: " + e.getMessage());
		}
	}

	private void depositInventory() {
		try {
			ButtonHandler.sendClick(DOCK_DEPOSIT_ALL_INVENTORY);
			enableFastUpdateMode();
		} catch (Exception e) {
			System.err.println("Error depositing inventory: " + e.getMessage());
		}
	}

	private void depositEquipment() {
		try {
			ButtonHandler.sendClick(DOCK_DEPOSIT_ALL_EQUIPMENT);
			enableFastUpdateMode();
		} catch (Exception e) {
			System.err.println("Error depositing equipment: " + e.getMessage());
		}
	}

	/**
	 * Enable fast update mode for a short period after user actions
	 */
	private void enableFastUpdateMode() {
		if (!isInFastUpdateMode) {
			isInFastUpdateMode = true;

			// Stop normal timer and start fast timer
			if (updateTimer != null && updateTimer.isRunning()) {
				updateTimer.stop();
			}

			updateTimer = new Timer(FAST_UPDATE_INTERVAL_MS, e -> updateBankData());
			updateTimer.start();
		}

		// Reset the fast mode timer
		if (fastUpdateModeTimer != null && fastUpdateModeTimer.isRunning()) {
			fastUpdateModeTimer.stop();
		}

		fastUpdateModeTimer = new Timer(2000, e -> disableFastUpdateMode()); // 2 seconds of fast updates
		fastUpdateModeTimer.setRepeats(false);
		fastUpdateModeTimer.start();
	}

	private void disableFastUpdateMode() {
		if (isInFastUpdateMode) {
			isInFastUpdateMode = false;

			// Stop fast timer and restart normal timer
			if (updateTimer != null && updateTimer.isRunning()) {
				updateTimer.stop();
			}

			updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateBankData());
			updateTimer.start();
		}
	}

	// Public methods for item panels to access
	public int getLeftClickAmount() {
		return leftClickAmount;
	}

	// Method to force update when items are deposited/withdrawn
	public void forceUpdate() {
		updateBankData();
	}

	// Method to force update after any click action
	public void forceUpdateAfterClick() {
		// Clear any lingering hover states first
		if (bankGrid != null) {
			bankGrid.clearHoverStates();
		}

		// Enable fast update mode for immediate responsiveness
		enableFastUpdateMode();

		// Immediate refresh
		Timer immediateRefresh = new Timer(50, e -> {
			if (bankGrid != null) {
				bankGrid.refreshItemAmounts();
			}
		});
		immediateRefresh.setRepeats(false);
		immediateRefresh.start();

		// Second refresh for server response
		Timer serverRefresh = new Timer(300, e -> {
			if (bankGrid != null) {
				bankGrid.refreshItemAmounts();
			}
		});
		serverRefresh.setRepeats(false);
		serverRefresh.start();
	}

	/**
	 * Efficient state change detection using hash comparison
	 */
	private boolean hasInventoryChanged() {
		try {
			RSInterface inventoryInterface = RSInterface.interfaceCache[3214];
			if (inventoryInterface == null || inventoryInterface.inv == null) {
				return lastInventoryStateHash != null;
			}

			int[] currentHash = computeStateHash(inventoryInterface.inv, inventoryInterface.invStackSizes);

			if (lastInventoryStateHash == null || !Arrays.equals(lastInventoryStateHash, currentHash)) {
				lastInventoryStateHash = currentHash;
				return true;
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	private boolean hasBankChanged() {
		try {
			RSInterface bankInterface = RSInterface.interfaceCache[5382];
			if (bankInterface == null || bankInterface.inv == null) {
				return lastBankStateHash != null;
			}

			int[] currentHash = computeStateHash(bankInterface.inv, bankInterface.invStackSizes);

			if (lastBankStateHash == null || !Arrays.equals(lastBankStateHash, currentHash)) {
				lastBankStateHash = currentHash;
				return true;
			}
			return false;
		} catch (Exception e) {
			return true;
		}
	}

	/**
	 * Compute a hash of the state for efficient comparison
	 */
	private int[] computeStateHash(int[] items, int[] amounts) {
		if (items == null || amounts == null) {
			return new int[0];
		}

		int maxIndex = Math.min(items.length, amounts.length);
		int[] hash = new int[maxIndex];

		for (int i = 0; i < maxIndex; i++) {
			// Combine item ID and amount into a single hash value
			hash[i] = items[i] * 31 + amounts[i];
		}

		return hash;
	}

	private int countItems(RSInterface itemInterface) {
		if (itemInterface == null || itemInterface.inv == null || itemInterface.invStackSizes == null) {
			return 0;
		}

		int count = 0;
		int maxIndex = Math.min(itemInterface.inv.length, itemInterface.invStackSizes.length);

		for (int i = 0; i < maxIndex; i++) {
			if (itemInterface.inv[i] > 0 && itemInterface.invStackSizes[i] > 0) {
				count++;
			}
		}

		return count;
	}

	public void updateBankData() {
		if (!Client.loggedIn) {
			if (lastItemCount != 0 || lastInventoryItemCount != 0) {
				lastItemCount = 0;
				lastInventoryItemCount = 0;
				lastBankStateHash = null;
				lastInventoryStateHash = null;
				SwingUtilities.invokeLater(() -> {
					itemCountLabel.setText("0/816");
					bankGrid.clearItems();
					repaint();
				});
			}
			return;
		}

		try {
			RSInterface bankInterface = RSInterface.interfaceCache[5382];
			RSInterface inventoryInterface = RSInterface.interfaceCache[3214];

			boolean bankChanged = hasBankChanged();
			boolean inventoryChanged = hasInventoryChanged();

			// Count items efficiently
			int bankItemCount = countItems(bankInterface);
			int inventoryItemCount = countItems(inventoryInterface);

			// Update if anything changed
			if (bankChanged || inventoryChanged ||
				bankItemCount != lastItemCount ||
				inventoryItemCount != lastInventoryItemCount) {

				lastItemCount = bankItemCount;
				lastInventoryItemCount = inventoryItemCount;

				SwingUtilities.invokeLater(() -> {
					itemCountLabel.setText(bankItemCount + "/816");

					if (bankChanged || inventoryChanged) {
						// Full rebuild needed
						bankGrid.updateItems();
					} else {
						// Just amounts changed
						bankGrid.refreshItemAmounts();
					}

					revalidate();
					repaint();
				});
			}

		} catch (Exception e) {
			SwingUtilities.invokeLater(() -> {
				itemCountLabel.setText("Error");
				repaint();
			});
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

		updateBankData();

		if (updateTimer != null && updateTimer.isRunning()) {
			updateTimer.stop();
		}
		updateTimer = new Timer(UPDATE_INTERVAL_MS, e -> updateBankData());
		updateTimer.start();

		invalidate();
		revalidate();
		repaint();
	}

	@Override
	public void onDeactivate() {
		if (updateTimer != null && updateTimer.isRunning()) {
			updateTimer.stop();
		}

		if (fastUpdateModeTimer != null && fastUpdateModeTimer.isRunning()) {
			fastUpdateModeTimer.stop();
		}

		setVisible(false);

		if (bankGrid != null) {
			bankGrid.clearHoverStates();
		}

		isInFastUpdateMode = false;
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

	@Override
	public void updateText() {
		repaint();
	}

	@Override
	public void updateDockText(int index, String text) {
		if (text != null && text.contains("Withdraw mode:")) {
			String mode = text.substring(text.indexOf(":") + 1).trim();
			SwingUtilities.invokeLater(() -> {
				withdrawModeLabel.setText(mode);
				repaint();
			});
		}
	}
}