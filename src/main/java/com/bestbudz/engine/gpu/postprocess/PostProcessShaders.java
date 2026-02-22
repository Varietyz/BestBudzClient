package com.bestbudz.engine.gpu.postprocess;

/**
 * GLSL source strings for all post-processing passes.
 * Each pass uses the fullscreen quad (attribute 0 = position, attribute 1 = texcoord).
 */
public final class PostProcessShaders {

	private PostProcessShaders() {}

	// ========== Shared fullscreen quad vertex shader ==========

	public static final String FULLSCREEN_VERTEX = "#version 330 core\n" +
		"layout (location = 0) in vec2 aPosition;\n" +
		"layout (location = 1) in vec2 aTexCoord;\n" +
		"out vec2 vTexCoord;\n" +
		"void main() {\n" +
		"    gl_Position = vec4(aPosition, 0.0, 1.0);\n" +
		"    vTexCoord = aTexCoord;\n" +
		"}\n";

	// ========== Sky shader ==========

	public static final String SKY_VERTEX = "#version 330 core\n" +
		"layout (location = 0) in vec2 aPosition;\n" +
		"uniform mat4 uInvViewProjection;\n" +
		"out vec3 vWorldDir;\n" +
		"void main() {\n" +
		"    gl_Position = vec4(aPosition, 1.0, 1.0);\n" +
		"    // Reconstruct world-space ray direction from clip coordinates\n" +
		"    vec4 worldPos = uInvViewProjection * vec4(aPosition, 1.0, 1.0);\n" +
		"    vWorldDir = worldPos.xyz / worldPos.w;\n" +
		"}\n";

	public static final String SKY_FRAGMENT = "#version 330 core\n" +
		"in vec3 vWorldDir;\n" +
		"uniform vec3 uSunDirection;\n" +
		"uniform vec3 uSunColor;\n" +
		"uniform vec3 uSkyColorZenith;\n" +
		"uniform vec3 uSkyColorHorizon;\n" +
		"uniform float uSunSize;\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 dir = normalize(vWorldDir);\n" +
		"    \n" +
		"    // Sky gradient based on elevation\n" +
		"    float elevation = max(dir.y, 0.0);\n" +
		"    vec3 sky = mix(uSkyColorHorizon, uSkyColorZenith, pow(elevation, 0.4));\n" +
		"    \n" +
		"    // Below-horizon: darken towards ground\n" +
		"    if (dir.y < 0.0) {\n" +
		"        float below = clamp(-dir.y * 3.0, 0.0, 1.0);\n" +
		"        sky = mix(uSkyColorHorizon, uSkyColorHorizon * 0.3, below);\n" +
		"    }\n" +
		"    \n" +
		"    // Sun disc\n" +
		"    float sunDot = max(dot(dir, uSunDirection), 0.0);\n" +
		"    float sunDisc = smoothstep(1.0 - uSunSize * 0.01, 1.0 - uSunSize * 0.005, sunDot);\n" +
		"    \n" +
		"    // Atmospheric glow around sun\n" +
		"    float glow = pow(sunDot, 8.0) * 0.5;\n" +
		"    float outerGlow = pow(sunDot, 3.0) * 0.15;\n" +
		"    \n" +
		"    vec3 color = sky + uSunColor * (sunDisc * 3.0 + glow + outerGlow);\n" +
		"    FragColor = vec4(color, 1.0);\n" +
		"}\n";

	// ========== Bloom extract ==========

	public static final String BLOOM_EXTRACT_FRAGMENT = "#version 330 core\n" +
		"in vec2 vTexCoord;\n" +
		"uniform sampler2D uScene;\n" +
		"uniform float uThreshold;\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 color = texture(uScene, vTexCoord).rgb;\n" +
		"    float luminance = dot(color, vec3(0.2126, 0.7152, 0.0722));\n" +
		"    if (luminance > uThreshold) {\n" +
		"        FragColor = vec4(color * (luminance - uThreshold) / luminance, 1.0);\n" +
		"    } else {\n" +
		"        FragColor = vec4(0.0);\n" +
		"    }\n" +
		"}\n";

	// ========== Gaussian blur (single direction) ==========

	public static final String BLUR_FRAGMENT = "#version 330 core\n" +
		"in vec2 vTexCoord;\n" +
		"uniform sampler2D uInput;\n" +
		"uniform vec2 uDirection;\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"// 5-tap Gaussian weights\n" +
		"const float weights[5] = float[](0.227027, 0.1945946, 0.1216216, 0.054054, 0.016216);\n" +
		"\n" +
		"void main() {\n" +
		"    vec2 texelSize = 1.0 / textureSize(uInput, 0);\n" +
		"    vec3 result = texture(uInput, vTexCoord).rgb * weights[0];\n" +
		"    for (int i = 1; i < 5; i++) {\n" +
		"        vec2 offset = uDirection * texelSize * float(i);\n" +
		"        result += texture(uInput, vTexCoord + offset).rgb * weights[i];\n" +
		"        result += texture(uInput, vTexCoord - offset).rgb * weights[i];\n" +
		"    }\n" +
		"    FragColor = vec4(result, 1.0);\n" +
		"}\n";

	// ========== SSAO ==========

	public static final String SSAO_FRAGMENT = "#version 330 core\n" +
		"in vec2 vTexCoord;\n" +
		"uniform sampler2D uDepthTex;\n" +
		"uniform sampler2D uNoiseTex;\n" +
		"uniform vec3 uSamples[32];\n" +
		"uniform mat4 uProjection;\n" +
		"uniform mat4 uInvProjection;\n" +
		"uniform vec2 uNoiseScale;\n" +
		"uniform float uRadius;\n" +
		"uniform float uBias;\n" +
		"out float FragColor;\n" +
		"\n" +
		"vec3 reconstructViewPos(vec2 uv) {\n" +
		"    float depth = texture(uDepthTex, uv).r;\n" +
		"    vec4 clipPos = vec4(uv * 2.0 - 1.0, depth * 2.0 - 1.0, 1.0);\n" +
		"    vec4 viewPos = uInvProjection * clipPos;\n" +
		"    return viewPos.xyz / viewPos.w;\n" +
		"}\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 fragPos = reconstructViewPos(vTexCoord);\n" +
		"    \n" +
		"    // Reconstruct normal from depth derivatives\n" +
		"    vec3 dPdx = dFdx(fragPos);\n" +
		"    vec3 dPdy = dFdy(fragPos);\n" +
		"    vec3 normal = normalize(cross(dPdx, dPdy));\n" +
		"    \n" +
		"    // Random rotation from noise\n" +
		"    vec3 randomVec = texture(uNoiseTex, vTexCoord * uNoiseScale).xyz;\n" +
		"    \n" +
		"    // Create TBN basis\n" +
		"    vec3 tangent = normalize(randomVec - normal * dot(randomVec, normal));\n" +
		"    vec3 bitangent = cross(normal, tangent);\n" +
		"    mat3 TBN = mat3(tangent, bitangent, normal);\n" +
		"    \n" +
		"    float occlusion = 0.0;\n" +
		"    for (int i = 0; i < 32; i++) {\n" +
		"        vec3 samplePos = fragPos + TBN * uSamples[i] * uRadius;\n" +
		"        \n" +
		"        // Project to screen\n" +
		"        vec4 offset = uProjection * vec4(samplePos, 1.0);\n" +
		"        offset.xyz /= offset.w;\n" +
		"        offset.xyz = offset.xyz * 0.5 + 0.5;\n" +
		"        \n" +
		"        float sampleDepth = reconstructViewPos(offset.xy).z;\n" +
		"        float rangeCheck = smoothstep(0.0, 1.0, uRadius / abs(fragPos.z - sampleDepth));\n" +
		"        occlusion += (sampleDepth >= samplePos.z + uBias ? 1.0 : 0.0) * rangeCheck;\n" +
		"    }\n" +
		"    \n" +
		"    FragColor = 1.0 - (occlusion / 32.0);\n" +
		"}\n";

	public static final String SSAO_BLUR_FRAGMENT = "#version 330 core\n" +
		"in vec2 vTexCoord;\n" +
		"uniform sampler2D uSSAO;\n" +
		"out float FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    vec2 texelSize = 1.0 / textureSize(uSSAO, 0);\n" +
		"    float result = 0.0;\n" +
		"    for (int x = -2; x <= 2; x++) {\n" +
		"        for (int y = -2; y <= 2; y++) {\n" +
		"            result += texture(uSSAO, vTexCoord + vec2(float(x), float(y)) * texelSize).r;\n" +
		"        }\n" +
		"    }\n" +
		"    FragColor = result / 25.0;\n" +
		"}\n";

	// ========== Tone mapping + bloom composite + SSAO ==========

	public static final String TONEMAP_FRAGMENT = "#version 330 core\n" +
		"in vec2 vTexCoord;\n" +
		"uniform sampler2D uScene;\n" +
		"uniform sampler2D uBloom;\n" +
		"uniform sampler2D uSSAO;\n" +
		"uniform float uExposure;\n" +
		"uniform float uGamma;\n" +
		"uniform float uBloomIntensity;\n" +
		"uniform float uSSAOStrength;\n" +
		"uniform bool uBloomEnabled;\n" +
		"uniform bool uSSAOEnabled;\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"vec3 ACESFilm(vec3 x) {\n" +
		"    float a = 2.51;\n" +
		"    float b = 0.03;\n" +
		"    float c = 2.43;\n" +
		"    float d = 0.59;\n" +
		"    float e = 0.14;\n" +
		"    return clamp((x * (a * x + b)) / (x * (c * x + d) + e), 0.0, 1.0);\n" +
		"}\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 hdrColor = texture(uScene, vTexCoord).rgb;\n" +
		"    \n" +
		"    // Apply SSAO\n" +
		"    if (uSSAOEnabled) {\n" +
		"        float ao = texture(uSSAO, vTexCoord).r;\n" +
		"        ao = mix(1.0, ao, uSSAOStrength);\n" +
		"        hdrColor *= ao;\n" +
		"    }\n" +
		"    \n" +
		"    // Add bloom\n" +
		"    if (uBloomEnabled) {\n" +
		"        vec3 bloom = texture(uBloom, vTexCoord).rgb;\n" +
		"        hdrColor += bloom * uBloomIntensity;\n" +
		"    }\n" +
		"    \n" +
		"    // Exposure\n" +
		"    hdrColor *= uExposure;\n" +
		"    \n" +
		"    // ACES tone mapping\n" +
		"    vec3 ldrColor = ACESFilm(hdrColor);\n" +
		"    \n" +
		"    // Gamma correction (linear to sRGB)\n" +
		"    ldrColor = pow(ldrColor, vec3(1.0 / uGamma));\n" +
		"    \n" +
		"    FragColor = vec4(ldrColor, 1.0);\n" +
		"}\n";

	// ========== Color grading (3D LUT) ==========

	public static final String COLOR_GRADING_FRAGMENT = "#version 330 core\n" +
		"in vec2 vTexCoord;\n" +
		"uniform sampler2D uScene;\n" +
		"uniform sampler3D uLUT;\n" +
		"uniform float uStrength;\n" +
		"out vec4 FragColor;\n" +
		"\n" +
		"void main() {\n" +
		"    vec3 color = texture(uScene, vTexCoord).rgb;\n" +
		"    // Scale UV to sample LUT center of texels (avoid edge clamping artifacts)\n" +
		"    float lutSize = 32.0;\n" +
		"    vec3 lutCoord = color * ((lutSize - 1.0) / lutSize) + 0.5 / lutSize;\n" +
		"    vec3 graded = texture(uLUT, lutCoord).rgb;\n" +
		"    FragColor = vec4(mix(color, graded, uStrength), 1.0);\n" +
		"}\n";
}
