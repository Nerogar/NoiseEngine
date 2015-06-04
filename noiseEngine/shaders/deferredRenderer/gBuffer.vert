#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;
layout (location = 2) in vec3 normal;

layout (location = 3) in mat4 modelMatrix;

out DATA
{
	vec4 position;
	vec4 normal;
	vec2 uv;
} vert_out;

void main(){
	vert_out.position = modelMatrix * vec4(position, 1.0);
	gl_Position = projectionMatrix * viewMatrix * vert_out.position;

	vert_out.normal = modelMatrix * vec4(normal, 0.0);
	vert_out.uv = uv;
}
