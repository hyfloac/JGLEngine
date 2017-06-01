package com.github.hyfloac.jglengine.reference;

import com.github.hyfloac.jglengine.player.Camera;
import com.github.hyfloac.jglengine.shader.Shader;

public final class Reference
{
    private Reference() {}

    public static boolean DEV_ENVIRONMENT = false;
    public static boolean DEBUG_MODE = false;
    public static long WINDOW_HANDLE;
    public static Camera CAMERA;
    public static Shader SHADER;
    public static Shader TEXT_SHADER;

    public static int FPS;
    public static boolean VSYNC;
    public static String AA;
    public static String RESOLUTION;
    public static int WIDTH;
    public static int HEIGHT;
    public static float ASPECT_RATIO;
    public static boolean FULLSCREEN;
    public static float FOV;
    public static float VIEW_DISTANCE;

    public static void deconstruct()
    {
        CAMERA = null;
        SHADER = null;
        AA = null;
        RESOLUTION = null;
    }
}
