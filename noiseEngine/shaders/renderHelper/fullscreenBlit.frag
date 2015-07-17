#version 330 core

uniform sampler2D blitTexture;

layout (location = 0) out vec4 color;

in DATA
{
	vec2 uv;
} frag_in;

void main(){
	color  = texture(blitTexture, frag_in.uv);
}
