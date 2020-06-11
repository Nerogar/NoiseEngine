#version 330 core

uniform vec3 u_color;
uniform float u_emission;
uniform float u_shadeless;

layout (location = 0) out vec4 out_albedo;
layout (location = 1) out vec4 out_normal;
layout (location = 2) out vec4 out_material;

void main(){
	out_albedo = vec4(u_color, u_emission);
	out_normal = vec4(0.5, 0.5, 0.5, u_shadeless);
	out_material = vec4(0.0, 0.0, 0.0, 0.0);
}
