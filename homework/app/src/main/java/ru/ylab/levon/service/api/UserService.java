package ru.ylab.levon.service.api;

import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.User;

public interface UserService {
    boolean register(UserCreateDto dto);

    boolean registerAdmin();

    boolean login(String username, String password);

    void logout();

    boolean isLoggedIn();

    boolean isAdmin();

    User getCurrentUser();
}
