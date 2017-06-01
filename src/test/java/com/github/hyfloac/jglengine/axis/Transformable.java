package com.github.hyfloac.jglengine.axis;

import org.joml.Vector3f;

public class Transformable implements ITransformable
{
    private Vector3f pos;
    private Vector3f rot;
    private Vector3f scale;

    public Transformable(Vector3f pos, Vector3f rot, Vector3f scale)
    {
        this.pos = pos;
        this.rot = rot;
        this.scale = scale;
    }

    public Transformable(Vector3f pos, Vector3f rot) { this(pos, rot, new Vector3f(0.0f, 0.0f, 0.0f)); }

    public Transformable(Vector3f pos) { this(pos, new Vector3f(0.0f, 0.0f, 0.0f)); }

    public Transformable() { this(new Vector3f(0.0f, 0.0f, 0.0f)); }

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
    public void incrementScaleX(float x) { scale.x = x; }

    @Override
    public void incrementScaleY(float y) { scale.y += y; }

    @Override
    public void incrementScaleZ(float z) { scale.z += z; }

    @Override
    public void setScaleXYZ(Vector3f xyz) { scale = xyz; }

    @Override
    public Vector3f getScaleXYZ() { return scale; }
}