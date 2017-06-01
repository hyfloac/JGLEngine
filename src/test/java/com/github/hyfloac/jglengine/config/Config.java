package com.github.hyfloac.jglengine.config;

import com.github.hyfloac.jglengine.reference.Reference;
import com.github.hyfloac.jglengine.resources.ResourceHelper;
import com.github.hyfloac.simplelog.Logger;

import java.io.IOException;

public final class Config
{
    private final Configuration config;

    public Config()
    {
        config = new Configuration(ResourceHelper.getGameDataDir() + "/settings.cfg");
    }

    public void init()
    {
        if(config.getConfigFile().exists())
        {
            try { config.readConfig(); }
            catch(IOException e) { Logger.traceS(e); }
        }
        else
        {
            config.create();
            config.addSetting("Sets the maximum FPS of the game.", "FPS", 60);
            config.addSetting("Enables VSync.", "VSync", false);
            config.addSetting("Anti-Aliasing modes: NONE, MSAA, SSAA, MLAA, FXAA, SMAA", "AA", "NONE");
            config.addSetting("Sets the resolution of the screen int the format of: WIDTHxHEIGTH", "Res", "720x480");
            config.addSetting("Sets whether or not the window should be in fullscreen mode.", "Fullscreen", false);
            config.addSetting("Sets the Field Of View.", "FOV", 60.0f);
            config.addSetting("Sets the view distance.", "ViewDist", 1000.0f);
            config.writeSettings();
        }
        try
        {
            Reference.FPS = (int) config.getSetting("FPS").value;
            Reference.VSYNC = (boolean) config.getSetting("VSync").value;
            Reference.AA = (String) config.getSetting("AA").value;
            Reference.RESOLUTION = (String) config.getSetting("Res").value;
            String[] tmp = Reference.RESOLUTION.split("x");
            Reference.WIDTH = Integer.parseInt(tmp[0]);
            Reference.HEIGHT = Integer.parseInt(tmp[1]);
            Reference.ASPECT_RATIO = (float) Reference.WIDTH / (float) Reference.HEIGHT;
            Reference.FULLSCREEN = (boolean) config.getSetting("Fullscreen").value;
            Reference.FOV = (float) config.getSetting("FOV").value;
            Reference.VIEW_DISTANCE = (float) config.getSetting("ViewDist").value;
        } catch(IOException e) { Logger.INSTANCE.trace(e); }
    }
}
