package com.github.hyfloac.jglengine.event.events;

@SuppressWarnings("unused")
public class Event
{
    protected final boolean cancelable;
    public boolean canceled;

    public Event(boolean cancelable)
    {
        this.cancelable = cancelable;
        this.canceled = false;
    }

    public Event()
    {
        this(false);
    }

    public void cancel()
    {
        if(cancelable) { canceled = true; }
    }
}