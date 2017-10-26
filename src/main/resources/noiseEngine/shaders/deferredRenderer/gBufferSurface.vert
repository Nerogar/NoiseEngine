#version 330 core

uniform mat4 projectionMatrix_N;
uniform mat4 viewMatrix_N;

layout (location = 0) in vec3 position_N;
layout (location = 1) in vec2 uv_N;
layout (location = 2) in vec3 normal_N;
layout (location = 3) in vec3 tangent_N;
layout (location = 4) in vec3 bitangent_N;

#parameter useUniforms
#if UNIFORM_MATRICES
	uniform mat4 modelMatrix_N;
	uniform mat4 normalMatrix_N;
#else
	layout (location = 5) in mat4 modelMatrix_N;
	layout (location = 9) in mat3 normalMatrix_N;
#endif

out DATA_N
{
	vec3 position;
	vec3 normal;
	vec3 tangent;
	vec3 bitangent;
	vec2 uv;
} vert_out_N;

void mainSurface(inout vec2 uv, inout vec3 position, inout vec3 normal);

#pinclude surfaceShaderVertex

void main(){
	mat3 normalMatrix3x3_N = mat3(normalMatrix_N);
	vert_out_N.normal = normalize(normalMatrix3x3_N * normal_N);
	vert_out_N.tangent = normalize(normalMatrix3x3_N * tangent_N);
	vert_out_N.bitangent = normalize(normalMatrix3x3_N * bitangent_N);
	vert_out_N.uv = uv_N;
	vert_out_N.position = (modelMatrix_N * vec4(position_N, 1.0)).xyz;

	mainSurface(vert_out_N.uv, vert_out_N.position, vert_out_N.normal);

	gl_Position = projectionMatrix_N * viewMatrix_N * vec4(vert_out_N.position, 1.0);
}
