#version 330 core

#include ../positionReconstruction.glsl

uniform sampler2D u_depthBuffer;
uniform sampler2D u_normalBuffer;
uniform sampler2D u_materialBuffer;

uniform vec3 u_position;
uniform vec3 u_color;
uniform float u_radius;
uniform float u_strength;

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

void main() {
	vec2 uv = gl_FragCoord.xy * u_inverseResolution;

	vec3 normal = texture(u_normalBuffer, uv).xyz * 2.0 - vec3(1.0);
	vec4 material = texture(u_materialBuffer, uv);
	vec3 position = getPositionReconstruct(
		u_depthBuffer, u_inverseDepthFunction,
		u_unitRayCenterStart, u_unitRayCenterDir, u_unitRayRightStart, u_unitRayRightDir, u_unitRayTopStart, u_unitRayTopDir,
		uv
	);

	float distance = length(position - u_position);

	// falloff computed by the inverted distace ^ 4
	// good tradeoff between performance and quality
	float distanceGradient = max(0.0, ((u_radius - distance) / u_radius));
	//distanceGradient = distanceGradient * distanceGradient;
    float strength = distanceGradient * distanceGradient * u_strength;

    vec3 direction = normalize(position - u_position);

	out_light.rgb = max(0.0, dot(-direction, normal)) * u_color * u_strength * strength;
}
