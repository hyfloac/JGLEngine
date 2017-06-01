package com.github.hyfloac.jglengine.event.handler;

import com.github.hyfloac.jglengine.event.EventSubscription;
import com.github.hyfloac.jglengine.event.events.EventCursorEnterWindow;
import com.github.hyfloac.jglengine.event.events.EventCursorMove;
import com.github.hyfloac.jglengine.event.events.EventKeyInput;
import com.github.hyfloac.jglengine.event.events.EventWindowResize;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.util.Debug;
import com.github.hyfloac.jglengine.window.GLUtil;
import com.github.hyfloac.simplelog.Logger;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class CameraMovementHandler
{
    private boolean isInWindow;
    private Vector2f previousPos;

    private boolean xPress = false;
    private boolean yPress = false;
    private boolean zPress = false;

    private final KeyInputHandler.KeyCheck moveX_press;
    private final KeyInputHandler.KeyCheck moveY_press;
    private final KeyInputHandler.KeyCheck moveZ_press;

    private final KeyInputHandler.KeyCheck moveX_release;
    private final KeyInputHandler.KeyCheck moveY_release;
    private final KeyInputHandler.KeyCheck moveZ_release;

    public CameraMovementHandler()
    {
        isInWindow = true;
        previousPos = new Vector2f(-1.0f, -1.0f);

        moveX_press = new KeyInputHandler.KeyCheck(new int[] { GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_D }, GLFW.GLFW_PRESS);
        moveZ_press = new KeyInputHandler.KeyCheck(new int[] { GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S }, GLFW.GLFW_PRESS);
        moveY_press = new KeyInputHandler.KeyCheck(new int[] { GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT }, GLFW.GLFW_PRESS);

        moveX_release = new KeyInputHandler.KeyCheck(new int[] { GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_D }, GLFW.GLFW_RELEASE);
        moveY_release = new KeyInputHandler.KeyCheck(new int[] { GLFW.GLFW_KEY_SPACE, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT }, GLFW.GLFW_RELEASE);
        moveZ_release = new KeyInputHandler.KeyCheck(new int[] { GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_S }, GLFW.GLFW_RELEASE);
    }

    @EventSubscription
    public void enterWindow(EventCursorEnterWindow event) { isInWindow = event.hasEntered(); }

    @EventSubscription(priority = 1)
    public void keyPress(EventKeyInput event)
    {
        if(!isInWindow && GLUtil.cursorVisible) { return; }
        int key = event.key;
        int action = event.action;

        if(moveX_press.check(event)) { xPress = true; }
        else if(moveX_release.check(event)) { xPress = false; }

        if(moveY_press.check(event)) { yPress = true; }
        else if(moveY_release.check(event)) { yPress = false; }

        if(moveZ_press.check(event)) { zPress = true; }
        else if(moveZ_release.check(event)) { zPress = false; }


        if(key == GLFW.GLFW_KEY_W) { Reference.CAMERA.moveZ(-Reference.CAMERA.getMovementSpeed(), zPress); }
        else if(key == GLFW.GLFW_KEY_S) { Reference.CAMERA.moveZ(Reference.CAMERA.getMovementSpeed(), zPress); }

        if(key == GLFW.GLFW_KEY_A) { Reference.CAMERA.moveX(-Reference.CAMERA.getMovementSpeed(), xPress); }
        else if(key == GLFW.GLFW_KEY_D) { Reference.CAMERA.moveX(Reference.CAMERA.getMovementSpeed(), xPress); }

        if(key == GLFW.GLFW_KEY_RIGHT_SHIFT || key == GLFW.GLFW_KEY_LEFT_SHIFT) { Reference.CAMERA.moveY(-Reference.CAMERA.getMovementSpeed(), yPress); }
        else if(key == GLFW.GLFW_KEY_SPACE) { Reference.CAMERA.moveY(Reference.CAMERA.getMovementSpeed(), yPress); }

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

        if(key == GLFW.GLFW_KEY_O && action == GLFW.GLFW_PRESS)
        {
            Reference.CAMERA.setXYZ(new Vector3f(0, 0, 0));
            Reference.CAMERA.setRotXYZ(new Vector3f(0, 0, 0));
        }
    }

    @EventSubscription(priority = 1)
    @SuppressWarnings("SuspiciousNameCombination")
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
            // I don't know why but if you change the deltaX & deltaY places mouse movement occurs on a opposite axis.
            if(deltaY != 0) { Reference.CAMERA.incrementRotX(deltaY); }
            if(deltaX != 0) { Reference.CAMERA.incrementRotY(deltaX); }
        }
        previousPos.x = (float) event.getX();
        previousPos.y = (float) event.getY();
    }

    @EventSubscription
    public void onWindowResize(EventWindowResize event)
    {
        Reference.WIDTH = event.width;
        Reference.HEIGHT = event.height;
        Reference.CAMERA.updateProjectionMatrix();
    }
}
