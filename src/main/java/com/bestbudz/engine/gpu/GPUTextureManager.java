package com.bestbudz.engine.gpu;

import com.bestbudz.engine.core.gamerender.Rasterizer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.ByteBuffer;

/**
 * Manages a GL_TEXTURE_2D_ARRAY holding all cache textures (128x128 each).
 * Textures are loaded from Rasterizer's mipmap cache on demand.
 *
 * Black pixels (RGB 0x000000) are treated as transparent (alpha=0).
 * All other pixels get alpha=255.
 */
public class GPUTextureManager {

	private static final int TEXTURE_SIZE = 128;
	private static int textureArray;
	private static int textureCount;
	private static boolean initialized = false;

	public static boolean initialize() {
		if (initialized) {
			return true;
		}

		textureCount = Rasterizer.textureAmount;
		if (textureCount <= 0) {
			System.err.println("[GPUTextureManager] No textures to load (textureAmount=" + textureCount + ")");
			return false;
		}

		try {
			textureArray = GL11.glGenTextures();
			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, textureArray);

			// Allocate storage for all layers
			GL12.glTexImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0, GL11.GL_RGBA8,
				TEXTURE_SIZE, TEXTURE_SIZE, textureCount, 0,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);

			// Upload each texture
			ByteBuffer layerBuffer = BufferUtils.createByteBuffer(TEXTURE_SIZE * TEXTURE_SIZE * 4);
			int loaded = 0;

			for (int id = 0; id < textureCount; id++) {
				if (uploadTexture(id, layerBuffer)) {
					loaded++;
				}
			}

			// Filtering
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL30.GL_TEXTURE_2D_ARRAY, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

			GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY, 0);

			initialized = true;
			System.out.println("[GPUTextureManager] Loaded " + loaded + "/" + textureCount + " textures into texture array");
			return true;

		} catch (Exception e) {
			System.err.println("[GPUTextureManager] Init failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	private static boolean uploadTexture(int textureId, ByteBuffer buffer) {
		try {
			int[][] mipmaps = Rasterizer.getMipmapTexels(textureId);
			if (mipmaps == null || mipmaps[0] == null) {
				// Upload blank transparent layer
				buffer.clear();
				for (int i = 0; i < TEXTURE_SIZE * TEXTURE_SIZE; i++) {
					buffer.put((byte) 0).put((byte) 0).put((byte) 0).put((byte) 0);
				}
				buffer.flip();
				GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0,
					0, 0, textureId, TEXTURE_SIZE, TEXTURE_SIZE, 1,
					GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
				return false;
			}

			int[] texels = mipmaps[0]; // level 0 = 128x128
			buffer.clear();

			for (int i = 0; i < TEXTURE_SIZE * TEXTURE_SIZE; i++) {
				int rgb = (i < texels.length) ? texels[i] : 0;
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = rgb & 0xFF;
				// Black (0x000000) = transparent
				int a = (rgb == 0) ? 0 : 255;
				buffer.put((byte) r).put((byte) g).put((byte) b).put((byte) a);
			}
			buffer.flip();

			GL12.glTexSubImage3D(GL30.GL_TEXTURE_2D_ARRAY, 0,
				0, 0, textureId, TEXTURE_SIZE, TEXTURE_SIZE, 1,
				GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);

			// Release the texture from the CPU cache to save memory
			Rasterizer.applyTexture(textureId);

			return true;

		} catch (Exception e) {
			System.err.println("[GPUTextureManager] Failed to upload texture " + textureId + ": " + e.getMessage());
			return false;
		}
	}

	public static int getTextureArray() {
		return textureArray;
	}

	public static int getTextureCount() {
		return textureCount;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void cleanup() {
		if (textureArray != 0) {
			GL11.glDeleteTextures(textureArray);
			textureArray = 0;
		}
		initialized = false;
		System.out.println("[GPUTextureManager] Cleaned up");
	}
}
