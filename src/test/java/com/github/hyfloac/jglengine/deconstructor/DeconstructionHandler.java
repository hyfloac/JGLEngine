package com.github.hyfloac.jglengine.deconstructor;

import java.util.ArrayList;
import java.util.List;

public final class DeconstructionHandler
{
    public static final DeconstructionHandler INSTANCE = new DeconstructionHandler();

    private final List<IDeconstructor> deconstructors;

    public DeconstructionHandler()
    {
        deconstructors = new ArrayList<IDeconstructor>();
        Runtime.getRuntime().addShutdownHook(new Thread(this::deconstruct));
    }

    public void addDeconstructor(IDeconstructor deconstructor) { deconstructors.add(deconstructor); }

    public void deconstruct()
    {
        for(IDeconstructor deconstructor : deconstructors) { deconstructor.finalize(); }
        deconstructors.clear();
    }

    public static void addDeconstructorS(IDeconstructor deconstructor) { INSTANCE.addDeconstructor(deconstructor); }

    public static void deconstructS() { INSTANCE.deconstruct(); }
}
