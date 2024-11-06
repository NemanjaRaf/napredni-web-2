package framework.core;

import framework.annotations.Controller;
import framework.annotations.GET;
import framework.annotations.POST;
import framework.annotations.Path;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class RouteRegistry {
    private final Map<String, RouteInfo> routes = new HashMap<>();

    public void registerController(Object controllerInstance) {
        Class<?> controllerClass = controllerInstance.getClass();
        if (controllerClass.isAnnotationPresent(Controller.class)) {
            for (Method method : controllerClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Path.class)) {
                    String httpMethod = null;
                    if (method.isAnnotationPresent(GET.class)) {
                        httpMethod = "GET";
                    } else if (method.isAnnotationPresent(POST.class)) {
                        httpMethod = "POST";
                    }

                    if (httpMethod != null) {
                        String path = method.getAnnotation(Path.class).value();
                        String routeKey = httpMethod + " " + path;

                        if (routes.containsKey(routeKey)) {
                            throw new IllegalStateException("Duplicate route: " + routeKey);
                        }

                        routes.put(routeKey, new RouteInfo(controllerInstance, method));
                    }
                }
            }
        }
    }

    public RouteInfo getRoute(String httpMethod, String path) {
        String routeKey = httpMethod + " " + path;
        return routes.get(routeKey);
    }
}