#version 330 core

uniform sampler2D u_albedoTexture;
uniform sampler2D u_normalTexture;
uniform sampler2D u_materialTexture;

layout (location = 0) out vec4 out_albedo;
layout (location = 1) out vec4 out_normal;
layout (location = 2) out vec4 out_material;

in DATA
{
	vec3 normal;
	vec3 tangent;
	vec3 bitangent;
	vec2 uv;
} frag_in;

void main(){
	vec3 normalSample = texture(u_normalTexture, frag_in.uv).xyz;
	mat3 worldSpaceMat = mat3(frag_in.tangent, frag_in.bitangent, frag_in.normal);

	out_albedo = vec4(texture(u_albedoTexture, frag_in.uv).rgb, 0.0);
	normalSample = normalSample * vec3(2.0) - vec3(1.0);
	out_normal = vec4((normalize(worldSpaceMat * normalSample) + vec3(1.0)) * vec3(0.5), 0.0);
}
