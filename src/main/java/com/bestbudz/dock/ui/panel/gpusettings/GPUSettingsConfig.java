package com.bestbudz.dock.ui.panel.gpusettings;

/**
 * Configuration constants for the GPU Settings dock panel.
 * Panel ID range: 31000-31099 (reserved for this panel).
 */
public final class GPUSettingsConfig {

	private GPUSettingsConfig() {}

	public static final String PANEL_ID = "GPU Settings";
	public static final int PANEL_ID_BASE = 31000;

	// Slider ranges
	public static final float SUN_ELEVATION_MIN = 0f;
	public static final float SUN_ELEVATION_MAX = 90f;
	public static final float SUN_AZIMUTH_MIN = 0f;
	public static final float SUN_AZIMUTH_MAX = 360f;
	public static final float STRENGTH_MIN = 0f;
	public static final float STRENGTH_MAX = 2f;
	public static final float AMBIENT_STRENGTH_MAX = 1f;
	public static final float FOG_START_MIN = 1000f;
	public static final float FOG_START_MAX = 5000f;
	public static final float FOG_END_MIN = 2000f;
	public static final float FOG_END_MAX = 8000f;
	public static final float BLOOM_THRESHOLD_MIN = 0.5f;
	public static final float BLOOM_THRESHOLD_MAX = 3f;
	public static final float BLOOM_INTENSITY_MIN = 0f;
	public static final float BLOOM_INTENSITY_MAX = 1f;
	public static final float SSAO_RADIUS_MIN = 0.5f;
	public static final float SSAO_RADIUS_MAX = 3f;
	public static final float SSAO_STRENGTH_MIN = 0f;
	public static final float SSAO_STRENGTH_MAX = 1f;
	public static final float EXPOSURE_MIN = 0.5f;
	public static final float EXPOSURE_MAX = 3f;
	public static final float GAMMA_MIN = 1f;
	public static final float GAMMA_MAX = 3f;
	public static final float COLOR_GRADING_MIN = 0f;
	public static final float COLOR_GRADING_MAX = 1f;
	public static final float SUN_SIZE_MIN = 0.2f;
	public static final float SUN_SIZE_MAX = 3f;

	// Section names
	public static final String SECTION_LIGHTING = "Lighting";
	public static final String SECTION_ATMOSPHERE = "Atmosphere";
	public static final String SECTION_POST_PROCESSING = "Post-Processing";
	public static final String SECTION_PRESETS = "Presets";
}
