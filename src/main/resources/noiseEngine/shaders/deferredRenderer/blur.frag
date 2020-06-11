#version 330 core

#parameter SAMPLE_COUNT
#parameter SAMPLE_WEIGHTS

uniform sampler2D u_sourceBuffer;

layout (location = 0) out vec4 out_color;

in DATA
{
	vec2[SAMPLE_COUNT] uv;
} frag_in;

void main(){
	vec3 color = vec3(0.0);

	const float[SAMPLE_COUNT] sampleWeights = float[](SAMPLE_WEIGHTS);
	for (int i = 0; i < SAMPLE_COUNT; i++) {
		color += texture(u_sourceBuffer, frag_in.uv[i]).rgb * sampleWeights[i];
	}

	out_color.rgb = color;
}
