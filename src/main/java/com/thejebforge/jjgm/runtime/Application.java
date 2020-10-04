package com.thejebforge.jjgm.runtime;

import com.thejebforge.jjgm.rendering.containers.Models;
import com.thejebforge.jjgm.rendering.containers.Shaders;
import com.thejebforge.jjgm.rendering.*;
import com.thejebforge.jjgm.rendering.elements.Camera;
import com.thejebforge.jjgm.rendering.elements.Model;
import com.thejebforge.jjgm.rendering.elements.Texture;
import com.thejebforge.jjgm.rendering.elements.Window;
import com.thejebforge.jjgm.rendering.shaders.StaticShader;
import com.thejebforge.jjgm.util.TimeUtil;
import org.joml.Vector3f;

public class Application {
    public static void main(String[] args){
        Core.init();

        double startTime = TimeUtil.getClock();

        Window main = new Window("Lol");
        main.setVisibility(true);
        main.requestFocus();

        Renderer mainRender = new Renderer(main);

        Camera camera = new Camera(90, main.getAspectRatio());

        mainRender.mainCamera = camera;

        camera.position.z = -30;

        Texture plank = new Texture("textures/plank.jpg");

        Model quad = new Model("models/vikingboat.fbx");


        quad.addShader(new StaticShader());

        mainRender.addModel(quad);

        while(!main.isWindowCloseRequested()){
            quad.position.x = (float)(Math.sin(TimeUtil.timeSince(startTime)));
            quad.position.y = (float)(Math.sin(TimeUtil.timeSince(startTime) * 1.5 + 5) * 2);
            quad.position.z = (float)(Math.sin(TimeUtil.timeSince(startTime) * 1.5 + 2.5) * 2);

            quad.rotation.rotateAxis((float)(TimeUtil.deltaTime()), new Vector3f(0, 1, 0));

            mainRender.draw();
            Core.pollEvents();
        }

        Models.destroyAll();
        Shaders.destroyAll();
        main.destroy();
    }
}
