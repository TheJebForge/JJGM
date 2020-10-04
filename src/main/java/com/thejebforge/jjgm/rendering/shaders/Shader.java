package com.thejebforge.jjgm.rendering.shaders;

import com.thejebforge.jjgm.rendering.containers.Shaders;
import org.joml.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL30.*;

public abstract class Shader {
    private int programID;
    private int vertexID;
    private int fragmentID;

    public Shader(String vertexFile, String fragmentFile){
        Shaders.addShader(this);

        vertexID = compileShader(readShader(vertexFile), GL_VERTEX_SHADER);
        fragmentID = compileShader(readShader(fragmentFile), GL_FRAGMENT_SHADER);

        programID = glCreateProgram();
        glAttachShader(programID, vertexID);
        glAttachShader(programID, fragmentID);

        bindAttributes();

        glLinkProgram(programID);
        glValidateProgram(programID);


    }

    protected abstract void bindAttributes();

    protected void bindAttribute(int attribute, String variableName){
        glBindAttribLocation(programID, attribute, variableName);
    }

    public void setUniform(String name, float value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform1f(location, value);
        }
    }

    public void setUniform(String name, float[] value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            if(value.length == 2)
                glUniform2f(location, value[0], value[1]);
            if(value.length == 3)
                glUniform3f(location, value[0], value[1], value[2]);
            if(value.length == 4)
                glUniform4f(location, value[0], value[1], value[2], value[3]);
        }
    }

    public void setUniform(String name, int value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform1i(location, value);
        }
    }

    public void setUniform(String name, int[] value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            if(value.length == 2)
                glUniform2i(location, value[0], value[1]);
            if(value.length == 3)
                glUniform3i(location, value[0], value[1], value[2]);
            if(value.length == 4)
                glUniform4i(location, value[0], value[1], value[2], value[3]);
        }
    }

    public void setUniform(String name, boolean value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform1i(location, value ? 1 : 0);
        }
    }

    public void setUniform(String name, boolean[] value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            if(value.length == 2)
                glUniform2i(location, value[0] ? 1 : 0, value[1] ? 1 : 0);
            if(value.length == 3)
                glUniform3i(location, value[0] ? 1 : 0, value[1] ? 1 : 0, value[2] ? 1 : 0);
            if(value.length == 4)
                glUniform4i(location, value[0] ? 1 : 0, value[1] ? 1 : 0, value[2] ? 1 : 0, value[3] ? 1 : 0);
        }
    }

    public void setUniform(String name, Vector2f value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform2f(location, value.x, value.y);
        }
    }

    public void setUniform(String name, Vector3f value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform3f(location, value.x, value.y, value.z);
        }
    }

    public void setUniform(String name, Vector4f value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform4f(location, value.x, value.y, value.z, value.w);
        }
    }

    public void setUniform(String name, Vector2i value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform2i(location, value.x, value.y);
        }
    }

    public void setUniform(String name, Vector3i value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform3i(location, value.x, value.y, value.z);
        }
    }

    public void setUniform(String name, Vector4i value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            glUniform4i(location, value.x, value.y, value.z, value.w);
        }
    }

    public void setUniform(String name, Matrix2f value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            float[] buffer = new float[4];

            value.get(buffer);

            glUniformMatrix2fv(location, false, buffer);
        }
    }

    public void setUniform(String name, Matrix3f value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            float[] buffer = new float[9];

            value.get(buffer);

            glUniformMatrix3fv(location, false, buffer);
        }
    }

    public void setUniform(String name, Matrix4f value){
        int location = glGetUniformLocation(programID, name);

        if(location != -1){
            float[] buffer = new float[16];

            value.get(buffer);

            glUniformMatrix4fv(location, false, buffer);
        }
    }


    public void enableShader(){
        glUseProgram(programID);
    }

    public void disableShader(){
        glUseProgram(0);
    }

    public void destroy(){
        disableShader();

        glDetachShader(programID, vertexID);
        glDetachShader(programID, fragmentID);

        glDeleteShader(vertexID);
        glDeleteShader(fragmentID);

        glDeleteProgram(programID);
    }





    private StringBuilder readShader(String filename) {
        StringBuilder shaderSource = new StringBuilder();

        InputStream is = getClass().getClassLoader().getResourceAsStream(filename);

        if(is == null) {
            throw new RuntimeException("Shader file '" + filename + "' is not found");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e){
            throw new RuntimeException("Shader file '" + filename + "' failed to read");
        }

        return shaderSource;
    }

    private int compileShader(StringBuilder source, int type){
        int shaderID = glCreateShader(type);
        glShaderSource(shaderID, source);
        glCompileShader(shaderID);

        if(glGetShaderi(shaderID, GL_COMPILE_STATUS) == 0){
            throw new RuntimeException("Could not compile shader. " + glGetShaderInfoLog(shaderID, 500));
        }

        return shaderID;
    }


}
