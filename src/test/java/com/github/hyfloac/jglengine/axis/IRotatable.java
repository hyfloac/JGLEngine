package com.github.hyfloac.jglengine.axis;

import org.joml.Vector3f;

public interface IRotatable
{
    void setRotX(float x);

    void setRotY(float y);

    void setRotZ(float z);

    float getRotX();

    float getRotY();

    float getRotZ();

    void incrementRotX(float x);

    void incrementRotY(float y);

    void incrementRotZ(float z);

    void setRotXYZ(Vector3f xyz);

    Vector3f getRotXYZ();
}