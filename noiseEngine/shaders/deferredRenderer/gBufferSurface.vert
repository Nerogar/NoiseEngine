#version 330 core

uniform mat4 projectionMatrix_N;
uniform mat4 viewMatrix_N;

layout (location = 0) in vec3 position_N;
layout (location = 1) in vec2 uv_N;
layout (location = 2) in vec3 normal_N;

layout (location = 3) in mat4 modelMatrix_N;

out DATA_N
{
	vec4 position;
	vec4 normal;
	vec2 uv;
} vert_out_N;

#parameter surfaceShaderVertex

void main(){
	vert_out_N.normal = modelMatrix_N * vec4(normal_N, 0.0);
	vert_out_N.uv = uv_N;
	vert_out_N.position = modelMatrix_N * vec4(position_N, 1.0);

	mainSurface(vert_out_N.uv, vert_out_N.position, vert_out_N.normal.xyz);

	gl_Position = projectionMatrix_N * viewMatrix_N * vert_out_N.position;
}
