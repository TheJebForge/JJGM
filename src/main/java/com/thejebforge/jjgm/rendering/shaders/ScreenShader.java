package com.thejebforge.jjgm.rendering.shaders;

public class ScreenShader extends Shader {

    private static final String VERTEX_FILE = "shaders/screen.vert";
    private static final String FRAGMENT_FILE = "shaders/screen.frag";

    public ScreenShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    @Override
    protected void bindAttributes() {
        bindAttribute(0, "position");
        bindAttribute(1, "textureCoords");
        bindAttribute(2, "normal");
    }
}
