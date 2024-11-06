package app.controllers;

import app.services.NotificationService;
import app.services.UserService;
import framework.annotations.*;
import framework.response.JsonResponse;
import framework.response.Response;

import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("sms")
    private NotificationService notificationService;

    @GET
    @Path("/user")
    public Response getUser() {
        String user = userService.getUserById(1);
        notificationService.sendNotification("User: " + user);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("user", user);

        return new JsonResponse(responseMap);
    }
}