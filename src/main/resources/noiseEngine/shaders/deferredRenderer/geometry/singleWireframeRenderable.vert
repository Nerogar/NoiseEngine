#version 330 core

uniform mat4 u_mMat;
uniform mat4 u_vMat;
uniform mat4 u_pMat;

layout (location = 0) in vec3 position;

void main(){
	gl_Position = u_pMat * u_vMat * u_mMat * vec4(position, 1.0);
}
