package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.components.DiagnosticSection;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

/**
 * Manages all diagnostic modules
 * Handles registration, updates, and lifecycle of diagnostics
 */
public class DiagnosticManager {

	private final List<BaseDiagnostic> diagnostics;
	private final Map<String, BaseDiagnostic> diagnosticMap;
	private Timer updateTimer;
	private boolean autoRefresh = true;
	private boolean isActive = false;
	private int activeUpdateInterval = 10;
	private int inactiveUpdateInterval = 20;

	public DiagnosticManager() {
		this.diagnostics = new CopyOnWriteArrayList<>();
		this.diagnosticMap = new HashMap<>();
	}

	/**
	 * Initialize with default diagnostics
	 */
	public void initializeDefaultDiagnostics() {
		registerDiagnostic(new PerformanceDiagnostic());
		registerDiagnostic(new SystemDiagnostic());
		registerDiagnostic(new EntityDiagnostic());
		registerDiagnostic(new WorldDiagnostic());
		registerDiagnostic(new CameraDiagnostic());
		registerDiagnostic(new CacheDiagnostic());
		registerDiagnostic(new GPUDiagnostic());

		// Sort by priority
		diagnostics.sort(Comparator.comparingInt(BaseDiagnostic::getPriority));

		System.out.println("Initialized " + diagnostics.size() + " diagnostics");
	}

	/**
	 * Register a new diagnostic
	 */
	public void registerDiagnostic(BaseDiagnostic diagnostic) {
		if (diagnostic.isAvailable()) {
			diagnostic.initialize();
			diagnostics.add(diagnostic);
			diagnosticMap.put(diagnostic.getSectionTitle(), diagnostic);
			System.out.println("Registered diagnostic: " + diagnostic.getSectionTitle());
		} else {
			System.out.println("Diagnostic not available: " + diagnostic.getSectionTitle());
		}
	}

	/**
	 * Unregister a diagnostic
	 */
	public void unregisterDiagnostic(String sectionTitle) {
		BaseDiagnostic diagnostic = diagnosticMap.remove(sectionTitle);
		if (diagnostic != null) {
			diagnostics.remove(diagnostic);
			diagnostic.cleanup();
			System.out.println("Unregistered diagnostic: " + sectionTitle);
		}
	}

	/**
	 * Get all diagnostic sections for display
	 */
	public List<DiagnosticSection> getAllSections() {
		List<DiagnosticSection> sections = new ArrayList<>();
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isEnabled()) {
				sections.add(diagnostic.getSection());
			}
		}
		return sections;
	}

	/**
	 * Update all diagnostics
	 */
	public void updateAll() {
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isEnabled()) {
				try {
					diagnostic.update();
				} catch (Exception e) {
					System.err.println("Error updating diagnostic " + diagnostic.getSectionTitle() + ": " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Force update all diagnostics immediately
	 */
	public void forceUpdateAll() {
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isEnabled()) {
				try {
					diagnostic.forceUpdate();
				} catch (Exception e) {
					System.err.println("Error force updating diagnostic " + diagnostic.getSectionTitle() + ": " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Update fonts for all diagnostics
	 */
	public void updateFonts(int containerWidth) {
		for (BaseDiagnostic diagnostic : diagnostics) {
			DiagnosticSection section = diagnostic.getSection();
			if (section != null) {
				section.updateFonts(containerWidth);
			}
		}
	}

	/**
	 * Enable or disable a specific diagnostic
	 */
	public void setDiagnosticEnabled(String sectionTitle, boolean enabled) {
		BaseDiagnostic diagnostic = diagnosticMap.get(sectionTitle);
		if (diagnostic != null) {
			diagnostic.setEnabled(enabled);
			System.out.println("Diagnostic " + sectionTitle + " " + (enabled ? "enabled" : "disabled"));
		}
	}

	/**
	 * Check if a diagnostic is enabled
	 */
	public boolean isDiagnosticEnabled(String sectionTitle) {
		BaseDiagnostic diagnostic = diagnosticMap.get(sectionTitle);
		return diagnostic != null && diagnostic.isEnabled();
	}

	/**
	 * Get diagnostic by section title
	 */
	public BaseDiagnostic getDiagnostic(String sectionTitle) {
		return diagnosticMap.get(sectionTitle);
	}

	/**
	 * Get diagnostics by category
	 */
	public List<BaseDiagnostic> getDiagnosticsByCategory(BaseDiagnostic.DiagnosticCategory category) {
		List<BaseDiagnostic> result = new ArrayList<>();
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.getCategory() == category) {
				result.add(diagnostic);
			}
		}
		return result;
	}

	/**
	 * Set update interval for performance-critical diagnostics
	 */
	public void setPerformanceUpdateInterval(int intervalMs) {
		List<BaseDiagnostic> perfDiagnostics = getDiagnosticsByCategory(BaseDiagnostic.DiagnosticCategory.PERFORMANCE);
		for (BaseDiagnostic diagnostic : perfDiagnostics) {
			diagnostic.setUpdateInterval(intervalMs);
		}
	}

	/**
	 * Start automatic updates
	 */
	public void startAutoUpdate() {
		if (updateTimer != null) {
			updateTimer.cancel();
		}
		if (!isActive) return;
		int interval = activeUpdateInterval;
		updateTimer = new Timer(true);
		updateTimer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (autoRefresh) {
					SwingUtilities.invokeLater(() -> updateAll());
				}
			}
		}, 100, interval);

		System.out.println("Started auto-update with interval: " + interval + "ms");
	}

	/**
	 * Stop automatic updates
	 */
	public void stopAutoUpdate() {
		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
			System.out.println("Stopped auto-update");
		}
	}

	/**
	 * Set auto refresh enabled/disabled
	 */
	public void setAutoRefresh(boolean enabled) {
		this.autoRefresh = enabled;
		if (enabled) {
			startAutoUpdate();
		} else {
			stopAutoUpdate();
		}
	}

	/**
	 * Check if auto refresh is enabled
	 */
	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	/**
	 * Set panel active state (affects update frequency)
	 */
	public void setActive(boolean active) {
		this.isActive = active;
		if (autoRefresh) {
			startAutoUpdate(); // Restart with new interval
		}

		// Update performance diagnostics interval
		if (active) {
			setPerformanceUpdateInterval(activeUpdateInterval);
		} else {
			//setPerformanceUpdateInterval(inactiveUpdateInterval);
		}
	}

	/**
	 * Check if panel is active
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * Set update intervals
	 */
	public void setUpdateIntervals(int activeMs, int inactiveMs) {
		this.activeUpdateInterval = activeMs;
		this.inactiveUpdateInterval = inactiveMs;
		if (autoRefresh) {
			startAutoUpdate(); // Restart with new interval
		}
	}

	/**
	 * Get current update interval
	 */
	public int getCurrentUpdateInterval() {
		return isActive ? activeUpdateInterval : inactiveUpdateInterval;
	}

	/**
	 * Get count of enabled diagnostics
	 */
	public int getEnabledDiagnosticCount() {
		int count = 0;
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isEnabled()) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Get total diagnostic count
	 */
	public int getTotalDiagnosticCount() {
		return diagnostics.size();
	}

	/**
	 * Clear all data in diagnostics
	 */
	public void clearAll() {
		for (BaseDiagnostic diagnostic : diagnostics) {
			DiagnosticSection section = diagnostic.getSection();
			if (section != null) {
				section.clearRows();
			}
		}
	}

	/**
	 * Cleanup all diagnostics
	 */
	public void cleanup() {
		stopAutoUpdate();
		for (BaseDiagnostic diagnostic : diagnostics) {
			diagnostic.cleanup();
		}
		diagnostics.clear();
		diagnosticMap.clear();
		System.out.println("DiagnosticManager cleaned up");
	}

	/**
	 * Get diagnostic statistics
	 */
	public String getStatistics() {
		return String.format("Diagnostics: %d enabled / %d total, Auto-refresh: %s, Update interval: %dms",
			getEnabledDiagnosticCount(), getTotalDiagnosticCount(),
			autoRefresh ? "ON" : "OFF", getCurrentUpdateInterval());
	}
}