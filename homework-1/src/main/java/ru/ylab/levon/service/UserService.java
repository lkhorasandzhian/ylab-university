package ru.ylab.levon.service;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import ru.ylab.levon.model.User;
import ru.ylab.levon.model.Role;

public class UserService {
    private final Map<String, User> users;
    @Getter
    private User currentUser;

    public UserService() {
        this.users = new HashMap<>();
    }

    public UserService(Map<String, User> users) {
        this.users = new HashMap<>(users);
    }

    public boolean register(@NonNull User user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }

        users.put(user.getUsername(), user);

        return true;
    }

    public boolean login(@NonNull String username, @NonNull String password) {
        User user = users.get(username);

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

    public Map<String, User> getAllUsers() {
        return Map.copyOf(users);
    }
}
