package com.bestbudz.engine.gpu.shader;

public final class ShaderSources {

	private ShaderSources() {}

	// Phase 2: Per-vertex colored models with texture support
	// Vertex format (32 bytes per vertex):
	//   ivec4 aPositionAndColor (xyz = position, w = HSL index)
	//   int   aAlpha
	//   int   aTextureId (-1 = untextured)
	//   vec2  aTexCoord  (UV in 0-1 range)

	public static final String MODEL_VERTEX = "#version 330 core\n" +
		"\n" +
		"layout (location = 0) in ivec4 aPositionAndColor;\n" +
		"layout (location = 1) in int aAlpha;\n" +
		"layout (location = 2) in int aTextureId;\n" +
		"layout (location = 3) in vec2 aTexCoord;\n" +
		"\n" +
		"uniform mat4 uViewProjection;\n" +
		"uniform vec3 uCameraPosition;\n" +
		"\n" +
		"flat out int vColorHSL;\n" +
		"flat out int vAlpha;\n" +
		"flat out int vTextureId;\n" +
		"out vec2 vTexCoord;\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 pos = vec3(aPositionAndColor.xyz) - uCameraPosition;\n" +
		"    gl_Position = uViewProjection * vec4(pos, 1.0);\n" +
		"    vColorHSL = aPositionAndColor.w;\n" +
		"    vAlpha = aAlpha;\n" +
		"    vTextureId = aTextureId;\n" +
		"    vTexCoord = aTexCoord;\n" +
		"}\n";

	public static final String MODEL_FRAGMENT = "#version 330 core\n" +
		"\n" +
		"uniform sampler2D uColorPalette;\n" +
		"uniform sampler2DArray uTextureArray;\n" +
		"\n" +
		"flat in int vColorHSL;\n" +
		"flat in int vAlpha;\n" +
		"flat in int vTextureId;\n" +
		"in vec2 vTexCoord;\n" +
		"\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    float alpha = float(vAlpha) / 255.0;\n" +
		"\n" +
		"    if (vTextureId >= 0) {\n" +
		"        // Textured face: sample from texture array\n" +
		"        vec4 texColor = texture(uTextureArray, vec3(vTexCoord, float(vTextureId)));\n" +
		"        if (texColor.a < 0.01) discard;\n" +
		"        FragColor = vec4(texColor.rgb, alpha);\n" +
		"    } else {\n" +
		"        // Untextured face: HSL palette lookup\n" +
		"        int hsl = vColorHSL & 0xFFFF;\n" +
		"        ivec2 palCoord = ivec2(hsl & 0xFF, hsl >> 8);\n" +
		"        vec4 color = texelFetch(uColorPalette, palCoord, 0);\n" +
		"        FragColor = vec4(color.rgb, alpha);\n" +
		"    }\n" +
		"}\n";
}
