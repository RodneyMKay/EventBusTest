package me.jangroen.eventbustest;

public final class EventHandle<L extends Listener, E extends Event> {
    private final L listener;
    private final EventExecutor<L, E> executor;

    public EventHandle(L listener, EventExecutor<L, E> executor) {
        this.listener = listener;
        this.executor = executor;
    }

    public void executeEvent(E event) {
        executor.onEvent(listener, event);
    }
}
