package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.cache.Signlink;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.engine.config.EngineConfig;
import com.bestbudz.world.ObjectDef;
import static com.bestbudz.engine.config.EngineConfig.worldSelected;

import java.awt.Color;

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

		// LRU Cache Stats
		addRow("── LRU Caches ──", "", DiagnosticStyle.CATEGORY_CACHE);

		if (ItemDef.cache != null) {
			int itemHits = ItemDef.cacheHits;
			int itemMisses = ItemDef.cacheMisses;
			int itemTotal = itemHits + itemMisses;
			String itemRate = itemTotal > 0 ? String.format("%.1f%%", itemHits * 100.0 / itemTotal) : "N/A";
			addRow("Item Cache", String.format("%d/%d (%s)", itemHits, itemTotal, itemRate),
				getHitRateColor(itemHits, itemTotal));
			addRow("Item Slots", ItemDef.cache.length + " slots", DiagnosticStyle.TEXT_MUTED);
		}

		if (EntityDef.cache != null) {
			int npcHits = EntityDef.cacheHits;
			int npcMisses = EntityDef.cacheMisses;
			int npcTotal = npcHits + npcMisses;
			String npcRate = npcTotal > 0 ? String.format("%.1f%%", npcHits * 100.0 / npcTotal) : "N/A";
			addRow("NPC Cache", String.format("%d/%d (%s)", npcHits, npcTotal, npcRate),
				getHitRateColor(npcHits, npcTotal));
			addRow("NPC Slots", EntityDef.cache.length + " slots", DiagnosticStyle.TEXT_MUTED);
		}

		if (ObjectDef.cache != null) {
			int objHits = ObjectDef.cacheHits;
			int objMisses = ObjectDef.cacheMisses;
			int objTotal = objHits + objMisses;
			String objRate = objTotal > 0 ? String.format("%.1f%%", objHits * 100.0 / objTotal) : "N/A";
			addRow("Object Cache", String.format("%d/%d (%s)", objHits, objTotal, objRate),
				getHitRateColor(objHits, objTotal));
			addRow("Object Slots", ObjectDef.cache.length + " slots", DiagnosticStyle.TEXT_MUTED);
		}
	}

	private Color getHitRateColor(int hits, int total) {
		if (total == 0) return DiagnosticStyle.TEXT_MUTED;
		double rate = hits * 100.0 / total;
		if (rate >= 80) return DiagnosticStyle.STATUS_GOOD;
		if (rate >= 50) return DiagnosticStyle.STATUS_WARNING;
		return DiagnosticStyle.STATUS_CRITICAL;
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
