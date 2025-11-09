package ru.ylab.levon.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import ru.ylab.levon.model.AuditRecord;

/**
 * Класс {@code AuditService} отвечает за хранение и управление записями аудита.
 * <p>
 * Позволяет фиксировать действия пользователей (например, вход, выход, добавление товаров и т.д.),
 * а также просматривать весь журнал или последние N записей.
 */
public class AuditService {
    private final List<AuditRecord> logs;

    /**
     * Создаёт новый пустой журнал аудита.
     */
    public AuditService() {
        this.logs = new ArrayList<>();
    }

    /**
     * Создаёт журнал аудита на основе существующих записей.
     *
     * @param auditRecords список ранее сохранённых записей
     */
    public AuditService(List<AuditRecord> auditRecords) {
        this.logs = new ArrayList<>(auditRecords);
    }

    /**
     * Добавляет новую запись об аудите действия пользователя.
     *
     * @param username имя пользователя, выполнившего действие
     * @param action   описание действия
     */
    public void log(String username, String action) {
        logs.add(new AuditRecord(username, action));
    }

    /**
     * Возвращает неизменяемый список всех записей аудита.
     *
     * @return список всех записей
     */
    public List<AuditRecord> getAll() {
        return Collections.unmodifiableList(logs);
    }

    /**
     * Возвращает последние {@code count} записей аудита.
     * Если записей меньше, чем {@code count}, возвращаются все доступные.
     *
     * @param count количество последних записей
     * @return список последних записей
     */
    public List<AuditRecord> getLast(int count) {
        int fromIndex = Math.max(0, logs.size() - count);
        return List.copyOf(logs.subList(fromIndex, logs.size()));
    }

    /**
     * Очищает журнал аудита.
     */
    public void clear() {
        logs.clear();
    }
}
