package me.jangroen.eventbustest;

public class Main implements Listener {
    public static void main(String[] args) throws Exception {
        EventBus.registerEvents(new Main());
        EventBus.dispatchEvent(new EventA("Hello World!"));
        EventBus.dispatchEvent(new EventB(123));
        EventBus.dispatchEvent(new EventA("Hello World!"));
    }

    @EventHandler
    public void onJoin(EventA event) {
        System.out.println(event);
    }

    @EventHandler
    public void onQuit(EventB event) {
        System.out.println(event.data + 198);
    }

    public static class EventA implements Event {
        private final String data;

        public EventA(String data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "EventA{" +
                    "data='" + data + '\'' +
                    '}';
        }
    }

    public static class EventB implements Event {
        private final int data;

        public EventB(int data) {
            this.data = data;
        }

        @Override
        public String toString() {
            return "EventB{" +
                    "data=" + data +
                    '}';
        }
    }
}
