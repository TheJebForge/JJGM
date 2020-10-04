#version 400 core

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

uniform float time;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 transform;

out vec2 uv;

void main(void){
    gl_Position = projection * view * transform * vec4(position, 1.0);
    uv = textureCoords;
}