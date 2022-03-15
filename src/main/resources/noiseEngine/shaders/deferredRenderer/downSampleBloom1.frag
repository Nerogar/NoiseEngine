#version 330 core

#include <util/color.glsl>

uniform sampler2D u_lightBuffer;
uniform vec2 u_inverseSourceResolution;

layout (location = 0) out vec4 out_color;

in DATA
{
	vec2 uv0;
	vec2 uv1;
	vec2 uv2;
	vec2 uv3;
} frag_in;

void main(){
	vec4 lightSample0 = texture(u_lightBuffer, frag_in.uv0);
	vec4 lightSample1 = texture(u_lightBuffer, frag_in.uv1);
	vec4 lightSample2 = texture(u_lightBuffer, frag_in.uv2);
	vec4 lightSample3 = texture(u_lightBuffer, frag_in.uv3);

	vec3 color = 0.25 * (
		lightSample0.rgb * lightSample0.a +
		lightSample1.rgb * lightSample1.a +
		lightSample2.rgb * lightSample2.a +
		lightSample3.rgb * lightSample3.a
	);

	float lum = luminance(color);

	float bloomIntensity = max(0.0, lum - 1.0);

	out_color.rgb = color * bloomIntensity;
}
