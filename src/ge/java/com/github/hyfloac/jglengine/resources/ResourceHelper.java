package com.github.hyfloac.jglengine.resources;

import com.github.hyfloac.jglengine.Game;
import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.io.ResourceUtil;
import com.github.vitrifiedcode.javautilities.math.MathUtil;
import com.github.vitrifiedcode.javautilities.propterties.Properties;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import static com.github.vitrifiedcode.javautilities.io.ResourceUtil.getResource;

@SuppressWarnings("unused")
public final class ResourceHelper
{
    private ResourceHelper() {}

    private static String gameDataDir = null;

    static final boolean DEV_ENV = System.getProperty("devEnv") != null;

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getGameDataDir()
    {
        if(gameDataDir == null)
        {
            String path = Properties.OS.USER_HOME + "/Documents/My Games";
            if(DEV_ENV) { path = Properties.OS.USER_DIR + "/My Games"; }
            path += "/" + Game.instance().name;
            File f = new File(path);
            if(!f.exists()) { f.mkdirs(); }
            gameDataDir = path;
        }
        return gameDataDir;
    }

    private static String ensurePath(String path) { return path.startsWith("/") ? path : "/" + path; }

    public static InputStream getAsset(String resource)
    {
        try { return ResourceUtil.getResource("/assets" + ensurePath(resource)); }
        catch(IOException e) { Logger.traceS(e); }
        return null;
    }

    public static InputStream getFont(String resource) { return getAsset("/fonts" + ensurePath(resource)); }

    public static InputStream getModel(String resource) { return getAsset("/models" + ensurePath(resource)); }

    public static InputStream getShader(String resource) { return getAsset("/shaders" + ensurePath(resource)); }

    public static InputStream getTexture(String resource) { return getAsset("/textures" + ensurePath(resource)); }

    public static String getShaderAsString(String resource)
    {
        Scanner scanner = new Scanner(getShader(resource), "UTF-8");
        String out = scanner.useDelimiter("\\A").next();
        scanner.close();
        return out;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static ByteBuffer getBufferedResource(InputStream resource, int capacity)
    {
        try
        {
            ReadableByteChannel channel = Channels.newChannel(resource);
            ByteBuffer buffer = BufferUtils.createByteBuffer(capacity);
            while(channel.read(buffer) != -1) {}
            channel.close();
            resource.close();
            return (ByteBuffer) buffer.flip();
        }
        catch(IOException e) { Logger.traceS(e); }
        return null;
    }

    public static ByteBuffer getBufferedResource(InputStream resource) { return getBufferedResource(resource, 1024); }

    /**
     * SIZE_x: ((x * x) * 4)
     */
    private static final int SIZE_1 = 4;
    private static final int SIZE_2 = 16;
    private static final int SIZE_4 = 64;
    private static final int SIZE_8 = 256;
    private static final int SIZE_16 = 1024;
    private static final int SIZE_32 = 4096;
    private static final int SIZE_64 = 16_384;
    private static final int SIZE_128 = 65_536;
    private static final int SIZE_256 = 262_144;
    private static final int SIZE_512 = 1_048_576;
    private static final int SIZE_1024 = 4_194_304;
    private static final int SIZE_2048 = 16_777_216;
    private static final int SIZE_4096 = 67_108_864;
    private static final int SIZE_8192 = 268_435_456;

    public static ByteBuffer getBufferedTexture1(InputStream resource) { return getBufferedResource(resource, SIZE_1); }

    public static ByteBuffer getBufferedTexture2(InputStream resource) { return getBufferedResource(resource, SIZE_2); }

    public static ByteBuffer getBufferedTexture4(InputStream resource) { return getBufferedResource(resource, SIZE_4); }

    public static ByteBuffer getBufferedTexture8(InputStream resource) { return getBufferedResource(resource, SIZE_8); }

    public static ByteBuffer getBufferedTexture16(InputStream resource) { return getBufferedResource(resource, SIZE_16); }

    public static ByteBuffer getBufferedTexture32(InputStream resource) { return getBufferedResource(resource, SIZE_32); }

    public static ByteBuffer getBufferedTexture64(InputStream resource) { return getBufferedResource(resource, SIZE_64); }

    public static ByteBuffer getBufferedTexture128(InputStream resource) { return getBufferedResource(resource, SIZE_128); }

    public static ByteBuffer getBufferedTexture256(InputStream resource) { return getBufferedResource(resource, SIZE_256); }

    public static ByteBuffer getBufferedTexture512(InputStream resource) { return getBufferedResource(resource, SIZE_512); }

    public static ByteBuffer getBufferedTexture1024(InputStream resource) { return getBufferedResource(resource, SIZE_1024); }

    public static ByteBuffer getBufferedTexture2048(InputStream resource) { return getBufferedResource(resource, SIZE_2048); }

    public static ByteBuffer getBufferedTexture4096(InputStream resource) { return getBufferedResource(resource, SIZE_4096); }

    public static ByteBuffer getBufferedTexture8192(InputStream resource) { return getBufferedResource(resource, SIZE_8192); }

    public static ByteBuffer getBufferedResource(String resource, int capacity)
    {
        try { return getBufferedResource(ResourceUtil.getResource(resource), capacity); }
        catch(IOException e) { Logger.traceS(e); }
        return null;
    }

    public static ByteBuffer getBufferedTexture(String resource, int size)
    {
        if(!MathUtil.isPowerOfTwo(size) && !MathUtil.isPowerOfTwo(size / 4)) { Logger.INSTANCE.warn("Texture: `" + resource + "` has a size which is not a power of 2."); }
        return getBufferedResource(getTexture(resource), size);
    }

    public static ByteBuffer getBufferedTexture1(String resource) { return getBufferedResource(getTexture(resource), SIZE_1); }

    public static ByteBuffer getBufferedTexture2(String resource) { return getBufferedResource(getTexture(resource), SIZE_2); }

    public static ByteBuffer getBufferedTexture4(String resource) { return getBufferedResource(getTexture(resource), SIZE_4); }

    public static ByteBuffer getBufferedTexture8(String resource) { return getBufferedResource(getTexture(resource), SIZE_8); }

    public static ByteBuffer getBufferedTexture16(String resource) { return getBufferedResource(getTexture(resource), SIZE_16); }

    public static ByteBuffer getBufferedTexture32(String resource) { return getBufferedResource(getTexture(resource), SIZE_32); }

    public static ByteBuffer getBufferedTexture64(String resource) { return getBufferedResource(getTexture(resource), SIZE_64); }

    public static ByteBuffer getBufferedTexture128(String resource) { return getBufferedResource(getTexture(resource), SIZE_128); }

    public static ByteBuffer getBufferedTexture256(String resource) { return getBufferedResource(getTexture(resource), SIZE_256); }

    public static ByteBuffer getBufferedTexture512(String resource) { return getBufferedResource(getTexture(resource), SIZE_512); }

    public static ByteBuffer getBufferedTexture1024(String resource) { return getBufferedResource(getTexture(resource), SIZE_1024); }

    public static ByteBuffer getBufferedTexture2048(String resource) { return getBufferedResource(getTexture(resource), SIZE_2048); }

    public static ByteBuffer getBufferedTexture4096(String resource) { return getBufferedResource(getTexture(resource), SIZE_4096); }

    public static ByteBuffer getBufferedTexture8192(String resource) { return getBufferedResource(getTexture(resource), SIZE_8192); }

}