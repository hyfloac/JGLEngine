package com.github.hyfloac.jglengine.axis;

import org.joml.Vector3f;

public interface IScalable
{
    void setScaleX(float x);

    void setScaleY(float y);

    void setScaleZ(float z);

    float getScaleX();

    float getScaleY();

    float getScaleZ();

    void incrementScaleX(float x);

    void incrementScaleY(float y);

    void incrementScaleZ(float z);

    void setScaleXYZ(Vector3f xyz);

    Vector3f getScaleXYZ();
}