package com.bestbudz.engine.gpu.scene;

import com.bestbudz.engine.core.gamerender.Rasterizer;
import com.bestbudz.engine.core.gamerender.WorldController;
import com.bestbudz.engine.gpu.GPUCameraSync;
import com.bestbudz.engine.gpu.GPUModelRenderer;
import com.bestbudz.engine.gpu.GPUTextureManager;
import com.bestbudz.engine.gpu.postprocess.EnvironmentConfig;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import com.bestbudz.rendering.Animable;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.world.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;

/**
 * Bakes all static scene objects (walls, decorations, game objects) into a
 * single GPU VBO at region load time. Renders all static objects in one draw
 * call per frame, enabling distance rendering beyond the tile traversal range.
 *
 * Same vertex format as GPUModelRenderer/GPUSceneUploader (32 bytes/8 ints).
 * Vertices are stored in absolute world space; the shader subtracts
 * uCameraPosition to convert to camera-relative coordinates.
 */
public class GPUStaticScene {

	private static final int INTS_PER_VERTEX = 8;
	private static final int HIDDEN_COLOR = 0xbc614e;

	private static boolean initialized = false;
	private static int vao;
	private static int vbo;
	private static int vertexCount = 0;
	private static boolean uploaded = false;

	// Cached sin/cos tables
	private static int[] sinTable;
	private static int[] cosTable;

	// Stats
	private static int wallsProcessed;
	private static int wallDecorationsProcessed;
	private static int groundDecorationsProcessed;
	private static int totalTriangles;

	public static boolean initialize() {
		if (initialized) {
			return true;
		}

		try {
			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();

			if (vao == 0 || vbo == 0) {
				System.err.println("[GPUStaticScene] Failed to create GL objects");
				return false;
			}

			int stride = INTS_PER_VERTEX * 4;

			GL30.glBindVertexArray(vao);
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

			// Same attribute layout as GPUModelRenderer
			GL30.glVertexAttribIPointer(0, 4, GL11.GL_INT, stride, 0);
			GL20.glEnableVertexAttribArray(0);

			GL30.glVertexAttribIPointer(1, 1, GL11.GL_INT, stride, 16);
			GL20.glEnableVertexAttribArray(1);

			GL30.glVertexAttribIPointer(2, 1, GL11.GL_INT, stride, 20);
			GL20.glEnableVertexAttribArray(2);

			GL20.glVertexAttribPointer(3, 2, GL11.GL_FLOAT, false, stride, 24);
			GL20.glEnableVertexAttribArray(3);

			GL30.glBindVertexArray(0);

			sinTable = Rasterizer.sinTable;
			cosTable = Rasterizer.cosTable;

			initialized = true;
			System.out.println("[GPUStaticScene] Initialized successfully");
			return true;

		} catch (Exception e) {
			System.err.println("[GPUStaticScene] Init failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Walk the tile grid and bake all static objects into a VBO.
	 */
	public static void uploadStaticObjects(WorldController wc) {
		if (!initialized || wc == null) {
			return;
		}

		Ground[][][] tiles = wc.tiles;
		int mapWidth = wc.getMapWidth();
		int mapHeight = wc.getMapHeight();

		if (tiles == null) {
			return;
		}

		long startTime = System.currentTimeMillis();
		wallsProcessed = 0;
		wallDecorationsProcessed = 0;
		groundDecorationsProcessed = 0;
		totalTriangles = 0;

		// First pass: count triangles
		int triCount = countObjectTriangles(tiles, mapWidth, mapHeight);
		if (triCount == 0) {
			vertexCount = 0;
			uploaded = false;
			System.out.println("[GPUStaticScene] No static objects to upload");
			return;
		}

		int intsNeeded = triCount * 3 * INTS_PER_VERTEX;
		IntBuffer vertexData = BufferUtils.createIntBuffer(intsNeeded);

		// Second pass: emit vertices
		int planes = Math.min(4, tiles.length);
		for (int plane = 0; plane < planes; plane++) {
			if (tiles[plane] == null) continue;

			for (int tileX = 0; tileX < mapWidth; tileX++) {
				if (tiles[plane][tileX] == null) continue;

				for (int tileY = 0; tileY < mapHeight; tileY++) {
					Ground ground = tiles[plane][tileX][tileY];
					if (ground == null) continue;

					// Walls
					if (ground.obj1 != null) {
						emitWall(vertexData, ground.obj1, plane);
					}

					// Wall decorations
					if (ground.obj2 != null) {
						emitWallDecoration(vertexData, ground.obj2, plane);
					}

					// Ground decorations
					if (ground.obj3 != null) {
						emitAnimableModel(vertexData, ground.obj3.model,
							ground.obj3.anInt812, ground.obj3.anInt811,
							ground.obj3.anInt813, 0, plane);
						groundDecorationsProcessed++;
					}

					// Roof decorations
					if (ground.obj4 != null) {
						emitRoofDecoration(vertexData, ground.obj4, plane);
					}

					// Game objects (obj5Array) are NOT baked — they contain a mix of
					// static map objects and dynamic per-frame entities (players, NPCs,
					// InteractiveObjects). WorldController renders them per-frame.
				}
			}
		}

		vertexData.flip();
		vertexCount = vertexData.remaining() / INTS_PER_VERTEX;

		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		GL30.glBindVertexArray(0);

		uploaded = true;
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("[GPUStaticScene] Uploaded: " +
			wallsProcessed + " walls, " +
			wallDecorationsProcessed + " wall decs, " +
			groundDecorationsProcessed + " ground decs, " +
			totalTriangles + " tris, " +
			vertexCount + " verts (" + elapsed + "ms)");
	}

	private static int countObjectTriangles(Ground[][][] tiles, int mapWidth, int mapHeight) {
		int count = 0;
		int planes = Math.min(4, tiles.length);
		for (int plane = 0; plane < planes; plane++) {
			if (tiles[plane] == null) continue;

			for (int x = 0; x < mapWidth; x++) {
				if (tiles[plane][x] == null) continue;

				for (int y = 0; y < mapHeight; y++) {
					Ground g = tiles[plane][x][y];
					if (g == null) continue;

					if (g.obj1 != null) {
						count += countAnimableTriangles(g.obj1.primaryModel);
						count += countAnimableTriangles(g.obj1.secondaryModel);
					}
					if (g.obj2 != null) {
						count += countAnimableTriangles(g.obj2.model);
					}
					if (g.obj3 != null) {
						count += countAnimableTriangles(g.obj3.model);
					}
					if (g.obj4 != null) {
						count += countAnimableTriangles(g.obj4.aClass30_Sub2_Sub4_48);
						count += countAnimableTriangles(g.obj4.aClass30_Sub2_Sub4_49);
						count += countAnimableTriangles(g.obj4.aClass30_Sub2_Sub4_50);
					}
					// obj5Array (game objects) excluded — rendered per-frame by WorldController
				}
			}
		}

		return count;
	}

	private static int countAnimableTriangles(Animable a) {
		if (a instanceof Model) {
			Model m = (Model) a;
			return m.triangleCount;
		}
		return 0;
	}

	private static void emitWall(IntBuffer buf, Wall wall, int plane) {
		if (wall.primaryModel != null) {
			emitAnimableModel(buf, wall.primaryModel,
				wall.anInt274, wall.anInt273, wall.anInt275, 0, plane);
		}
		if (wall.secondaryModel != null) {
			emitAnimableModel(buf, wall.secondaryModel,
				wall.anInt274, wall.anInt273, wall.anInt275, 0, plane);
		}
		wallsProcessed++;
	}

	private static void emitWallDecoration(IntBuffer buf, WallDecoration wd, int plane) {
		if (wd.model != null) {
			emitAnimableModel(buf, wd.model,
				wd.anInt500, wd.anInt499, wd.anInt501, wd.anInt503, plane);
		}
		wallDecorationsProcessed++;
	}

	private static void emitRoofDecoration(IntBuffer buf, RoofDecoration roof, int plane) {
		if (roof.aClass30_Sub2_Sub4_49 != null) {
			emitAnimableModel(buf, roof.aClass30_Sub2_Sub4_49,
				roof.anInt46, roof.anInt45, roof.anInt47, 0, plane);
		}
		if (roof.aClass30_Sub2_Sub4_50 != null) {
			emitAnimableModel(buf, roof.aClass30_Sub2_Sub4_50,
				roof.anInt46, roof.anInt45, roof.anInt47, 0, plane);
		}
		if (roof.aClass30_Sub2_Sub4_48 != null) {
			emitAnimableModel(buf, roof.aClass30_Sub2_Sub4_48,
				roof.anInt46, roof.anInt45, roof.anInt47, 0, plane);
		}
	}

	/**
	 * Extract Model from Animable, transform vertices to world space, and emit.
	 */
	private static void emitAnimableModel(IntBuffer buf, Animable animable,
										  int worldX, int worldY, int worldZ,
										  int rotation, int plane) {
		if (!(animable instanceof Model)) {
			return;
		}

		Model model = (Model) animable;
		if (model.vertexCount == 0 || model.triangleCount == 0) {
			return;
		}
		if (model.triangleColorA == null) {
			return;
		}

		// Pre-compute rotation
		int sinRot = 0, cosRot = 0;
		boolean hasRotation = rotation != 0 && sinTable != null;
		if (hasRotation) {
			sinRot = sinTable[rotation & 0x7FF];
			cosRot = cosTable[rotation & 0x7FF];
		}

		for (int face = 0; face < model.triangleCount; face++) {
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

			// Bounds check
			if (idxA < 0 || idxA >= model.vertexCount ||
				idxB < 0 || idxB >= model.vertexCount ||
				idxC < 0 || idxC >= model.vertexCount) {
				continue;
			}

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

						float[] uvs = computeUVs(model, idxA, idxB, idxC, tA, tB, tC);
						uA = uvs[0]; vA = uvs[1];
						uB = uvs[2]; vB = uvs[3];
						uC = uvs[4]; vC = uvs[5];
					}
				}
				hslA = hslB = hslC = 0;
			} else if (faceType == 1) {
				hslA = hslB = hslC = model.triangleColorA[face];
			} else {
				hslA = model.triangleColorA[face];
				hslB = model.triangleColorB[face];
				hslC = model.triangleColorC[face];
			}

			int alpha = 255;
			if (model.triangleAlpha != null) {
				alpha = 255 - (model.triangleAlpha[face] & 0xFF);
			}

			emitWorldVertex(buf, model, idxA, worldX, worldY, worldZ,
				sinRot, cosRot, hasRotation, hslA, alpha, textureId, uA, vA, plane);
			emitWorldVertex(buf, model, idxB, worldX, worldY, worldZ,
				sinRot, cosRot, hasRotation, hslB, alpha, textureId, uB, vB, plane);
			emitWorldVertex(buf, model, idxC, worldX, worldY, worldZ,
				sinRot, cosRot, hasRotation, hslC, alpha, textureId, uC, vC, plane);

			totalTriangles++;
		}
	}

	/**
	 * Emit a vertex in absolute world space (model rotation + world offset).
	 */
	private static void emitWorldVertex(IntBuffer buf, Model model, int vertIdx,
										int worldX, int worldY, int worldZ,
										int sinRot, int cosRot, boolean hasRotation,
										int hsl, int alpha, int textureId,
										float u, float v, int plane) {
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

		// World-space position (absolute, not camera-relative)
		vx += worldX;
		vy += worldY;
		vz += worldZ;

		buf.put(vx);
		buf.put(vy);
		buf.put(vz);
		buf.put((plane << 16) | (hsl & 0xFFFF));
		buf.put(alpha);
		buf.put(textureId);
		buf.put(Float.floatToRawIntBits(u));
		buf.put(Float.floatToRawIntBits(v));
	}

	/**
	 * Compute UV coordinates for textured faces (same as GPUModelRenderer).
	 */
	private static float[] computeUVs(Model model, int faceA, int faceB, int faceC,
									  int tA, int tB, int tC) {
		float ax = model.verticesX[tA], ay = model.verticesY[tA], az = model.verticesZ[tA];
		float bx = model.verticesX[tB], by = model.verticesY[tB], bz = model.verticesZ[tB];
		float cx = model.verticesX[tC], cy = model.verticesY[tC], cz = model.verticesZ[tC];

		float d1x = bx - ax, d1y = by - ay, d1z = bz - az;
		float d2x = cx - ax, d2y = cy - ay, d2z = cz - az;

		float d1d1 = d1x * d1x + d1y * d1y + d1z * d1z;
		float d1d2 = d1x * d2x + d1y * d2y + d1z * d2z;
		float d2d2 = d2x * d2x + d2y * d2y + d2z * d2z;

		float denom = d1d1 * d2d2 - d1d2 * d1d2;
		if (Math.abs(denom) < 0.0001f) {
			return new float[]{0, 0, 1, 0, 0, 1};
		}

		float invDenom = 1.0f / denom;
		float[] result = new float[6];
		int[] faceVerts = {faceA, faceB, faceC};

		for (int i = 0; i < 3; i++) {
			float px = model.verticesX[faceVerts[i]] - ax;
			float py = model.verticesY[faceVerts[i]] - ay;
			float pz = model.verticesZ[faceVerts[i]] - az;

			float d1p = d1x * px + d1y * py + d1z * pz;
			float d2p = d2x * px + d2y * py + d2z * pz;

			result[i * 2] = (d2d2 * d1p - d1d2 * d2p) * invDenom;
			result[i * 2 + 1] = (d1d1 * d2p - d1d2 * d1p) * invDenom;
		}

		return result;
	}

	/**
	 * Render the baked static objects VBO.
	 */
	public static void renderStaticObjects(int cameraX, int cameraY, int cameraZ, int currentPlane) {
		if (!initialized || !uploaded || vertexCount == 0) {
			return;
		}

		ShaderProgram modelShader = GPUModelRenderer.getShader();
		if (modelShader == null || !modelShader.isValid()) {
			return;
		}

		GL30.glBindVertexArray(vao);

		modelShader.bind();

		int vpLoc = modelShader.getUniformLocation("uViewProjection");
		modelShader.setUniformMatrix4fv(vpLoc, GPUCameraSync.getViewProjectionMatrix());

		int camLoc = modelShader.getUniformLocation("uCameraPosition");
		modelShader.setUniform3f(camLoc, (float) cameraX, (float) cameraY, (float) cameraZ);

		// Set current plane for roof hiding (discard fragments above this plane)
		int planeLoc = modelShader.getUniformLocation("uCurrentPlane");
		modelShader.setUniform1i(planeLoc, currentPlane);

		// Set lighting and fog uniforms
		GPUModelRenderer.setLightingUniforms(modelShader);

		int palLoc = modelShader.getUniformLocation("uColorPalette");
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GPUModelRenderer.getColorPaletteTexture());
		modelShader.setUniform1i(palLoc, 0);

		if (GPUTextureManager.isInitialized()) {
			int texLoc = modelShader.getUniformLocation("uTextureArray");
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, GPUTextureManager.getTextureArray());
			modelShader.setUniform1i(texLoc, 1);
		}

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);

		modelShader.unbind();
		GL30.glBindVertexArray(0);
	}

	public static boolean isUploaded() {
		return uploaded;
	}

	public static int getVertexCount() {
		return vertexCount;
	}

	public static void invalidate() {
		uploaded = false;
		vertexCount = 0;
	}

	public static void cleanup() {
		if (vao != 0) {
			GL30.glDeleteVertexArrays(vao);
			vao = 0;
		}
		if (vbo != 0) {
			GL15.glDeleteBuffers(vbo);
			vbo = 0;
		}
		uploaded = false;
		vertexCount = 0;
		initialized = false;
		System.out.println("[GPUStaticScene] Cleaned up");
	}
}
