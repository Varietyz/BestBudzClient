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

/**
 * Refactored Diagnostic Panel - Now uses modular diagnostic system
 *
 * Features:
 * - Modular diagnostic architecture
 * - Centralized styling system
 * - Responsive layout management
 * - Easy to extend and maintain
 */
public class DiagnosticPanel extends JPanel implements UIPanel, DockTextUpdatable {

	// Core components
	private DiagnosticManager diagnosticManager;
	private ResponsiveLayout responsiveLayout;

	// UI Components
	private JScrollPane scrollPane;
	private JPanel contentPanel;
	private JLabel titleLabel;
	private JPanel buttonPanel;

	// State management
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

		// Initialize managers
		diagnosticManager = new DiagnosticManager();
		responsiveLayout = new ResponsiveLayout();

		// Create content panel
		contentPanel = new JPanel();
		contentPanel.setOpaque(true);
		contentPanel.setBackground(DiagnosticStyle.BACKGROUND_DARK);

		// Create scroll pane
		scrollPane = ResponsiveLayout.createStyledScrollPane(contentPanel);

		// Add component listener for responsive behavior
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

		// Initial layout update
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
		// Update fonts for all diagnostics
		diagnosticManager.updateFonts(currentWidth);

		// Get all sections and update layout
		List<DiagnosticSection> sections = diagnosticManager.getAllSections();
		List<JPanel> sectionPanels = new ArrayList<>();
		for (DiagnosticSection section : sections) {
			sectionPanels.add(section);
		}

		// Account for main panel padding when calculating available width
		int mainPadding = DiagnosticStyle.CONTENT_PADDING * 2; // Left + right padding
		int availableWidth = getWidth() - mainPadding;

		responsiveLayout.setupResponsiveGrid(contentPanel, sectionPanels, availableWidth);

		System.out.println("Layout updated: " + responsiveLayout.getCurrentColumns() +
			" columns (available width: " + availableWidth + ")");
	}


	// UIPanel Interface Implementation

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

		// Immediate update and layout refresh
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

	// Public API for extending functionality

	/**
	 * Enable or disable a specific diagnostic
	 */
	public void setDiagnosticEnabled(String sectionTitle, boolean enabled) {
		diagnosticManager.setDiagnosticEnabled(sectionTitle, enabled);
		updateResponsiveLayout();
	}

	/**
	 * Check if a diagnostic is enabled
	 */
	public boolean isDiagnosticEnabled(String sectionTitle) {
		return diagnosticManager.isDiagnosticEnabled(sectionTitle);
	}

	/**
	 * Add a custom diagnostic
	 */
	public void addCustomDiagnostic(com.bestbudz.dock.ui.panel.debug.diagnostics.BaseDiagnostic diagnostic) {
		diagnosticManager.registerDiagnostic(diagnostic);
		updateResponsiveLayout();
	}

	/**
	 * Remove a diagnostic
	 */
	public void removeDiagnostic(String sectionTitle) {
		diagnosticManager.unregisterDiagnostic(sectionTitle);
		updateResponsiveLayout();
	}

	/**
	 * Get diagnostic manager for advanced operations
	 */
	public DiagnosticManager getDiagnosticManager() {
		return diagnosticManager;
	}

	/**
	 * Get responsive layout manager
	 */
	public ResponsiveLayout getResponsiveLayout() {
		return responsiveLayout;
	}
}