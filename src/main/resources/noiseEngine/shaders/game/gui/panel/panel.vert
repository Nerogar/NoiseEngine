#version 330 core

uniform mat4 projectionMatrix;
uniform vec2 position;
uniform vec2 size;

layout (location = 0) in vec2 vert_position;

void main(){
	gl_Position = projectionMatrix * vec4(vert_position * size + position, 0.0, 1.0);
}
