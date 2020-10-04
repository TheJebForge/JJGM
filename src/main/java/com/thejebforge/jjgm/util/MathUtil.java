package com.thejebforge.jjgm.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MathUtil {
    public static Matrix4f transformMatrix(Vector3f pos, Quaternionf rot, Vector3f scale){
        Matrix4f matrix = new Matrix4f();
        matrix.identity();

        matrix.translate(pos);
        matrix.rotate(rot);
        matrix.scale(scale);

        return matrix;
    }
}
