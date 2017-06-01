package com.github.hyfloac.jglengine.event.events;

public class EventCursorMove extends Event
{
    private double m_x;
    private double m_y;

    public EventCursorMove() {}

    public EventCursorMove(double x, double y)
    {
        m_x = x;
        m_y = y;
    }

    public double getX() { return m_x; }

    public double getY() { return m_y; }
}