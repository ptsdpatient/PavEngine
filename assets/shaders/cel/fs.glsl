#ifdef GLSL3
out vec4 out_FragColor;
#else
#define out_FragColor gl_FragColor
#endif

precision highp float;

varying vec3 v_normal;
varying vec2 v_texCoord0;

uniform vec4 u_BaseColorFactor;   // base color
uniform vec3 u_lightDir;          // normalized light direction

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

// Brighter anime-style palette
vec3 animeShade(float step, vec3 base) {
    if(step < 0.3) return base * vec3(0.7, 0.65, 0.8);   // brighter shadow
    else if(step < 0.7) return base * vec3(0.95, 0.9, 1.0); // bright midtone
    else return base;                                      // highlight (full color)
}

void main() {
    vec3 normal = normalize(v_normal);
    vec3 lightDir = normalize(u_lightDir);

    // Lambertian dot product
    float NdotL = max(dot(normal, lightDir), 0.0);

    // Quantize for cel shading
    float celStep;
    if(NdotL < 0.3) celStep = 0.0;       // shadow
    else if(NdotL < 0.7) celStep = 0.5;  // midtone
    else celStep = 1.0;                   // highlight

    // Base color
    vec3 baseColor = u_BaseColorFactor.rgb;
#ifdef diffuseTextureFlag
    baseColor *= texture2D(u_diffuseTexture, v_texCoord0).rgb;
#endif

    // Apply anime shading
    vec3 color = animeShade(celStep, baseColor);

    // Slight vibrance boost
    color = clamp(color * 1.2, 0.0, 1.0);

    out_FragColor = vec4(color, u_BaseColorFactor.a);
}
