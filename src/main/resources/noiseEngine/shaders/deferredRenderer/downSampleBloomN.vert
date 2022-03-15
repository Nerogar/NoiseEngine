#version 330 core

uniform vec2 u_inverseSourceResolution;
uniform vec2 u_padSourceTexture;

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;

out DATA
{
	vec2 uv;
} vert_out;

void main(){
	gl_Position = vec4(position, 0.0, 1.0);
	vert_out.uv = uv + (u_inverseSourceResolution * uv * u_padSourceTexture);
}
