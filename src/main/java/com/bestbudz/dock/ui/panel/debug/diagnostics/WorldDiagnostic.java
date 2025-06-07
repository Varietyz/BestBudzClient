package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.engine.core.Client;

public class WorldDiagnostic extends BaseDiagnostic {

	public WorldDiagnostic() {
		super("[Game World]", DiagnosticStyle.CATEGORY_WORLD, DiagnosticStyle.CATEGORY_WORLD);
	}

	@Override
	protected void onInitialize() {
		// No special initialization needed
	}

	@Override
	protected void collectData() {
		if (Client.myStoner != null) {
			int worldX = Client.myStoner.x - 6;
			int worldY = Client.myStoner.y - 6;
			int tileX = worldX >> 7;
			int tileY = worldY >> 7;

			addRow("Coords X Y", String.format("(%d, %d)", Client.baseX + tileX, Client.baseY + tileY), DiagnosticStyle.TEXT_MUTED);

			addRow("Server", String.format("(%d, %d)", Client.myStoner.x, Client.myStoner.y), DiagnosticStyle.TEXT_MUTED);

			if (Client.myStoner.smallX != null && Client.myStoner.smallY != null &&
				Client.myStoner.smallX.length > 0 && Client.myStoner.smallY.length > 0) {
				addRow("Local Pos", String.format("(%d, %d)", Client.myStoner.smallX[0], Client.myStoner.smallY[0]), DiagnosticStyle.TEXT_MUTED);
			}
		} else {
			addRow("Status", "No Player Data", DiagnosticStyle.STATUS_WARNING);
		}
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.GAME_WORLD;
	}

	@Override
	public int getPriority() {
		return 2;
	}
}