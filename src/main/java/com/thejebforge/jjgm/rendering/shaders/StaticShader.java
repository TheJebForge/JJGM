package com.thejebforge.jjgm.rendering.shaders;

public class StaticShader extends Shader {

    private static final String VERTEX_FILE = "shaders/basic.vert";
    private static final String FRAGMENT_FILE = "shaders/basic.frag";

    public StaticShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "normal");
    }
}
