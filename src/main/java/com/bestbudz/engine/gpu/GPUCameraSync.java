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
	private static final float[] inverseViewProjectionMatrix = new float[16];
	private static final float[] projectionMatrix = new float[16];
	private static final float[] inverseProjectionMatrix = new float[16];
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

		// Store projection matrix (column-major) for SSAO
		projectionMatrix[0]  = p00; projectionMatrix[1]  = 0;    projectionMatrix[2]  = 0;   projectionMatrix[3]  = 0;
		projectionMatrix[4]  = 0;   projectionMatrix[5]  = p11;  projectionMatrix[6]  = 0;   projectionMatrix[7]  = 0;
		projectionMatrix[8]  = 0;   projectionMatrix[9]  = 0;    projectionMatrix[10] = p22; projectionMatrix[11] = p23;
		projectionMatrix[12] = 0;   projectionMatrix[13] = 0;    projectionMatrix[14] = p32; projectionMatrix[15] = 0;

		// Inverse projection (analytic inversion of perspective matrix)
		inverseProjectionMatrix[0]  = 1.0f / p00; inverseProjectionMatrix[1]  = 0;           inverseProjectionMatrix[2]  = 0;   inverseProjectionMatrix[3]  = 0;
		inverseProjectionMatrix[4]  = 0;           inverseProjectionMatrix[5]  = 1.0f / p11;  inverseProjectionMatrix[6]  = 0;   inverseProjectionMatrix[7]  = 0;
		inverseProjectionMatrix[8]  = 0;           inverseProjectionMatrix[9]  = 0;            inverseProjectionMatrix[10] = 0;   inverseProjectionMatrix[11] = 1.0f / p32;
		inverseProjectionMatrix[12] = 0;           inverseProjectionMatrix[13] = 0;            inverseProjectionMatrix[14] = 1.0f / p23; inverseProjectionMatrix[15] = -p22 / (p23 * p32);

		// Inverse view-projection (used by sky shader)
		invertMatrix4(viewProjectionMatrix, inverseViewProjectionMatrix);
	}

	/**
	 * Invert a 4x4 column-major matrix. Used for inverse VP (sky shader ray reconstruction).
	 */
	private static void invertMatrix4(float[] m, float[] out) {
		// Compute cofactors and determinant
		float a00 = m[0], a01 = m[1], a02 = m[2], a03 = m[3];
		float a10 = m[4], a11 = m[5], a12 = m[6], a13 = m[7];
		float a20 = m[8], a21 = m[9], a22 = m[10], a23 = m[11];
		float a30 = m[12], a31 = m[13], a32 = m[14], a33 = m[15];

		float b00 = a00 * a11 - a01 * a10;
		float b01 = a00 * a12 - a02 * a10;
		float b02 = a00 * a13 - a03 * a10;
		float b03 = a01 * a12 - a02 * a11;
		float b04 = a01 * a13 - a03 * a11;
		float b05 = a02 * a13 - a03 * a12;
		float b06 = a20 * a31 - a21 * a30;
		float b07 = a20 * a32 - a22 * a30;
		float b08 = a20 * a33 - a23 * a30;
		float b09 = a21 * a32 - a22 * a31;
		float b10 = a21 * a33 - a23 * a31;
		float b11 = a22 * a33 - a23 * a32;

		float det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;
		if (Math.abs(det) < 1e-10f) {
			// Singular matrix, return identity
			System.arraycopy(new float[]{1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1}, 0, out, 0, 16);
			return;
		}

		float invDet = 1.0f / det;
		out[0]  = ( a11 * b11 - a12 * b10 + a13 * b09) * invDet;
		out[1]  = (-a01 * b11 + a02 * b10 - a03 * b09) * invDet;
		out[2]  = ( a31 * b05 - a32 * b04 + a33 * b03) * invDet;
		out[3]  = (-a21 * b05 + a22 * b04 - a23 * b03) * invDet;
		out[4]  = (-a10 * b11 + a12 * b08 - a13 * b07) * invDet;
		out[5]  = ( a00 * b11 - a02 * b08 + a03 * b07) * invDet;
		out[6]  = (-a30 * b05 + a32 * b02 - a33 * b01) * invDet;
		out[7]  = ( a20 * b05 - a22 * b02 + a23 * b01) * invDet;
		out[8]  = ( a10 * b10 - a11 * b08 + a13 * b06) * invDet;
		out[9]  = (-a00 * b10 + a01 * b08 - a03 * b06) * invDet;
		out[10] = ( a30 * b04 - a31 * b02 + a33 * b00) * invDet;
		out[11] = (-a20 * b04 + a21 * b02 - a23 * b00) * invDet;
		out[12] = (-a10 * b09 + a11 * b07 - a12 * b06) * invDet;
		out[13] = ( a00 * b09 - a01 * b07 + a02 * b06) * invDet;
		out[14] = (-a30 * b03 + a31 * b01 - a32 * b00) * invDet;
		out[15] = ( a20 * b03 - a21 * b01 + a22 * b00) * invDet;
	}

	public static float[] getViewProjectionMatrix() {
		return viewProjectionMatrix;
	}

	public static float[] getInverseViewProjectionMatrix() {
		return inverseViewProjectionMatrix;
	}

	public static float[] getProjectionMatrix() {
		return projectionMatrix;
	}

	public static float[] getInverseProjectionMatrix() {
		return inverseProjectionMatrix;
	}

	public static void invalidate() {
		dirty = true;
	}
}
