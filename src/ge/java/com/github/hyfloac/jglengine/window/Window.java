package com.github.hyfloac.jglengine.window;

import com.github.hyfloac.jglengine.event.EventBus;
import com.github.hyfloac.jglengine.event.events.*;
import com.github.hyfloac.jglengine.util.Log;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class Window
{
    public long windowHandle;
    public int width;
    public int height;

    public Window()
    {
    }

    public long createWindow(final String name, final int width, final int height, final int fps, final boolean fullscreen, final boolean vsync)
    {
        this.width = width;
        this.height = height;

        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, fps);

        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        windowHandle = GLFW.glfwCreateWindow(width, height, name, fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL);

        if(windowHandle == MemoryUtil.NULL) { Log.traceKill(new RuntimeException("Failed to create the GLFW window")); }
        if(!fullscreen)
        {
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(windowHandle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }
        GLFW.glfwMakeContextCurrent(windowHandle);
        GLFW.glfwSwapInterval(vsync ? 1 : 0);
        GLFW.glfwShowWindow(windowHandle);

        setupSettings();
        return windowHandle;
    }

    protected void setupSettings()
    {
        GL.createCapabilities();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glClearColor(0.7f, 1.0f, 0.3f, 0.0f);

        GLUtil.blending(true);
        GLUtil.depthTest(true);
        GLUtil.debugPolygons(false);
        GLUtil.backfaceCulling(true);

        GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> EventBus.INSTANCE.dispatch(new EventKeyInput(key, scancode, action, mods)));
        GLFW.glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> EventBus.INSTANCE.dispatch(new EventCursorMove(xpos, ypos)));
        GLFW.glfwSetCursorEnterCallback(windowHandle, (window, entered) -> EventBus.INSTANCE.dispatch(new EventCursorEnterWindow(entered)));
        GLFW.glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> EventBus.INSTANCE.dispatch(new EventMouseButton(button, action, mods)));

        GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> EventBus.INSTANCE.dispatch(new EventWindowResize(window, this.width, this.height, width, height)));

        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    public boolean isFocused() { return GLFW.glfwGetWindowAttrib(windowHandle, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE; }

    @Override
    public void finalize()
    {
        Callbacks.glfwFreeCallbacks(windowHandle);
        GLFW.glfwDestroyWindow(windowHandle);
    }
}
