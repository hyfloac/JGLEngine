package com.github.hyfloac.jglengine.window;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.deconstructor.IDeconstructor;
import com.github.hyfloac.jglengine.event.EventBus;
import com.github.hyfloac.jglengine.event.events.EventCursorEnterWindow;
import com.github.hyfloac.jglengine.event.events.EventCursorMove;
import com.github.hyfloac.jglengine.event.events.EventKeyInput;
import com.github.hyfloac.jglengine.event.events.EventMouseButton;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.util.Log;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowHandler implements IDeconstructor
{
    public static WindowHandler INSTANCE;

    public long windowHandle;

    public WindowHandler()
    {
        DeconstructionHandler.INSTANCE.addDeconstructor(this);
        INSTANCE = this;
    }

    public long createWindow(final String name, final int width, final int height, final int fps, final boolean fullscreen)
    {
        GLFW.glfwDefaultWindowHints();
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GLFW.GLFW_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_REFRESH_RATE, fps);

        /* Required for OpenGL versions > 2.4 */
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 2);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GLFW.GLFW_TRUE);
        /* Create window, allows for fullscreen */
        windowHandle = GLFW.glfwCreateWindow(width, height, name, fullscreen ? GLFW.glfwGetPrimaryMonitor() : MemoryUtil.NULL, MemoryUtil.NULL);

        /* Ensure window created correctly */
        if(windowHandle == MemoryUtil.NULL) { Log.traceKill(new RuntimeException("Failed to create the GLFW window")); }
        /* Center window */
        if(!fullscreen)
        {
            GLFWVidMode vidmode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
            GLFW.glfwSetWindowPos(windowHandle, (vidmode.width() - width) / 2, (vidmode.height() - height) / 2);
        }
        GLFW.glfwMakeContextCurrent(windowHandle);
        /* VSync */
        GLFW.glfwSwapInterval(Reference.VSYNC ? 1 : 0);
        GLFW.glfwShowWindow(windowHandle);

        setupSettings();
        return windowHandle;
    }

    protected void setupSettings()
    {
        /* Initialize OpenGL <b>REQUIRED</b> */
        GL.createCapabilities();

        /* I don't think this is actually required */
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        /* Create background color */
        GL11.glClearColor(0.7f, 1.0f, 0.3f, 0.0f);

        /* Color smoothing or something */
        GLUtil.blending(true);
        /* Order vertices in scene, otherwise everything looks really fucked up */
        GLUtil.depthTest(true);
        /* Show polygon lines instead faces */
        GLUtil.debugPolygons(false);
        /* Don't render back faces, optimization */
        GLUtil.backfaceCulling(true);

        /* Dispatch events when keys or mouse movement happens */
        GLFW.glfwSetKeyCallback(windowHandle, (window, key, scancode, action, mods) -> EventBus.INSTANCE.dispatch(new EventKeyInput(key, scancode, action, mods)));
        GLFW.glfwSetCursorPosCallback(windowHandle, (window, xpos, ypos) -> EventBus.INSTANCE.dispatch(new EventCursorMove(xpos, ypos)));
        GLFW.glfwSetCursorEnterCallback(windowHandle, (window, entered) -> EventBus.INSTANCE.dispatch(new EventCursorEnterWindow(entered)));
        GLFW.glfwSetMouseButtonCallback(windowHandle, (window, button, action, mods) -> EventBus.INSTANCE.dispatch(new EventMouseButton(button, action, mods)));

        /* Hide the cursor */
        GLFW.glfwSetInputMode(windowHandle, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);
    }

    /* Is window in background */
    public boolean isFocused() { return GLFW.glfwGetWindowAttrib(windowHandle, GLFW.GLFW_FOCUSED) == GLFW.GLFW_TRUE; }

    @Override
    public void finalize() { GLFW.glfwDestroyWindow(windowHandle); }
}
