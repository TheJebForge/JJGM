package com.thejebforge.jjgm.rendering.elements;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;
import org.joml.*;

import java.lang.Math;

public class Camera {
    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1);

    public float fov = 90;
    public float aspectRatio = 1.5f;
    public float nearPlane = 0.01f;
    public float farPlane = 50f;

    public Camera(float fov, float aspectRatio){
        this.fov = fov;
        this.aspectRatio = aspectRatio;
    }

    public Matrix4f viewMatrix(){
        Matrix4f output = new Matrix4f();
        output.identity();

        Vector3f forward = new Vector3f(0, 0, 1);
        forward.rotate(rotation);

        Vector3f center = new Vector3f();

        position.add(forward, center);

        output.lookAt(position, center, new Vector3f(0, 1, 0));

        return output;
    }

    public Matrix4f projectionMatrix(){
        Matrix4f output = new Matrix4f();
        output.identity();

        float fovy = 2 * (float) Math.atan( Math.tan(Math.toRadians(fov) / 2) / aspectRatio );

        output.perspective(fovy, aspectRatio, nearPlane, farPlane, false);

        return output;
    }


}
