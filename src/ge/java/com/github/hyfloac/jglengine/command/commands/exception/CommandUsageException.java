package com.github.hyfloac.jglengine.command.commands.exception;

public class CommandUsageException extends Exception
{
    public CommandUsageException()
    {
        super();
    }

    public CommandUsageException(String message)
    {
        super(message);
    }

    public CommandUsageException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CommandUsageException(Throwable cause)
    {
        super(cause);
    }

    protected CommandUsageException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
