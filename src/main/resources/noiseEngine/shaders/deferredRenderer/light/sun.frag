#version 330 core

uniform sampler2D u_normalBuffer;
uniform sampler2D u_materialBuffer;

uniform vec3 u_direction;
uniform vec3 u_color;
uniform float u_strength;

layout (location = 0) out vec4 out_light;

in DATA
{
    vec2 uv;
} frag_in;

void main() {
    vec3 normal = texture(u_normalBuffer, frag_in.uv).xyz * 2.0 - vec3(1.0);
    vec4 material = texture(u_materialBuffer, frag_in.uv);

    out_light.rgb = max(0.0, dot(-u_direction, normal)) * u_color * u_strength;
}
