package com.github.hyfloac.jglengine.shader.exception;

public class ShaderCompilationException extends ShaderException
{
    public ShaderCompilationException()
    {
        super();
    }

    public ShaderCompilationException(String message)
    {
        super(message);
    }

    public ShaderCompilationException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ShaderCompilationException(Throwable cause)
    {
        super(cause);
    }

    protected ShaderCompilationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
