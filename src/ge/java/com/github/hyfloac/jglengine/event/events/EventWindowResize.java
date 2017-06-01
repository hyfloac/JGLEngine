package com.github.hyfloac.jglengine.event.events;

public class EventWindowResize extends Event
{
    public final long windowHandle;
    public final int oldWidth;
    public final int oldHeight;
    public final int width;
    public final int height;

    public EventWindowResize(long windowHandle, int oldWidth, int oldHeight, int width, int height)
    {
        this.windowHandle = windowHandle;
        this.oldWidth = oldWidth;
        this.oldHeight = oldHeight;
        this.width = width;
        this.height = height;
    }

    public EventWindowResize() { this(0, 0, 0, 0, 0); }
}
