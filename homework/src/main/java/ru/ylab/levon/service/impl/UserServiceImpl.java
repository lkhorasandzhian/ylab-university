package ru.ylab.levon.service.impl;

import lombok.NonNull;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;
import ru.ylab.levon.service.api.AuditService;
import ru.ylab.levon.service.api.UserService;

/**
 * Сервис для управления пользователями приложения.
 * <p>
 * Отвечает за регистрацию, аутентификацию, определение роли текущего пользователя
 * и доступ к хранилищу пользователей.
 */
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final AuditService auditService;

    /**
     * Текущий авторизованный пользователь.
     * Значение {@code null} означает, что пользователь не вошёл в систему.
     */
    private User currentUser;

    /**
     * Создаёт сервис пользователей.
     *
     * @param repository репозиторий пользователей
     */
    public UserServiceImpl(UserRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    /**
     * Регистрирует нового пользователя.
     * <p>
     * Выполняет базовую валидацию данных и создаёт нового пользователя
     * с ролью, указанной в {@link UserCreateDto}.
     *
     * @param dto данные для создания пользователя
     * @return {@code true}, если пользователь был сохранён успешно;
     * {@code false}, если пользователь с таким именем уже существует
     * @throws IllegalArgumentException если логин или пароль пустые
     */
    @Override
    public boolean register(@NonNull UserCreateDto dto) {
        if (dto.username().isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым.");
        }
        if (dto.password().isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым.");
        }

        User user = new User(null, dto.username(), dto.password(), Role.USER);
        boolean isSuccess = repository.save(user);

        if (isSuccess) {
            auditService.log(user.getUsername(), "REGISTER");
        }

        return isSuccess;
    }

    /**
     * Выполняет регистрацию администратора.
     *
     * @return {@code true}, если администратор был сохранён успешно;
     * {@code false}, если администратор уже существует
     */
    @Override
    public boolean registerAdmin() {
        User admin = new User(null, "admin", "admin", Role.ADMIN);
        boolean isSuccess = repository.save(admin);

        if (isSuccess) {
            auditService.log(admin.getUsername(), "REGISTER_ADMIN");
        }

        return isSuccess;
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return {@code true}, если аутентификация выполнена успешно;
     * {@code false}, если логин или пароль неверны
     */
    @Override
    public boolean login(@NonNull String username, @NonNull String password) {
        User user = repository.findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            auditService.log(user.getUsername(), "LOGIN");
            return true;
        }
        return false;
    }

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    @Override
    public void logout() {
        if (currentUser != null) {
            auditService.log(currentUser.getUsername(), "LOGOUT");
        }
        currentUser = null;
    }

    /**
     * Проверяет, выполнен ли вход в систему.
     *
     * @return {@code true}, если пользователь авторизован
     */
    @Override
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Проверяет, является ли текущий пользователь администратором.
     *
     * @return {@code true}, если роль пользователя — {@link Role#ADMIN}
     */
    @Override
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }
}
