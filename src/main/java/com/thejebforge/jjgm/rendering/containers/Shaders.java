package com.thejebforge.jjgm.rendering.containers;

import com.thejebforge.jjgm.rendering.shaders.Shader;

import java.util.ArrayList;
import java.util.List;

public class Shaders {
    private static List<Shader> shaderList = new ArrayList<>();

    public static void addShader(Shader shader){
        shaderList.add(shader);
    }

    public static void destroyAll(){
        for(Shader shader : shaderList){
            shader.destroy();
        }
    }
}
