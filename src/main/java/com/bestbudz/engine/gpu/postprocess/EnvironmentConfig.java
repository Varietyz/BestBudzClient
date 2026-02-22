package com.bestbudz.engine.gpu.postprocess;

/**
 * Central source of truth for all lighting, atmosphere, and post-processing parameters.
 * All fields are static and mutable -- the GPU Settings dock panel writes directly here.
 * Renderers read from here each frame.
 */
public class EnvironmentConfig {

	// ========== Sun / Directional Light ==========
	public static float sunAzimuth = 225.0f;    // degrees, 0=north, clockwise
	public static float sunElevation = 45.0f;   // degrees above horizon
	public static float sunColorR = 1.0f;
	public static float sunColorG = 0.95f;
	public static float sunColorB = 0.85f;
	public static float sunStrength = 1.2f;

	// ========== Ambient Light ==========
	public static float ambientColorR = 0.4f;
	public static float ambientColorG = 0.45f;
	public static float ambientColorB = 0.6f;
	public static float ambientStrength = 0.35f;

	// ========== Sky ==========
	public static float skyZenithR = 0.15f;
	public static float skyZenithG = 0.3f;
	public static float skyZenithB = 0.65f;
	public static float skyHorizonR = 0.6f;
	public static float skyHorizonG = 0.7f;
	public static float skyHorizonB = 0.85f;
	public static float sunSize = 1.0f;

	// ========== Fog ==========
	public static float fogStart = 2000.0f;
	public static float fogEnd = 5000.0f;
	public static float fogDensity = 0.5f;
	public static float fogColorR = 0.6f;
	public static float fogColorG = 0.65f;
	public static float fogColorB = 0.75f;

	// ========== Bloom ==========
	public static boolean bloomEnabled = true;
	public static float bloomThreshold = 1.2f;
	public static float bloomIntensity = 0.3f;

	// ========== SSAO ==========
	public static boolean ssaoEnabled = true;
	public static float ssaoRadius = 1.5f;
	public static float ssaoStrength = 0.6f;
	public static float ssaoBias = 0.025f;

	// ========== Tone Mapping ==========
	public static float exposure = 1.0f;
	public static float gamma = 2.2f;

	// ========== Color Grading ==========
	public static float colorGradingStrength = 0.5f;

	// ========== Animated Textures ==========
	public static boolean enableAnimatedTextures = true;

	/**
	 * Compute normalized sun direction vector from azimuth/elevation angles.
	 * Returns float[3] = {x, y, z} where +y = up.
	 */
	public static float[] getSunDirection() {
		float azRad = (float) Math.toRadians(sunAzimuth);
		float elRad = (float) Math.toRadians(sunElevation);
		float cosEl = (float) Math.cos(elRad);
		return new float[] {
			(float) -Math.sin(azRad) * cosEl,  // x
			(float) Math.sin(elRad),             // y (up)
			(float) -Math.cos(azRad) * cosEl    // z
		};
	}

	/** Apply "Default" preset */
	public static void presetDefault() {
		sunAzimuth = 225.0f; sunElevation = 45.0f;
		sunColorR = 1.0f; sunColorG = 0.95f; sunColorB = 0.85f; sunStrength = 1.2f;
		ambientColorR = 0.4f; ambientColorG = 0.45f; ambientColorB = 0.6f; ambientStrength = 0.35f;
		skyZenithR = 0.15f; skyZenithG = 0.3f; skyZenithB = 0.65f;
		skyHorizonR = 0.6f; skyHorizonG = 0.7f; skyHorizonB = 0.85f; sunSize = 1.0f;
		fogStart = 2000.0f; fogEnd = 5000.0f; fogDensity = 0.5f;
		fogColorR = 0.6f; fogColorG = 0.65f; fogColorB = 0.75f;
		bloomEnabled = true; bloomThreshold = 1.2f; bloomIntensity = 0.3f;
		ssaoEnabled = true; ssaoRadius = 1.5f; ssaoStrength = 0.6f; ssaoBias = 0.025f;
		exposure = 1.0f; gamma = 2.2f; colorGradingStrength = 0.5f;
	}

	/** Apply "Cinematic" preset -- warm, contrasty, dramatic */
	public static void presetCinematic() {
		sunAzimuth = 200.0f; sunElevation = 25.0f;
		sunColorR = 1.0f; sunColorG = 0.85f; sunColorB = 0.6f; sunStrength = 1.8f;
		ambientColorR = 0.3f; ambientColorG = 0.35f; ambientColorB = 0.5f; ambientStrength = 0.25f;
		skyZenithR = 0.1f; skyZenithG = 0.2f; skyZenithB = 0.5f;
		skyHorizonR = 0.7f; skyHorizonG = 0.55f; skyHorizonB = 0.4f; sunSize = 1.5f;
		fogStart = 1500.0f; fogEnd = 4000.0f; fogDensity = 0.7f;
		fogColorR = 0.5f; fogColorG = 0.45f; fogColorB = 0.4f;
		bloomEnabled = true; bloomThreshold = 0.8f; bloomIntensity = 0.5f;
		ssaoEnabled = true; ssaoRadius = 2.0f; ssaoStrength = 0.8f; ssaoBias = 0.025f;
		exposure = 1.2f; gamma = 2.2f; colorGradingStrength = 0.8f;
	}

	/** Apply "Vivid" preset -- saturated, bright */
	public static void presetVivid() {
		sunAzimuth = 240.0f; sunElevation = 55.0f;
		sunColorR = 1.0f; sunColorG = 1.0f; sunColorB = 0.95f; sunStrength = 1.4f;
		ambientColorR = 0.5f; ambientColorG = 0.5f; ambientColorB = 0.65f; ambientStrength = 0.4f;
		skyZenithR = 0.1f; skyZenithG = 0.35f; skyZenithB = 0.75f;
		skyHorizonR = 0.55f; skyHorizonG = 0.75f; skyHorizonB = 0.95f; sunSize = 0.8f;
		fogStart = 2500.0f; fogEnd = 6000.0f; fogDensity = 0.3f;
		fogColorR = 0.65f; fogColorG = 0.7f; fogColorB = 0.85f;
		bloomEnabled = true; bloomThreshold = 1.0f; bloomIntensity = 0.4f;
		ssaoEnabled = true; ssaoRadius = 1.2f; ssaoStrength = 0.5f; ssaoBias = 0.025f;
		exposure = 1.1f; gamma = 2.2f; colorGradingStrength = 0.3f;
	}

	/** Apply "Dark Fantasy" preset -- moody, dark, desaturated */
	public static void presetDarkFantasy() {
		sunAzimuth = 180.0f; sunElevation = 15.0f;
		sunColorR = 0.85f; sunColorG = 0.75f; sunColorB = 0.65f; sunStrength = 0.8f;
		ambientColorR = 0.25f; ambientColorG = 0.25f; ambientColorB = 0.35f; ambientStrength = 0.3f;
		skyZenithR = 0.05f; skyZenithG = 0.08f; skyZenithB = 0.15f;
		skyHorizonR = 0.25f; skyHorizonG = 0.2f; skyHorizonB = 0.2f; sunSize = 2.0f;
		fogStart = 1000.0f; fogEnd = 3000.0f; fogDensity = 0.9f;
		fogColorR = 0.2f; fogColorG = 0.2f; fogColorB = 0.25f;
		bloomEnabled = true; bloomThreshold = 0.6f; bloomIntensity = 0.2f;
		ssaoEnabled = true; ssaoRadius = 2.5f; ssaoStrength = 1.0f; ssaoBias = 0.025f;
		exposure = 0.8f; gamma = 2.2f; colorGradingStrength = 0.9f;
	}
}
