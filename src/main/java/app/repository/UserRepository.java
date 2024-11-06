package app.repository;

import framework.annotations.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository {
    private final Map<Integer, String> userDatabase = new HashMap<>();

    public UserRepository() {
        userDatabase.put(1, "John Doe");
        userDatabase.put(2, "Jane Smith");
    }

    public String findUserById(int userId) {
        return userDatabase.get(userId);
    }

    public void saveUser(int userId, String userName) {
        userDatabase.put(userId, userName);
    }
}