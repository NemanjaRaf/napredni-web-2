package framework.core;

import java.util.HashMap;
import java.util.Map;

public class DependencyContainer {
    private final Map<Class<?>, Map<String, Class<?>>> container = new HashMap<>();

    public void register(Class<?> iface, String qualifier, Class<?> impl) {
        container.computeIfAbsent(iface, k -> new HashMap<>()).put(qualifier, impl);
    }

    public Class<?> resolve(Class<?> iface, String qualifier) {
        Map<String, Class<?>> implementations = container.get(iface);
        if (implementations == null || !implementations.containsKey(qualifier)) {
            throw new IllegalArgumentException("Nema implementacije za interface " + iface.getName() + " sa qualifier " + qualifier);
        }
        return implementations.get(qualifier);
    }
}
