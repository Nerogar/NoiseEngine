#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 pointSize;
uniform vec2 offset;

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 uv;

out DATA
{
	vec2 uv;
} vert_out;

void main(){
	gl_Position = projectionMatrix * vec4(position.xy / pointSize + offset / pointSize, 0.0, 1.0);

	vert_out.uv = uv;
}
