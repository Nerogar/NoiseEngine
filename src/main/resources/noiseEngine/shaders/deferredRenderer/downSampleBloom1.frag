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

	vec3 light0 = lightSample0.rgb * lightSample0.a;
	vec3 light1 = lightSample1.rgb * lightSample1.a;
	vec3 light2 = lightSample2.rgb * lightSample2.a;
	vec3 light3 = lightSample3.rgb * lightSample3.a;

	float lum0 = luminance(light0);
	float lum1 = luminance(light1);
	float lum2 = luminance(light2);
	float lum3 = luminance(light3);

	float bloomIntensity0 = max(0.0, lum0 - 1.0);
	float bloomIntensity1 = max(0.0, lum1 - 1.0);
	float bloomIntensity2 = max(0.0, lum2 - 1.0);
	float bloomIntensity3 = max(0.0, lum3 - 1.0);

	vec3 color = 0.25 * (
		bloomIntensity0 * light0 +
		bloomIntensity1 * light1 +
		bloomIntensity2 * light2 +
		bloomIntensity3 * light3
	);

	out_color.rgb = color;
}
