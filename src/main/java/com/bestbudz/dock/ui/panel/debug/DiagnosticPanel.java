package com.bestbudz.dock.ui.panel.debug;

import com.bestbudz.dock.ui.panel.debug.components.DiagnosticSection;
import com.bestbudz.dock.ui.panel.debug.diagnostics.DiagnosticManager;
import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.dock.ui.panel.debug.style.ResponsiveLayout;
import com.bestbudz.dock.util.DockTextUpdatable;
import com.bestbudz.dock.util.UIPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;
import java.util.ArrayList;

public class DiagnosticPanel extends JPanel implements UIPanel, DockTextUpdatable {

	private DiagnosticManager diagnosticManager;
	private ResponsiveLayout responsiveLayout;

	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel titleLabel;
	private JPanel buttonPanel;

	public static boolean isActive = false;
	private int currentWidth = 0;

	public DiagnosticPanel() {
		System.out.println("Creating modular DiagnosticPanel...");
		initializePanel();
		setupLayout();
		initializeDiagnostics();
		System.out.println("Modular DiagnosticPanel created successfully");
	}

	private void initializePanel() {
		setLayout(new BorderLayout());
		setOpaque(true);
		setBackground(DiagnosticStyle.BACKGROUND_DARK);
		setBorder(DiagnosticStyle.createContentBorder());

		diagnosticManager = new DiagnosticManager();
		responsiveLayout = new ResponsiveLayout();

		contentPanel = new JPanel();
		contentPanel.setOpaque(true);
		contentPanel.setBackground(DiagnosticStyle.BACKGROUND_DARK);

		scrollPane = ResponsiveLayout.createStyledScrollPane(contentPanel);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				handleResize();
			}
		});
	}

	private void setupLayout() {

		add(scrollPane, BorderLayout.CENTER);
	}

	private void initializeDiagnostics() {
		diagnosticManager.initializeDefaultDiagnostics();
		diagnosticManager.setAutoRefresh(true);
		diagnosticManager.startAutoUpdate();

		SwingUtilities.invokeLater(() -> {
			currentWidth = getWidth();
			updateResponsiveLayout();
		});
	}

	private void handleResize() {
		int newWidth = getWidth();
		if (responsiveLayout.needsLayoutUpdate(newWidth)) {
			System.out.println("Panel resized: " + currentWidth + " -> " + newWidth);
			currentWidth = newWidth;
			updateResponsiveLayout();
		}
	}

	private void updateResponsiveLayout() {

		diagnosticManager.updateFonts(currentWidth);

		List<DiagnosticSection> sections = diagnosticManager.getAllSections();
		List<JPanel> sectionPanels = new ArrayList<>();
		for (DiagnosticSection section : sections) {
			sectionPanels.add(section);
		}

		int mainPadding = DiagnosticStyle.CONTENT_PADDING * 2;
		int availableWidth = getWidth() - mainPadding;

		responsiveLayout.setupResponsiveGrid(contentPanel, sectionPanels, availableWidth);

		System.out.println("Layout updated: " + responsiveLayout.getCurrentColumns() +
			" columns (available width: " + availableWidth + ")");
	}

	@Override
	public void updateText() {
		if (diagnosticManager.isAutoRefresh()) {
			diagnosticManager.updateAll();
		}
	}

	@Override
	public String getPanelID() {
		return "Debug";
	}

	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public void onActivate() {
		System.out.println("Modular DiagnosticPanel activated");
		isActive = true;
		diagnosticManager.setActive(true);

		diagnosticManager.forceUpdateAll();
		updateResponsiveLayout();
	}

	@Override
	public void onDeactivate() {
		System.out.println("Modular DiagnosticPanel deactivated");
		isActive = false;
		diagnosticManager.setActive(false);
	}

	@Override
	public void updateDockText(int index, String text) {
		System.out.println("Received dock text update [" + index + "]: " + text);
	}

	public void cleanup() {
		diagnosticManager.cleanup();
	}

	public void setDiagnosticEnabled(String sectionTitle, boolean enabled) {
		diagnosticManager.setDiagnosticEnabled(sectionTitle, enabled);
		updateResponsiveLayout();
	}

	public boolean isDiagnosticEnabled(String sectionTitle) {
		return diagnosticManager.isDiagnosticEnabled(sectionTitle);
	}

	public void addCustomDiagnostic(com.bestbudz.dock.ui.panel.debug.diagnostics.BaseDiagnostic diagnostic) {
		diagnosticManager.registerDiagnostic(diagnostic);
		updateResponsiveLayout();
	}

	public void removeDiagnostic(String sectionTitle) {
		diagnosticManager.unregisterDiagnostic(sectionTitle);
		updateResponsiveLayout();
	}

	public DiagnosticManager getDiagnosticManager() {
		return diagnosticManager;
	}

	public ResponsiveLayout getResponsiveLayout() {
		return responsiveLayout;
	}
}
