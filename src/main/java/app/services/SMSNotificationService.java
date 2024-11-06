package app.services;

import framework.annotations.Bean;
import framework.annotations.Qualifier;

@Bean(scope = "singleton")
@Qualifier("sms")
public class SMSNotificationService implements NotificationService {
    @Override
    public void sendNotification(String message) {
        System.out.println("SMS notification: " + message);
    }
}