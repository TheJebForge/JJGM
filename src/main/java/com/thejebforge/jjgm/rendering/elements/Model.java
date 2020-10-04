package com.thejebforge.jjgm.rendering.elements;

import com.thejebforge.jjgm.rendering.containers.Models;
import com.thejebforge.jjgm.rendering.shaders.Shader;
import com.thejebforge.jjgm.util.MathUtil;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.assimp.Assimp.*;

public class Model {


    private int vaoID = 0;
    private List<Integer> vbosID;
    private int vertexCount;

    private List<Shader> shaders;
    private Texture texture = null;

    public Vector3f position = new Vector3f();
    public Quaternionf rotation = new Quaternionf();
    public Vector3f scale = new Vector3f(1);

    public Model(){
        this(new Vector3f[]{}, new int[]{}, new Vector3f[]{}, new Vector2f[]{});
    }

    public Model(int vaoID, int vertexCount){
        Models.addModel(this);

        vbosID = new ArrayList<>();
        shaders = new ArrayList<>();

        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public Model(String filename){
        File model = new File(filename);

        AIScene scene = aiImportFile(model.getAbsolutePath(), aiProcess_Triangulate | aiProcess_FixInfacingNormals);
        if (scene == null) {
            throw new RuntimeException("Error loading model");
        }

        List<Vector3f> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> uv = new ArrayList<>();

        int numMeshes = scene.mNumMeshes();
        PointerBuffer aiMeshes = scene.mMeshes();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));

            AIVector3D.Buffer aiVertices = aiMesh.mVertices();
            while(aiVertices.remaining() > 0){
                AIVector3D aiVertex = aiVertices.get();
                vertices.add(new Vector3f(aiVertex.x(), aiVertex.y(), aiVertex.z()));
            }

            AIVector3D.Buffer aiNormals = aiMesh.mNormals();
            if(aiNormals != null) {
                while (aiNormals.remaining() > 0) {
                    AIVector3D aiNormal = aiNormals.get();
                    normals.add(new Vector3f(aiNormal.x(), aiNormal.y(), aiNormal.z()));
                }
            }

            AIVector3D.Buffer aiTexCoords = aiMesh.mTextureCoords(0);
            if(aiTexCoords != null) {
                while (aiTexCoords.remaining() > 0) {
                    AIVector3D aiTextureCoord = aiTexCoords.get();
                    uv.add(new Vector2f(aiTextureCoord.x(), aiTextureCoord.y()));
                }
            }

            AIFace.Buffer aiFaces = aiMesh.mFaces();
            while (aiFaces.remaining() > 0) {
                AIFace aiFace = aiFaces.get();
                IntBuffer indicesBuffer = aiFace.mIndices();

                while (indicesBuffer.remaining() > 0) {
                    indices.add(indicesBuffer.get());
                }
            }
        }

        Vector3f[] verticesArray = new Vector3f[vertices.size()];
        for(int i = 0;i < verticesArray.length;i++) verticesArray[i] = vertices.get(i);

        int[] indicesArray = new int[indices.size()];
        for(int i = 0;i < indicesArray.length;i++) indicesArray[i] = indices.get(i);

        Vector3f[] normalsArray = new Vector3f[normals.size()];
        for(int i = 0;i < normalsArray.length;i++) normalsArray[i] = normals.get(i);

        Vector2f[] uvArray = new Vector2f[uv.size()];
        for(int i = 0;i < uvArray.length;i++) uvArray[i] = uv.get(i);

        initModel(verticesArray, indicesArray, normalsArray, uvArray);
    }

    public Model(Vector3f[] vertices, int[] indices, Vector3f[] normals, Vector2f[] uv){
        initModel(vertices, indices, normals, uv);
    }

    private void initModel(Vector3f[] vertices, int[] indices, Vector3f[] normals, Vector2f[] uv){
        Models.addModel(this);

        vbosID = new ArrayList<>();
        shaders = new ArrayList<>();

        vaoID = glGenVertexArrays();
        vertexCount = indices.length;

        glBindVertexArray(vaoID);

        if(vertices.length > 0) {
            int indicesVboID = glGenBuffers();
            vbosID.add(indicesVboID);

            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indicesVboID);

            IntBuffer indicesBuffer = indicesToIntBuffer(indices);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            FloatBuffer vertexBuffer = verticesToFloatBuffer(vertices);
            storeDataInAttributeList(0, 3, vertexBuffer);

            FloatBuffer uvBuffer = uvToFloatBuffer(uv);
            storeDataInAttributeList(1, 2, uvBuffer);

            FloatBuffer normalBuffer = verticesToFloatBuffer(normals);
            storeDataInAttributeList(2, 3, normalBuffer);

        }

        glBindVertexArray(0);
    }

    private void storeDataInAttributeList(int attribNumber, int coordSize, FloatBuffer data){
        int vboID = glGenBuffers();
        vbosID.add(vboID);

        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        glBufferData(GL_ARRAY_BUFFER, data, GL_STATIC_DRAW);

        glVertexAttribPointer(attribNumber, coordSize, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    private FloatBuffer verticesToFloatBuffer(Vector3f[] vertices){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length * 3);

        for(Vector3f vertex : vertices){
            buffer.put(vertex.x);
            buffer.put(vertex.y);
            buffer.put(vertex.z);
        }

        buffer.flip();

        return buffer;
    }

    private IntBuffer indicesToIntBuffer(int[] indices){
        IntBuffer buffer = BufferUtils.createIntBuffer(indices.length);

        buffer.put(indices);

        buffer.flip();

        return buffer;
    }

    private FloatBuffer uvToFloatBuffer(Vector2f[] uvs){
        FloatBuffer buffer = BufferUtils.createFloatBuffer(uvs.length * 2);

        for(Vector2f uv : uvs){
            buffer.put(uv.x);
            buffer.put(uv.y);
        }

        buffer.flip();

        return buffer;
    }

    public Matrix4f getTransformMatrix(){
        return MathUtil.transformMatrix(position, rotation, scale);
    }

    public void addShader(Shader shader){
        shaders.add(shader);
    }

    public List<Shader> getShaders(){
        return shaders;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public int getVaoID() {
        return vaoID;
    }

    public List<Integer> getVbosID() {
        return vbosID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public Model clone(){
        return new Model(vaoID, vertexCount);
    }

    public void destroy(){
        if(vaoID != 0) glDeleteVertexArrays(vaoID);

        if(vbosID.size() > 0){
            for(int vboID : vbosID) glDeleteBuffers(vboID);
        }
    }
}
