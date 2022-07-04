#version 330 core

#include <util/color.glsl>

uniform sampler2D u_inColor;

layout (location = 0) out vec4 out_color;

in DATA
{
    vec2 uv;
} frag_in;

void main(){
    vec4 color = texture(u_inColor, frag_in.uv);
    out_color = color;
}
