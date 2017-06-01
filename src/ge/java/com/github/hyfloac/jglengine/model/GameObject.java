package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.jglengine.axis.Transformable;
import com.github.hyfloac.jglengine.util.MatrixUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

@SuppressWarnings("unused")
public class GameObject extends Transformable implements Cloneable
{
    protected Model model;
    protected Vector3f prevPos = new Vector3f(0.0f, 0.0f, 0.0f);
    protected Vector3f prevRot = new Vector3f(0.0f, 0.0f, 0.0f);
    protected Vector3f prevScale = new Vector3f(0.0f, 0.0f, 0.0f);
    protected Matrix4f modelViewViewMatrix;

    public GameObject(Model model, Vector3f position, Vector3f rotation, Vector3f scale)
    {
        this.model = model;
        pos = position;
        rot = rotation;
        this.scale = scale;
    }

    public GameObject(Model model, Vector3f position, Vector3f rotation) { this(model, position, rotation, new Vector3f(1.0f, 1.0f, 1.0f)); }

    public GameObject(Model model, Vector3f position) { this(model, position, new Vector3f(0.0f, 0.0f, 0.0f)); }

    public GameObject(Model model) { this(model, new Vector3f(0.0f, 0.0f, 0.0f)); }

    public Model getModel() { return model; }

    private Matrix4f prevViewMatrix = new Matrix4f();
    private Matrix4f prevModelViewMatrix = new Matrix4f();

    public Matrix4f getModelViewMatrix(Matrix4f viewMatrix)
    {
        if(mvCache != null)
        {
            Matrix4f tmp = new Matrix4f(mvCache);
            mvCache = null;
            return tmp;
        }
        return genModelViewMatrix(viewMatrix);
    }

    private Matrix4f mvCache = null;

    private Matrix4f genModelViewMatrix(Matrix4f viewMatrix)
    {
        if(prevPos.equals(pos) && prevRot.equals(rot) && prevScale.equals(scale))
        {
            if(prevViewMatrix.equals(viewMatrix)) { return modelViewViewMatrix; }
            prevViewMatrix = new Matrix4f(viewMatrix);
            return modelViewViewMatrix = new Matrix4f(viewMatrix).mul(prevModelViewMatrix);
        }
        prevModelViewMatrix = MatrixUtil.getModelViewMatrix(this);

        prevPos = new Vector3f(pos);
        prevRot = new Vector3f(rot);
        prevScale = new Vector3f(scale);
        return modelViewViewMatrix = new Matrix4f(viewMatrix).mul(prevModelViewMatrix);
    }

    public void cacheModelViewMatrix(Matrix4f viewMatrix)
    {
        mvCache = genModelViewMatrix(viewMatrix);
    }

    @Override
    public GameObject clone()
    {
        try { return (GameObject) super.clone(); }
        catch(CloneNotSupportedException e) { e.printStackTrace(); }
        return null;
    }
}
