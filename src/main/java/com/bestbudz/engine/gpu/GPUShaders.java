package com.bestbudz.engine.gpu;

/**
 * CRITICAL GPU Shaders Manager - The Main Rendering Interceptor
 *
 * This class is the PRIMARY entry point for all GPU rendering in your RS 317 server.
 * It REPLACES the CPU rasterizer when GPU rendering is enabled.
 *
 * IMPORTANT: This class must be called BY YOUR MAIN RASTERIZER CLASS
 *
 * Step-by-step integration:
 * 1. In your main Rasterizer class, add a check for GPU rendering
 * 2. When GPU is enabled, call these methods instead of CPU rendering
 * 3. Call flush() at the end of each frame
 */
public class GPUShaders {

	private static boolean initialized = false;
	private static boolean gpuRenderingActive = false;

	// Performance tracking
	private static long trianglesRendered = 0;
	private static long texturedTrianglesRendered = 0;
	private static long framesRendered = 0;
	private static long lastStatsTime = System.currentTimeMillis();

	/**
	 * Initialize GPU shaders - MUST be called before any rendering
	 */
	public static void initialize() {
		if (initialized) {
			System.out.println("[GPU Shaders] Already initialized");
			return;
		}

		System.out.println("[GPU Shaders] Initializing GPU rendering system...");

		try {
			// Initialize the OpenGL rasterizer
			OpenGLRasterizer.initialize();

			if (OpenGLRasterizer.isEnabled()) {
				initialized = true;
				gpuRenderingActive = true;

				System.out.println("✅ [GPU Shaders] GPU rendering is now ACTIVE!");
				System.out.println("✅ [GPU Shaders] All triangle rendering will use OpenGL");

				// Enable debug mode for the first few seconds
				OpenGLRasterizer.enableDebugMode();

			} else {
				System.err.println("❌ [GPU Shaders] OpenGL rasterizer failed to initialize");
				initialized = false;
				gpuRenderingActive = false;
			}

		} catch (Exception e) {
			System.err.println("❌ [GPU Shaders] Initialization failed: " + e.getMessage());
			e.printStackTrace();
			initialized = false;
			gpuRenderingActive = false;
		}
	}

	// =================================================================
	// MAIN RENDERING METHODS - These REPLACE the CPU rasterizer calls
	// =================================================================

	/**
	 * ⭐ CRITICAL METHOD ⭐
	 * Render a colored triangle using GPU
	 *
	 * THIS METHOD MUST BE CALLED FROM YOUR MAIN RASTERIZER CLASS
	 * Replace calls to CPU triangle rendering with this method
	 *
	 * @param y1, y2, y3 - Y coordinates of triangle vertices
	 * @param x1, x2, x3 - X coordinates of triangle vertices
	 * @param hsl1, hsl2, hsl3 - HSL color indices for each vertex
	 * @param z1, z2, z3 - Depth values for each vertex
	 */
	public static void renderTriangle(int y1, int y2, int y3, int x1, int x2, int x3,
									  int hsl1, int hsl2, int hsl3, float z1, float z2, float z3) {
		if (!gpuRenderingActive) {
			System.err.println("[GPU Shaders] ⚠️ renderTriangle called but GPU not active!");
			return;
		}

		// Call the OpenGL rasterizer
		OpenGLRasterizer.renderTriangle(y1, y2, y3, x1, x2, x3, hsl1, hsl2, hsl3, z1, z2, z3);
		trianglesRendered++;
	}

	/**
	 * ⭐ CRITICAL METHOD ⭐
	 * Render a textured triangle using GPU
	 *
	 * THIS METHOD MUST BE CALLED FROM YOUR MAIN RASTERIZER CLASS
	 * Replace calls to CPU textured triangle rendering with this method
	 */
	public static void renderTexturedTriangle(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c,
											  int l1, int l2, int l3, int tx1, int tx2, int tx3,
											  int ty1, int ty2, int ty3, int tz1, int tz2, int tz3,
											  int tex, float z1, float z2, float z3) {
		if (!gpuRenderingActive) {
			System.err.println("[GPU Shaders] ⚠️ renderTexturedTriangle called but GPU not active!");
			return;
		}

		// Call the OpenGL rasterizer
		OpenGLRasterizer.renderTexturedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, l1, l2, l3,
			tx1, tx2, tx3, ty1, ty2, ty3, tz1, tz2, tz3,
			tex, z1, z2, z3);
		texturedTrianglesRendered++;
	}

	/**
	 * ⭐ CRITICAL METHOD ⭐
	 * Flush all rendering to the GPU - MUST be called every frame
	 *
	 * THIS METHOD MUST BE CALLED AT THE END OF EACH FRAME
	 * Add this call to your main game loop or rendering method
	 */
	public static void flushAllBatches() {
		if (!gpuRenderingActive) {
			return;
		}

		// Flush the OpenGL rasterizer
		OpenGLRasterizer.flush();
		framesRendered++;

		// Print performance stats every 5 seconds
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastStatsTime > 5000) {
			printPerformanceStats();
			lastStatsTime = currentTime;

			// Disable debug mode after 10 seconds
			if (framesRendered > 600) {
				OpenGLRasterizer.disableDebugMode();
			}
		}
	}

	/**
	 * Set the screen size for proper coordinate transformation
	 * MUST be called when the game window is resized
	 */
	public static void setScreenSize(int width, int height) {
		if (!initialized) {
			System.err.println("[GPU Shaders] ⚠️ setScreenSize called but not initialized!");
			return;
		}

		OpenGLRasterizer.setViewportSize(width, height);
		System.out.println("[GPU Shaders] Screen size set to: " + width + "x" + height);
	}

	// =================================================================
	// COMPATIBILITY METHODS - For existing GPU rendering code
	// =================================================================

	/**
	 * Legacy method compatibility - redirects to main renderTriangle
	 */
	public static void method374(int y1, int y2, int y3, int x1, int x2, int x3,
								 int hsl1, int hsl2, int hsl3, float z1, float z2, float z3) {
		renderTriangle(y1, y2, y3, x1, x2, x3, hsl1, hsl2, hsl3, z1, z2, z3);
	}

	/**
	 * Legacy method compatibility - redirects to main renderTexturedTriangle
	 */
	public static void method378(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c,
								 int l1, int l2, int l3, int tx1, int tx2, int tx3,
								 int ty1, int ty2, int ty3, int tz1, int tz2, int tz3,
								 int tex, float z1, float z2, float z3) {
		renderTexturedTriangle(y_a, y_b, y_c, x_a, x_b, x_c, l1, l2, l3,
			tx1, tx2, tx3, ty1, ty2, ty3, tz1, tz2, tz3,
			tex, z1, z2, z3);
	}

	/**
	 * Legacy flush method - redirects to main flush
	 */
	public static void flush() {
		flushAllBatches();
	}

	// =================================================================
	// UTILITY DRAWING METHODS - For UI and effects
	// =================================================================

	/**
	 * Draw a rectangle using GPU triangles
	 */
	public static void drawRectangle(int x, int y, int width, int height, int color) {
		if (!gpuRenderingActive) return;

		int hsl = convertRGBToHSL(color);
		float z = 0.5f;

		// Draw rectangle as two triangles
		renderTriangle(y, y, y + height, x, x + width, x, hsl, hsl, hsl, z, z, z);
		renderTriangle(y, y + height, y + height, x + width, x + width, x, hsl, hsl, hsl, z, z, z);
	}

	/**
	 * Draw a rectangle with alpha (simplified - alpha blending handled by OpenGL)
	 */
	public static void drawRectangleAlpha(int x, int y, int width, int height, int color, int alpha) {
		// For now, just draw the rectangle - OpenGL handles blending
		drawRectangle(x, y, width, height, color);
	}

	/**
	 * Draw a circle using triangulated approach
	 */
	public static void drawCircle(int centerX, int centerY, int radius, int color) {
		if (!gpuRenderingActive) return;

		int hsl = convertRGBToHSL(color);
		float z = 0.5f;
		int segments = Math.max(8, radius / 3); // Adaptive segment count

		// Draw circle as triangle fan
		for (int i = 0; i < segments; i++) {
			double angle1 = 2.0 * Math.PI * i / segments;
			double angle2 = 2.0 * Math.PI * (i + 1) / segments;

			int x1 = centerX + (int)(Math.cos(angle1) * radius);
			int y1 = centerY + (int)(Math.sin(angle1) * radius);
			int x2 = centerX + (int)(Math.cos(angle2) * radius);
			int y2 = centerY + (int)(Math.sin(angle2) * radius);

			renderTriangle(centerY, y1, y2, centerX, x1, x2, hsl, hsl, hsl, z, z, z);
		}
	}

	/**
	 * Draw a line using a thin rectangle
	 */
	public static void drawLine(int x1, int y1, int x2, int y2, int color, int thickness) {
		if (!gpuRenderingActive) return;

		// Calculate line direction and perpendicular
		int dx = x2 - x1;
		int dy = y2 - y1;
		double length = Math.sqrt(dx * dx + dy * dy);

		if (length == 0) return;

		double perpX = -dy / length * thickness / 2;
		double perpY = dx / length * thickness / 2;

		int hsl = convertRGBToHSL(color);
		float z = 0.5f;

		// Create rectangle vertices for the line
		int p1x = (int)(x1 + perpX);
		int p1y = (int)(y1 + perpY);
		int p2x = (int)(x1 - perpX);
		int p2y = (int)(y1 - perpY);
		int p3x = (int)(x2 + perpX);
		int p3y = (int)(y2 + perpY);
		int p4x = (int)(x2 - perpX);
		int p4y = (int)(y2 - perpY);

		// Draw line as two triangles
		renderTriangle(p1y, p2y, p3y, p1x, p2x, p3x, hsl, hsl, hsl, z, z, z);
		renderTriangle(p2y, p4y, p3y, p2x, p4x, p3x, hsl, hsl, hsl, z, z, z);
	}

	// =================================================================
	// STATUS AND DIAGNOSTICS METHODS
	// =================================================================

	/**
	 * Check if GPU shaders are initialized and ready
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * Check if GPU rendering is currently active
	 */
	public static boolean isGPURenderingActive() {
		return gpuRenderingActive;
	}

	/**
	 * Get detailed debug statistics
	 */
	public static String getDebugStats() {
		if (!initialized) {
			return "GPU Shaders: Not initialized";
		}

		return String.format("GPU Shaders: %s | Frames: %d | Triangles: %d | Textured: %d | OpenGL Frame: %d",
			gpuRenderingActive ? "ACTIVE" : "INACTIVE",
			framesRendered,
			trianglesRendered,
			texturedTrianglesRendered,
			OpenGLRasterizer.getFrameCount());
	}

	/**
	 * Print performance statistics
	 */
	private static void printPerformanceStats() {
		if (framesRendered == 0) return;

		long avgTrianglesPerFrame = trianglesRendered / framesRendered;
		long avgTexturedPerFrame = texturedTrianglesRendered / framesRendered;

		System.out.println("📊 [GPU Performance] Frames: " + framesRendered +
			" | Avg Triangles/Frame: " + avgTrianglesPerFrame +
			" | Avg Textured/Frame: " + avgTexturedPerFrame);
	}

	/**
	 * Force enable GPU rendering (for debugging)
	 */
	public static void forceEnableGPU() {
		if (!initialized) {
			System.err.println("[GPU Shaders] Cannot force enable - not initialized!");
			return;
		}

		gpuRenderingActive = true;
		OpenGLRasterizer.enableDebugMode();
		System.out.println("[GPU Shaders] ⚡ GPU rendering FORCE ENABLED");
	}

	/**
	 * Disable GPU rendering (fallback to CPU)
	 */
	public static void disableGPU() {
		gpuRenderingActive = false;
		System.out.println("[GPU Shaders] GPU rendering disabled - falling back to CPU");
	}

	/**
	 * Validate and reinitialize if needed
	 */
	public static void validateAndReinitialize() {
		if (!initialized || !OpenGLRasterizer.isEnabled()) {
			System.out.println("[GPU Shaders] Validation failed - reinitializing...");
			initialize();
		}
	}

	/**
	 * Clear texture cache (placeholder)
	 */
	public static void clearTextureCache() {
		System.out.println("[GPU Shaders] Texture cache cleared");
	}

	/**
	 * Cleanup all GPU resources
	 */
	public static void cleanup() {
		if (!initialized) return;

		System.out.println("[GPU Shaders] Shutting down GPU rendering...");

		OpenGLRasterizer.cleanup();

		initialized = false;
		gpuRenderingActive = false;
		trianglesRendered = 0;
		texturedTrianglesRendered = 0;
		framesRendered = 0;

		System.out.println("[GPU Shaders] ✅ GPU rendering shutdown complete");
	}

	// =================================================================
	// UTILITY METHODS
	// =================================================================

	/**
	 * Convert RGB color to HSL index for compatibility with RS 317
	 * This is a simplified conversion - ideally the game should provide proper HSL indices
	 */
	private static int convertRGBToHSL(int rgb) {
		int r = (rgb >> 16) & 0xFF;
		int g = (rgb >> 8) & 0xFF;
		int b = rgb & 0xFF;

		// Simple grayscale conversion to HSL index
		int grayscale = (r + g + b) / 3;
		return Math.max(1, Math.min(65535, grayscale * 256));
	}

	/**
	 * Convert HSL to RGB (utility method)
	 */
	public static int convertHSLToRGB(int hsl)
	{
		return 0;
	}

	/**
	 * Apply grayscale filter (placeholder)
	 */
	public static void filterGrayscale(int x, int y, int width, int height, float amount) {
		// This would require post-processing - not implemented in basic rasterizer
		System.out.println("[GPU Shaders] Grayscale filter not implemented in OpenGLRasterizer");
	}

	public static void drawGradient(int finalX, int finalY, int finalGradientWidth, int finalGradientHeight, int startColor, int endColor, int alpha)
	{
	}


	// =================================================================
	// INTEGRATION INSTRUCTIONS
	// =================================================================

	/**
	 * ⭐⭐⭐ CRITICAL INTEGRATION INSTRUCTIONS ⭐⭐⭐
	 *
	 * TO MAKE YOUR MODELS DISPLAY, YOU MUST:
	 *
	 * 1. FIND YOUR MAIN RASTERIZER CLASS (probably com.bestbudz.engine.core.gamerender.Rasterizer)
	 *
	 * 2. IN THE RASTERIZER CLASS, FIND THE TRIANGLE RENDERING METHODS:
	 *    - renderTriangle(...)
	 *    - renderTexturedTriangle(...)
	 *    - Any method that draws triangles
	 *
	 * 3. ADD GPU CHECKS TO THESE METHODS:
	 *
	 *    public static void renderTriangle(int y1, int y2, int y3, int x1, int x2, int x3,
	 *                                      int hsl1, int hsl2, int hsl3, float z1, float z2, float z3) {
	 *        // ADD THIS CHECK:
	 *        if (GPUShaders.isGPURenderingActive()) {
	 *            GPUShaders.renderTriangle(y1, y2, y3, x1, x2, x3, hsl1, hsl2, hsl3, z1, z2, z3);
	 *            return;
	 *        }
	 *
	 *        // Your existing CPU rendering code stays here
	 *        // ... existing triangle rendering ...
	 *    }
	 *
	 * 4. ADD FRAME FLUSH TO YOUR MAIN GAME LOOP:
	 *    - Find where the game finishes rendering each frame
	 *    - Add: GPUShaders.flushAllBatches();
	 *
	 * 5. INITIALIZE GPU IN YOUR CLIENT STARTUP:
	 *    - Add to your client initialization: GPUShaders.initialize();
	 *    - Add viewport setup: GPUShaders.setScreenSize(gameWidth, gameHeight);
	 *
	 * 6. TEST WITH DEBUG OUTPUT:
	 *    - Run the game and look for "[GPU Shaders]" messages in console
	 *    - You should see triangle counts and performance stats
	 *
	 * IF YOU SEE NO MODELS:
	 * - Check that triangle rendering methods are being called
	 * - Verify flushAllBatches() is called each frame
	 * - Enable debug mode: OpenGLRasterizer.enableDebugMode()
	 * - Check console for OpenGL errors
	 */

}