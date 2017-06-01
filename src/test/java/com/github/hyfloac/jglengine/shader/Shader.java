package com.github.hyfloac.jglengine.shader;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.deconstructor.IDeconstructor;
import com.github.hyfloac.jglengine.util.Log;
import com.github.hyfloac.simplelog.Logger;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class Shader implements IDeconstructor
{
    public final int programID;
    public int vertexShaderID;
    public int fragmentShaderID;
    public final Map<String, Integer> uniforms;

    public Shader()
    {
        DeconstructionHandler.INSTANCE.addDeconstructor(this);
        programID = GL20.glCreateProgram();
        if(programID == 0) { Log.traceKill(new Exception("Unable to create shader.")); }
        uniforms = new HashMap<String, Integer>();
    }

    public void createShader(final String path, final int type)
    {
        if(!(type == GL20.GL_VERTEX_SHADER || type == GL20.GL_FRAGMENT_SHADER)) { Log.traceKill(new Exception("Invalid shader type, must be `GL20.GL_VERTEX_SHADER` or `GL20.GL_FRAGMENT_SHADER`")); }

        int shaderID = GL20.glCreateShader(type);
        if(shaderID == 0) { Log.traceKill(new Exception("Error creating shader.")); }

        GL20.glShaderSource(shaderID, path);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0) { Log.traceKill(new Exception("Error compiling shader, ShaderID: " + shaderID + ",\n\tShader Type: `" + (type == GL20.GL_VERTEX_SHADER ? "VERTEX" : "FRAGMENT") + "_SHADER`, Error:" + GL20.glGetShaderInfoLog(shaderID))); }

        GL20.glAttachShader(programID, shaderID);

        if(type == GL20.GL_VERTEX_SHADER) { vertexShaderID = shaderID; }
        else { fragmentShaderID = shaderID; }
    }

    public void createUniform(final String name)
    {
        if(uniforms.containsKey(name)) { return; }
        int uniformLocation = GL20.glGetUniformLocation(programID, name);
        if(uniformLocation < 0) { Logger.traceS(new Exception("Could not find uniform variable with name " + name)); }
        uniforms.put(name, uniformLocation);
    }

    /* INT */
    public void setUniform(final String name, final int value)
    {
        createUniform(name);
        GL20.glUniform1i(uniforms.get(name), value);
    }

    public void setUniform(final String name, final Vector2i value)
    {
        createUniform(name);
        GL20.glUniform2i(uniforms.get(name), value.x, value.y);
    }

    public void setUniform(final String name, final Vector3i value)
    {
        createUniform(name);
        GL20.glUniform3i(uniforms.get(name), value.x, value.y, value.z);
    }

    public void setUniform(final String name, final Vector4i value)
    {
        createUniform(name);
        GL20.glUniform4i(uniforms.get(name), value.x, value.y, value.z, value.w);
    }
    /* ~INT */

    /* FLOAT */
    public void setUniform(final String name, final float value)
    {
        createUniform(name);
        GL20.glUniform1f(uniforms.get(name), value);
    }

    public void setUniform(final String name, final Vector2f value)
    {
        createUniform(name);
        GL20.glUniform2f(uniforms.get(name), value.x, value.y);
    }

    public void setUniform(final String name, final Vector3f value)
    {
        createUniform(name);
        GL20.glUniform3f(uniforms.get(name), value.x, value.y, value.z);
    }

    public void setUniform(final String name, final Vector4f value)
    {
        createUniform(name);
        GL20.glUniform4f(uniforms.get(name), value.x, value.y, value.z, value.w);
    }
    /* ~FLOAT */

    /* MATRIX */
    public void setUniform(final String name, final Matrix3f value)
    {
        createUniform(name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(9);
        value.get(buffer);
        GL20.glUniformMatrix3fv(uniforms.get(name), false, buffer);
    }

    public void setUniform(final String name, final Matrix4f value)
    {
        createUniform(name);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(16);
        value.get(buffer);
        GL20.glUniformMatrix4fv(uniforms.get(name), false, buffer);
        buffer.clear();
    }
    /* ~MATRIX */

    public void setUniform(final String name, final boolean value) { setUniform(name, (value ? 1 : 0)); }

    /* LIGHT */
    public void setUniform(final String name, final Light.Attenuation att)
    {
        setUniform(name + ".constant", att.constant);
        setUniform(name + ".linear", att.linear);
        setUniform(name + ".exponent", att.exponent);
    }

    public void setUniform(final String name, final Light.PointLight light)
    {
        setUniform(name + ".color", light.color);
        setUniform(name + ".position", light.position);
        setUniform(name + ".intensity", light.intensity);
        setUniform(name + ".att", light.att);
    }

    public void setUniform(final String name, final Light.Material mat)
    {
        setUniform(name + ".color", mat.color);
        setUniform(name + ".useColor", mat.useColor);
        setUniform(name + ".useTexture", mat.useTexture);
        setUniform(name + ".useLight", mat.useLight);
        setUniform(name + ".reflectance", mat.reflectance);
    }
    /* ~LIGHT */

    public void link()
    {
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0) { Logger.traceS(new Exception("Error linking program: " + GL20.glGetShaderInfoLog(programID, 1024))); }

        GL20.glValidateProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
        {
            String tmp = GL20.glGetShaderInfoLog(programID);
            if(!tmp.isEmpty()) { Logger.INSTANCE.warn("Warning validating program: " + tmp); }
        }
    }

    public void bind() { GL20.glUseProgram(programID); }

    public void unbind() { GL20.glUseProgram(0); }

    @Override
    public void finalize()
    {
        unbind();
        if(programID != 0)
        {
            if(vertexShaderID != 0) { GL20.glDetachShader(programID, vertexShaderID); }
            if(fragmentShaderID != 0) { GL20.glDetachShader(programID, fragmentShaderID); }
            GL20.glDeleteProgram(programID);
        }
    }
}