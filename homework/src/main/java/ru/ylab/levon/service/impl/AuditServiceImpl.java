package ru.ylab.levon.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;
import ru.ylab.levon.service.api.AuditService;

/**
 * Сервис для работы с журналом аудита.
 * <p>
 * Позволяет фиксировать действия пользователей и получать историю
 * сохранённых событий.
 */
@Service
public class AuditServiceImpl implements AuditService {
    private final AuditRepository repository;

    /**
     * Создаёт сервис аудита.
     *
     * @param repository репозиторий записей аудита
     */
    public AuditServiceImpl(AuditRepository repository) {
        this.repository = repository;
    }

    /**
     * Добавляет новую запись в журнал аудита.
     *
     * @param username имя пользователя, совершившего действие
     * @param action   описание действия
     */
    @Override
    public void log(String username, String action) {
        repository.add(new AuditRecord(username, action));
    }

    /**
     * Возвращает список всех записей аудита.
     *
     * @return список всех событий
     */
    @Override
    public List<AuditRecord> getAll() {
        return repository.findAll();
    }
}
