#version 330 core

uniform mat4 u_mMat;
uniform mat4 u_vpMat;
uniform vec3 u_position;
uniform float u_radius;

layout (location = 0) in vec3 a_position;

void main() {
	gl_Position = u_vpMat * u_mMat * vec4(a_position, 1.0);
}
