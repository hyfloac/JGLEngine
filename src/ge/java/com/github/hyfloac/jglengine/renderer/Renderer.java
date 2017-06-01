package com.github.hyfloac.jglengine.renderer;

import com.github.hyfloac.jglengine.model.GameObject;
import com.github.hyfloac.jglengine.model.Model;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.shader.Light;
import com.github.hyfloac.jglengine.tick.ITickable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class Renderer implements ITickable
{
    public static final Renderer INSTANCE = new Renderer();

    public final MutableMultimap<Model, GameObject> worldObjects;
    protected final List<Light> lights;
//    protected final List<Text> texts;

    public Renderer()
    {
//        worldObjects = HashMultimap.create();
        worldObjects = new HashBagMultimap<>();
        lights = new FastList<>();
//        texts = new FastList<>();

}

    private static final Vector3f AMBIENT_LIGHT = new Vector3f(0.3F, 0.3F, 0.3F);


    @Override
    public void render()
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//        GLUtil.smoothBackground();
        Reference.SHADER.bind();

        Reference.CAMERA.render();
        Matrix4f viewMatrix = Reference.CAMERA.updateViewMatrix();
        Reference.SHADER.setUniform("cameraPos", Reference.CAMERA.getXYZ());
        Reference.SHADER.setUniform("ambientLight", AMBIENT_LIGHT);
        Reference.SHADER.setUniform("specularPower", 10.0f);
        for(Light l : lights)
        {
            if(l instanceof Light.PointLight)
            {
                Light.PointLight copy = new Light.PointLight((Light.PointLight) l);
                Vector3f lightPos = copy.position;
                Vector4f aux = new Vector4f(lightPos, 1);
                aux.mul(viewMatrix);
                lightPos.x = aux.x;
                lightPos.y = aux.y;
                lightPos.z = aux.z;
                Reference.SHADER.setUniform("pointLight", copy);
            }
        }

        Reference.SHADER.setUniform("textureSampler", 0);
        for(Model m : worldObjects.keyBag())
        {
            Reference.SHADER.setUniform("material", m.getMaterial());
            m.prepareRender();
            for(GameObject obj : worldObjects.get(m))
            {
//                if(KeyInputHandler.check && !Reference.CAMERA.isInView(obj.getXYZ())) { continue; }
                Reference.SHADER.setUniform("modelViewMatrix", obj.getModelViewMatrix(viewMatrix));
                m.render();
            }
            m.finishRender();
        }
        Reference.SHADER.unbind();
    }

    public void add(GameObject obj) { worldObjects.put(obj.getModel(), obj); }

    public void add(Light light) { lights.add(light); }

//    public void add(Text text) { texts.add(text); }

    public void removeGameObject(GameObject obj) { worldObjects.remove(obj.getModel(), obj); }

    public void removeLight(int index)
    {
        if(lights.size() == index) { lights.remove(index); }
        else { lights.set(index, lights.remove(lights.size() - 1)); }
    }

    public List<Light> getLights() { return lights; }
}
