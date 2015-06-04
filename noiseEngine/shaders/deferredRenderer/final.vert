#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 inverseResolution;

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;

//instance
layout (location = 3) in vec3 transform;

out DATA
{
	vec2 uv;
	vec2 uvNW;
	vec2 uvNE;
	vec2 uvSW;
	vec2 uvSE;
} vert_out;

void main(){
	gl_Position = projectionMatrix * vec4(position, 0.0, 1.0);

	vec3 pixelOffset = vec3(inverseResolution, 0.0);

	vert_out.uv = uv;
	vert_out.uvNW = uv + vec2(1.0, -1.0) * inverseResolution;
	vert_out.uvNE = uv + vec2(1.0, 1.0) * inverseResolution;
	vert_out.uvSW = uv + vec2(-1.0, -1.0) * inverseResolution;
	vert_out.uvSE = uv + vec2(-1.0, 1.0) * inverseResolution;
}
