package com.thejebforge.jjgm.rendering.containers;

import com.thejebforge.jjgm.rendering.elements.Model;

import java.util.ArrayList;
import java.util.List;

public class Models {
    private static List<Model> modelList = new ArrayList<>();

    public static void addModel(Model model){
        modelList.add(model);
    }

    public static void destroyAll(){
        for(Model model : modelList){
            model.destroy();
        }
    }
}
