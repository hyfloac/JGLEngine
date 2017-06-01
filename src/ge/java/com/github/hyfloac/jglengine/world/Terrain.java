package com.github.hyfloac.jglengine.world;

import com.github.hyfloac.jglengine.model.Model;

public class Terrain
{
    private static final int VERTEX_COUNT = 2;
    private static final float SIZE = VERTEX_COUNT * 1;

    private int m_chunkX;
    private int m_chunkZ;
    private float m_x;
    private float m_z;
    private Model m_model;

    public Terrain(int chunkX, int chunkZ)
    {
        m_chunkX = chunkX;
        m_chunkZ = chunkZ;
        m_x = m_chunkX * SIZE;
        m_z = m_chunkZ * SIZE;
        m_model = generateTerrain();
    }

    public int getChunkX() { return m_chunkX; }

    public int getChunkZ() { return m_chunkZ; }

    public float getX() { return m_x; }

    public float getZ() { return m_z; }

    public Model getModel() { return m_model; }

    private Model generateTerrain()
    {
        int numVertices = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[numVertices * 3];
        float[] textureCoords = new float[numVertices * 2];
        float[] normals = new float[numVertices * 3];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

        int pointer = 0;
        for(int i = 0; i < VERTEX_COUNT; ++i)
        {
            for(int j = 0; j < VERTEX_COUNT; ++j)
            {
                vertices[pointer * 3] = j / ((float) VERTEX_COUNT - 1) * SIZE;
                vertices[pointer * 3 + 1] = (float) Math.random() * 0;
                vertices[pointer * 3 + 2] = i / ((float) VERTEX_COUNT - 1) * SIZE;
                textureCoords[pointer * 2] = j / ((float) VERTEX_COUNT - 1);
                textureCoords[pointer * 2 + 1] = i / ((float) VERTEX_COUNT - 1);
                normals[pointer * 3] = 0;
                normals[pointer * 3 + 1] = 1;
                normals[pointer * 3 + 2] = 0;
                pointer++;
            }
        }

        pointer = 0;
        for(int z = 0; z < VERTEX_COUNT - 1; ++z)
        {
            for(int x = 0; x < VERTEX_COUNT - 1; ++x)
            {
                int topLeft = z * VERTEX_COUNT + x;
                int topRight = topLeft + 1;
                int bottomLeft = (z + 1) * VERTEX_COUNT + x;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return new Model(vertices, textureCoords, normals, indices);
    }
}