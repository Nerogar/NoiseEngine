#version 330 core

layout (location = 0) in vec2 a_position;
layout (location = 1) in vec2 a_uv;

out DATA
{
	vec2 uv;
} vert_out;

void main(){
	gl_Position = vec4(a_position, 0.0, 1.0);
	vert_out.uv = a_uv;
}
