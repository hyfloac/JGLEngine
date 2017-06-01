package com.github.hyfloac.jglengine.util;

import com.github.hyfloac.jglengine.model.GameObject;
import com.github.hyfloac.jglengine.model.Model;
import com.github.hyfloac.jglengine.reference.Models;
import com.github.hyfloac.jglengine.reference.Textures;
import com.github.hyfloac.jglengine.renderer.Renderer;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Debug
{
    private static float[] vertices = new float[] { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f, 0.5f, 0.5f };
    private static float[] textures = new float[] { 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f };
    private static int[] indices = new int[] { 0, 1, 3, 3, 1, 2 };

    public static Model getTextureTestModel() { return new Model(vertices, 2, textures, 2, null, 2, indices); }

    public static void debugTexture(int textureID) { Renderer.INSTANCE.add(new GameObject(getTextureTestModel().clone().setTexID(textureID), new Vector3f(0.0f, 0.0f, -3.0f))); }

    public static void debugVector3f(Vector3f in) { com.github.hyfloac.simplelog.Logger.debugS("(" + in.x + ", " + in.y + ", " + in.z + ")"); }

    public static void debugVector4f(Vector4f in) { com.github.hyfloac.simplelog.Logger.debugS("(" + in.x + ", " + in.y + ", " + in.z + ", " + in.w + ")"); }

    public static void addDebugObjects(int radius, int numObjects, boolean rotate)
    {
        float r = 1;
        int rx = 0, ry = 0, rz = 0;
        for(int i = 0; i < numObjects; ++i)
        {
            float x = (float) (Math.random() * radius * r);
            r = updateRandom(r);
            float y = (float) (Math.random() * radius * r);
            r = updateRandom(r);
            float z = (float) (Math.random() * radius * r);
            r = updateRandom(r);

            if(rotate)
            {
                rx = (int) (Math.random() * 500) % 360;
                ry = (int) (Math.random() * 500) % 360;
                rz = (int) (Math.random() * 500) % 360;
            }

            Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(x, y, z), new Vector3f(rx, ry, rz)));
        }
    }

    private static float updateRandom(float r)
    {
        if(Math.random() * 10 > 5) { r *= -1; }
        return r;
    }

    public static float rad = 1;
    public static int len = 500;

    public static void test()
    {
        float r = 1;
        for(int i = 0; i < len; ++i)
        {
            double tmp = (double) i / (double) len;
            double angle = tmp * Math.PI * 2;
            double x = Math.sin(angle) * rad /* Math.random()*/;
            double y = Math.cos(angle) * rad /* Math.random()*/ * 0;
            double z = Math.cos(angle) * rad /* Math.random()*/;
            Vector3f pos = new Vector3f((float) x, (float) y, (float) z);

            Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), pos, new Vector3f(0.0f, (float) (x * z * 120), 0.0f)));
        }
        rad *= 2;
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(rad, rad, rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(rad, rad, -rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(rad, -rad, rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(rad, -rad, -rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(-rad, rad, rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(-rad, rad, -rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(-rad, -rad, rad), new Vector3f(0, 0, 0)));
        Renderer.INSTANCE.add(new GameObject(Models.CUBE.clone().setTexID(Textures.CUBE.texID), new Vector3f(-rad, -rad, -rad), new Vector3f(0, 0, 0)));
        rad /= 2;
    }
}
