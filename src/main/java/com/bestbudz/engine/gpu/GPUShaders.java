package com.bestbudz.engine.gpu;

/**
 * Simplified GPU Shaders - Direct wrapper for OpenGLRasterizer
 *
 * This class acts as a simple bridge between the game's rendering calls
 * and the OpenGL implementation, maintaining compatibility with existing
 * GPU rendering code while using the proper OpenGL rasterizer.
 */
public class GPUShaders {

	private static boolean initialized = false;

	/**
	 * Initialize GPU shaders - delegates to OpenGLRasterizer
	 */
	public static void initialize() {
		if (initialized) {
			return;
		}

		try {
			OpenGLRasterizer.initialize();
			initialized = true;
			System.out.println("✅ [GPU Shaders] Initialized using OpenGLRasterizer");
		} catch (Exception e) {
			System.err.println("❌ [GPU Shaders] Failed to initialize: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Render triangle - direct pass-through to OpenGLRasterizer
	 */
	public static void renderTriangle(int x1, int y1, int x2, int y2, int x3, int y3,
									  int hsl1, int hsl2, int hsl3, float z1, float z2, float z3) {
		if (!initialized) {
			return;
		}

		OpenGLRasterizer.method374(y1, y2, y3, x1, x2, x3, hsl1, hsl2, hsl3, z1, z2, z3);
	}

	/**
	 * Render textured triangle - direct pass-through to OpenGLRasterizer
	 */
	public static void renderTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c,
											  int l1, int l2, int l3, int tx1, int tx2, int tx3,
											  int ty1, int ty2, int ty3, int tz1, int tz2, int tz3,
											  int tex, float z1, float z2, float z3) {
		if (!initialized) {
			return;
		}

		OpenGLRasterizer.method378(y_a, y_b, y_c, x_a, x_b, x_c, l1, l2, l3,
			tx1, tx2, tx3, ty1, ty2, ty3, tz1, tz2, tz3,
			tex, z1, z2, z3);
	}

	/**
	 * Flush all batched rendering
	 */
	public static void flushAllBatches() {
		if (!initialized) {
			return;
		}

		OpenGLRasterizer.flush();
	}

	/**
	 * Set screen size for viewport calculations
	 */
	public static void setScreenSize(int width, int height) {
		if (!initialized) {
			return;
		}

		OpenGLRasterizer.setViewportSize(width, height);
	}

	/**
	 * Check if GPU shaders are initialized
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * Validate and reinitialize if needed
	 */
	public static void validateAndReinitialize() {
		if (!initialized) {
			initialize();
		}
		// OpenGLRasterizer handles its own validation internally
	}

	/**
	 * Clear texture cache
	 */
	public static void clearTextureCache() {
		// OpenGLRasterizer manages its own texture cache
		if (initialized) {
			System.out.println("[GPU Shaders] Texture cache managed by OpenGLRasterizer");
		}
	}

	/**
	 * Get debug statistics
	 */
	public static String getDebugStats() {
		if (!initialized) {
			return "GPU Shaders: Not initialized";
		}

		return "GPU Shaders: Using OpenGLRasterizer (active)";
	}

	/**
	 * Cleanup resources
	 */
	public static void cleanup() {
		if (!initialized) {
			return;
		}

		try {
			OpenGLRasterizer.cleanup();
			initialized = false;
			System.out.println("[GPU Shaders] Cleaned up OpenGLRasterizer resources");
		} catch (Exception e) {
			System.err.println("[GPU Shaders] Error during cleanup: " + e.getMessage());
		}
	}

	// ===== LEGACY DRAWING METHODS FOR COMPATIBILITY =====
	// These maintain compatibility with existing UI rendering code

	/**
	 * Draw rectangle using basic OpenGL
	 */
	public static void drawRectangle(int x, int y, int width, int height, int color) {
		if (!initialized) {
			return;
		}

		// Convert to triangles and use OpenGLRasterizer
		int hsl = convertRGBToHSL(color);
		float z = 0.5f;

		// Triangle 1: Top-left, top-right, bottom-left
		renderTriangle(x, y, x + width, y, x, y + height, hsl, hsl, hsl, z, z, z);

		// Triangle 2: Top-right, bottom-right, bottom-left
		renderTriangle(x + width, y, x + width, y + height, x, y + height, hsl, hsl, hsl, z, z, z);
	}

	/**
	 * Draw rectangle with alpha
	 */
	public static void drawRectangleAlpha(int x, int y, int width, int height, int color, int alpha) {
		// For now, just draw without alpha - OpenGLRasterizer will handle blending properly
		drawRectangle(x, y, width, height, color);
	}

	/**
	 * Draw circle using triangulated approach
	 */
	public static void drawCircle(int centerX, int centerY, int radius, int color) {
		if (!initialized) {
			return;
		}

		int hsl = convertRGBToHSL(color);
		float z = 0.5f;
		int segments = Math.max(8, radius / 2); // Adaptive segment count

		// Draw circle as triangle fan
		for (int i = 0; i < segments; i++) {
			double angle1 = 2.0 * Math.PI * i / segments;
			double angle2 = 2.0 * Math.PI * (i + 1) / segments;

			int x1 = centerX + (int)(Math.cos(angle1) * radius);
			int y1 = centerY + (int)(Math.sin(angle1) * radius);
			int x2 = centerX + (int)(Math.cos(angle2) * radius);
			int y2 = centerY + (int)(Math.sin(angle2) * radius);

			renderTriangle(centerX, centerY, x1, y1, x2, y2, hsl, hsl, hsl, z, z, z);
		}
	}

	/**
	 * Apply grayscale filter (placeholder)
	 */
	public static void filterGrayscale(int x, int y, int width, int height, float amount) {
		// This would require post-processing - not implemented in basic rasterizer
		System.out.println("[GPU Shaders] Grayscale filter not implemented in OpenGLRasterizer");
	}

	/**
	 * Draw gradient (simplified to solid color for now)
	 */
	public static void drawGradient(int x, int y, int width, int height, int startColor, int endColor, int alpha) {
		// Draw as interpolated triangles
		int startHSL = convertRGBToHSL(startColor);
		int endHSL = convertRGBToHSL(endColor);
		float z = 0.5f;

		// Triangle 1: Top-left, top-right, bottom-left
		renderTriangle(x, y, x + width, y, x, y + height, startHSL, startHSL, endHSL, z, z, z);

		// Triangle 2: Top-right, bottom-right, bottom-left
		renderTriangle(x + width, y, x + width, y + height, x, y + height, startHSL, endHSL, endHSL, z, z, z);
	}

	/**
	 * Draw sprite (placeholder)
	 */
	public static void drawSprite(int x, int y, int width, int height, int textureId) {
		// This would require texture loading - handled by textured triangles in game
		System.out.println("[GPU Shaders] Sprite drawing handled by renderTexturedTriangle");
	}

	// ===== UTILITY METHODS =====

	/**
	 * Convert RGB color to HSL index for compatibility
	 * This is a simplified conversion - the game should provide proper HSL indices
	 */
	private static int convertRGBToHSL(int rgb) {
		// For now, just find closest match in the palette
		// In practice, the game should already have HSL indices
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;

		// Simple approximation - find closest color in palette
		int closestIndex = 0;
		int closestDistance = Integer.MAX_VALUE;

		try {
			// Search a subset of the palette for performance
			for (int i = 0; i < Math.min(1000, com.bestbudz.engine.core.gamerender.Rasterizer.anIntArray1482.length); i++) {
				int paletteRGB = com.bestbudz.engine.core.gamerender.Rasterizer.anIntArray1482[i];
				int pr = (paletteRGB >> 16) & 0xFF;
				int pg = (paletteRGB >> 8) & 0xFF;
				int pb = paletteRGB & 0xFF;

				int distance = Math.abs(r - pr) + Math.abs(g - pg) + Math.abs(b - pb);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestIndex = i;
				}
			}
		} catch (Exception e) {
			// Fallback to a reasonable default
			return 1000;
		}

		return closestIndex;
	}
}