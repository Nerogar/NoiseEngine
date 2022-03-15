#version 330 core

uniform sampler2D u_bloomBuffer;
uniform vec2 u_inverseSourceResolution;

layout (location = 0) out vec4 out_color;

in DATA
{
	vec2 uv;
} frag_in;

void main(){
	vec4 albedoSample = texture(u_bloomBuffer, frag_in.uv);
	vec3 color = albedoSample.rgb;
	float emission = albedoSample.a;
	out_color.rgb = color;
}
