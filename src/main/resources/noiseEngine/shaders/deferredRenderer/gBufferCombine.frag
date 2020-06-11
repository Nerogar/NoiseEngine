#version 330 core

#include positionReconstruction.glsl

uniform sampler2D u_albedoBuffer;
uniform sampler2D u_normalBuffer;
uniform sampler2D u_materialBuffer;
uniform sampler2D u_lightBuffer;
uniform sampler2D u_emissionBuffer1;
uniform sampler2D u_emissionBuffer2;
uniform sampler2D u_emissionBuffer3;
uniform sampler2D u_emissionBuffer4;

layout (location = 0) out vec4 out_color;

in DATA
{
    vec2 uv;
} frag_in;

void main(){
    vec4 albedoSample = texture(u_albedoBuffer, frag_in.uv);
    vec3 albedo = albedoSample.rgb;
    float emission = albedoSample.a * 20.0;
    vec4 normalSample = texture(u_normalBuffer, frag_in.uv);
    vec3 normal = normalSample.xyz;
    float shadeless = normalSample.w;
    vec4 material = texture(u_materialBuffer, frag_in.uv);
    vec3 light = texture(u_lightBuffer, frag_in.uv).rgb;

    float ambient = 0.1;
    light = pow(max(vec3(ambient), light), vec3(1.0 / 2.2));

    vec3 color = mix(albedo * light, albedo, shadeless) * (1.0 + emission);

    // bloom test
    vec3 emission1 = texture(u_emissionBuffer1, frag_in.uv).rgb;
    vec3 emission2 = texture(u_emissionBuffer2, frag_in.uv).rgb;
    vec3 emission3 = texture(u_emissionBuffer3, frag_in.uv).rgb;
    vec3 emission4 = texture(u_emissionBuffer4, frag_in.uv).rgb;

    out_color.rgb = color + emission1 + emission2 + emission3 + emission4;
}
