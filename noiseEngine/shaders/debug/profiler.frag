#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec3 color;
} frag_in;

void main(){
	color = vec4(frag_in.color, 0.0);
}
