package com.github.hyfloac.jglengine.util;

import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class ColorUtil
{
    public static Vector3f BLACK = new Vector3f(0.0f, 0.0f, 0.0f);
    public static Vector3f WHITE = new Vector3f(1.0f, 1.0f, 1.0f);
    public static Vector3f RED = new Vector3f(1.0f, 0.0f, 0.0f);
    public static Vector3f GREEN = new Vector3f(0.0f, 1.0f, 0.0f);
    public static Vector3f BLUE = new Vector3f(0.0f, 0.0f, 1.0f);
    public static Vector3f YELLOW = new Vector3f(1.0f, 1.0f, 0.0f);
    public static Vector3f PURPLE = new Vector3f(1.0f, 0.0f, 1.0f);
    public static Vector3f CYAN = new Vector3f(0.0f, 1.0f, 1.0f);

    /**
     * Converts three int's (0 - 255) to a {@link Vector3f} (values between 0.0f and 1.0f)
     */
    public static Vector3f toVec3f(int r, int g, int b) { return new Vector3f(((float) r) / 255.0F, ((float) g) / 255.0F, ((float) b) / 255.0F); }

    public static Vector3f toVec3f(Vector3i color) { return toVec3f(color.x, color.y, color.z); }

    public static Vector4f toVec4f(int r, int g, int b, int a) { return new Vector4f(toVec3f(r, g, b), ((float) a) / 255.0F); }

    public static Vector4f toVec4f(int r, int g, int b) { return new Vector4f(toVec3f(r, g, b), 1.0F); }

    public static Vector4f toVec4f(Vector3f color) { return new Vector4f(color, 1.0F); }

    public static Vector4f toVec4f(Vector3i color) { return toVec4f(color.x, color.y, color.z, 255); }

    public static Vector4f toVec4f(Vector4i color) { return toVec4f(color.x, color.y, color.z, color.w); }

    /**
     * Converts hex into an int[] of rgb values
     */
    public static int[] toRGB(int hex) { return new int[] { (hex & 0xff0000) >> 16, (hex & 0xff00) >> 8, hex & 0xff }; }

    /**
     * Converts a {@link Vector3f} (values between 0.0f and 1.0f) to an int[]
     */
    public static int[] toRGB(Vector3f rgb) { return new int[] { (int) (rgb.x * 255.0f), (int) (rgb.y * 255.0f), (int) (rgb.z * 255.0f) }; }

    /**
     * Converts RGB values into hex
     */
    public static int toHex(int r, int g, int b) { return ((r << 16) | (g << 8)) | b; }

    /**
     * Converts an int[] into hex
     */
    public static int toHex(int[] rgb) { return toHex(rgb[0], rgb[1], rgb[2]); }

    /**
     * Converts a {@link Vector3f} (values between 0.0f and 1.0f) to hex
     */
    public static int toHex(Vector3f rgb) { return toHex(toRGB(rgb)); }

    /**
     * Inverses RGB values
     */
    public static int[] inverse(int r, int g, int b) { return new int[] { 255 - r, 255 - g, 255 - b }; }

    /**
     * Inverses {@link Vector3f} (values between 0.0f and 1.0f) RGB values
     */
    public static int[] inverse(Vector3f rgb) { return inverse(toRGB(rgb)); }

    /**
     * Inverses int[] RGB values
     */
    public static int[] inverse(int[] rgb) { return inverse(rgb[0], rgb[1], rgb[2]); }

    /**
     * Inverses hex values
     */
    public static int[] inverse(int hex) { return inverse(toRGB(hex)); }
}