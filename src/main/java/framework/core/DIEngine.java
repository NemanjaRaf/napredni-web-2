package framework.core;

import framework.annotations.*;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class DIEngine {
    private final Map<Class<?>, Object> singletonInstances = new HashMap<>();
    private final DependencyContainer dependencyContainer;

    public DIEngine(DependencyContainer dependencyContainer) {
        this.dependencyContainer = dependencyContainer;
    }

    public void initializeAnnotatedClasses(ClassDiscovery discovery) throws Exception {
        for (Class<?> clazz : discovery.discoverClasses(Service.class)) {
            initializeServiceOrComponent(clazz);
        }
        for (Class<?> clazz : discovery.discoverClasses(Bean.class)) {
            initializeServiceOrComponent(clazz);
        }
        for (Class<?> clazz : discovery.discoverClasses(Component.class)) {
            initializeServiceOrComponent(clazz);
        }
    }

    private void initializeServiceOrComponent(Class<?> clazz) throws Exception {
        if (singletonInstances.containsKey(clazz)) return;

        Object instance = createInstance(clazz);

        if (clazz.isAnnotationPresent(Qualifier.class)) {
            Qualifier qualifier = clazz.getAnnotation(Qualifier.class);
            dependencyContainer.register(clazz.getInterfaces()[0], qualifier.value(), clazz);
        }
    }

    public <T> T initializeController(Class<T> controllerClass) throws Exception {
        return (T) createInstance(controllerClass);
    }

    private Object createInstance(Class<?> clazz) throws Exception {
        if (clazz.isAnnotationPresent(Service.class) ||
                (clazz.isAnnotationPresent(Bean.class) && clazz.getAnnotation(Bean.class).scope().equals("singleton"))) {
            if (singletonInstances.containsKey(clazz)) {
                return singletonInstances.get(clazz);
            }
            Object instance = clazz.getDeclaredConstructor().newInstance();
            singletonInstances.put(clazz, instance);
            injectDependencies(instance);
            return instance;
        }

        Object instance = clazz.getDeclaredConstructor().newInstance();
        injectDependencies(instance);
        return instance;
    }

    private void injectDependencies(Object instance) throws Exception {
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                Autowired autowired = field.getAnnotation(Autowired.class);

                Object dependency = resolveDependency(field);
                field.set(instance, dependency);

                if (autowired.verbose()) {
                    System.out.println("Initialized " + dependency.getClass().getSimpleName() + " " + field.getName() + " in "
                            + instance.getClass().getSimpleName() + " on " + LocalDateTime.now()
                            + " with " + System.identityHashCode(dependency));
                }
            }
        }
    }

    private Object resolveDependency(Field field) throws Exception {
        Class<?> fieldType = field.getType();

        if (fieldType.isInterface()) {
            Qualifier qualifier = field.getAnnotation(Qualifier.class);
            if (qualifier == null) {
                throw new IllegalStateException("Autowired field of type interface " + fieldType.getName() + " requires @Qualifier");
            }
            Class<?> implementation = dependencyContainer.resolve(fieldType, qualifier.value());
            return createInstance(implementation);
        }

        return createInstance(fieldType);
    }
}