package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.cache.Signlink;
import com.bestbudz.engine.config.EngineConfig;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;

public class CacheDiagnostic extends BaseDiagnostic {

	public CacheDiagnostic() {
		super("[Cache & Data]", DiagnosticStyle.CATEGORY_CACHE, DiagnosticStyle.CATEGORY_CACHE);
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

		addRow("Data Source", "JSON (internal)", DiagnosticStyle.STATUS_GOOD);
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
