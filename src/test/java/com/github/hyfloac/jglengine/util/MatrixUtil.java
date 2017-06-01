package com.github.hyfloac.jglengine.util;

import com.github.hyfloac.jglengine.axis.ITransformable;
import com.github.vitrifiedcode.javautilities.math.MathUtil;
import org.joml.Matrix4f;

public class MatrixUtil
{
    public static Matrix4f getModelViewMatrix(ITransformable obj) { return new Matrix4f().identity().translate(obj.getXYZ()).rotateX((float) MathUtil.deg2rad(-obj.getRotX())).rotateY((float) MathUtil.deg2rad(-obj.getRotY())).rotateZ((float) MathUtil.deg2rad(-obj.getRotZ())).scale(obj.getScaleXYZ()); }
}