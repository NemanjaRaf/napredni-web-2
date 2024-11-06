package framework.core;

import java.lang.reflect.Method;

public class RouteInfo {
    private final Object controllerInstance;
    private final Method routeMethod;

    public RouteInfo(Object controllerInstance, Method routeMethod) {
        this.controllerInstance = controllerInstance;
        this.routeMethod = routeMethod;
    }

    public Object getControllerInstance() {
        return controllerInstance;
    }

    public Method getRouteMethod() {
        return routeMethod;
    }
}
