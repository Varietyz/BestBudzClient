package com.bestbudz.engine.gpu;

import com.bestbudz.rendering.model.Model;
import org.lwjgl.opengl.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;

/**
 * Fixed RS317 Model Renderer - Properly handles RS317 coordinate system
 */
public class RS317ModelRenderer {

	private static boolean initialized = false;
	private static int shaderProgram;
	private static int vao;
	private static int vbo;
	private static int ebo;
	private static int mvpUniform;
	private static int colorUniform;

	// Camera settings - adjust these to match your game's camera
	private static float cameraX = 0.0f;
	private static float cameraY = 0.0f;
	private static float cameraZ = 5.0f;
	private static float viewDistance = 10000.0f;

	/**
	 * Initialize with proper OpenGL setup for RS317 models
	 */
	public static boolean initialize() {
		if (initialized) {
			return true;
		}

		System.out.println("[RS317 Renderer] Initializing fixed renderer...");

		try {
			// Improved shaders with color support
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

			// Create shader program
			shaderProgram = createShaderProgram(vertexShader, fragmentShader);
			if (shaderProgram == 0) {
				System.err.println("[RS317 Renderer] Failed to getPooledStream shader program");
				return false;
			}

			// Get uniform locations
			mvpUniform = GL20.glGetUniformLocation(shaderProgram, "uMVP");
			colorUniform = GL20.glGetUniformLocation(shaderProgram, "uColor");

			// Create OpenGL objects
			vao = GL30.glGenVertexArrays();
			vbo = GL15.glGenBuffers();
			ebo = GL15.glGenBuffers();

			if (vao == 0 || vbo == 0 || ebo == 0) {
				System.err.println("[RS317 Renderer] Failed to getPooledStream OpenGL objects");
				return false;
			}

			// Proper OpenGL state for 3D rendering
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthFunc(GL11.GL_LEQUAL);  // Use LEQUAL for better depth handling
			GL11.glEnable(GL11.GL_CULL_FACE);
			GL11.glCullFace(GL11.GL_BACK);
			GL11.glFrontFace(GL11.GL_CCW);

			// Enable blending for transparency
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

	/**
	 * Main rendering method - fixed for RS317 coordinate system
	 */
	public static void renderModel(Model model, int worldX, int worldY, int worldZ,
								   int rotX, int rotY, int rotZ, int scale) {
		if (!initialized || model == null) {
			return;
		}

		// Basic validation
		if (model.vertexCount == 0 || model.triangleCount == 0) {
			return;
		}

		try {
			// Convert RS317 model to OpenGL vertex data
			float[] vertices = convertVertices(model, scale);
			int[] indices = convertIndices(model);

			if (vertices == null || indices == null) {
				return;
			}

			// Bind VAO
			GL30.glBindVertexArray(vao);

			// Upload vertices to VBO
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
			FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
			vertexBuffer.put(vertices).flip();
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexBuffer, GL15.GL_DYNAMIC_DRAW);

			// Upload indices to EBO
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, ebo);
			IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.length);
			indexBuffer.put(indices).flip();
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL15.GL_DYNAMIC_DRAW);

			// Setup vertex attributes (position only for now)
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 12, 0);
			GL20.glEnableVertexAttribArray(0);

			// Use shader program
			GL20.glUseProgram(shaderProgram);

			// Create and set MVP matrix
			float[] mvpMatrix = createMVPMatrix(worldX, worldY, worldZ, rotX, rotY, rotZ);
			FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
			matrixBuffer.put(mvpMatrix).flip();
			GL20.glUniformMatrix4fv(mvpUniform, false, matrixBuffer);

			// Set model color (white for now, can be improved later)
			GL20.glUniform3f(colorUniform, 1.0f, 1.0f, 1.0f);

			// Render the model
			GL11.glDrawElements(GL11.GL_TRIANGLES, indices.length, GL11.GL_UNSIGNED_INT, 0);

			// Cleanup
			GL20.glDisableVertexAttribArray(0);
			GL30.glBindVertexArray(0);
			GL20.glUseProgram(0);

		} catch (Exception e) {
			System.err.println("[RS317 Renderer] Error rendering model: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Convert RS317 vertices to OpenGL format with proper scaling
	 */
	private static float[] convertVertices(Model model, int scale) {
		try {
			float[] vertices = new float[model.vertexCount * 3];
			float scaleFactor = scale / 128.0f; // RS317 uses 128 as base scale

			for (int i = 0; i < model.vertexCount; i++) {
				// Convert RS317 coordinates to OpenGL with proper scaling
				// RS317: +X = East, +Y = Up, +Z = North
				// OpenGL: +X = Right, +Y = Up, +Z = Towards viewer
				vertices[i * 3 + 0] = (model.verticesX[i] * scaleFactor) / 128.0f; // X
				vertices[i * 3 + 1] = -(model.verticesY[i] * scaleFactor) / 128.0f; // Y (flip for OpenGL)
				vertices[i * 3 + 2] = -(model.verticesZ[i] * scaleFactor) / 128.0f; // Z (flip for OpenGL)
			}

			return vertices;
		} catch (Exception e) {
			System.err.println("[RS317 Renderer] Error converting vertices: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Convert RS317 triangle indices to OpenGL format
	 */
	private static int[] convertIndices(Model model) {
		try {
			int[] indices = new int[model.triangleCount * 3];

			for (int i = 0; i < model.triangleCount; i++) {
				// RS317 uses clockwise winding, OpenGL expects counter-clockwise
				// So we flip the triangle winding order
				indices[i * 3 + 0] = model.triangleVertexA[i]; // Vertex 1
				indices[i * 3 + 1] = model.triangleVertexC[i]; // Vertex 3 (swapped)
				indices[i * 3 + 2] = model.triangleVertexB[i]; // Vertex 2 (swapped)
			}

			return indices;
		} catch (Exception e) {
			System.err.println("[RS317 Renderer] Error converting indices: " + e.getMessage());
			return null;
		}
	}

	/**
	 * Create proper MVP matrix for RS317 world coordinates
	 */
	private static float[] createMVPMatrix(int worldX, int worldY, int worldZ,
										   int rotX, int rotY, int rotZ) {
		// Create identity matrix
		float[] matrix = new float[16];
		setIdentityMatrix(matrix);

		// Create projection matrix (perspective)
		float[] projection = createProjectionMatrix();

		// Create view matrix (camera)
		float[] view = createViewMatrix();

		// Create model matrix (world transform)
		float[] model = createModelMatrix(worldX, worldY, worldZ, rotX, rotY, rotZ);

		// Combine: MVP = Projection * View * Model
		float[] vp = multiplyMatrices(projection, view);
		float[] mvp = multiplyMatrices(vp, model);

		return mvp;
	}

	/**
	 * Create perspective projection matrix
	 */
	private static float[] createProjectionMatrix() {
		float[] matrix = new float[16];

		float fov = (float) Math.toRadians(45.0f); // 45 degree field of view
		float aspect = 4.0f / 3.0f; // Assume 4:3 aspect ratio for now
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

	/**
	 * Create view matrix (camera)
	 */
	private static float[] createViewMatrix() {
		float[] matrix = new float[16];
		setIdentityMatrix(matrix);

		// Simple translation for camera position
		matrix[12] = -cameraX;
		matrix[13] = -cameraY;
		matrix[14] = -cameraZ;

		return matrix;
	}

	/**
	 * Create model matrix (world transform)
	 */
	private static float[] createModelMatrix(int worldX, int worldY, int worldZ,
											 int rotX, int rotY, int rotZ) {
		float[] matrix = new float[16];
		setIdentityMatrix(matrix);

		// Convert RS317 world coordinates to OpenGL coordinates
		// Scale down the world coordinates significantly
		float x = worldX / 1000.0f;  // Reduced scaling
		float y = -worldY / 1000.0f; // Flip Y for OpenGL
		float z = -worldZ / 1000.0f; // Flip Z for OpenGL

		// Apply translation
		matrix[12] = x;
		matrix[13] = y;
		matrix[14] = z;

		// TODO: Add rotation transformations if needed
		// For now, just translation to see if models appear

		return matrix;
	}

	/**
	 * Set matrix to identity
	 */
	private static void setIdentityMatrix(float[] matrix) {
		for (int i = 0; i < 16; i++) {
			matrix[i] = 0.0f;
		}
		matrix[0] = matrix[5] = matrix[10] = matrix[15] = 1.0f;
	}

	/**
	 * Multiply two 4x4 matrices
	 */
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

	/**
	 * Update camera position (call this from your main rendering loop)
	 */
	public static void setCameraPosition(float x, float y, float z) {
		cameraX = x;
		cameraY = y;
		cameraZ = z;
	}

	/**
	 * Set view distance
	 */
	public static void setViewDistance(float distance) {
		viewDistance = distance;
	}

	/**
	 * Create shader program from vertex and fragment shader source
	 */
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

		// Check linking
		if (GL20.glGetProgrami(program, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
			String error = GL20.glGetProgramInfoLog(program);
			System.err.println("[RS317 Renderer] Shader linking failed: " + error);
			GL20.glDeleteProgram(program);
			GL20.glDeleteShader(vertexShader);
			GL20.glDeleteShader(fragmentShader);
			return 0;
		}

		// Cleanup
		GL20.glDeleteShader(vertexShader);
		GL20.glDeleteShader(fragmentShader);

		return program;
	}

	/**
	 * Compile individual shader
	 */
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

	/**
	 * Check if renderer is initialized
	 */
	public static boolean isInitialized() {
		return initialized;
	}

	/**
	 * Cleanup OpenGL resources
	 */
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