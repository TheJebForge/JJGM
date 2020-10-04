package com.thejebforge.jjgm.rendering.utils;

import com.thejebforge.jjgm.rendering.containers.RenderBuffers;
import com.thejebforge.jjgm.rendering.elements.Model;
import com.thejebforge.jjgm.rendering.elements.Window;
import com.thejebforge.jjgm.rendering.shaders.ScreenShader;
import com.thejebforge.jjgm.rendering.shaders.Shader;
import com.thejebforge.jjgm.rendering.shaders.StaticShader;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.opengl.GL30.*;

public class RenderBuffer {
    private int frameBufferID;
    private int textureID;
    private int depthTextureID;

    private Shader shader;

    private Window window;
    private Model quad;

    public RenderBuffer(Window window){
        RenderBuffers.addRenderBuffer(this);
        this.window = window;
        this.shader = new ScreenShader();

        this.quad = new Model(new Vector3f[]{
                new Vector3f(-1f, 1f, 0),
                new Vector3f(1f, 1f, 0),
                new Vector3f(-1f, -1f, 0),
                new Vector3f(1f, -1f, 0),
        }, new int[]{
                0, 2, 1, 1, 2, 3
        }, new Vector3f[]{
                new Vector3f(0, 0, 1f),
                new Vector3f(0, 0, 1f),
                new Vector3f(0, 0, 1f),
                new Vector3f(0, 0, 1f),
        }, new Vector2f[]{
                new Vector2f(0, 1f),
                new Vector2f(1f, 1f),
                new Vector2f(0, 0),
                new Vector2f(1f, 0),
        });

        frameBufferID = glGenFramebuffers();

        System.out.println("FramebufferID: "+frameBufferID);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);


        textureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, window.width, window.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        depthTextureID = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTextureID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, window.width, window.height, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE, 0);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
        glTexParameteri (GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTextureID, 0);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureID, 0);

        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            throw new RuntimeException("Cannot create render target");

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void enableBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glViewport(0,0,window.width,window.height);
    }

    public void disableBuffer(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void drawBuffer(){
        //shader.enableShader();

//        glBindFramebuffer(GL_READ_FRAMEBUFFER, frameBufferID);
//
//        glBlitFramebuffer(0, 0, window.width, window.height, 0, 0, window.width, window.height, GL_COLOR_BUFFER_BIT, GL_NEAREST);

        glBindVertexArray(quad.getVaoID());

        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureID);

        glDrawElements(GL_TRIANGLES, quad.getVertexCount(), GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(1);
        glDisableVertexAttribArray(0);

        glBindVertexArray(0);
    }

    public void destroy(){
        glDeleteTextures(textureID);
        glDeleteTextures(depthTextureID);
        glDeleteFramebuffers(frameBufferID);
    }

}
