package listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class EventListener<TArgs> {
    private final Set<Consumer<TArgs>> listeners = new HashSet<>();

    public void addListener(Consumer<TArgs> listener) {
        listeners.add(listener);
    }

    public void broadcast(TArgs args) {
        if (listeners.size() > 0) {
            listeners.forEach(x -> x.accept(args));
        }
    }
}
