package app.services;

import framework.annotations.Bean;
import framework.annotations.Qualifier;

@Bean(scope = "singleton")
@Qualifier("email")
public class EmailNotificationService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("Email notification: " + message);
    }
}
