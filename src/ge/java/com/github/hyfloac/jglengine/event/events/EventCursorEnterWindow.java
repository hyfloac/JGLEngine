package com.github.hyfloac.jglengine.event.events;

public class EventCursorEnterWindow extends Event
{
    private boolean m_entered;

    public EventCursorEnterWindow() {}

    public EventCursorEnterWindow(boolean entered) { m_entered = entered; }

    public boolean hasEntered() { return m_entered; }
}