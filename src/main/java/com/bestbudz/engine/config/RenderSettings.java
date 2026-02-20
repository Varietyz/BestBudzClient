package com.bestbudz.engine.config;

public final class RenderSettings {

	public static int WORLD_VIEW_DISTANCE = 10;
	public static int REGION_LOAD_DISTANCE = 2;

	public static float LOD_DISTANCE_HIGH = 500.0f;
	public static float LOD_DISTANCE_MEDIUM = 1500.0f;
	public static float LOD_DISTANCE_LOW = 3000.0f;

	public static int MAX_TRIANGLES_PER_FRAME = 50000;
	public static boolean ENABLE_FRUSTUM_CULLING = true;
	public static boolean ENABLE_OCCLUSION_CULLING = false;
	public static boolean ENABLE_LOD_SYSTEM = true;

	public static boolean ENABLE_ROOFS = true;
	public static boolean ENABLE_DISTANCE_FOG = true;

	public static int getLODLevel(float worldDistance) {
		if (!ENABLE_LOD_SYSTEM) return 0;

		if (worldDistance < LOD_DISTANCE_HIGH) return 0;
		if (worldDistance < LOD_DISTANCE_MEDIUM) return 1;
		if (worldDistance < LOD_DISTANCE_LOW) return 2;
		return 3;
	}

	public static boolean shouldRender(float worldDistance) {
		return worldDistance < (WORLD_VIEW_DISTANCE * 128);
	}
}
