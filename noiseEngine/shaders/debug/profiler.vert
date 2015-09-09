#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 cornerSmall;
uniform vec2 cornerBig;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 color;

out DATA
{
	vec3 color;
} vert_out;

void main(){
	vec2 pos = position.xy * (cornerBig - cornerSmall) + cornerSmall;
	gl_Position = projectionMatrix * vec4(pos, 0.0, 1.0);

	vert_out.color = color;
}
