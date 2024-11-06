package server;

import framework.annotations.Controller;
import framework.annotations.Service;
import framework.core.ClassDiscovery;
import framework.core.DIEngine;
import framework.core.DependencyContainer;
import framework.core.RouteRegistry;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int TCP_PORT = 5000;
    static final RouteRegistry routeRegistry = new RouteRegistry();

    public static void main(String[] args) throws Exception {
        DependencyContainer dependencyContainer = new DependencyContainer();
        DIEngine diEngine = new DIEngine(dependencyContainer);

        ClassDiscovery discovery = new ClassDiscovery("target/classes");

        diEngine.initializeAnnotatedClasses(discovery);

        for (Class<?> controllerClass : discovery.discoverClasses(Controller.class)) {
            Object controllerInstance = diEngine.initializeController(controllerClass);
            routeRegistry.registerController(controllerInstance);
        }

        try (ServerSocket serverSocket = new ServerSocket(TCP_PORT)) {
            System.out.println("Server is running at http://localhost:" + TCP_PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new ServerThread(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}