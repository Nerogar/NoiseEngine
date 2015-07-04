#version 330 core

uniform sampler2D textureColor_N;
uniform sampler2D textureNormal_N;
uniform sampler2D textureLight_N;

layout (location = 0) out vec4 color_out_N;
layout (location = 1) out vec4 normal_out_N;
layout (location = 2) out vec4 position_out_N;
layout (location = 3) out vec4 light_out_N; //ambient, reflection

in DATA_N
{
	vec4 position;
	vec4 normal;
	vec2 uv;
} frag_in_N;

#parameter surfaceShaderFragment

void main(){
	color_out_N = texture(textureColor_N, frag_in_N.uv);

	vec3 normalSample = texture(textureNormal_N, frag_in_N.uv).xyz;
	normal_out_N = normalize(frag_in_N.normal);

	position_out_N = frag_in_N.position;

	light_out_N = texture(textureLight_N, frag_in_N.uv);

	mainSurface(color_out_N, frag_in_N.uv, position_out_N, normal_out_N.xyz, light_out_N);
}
