package com.github.hyfloac.jglengine.model;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.deconstructor.IDeconstructor;
import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.simplelog.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Texture implements IDeconstructor
{
    public final int texID;

    public Texture(int texID)
    {
        DeconstructionHandler.INSTANCE.addDeconstructor(this);
        this.texID = texID;
    }

    public Texture(String resource, boolean smooth, int size) { this(loadTexture(resource, smooth, size)); }

    public Texture(String resource, boolean smooth) { this(loadTexture(resource, smooth)); }

    public Texture(String resource) { this(loadTexture(resource)); }

    /**
     *
     * @param resource
     * @param smooth
     * @param size Num pixels in width
     * @return
     */
    public static int loadTexture(String resource, boolean smooth, int size)
    {
        /* Square size and compensate for pixel byte size */
        size *= size * 4;
        /* Load image data into buffer */
        ByteBuffer image = ResourceHelper.getBufferedTexture(resource, size);
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer components = BufferUtils.createIntBuffer(1);

        /* Ensure image has a proper format */
        if(!STBImage.stbi_info_from_memory(image, width, height, components)) { Logger.traceS(new RuntimeException("Failed to read image info: " + STBImage.stbi_failure_reason())); }

        /* Load image */
        ByteBuffer buffer = STBImage.stbi_load_from_memory(image, width, height, components, 0);
        if(buffer == null) { Logger.traceS(new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason())); }

        /* Setup texture with OpenGL */
        int texID = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texID);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        /* Should image have pixelated look (useful for things like minecraft's 16x16 textures */
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

        /* Determine if image has alpha layer */
        int type = GL11.GL_RGB;
        if(components.get(0) > 3) { type = GL11.GL_RGBA; }
        /* Create texture */
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, type, width.get(0), height.get(0), 0, type, GL11.GL_UNSIGNED_BYTE, buffer);

        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        return texID;
    }

    public static int loadTexture(String resource, boolean smooth)  { return loadTexture(resource, smooth, 1892); }

    public static int loadTexture(String resource) { return loadTexture(resource, true, 8192); }

    @Override
    public void finalize() { GL11.glDeleteTextures(texID); }
}