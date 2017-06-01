package com.github.hyfloac.jglengine.event.events;

public class EventExit extends Event
{
    private int m_statusCode;

    public EventExit() { this(0); }

    public EventExit(int statusCode) { m_statusCode = statusCode; }

    public int getStatusCode() { return m_statusCode; }
}