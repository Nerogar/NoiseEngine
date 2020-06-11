#version 330 core

uniform sampler2D textureColor_N;
uniform sampler2D textureNormal_N;
uniform sampler2D textureLight_N;

layout (location = 0) out vec4 color_out_N;
layout (location = 1) out vec4 normal_out_N;
layout (location = 2) out vec4 light_out_N; //ambient, reflection

in DATA_N
{
	vec3 normal;
	vec3 tangent;
	vec3 bitangent;
	vec2 uv;
} frag_in_N;

void main(){
	color_out_N = texture(textureColor_N, frag_in_N.uv);

	//if(color_out_N.a == 0.0) discard;

	vec3 normalSample = texture(textureNormal_N, frag_in_N.uv).xyz;
	mat3 worldSpaceMat = mat3(frag_in_N.tangent, frag_in_N.bitangent, frag_in_N.normal);
	normalSample = normalSample * 2.0 - 1.0;
	normal_out_N.xyz = normalize(worldSpaceMat * normalSample);
	normal_out_N.xyz = (normal_out_N.xyz + 1.0) * 0.5;

	light_out_N = texture(textureLight_N, frag_in_N.uv);

	/*color_out_N = texture(textureColor_N, frag_in_N.uv);
	normal_out_N.xyz = frag_in_N.normal;
	light_out_N = texture(textureLight_N, frag_in_N.uv);*/
}
