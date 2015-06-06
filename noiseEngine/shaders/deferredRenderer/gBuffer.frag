#version 330 core

uniform sampler2D textureColor;
uniform sampler2D textureNormal;
uniform sampler2D textureLight;

layout (location = 0) out vec4 color;
layout (location = 1) out vec4 normal;
layout (location = 2) out vec4 position;
layout (location = 3) out vec4 light; //ambient, reflection

in DATA
{
	vec4 position;
	vec4 normal;
	vec2 uv;
} frag_in;

void main(){
	color = texture2D(textureColor, frag_in.uv);

	vec3 normalSample = texture2D(textureNormal, frag_in.uv).xyz;
	normal = frag_in.normal;

	position = frag_in.position;

	light = texture2D(textureLight, frag_in.uv);
}