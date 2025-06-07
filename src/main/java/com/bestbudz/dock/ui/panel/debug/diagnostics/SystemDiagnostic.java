package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.engine.config.EngineConfig;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;
import java.io.File;

public class SystemDiagnostic extends BaseDiagnostic {

	public SystemDiagnostic() {
		super("[System Info]", DiagnosticStyle.CATEGORY_SYSTEM, DiagnosticStyle.CATEGORY_SYSTEM);
		setUpdateInterval(5000); // Update less frequently for system info
	}

	@Override
	protected void onInitialize() {
		// No special initialization needed
	}

	@Override
	protected void collectData() {
		// System information
		String osName = System.getProperty("os.name");
		addRow("OS", DiagnosticStyle.truncateString(osName, 15), DiagnosticStyle.TEXT_MUTED);

		String osArch = System.getProperty("os.arch");
		addRow("Arch", osArch, DiagnosticStyle.TEXT_MUTED);

		String javaVersion = System.getProperty("java.version");
		addRow("Java Ver", javaVersion, DiagnosticStyle.TEXT_MUTED);

		int cores = Runtime.getRuntime().availableProcessors();
		addRow("CPU Cores", String.valueOf(cores), DiagnosticStyle.TEXT_MUTED);

		// Disk space
		File tempDir = new File(System.getProperty("java.io.tmpdir"));
		boolean tempWritable = tempDir.canWrite();
		addRow("Temp Access", tempWritable ? "Writable" : "Read-only",
			tempWritable ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_WARNING);

		long freeSpace = tempDir.getFreeSpace();
		addRow("Free Space", DiagnosticStyle.formatBytes(freeSpace),
			freeSpace < 1024 * 1024 * 100 ? DiagnosticStyle.STATUS_WARNING : DiagnosticStyle.STATUS_GOOD);
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.SYSTEM;
	}

	@Override
	public int getPriority() {
		return 6;
	}
}