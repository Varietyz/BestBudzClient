package com.bestbudz.engine;

public final class GraphicsConfig {
	public static final String TITLE = "BestBudz RSPS";

	public static final int VIEW_DISTANCE = 10;
	public static final int CAMERA_VIEW_DISTANCE = 10;
	public static final int CAMERA_ZOOM = 600;
	public static final int VISIBILITY_MARGIN = 64;
	public static final int PLANE_HEIGHT = 128;   // world-units between each z-plane

	public static final int TARGET_FPS = 90;      // e.g. 60 FPS
	public static final int BUFFERS = 3;          // triple buffering
	public static final long LOGIC_TICK_MS = 18; // RS tick, typically 300ms

	public static int MIN_WIDTH = 1280;
	public static int MIN_HEIGHT = 720;

	public static int REGION_RENDER = 104; // 1 region = 104, 2 regions = 208

	public static int nearPlane(int currentZoom) {
		return Math.max(15, currentZoom / 10);   // 500→50  300→30  200→20 …
	}

}

