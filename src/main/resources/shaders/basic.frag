#version 400 core

in vec2 uv;
out vec4 out_Color;

uniform sampler2D texSample;

void main(void){
    out_Color = texture(texSample, uv);
}