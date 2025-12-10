package ru.ylab.levon.service.api;

import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.User;

/**
 * Интерфейс сервиса для управления пользователями.
 * <p>
 * Определяет операции регистрации, аутентификации, проверки роли
 * и получения текущего авторизованного пользователя.
 */
public interface UserService {
    /**
     * Регистрирует нового пользователя.
     *
     * @param dto данные для создания пользователя
     * @return {@code true}, если пользователь был успешно сохранён;
     * {@code false}, если пользователь с таким именем уже существует
     * @throws IllegalArgumentException если логин или пароль пустые
     */
    boolean register(UserCreateDto dto);

    /**
     * Регистрирует администратора по умолчанию.
     *
     * @return {@code true}, если администратор был создан;
     * {@code false}, если такой пользователь уже существует
     */
    boolean registerAdmin();

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return {@code true}, если аутентификация выполнена успешно;
     * {@code false}, если данные неверны
     */
    boolean login(String username, String password);

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    void logout();

    /**
     * Проверяет, авторизован ли пользователь.
     *
     * @return {@code true}, если пользователь вошёл в систему
     */
    boolean isLoggedIn();

    /**
     * Проверяет, обладает ли текущий пользователь ролью администратора.
     *
     * @return {@code true}, если пользователь — ADMIN
     */
    boolean isAdmin();

    /**
     * Возвращает текущего авторизованного пользователя.
     *
     * @return авторизованный {@link User} или {@code null}, если вход не выполнен
     */
    User getCurrentUser();
}
