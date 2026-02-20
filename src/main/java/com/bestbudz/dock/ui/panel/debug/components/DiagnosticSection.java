package com.bestbudz.dock.ui.panel.debug.components;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticSection extends JPanel {

	private final String title;
	private final Color headerColor;
	private final Color borderColor;
	private final List<DataRow> rows;
	private JPanel contentPanel;
	private int containerWidth;

	public DiagnosticSection(String title, Color headerColor, Color borderColor) {
		this.title = title;
		this.headerColor = headerColor;
		this.borderColor = borderColor;
		this.rows = new ArrayList<>();
		this.containerWidth = 300;

		initializeSection();
	}

	private void initializeSection() {
		setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(DiagnosticStyle.BACKGROUND_SECTION);
		setBorder(DiagnosticStyle.createSectionBorder(borderColor));

		contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		contentPanel.setOpaque(false);
		contentPanel.setBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0));

		add(contentPanel, BorderLayout.CENTER);
	}

	public void addRow(String key, String value, Color valueColor) {
		DataRow row = new DataRow(key, value, valueColor);
		rows.add(row);

		JPanel rowPanel = row.createRowPanel(containerWidth);

		addCopyToClipboardListener(rowPanel, value);

		contentPanel.add(rowPanel);
	}

	private void addCopyToClipboardListener(JPanel rowPanel, String value) {
		MouseAdapter copyListener = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				copyToClipboard(value);

				Color originalBg = rowPanel.getBackground();
				rowPanel.setBackground(DiagnosticStyle.BUTTON_PRIMARY.darker());
				rowPanel.setOpaque(true);

				Timer timer = new Timer(150, evt -> {
					rowPanel.setBackground(originalBg);
					rowPanel.setOpaque(false);
					rowPanel.repaint();
				});
				timer.setRepeats(false);
				timer.start();
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				rowPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				rowPanel.setCursor(Cursor.getDefaultCursor());
			}
		};

		rowPanel.addMouseListener(copyListener);

		addMouseListenerRecursively(rowPanel, copyListener);
	}

	private void addMouseListenerRecursively(Container container, MouseAdapter listener) {
		for (Component component : container.getComponents()) {
			component.addMouseListener(listener);
			if (component instanceof Container) {
				addMouseListenerRecursively((Container) component, listener);
			}
		}
	}

	private void copyToClipboard(String text) {
		try {
			StringSelection selection = new StringSelection(text);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(selection, null);
			System.out.println("Copied to clipboard: " + text);
		} catch (Exception e) {
			System.err.println("Failed to copy to clipboard: " + e.getMessage());
		}
	}

	public void clearRows() {
		contentPanel.removeAll();
		rows.clear();
		revalidate();
		repaint();
	}

	public void updateFonts(int newContainerWidth) {
		this.containerWidth = newContainerWidth;

		rebuild();
	}

	public int calculatePreferredHeight() {
		int rowHeight = 18;
		int borderPadding = DiagnosticStyle.SECTION_INTERNAL_PADDING * 2;

		return (rows.size() * rowHeight) + borderPadding;
	}

	@Override
	public Dimension getPreferredSize() {
		int width = Math.max(DiagnosticStyle.MIN_SECTION_WIDTH, getWidth());
		int height = calculatePreferredHeight();
		return new Dimension(width, height);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(DiagnosticStyle.MIN_SECTION_WIDTH, calculatePreferredHeight());
	}

	public String getTitle() {
		return title;
	}

	public int getRowCount() {
		return rows.size();
	}

	public void rebuild() {
		clearRows();
		for (DataRow row : rows) {
			JPanel rowPanel = row.createRowPanel(containerWidth);
			addCopyToClipboardListener(rowPanel, row.getValue());
			contentPanel.add(rowPanel);
		}
		revalidate();
		repaint();
	}
}
