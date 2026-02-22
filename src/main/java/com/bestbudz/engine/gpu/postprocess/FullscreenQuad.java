package com.bestbudz.engine.gpu.postprocess;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;

/**
 * Static fullscreen quad (triangle strip) for post-processing passes.
 * Covers NDC [-1,1] with UV [0,1]. Call init() once, then draw() per pass.
 */
public class FullscreenQuad {

	private static int vao;
	private static int vbo;
	private static boolean initialized = false;

	// 4 vertices: position (x,y) + texcoord (u,v) as triangle strip
	private static final float[] QUAD_DATA = {
		// x,    y,   u,   v
		-1.0f, -1.0f, 0.0f, 0.0f,  // bottom-left
		 1.0f, -1.0f, 1.0f, 0.0f,  // bottom-right
		-1.0f,  1.0f, 0.0f, 1.0f,  // top-left
		 1.0f,  1.0f, 1.0f, 1.0f,  // top-right
	};

	public static void init() {
		if (initialized) return;

		vao = GL30.glGenVertexArrays();
		vbo = GL15.glGenBuffers();

		GL30.glBindVertexArray(vao);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

		FloatBuffer buf = BufferUtils.createFloatBuffer(QUAD_DATA.length);
		buf.put(QUAD_DATA).flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buf, GL15.GL_STATIC_DRAW);

		// Attribute 0: vec2 position
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 16, 0);
		GL20.glEnableVertexAttribArray(0);

		// Attribute 1: vec2 texcoord
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 16, 8);
		GL20.glEnableVertexAttribArray(1);

		GL30.glBindVertexArray(0);

		initialized = true;
	}

	public static void draw() {
		if (!initialized) return;
		GL30.glBindVertexArray(vao);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		GL30.glBindVertexArray(0);
	}

	public static void cleanup() {
		if (!initialized) return;
		GL30.glDeleteVertexArrays(vao);
		GL15.glDeleteBuffers(vbo);
		vao = 0;
		vbo = 0;
		initialized = false;
	}
}
