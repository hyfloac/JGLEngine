package com.github.hyfloac.jglengine.shader;

import com.github.hyfloac.jglengine.shader.exception.InvalidShaderTypeException;
import com.github.hyfloac.jglengine.shader.exception.ShaderCompilationException;
import com.github.hyfloac.jglengine.shader.exception.ShaderException;
import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.string.StringUtil;
import com.google.common.collect.ImmutableMap;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class Shader
{
    public final int programID;
    public final ShaderList shaderIDs;
    public final Map<String, Integer> uniforms;

    public Shader() throws ShaderException
    {
        programID = GL20.glCreateProgram();
        if(programID == 0) { throw new ShaderException("Unable to create shader program."); }
        shaderIDs = new ShaderList(programID);
        uniforms = new HashMap<String, Integer>();
    }

    /**
     * Used to initialize a shader.
     * This should read in, compile and attach the shader.
     *
     * @param path The path to the shader
     * @param type The type of shader. e.g. Fragment, Vertex, Geometry, etc
     * @return The program ID
     */
    @SuppressWarnings("UnusedReturnValue")
    public ShaderID createShader(final String path, final int type) throws ShaderException
    {
        if(!ShaderID.ShaderType.contains(type)) { throw new InvalidShaderTypeException(); }

        ShaderID.ShaderType shaderType = ShaderID.ShaderType.getByType(type);

        int shaderID = GL20.glCreateShader(type);
        if(shaderID == 0) { throw new ShaderException("Error creating shader."); }

        GL20.glShaderSource(shaderID, path);
        GL20.glCompileShader(shaderID);

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
        {
            String shaderError = GL20.glGetShaderInfoLog(shaderID);
            throw new ShaderCompilationException(new StringBuilder(68 + shaderError.length()).append("Error compiling shader, ShaderID: ").append(shaderID).append(",\n\tShader Type: `").append(shaderType).append("_SHADER`, Error: ").append(shaderError).toString());
        }

        GL20.glAttachShader(programID, shaderID);

        ShaderID out = new ShaderID(shaderType, shaderID);
        shaderIDs.set(out);
        return out;
    }

    /**
     * Used to bind the shader for rendering.
     */
    public void bind() { GL20.glUseProgram(getProgramID()); }

    /**
     * Used to unbind the shader from rendering.
     */
    public void unbind() { GL20.glUseProgram(0); }

    /**
     * Used to link and verify the shader.
     */
    public void link() throws ShaderException
    {
        GL20.glLinkProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS) == 0)
        {
            String error = GL20.glGetShaderInfoLog(programID, 1024);
            throw new ShaderException(new StringBuilder(23 + error.length()).append("Error linking program: ").append(error).toString());
        }

//        shaderIDs.link();

        GL20.glValidateProgram(programID);
        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
        {
            String tmp = GL20.glGetShaderInfoLog(programID);
            if(!tmp.trim().isEmpty()) { Logger.INSTANCE.warn("Warning validating program: " + tmp); }
        }
    }

    /**
     * Gets a list of all the uniform variables the program uses.
     *
     * @return A list of uniforms.
     */
    public Map<String, Integer> getUniforms() { return ImmutableMap.copyOf(uniforms); }

    /**
     * @return A the id of the shader program (NOT the individual id's of the shaders).
     */
    public int getProgramID() { return programID; }

    /**
     * @return An object which contains the ID's of the different shaders.
     */
    public ShaderList getShaderIDs() { return new ImmutableShaderList(shaderIDs); }

    /**
     * Get and store the address of a uniform variable in the shader.
     *
     * @param name The name of the uniform variable
     */
    public void createUniform(final String name)
    {
        if(uniforms.containsKey(name)) { return; }
        int uniformLocation = GL20.glGetUniformLocation(getProgramID(), name);
        int loc = GL20.glGetUniformLocation(getProgramID(), name);
        if(loc < 0) { Logger.traceS(new Exception("Could not find uniform variable with name " + name)); }
        uniforms.put(name, uniformLocation);
    }

    //region INT
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
    //endregion

    //region FLOAT
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
    //endregion

    //region MATRIX
    FloatBuffer m3f = BufferUtils.createFloatBuffer(9);
    FloatBuffer m4f = BufferUtils.createFloatBuffer(16);

    /**
     * This is an optimization thing.
     *
     * @return A pre-initialized {@link java.nio.FloatBuffer} for a Matrix3f.
     */
    public FloatBuffer getMatrix3fBuffer() { return m3f; }

    /**
     * This is an optimization thing.
     *
     * @return A pre-initialized {@link java.nio.FloatBuffer} for a Matrix4f.
     */
    public FloatBuffer getMatrix4fBuffer() { return m4f; }

    public void setUniform(final String name, final Matrix3f value)
    {
        FloatBuffer m3f = getMatrix3fBuffer();
        m3f.clear();
        createUniform(name);
        value.get(m3f);
        GL20.glUniformMatrix3fv(uniforms.get(name), false, m3f);
        m3f.clear();
    }

    public void setUniform(final String name, final Matrix4f value)
    {
        FloatBuffer m4f = getMatrix4fBuffer();
        m4f.clear();
        createUniform(name);
        value.get(m4f);
        GL20.glUniformMatrix4fv(uniforms.get(name), false, m4f);
        m4f.clear();
    }
    //endregion

    public void setUniform(final String name, final boolean value) { setUniform(name, (value ? 1 : 0)); }

    //region LIGHT
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

    public void setMaterialMini(final String name, final Light.Material mat)
    {
        setUniform(name + ".color", mat.color);
        setUniform(name + ".useColor", mat.useColor);
        setUniform(name + ".useTexture", mat.useTexture);
    }

    public void setUniform(final String name, final Light.Material mat)
    {
        setMaterialMini(name, mat);
        setUniform(name + ".useLight", mat.useLight);
        setUniform(name + ".reflectance", mat.reflectance);
    }
    //endregion

    @Override
    public void finalize()
    {
        unbind();
        if(programID != 0)
        {
            GL20.glDeleteProgram(programID);
        }
    }

    public static final class ShaderID
    {
        public ShaderType type;
        public int id;

        public ShaderID() {}

        public ShaderID(ShaderType type)
        {
            this.type = type;
        }

        public ShaderID(ShaderType type, int id)
        {
            this.type = type;
            this.id = id;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(id, type);
        }

        @Override
        public boolean equals(Object other)
        {
            return other != null && (this == other || this.getClass() == other.getClass() || (other instanceof ShaderID && this.id == ((ShaderID) other).id && this.type == ((ShaderID) other).type));
        }

        @Override
        public String toString()
        {
            return super.toString();
        }

        public enum ShaderType
        {
            VERTEX(GL20.GL_VERTEX_SHADER),
            FRAGMENT(GL20.GL_FRAGMENT_SHADER),
            GEOMETRY(GL32.GL_GEOMETRY_SHADER),
            TESSELATION_CONTROL(GL40.GL_TESS_CONTROL_SHADER),
            EVALUATION(GL40.GL_TESS_EVALUATION_SHADER),
            COMPUTE(GL43.GL_COMPUTE_SHADER);

            public final int type;

            ShaderType(final int type)
            {
                this.type = type;
            }

            public static final ShaderType[] VALUES = ShaderType.values();

            public static ShaderType getByType(final int type)
            {
                for(ShaderType st : VALUES)
                {
                    if(st.type == type) { return st; }
                }
                return null;
            }

            public static ShaderType getByName(final String name)
            {
                if(name == null || name.isEmpty()) { return null; }
                for(ShaderType st : VALUES)
                {
                    if(StringUtil.equals(st.name(), name)) { return st; }
                }
                return null;
            }

            public static boolean contains(final int type)
            {
                for(ShaderType st : VALUES)
                {
                    if(st.type == type) { return true; }
                }
                return false;
            }

            public static boolean contains(final String name)
            {
                if(name == null || name.isEmpty()) { return false; }
                for(ShaderType st : VALUES)
                {
                    if(StringUtil.equals(st.name(), name)) { return true; }
                }
                return false;
            }
        }
    }

    @SuppressWarnings("unused")
    public static class ShaderList
    {
        protected ShaderID[] shaders;
        protected final int programID;

        public ShaderList(ShaderID vertex, ShaderID fragment, ShaderID geometry, ShaderID tesselationControl, ShaderID evaluation, ShaderID compute, int programID)
        {
            shaders = new ShaderID[] { vertex, fragment, geometry, tesselationControl, evaluation, compute };
            this.programID = programID;
        }

        public ShaderList(int vertex, int fragment, int geometry, int tesselationControl, int evaluation, int compute, int programID)
        {
            this(new ShaderID(ShaderID.ShaderType.VERTEX, vertex),
                 new ShaderID(ShaderID.ShaderType.FRAGMENT, fragment),
                 new ShaderID(ShaderID.ShaderType.GEOMETRY, geometry),
                 new ShaderID(ShaderID.ShaderType.TESSELATION_CONTROL, tesselationControl),
                 new ShaderID(ShaderID.ShaderType.EVALUATION, evaluation),
                 new ShaderID(ShaderID.ShaderType.COMPUTE, compute), programID);
        }

        public ShaderList(int programID)
        {
            shaders = new ShaderID[6];
            this.programID = programID;
        }

        public void link()
        {
            for(ShaderID id : shaders)
            {
                if(id != null && id.id != 0)
                {
                    GL20.glDetachShader(programID, id.id);
                }
            }
        }

        public void set(ShaderID shaderID)
        {
            shaders[shaderID.type.ordinal()] = shaderID;
        }

        /**
         * @return An ShaderID array (size 6) of shader ids.
         */
        public ShaderID[] getShaders()
        {
            return shaders;
        }

        public ShaderID getVertexShader() { return shaders[0]; }

        public int getVertexShaderID() { return shaders[0].id; }

        public void setVertexShader(ShaderID id)
        {
            if(id.type != ShaderID.ShaderType.VERTEX) { throw new IllegalArgumentException("ShaderID must be of type Vertex."); }
            this.shaders[0] = id;
        }

        public void setVertexShader(int id) { this.shaders[0] = new ShaderID(ShaderID.ShaderType.VERTEX, id); }

        public ShaderID getFragmentShader() { return shaders[0]; }

        public int getFragmentShaderID() { return shaders[0].id; }

        public void setFragmentShader(ShaderID id)
        {
            if(id.type != ShaderID.ShaderType.FRAGMENT) { throw new IllegalArgumentException("ShaderID must be of type Fragment."); }
            this.shaders[0] = id;
        }

        public void setFragmentShader(int id) { this.shaders[0] = new ShaderID(ShaderID.ShaderType.FRAGMENT, id); }

        public ShaderID getGeometryShader() { return shaders[0]; }

        public int getGeometryShaderID() { return shaders[0].id; }

        public void setGeometryShader(ShaderID id)
        {
            if(id.type != ShaderID.ShaderType.GEOMETRY) { throw new IllegalArgumentException("ShaderID must be of type Geometry."); }
            this.shaders[0] = id;
        }

        public void setGeometryShader(int id) { this.shaders[0] = new ShaderID(ShaderID.ShaderType.GEOMETRY, id); }

        public ShaderID getTesselationControlShader() { return shaders[0]; }

        public int getTesselationControlShaderID() { return shaders[0].id; }

        public void setTesselationControlShader(ShaderID id)
        {
            if(id.type != ShaderID.ShaderType.TESSELATION_CONTROL) { throw new IllegalArgumentException("ShaderID must be of type TesselationControl."); }
            this.shaders[0] = id;
        }

        public void setTesselationControlShader(int id) { this.shaders[0] = new ShaderID(ShaderID.ShaderType.TESSELATION_CONTROL, id); }

        public ShaderID getEvaluationShader() { return shaders[0]; }

        public int getEvaluationShaderID() { return shaders[0].id; }

        public void setEvaluationShader(ShaderID id)
        {
            if(id.type != ShaderID.ShaderType.EVALUATION) { throw new IllegalArgumentException("ShaderID must be of type Evaluation."); }
            this.shaders[0] = id;
        }

        public void setEvaluationShader(int id) { this.shaders[0] = new ShaderID(ShaderID.ShaderType.EVALUATION, id); }

        public ShaderID getComputehader() { return shaders[0]; }

        public int getComputeShaderID() { return shaders[0].id; }

        public void setComputeShader(ShaderID id)
        {
            if(id.type != ShaderID.ShaderType.COMPUTE) { throw new IllegalArgumentException("ShaderID must be of type Compute."); }
            this.shaders[0] = id;
        }

        public void setComputeShader(int id) { this.shaders[0] = new ShaderID(ShaderID.ShaderType.COMPUTE, id); }

        @Override
        public void finalize()
        {
            if(programID != 0)
            {
                for(ShaderID id : shaders)
                {
                    if(id != null) { GL20.glDetachShader(programID, id.id); }
                }
            }
        }
    }

    public static final class ImmutableShaderList extends ShaderList
    {
        public ImmutableShaderList(ShaderID vertex, ShaderID fragment, ShaderID geometry, ShaderID tesselationControl, ShaderID evaluation, ShaderID compute) { super(vertex, fragment, geometry, tesselationControl, evaluation, compute, 0); }

        public ImmutableShaderList(int vertex, int fragment, int geometry, int tesselationControl, int evaluation, int compute) { super(vertex, fragment, geometry, tesselationControl, evaluation, compute, 0); }

        public ImmutableShaderList(ShaderList shaderList)
        {
            super(0);
            super.shaders = shaderList.shaders;
        }

        @Override
        public void set(ShaderID shaderID) {}

        @Override
        public void setVertexShader(ShaderID id) {}

        @Override
        public void setVertexShader(int id) {}

        @Override
        public void setFragmentShader(ShaderID id) {}

        @Override
        public void setFragmentShader(int id) {}

        @Override
        public void setGeometryShader(ShaderID id) {}

        @Override
        public void setGeometryShader(int id) {}

        @Override
        public void setTesselationControlShader(ShaderID id) {}

        @Override
        public void setTesselationControlShader(int id) {}

        @Override
        public void setEvaluationShader(ShaderID id) {}

        @Override
        public void setEvaluationShader(int id) {}

        @Override
        public void setComputeShader(ShaderID id) {}

        @Override
        public void setComputeShader(int id) {}

        @Override
        public void finalize() {}
    }
}
