package com.thejebforge.jjgm.rendering.elements;

import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    long window;
    public int width;
    public int height;

    public Window(String title){
        this (1200, 800, title, glfwGetPrimaryMonitor());
    }

    public Window(int width, int height, String title){
        this (width, height, title, glfwGetPrimaryMonitor());
    }

    public Window(int width, int height, String title, long monitor){
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, 0);
        glfwWindowHint(GLFW_RESIZABLE, 0);

        this.width = width;
        this.height = height;

        window = glfwCreateWindow(width, height, title, NULL, NULL);
        if(window == NULL)
            throw new RuntimeException("Failed to create GLFW window");

//        try ( MemoryStack stack = stackPush() ) {
//            IntBuffer pWidth = stack.mallocInt(1); // int*
//            IntBuffer pHeight = stack.mallocInt(1); // int*
//
//            glfwGetWindowSize(window, pWidth, pHeight);
//
//            GLFWVidMode vidmode = glfwGetVideoMode(monitor);
//
//            glfwSetWindowPos(
//                    window,
//                    (vidmode.width() - pWidth.get(0)) / 2,
//                    (vidmode.height() - pHeight.get(0)) / 2
//            );
//        }

        makeActive();

        GL.createCapabilities();
    }

    public float getAspectRatio(){
        return (float) width / (float) height;
    }

    public void swap(){
        glfwSwapBuffers(window);
    }

    public void makeActive(){
        glfwSwapInterval(1);
        glfwMakeContextCurrent(window);
    }

    public void requestFocus(){
        glfwFocusWindow(window);
    }

    public void setVisibility(boolean state){
        if(state) glfwShowWindow(window);
        else glfwHideWindow(window);
    }

    public boolean isWindowCloseRequested(){
        return glfwWindowShouldClose(window);
    }

    public void destroy(){
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }
}
