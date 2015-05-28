#version 330 core

layout (location = 0) in vec3 pos;
layout (location = 1) in vec2 uv;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

out DATA
{
	vec3 position;
	vec2 uv;
} vert_out;

void main(){
	gl_Position = projectionMatrix * viewMatrix * vec4(pos, 1.0);
	//gl_Position = projectionMatrix * vec4(pos, 1.0);
	vert_out.position = pos;
	vert_out.uv = uv;
}
