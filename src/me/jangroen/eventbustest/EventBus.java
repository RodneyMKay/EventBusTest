package me.jangroen.eventbustest;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class EventBus {
    // Prevent instantiation
    private EventBus() {}

    private static final Map<Class<?>, List<EventHandle<?, ?>>> eventHandles = new HashMap<>();

    public static <L extends Listener> void registerEvents(L listener) throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();

        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventHandler.class) &&
                    method.getReturnType() == Void.TYPE &&
                    !Modifier.isStatic(method.getModifiers()) &&
                    method.getParameterCount() == 1) {

                Class<?> eventClass = method.getParameterTypes()[0];

                if (!Event.class.isAssignableFrom(eventClass)) {
                    continue;
                }

                MethodHandle handle = lookup.unreflect(method);

                CallSite callSite = LambdaMetafactory.metafactory(lookup, "onEvent",
                        MethodType.methodType(EventExecutor.class),
                        MethodType.methodType(Void.TYPE, Listener.class, Event.class),
                        handle, handle.type());

                try {
                    //noinspection unchecked
                    EventExecutor<L, ?> executor = (EventExecutor<L, ?>) callSite.getTarget().invokeExact();
                    EventHandle<L, ?> eventHandle = new EventHandle<>(listener, executor);

                    List<EventHandle<?, ?>> eventHandleList = eventHandles.get(eventClass);
                    if (eventHandleList != null) {
                        eventHandleList.add(eventHandle);
                    } else {
                        eventHandleList = new ArrayList<>();
                        eventHandleList.add(eventHandle);
                        eventHandles.put(eventClass, eventHandleList);
                    }
                } catch (Exception e) {
                    throw e;
                } catch (Throwable t) {
                    throw new Error(t);
                }
            }
        }
    }

    public static <T extends Event> void dispatchEvent(T event) {
        for (EventHandle<?, ?> eventHandle : eventHandles.get(event.getClass())) {
            //noinspection unchecked
            ((EventHandle<?, T>) eventHandle).executeEvent(event);
        }
    }
}
