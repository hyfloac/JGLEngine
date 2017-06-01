package com.github.hyfloac.jglengine.shader.exception;

public class InvalidShaderTypeException extends ShaderException
{
    public InvalidShaderTypeException()
    {
        super();
    }

    public InvalidShaderTypeException(String message)
    {
        super(message);
    }

    public InvalidShaderTypeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public InvalidShaderTypeException(Throwable cause)
    {
        super(cause);
    }

    protected InvalidShaderTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String toString()
    {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? new StringBuilder(s.length() + 197 + message.length()).append(s).append("(Shader must be of type `GL20.GL_VERTEX_SHADER`, `GL20.GL_FRAGMENT_SHADER`, `GL32.GL_GEOMETRY_SHADER`, `GL40.GL_TESS_CONTROL_SHADER`, `GL40.GL_TESS_EVALUATION_SHADER` or `GL43.GL_COMPUTE_SHADER`): ").append(message).toString() : s;
    }
}
