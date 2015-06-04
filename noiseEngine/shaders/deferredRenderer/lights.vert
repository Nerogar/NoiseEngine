#version 330 core

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec2 inverseResolution;

layout (location = 0) in vec3 position;

layout (location = 1) in vec3 lightPos;
layout (location = 2) in vec3 lightColor;
layout (location = 3) in float lightReach;
layout (location = 4) in float lightIntensity;

out DATA
{
	vec3 position;
	vec3 color;
	float reach;
	float intensity;
} vert_out;

void main(){
	gl_Position = projectionMatrix * viewMatrix * vec4(position * lightReach + lightPos, 1.0);

	vert_out.position = lightPos;
	vert_out.color = lightColor;
	vert_out.reach = lightReach;
	vert_out.intensity = lightIntensity;
}
