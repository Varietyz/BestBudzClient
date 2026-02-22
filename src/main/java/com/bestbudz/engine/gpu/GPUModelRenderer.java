package com.bestbudz.engine.gpu;

import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.engine.gpu.postprocess.EnvironmentConfig;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import com.bestbudz.engine.gpu.shader.ShaderSources;
import com.bestbudz.rendering.model.Model;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * GPU model renderer with HSL color palette and texture array support.
 *
 * Vertex format per triangle vertex (32 bytes = 8 ints):
 *   int posX, posY, posZ     (12 bytes) - RS2 world-unit position
 *   int colorHSL             (4 bytes)  - 16-bit HSL palette index
 *   int alpha                (4 bytes)  - 0-255 opacity (255 = opaque)
 *   int textureId            (4 bytes)  - texture array layer (-1 = untextured)
 *   float u, v               (8 bytes)  - UV coordinates (stored as int bits)
 *
 * Fragment shader: if textureId >= 0, samples GL_TEXTURE_2D_ARRAY;
 * otherwise uses 256x256 HSL color palette texture.
 */
public class GPUModelRenderer {

	private static final int INTS_PER_VERTEX = 8;  // 32 bytes

	private static boolean initialized = false;
	private static ShaderProgram shader;
	private static int vao;
	private static int vbo;

	// Color palette texture: 256x256 RGBA, holding 65536 RGB entries
	private static int colorPaletteTexture;
	private static boolean paletteUploaded = false;

	// Uniform locations
	private static int uViewProjection;
	private static int uCameraPosition;
	private static int uColorPalette;
	private static int uTextureArray;
	private static int uCurrentPlane;

	// Lighting uniform locations
	private static int uSunDirection;
	private static int uSunColor;
	private static int uSunStrength;
	private static int uAmbientColor;
	private static int uAmbientStrength;

	// Fog uniform locations
	private static int uFogColor;
	private static int uFogStart;
	private static int uFogEnd;

	// Animated texture uniform locations
	private static int uTime;
	private static int uAnimateTextures;

	// Frame time counter (seconds since GPU init)
	private static long gpuStartTimeNanos = 0;
	private static float currentFrameTime = 0.0f;

	// Reusable vertex buffer (grows as needed)
	private static IntBuffer vertexData;
	private static int vertexDataCapacity;

	// Sin/cos tables reference (from Rasterizer)
	private static int[] sinTable;
	private static int[] cosTable;

	// Frame state
	private static boolean inFrame = false;

	// Stats
	private static int modelsRenderedThisFrame;
	private static int trianglesRenderedThisFrame;

	public static boolean initialize() {
		if (initialized) {
			return true;
		}

		try {
			shader = new ShaderProgram(ShaderSources.SCENE_VERTEX, ShaderSources.SCENE_GEOMETRY, ShaderSources.SCENE_FRAGMENT);
			if (!shader.isValid()) {
				System.err.println("[GPUModelRenderer] Scene shader compilation failed");
				return false;
			}

			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();

			if (vao == 0 || vbo == 0) {
				System.err.println("[GPUModelRenderer] Failed to create GL objects");
				return false;
			}

			int stride = INTS_PER_VERTEX * 4; // 32 bytes

			GL30.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

			// Attribute 0: ivec4 positionAndColor (location 0) - offset 0
			GL30.glVertexAttribIPointer(0, 4, GL11.GL_INT, stride, 0);
			GL20.glEnableVertexAttribArray(0);

			// Attribute 1: int alpha (location 1) - offset 16
			GL30.glVertexAttribIPointer(1, 1, GL11.GL_INT, stride, 16);
			GL20.glEnableVertexAttribArray(1);

			// Attribute 2: int textureId (location 2) - offset 20
			GL30.glVertexAttribIPointer(2, 1, GL11.GL_INT, stride, 20);
			GL20.glEnableVertexAttribArray(2);

			// Attribute 3: vec2 texCoord (location 3) - offset 24
			// Float data stored as raw int bits in the IntBuffer
			GL20.glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, stride, 24);
			GL20.glEnableVertexAttribArray(3);

			GL30.glBindVertexArray(0);

			// Cache uniform locations
			shader.bind();
			uViewProjection = shader.getUniformLocation("uViewProjection");
			uCameraPosition = shader.getUniformLocation("uCameraPosition");
			uColorPalette = shader.getUniformLocation("uColorPalette");
			uTextureArray = shader.getUniformLocation("uTextureArray");
			uCurrentPlane = shader.getUniformLocation("uCurrentPlane");

			// Lighting uniforms
			uSunDirection = shader.getUniformLocation("uSunDirection");
			uSunColor = shader.getUniformLocation("uSunColor");
			uSunStrength = shader.getUniformLocation("uSunStrength");
			uAmbientColor = shader.getUniformLocation("uAmbientColor");
			uAmbientStrength = shader.getUniformLocation("uAmbientStrength");

			// Fog uniforms
			uFogColor = shader.getUniformLocation("uFogColor");
			uFogStart = shader.getUniformLocation("uFogStart");
			uFogEnd = shader.getUniformLocation("uFogEnd");

			// Animated texture uniforms
			uTime = shader.getUniformLocation("uTime");
			uAnimateTextures = shader.getUniformLocation("uAnimateTextures");

			shader.unbind();

			gpuStartTimeNanos = System.nanoTime();

			// Create color palette texture
			createColorPaletteTexture();

			// Initial vertex buffer
			vertexDataCapacity = 5000 * INTS_PER_VERTEX;
			vertexData = BufferUtils.createIntBuffer(vertexDataCapacity);

			// Cache sin/cos tables
			sinTable = Rasterizer.sinTable;
			cosTable = Rasterizer.cosTable;

			initialized = true;
			System.out.println("[GPUModelRenderer] Initialized successfully (Phase 2 - textures)");
			return true;

		} catch (Exception e) {
			System.err.println("[GPUModelRenderer] Init failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private static void createColorPaletteTexture() {
		colorPaletteTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorPaletteTexture);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, 256, 256, 0,
			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	}

	public static void uploadColorPalette() {
		if (!initialized || Rasterizer.colorPalette == null) {
			return;
		}

		ByteBuffer paletteBuffer = BufferUtils.createByteBuffer(256 * 256 * 4);
		int[] palette = Rasterizer.colorPalette;
		for (int i = 0; i < 65536; i++) {
			int rgb = palette[i];
			paletteBuffer.put((byte) ((rgb >> 16) & 0xFF));
			paletteBuffer.put((byte) ((rgb >> 8) & 0xFF));
			paletteBuffer.put((byte) (rgb & 0xFF));
			paletteBuffer.put((byte) 0xFF);
		}
		paletteBuffer.flip();

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorPaletteTexture);
		GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 256, 256,
			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, paletteBuffer);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);

		paletteUploaded = true;
	}

	public static void beginFrame() {
		modelsRenderedThisFrame = 0;
		trianglesRenderedThisFrame = 0;
		inFrame = true;

		// Compute elapsed time in seconds for animated textures
		currentFrameTime = (float) ((System.nanoTime() - gpuStartTimeNanos) / 1_000_000_000.0);

		if (!paletteUploaded) {
			uploadColorPalette();
		}
	}

	public static void renderModel(Model model, int worldX, int worldY, int worldZ,
									int rotation) {
		if (!initialized || model == null) {
			return;
		}

		if (model.vertexCount == 0 || model.triangleCount == 0) {
			return;
		}

		if (model.triangleColorA == null) {
			return;
		}

		try {
			int triCount = model.triangleCount;
			int vertCount = triCount * 3;
			int intsNeeded = vertCount * INTS_PER_VERTEX;

			ensureVertexBufferCapacity(intsNeeded);
			vertexData.clear();

			// Pre-compute rotation
			int sinRot = 0, cosRot = 0;
			boolean hasRotation = rotation != 0 && sinTable != null;
			if (hasRotation) {
				sinRot = sinTable[rotation & 0x7FF];
				cosRot = cosTable[rotation & 0x7FF];
			}

			int uploadedTris = 0;

			for (int face = 0; face < triCount; face++) {
				// Skip hidden faces
				if (model.triangleInfo != null && model.triangleInfo[face] == -1) {
					continue;
				}
				if (model.triangleColors != null && model.triangleColors[face] == 65535) {
					continue;
				}

				int idxA = model.triangleVertexA[face];
				int idxB = model.triangleVertexB[face];
				int idxC = model.triangleVertexC[face];

				// Determine face type
				int faceType = 0;
				if (model.triangleInfo != null) {
					faceType = model.triangleInfo[face] & 3;
				}

				boolean isTextured = (faceType & 2) != 0;

				// Per-vertex HSL colors
				int hslA, hslB, hslC;
				// Texture info
				int textureId = -1;
				float uA = 0, vA = 0, uB = 0, vB = 0, uC = 0, vC = 0;

				if (isTextured && model.triangleInfo != null) {
					// Textured face
					int texTriIdx = model.triangleInfo[face] >> 2;
					textureId = (model.triangleColors != null) ? model.triangleColors[face] : -1;

					if (textureId >= 0 && textureId < Rasterizer.textureAmount
						&& model.textureTriangleA != null
						&& texTriIdx >= 0 && texTriIdx < model.textureTriangleCount) {

						// Compute UV coordinates from texture triangle
						int tA = model.textureTriangleA[texTriIdx];
						int tB = model.textureTriangleB[texTriIdx];
						int tC = model.textureTriangleC[texTriIdx];

						if (tA >= 0 && tA < model.vertexCount
							&& tB >= 0 && tB < model.vertexCount
							&& tC >= 0 && tC < model.vertexCount) {

							float[] uvs = computeUVs(model, idxA, idxB, idxC, tA, tB, tC);
							uA = uvs[0]; vA = uvs[1];
							uB = uvs[2]; vB = uvs[3];
							uC = uvs[4]; vC = uvs[5];
						}
					}

					// HSL is not used for textured faces but set to 0 as fallback
					hslA = hslB = hslC = 0;
				} else if (faceType == 1) {
					hslA = hslB = hslC = model.triangleColorA[face];
				} else {
					hslA = model.triangleColorA[face];
					hslB = model.triangleColorB[face];
					hslC = model.triangleColorC[face];
				}

				// Per-face alpha
				int alpha = 255;
				if (model.triangleAlpha != null) {
					alpha = 255 - (model.triangleAlpha[face] & 0xFF);
				}

				// Emit 3 vertices
				emitVertex(model, idxA, worldX, worldY, worldZ, sinRot, cosRot,
					hasRotation, hslA, alpha, textureId, uA, vA);
				emitVertex(model, idxB, worldX, worldY, worldZ, sinRot, cosRot,
					hasRotation, hslB, alpha, textureId, uB, vB);
				emitVertex(model, idxC, worldX, worldY, worldZ, sinRot, cosRot,
					hasRotation, hslC, alpha, textureId, uC, vC);

				uploadedTris++;
			}

			if (uploadedTris == 0) {
				return;
			}

			vertexData.flip();
			int uploadedVerts = uploadedTris * 3;

			// Upload and draw
			GL30.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STREAM_DRAW);

			shader.bind();

			// Set view-projection matrix
			shader.setUniformMatrix4fv(uViewProjection, GPUCameraSync.getViewProjectionMatrix());

			// Models are already in camera-relative coordinates, no camera offset needed
			shader.setUniform3f(uCameraPosition, 0.0f, 0.0f, 0.0f);

			// Per-frame models (players, NPCs) show on all planes
			shader.setUniform1i(uCurrentPlane, 3);

			// Lighting uniforms
			setLightingUniforms(shader);

			// Bind color palette texture to unit 0
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorPaletteTexture);
			shader.setUniform1i(uColorPalette, 0);

			// Bind texture array to unit 1
			if (GPUTextureManager.isInitialized()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, GPUTextureManager.getTextureArray());
				shader.setUniform1i(uTextureArray, 1);
			}

			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, uploadedVerts);

			shader.unbind();
			GL30.glBindVertexArray(0);

			modelsRenderedThisFrame++;
			trianglesRenderedThisFrame += uploadedTris;

		} catch (Exception e) {
			System.err.println("[GPUModelRenderer] Render error: " + e.getMessage());
		}
	}

	/**
	 * Compute UV coordinates for face vertices A, B, C using the texture triangle.
	 *
	 * The texture triangle (tA, tB, tC) defines a coordinate frame:
	 *   tA → UV (0, 0)
	 *   tB → UV (1, 0)
	 *   tC → UV (0, 1)
	 *
	 * For each face vertex, project onto this frame using least-squares.
	 *
	 * @return float[6]: {uA, vA, uB, vB, uC, vC}
	 */
	static float[] computeUVs(Model model, int faceA, int faceB, int faceC,
										int tA, int tB, int tC) {
		// Texture triangle vertex positions (model space, no rotation)
		float ax = model.verticesX[tA], ay = model.verticesY[tA], az = model.verticesZ[tA];
		float bx = model.verticesX[tB], by = model.verticesY[tB], bz = model.verticesZ[tB];
		float cx = model.verticesX[tC], cy = model.verticesY[tC], cz = model.verticesZ[tC];

		// AB and AC vectors
		float d1x = bx - ax, d1y = by - ay, d1z = bz - az;
		float d2x = cx - ax, d2y = cy - ay, d2z = cz - az;

		// Dot products for the Gram matrix
		float d1d1 = d1x * d1x + d1y * d1y + d1z * d1z;
		float d1d2 = d1x * d2x + d1y * d2y + d1z * d2z;
		float d2d2 = d2x * d2x + d2y * d2y + d2z * d2z;

		float denom = d1d1 * d2d2 - d1d2 * d1d2;
		if (Math.abs(denom) < 0.0001f) {
			// Degenerate texture triangle
			return new float[]{0, 0, 1, 0, 0, 1};
		}

		float invDenom = 1.0f / denom;

		float[] result = new float[6];

		// Project each face vertex
		int[] faceVerts = {faceA, faceB, faceC};
		for (int i = 0; i < 3; i++) {
			float px = model.verticesX[faceVerts[i]] - ax;
			float py = model.verticesY[faceVerts[i]] - ay;
			float pz = model.verticesZ[faceVerts[i]] - az;

			float d1p = d1x * px + d1y * py + d1z * pz;
			float d2p = d2x * px + d2y * py + d2z * pz;

			result[i * 2] = (d2d2 * d1p - d1d2 * d2p) * invDenom;     // u
			result[i * 2 + 1] = (d1d1 * d2p - d1d2 * d1p) * invDenom; // v
		}

		return result;
	}

	private static void emitVertex(Model model, int vertIdx,
									int worldX, int worldY, int worldZ,
									int sinRot, int cosRot, boolean hasRotation,
									int hsl, int alpha, int textureId,
									float u, float v) {
		int vx = model.verticesX[vertIdx];
		int vy = model.verticesY[vertIdx];
		int vz = model.verticesZ[vertIdx];

		// Apply model yaw rotation (XZ plane)
		if (hasRotation) {
			int rx = vz * sinRot + vx * cosRot >> 16;
			int rz = vz * cosRot - vx * sinRot >> 16;
			vx = rx;
			vz = rz;
		}

		// Add camera-relative world offset
		vx += worldX;
		vy += worldY;
		vz += worldZ;

		// Emit 8 ints: posX, posY, posZ, colorHSL, alpha, textureId, u(bits), v(bits)
		vertexData.put(vx);
		vertexData.put(vy);
		vertexData.put(vz);
		vertexData.put(hsl & 0xFFFF);
		vertexData.put(alpha);
		vertexData.put(textureId);
		vertexData.put(Float.floatToRawIntBits(u));
		vertexData.put(Float.floatToRawIntBits(v));
	}

	private static void ensureVertexBufferCapacity(int intsNeeded) {
		if (intsNeeded > vertexDataCapacity) {
			vertexDataCapacity = intsNeeded + 10000;
			vertexData = BufferUtils.createIntBuffer(vertexDataCapacity);
		}
	}

	/**
	 * Set lighting and fog uniforms on the given shader from EnvironmentConfig.
	 * Called by GPUModelRenderer.renderModel and by terrain/static renderers.
	 */
	public static void setLightingUniforms(ShaderProgram s) {
		float[] sunDir = EnvironmentConfig.getSunDirection();
		s.setUniform3f(uSunDirection, sunDir[0], sunDir[1], sunDir[2]);
		s.setUniform3f(uSunColor, EnvironmentConfig.sunColorR, EnvironmentConfig.sunColorG, EnvironmentConfig.sunColorB);
		s.setUniform1f(uSunStrength, EnvironmentConfig.sunStrength);
		s.setUniform3f(uAmbientColor, EnvironmentConfig.ambientColorR, EnvironmentConfig.ambientColorG, EnvironmentConfig.ambientColorB);
		s.setUniform1f(uAmbientStrength, EnvironmentConfig.ambientStrength);
		s.setUniform3f(uFogColor, EnvironmentConfig.fogColorR, EnvironmentConfig.fogColorG, EnvironmentConfig.fogColorB);
		s.setUniform1f(uFogStart, EnvironmentConfig.fogStart);
		s.setUniform1f(uFogEnd, EnvironmentConfig.fogEnd);
		s.setUniform1f(uTime, currentFrameTime);
		s.setUniform1i(uAnimateTextures, EnvironmentConfig.enableAnimatedTextures ? 1 : 0);
	}

	public static float getCurrentFrameTime() {
		return currentFrameTime;
	}

	public static void endFrame() {
		inFrame = false;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static boolean isInFrame() {
		return inFrame;
	}

	public static int getModelsRendered() {
		return modelsRenderedThisFrame;
	}

	public static int getTrianglesRendered() {
		return trianglesRenderedThisFrame;
	}

	public static ShaderProgram getShader() {
		return shader;
	}

	public static int getColorPaletteTexture() {
		return colorPaletteTexture;
	}

	public static void cleanup() {
		if (!initialized) {
			return;
		}

		if (shader != null) {
			shader.cleanup();
			shader = null;
		}
		if (vao != 0) {
			GL30.glDeleteVertexArrays(vao);
			vao = 0;
		}
		if (vbo != 0) {
			GL15.glDeleteBuffers(vbo);
			vbo = 0;
		}
		if (colorPaletteTexture != 0) {
			GL11.glDeleteTextures(colorPaletteTexture);
			colorPaletteTexture = 0;
		}

		initialized = false;
		paletteUploaded = false;
		System.out.println("[GPUModelRenderer] Cleaned up");
	}
}
