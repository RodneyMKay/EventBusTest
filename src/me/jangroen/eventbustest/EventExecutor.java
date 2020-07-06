package me.jangroen.eventbustest;

@FunctionalInterface
public interface EventExecutor<L extends Listener, E extends Event> {
    void onEvent(L listener, E event);
}
