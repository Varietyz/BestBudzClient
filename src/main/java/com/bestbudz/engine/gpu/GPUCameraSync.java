package com.bestbudz.engine.gpu;

import com.bestbudz.engine.core.gamerender.WorldController;

/**
 * Converts RS2 camera angles into an OpenGL view-projection matrix.
 *
 * Key insight: Model.render() receives camera-relative world positions (worldX/Y/Z),
 * so the view matrix only needs camera ROTATION, not translation.
 *
 * RS2 camera transform order (from Model.render):
 *   1. Apply model yaw rotation in XZ plane
 *   2. Add camera-relative offset (worldX, worldY, worldZ)
 *   3. Camera yaw: rotate XZ by xCameraCurve (sinHorizontal/cosHorizontal)
 *   4. Camera pitch: rotate YZ by yCameraCurve (sinVertical/cosVertical)
 *   5. Perspective: screenX = centerX + (x << viewDist) / depth
 *
 * Steps 1-2 are done per-vertex on the CPU before upload.
 * Steps 3-5 become the view-projection matrix computed here.
 *
 * RS2 conventions:
 *   Rotations: 0-2047 (2048 = 360 degrees), sin/cos scaled by 65536
 *   Focal length: 2^WorldController.viewDistance (typically 1024)
 *   Near clip: depth < 50
 *   After rotation: +x = right, +y = up, +z = into screen (depth)
 */
public class GPUCameraSync {

	private static final float[] viewProjectionMatrix = new float[16];
	private static final float TWO_PI_OVER_2048 = (float) (2.0 * Math.PI / 2048.0);
	private static final float NEAR = 50.0f;
	private static final float FAR = 6400.0f;

	private static int lastXCameraCurve = -1;
	private static int lastYCameraCurve = -1;
	private static int lastViewportWidth;
	private static int lastViewportHeight;
	private static boolean dirty = true;

	/**
	 * Update the view-projection matrix from RS2 camera angles.
	 *
	 * @param xCameraCurve horizontal rotation (yaw), 0-2047
	 * @param yCameraCurve vertical rotation (pitch), 0-2047
	 * @param viewportWidth  viewport width in pixels
	 * @param viewportHeight viewport height in pixels
	 */
	public static void update(int xCameraCurve, int yCameraCurve,
							  int viewportWidth, int viewportHeight) {
		if (!dirty &&
			xCameraCurve == lastXCameraCurve &&
			yCameraCurve == lastYCameraCurve &&
			viewportWidth == lastViewportWidth &&
			viewportHeight == lastViewportHeight) {
			return;
		}

		lastXCameraCurve = xCameraCurve;
		lastYCameraCurve = yCameraCurve;
		lastViewportWidth = viewportWidth;
		lastViewportHeight = viewportHeight;
		dirty = false;

		computeViewProjection(xCameraCurve, yCameraCurve, viewportWidth, viewportHeight);
	}

	private static void computeViewProjection(int xCameraCurve, int yCameraCurve,
											   int vpWidth, int vpHeight) {
		// RS2 angles to radians
		float yaw = xCameraCurve * TWO_PI_OVER_2048;
		float pitch = yCameraCurve * TWO_PI_OVER_2048;

		float sinYaw = (float) Math.sin(yaw);
		float cosYaw = (float) Math.cos(yaw);
		float sinPitch = (float) Math.sin(pitch);
		float cosPitch = (float) Math.cos(pitch);

		// View rotation = RotPitch * RotYaw (matching RS2 Model.render order)
		//
		// RotYaw (Y-axis rotation in XZ plane):
		//   x' = x*cos + z*sin
		//   y' = y
		//   z' = -x*sin + z*cos
		//
		// RotPitch (X-axis rotation in YZ plane):
		//   x' = x
		//   y' = y*cos - z*sin
		//   z' = y*sin + z*cos
		//
		// Combined V_rs2 = RotPitch * RotYaw:
		//   row0: [ cosYaw,            0,       sinYaw          ]
		//   row1: [ sinPitch*sinYaw,   cosPitch, -sinPitch*cosYaw ]
		//   row2: [-cosPitch*sinYaw,   sinPitch,  cosPitch*cosYaw ]
		//
		// After this transform, z' > 0 means in front of camera (RS2 convention).
		// OpenGL needs z' < 0 for visible geometry, so negate row 2.
		//
		// V_gl (row-major, but stored column-major below):
		//   row0: [ cosYaw,           0,        sinYaw           ]
		//   row1: [ sinPitch*sinYaw,  cosPitch, -sinPitch*cosYaw ]
		//   row2: [ cosPitch*sinYaw, -sinPitch, -cosPitch*cosYaw ]

		// View matrix entries (v_row_col)
		float v00 = cosYaw;
		float v01 = 0;
		float v02 = sinYaw;

		float v10 = sinPitch * sinYaw;
		float v11 = cosPitch;
		float v12 = -sinPitch * cosYaw;

		float v20 = cosPitch * sinYaw;
		float v21 = -sinPitch;
		float v22 = -cosPitch * cosYaw;

		// No translation: input coordinates are already camera-relative

		// Projection: RS2 uses screenX = centerX + (x * focal) / depth
		// focal = 2^viewDistance, typically 1024
		// Matching frustum: right = near * (width/2) / focal
		//                   top   = near * (height/2) / focal

		int viewDist = WorldController.viewDistance;
		float focal = 1 << viewDist;

		float right = NEAR * (vpWidth * 0.5f) / focal;
		float top = NEAR * (vpHeight * 0.5f) / focal;

		// Projection matrix entries (standard OpenGL frustum)
		// p11 is negated to flip Y: RS2 has +Y = down on screen, GL has +Y = up
		float p00 = NEAR / right;    // = 2*focal/width
		float p11 = -NEAR / top;     // negated: RS2 Y-down → GL Y-up
		float p22 = -(FAR + NEAR) / (FAR - NEAR);
		float p23 = -1.0f;          // perspective divide: w_clip = -z_eye
		float p32 = -(2.0f * FAR * NEAR) / (FAR - NEAR);

		// VP = P * V, stored column-major
		// P is diagonal-ish, V has no translation, so this is straightforward.
		//
		// P (column-major):          V (column-major, no translation):
		// col0: [p00, 0,   0,   0]   col0: [v00, v10, v20, 0]
		// col1: [0,   p11, 0,   0]   col1: [v01, v11, v21, 0]
		// col2: [0,   0,   p22, p23]  col2: [v02, v12, v22, 0]
		// col3: [0,   0,   p32, 0 ]   col3: [0,   0,   0,   1]
		//
		// VP column j = P * V_column_j:
		//   For col j of V = [a, b, c, d]:
		//   VP[j*4+0] = p00*a
		//   VP[j*4+1] = p11*b
		//   VP[j*4+2] = p22*c + p32*d
		//   VP[j*4+3] = p23*c + 0*d

		// Column 0
		viewProjectionMatrix[0]  = p00 * v00;
		viewProjectionMatrix[1]  = p11 * v10;
		viewProjectionMatrix[2]  = p22 * v20;
		viewProjectionMatrix[3]  = p23 * v20;

		// Column 1
		viewProjectionMatrix[4]  = p00 * v01;  // 0
		viewProjectionMatrix[5]  = p11 * v11;
		viewProjectionMatrix[6]  = p22 * v21;
		viewProjectionMatrix[7]  = p23 * v21;

		// Column 2
		viewProjectionMatrix[8]  = p00 * v02;
		viewProjectionMatrix[9]  = p11 * v12;
		viewProjectionMatrix[10] = p22 * v22;
		viewProjectionMatrix[11] = p23 * v22;

		// Column 3 (V col3 = [0,0,0,1])
		viewProjectionMatrix[12] = 0;
		viewProjectionMatrix[13] = 0;
		viewProjectionMatrix[14] = p32;  // p22*0 + p32*1
		viewProjectionMatrix[15] = 0;    // p23*0 + 0*1
	}

	public static float[] getViewProjectionMatrix() {
		return viewProjectionMatrix;
	}

	public static void invalidate() {
		dirty = true;
	}
}
