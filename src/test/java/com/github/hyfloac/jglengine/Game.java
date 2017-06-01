package com.github.hyfloac.jglengine;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.event.EventBus;
import com.github.hyfloac.jglengine.event.handler.CameraMovementHandler;
import com.github.hyfloac.jglengine.event.handler.KeyInputHandler;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.jglengine.window.WindowHandler;
import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.propterties.Properties;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class Game
{
    protected final WindowHandler window;

    public Game(WindowHandler window) { this.window = window; }

    protected abstract void setupWindow();

    private void init0()
    {
        /* Error handling */
        GLFWErrorCallback.createPrint(System.err).set();
        /* Required setup stage */
        if(!GLFW.glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW."); }
        setupWindow();
        /* An attempt to stop the create of hs err pid files */
        Runtime.getRuntime().addShutdownHook(new Thread(() -> GLFW.glfwSetWindowShouldClose(Reference.WINDOW_HANDLE, true)));

        /* Register key handlers with the event bus */
        EventBus.INSTANCE.register(new KeyInputHandler());
        EventBus.INSTANCE.register(new CameraMovementHandler());

        /* Initialize the shader */
        Reference.SHADER = new Shader();

        /* Call the implementors initialization phase */
        init();
        /* Game Loop */
        loop();

        /* Clean up data, required because there are native calls and natives don't have a GC */
        DeconstructionHandler.INSTANCE.deconstruct();
        GLFW.glfwTerminate();
        GLFW.glfwSetErrorCallback(null).free();
        Callbacks.glfwFreeCallbacks(Reference.WINDOW_HANDLE);
    }

    protected abstract void init();

    private void update0()
    {
        /* Get user input (mouse, keyboard) */
        GLFW.glfwPollEvents();
        update();
    }

    protected abstract void update();

    private void render0() { render(); }

    protected abstract void render();

    /* How often to log to the console the current FPS, UPS, and MiB */
    public static int timeStep = 1_000;

    private void loop()
    {
        /* Number of updates per second to always achieve */
        final byte UPS = 20;
        /* Time of last update */
        long lastTime = System.currentTimeMillis();
        long now;
        /* Something */
        float delta = 0.0F;
        /* # MS per update */
        final float ns = 1000.0f / UPS;
        /* For logging */
        long timer = lastTime;
        int updates = 0;
        int updatesL = 0;
        int frames = 0;

        while(!GLFW.glfwWindowShouldClose(Reference.WINDOW_HANDLE))
        {
            now = System.currentTimeMillis();
            delta += (now - lastTime) / ns;
            lastTime = now;

            /* Only update if enough time has passed (don't do all updates at the beginning of the second, and the rest of the second
             * spent rendering.) and if there are not more than `UPS` updates (constant update, dynamic framerate).
             */
            if(delta >= 1.0f && updates < UPS)
            {
                update0();
                ++updates;
                ++updatesL;
                --delta;
            }
            /* While there is still time between the next update, render as many frames as possible. Only render if window is focused,
             * if you don't do this you will get a copious high FPS which rapidly causes memory usage to increase (this is how I know
             * that the memory leak is in the render method).
             */
            while(delta <= 1.0f && WindowHandler.INSTANCE.isFocused())
            {
                now = System.currentTimeMillis();
                delta += (now - lastTime) / ns;
                lastTime = now;
                render0();
                ++frames;
            }

            /* Used to allow 20 UPS. */
            if(lastTime - timer > 1000) { updates = 0; }

            /* Logging */
            if(lastTime - timer > timeStep)
            {
                timer = lastTime;
                Logger.debugS("UPS: " + (updatesL / (timeStep / 1000)) + "\tFPS: " + frames + (frames == 0 && !WindowHandler.INSTANCE.isFocused() && Reference.DEV_ENVIRONMENT ? ";\tWindow not focused." : ""));
                Logger.debugS("Memory Usage (MiB): " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1_048_576);

                updatesL = 0;
                frames = 0;
            }
        }
    }

    protected static void main(String[] args, Game instance) throws IOException, InterruptedException, URISyntaxException
    {
        main(args);
        instance.init0();
    }

    public static void main(String[] args) throws IOException, InterruptedException, URISyntaxException
    {
        boolean fixed = false;
        for(String arg : args)
        {
            if("debug".equals(arg)) { Reference.DEBUG_MODE = true; }
            if("dev".equals(arg)) { Reference.DEV_ENVIRONMENT = true; }
            if("fix".equals(arg)) { fixed = true; }
        }
        Logger.INSTANCE = new Logger(Reference.DEBUG_MODE, ResourceHelper.getGameDataDir() + "/logs", "GamEngine");

        /* OSX has a problem which requires the program to be launched with JVM arg `-XstartOnFirstThread` */
        if(!Properties.OS.IS_MAC) { fixed = true; }
        if(!fixed)
        {
            List<String> arguments = new ArrayList<>();
            arguments.add(System.getProperty("java.home") + "/bin/java");
            arguments.add("-XstartOnFirstThread -jar");
            arguments.add(new File(Game.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath());
            arguments.add("fix");
            int length = 0;
            for(String s : arguments) { length += s.length() + 1; }
            StringBuilder con = new StringBuilder(length - 1);
            for(String arg : arguments) { con.append(arg).append(" "); }
            con.deleteCharAt(con.length() - 1);
            Logger.warnS("This application will be restarting,\n\tthis is because LWJGL 3 running on Mac OSX\n\trequires jvm argument '-XstartOnFirstThread', this will stop console logging.\n\tIf you would like to run this application with logging run the command\n\t`" + con.toString() + "`.");
            Process pro = new ProcessBuilder(arguments).start();
            pro.waitFor(5, TimeUnit.SECONDS);
        }
    }
}
