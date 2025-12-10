package ru.ylab.levon.service.api;

import ru.ylab.levon.model.AuditRecord;

import java.util.List;

/**
 * Интерфейс сервиса для работы с журналом аудита.
 * <p>
 * Определяет операции для фиксации действий пользователей
 * и получения сохранённых записей аудита.
 */
public interface AuditService {
    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param username имя пользователя, совершившего действие
     * @param action   описание действия
     */
    void log(String username, String action);

    /**
     * Возвращает список всех записей аудита.
     *
     * @return список событий аудита
     */
    List<AuditRecord> getAll();
}
