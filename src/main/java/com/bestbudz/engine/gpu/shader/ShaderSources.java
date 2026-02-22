package com.bestbudz.engine.gpu.shader;

public final class ShaderSources {

	private ShaderSources() {}

	// Vertex format (32 bytes per vertex):
	//   ivec4 aPositionAndColor (xyz = position, w = bits 0-15: HSL index, bits 16-17: plane)
	//   int   aAlpha
	//   int   aTextureId (-1 = untextured)
	//   vec2  aTexCoord  (UV in 0-1 range)

	// ========== Phase 5 Scene Shader (vertex -> geometry -> fragment) ==========
	// Geometry shader computes flat normals from triangle edges (cross product).
	// Fragment shader applies directional lighting, ambient, fog, and outputs HDR linear color.

	public static final String SCENE_VERTEX = "#version 330 core\n" +
		"\n" +
		"layout (location = 0) in ivec4 aPositionAndColor;\n" +
		"layout (location = 1) in int aAlpha;\n" +
		"layout (location = 2) in int aTextureId;\n" +
		"layout (location = 3) in vec2 aTexCoord;\n" +
		"\n" +
		"uniform vec3 uCameraPosition;\n" +
		"\n" +
		"// Pass-through to geometry shader (no projection yet)\n" +
		"out VS_OUT {\n" +
		"    vec3 worldPos;\n" +
		"    flat int colorHSL;\n" +
		"    flat int alpha;\n" +
		"    flat int textureId;\n" +
		"    vec2 texCoord;\n" +
		"} vs_out;\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 pos = vec3(aPositionAndColor.xyz) - uCameraPosition;\n" +
		"    vs_out.worldPos = pos;\n" +
		"    vs_out.colorHSL = aPositionAndColor.w;\n" +
		"    vs_out.alpha = aAlpha;\n" +
		"    vs_out.textureId = aTextureId;\n" +
		"    vs_out.texCoord = aTexCoord;\n" +
		"    // gl_Position set by geometry shader\n" +
		"    gl_Position = vec4(pos, 1.0);\n" +
		"}\n";

	public static final String SCENE_GEOMETRY = "#version 330 core\n" +
		"\n" +
		"layout (triangles) in;\n" +
		"layout (triangle_strip, max_vertices = 3) out;\n" +
		"\n" +
		"uniform mat4 uViewProjection;\n" +
		"\n" +
		"in VS_OUT {\n" +
		"    vec3 worldPos;\n" +
		"    flat int colorHSL;\n" +
		"    flat int alpha;\n" +
		"    flat int textureId;\n" +
		"    vec2 texCoord;\n" +
		"} gs_in[];\n" +
		"\n" +
		"// Outputs to fragment shader\n" +
		"flat out int vColorHSL;\n" +
		"flat out int vAlpha;\n" +
		"flat out int vTextureId;\n" +
		"out vec2 vTexCoord;\n" +
		"flat out vec3 vFlatNormal;\n" +
		"out vec3 vWorldPos;\n" +
		"\n" +
		"void main() {\n" +
		"    // Compute flat normal from triangle edges\n" +
		"    vec3 edge1 = gs_in[1].worldPos - gs_in[0].worldPos;\n" +
		"    vec3 edge2 = gs_in[2].worldPos - gs_in[0].worldPos;\n" +
		"    vec3 N = normalize(cross(edge1, edge2));\n" +
		"\n" +
		"    for (int i = 0; i < 3; i++) {\n" +
		"        gl_Position = uViewProjection * vec4(gs_in[i].worldPos, 1.0);\n" +
		"        vColorHSL = gs_in[i].colorHSL;\n" +
		"        vAlpha = gs_in[i].alpha;\n" +
		"        vTextureId = gs_in[i].textureId;\n" +
		"        vTexCoord = gs_in[i].texCoord;\n" +
		"        vFlatNormal = N;\n" +
		"        vWorldPos = gs_in[i].worldPos;\n" +
		"        EmitVertex();\n" +
		"    }\n" +
		"    EndPrimitive();\n" +
		"}\n";

	public static final String SCENE_FRAGMENT = "#version 330 core\n" +
		"\n" +
		"uniform sampler2D uColorPalette;\n" +
		"uniform sampler2DArray uTextureArray;\n" +
		"uniform int uCurrentPlane;\n" +
		"\n" +
		"// Lighting uniforms\n" +
		"uniform vec3 uSunDirection;\n" +
		"uniform vec3 uSunColor;\n" +
		"uniform float uSunStrength;\n" +
		"uniform vec3 uAmbientColor;\n" +
		"uniform float uAmbientStrength;\n" +
		"\n" +
		"// Fog uniforms\n" +
		"uniform vec3 uFogColor;\n" +
		"uniform float uFogStart;\n" +
		"uniform float uFogEnd;\n" +
		"\n" +
		"flat in int vColorHSL;\n" +
		"flat in int vAlpha;\n" +
		"flat in int vTextureId;\n" +
		"in vec2 vTexCoord;\n" +
		"flat in vec3 vFlatNormal;\n" +
		"in vec3 vWorldPos;\n" +
		"\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    // Plane culling: bits 16-17 of vColorHSL hold the plane (0-3)\n" +
		"    int vertexPlane = (vColorHSL >> 16) & 0x3;\n" +
		"    if (vertexPlane > uCurrentPlane) discard;\n" +
		"\n" +
		"    float alpha = float(vAlpha) / 255.0;\n" +
		"\n" +
		"    vec3 baseColor;\n" +
		"    if (vTextureId >= 0) {\n" +
		"        vec4 texColor = texture(uTextureArray, vec3(vTexCoord, float(vTextureId)));\n" +
		"        if (texColor.a < 0.01) discard;\n" +
		"        baseColor = texColor.rgb;\n" +
		"    } else {\n" +
		"        int hsl = vColorHSL & 0xFFFF;\n" +
		"        ivec2 palCoord = ivec2(hsl & 0xFF, hsl >> 8);\n" +
		"        baseColor = texelFetch(uColorPalette, palCoord, 0).rgb;\n" +
		"    }\n" +
		"\n" +
		"    // sRGB to linear\n" +
		"    baseColor = pow(baseColor, vec3(2.2));\n" +
		"\n" +
		"    // Directional lighting (Lambert diffuse)\n" +
		"    vec3 N = normalize(vFlatNormal);\n" +
		"    float NdotL = max(dot(N, uSunDirection), 0.0);\n" +
		"    vec3 diffuse = uSunColor * uSunStrength * NdotL;\n" +
		"    vec3 ambient = uAmbientColor * uAmbientStrength;\n" +
		"    vec3 lit = baseColor * (ambient + diffuse);\n" +
		"\n" +
		"    // Distance fog (exponential squared)\n" +
		"    float dist = length(vWorldPos);\n" +
		"    float fogRange = uFogEnd - uFogStart;\n" +
		"    float f = clamp((dist - uFogStart) / fogRange, 0.0, 1.0);\n" +
		"    float fogFactor = 1.0 - exp(-f * f * 3.0);\n" +
		"    // Fog color in linear space\n" +
		"    vec3 fogLin = pow(uFogColor, vec3(2.2));\n" +
		"    lit = mix(lit, fogLin, fogFactor);\n" +
		"\n" +
		"    // Output HDR linear color\n" +
		"    FragColor = vec4(lit, alpha);\n" +
		"}\n";

	// ========== Legacy shaders (kept for reference, no longer used) ==========

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
		"uniform int uCurrentPlane;\n" +
		"\n" +
		"flat in int vColorHSL;\n" +
		"flat in int vAlpha;\n" +
		"flat in int vTextureId;\n" +
		"in vec2 vTexCoord;\n" +
		"\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    // Plane culling: bits 16-17 of vColorHSL hold the plane (0-3)\n" +
		"    int vertexPlane = (vColorHSL >> 16) & 0x3;\n" +
		"    if (vertexPlane > uCurrentPlane) discard;\n" +
		"\n" +
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
