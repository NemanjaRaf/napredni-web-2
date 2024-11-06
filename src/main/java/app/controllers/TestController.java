package app.controllers;

import framework.annotations.Controller;
import framework.annotations.GET;
import framework.annotations.POST;
import framework.annotations.Path;
import framework.response.JsonResponse;
import framework.response.Response;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TestController {
    @GET
    @Path("/test")
    public Response hello() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello world");

        return new JsonResponse(response);
    }
}
