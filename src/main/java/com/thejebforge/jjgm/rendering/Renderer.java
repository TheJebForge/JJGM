package com.thejebforge.jjgm.rendering;

import com.thejebforge.jjgm.rendering.elements.Camera;
import com.thejebforge.jjgm.rendering.elements.Model;
import com.thejebforge.jjgm.rendering.elements.Window;
import com.thejebforge.jjgm.rendering.shaders.Shader;
import com.thejebforge.jjgm.rendering.utils.RenderBuffer;
import com.thejebforge.jjgm.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private Window window;
    private double startTime;
    private List<Model> models;

    public Camera mainCamera;

    private RenderBuffer test;
    private RenderBuffer transparent;
    private Shader red;

    public Renderer(Window window){
        this.window = window;
        startTime = TimeUtil.getClock();
        models = new ArrayList<>();

        test = new RenderBuffer(window);
        transparent = new RenderBuffer(window);
        red = new Shader("shaders/basicred.vert", "shaders/basicred.frag") {
            @Override
            protected void bindAttributes() {
                bindAttribute(0, "position");
                bindAttribute(1, "textureCoords");
                bindAttribute(2, "normal");
            }
        };
    }

    public void addModel(Model model){
        models.add(model);
    }

    public void draw(){
        window.makeActive();

        glEnable(GL_DEPTH_TEST);

        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);
        glFrontFace(GL_CCW);

        test.enableBuffer();

        glClearColor(0, 0, 0, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        for(Model model : models){
            for(Shader shader : model.getShaders()){
                shader.setUniform("time", (float)TimeUtil.timeSince(startTime));
                shader.setUniform("projection", mainCamera.projectionMatrix());
                shader.setUniform("view", mainCamera.viewMatrix());
                shader.setUniform("transform", model.getTransformMatrix());

                shader.enableShader();
                drawModel(model);
            }
        }

        test.disableBuffer();

        //transparent.enableBuffer();

//        glClearColor(0, 0, 0, 0);
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
//
//        for(Model model : models){
//            red.setUniform("time", (float)TimeUtil.timeSince(startTime));
//            red.setUniform("projection", mainCamera.projectionMatrix());
//            red.setUniform("view", mainCamera.viewMatrix());
//            red.setUniform("transform", model.getTransformMatrix());
//
//            //red.enableShader();
//            drawModel(model);
//        }

        //transparent.disableBuffer();

        test.drawBuffer();

        window.swap();
    }



    private void drawModel(Model model){
        glBindVertexArray(model.getVaoID());

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        if(model.getTexture() != null){
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, model.getTexture().getTextureID());
        }

        glDrawElements(GL_TRIANGLES, model.getVertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public double getStartTime() {
        return startTime;
    }

    public void setStartTime(double startTime) {
        this.startTime = startTime;
    }

}
