package ru.ylab.levon.repository.file;

import java.util.HashMap;
import java.util.Map;

import ru.ylab.levon.model.User;
import ru.ylab.levon.repository.api.UserRepository;

/**
 * Файловая реализация {@link UserRepository}, работающая с данными,
 * загруженными из сериализованного хранилища.
 * <p>
 * Все операции производятся над внутренней картой пользователей.
 */
public class FileUserRepository implements UserRepository {

    private final Map<String, User> users;

    /**
     * Создаёт файловый репозиторий пользователей.
     *
     * @param initialData предварительно загруженные данные пользователей
     */
    public FileUserRepository(Map<String, User> initialData) {
        this.users = new HashMap<>(initialData);
    }

    /**
     * Сохраняет пользователя, если имя пользователя уникально.
     *
     * @param user сохраняемый пользователь
     * @return {@code true}, если пользователь сохранён; {@code false}, если уже существует
     */
    @Override
    public boolean save(User user) {
        if (users.containsKey(user.getUsername())) {
            return false;
        }
        users.put(user.getUsername(), user);
        return true;
    }

    /**
     * Находит пользователя по имени.
     *
     * @param username имя пользователя
     * @return пользователь или {@code null}, если не найден
     */
    @Override
    public User findByUsername(String username) {
        return users.get(username);
    }

    /**
     * Возвращает текущее хранилище пользователей.
     *
     * @return карта пользователей
     */
    @Override
    public Map<String, User> getStorage() {
        return users;
    }
}
