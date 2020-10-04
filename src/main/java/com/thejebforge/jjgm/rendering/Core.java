package com.thejebforge.jjgm.rendering;

import static org.lwjgl.glfw.GLFW.*;

public class Core {
    public static boolean initHappened = false;

    public static void init(){
        if(initHappened) return;

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        initHappened = true;
    }

    public static void pollEvents(){
        glfwPollEvents();
    }
}
