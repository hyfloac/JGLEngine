package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.deconstructor.IDeconstructor;
import com.github.hyfloac.jglengine.shader.Light;
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

public class Model implements IDeconstructor
{
    //region Vars
    private final Quad<float[], float[], float[], int[]> m_modelCoords;
    private final Tuple<Integer, Integer, Integer> m_vboSizes;
    private final int m_vertexCount;
    private final Light.Material m_mat;
    private int m_texID;
    private final List<Integer> m_vbos;
    private final int m_vaoID;
    private int[] indexes = new int[] { -1, -1, -1 };
    //endregion

    //region Constructors
    public Model(float[] vertices, int vertSize, float[] textures, int texSize, float[] normals, int normSize, int[] indices)
    {
        int counter = 0;
        m_modelCoords = new Quad<>(vertices, textures, normals, indices);
        m_vboSizes = new Tuple<>(vertSize, texSize, normSize);
        DeconstructionHandler.INSTANCE.addDeconstructor(this);
        m_vertexCount = indices.length;
        m_vbos = new ArrayList<>();

        m_vaoID = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(m_vaoID);

        int vboID = GL15.glGenBuffers();
        m_vbos.add(vboID);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(counter, vertSize, GL11.GL_FLOAT, false, 0, 0);
        indexes[0] = counter;
        ++counter;

        if(textures != null)
        {
            vboID = GL15.glGenBuffers();
            m_vbos.add(vboID);
            buffer = BufferUtils.createFloatBuffer(textures.length);
            buffer.put(textures).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(counter, texSize, GL11.GL_FLOAT, false, 0, 0);
            indexes[1] = counter;
            ++counter;
        }

        if(normals != null)
        {
            vboID = GL15.glGenBuffers();
            m_vbos.add(vboID);
            buffer = BufferUtils.createFloatBuffer(normals.length);
            buffer.put(normals).flip();
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
            GL20.glVertexAttribPointer(counter, normSize, GL11.GL_FLOAT, false, 0, 0);
            indexes[2] = counter;
        }

        vboID = GL15.glGenBuffers();
        m_vbos.add(vboID);
        IntBuffer iBuffer = BufferUtils.createIntBuffer(indices.length);
        iBuffer.put(indices).flip();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, iBuffer, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        m_mat = new Light.Material(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), true, isTextured(), false, 0.0f);
    }

    public Model(float[] vertices, float[] textures, float[] normals, int[] indices) { this(vertices, 3, textures, 2, normals, 3, indices); }

    public Model(Quad<float[], float[], float[], int[]> modelCoords, Tuple<Integer, Integer, Integer> vboSizes) { this(modelCoords.left, vboSizes.left, modelCoords.middleL, vboSizes.middle, modelCoords.middleR, vboSizes.right, modelCoords.right); }

    public Model(Quad<float[], float[], float[], int[]> modelCoords) { this(modelCoords.left, modelCoords.middleL, modelCoords.middleR, modelCoords.right); }

    public Model(Model model)
    {
        this(model.getModelCoords(), model.getVboSizes());
        m_texID = model.getTexID();
    }
    //endregion

    //region Getters
    public Quad<float[], float[], float[], int[]> getModelCoords() { return m_modelCoords; }

    public Tuple<Integer, Integer, Integer> getVboSizes() { return m_vboSizes; }

    public int getVaoID() { return m_vaoID; }

    public int getVertexCount() { return m_vertexCount; }

    public Model setTexID(int texID)
    {
        m_texID = texID;
        m_mat.useTexture = isTextured();
        return this;
    }

    public int getTexID() { return m_texID; }

    public boolean isTextured() { return m_texID != MemoryUtil.NULL && indexes[1] > -1; }

    public Light.Material getMaterial() { return m_mat; }

    public List<Integer> getVbos() { return m_vbos; }
    //endregion

    public void prepareRender()
    {
        if(isTextured())
        {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, m_texID);
        }

        GL30.glBindVertexArray(m_vaoID);
        GL20.glEnableVertexAttribArray(indexes[0]);
        if(isTextured()) { GL20.glEnableVertexAttribArray(indexes[1]); }
        if(indexes[2] > -1) { GL20.glEnableVertexAttribArray(indexes[2]); }
    }

    public void render() { GL11.glDrawElements(GL11.GL_TRIANGLES, m_vertexCount, GL11.GL_UNSIGNED_INT, 0); }

    public void finishRender()
    {
        if(indexes[2] > -1) { GL20.glDisableVertexAttribArray(indexes[2]); }
        if(isTextured()) { GL20.glDisableVertexAttribArray(indexes[1]); }
        GL20.glDisableVertexAttribArray(indexes[0]);
        GL30.glBindVertexArray(0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }

    @Override
    public void finalize()
    {
        if(indexes[2] > -1) { GL20.glDisableVertexAttribArray(indexes[2]); }
        if(isTextured()) { GL20.glDisableVertexAttribArray(indexes[1]); }
        GL20.glDisableVertexAttribArray(indexes[0]);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        m_vbos.forEach(GL15::glDeleteBuffers);
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(m_vaoID);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
    }
}
