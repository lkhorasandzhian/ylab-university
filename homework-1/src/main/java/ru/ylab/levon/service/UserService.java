package ru.ylab.levon.service;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.NonNull;
import ru.ylab.levon.model.User;
import ru.ylab.levon.model.Role;

/**
 * Класс {@code UserService} управляет пользователями системы
 * и отвечает за процессы регистрации, авторизации и выхода.
 * <p>
 * Хранит пользователей в памяти и поддерживает текущего авторизованного пользователя.
 * Реализует базовую проверку ролей (ADMIN и USER).
 */
public class UserService {
    private final Map<String, User> users;
    @Getter
    private User currentUser;

    /**
     * Создаёт пустой сервис пользователей.
     */
    public UserService() {
        this.users = new HashMap<>();
    }

    /**
     * Создаёт сервис пользователей с предзагруженными данными.
     *
     * @param users хранилище существующих пользователей
     */
    public UserService(Map<String, User> users) {
        this.users = new HashMap<>(users);
    }

    /**
     * Регистрирует нового пользователя, если имя пользователя уникально.
     *
     * @param user пользователь для регистрации
     * @return {@code true}, если регистрация успешна; {@code false}, если пользователь уже существует
     */
    public boolean register(@NonNull User user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return {@code true}, если авторизация прошла успешно; {@code false} в противном случае
     */
    public boolean login(@NonNull String username, @NonNull String password) {
        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Проверяет, выполнен ли вход пользователем.
     *
     * @return {@code true}, если пользователь авторизован; {@code false} — если нет
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Проверяет, имеет ли текущий пользователь административные права.
     *
     * @return {@code true}, если текущий пользователь — ADMIN; {@code false} в противном случае
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    /**
     * Возвращает неизменяемую копию всех зарегистрированных пользователей.
     *
     * @return копия хранилища пользователей
     */
    public Map<String, User> getAllUsers() {
        return Map.copyOf(users);
    }
}
