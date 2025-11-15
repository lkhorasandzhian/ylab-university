package ru.ylab.levon.repository.api;

import ru.ylab.levon.model.User;

import java.util.Map;

public interface UserRepository {

    boolean save(User user);

    User findByUsername(String username);

    Map<String, User> getStorage();
}
