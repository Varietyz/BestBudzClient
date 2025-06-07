package com.bestbudz.engine.gpu;

import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public final class OpenGLRasterizer {

	// Shader programs
	private static int colorShaderProgram;
	private static int textureShaderProgram;

	// Uniform locations for color shader
	private static int colorMVPLocation;
	private static int colorPaletteLocation;

	// Uniform locations for texture shader
	private static int textureMVPLocation;
	private static int texturePaletteLocation;
	private static int textureMapLocation;
	private static int textureMipLevelLocation;

	// Vertex array objects and buffers
	private static int colorVAO, colorVBO;
	private static int textureVAO, textureVBO;

	// Single texture array for all game textures
	private static int textureArray;
	private static final int MAX_TEXTURES = 51;

	// Palette texture for HSL color conversion - now 2D instead of 1D
	private static int paletteTexture;
	private static final int PALETTE_WIDTH = 256;
	private static final int PALETTE_HEIGHT = 256;

	// Batch rendering data
	private static List<Float> colorVertices = new ArrayList<>();
	private static List<Float> textureVertices = new ArrayList<>();

	// Matrix uniforms
	private static float[] mvpMatrix = new float[16];

	public static void initialize() {
		System.out.println("[OpenGL Rasterizer] Initializing...");

		setupShaders();
		setupBuffers();
		setupPaletteTexture();
		setupGameTextures();

		// Enable depth testing to match original z-buffer behavior
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);

		// Enable blending for transparency
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		// Disable face culling to match CPU rasterizer
		glDisable(GL_CULL_FACE);

		checkGLError("After initialization");
		System.out.println("[OpenGL Rasterizer] ✅ Initialization complete");
	}

	private static void setupShaders() {
		// Color vertex shader
		String colorVertexShader = "#version 330 core\n" +
			"layout (location = 0) in vec3 aPosition;\n" +
			"layout (location = 1) in float aColorIndex;\n" +
			"layout (location = 2) in float aDepth;\n" +
			"\n" +
			"uniform mat4 uMVP;\n" +
			"\n" +
			"out float colorIndex;\n" +
			"out float depth;\n" +
			"\n" +
			"void main() {\n" +
			"    gl_Position = uMVP * vec4(aPosition.xy, aDepth, 1.0);\n" +  // Remove negation
			"    colorIndex = aColorIndex;\n" +
			"    depth = aDepth;\n" +
			"}\n";

		// Fixed color fragment shader - now uses 2D palette texture
		String colorFragmentShader = "#version 330 core\n" +
			"in float colorIndex;\n" +
			"in float depth;\n" +
			"\n" +
			"uniform sampler2D uPalette;\n" +
			"\n" +
			"out vec4 FragColor;\n" +
			"\n" +
			"void main() {\n" +
			"    // Convert HSL index to 2D texture coordinates\n" +
			"    float normalizedIndex = colorIndex / 65535.0;\n" +
			"    float x = mod(normalizedIndex * 65536.0, 256.0) / 256.0;\n" +
			"    float y = floor(normalizedIndex * 65536.0 / 256.0) / 256.0;\n" +
			"    \n" +
			"    // Sample palette texture\n" +
			"    vec3 color = texture(uPalette, vec2(x, y)).rgb;\n" +
			"    FragColor = vec4(color, 1.0);\n" +
			"}\n";

		// Texture vertex shader
		String textureVertexShader = "#version 330 core\n" +
			"layout (location = 0) in vec3 aPosition;\n" +
			"layout (location = 1) in vec2 aTexCoord;\n" +
			"layout (location = 2) in vec3 aLighting;\n" +
			"layout (location = 3) in float aDepth;\n" +
			"layout (location = 4) in float aTexIndex;\n" +
			"\n" +
			"uniform mat4 uMVP;\n" +
			"\n" +
			"out vec2 fragTexCoord;\n" +
			"out vec3 fragLighting;\n" +
			"out float fragDepth;\n" +
			"flat out int fragTexIndex;\n" +
			"\n" +
			"void main() {\n" +
			"    gl_Position = uMVP * vec4(aPosition.xy, aDepth, 1.0);\n" +  // Remove negation
			"    \n" +
			"    fragTexCoord = aTexCoord;\n" +
			"    fragLighting = aLighting;\n" +
			"    fragDepth = aDepth;\n" +
			"    fragTexIndex = int(aTexIndex);\n" +
			"}\n";

		// Fixed texture fragment shader - proper depth handling
		String textureFragmentShader = "#version 330 core\n" +
			"in vec2 fragTexCoord;\n" +
			"in vec3 fragLighting;\n" +
			"in float fragDepth;\n" +
			"flat in int fragTexIndex;\n" +
			"\n" +
			"uniform sampler2DArray uTextureArray;\n" +
			"uniform float uMipLevel;\n" +
			"\n" +
			"out vec4 FragColor;\n" +
			"\n" +
			"void main() {\n" +
			"    // Clamp texture index to valid range\n" +
			"    int texIndex = clamp(fragTexIndex, 0, " + (MAX_TEXTURES - 1) + ");\n" +
			"    \n" +
			"    // Sample texture with mipmap level\n" +
			"    vec4 texColor = textureLod(uTextureArray,\n" +
			"        vec3(fragTexCoord, float(texIndex)), uMipLevel);\n" +
			"\n" +
			"    // Handle transparency\n" +
			"    if (texColor.a < 0.1) discard;\n" +
			"\n" +
			"    // Apply lighting (ensure it's not too dark)\n" +
			"    vec3 lighting = max(fragLighting, vec3(0.1));\n" +
			"    vec3 litColor = texColor.rgb * lighting;\n" +
			"\n" +
			"    FragColor = vec4(litColor, texColor.a);\n" +
			"    \n" +
			"    // Proper depth handling for 0-1000 range\n" +
			"    gl_FragDepth = fragDepth / 1000.0;\n" +
			"}\n";

		colorShaderProgram = createShaderProgram(colorVertexShader, colorFragmentShader);
		textureShaderProgram = createShaderProgram(textureVertexShader, textureFragmentShader);

		// Get uniform locations
		colorMVPLocation = glGetUniformLocation(colorShaderProgram, "uMVP");
		colorPaletteLocation = glGetUniformLocation(colorShaderProgram, "uPalette");

		textureMVPLocation = glGetUniformLocation(textureShaderProgram, "uMVP");
		textureMapLocation = glGetUniformLocation(textureShaderProgram, "uTextureArray");
		textureMipLevelLocation = glGetUniformLocation(textureShaderProgram, "uMipLevel");

		checkGLError("After shader setup");
	}

	private static int createShaderProgram(String vertexSource, String fragmentSource) {
		int vertexShader = compileShader(GL_VERTEX_SHADER, vertexSource);
		int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

		int program = glCreateProgram();
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);
		glLinkProgram(program);

		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			String error = glGetProgramInfoLog(program);
			glDeleteProgram(program);
			throw new RuntimeException("Shader program linking failed: " + error);
		}

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);

		return program;
	}

	private static int compileShader(int type, String source) {
		int shader = glCreateShader(type);
		glShaderSource(shader, source);
		glCompileShader(shader);

		if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
			String error = glGetShaderInfoLog(shader);
			glDeleteShader(shader);
			throw new RuntimeException("Shader compilation failed: " + error);
		}

		return shader;
	}

	private static void setupBuffers() {
		// Color rendering VAO/VBO
		colorVAO = glGenVertexArrays();
		colorVBO = glGenBuffers();

		glBindVertexArray(colorVAO);
		glBindBuffer(GL_ARRAY_BUFFER, colorVBO);

		// Position (3 floats) - location 0
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		// Color HSL index (1 float) - location 1
		glVertexAttribPointer(1, 1, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		// Depth (1 float) - location 2
		glVertexAttribPointer(2, 1, GL_FLOAT, false, 5 * Float.BYTES, 4 * Float.BYTES);
		glEnableVertexAttribArray(2);

		// Texture rendering VAO/VBO
		textureVAO = glGenVertexArrays();
		textureVBO = glGenBuffers();

		glBindVertexArray(textureVAO);
		glBindBuffer(GL_ARRAY_BUFFER, textureVBO);

		// Position (3 floats) - location 0
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 10 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		// Texture coordinates (2 floats) - location 1
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 10 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		// Lighting (3 floats) - location 2
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 10 * Float.BYTES, 5 * Float.BYTES);
		glEnableVertexAttribArray(2);

		// Depth (1 float) - location 3
		glVertexAttribPointer(3, 1, GL_FLOAT, false, 10 * Float.BYTES, 8 * Float.BYTES);
		glEnableVertexAttribArray(3);

		// Texture index (1 float) - location 4
		glVertexAttribPointer(4, 1, GL_FLOAT, false, 10 * Float.BYTES, 9 * Float.BYTES);
		glEnableVertexAttribArray(4);

		glBindVertexArray(0);
		checkGLError("After buffer setup");
	}

	private static void setupPaletteTexture() {
		// Check maximum texture size first
		int maxTextureSize = glGetInteger(GL_MAX_TEXTURE_SIZE);
		System.out.println("[OpenGL] Max texture size: " + maxTextureSize);

		if (PALETTE_WIDTH > maxTextureSize || PALETTE_HEIGHT > maxTextureSize) {
			throw new RuntimeException("Palette texture size exceeds maximum supported texture size");
		}

		// Generate the exact same color palette as the 317 client
		paletteTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, paletteTexture);

		// Create RGB palette data using the actual 317 color generation algorithm
		FloatBuffer paletteData = MemoryUtil.memAllocFloat(PALETTE_WIDTH * PALETTE_HEIGHT * 3);

		try {
			// Generate the palette using the same algorithm as the client
			int[] palette = generate317ColorPalette();

			for (int i = 0; i < 65536; i++) {
				int rgb = palette[i];

				float r = ((rgb >> 16) & 0xFF) / 255.0f;
				float g = ((rgb >> 8) & 0xFF) / 255.0f;
				float b = (rgb & 0xFF) / 255.0f;

				paletteData.put(r).put(g).put(b);
			}
			paletteData.flip();

			// Create 2D texture instead of 1D
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, PALETTE_WIDTH, PALETTE_HEIGHT, 0, GL_RGB, GL_FLOAT, paletteData);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			checkGLError("After palette texture setup");
			System.out.println("[OpenGL] ✅ Palette texture created successfully (256x256)");

		} catch (Exception e) {
			System.err.println("[OpenGL] ❌ Error creating palette texture: " + e.getMessage());
			e.printStackTrace();
		} finally {
			MemoryUtil.memFree(paletteData);
		}
	}

	private static void setupGameTextures() {
		System.out.println("[OpenGL] Setting up game textures...");

		// Create single texture array for all textures
		textureArray = glGenTextures();
		glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray);

		// Check if texture arrays are supported
		int maxArrayLayers = glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS);
		System.out.println("[OpenGL] Max array texture layers: " + maxArrayLayers);

		if (MAX_TEXTURES > maxArrayLayers) {
			throw new RuntimeException("Required texture array layers exceed maximum supported");
		}

		// Allocate storage for all texture layers (51 textures, 8 mipmap levels)
		// Start with level 0 (128x128) and generate mipmaps
		glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA8, 128, 128, MAX_TEXTURES,
			0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

		// Upload each texture
		int validTextures = 0;
		for (int i = 0; i < Math.min(MAX_TEXTURES, com.bestbudz.engine.core.gamerender.Rasterizer.textureAmount); i++) {
			if (com.bestbudz.engine.core.gamerender.Rasterizer.aBackgroundArray1474s[i] != null) {
				try {
					uploadTextureToLayer(i);
					validTextures++;
				} catch (Exception e) {
					System.err.println("[OpenGL] Failed to upload texture " + i + ": " + e.getMessage());
					// Fill with a default color to prevent issues
					fillTextureLayerWithColor(i, 0xFF00FF00); // Green as placeholder
				}
			} else {
				// Fill empty slots with a default texture
				fillTextureLayerWithColor(i, 0xFF808080); // Gray as placeholder
			}
		}

		// Generate mipmaps
		glGenerateMipmap(GL_TEXTURE_2D_ARRAY);

		// Set texture parameters
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_REPEAT);

		checkGLError("After texture array setup");
		System.out.println("[OpenGL] ✅ Uploaded " + validTextures + " textures to array");
	}

	private static void uploadTextureToLayer(int textureIndex) {
		int[][] mipmaps = com.bestbudz.engine.core.gamerender.Rasterizer.method371(textureIndex);

		if (mipmaps == null || mipmaps.length == 0) {
			return;
		}

		// Upload base level (128x128)
		int[] baseLevel = mipmaps[0];
		IntBuffer textureData = MemoryUtil.memAllocInt(128 * 128);

		try {
			// Fill with texture data or black if insufficient data
			for (int i = 0; i < 128 * 128; i++) {
				if (i < baseLevel.length) {
					// Convert from ARGB to RGBA if necessary
					int pixel = baseLevel[i];
					int a = (pixel >> 24) & 0xFF;
					int r = (pixel >> 16) & 0xFF;
					int g = (pixel >> 8) & 0xFF;
					int b = pixel & 0xFF;

					// If alpha is 0, make it fully opaque
					if (a == 0 && (r != 0 || g != 0 || b != 0)) {
						a = 255;
					}

					textureData.put((a << 24) | (b << 16) | (g << 8) | r); // RGBA format
				} else {
					textureData.put(0xFF000000); // Black with full alpha
				}
			}
			textureData.flip();

			glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, textureIndex,
				128, 128, 1, GL_RGBA, GL_UNSIGNED_BYTE, textureData);

		} finally {
			MemoryUtil.memFree(textureData);
		}
	}

	private static void fillTextureLayerWithColor(int textureIndex, int color) {
		IntBuffer textureData = MemoryUtil.memAllocInt(128 * 128);

		try {
			for (int i = 0; i < 128 * 128; i++) {
				textureData.put(color);
			}
			textureData.flip();

			glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, textureIndex,
				128, 128, 1, GL_RGBA, GL_UNSIGNED_BYTE, textureData);

		} finally {
			MemoryUtil.memFree(textureData);
		}
	}

	// Main rendering methods - drop-in replacements for original methods
	public static void method374(int y1, int y2, int y3, int x1, int x2, int x3,
								 int hsl1, int hsl2, int hsl3, float z1, float z2, float z3) {
		// Add triangle to color batch
		addColorTriangle(x1, y1, x2, y2, x3, y3, hsl1, hsl2, hsl3, z1, z2, z3);
	}

	public static void method378(int y_a, int y_b, int y_c, int x_a, int x_b, int x_c,
								 int l1, int l2, int l3, int tx1, int tx2, int tx3,
								 int ty1, int ty2, int ty3, int tz1, int tz2, int tz3,
								 int tex, float z1, float z2, float z3) {
		// Add textured triangle to texture batch
		addTexturedTriangle(x_a, y_a, x_b, y_b, x_c, y_c, l1, l2, l3,
			tx1, tx2, tx3, ty1, ty2, ty3, tz1, tz2, tz3, tex, z1, z2, z3);
	}

	private static void addColorTriangle(int x1, int y1, int x2, int y2, int x3, int y3,
										 int hsl1, int hsl2, int hsl3, float z1, float z2, float z3) {
		// Vertex 1
		colorVertices.add((float) x1);
		colorVertices.add((float) y1);
		colorVertices.add(0.0f); // Z coordinate (unused in 2D)
		colorVertices.add((float) hsl1); // HSL index for palette lookup
		colorVertices.add(z1); // Depth

		// Vertex 2
		colorVertices.add((float) x2);
		colorVertices.add((float) y2);
		colorVertices.add(0.0f);
		colorVertices.add((float) hsl2);
		colorVertices.add(z2);

		// Vertex 3
		colorVertices.add((float) x3);
		colorVertices.add((float) y3);
		colorVertices.add(0.0f);
		colorVertices.add((float) hsl3);
		colorVertices.add(z3);
	}

	private static void addTexturedTriangle(int x1, int y1, int x2, int y2, int x3, int y3,
											int l1, int l2, int l3, int tx1, int tx2, int tx3,
											int ty1, int ty2, int ty3, int tz1, int tz2, int tz3,
											int tex, float z1, float z2, float z3) {

		// Improved texture coordinate calculation
		float u1 = (float) tx1 / Math.max(Math.abs(tz1), 1) / 128.0f;
		float v1 = (float) ty1 / Math.max(Math.abs(tz1), 1) / 128.0f;
		float u2 = (float) tx2 / Math.max(Math.abs(tz2), 1) / 128.0f;
		float v2 = (float) ty2 / Math.max(Math.abs(tz2), 1) / 128.0f;
		float u3 = (float) tx3 / Math.max(Math.abs(tz3), 1) / 128.0f;
		float v3 = (float) ty3 / Math.max(Math.abs(tz3), 1) / 128.0f;

		// Improved lighting calculation
		float light1 = Math.max(0.2f, Math.min(1.0f, (127.0f - l1) / 127.0f));
		float light2 = Math.max(0.2f, Math.min(1.0f, (127.0f - l2) / 127.0f));
		float light3 = Math.max(0.2f, Math.min(1.0f, (127.0f - l3) / 127.0f));

		// Vertex 1
		textureVertices.add((float) x1);
		textureVertices.add((float) y1);
		textureVertices.add(0.0f);
		textureVertices.add(u1);
		textureVertices.add(v1);
		textureVertices.add(light1);
		textureVertices.add(light1);
		textureVertices.add(light1);
		textureVertices.add(z1);
		textureVertices.add((float) tex);

		// Vertex 2
		textureVertices.add((float) x2);
		textureVertices.add((float) y2);
		textureVertices.add(0.0f);
		textureVertices.add(u2);
		textureVertices.add(v2);
		textureVertices.add(light2);
		textureVertices.add(light2);
		textureVertices.add(light2);
		textureVertices.add(z2);
		textureVertices.add((float) tex);

		// Vertex 3
		textureVertices.add((float) x3);
		textureVertices.add((float) y3);
		textureVertices.add(0.0f);
		textureVertices.add(u3);
		textureVertices.add(v3);
		textureVertices.add(light3);
		textureVertices.add(light3);
		textureVertices.add(light3);
		textureVertices.add(z3);
		textureVertices.add((float) tex);
	}

	public static void flush() {
		// Render all batched triangles
		renderColorBatch();
		renderTextureBatch();

		// Clear batches for next frame
		colorVertices.clear();
		textureVertices.clear();
	}

	private static void renderColorBatch() {
		if (colorVertices.isEmpty()) {
			return;
		}

		glUseProgram(colorShaderProgram);

		// Bind palette texture
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, paletteTexture);
		glUniform1i(colorPaletteLocation, 0);

		// Update MVP matrix
		glUniformMatrix4fv(colorMVPLocation, false, mvpMatrix);

		// Upload vertex data
		float[] vertexArray = new float[colorVertices.size()];
		for (int i = 0; i < colorVertices.size(); i++) {
			vertexArray[i] = colorVertices.get(i);
		}

		glBindVertexArray(colorVAO);
		glBindBuffer(GL_ARRAY_BUFFER, colorVBO);
		glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

		// Draw triangles
		int vertexCount = colorVertices.size() / 5; // 5 components per vertex
		glDrawArrays(GL_TRIANGLES, 0, vertexCount);

		glBindVertexArray(0);
		glUseProgram(0);

		checkGLError("After color batch render");
	}

	private static void renderTextureBatch() {
		if (textureVertices.isEmpty()) return;

		glUseProgram(textureShaderProgram);

		// Bind texture array
		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D_ARRAY, textureArray);
		glUniform1i(textureMapLocation, 0);

		// Set mipmap level
		glUniform1f(textureMipLevelLocation, 0.0f);

		// Update MVP matrix
		glUniformMatrix4fv(textureMVPLocation, false, mvpMatrix);

		// Upload vertex data
		float[] vertexArray = new float[textureVertices.size()];
		for (int i = 0; i < textureVertices.size(); i++) {
			vertexArray[i] = textureVertices.get(i);
		}

		glBindVertexArray(textureVAO);
		glBindBuffer(GL_ARRAY_BUFFER, textureVBO);
		glBufferData(GL_ARRAY_BUFFER, vertexArray, GL_DYNAMIC_DRAW);

		// Draw triangles
		glDrawArrays(GL_TRIANGLES, 0, textureVertices.size() / 10); // 10 components per vertex

		glBindVertexArray(0);
		glUseProgram(0);

		checkGLError("After texture batch render");
	}

	public static void setViewportSize(int width, int height) {
		System.out.println("[OpenGL Rasterizer] Setting viewport: " + width + "x" + height);
		glViewport(0, 0, width, height);
		updateProjectionMatrix(width, height);
	}

	private static void updateProjectionMatrix(int width, int height) {
		// Create orthographic projection matrix for screen coordinates (0,0) top-left
		float left = 0.0f;
		float right = (float) width;
		float bottom = (float) height;  // bottom is height
		float top = 0.0f;               // top is 0
		float near = 0.0f;              // Change to 0-1000 range
		float far = 1000.0f;

		// Clear matrix
		for (int i = 0; i < 16; i++) {
			mvpMatrix[i] = 0.0f;
		}

		// Corrected orthographic projection matrix
		mvpMatrix[0] = 2.0f / (right - left);           // X scale
		mvpMatrix[5] = 2.0f / (top - bottom);           // Y scale (positive for top-left origin)
		mvpMatrix[10] = -2.0f / (far - near);           // Z scale
		mvpMatrix[12] = -(right + left) / (right - left);   // X offset
		mvpMatrix[13] = -(top + bottom) / (top - bottom);   // Y offset
		mvpMatrix[14] = -(far + near) / (far - near);       // Z offset
		mvpMatrix[15] = 1.0f;
	}

	// Utility method for error checking
	private static void checkGLError(String operation) {
		int error = glGetError();
		if (error != GL_NO_ERROR) {
			String errorString;
			switch (error) {
				case GL_INVALID_ENUM: errorString = "GL_INVALID_ENUM"; break;
				case GL_INVALID_VALUE: errorString = "GL_INVALID_VALUE"; break;
				case GL_INVALID_OPERATION: errorString = "GL_INVALID_OPERATION"; break;
				case GL_OUT_OF_MEMORY: errorString = "GL_OUT_OF_MEMORY"; break;
				default: errorString = "Unknown error " + error;
			}
			System.err.println("[OpenGL Error] " + operation + ": " + errorString);
		}
	}

	public static void cleanup() {
		glDeleteProgram(colorShaderProgram);
		glDeleteProgram(textureShaderProgram);
		glDeleteVertexArrays(colorVAO);
		glDeleteVertexArrays(textureVAO);
		glDeleteBuffers(colorVBO);
		glDeleteBuffers(textureVBO);
		glDeleteTextures(paletteTexture);
		glDeleteTextures(textureArray);
	}

	/**
	 * Generates the exact same color palette as the 317 client
	 * This matches your generateColorPalette() method exactly
	 */
	private static int[] generate317ColorPalette() {
		int[] palette = new int[65536];
		int index = 0;

		// Color generation constants (matching your client settings)
		final double GLOBAL_HUE_SHIFT = 0.6; // +10%
		final boolean ENABLE_RED_OVERRIDE = true;
		final boolean ENABLE_YELLOW_SKIP = true;

		// Generate colors using the exact same algorithm as your client
		for (int k = 0; k < 512; k++) {
			double originalHue = (k / 8.0) / 64.0 + 0.0078125;
			double hue = modifyHue(originalHue, ENABLE_RED_OVERRIDE, ENABLE_YELLOW_SKIP, GLOBAL_HUE_SHIFT);

			double saturation = (k & 7) / 8.0 + 0.0625;
			saturation = Math.min(1.0, saturation * 0.8);

			for (int i = 0; i < 128; i++) {
				double luminance = i / 128.0;

				double r = luminance, g = luminance, b = luminance;

				if (saturation != 0.0) {
					double q = luminance < 0.5
						? luminance * (1.0 + saturation)
						: (luminance + saturation) - (luminance * saturation);
					double p = 2 * luminance - q;

					r = hueToRgb(p, q, hue + 1.0 / 3.0);
					g = hueToRgb(p, q, hue);
					b = hueToRgb(p, q, hue - 1.0 / 3.0);
				}

				int ri = (int) (r * 256.0);
				int gi = (int) (g * 256.0);
				int bi = (int) (b * 256.0);

				int rgb = (ri << 16) | (gi << 8) | bi;
				rgb = adjustColorBrightness(rgb, GLOBAL_HUE_SHIFT);
				palette[index++] = rgb != 0 ? rgb : 1;
			}
		}

		return palette;
	}

	/**
	 * Modifies hue exactly like your client code
	 */
	private static double modifyHue(double originalHue, boolean enableRedOverride,
									boolean enableYellowSkip, double globalHueShift) {
		boolean inYellowRange = originalHue >= 0.05 && originalHue <= 0.16;

		if (enableRedOverride && (originalHue <= 0.05 || originalHue >= 0.97)) {
			return 0.50; // on red
		}

		if (enableYellowSkip && inYellowRange) {
			return originalHue;
		}

		double shifted = originalHue + globalHueShift;
		return shifted > 1.0 ? shifted - 1.0 : shifted;
	}

	/**
	 * HSL to RGB conversion - exact match to your client
	 */
	private static double hueToRgb(double p, double q, double t) {
		if (t < 0) t += 1.0;
		if (t > 1) t -= 1.0;
		if (t < 1.0 / 6.0) return p + (q - p) * 6.0 * t;
		if (t < 1.0 / 2.0) return q;
		if (t < 2.0 / 3.0) return p + (q - p) * (2.0 / 3.0 - t) * 6.0;
		return p;
	}

	/**
	 * Color brightness adjustment - exact match to your client
	 */
	private static int adjustColorBrightness(int i, double d) {
		double d1 = (double) (i >> 16) / 256.0;
		double d2 = (double) (i >> 8 & 0xff) / 256.0;
		double d3 = (double) (i & 0xff) / 256.0;
		d1 = Math.pow(d1, d);
		d2 = Math.pow(d2, d);
		d3 = Math.pow(d3, d);
		int j = (int) (d1 * 256.0);
		int k = (int) (d2 * 256.0);
		int l = (int) (d3 * 256.0);
		return (j << 16) + (k << 8) + l;
	}

	public static void renderDebugTriangle() {
		// Add a simple test triangle to verify rendering works
		colorVertices.clear();

		// Red triangle in screen center
		float centerX = 640.0f;  // Assuming 1280x720
		float centerY = 360.0f;

		// Triangle vertices (screen coordinates)
		colorVertices.add(centerX);      // x1
		colorVertices.add(centerY - 50); // y1
		colorVertices.add(0.0f);         // z1
		colorVertices.add(255.0f);       // red color index
		colorVertices.add(100.0f);       // depth

		colorVertices.add(centerX - 50); // x2
		colorVertices.add(centerY + 50); // y2
		colorVertices.add(0.0f);         // z2
		colorVertices.add(255.0f);       // red color index
		colorVertices.add(100.0f);       // depth

		colorVertices.add(centerX + 50); // x3
		colorVertices.add(centerY + 50); // y3
		colorVertices.add(0.0f);         // z3
		colorVertices.add(255.0f);       // red color index
		colorVertices.add(100.0f);       // depth

		renderColorBatch();
		colorVertices.clear();
	}
}