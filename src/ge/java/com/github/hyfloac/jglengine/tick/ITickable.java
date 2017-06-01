package com.github.hyfloac.jglengine.tick;

public interface ITickable
{
    default void update() {}

    default void render() {}
}
