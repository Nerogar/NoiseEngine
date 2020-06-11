#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 inverseResolution;

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;

out DATA
{
	vec2 uv;
} vert_out;

void main(){
	vec4 unitCubePosition = projectionMatrix * vec4(position, 0.0, 1.0);
	gl_Position = unitCubePosition;

	vert_out.uv = uv;
}
