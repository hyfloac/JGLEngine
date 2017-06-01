package com.github.hyfloac.jglengine.renderer;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.deconstructor.IDeconstructor;
import com.github.hyfloac.jglengine.model.GameObject;
import com.github.hyfloac.jglengine.model.Model;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.shader.Light;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class Renderer implements IDeconstructor
{
    public static final Renderer INSTANCE = new Renderer();

    public final Multimap<Model, GameObject> worldObjects;
    protected final List<Light> lights;

    public Renderer()
    {
        DeconstructionHandler.INSTANCE.addDeconstructor(this);
        worldObjects = HashMultimap.create();
        lights = new ArrayList<>();
    }

    public void render()
    {
        /* CLear screen */
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
//        GLUtil.smoothBackground();
        Reference.SHADER.bind();

        /* Set uniform projection matrix */
        Reference.CAMERA.render();
        /* Get the current view Matrix */
        Matrix4f viewMatrix = Reference.CAMERA.updateViewMatrix();
        Reference.SHADER.setUniform("cameraPos", Reference.CAMERA.getXYZ());
        Reference.SHADER.setUniform("ambientLight", new Vector3f(0.3f, 0.3f, 0.3f));
        Reference.SHADER.setUniform("specularPower", 10.0f);
        /* Handle lighting */
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
        /* Not quite sure */
        Reference.SHADER.setUniform("textureSampler", 0);
        /* Loop through all objects in the scene with the same model */
        for(Model m : worldObjects.keySet())
        {
            /* Set the objects color &| texture */
            Reference.SHADER.setUniform("material", m.getMaterial());
            /* Enable VAO's */
            m.prepareRender();
            for(GameObject obj : worldObjects.get(m))
            {
                /* Add the objects model view matrix */
                Reference.SHADER.setUniform("modelViewMatrix", obj.getModelViewMatrix(viewMatrix) /*new Matrix4f(viewMatrix).mul(MatrixUtil.getModelViewMatrix(obj))*/);
                /* Send all vertex data to shaders */
                m.render();
            }
            /* Disable VAO's */
            m.finishRender();
        }

        Reference.SHADER.unbind();
        /* Upload frame to monitor */
        GLFW.glfwSwapBuffers(Reference.WINDOW_HANDLE);
    }

    public void add(GameObject obj) { worldObjects.put(obj.getModel(), obj); }

    public void add(Light light) { lights.add(light); }

    public void removeGameObject(GameObject obj) { worldObjects.remove(obj.getModel(), obj); }

    /* Optimized object removal from list (normal removal shifts everything over 1) */
    public void removeLight(int index)
    {
        if(lights.size() == index) { lights.remove(index); }
        else { lights.set(index, lights.remove(lights.size() - 1)); }
    }

    public Multimap<Model, GameObject> getWorldObjects() { return worldObjects; }

    public List<Light> getLights() { return lights; }

    @Override
    public void finalize()
    {
        worldObjects.clear();
        lights.clear();
    }
}
