package ru.ylab.levon.audit.api;

/**
 * Поставщик информации о текущем пользователе.
 * Приложение должно вернуть username текущего юзера.
 */
public interface CurrentUserProvider {
    String getCurrentUsername();
}
