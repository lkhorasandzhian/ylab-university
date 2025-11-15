package ru.ylab.levon.service;

import lombok.Getter;
import lombok.NonNull;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;

public class UserService {

    private final UserRepository repository;

    @Getter
    private User currentUser;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public boolean register(@NonNull User user) {
        return repository.save(user);
    }

    public boolean login(@NonNull String username, @NonNull String password) {
        User user = repository.findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    public User findUser(String username) {
        return repository.findByUsername(username);
    }

    public java.util.Map<String, User> getStorage() {
        return repository.getStorage();
    }
}
