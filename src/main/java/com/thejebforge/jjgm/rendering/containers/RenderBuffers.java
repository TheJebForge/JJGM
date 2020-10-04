package com.thejebforge.jjgm.rendering.containers;

import com.thejebforge.jjgm.rendering.elements.Texture;
import com.thejebforge.jjgm.rendering.utils.RenderBuffer;

import java.util.ArrayList;
import java.util.List;

public class RenderBuffers {
    private static List<RenderBuffer> renderBufferList = new ArrayList<>();

    public static void addRenderBuffer(RenderBuffer renderBuffer){
        renderBufferList.add(renderBuffer);
    }

    public static void destroyAll(){
        for(RenderBuffer renderBuffer : renderBufferList){
            renderBuffer.destroy();
        }
    }
}
