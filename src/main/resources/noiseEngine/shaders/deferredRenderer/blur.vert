#version 330 core

#parameter SAMPLE_COUNT
#parameter SAMPLE_POSITIONS

uniform vec2 u_inverseResolution;
uniform vec2 u_blurDirection;

layout (location = 0) in vec2 position;
layout (location = 1) in vec2 uv;

out DATA
{
	vec2[SAMPLE_COUNT] uv;
} vert_out;

void main(){
	gl_Position = vec4(position, 0.0, 1.0);

	const float[SAMPLE_COUNT] samplePositions = float[](SAMPLE_POSITIONS);

	for (int i = 0; i < SAMPLE_COUNT; i++) {
		vert_out.uv[i] = uv + (u_inverseResolution * u_blurDirection * vec2(samplePositions[i]));
	}
}
