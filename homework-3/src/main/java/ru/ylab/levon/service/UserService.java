package ru.ylab.levon.service;

import lombok.Getter;
import lombok.NonNull;
import ru.ylab.levon.annotation.Audit;
import ru.ylab.levon.dto.UserCreateDto;
import ru.ylab.levon.model.Role;
import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;

/**
 * Сервис для управления пользователями приложения.
 * <p>
 * Отвечает за регистрацию, аутентификацию, определение роли текущего пользователя
 * и доступ к хранилищу пользователей.
 */
public class UserService {
    private final UserRepository repository;

    /**
     * Текущий авторизованный пользователь.
     * Значение {@code null} означает, что пользователь не вошёл в систему.
     */
    @Getter
    private User currentUser;

    /**
     * Создаёт сервис пользователей.
     *
     * @param repository репозиторий пользователей
     */
    public UserService(UserRepository repository) {
        this.repository = repository;
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
    @Audit("REGISTER")
    public boolean register(@NonNull UserCreateDto dto) {
        if (dto.username().isBlank()) {
            throw new IllegalArgumentException("Логин не может быть пустым.");
        }
        if (dto.password().isBlank()) {
            throw new IllegalArgumentException("Пароль не может быть пустым.");
        }

        User user = new User(null, dto.username(), dto.password(), Role.USER);

        return repository.save(user);
    }

    /**
     * Выполняет регистрацию администратора.
     *
     * @return {@code true}, если администратор был сохранён успешно;
     * {@code false}, если администратор уже существует
     */
    @Audit("REGISTER_ADMIN")
    public boolean registerAdmin() {
        User admin = new User(null, "admin", "admin", Role.ADMIN);

        return repository.save(admin);
    }

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param username имя пользователя
     * @param password пароль
     * @return {@code true}, если аутентификация выполнена успешно;
     * {@code false}, если логин или пароль неверны
     */
    @Audit("LOGIN")
    public boolean login(@NonNull String username, @NonNull String password) {
        User user = repository.findByUsername(username);
        if (user != null && user.checkPassword(password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    /**
     * Выполняет выход текущего пользователя из системы.
     */
    @Audit("LOGOUT")
    public void logout() {
        currentUser = null;
    }

    /**
     * Проверяет, выполнен ли вход в систему.
     *
     * @return {@code true}, если пользователь авторизован
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Проверяет, является ли текущий пользователь администратором.
     *
     * @return {@code true}, если роль пользователя — {@link Role#ADMIN}
     */
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole() == Role.ADMIN;
    }
}
