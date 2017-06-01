package com.github.hyfloac.jglengine.command.commands;

import com.github.hyfloac.jglengine.command.commands.exception.CommandUsageException;
import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.window.GLUtil;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public class GLCommand extends Command
{
    @Override
    public String[] getNames() { return new String[] { "GL", "openGL" }; }

    @Override
    public String getUsage()
    {
        return "/GL <GL_PARAM> <VALUE>";
    }

    @Override
    public boolean execute(String[] args) throws CommandUsageException
    {
        if(args.length < 1) { throw new CommandUsageException(getUsage()); }
        int gl11 = 0;
        try { gl11 = parseInt(args[0]); }
        catch(NumberFormatException ignored) { }

        if(gl11 != 0)
        {
            if(args.length > 2)
            {
                Boolean it = isTrue(args[1]);
                if(it == null) { throw new CommandUsageException(getUsage()); }
                if(it) { GL11.glEnable(gl11); }
                else { GL11.glDisable(gl11); }
            }
        }
        else
        {
            GLFW.glfwMakeContextCurrent(Reference.WINDOW_HANDLE);
            try { GL.getCapabilities(); }
            catch(Exception ignored) { GL.createCapabilities(); }

            if(equals(args[0], "glPolygonMode", "polygonMode", "GL_POLYGON_MODE", "POLYGON_MODE"))
            {
                if(args.length > 1)
                {
                    Boolean it = isTrue(args[1]);
                    if(it == null) { throw new CommandUsageException(getUsage()); }
                    GLUtil.debugPolygons(it);
                }
                else { GLUtil.debugPolygons(); }
                return true;
            }
            else if(equals(args[0], "glClearColor"))
            {
                if(args.length > 3)
                {
                    try
                    {
                        if(args.length > 4) { GL11.glClearColor(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), Float.parseFloat(args[4])); }
                        else { GL11.glClearColor(Float.parseFloat(args[1]), Float.parseFloat(args[2]), Float.parseFloat(args[3]), 0.0F); }
                        return true;
                    }
                    catch(NumberFormatException e) { throw new CommandUsageException("Invalid floating point number."); }
                }
            }
        }

        return false;
    }
}
