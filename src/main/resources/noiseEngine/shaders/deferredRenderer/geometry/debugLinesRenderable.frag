#version 330 core

uniform vec3 u_color;
uniform float u_emission;

layout (location = 0) out vec4 out_albedo;
layout (location = 1) out vec3 out_normal;
layout (location = 2) out vec4 out_material;
layout (location = 3) out vec4 out_light;

void main(){
	out_albedo = vec4(u_color, 0.0);
	out_normal = vec3(0.5, 0.5, 0.5);
	out_material = vec4(1.0, 0.0, 0.0, 0.0);
	out_light = vec4(u_color * u_emission, 0.0);
}
