#version 330 core

uniform sampler2D u_texture;
uniform vec4 u_color;

layout (location = 0) out vec4 frag_color;

in DATA
{
	vec2 uv;
} frag_in;

void main(){
	frag_color = u_color * texture(u_texture, frag_in.uv);
}
