package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.data.items.ItemDef;
import com.bestbudz.engine.core.Client;
import com.bestbudz.entity.EntityDef;
import com.bestbudz.rendering.SequenceFrame;
import com.bestbudz.rendering.animation.Animation;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.world.ObjectDef;
import com.bestbudz.world.Varp;
import com.bestbudz.world.VarBit;

import java.awt.Color;

public class AllocationDiagnostic extends BaseDiagnostic {

	public AllocationDiagnostic() {
		super("[Allocations]", new Color(96, 165, 250), new Color(96, 165, 250));
		setUpdateInterval(2000);
	}

	@Override
	protected void onInitialize() {

	}

	@Override
	protected void collectData() {
		// Definition stores
		addRow("── Definitions ──", "", DiagnosticStyle.CATEGORY_HEADER);
		addRow("Items", String.format("%d defs", ItemDef.totalItems), DiagnosticStyle.TEXT_MUTED);
		addRow("NPCs", String.format("%d defs", EntityDef.totalNPCs), DiagnosticStyle.TEXT_MUTED);
		addRow("Objects", String.format("%d defs", ObjectDef.totalObjects), DiagnosticStyle.TEXT_MUTED);

		if (Animation.anims != null) {
			addRow("Animations", String.format("%d slots", Animation.anims.length), DiagnosticStyle.TEXT_MUTED);
		}
		if (SequenceFrame.animationlist != null) {
			addRow("Anim Frames", String.format("%d files", SequenceFrame.animationlist.length), DiagnosticStyle.TEXT_MUTED);
		}
		if (Varp.cache != null) {
			addRow("Varps", String.format("%d", Varp.cache.length), DiagnosticStyle.TEXT_MUTED);
		}
		if (VarBit.cache != null) {
			addRow("VarBits", String.format("%d", VarBit.cache.length), DiagnosticStyle.TEXT_MUTED);
		}

		// Config arrays
		addRow("── Config ──", "", DiagnosticStyle.CATEGORY_HEADER);
		if (Client.variousSettings != null) {
			addRow("Settings", String.format("%d slots", Client.variousSettings.length), DiagnosticStyle.STATUS_GOOD);
		}
		if (Client.experienceDrops != null) {
			addRow("XP Drops", String.format("%d slots", Client.experienceDrops.length), DiagnosticStyle.STATUS_GOOD);
		}

		// Sprite arrays
		addRow("── Sprites ──", "", DiagnosticStyle.CATEGORY_HEADER);
		addRow("Map Functions", formatArrayLen(Client.mapFunctions), DiagnosticStyle.TEXT_MUTED);
		addRow("Map Scenes", formatArrayLen(Client.mapScenes), DiagnosticStyle.TEXT_MUTED);
		addRow("Hit Marks", formatArrayLen(Client.hitMarks), DiagnosticStyle.TEXT_MUTED);
		addRow("Head Icons", formatArrayLen(Client.headIcons), DiagnosticStyle.TEXT_MUTED);
		addRow("Skull Icons", formatArrayLen(Client.skullIcons), DiagnosticStyle.TEXT_MUTED);

		// Entity arrays
		addRow("── Entities ──", "", DiagnosticStyle.CATEGORY_HEADER);
		if (Client.stonerArray != null) {
			int activePlayers = countNonNull(Client.stonerArray);
			addRow("Players", String.format("%d / %d", activePlayers, Client.maxStoners), DiagnosticStyle.TEXT_MUTED);
		}
		if (Client.npcArray != null) {
			addRow("NPCs", String.format("%d / %d", Client.npcCount, Client.maxNpcs), DiagnosticStyle.TEXT_MUTED);
		}

		// Model cache
		addRow("── Models ──", "", DiagnosticStyle.CATEGORY_HEADER);
		addRow("Model Count", String.valueOf(Model.modelCount), DiagnosticStyle.TEXT_MUTED);
	}

	private String formatArrayLen(Object[] array) {
		if (array == null) return "null";
		return String.format("%d slots", array.length);
	}

	private int countNonNull(Object[] array) {
		if (array == null) return 0;
		int count = 0;
		for (Object o : array) if (o != null) count++;
		return count;
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.CACHE;
	}

	@Override
	public int getPriority() {
		return 6;
	}
}
