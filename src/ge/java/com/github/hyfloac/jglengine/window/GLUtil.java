package com.github.hyfloac.jglengine.window;

import com.github.hyfloac.jglengine.util.ColorUtil;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public final class GLUtil
{
    private GLUtil() {}

    static
    {
        try { GL.getCapabilities(); }
        catch(Exception ignored) { GL.createCapabilities(); }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void setBackgroundColor(final Vector3f color) { GL11.glClearColor(color.x, color.y, color.z, 0.0F); }

    public static void setBackgroundColor(final Vector4f color) { GL11.glClearColor(color.x, color.y, color.z, color.w); }

    public static void setBackgroundColor(final Vector3i color) { setBackgroundColor(ColorUtil.toVec3f(color)); }

    public static void setBackgroundColor(Vector4i color) { setBackgroundColor(ColorUtil.toVec4f(color)); }

    private static boolean debugPolygons = false;
    public static void debugPolygons(boolean enabled)
    {
        debugPolygons = enabled;
        if(enabled) { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE); }
        else { GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL); }
    }

    public static void debugPolygons() { debugPolygons(!debugPolygons); }

    public static void blending(boolean enabled)
    {
        if(enabled) { GL11.glEnable(GL11.GL_BLEND); }
        else { GL11.glDisable(GL11.GL_BLEND); }
    }

    private static boolean backfaceCulling = false;
    public static void backfaceCulling(boolean enabled)
    {
        backfaceCulling = enabled;
        if(enabled) { GL11.glEnable(GL11.GL_CULL_FACE); }
        else { GL11.glDisable(GL11.GL_CULL_FACE); }
    }

    public static void backfaceCulling() { backfaceCulling(!backfaceCulling); }

    public static void depthTest(boolean enabled)
    {
        if(enabled) { GL11.glEnable(GL11.GL_DEPTH_TEST); }
        else { GL11.glDisable(GL11.GL_DEPTH_TEST); }
    }

    public static boolean cursorVisible = false;
    public static void cursorVisibility(boolean enabled, long windowHandle)
    {
        cursorVisible = enabled;
        if(cursorVisible) { GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_NORMAL); }
        else { GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED); }
    }

    public static void cursorVisibility(long windowHandle) { cursorVisibility(!cursorVisible, windowHandle); }

    public static void glfwWindowSetVersion(int major, int minor, int profile, boolean forwardCompatible)
    {
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, major);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, minor);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, profile);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, forwardCompatible ? GLFW.GLFW_TRUE : GLFW.GLFW_FALSE);
    }

    private static final int LOWER_BOUND = 64, UPPER_BOUND = 256, MULTIPLIER = 1, INCREMENTER = 1;
    private static int RED = LOWER_BOUND, GREEN = LOWER_BOUND, BLUE = LOWER_BOUND;
    private static boolean INC_RED = true, INC_GREEN = true, INC_BLUE = true;

    public static void smoothBackground()
    {
        if(INC_RED) { RED += INCREMENTER; }
        else { RED -= INCREMENTER; }

        if(RED == LOWER_BOUND) { INC_RED = true; }
        else if(RED == UPPER_BOUND) { INC_RED = false; }

        if(RED == LOWER_BOUND || RED == UPPER_BOUND)
        {
            if(INC_GREEN) { GREEN += INCREMENTER; }
            else { GREEN -= INCREMENTER; }

            if(GREEN == LOWER_BOUND) { INC_GREEN = true; }
            else if(GREEN == UPPER_BOUND) { INC_GREEN = false; }
            if(GREEN == LOWER_BOUND || GREEN == UPPER_BOUND)
            {
                if(INC_BLUE) { BLUE += INCREMENTER; }
                else { BLUE -= INCREMENTER; }

                if(BLUE == LOWER_BOUND) { INC_BLUE = true; }
                else if(BLUE == UPPER_BOUND) { INC_BLUE = false; }
            }
        }

        setBackgroundColor(new Vector3i(RED * MULTIPLIER, GREEN * MULTIPLIER, BLUE * MULTIPLIER));
    }
}
