package ru.ylab.levon.repository.api;

import java.util.List;

import ru.ylab.levon.model.AuditRecord;

/**
 * Репозиторий для управления записями аудита.
 * <p>
 * Определяет операции добавления новых записей, получения всех событий
 * и выборки последних N действий.
 */
public interface AuditRepository {

    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param record запись аудита
     */
    void add(AuditRecord record);

    /**
     * Возвращает все записи аудита.
     *
     * @return список всех событий
     */
    List<AuditRecord> findAll();

    /**
     * Возвращает последние {@code count} записей аудита.
     *
     * @param count количество запрашиваемых записей
     * @return список последних событий
     */
    List<AuditRecord> findLast(int count);

    /**
     * Возвращает внутреннее хранилище записей аудита.
     *
     * @return список всех записей
     */
    List<AuditRecord> getStorage();
}
