package com.github.hyfloac.jglengine.event.events;

public class EventKeyInput extends Event
{
    public final int key;
    public final int scancode;
    public final int action;
    public final int mods;

    public EventKeyInput(int key, int scancode, int action, int mods)
    {
        this.key = key;
        this.scancode = scancode;
        this.action = action;
        this.mods = mods;
    }

    public EventKeyInput() { this(0, 0, 0, 0); }
}