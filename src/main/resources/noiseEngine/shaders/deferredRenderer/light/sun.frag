#version 330 core

#include <util/color.glsl>
#include ../positionReconstruction.glsl
#include ../brdf.glsl

uniform sampler2D u_depthBuffer;
uniform sampler2D u_albedoBuffer;
uniform sampler2D u_normalBuffer;
uniform sampler2D u_materialBuffer;

uniform vec3 u_direction;
uniform vec3 u_color;
uniform float u_intensity;
uniform vec3 u_cameraPosition;

// position reconstruction
uniform vec3 u_unitRayCenterStart;
uniform vec3 u_unitRayCenterDir;
uniform vec3 u_unitRayRightStart;
uniform vec3 u_unitRayRightDir;
uniform vec3 u_unitRayTopStart;
uniform vec3 u_unitRayTopDir;
uniform vec4 u_inverseDepthFunction;
uniform vec2 u_inverseResolution;

layout (location = 0) out vec4 out_light;

in DATA
{
    vec2 uv;
} frag_in;

void main() {
    vec2 uv = gl_FragCoord.xy * u_inverseResolution;

    vec4 albedoSample = texture(u_albedoBuffer, frag_in.uv);
    vec4 normalSample = texture(u_normalBuffer, frag_in.uv);
    vec4 materialSample = texture(u_materialBuffer, frag_in.uv);
    vec3 position = getPositionReconstruct(
        u_depthBuffer, u_inverseDepthFunction,
        u_unitRayCenterStart, u_unitRayCenterDir, u_unitRayRightStart, u_unitRayRightDir, u_unitRayTopStart, u_unitRayTopDir,
        uv
    );

    vec3 lightVector = -u_direction;
    vec3 normalVector = normalSample.xyz * vec3(2.0) - vec3(1.0);
    vec3 viewVector = normalize(u_cameraPosition - position);

    vec3 albedo = srgbToLinear(albedoSample.rgb);
    float ambientOcclusion = materialSample.r;
    float metalness = materialSample.g;
    float roughness = materialSample.b;
    float reflectivity = materialSample.a;

    vec3 ambient = vec3(0.02, 0.02, 0.05) * albedo * ambientOcclusion;
    vec3 sun = brdf(lightVector, normalVector, viewVector, albedo, metalness, roughness, reflectivity, u_color, u_intensity) * ambientOcclusion;
    out_light.rgb = sun + ambient;
}
