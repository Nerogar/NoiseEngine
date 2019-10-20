#version 330 core

uniform mat4 u_projectionMatrix;
uniform vec2 u_position;
uniform vec2 u_size;

layout (location = 0) in vec2 a_position;
layout (location = 1) in vec2 a_uv;

out DATA
{
	vec2 uv;
} vert_out;

void main(){
	gl_Position = u_projectionMatrix * vec4(a_position * u_size + u_position, 0.0, 1.0);
	vert_out.uv = a_uv;
}
