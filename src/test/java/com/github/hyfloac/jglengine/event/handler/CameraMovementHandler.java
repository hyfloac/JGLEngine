package com.github.hyfloac.jglengine.event.handler;

import com.github.hyfloac.jglengine.event.EventSubscription;
import com.github.hyfloac.jglengine.event.events.EventCursorEnterWindow;
import com.github.hyfloac.jglengine.event.events.EventCursorMove;
import com.github.hyfloac.jglengine.event.events.EventKeyInput;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.util.Debug;
import com.github.hyfloac.jglengine.window.GLUtil;
import com.github.hyfloac.simplelog.Logger;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

public class CameraMovementHandler
{
    private boolean isInWindow;
    private Vector2f previousPos;

    private boolean XPress = false;
    private boolean YPress = false;
    private boolean ZPress = false;

    public CameraMovementHandler()
    {
        isInWindow = true;
        previousPos = new Vector2f(-1.0f, -1.0f);
    }

    @EventSubscription
    public void enterWindow(EventCursorEnterWindow event) { isInWindow = event.hasEntered(); }

    @EventSubscription(priority = 1)
    public void keyPress(EventKeyInput event)
    {
        if(!isInWindow && GLUtil.cursorVisible) { return; }
        int key = event.key;
        int action = event.action;

        if(action == GLFW.GLFW_PRESS && (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_S)) { ZPress = true; }
        else if(action == GLFW.GLFW_RELEASE && (key == GLFW.GLFW_KEY_W || key == GLFW.GLFW_KEY_S)) { ZPress = false; }

        if(action == GLFW.GLFW_PRESS && (key == GLFW.GLFW_KEY_SPACE || key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) { YPress = true; }
        else if(action == GLFW.GLFW_RELEASE && (key == GLFW.GLFW_KEY_SPACE || key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) { YPress = false; }

        if(action == GLFW.GLFW_PRESS && (key == GLFW.GLFW_KEY_A || key == GLFW.GLFW_KEY_D)) { XPress = true; }
        else if(action == GLFW.GLFW_RELEASE && (key == GLFW.GLFW_KEY_A || key == GLFW.GLFW_KEY_D)) { XPress = false; }

        if(key == GLFW.GLFW_KEY_W) { Reference.CAMERA.moveZ(-Reference.CAMERA.getMovementSpeed(), ZPress); }
        else if(key == GLFW.GLFW_KEY_S) { Reference.CAMERA.moveZ(Reference.CAMERA.getMovementSpeed(), ZPress); }

        if(key == GLFW.GLFW_KEY_A) { Reference.CAMERA.moveX(-Reference.CAMERA.getMovementSpeed(), XPress); }
        else if(key == GLFW.GLFW_KEY_D) { Reference.CAMERA.moveX(Reference.CAMERA.getMovementSpeed(), XPress); }

        if(key == GLFW.GLFW_KEY_RIGHT_SHIFT || key == GLFW.GLFW_KEY_LEFT_SHIFT) { Reference.CAMERA.moveY(-Reference.CAMERA.getMovementSpeed(), YPress); }
        else if(key == GLFW.GLFW_KEY_SPACE) { Reference.CAMERA.moveY(Reference.CAMERA.getMovementSpeed(), YPress); }

        if(key == GLFW.GLFW_KEY_LEFT_BRACKET && action == GLFW.GLFW_PRESS)
        {
            Reference.CAMERA.incrementMovementSpeed(0.05f);
            if(Reference.CAMERA.getMovementSpeed() > 10.0f) { Reference.CAMERA.setMovementSpeed(10.0f); }
            Logger.debugS(Reference.CAMERA.getMovementSpeed());
        }
        else if(key == GLFW.GLFW_KEY_RIGHT_BRACKET && action == GLFW.GLFW_PRESS)
        {
            Reference.CAMERA.incrementMovementSpeed(-0.05f);
            if(Reference.CAMERA.getMovementSpeed() < 0.0f) { Reference.CAMERA.setMovementSpeed(0.0f); }
            Logger.debugS(Reference.CAMERA.getMovementSpeed());
        }

        if(key == GLFW.GLFW_KEY_I && action == GLFW.GLFW_PRESS)
        {
            Debug.debugVector3f(Reference.CAMERA.getXYZ());
            Debug.debugVector3f(Reference.CAMERA.getRotXYZ());
        }
    }

    @EventSubscription(priority = 1)
    public void cursorMove(EventCursorMove event)
    {
        if(isInWindow && !GLUtil.cursorVisible)
        {
            float deltaX = (float) event.getX() - previousPos.x;
            float deltaY = (float) event.getY() - previousPos.y;
            if(deltaX > 10.0f) { deltaX = 10.0f; }
            if(deltaX < -10.0f) { deltaX = -10.0f; }
            if(deltaY > 10.0f) { deltaY = 10.0f; }
            if(deltaY < -10.0f) { deltaY = -10.0f; }
            // I don't know why but if you change the deltaX & deltaY places mouse movement occurs on a opposite axis (this might be a mac problem).
            if(deltaX != 0) { Reference.CAMERA.incrementRotX(deltaY); }
            if(deltaY != 0) { Reference.CAMERA.incrementRotY(deltaX); }
        }
        previousPos.x = (float) event.getX();
        previousPos.y = (float) event.getY();
    }
}
