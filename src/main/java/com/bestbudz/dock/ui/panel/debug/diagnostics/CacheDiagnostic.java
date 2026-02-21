package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.config.EngineConfig;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;
import com.bestbudz.engine.core.Client;
import java.io.File;

public class CacheDiagnostic extends BaseDiagnostic {

	public CacheDiagnostic() {
		super("[Cache & OnDemand]", DiagnosticStyle.CATEGORY_CACHE, DiagnosticStyle.CATEGORY_CACHE);
	}

	@Override
	protected void onInitialize() {

	}

	@Override
	protected void collectData() {

		if (worldSelected == 3) {
			addRow("Client Version", String.valueOf(EngineConfig.DEV_ENGINE_VERSION), DiagnosticStyle.TEXT_MUTED);
		} else {
			addRow("Client Version", String.valueOf(EngineConfig.ENGINE_VERSION), DiagnosticStyle.TEXT_MUTED);
		}

		addRow("Cache Ver", String.valueOf(Signlink.clientversion), DiagnosticStyle.TEXT_MUTED);
		addRow("UID", String.valueOf(Signlink.uid), DiagnosticStyle.TEXT_MUTED);
		addRow("Store ID", String.valueOf(Signlink.storeid), DiagnosticStyle.TEXT_MUTED);

		addRow("Data Source", "JSON", DiagnosticStyle.STATUS_GOOD);

		if (Client.cacheManager != null) {
			String status = Client.cacheManager.statusString;
			addRow("OD Status", (status != null && !status.isEmpty()) ?
				DiagnosticStyle.truncateString(status, 15) : "Ready", DiagnosticStyle.TEXT_MUTED);

			addRow("OD Cycles", String.valueOf(Client.cacheManager.onDemandCycle), DiagnosticStyle.TEXT_MUTED);

			try {
				int nodeCount = Client.cacheManager.getNodeCount();
				addRow("Nodes", String.valueOf(nodeCount),
					nodeCount > 0 ? DiagnosticStyle.STATUS_WARNING : DiagnosticStyle.STATUS_GOOD);
			} catch (Exception e) {  }
		}

		try {
			String cacheDir = Signlink.findCacheDir();
			if (cacheDir != null) {
				File cacheDirFile = new File(cacheDir);
				if (cacheDirFile.exists()) {
					File[] files = cacheDirFile.listFiles();
					int fileCount = files != null ? files.length : 0;
					addRow("FS Files", String.valueOf(fileCount), DiagnosticStyle.TEXT_MUTED);

					if (files != null) {
						long totalSize = 0;
						for (File file : files) {
							if (file.isFile()) {
								totalSize += file.length();
							}
						}
						addRow("FS Size", DiagnosticStyle.formatBytes(totalSize), DiagnosticStyle.TEXT_MUTED);

						String health = fileCount > 10 ? "Good" : "Limited";
						addRow("Health", health, fileCount > 10 ? DiagnosticStyle.STATUS_GOOD : DiagnosticStyle.STATUS_WARNING);
					}
				}
			}
		} catch (Exception e) {
			addRow("FS Error", "Access failed", DiagnosticStyle.STATUS_CRITICAL);
		}
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.CACHE;
	}

	@Override
	public int getPriority() {
		return 5;
	}
}
