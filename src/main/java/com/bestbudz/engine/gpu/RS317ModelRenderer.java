package com.bestbudz.engine.gpu;

import com.bestbudz.rendering.model.Model;
import org.lwjgl.opengl.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

public class RS317ModelRenderer {

	private static boolean initialized = false;
	private static int shaderProgram;
	private static int vao;
	private static int vbo;
	private static int ebo;
	private static int mvpUniform;
	private static int colorUniform;

	private static float cameraX = 0.0f;
	private static float cameraY = 0.0f;
	private static float cameraZ = 5.0f;
	private static float viewDistance = 10000.0f;

	public static boolean initialize() {
		if (initialized) {
			return true;
		}

		System.out.println("[RS317 Renderer] Initializing fixed renderer...");

		try {

			String vertexShader =
				"#version 330 core\n" +
					"layout (location = 0) in vec3 aPos;\n" +
					"uniform mat4 uMVP;\n" +
					"void main() {\n" +
					"    gl_Position = uMVP * vec4(aPos, 1.0);\n" +
					"}\n";

			String fragmentShader =
				"#version 330 core\n" +
					"out vec4 FragColor;\n" +
					"uniform vec3 uColor;\n" +
					"void main() {\n" +
					"    FragColor = vec4(uColor, 1.0);\n" +
					"}\n";

			shaderProgram = createShaderProgram(vertexShader, fragmentShader);
			if (shaderProgram == 0) {
				System.err.println("[RS317 Renderer] Failed to getPooledStream shader program");
				return false;
			}

			mvpUniform = GL20.glGetUniformLocation(shaderProgram, "uMVP");
			colorUniform = GL20.glGetUniformLocation(shaderProgram, "uColor");

			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();
			ebo = GL15.glGenBuffers();

			if (vao == 0 || vbo == 0 || ebo == 0) {
				System.err.println("[RS317 Renderer] Failed to getPooledStream OpenGL objects");
				return false;
			}

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			GL11.glFrontFace(GL11.GL_CCW);

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

			initialized = true;
			System.out.println("[RS317 Renderer] ✅ Fixed renderer initialized successfully");
			return true;

		} catch (Exception e) {
			System.err.println("[RS317 Renderer] ❌ Initialization failed: " + e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public static void renderModel(Model model, int worldX, int worldY, int worldZ,
								   int rotX, int rotY, int rotZ, int scale) {
		if (!initialized || model == null) {
			return;
		}

		if (model.vertexCount == 0 || model.triangleCount == 0) {
			return;
		}

		try {

			float[] vertices = convertVertices(model, scale);
			int[] indices = convertIndices(model);

			if (vertices == null || indices == null) {
				return;
			}

			GL30.glBindVertexArray(vao);

			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
			vertexBuffer.put(vertices).flip();
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_DYNAMIC_DRAW);

			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
			IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
			indexBuffer.put(indices).flip();
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_DYNAMIC_DRAW);

			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12, 0);
			GL20.glEnableVertexAttribArray(0);

			GL20.glUseProgram(shaderProgram);

			float[] mvpMatrix = createMVPMatrix(worldX, worldY, worldZ, rotX, rotY, rotZ);
			FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
			matrixBuffer.put(mvpMatrix).flip();
			GL20.glUniformMatrix4fv(mvpUniform, false, matrixBuffer);

			GL20.glUniform3f(colorUniform, 1.0f, 1.0f, 1.0f);

			GL11.glDrawElements(GL11.GL_TRIANGLES, indices.length, GL11.GL_UNSIGNED_INT, 0);

			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			GL20.glUseProgram(0);

		} catch (Exception e) {
			System.err.println("[RS317 Renderer] Error rendering model: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static float[] convertVertices(Model model, int scale) {
		try {
			float[] vertices = new float[model.vertexCount * 3];
			float scaleFactor = scale / 128.0f;

			for (int i = 0; i < model.vertexCount; i++) {

				vertices[i * 3 + 0] = (model.verticesX[i] * scaleFactor) / 128.0f;
				vertices[i * 3 + 1] = -(model.verticesY[i] * scaleFactor) / 128.0f;
				vertices[i * 3 + 2] = -(model.verticesZ[i] * scaleFactor) / 128.0f;
			}

			return vertices;
		} catch (Exception e) {
			System.err.println("[RS317 Renderer] Error converting vertices: " + e.getMessage());
			return null;
		}
	}

	private static int[] convertIndices(Model model) {
		try {
			int[] indices = new int[model.triangleCount * 3];

			for (int i = 0; i < model.triangleCount; i++) {

				indices[i * 3 + 0] = model.triangleVertexA[i];
				indices[i * 3 + 1] = model.triangleVertexC[i];
				indices[i * 3 + 2] = model.triangleVertexB[i];
			}

			return indices;
		} catch (Exception e) {
			System.err.println("[RS317 Renderer] Error converting indices: " + e.getMessage());
			return null;
		}
	}

	private static float[] createMVPMatrix(int worldX, int worldY, int worldZ,
										   int rotX, int rotY, int rotZ) {

		float[] matrix = new float[16];
		setIdentityMatrix(matrix);

		float[] projection = createProjectionMatrix();

		float[] view = createViewMatrix();

		float[] model = createModelMatrix(worldX, worldY, worldZ, rotX, rotY, rotZ);

		float[] vp = multiplyMatrices(projection, view);
		float[] mvp = multiplyMatrices(vp, model);

		return mvp;
	}

	private static float[] createProjectionMatrix() {
		float[] matrix = new float[16];

		float fov = (float) Math.toRadians(45.0f);
		float aspect = 4.0f / 3.0f;
		float near = 1.0f;
		float far = viewDistance;

		float f = (float) (1.0f / Math.tan(fov / 2.0f));

		matrix[0] = f / aspect;
		matrix[5] = f;
		matrix[10] = (far + near) / (near - far);
		matrix[11] = -1.0f;
		matrix[14] = (2.0f * far * near) / (near - far);

		return matrix;
	}

	private static float[] createViewMatrix() {
		float[] matrix = new float[16];
		setIdentityMatrix(matrix);

		matrix[12] = -cameraX;
		matrix[13] = -cameraY;
		matrix[14] = -cameraZ;

		return matrix;
	}

	private static float[] createModelMatrix(int worldX, int worldY, int worldZ,
											 int rotX, int rotY, int rotZ) {
		float[] matrix = new float[16];
		setIdentityMatrix(matrix);

		float x = worldX / 1000.0f;
		float y = -worldY / 1000.0f;
		float z = -worldZ / 1000.0f;

		matrix[12] = x;
		matrix[13] = y;
		matrix[14] = z;

		return matrix;
	}

	private static void setIdentityMatrix(float[] matrix) {
		for (int i = 0; i < 16; i++) {
			matrix[i] = 0.0f;
		}
		matrix[0] = matrix[5] = matrix[10] = matrix[15] = 1.0f;
	}

	private static float[] multiplyMatrices(float[] a, float[] b) {
		float[] result = new float[16];

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					result[i * 4 + j] += a[i * 4 + k] * b[k * 4 + j];
				}
			}
		}

		return result;
	}

	public static void setCameraPosition(float x, float y, float z) {
		cameraX = x;
		cameraY = y;
		cameraZ = z;
	}

	public static void setViewDistance(float distance) {
		viewDistance = distance;
	}

	private static int createShaderProgram(String vertexSource, String fragmentSource) {
		int vertexShader = compileShader(GL20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) return 0;

		int fragmentShader = compileShader(GL20.GL_FRAGMENT_SHADER, fragmentSource);
		if (fragmentShader == 0) {
			GL20.glDeleteShader(vertexShader);
			return 0;
		}

		int program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vertexShader);
		GL20.glAttachShader(program, fragmentShader);
		GL20.glLinkProgram(program);

		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetProgramInfoLog(program);
			System.err.println("[RS317 Renderer] Shader linking failed: " + error);
			GL20.glDeleteProgram(program);
			GL20.glDeleteShader(vertexShader);
			GL20.glDeleteShader(fragmentShader);
			return 0;
		}

		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(fragmentShader);

		return program;
	}

	private static int compileShader(int type, String source) {
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);

		if (GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetShaderInfoLog(shader);
			System.err.println("[RS317 Renderer] Shader compilation failed: " + error);
			GL20.glDeleteShader(shader);
			return 0;
		}

		return shader;
	}

	public static boolean isInitialized() {
		return initialized;
	}

	public static void cleanup() {
		if (initialized) {
			if (vao != 0) GL30.glDeleteVertexArrays(vao);
			if (vbo != 0) GL15.glDeleteBuffers(vbo);
			if (ebo != 0) GL15.glDeleteBuffers(ebo);
			if (shaderProgram != 0) GL20.glDeleteProgram(shaderProgram);

			vao = vbo = ebo = shaderProgram = 0;
			initialized = false;

			System.out.println("[RS317 Renderer] Cleanup completed");
		}
	}
}
