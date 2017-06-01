package com.github.hyfloac.jglengine.util;

import com.github.hyfloac.jglengine.axis.ITransformable;
import com.github.vitrifiedcode.javautilities.math.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MatrixUtil
{
    public static Matrix4f getModelViewMatrix(ITransformable obj)
    {
        Matrix4f out = new Matrix4f().identity();
        Vector3f xyz = obj.getXYZ();
        Vector3f rot = obj.getRotXYZ();
        Vector3f scale = obj.getScaleXYZ();
        if(!xyz.equals(ColorUtil.BLACK)) { out.translate(xyz); }
        if(!rot.equals(ColorUtil.BLACK)) { out.rotateX((float) MathUtil.deg2rad(-obj.getRotX())).rotateY((float) MathUtil.deg2rad(-obj.getRotY())).rotateZ((float) MathUtil.deg2rad(-obj.getRotZ())); }
        if(!scale.equals(ColorUtil.WHITE)) { out.scale(xyz); }

        return out;
    }
}