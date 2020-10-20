package com.thejebforge.jjgm.rendering;

import com.thejebforge.jjgm.rendering.elements.Camera;
import com.thejebforge.jjgm.rendering.elements.Model;
import com.thejebforge.jjgm.rendering.elements.Window;
import com.thejebforge.jjgm.rendering.shaders.Shader;
import com.thejebforge.jjgm.rendering.utils.RenderBuffer;
import com.thejebforge.jjgm.util.TimeUtil;
import org.lwjgl.opengl.GLDebugMessageCallback;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.KHRDebug.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Renderer {
    private Window window;
    private double startTime;
    private List<Model> models;

    public Camera mainCamera;

    private RenderBuffer test;
    private RenderBuffer transparent;
    private Shader red;

    private boolean callBackCreated = false;

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

        System.out.println("FRAME START ------------------------------------------");


        glEnable(GL_DEBUG_OUTPUT);

        if(!callBackCreated) {
            glDebugMessageCallback(new GLDebugMessageCallback() {
                @Override
                public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
                    String sourceStr = "UNKNOWN";

                    switch (source) {
                        case GL_DEBUG_SOURCE_API:
                            sourceStr = "API";
                        case GL_DEBUG_SOURCE_WINDOW_SYSTEM:
                            sourceStr = "WINDOW SYSTEM";
                        case GL_DEBUG_SOURCE_SHADER_COMPILER:
                            sourceStr = "SHADER COMPILER";
                        case GL_DEBUG_SOURCE_THIRD_PARTY:
                            sourceStr = "THIRD PARTY";
                        case GL_DEBUG_SOURCE_APPLICATION:
                            sourceStr = "APPLICATION";
                        case GL_DEBUG_SOURCE_OTHER:
                            sourceStr = "OTHER";
                    }

                    String typeStr = "UNKNOWN";

                    switch (type)
                    {
                        case GL_DEBUG_TYPE_ERROR: typeStr = "ERROR";
                        case GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR: typeStr = "DEPRECATED_BEHAVIOR";
                        case GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR: typeStr = "UNDEFINED_BEHAVIOR";
                        case GL_DEBUG_TYPE_PORTABILITY: typeStr = "PORTABILITY";
                        case GL_DEBUG_TYPE_PERFORMANCE: typeStr = "PERFORMANCE";
                        case GL_DEBUG_TYPE_MARKER: typeStr = "MARKER";
                        case GL_DEBUG_TYPE_OTHER: typeStr = "OTHER";
                    }

                    String severityStr = "UNKNOWN";

                    switch (severity) {
                        case GL_DEBUG_SEVERITY_NOTIFICATION: severityStr = "NOTIFICATION";
                        case GL_DEBUG_SEVERITY_LOW: severityStr = "LOW";
                        case GL_DEBUG_SEVERITY_MEDIUM: severityStr = "MEDIUM";
                        case GL_DEBUG_SEVERITY_HIGH: severityStr = "HIGH";
                    }

                    System.out.println("["+sourceStr+"] "+typeStr+" "+severityStr+" id:"+id+" : "+ memUTF8(memByteBuffer(message, length)));
                }
            }, 0);

            callBackCreated = true;
        }

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


        System.out.println("FRAME END ------------------------------------------");

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
