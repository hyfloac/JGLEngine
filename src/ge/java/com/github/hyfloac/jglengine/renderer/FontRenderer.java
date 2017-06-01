package com.github.hyfloac.jglengine.renderer;

import com.github.hyfloac.jglengine.font.Char;
import com.github.hyfloac.jglengine.font.Text;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.tick.ITickable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class FontRenderer implements ITickable
{
    private List<Text> texts = new ArrayList<>();

    public static float x = 0.0F, y = 0.0F;

    @Override
    public void render()
    {
        prepareRender();

        Reference.TEXT_SHADER.setUniform("textureSampler", 0);
        Reference.TEXT_SHADER.setUniform("projectionMatrix", Reference.CAMERA.getProjectionMatrix());
        for(Text t : texts)
        {
            for(Char c : t)
            {
                c.model.prepareRender();
                Reference.TEXT_SHADER.setMaterialMini("material", c.model.getMaterial());
                Reference.TEXT_SHADER.setUniform("translation", new Vector2f(x, y));
                Reference.TEXT_SHADER.setUniform("modelViewMatrix", new Matrix4f().identity().translate(new Vector3f(x, y, 0.0f)).scale(new Vector3f(1.0F)));
                c.model.render();
                c.model.finishRender();
            }
        }

        finishRender();
    }

    public void prepareRender()
    {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        Reference.TEXT_SHADER.bind();
    }

    public void finishRender()
    {
        Reference.TEXT_SHADER.unbind();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
    }

    public void add(Text t)
    {
        texts.add(t);
    }

    @Override
    public void finalize()
    {

    }
}
