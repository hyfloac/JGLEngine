package com.github.hyfloac.jglengine.event.handler;

import com.github.hyfloac.jglengine.Game;
import com.github.hyfloac.jglengine.event.EventBus;
import com.github.hyfloac.jglengine.event.EventSubscription;
import com.github.hyfloac.jglengine.event.events.EventExit;
import com.github.hyfloac.jglengine.event.events.EventKeyInput;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.renderer.Renderer;
import com.github.hyfloac.jglengine.util.Debug;
import com.github.hyfloac.jglengine.window.GLUtil;
import com.github.hyfloac.simplelog.Logger;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler
{
    private final KeyCheck quit;
    private final KeyCheck toggleDebug;
    private final KeyCheck increaseRad;
    private final KeyCheck decreaseRad;
    private final KeyCheck increaseLen;
    private final KeyCheck decreaseLen;
    private final KeyCheck debugPolys;
    private final KeyCheck backfaceCulling;
    private final KeyCheck cursor;

    public KeyInputHandler()
    {
        quit = new KeyCheck(GLFW.GLFW_KEY_ESCAPE, GLFW.GLFW_RELEASE,0);
        toggleDebug = new KeyCheck(GLFW.GLFW_KEY_RIGHT_ALT, GLFW.GLFW_PRESS, 0);
        increaseRad = new KeyCheck(GLFW.GLFW_KEY_UP, GLFW.GLFW_PRESS,0);
        decreaseRad = new KeyCheck(GLFW.GLFW_KEY_DOWN, GLFW.GLFW_PRESS,0);
        increaseLen = new KeyCheck(GLFW.GLFW_KEY_RIGHT, GLFW.GLFW_PRESS,0);
        decreaseLen = new KeyCheck(GLFW.GLFW_KEY_LEFT, GLFW.GLFW_PRESS,0);
        debugPolys = new KeyCheck(GLFW.GLFW_KEY_TAB, GLFW.GLFW_PRESS, 0);
        backfaceCulling = new KeyCheck(GLFW.GLFW_KEY_TAB, GLFW.GLFW_PRESS, GLFW.GLFW_MOD_ALT);
        cursor = new KeyCheck(GLFW.GLFW_KEY_X, GLFW.GLFW_PRESS, 0);
    }

    @EventSubscription(priority = 2)
    public void onKeyPress(EventKeyInput event)
    {
        int key = event.key;

        if(quit.check(event)) { EventBus.INSTANCE.dispatch(new EventExit()); }
        else if(toggleDebug.check(event)) { Logger.toggleDebugModeS(); }
        else if(key == GLFW.GLFW_KEY_UP || key == GLFW.GLFW_KEY_DOWN || key == GLFW.GLFW_KEY_LEFT || key == GLFW.GLFW_KEY_RIGHT)
        {
            Renderer.INSTANCE.worldObjects.clear();
            if(increaseRad.check(event)) { Debug.rad += 0.5f; }
            else if(decreaseRad.check(event)) { Debug.rad -= 0.5f; }
            else if(increaseLen.check(event)) { Debug.len = (int) (Debug.len * 1.5f); }
            else if(decreaseLen.check(event)) { Debug.len = (int) (Debug.len * 0.75f); }
            Debug.test();
        }
        else if(debugPolys.check(event)) { GLUtil.debugPolygons(); }
        else if(backfaceCulling.check(event)) { GLUtil.backfaceCulling(); }
        else if(cursor.check(event)) { GLUtil.toogleCursorVisiility(); }
        else if(key == GLFW.GLFW_KEY_9 && event.action == GLFW.GLFW_PRESS) { Game.timeStep -= 1000; }
        else if(key == GLFW.GLFW_KEY_0 && event.action == GLFW.GLFW_PRESS) { Game.timeStep += 1000; }
    }

    @EventSubscription
    public void onExit(EventExit event)
    {
        Logger.infoS("Exiting with status code " + event.getStatusCode());
        GLFW.glfwSetWindowShouldClose(Reference.WINDOW_HANDLE, true);
    }

    public static abstract class HandleKey
    {
        private final int keyCheck;
        private final int scanCodeCheck;
        private final int actionCheck;
        private final int modsCheck;

        public HandleKey(int keyCheck, int scanCodeCheck, int actionCheck, int modsCheck)
        {
            this.keyCheck = keyCheck;
            this.scanCodeCheck = scanCodeCheck;
            this.actionCheck = actionCheck;
            this.modsCheck = modsCheck;
            EventBus.INSTANCE.register(this);
        }

        public HandleKey(int keyCheck, int actionCheck, int modsCheck) { this(keyCheck, actionCheck, modsCheck, -1); }

        public HandleKey(int keyCheck, int actionCheck) { this(keyCheck, actionCheck, -1, -1); }

        public HandleKey(int keyCheck) { this(keyCheck, -1, -1, -1); }

        @EventSubscription
        private void onKeyPress(EventKeyInput event)
        {
            if((keyCheck == -1 || keyCheck == event.key) && (scanCodeCheck == -1 || scanCodeCheck == event.scancode) && (actionCheck == -1 || actionCheck == event.action) && (modsCheck == -1 || modsCheck == event.mods)) { onKey(event); }
        }

        protected abstract void onKey(EventKeyInput event);
    }

    public static final class KeyCheck
    {
        private final int keyCheck;
        private final int scanCodeCheck;
        private final int actionCheck;
        private final int modsCheck;

        public KeyCheck(int keyCheck, int scanCodeCheck, int actionCheck, int modsCheck)
        {
            this.keyCheck = keyCheck;
            this.scanCodeCheck = scanCodeCheck;
            this.actionCheck = actionCheck;
            this.modsCheck = modsCheck;
        }

        public KeyCheck(EventKeyInput event) { this(event.key, event.scancode, event.action, event.mods); }

        public KeyCheck(int keyCheck, int actionCheck, int modsCheck) { this(keyCheck, -1, actionCheck, modsCheck); }

        public KeyCheck(int keyCheck, int actionCheck) { this(keyCheck, -1, actionCheck, -1); }

        public KeyCheck(int keyCheck) { this(keyCheck, -1, -1, -1); }

        public boolean check(EventKeyInput event) { return (keyCheck == -1 || keyCheck == event.key) && (scanCodeCheck == -1 || scanCodeCheck == event.scancode) && (actionCheck == -1 || actionCheck == event.action) && (modsCheck == -1 || modsCheck == event.mods); }
    }
}
