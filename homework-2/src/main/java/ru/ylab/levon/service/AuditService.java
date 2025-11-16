package ru.ylab.levon.service;

import java.util.List;

import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;

/**
 * Сервис для работы с журналом аудита.
 * <p>
 * Позволяет фиксировать действия пользователей и получать историю
 * сохранённых событий.
 */
public class AuditService {
    private final AuditRepository repository;

    /**
     * Создаёт сервис аудита.
     *
     * @param repository репозиторий записей аудита
     */
    public AuditService(AuditRepository repository) {
        this.repository = repository;
    }

    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param username имя пользователя, совершившего действие
     * @param action   описание действия
     */
    public void log(String username, String action) {
        repository.add(new AuditRecord(username, action));
    }

    /**
     * Возвращает список всех записей аудита.
     *
     * @return список всех событий
     */
    public List<AuditRecord> getAll() {
        return repository.findAll();
    }

    /**
     * Возвращает последние {@code count} записей аудита.
     *
     * @param count количество необходимых записей
     * @return список последних событий
     */
    public List<AuditRecord> getLast(int count) {
        return repository.findLast(count);
    }
}
