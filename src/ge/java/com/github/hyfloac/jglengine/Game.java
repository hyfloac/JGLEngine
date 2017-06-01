package com.github.hyfloac.jglengine;

import com.github.hyfloac.jglengine.event.EventBus;
import com.github.hyfloac.jglengine.event.handler.CameraMovementHandler;
import com.github.hyfloac.jglengine.event.handler.KeyInputHandler;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.jglengine.window.Window;
import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.propterties.Properties;
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
    private static Game instance;

    public static Game instance() { return instance; }

    public final Window window;
    public final short ups;
    public final String name;

    public Game(final Window window, final short ups, final String name)
    {
        if(ups < 1) { throw new IllegalArgumentException("UPS must be larger than 0."); }
        this.window = window;
        this.ups = ups;
        this.name = name;
        instance = this;
    }

    @SuppressWarnings("unused")
    public Game(final Window window, final int ups, final String name) { this(window, (short) ups, name); }

    public Game(final Window window, final String name) { this(window, (short) 20, name); }

    protected abstract void setupWindow();

    protected abstract void init();

    private void init0()
    {
        GLFWErrorCallback.createPrint(System.err).set();
        if(!GLFW.glfwInit()) { throw new IllegalStateException("Unable to initialize GLFW."); }
        setupWindow();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> GLFW.glfwSetWindowShouldClose(window.windowHandle, true)));

        EventBus.INSTANCE.register(new KeyInputHandler());
        EventBus.INSTANCE.register(new CameraMovementHandler());

//        Reference.SHADER = new Shader();
//        Reference.TEXT_SHADER = new Shader();

        init();
        System.gc();
    }

    protected abstract void update();

    private void update0()
    {
        GLFW.glfwPollEvents();
        update();
    }

    protected abstract void render();

    private void render0()
    {
        render();
        GLFW.glfwSwapBuffers(Reference.WINDOW_HANDLE);
    }

    public static int timeStep = 1_000;
    public static int timeStep0 = timeStep / 1000;
    public static boolean logStuff = true;

    private void loop()
    {
        /* Time of last update */
        long lastTime = System.currentTimeMillis();
        long now;
        /* Something */
        float delta = 0.0F;
        /* # MS per update */
        final float ns = 1000.0f / ups;
        int updates = 0;
        long timer = lastTime;
        /* For logging */
        int updatesLogger = 0;
        int frames = 0;

//        List<Long> timeLapse = new ArrayList<Long>();
        while(!GLFW.glfwWindowShouldClose(Reference.WINDOW_HANDLE))
        {
            now = System.currentTimeMillis();
            delta += (now - lastTime) / ns;
            lastTime = now;

            /* Only update if enough time has passed (don't do all updates at the beginning of the second, and the rest of the second
             * spent rendering.) and if there are not more than `UPS` updates (constant update, dynamic framerate).
             */
            if(delta >= 1.0f && updates < ups)
            {
                update0();
                ++updates;
                ++updatesLogger;
                --delta;
            }
            /* While there is still time between the next update, render as many frames as possible. Only render if window is focused,
             * if you don't do this you will get a copious high FPS which rapidly causes memory usage to increase (this is how I know
             * that the memory leak is in the render method).
             */
            while(delta <= 1.0f && window.isFocused())
            {
                now = System.currentTimeMillis();
                delta += (now - lastTime) / ns;
//                timeLapse.add(now - lastTime);
                lastTime = now;
                render0();
                ++frames;
            }

            long t = lastTime - timer;

            /* Used to allow for a constant UPS. */
            if(t >= 1000)
            {
                timer = lastTime;
                updates = 0;
            }

            /* Logging */
            if(t >= timeStep)
            {
                if(logStuff)
                {
                    Logger.debugS(new StringBuilder().append("UPS: ").append(updatesLogger / timeStep0).append("\tFPS: ").append(frames / (timeStep0)).append(frames == 0 && !window.isFocused() && Reference.DEV_ENVIRONMENT ? ";\tWindow not focused." : "").toString());
//                    Logger.debugS("FPS Time Steps: " + Arrays.toString(timeLapse.toArray()));
                    Logger.debugS(new StringBuilder().append("Memory Usage (MiB): ").append((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) >> 20).toString());
                }

//                timeLapse.clear();
                updatesLogger = 0;
                frames = 0;
            }
        }
    }

    protected static void main(String[] args, Game instance) throws IOException, InterruptedException, URISyntaxException
    {
        main(args);
        try
        {
            instance.init0();
            instance.loop();
        }
        finally
        {
            GLFW.glfwTerminate();
            GLFW.glfwSetErrorCallback(null).free();
        }
        System.exit(0);
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
            StringBuilder con = new StringBuilder(length);
            for(String arg : arguments) { con.append(arg).append(" "); }
            con.deleteCharAt(con.length() - 1);
            Logger.warnS("This application will be restarting,\n\tthis is because LWJGL 3 running on Mac OSX\n\trequires jvm argument '-XstartOnFirstThread', this will stop on console logging.\n\tIf you would like to run this application with logging run the command\n\t'" + con.toString() + "'");
            Process pro = new ProcessBuilder(arguments).start();
            pro.waitFor(5, TimeUnit.SECONDS);
        }
    }
}
