package ru.ylab.levon.audit;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import ru.ylab.levon.audit.api.AuditHandler;
import ru.ylab.levon.audit.api.CurrentUserProvider;

/**
 * Аспект для автоматической записи аудита методов,
 * помеченных аннотацией {@link Audit}.
 * <p>
 * Перехватывает вызов метода и фиксирует действие
 * текущего авторизованного пользователя.
 */
@Aspect
@RequiredArgsConstructor
public class AuditAspect {
    private final AuditHandler handler;
    private final CurrentUserProvider currentUserProvider;

    /**
     * Выполняет запись аудита после вызова метода,
     * помеченного @Audit.
     *
     * @param jp    точка соединения
     * @param audit аннотация с описанием действия
     */
    @AfterReturning("@annotation(audit)")
    public void audit(JoinPoint jp, Audit audit) {
        String username = currentUserProvider.getCurrentUsername();
        if (username == null) {
            return;  // Не логировать гостей.
        }

        handler.handle(username, audit.value());
    }

    /**
     * Выполняет запись аудита перед вызовом метода,
     * помеченного @Audit.
     *
     * @param jp    точка соединения
     * @param audit аннотация с описанием действия
     */
    @Before("@annotation(audit)")
    public void auditLogout(JoinPoint jp, Audit audit) {
        if (!audit.value().equals("LOGOUT")) {
            return;
        }

        String username = currentUserProvider.getCurrentUsername();
        if (username == null) {
            return;  // Не логировать гостей.
        }

        handler.handle(username, audit.value());
    }
}
