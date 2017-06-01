package com.github.hyfloac.jglengine.reference;

import com.github.hyfloac.jglengine.model.Model;
import com.github.hyfloac.jglengine.model.OBJParser;
import com.github.hyfloac.jglengine.resources.ResourceHelper;

public final class Models
{
    private Models() {}

    public static final Model CUBE = OBJParser.loadOBJ(ResourceHelper.getModel("/Base.obj"));
    public static final Model DRAGON = OBJParser.loadOBJ(ResourceHelper.getModel("/dragon.obj"));
    public static final Model BUNNY = OBJParser.loadOBJ(ResourceHelper.getModel("/bunny.obj"));
    public static final Model JAR = OBJParser.loadOBJ(ResourceHelper.getModel("/JarPot.obj"));
}
