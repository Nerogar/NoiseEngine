#version 330 core

uniform mat4 projectionMatrix;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;

out DATA
{
	vec3 color;
} vert_out;

void main(){
	gl_Position = projectionMatrix * vec4(position, 1.0);

	vert_out.color = color;
}
