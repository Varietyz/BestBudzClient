package com.bestbudz.engine.gpu.scene;

import com.bestbudz.engine.core.gamerender.WorldController;
import com.bestbudz.engine.gpu.GPUCameraSync;
import com.bestbudz.engine.gpu.GPUModelRenderer;
import com.bestbudz.engine.gpu.GPUTextureManager;
import com.bestbudz.engine.gpu.shader.ShaderProgram;
import com.bestbudz.rendering.animation.FloorDecoration;
import com.bestbudz.rendering.model.SimpleTile;
import com.bestbudz.world.Ground;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;

/**
 * Uploads terrain geometry (SimpleTile + FloorDecoration) into a GPU VBO
 * at region load time. Renders all terrain in 1-2 draw calls per frame
 * instead of per-tile CPU rasterizer calls.
 *
 * Terrain vertices are stored in absolute world space. The vertex shader
 * subtracts uCameraPosition to convert to camera-relative coordinates
 * before applying the view-projection matrix.
 *
 * Uses the same vertex format as GPUModelRenderer (32 bytes / 8 ints):
 *   ivec4 positionAndColor (xyz = world position, w = HSL index)
 *   int   alpha (255 = opaque)
 *   int   textureId (-1 = untextured)
 *   vec2  texCoord (UV in 0-1 range)
 */
public class GPUSceneUploader {

	private static final int INTS_PER_VERTEX = 8;
	private static final int HIDDEN_COLOR = 0xbc614e;

	private static boolean initialized = false;
	private static int vao;
	private static int vbo;
	private static int terrainVertexCount = 0;
	private static boolean terrainUploaded = false;

	// Uniform locations (from our own shader instance)
	private static ShaderProgram shader;
	private static int uViewProjection;
	private static int uCameraPosition;
	private static int uColorPalette;
	private static int uTextureArray;

	// Stats
	private static int simpleTilesUploaded;
	private static int complexTilesUploaded;
	private static int totalTriangles;

	public static boolean initialize() {
		if (initialized) {
			return true;
		}

		try {
			// Create our own VAO/VBO (shares same vertex format as GPUModelRenderer)
			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();

			if (vao == 0 || vbo == 0) {
				System.err.println("[GPUSceneUploader] Failed to create GL objects");
				return false;
			}

			int stride = INTS_PER_VERTEX * 4; // 32 bytes

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

			initialized = true;
			System.out.println("[GPUSceneUploader] Initialized successfully");
			return true;

		} catch (Exception e) {
			System.err.println("[GPUSceneUploader] Init failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Upload all terrain geometry from the WorldController's tile grid.
	 * Called once after region load (resetGameState completes).
	 */
	public static void uploadTerrain(WorldController wc) {
		if (!initialized || wc == null) {
			return;
		}

		Ground[][][] tiles = wc.tiles;
		int[][][] heightMap = wc.getHeightMap();
		int mapWidth = wc.getMapWidth();
		int mapHeight = wc.getMapHeight();

		if (tiles == null || heightMap == null) {
			System.err.println("[GPUSceneUploader] tiles or heightMap is null");
			return;
		}

		long startTime = System.currentTimeMillis();
		simpleTilesUploaded = 0;
		complexTilesUploaded = 0;
		totalTriangles = 0;

		// First pass: count triangles to size the buffer
		int triCount = countTerrainTriangles(tiles, mapWidth, mapHeight);
		if (triCount == 0) {
			terrainVertexCount = 0;
			terrainUploaded = false;
			System.out.println("[GPUSceneUploader] No terrain triangles to upload");
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

					// Bridge tiles (lower plane)
					if (ground.bridgeTile != null) {
						Ground lower = ground.bridgeTile;
						if (lower.simpleTile != null) {
							emitSimpleTile(vertexData, lower.simpleTile,
								heightMap, 0, tileX, tileY);
						} else if (lower.floorDecoration != null) {
							emitFloorDecoration(vertexData, lower.floorDecoration);
						}
					}

					// Main tile
					if (ground.simpleTile != null) {
						emitSimpleTile(vertexData, ground.simpleTile,
							heightMap, plane, tileX, tileY);
					} else if (ground.floorDecoration != null) {
						emitFloorDecoration(vertexData, ground.floorDecoration);
					}
				}
			}
		}

		vertexData.flip();
		terrainVertexCount = vertexData.remaining() / INTS_PER_VERTEX;

		// Upload to GPU
		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STATIC_DRAW);
		GL30.glBindVertexArray(0);

		terrainUploaded = true;
		long elapsed = System.currentTimeMillis() - startTime;
		System.out.println("[GPUSceneUploader] Uploaded terrain: " +
			simpleTilesUploaded + " simple tiles, " +
			complexTilesUploaded + " complex tiles, " +
			totalTriangles + " triangles, " +
			terrainVertexCount + " vertices (" + elapsed + "ms)");
	}

	private static int countTerrainTriangles(Ground[][][] tiles, int mapWidth, int mapHeight) {
		int count = 0;
		int planes = Math.min(4, tiles.length);

		for (int plane = 0; plane < planes; plane++) {
			if (tiles[plane] == null) continue;

			for (int x = 0; x < mapWidth; x++) {
				if (tiles[plane][x] == null) continue;

				for (int y = 0; y < mapHeight; y++) {
					Ground g = tiles[plane][x][y];
					if (g == null) continue;

					if (g.bridgeTile != null) {
						Ground lower = g.bridgeTile;
						if (lower.simpleTile != null) {
							count += 2; // 2 triangles per simple tile
						} else if (lower.floorDecoration != null && lower.floorDecoration.anIntArray679 != null) {
							count += lower.floorDecoration.anIntArray679.length;
						}
					}

					if (g.simpleTile != null) {
						count += 2;
					} else if (g.floorDecoration != null && g.floorDecoration.anIntArray679 != null) {
						count += g.floorDecoration.anIntArray679.length;
					}
				}
			}
		}

		return count;
	}

	/**
	 * Emit 2 triangles (6 vertices) for a SimpleTile.
	 *
	 * Corner layout (looking down, +X right, +Z down):
	 *   SW (tileX, tileY)     SE (tileX+1, tileY)
	 *   NW (tileX, tileY+1)   NE (tileX+1, tileY+1)
	 *
	 * Triangle 1: NE, NW, SE  (colors: anInt718, anInt719, anInt717)
	 * Triangle 2: SW, SE, NW  (colors: anInt716, anInt717, anInt719)
	 */
	private static void emitSimpleTile(IntBuffer buf, SimpleTile tile,
									   int[][][] heightMap, int plane, int tileX, int tileY) {
		// World positions of 4 corners
		int swX = tileX * 128;
		int swZ = tileY * 128;
		int seX = swX + 128;
		int seZ = swZ;
		int neX = swX + 128;
		int neZ = swZ + 128;
		int nwX = swX;
		int nwZ = swZ + 128;

		// Heights from heightMap
		int swY, seY, neY, nwY;
		try {
			swY = heightMap[plane][tileX][tileY];
			seY = heightMap[plane][tileX + 1][tileY];
			neY = heightMap[plane][tileX + 1][tileY + 1];
			nwY = heightMap[plane][tileX][tileY + 1];
		} catch (ArrayIndexOutOfBoundsException e) {
			return; // Skip tiles at map edges
		}

		// Texture info
		int textureId = -1;
		if (tile.anInt720 >= 0 && tile.anInt720 <= 50) {
			textureId = tile.anInt720;
		}

		int alpha = 255;

		// Triangle 1: NE, NW, SE
		if (tile.anInt718 != HIDDEN_COLOR) {
			emitVertex(buf, neX, neY, neZ, tile.anInt718, alpha, textureId,
				textureId >= 0 ? 1.0f : 0, textureId >= 0 ? 1.0f : 0);
			emitVertex(buf, nwX, nwY, nwZ, tile.anInt719, alpha, textureId,
				textureId >= 0 ? 0.0f : 0, textureId >= 0 ? 1.0f : 0);
			emitVertex(buf, seX, seY, seZ, tile.anInt717, alpha, textureId,
				textureId >= 0 ? 1.0f : 0, textureId >= 0 ? 0.0f : 0);
			totalTriangles++;
		}

		// Triangle 2: SW, SE, NW
		if (tile.anInt716 != HIDDEN_COLOR) {
			emitVertex(buf, swX, swY, swZ, tile.anInt716, alpha, textureId,
				textureId >= 0 ? 0.0f : 0, textureId >= 0 ? 0.0f : 0);
			emitVertex(buf, seX, seY, seZ, tile.anInt717, alpha, textureId,
				textureId >= 0 ? 1.0f : 0, textureId >= 0 ? 0.0f : 0);
			emitVertex(buf, nwX, nwY, nwZ, tile.anInt719, alpha, textureId,
				textureId >= 0 ? 0.0f : 0, textureId >= 0 ? 1.0f : 0);
			totalTriangles++;
		}

		simpleTilesUploaded++;
	}

	/**
	 * Emit triangles for a FloorDecoration (complex/shaped tile).
	 * Vertices are already in world-space coordinates from the constructor.
	 */
	private static void emitFloorDecoration(IntBuffer buf, FloorDecoration fd) {
		if (fd.anIntArray679 == null) return;

		int triCount = fd.anIntArray679.length;
		for (int i = 0; i < triCount; i++) {
			int idxA = fd.anIntArray679[i];
			int idxB = fd.anIntArray680[i];
			int idxC = fd.anIntArray681[i];

			// Bounds check
			if (idxA >= fd.anIntArray673.length || idxB >= fd.anIntArray673.length
				|| idxC >= fd.anIntArray673.length) {
				continue;
			}

			int colorA = fd.anIntArray676[i];
			int colorB = fd.anIntArray677[i];
			int colorC = fd.anIntArray678[i];

			// Skip hidden triangles
			if (colorA == HIDDEN_COLOR) continue;

			// Texture info
			int textureId = -1;
			if (fd.anIntArray682 != null && fd.anIntArray682[i] >= 0
				&& fd.anIntArray682[i] <= 50) {
				textureId = fd.anIntArray682[i];
			}

			int alpha = 255;

			// UV coords for complex tiles: derive from vertex position within tile
			float uA = 0, vA = 0, uB = 0, vB = 0, uC = 0, vC = 0;
			if (textureId >= 0) {
				uA = (fd.anIntArray673[idxA] % 128) / 128.0f;
				vA = (fd.anIntArray675[idxA] % 128) / 128.0f;
				uB = (fd.anIntArray673[idxB] % 128) / 128.0f;
				vB = (fd.anIntArray675[idxB] % 128) / 128.0f;
				uC = (fd.anIntArray673[idxC] % 128) / 128.0f;
				vC = (fd.anIntArray675[idxC] % 128) / 128.0f;
			}

			emitVertex(buf,
				fd.anIntArray673[idxA], fd.anIntArray674[idxA], fd.anIntArray675[idxA],
				colorA, alpha, textureId, uA, vA);
			emitVertex(buf,
				fd.anIntArray673[idxB], fd.anIntArray674[idxB], fd.anIntArray675[idxB],
				colorB, alpha, textureId, uB, vB);
			emitVertex(buf,
				fd.anIntArray673[idxC], fd.anIntArray674[idxC], fd.anIntArray675[idxC],
				colorC, alpha, textureId, uC, vC);

			totalTriangles++;
		}

		complexTilesUploaded++;
	}

	private static void emitVertex(IntBuffer buf, int x, int y, int z,
									int hsl, int alpha, int textureId,
									float u, float v) {
		buf.put(x);
		buf.put(y);
		buf.put(z);
		buf.put(hsl & 0xFFFF);
		buf.put(alpha);
		buf.put(textureId);
		buf.put(Float.floatToRawIntBits(u));
		buf.put(Float.floatToRawIntBits(v));
	}

	/**
	 * Render the uploaded terrain geometry.
	 * Must be called within a GPU context with FBO bound.
	 *
	 * @param cameraX RS2 xCameraPos (horizontal X)
	 * @param cameraY RS2 zCameraPos (height)
	 * @param cameraZ RS2 yCameraPos (horizontal Z)
	 */
	public static void renderTerrain(int cameraX, int cameraY, int cameraZ) {
		if (!initialized || !terrainUploaded || terrainVertexCount == 0) {
			return;
		}

		ShaderProgram modelShader = GPUModelRenderer.getShader();
		if (modelShader == null || !modelShader.isValid()) {
			return;
		}

		GL30.glBindVertexArray(vao);

		modelShader.bind();

		// Set view-projection matrix
		int vpLoc = modelShader.getUniformLocation("uViewProjection");
		modelShader.setUniformMatrix4fv(vpLoc, GPUCameraSync.getViewProjectionMatrix());

		// Set camera position: terrain is in world space, shader subtracts this
		int camLoc = modelShader.getUniformLocation("uCameraPosition");
		modelShader.setUniform3f(camLoc, (float) cameraX, (float) cameraY, (float) cameraZ);

		// Bind color palette texture to unit 0
		int palLoc = modelShader.getUniformLocation("uColorPalette");
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GPUModelRenderer.getColorPaletteTexture());
		modelShader.setUniform1i(palLoc, 0);

		// Bind texture array to unit 1
		if (GPUTextureManager.isInitialized()) {
			int texLoc = modelShader.getUniformLocation("uTextureArray");
			GL13.glActiveTexture(GL13.GL_TEXTURE1);
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, GPUTextureManager.getTextureArray());
			modelShader.setUniform1i(texLoc, 1);
		}

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, terrainVertexCount);

		modelShader.unbind();
		GL30.glBindVertexArray(0);
	}

	public static boolean isTerrainUploaded() {
		return terrainUploaded;
	}

	public static int getTerrainVertexCount() {
		return terrainVertexCount;
	}

	public static void invalidateTerrain() {
		terrainUploaded = false;
		terrainVertexCount = 0;
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
		terrainUploaded = false;
		terrainVertexCount = 0;
		initialized = false;
		System.out.println("[GPUSceneUploader] Cleaned up");
	}
}
