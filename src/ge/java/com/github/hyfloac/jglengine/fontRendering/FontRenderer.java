package com.github.hyfloac.jglengine.fontRendering;

import com.github.hyfloac.jglengine.fontMeshCreator.GUIText;
import com.github.hyfloac.jglengine.shader.exception.ShaderException;
import com.github.hyfloac.jglengine.tick.ITickable;

public class FontRenderer implements ITickable
{
    private FontShader shader;

    public FontRenderer()
    {
        try
        {
            shader = new FontShader();
        }
        catch(ShaderException e)
        {
            e.printStackTrace();
        }
    }

    private void prepare() {}

    private void renderText(GUIText text) {}

    @Override
    public void render()
    {

    }

    private void endRendering() {}
}
