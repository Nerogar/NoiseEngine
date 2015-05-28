#version 330 core

uniform sampler2D textureColor;

in DATA
{
	vec3 position;
	vec2 uv;
} frag_in;

layout (location = 0) out vec4 color;

void main(){
	vec4 colorSample = texture2D(textureColor, frag_in.uv);

	color = colorSample;
}
