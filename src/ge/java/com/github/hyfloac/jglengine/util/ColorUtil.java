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

    //region To Vector
    //region Vec3f

    /**
     * Converts three int's (0 - 255) to a {@link org.joml.Vector3f} (values between 0.0f and 1.0f)
     */
    public static Vector3f toVec3f(int r, int g, int b)
    {
        return new Vector3f(((float) r) / 255.0F, ((float) g) / 255.0F, ((float) b) / 255.0F);
    }

    public static Vector3f toVec3f(byte r, byte g, byte b)
    {
        return new Vector3f(((float) r) / 255.0F, ((float) g) / 255.0F, ((float) b) / 255.0F);
    }

    public static Vector3f toVec3f(Vector3i color)
    {
        return toVec3f(color.x, color.y, color.z);
    }
    //endregion

    //region Vec4f
    public static Vector4f toVec4f(int r, int g, int b, int a)
    {
        return new Vector4f(toVec3f(r, g, b), ((float) a) / 255.0F);
    }

    public static Vector4f toVec4f(int r, int g, int b)
    {
        return new Vector4f(toVec3f(r, g, b), 1.0F);
    }

    public static Vector4f toVec4f(byte r, byte g, byte b)
    {
        return new Vector4f(toVec3f(r, g, b), 1.0F);
    }

    public static Vector4f toVec4f(byte r, byte g, byte b, byte a)
    {
        return new Vector4f(toVec3f(r, g, b), ((float) a) / 255.0F);
    }

    public static Vector4f toVec4f(Vector3f color)
    {
        return new Vector4f(color, 1.0F);
    }

    public static Vector4f toVec4f(Vector3i color)
    {
        return toVec4f(color.x, color.y, color.z, 255);
    }

    public static Vector4f toVec4f(Vector4i color)
    {
        return toVec4f(color.x, color.y, color.z, color.w);
    }
    //endregion
    //endregion

    //region To Array
    /**
     * Converts hex into an int[] of rgb values
     */
    public static int[] toRGB(int hex)
    {
        return new int[] { (hex & 0xff0000) >> 16, (hex & 0xff00) >> 8, hex & 0xff };
    }

    /**
     * Converts a {@link org.joml.Vector3f} (values between 0.0f and 1.0f) to an int[]
     */
    public static int[] toRGB(Vector3f rgb)
    {
        return new int[] { (int) (rgb.x * 255.0f), (int) (rgb.y * 255.0f), (int) (rgb.z * 255.0f) };
    }

    /**
     * Converts a {@link org.joml.Vector3i} (values between 0 and 255) to an int[]
     */
    public static int[] toRGB(Vector3i rgb)
    {
        return new int[] { rgb.x, rgb.y, rgb.z };
    }

    /**
     * Converts hex into an int[] of rgba values
     */
    public static int[] toRGBA(int hex)
    {
        return new int[] { (hex & 0xFF000000) >> 24, (hex & 0xFF0000) >> 16, (hex & 0xFF00) >> 8, hex & 0xFF };
    }

    /**
     * Converts a {@link org.joml.Vector4f} (values between 0.0f and 1.0f) to an int[]
     */
    public static int[] toRGBA(Vector4f rgba)
    {
        return new int[] { (int) (rgba.x * 255.0F), (int) (rgba.y * 255.0F), (int) (rgba.z * 255.0F), (int) (rgba.w * 255.0F) };
    }

    /**
     * Converts a {@link org.joml.Vector4i} (values between 0 and 255) to an int[]
     */
    public static int[] toRGBA(Vector4i rgba)
    {
        return new int[] { rgba.x, rgba.y, rgba.z, rgba.w };
    }
    //endregion

    //region To Hex
    /**
     * Converts RGBA values into hex
     */
    public static int toHex(byte r, byte g, byte b, byte a)
    {
        return (((r << 24) | (g << 16)) | (b << 8)) | a;
    }

    /**
     * Converts RGB values into hex
     */
    public static int toHex(byte r, byte g, byte b)
    {
        return toHex(r, g, b, (byte) 0xFF);
    }

    /**
     * Converts RGBA values into hex
     */
    public static int toHex(int r, int g, int b, int a)
    {
        return toHex((byte) (r % 256), (byte) (g % 256), (byte) (b % 256), (byte) (a % 256));
    }

    /**
     * Converts RGB values into hex
     */
    public static int toHex(int r, int g, int b)
    {
        return toHex((byte) (r % 256), (byte) (g % 256), (byte) (b % 256), (byte) 0xFF);
    }

    /**
     * Converts an int[] into hex
     */
    public static int toHex(int[] rgb)
    {
        if(rgb.length != 3 && rgb.length != 4) { throw new IllegalArgumentException("Array must have 3 or 4 elements."); }
        return toHex(rgb[0], rgb[1], rgb[2], rgb.length == 4 ? rgb[3] : 255);
    }

    /**
     * Converts a {@link org.joml.Vector3f} (values between 0.0f and 1.0f) to hex
     */
    public static int toHex(Vector3f rgb)
    {
        return toHex(toRGB(rgb));
    }

    /**
     * Converts a {@link org.joml.Vector3i} (values between 0.0f and 1.0f) to hex
     */
    public static int toHex(Vector3i rgb)
    {
        return toHex(toRGB(rgb));
    }

    /**
     * Converts a {@link org.joml.Vector4f} (values between 0.0f and 1.0f) to hex
     */
    public static int toHex(Vector4f rgb)
    {
        return toHex(toRGBA(rgb));
    }

    /**
     * Converts a {@link org.joml.Vector4i} (values between 0.0f and 1.0f) to hex
     */
    public static int toHex(Vector4i rgb)
    {
        return toHex(toRGBA(rgb));
    }
    //endregion

    //region Inverse
    /**
     * Inverses RGB values
     */
    public static int[] inverse(int r, int g, int b)
    {
        return new int[] { 255 - r, 255 - g, 255 - b };
    }

    /**
     * Inverses RGB values
     */
    public static int[] inverse(int r, int g, int b, int a)
    {
        return new int[] { 255 - r, 255 - g, 255 - b, 255 - a };
    }

    /**
     * Inverses {@link org.joml.Vector3f} (values between 0.0f and 1.0f) RGB values
     */
    public static int[] inverse(Vector3f rgb)
    {
        return inverse(toRGB(rgb));
    }

    /**
     * Inverses {@link org.joml.Vector3i} (values between 0 and 255) RGB values
     */
    public static int[] inverse(Vector3i rgb)
    {
        return inverse(toRGB(rgb));
    }

    /**
     * Inverses {@link org.joml.Vector4f} (values between 0.0f and 1.0f) RGB values
     */
    public static int[] inverse(Vector4f rgb)
    {
        return inverse(toRGBA(rgb));
    }

    /**
     * Inverses {@link org.joml.Vector4i} (values between 0 and 255) RGB values
     */
    public static int[] inverse(Vector4i rgb)
    {
        return inverse(toRGBA(rgb));
    }

    /**
     * Inverses int[] RGB(A) values
     */
    public static int[] inverse(int[] rgb)
    {
        if(rgb.length != 3 && rgb.length != 4) { throw new IllegalArgumentException("Array must have 3 or 4 elements."); }
        return inverse(rgb[0], rgb[1], rgb[2], rgb.length == 4 ? rgb[3] : 255);
    }

    /**
     * Inverses hex values
     */
    public static int[] inverse(int hex)
    {
        return inverse(toRGBA(hex));
    }
    //endregion

    //region Interpolate
    public static Vector3f interpolate(Vector3f left, Vector3f right)
    {
        return new Vector3f(left).add(right).div(2.0F);
    }
    //endregion
}