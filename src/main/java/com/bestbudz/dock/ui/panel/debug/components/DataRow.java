package com.bestbudz.dock.ui.panel.debug.components;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Represents a single data row with key-value pair
 * Now with proper padding and size management
 */
public class DataRow {

	private final String key;
	private String value;
	private Color valueColor;

	public DataRow(String key, String value, Color valueColor) {
		this.key = key;
		this.value = value;
		this.valueColor = valueColor;
	}

	/**
	 * Create the visual panel for this row with proper padding and sizing
	 */
	public JPanel createRowPanel(int containerWidth) {
		JPanel row = new JPanel(new BorderLayout(4, 0));
		row.setOpaque(false);

		// Set proper size constraints
		int rowHeight = 16;
		row.setPreferredSize(new Dimension(0, rowHeight));
		row.setMinimumSize(new Dimension(0, rowHeight));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, rowHeight));

		// Add padding to prevent text cutoff
		row.setBorder(new EmptyBorder(2, 4, 2, 4));

		// Create labels with proper fonts
		JLabel keyLabel = new JLabel(key + ":");
		keyLabel.setForeground(DiagnosticStyle.TEXT_SECONDARY);
		keyLabel.setFont(DiagnosticStyle.getDataFont(containerWidth));
		keyLabel.setVerticalAlignment(SwingConstants.CENTER);

		JLabel valueLabel = new JLabel(value);
		valueLabel.setForeground(valueColor);
		valueLabel.setFont(DiagnosticStyle.getBoldDataFont(containerWidth));
		valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		valueLabel.setVerticalAlignment(SwingConstants.CENTER);

		// Ensure labels don't get cut off
		keyLabel.setPreferredSize(new Dimension(keyLabel.getPreferredSize().width, rowHeight - 4));
		valueLabel.setPreferredSize(new Dimension(valueLabel.getPreferredSize().width, rowHeight - 4));

		row.add(keyLabel, BorderLayout.WEST);
		row.add(valueLabel, BorderLayout.EAST);

		return row;
	}

	/**
	 * Update the value and color
	 */
	public void updateValue(String newValue, Color newColor) {
		this.value = newValue;
		this.valueColor = newColor;
	}

	// Getters
	public String getKey() { return key; }
	public String getValue() { return value; }
	public Color getValueColor() { return valueColor; }
}