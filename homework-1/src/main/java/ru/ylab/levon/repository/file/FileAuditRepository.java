package ru.ylab.levon.repository.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.ylab.levon.model.AuditRecord;
import ru.ylab.levon.repository.api.AuditRepository;

/**
 * Файловая реализация {@link AuditRepository}, основанная на работе
 * с коллекцией объектов {@link AuditRecord}.
 * <p>
 * Использует список для хранения записей аудита, предоставляя доступ
 * ко всем событиям и к последним N записям.
 */
public class FileAuditRepository implements AuditRepository {

    private final List<AuditRecord> logs;

    /**
     * Создаёт репозиторий аудита на основе заранее загруженных данных.
     *
     * @param initialData список существующих записей аудита
     */
    public FileAuditRepository(List<AuditRecord> initialData) {
        this.logs = new ArrayList<>(initialData);
    }

    /**
     * Добавляет новую запись аудита.
     *
     * @param record запись аудита
     */
    @Override
    public void add(AuditRecord record) {
        logs.add(record);
    }

    /**
     * Возвращает все записи аудита в виде неизменяемого списка.
     *
     * @return список всех событий
     */
    @Override
    public List<AuditRecord> findAll() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Возвращает последние {@code count} записей аудита.
     *
     * @param count количество необходимых событий
     * @return список последних записей
     */
    @Override
    public List<AuditRecord> findLast(int count) {
        int from = Math.max(0, logs.size() - count);
        return List.copyOf(logs.subList(from, logs.size()));
    }

    /**
     * Возвращает внутреннее хранилище аудита.
     *
     * @return список всех записей
     */
    @Override
    public List<AuditRecord> getStorage() {
        return logs;
    }
}
