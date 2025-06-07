package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.components.DiagnosticSection;
import java.awt.Color;

/**
 * Abstract base class for all diagnostic modules
 * Provides common interface and structure for diagnostic data collection
 */
public abstract class BaseDiagnostic {

	protected final String sectionTitle;
	protected final Color headerColor;
	protected final Color borderColor;
	protected DiagnosticSection section;
	protected boolean isEnabled = true;
	protected long lastUpdateTime = 0;
	protected int updateInterval = 1000; // Default 1 second

	protected BaseDiagnostic(String sectionTitle, Color headerColor, Color borderColor) {
		this.sectionTitle = sectionTitle;
		this.headerColor = headerColor;
		this.borderColor = borderColor;
	}

	/**
	 * Initialize the diagnostic section
	 * Called once when the diagnostic is first created
	 */
	public void initialize() {
		section = new DiagnosticSection(sectionTitle, headerColor, borderColor);
		onInitialize();
	}

	/**
	 * Update the diagnostic data
	 * Called periodically to refresh the displayed information
	 */
	public final void update() {
		if (!isEnabled) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdateTime < updateInterval) {
			return; // Skip update if not enough time has passed
		}

		try {
			section.clearRows();
			collectData();
			lastUpdateTime = currentTime;
		} catch (Exception e) {
			handleError(e);
		}
	}

	/**
	 * Force an immediate update regardless of timing
	 */
	public final void forceUpdate() {
		lastUpdateTime = 0;
		update();
	}

	/**
	 * Get the visual section component
	 */
	public DiagnosticSection getSection() {
		if (section == null) {
			initialize();
		}
		return section;
	}

	/**
	 * Enable or disable this diagnostic
	 */
	public void setEnabled(boolean enabled) {
		this.isEnabled = enabled;
		if (!enabled && section != null) {
			section.clearRows();
			section.addRow("Status", "Disabled", Color.GRAY);
		}
	}

	/**
	 * Check if this diagnostic is enabled
	 */
	public boolean isEnabled() {
		return isEnabled;
	}

	/**
	 * Set the update interval in milliseconds
	 */
	public void setUpdateInterval(int intervalMs) {
		this.updateInterval = Math.max(100, intervalMs); // Minimum 100ms
	}

	/**
	 * Get the update interval
	 */
	public int getUpdateInterval() {
		return updateInterval;
	}

	/**
	 * Get the section title
	 */
	public String getSectionTitle() {
		return sectionTitle;
	}

	/**
	 * Check if this diagnostic needs updating based on timing
	 */
	public boolean needsUpdate() {
		return isEnabled && (System.currentTimeMillis() - lastUpdateTime >= updateInterval);
	}

	/**
	 * Add a data row to the section
	 */
	protected final void addRow(String key, String value, Color valueColor) {
		if (section != null) {
			section.addRow(key, value, valueColor);
		}
	}

	/**
	 * Handle errors that occur during data collection
	 */
	protected void handleError(Exception e) {
		System.err.println("Error in " + sectionTitle + " diagnostic: " + e.getMessage());
		if (section != null) {
			section.clearRows();
			section.addRow("Error", "Data unavailable", Color.RED);
		}
	}

	// Abstract methods that must be implemented by subclasses

	/**
	 * Called once during initialization
	 * Override to perform any setup required for this diagnostic
	 */
	protected abstract void onInitialize();

	/**
	 * Collect and display diagnostic data
	 * This is where the actual diagnostic logic goes
	 */
	protected abstract void collectData();

	/**
	 * Get diagnostic category for grouping
	 * Used for organizing diagnostics in the UI
	 */
	public abstract DiagnosticCategory getCategory();

	/**
	 * Get priority for ordering (lower numbers = higher priority)
	 */
	public abstract int getPriority();

	/**
	 * Check if this diagnostic is available in the current environment
	 * Override to check for required dependencies
	 */
	public boolean isAvailable() {
		return true;
	}

	/**
	 * Cleanup resources when diagnostic is no longer needed
	 */
	public void cleanup() {
		// Override if cleanup is needed
	}

	/**
	 * Categories for organizing diagnostics
	 */
	public enum DiagnosticCategory {
		CORE,           // Essential system info
		PERFORMANCE,    // FPS, memory, GC
		GAME_WORLD,     // World coordinates, entities
		GRAPHICS,       // Camera, rendering
		CACHE,          // Cache files, signlink
		NETWORK,        // Network and I/O
		SYSTEM,          // OS, JVM, hardware
		GPU 			// OpenGL, GPU, Rendering
	}
}