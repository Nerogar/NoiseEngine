#version 330 core

uniform sampler2D fontSheet;
uniform vec4 fontColor;

layout (location = 0) out vec4 color;

in DATA
{
	vec2 uv;
} frag_in;

void main(){
	color  = fontColor * texture(fontSheet, frag_in.uv);
}
