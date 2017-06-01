package com.github.hyfloac.jglengine.world;

import com.github.hyfloac.jglengine.util.ColorUtil;

public class World
{
    private static final int SIZE = 40;

    public World()
    {
        for(int i = 0; i < SIZE; ++i)
        {
            for(int j = 0; j < SIZE; ++j)
            {
                Terrain n = new Terrain(i, j);
                n.getModel().getMaterial().color = ColorUtil.toVec4f((5 * i), (i * j) % 255, (5 * j));
//                Renderer.INSTANCE.add(new GameObject(n, new Vector3f(n.getX(), 0, n.getZ())));
            }
        }
    }
}