package app.services;

import app.repository.UserRepository;
import framework.annotations.Autowired;
import framework.annotations.Service;

@Service
public class UserService {

    @Autowired(verbose = true)
    private UserRepository userRepository;

    public String getUserById(int userId) {
        return userRepository.findUserById(userId);
    }

    public void addUser(int userId, String userName) {
        userRepository.saveUser(userId, userName);
    }
}
