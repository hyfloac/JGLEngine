package com.github.hyfloac.jglengine;

import com.github.hyfloac.jglengine.config.Config;
import com.github.hyfloac.jglengine.player.Camera;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.renderer.Renderer;
import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.jglengine.util.Debug;
import com.github.hyfloac.jglengine.window.WindowHandler;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;

import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Game
{
    public Main() { super(new WindowHandler()); }

    @Override
    protected void setupWindow()
    {
        new Config().init();
        Reference.WINDOW_HANDLE = super.window.createWindow("Hello World", Reference.WIDTH, Reference.HEIGHT, Reference.FPS, Reference.FULLSCREEN);
    }

    @Override
    protected void init()
    {
        /* Create camera object */
        new Camera(new Vector3f(1.0f, 0.0f, 0.0f), new Vector3f(0.0f, 0.0f, 0.0f));

        /* Initialize shader with Vertex and Fragment shaders. */
        Reference.SHADER.createShader(ResourceHelper.getShaderAsString("/VertexShader.glsl"), GL20.GL_VERTEX_SHADER);
        Reference.SHADER.createShader(ResourceHelper.getShaderAsString("/FragmentShader.glsl"), GL20.GL_FRAGMENT_SHADER);
        Reference.SHADER.link();

        /* Add 100 cubes */
        Debug.addDebugObjects(10, 100, true);
        /* LIGHT! */
//        Renderer.INSTANCE.add(new PointLight(new Vector3f(ColorUtil.WHITE), new Vector3f(0.0f, 15.0f, 0.0f), 500.0f, new Attenuation(0.0f, 0.0f, 1.0f)));
    }

    @Override
    protected void update() { Reference.CAMERA.update(); }

    @Override
    protected void render() { Renderer.INSTANCE.render(); }

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException { Game.main(args, new Main()); }
}
