#version 330 core

uniform mat4 projectionMatrix_N;
uniform mat4 viewMatrix_N;

layout (location = 0) in vec3 position_N;
layout (location = 1) in vec2 uv_N;
layout (location = 2) in vec3 normal_N;
layout (location = 3) in vec3 tangent_N;
layout (location = 4) in vec3 bitangent_N;

layout (location = 5) in mat4 modelMatrix_N;
layout (location = 9) in mat3 normalMatrix_N;

out DATA_N
{
	vec4 position;
	vec3 normal;
	vec3 tangent;
	vec3 bitangent;
	vec2 uv;
} vert_out_N;

void mainSurface(inout vec2 uv, inout vec4 position, inout vec3 normal);

#parameter surfaceShaderVertex

void main(){
	vert_out_N.normal = normalize(normalMatrix_N * normal_N);
	vert_out_N.tangent = normalize(normalMatrix_N * tangent_N);
	vert_out_N.bitangent = normalize(normalMatrix_N * bitangent_N);
	vert_out_N.uv = uv_N;
	vert_out_N.position = modelMatrix_N * vec4(position_N, 1.0);

	mainSurface(vert_out_N.uv, vert_out_N.position, vert_out_N.normal.xyz);

	gl_Position = projectionMatrix_N * viewMatrix_N * vert_out_N.position;
}
