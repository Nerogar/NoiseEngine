#version 330 core

uniform mat4 u_vpMat;
uniform vec3 u_position;
uniform float u_radius;

layout (location = 0) in vec3 a_position;

void main() {
	gl_Position = u_vpMat * vec4(u_position + (a_position * u_radius), 1.0);
}
