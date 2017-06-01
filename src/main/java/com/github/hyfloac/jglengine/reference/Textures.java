package com.github.hyfloac.jglengine.reference;

import com.github.hyfloac.jglengine.model.Texture;

public final class Textures
{
    private Textures() {}

    public static final Texture CUBE = new Texture("/Cube.png", false, 16);
    public static final Texture TERRAIN = new Texture("/Terrain.png", true, 256);
}
