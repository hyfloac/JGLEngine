package com.github.hyfloac.jglengine.util;

import com.github.hyfloac.jglengine.event.EventBus;
import com.github.hyfloac.jglengine.event.events.EventExit;

public class Log
{
    public static void traceKill(Exception e)
    {
        com.github.hyfloac.simplelog.Logger.traceS(e);
        EventBus.INSTANCE.dispatch(new EventExit(-1));
    }

    public static <T> void traceKill(Exception e, T msg)
    {
        com.github.hyfloac.simplelog.Logger.traceS(e, msg);
        EventBus.INSTANCE.dispatch(new EventExit(-1));
    }
}
