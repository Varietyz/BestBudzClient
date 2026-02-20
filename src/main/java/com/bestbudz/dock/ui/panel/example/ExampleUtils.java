package com.bestbudz.dock.ui.panel.example;

import com.bestbudz.engine.core.Client;
import com.bestbudz.dock.util.ButtonHandler;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ExampleUtils {

	public static String processExampleData(String input) {
		if (input == null || input.trim().isEmpty()) {
			return "No data";
		}

		return input.trim().toUpperCase();
	}

	public static boolean isValidExampleValue(int value) {
		return value >= ExampleConfig.MIN_VALUE && value <= ExampleConfig.MAX_VALUE;
	}

	public static String formatExampleValue(long value) {
		if (value >= 1_000_000) {
			return String.format("%.1fM", value / 1_000_000.0);
		} else if (value >= 1_000) {
			return String.format("%.1fK", value / 1_000.0);
		}
		return String.valueOf(value);
	}

	public static List<String> getExampleDataRows() {
		List<String> rows = new ArrayList<>();

		for (int i = 0; i < ExampleConfig.MAX_DISPLAY_ROWS; i++) {
			rows.add("Example Item " + (i + 1));
		}

		return rows;
	}

	public static void sendExampleAction(String action, Object data) {
		if (!Client.loggedIn) {
			System.out.println("Cannot send action - not logged in");
			return;
		}

		int interfaceId = ExampleConfig.getActionInterfaceId(action);
		if (interfaceId != -1) {
			ButtonHandler.sendClick(interfaceId);
			System.out.println("Sent example action: " + action + " with interface ID: " + interfaceId);
		}
	}

	public static int calculateProgress(int current, int maximum) {
		if (maximum <= 0) return 0;
		return Math.min(100, Math.max(0, (current * 100) / maximum));
	}

	public static Color getValueColor(int value) {
		if (value <= ExampleConfig.LOW_THRESHOLD) {
			return ExampleStyle.LOW_VALUE_COLOR;
		} else if (value <= ExampleConfig.MEDIUM_THRESHOLD) {
			return ExampleStyle.MEDIUM_VALUE_COLOR;
		} else {
			return ExampleStyle.HIGH_VALUE_COLOR;
		}
	}

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

	public static List<String> sortExampleItems(List<String> items, boolean ascending) {
		List<String> sorted = new ArrayList<>(items);

		if (ascending) {
			sorted.sort(String::compareToIgnoreCase);
		} else {
			sorted.sort((a, b) -> b.compareToIgnoreCase(a));
		}

		return sorted;
	}

	public static String createExampleTooltip(String item) {
		return "<html>" +
			"<b>" + item + "</b><br>" +
			"Example tooltip information<br>" +
			"<i>Click for more details</i>" +
			"</html>";
	}

	public static void cleanup() {

		System.out.println("Example panel cleanup completed");
	}

	public static void initialize() {

		System.out.println("Example panel initialized");
	}

	public static void handleError(String operation, Exception error) {
		System.err.println("Error in example panel during " + operation + ": " + error.getMessage());

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
