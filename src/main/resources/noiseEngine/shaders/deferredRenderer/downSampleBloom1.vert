#version 330 core

uniform vec2 u_inverseSourceResolution;
uniform vec2 u_padSourceTexture;

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;

out DATA
{
	vec2 uv0;
	vec2 uv1;
	vec2 uv2;
	vec2 uv3;
} vert_out;

void main(){
	gl_Position = vec4(position, 0.0, 1.0);

	// uv + (correction for un-even texture scaling) + (offset to sample the center of each texel)
	vert_out.uv0 = uv + (u_inverseSourceResolution * uv * u_padSourceTexture) + (u_inverseSourceResolution * vec2(-0.5, -0.5));
	vert_out.uv1 = uv + (u_inverseSourceResolution * uv * u_padSourceTexture) + (u_inverseSourceResolution * vec2(-0.5, 0.5));
	vert_out.uv2 = uv + (u_inverseSourceResolution * uv * u_padSourceTexture) + (u_inverseSourceResolution * vec2(0.5, -0.5));
	vert_out.uv3 = uv + (u_inverseSourceResolution * uv * u_padSourceTexture) + (u_inverseSourceResolution * vec2(0.5, 0.5));
}
