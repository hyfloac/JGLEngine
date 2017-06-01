package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.simplelog.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture
{
    public final int texID;

    public Texture(int texID)
    {
        this.texID = texID;
    }

    public Texture(String resource, boolean smooth, int size) { this(loadTexture(resource, smooth, size)); }

    public Texture(String resource, boolean smooth) { this(loadTexture(resource, smooth)); }

    public Texture(String resource) { this(loadTexture(resource)); }

    private static final IntBuffer width = BufferUtils.createIntBuffer(1);
    private static final IntBuffer height = BufferUtils.createIntBuffer(1);
    private static final IntBuffer components = BufferUtils.createIntBuffer(1);

    public static int loadTexture(String resource, boolean smooth, int size)
    {
        size *= size * 4; // Yes, this is syntactically correct.
        ByteBuffer image = ResourceHelper.getBufferedTexture(resource, size);

        if(!STBImage.stbi_info_from_memory(image, width, height, components)) { Logger.traceS(new RuntimeException("Failed to read image info: " + STBImage.stbi_failure_reason())); }

        ByteBuffer buffer = STBImage.stbi_load_from_memory(image, width, height, components, 0);
        if(buffer == null) { Logger.traceS(new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason())); }

        int texID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        if(smooth)
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        }
        else
        {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        }

        int type = GL11.GL_RGB;
        if(components.get(0) > 3) { type = GL11.GL_RGBA; }
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, type, width.get(0), height.get(0), 0, type, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        return texID;
    }

    public static int loadTexture(String resource, boolean smooth) { return loadTexture(resource, smooth, 8192); }

    public static int loadTexture(String resource) { return loadTexture(resource, true, 8192); }

    @Override
    public void finalize() { GL11.glDeleteTextures(texID); }
}