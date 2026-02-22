package com.bestbudz.engine.gpu.shader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;

import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class ShaderProgram {

	private int programId;
	private boolean valid;

	public ShaderProgram(String vertexSource, String fragmentSource) {
		this(vertexSource, null, fragmentSource);
	}

	public ShaderProgram(String vertexSource, String geometrySource, String fragmentSource) {
		programId = 0;
		valid = false;

		int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return;
		}

		int geometryShader = 0;
		if (geometrySource != null) {
			geometryShader = compileShader(GL32.GL_GEOMETRY_SHADER, geometrySource);
			if (geometryShader == 0) {
				GL20.glDeleteShader(vertexShader);
				return;
			}
		}

		int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);
		if (fragmentShader == 0) {
			GL20.glDeleteShader(vertexShader);
			if (geometryShader != 0) GL20.glDeleteShader(geometryShader);
			return;
		}

		programId = GL20.glCreateProgram();
		GL20.glAttachShader(programId, vertexShader);
		if (geometryShader != 0) GL20.glAttachShader(programId, geometryShader);
		GL20.glAttachShader(programId, fragmentShader);
		GL20.glLinkProgram(programId);

		if (GL20.glGetProgrami(programId, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetProgramInfoLog(programId);
			System.err.println("[ShaderProgram] Link failed: " + error);
			GL20.glDeleteProgram(programId);
			programId = 0;
			GL20.glDeleteShader(vertexShader);
			if (geometryShader != 0) GL20.glDeleteShader(geometryShader);
			GL20.glDeleteShader(fragmentShader);
			return;
		}

		GL20.glDeleteShader(vertexShader);
		if (geometryShader != 0) GL20.glDeleteShader(geometryShader);
		GL20.glDeleteShader(fragmentShader);
		valid = true;
	}

	private static int compileShader(int type, String source) {
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);

		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetShaderInfoLog(shader);
			String typeName;
			if (type == GL20.GL_VERTEX_SHADER) typeName = "vertex";
			else if (type == GL20.GL_FRAGMENT_SHADER) typeName = "fragment";
			else if (type == GL32.GL_GEOMETRY_SHADER) typeName = "geometry";
			else typeName = "unknown(" + type + ")";
			System.err.println("[ShaderProgram] " + typeName + " compile failed: " + error);
			GL20.glDeleteShader(shader);
			return 0;
		}

		return shader;
	}

	public void bind() {
		GL20.glUseProgram(programId);
	}

	public void unbind() {
		GL20.glUseProgram(0);
	}

	public int getUniformLocation(String name) {
		return GL20.glGetUniformLocation(programId, name);
	}

	public void setUniform1i(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	public void setUniform1f(int location, float value) {
		GL20.glUniform1f(location, value);
	}

	public void setUniform2f(int location, float x, float y) {
		GL20.glUniform2f(location, x, y);
	}

	public void setUniform3f(int location, float x, float y, float z) {
		GL20.glUniform3f(location, x, y, z);
	}

	public void setUniform4f(int location, float x, float y, float z, float w) {
		GL20.glUniform4f(location, x, y, z, w);
	}

	public void setUniform2i(int location, int x, int y) {
		GL20.glUniform2i(location, x, y);
	}

	public void setUniformMatrix4fv(int location, float[] matrix) {
		FloatBuffer buf = BufferUtils.createFloatBuffer(16);
		buf.put(matrix).flip();
		GL20.glUniformMatrix4fv(location, false, buf);
	}

	public boolean isValid() {
		return valid;
	}

	public int getProgramId() {
		return programId;
	}

	public void cleanup() {
		if (programId != 0) {
			GL20.glDeleteProgram(programId);
			programId = 0;
			valid = false;
		}
	}
}
