package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.components.DiagnosticSection;
import java.awt.Color;

public abstract class BaseDiagnostic {

	protected final String sectionTitle;
	protected final Color headerColor;
	protected final Color borderColor;
	protected DiagnosticSection section;
	protected boolean isEnabled = true;
	protected long lastUpdateTime = 0;
	protected int updateInterval = 1000;

	protected BaseDiagnostic(String sectionTitle, Color headerColor, Color borderColor) {
		this.sectionTitle = sectionTitle;
		this.headerColor = headerColor;
		this.borderColor = borderColor;
	}

	public void initialize() {
		section = new DiagnosticSection(sectionTitle, headerColor, borderColor);
		onInitialize();
	}

	public final void update() {
		if (!isEnabled) {
			return;
		}

		long currentTime = System.currentTimeMillis();
		if (currentTime - lastUpdateTime < updateInterval) {
			return;
		}

		try {
			section.clearRows();
			collectData();
			lastUpdateTime = currentTime;
		} catch (Exception e) {
			handleError(e);
		}
	}

	public final void forceUpdate() {
		lastUpdateTime = 0;
		update();
	}

	public DiagnosticSection getSection() {
		if (section == null) {
			initialize();
		}
		return section;
	}

	public void setEnabled(boolean enabled) {
		this.isEnabled = enabled;
		if (!enabled && section != null) {
			section.clearRows();
			section.addRow("Status", "Disabled", Color.GRAY);
		}
	}

	public boolean isEnabled() {
		return isEnabled;
	}

	public void setUpdateInterval(int intervalMs) {
		this.updateInterval = Math.max(100, intervalMs);
	}

	public int getUpdateInterval() {
		return updateInterval;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public boolean needsUpdate() {
		return isEnabled && (System.currentTimeMillis() - lastUpdateTime >= updateInterval);
	}

	protected final void addRow(String key, String value, Color valueColor) {
		if (section != null) {
			section.addRow(key, value, valueColor);
		}
	}

	protected void handleError(Exception e) {
		System.err.println("Error in " + sectionTitle + " diagnostic: " + e.getMessage());
		if (section != null) {
			section.clearRows();
			section.addRow("Error", "Data unavailable", Color.RED);
		}
	}

	protected abstract void onInitialize();

	protected abstract void collectData();

	public abstract DiagnosticCategory getCategory();

	public abstract int getPriority();

	public boolean isAvailable() {
		return true;
	}

	public void cleanup() {

	}

	public enum DiagnosticCategory {
		CORE,
		PERFORMANCE,
		GAME_WORLD,
		GRAPHICS,
		CACHE,
		NETWORK,
		SYSTEM,
		GPU
	}
}
