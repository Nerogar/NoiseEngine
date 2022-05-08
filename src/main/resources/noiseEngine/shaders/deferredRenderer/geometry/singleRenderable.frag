#version 330 core

uniform sampler2D u_albedoTexture;
uniform sampler2D u_normalTexture;
uniform sampler2D u_materialTexture;

layout (location = 0) out vec4 out_albedo;
layout (location = 1) out vec3 out_normal;
layout (location = 2) out vec4 out_material;
layout (location = 3) out vec4 out_light;

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
	vec4 albedo_sample = texture(u_albedoTexture, frag_in.uv);

	if (albedo_sample.a < 0.9) {
		discard;
	}

	out_albedo = vec4(albedo_sample.rgb, 0.0);
	normalSample = normalSample * vec3(2.0) - vec3(1.0);
	out_normal = vec3((normalize(worldSpaceMat * normalSample) + vec3(1.0)) * vec3(0.5));
	out_material = vec4(1.0, 0.0, 0.9, 0.02);
	out_light = vec4(0.0);
}
