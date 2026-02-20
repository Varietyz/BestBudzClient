package com.bestbudz.dock.ui.panel.debug.style;

import com.bestbudz.dock.ui.panel.debug.components.DiagnosticSection;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResponsiveLayout {

	private int currentColumns = 1;
	private int currentWidth = 0;

	public int calculateOptimalColumns(int availableWidth) {
		if (availableWidth < DiagnosticStyle.BREAKPOINT_SM) return 1;
		if (availableWidth < DiagnosticStyle.BREAKPOINT_MD) return 2;
		if (availableWidth < DiagnosticStyle.BREAKPOINT_LG) return 3;
		return Math.min(4, availableWidth / (DiagnosticStyle.MIN_SECTION_WIDTH + DiagnosticStyle.SECTION_GAP));
	}

	public int calculateSectionWidth(int availableWidth, int columns) {
		int contentPadding = DiagnosticStyle.CONTENT_PADDING * 2;
		int gapSpace = DiagnosticStyle.SECTION_GAP * (columns + 1);
		int usableWidth = availableWidth - contentPadding - gapSpace;
		int sectionWidth = usableWidth / columns;

		return Math.max(DiagnosticStyle.MIN_SECTION_WIDTH,
			Math.min(DiagnosticStyle.MAX_SECTION_WIDTH, sectionWidth));
	}

	public void setupResponsiveGrid(JPanel contentPanel, List<JPanel> sections, int availableWidth) {
		int optimalColumns = calculateOptimalColumns(availableWidth);
		int sectionWidth = calculateSectionWidth(availableWidth, optimalColumns);

		if (Math.abs(optimalColumns - currentColumns) > 0 || Math.abs(availableWidth - currentWidth) > 50) {
			currentColumns = optimalColumns;
			currentWidth = availableWidth;

			contentPanel.setLayout(new GridBagLayout());
			contentPanel.removeAll();

			if (sections.isEmpty()) {
				contentPanel.revalidate();
				contentPanel.repaint();
				return;
			}

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 1.0;
			gbc.weighty = 0.0;

			int totalRows = (int) Math.ceil((double) sections.size() / currentColumns);

			for (int i = 0; i < sections.size(); i++) {
				gbc.gridx = i % currentColumns;
				gbc.gridy = i / currentColumns;

				gbc.insets = new Insets(
					DiagnosticStyle.SECTION_GAP,
					DiagnosticStyle.SECTION_GAP,
					DiagnosticStyle.SECTION_GAP,
					DiagnosticStyle.SECTION_GAP
				);

				if (gbc.gridy == totalRows - 1) {
					gbc.weighty = 1.0;
				} else {
					gbc.weighty = 0.0;
				}

				JPanel section = sections.get(i);
				updateSectionSize(section, sectionWidth);

				section.setOpaque(true);
				if (section instanceof DiagnosticSection) {
					section.setBackground(DiagnosticStyle.BACKGROUND_SECTION);
				}

				contentPanel.add(section, gbc);
			}

			contentPanel.setOpaque(true);
			contentPanel.setBackground(DiagnosticStyle.BACKGROUND_DARK);
			contentPanel.setBorder(new javax.swing.border.EmptyBorder(
				DiagnosticStyle.CONTENT_PADDING,
				DiagnosticStyle.CONTENT_PADDING,
				DiagnosticStyle.CONTENT_PADDING,
				DiagnosticStyle.CONTENT_PADDING
			));

			contentPanel.revalidate();
			contentPanel.repaint();
		}
	}

	private void updateSectionSize(JPanel section, int width) {

		int height = DiagnosticStyle.SECTION_HEIGHT;

		if (section instanceof DiagnosticSection) {
			DiagnosticSection diagSection = (DiagnosticSection) section;
			height = diagSection.calculatePreferredHeight();
			height = Math.max(height, DiagnosticStyle.SECTION_HEIGHT);
		}

		Dimension preferredSize = new Dimension(width, height);
		Dimension minSize = new Dimension(DiagnosticStyle.MIN_SECTION_WIDTH, height);
		Dimension maxSize = new Dimension(DiagnosticStyle.MAX_SECTION_WIDTH, Integer.MAX_VALUE);

		section.setPreferredSize(preferredSize);
		section.setMinimumSize(minSize);
		section.setMaximumSize(maxSize);
	}

	public boolean needsLayoutUpdate(int newWidth) {
		int newColumns = calculateOptimalColumns(newWidth);
		return newColumns != currentColumns || Math.abs(newWidth - currentWidth) > 50;
	}

	public int getCurrentColumns() {
		return currentColumns;
	}

	public int getCurrentWidth() {
		return currentWidth;
	}

	public static JScrollPane createStyledScrollPane(JComponent content) {
		JScrollPane scrollPane = new JScrollPane(content);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setOpaque(true);
		scrollPane.setBackground(DiagnosticStyle.BACKGROUND_DARK);
		scrollPane.getViewport().setBackground(DiagnosticStyle.BACKGROUND_DARK);
		scrollPane.setBorder(null);

		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getVerticalScrollBar().setBlockIncrement(64);

		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPane.getVerticalScrollBar().setOpaque(true);

		return scrollPane;
	}
}
