#version 330 core

uniform sampler2D u_albedoBuffer;
uniform vec2 u_inverseSourceResolution;

layout (location = 0) out vec4 out_color;

in DATA
{
	vec2 uv0;
	vec2 uv1;
	vec2 uv2;
	vec2 uv3;
} frag_in;

void main(){
	vec4 albedoSample0 = texture(u_albedoBuffer, frag_in.uv0);
	vec4 albedoSample1 = texture(u_albedoBuffer, frag_in.uv1);
	vec4 albedoSample2 = texture(u_albedoBuffer, frag_in.uv2);
	vec4 albedoSample3 = texture(u_albedoBuffer, frag_in.uv3);

	out_color.rgb = 0.25 * (
		albedoSample0.rgb * albedoSample0.a +
		albedoSample1.rgb * albedoSample1.a +
		albedoSample2.rgb * albedoSample2.a +
		albedoSample3.rgb * albedoSample3.a) * 10.0;
}
