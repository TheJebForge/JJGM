package com.thejebforge.jjgm.rendering.containers;

import com.thejebforge.jjgm.rendering.elements.Texture;

import java.util.ArrayList;
import java.util.List;

public class Textures {
    private static List<Texture> textureList = new ArrayList<>();

    public static void addTexture(Texture texture){
        textureList.add(texture);
    }

    public static void destroyAll(){
        for(Texture texture : textureList){
            texture.destroy();
        }
    }
}
