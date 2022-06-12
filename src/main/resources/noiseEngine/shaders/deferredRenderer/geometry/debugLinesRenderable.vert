#version 330 core

uniform vec3 u_v0;
uniform vec3 u_v1;
uniform mat4 u_vMat;
uniform mat4 u_pMat;

layout (location = 0) in float index;

void main(){
	vec4 position = vec4(mix(u_v0, u_v1, index), 1.0);

	gl_Position = u_pMat * u_vMat * position;
}
