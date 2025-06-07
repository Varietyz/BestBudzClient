package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.engine.core.Client;
import java.lang.management.*;

public class PerformanceDiagnostic extends BaseDiagnostic {

	public PerformanceDiagnostic() {
		super("[Performance]", DiagnosticStyle.CATEGORY_PERFORMANCE, DiagnosticStyle.CATEGORY_PERFORMANCE);
		setUpdateInterval(300); // Update more frequently for performance data
	}

	@Override
	protected void onInitialize() {
		// No special initialization needed
	}

	@Override
	protected void collectData() {
		// FPS and frame time
		int fps = Client.fps;
		addRow("FPS", String.valueOf(fps), DiagnosticStyle.getFpsColor(fps));

		if (fps > 0) {
			double frameTime = 1000.0 / fps;
			java.awt.Color frameTimeColor = frameTime > 50 ? DiagnosticStyle.STATUS_CRITICAL :
				frameTime > 16.7 ? DiagnosticStyle.STATUS_WARNING :
					DiagnosticStyle.STATUS_GOOD;
			addRow("Frame Time", String.format("%.1f ms", frameTime), frameTimeColor);
		}

		// Memory information
		Runtime rt = Runtime.getRuntime();
		long used = rt.totalMemory() - rt.freeMemory();
		long max = rt.maxMemory();

		addRow("Memory Used", DiagnosticStyle.formatBytes(used), DiagnosticStyle.getMemoryColor(used, max));
		addRow("Memory Usage", String.format("%.1f%%", (used * 100.0 / max)), DiagnosticStyle.getMemoryColor(used, max));

		// Thread information
		ThreadMXBean tm = ManagementFactory.getThreadMXBean();
		addRow("Threads", String.valueOf(tm.getThreadCount()), DiagnosticStyle.TEXT_MUTED);

		// GC information
		long gcRuns = getGcRuns();
		long gcTime = getGcTime();
		addRow("GC Runs", String.valueOf(gcRuns), DiagnosticStyle.TEXT_MUTED);
		addRow("GC Time", gcTime + " ms", gcTime > 1000 ? DiagnosticStyle.STATUS_WARNING : DiagnosticStyle.TEXT_MUTED);

		// Uptime
		long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
		addRow("Uptime", DiagnosticStyle.formatTime(uptime), DiagnosticStyle.TEXT_MUTED);
	}

	private long getGcRuns() {
		long count = 0;
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
			count += gc.getCollectionCount();
		}
		return count;
	}

	private long getGcTime() {
		long time = 0;
		for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
			time += gc.getCollectionTime();
		}
		return time;
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.PERFORMANCE;
	}

	@Override
	public int getPriority() {
		return 1; // High priority for performance data
	}
}