package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.components.DiagnosticSection;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.SwingUtilities;

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

	public void initializeDefaultDiagnostics() {
		registerDiagnostic(new PerformanceDiagnostic());
		registerDiagnostic(new SystemDiagnostic());
		registerDiagnostic(new EntityDiagnostic());
		registerDiagnostic(new WorldDiagnostic());
		registerDiagnostic(new CameraDiagnostic());
		registerDiagnostic(new CacheDiagnostic());
		registerDiagnostic(new GPUDiagnostic());

		diagnostics.sort(Comparator.comparingInt(BaseDiagnostic::getPriority));

		System.out.println("Initialized " + diagnostics.size() + " diagnostics");
	}

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

	public void unregisterDiagnostic(String sectionTitle) {
		BaseDiagnostic diagnostic = diagnosticMap.remove(sectionTitle);
		if (diagnostic != null) {
			diagnostics.remove(diagnostic);
			diagnostic.cleanup();
			System.out.println("Unregistered diagnostic: " + sectionTitle);
		}
	}

	public List<DiagnosticSection> getAllSections() {
		List<DiagnosticSection> sections = new ArrayList<>();
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isEnabled()) {
				sections.add(diagnostic.getSection());
			}
		}
		return sections;
	}

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

	public void updateFonts(int containerWidth) {
		for (BaseDiagnostic diagnostic : diagnostics) {
			DiagnosticSection section = diagnostic.getSection();
			if (section != null) {
				section.updateFonts(containerWidth);
			}
		}
	}

	public void setDiagnosticEnabled(String sectionTitle, boolean enabled) {
		BaseDiagnostic diagnostic = diagnosticMap.get(sectionTitle);
		if (diagnostic != null) {
			diagnostic.setEnabled(enabled);
			System.out.println("Diagnostic " + sectionTitle + " " + (enabled ? "enabled" : "disabled"));
		}
	}

	public boolean isDiagnosticEnabled(String sectionTitle) {
		BaseDiagnostic diagnostic = diagnosticMap.get(sectionTitle);
		return diagnostic != null && diagnostic.isEnabled();
	}

	public BaseDiagnostic getDiagnostic(String sectionTitle) {
		return diagnosticMap.get(sectionTitle);
	}

	public List<BaseDiagnostic> getDiagnosticsByCategory(BaseDiagnostic.DiagnosticCategory category) {
		List<BaseDiagnostic> result = new ArrayList<>();
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.getCategory() == category) {
				result.add(diagnostic);
			}
		}
		return result;
	}

	public void setPerformanceUpdateInterval(int intervalMs) {
		List<BaseDiagnostic> perfDiagnostics = getDiagnosticsByCategory(BaseDiagnostic.DiagnosticCategory.PERFORMANCE);
		for (BaseDiagnostic diagnostic : perfDiagnostics) {
			diagnostic.setUpdateInterval(intervalMs);
		}
	}

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

	public void stopAutoUpdate() {
		if (updateTimer != null) {
			updateTimer.cancel();
			updateTimer = null;
			System.out.println("Stopped auto-update");
		}
	}

	public void setAutoRefresh(boolean enabled) {
		this.autoRefresh = enabled;
		if (enabled) {
			startAutoUpdate();
		} else {
			stopAutoUpdate();
		}
	}

	public boolean isAutoRefresh() {
		return autoRefresh;
	}

	public void setActive(boolean active) {
		this.isActive = active;
		if (autoRefresh) {
			startAutoUpdate();
		}

		if (active) {
			setPerformanceUpdateInterval(activeUpdateInterval);
		} else {

		}
	}

	public boolean isActive() {
		return isActive;
	}

	public void setUpdateIntervals(int activeMs, int inactiveMs) {
		this.activeUpdateInterval = activeMs;
		this.inactiveUpdateInterval = inactiveMs;
		if (autoRefresh) {
			startAutoUpdate();
		}
	}

	public int getCurrentUpdateInterval() {
		return isActive ? activeUpdateInterval : inactiveUpdateInterval;
	}

	public int getEnabledDiagnosticCount() {
		int count = 0;
		for (BaseDiagnostic diagnostic : diagnostics) {
			if (diagnostic.isEnabled()) {
				count++;
			}
		}
		return count;
	}

	public int getTotalDiagnosticCount() {
		return diagnostics.size();
	}

	public void clearAll() {
		for (BaseDiagnostic diagnostic : diagnostics) {
			DiagnosticSection section = diagnostic.getSection();
			if (section != null) {
				section.clearRows();
			}
		}
	}

	public void cleanup() {
		stopAutoUpdate();
		for (BaseDiagnostic diagnostic : diagnostics) {
			diagnostic.cleanup();
		}
		diagnostics.clear();
		diagnosticMap.clear();
		System.out.println("DiagnosticManager cleaned up");
	}

	public String getStatistics() {
		return String.format("Diagnostics: %d enabled / %d total, Auto-refresh: %s, Update interval: %dms",
			getEnabledDiagnosticCount(), getTotalDiagnosticCount(),
			autoRefresh ? "ON" : "OFF", getCurrentUpdateInterval());
	}
}
