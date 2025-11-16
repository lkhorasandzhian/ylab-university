package ru.ylab.levon.repository.api;

import ru.ylab.levon.model.User;

/**
 * Репозиторий для управления пользователями.
 * <p>
 * Определяет базовые операции по сохранению и поиску пользователей,
 * а также доступ к внутреннему хранилищу.
 */
public interface UserRepository {
    /**
     * Сохраняет нового пользователя.
     *
     * @param user пользователь для сохранения
     * @return {@code true}, если пользователь сохранён успешно;
     * {@code false}, если пользователь с таким именем уже существует
     */
    boolean save(User user);

    /**
     * Выполняет поиск пользователя по имени.
     *
     * @param username имя пользователя
     * @return найденный пользователь или {@code null}, если не найден
     */
    User findByUsername(String username);
}
