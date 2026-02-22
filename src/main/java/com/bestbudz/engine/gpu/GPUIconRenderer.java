package com.bestbudz.engine.gpu;

import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import com.bestbudz.engine.gpu.shader.ShaderSources;
import com.bestbudz.rendering.model.Model;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * GPU icon renderer: renders models to pixel arrays via an offscreen FBO.
 * Used for item inventory sprites, interface model widgets, etc.
 *
 * Uses MODEL_VERTEX + MODEL_FRAGMENT shaders (no geometry shader, no lighting/fog).
 * Same 32-byte vertex format as GPUModelRenderer.
 */
public class GPUIconRenderer {

	private static final int INTS_PER_VERTEX = 8; // 32 bytes

	private static boolean initialized = false;
	private static ShaderProgram shader;
	private static int vao;
	private static int vbo;

	// FBO resources
	private static int fbo;
	private static int fboColorTex;
	private static int fboDepthTex;
	private static int fboWidth;
	private static int fboHeight;

	// Pixel readback buffer
	private static ByteBuffer pixelBuffer;

	// Uniform locations
	private static int uViewProjection;
	private static int uCameraPosition;
	private static int uColorPalette;
	private static int uTextureArray;
	private static int uCurrentPlane;

	// Reusable vertex buffer
	private static IntBuffer vertexData;
	private static int vertexDataCapacity;

	// Sin/cos tables
	private static int[] sinTable;
	private static int[] cosTable;

	public static boolean initialize() {
		if (initialized) return true;

		try {
			// MODEL_VERTEX + MODEL_FRAGMENT (2-arg, no geometry shader)
			shader = new ShaderProgram(ShaderSources.MODEL_VERTEX, ShaderSources.MODEL_FRAGMENT);
			if (!shader.isValid()) {
				System.err.println("[GPUIconRenderer] Shader compilation failed");
				return false;
			}

			// Create VAO/VBO with same attribute layout as GPUModelRenderer
			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();
			if (vao == 0 || vbo == 0) {
				System.err.println("[GPUIconRenderer] Failed to create GL objects");
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
			shader.unbind();

			// Create initial FBO (32x32)
			fboWidth = 32;
			fboHeight = 32;
			createFBO(fboWidth, fboHeight);

			// Pixel readback buffer
			pixelBuffer = BufferUtils.createByteBuffer(4 * 512 * 512);

			// Initial vertex buffer
			vertexDataCapacity = 3000 * INTS_PER_VERTEX;
			vertexData = BufferUtils.createIntBuffer(vertexDataCapacity);

			// Cache sin/cos tables
			sinTable = Rasterizer.sinTable;
			cosTable = Rasterizer.cosTable;

			initialized = true;
			System.out.println("[GPUIconRenderer] Initialized successfully");
			return true;

		} catch (Exception e) {
			System.err.println("[GPUIconRenderer] Init failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private static void createFBO(int width, int height) {
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

		// RGBA8 color attachment
		fboColorTex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fboColorTex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height, 0,
			GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
			GL11.GL_TEXTURE_2D, fboColorTex, 0);

		// Depth24 attachment
		fboDepthTex = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fboDepthTex);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24, width, height, 0,
			GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT,
			GL11.GL_TEXTURE_2D, fboDepthTex, 0);

		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if (status != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("[GPUIconRenderer] FBO incomplete: " + status);
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	private static void resizeFBO(int width, int height) {
		// Delete old FBO resources
		if (fbo != 0) GL30.glDeleteFramebuffers(fbo);
		if (fboColorTex != 0) GL11.glDeleteTextures(fboColorTex);
		if (fboDepthTex != 0) GL11.glDeleteTextures(fboDepthTex);

		fboWidth = width;
		fboHeight = height;
		createFBO(width, height);

		// Ensure pixel buffer is large enough
		int needed = 4 * width * height;
		if (pixelBuffer.capacity() < needed) {
			pixelBuffer = BufferUtils.createByteBuffer(needed);
		}
	}

	/**
	 * Render a model to a pixel array via GPU FBO, matching the RS2
	 * renderAtFixedPosition transform exactly.
	 *
	 * @param model       The model to render
	 * @param rotX        Yaw rotation (around Y-axis in RS2 convention)
	 * @param rotZ        Roll rotation (around Z-axis)
	 * @param rotY        Pitch rotation (final camera rotation)
	 * @param offsetX     X translation
	 * @param offsetY     Y translation
	 * @param offsetZ     Z translation
	 * @param targetPixels  The pixel array to write into (DrawingArea.pixels)
	 * @param width       Viewport width
	 * @param height      Viewport height
	 */
	public static void renderModelToPixels(
			Model model, int rotX, int rotZ, int rotY,
			int offsetX, int offsetY, int offsetZ,
			int[] targetPixels, int width, int height) {

		if (!initialized || model == null) return;
		if (model.vertexCount == 0 || model.triangleCount == 0) return;
		if (width <= 0 || height <= 0) return;

		try {
			// Resize FBO if needed
			if (width != fboWidth || height != fboHeight) {
				resizeFBO(width, height);
			}

			// Save current GL state
			int prevFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
			int[] prevViewport = new int[4];
			GL11.glGetIntegerv(GL11.GL_VIEWPORT, prevViewport);

			// Bind icon FBO
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
			GL11.glViewport(0, 0, width, height);
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			// Build projection matrix matching RS2 focal length 512
			// RS2: screenX = centerX + (vx * 512) / vz
			// OpenGL perspective: P[0] = 2*focal/width = 1024/width
			float near = 50.0f;
			float far = 5000.0f;
			float[] projection = new float[16];
			projection[0] = 1024.0f / width;
			projection[5] = -1024.0f / height; // Flip Y for OpenGL convention
			projection[10] = (far + near) / (far - near);
			projection[11] = 1.0f;
			projection[14] = -2.0f * far * near / (far - near);

			// Bind shader and set uniforms
			shader.bind();
			shader.setUniformMatrix4fv(uViewProjection, projection);
			shader.setUniform3f(uCameraPosition, 0.0f, 0.0f, 0.0f);
			shader.setUniform1i(uCurrentPlane, 3);

			// Bind color palette texture (unit 0)
			GL13.glActiveTexture(GL13.GL_TEXTURE0);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, GPUModelRenderer.getColorPaletteTexture());
			shader.setUniform1i(uColorPalette, 0);

			// Bind texture array (unit 1)
			if (GPUTextureManager.isInitialized()) {
				GL13.glActiveTexture(GL13.GL_TEXTURE1);
				GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, GPUTextureManager.getTextureArray());
				shader.setUniform1i(uTextureArray, 1);
			}

			// Pack vertices with rotation transform
			int triCount = model.triangleCount;
			int intsNeeded = triCount * 3 * INTS_PER_VERTEX;
			ensureVertexBufferCapacity(intsNeeded);
			vertexData.clear();

			// Pre-compute rotation sin/cos
			int sinX = sinTable[rotX & 0x7FF], cosX = cosTable[rotX & 0x7FF];
			int sinZ = sinTable[rotZ & 0x7FF], cosZ = cosTable[rotZ & 0x7FF];
			int sinY = sinTable[rotY & 0x7FF], cosY = cosTable[rotY & 0x7FF];

			int uploadedTris = 0;

			for (int face = 0; face < triCount; face++) {
				// Skip hidden faces
				if (model.triangleInfo != null && model.triangleInfo[face] == -1) continue;
				if (model.triangleColors != null && model.triangleColors[face] == 65535) continue;

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
				int textureId = -1;
				float uA = 0, vA = 0, uB = 0, vB = 0, uC = 0, vC = 0;

				if (isTextured && model.triangleInfo != null) {
					int texTriIdx = model.triangleInfo[face] >> 2;
					textureId = (model.triangleColors != null) ? model.triangleColors[face] : -1;

					if (textureId >= 0 && textureId < Rasterizer.textureAmount
						&& model.textureTriangleA != null
						&& texTriIdx >= 0 && texTriIdx < model.textureTriangleCount) {

						int tA = model.textureTriangleA[texTriIdx];
						int tB = model.textureTriangleB[texTriIdx];
						int tC = model.textureTriangleC[texTriIdx];

						if (tA >= 0 && tA < model.vertexCount
							&& tB >= 0 && tB < model.vertexCount
							&& tC >= 0 && tC < model.vertexCount) {

							float[] uvs = GPUModelRenderer.computeUVs(model, idxA, idxB, idxC, tA, tB, tC);
							uA = uvs[0]; vA = uvs[1];
							uB = uvs[2]; vB = uvs[3];
							uC = uvs[4]; vC = uvs[5];
						}
					}

					hslA = hslB = hslC = 0;
				} else if (faceType == 1) {
					// Flat shaded: use triangleColorA for all 3 vertices
					if (model.triangleColorA == null) continue;
					hslA = hslB = hslC = model.triangleColorA[face];
				} else {
					// Gouraud shaded
					if (model.triangleColorA == null) continue;
					hslA = model.triangleColorA[face];
					hslB = model.triangleColorB[face];
					hslC = model.triangleColorC[face];
				}

				// Per-face alpha
				int alpha = 255;
				if (model.triangleAlpha != null) {
					alpha = 255 - (model.triangleAlpha[face] & 0xFF);
				}

				// Emit 3 vertices with rotation transform
				emitIconVertex(model, idxA, sinX, cosX, sinZ, cosZ, sinY, cosY,
					offsetX, offsetY, offsetZ, hslA, alpha, textureId, uA, vA);
				emitIconVertex(model, idxB, sinX, cosX, sinZ, cosZ, sinY, cosY,
					offsetX, offsetY, offsetZ, hslB, alpha, textureId, uB, vB);
				emitIconVertex(model, idxC, sinX, cosX, sinZ, cosZ, sinY, cosY,
					offsetX, offsetY, offsetZ, hslC, alpha, textureId, uC, vC);

				uploadedTris++;
			}

			if (uploadedTris == 0) {
				// Restore GL state and return
				shader.unbind();
				GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFBO);
				GL11.glViewport(prevViewport[0], prevViewport[1], prevViewport[2], prevViewport[3]);
				return;
			}

			vertexData.flip();
			int uploadedVerts = uploadedTris * 3;

			// Upload and draw
			GL30.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STREAM_DRAW);
			GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, uploadedVerts);

			GL30.glBindVertexArray(0);
			shader.unbind();

			// Read pixels back
			pixelBuffer.clear();
			GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, pixelBuffer);

			// Convert RGBA bytes -> RGB int[] with Y-flip, write to targetPixels
			for (int row = 0; row < height; row++) {
				int srcRow = (height - 1 - row); // Y-flip: OpenGL bottom-up -> top-down
				int srcOffset = srcRow * width * 4;
				int dstOffset = row * width;

				for (int col = 0; col < width; col++) {
					int r = pixelBuffer.get(srcOffset) & 0xFF;
					int g = pixelBuffer.get(srcOffset + 1) & 0xFF;
					int b = pixelBuffer.get(srcOffset + 2) & 0xFF;
					int a = pixelBuffer.get(srcOffset + 3) & 0xFF;
					srcOffset += 4;

					if (a > 0) {
						targetPixels[dstOffset + col] = (r << 16) | (g << 8) | b;
					}
					// a == 0 means transparent -> leave targetPixels unchanged (already 0)
				}
			}

			// Restore GL state
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFBO);
			GL11.glViewport(prevViewport[0], prevViewport[1], prevViewport[2], prevViewport[3]);

		} catch (Exception e) {
			System.err.println("[GPUIconRenderer] Render error: " + e.getMessage());
		}
	}

	/**
	 * Emit a single vertex with the RS2 renderAtFixedPosition 3-axis rotation transform.
	 *
	 * Transform order (matching CPU renderAtFixedPosition exactly):
	 * 1. Roll (Z-axis) via rotZ
	 * 2. Yaw (Y-axis) via rotX param
	 * 3. Translate by offset
	 * 4. Pitch (final camera rotation) via rotY
	 */
	private static void emitIconVertex(
			Model model, int vertIdx,
			int sinX, int cosX, int sinZ, int cosZ, int sinY, int cosY,
			int offsetX, int offsetY, int offsetZ,
			int hsl, int alpha, int textureId, float u, float v) {

		int vx = model.verticesX[vertIdx];
		int vy = model.verticesY[vertIdx];
		int vz = model.verticesZ[vertIdx];

		// 1. Roll (Z-axis) if rotZ != 0
		if (sinZ != 0 || cosZ != 65536) {
			int t = vy * sinZ + vx * cosZ >> 16;
			vy = vy * cosZ - vx * sinZ >> 16;
			vx = t;
		}

		// 2. Yaw (Y-axis) if rotX != 0
		if (sinX != 0 || cosX != 65536) {
			int t = vz * sinX + vx * cosX >> 16;
			vz = vz * cosX - vx * sinX >> 16;
			vx = t;
		}

		// 3. Translate
		vx += offsetX;
		vy += offsetY;
		vz += offsetZ;

		// 4. Pitch (final camera rotation via rotY)
		if (sinY != 0 || cosY != 65536) {
			int t = vy * cosY - vz * sinY >> 16;
			vz = vy * sinY + vz * cosY >> 16;
			vy = t;
		}

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
			vertexDataCapacity = intsNeeded + 5000;
			vertexData = BufferUtils.createIntBuffer(vertexDataCapacity);
		}
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void cleanup() {
		if (!initialized) return;

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
		if (fbo != 0) {
			GL30.glDeleteFramebuffers(fbo);
			fbo = 0;
		}
		if (fboColorTex != 0) {
			GL11.glDeleteTextures(fboColorTex);
			fboColorTex = 0;
		}
		if (fboDepthTex != 0) {
			GL11.glDeleteTextures(fboDepthTex);
			fboDepthTex = 0;
		}

		initialized = false;
		System.out.println("[GPUIconRenderer] Cleaned up");
	}
}
