package com.github.hyfloac.jglengine;

import com.github.hyfloac.jglengine.command.CLI;
import com.github.hyfloac.jglengine.config.Config;
import com.github.hyfloac.jglengine.player.Camera;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.renderer.FontRenderer;
import com.github.hyfloac.jglengine.renderer.Renderer;
import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.jglengine.shader.Shader;
import com.github.hyfloac.jglengine.shader.exception.ShaderException;
import com.github.hyfloac.jglengine.util.Debug;
import com.github.hyfloac.jglengine.util.Log;
import com.github.hyfloac.jglengine.window.Window;
import com.github.hyfloac.simplelog.Logger;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Game
{
    CLI cli;

    public Main()
    {
        super(new Window(), "Test0x00");
        this.cli = new CLI();
    }

    @Override
    protected void setupWindow()
    {
        new Config().init();
        Reference.WINDOW_HANDLE = super.window.createWindow("Hello World", Reference.WIDTH, Reference.HEIGHT, Reference.FPS, Reference.FULLSCREEN, Reference.VSYNC);
    }


    @Override
    protected void init()
    {
//        new Camera(new Vector3f(0.0f, 10.0f, 0.0f), new Vector3f(25.0f, 135.0f, 0.0f));
        new Camera(new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));

        try
        {
            Reference.SHADER = new Shader();
        }
        catch(ShaderException e)
        {
            e.printStackTrace();
        }

        try
        {
            Reference.SHADER.createShader(ResourceHelper.getShaderAsString("/VertexShader.glsl"), GL20.GL_VERTEX_SHADER);
            Reference.SHADER.createShader(ResourceHelper.getShaderAsString("/FragmentShader.glsl"), GL20.GL_FRAGMENT_SHADER);
            Reference.SHADER.link();
        }
        catch(ShaderException e) { Log.traceKill(e); }

        Debug.addDebugObjects(5, 100, true);
//        Renderer.INSTANCE.add(new PointLight(new Vector3f(ColorUtil.WHITE), new Vector3f(0.0f, 15.0f, 0.0f), 500.0f, new Attenuation(0.0f, 0.0f, 1.0f)));
    }

    @Override
    protected void update()
    {
        Reference.CAMERA.update();
        cli.update();
    }

    @Override
    protected void render()
    {
        Renderer.INSTANCE.render();
//        renderer.render();
    }

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException { Game.main(args, new Main()); }
}
