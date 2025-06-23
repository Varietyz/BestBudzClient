package com.bestbudz.engine.config;

/**
 * FIXED: Render settings completely separate from camera
 * These control WHAT gets rendered, not HOW the camera views it
 */
public final class RenderSettings {

	// === WORLD RENDER DISTANCE (NOT CAMERA DEPENDENT) ===
	public static int WORLD_VIEW_DISTANCE = 10;        // How far to render world geometry
	public static int REGION_LOAD_DISTANCE = 2;        // How many regions around player to keep loaded

	// === LEVEL OF DETAIL SETTINGS ===
	public static float LOD_DISTANCE_HIGH = 500.0f;    // High detail within 500 units
	public static float LOD_DISTANCE_MEDIUM = 1500.0f; // Medium detail within 1500 units
	public static float LOD_DISTANCE_LOW = 3000.0f;    // Low detail within 3000 units

	// === QUALITY SETTINGS ===
	public static int MAX_TRIANGLES_PER_FRAME = 50000; // Performance limit
	public static boolean ENABLE_FRUSTUM_CULLING = true;
	public static boolean ENABLE_OCCLUSION_CULLING = false;
	public static boolean ENABLE_LOD_SYSTEM = true;

	// === CAMERA INDEPENDENT SETTINGS ===
	public static boolean ENABLE_ROOFS = true;         // From SettingsConfig
	public static boolean ENABLE_DISTANCE_FOG = true;  // From SettingsConfig

	/**
	 * Get LOD level based on WORLD distance (not camera)
	 */
	public static int getLODLevel(float worldDistance) {
		if (!ENABLE_LOD_SYSTEM) return 0; // High detail always

		if (worldDistance < LOD_DISTANCE_HIGH) return 0;    // High detail
		if (worldDistance < LOD_DISTANCE_MEDIUM) return 1;  // Medium detail
		if (worldDistance < LOD_DISTANCE_LOW) return 2;     // Low detail
		return 3; // Very low detail
	}

	/**
	 * Should this object be rendered based on distance?
	 */
	public static boolean shouldRender(float worldDistance) {
		return worldDistance < (WORLD_VIEW_DISTANCE * 128); // Convert to world units
	}
}