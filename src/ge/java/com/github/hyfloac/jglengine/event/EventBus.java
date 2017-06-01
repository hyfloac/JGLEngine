package com.github.hyfloac.jglengine.event;

import com.github.hyfloac.jglengine.event.events.Event;
import com.github.hyfloac.jglengine.event.events.EventKeyInput;
import com.github.hyfloac.jglengine.event.handler.KeyInputHandler;
import com.github.hyfloac.simplelog.Logger;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.tuple.primitive.ObjectIntPair;
import com.gs.collections.impl.multimap.bag.HashBagMultimap;
import com.gs.collections.impl.tuple.primitive.PrimitiveTuples;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;

public class EventBus
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
    private final MutableMultimap<Class<? extends Event>, SortedSetMultimap<Object, ObjectIntPair<Method>>> handlers;

    public EventBus()
    {
        handlers = new HashBagMultimap<>();
    }

    public void dispatch(final @Nonnull Event event)
    {
        for(final Class<? extends Event> real : handlers.keyBag())
        {
            if(real.isAssignableFrom(event.getClass()))
            {
                for(final SortedSetMultimap<Object, ObjectIntPair<Method>> handler : handlers.get(real))
                {
                    if(event instanceof EventKeyInput)
                    {
                        Logger.debugS(handlers.keyBag().size());
                    }
                    for(final Object clazz : handler.keySet())
                    {
                        for(final ObjectIntPair<Method> method : handler.get(clazz))
                        {
                            if(event.canceled)
                            {
                                if(method.getOne().isAnnotationPresent(EventSubscription.class))
                                {
                                    if(!method.getOne().getAnnotation(EventSubscription.class).ignoreCancelation()) { continue; }
                                }
                                else { continue; }
                            }
                            try
                            {
                                method.getOne().setAccessible(true);
                                method.getOne().invoke(clazz, event);
                            }
                            catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException | NullPointerException e) { Logger.traceS(e); }
                        }
                    }
                }
            }
        }
    }

    public void register(final @Nonnull Object obj)
    {
        for(final Method method : obj.getClass().getDeclaredMethods())
        {
            if(method.isAnnotationPresent(EventSubscription.class))
            {
                try
                {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if(parameterTypes.length != 1) { throw new IllegalArgumentException("Method " + method + " has @EventSubscription annotation, but requires " + parameterTypes.length + " arguments.  Event handler methods must require a single argument."); }

                    Class<?> parameterType = parameterTypes[0];
                    if(!Event.class.isAssignableFrom(parameterType)) { throw new IllegalArgumentException("Method " + method + " has @EventSubscription annotation, but takes a argument that is not an Event " + parameterType); }

                    @SuppressWarnings("unchecked")
                    Class<? extends Event> eventType = (Class<? extends Event>) parameterType;

                    SortedSetMultimap<Object, ObjectIntPair<Method>> handler = null;

                    for(final SortedSetMultimap<Object, ObjectIntPair<Method>> o : handlers.get(eventType))
                    {
                        if(o.containsKey(obj))
                        {
                            handler = o;
                            handlers.remove(eventType, o);
                            break;
                        }
                    }

                    if(handler == null)
                    {
                        handler = TreeMultimap.create(KeyComparator.INSTANCE, ValueComparator.INSTANCE);
                    }

                    handler.put(obj, PrimitiveTuples.pair(method, method.getAnnotation(EventSubscription.class).priority()));
                    handlers.put(eventType, handler);
                }
                catch(SecurityException | IllegalArgumentException e) { Logger.traceS(e); }
            }
        }
    }

    //region Comparators
    private static final class KeyComparator implements Comparator<Object>
    {
        public static final KeyComparator INSTANCE = new KeyComparator();

        @Override
        public int compare(Object o1, Object o2)
        {
            return (o1 == null || o2 == null ? 1 : o1.equals(o2) ? 0 : 1);
        }
    }

    private static final class ValueComparator implements Comparator<ObjectIntPair<Method>>
    {
        public static final ValueComparator INSTANCE = new ValueComparator();

        @Override
        public int compare(ObjectIntPair<Method> o1, ObjectIntPair<Method> o2)
        {
            return ((o1 == null) ? ((o2 == null) ? 0 : -1) : ((o2 == null) ? 1 : ((o1.getOne() == null) ? ((o2.getOne() == null) ? 0 : -1) : ((o2.getOne() == null) ? 1 : ((o1.getTwo() == o2.getTwo()) ? 0 : ((o1.getTwo() > o2.getTwo()) ? 1 : -1))))));
        }
    }
    //endregion
}