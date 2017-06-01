package com.github.hyfloac.jglengine.axis;

import org.joml.Vector3f;

@SuppressWarnings("unused")
public interface ITranslatable
{
    void setX(float x);

    void setY(float y);

    void setZ(float z);

    float getX();

    float getY();

    float getZ();

    void incrementX(float x);

    void incrementY(float y);

    void incrementZ(float z);

    void setXYZ(Vector3f xyz);

    Vector3f getXYZ();
}