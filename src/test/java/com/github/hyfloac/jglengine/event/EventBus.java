package com.github.hyfloac.jglengine.event;

import com.github.hyfloac.jglengine.deconstructor.DeconstructionHandler;
import com.github.hyfloac.jglengine.deconstructor.IDeconstructor;
import com.github.hyfloac.jglengine.event.events.Event;
import com.github.hyfloac.simplelog.Logger;
import com.github.vitrifiedcode.javautilities.list.container.Pair;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventBus implements IDeconstructor
{
    public static final EventBus INSTANCE = new EventBus();

    /**
     * Holds all the event handlers.
     * Multimap // Can hold multiple values for each key.
     * <
     * Event, // The event which will be handled.
     * SortedSetMultimap // Multimap which can't have multiples of the same key or of the same value in a key.
     * <
     * Object, // The class containing the handler.
     * Pair // A grouping of two generic.
     * <
     * Method, // The method which handles the event.
     * Integer // The priority of the handler
     * >
     * >
     * >
     */
    private final Multimap<Event, SortedSetMultimap<Object, Pair<Method, Integer>>> handlers;

    public EventBus()
    {
        DeconstructionHandler.addDeconstructorS(this);
        handlers = HashMultimap.create();
    }

    public void dispatch(@Nonnull Event event)
    {
        for(Event real : handlers.keySet())
        {
            if(real.getClass().isAssignableFrom(event.getClass()))
            {
                for(SortedSetMultimap<Object, Pair<Method, Integer>> handler : handlers.get(real))
                {
                    for(Object clazz : handler.keySet())
                    {
                        for(Pair<Method, Integer> method : handler.get(clazz))
                        {
                            try
                            {
                                method.left.setAccessible(true);
                                method.left.invoke(clazz, event);
                            }
                            catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) { Logger.traceS(e); }
                        }
                    }
                }
            }
        }
    }

    public void register(@Nonnull Object obj)
    {
        for(Method method : obj.getClass().getDeclaredMethods())
        {
            if(method.isAnnotationPresent(EventSubscription.class))
            {
                try
                {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if(parameterTypes.length != 1) { throw new IllegalArgumentException("Method " + method + " has @EventSubscription annotation, but requires " + parameterTypes.length + " arguments.  Event handler methods must require a single argument."); }

                    Class<?> eventType = parameterTypes[0];
                    if(!Event.class.isAssignableFrom(eventType)) { throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but takes a argument that is not an Event " + eventType); }

                    SortedSetMultimap<Object, Pair<Method, Integer>> handler = null;

                    Constructor<?> ctr = eventType.getConstructor();
                    ctr.setAccessible(true);
                    Event event = (Event) ctr.newInstance();

                    for(Event key : handlers.keySet())
                    {
                        if(key.getClass().isAssignableFrom(event.getClass()))
                        {
                            event = key;
                            break;
                        }
                    }

                    for(SortedSetMultimap<Object, Pair<Method, Integer>> o : handlers.get(event))
                    {
                        if(o.containsKey(obj))
                        {
                            handler = o;
                            handlers.remove(event, o);
                            break;
                        }
                    }

                    if(handler == null)
                    {
                        handler = TreeMultimap.create((o1, o2) -> (o1 == null || o2 == null ? 1 : o1.equals(o2) ? 0 : 1),
                                                      (o1, o2) -> o1.right == null ? (o2.right == null ? 0 : 1) : (o2.right == null ? -1 : (o1.right < o2.right ? 1 : (o1.right.equals(o2.right) ? 0 : -1))));
                    }

                    handler.put(obj, new Pair<>(method, method.getAnnotation(EventSubscription.class).priority()));
                    handlers.put(event, handler);
                }
                catch(NoSuchMethodException | SecurityException | InstantiationException | IllegalArgumentException | InvocationTargetException | IllegalAccessException e) { Logger.traceS(e); }
            }
        }
    }

    @Override
    public void finalize()
    {
        handlers.clear();
    }
}