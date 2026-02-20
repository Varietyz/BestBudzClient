package com.bestbudz.dock.ui.panel.bank.components;

import com.bestbudz.dock.ui.panel.bank.search.BankFilterEngine;
import com.bestbudz.engine.config.ColorConfig;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class BankFilterPanel extends JPanel {

	private final JComboBox<BankFilterEngine.FilterType> filterComboBox;
	private final JComboBox<BankFilterEngine.SortType> sortComboBox;
	private final JTextField searchField;
	private final Timer searchDelayTimer;

	private Consumer<BankFilterEngine.FilterType> filterChangeCallback;
	private Consumer<BankFilterEngine.SortType> sortChangeCallback;
	private Consumer<String> searchChangeCallback;

	public BankFilterPanel() {
		setLayout(new GridLayout(3, 1, 2, 2));
		setBackground(ColorConfig.MAIN_FRAME_COLOR);
		setOpaque(true);
		setBorder(new EmptyBorder(4, 8, 4, 8));

		filterComboBox = createFilterComboBox();
		sortComboBox = createSortComboBox();
		searchField = createSearchField();

		searchDelayTimer = new Timer(200, e -> applySearch());
		searchDelayTimer.setRepeats(false);

		add(createFilterRow());
		add(createSortRow());
		add(createSearchRow());
	}

	private JPanel createFilterRow() {
		JPanel filterRow = new JPanel(new BorderLayout(4, 0));
		filterRow.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		filterRow.setOpaque(true);

		JLabel filterLabel = new JLabel("View:");
		filterLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		filterLabel.setForeground(new Color(180, 180, 180));

		filterRow.add(filterLabel, BorderLayout.WEST);
		filterRow.add(filterComboBox, BorderLayout.CENTER);

		return filterRow;
	}

	private JPanel createSortRow() {
		JPanel sortRow = new JPanel(new BorderLayout(4, 0));
		sortRow.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		sortRow.setOpaque(true);

		JLabel sortLabel = new JLabel("Sort:");
		sortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		sortLabel.setForeground(new Color(180, 180, 180));

		sortRow.add(sortLabel, BorderLayout.WEST);
		sortRow.add(sortComboBox, BorderLayout.CENTER);

		return sortRow;
	}

	private JPanel createSearchRow() {
		JPanel searchRow = new JPanel(new BorderLayout(4, 0));
		searchRow.setBackground(ColorConfig.MAIN_FRAME_COLOR);
		searchRow.setOpaque(true);

		JLabel searchLabel = new JLabel("Find:");
		searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		searchLabel.setForeground(new Color(180, 180, 180));

		searchRow.add(searchLabel, BorderLayout.WEST);
		searchRow.add(searchField, BorderLayout.CENTER);

		return searchRow;
	}

	private JComboBox<BankFilterEngine.FilterType> createFilterComboBox() {
		JComboBox<BankFilterEngine.FilterType> combo = new JComboBox<>(BankFilterEngine.FilterType.values());
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		combo.setBackground(new Color(55, 55, 55));
		combo.setForeground(Color.WHITE);
		combo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
		combo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof BankFilterEngine.FilterType) {
					setText(((BankFilterEngine.FilterType) value).getDisplayName());
				}
				setBackground(isSelected ? new Color(70, 70, 70) : new Color(55, 55, 55));
				setForeground(Color.WHITE);
				return this;
			}
		});
		combo.addActionListener(e -> {
			if (filterChangeCallback != null) {
				BankFilterEngine.FilterType selected = (BankFilterEngine.FilterType) combo.getSelectedItem();
				if (selected != null) {
					filterChangeCallback.accept(selected);
				}
			}
		});
		return combo;
	}

	private JComboBox<BankFilterEngine.SortType> createSortComboBox() {
		JComboBox<BankFilterEngine.SortType> combo = new JComboBox<>(BankFilterEngine.SortType.values());
		combo.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		combo.setBackground(new Color(55, 55, 55));
		combo.setForeground(Color.WHITE);
		combo.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));
		combo.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if (value instanceof BankFilterEngine.SortType) {
					setText(((BankFilterEngine.SortType) value).getDisplayName());
				}
				setBackground(isSelected ? new Color(70, 70, 70) : new Color(55, 55, 55));
				setForeground(Color.WHITE);
				return this;
			}
		});
		combo.addActionListener(e -> {
			if (sortChangeCallback != null) {
				BankFilterEngine.SortType selected = (BankFilterEngine.SortType) combo.getSelectedItem();
				if (selected != null) {
					sortChangeCallback.accept(selected);
				}
			}
		});
		return combo;
	}

	private JTextField createSearchField() {
		JTextField field = new JTextField();
		field.setFont(new Font("Segoe UI", Font.PLAIN, 9));
		field.setBackground(new Color(55, 55, 55));
		field.setForeground(Color.WHITE);
		field.setCaretColor(Color.WHITE);
		field.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 80)));

		field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
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
				if (searchDelayTimer.isRunning()) {
					searchDelayTimer.stop();
				}
				searchDelayTimer.start();
			}
		});

		field.addActionListener(e -> {
			if (searchDelayTimer.isRunning()) {
				searchDelayTimer.stop();
			}
			applySearch();
		});

		field.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (searchDelayTimer.isRunning()) {
					searchDelayTimer.stop();
				}
				applySearch();
			}
		});

		return field;
	}

	private void applySearch() {
		if (searchChangeCallback != null) {
			String searchText = searchField.getText().trim();
			searchChangeCallback.accept(searchText);

			if (!searchText.isEmpty()) {
				searchField.setBackground(new Color(45, 60, 45));
			} else {
				searchField.setBackground(new Color(55, 55, 55));
			}
		}
	}

	public void setFilterChangeCallback(Consumer<BankFilterEngine.FilterType> callback) {
		this.filterChangeCallback = callback;
	}

	public void setSortChangeCallback(Consumer<BankFilterEngine.SortType> callback) {
		this.sortChangeCallback = callback;
	}

	public void setSearchChangeCallback(Consumer<String> callback) {
		this.searchChangeCallback = callback;
	}

	public BankFilterEngine.FilterType getCurrentFilter() {
		return (BankFilterEngine.FilterType) filterComboBox.getSelectedItem();
	}

	public BankFilterEngine.SortType getCurrentSort() {
		return (BankFilterEngine.SortType) sortComboBox.getSelectedItem();
	}

	public String getSearchText() {
		return searchField.getText().trim();
	}

	public void clearSearch() {
		searchField.setText("");
		applySearch();
	}
}
