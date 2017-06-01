package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.jglengine.util.MatrixUtil;
import com.github.hyfloac.jglengine.axis.ITransformable;
import com.github.hyfloac.jglengine.world.Terrain;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class GameObject implements ITransformable
{
    //region Vars
    private Model model;
    private Vector3f pos;
    private Vector3f rot;
    private Vector3f scale;
    private Vector3f prevPos = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f prevRot = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f prevScale = new Vector3f(0.0f, 0.0f, 0.0f);
    private Matrix4f modelViewMatrix;
    //endregion

    public GameObject(GameObject obj)
    {
        model = new Model(obj.getModel());
        pos = new Vector3f(obj.getXYZ());
        rot = new Vector3f(obj.getRotXYZ());
        scale = obj.getScaleXYZ();
        modelViewMatrix = new Matrix4f();
    }

    //region Constructors
    public GameObject(Model model, Vector3f position, Vector3f rotation, Vector3f scale)
    {
        this.model = model;
        pos = position;
        rot = rotation;
        this.scale = scale;
    }

    public GameObject(Model model, Vector3f position, Vector3f rotation) { this(model, position, rotation, new Vector3f(1.0f, 1.0f, 1.0f)); }

    public GameObject(Model model, Vector3f position) { this(model, position, new Vector3f(0.0f, 0.0f, 0.0f)); }

    public GameObject(Terrain terrain, Vector3f position) { this(terrain.getModel(), position); }

    public GameObject(Model model) { this(model, new Vector3f(0.0f, 0.0f, 0.0f)); }
    //endregion

    public Model getModel() { return model; }

    private Matrix4f prevViewMatrix = new Matrix4f();

    public Matrix4f getModelViewMatrix(Matrix4f viewMatrix)
    {
        /* Only update the model's view matrix if the object has changed (extremely useful because the cubes never move) */
        if(prevPos.equals(pos) && prevRot.equals(rot) && prevScale.equals(scale) && prevViewMatrix.equals(viewMatrix)) { return modelViewMatrix; }
        modelViewMatrix = new Matrix4f(viewMatrix).mul(MatrixUtil.getModelViewMatrix(this));

        prevPos = new Vector3f(pos);
        prevRot = new Vector3f(rot);
        prevScale = new Vector3f(scale);
        prevViewMatrix = new Matrix4f(viewMatrix);
        return modelViewMatrix;
    }

    //region Position getters & setters
    @Override
    public void setX(float x) { pos.x = x; }

    @Override
    public void setY(float y) { pos.y = y; }

    @Override
    public void setZ(float z) { pos.z = z; }

    @Override
    public float getX() { return pos.x; }

    @Override
    public float getY() { return pos.y; }

    @Override
    public float getZ() { return pos.z; }

    @Override
    public void incrementX(float x) { pos.x += x; }

    @Override
    public void incrementY(float y) { pos.y += y; }

    @Override
    public void incrementZ(float z) { pos.z += z; }

    @Override
    public void setXYZ(Vector3f xyz) { pos = xyz; }

    @Override
    public Vector3f getXYZ() { return pos; }

    @Override
    public void setRotX(float x) { rot.x = x; }

    @Override
    public void setRotY(float y) { rot.y = y; }

    @Override
    public void setRotZ(float z) { rot.z = z; }

    @Override
    public float getRotX() { return rot.x; }

    @Override
    public float getRotY() { return rot.y; }

    @Override
    public float getRotZ() { return rot.z; }

    @Override
    public void incrementRotX(float x) { rot.x += x; }

    @Override
    public void incrementRotY(float y) { rot.y += y; }

    @Override
    public void incrementRotZ(float z) { rot.z += z; }

    @Override
    public void setRotXYZ(Vector3f xyz) { rot = xyz; }

    @Override
    public Vector3f getRotXYZ() { return rot; }

    @Override
    public void setScaleX(float x) { scale.x = x; }

    @Override
    public void setScaleY(float y) { scale.y = y; }

    @Override
    public void setScaleZ(float z) { scale.z = z; }

    @Override
    public float getScaleX() { return scale.x; }

    @Override
    public float getScaleY() { return scale.y; }

    @Override
    public float getScaleZ() { return scale.z; }

    @Override
    public void incrementScaleX(float x) { scale.x += x; }

    @Override
    public void incrementScaleY(float y) { scale.y += y; }

    @Override
    public void incrementScaleZ(float z) { scale.z += z; }

    @Override
    public void setScaleXYZ(Vector3f xyz) { scale = xyz; }

    @Override
    public Vector3f getScaleXYZ() { return scale; }
    //endregion
}
