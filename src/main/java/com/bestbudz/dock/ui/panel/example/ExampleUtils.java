package com.bestbudz.dock.ui.panel.example;

import com.bestbudz.engine.core.Client;
import com.bestbudz.dock.util.ButtonHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for Example Panel specific operations
 *
 * PURPOSE:
 * - Contains all business logic for the Example panel
 * - Handles data processing and calculations
 * - Provides helper methods for panel operations
 * - Manages panel-specific state and operations
 *
 * WHEN TO USE:
 * - Panel-specific calculations or data processing
 * - Complex operations that would clutter the main panel class
 * - Reusable methods that might be called from multiple places
 * - Data validation and formatting
 * - API calls or server communication specific to this panel
 */
public class ExampleUtils {

	/**
	 * Example utility method - replace with your panel's specific logic
	 * @param input Sample input parameter
	 * @return Processed result
	 */
	public static String processExampleData(String input) {
		if (input == null || input.trim().isEmpty()) {
			return "No data";
		}

		// Add your processing logic here
		return input.trim().toUpperCase();
	}

	/**
	 * Example validation method
	 * @param value Value to validate
	 * @return true if valid, false otherwise
	 */
	public static boolean isValidExampleValue(int value) {
		return value >= ExampleConfig.MIN_VALUE && value <= ExampleConfig.MAX_VALUE;
	}

	/**
	 * Example data formatter
	 * @param value Raw value
	 * @return Formatted string for display
	 */
	public static String formatExampleValue(long value) {
		if (value >= 1_000_000) {
			return String.format("%.1fM", value / 1_000_000.0);
		} else if (value >= 1_000) {
			return String.format("%.1fK", value / 1_000.0);
		}
		return String.valueOf(value);
	}

	/**
	 * Example method to getPooledStream data rows for display
	 * @return List of formatted data for the panel
	 */
	public static List<String> getExampleDataRows() {
		List<String> rows = new ArrayList<>();

		// Replace with your actual data source
		for (int i = 0; i < ExampleConfig.MAX_DISPLAY_ROWS; i++) {
			rows.add("Example Item " + (i + 1));
		}

		return rows;
	}

	/**
	 * Example server communication method
	 * @param action Action identifier
	 * @param data Optional data to send
	 */
	public static void sendExampleAction(String action, Object data) {
		if (!Client.loggedIn) {
			System.out.println("Cannot send action - not logged in");
			return;
		}

		// Replace with your actual server communication logic
		int interfaceId = ExampleConfig.getActionInterfaceId(action);
		if (interfaceId != -1) {
			ButtonHandler.sendClick(interfaceId);
			System.out.println("Sent example action: " + action + " with interface ID: " + interfaceId);
		}
	}

	/**
	 * Example method to calculate progress or status
	 * @param current Current value
	 * @param maximum Maximum value
	 * @return Progress percentage (0-100)
	 */
	public static int calculateProgress(int current, int maximum) {
		if (maximum <= 0) return 0;
		return Math.min(100, Math.max(0, (current * 100) / maximum));
	}

	/**
	 * Example color determination based on value
	 * @param value Input value
	 * @return Appropriate color for the value
	 */
	public static Color getValueColor(int value) {
		if (value <= ExampleConfig.LOW_THRESHOLD) {
			return ExampleStyle.LOW_VALUE_COLOR;
		} else if (value <= ExampleConfig.MEDIUM_THRESHOLD) {
			return ExampleStyle.MEDIUM_VALUE_COLOR;
		} else {
			return ExampleStyle.HIGH_VALUE_COLOR;
		}
	}

	/**
	 * Example filtering method
	 * @param items List of items to filter
	 * @param filterText Filter criteria
	 * @return Filtered list
	 */
	public static List<String> filterExampleItems(List<String> items, String filterText) {
		if (filterText == null || filterText.trim().isEmpty()) {
			return new ArrayList<>(items);
		}

		List<String> filtered = new ArrayList<>();
		String lowerFilter = filterText.toLowerCase().trim();

		for (String item : items) {
			if (item.toLowerCase().contains(lowerFilter)) {
				filtered.add(item);
			}
		}

		return filtered;
	}

	/**
	 * Example sorting method
	 * @param items List of items to sort
	 * @param ascending true for ascending, false for descending
	 * @return Sorted list
	 */
	public static List<String> sortExampleItems(List<String> items, boolean ascending) {
		List<String> sorted = new ArrayList<>(items);

		if (ascending) {
			sorted.sort(String::compareToIgnoreCase);
		} else {
			sorted.sort((a, b) -> b.compareToIgnoreCase(a));
		}

		return sorted;
	}

	/**
	 * Example method to getPooledStream tooltip text
	 * @param item Item to getPooledStream tooltip for
	 * @return HTML formatted tooltip
	 */
	public static String createExampleTooltip(String item) {
		return "<html>" +
			"<b>" + item + "</b><br>" +
			"Example tooltip information<br>" +
			"<i>Click for more details</i>" +
			"</html>";
	}

	/**
	 * Example cleanup method (called when panel is deactivated)
	 */
	public static void cleanup() {
		// Clean up resources, stop timers, clear caches, etc.
		System.out.println("Example panel cleanup completed");
	}

	/**
	 * Example initialization method (called when panel is activated)
	 */
	public static void initialize() {
		// Initialize data, start timers, load resources, etc.
		System.out.println("Example panel initialized");
	}

	/**
	 * Example error handling method
	 * @param operation Description of the operation that failed
	 * @param error The error that occurred
	 */
	public static void handleError(String operation, Exception error) {
		System.err.println("Error in example panel during " + operation + ": " + error.getMessage());

		// You could show user-friendly error messages here
		if (ExampleConfig.SHOW_ERROR_DIALOGS) {
			JOptionPane.showMessageDialog(
				null,
				"An error occurred: " + operation,
				"Example Panel Error",
				JOptionPane.ERROR_MESSAGE
			);
		}
	}
}