#version 330 core

uniform mat4 u_mMat;
uniform mat4 u_nMat;
uniform mat4 u_vMat;
uniform mat4 u_pMat;

layout (location = 0) in vec3 position;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec3 tangent;
layout (location = 3) in vec3 bitangent;
layout (location = 4) in vec2 uv;

out DATA
{
	vec3 normal;
	vec3 tangent;
	vec3 bitangent;
	vec2 uv;
} vert_out;

void main(){
	mat3 normalMatrix3x3 = mat3(u_nMat);
	vert_out.normal = normalize(normalMatrix3x3 * normal);
	vert_out.tangent = normalize(normalMatrix3x3 * tangent);
	vert_out.bitangent = normalize(normalMatrix3x3 * bitangent);
	vert_out.uv = uv;

	gl_Position = u_pMat * u_vMat * u_mMat * vec4(position, 1.0);
}
