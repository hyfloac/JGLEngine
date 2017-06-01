package com.github.hyfloac.jglengine.player;

import com.github.hyfloac.jglengine.axis.IRotatable;
import com.github.hyfloac.jglengine.axis.ITranslatable;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.vitrifiedcode.javautilities.math.MathUtil;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera implements ITranslatable, IRotatable
{
    private static final float ROT_INCREMENT = 9.0f;
    private static final float POS_INCREMENT = 0.003f;
    private static final float VIEW_RANGE = ROT_INCREMENT * 1.05f;
    private static final Vector3f X_AXIS = new Vector3f(1.0F, 0.0F, 0.0F);
    private static final Vector3f Y_AXIS = new Vector3f(0.0F, 1.0F, 0.0F);

    private Vector3f m_pos;
    private Vector3f m_rot;
    private Vector3f m_posUpdate;
    private Vector3f m_rotUpdate;
    private Vector3f prevPos;
    private Vector3f prevRot;
    private Matrix4f m_projectionMatrix;
    private Matrix4f m_viewMatrix;
    private boolean m_moveX = false;
    private boolean m_moveY = false;
    private boolean m_moveZ = false;
    private float m_movementSpeed;


    public Camera(Vector3f pos, Vector3f rot)
    {
        this.m_pos = pos;
        this.m_rot = rot;
        this.m_posUpdate = new Vector3f(0.0f, 0.0f, 0.0f);
        this.m_rotUpdate = new Vector3f(0.0f, 0.0f, 0.0f);
        this.prevPos = new Vector3f(0, 0, 0);
        this.prevRot = new Vector3f(0, 0, 0);
        this.m_viewMatrix = new Matrix4f().identity().rotate((float) MathUtil.deg2rad(m_rot.x), X_AXIS).rotate((float) MathUtil.deg2rad(m_rot.y), Y_AXIS).translate(-m_pos.x, -m_pos.y, -m_pos.z);
        this.updateProjectionMatrix();
        this.m_movementSpeed = 1.0F;
        Reference.CAMERA = this;
    }

    public void moveX(float inc, boolean move)
    {
        m_moveX = move;
        m_posUpdate.x = inc;
    }

    public void moveY(float inc, boolean move)
    {
        m_moveY = move;
        m_posUpdate.y = inc;
    }

    public void moveZ(float inc, boolean move)
    {
        m_moveZ = move;
        m_posUpdate.z = inc;
    }

    public void update()
    {
        /* Move along X axis while taking current rotation into account */
        if(m_posUpdate.x != 0)
        {
            m_pos.x += MathUtil.sin((float) MathUtil.deg2rad(m_rot.y - 90)) * -m_posUpdate.x;
            m_pos.z += MathUtil.cos((float) MathUtil.deg2rad(m_rot.y - 90)) * m_posUpdate.x;
        }

        /* Move along Z axis while taking current rotation into account */
        if(m_posUpdate.z != 0)
        {
            m_pos.x += MathUtil.sin((float) MathUtil.deg2rad(m_rot.y)) * -m_posUpdate.z;
            m_pos.z += MathUtil.cos((float) MathUtil.deg2rad(m_rot.y)) * m_posUpdate.z;
        }

        /* Move along Y axis */
        m_pos.y += m_posUpdate.y;

        /* Gradually slow down */
        if(m_posUpdate.x < 0.0f && !m_moveX) { m_posUpdate.x += POS_INCREMENT * (m_movementSpeed); }
        else if(m_posUpdate.x > 0.0f && !m_moveX) { m_posUpdate.x -= POS_INCREMENT * (m_movementSpeed); }
        if(m_posUpdate.y < 0.0f && !m_moveY) { m_posUpdate.y += POS_INCREMENT * (m_movementSpeed); }
        else if(m_posUpdate.y > 0.0f && !m_moveY) { m_posUpdate.y -= POS_INCREMENT * (m_movementSpeed); }
        if(m_posUpdate.z < 0.0f && !m_moveZ) { m_posUpdate.z += POS_INCREMENT * (m_movementSpeed); }
        else if(m_posUpdate.z > 0.0f && !m_moveZ) { m_posUpdate.z -= POS_INCREMENT * (m_movementSpeed); }
        if(m_posUpdate.x > -0.03f && m_posUpdate.x < 0.03f) { m_posUpdate.x = 0.0f; }
        if(m_posUpdate.y > -0.03f && m_posUpdate.y < 0.03f) { m_posUpdate.y = 0.0f; }
        if(m_posUpdate.z > -0.03f && m_posUpdate.z < 0.03f) { m_posUpdate.z = 0.0f; }

        /* Turn view */
        m_rot.x += m_rotUpdate.x;
        m_rot.y += m_rotUpdate.y;

        /* limit the amount of degrees Up and Down the camera can turn */
        if(m_rot.x < -85.0f) { m_rot.x = -85.0f; }
        if(m_rot.x > 85.0f) { m_rot.x = 85.0f; }
        /* Ensures the Left/Right views are always within the bounds of 0 - 360 (will not be noticeable from a players perspective) */
        m_rot.y %= 360;

        /* Gradually slow down */
        if(m_rotUpdate.x < 0.0f) { m_rotUpdate.x += ROT_INCREMENT; }
        else if(m_rotUpdate.x > 0.0f) { m_rotUpdate.x -= ROT_INCREMENT; }
        if(m_rotUpdate.y < 0.0f) { m_rotUpdate.y += ROT_INCREMENT; }
        else if(m_rotUpdate.y > 0.0f) { m_rotUpdate.y -= ROT_INCREMENT; }
        if(m_rotUpdate.x >= -VIEW_RANGE && m_rotUpdate.x <= VIEW_RANGE) { m_rotUpdate.x = 0.0f; }
        if(m_rotUpdate.y >= -VIEW_RANGE && m_rotUpdate.y <= VIEW_RANGE) { m_rotUpdate.y = 0.0f; }
    }

    public float getMovementSpeed()
    {
        return m_movementSpeed / 20;
    }

    public void setMovementSpeed(float movementSpeed)
    {
        m_movementSpeed = movementSpeed * 20;
    }

    public void incrementMovementSpeed(float increment)
    {
        m_movementSpeed = ((m_movementSpeed / 20) + increment) * 20;
    }

    public Matrix4f updateViewMatrix()
    {
        if(!prevPos.equals(m_pos) || !prevRot.equals(m_rot))
        {
            prevPos = new Vector3f(m_pos);
            prevRot = new Vector3f(m_rot);
            m_viewMatrix = m_viewMatrix.identity().rotate((float) MathUtil.deg2rad(m_rot.x), X_AXIS).rotate((float) MathUtil.deg2rad(m_rot.y), Y_AXIS).translate(-m_pos.x, -m_pos.y, -m_pos.z);
        }
        return m_viewMatrix;
    }

    public void updateProjectionMatrix() { m_projectionMatrix = new Matrix4f().identity().perspective((float) Math.toRadians(Reference.FOV), (float) Reference.WIDTH / (float) Reference.HEIGHT, 0.01f, Reference.VIEW_DISTANCE); }

    public void render() { Reference.SHADER.setUniform("projectionMatrix", m_projectionMatrix); }

    public Matrix4f getProjectionMatrix()
    {
        return m_projectionMatrix;
    }

    //region Position Getters & Setters
    @Override
    public void setX(float x) { m_pos.x = x; }

    @Override
    public void setY(float y) { m_pos.y = y; }

    @Override
    public void setZ(float z) { m_pos.z = z; }

    @Override
    public float getX() { return m_pos.x; }

    @Override
    public float getY() { return m_pos.y; }

    @Override
    public float getZ() { return m_pos.z; }

    @Override
    public void incrementX(float x) { m_posUpdate.x = x; }

    @Override
    public void incrementY(float y) { m_posUpdate.y = y; }

    @Override
    public void incrementZ(float z) { m_posUpdate.z = z; }

    @Override
    public void setXYZ(Vector3f xyz)
    {
        m_pos.x = xyz.x;
        m_pos.y = xyz.y;
        m_pos.z = xyz.z;
    }

    @Override
    public Vector3f getXYZ() { return m_pos; }

    @Override
    public void setRotX(float x) { m_rot.x = x; }

    @Override
    public void setRotY(float y) { m_rot.y = y; }

    @Override
    public void setRotZ(float z) { m_rot.z = z; }

    @Override
    public float getRotX() { return m_rot.x; }

    @Override
    public float getRotY() { return m_rot.y; }

    @Override
    public float getRotZ() { return m_rot.z; }

    @Override
    public void incrementRotX(float x) { m_rotUpdate.x += x; }

    @Override
    public void incrementRotY(float y) { m_rotUpdate.y += y; }

    @Override
    public void incrementRotZ(float z) { m_rotUpdate.z += z; }

    @Override
    public void setRotXYZ(Vector3f xyz)
    {
        m_rot.x = xyz.x;
        m_rot.y = xyz.y;
        m_rot.z = xyz.z;
    }

    @Override
    public Vector3f getRotXYZ() { return m_rot; }
    //endregion
}