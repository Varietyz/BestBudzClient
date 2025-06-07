package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.Stoner;

public class EntityDiagnostic extends BaseDiagnostic {

	public EntityDiagnostic() {
		super("[Entities]", DiagnosticStyle.CATEGORY_ENTITY, DiagnosticStyle.CATEGORY_ENTITY);
	}

	@Override
	protected void onInitialize() {
		// No special initialization needed
	}

	@Override
	protected void collectData() {
		int playerCount = getActiveStoners();
		addRow("Players", String.valueOf(playerCount), DiagnosticStyle.TEXT_MUTED);
		addRow("NPCs", String.valueOf(Client.npcCount), DiagnosticStyle.TEXT_MUTED);

		int totalEntities = playerCount + Client.npcCount;
		addRow("Total", String.valueOf(totalEntities), DiagnosticStyle.TEXT_MUTED);

		String density = totalEntities > 50 ? "High" : totalEntities > 20 ? "Medium" : "Low";
		java.awt.Color densityColor = totalEntities > 50 ? DiagnosticStyle.STATUS_WARNING : DiagnosticStyle.STATUS_GOOD;
		addRow("Density", density, densityColor);
	}

	private int getActiveStoners() {
		int count = 0;
		try {
			if (Client.stonerArray != null) {
				for (Stoner stoner : Client.stonerArray) {
					if (stoner != null) count++;
				}
			}
		} catch (Exception e) {
			System.err.println("Error counting active stoners: " + e.getMessage());
		}
		return count;
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.GAME_WORLD;
	}

	@Override
	public int getPriority() {
		return 3;
	}
}