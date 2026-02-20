package com.bestbudz.dock.ui.panel.debug.diagnostics;

import com.bestbudz.dock.ui.panel.debug.style.DiagnosticStyle;
import com.bestbudz.engine.core.Client;
import com.bestbudz.ui.handling.input.MouseState;

public class CameraDiagnostic extends BaseDiagnostic {

	public CameraDiagnostic() {
		super("[Graphics]", DiagnosticStyle.CATEGORY_HEADER, DiagnosticStyle.CATEGORY_HEADER);
	}

	@Override
	protected void onInitialize() {

	}

	@Override
	protected void collectData() {
		addRow("Camera Zoom", String.valueOf(Client.cameraZoom), DiagnosticStyle.TEXT_MUTED);
		addRow("Mouse", String.format("(%d, %d)", MouseState.x, MouseState.y), DiagnosticStyle.TEXT_MUTED);
		addRow("Resolution", String.format("%dx%d", Client.frameWidth, Client.frameHeight), DiagnosticStyle.TEXT_MUTED);

		double aspectRatio = (double) Client.frameWidth / Client.frameHeight;
		addRow("Aspect", String.format("%.2f:1", aspectRatio), DiagnosticStyle.TEXT_MUTED);

		int totalPixels = Client.frameWidth * Client.frameHeight;
		addRow("Pixels", DiagnosticStyle.formatNumber(totalPixels), DiagnosticStyle.TEXT_MUTED);
	}

	@Override
	public DiagnosticCategory getCategory() {
		return DiagnosticCategory.GRAPHICS;
	}

	@Override
	public int getPriority() {
		return 4;
	}
}
