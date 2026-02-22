package com.bestbudz.engine.gpu.postprocess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

/**
 * Lightweight FBO + texture wrapper for post-processing passes.
 * Supports RGBA8, RGBA16F, and R8 internal formats.
 * No depth attachment -- post-process passes don't need depth testing.
 */
public class PostProcessFBO {

	public static final int FORMAT_RGBA8 = GL11.GL_RGBA8;
	public static final int FORMAT_RGBA16F = GL30.GL_RGBA16F;
	public static final int FORMAT_R8 = GL30.GL_R8;

	private int fbo;
	private int colorTexture;
	private int width;
	private int height;
	private int internalFormat;

	public PostProcessFBO(int width, int height, int internalFormat) {
		this.width = width;
		this.height = height;
		this.internalFormat = internalFormat;
		create();
	}

	private void create() {
		fbo = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);

		colorTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);

		// Determine pixel type from internal format
		int pixelType = (internalFormat == FORMAT_RGBA16F) ? GL11.GL_FLOAT : GL11.GL_UNSIGNED_BYTE;
		int pixelFormat = (internalFormat == FORMAT_R8) ? GL11.GL_RED : GL11.GL_RGBA;

		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, internalFormat, width, height, 0,
			pixelFormat, pixelType, 0);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
			GL11.GL_TEXTURE_2D, colorTexture, 0);

		if (GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER) != GL30.GL_FRAMEBUFFER_COMPLETE) {
			System.err.println("[PostProcessFBO] Framebuffer incomplete! Format: " + internalFormat);
		}

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void bind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fbo);
		GL11.glViewport(0, 0, width, height);
	}

	public void unbind() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}

	public void resize(int newWidth, int newHeight) {
		if (newWidth == width && newHeight == height) return;
		cleanup();
		width = newWidth;
		height = newHeight;
		create();
	}

	public int getColorTexture() {
		return colorTexture;
	}

	public int getFbo() {
		return fbo;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void cleanup() {
		if (fbo != 0) {
			GL30.glDeleteFramebuffers(fbo);
			fbo = 0;
		}
		if (colorTexture != 0) {
			GL11.glDeleteTextures(colorTexture);
			colorTexture = 0;
		}
	}
}
