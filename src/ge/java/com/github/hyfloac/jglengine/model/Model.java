package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.jglengine.shader.Light;
import com.github.vitrifiedcode.javautilities.array.ArrayUtil;
import com.github.vitrifiedcode.javautilities.list.container.Quad;
import com.github.vitrifiedcode.javautilities.list.container.Tuple;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class Model implements Cloneable
{
    private final Quad<float[], float[], float[], int[]> m_modelCoords;
    private final int[] m_vboSizes;
    private final int m_vertexCount;
    private final Light.Material m_mat;
    private int m_texID;
    private final int[] m_vbos;
    private final int m_vaoID;
    private int[] indexes = new int[] { -1, -1 };

    public Model(float[] vertices, int vertSize, float[] textures, int texSize, float[] normals, int normSize, int[] indices)
    {
        int counter = 0;
        m_modelCoords = new Quad<float[], float[], float[], int[]>(vertices, textures, normals, indices);
        m_vboSizes = new int[] { vertSize, texSize, normSize };
        m_vertexCount = indices.length;
        m_vbos = new int[4];

        m_vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(m_vaoID);

        int vboID = GL15.glGenBuffers();
        m_vbos[0] = vboID;
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(counter++, vertSize, GL11.GL_FLOAT, false, 0, 0);

        if(textures != null)
        {
            vboID = GL15.glGenBuffers();
            m_vbos[1] = vboID;
            buffer = BufferUtils.createFloatBuffer(textures.length);
            buffer.put(textures).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(counter, texSize, GL11.GL_FLOAT, false, 0, 0);
            indexes[0] = counter++;
        }


        if(normals != null)
        {
            vboID = GL15.glGenBuffers();
            m_vbos[2] = vboID;
            buffer = BufferUtils.createFloatBuffer(normals.length);
            buffer.put(normals).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(counter, normSize, GL11.GL_FLOAT, false, 0, 0);
            indexes[1] = counter;
        }

        vboID = GL15.glGenBuffers();
        m_vbos[3] = vboID;
        IntBuffer iBuffer = BufferUtils.createIntBuffer(indices.length);
        iBuffer.put(indices).flip();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);

//        MemoryUtil.memFree(buffer);
//        MemoryUtil.memFree(iBuffer);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        m_mat = new Light.Material(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), false, isTextured(), false, 0.0f);
    }

    public Model(float[] vertices, float[] textures, float[] normals, int[] indices) { this(vertices, 3, textures, 2, normals, 3, indices); }

    public Model(Quad<float[], float[], float[], int[]> modelCoords, int[] vboSizes) { this(modelCoords.left, vboSizes[0], modelCoords.middleL, vboSizes[1], modelCoords.middleR, vboSizes[2], modelCoords.right); }

    public Model(Quad<float[], float[], float[], int[]> modelCoords) { this(modelCoords.left, modelCoords.middleL, modelCoords.middleR, modelCoords.right); }

    public Quad<float[], float[], float[], int[]> getModelCoords() { return m_modelCoords; }

    public int[] getVboSizes() { return m_vboSizes; }

    public int getVaoID() { return m_vaoID; }

    public int getVertexCount() { return m_vertexCount; }

    public Model setTexID(int texID)
    {
        m_texID = texID;
        m_mat.useTexture = isTextured();
        return this;
    }

    public int getTexID() { return m_texID; }

    public boolean isTextured() { return m_texID != MemoryUtil.NULL && indexes[0] > -1; }

    public Light.Material getMaterial() { return m_mat; }

    public int[] getVbos() { return m_vbos; }

    public void prepareRender()
    {
        if(isTextured())
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_texID);
        }

        GL30.glBindVertexArray(m_vaoID);
        GL20.glEnableVertexAttribArray(0);
        if(isTextured()) { GL20.glEnableVertexAttribArray(indexes[0]); }
        if(m_vbos[2] != 0) { GL20.glEnableVertexAttribArray(indexes[1]); }
    }

    public void render()
    {
        GL11.glDrawElements(GL11.GL_TRIANGLES, m_vertexCount, GL11.GL_UNSIGNED_INT, 0);
    }

    public void finishRender()
    {
        if(m_vbos[2] != 0) { GL20.glDisableVertexAttribArray(indexes[1]); }
        if(isTextured()) { GL20.glDisableVertexAttribArray(indexes[0]); }
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    public boolean equals(Object obj)
    {
        return !(obj == null || !(obj instanceof Model)) && !(!((Model) obj).m_modelCoords.equals(m_modelCoords) || !((Model) obj).m_mat.equals(m_mat) || m_texID != ((Model) obj).m_texID);
    }

    @Override
    public int hashCode()
    {
        return m_modelCoords.hashCode() / m_texID + m_mat.hashCode();
    }

    @Override
    public String toString()
    {
        return super.toString();
    }

    @Override
    public Model clone()
    {
        try { return (Model) super.clone(); }
        catch(CloneNotSupportedException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public void finalize()
    {
        if(m_vbos[2] != 0) { GL20.glDisableVertexAttribArray(indexes[1]); }
        if(isTextured()) { GL20.glDisableVertexAttribArray(indexes[0]); }
        GL20.glDisableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        for(int vbo : m_vbos) { GL15.glDeleteBuffers(vbo); }
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(m_vaoID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
