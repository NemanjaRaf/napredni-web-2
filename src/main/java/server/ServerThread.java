package server;

import framework.core.RouteInfo;
import framework.core.RouteRegistry;
import framework.response.JsonResponse;
import framework.response.Response;
import framework.request.enums.Method;
import framework.request.Header;
import framework.request.Helper;
import framework.request.Request;
import framework.request.exceptions.RequestNotValidException;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerThread implements Runnable{

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ServerThread(Socket socket){
        this.socket = socket;

        try {
            in = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            out = new PrintWriter(
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    socket.getOutputStream())), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run(){
        try {

            Request request = this.generateRequest();
            if(request == null) {
                in.close();
                out.close();
                socket.close();
                return;
            }

            System.out.println("Request received: " + request.getMethod() + " " + request.getLocation() + " " + request.getParameters());

            RouteInfo route = Server.routeRegistry.getRoute(request.getMethod().toString(), request.getLocation());
            if(route == null) {
                out.println("HTTP/1.1 404 Not Found");
                out.println("Content-Type: text/plain");
                out.println();
                out.println("404 Not Found");
                in.close();
                out.close();
                socket.close();
                return;
            }

            try {
                Response response = (Response) route.getRouteMethod().invoke(route.getControllerInstance());
                out.println(response.render());
            } catch (Exception e) {
                out.println("HTTP/1.1 500 Internal Server Error");
                out.println("Content-Type: text/plain");
                out.println();
                out.println("500 Internal Server Error: " + e.getMessage());
                e.printStackTrace();
                in.close();
                out.close();
                socket.close();
                return;
            };

            in.close();
            out.close();
            socket.close();

        } catch (IOException | RequestNotValidException e) {
            e.printStackTrace();
        }
    }

    private Request generateRequest() throws IOException, RequestNotValidException {
        String command = in.readLine();
        if(command == null) {
            return null;
        }

        String[] actionRow = command.split(" ");
        Method method = Method.valueOf(actionRow[0]);
        String route = actionRow[1];
        Header header = new Header();
        HashMap<String, String> parameters = Helper.getParametersFromRoute(route);

        do {
            command = in.readLine();
            String[] headerRow = command.split(": ");
            if(headerRow.length == 2) {
                header.add(headerRow[0], headerRow[1]);
            }
        } while(!command.trim().equals(""));

        if(method.equals(Method.POST)) {
            if (header.get("content-length") != null) {
                int contentLength = Integer.parseInt(header.get("content-length"));
                char[] buff = new char[contentLength];
                in.read(buff, 0, contentLength);
                String parametersString = new String(buff);

                HashMap<String, String> postParameters = Helper.getParametersFromString(parametersString);
                for (String parameterName : postParameters.keySet()) {
                    parameters.put(parameterName, postParameters.get(parameterName));
                }
            }
        }

        Request request = new Request(method, route, header, parameters);

        return request;
    }
}
