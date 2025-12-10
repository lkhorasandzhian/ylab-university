package ru.ylab.levon.audit.api;

/**
 * Универсальный обработчик аудита.
 * Приложение должно реализовать его и записывать аудит
 * так, как ему нужно (в БД, лог, Kafka и т.д.).
 */
public interface AuditHandler {
    void handle(String username, String action);
}
