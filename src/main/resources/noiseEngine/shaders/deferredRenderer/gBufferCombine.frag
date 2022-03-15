#version 330 core

#include <util/color.glsl>
#include positionReconstruction.glsl
#parameter BLOOM_TEXTURE_COUNT

uniform sampler2D u_lightBuffer;

#if BLOOM_TEXTURE_COUNT > 0
uniform sampler2D u_bloomBuffer1;
#endif
#if BLOOM_TEXTURE_COUNT > 1
uniform sampler2D u_bloomBuffer2;
#endif
#if BLOOM_TEXTURE_COUNT > 2
uniform sampler2D u_bloomBuffer3;
#endif
#if BLOOM_TEXTURE_COUNT > 3
uniform sampler2D u_bloomBuffer4;
#endif
#if BLOOM_TEXTURE_COUNT > 4
uniform sampler2D u_bloomBuffer5;
#endif
#if BLOOM_TEXTURE_COUNT > 5
uniform sampler2D u_bloomBuffer6;
#endif

layout (location = 0) out vec4 out_color;

in DATA
{
    vec2 uv;
} frag_in;

void main(){
    // input data
    //float emissionStrength = albedoSample.a * 20.0;

    vec3 light = texture(u_lightBuffer, frag_in.uv).rgb;

    vec3 color = linearToSrgb(light);

    // add bloom to the final output
    #if BLOOM_TEXTURE_COUNT > 0
        vec3 bloom1 = texture(u_bloomBuffer1, frag_in.uv).rgb;
        color += bloom1;
    #endif
    #if BLOOM_TEXTURE_COUNT > 1
        vec3 bloom2 = texture(u_bloomBuffer2, frag_in.uv).rgb;
        color += bloom2;
    #endif
    #if BLOOM_TEXTURE_COUNT > 2
        vec3 bloom3 = texture(u_bloomBuffer3, frag_in.uv).rgb;
        color += bloom3;
    #endif
    #if BLOOM_TEXTURE_COUNT > 3
        vec3 bloom4 = texture(u_bloomBuffer4, frag_in.uv).rgb;
        color += bloom4;
    #endif
    #if BLOOM_TEXTURE_COUNT > 4
        vec3 bloom5 = texture(u_bloomBuffer5, frag_in.uv).rgb;
        color += bloom5;
    #endif
    #if BLOOM_TEXTURE_COUNT > 5
        vec3 bloom6 = texture(u_bloomBuffer6, frag_in.uv).rgb;
        color += bloom6;
    #endif

    out_color.rgb = color;
}
